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
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ProjectInnovationValidator extends BaseValidator {

  private static final Logger LOG = LoggerFactory.getLogger(ProjectInnovationValidator.class);

  private final GlobalUnitManager crpManager;
  private BaseAction baseAction;
  private boolean resultProgessValidate = false;
  private boolean struts = true;
  private Boolean clearLead = false;

  String innovationGeneral = "";
  String innovationAlliance = "";
  String innovationOneCgiar = "";
  String innovationReadiness = "";
  String innovationRights = "";

  @Inject
  public ProjectInnovationValidator(GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(ProjectInnovation innovation, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = innovation.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.INNOVATION.getStatus().replace("/", "_");
    String autoSaveFile = innovation.getId() + "_" + composedClassName + "_" + action.getActualPhase().getName() + "_"
      + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public void validate(BaseAction action, Project project, ProjectInnovation projectInnovation, Boolean clearLead,
    boolean saving, boolean struts, int year, boolean upkeep) {
    String value;

    value = BaseAction.getIsInnovationGeneralInformationCompleteMap().get(projectInnovation.getId() + "");
    if (value != null) {
      BaseAction.getIsInnovationGeneralInformationCompleteMap().remove(projectInnovation.getId() + "");
    }

    value = BaseAction.getIsInnovationAllianceAlignmentCompleteMap().get(projectInnovation.getId() + "");
    if (value != null) {
      BaseAction.getIsInnovationAllianceAlignmentCompleteMap().remove(projectInnovation.getId() + "");
    }

    value = BaseAction.getIsInnovationOneCgiarAlignmentCompleteMap().get(projectInnovation.getId() + "");
    if (value != null) {
      BaseAction.getIsInnovationOneCgiarAlignmentCompleteMap().remove(projectInnovation.getId() + "");
    }

    value = BaseAction.getIsInnovationReadinessCompleteMap().get(projectInnovation.getId() + "");
    if (value != null) {
      BaseAction.getIsInnovationReadinessCompleteMap().remove(projectInnovation.getId() + "");
    }

    value = BaseAction.getIsInnovationRightsCompleteMap().get(projectInnovation.getId() + "");
    if (value != null) {
      BaseAction.getIsInnovationRightsCompleteMap().remove(projectInnovation.getId() + "");
    }

    if (!saving) {
      Path path = this.getAutoSaveFilePath(projectInnovation, action.getCrpID(), action);
      if (path.toFile().exists()) {
        // Draft label cause that the section appears like there were missing fields
        // this.addMissingField("draft");
      }
    }
    if (struts) {
      action.setInvalidFields(new HashMap<>());
      baseAction = action;
    }
    resultProgessValidate = this.validateIsProgress(action);
    this.clearLead = clearLead;
    this.struts = struts;
    this.validateGeneralInformation(action, project, projectInnovation, saving);
    this.validateAllianceAlignment(action, project, projectInnovation, saving);
    this.validateOneCgiarAlignment(action, project, projectInnovation, saving);
    this.validateInnovationReadiness(action, project, projectInnovation, saving);
    this.validateInnovationRights(action, project, projectInnovation, saving);

    // The validator is called by Struts
    if (struts) {
      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }
    }

    this.saveMissingFields(project, projectInnovation, action.getActualPhase().getDescription(),
      action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
      ProjectSectionStatusEnum.INNOVATIONS.getStatus(), action);
  }


  /**
   * Validate the data of the AllianceAlignment tab
   *
   * @param action base action
   * @param project related project
   * @param projectInnovation An specific projectInnovation
   * @param saving related action
   */
  public void validateAllianceAlignment(BaseAction action, Project project, ProjectInnovation projectInnovation,
    boolean saving) {
    innovationAlliance = action.getMissingFields().toString();
    if (projectInnovation != null && projectInnovation.getId() != null && (innovationAlliance.length() > 0)) {
      BaseAction.getIsInnovationAllianceAlignmentCompleteMap().put("" + projectInnovation.getId(), "1");
    }
  }

  /**
   * Validate the data of the general information tab
   *
   * @param action base action
   * @param project related project
   * @param ProjectInnovation An specific projectInnovation
   * @param saving related action
   */
  public void validateGeneralInformation(BaseAction action, Project project, ProjectInnovation projectInnovation,
    boolean saving) {
    ProjectInnovationInfo innovationInfo = projectInnovation.getProjectInnovationInfo(action.getActualPhase());
    // Validate Title
    if (!(this.isValidString(innovationInfo.getTitle()) && this.wordCount(innovationInfo.getTitle()) <= 30)) {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.title"));
        action.addMissingField("projectInnovations.title");
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.title", InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    // Validate Short Title
    if (!(this.isValidString(innovationInfo.getShortTitle()) && this.wordCount(innovationInfo.getShortTitle()) <= 30)) {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.shortTitle"));
        action.addMissingField("projectInnovations.shortTitle");
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.shortTitle",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    // Validate Narrative
    if (!(this.isValidString(innovationInfo.getNarrative()) && this.wordCount(innovationInfo.getNarrative()) <= 80)) {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.narrative"));
        action.addMissingField("projectInnovations.narrative");
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.narrative",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    // validate Milestones
    /*
     * if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()) != null
     * && (projectInnovation.getProjectInnovationInfo().getHasMilestones() != null
     * && projectInnovation.getProjectInnovationInfo().getHasMilestones() == true
     * && (projectInnovation.getProjectOutcomes() == null || projectInnovation.getProjectOutcomes().isEmpty()))
     * || projectInnovation.getProjectInnovationInfo().getHasMilestones() == null) {
     * action.addMessage(action.getText("projectOutcomes"));
     * action.addMissingField("innovation.projectOutcomes");
     * action.getInvalidFields().put("list-innovation.projectOutcomes",
     * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"projectOutcomes"}));
     * }
     */
    // validate crp outcomes

    if (innovationInfo.getHasMilestones() == null) {
      action.addMessage(action.getText("innovation.projectInnovationInfo.hasMilestones"));
      action.addMissingField("innovation.projectInnovationInfo.hasMilestones");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.hasMilestones",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (!resultProgessValidate) {
      if (innovationInfo.getHasMilestones() != null && innovationInfo.getHasMilestones()
        && (projectInnovation.getCrpOutcomes() == null || projectInnovation.getCrpOutcomes().isEmpty())) {
        action.addMessage(action.getText("crpOutcomes"));
        action.addMissingField("innovation.crpOutcomes");
        action.getInvalidFields().put("list-innovation.crpOutcomes",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"crpOutcomes"}));
      }
    }

    if (!resultProgessValidate) {
      if (innovationInfo != null && (innovationInfo.getHasMilestones() == null)) {
        action.addMessage(action.getText("projectOutcomes"));
        action.addMissingField("innovation.projectOutcomes");
        action.getInvalidFields().put("list-innovation.projectOutcomes",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"projectOutcomes"}));
      } else {

        // Validate primary milestones
        /*
         * if (projectInnovation.getMilestones() != null
         * && (projectInnovation.getProjectInnovationInfo().getHasMilestones() != null
         * && projectInnovation.getProjectInnovationInfo().getHasMilestones() == true
         * && !projectInnovation.getMilestones().isEmpty())) {
         * int count = 0;
         * for (ProjectInnovationMilestone innovationMilestone : projectInnovation.getMilestones()) {
         * if (innovationMilestone.getPrimary() != null && innovationMilestone.getPrimary()) {
         * count++;
         * }
         * }
         * if (count == 0) {
         * action.addMessage(action.getText("milestones"));
         * action.addMissingField("innovation.milestones");
         * action.getInvalidFields().put("list-innovation.milestones",
         * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"milestones"}));
         * }
         * }
         */
      }
    }

    // Validate SubIdos
    if (!action.isAiccra()) {
      if (projectInnovation.getSubIdos() == null || projectInnovation.getSubIdos().isEmpty()) {
        action.addMessage(action.getText("subIdos"));
        action.addMissingField("innovation.subIdos");
        action.getInvalidFields().put("list-innovation.subIdos",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
      } else {
        // Validate primary Sub-IDOS
        int count = 0;
        for (ProjectInnovationSubIdo subido : projectInnovation.getSubIdos()) {
          if (subido.getPrimary() != null && subido.getPrimary() == true) {
            count++;
          }
        }
        if (count == 0) {
          action.addMessage(action.getText("subIdos"));
          action.addMissingField("innovation.subIdos");
          action.getInvalidFields().put("list-innovation.subIdos",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
        }
      }
    }

    if (!resultProgessValidate) {
      // Validate Stage of Innovation
      if (innovationInfo.getRepIndStageInnovation() != null) {
        if (innovationInfo.getRepIndStageInnovation().getId() == null
          || innovationInfo.getRepIndStageInnovation().getId() == -1) {
          if (!resultProgessValidate) {
            if (struts) {
              action.addMessage(action.getText("projectInnovations.stage"));
              action.addMissingField("projectInnovations.stage");
              action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndStageInnovation.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          }
        } else {
          // Validate if Stage is = 4 and review if the innovation has an Organization Types and Outcome Case Study
          if (innovationInfo.getRepIndStageInnovation().getId() == 4) {
            // Validate Organization Types
            if (projectInnovation.getOrganizations() == null || projectInnovation.getOrganizations().isEmpty()) {
              if (struts) {
                action.addMessage(action.getText("projectInnovations.nextUserOrganizationalType"));
                action.addMissingField("projectInnovations.nextUserOrganizationalType");
                action.getInvalidFields().put("list-innovation.organizations",
                  action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Organization Types"}));
              }
            }

            // Validate Outcome Case Study
            if (innovationInfo.getProjectExpectedStudy() != null) {
              if (innovationInfo.getProjectExpectedStudy().getId() == null
                || innovationInfo.getProjectExpectedStudy().getId() == -1) {
                if (struts) {
                  action.addMessage(action.getText("projectInnovations.outcomeCaseStudy"));
                  action.addMissingField("projectInnovations.outcomeCaseStudy");
                  action.getInvalidFields().put("input-innovation.projectInnovationInfo.projectExpectedStudy.id",
                    InvalidFieldsMessages.EMPTYFIELD);
                }
              }
            }
          } else {
            // Validate Evidence Link (URL)
            if (!this.isValidString(innovationInfo.getEvidenceLink())) {
              if (struts) {
                action.addMessage(action.getText("projectInnovations.evidenceLink"));
                action.addMissingField("projectInnovations.evidenceLink");
                action.getInvalidFields().put("input-innovation.projectInnovationInfo.evidenceLink",
                  InvalidFieldsMessages.EMPTYFIELD);
              }
            }
          }
        }
      } else {
        if (struts) {
          action.addMessage(action.getText("projectInnovations.stage"));
          action.addMissingField("projectInnovations.stage");
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndStageInnovation.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }


    // Validate Geographic Scope
    boolean haveRegions = false;
    boolean haveCountries = false;

    if (projectInnovation.getGeographicScopes() == null || projectInnovation.getGeographicScopes().isEmpty()) {
      if (!resultProgessValidate) {
        if (struts) {
          action.addMessage(action.getText("projectInnovations.geographicScope"));
          action.getInvalidFields().put("list-innovation.geographicScopes",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"geographicScopes"}));
          action.addMissingField("projectInnovations.geographicScope");
        }
      }

    } else {
      for (ProjectInnovationGeographicScope innovationGeographicScope : projectInnovation.getGeographicScopes()) {
        if (innovationGeographicScope.getRepIndGeographicScope().getId() == 2) {
          haveRegions = true;
        }
        if (innovationGeographicScope.getRepIndGeographicScope().getId() != 1
          && innovationGeographicScope.getRepIndGeographicScope().getId() != 2) {
          haveCountries = true;
        }
      }
    }


    if (haveRegions) {
      // Validate Regions
      if (projectInnovation.getRegions() == null) {
        if (struts) {
          action.addMessage(action.getText("projectInnovations.region"));
          action.addMissingField("projectInnovations.region");
          action.getInvalidFields().put("list-innovation.regions",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"regions"}));
        }
      }
    }

    if (haveCountries) {
      // Validate Countries
      if (projectInnovation.getCountriesIds() == null || projectInnovation.getCountriesIds().isEmpty()) {
        if (struts) {
          action.addMessage(action.getText("innovation.countries"));
          action.addMissingField("innovation.countries");
          action.getInvalidFields().put("input-innovation.countriesIds",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"countries"}));
        }

      }
    }

    if (!resultProgessValidate) {

      // Validate Innovation Nature
      if (innovationInfo.getRepIndInnovationNature() != null) {
        if (innovationInfo.getRepIndInnovationNature().getId() == null
          || innovationInfo.getRepIndInnovationNature().getId() == -1) {
          if (struts) {
            action.addMessage(action.getText("projectInnovations.innovationNature"));
            action.addMissingField("projectInnovations.innovationNature");
            action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndInnovationNature.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      } else {
        if (struts) {
          action.addMessage(action.getText("projectInnovations.innovationNature"));
          action.addMissingField("projectInnovations.innovationNature");
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndInnovationNature.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }

      // Validate Innovation Type
      if (innovationInfo.getRepIndInnovationType() != null) {
        if (innovationInfo.getRepIndInnovationType().getId() == null
          || innovationInfo.getRepIndInnovationType().getId() == -1) {
          if (struts) {
            action.addMessage(action.getText("projectInnovations.innovationType"));
            action.addMissingField("projectInnovations.innovationType");
            action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndInnovationType.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      } else {
        if (struts) {
          action.addMessage(action.getText("projectInnovations.innovationType"));
          action.addMissingField("projectInnovations.innovationType");
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndInnovationType.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }

    // Other Innovation Type Field
    if (innovationInfo.getRepIndInnovationType() != null) {
      if (innovationInfo.getRepIndInnovationType().getId() != null
        && innovationInfo.getRepIndInnovationType().getId() == 6
        && (innovationInfo.getOtherInnovationType() == null || innovationInfo.getOtherInnovationType().isEmpty())) {
        if (struts) {
          action.addMessage(action.getText("projectInnovations.otherInnovation"));
          action.addMissingField("projectInnovations.otherInnovation");
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.otherInnovationType",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }

    // Validate Description Stage
    if (!resultProgessValidate) {
      if (!(this.isValidString(innovationInfo.getDescriptionStage())
        && this.wordCount(innovationInfo.getDescriptionStage()) <= 50)) {
        if (struts) {
          action.addMessage(action.getText("projectInnovations.stageDescription"));
          action.addMissingField("projectInnovations.stageDescription");
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.descriptionStage",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }

    // Validate lead organization
    // NOTE -> FOR SOME REASON "CLEAR LEAD" MEANS "NOT A CLEAR LEAD", SO WE HAVE TO REVERSE THE CONDITIONAL
    if (clearLead == null || /* NO */clearLead == false) {
      if (innovationInfo.getLeadOrganization() == null || innovationInfo.getLeadOrganization().getId() == -1) {
        if (!resultProgessValidate) {
          if (struts) {
            action.addMessage(action.getText("projectInnovations.leadOrganization"));
            action.addMissingField("projectInnovations.leadOrganization");
            action.getInvalidFields().put("input-innovation.projectInnovationInfo.leadOrganization.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      }
    }

    // Validate contributing organizations
    // NOTE -> FOR SOME REASON "CLEAR LEAD" MEANS "NOT A CLEAR LEAD", SO WE HAVE TO REVERSE THE CONDITIONAL
    if (clearLead != null && /* NO */clearLead == true) {
      if ((projectInnovation.getContributingOrganizations() == null)
        || projectInnovation.getContributingOrganizations().isEmpty()
        || projectInnovation.getContributingOrganizations().size() > 5) {
        if (struts) {
          action.addMessage(action.getText(action.getText("projectInnovations.contributingOrganizations")));
          action.addMissingField("innovation.contributingOrganizations");
          action.getInvalidFields().put("list-innovation.contributingOrganizations",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Contributing organizations"}));
        }
      }
    } else {
      if (projectInnovation.getContributingOrganizations() != null
        && projectInnovation.getContributingOrganizations().size() > 5) {
        if (struts) {
          action.addMessage(action.getText(action.getText("innovation.contributingOrganizations")));
          action.addMissingField("innovation.contributingOrganizations");
          action.getInvalidFields().put("list-innovation.contributingOrganizations",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Contributing organizations"}));
        }
      }
    }

    // Validate adaptative research narrative
    if (!(this.wordCount(innovationInfo.getAdaptativeResearchNarrative()) <= 800)) {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.adaptativeResearchNarrative"));
        action.addMissingField("projectInnovations.adaptativeResearchNarrative");
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.novelOrAdaptative",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    // Validate Innovation Centers
    if (!resultProgessValidate) {
      if (projectInnovation.getCenters() == null || projectInnovation.getCenters().isEmpty()) {
        action.addMessage(action.getText("projectInnovations.contributingCenters"));
        action.addMissingField("innovation.centers");
        action.getInvalidFields().put("list-innovation.centers",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"centers"}));
      }
    }

    innovationGeneral = action.getMissingFields().toString();
    if (projectInnovation != null && projectInnovation.getId() != null && (innovationGeneral.length() > 0)) {
      BaseAction.getIsInnovationGeneralInformationCompleteMap().put("" + projectInnovation.getId(), "1");
    }


    /*
     * if (action.getValidationMessage() == null || action.getValidationMessage().toString() == null
     * || action.getValidationMessage().toString().isEmpty()) {
     * this.saveMissingFields(project, projectInnovation, action.getActualPhase().getDescription(), year, upkeep,
     * ProjectSectionStatusEnum.INNOVATIONS.getStatus(), "");
     * } else {
     * this.saveMissingFields(project, projectInnovation, action.getActualPhase().getDescription(), year, upkeep,
     * ProjectSectionStatusEnum.INNOVATIONS.getStatus(), action.getMissingFields().toString());
     * }
     */

  }

  /**
   * Validate the data of the Innovation Readiness tab
   *
   * @param action base action
   * @param project related project
   * @param projectInnovation An specific projectInnovation
   * @param saving related action
   */
  public void validateInnovationReadiness(BaseAction action, Project project, ProjectInnovation projectInnovation,
    boolean saving) {
    if (projectInnovation.getProjectInnovationInfo(action.getActualPhase()) != null) {
      ProjectInnovationInfo innovationInfo = projectInnovation.getProjectInnovationInfo(action.getActualPhase());

      if (!(this.isValidString(innovationInfo.getReadinessReason()))) {
        action.addMessage(action.getText("innovation.projectInnovationInfo.readinessReason"));
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.readinessReason",
          InvalidFieldsMessages.EMPTYFIELD);
      }

    }
    innovationReadiness = action.getMissingFields().toString();
    if (projectInnovation.getId() != null && (innovationAlliance.length() > 0)) {
      BaseAction.getIsInnovationReadinessCompleteMap().put("" + projectInnovation.getId(), "1");
    }
  }

  /**
   * Validate the data of the Innovation Rights tab
   *
   * @param action base action
   * @param project related project
   * @param projectInnovation An specific projectInnovation
   * @param saving related action
   */
  public void validateInnovationRights(BaseAction action, Project project, ProjectInnovation projectInnovation,
    boolean saving) {
    if (projectInnovation.getProjectInnovationInfo(action.getActualPhase()) != null) {
      ProjectInnovationInfo innovationInfo = projectInnovation.getProjectInnovationInfo(action.getActualPhase());

      if (!(this.isValidString(innovationInfo.getReasonNotKnowledgePotential()))) {
        action.addMessage(action.getText("innovation.projectInnovationInfo.reasonNotKnowledgePotential"));
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.reasonNotKnowledgePotential",
          InvalidFieldsMessages.EMPTYFIELD);
      }

    }
    innovationRights = action.getMissingFields().toString();
    if (projectInnovation.getId() != null && (innovationAlliance.length() > 0)) {
      BaseAction.getIsInnovationRightsCompleteMap().put("" + projectInnovation.getId(), "1");
    }
  }


  /**
   * Validate if the current phase is progress
   *
   * @param action base action
   * @return validation result
   */
  public boolean validateIsProgress(BaseAction action) {
    boolean result = false;
    try {
      if (action.isProgressActive()) {
        result = true;
      }
      return result;
    } catch (Exception e) {
      LOG.error(" error in validateIsProgress function [ProjectInnovationValidator]");
      return result;
    }
  }

  /**
   * Validate the data of the oneCgiarAllignment tab
   *
   * @param action base action
   * @param project related project
   * @param projectInnovation An specific projectInnovation
   * @param saving related action
   */
  public void validateOneCgiarAlignment(BaseAction action, Project project, ProjectInnovation projectInnovation,
    boolean saving) {
    if (projectInnovation.getProjectInnovationInfo(action.getActualPhase()) != null) {
      ProjectInnovationInfo innovationInfo = projectInnovation.getProjectInnovationInfo(action.getActualPhase());

      if (innovationInfo.getHasCgiarContribution() == null) {
        action.addMessage(action.getText("innovation.projectInnovationInfo.hasCgiarContribution"));
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.hasCgiarContribution",
          InvalidFieldsMessages.EMPTYFIELD);
      } else {
        // When the has CGIAR contribution question is true
        if (innovationInfo.getHasCgiarContribution()) {
          if (!(this.isValidString(innovationInfo.getInnovationImportance()))) {
            action.addMessage(action.getText("innovation.projectInnovationInfo.innovationImportance"));
            action.getInvalidFields().put("input-innovation.projectInnovationInfo.innovationImportance",
              InvalidFieldsMessages.EMPTYFIELD);
          }
          // When the CGIAR contribution is false
        } else if (innovationInfo.getHasCgiarContribution() == false
          && !(this.isValidString(innovationInfo.getReasonNotCgiarContribution()))) {
          action.addMessage(action.getText("innovation.projectInnovationInfo.reasonNotCgiarContribution"));
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.reasonNotCgiarContribution",
            InvalidFieldsMessages.EMPTYFIELD);
        }

      }
    }
    innovationOneCgiar = action.getMissingFields().toString();
    if (projectInnovation.getId() != null && (innovationAlliance.length() > 0)) {
      BaseAction.getIsInnovationOneCgiarAlignmentCompleteMap().put("" + projectInnovation.getId(), "1");
    }
  }

  /*
   * private void validateProjectInnovation(BaseAction action, ProjectInnovation projectInnovation, boolean struts) {
   * // Validate Title
   * if (!(this.isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getTitle())
   * && this.wordCount(projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getTitle()) <= 30)) {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.title"));
   * action.addMissingField("projectInnovations.title");
   * action.getInvalidFields().put("input-innovation.projectInnovationInfo.title", InvalidFieldsMessages.EMPTYFIELD);
   * }
   * }
   * // Validate Narrative
   * if (!(this.wordCount(projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getNarrative()) <= 75)) {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.narrative"));
   * action.addMissingField("projectInnovations.narrative");
   * action.getInvalidFields().put("input-innovation.projectPolicyInfo.narrativeEvidence",
   * InvalidFieldsMessages.EMPTYFIELD);
   * }
   * }
   * // validate Milestones
   * if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()) != null
   * && (projectInnovation.getProjectInnovationInfo().getHasMilestones() != null
   * && projectInnovation.getProjectInnovationInfo().getHasMilestones() == true
   * && (projectInnovation.getMilestones() == null || projectInnovation.getMilestones().isEmpty()))
   * || projectInnovation.getProjectInnovationInfo().getHasMilestones() == null) {
   * action.addMessage(action.getText("milestones"));
   * action.addMissingField("innovation.milestones");
   * action.getInvalidFields().put("list-innovation.milestones",
   * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"milestones"}));
   * }
   * // Validate SubIdos
   * if (projectInnovation.getSubIdos() == null || projectInnovation.getSubIdos().isEmpty()) {
   * action.addMessage(action.getText("subIdos"));
   * action.addMissingField("innovation.subIdos");
   * action.getInvalidFields().put("list-innovation.subIdos",
   * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
   * int count = 0;
   * for (ProjectInnovationSubIdo subido : projectInnovation.getSubIdos()) {
   * if (subido.getPrimary() != null && subido.getPrimary() == true) {
   * count++;
   * }
   * }
   * if (count == 0) {
   * action.addMessage(action.getText("subIdos"));
   * action.addMissingField("innovation.subIdos");
   * action.getInvalidFields().put("list-innovation.subIdos",
   * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
   * }
   * }
   * // Validate Innovation Centers
   * if (projectInnovation.getCenters() == null || projectInnovation.getCenters().isEmpty()) {
   * action.addMessage(action.getText("projectInnovations.contributingCenters"));
   * action.addMissingField("projectInnovations.contributingCenters");
   * action.getInvalidFields().put("input-innovation.centers",
   * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"centers"}));
   * }
   * // Validate Stage of Innovation
   * if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation() != null) {
   * if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation()
   * .getId() == null
   * || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation()
   * .getId() == -1) {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.stage"));
   * action.addMissingField("projectInnovations.stage");
   * action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndStageInnovation.id",
   * InvalidFieldsMessages.EMPTYFIELD);
   * }
   * } else {
   * // Validate if Stage is = 4 and review if the innovation has an Organization Types and Outcome Case Study
   * if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation()
   * .getId() == 4) {
   * // Validate Organization Types
   * if (projectInnovation.getOrganizations() == null || projectInnovation.getOrganizations().isEmpty()) {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.nextUserOrganizationalType"));
   * action.addMissingField("projectInnovations.nextUserOrganizationalType");
   * action.getInvalidFields().put("list-innovation.organizations",
   * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Organization Types"}));
   * }
   * }
   * // Validate Outcome Case Study
   * if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase())
   * .getProjectExpectedStudy() != null) {
   * if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getProjectExpectedStudy()
   * .getId() == null
   * || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getProjectExpectedStudy()
   * .getId() == -1) {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.outcomeCaseStudy"));
   * action.addMissingField("projectInnovations.outcomeCaseStudy");
   * action.getInvalidFields().put("input-innovation.projectInnovationInfo.projectExpectedStudy.id",
   * InvalidFieldsMessages.EMPTYFIELD);
   * }
   * }
   * }
   * } else {
   * // Validate Evidence Link (URL)
   * if (!this
   * .isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getEvidenceLink())) {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.evidenceLink"));
   * action.addMissingField("projectInnovations.evidenceLink");
   * action.getInvalidFields().put("input-innovation.projectInnovationInfo.evidenceLink",
   * InvalidFieldsMessages.EMPTYFIELD);
   * }
   * }
   * }
   * }
   * } else {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.stage"));
   * action.addMissingField("projectInnovations.stage");
   * action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndStageInnovation.id",
   * InvalidFieldsMessages.EMPTYFIELD);
   * }
   * }
   * // Validate Geographic Scope
   * boolean haveRegions = false;
   * boolean haveCountries = false;
   * if (projectInnovation.getGeographicScopes() == null || projectInnovation.getGeographicScopes().isEmpty()) {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.geographicScope"));
   * action.getInvalidFields().put("list-innovation.geographicScopes",
   * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"geographicScopes"}));
   * action.addMissingField("projectInnovations.geographicScope");
   * }
   * } else {
   * for (ProjectInnovationGeographicScope innovationGeographicScope : projectInnovation.getGeographicScopes()) {
   * if (innovationGeographicScope.getRepIndGeographicScope().getId() == 2) {
   * haveRegions = true;
   * }
   * if (innovationGeographicScope.getRepIndGeographicScope().getId() != 1
   * && innovationGeographicScope.getRepIndGeographicScope().getId() != 2) {
   * haveCountries = true;
   * }
   * }
   * }
   * if (haveRegions) {
   * // Validate Regions
   * if (projectInnovation.getRegions() == null) {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.region"));
   * action.addMissingField("projectInnovations.region");
   * action.getInvalidFields().put("list-innovation.regions",
   * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"regions"}));
   * }
   * }
   * }
   * if (haveCountries) {
   * // Validate Countries
   * if (projectInnovation.getCountriesIds() == null || projectInnovation.getCountriesIds().isEmpty()) {
   * if (struts) {
   * action.addMessage(action.getText("innovation.countries"));
   * action.addMissingField("innovation.countries");
   * action.getInvalidFields().put("input-innovation.countriesIds",
   * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"countries"}));
   * }
   * }
   * }
   * // Validate Innovation Type
   * if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType() != null) {
   * if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType()
   * .getId() == null
   * || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType()
   * .getId() == -1) {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.innovationType"));
   * action.addMissingField("projectInnovations.innovationType");
   * action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndInnovationType.id",
   * InvalidFieldsMessages.EMPTYFIELD);
   * }
   * }
   * } else {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.innovationType"));
   * action.addMissingField("projectInnovations.innovationType");
   * action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndInnovationType.id",
   * InvalidFieldsMessages.EMPTYFIELD);
   * }
   * }
   * // Other Innovation Type Field
   * if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType() != null) {
   * if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType()
   * .getId() != null
   * && projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType()
   * .getId() == 6
   * && (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getOtherInnovationType() == null
   * || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getOtherInnovationType()
   * .isEmpty())) {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.otherInnovation"));
   * action.addMissingField("projectInnovations.otherInnovation");
   * action.getInvalidFields().put("input-innovation.projectInnovationInfo.otherInnovationType",
   * InvalidFieldsMessages.EMPTYFIELD);
   * }
   * }
   * }
   * // Validate Description Stage
   * if (!(this
   * .isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getDescriptionStage())
   * && this
   * .wordCount(projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getDescriptionStage()) <= 50)) {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.stageDescription"));
   * action.addMissingField("projectInnovations.stageDescription");
   * action.getInvalidFields().put("input-innovation.projectInnovationInfo.descriptionStage",
   * InvalidFieldsMessages.EMPTYFIELD);
   * }
   * }
   * // Validate lead organization
   * if (clearLead == null || clearLead == false) {
   * if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getLeadOrganization() != null) {
   * if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getLeadOrganization()
   * .getId() == null
   * || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getLeadOrganization()
   * .getId() == -1) {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.leadOrganization"));
   * action.addMissingField("projectInnovations.leadOrganization");
   * action.getInvalidFields().put("list-innovation.projectInnovationInfo.leadOrganization.id",
   * InvalidFieldsMessages.EMPTYFIELD);
   * }
   * }
   * } else {
   * action.addMessage(action.getText("projectInnovations.leadOrganization"));
   * action.getInvalidFields().put("list-innovation.projectInnovationInfo.leadOrganization.id",
   * InvalidFieldsMessages.EMPTYFIELD);
   * }
   * }
   * // Validate contributing organizations
   * if (projectInnovation.getContributingOrganizations() == null
   * || projectInnovation.getContributingOrganizations().isEmpty()) {
   * if (struts) {
   * action.addMessage(action.getText(action.getText("projectInnovations.contributingOrganizations")));
   * action.addMissingField("projectInnovations.contributingOrganizations");
   * action.getInvalidFields().put("input-innovation.contributingOrganizations",
   * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Contributing organizations"}));
   * }
   * }
   * // Validate adaptative research narrative
   * if (!(this.wordCount(
   * projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getAdaptativeResearchNarrative()) <= 800)) {
   * if (struts) {
   * action.addMessage(action.getText("projectInnovations.adaptativeResearchNarrative"));
   * action.addMissingField("projectInnovations.adaptativeResearchNarrative");
   * action.getInvalidFields().put("input-innovation.projectInnovationInfo.novelOrAdaptative",
   * InvalidFieldsMessages.EMPTYFIELD);
   * }
   * }
   * }
   */

}
