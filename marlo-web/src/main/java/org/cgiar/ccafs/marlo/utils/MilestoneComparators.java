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

import org.apache.commons.collections4.comparators.ComparatorChain;


/**************
 * Null-safe comparator
 * 
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class MilestoneComparators {

  /**
   * The order the Outcomes section renders an indicator's period targets in.
   * This is the single definition of that order, and it has to stay that way. The validator
   * reports each gap against the position of the period target in the list it was handed --
   * "outcomesForm[0].milestones[9].value" -- and the front end resolves that key against the
   * cell rendered at that position. CrpProgramOutcome.getCrpMilestones() is a HashSet, so a
   * caller that does not sort gets an arbitrary order and every finding lands on the wrong cell.
   *
   * @return the comparator that puts period targets in the order the form renders them
   */
  public static Comparator<CrpMilestone> renderOrder() {
    return new ComparatorChain<CrpMilestone>(new YearComparator()).thenComparing(new ComposedIdComparator());
  }

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
