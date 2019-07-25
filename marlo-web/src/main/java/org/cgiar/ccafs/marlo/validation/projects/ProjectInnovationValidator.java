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
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
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
  private Boolean clearLead;

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

  public void validate(BaseAction action, Project project, ProjectInnovation innovation, Boolean clearLead,
    boolean saving, boolean struts, int year, boolean upkeep) {

    // The validator is called by Struts
    if (struts) {
      action.setInvalidFields(new HashMap<>());
      baseAction = action;
    }

    if (!saving) {
      Path path = this.getAutoSaveFilePath(innovation, action.getCrpID(), action);
      if (path.toFile().exists()) {
        this.addMissingField("draft");
      }
    }

    this.clearLead = clearLead;

    this.validateProjectInnovation(action, innovation, struts);

    // The validator is called by Struts
    if (struts) {
      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }
    }

    this.saveMissingFields(project, innovation, action.getActualPhase().getDescription(), year, upkeep,
      ProjectSectionStatusEnum.INNOVATIONS.getStatus(), this.getMissingFields().toString());
  }

  private void validateProjectInnovation(BaseAction action, ProjectInnovation projectInnovation, boolean struts) {

    // Validate Title
    if (!(this.isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getTitle())
      && this.wordCount(projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getTitle()) <= 30)) {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.title"));
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.title", InvalidFieldsMessages.EMPTYFIELD);
      }
      this.addMissingField("projectInnovations.title");
    }


    // Validate Narrative
    if (!(this.wordCount(projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getNarrative()) <= 75)) {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.narrative"));
        action.getInvalidFields().put("input-innovation.projectPolicyInfo.narrativeEvidence",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      this.addMissingField("projectInnovations.narrative");

    }

    // Validate Stage of Innovation
    if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation() != null) {
      if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation()
        .getId() == null
        || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation()
          .getId() == -1) {
        if (struts) {
          action.addMessage(action.getText("projectInnovations.stage"));
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndStageInnovation.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
        this.addMissingField("projectInnovations.stage");
      } else {
        // Validate if Stage is = 4 and review if the innovation has an Organization Types and Outcome Case Study
        if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation()
          .getId() == 4) {
          // Validate Organization Types
          if (projectInnovation.getOrganizations() == null || projectInnovation.getOrganizations().isEmpty()) {
            if (struts) {
              action.addMessage(action.getText("projectInnovations.nextUserOrganizationalType"));
              action.getInvalidFields().put("list-innovation.organizations",
                action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Organization Types"}));
            }
            this.addMissingField("projectInnovations.nextUserOrganizationalType");
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
                action.getInvalidFields().put("input-innovation.projectInnovationInfo.projectExpectedStudy.id",
                  InvalidFieldsMessages.EMPTYFIELD);
              }
              this.addMissingField("projectInnovations.outcomeCaseStudy");
            }
          } else {
            if (struts) {
              action.addMessage(action.getText("projectInnovations.outcomeCaseStudy"));
              action.getInvalidFields().put("input-innovation.projectInnovationInfo.projectExpectedStudy.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }
            this.addMissingField("projectInnovations.outcomeCaseStudy");
          }
        } else {
          // Validate Evidence Link (URL)
          if (!this
            .isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getEvidenceLink())
            && !this
              .isValidUrl(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getEvidenceLink())) {
            if (struts) {
              action.addMessage(action.getText("projectInnovations.evidenceLink"));
              action.getInvalidFields().put("input-innovation.projectInnovationInfo.evidenceLink",
                InvalidFieldsMessages.EMPTYFIELD);
            }
            this.addMissingField("projectInnovations.evidenceLink");
          }
        }
      }
    } else {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.stage"));
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndStageInnovation.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      this.addMissingField("projectInnovations.stage");
    }


    // Validate Geographic Scope
    boolean haveRegions = false;
    boolean haveCountries = false;

    if (projectInnovation.getGeographicScopes() == null || projectInnovation.getGeographicScopes().isEmpty()) {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.geographicScope"));
        action.getInvalidFields().put("list-innovation.geographicScopes",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"geographicScopes"}));
      }
      this.addMissingField("projectInnovations.geographicScope");

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
      if (projectInnovation.getRegions() == null || projectInnovation.getRegions().isEmpty()) {
        if (struts) {
          action.addMessage(action.getText("projectInnovations.region"));
          action.getInvalidFields().put("list-innovation.regions",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"regions"}));
        }
        this.addMissingField("projectInnovations.region");
      }
    }

    if (haveCountries) {
      // Validate Countries
      if (projectInnovation.getCountriesIds() == null || projectInnovation.getCountriesIds().isEmpty()) {
        if (struts) {
          action.addMessage(action.getText("innovation.countries"));
          action.getInvalidFields().put("input-innovation.countriesIds",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"countries"}));
        }
        this.addMissingField("innovation.countries");
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
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndInnovationType.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
        this.addMissingField("projectInnovations.innovationType");
      }
    } else {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.innovationType"));
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndInnovationType.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      this.addMissingField("projectInnovations.innovationType");
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
          action.getInvalidFields().put("input-innovation.projectInnovationInfo.otherInnovationType",
            InvalidFieldsMessages.EMPTYFIELD);
        }
        this.addMissingField("projectInnovations.otherInnovation");
      }
    }

    // Validate Description Stage
    if (!(this
      .isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getDescriptionStage())
      && this
        .wordCount(projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getDescriptionStage()) <= 50)) {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.stageDescription"));
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.descriptionStage",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      this.addMissingField("projectInnovations.stageDescription");
    }

    // Validate lead organization
    if (clearLead == null || clearLead == false) {
      if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getLeadOrganization() != null) {
        if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getLeadOrganization()
          .getId() == null
          || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getLeadOrganization()
            .getId() == -1) {
          if (struts) {
            action.addMessage(action.getText("projectInnovations.leadOrganization"));
            action.getInvalidFields().put("input-innovation.projectInnovationInfo.leadOrganization.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }
          this.addMissingField("projectInnovations.leadOrganization");
        }
      } else {
        action.addMessage(action.getText("projectInnovations.leadOrganization"));
        this.addMissingField("projectInnovations.leadOrganization");
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.leadOrganization.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    // Validate contributing organizations
    if (projectInnovation.getContributingOrganizations() == null
      || projectInnovation.getContributingOrganizations().isEmpty()) {
      if (struts) {
        action.addMessage(action.getText(action.getText("projectInnovations.contributingOrganizations")));
        action.getInvalidFields().put("input-innovation.contributingOrganizations", InvalidFieldsMessages.EMPTYFIELD);
        action.getInvalidFields().put("list-innovation.contributingOrganizations",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Contributing organizations"}));
      }
      this.addMissingField("projectInnovations.contributingOrganizations");
    }


    // Validate Crps
    if (projectInnovation.getCrps() == null || projectInnovation.getCrps().isEmpty()) {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.contributing"));
        action.getInvalidFields().put("list-innovation.crps",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Crps"}));
      }
      this.addMissingField("projectInnovations.contributing");
    }

    // Validate adaptative research narrative
    if (!(this.wordCount(
      projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getAdaptativeResearchNarrative()) <= 800)) {
      if (struts) {
        action.addMessage(action.getText("projectInnovations.adaptativeResearchNarrative"));
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.novelOrAdaptative",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      this.addMissingField("projectInnovations.adaptativeResearchNarrative");
    }
  }


}
