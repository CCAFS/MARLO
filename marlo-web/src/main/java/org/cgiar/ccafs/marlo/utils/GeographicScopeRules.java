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

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * A2-2706 / A2-2709: the geographic scope rules shared by the deliverable and the innovation forms (the PRMS rules).
 * <p>
 * A record has exactly one scope (rep_ind_geographic_scopes):
 * <ul>
 * <li>1 Global and 6 "This is yet to be determined": nothing else is asked.</li>
 * <li>2 Regional: one or more regions.</li>
 * <li>3 Multi-national: two or more countries.</li>
 * <li>4 National and 5 Sub-national: exactly one country.</li>
 * </ul>
 */
public final class GeographicScopeRules {

  public static final long GLOBAL = 1L;
  public static final long REGIONAL = 2L;
  public static final long MULTI_NATIONAL = 3L;
  public static final long NATIONAL = 4L;
  public static final long SUB_NATIONAL = 5L;
  public static final long TO_BE_DETERMINED = 6L;

  /** Fewest countries a multi-national scope takes. */
  public static final int MULTI_NATIONAL_MIN_COUNTRIES = 2;

  private GeographicScopeRules() {
  }

  /**
   * The scope a record shows when legacy data left it more than one: the broadest, i.e. the lowest catalogue id, so
   * the choice does not depend on the order rows come back in.
   *
   * @param scopeIds the stored scope ids, nulls allowed
   * @return the broadest id, or null when there is none
   */
  public static Long broadest(Collection<Long> scopeIds) {
    if (scopeIds == null) {
      return null;
    }
    return scopeIds.stream().filter(Objects::nonNull).min(Long::compare).orElse(null);
  }

  /**
   * @param scopeId the chosen scope id
   * @return true when the scope asks for countries (multi-national, national, sub-national)
   */
  public static boolean needsCountries(Long scopeId) {
    return scopeId != null
      && (scopeId == MULTI_NATIONAL || scopeId == NATIONAL || scopeId == SUB_NATIONAL);
  }

  /**
   * @param scopeId the chosen scope id
   * @return true when the scope asks for regions
   */
  public static boolean needsRegions(Long scopeId) {
    return scopeId != null && scopeId == REGIONAL;
  }

  /**
   * Reads the one scope a form posted. The form names it {@code <prefix>.geographicScopes[i].repIndGeographicScope.id}
   * and Struts does not build the nested object, so the id is read from the raw parameters. When several are posted
   * (a stale page), the lowest index wins.
   *
   * @param parameters the request parameter map
   * @param prefix the form object name, e.g. "deliverable" or "innovation"
   * @return the posted scope id, or null when none was chosen
   */
  public static Long postedScopeId(Map<String, String[]> parameters, String prefix) {
    if (parameters == null) {
      return null;
    }
    String start = prefix + ".geographicScopes[";
    String end = "].repIndGeographicScope.id";
    Map<Integer, Long> byIndex = new TreeMap<>();
    for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
      String key = entry.getKey();
      if (key == null || !key.startsWith(start) || !key.endsWith(end) || entry.getValue() == null
        || entry.getValue().length == 0) {
        continue;
      }
      try {
        int index = Integer.parseInt(key.substring(start.length(), key.length() - end.length()));
        String value = entry.getValue()[0];
        if (value != null && !value.trim().isEmpty() && !"-1".equals(value.trim())) {
          byIndex.put(index, Long.valueOf(value.trim()));
        }
      } catch (NumberFormatException e) {
        // Not a scope id: ignored, as an empty choice
      }
    }
    return byIndex.isEmpty() ? null : byIndex.values().iterator().next();
  }

  /**
   * @param scopeId the chosen scope id
   * @param countryCount how many countries were chosen
   * @return true when the number of countries fits the scope; always true for a scope that asks for none
   */
  public static boolean validCountryCount(Long scopeId, int countryCount) {
    if (!needsCountries(scopeId)) {
      return true;
    }
    if (scopeId == MULTI_NATIONAL) {
      return countryCount >= MULTI_NATIONAL_MIN_COUNTRIES;
    }
    return countryCount == 1;
  }
}
