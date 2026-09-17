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

package org.cgiar.ccafs.marlo.utils;

/**
 * CHG-COGNITO-AUTH-001-T14 (audit finding, OPS-001): strips log-forging control characters from a value
 * before it is interpolated into a log line.
 * <p>
 * <b>Why this exists.</b> {@code CognitoLoginAction}, {@code LoginAction} and {@code ValidateUserAction}
 * each log an attacker-supplied {@code email} on an <b>unauthenticated</b> endpoint, before that value has
 * been proven to belong to anyone. Without this, a submitted value such as
 * {@code a%0A2026-09-02 10:00:00 INFO LoginAction - User victim@cgiar.org logged in successfully for
 * Global Unit CCAFS.} writes a fabricated line into the same file the real log line occupies -- a log
 * injection into the very audit trail OPS-001 exists to make trustworthy.
 * <p>
 * This is deliberately narrow: it strips {@code \r} and {@code \n} (the two characters that let a single
 * log call masquerade as several lines) and bounds length, nothing more. It does not attempt to detect or
 * reject malicious input -- that is not this method's job, and BaseAction validation is a separate
 * concern -- it only ensures one log call produces exactly one line, of bounded size.
 */
public final class LogSanitizer {

  /** Caps a sanitized value's length so one field cannot make a single log line unbounded. */
  private static final int MAX_LENGTH = 200;

  private static final String TRUNCATION_SUFFIX = "...(truncated)";

  private LogSanitizer() {
    throw new AssertionError("utility class, never instantiated");
  }

  /**
   * @param value the raw, possibly attacker-controlled value about to be interpolated into a log line
   * @return {@code value} with every {@code \r} and {@code \n} removed and length capped at
   *         {@value #MAX_LENGTH}, or the empty string when {@code value} is {@code null}. Never {@code null}
   */
  public static String sanitizeForLog(String value) {
    if (value == null) {
      return "";
    }
    String stripped = value.replace("\r", "").replace("\n", "");
    if (stripped.length() > MAX_LENGTH) {
      return stripped.substring(0, MAX_LENGTH) + TRUNCATION_SUFFIX;
    }
    return stripped;
  }
}
