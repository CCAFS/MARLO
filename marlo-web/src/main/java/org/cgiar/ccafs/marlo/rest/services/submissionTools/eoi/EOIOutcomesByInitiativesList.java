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

package org.cgiar.ccafs.marlo.rest.services.submissionTools.eoi;

import java.io.Serializable;
import java.util.List;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class EOIOutcomesByInitiativesList implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  List<EndOfInitiativeOutcome> eoi_outcome_by_initiatives;


  public List<EndOfInitiativeOutcome> getEoi_outcome_by_initiatives() {
    return eoi_outcome_by_initiatives;
  }

  public void setEoi_outcome_by_initiatives(List<EndOfInitiativeOutcome> eoi_outcome_by_initiatives) {
    this.eoi_outcome_by_initiatives = eoi_outcome_by_initiatives;
  }
}
