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

import org.apache.commons.lang3.StringUtils;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public enum PhaseNames {

  POWB("POWB", 1), UPKEEP("Upkeep", 2), AR("AR", 3);

  public static PhaseNames getByName(String phaseName) {
    for (PhaseNames name : PhaseNames.values()) {
      if (StringUtils.equalsIgnoreCase(name.getPhaseName(), phaseName)) {
        return name;
      }
    }

    return null;
  }

  private String phaseName;

  private int order;

  private PhaseNames(String phaseName, int order) {
    this.phaseName = phaseName;
    this.order = order;
  }


  public int getOrder() {
    return order;
  }

  public String getPhaseName() {
    return phaseName;
  }
}
