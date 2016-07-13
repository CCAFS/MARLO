/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning & 
 * Outcomes Platform (MARLO). * MARLO is free software: you can redistribute it and/or modify
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


package org.cgiar.ccafs.marlo.validation.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.model.CrpAssumption;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
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

  public void validate(BaseAction action, List<CrpProgramOutcome> outcomes, CrpProgram program) {

    if (outcomes.size() == 0) {
      this.addMissingField("program.outcomes");
    }
    for (int i = 0; i < outcomes.size(); i++) {
      CrpProgramOutcome outcome = outcomes.get(i);
      this.validateOuctome(action, outcome, i);
    }
    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }
    this.saveMissingFieldsImpactPathway(program, "outcomes");
  }


  public void validateAssumption(BaseAction action, CrpAssumption assuption, int i, int j, int k) {
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));
    params.add(String.valueOf(j + 1));
    params.add(String.valueOf(k + 1));
    if (!(this.isValidString(assuption.getDescription()) && this.wordCount(assuption.getDescription()) <= 100)) {
      this.addMessage(action.getText("outcome.action.subido.assumption.required", params));
    }
  }

  public void validateMilestone(BaseAction action, CrpMilestone milestone, int i, int j) {

    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));
    params.add(String.valueOf(j + 1));
    if (!(this.isValidString(milestone.getTitle()) && this.wordCount(milestone.getTitle()) <= 100)) {
      this.addMessage(action.getText("outcome.action.title.required", params));
    }
    if (milestone.getValue() == null || !this.isValidNumber(milestone.getValue().toString())) {
      this.addMessage(action.getText("outcome.action.milestone.value.required", params));
    }
    if (!this.isValidNumber(String.valueOf(milestone.getYear())) || milestone.getYear() <= 0) {
      this.addMessage(action.getText("outcome.action.milestone.year.required", params));
    }


    if (milestone.getCrpProgramOutcome() != null && milestone.getCrpProgramOutcome().getYear() != null) {
      if (milestone.getYear() == null
        || (milestone.getCrpProgramOutcome().getYear().intValue() < milestone.getYear().intValue())) {
        this.addMessage(action.getText("outcome.action.milestone.year.required", params));
      }
    }
    if (milestone.getSrfTargetUnit() == null || milestone.getSrfTargetUnit().getId() == -1) {
      this.addMessage(action.getText("outcome.action.milestone.srfTargetUnit.required", params));
      milestone.setSrfTargetUnit(null);
    }
  }

  public void validateOuctome(BaseAction action, CrpProgramOutcome outcome, int i) {
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));
    if (!(this.isValidString(outcome.getDescription()) && this.wordCount(outcome.getDescription()) <= 100)) {
      this.addMessage(action.getText("outcome.action.statement.required", params));
    }
    if (outcome.getValue() == null || !this.isValidNumber(outcome.getValue().toString())) {
      this.addMessage(action.getText("outcome.action.value.required", params));
    }
    if (!this.isValidNumber(String.valueOf(outcome.getYear())) || (outcome.getYear() <= 0)) {
      this.addMessage(action.getText("outcome.action.year.required", params));
    }
    if (outcome.getSrfTargetUnit() == null || outcome.getSrfTargetUnit().getId() == -1) {
      outcome.setSrfTargetUnit(null);
      this.addMessage(action.getText("outcome.action.srfTargetUnit.required", params));
    }
    if (outcome.getMilestones() != null) {
      for (int j = 0; j < outcome.getMilestones().size(); j++) {
        outcome.getMilestones().get(j).setCrpProgramOutcome(outcome);
        this.validateMilestone(action, outcome.getMilestones().get(j), i, j);
      }
      if (outcome.getSubIdos() != null) {
        double contributions = 0;
        for (int j = 0; j < outcome.getSubIdos().size(); j++) {
          outcome.getSubIdos().get(j).setCrpProgramOutcome(outcome);
          this.validateSubIDO(action, outcome.getSubIdos().get(j), i, j);
          if (outcome.getSubIdos().get(j).getContribution() != null) {
            contributions = contributions + outcome.getSubIdos().get(j).getContribution().doubleValue();
          }

        }
        if (contributions != 100) {
          this.addMessage(action.getText("outcome.action.subido.contribution.required", params));
        }
      }

    }
  }

  public void validateSubIDO(BaseAction action, CrpOutcomeSubIdo subIdo, int i, int j) {

    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));
    params.add(String.valueOf(j + 1));
    if (subIdo.getSrfSubIdo() == null || subIdo.getSrfSubIdo().getId() == null || subIdo.getSrfSubIdo().getId() == -1) {
      subIdo.setSrfSubIdo(null);
      this.addMessage(action.getText("outcome.action.subido.subido.required", params));
    }
    if (subIdo.getContribution() == null || !this.isValidNumber(subIdo.getContribution().toString())
      || subIdo.getContribution().doubleValue() > 100) {
      this.addMessage(action.getText("outcome.action.subido.contribution.required", params));
    }
    int k = 0;
    if (subIdo.getAssumptions() != null) {
      for (CrpAssumption crpAssumption : subIdo.getAssumptions()) {
        this.validateAssumption(action, crpAssumption, i, j, k);
        k++;
      }
    }

  }
}
