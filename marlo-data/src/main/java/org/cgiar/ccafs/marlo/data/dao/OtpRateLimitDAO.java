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

import java.time.Instant;

/**
 * Storage of the rate-limit counters and cooldowns of the Center email one-time-code sign-in
 * (CHG-OTP-LOGIN-001-T04, SEC-010, FN-008, DA-003).
 * <p>
 * The tables hold throw-away counters keyed by an HMAC bucket, so no raw email or address is ever stored. The caller
 * owns the transaction. Every {@link Instant} is written as a UTC wall clock: the Java clock is the only time source.
 */
public interface OtpRateLimitDAO {

  /**
   * Adds one to the counter of the bucket in the fixed window, creating it at 1 when it does not exist yet. A single
   * atomic upsert, so concurrent requests for one bucket cannot lose a count (JI-12).
   *
   * @param bucket the hex MAC of {@code <route>|<dimension>|<value>}.
   * @param windowStart the start of the fixed window, a UTC wall clock.
   * @return the rows affected, which the caller must not interpret: the answer is read back with
   *         {@link #findHits(String, Instant)}.
   */
  public int incrementWindow(String bucket, Instant windowStart);

  /**
   * Reads the counter of the bucket in the window, as a fresh read of the database.
   *
   * @return the hits, or null when the bucket has no row in that window.
   */
  public Integer findHits(String bucket, Instant windowStart);

  /**
   * Ensures the cooldown row of the bucket exists and takes its exclusive lock, with a no-op upsert: a new row is
   * written with {@code sentinel}, an existing one is locked and left unchanged. The lock is exclusive from this
   * first statement and is held until the caller's transaction ends, so the requests of one bucket serialize and
   * the lock is never upgraded from shared (an {@code INSERT IGNORE} would take a shared lock first, and two
   * sessions that then both ask for the exclusive one deadlock).
   *
   * @param sentinel a far-past instant for a new row, so the first request is always due.
   * @return the rows affected, which the caller must not interpret.
   */
  public int lockCooldown(String bucket, Instant sentinel);

  /**
   * Reads {@code last_request_at} of the bucket with {@code SELECT ... FOR UPDATE}: the committed value, read under
   * the exclusive lock. It is returned as the UTC wall clock the column stores, through its text, so no JVM or
   * driver time zone takes part.
   *
   * @return the stored instant, or null when the bucket has no row.
   */
  public Instant findLastRequestAtForUpdate(String bucket);

  /**
   * Sets {@code last_request_at} of the bucket to {@code now}. The caller holds the exclusive lock.
   *
   * @return the rows affected: 1.
   */
  public int updateCooldown(String bucket, Instant now);

  /**
   * Deletes the window counters that started before the cutoff.
   *
   * @return the rows deleted.
   */
  public int deleteWindowsBefore(Instant olderThan);

  /**
   * Deletes the cooldowns whose last accepted request was before the cutoff.
   *
   * @return the rows deleted.
   */
  public int deleteCooldownsBefore(Instant olderThan);
}
