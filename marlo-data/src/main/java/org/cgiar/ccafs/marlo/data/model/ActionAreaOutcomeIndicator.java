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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.model;

import org.apache.commons.lang3.StringUtils;

public class ActionAreaOutcomeIndicator extends MarloAuditableEntity implements java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  public ActionAreaOutcome actionAreaOutcome;
  private OutcomeIndicator outcomeIndicator;
  private ActionArea actionArea;


  public ActionArea getActionArea() {
    return actionArea;
  }

  public ActionAreaOutcome getActionAreaOutcome() {
    return actionAreaOutcome;
  }

  public String getComposedName() {
    String composedName = "";
    if (this.getId() == null || this.getId() == -1) {
      return "<Not defined>";
    } else {
      if (this.getActionArea() != null && this.getActionArea().getId() != null
        && StringUtils.isNotBlank(this.getActionArea().getName())) {
        composedName = "<b>" + this.getActionArea().getName() + ":</b> ";
      }

      if (this.getActionAreaOutcome() != null && this.getActionAreaOutcome().getId() != null
        && StringUtils.isNotBlank(this.getActionAreaOutcome().getSmoCode())) {
        composedName += "(<i>" + this.getActionAreaOutcome().getSmoCode() + ")</i> ";
      }

      if (this.getOutcomeIndicator() != null && this.getOutcomeIndicator().getId() != null
        && StringUtils.isNotBlank(this.getOutcomeIndicator().getOutcomeIndicatorStatement())) {
        composedName += " -  " + this.getOutcomeIndicator().getOutcomeIndicatorStatement();
      }
    }

    return composedName;
  }

  public OutcomeIndicator getOutcomeIndicator() {
    return outcomeIndicator;
  }


  public void setActionArea(ActionArea actionArea) {
    this.actionArea = actionArea;
  }

  public void setActionAreaOutcome(ActionAreaOutcome actionAreaOutcome) {
    this.actionAreaOutcome = actionAreaOutcome;
  }

  public void setOutcomeIndicator(OutcomeIndicator outcomeIndicator) {
    this.outcomeIndicator = outcomeIndicator;
  }
}
