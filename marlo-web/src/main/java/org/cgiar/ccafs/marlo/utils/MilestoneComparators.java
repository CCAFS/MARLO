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

import org.cgiar.ccafs.marlo.data.model.CrpMilestone;

import java.util.Comparator;


/**************
 * Null-safe comparator
 * 
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class MilestoneComparators {

  public static class ComposedIdComparator implements Comparator<CrpMilestone> {

    @Override
    public int compare(CrpMilestone m1, CrpMilestone m2) {
      return Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER).compare(m1.getComposeID(), m2.getComposeID());
    }
  }

  public static class YearComparator implements Comparator<CrpMilestone> {

    @Override
    public int compare(CrpMilestone m1, CrpMilestone m2) {
      // Integer yearM1 = m1.getYear();
      // Integer yearM2 = m2.getYear();
      Integer yearM1 = m1.getExtendedYear();
      Integer yearM2 = m2.getExtendedYear();

      if (yearM1 == null || yearM1 == -1) {
        yearM1 = m1.getYear();
      }

      if (yearM2 == null || yearM2 == -1) {
        yearM2 = m2.getYear();
      }

      if (yearM1 == null || yearM1 == -1) {
        if (yearM2 == null || yearM1 == -1) {
          return 0;
        } else {
          return -1;
        }
      }

      if (yearM2 == null || yearM2 == -1) {
        return 1;
      }

      return yearM1.compareTo(yearM2);
    }
  }
}
