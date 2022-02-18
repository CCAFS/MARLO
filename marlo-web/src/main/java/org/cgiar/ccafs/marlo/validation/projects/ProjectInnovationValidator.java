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
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationEvidenceLink;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.utils.Patterns;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ProjectInnovationValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private BaseAction baseAction;
  // private Boolean clearLead;

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

    // The validator is called by Struts
    if (struts) {
      action.setInvalidFields(new HashMap<>());
      baseAction = action;
    }

    if (!saving) {
      Path path = this.getAutoSaveFilePath(projectInnovation, action.getCrpID(), action);
      if (path.toFile().exists()) {
        // Draft label cause that the section appears like there were missing fields
        // this.addMissingField("draft");
      }
    }

    // this.clearLead = clearLead;

    // this.validateProjectInnovation(baseAction, innovation, struts);


    // Validate Title
    if (!(this.isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getTitle())
      && this.wordCount(projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getTitle()) <= 30)) {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.title"));
        action.addMissingField("projectInnovations.title");
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.title", InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    // Validate Narrative (removed. marked on front as optional)
    /*
     * if (!(this.wordCount(projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getNarrative()) <= 75))
     * {
     * if (struts) {
     * action.addMessage(action.getText("projectInnovations.narrative"));
     * action.addMissingField("projectInnovations.narrative");
     * action.getInvalidFields().put("input-innovation.projectPolicyInfo.narrativeEvidence",
     * InvalidFieldsMessages.EMPTYFIELD);
     * }
     * }
     */

    // validate Milestones
    if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()) != null
      && (projectInnovation.getProjectInnovationInfo().getHasMilestones() != null
        && projectInnovation.getProjectInnovationInfo().getHasMilestones() == true
        && (projectInnovation.getMilestones() == null || projectInnovation.getMilestones().isEmpty()))
      || projectInnovation.getProjectInnovationInfo().getHasMilestones() == null) {
      action.addMessage(action.getText("milestones"));
      action.addMissingField("innovation.milestones");
      action.getInvalidFields().put("list-innovation.milestones",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"milestones"}));
    }

    if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()) != null
      && (projectInnovation.getProjectInnovationInfo().getHasMilestones() == null)) {
      action.addMessage(action.getText("milestones"));
      action.addMissingField("innovation.milestones");
      action.getInvalidFields().put("list-innovation.milestones",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"milestones"}));
    } else {

      // Validate primary milestones
      if (projectInnovation.getMilestones() != null
        && (projectInnovation.getProjectInnovationInfo().getHasMilestones() != null
          && projectInnovation.getProjectInnovationInfo().getHasMilestones() == true
          && !projectInnovation.getMilestones().isEmpty())) {
        if (!action.isSelectedPhaseAR2021()) {
          int count = 0;
          for (ProjectInnovationMilestone innovationMilestone : projectInnovation.getMilestones()) {
            if (innovationMilestone.getPrimary() != null && innovationMilestone.getPrimary()) {
              count++;
            }
          }

          if (count != 1) {
            action.addMessage(action.getText("milestones"));
            action.addMissingField("innovation.milestones");
            action.getInvalidFields().put("list-innovation.milestones",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"milestones"}));
          }
        }
      }
    }

    // Validate Sub-Idos
    if (projectInnovation.getSubIdos() == null || projectInnovation.getSubIdos().isEmpty()) {
      action.addMessage(action.getText("subIdos"));
      action.addMissingField("innovation.subIdos");
      action.getInvalidFields().put("list-innovation.subIdos",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"subIdos"}));
    } else {
      // Validate primary sub-IDO
      if (projectInnovation.getSubIdos().size() > 1) {
        if (projectInnovation.getSubIdos().size() > 3) {
          action.addMessage(action.getText("subIdos"));
          action.addMissingField("study.stratgicResultsLink.subIDOs");
          action.getInvalidFields().put("list-expectedStudy.subIdos",
            action.getText(InvalidFieldsMessages.WRONGVALUE, new String[] {"subIdos"}));
        }

        int count = 0;
        for (ProjectInnovationSubIdo innovationSubIdo : projectInnovation.getSubIdos()) {
          if (innovationSubIdo.getPrimary() != null && innovationSubIdo.getPrimary()) {
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

    // Validate Stage of Innovation
    if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation() != null) {
      if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation()
        .getId() == null
        || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation()
          .getId() == -1) {
        if (struts) {
          action.addMessage(action.getText("projectInnovations.stage"));
          action.addMissingField("projectInnovations.stage");
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndStageInnovation.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      } else {
        // Validate if Stage is = 4 and review if the innovation has an Organization Types and Outcome Case Study
        if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation()
          .getId() == 4) {
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
          if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase())
            .getProjectExpectedStudy() != null) {
            if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getProjectExpectedStudy()
              .getId() == null
              || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getProjectExpectedStudy()
                .getId() == -1) {
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
          // Validate stage different to 1 and 3
          boolean hasDeliverables = true;
          boolean validEvidenceLinks = true;

          if (action.isNotEmpty(projectInnovation.getInnovationLinks())) {
            for (int i = 0; i < projectInnovation.getInnovationLinks().size(); i++) {
              ProjectInnovationEvidenceLink link = projectInnovation.getInnovationLinks().get(i);
              if (link == null || link.getLink() == null || !Patterns.WEB_URL.matcher(link.getLink()).find()) {
                if (struts) {
                  // Does not work. On load there is not an specific way we can know the links are going to
                  // be loaded in the same order they were saved
                  /*
                   * action.addMessage(action.getText("Evidence Link"));
                   * action.addMissingField("Evidence Link");
                   * action.getInvalidFields().put("input-innovation.innovationLinks[" + i + "].link",
                   * InvalidFieldsMessages.EMPTYFIELD);
                   */
                  validEvidenceLinks = false;
                }
              }
            }
            if (!validEvidenceLinks) {
              action.addMessage(action.getText("Evidence Link"));
              action.addMissingField("Evidence Link");
              action.getInvalidFields().put("innovation.projectInnovationInfo.evidenceLink",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          } else {
            validEvidenceLinks = false;
            /*
             * action.addMessage(action.getText("Evidence Link"));
             * action.addMissingField("Evidence Link");
             * action.getInvalidFields().put("innovation.projectInnovationInfo.evidenceLink",
             * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Evidence Link"}));
             */
          }

          // Validate deliverables
          if (action.isEmpty(projectInnovation.getProjectInnovationDeliverables())) {
            hasDeliverables = false;
            if (struts) {
              /*
               * action.addMessage(action.getText("projectInnovations.evidenceLink"));
               * action.addMissingField("projectInnovations.evidenceLink");
               * action.getInvalidFields().put("input-innovation.projectInnovationInfo.evidenceLink",
               * InvalidFieldsMessages.EMPTYFIELD);
               */
            }
          }

          // validate evidences (either deliverables and/or links)
          if (!(hasDeliverables || validEvidenceLinks)) {
            action.addMessage(action.getText("projectInnovations.evidenceLink"));
            action.addMissingField("projectInnovations.evidenceLink");
            action.getInvalidFields().put("input-innovation.projectInnovationInfo.evidenceLink",
              InvalidFieldsMessages.EMPTYFIELD);
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


    // Validate Geographic Scope
    boolean haveRegions = false;
    boolean haveCountries = false;

    if (projectInnovation.getGeographicScopes() == null || projectInnovation.getGeographicScopes().isEmpty()) {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.geographicScope"));
        action.getInvalidFields().put("list-innovation.geographicScopes",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"geographicScopes"}));
        action.addMissingField("projectInnovations.geographicScope");
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

    // Validate Innovation Type
    if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType() != null) {
      if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType()
        .getId() == null
        || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType()
          .getId() == -1) {
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
    if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType() != null) {
      if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType()
        .getId() != null
        && projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType()
          .getId() == 6
        && (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getOtherInnovationType() == null
          || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getOtherInnovationType()
            .isEmpty())) {
        if (struts) {
          action.addMessage(action.getText("projectInnovations.otherInnovation"));
          action.addMissingField("projectInnovations.otherInnovation");
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.otherInnovationType",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }

    // Validate Description Stage
    if (!(this
      .isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getDescriptionStage())
      && this
        .wordCount(projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getDescriptionStage()) <= 50)) {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.stageDescription"));
        action.addMissingField("projectInnovations.stageDescription");
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.descriptionStage",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    if (projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getRepIndInnovationType() != null
      && projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getRepIndInnovationType().getId() != null
      && projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getRepIndInnovationType().getId() != -1L
      && projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getRepIndStageInnovation() != null
      && projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getRepIndStageInnovation().getId() != null
      && projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getRepIndStageInnovation()
        .getId() != -1L) {
      if (projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getRepIndInnovationType().getId() == 1L) {
        if (projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getRepIndStageInnovation()
          .getId() < 3) {
          if (projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getInnovationNumber() == null
            || projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getInnovationNumber() < 1L) {
            action.addMessage(action.getText("projectInnovations.innovationNumber"));
            action.addMissingField("projectInnovations.innovationNumber");
            action.getInvalidFields().put("input-innovation.projectInnovationInfo.innovationNumber",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        } else {
          if (projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getInnovationNumber() == null
            || projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getInnovationNumber() != 1L) {
            action.addMessage(action.getText("projectInnovations.innovationNumber"));
            action.addMissingField("projectInnovations.innovationNumber");
            action.getInvalidFields().put("input-innovation.projectInnovationInfo.innovationNumber",
              InvalidFieldsMessages.WRONGVALUE);
          }
        }
      }
    }

    // Validate lead organization
    // NOTE -> FOR SOME REASON "CLEAR LEAD" MEANS "NOT A CLEAR LEAD", SO WE HAVE TO REVERSE THE CONDITIONAL
    if (clearLead == null || /* NO */clearLead == false) {
      if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getLeadOrganization() == null
        || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getLeadOrganization()
          .getId() == -1) {
        if (struts) {
          action.addMessage(action.getText("projectInnovations.leadOrganization"));
          action.addMissingField("projectInnovations.leadOrganization");
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.leadOrganization.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }

    // Validate contributing organizations
    // NOTE -> FOR SOME REASON "CLEAR LEAD" MEANS "NOT A CLEAR LEAD", SO WE HAVE TO REVERSE THE CONDITIONAL
    if (clearLead != null && /* NO */clearLead == true) {
      if (projectInnovation.getContributingOrganizations() == null
        || projectInnovation.getContributingOrganizations().size() < 2
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
    if (!(this.wordCount(
      projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getAdaptativeResearchNarrative()) <= 800)) {
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
