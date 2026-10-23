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

package org.cgiar.ccafs.marlo.security.otp;

import org.cgiar.ccafs.marlo.utils.LogSanitizer;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The only log writer of the Center email one-time-code sign-in (CHG-OTP-LOGIN-001-T05, OPS-001, OPS-002, T16).
 * <p>
 * One line per call, in the shape {@code auth.otp.<request|verify|delivery> outcome=<enum> gu=<acronym>
 * domain=<domain> ms=<n>}. The outcome comes from a closed enum. The acronym and the domain are interpolated through
 * {@link LogSanitizer#sanitizeForLog(String)}, so a forged line break cannot add a line, and are then reduced to
 * {@code [a-z0-9.-]} (anything else becomes {@code _}), so they cannot forge a {@code key=value} field either. A
 * {@code null} outcome logs as {@code internal_error}. The methods take no code,
 * nonce, email or session id, so none of them can reach a line. A delivery failure is logged at WARN (OPS-002), every
 * other line at INFO.
 * <p>
 * A request line whose code was issued only because the unit's password switch is off ends with
 * {@code via=password_off} (CHG-PWD-SWITCH-001 design 11). Only {@code outcome=sent} can carry it: a neutral, refused
 * or failed request issued no real code, and the field must say nothing about an account that got none.
 */
public final class OtpLog {

  /** The closed set of {@code auth.otp.request} outcomes. */
  public enum RequestOutcome {
    SENT, NEUTRAL, DENIED_DOMAIN, TERMS_REQUIRED, RATE_LIMITED, UNCONFIGURED, INTERNAL_ERROR, BAD_METHOD
  }

  /** The closed set of {@code auth.otp.verify} outcomes. */
  public enum VerifyOutcome {
    OK, MISMATCH, EXHAUSTED, INVALID, NOT_ELIGIBLE, RATE_LIMITED, FAILED, INTERNAL_ERROR, BAD_METHOD
  }

  /** The closed set of {@code auth.otp.delivery} outcomes. */
  public enum DeliveryOutcome {
    DELIVERED, EMAIL_FAILED
  }

  private static final Logger LOG = LoggerFactory.getLogger(OtpLog.class);

  private static final String LINE = "auth.otp.{} outcome={} gu={} domain={} ms={}";

  /** The same line for a code issued by the password-off override: one closed, fixed suffix. */
  private static final String LINE_VIA_PASSWORD_OFF = LINE + " via=password_off";

  private OtpLog() {
    throw new AssertionError("utility class, never instantiated");
  }

  /** A null outcome is a caller bug; it is logged as this rather than thrown out of a log call. */
  private static final String NULL_OUTCOME = "internal_error";

  private static String name(Enum<?> outcome) {
    return outcome == null ? NULL_OUTCOME : outcome.name().toLowerCase(Locale.ROOT);
  }

  /**
   * Reduces an attacker-influenced value to {@code [a-z0-9.-]}: line breaks and length go through
   * {@link LogSanitizer}, ASCII letters are lower-cased, and every other character, a space, {@code =} or a
   * non-ASCII one included, becomes {@code _}. A value can therefore never forge a {@code key=value} field. Non-ASCII
   * characters are never case-folded onto ASCII ones.
   */
  private static String field(String value) {
    String clean = LogSanitizer.sanitizeForLog(value);
    StringBuilder reduced = new StringBuilder(clean.length());
    for (int i = 0; i < clean.length(); i++) {
      char c = clean.charAt(i);
      if (c >= 'A' && c <= 'Z') {
        reduced.append((char) (c + ('a' - 'A')));
      } else if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.' || c == '-') {
        reduced.append(c);
      } else {
        reduced.append('_');
      }
    }
    return reduced.toString();
  }

  /**
   * Writes the one {@code auth.otp.request} line of a request-code call.
   *
   * @param outcome the outcome
   * @param gu the Global Unit acronym, possibly {@code null}
   * @param domain the email domain, possibly {@code null}
   * @param ms the duration of the call in milliseconds
   */
  public static void request(RequestOutcome outcome, String gu, String domain, long ms) {
    LOG.info(LINE, "request", name(outcome), field(gu), field(domain), ms);
  }

  /**
   * Writes the one {@code auth.otp.request} line of a request-code call, with {@code via=password_off} at its end when
   * the code was issued only because the unit's password switch is off. The suffix is written for
   * {@link RequestOutcome#SENT} only, whatever the caller passes: no other outcome issued a real code.
   *
   * @param outcome the outcome
   * @param gu the Global Unit acronym, possibly {@code null}
   * @param domain the email domain, possibly {@code null}
   * @param ms the duration of the call in milliseconds
   * @param viaPasswordOff {@code true} when the code was issued by the password-off override
   */
  public static void request(RequestOutcome outcome, String gu, String domain, long ms, boolean viaPasswordOff) {
    if (viaPasswordOff && outcome == RequestOutcome.SENT) {
      LOG.info(LINE_VIA_PASSWORD_OFF, "request", name(outcome), field(gu), field(domain), ms);
    } else {
      request(outcome, gu, domain, ms);
    }
  }

  /**
   * Writes the one {@code auth.otp.verify} line of a verify-code call.
   *
   * @param outcome the outcome
   * @param gu the Global Unit acronym, possibly {@code null}
   * @param domain the email domain, possibly {@code null}
   * @param ms the duration of the call in milliseconds
   */
  public static void verify(VerifyOutcome outcome, String gu, String domain, long ms) {
    LOG.info(LINE, "verify", name(outcome), field(gu), field(domain), ms);
  }

  /**
   * Writes the one {@code auth.otp.delivery} line of an email handed to delivery: INFO when delivered, WARN when it
   * failed.
   *
   * @param outcome the outcome
   * @param gu the Global Unit acronym, possibly {@code null}
   * @param domain the email domain, possibly {@code null}
   * @param ms the duration of the delivery in milliseconds
   */
  public static void delivery(DeliveryOutcome outcome, String gu, String domain, long ms) {
    if (outcome != DeliveryOutcome.DELIVERED) {
      LOG.warn(LINE, "delivery", name(outcome), field(gu),
        field(domain), ms);
    } else {
      LOG.info(LINE, "delivery", name(outcome), field(gu),
        field(domain), ms);
    }
  }
}
