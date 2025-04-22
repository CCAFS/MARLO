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
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationActor;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReference;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceComplementarySolution;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceUrl;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.jfree.util.Log;
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
  private InstitutionManager institutionManager;

  String innovationGeneral = "";
  String innovationAlliance = "";
  String innovationOneCgiar = "";
  String innovationReadiness = "";
  String innovationRights = "";

  @Inject
  public ProjectInnovationValidator(GlobalUnitManager crpManager, InstitutionManager institutionManager) {
    super();
    this.crpManager = crpManager;
    this.institutionManager = institutionManager;
  }

  private Path getAutoSaveFilePath(ProjectInnovation innovation, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = innovation.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.INNOVATION.getStatus().replace("/", "_");
    String autoSaveFile = innovation.getId() + "_" + composedClassName + "_" + action.getActualPhase().getName() + "_"
      + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  /**
   * Validate the Alliance center selection
   *
   * @param projectInnovation An specific projectInnovation
   * @param saving related action
   */
  public boolean isAllianceSelected(ProjectInnovation projectInnovation) {
    // Validate if the Alliance institution is selected
    if (projectInnovation != null && projectInnovation.getCenters() != null) {
      for (ProjectInnovationCenter center : projectInnovation.getCenters()) {
        if (center != null && center.getInstitution() != null && center.getInstitution().getId() != null) {
          Institution institutiontmp = this.institutionManager.getInstitutionById(center.getInstitution().getId());
          if (institutiontmp != null && institutiontmp.getName() != null) {
            center.getInstitution().setName(institutiontmp.getName());
          }
        }
        if (center != null && center.getInstitution() != null && center.getInstitution().getId() != null
          && center.getInstitution().getName() != null
          && center.getInstitution().getName().toLowerCase().contains(APConstants.ALLIANCE_INSTITUTION_NAME)) {
          return true;
        }
      }
    }
    return false;
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
    ProjectInnovationInfo innovationInfo = projectInnovation.getProjectInnovationInfo(action.getActualPhase());
    // Validate if the Alliance institution is selected in center section to validate the Alliance Tab
    if (this.isAllianceSelected(projectInnovation)) {

      if (projectInnovation.getSdgs() == null || projectInnovation.getSdgs().isEmpty()) {
        action.addMessage(action.getText("innovation.sdgs"));
        action.addMissingField("innovation.sdgs");
        action.getInvalidFields().put("list-innovation.sdgs", InvalidFieldsMessages.EMPTYLIST);
      }

      if (projectInnovation.getAllianceLevers() == null || projectInnovation.getAllianceLevers().isEmpty()) {
        action.addMessage(action.getText("innovation.allianceLevers"));
        action.addMissingField("innovation.allianceLevers");
        action.getInvalidFields().put("input-innovation.allianceLevers", InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    innovationAlliance = action.getMissingFields().toString();
    if (projectInnovation.getId() != null && (innovationAlliance.length() > innovationGeneral.length())) {
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
    if (!(this.isValidString(innovationInfo.getShortTitle()) && this.wordCount(innovationInfo.getShortTitle()) <= 15)) {
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
    /*
     * if (innovationInfo.getHasMilestones() == null) {
     * action.addMessage(action.getText("innovation.projectInnovationInfo.hasMilestones"));
     * action.addMissingField("innovation.projectInnovationInfo.hasMilestones");
     * action.getInvalidFields().put("input-innovation.projectInnovationInfo.hasMilestones",
     * InvalidFieldsMessages.EMPTYFIELD);
     * }
     */
    if ((projectInnovation.getCrpOutcomes() == null || projectInnovation.getCrpOutcomes().isEmpty())) {
      action.addMessage(action.getText("crpOutcomes"));
      action.addMissingField("innovation.crpOutcomes");
      action.getInvalidFields().put("list-innovation.crpOutcomes",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"crpOutcomes"}));
    }

    if (innovationInfo != null && (innovationInfo.getAreUsersDetermined() == null)) {
      action.addMessage(action.getText("areUsersDetermined"));
      action.addMissingField("innovation.areUsersDetermined");
      action.getInvalidFields().put("list-innovation.projectInnovationInfo.areUsersDetermined",
        action.getText(InvalidFieldsMessages.EMPTYFIELD));
    }


    if (!action.isAiccra() && innovationInfo != null && (innovationInfo.getHasMilestones() == null)) {
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
          if (subido.getPrimary() != null && subido.getPrimary()) {
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

    if (!resultProgessValidate && !action.isAiccra()) {
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

    if (innovationInfo.getRepIndInnovationNature() != null
      && (innovationInfo.getRepIndInnovationNature().getId() != null
        && innovationInfo.getRepIndInnovationNature().getId() == 4)
      && !(this.isValidString(innovationInfo.getOtherInnovationNature()))) {
      action.addMessage(action.getText("innovation.projectInnovationInfo.otherInnovationNature"));
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.otherInnovationNature",
        InvalidFieldsMessages.EMPTYFIELD);
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
    if (!resultProgessValidate && !action.isAiccra()) {
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
    if (!action.isAiccra() && (clearLead == null || /* NO */clearLead == false)) {
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
    if (projectInnovation.getCenters() == null || projectInnovation.getCenters().isEmpty()) {
      action.addMessage(action.getText("projectInnovations.contributingCenters"));
      action.addMissingField("innovation.centers");
      action.getInvalidFields().put("list-innovation.centers",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"centers"}));
    }

    // Validate beneficiaries
    if (!(this.isValidString(innovationInfo.getBeneficiariesNarrative()))) {
      action.addMessage(action.getText("projectInnovations.beneficiariesNarrative"));
      action.addMissingField("projectInnovations.beneficiariesNarrative");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.beneficiariesNarrative",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (innovationInfo.getInnovationBundle() == null) {
      action.addMessage(action.getText("innovation.innovationBundle"));
      action.addMissingField("innovation.innovationBundle");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.innovationBundle",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (innovationInfo.getAreUsersDetermined() != null && innovationInfo.getAreUsersDetermined()) {
      // Validate actors
      if (projectInnovation.getActors() == null || projectInnovation.getActors().isEmpty()) {
        action.addMessage(action.getText("innovation.actors"));
        action.addMissingField("innovation.actors");
        action.getInvalidFields().put("add-innovation.actors",
          action.getText(InvalidFieldsMessages.EMPTYFIELD, new String[] {"actors"}));
      }

      try {
        if (projectInnovation.getActors() != null && !projectInnovation.getActors().isEmpty()) {
          int count = 0;
          for (ProjectInnovationActor actor : projectInnovation.getActors()) {
            if (actor.getActor() == null || actor.getActor().getId() == null || actor.getActor().getId() == -1) {
              action.addMessage(action.getText("innovation.actors[" + count + "]actor.id"));
              action.addMissingField("innovation.actors[" + count + "].id");
              action.getInvalidFields().put("list-innovation.actors[" + count + "].actor.id",
                action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"actors"}));
            }

            count++;
          }

        }
      } catch (Exception e) {
        Log.error("error validating actors " + e);
      }

      // Validate organizations
      if (projectInnovation.getAllianceOrganizations() == null
        || projectInnovation.getAllianceOrganizations().isEmpty()) {
        action.addMessage(action.getText("innovation.allianceOrganizations"));
        action.addMissingField("innovation.allianceOrganizations");
        action.getInvalidFields().put("add-innovation.allianceOrganizations",
          action.getText(InvalidFieldsMessages.EMPTYFIELD, new String[] {"allianceOrganizations"}));
      }

      try {
        if (projectInnovation.getAllianceOrganizations() != null
          && !projectInnovation.getAllianceOrganizations().isEmpty()) {
          // Removed 07/02/2025
          /*
           * int count = 0;
           * for (ProjectInnovationAllianceOrganization allianceOrganizations : projectInnovation
           * .getAllianceOrganizations()) {
           * if (allianceOrganizations.getInstitutionType() == null
           * || allianceOrganizations.getInstitutionType().getId() == null
           * || allianceOrganizations.getInstitutionType().getId() == -1) {
           * action.addMessage(action.getText("innovation.allianceOrganizations[" + count + "].institutionType.id"));
           * action.addMissingField("innovation.allianceOrganizations[" + count + "].institutionType.id");
           * action.getInvalidFields().put("list-innovation.allianceOrganizations[" + count + "].institutionType.id",
           * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"actors"}));
           * }
           * count++;
           * }
           */
        }
      } catch (Exception e) {
        Log.error("error validating actors " + e);
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
    try {
      if (projectInnovation.getProjectInnovationInfo(action.getActualPhase()) != null) {
        ProjectInnovationInfo innovationInfo = projectInnovation.getProjectInnovationInfo(action.getActualPhase());

        if (!(this.isValidString(innovationInfo.getReadinessReason()))) {
          action.addMessage("innovation.projectInnovationInfo.readinessReason");
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.readinessReason",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        /*
         * if (!(this.isValidString(innovationInfo.getInnovationImportance()))) {
         * action.addMessage(action.getText("innovation.projectInnovationInfo.innovationImportance"));
         * action.getInvalidFields().put("input-innovation.projectInnovationInfo.innovationImportance",
         * InvalidFieldsMessages.EMPTYFIELD);
         * }
         */
        // Validate References Cited
        /*
         * if (projectInnovation.getReferences() == null) {
         * action.addMessage("References Cited");
         * action.getInvalidFields().put("input-innovation.references", InvalidFieldsMessages.EMPTYFIELD);
         * }
         */
        // Validate Readiness scale
        if (innovationInfo.getReadinessScale() == null) {
          action.addMessage("Readiness scale");
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.readinessScale",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        if (projectInnovation.getReferences() != null && !projectInnovation.getReferences().isEmpty()) {
          for (int i = 0; i < projectInnovation.getReferences().size(); i++) {
            ProjectInnovationReference reference = projectInnovation.getReferences().get(i);
            if (reference != null) {
              // Evidence by deliverable false
              if (reference.getEvidenceByDeliverable() != null && !reference.getEvidenceByDeliverable()) {
                if (reference.getReference() == null || !this.isValidString(reference.getReference())) {
                  action.addMessage("References Cited");
                  action.getInvalidFields().put("input-innovation.references[" + i + "].reference",
                    InvalidFieldsMessages.EMPTYFIELD);
                }
                if (reference.getLink() == null || reference.getLink().isEmpty()
                  || !this.isValidUrl(reference.getLink())) {
                  action.addMessage("Reference Link");
                  action.getInvalidFields().put("input-innovation.references[" + i + "].link",
                    InvalidFieldsMessages.EMPTYFIELD);
                }
                if (reference.getDeliverableType() == null || reference.getDeliverableType().getId() == null
                  || reference.getDeliverableType().getId() == -1) {
                  action.addMessage("References type");
                  action.getInvalidFields().put("input-innovation.references[" + i + "].deliverableType.id",
                    InvalidFieldsMessages.EMPTYFIELD);
                }
                if (reference.getDeliverableType() != null
                  && reference.getDeliverableType().getDeliverableCategory() == null
                  || (reference.getDeliverableType().getDeliverableCategory() != null
                    && reference.getDeliverableType().getDeliverableCategory().getId() == null
                    || reference.getDeliverableType().getDeliverableCategory().getId() == -1)) {
                  action.addMessage("References type");
                  action.getInvalidFields().put(
                    "input-innovation.references[" + i + "].deliverableType.deliverableCategory.id",
                    InvalidFieldsMessages.EMPTYFIELD);
                }

              } else {
                // Evidence by deliverable true
                if (reference.getEvidenceByDeliverable() != null && reference.getEvidenceByDeliverable()
                  && reference.getDeliverable() == null
                  || (reference.getDeliverable() != null && reference.getDeliverable().getId() != null
                    && reference.getDeliverable().getId() == -1)) {
                  action.addMessage("References Cited Link");
                  action.getInvalidFields().put("input-innovation.references[" + i + "].deliverable.id",
                    InvalidFieldsMessages.EMPTYFIELD);
                }
              }
              // Evidence by deliverable null
              if (reference.getEvidenceByDeliverable() == null) {
                action.addMessage("References Evidence by deliverable");
                action.getInvalidFields().put("input-innovation.references[" + i + "].evidenceByDeliverable",
                  InvalidFieldsMessages.EMPTYFIELD);
              }

            }
          }

        }

      }

      innovationReadiness = action.getMissingFields().toString();
      if (projectInnovation.getId() != null && (innovationReadiness.length() > innovationOneCgiar.length())) {
        BaseAction.getIsInnovationReadinessCompleteMap().put("" + projectInnovation.getId(), "1");
      }
    } catch (Exception e) {
      Log.error("error validating InnovationReadiness tab ");
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
    try {
      if (projectInnovation.getProjectInnovationInfo(action.getActualPhase()) != null) {
        ProjectInnovationInfo innovationInfo = projectInnovation.getProjectInnovationInfo(action.getActualPhase());

        if (projectInnovation.getToolCategories() != null && !projectInnovation.getToolCategories().isEmpty()
          && projectInnovation.getToolCategories().get(0) != null
          && projectInnovation.getToolCategories().get(0).getId() != null
          && projectInnovation.getToolCategories().get(0).getId() == 6) {
          action.addMessage(action.getText("innovation.projectInnovationInfo.otherToolNarrative"));
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.otherToolNarrative",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        // HasKnowledgePotential
        if (innovationInfo.getHasKnowledgePotential() != null) {
          if (innovationInfo.getHasKnowledgePotential()
            && !(this.isValidString(innovationInfo.getReasonKnowledgePotential()))) {
            action.addMessage(action.getText("innovation.projectInnovationInfo.reasonKnowledgePotential"));
            action.getInvalidFields().put("input-innovation.projectInnovationInfo.reasonKnowledgePotential",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          if (!innovationInfo.getHasKnowledgePotential()
            && !(this.isValidString(innovationInfo.getReasonNotKnowledgePotential()))) {
            action.addMessage(action.getText("innovation.projectInnovationInfo.reasonNotKnowledgePotential"));
            action.getInvalidFields().put("input-innovation.projectInnovationInfo.reasonNotKnowledgePotential",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }

        if (!(this.isValidString(innovationInfo.getKnowledgeResultsNarrative()))) {
          action.addMessage(action.getText("innovation.projectInnovationInfo.knowledgeResultsNarrative"));
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.knowledgeResultsNarrative",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        // Has tool URL
        if (innovationInfo.getHasToolUrl() != null) {
          if (!innovationInfo.getHasToolUrl() && !(this.isValidString(innovationInfo.getReasonNotToolUrl()))) {
            action.addMessage(action.getText("innovation.projectInnovationInfo.reasonNotToolUrl"));
            action.getInvalidFields().put("input-innovation.projectInnovationInfo.reasonNotToolUrl",
              InvalidFieldsMessages.EMPTYFIELD);
          }

          if (innovationInfo.getHasToolUrl()) {
            if (projectInnovation.getReferenceUrls() != null && !projectInnovation.getReferenceUrls().isEmpty()) {

              for (int i = 0; i < projectInnovation.getReferenceUrls().size(); i++) {
                ProjectInnovationReferenceUrl reference = projectInnovation.getReferenceUrls().get(i);
                if (reference != null) {
                  // Evidence by deliverable false
                  if (reference.getEvidenceByDeliverable() != null && !reference.getEvidenceByDeliverable()) {
                    if (reference.getReference() == null || !this.isValidString(reference.getReference())) {
                      action.addMessage("References Urls Cited");
                      action.getInvalidFields().put("input-innovation.referenceUrls[" + i + "].reference",
                        InvalidFieldsMessages.EMPTYFIELD);
                    }
                    if (reference.getLink() == null || reference.getLink().isEmpty()
                      || !this.isValidUrl(reference.getLink())) {
                      action.addMessage("Reference Urls Link");
                      action.getInvalidFields().put("input-innovation.referenceUrls[" + i + "].link",
                        InvalidFieldsMessages.EMPTYFIELD);
                    }
                    if (reference.getDeliverableType() == null || reference.getDeliverableType().getId() == null
                      || reference.getDeliverableType().getId() == -1) {
                      action.addMessage("References type");
                      action.getInvalidFields().put("input-innovation.referenceUrls[" + i + "].deliverableType.id",
                        InvalidFieldsMessages.EMPTYFIELD);
                    }
                    if (reference.getDeliverableType() != null
                      && (reference.getDeliverableType().getDeliverableCategory() == null
                        || reference.getDeliverableType().getDeliverableCategory().getId() == null
                        || reference.getDeliverableType().getDeliverableCategory().getId() == -1)) {
                      action.addMessage("References Urls type");
                      action.getInvalidFields().put(
                        "input-innovation.referenceUrls[" + i + "].deliverableType.deliverableCategory.id",
                        InvalidFieldsMessages.EMPTYFIELD);
                    }

                  } else {
                    // Evidence by deliverable true
                    if (reference.getEvidenceByDeliverable() != null && reference.getEvidenceByDeliverable()
                      && reference.getDeliverable() == null
                      || (reference.getDeliverable() != null && reference.getDeliverable().getId() != null
                        && reference.getDeliverable().getId() == -1)) {
                      action.addMessage("References Urls Cited Link");
                      action.getInvalidFields().put("input-innovation.referenceUrls[" + i + "].deliverable.id",
                        InvalidFieldsMessages.EMPTYFIELD);
                    }
                  }
                  // Evidence by deliverable null
                  if (reference.getEvidenceByDeliverable() == null) {
                    action.addMessage("References Evidence by deliverable");
                    action.getInvalidFields().put("input-innovation.referenceUrls[" + i + "].evidenceByDeliverable",
                      InvalidFieldsMessages.EMPTYFIELD);
                  }

                }
              }

            }
          }
        }


        // Validate Reference Complementary Solutions
        if (projectInnovation.getReferenceComplementarySolutions() != null
          && !projectInnovation.getReferenceComplementarySolutions().isEmpty()) {

          for (int i = 0; i < projectInnovation.getReferenceComplementarySolutions().size(); i++) {
            ProjectInnovationReferenceComplementarySolution reference =
              projectInnovation.getReferenceComplementarySolutions().get(i);
            if (reference != null) {
              // Evidence by deliverable false
              if (reference.getEvidenceByDeliverable() != null && !reference.getEvidenceByDeliverable()) {
                if (reference.getReference() == null || !this.isValidString(reference.getReference())) {
                  action.addMessage("References Complementary solutions Cited");
                  action.getInvalidFields().put("input-innovation.referenceComplementarySolutions[" + i + "].reference",
                    InvalidFieldsMessages.EMPTYFIELD);
                }
                if (reference.getLink() == null || reference.getLink().isEmpty()
                  || !this.isValidUrl(reference.getLink())) {
                  action.addMessage("Reference Complementary solutions Link");
                  action.getInvalidFields().put("input-innovation.referenceComplementarySolutions[" + i + "].link",
                    InvalidFieldsMessages.EMPTYFIELD);
                }
                if (reference.getDeliverableType() == null || reference.getDeliverableType().getId() == null
                  || reference.getDeliverableType().getId() == -1) {
                  action.addMessage("References Complementary solutions type");
                  action.getInvalidFields().put(
                    "input-innovation.referenceComplementarySolutions[" + i + "].deliverableType.id",
                    InvalidFieldsMessages.EMPTYFIELD);
                }
                if (reference.getDeliverableType() != null
                  && (reference.getDeliverableType().getDeliverableCategory() == null
                    || reference.getDeliverableType().getDeliverableCategory().getId() == null
                    || reference.getDeliverableType().getDeliverableCategory().getId() == -1)) {
                  action.addMessage("References Complementary solutions type");
                  action.getInvalidFields().put("input-innovation.referenceComplementarySolutions[" + i
                    + "].deliverableType.deliverableCategory.id", InvalidFieldsMessages.EMPTYFIELD);
                }

              } else {
                // Evidence by deliverable true
                if (reference.getEvidenceByDeliverable() != null && reference.getEvidenceByDeliverable()
                  && reference.getInnovation() == null
                  || (reference.getInnovation() != null && reference.getInnovation().getId() != null
                    && reference.getInnovation().getId() == -1)) {
                  action.addMessage("References Complementary solutions Cited Link");
                  action.getInvalidFields().put(
                    "input-innovation.referenceComplementarySolutions[" + i + "].innovation.id",
                    InvalidFieldsMessages.EMPTYFIELD);
                }
              }
              // Evidence by deliverable null
              if (reference.getEvidenceByDeliverable() == null) {
                action.addMessage("References Complementary solutions Evidence by deliverable");
                action.getInvalidFields().put(
                  "input-innovation.referenceComplementarySolutions[" + i + "].evidenceByDeliverable",
                  InvalidFieldsMessages.EMPTYFIELD);
              }

            }
          }

        }

      }

      innovationRights = action.getMissingFields().toString();
      if (projectInnovation.getId() != null && (innovationRights.length() > innovationReadiness.length())) {
        BaseAction.getIsInnovationRightsCompleteMap().put("" + projectInnovation.getId(), "1");
      }
    } catch (Exception e) {
      Log.error("error validating rights tab ");
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

      // Validate Impact Area Score
      if (innovationInfo.getGenderScore() == null) {
        action.addMessage(action.getText("innovation.projectInnovationInfo.genderScore"));
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.genderScore",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (innovationInfo.getClimateChangeScore() == null) {
        action.addMessage(action.getText("innovation.projectInnovationInfo.climateChangeScore"));
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.climateChangeScore",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (innovationInfo.getFoodSecurityScore() == null) {
        action.addMessage(action.getText("innovation.projectInnovationInfo.foodSecurityScore"));
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.foodSecurityScore",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (innovationInfo.getEnvironmentalScore() == null) {
        action.addMessage(action.getText("innovation.projectInnovationInfo.environmentalScore"));
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.environmentalScore",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (innovationInfo.getPovertyScore() == null) {
        action.addMessage(action.getText("innovation.projectInnovationInfo.povertyScore"));
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.povertyScore",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      /*
       * if (innovationInfo.getHasCgiarContribution() == null) {
       * action.addMessage(action.getText("innovation.projectInnovationInfo.hasCgiarContribution"));
       * action.getInvalidFields().put("input-innovation.projectInnovationInfo.hasCgiarContribution",
       * InvalidFieldsMessages.EMPTYFIELD);
       * } else {
       * // When the has CGIAR contribution question is true
       * if (innovationInfo.getHasCgiarContribution()) {
       * if (projectInnovation.getImpactAreas() == null || projectInnovation.getImpactAreas().isEmpty()) {
       * action.addMessage(action.getText("innovation.impactAreas"));
       * action.getInvalidFields().put("list-innovation.impactAreas", InvalidFieldsMessages.EMPTYFIELD);
       * }
       * // When the CGIAR contribution is false
       * } else if (!innovationInfo.getHasCgiarContribution()
       * && !(this.isValidString(innovationInfo.getReasonNotCgiarContribution()))) {
       * action.addMessage(action.getText("innovation.projectInnovationInfo.reasonNotCgiarContribution"));
       * action.getInvalidFields().put("input-innovation.projectInnovationInfo.reasonNotCgiarContribution",
       * InvalidFieldsMessages.EMPTYFIELD);
       * }
       * }
       */
    }
    innovationOneCgiar = action.getMissingFields().toString();
    if (projectInnovation.getId() != null && (innovationOneCgiar.length() > innovationAlliance.length())) {
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
