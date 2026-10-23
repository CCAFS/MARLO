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

package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.OtpChallenge;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;

/**
 * Storage of the Center email one-time-code challenges (CHG-OTP-LOGIN-001-T03).
 * <p>
 * Every statement is a single conditional native statement whose affected-row count is the answer (DD-3). The caller
 * owns the transaction. Every {@link Instant} is written as a UTC wall clock: the Java clock is the only time source.
 */
public interface OtpChallengeDAO {

  /**
   * Reads {@code attempts} of the row, as a fresh read of the database.
   *
   * @param nonce the challenge handle.
   * @return the attempts, or null when no row has that nonce.
   */
  public Integer findAttempts(String nonce);

  /**
   * Loads a challenge as a detached copy, so a later read in the same session never returns a stale instance.
   *
   * @param nonce the challenge handle.
   * @return the row, or null when none has that nonce.
   */
  public OtpChallenge findByNonce(String nonce);

  /**
   * Reads the value of each key for each Global Unit straight from the tables, through neither the second-level
   * cache nor the query cache (DD-12, FN-002), in one statement. An active {@code custom_parameters} row for the
   * Global Unit wins, even when its value is NULL or blank; otherwise the catalog {@code parameters.default_value}
   * for the Global Unit's type applies.
   *
   * @param keys the {@code parameters.key} values.
   * @param globalUnitIds the Global Units to resolve.
   * @return the raw effective values by Global Unit id and then by key. A Global Unit that does not exist is
   *         absent, and so is a key with no catalog row for the Global Unit's type. A value can be NULL (a NULL
   *         default or a NULL custom value) or blank.
   */
  public Map<Long, Map<String, String>> findSpecificityValues(Collection<String> keys,
    Collection<Long> globalUnitIds);

  /**
   * Adds one to {@code attempts} while the row is not consumed and has fewer than {@link OtpChallenge#MAX_ATTEMPTS}.
   *
   * @param nonce the challenge handle.
   * @return the rows affected: 1, or 0 when the row is consumed, exhausted or missing.
   */
  public int incrementAttempts(String nonce);

  /**
   * Inserts a challenge with zero attempts and no consumption.
   *
   * @return the rows affected: 1.
   */
  public int insert(String emailHmac, long globalUnitId, String nonce, String codeHmac, Instant expiresAt,
    Instant now);

  /**
   * Marks the row consumed only while it is not consumed, has fewer than {@link OtpChallenge#MAX_ATTEMPTS} and has
   * not expired at {@code now} (JS-1, FN-012, FN-013, FN-014).
   *
   * @return the rows affected: 1, or 0 when any condition fails.
   */
  public int consume(String nonce, Instant now);

  /**
   * Deletes the challenges that expired before the cutoff (OPS-003).
   *
   * @return the rows deleted.
   */
  public int deleteExpiredBefore(Instant olderThan);

  /**
   * Marks every not-yet-consumed challenge of the email as consumed (superseded, DD-6).
   *
   * @return the rows affected.
   */
  public int supersede(String emailHmac, Instant now);
}
