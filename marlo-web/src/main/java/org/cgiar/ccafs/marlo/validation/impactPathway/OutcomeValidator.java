/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.validation.impactPathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian David Garcia -CIAT/CCAFS
 */

public class OutcomeValidator extends BaseValidator

{

  @Inject
  public OutcomeValidator() {

  }

  public void validate(BaseAction action, List<CrpProgramOutcome> outcomes) {


    for (int i = 0; i < outcomes.size(); i++) {
      CrpProgramOutcome outcome = outcomes.get(i);
      this.validateOuctome(action, outcome, i);
    }
  }

  public void validateOuctome(BaseAction action, CrpProgramOutcome outcome, int i) {
    if (!this.isValidString(outcome.getDescription())) {
      action.addFieldError("outcomes[" + i + "].description", action.getText("outcome.action.statement.required"));
    }
    if (!this.isValidNumber(outcome.getValue().toString())) {
      action.addFieldError("outcomes[" + i + "].value", action.getText("outcome.action.value.required"));
    }
    if (!this.isValidNumber(String.valueOf(outcome.getYear()))) {
      action.addFieldError("outcomes[" + i + "].year", action.getText("outcome.action.year.required"));
    }
    if (outcome.getSrfTargetUnit() == null || outcome.getSrfTargetUnit().getId() == -1) {
      action.addFieldError("outcomes[" + i + "].srfTargetUnit.id",
        action.getText("outcome.action.srfTargetUnit.required"));
    }
  }

}
