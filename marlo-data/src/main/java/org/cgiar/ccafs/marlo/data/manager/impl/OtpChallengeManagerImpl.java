/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.OtpChallengeDAO;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager;
import org.cgiar.ccafs.marlo.data.model.OtpChallenge;

import java.time.Clock;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * CHG-OTP-LOGIN-001-T03. Every write method is {@code @Transactional}: without a transaction the request session
 * stays in manual flush mode and the pool rolls the write back silently. The only time source is {@link #clock},
 * UTC, so a statement never mixes the Java and the database clock.
 */
@Named
public class OtpChallengeManagerImpl implements OtpChallengeManager {

  private final OtpChallengeDAO otpChallengeDAO;
  private final Clock clock;

  @Inject
  public OtpChallengeManagerImpl(OtpChallengeDAO otpChallengeDAO) {
    this(otpChallengeDAO, Clock.systemUTC());
  }

  OtpChallengeManagerImpl(OtpChallengeDAO otpChallengeDAO, Clock clock) {
    this.otpChallengeDAO = otpChallengeDAO;
    this.clock = clock;
  }

  @Override
  public Map<Long, String> allowListFor(String key, Collection<Long> globalUnitIds) {
    Map<Long, String> allowLists = new HashMap<>();
    if (globalUnitIds == null || globalUnitIds.isEmpty()) {
      return allowLists;
    }
    Map<Long, String> stored = this.otpChallengeDAO.findAllowListValues(key, globalUnitIds);
    for (Long globalUnitId : globalUnitIds) {
      String value = stored.get(globalUnitId);
      // NULL, empty and whitespace-only all mean "off", as does a Global Unit the store did not return.
      allowLists.put(globalUnitId, value == null || value.trim().isEmpty() ? "" : value);
    }
    return allowLists;
  }

  @Override
  @Transactional
  public boolean consume(String nonce) {
    return this.otpChallengeDAO.consume(nonce, this.clock.instant()) == 1;
  }

  @Override
  public OtpChallenge findByNonce(String nonce) {
    return this.otpChallengeDAO.findByNonce(nonce);
  }

  @Override
  @Transactional
  public void issue(String emailHmac, long globalUnitId, String nonce, String codeHmac, Instant expiresAt) {
    Instant now = this.clock.instant();
    this.otpChallengeDAO.supersede(emailHmac, now);
    int inserted = this.otpChallengeDAO.insert(emailHmac, globalUnitId, nonce, codeHmac, expiresAt, now);
    if (inserted != 1) {
      throw new IllegalStateException("The challenge insert affected " + inserted + " rows, expected 1");
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public int purgeExpired(Instant olderThan) {
    return this.otpChallengeDAO.deleteExpiredBefore(olderThan);
  }

  @Override
  @Transactional
  public MismatchOutcome registerMismatch(String nonce) {
    if (this.otpChallengeDAO.incrementAttempts(nonce) == 1) {
      Integer attempts = this.otpChallengeDAO.findAttempts(nonce);
      if (attempts == null) {
        return MismatchOutcome.INVALID;
      }
      return attempts >= OtpChallenge.MAX_ATTEMPTS ? MismatchOutcome.EXHAUSTED : MismatchOutcome.MISMATCH;
    }
    // Nothing was incremented: a concurrent consume, supersede or exhaustion won. Answer as the verify
    // pre-checks do (RJ-3): consumed, then exhausted; an expired or missing row is invalid.
    OtpChallenge challenge = this.otpChallengeDAO.findByNonce(nonce);
    if (challenge == null || challenge.getConsumedAt() != null) {
      return MismatchOutcome.INVALID;
    }
    if (challenge.getAttempts() >= OtpChallenge.MAX_ATTEMPTS) {
      return MismatchOutcome.EXHAUSTED;
    }
    // Expired, or a row that looks open although nothing was incremented, which no state explains: refuse.
    return MismatchOutcome.INVALID;
  }
}
