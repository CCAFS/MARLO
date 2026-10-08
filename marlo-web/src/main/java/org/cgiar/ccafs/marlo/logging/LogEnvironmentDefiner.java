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

package org.cgiar.ccafs.marlo.logging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ch.qos.logback.core.PropertyDefinerBase;

/**
 * Defines the logback property "environment" (DEV, TEST or PROD), the environment field of the logging standard
 * proposed for the CGIAR tools (ENH-LOGGING-STANDARDIZATION-001). logback.xml computes it once, when the logging
 * configuration loads, so that it is printed on every line, startup lines and scheduled jobs included, and not only on
 * the lines of a request.
 * <p>
 * The value is derived from the Spring active profile, which is already set on every server, so that no new property
 * is needed: pro is PROD, test is TEST, and dev or fast is DEV. With no profile the value is DEV, the profile
 * ApplicationContextConfig falls back to. A profile outside that list is printed upper-cased rather than guessed.
 * The optional property marlo.environment overrides the derived value, for the case where one profile serves two
 * machines.
 * <p>
 * Only up to 20 letters, digits, '_' and '-' are accepted, so that the value can never split or forge a log line;
 * anything else, like a failure, gives UNKNOWN. This class never throws, so that it can never break the logging
 * configuration it is part of.
 */
public class LogEnvironmentDefiner extends PropertyDefinerBase {

  static final String DEV = "DEV";

  static final String PROD = "PROD";

  static final String TEST = "TEST";

  static final String UNKNOWN = "UNKNOWN";

  private static final Pattern SAFE_VALUE = Pattern.compile("[A-Za-z0-9_-]{1,20}");

  private static final Pattern PROFILE_SEPARATOR = Pattern.compile(",");

  // What logback 1.2 substitutes for ${x:-} when x is not set: an empty default counts as no default
  private static final String UNDEFINED_SUFFIX = "_IS_UNDEFINED";

  /**
   * Returns the environment for a Spring active profile list and an optional override.
   *
   * @param override the value of marlo.environment, may be null, blank or logback's undefined marker
   * @param profiles the value of spring.profiles.active, a comma-separated list, may be null or blank
   * @return DEV, TEST, PROD, the upper-cased override or unknown profile, or UNKNOWN; never null
   */
  static String resolve(String override, String profiles) {
    try {
      if (override != null && !override.trim().isEmpty() && !override.endsWith(UNDEFINED_SUFFIX)) {
        return safeUpperCase(override.trim());
      }
      List<String> active = profiles == null ? Collections.<String>emptyList()
        : Arrays.stream(PROFILE_SEPARATOR.split(profiles)).map(String::trim).filter(profile -> !profile.isEmpty())
          .map(profile -> profile.toLowerCase(Locale.ROOT)).collect(Collectors.toList());
      if (active.contains("pro")) {
        return PROD;
      }
      if (active.contains("test")) {
        return TEST;
      }
      if (active.isEmpty() || active.contains("dev") || active.contains("fast")) {
        return DEV;
      }
      return safeUpperCase(active.get(0));
    } catch (RuntimeException e) {
      return UNKNOWN;
    }
  }

  private static String safeUpperCase(String value) {
    return SAFE_VALUE.matcher(value).matches() ? value.toUpperCase(Locale.ROOT) : UNKNOWN;
  }

  private String override;

  private String profiles;

  @Override
  public String getPropertyValue() {
    return resolve(this.override, this.profiles);
  }

  /**
   * @param override the value of marlo.environment, set from logback.xml
   */
  public void setOverride(String override) {
    this.override = override;
  }

  /**
   * @param profiles the value of spring.profiles.active, set from logback.xml
   */
  public void setProfiles(String profiles) {
    this.profiles = profiles;
  }
}
