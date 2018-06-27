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

    baseAction = action;

    if (!saving) {
      Path path = this.getAutoSaveFilePath(projectExpectedStudy, action.getCrpID(), action);
      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }


    this.validateProjectExpectedStudy(projectExpectedStudy, action);


    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(project, projectExpectedStudy, action.getActualPhase().getDescription(),
      action.getActualPhase().getYear(), ProjectSectionStatusEnum.EXPECTEDSTUDIES.getStatus(), action);

  }

  public void validateHidden(ProjectExpectedStudy projectExpectedStudy, BaseAction action) {
    // Validate Maturity radio Button
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndStageStudy() == null) {
      action.addMessage(action.getText("Maturity of Change reported"));
      action.addMissingField("Maturity of Change reported");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndStageStudy.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Srf Targets
    if (projectExpectedStudy.getSrfTargets() == null || projectExpectedStudy.getSrfTargets().isEmpty()) {
      action.addMessage(action.getText("targets"));
      action.addMissingField("study.stratgicResultsLink.srfTargets");
      action.getInvalidFields().put("list-expectedStudy.srfTargets",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"targets"}));
    }


    // Validate Elaboration Outcomes
    if (!this.isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
      .getElaborationOutcomeImpactStatement())
      && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
        .getElaborationOutcomeImpactStatement()) <= 400) {
      action.addMessage(action.getText("Elaboration Outcome"));
      action.addMissingField("study.elaborationStatement");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.elaborationOutcomeImpactStatement",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate References Cited
    if (!this.isValidString(
      projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getReferencesText())) {
      action.addMessage(action.getText("References Cited"));
      action.addMissingField("study.referencesCited");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.referencesText",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Quantification
    if (!this.isValidString(
      projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getQuantification())) {
      action.addMessage(action.getText("Quantification"));
      action.addMissingField("study.quantification");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.quantification",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Contacts
    if (!this
      .isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getContacts())) {
      action.addMessage(action.getText("Contacts"));
      action.addMissingField("study.contacts");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.contacts",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }


  public void validateProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy, BaseAction action) {

    // Validate Study type
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType() != null) {
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType().getId() == null
        || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType().getId() == -1) {
        action.addMessage(action.getText("Study Type"));
        action.addMissingField("study.type");
        action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.studyType.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("Study Type"));
      action.addMissingField("study.type");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.studyType.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Status
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStatus() != null) {
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStatus() == -1) {
        action.addMessage(action.getText("Status"));
        action.addMissingField("study.status");
        action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.status",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("Status"));
      action.addMissingField("study.status");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.status",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Title
    if (!this.isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTitle())
      && this
        .wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTitle()) <= 20) {
      action.addMessage(action.getText("Title"));
      action.addMissingField("study.title");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.title",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Sub-Idos
    if (projectExpectedStudy.getSubIdos() == null || projectExpectedStudy.getSubIdos().isEmpty()) {
      action.addMessage(action.getText("subIdos"));
      action.addMissingField("study.stratgicResultsLink.subIDOs");
      action.getInvalidFields().put("list-expectedStudy.subIdos",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
    }

    if (baseAction.isPlanningActive()) {
      // Validate Srf Targets
      if (projectExpectedStudy.getSrfTargets() == null || projectExpectedStudy.getSrfTargets().isEmpty()) {
        action.addMessage(action.getText("targets"));
        action.addMissingField("study.stratgicResultsLink.srfTargets");
        action.getInvalidFields().put("list-expectedStudy.srfTargets",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"targets"}));
      }
    } else {

      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType() != null
        && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType().getId() == 1) {
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

          this.validateHidden(projectExpectedStudy, action);

        } else {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsContribution()) {

            // Validate Policy/Investment Type
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getRepIndPolicyInvestimentType() != null) {
              if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getRepIndPolicyInvestimentType().getId() == null
                || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                  .getRepIndPolicyInvestimentType().getId() == -1) {
                action.addMessage(action.getText("Policy/Investiment Type"));
                action.addMissingField("study.reportingIndicatorThree.policyType");
                action.getInvalidFields().put(
                  "input-expectedStudy.projectExpectedStudyInfo.repIndPolicyInvestimentType.id",
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
                  } else {
                    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                      .getPolicyAmount() <= 0.0) {
                      action.addMessage(action.getText("Policy Amount"));
                      action.addMissingField("study.reportingIndicatorThree.amount");
                      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.policyAmount",
                        InvalidFieldsMessages.EMPTYFIELD);
                    }
                  }
                }
              }
            } else {
              action.addMessage(action.getText("Policy/Investiment Type"));
              action.addMissingField("study.reportingIndicatorThree.policyType");
              action.getInvalidFields().put(
                "input-expectedStudy.projectExpectedStudyInfo.repIndPolicyInvestimentType.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }

            // Validate organization Type
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getRepIndOrganizationType() != null) {
              if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getRepIndOrganizationType().getId() == null
                || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                  .getRepIndOrganizationType().getId() == -1) {
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
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getRepIndStageProcess() != null) {
              if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndStageProcess()
                .getId() == null
                || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndStageProcess()
                  .getId() == -1) {
                action.addMessage(action.getText("Stage in process"));
                action.addMissingField("study.reportingIndicatorThree.stage");
                action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndStageProcess.id",
                  InvalidFieldsMessages.EMPTYFIELD);
              } else {
                if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                  .getRepIndStageProcess().getId() != 1) {

                  // Validate Maturity radio Button
                  if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                    .getRepIndStageStudy() == null) {
                    action.addMessage(action.getText("Maturity of Change reported"));
                    action.addMissingField("Maturity of Change reported");
                    action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndStageStudy.id",
                      InvalidFieldsMessages.EMPTYFIELD);
                  }
                  // Validate Srf Targets
                  if (projectExpectedStudy.getSrfTargets() == null || projectExpectedStudy.getSrfTargets().isEmpty()) {
                    action.addMessage(action.getText("targets"));
                    action.addMissingField("study.stratgicResultsLink.srfTargets");
                    action.getInvalidFields().put("list-expectedStudy.srfTargets",
                      action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"targets"}));
                  }

                  // Validate Elaboration Outcomes
                  if (!this.isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                    .getElaborationOutcomeImpactStatement())
                    && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                      .getElaborationOutcomeImpactStatement()) <= 400) {
                    action.addMessage(action.getText("Elaboration Outcome"));
                    action.addMissingField("study.elaborationStatement");
                    action.getInvalidFields().put(
                      "input-expectedStudy.projectExpectedStudyInfo.elaborationOutcomeImpactStatement",
                      InvalidFieldsMessages.EMPTYFIELD);
                  }

                  // Validate References Cited
                  if (!this.isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                    .getReferencesText())) {
                    action.addMessage(action.getText("References Cited"));
                    action.addMissingField("study.referencesCited");
                    action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.referencesText",
                      InvalidFieldsMessages.EMPTYFIELD);
                  }

                  // Validate Quantification
                  if (!this.isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                    .getQuantification())) {
                    action.addMessage(action.getText("Quantification"));
                    action.addMissingField("study.quantification");
                    action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.quantification",
                      InvalidFieldsMessages.EMPTYFIELD);
                  }


                  // Validate Describe Gender
                  if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                    .getGenderLevel() != null
                    && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getGenderLevel()
                      .getId() != 1) {
                    if (!this.isValidString(
                      projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getDescribeGender())
                      && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                        .getDescribeGender()) <= 100) {
                      action.addMessage(action.getText("Describe Gender"));
                      action.addMissingField("study.achievementsGenderRelevance");
                      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.describeGender",
                        InvalidFieldsMessages.EMPTYFIELD);
                    }
                  }

                  // Validate Describe Youth
                  if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                    .getYouthLevel() != null
                    && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getYouthLevel()
                      .getId() != 1) {
                    if (!this.isValidString(
                      projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getDescribeYouth())
                      && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                        .getDescribeYouth()) <= 100) {
                      action.addMessage(action.getText("Describe Youth"));
                      action.addMissingField("study.achievementsYouthRelevance");
                      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.describeYouth",
                        InvalidFieldsMessages.EMPTYFIELD);
                    }
                  }
                  // Validate Describe Capdev
                  if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                    .getCapdevLevel() != null
                    && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCapdevLevel()
                      .getId() != 1) {
                    if (!this.isValidString(
                      projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getDescribeCapdev())
                      && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                        .getDescribeCapdev()) <= 100) {
                      action.addMessage(action.getText("Describe Capdev"));
                      action.addMissingField("study.achievementsCapDevRelevance");
                      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.describeCapdev",
                        InvalidFieldsMessages.EMPTYFIELD);
                    }
                  }

                  // Validate Contacts
                  if (!this.isValidString(
                    projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getContacts())) {
                    action.addMessage(action.getText("Contacts"));
                    action.addMissingField("study.contacts");
                    action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.contacts",
                      InvalidFieldsMessages.EMPTYFIELD);
                  }

                }
              }
            } else {
              action.addMessage(action.getText("Stage in process"));
              action.addMissingField("study.reportingIndicatorThree.stage");
              action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndStageProcess.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          } else {

            // Validate no hidden fields
            this.validateHidden(projectExpectedStudy, action);


          }
        }


        // Validate Geographic Scope
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
          .getRepIndGeographicScope() != null) {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndGeographicScope()
            .getId() == null
            || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndGeographicScope()
              .getId() == -1) {
            action.addMessage(action.getText("Geographic Scope"));
            action.addMissingField("study.geographicScope");
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndGeographicScope.id",
              InvalidFieldsMessages.EMPTYFIELD);
          } else {
            // Validate if Scope is Multi-national, National or Sub-National and review if the innovation has Countries
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndGeographicScope()
              .getId().equals(action.getReportingIndGeographicScopeMultiNational())
              || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getRepIndGeographicScope().getId().equals(action.getReportingIndGeographicScopeNational())
              || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getRepIndGeographicScope().getId().equals(action.getReportingIndGeographicScopeSubNational())) {
              // Validate Countries
              if (projectExpectedStudy.getCountriesIds() == null || projectExpectedStudy.getCountriesIds().isEmpty()) {
                action.addMessage(action.getText("countries"));
                action.addMissingField("study.countries");
                action.getInvalidFields().put("input-expectedStudy.countriesIds",
                  action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"countries"}));
              }
            }

            // Validate if Scope is Regional and review if The innovation has a region
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndGeographicScope()
              .getId().equals(action.getReportingIndGeographicScopeRegional())) {
              // Validate Region
              if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
                .getRepIndRegion() != null) {
                if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndRegion()
                  .getId() == null
                  || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndRegion()
                    .getId() == -1) {
                  action.addMessage(action.getText("Region"));
                  action.addMissingField("study.region");
                  action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndRegion.id",
                    InvalidFieldsMessages.EMPTYFIELD);
                }
              } else {
                action.addMessage(action.getText("Region"));
                action.addMissingField("study.region");
                action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndRegion.id",
                  InvalidFieldsMessages.EMPTYFIELD);
              }
            }
          }
        } else {
          action.addMessage(action.getText("Geographic Scope"));
          action.addMissingField("study.geographicScope");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndGeographicScope.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }


        // Validate Gender Radio Button
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getGenderLevel() == null) {
          action.addMessage(action.getText("Gender Relevance"));
          action.addMissingField("Gender Relevance");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.genderLevel.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }


        // Validate Youth Radio Button
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getYouthLevel() == null) {
          action.addMessage(action.getText("Youth Relevance"));
          action.addMissingField("Youth Relevance");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.youthLevel.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }


        // Validate Capdev Radio Button
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCapdevLevel() == null) {
          action.addMessage(action.getText("Capdev Relevance"));
          action.addMissingField("Youth Relevance");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.capdevLevel.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }

      } else {
        // Validate Srf Targets
        if (projectExpectedStudy.getSrfTargets() == null || projectExpectedStudy.getSrfTargets().isEmpty()) {
          action.addMessage(action.getText("targets"));
          action.addMissingField("study.stratgicResultsLink.srfTargets");
          action.getInvalidFields().put("list-expectedStudy.srfTargets",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"targets"}));
        }

        // Validate Comments (TopLevel)
        if (!this.isValidString(
          projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTopLevelComments())) {
          action.addMessage(action.getText("Comments"));
          action.addMissingField("study.comments");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.topLevelComments",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }
  }
}
