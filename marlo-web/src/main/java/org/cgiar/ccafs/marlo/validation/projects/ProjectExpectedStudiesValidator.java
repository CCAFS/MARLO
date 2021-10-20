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
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyQuantification;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
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
    String autoSaveFile = expectedStudy.getId() + "_" + composedClassName + "_" + action.getActualPhase().getName()
      + "_" + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";
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
      action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
      ProjectSectionStatusEnum.EXPECTEDSTUDIES.getStatus(), action);

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
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStatus().getId() == -1) {
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
    if ((!this.isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTitle()))
      || this
        .wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTitle()) > 25) {
      action.addMessage(action.getText("Title"));
      action.addMissingField("study.title");
      action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.title",
        InvalidFieldsMessages.EMPTYFIELD);
    }


    // Validate Sub-Idos
    if (!action.isAiccra()) {
      if (projectExpectedStudy.getSubIdos() == null || projectExpectedStudy.getSubIdos().isEmpty()) {
        action.addMessage(action.getText("subIdos"));
        action.addMissingField("study.stratgicResultsLink.subIDOs");
        action.getInvalidFields().put("list-expectedStudy.subIdos",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
      } else {
        // Validate primary sub-IDO
        if (projectExpectedStudy.getSubIdos().size() > 1) {
          int count = 0;
          for (ProjectExpectedStudySubIdo studySubIdo : projectExpectedStudy.getSubIdos()) {
            if (studySubIdo.getPrimary() != null && studySubIdo.getPrimary()) {
              count++;
            }
          }

          if (count == 0) {
            action.addMessage(action.getText("subIdos"));
            action.addMissingField("study.stratgicResultsLink.subIDOs");
            action.getInvalidFields().put("list-expectedStudy.subIdos",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
          }
        }
      }
    }

    // validate Milestones
    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
      && baseAction.getActualPhase().getName() != null && baseAction.getActualPhase().getName().contains("AR")
      && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() != null
      && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType().getId().intValue() == 1) {
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
        && projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == null) {
        action.addMessage(action.getText("hasMilestones"));
        action.addMissingField("expectedStudy.projectExpectedStudyInfo.hasMilestones");
        action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.hasMilestones",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
      && baseAction.getActualPhase().getName() != null && baseAction.getActualPhase().getName().contains("AR")
      && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() != null
      && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType().getId().intValue() == 1) {
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null
        && (projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == true
          && (projectExpectedStudy.getProjectOutcomes() == null || projectExpectedStudy.getProjectOutcomes().isEmpty()))
        || projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == null) {

        action.addMessage(action.getText("projectOutcomes"));
        action.addMissingField("expectedStudy.projectOutcomes");
        action.getInvalidFields().put("list-expectedStudy.projectOutcomes",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"projectOutcomes"}));

      } else {

        // Validate milestones
        /*
         * if (projectExpectedStudy.getMilestones() != null
         * && (projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() != null
         * && projectExpectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == true
         * && !projectExpectedStudy.getMilestones().isEmpty())) {
         * int count = 0;
         * for (ProjectExpectedStudyMilestone studyMilestone : projectExpectedStudy.getMilestones()) {
         * if (studyMilestone.getPrimary() != null && studyMilestone.getPrimary()) {
         * count++;
         * }
         * }
         * if (count != 1) {
         * action.addMessage(action.getText("milestones"));
         * action.addMissingField("expectedStudy.milestones");
         * action.getInvalidFields().put("list-expectedStudy.milestones",
         * action.getText(InvalidFieldsMessages.WRONGVALUE, new String[] {"milestones"}));
         * }
         * }
         */
      }
    }

    // Validate Centers
    if (projectExpectedStudy.getProjectExpectedStudyInfo() != null
      && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType() != null
      && projectExpectedStudy.getProjectExpectedStudyInfo().getStudyType().getId() == 1
      && (projectExpectedStudy.getCenters() == null || projectExpectedStudy.getCenters().isEmpty())) {
      action.addMessage(action.getText("expectedStudy.contributingCenters"));
      action.addMissingField("expectedStudy.centers");
      action.getInvalidFields().put("list-expectedStudy.centers",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"centers"}));
    }


    // Validate Geographic Scope
    boolean haveRegions = false;
    boolean haveCountries = false;

    if (projectExpectedStudy.getGeographicScopes() == null || projectExpectedStudy.getGeographicScopes().isEmpty()) {
      action.addMessage(action.getText("geographicScopes"));
      action.addMissingField("policy.geographicScope");
      action.getInvalidFields().put("list-expectedStudy.geographicScopes",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"geographicScopes"}));
    } else {
      for (ProjectExpectedStudyGeographicScope projectPolicyGeographicScope : projectExpectedStudy
        .getGeographicScopes()) {
        if (projectPolicyGeographicScope.getRepIndGeographicScope().getId() == 2) {
          haveRegions = true;
        }
        if (projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 1
          && projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 2) {
          haveCountries = true;
        }
      }
    }

    if (haveRegions) {
      // Validate Regions
      if (projectExpectedStudy.getStudyRegions() == null || projectExpectedStudy.getStudyRegions().isEmpty()) {
        action.addMessage(action.getText("regions"));
        action.addMissingField("expectedStudy.studyRegions");
        action.getInvalidFields().put("list-expectedStudy.studyRegions",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"regions"}));
      }
    }

    if (haveCountries) {
      // Validate Countries
      if (projectExpectedStudy.getCountriesIds() == null || projectExpectedStudy.getCountriesIds().isEmpty()) {
        action.addMessage(action.getText("countries"));
        action.addMissingField("policy.countries");
        action.getInvalidFields().put("input-expectedStudy.countriesIds",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"countries"}));
      }
    }


    if (!(baseAction.isReportingActive() || baseAction.isUpKeepActive())) {
      // Validate Srf Targets Selection
      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget() == null) {
        action.addMessage(action.getText("targets"));
        action.addMissingField("expectedStudy.projectExpectedStudyInfo.isSrfTarget");
        action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.isSrfTarget",
          InvalidFieldsMessages.EMPTYFIELD);
      } else {
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget()
            .equals("targetsOptionYes")) {
          // Validate Srf Targets
          if (projectExpectedStudy.getSrfTargets() == null || projectExpectedStudy.getSrfTargets().isEmpty()) {
            action.addMessage(action.getText("targets"));
            action.addMissingField("study.stratgicResultsLink.srfTargets");
            action.getInvalidFields().put("list-expectedStudy.srfTargets",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"targets"}));
          }
        }
        // Validate Commissioning Study
        if (!this.isValidString(
          projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCommissioningStudy())
          && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
            .getCommissioningStudy()) <= 20) {
          action.addMessage(action.getText("Commissioning Study"));
          action.addMissingField("study.commissioningStudy.readText");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.commissioningStudy",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        if ((projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
          .getCommissioningStudy() != null
          && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCommissioningStudy()
            .isEmpty())
          || (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
            .getCommissioningStudy() == null)) {
          action.addMessage(action.getText("Commissioning Study"));
          action.addMissingField("study.commissioningStudy.readText");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.commissioningStudy",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }


    } else {

      if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType() != null
        && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getStudyType().getId() == 1) {
        // Validate Outcome/Impact Statement
        /*
         * if ((!this.isValidString(
         * projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getOutcomeImpactStatement()))
         * || this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
         * .getOutcomeImpactStatement()) > 80) {
         * action.addMessage(action.getText("Outcome/Impact Statement"));
         * action.addMissingField("study.outcomeStatement");
         * action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.outcomeImpactStatement",
         * InvalidFieldsMessages.EMPTYFIELD);
         * }
         */
        // Validate Comunications Material
        if ((!this.isValidString(
          projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getComunicationsMaterial()))
          || this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
            .getComunicationsMaterial()) > 400) {
          action.addMessage(action.getText("Outcome story for communications"));
          action.addMissingField("study.comunicationsMaterial");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.comunicationsMaterial",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        // Validate CGIAR Innovation
        if (!this.isValidString(
          projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCgiarInnovation())) {
          action.addMessage(action.getText("CGIAR innovation"));
          action.addMissingField("study.cgiarInnovation");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.cgiarInnovation",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        // Validate Is Contribution Radio Button (Yes/No)
        if (!action.isAiccra()) {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
            .getIsContribution() == null) {

            action.addMessage(action.getText("Involve a contribution of the CGIAR"));
            action.addMissingField("study.reportingIndicatorThree");
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.isContribution",
              InvalidFieldsMessages.EMPTYFIELD);

            // this.validateHidden(projectExpectedStudy, action);

          } else {
            if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsContribution()) {

              // Validate Policies
              if (projectExpectedStudy.getPolicies() == null || projectExpectedStudy.getPolicies().isEmpty()) {
                action.addMessage(action.getText("policyList"));
                action.addMissingField("policy.policies");
                action.getInvalidFields().put("list-expectedStudy.policies",
                  action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"policyList"}));
              }
            }
          }
        }

        // Validate Stage Study
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
          .getRepIndStageStudy() != null) {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndStageStudy()
            .getId() == null
            || projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getRepIndStageStudy()
              .getId() == -1) {
            action.addMessage(action.getText("Stage Study"));
            action.addMissingField("study.reportingIndicatorThree.stage");
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndStageStudy.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        } else {
          action.addMessage(action.getText("Stage Study"));
          action.addMissingField("study.reportingIndicatorThree.stage");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.repIndStageStudy.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        // Validate Srf Targets Selection
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget() == null) {
          action.addMessage(action.getText("targets"));
          action.addMissingField("expectedStudy.projectExpectedStudyInfo.isSrfTarget");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.isSrfTarget",
            InvalidFieldsMessages.EMPTYFIELD);
        } else {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget()
            .equals("targetsOptionYes")) {
            // Validate Srf Targets
            if (projectExpectedStudy.getSrfTargets() == null || projectExpectedStudy.getSrfTargets().isEmpty()) {
              action.addMessage(action.getText("targets"));
              action.addMissingField("study.stratgicResultsLink.srfTargets");
              action.getInvalidFields().put("list-expectedStudy.srfTargets",
                action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"targets"}));
            }
          }
        }

        // Validate Elaboration Outcomes
        if ((!this.isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
          .getElaborationOutcomeImpactStatement()))
          || this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
            .getElaborationOutcomeImpactStatement()) > 400) {
          action.addMessage(action.getText("Elaboration Outcome"));
          action.addMissingField("study.elaborationStatement");
          action.getInvalidFields().put(
            "input-expectedStudy.projectExpectedStudyInfo.elaborationOutcomeImpactStatement",
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

        // Validate Describe Gender
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getGenderLevel() != null
          && (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getGenderLevel()
            .getId() != 1
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getGenderLevel()
              .getId() != 4)) {
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
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getYouthLevel() != null
          && (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getYouthLevel().getId() != 1
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getYouthLevel()
              .getId() != 4)) {
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
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCapdevLevel() != null
          && (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCapdevLevel()
            .getId() != 1
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCapdevLevel()
              .getId() != 4)) {
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

        // Validate Describe ClimateChange
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
          .getClimateChangeLevel() != null
          && (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getClimateChangeLevel()
            .getId() != 1
            && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getClimateChangeLevel()
              .getId() != 4)) {
          if (!this.isValidString(
            projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getDescribeClimateChange())
            && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
              .getDescribeClimateChange()) <= 100) {
            action.addMessage(action.getText("Describe Climate Change"));
            action.addMissingField("study.achievementsCapDevRelevance");
            action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.describeClimateChange",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }

        // Validate Contacts
        if (!this
          .isValidString(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getContacts())) {
          action.addMessage(action.getText("Contacts"));
          action.addMissingField("study.contacts");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.contacts",
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

        // Validate Quantification
        if (projectExpectedStudy.getQuantifications() != null) {
          for (int i = 0; i < projectExpectedStudy.getQuantifications().size(); i++) {
            this.validateQuantifications(action, projectExpectedStudy.getQuantifications().get(i), i);
          }
        }

        // REMOVED FOR AR 2020
        // Validate Evidence Tag
        /*
         * if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getEvidenceTag() == null) {
         * action.addMessage(action.getText("Evidence Tag"));
         * action.addMissingField("study.evidenceTag");
         * action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.evidenceTag.id",
         * InvalidFieldsMessages.EMPTYFIELD);
         * }
         */

      } else {
        // Validate Srf Targets Selection
        if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget() == null) {
          action.addMessage(action.getText("targets"));
          action.addMissingField("expectedStudy.projectExpectedStudyInfo.isSrfTarget");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.isSrfTarget",
            InvalidFieldsMessages.EMPTYFIELD);
        } else {
          if (projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsSrfTarget()
            .equals("targetsOptionYes")) {
            // Validate Srf Targets
            if (projectExpectedStudy.getSrfTargets() == null || projectExpectedStudy.getSrfTargets().isEmpty()) {
              action.addMessage(action.getText("targets"));
              action.addMissingField("study.stratgicResultsLink.srfTargets");
              action.getInvalidFields().put("list-expectedStudy.srfTargets",
                action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"targets"}));
            }
          }
        }


        // Validate Comments (TopLevel)
        if (!this.isValidString(
          projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getTopLevelComments())) {
          action.addMessage(action.getText("Comments"));
          action.addMissingField("study.comments");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.topLevelComments",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        // Validate Commissioning Study
        if (!this.isValidString(
          projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getCommissioningStudy())
          && this.wordCount(projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase())
            .getCommissioningStudy()) <= 20) {
          action.addMessage(action.getText("Commissioning Study"));
          action.addMissingField("study.commissioningStudy.readText");
          action.getInvalidFields().put("input-expectedStudy.projectExpectedStudyInfo.commissioningStudy",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }
  }

  private void validateQuantifications(BaseAction action,
    ProjectExpectedStudyQuantification projectExpectedStudyQuantification, int i) {

    // Validate Type of Quantification
    if (projectExpectedStudyQuantification.getTypeQuantification() == null) {
      action.addMessage(action.getText("Type Quantification"));
      action.addMissingField("Type Quantification");
      action.getInvalidFields().put("input-expectedStudy.quantifications[" + i + "].typeQuantification",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Number
    if (!this.isValidNumber(String.valueOf(projectExpectedStudyQuantification.getNumber()))) {
      action.addMessage(action.getText("Number"));
      action.addMissingField("study.number");
      action.getInvalidFields().put("input-expectedStudy.quantifications[" + i + "].number",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Unit
    if (!this.isValidString(projectExpectedStudyQuantification.getTargetUnit())) {
      action.addMessage(action.getText("Unit"));
      action.addMissingField("study.Unit");
      action.getInvalidFields().put("input-expectedStudy.quantifications[" + i + "].targetUnit",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Unit
    if (!this.isValidString(projectExpectedStudyQuantification.getComments())) {
      action.addMessage(action.getText("Comments"));
      action.addMissingField("study.comments");
      action.getInvalidFields().put("input-expectedStudy.quantifications[" + i + "].comments",
        InvalidFieldsMessages.EMPTYFIELD);
    }


  }
}
