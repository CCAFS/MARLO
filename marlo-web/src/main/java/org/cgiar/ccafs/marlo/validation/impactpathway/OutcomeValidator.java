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


package org.cgiar.ccafs.marlo.validation.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.model.CrpAssumption;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.SectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Named;

/**
 * @author Christian David Garcia -CIAT/CCAFS
 */

@Named
public class OutcomeValidator extends BaseValidator

{

  public OutcomeValidator() {

  }


  private Path getAutoSaveFilePath(CrpProgram program) {
    String composedClassName = program.getClass().getSimpleName();
    String actionFile = SectionStatusEnum.OUTCOMES.getStatus().replace("/", "_");
    String autoSaveFile = program.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, List<CrpProgramOutcome> outcomes, CrpProgram program, boolean saving) {
    // BaseValidator does not Clean this variables.. so before validate the section, it be clear these variables
    this.missingFields.setLength(0);
    this.validationMessage.setLength(0);
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(program);

      if (path.toFile().exists()) {
        this.addMissingField("program.outcomes.draft");
      }
    }


    if (outcomes.size() == 0) {
      this.addMissingField("program.outcomes");
      action.getInvalidFields().put("list-outcomes",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Outcomes"}));

    }
    for (int i = 0; i < outcomes.size(); i++) {
      CrpProgramOutcome outcome = outcomes.get(i);
      this.validateOuctome(action, outcome, i);
    }
    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    }
    this.saveMissingFieldsImpactPathway(program, "outcomes", action.getActualPhase().getYear(),
      action.getActualPhase().getDescription());
  }


  public void validateAssumption(BaseAction action, CrpAssumption assuption, int i, int j, int k) {
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));
    params.add(String.valueOf(j + 1));
    params.add(String.valueOf(k + 1));


    if (!(this.isValidString(assuption.getDescription()) && this.wordCount(assuption.getDescription()) <= 100)) {
      this.addMessage(action.getText("outcome.action.subido.assumption.required", params));
      action.getInvalidFields().put("input-outcomes[" + i + "].subIdos[" + j + "].assumptions[" + k + "].description",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }

  public void validateMilestone(BaseAction action, CrpMilestone milestone, int i, int j) {

    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));
    params.add(String.valueOf(j + 1));
    if (!(this.isValidString(milestone.getTitle()) && this.wordCount(milestone.getTitle()) <= 100)) {
      action.getInvalidFields().put("input-outcomes[" + i + "].milestones[" + j + "].title",
        InvalidFieldsMessages.EMPTYFIELD);
      this.addMessage(action.getText("outcome.action.title.required", params));
    }

    /*
     * if (milestone.getValue() == null || !this.isValidNumber(milestone.getValue().toString())) {
     * this.addMessage(action.getText("outcome.action.milestone.value.required", params));
     * }
     */

    if (!this.isValidNumber(String.valueOf(milestone.getYear())) || milestone.getYear() <= 0) {
      this.addMessage(action.getText("outcome.action.milestone.year.required", params));
      action.getInvalidFields().put("input-outcomes[" + i + "].milestones[" + j + "].year",
        InvalidFieldsMessages.EMPTYFIELD);
    }


    if (milestone.getCrpProgramOutcome() != null && milestone.getCrpProgramOutcome().getYear() != null) {
      if (milestone.getYear() == null
        || (milestone.getCrpProgramOutcome().getYear().intValue() < milestone.getYear().intValue())) {
        action.getInvalidFields().put("input-outcomes[" + i + "].milestones[" + j + "].year",
          InvalidFieldsMessages.EMPTYFIELD);
        this.addMessage(action.getText("outcome.action.milestone.year.required", params));
      }
    }

    if (milestone.getSrfTargetUnit() != null && milestone.getSrfTargetUnit().getId() != -1) {

      if (milestone.getValue() == null || !this.isValidNumber(milestone.getValue().toString())) {
        this.addMessage(action.getText("outcome.action.milestone.value.required", params));
        action.getInvalidFields().put("input-outcomes[" + i + "].milestones[" + j + "].value",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

  }

  public void validateOuctome(BaseAction action, CrpProgramOutcome outcome, int i) {
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));


    if (!(this.isValidString(outcome.getDescription()) && this.wordCount(outcome.getDescription()) <= 100)) {
      this.addMessage(action.getText("outcome.action.statement.required", params));
      action.getInvalidFields().put("input-outcomes[" + i + "].description", InvalidFieldsMessages.EMPTYFIELD);


    }


    if (outcome.getSrfTargetUnit() != null && outcome.getSrfTargetUnit().getId() != null
      && outcome.getSrfTargetUnit().getId().longValue() != -1) {
      if (outcome.getValue() == null || !this.isValidNumber(outcome.getValue().toString())) {
        this.addMessage(action.getText("outcome.action.value.required", params));
        action.getInvalidFields().put("input-outcomes[" + i + "].value", InvalidFieldsMessages.EMPTYFIELD);
      }
    }


    if (!this.isValidNumber(String.valueOf(outcome.getYear())) || (outcome.getYear() <= 0)) {
      this.addMessage(action.getText("outcome.action.year.required", params));
      action.getInvalidFields().put("input-outcomes[" + i + "].year", InvalidFieldsMessages.EMPTYFIELD);
    }

    /*
     * if (outcome.getSrfTargetUnit() == null || outcome.getSrfTargetUnit().getId() == -1) {
     * outcome.setSrfTargetUnit(null);
     * this.addMessage(action.getText("outcome.action.srfTargetUnit.required", params));
     * action.getInvalidFields().put("input-outcomes[" + i + "].srfTargetUnit.id", InvalidFieldsMessages.EMPTYFIELD);
     * }
     */
    if (outcome.getMilestones() != null && !outcome.getMilestones().isEmpty()) {
      for (int j = 0; j < outcome.getMilestones().size(); j++) {
        outcome.getMilestones().get(j).setCrpProgramOutcome(outcome);
        this.validateMilestone(action, outcome.getMilestones().get(j), i, j);
      }
    } else {
      this.addMessage("outcome.action.milestones.requeried");

      action.getInvalidFields().put("list-outcomes[" + i + "].milestones",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Milestones"}));
    }
    if (outcome.getSubIdos() != null) {
      if (outcome.getSubIdos().isEmpty()) {
        this.addMessage(action.getText("outcome.action.subido.requeried", params));
        action.getInvalidFields().put("list-outcomes[" + i + "].subIdos",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Sub Idos"}));
      }
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

        for (int j = 0; j < outcome.getSubIdos().size(); j++) {
          action.getInvalidFields().put("input-outcomes[" + i + "].subIdos[" + j + "].contribution",
            InvalidFieldsMessages.EMPTYFIELD);
        }


      }
    } else {
      this.addMessage(action.getText("outcome.action.subido.requeried", params));

      action.getInvalidFields().put("list-outcomes[" + i + "].subIdos",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Sub Idos"}));
    }


  }

  public void validateSubIDO(BaseAction action, CrpOutcomeSubIdo subIdo, int i, int j) {

    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));
    params.add(String.valueOf(j + 1));
    if (subIdo.getSrfSubIdo() == null || subIdo.getSrfSubIdo().getId() == null || subIdo.getSrfSubIdo().getId() == -1) {
      subIdo.setSrfSubIdo(null);
      this.addMessage(action.getText("outcome.action.subido.subido.required", params));
      action.getInvalidFields().put("input-outcomes[" + i + "].subIdos[" + j + "].srfSubIdo.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (subIdo.getContribution() == null || !this.isValidNumber(subIdo.getContribution().toString())
      || subIdo.getContribution().doubleValue() > 100) {
      this.addMessage(action.getText("outcome.action.subido.contribution.required", params));
      action.getInvalidFields().put("input-outcomes[" + i + "].subIdos[" + j + "].contribution",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    /*
     * int k = 0;
     * if (subIdo.getAssumptions() != null) {
     * for (CrpAssumption crpAssumption : subIdo.getAssumptions()) {
     * this.validateAssumption(action, crpAssumption, i, j, k);
     * k++;
     * }
     * }
     */
  }
}
