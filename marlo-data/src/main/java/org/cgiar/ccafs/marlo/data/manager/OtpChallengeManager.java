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

package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.OtpChallenge;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

/**
 * The challenge store of the Center email one-time-code sign-in (CHG-OTP-LOGIN-001-T03).
 * <p>
 * Correctness lives in the database: each write is a conditional statement whose affected-row count decides the
 * outcome (DD-3), and every method that writes is transactional. The Java clock is the only time source, and every
 * stored {@code DATETIME} is a UTC wall clock.
 */
public interface OtpChallengeManager {

  /**
   * How a wrong code ended, after the attempt was registered (design 5.2 step 6). The three values are the answers
   * {@code mismatch}, {@code exhausted} and {@code invalid} of the verify action.
   */
  public enum MismatchOutcome {
    /** The wrong code was counted and the challenge still has attempts left. */
    MISMATCH,
    /** The wrong code was the last allowed one, or the challenge was already exhausted. */
    EXHAUSTED,
    /** The challenge is consumed, superseded, expired or gone. */
    INVALID
  }

  /**
   * Resolves the allow-list of Global Units straight from the database, never from a cache (DD-12, FN-002).
   *
   * @param key the {@code parameters.key}, for example {@code APConstants.OTP_ALLOWED_EMAIL_DOMAINS}.
   * @param globalUnitIds the Global Units to resolve.
   * @return one entry for every requested id, never null. A value of {@code ""} means the list is off for that
   *         Global Unit: a NULL, empty or blank value, no catalog row for its type, or a Global Unit that does not
   *         exist. Any other value is returned as stored, for the caller to parse.
   */
  public Map<Long, String> allowListFor(String key, Collection<Long> globalUnitIds);

  /**
   * Consumes the challenge: sets {@code consumed_at} only while it is open, has fewer than
   * {@link OtpChallenge#MAX_ATTEMPTS} attempts and has not expired (JS-1).
   *
   * @return true when exactly one row was consumed. False for an exhausted, expired, consumed, superseded or missing
   *         challenge (FN-013).
   */
  public boolean consume(String nonce);

  /**
   * Loads a challenge as a detached copy that is always read fresh from the database.
   *
   * @return the row, or null when none has that nonce (never written, or purged).
   */
  public OtpChallenge findByNonce(String nonce);

  /**
   * Supersedes every earlier open challenge of the email and then inserts the new one, in one transaction (FN-007,
   * DD-6). The method is the same for an eligible account and for a decoy (DD-5).
   *
   * @param emailHmac the hex MAC of the normalised email.
   * @param globalUnitId the Global Unit the code is for.
   * @param nonce the handle of the new challenge.
   * @param codeHmac the hex MAC of {@code nonce|code}.
   * @param expiresAt when the challenge expires (FN-010).
   * @throws IllegalStateException if the insert did not add exactly one row. The transaction then rolls back, and the
   *         supersede with it.
   */
  public void issue(String emailHmac, long globalUnitId, String nonce, String codeHmac, Instant expiresAt);

  /**
   * Deletes the challenges that expired before the cutoff, in a transaction of its own that never joins the
   * caller's (OPS-003).
   *
   * @return the rows deleted.
   */
  public int purgeExpired(Instant olderThan);

  /**
   * Registers a wrong code in one transaction (design 5.2 step 6): a conditional increment, then a re-read.
   * <ul>
   * <li>1 row affected and the new attempts reached {@link OtpChallenge#MAX_ATTEMPTS}: {@code EXHAUSTED}.</li>
   * <li>1 row affected and fewer attempts: {@code MISMATCH}.</li>
   * <li>0 rows affected: the row is re-read and classified in the order of the verify pre-checks. Consumed:
   * {@code INVALID}. Exhausted: {@code EXHAUSTED}. Expired or gone: {@code INVALID} (RJ-3).</li>
   * </ul>
   */
  public MismatchOutcome registerMismatch(String nonce);
}
