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

  @Inject
  public ProjectInnovationValidator(GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(ProjectInnovation innovation, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = innovation.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.INNOVATION.getStatus().replace("/", "_");
    String autoSaveFile = innovation.getId() + "_" + composedClassName + "_" + action.getActualPhase().getDescription()
      + "_" + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Project project, ProjectInnovation innovation, boolean saving) {

    action.setInvalidFields(new HashMap<>());
    baseAction = action;

    if (!saving) {
      Path path = this.getAutoSaveFilePath(innovation, action.getCrpID(), action);
      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }

    this.validateProjectInnovation(action, innovation);

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }
    this.saveMissingFields(project, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      ProjectSectionStatusEnum.INNOVATIONS.getStatus(), action);
  }

  private void validateProjectInnovation(BaseAction action, ProjectInnovation projectInnovation) {

    // Validate Title
    if (!this.isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getTitle())
      && this.wordCount(projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getTitle()) <= 20) {
      action.addMessage(action.getText("Title"));
      action.addMissingField("projectInnovations.title");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.title", InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Narrative
    if (!this.isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getNarrative())
      && this.wordCount(projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getNarrative()) <= 50) {
      action.addMessage(action.getText("Narrative of The Innovation"));
      action.addMissingField("projectInnovations.narrative");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.narrative",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Phase Research
    if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase())
      .getRepIndPhaseResearchPartnership() != null) {
      if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndPhaseResearchPartnership()
        .getId() == null
        || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndPhaseResearchPartnership()
          .getId() == -1) {
        action.addMessage(action.getText("Phase of Research"));
        action.addMissingField("projectInnovations.phase");
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndPhaseResearchPartnership.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("Phase of Research"));
      action.addMissingField("projectInnovations.phase");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndPhaseResearchPartnership.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Stage of Innovation
    if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation() != null) {
      if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation()
        .getId() == null
        || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation()
          .getId() == -1) {
        action.addMessage(action.getText("Stage of innovation"));
        action.addMissingField("projectInnovations.stage");
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndStageInnovation.id",
          InvalidFieldsMessages.EMPTYFIELD);
      } else {
        // Validate if Stage is = 4 and review if the innovation has an Organization Types and Outcome Case Study
        if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndStageInnovation()
          .getId() == 4) {
          // Validate Organization Types
          if (projectInnovation.getOrganizations() == null || projectInnovation.getOrganizations().isEmpty()) {
            action.addMessage(action.getText("Organization Types"));
            action.addMissingField("projectInnovations.nextUserOrganizationalType");
            action.getInvalidFields().put("list-innovation.organizations",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Organization Types"}));
          }

          // Validate Outcome Case Study
          if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase())
            .getProjectExpectedStudy() != null) {
            if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getProjectExpectedStudy()
              .getId() == null
              || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getProjectExpectedStudy()
                .getId() == -1) {
              action.addMessage(action.getText("Outcome Case Study"));
              action.addMissingField("projectInnovations.outcomeCaseStudy");
              action.getInvalidFields().put("input-innovation.projectInnovationInfo.projectExpectedStudy.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          } else {
            action.addMessage(action.getText("Outcome Case Study"));
            action.addMissingField("projectInnovations.outcomeCaseStudy");
            action.getInvalidFields().put("input-innovation.projectInnovationInfo.projectExpectedStudy.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      }
    } else {
      action.addMessage(action.getText("Stage of innovation"));
      action.addMissingField("projectInnovations.stage");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndStageInnovation.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }


    // Validate Geographic Scope
    if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndGeographicScope() != null) {
      if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndGeographicScope()
        .getId() == null
        || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndGeographicScope()
          .getId() == -1) {
        action.addMessage(action.getText("Geographic Scope"));
        action.addMissingField("projectInnovations.geographicScope");
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndGeographicScope.id",
          InvalidFieldsMessages.EMPTYFIELD);
      } else {
        // Validate if Scope is Multi-national, National or Sub-National and review if the innovation has Countries
        if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndGeographicScope()
          .getId() == 3
          || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndGeographicScope()
            .getId() == 4
          || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndGeographicScope()
            .getId() == 5) {
          // Validate Countries
          if (projectInnovation.getCountries() == null || projectInnovation.getCountries().isEmpty()) {
            action.addMessage(action.getText("Countries"));
            action.addMissingField("projectInnovations.nextUserOrganizationalType");
            action.getInvalidFields().put("list-projectInnovations.countries",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Countries"}));
          }
        }

        // Validate if Scope is Regional and review if The innovation has a region
        if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndGeographicScope()
          .getId() == 2) {
          // Validate Region
          if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndRegion() != null) {
            if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndRegion()
              .getId() == null
              || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndRegion()
                .getId() == -1) {
              action.addMessage(action.getText("Region"));
              action.addMissingField("projectInnovations.region");
              action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndRegion.id",
                InvalidFieldsMessages.EMPTYFIELD);
            }
          } else {
            action.addMessage(action.getText("Region"));
            action.addMissingField("projectInnovations.region");
            action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndRegion.id",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      }
    } else {
      action.addMessage(action.getText("Geographic Scope"));
      action.addMissingField("projectInnovations.geographicScope");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndGeographicScope.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }


    // Validate Innovation Type
    if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType() != null) {
      if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType()
        .getId() == null
        || projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getRepIndInnovationType()
          .getId() == -1) {
        action.addMessage(action.getText("Innovation Type"));
        action.addMissingField("projectInnovations.innovationType");
        action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndInnovationType.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("Phase of Research"));
      action.addMissingField("projectInnovations.innovationType");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.repIndInnovationType.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Novel
    if (!this.isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getNovel())
      && this.wordCount(projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getNovel()) <= 100) {
      action.addMessage(action.getText("Novel or adaptive research"));
      action.addMissingField("projectInnovations.novelOrAdaptative");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.novel", InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Evidence Link (URL)
    if (!this.isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getEvidenceLink())
      && !this.isValidUrl(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getEvidenceLink())) {
      action.addMessage(action.getText("Evidence Link"));
      action.addMissingField("projectInnovations.evidenceLink");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.evidenceLink",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Crps
    if (projectInnovation.getCrps() == null || projectInnovation.getCrps().isEmpty()) {
      action.addMessage(action.getText("Crps"));
      action.addMissingField("projectInnovations.contributing");
      action.getInvalidFields().put("list-innovation.crps",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Crps"}));
    }

    // Validate Gender relevance radio Button
    if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getGenderFocusLevel() == null) {
      action.addMessage(action.getText("Gender Relevance"));
      action.addMissingField("projectInnovations.genderRelevance");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.genderFocusLevel.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Gender Explaniation
    if (!this
      .isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getGenderExplaniation())
      && this.wordCount(
        projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getGenderExplaniation()) <= 100) {
      action.addMessage(action.getText("Gender explaniation"));
      action.addMissingField("projectInnovations.genderRelevance.explanation");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.genderExplaniation",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Youth relevance radio Button
    if (projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getYouthFocusLevel() == null) {
      action.addMessage(action.getText("Youth Relevance"));
      action.addMissingField("projectInnovations.youthRelevance");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.youthFocusLevel.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Gender Explaniation
    if (!this
      .isValidString(projectInnovation.getProjectInnovationInfo(baseAction.getActualPhase()).getYouthExplaniation())
      && this
        .wordCount(projectInnovation.getProjectInnovationInfo(action.getActualPhase()).getYouthExplaniation()) <= 100) {
      action.addMessage(action.getText("Youth explaniation"));
      action.addMissingField("projectInnovations.youthRelevance.explanation");
      action.getInvalidFields().put("input-innovation.projectInnovationInfo.youthExplaniation",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }


}
