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

import org.cgiar.ccafs.marlo.data.dao.OtpRateLimitDAO;
import org.cgiar.ccafs.marlo.data.manager.OtpRateLimitManager;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * CHG-OTP-LOGIN-001-T04. Every method writes, so every method is {@code @Transactional}: without a transaction the
 * request session stays in manual flush mode and the pool rolls the write back silently.
 * <p>
 * Every method is {@code REQUIRES_NEW}. A hit must count even when the request that made it later fails, and the
 * row lock of a hot bucket must be released as soon as its two statements are done instead of being held by a
 * longer transaction of the caller. The manager reads no clock: the caller computes {@code now} once per request and
 * passes it, so a statement never mixes the Java and the database clock.
 */
@Named
public class OtpRateLimitManagerImpl implements OtpRateLimitManager {

  /** Written to a new cooldown row, so the first request of a bucket is always due. */
  static final Instant COOLDOWN_SENTINEL = Instant.EPOCH;

  private final OtpRateLimitDAO otpRateLimitDAO;

  @Inject
  public OtpRateLimitManagerImpl(OtpRateLimitDAO otpRateLimitDAO) {
    this.otpRateLimitDAO = otpRateLimitDAO;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public boolean acceptCooldown(String bucket, Instant now, Duration minimumSpacing) {
    // The column keeps milliseconds, so the decision and the written value use the millisecond it will store.
    Instant at = now.truncatedTo(ChronoUnit.MILLIS);
    // The exclusive lock is taken by the first statement and held to the end of this transaction: the requests of one
    // bucket serialize here, and each reads the value the previous one committed.
    this.otpRateLimitDAO.lockCooldown(bucket, COOLDOWN_SENTINEL);
    Instant lastRequestAt = this.otpRateLimitDAO.findLastRequestAtForUpdate(bucket);
    if (lastRequestAt == null) {
      // The row this transaction just locked cannot be read: nothing can be decided.
      throw new IllegalStateException("The cooldown row cannot be read after it was locked");
    }
    if (lastRequestAt.isAfter(at.minus(minimumSpacing))) {
      return false;
    }
    if (this.otpRateLimitDAO.updateCooldown(bucket, at) != 1) {
      throw new IllegalStateException("The cooldown update did not touch exactly one row");
    }
    return true;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public int hit(String bucket, Instant windowStart) {
    this.otpRateLimitDAO.incrementWindow(bucket, windowStart);
    Integer hits = this.otpRateLimitDAO.findHits(bucket, windowStart);
    if (hits == null) {
      // The row this transaction just wrote cannot be read back: no count is trustworthy.
      throw new IllegalStateException("The window counter cannot be read back after its increment");
    }
    return hits;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public int purgeCooldownsBefore(Instant cutoff) {
    return this.otpRateLimitDAO.deleteCooldownsBefore(cutoff);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public int purgeWindowsBefore(Instant cutoff) {
    return this.otpRateLimitDAO.deleteWindowsBefore(cutoff);
  }
}
