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

package org.cgiar.ccafs.marlo.validation.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
@Named
public class ProjectExpectedStudiesValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private BaseAction baseAction;

  @Inject
  public ProjectExpectedStudiesValidator(GlobalUnitManager crpManager) {
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(ProjectExpectedStudy expectedStudy, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = expectedStudy.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.EXPECTEDSTUDY.getStatus().replace("/", "_");
    String autoSaveFile =
      expectedStudy.getId() + "_" + composedClassName + "_" + action.getActualPhase().getDescription() + "_"
        + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Project project, ProjectExpectedStudy projectExpectedStudy, boolean saving) {
    action.setInvalidFields(new HashMap<>());

    action.setInvalidFields(new HashMap<>());
    baseAction = action;

    if (!saving) {
      Path path = this.getAutoSaveFilePath(projectExpectedStudy, action.getCrpID(), action);
      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }


    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(project, projectExpectedStudy, action.getActualPhase().getDescription(),
      action.getActualPhase().getYear(), ProjectSectionStatusEnum.EXPECTEDSTUDIES.getStatus(), action);

  }

  public void validateProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy, BaseAction action) {

    // Validate Title
    if (!this.isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTitle())
      && this
        .wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTitle()) <= 20) {
      action.addMessage(action.getText("Title"));
      action.addMissingField("study.title");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.title",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Outcome/Impact Statement
    if (!this.isValidString(
      projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getOutcomeImpactStatement())
      && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
        .getOutcomeImpactStatement()) <= 80) {
      action.addMessage(action.getText("Outcome/Impact Statement"));
      action.addMissingField("study.outcomeStatement");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.outcomeImpactStatement",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Is Contribution Radio Button (Yes/No)
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsContribution() == null) {
      action.addMessage(action.getText("Involve a contribution of the CGIAR"));
      action.addMissingField("study.reportingIndicatorThree");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.isContribution",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Policy/Investment Type
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
      .getRepIndPolicyInvestimentType() != null) {
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndPolicyInvestimentType()
        .getId() == null
        || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
          .getRepIndPolicyInvestimentType().getId() == -1) {
        action.addMessage(action.getText("Policy/Investiment Type"));
        action.addMissingField("study.reportingIndicatorThree.policyType");
        action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndPolicyInvestimentType.id",
          InvalidFieldsMessages.EMPTYFIELD);
      } else {
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
          .getRepIndPolicyInvestimentType().getId() == 3) {

          // Validate Amount
          if (!this.isValidNumber(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
            .getPolicyAmount().toString())) {
            action.addMessage(action.getText("Policy Amount"));
            action.addMissingField("study.reportingIndicatorThree.amount");
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.policyAmount",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      }
    } else {
      action.addMessage(action.getText("Policy/Investiment Type"));
      action.addMissingField("study.reportingIndicatorThree.policyType");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndPolicyInvestimentType.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate organization Type
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
      .getRepIndOrganizationType() != null) {
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndOrganizationType()
        .getId() == null
        || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndOrganizationType()
          .getId() == -1) {
        action.addMessage(action.getText("Organization Type"));
        action.addMissingField("study.reportingIndicatorThree.organizationType");
        action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndOrganizationType.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("Organization Type"));
      action.addMissingField("study.reportingIndicatorThree.organizationType");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndOrganizationType.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Stage in Process
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndStageProcess() != null) {
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndStageProcess()
        .getId() == null
        || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndStageProcess()
          .getId() == -1) {
        action.addMessage(action.getText("Stage in process"));
        action.addMissingField("study.reportingIndicatorThree.stage");
        action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndStageProcess.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("Stage in process"));
      action.addMissingField("study.reportingIndicatorThree.stage");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndStageProcess.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Maturity radio Button
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndStageStudy() == null) {
      action.addMessage(action.getText("Maturity of Change reported"));
      action.addMissingField("Maturity of Change reported");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndStageStudy.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }


  }
}
