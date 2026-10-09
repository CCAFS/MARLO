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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;

import java.util.Comparator;

/**************
 * The order the Outcomes section renders its indicators in: the deprecated ones last, and within
 * each group by id.
 * This is the single definition of that order, and it has to stay that way. The validator reports
 * every gap against the position of the indicator in the list it was handed -- "outcomesForm[3]" --
 * and the front end resolves that key against the card sitting in that position on screen. If the
 * two lists are ordered differently the report lands on the wrong card, which is what happened
 * while the validator read straight from CrpProgram.getCrpProgramOutcomes(): that is a HashSet, so
 * its iteration order is arbitrary and had nothing to do with the rendered order.
 **************/
public class OutcomeComparators {

  /**
   * @return the comparator that puts a list of indicators in the order the form renders them
   */
  public static Comparator<CrpProgramOutcome> renderOrder() {
    return Comparator
      .comparing((CrpProgramOutcome outcome) -> OutcomeComparators.isDeprecated(outcome))
      .thenComparing(CrpProgramOutcome::getId);
  }

  /**
   * @param outcome the indicator to inspect
   * @return whether its statement marks it as deprecated
   */
  private static boolean isDeprecated(CrpProgramOutcome outcome) {
    String description = outcome == null ? null : outcome.getDescription();
    return description != null
      && description.toLowerCase().contains(APConstants.CRP_PROGRAM_OUTCOME_DEPRECATED.toLowerCase());
  }

  private OutcomeComparators() {
  }
}
