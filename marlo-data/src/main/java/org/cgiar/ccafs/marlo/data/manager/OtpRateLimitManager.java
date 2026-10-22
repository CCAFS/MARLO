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

import java.time.Duration;
import java.time.Instant;

/**
 * The shared rate-limit store of the Center email one-time-code sign-in (CHG-OTP-LOGIN-001-T04, SEC-010, FN-008,
 * DA-003, DD-8).
 * <p>
 * The state lives in the database, so every application instance reads the same values. Each method runs in a
 * transaction of its own that never joins the caller's, so a count or a purge is committed (and its row lock
 * released) when the method returns, whatever the caller later does. A store failure is an exception: the caller
 * decides what a failure means, and for a limiter it means refused.
 */
public interface OtpRateLimitManager {

  /**
   * Counts one hit of the bucket in the fixed window and reads the total back, in one transaction.
   *
   * @param bucket the hex MAC of {@code <route>|<dimension>|<value>}.
   * @param windowStart the start of the fixed window.
   * @return the hits of the bucket in that window, this one included.
   * @throws IllegalStateException if the count cannot be read back.
   */
  public int hit(String bucket, Instant windowStart);

  /**
   * Accepts a request when the last accepted one of the bucket was at least {@code minimumSpacing} before
   * {@code now} (FN-008, RJ-1), in one transaction:
   * <ol>
   * <li>a no-op upsert ensures the row exists and takes its exclusive lock, so the requests of one bucket
   * serialize;</li>
   * <li>{@code SELECT ... FOR UPDATE} reads {@code last_request_at};</li>
   * <li>Java decides: when the stored value is at least {@code minimumSpacing} older it is set to {@code now} and
   * the request is accepted, otherwise the row is left unchanged.</li>
   * </ol>
   * The row lock, not a comparison of values, picks the single winner, so exactly one of several requests that
   * carry the same millisecond is accepted.
   *
   * @return true when this request was accepted; false when the earlier request is too recent, in which case nothing
   *         was changed.
   * @throws IllegalStateException if the row cannot be read after it was locked, or the update does not touch exactly
   *         one row. A lock timeout or a deadlock also reaches the caller as an exception.
   */
  public boolean acceptCooldown(String bucket, Instant now, Duration minimumSpacing);

  /**
   * Deletes the window counters that started before the cutoff.
   *
   * @return the rows deleted.
   */
  public int purgeWindowsBefore(Instant cutoff);

  /**
   * Deletes the cooldowns whose last accepted request was before the cutoff.
   *
   * @return the rows deleted.
   */
  public int purgeCooldownsBefore(Instant cutoff);
}
