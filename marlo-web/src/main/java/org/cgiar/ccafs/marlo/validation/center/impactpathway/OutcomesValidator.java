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

package org.cgiar.ccafs.marlo.validation.center.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.CenterMilestone;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ImpactPathwaySectionsEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class OutcomesValidator extends BaseValidator {

  // GlobalUnit Manager
  private GlobalUnitManager centerService;


  public OutcomesValidator(GlobalUnitManager centerService) {
    this.centerService = centerService;
  }


  private Path getAutoSaveFilePath(CenterOutcome outcome, long centerID, BaseAction baseAction) {
    GlobalUnit center = centerService.getGlobalUnitById(centerID);
    String composedClassName = outcome.getClass().getSimpleName();
    String actionFile = ImpactPathwaySectionsEnum.OUTCOME.getStatus().replace("/", "_");
    String autoSaveFile = outcome.getId() + "_" + composedClassName + "_" + baseAction.getActualPhase().getDescription()
      + "_" + baseAction.getActualPhase().getYear() + "_" + center.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public void validate(BaseAction baseAction, CenterOutcome outcome, CrpProgram selectedProgram, boolean saving) {
    baseAction.setInvalidFields(new HashMap<>());

    if (!saving) {
      Path path = this.getAutoSaveFilePath(outcome, baseAction.getCenterID(), baseAction);

      if (path.toFile().exists()) {
        baseAction.addMissingField(baseAction.getText("outcome.action.draft"));
      }
    }

    if (!baseAction.getFieldErrors().isEmpty()) {
      baseAction.addActionError(baseAction.getText("saving.fields.required"));
    }

    this.validateOutcome(baseAction, outcome);

    this.saveMissingFields(selectedProgram, outcome, "outcomesList", baseAction);

  }

  public void validateMilestones(BaseAction baseAction, CenterMilestone milestone, int i) {
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));

    if (!this.isValidString(milestone.getTitle()) && this.wordCount(milestone.getTitle()) <= 50) {
      baseAction.addMessage(baseAction.getText("outcome.milestone.action.statement.required"));
      baseAction.getInvalidFields().put("input-outcome.milestones[" + i + "].title", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (milestone.getTargetYear() != null) {
      if (milestone.getTargetYear() == -1) {
        baseAction.addMessage(baseAction.getText("outcome.milestone.action.targetYear.required"));
        baseAction.getInvalidFields().put("input-outcome.milestones[" + i + "].targetYear",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      baseAction.addMessage(baseAction.getText("outcome.milestone.action.targetYear.required"));
      baseAction.getInvalidFields().put("input-outcome.milestones[" + i + "].targetYear",
        InvalidFieldsMessages.EMPTYFIELD);
    }


    if (milestone.getTargetUnit() == null) {
      baseAction.addMessage(baseAction.getText("outcome.action.targetUnit.required"));
      baseAction.getInvalidFields().put("input-outcome.milestones[" + i + "].targetUnit.id",
        InvalidFieldsMessages.EMPTYFIELD);


    } else {
      if (milestone.getTargetUnit().getId() != -1 && milestone.getValue() != null) {
        if (!this.isValidNumber(String.valueOf(milestone.getValue()))) {
          baseAction.addMessage(baseAction.getText("outcome.milestone.action.value.required"));
          baseAction.getInvalidFields().put("input-outcome.milestones[" + i + "].value",
            InvalidFieldsMessages.EMPTYFIELD);

        }
      }
    }

    if (!this.isValidNumber(String.valueOf(milestone.getValue()))) {
      if (milestone.getTargetUnit().getId() != -1) {
        baseAction.addMessage(baseAction.getText("outcome.milestone.action.value.required"));
        baseAction.getInvalidFields().put("input-outcome.milestones[" + i + "].value",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

  }

  public void validateOutcome(BaseAction baseAction, CenterOutcome outcome) {

    if (outcome.getResearchImpact() != null) {
      if (outcome.getResearchImpact().getId() == -1) {
        baseAction.addMessage(baseAction.getText("outcome.action.impactPathway.required"));
        baseAction.getInvalidFields().put("input-outcome.researchImpact.id", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      baseAction.addMessage(baseAction.getText("outcome.action.impactPathway.required"));
      baseAction.getInvalidFields().put("input-outcome.researchImpact.id", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (outcome.getDescription() != null) {
      if (!this.isValidString(outcome.getDescription()) && this.wordCount(outcome.getDescription()) >= 50) {
        baseAction.addMessage(baseAction.getText("outcome.action.statement.required"));
        baseAction.getInvalidFields().put("input-outcome.description", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      baseAction.addMessage(baseAction.getText("outcome.action.statement.required"));
      baseAction.getInvalidFields().put("input-outcome.description", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (outcome.getTargetYear() != null) {
      if (outcome.getTargetYear() == -1) {
        baseAction.addMessage(baseAction.getText("outcome.action.targetYear.required"));
        baseAction.getInvalidFields().put("input-outcome.targetYear", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      baseAction.addMessage(baseAction.getText("outcome.action.targetYear.required"));
      baseAction.getInvalidFields().put("input-outcome.targetYear", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (outcome.getTargetUnit() == null) {
      baseAction.addMessage(baseAction.getText("outcome.action.targetUnit.required"));
      baseAction.getInvalidFields().put("input-outcome.targetUnit.id", InvalidFieldsMessages.EMPTYFIELD);


    } else {
      if (outcome.getTargetUnit().getId() != -1 && outcome.getValue() != null) {
        if (!this.isValidNumber(String.valueOf(outcome.getValue()))) {
          baseAction.addMessage(baseAction.getText("outcome.action.value.required"));
          baseAction.getInvalidFields().put("input-outcome.value", InvalidFieldsMessages.EMPTYFIELD);

        }
      }
    }


    if (outcome.getMilestones() == null || outcome.getMilestones().isEmpty()) {
      baseAction.addMessage(baseAction.getText("outcome.action.milestones"));
      baseAction.getInvalidFields().put("list-outcome.milestones",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Milestones"}));
    } else {
      for (int i = 0; i < outcome.getMilestones().size(); i++) {
        this.validateMilestones(baseAction, outcome.getMilestones().get(i), i);
      }

    }


  }

}
