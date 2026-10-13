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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * The pure email and allow-list rules of the Center email one-time-code sign-in (CHG-OTP-LOGIN-001-T05, FN-003,
 * SEC-009).
 * <p>
 * Eligibility by domain is <b>exact equality</b> between the domain of the normalised email and an entry of the
 * parsed allow-list. There is no suffix, substring or sub-domain matching: {@code x@attackertrusted.org},
 * {@code x@trusted.org.evil.com} and {@code x@sub.trusted.org} do not match {@code trusted.org}, and a sub-domain
 * qualifies only when it is listed itself.
 * <p>
 * The class is stateless and never throws on malformed input; a malformed email simply has no domain.
 */
public final class OtpDomainPolicy {

  /** SEC-009: an allow-list entry of this domain is always ignored. */
  private static final String IGNORED_DOMAIN = "cgiar.org";

  private static final char AT = '@';
  private static final char FIRST_PRINTABLE = 0x21;
  private static final char LAST_PRINTABLE = 0x7E;
  private static final String MASK_FILLER = "***";

  private OtpDomainPolicy() {
    throw new AssertionError("utility class, never instantiated");
  }

  /**
   * @param email the raw email, possibly {@code null}
   * @return the email trimmed and lower-cased, or the empty string for {@code null} and for any email with a
   *         character outside printable ASCII (0x21-0x7E after the trim), checked before and after lower-casing.
   *         Never {@code null}
   */
  public static String normaliseEmail(String email) {
    if (email == null) {
      return "";
    }
    String trimmed = email.trim();
    if (!isPrintableAscii(trimmed)) {
      return "";
    }
    String lowerCased = trimmed.toLowerCase(Locale.ROOT);
    return isPrintableAscii(lowerCased) ? lowerCased : "";
  }

  /**
   * FN-003 (amended 2026-10-13): Unicode case folding maps, for example, the Kelvin sign U+212A onto {@code k}, so a
   * lookalike address could normalise onto another account's. Anything outside printable ASCII is refused instead.
   */
  private static boolean isPrintableAscii(String value) {
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      if (c < FIRST_PRINTABLE || c > LAST_PRINTABLE) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param email the raw email, possibly {@code null}
   * @return the domain of the normalised email, which is the part after its single {@code @}, or {@code null} when
   *         the email does not have exactly one {@code @} or when its local part or its domain is empty
   */
  public static String domainOf(String email) {
    String normalised = normaliseEmail(email);
    int at = normalised.indexOf(AT);
    if (at <= 0 || at != normalised.lastIndexOf(AT) || at == normalised.length() - 1) {
      return null;
    }
    return normalised.substring(at + 1);
  }

  /**
   * @param email the raw email, possibly {@code null}
   * @param allowList the entries returned by {@link #parseAllowList(String)}; may be {@code null}
   * @return {@code true} only when the email has a domain and that domain equals an entry of the list exactly
   */
  public static boolean isAllowed(String email, Collection<String> allowList) {
    String domain = domainOf(email);
    return domain != null && allowList != null && allowList.contains(domain);
  }

  /**
   * Parses the comma-separated allow-list stored in {@code custom_parameters}. Each entry is trimmed, lower-cased
   * and stripped of one leading {@code @}; it is dropped when it is then empty or still contains an {@code @}, and
   * duplicates are removed. An entry with a character outside printable ASCII is dropped (FN-003). An entry of
   * {@code cgiar.org} is dropped too (SEC-009).
   *
   * @param raw the stored value, possibly {@code null}
   * @return the normalised domains in first-seen order. Never {@code null}
   */
  public static List<String> parseAllowList(String raw) {
    if (raw == null) {
      return Collections.emptyList();
    }
    Set<String> domains = new LinkedHashSet<String>();
    for (String part : raw.split(",")) {
      String trimmed = part.trim();
      if (!isPrintableAscii(trimmed)) {
        continue;
      }
      String entry = trimmed.toLowerCase(Locale.ROOT);
      if (!isPrintableAscii(entry)) {
        continue;
      }
      if (entry.startsWith("@")) {
        entry = entry.substring(1);
      }
      if (entry.isEmpty() || entry.indexOf(AT) >= 0 || IGNORED_DOMAIN.equals(entry)) {
        continue;
      }
      domains.add(entry);
    }
    return new ArrayList<String>(domains);
  }

  /**
   * @param email the raw email, possibly {@code null}
   * @return the normalised email with all of its local part but the first character replaced by {@code ***}
   *         ({@code ana@partner.org} gives {@code a***@partner.org}), or just {@code ***} when the email has no
   *         domain
   */
  public static String mask(String email) {
    String domain = domainOf(email);
    if (domain == null) {
      return MASK_FILLER;
    }
    return normaliseEmail(email).charAt(0) + MASK_FILLER + AT + domain;
  }
}
