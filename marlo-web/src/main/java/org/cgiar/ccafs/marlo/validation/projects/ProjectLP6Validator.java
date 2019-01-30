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
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;
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
public class ProjectLP6Validator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private BaseAction baseAction;

  @Inject
  public ProjectLP6Validator(GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(ProjectLp6Contribution projectLp6Contribution, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = projectLp6Contribution.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.CONTRIBUTIONLP6.getStatus().replace("/", "_");
    String autoSaveFile =
      projectLp6Contribution.getId() + "_" + composedClassName + "_" + action.getActualPhase().getName() + "_"
        + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Project project, ProjectLp6Contribution projectLp6Contribution,
    boolean saving) {

    action.setInvalidFields(new HashMap<>());
    baseAction = action;

    if (!saving) {
      Path path = this.getAutoSaveFilePath(projectLp6Contribution, action.getCrpID(), action);

      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }

    this.validateContributionLp6(action, projectLp6Contribution);
    this.validateContributionLp6Locations(action, projectLp6Contribution);


    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }
    this.saveMissingFields(project, projectLp6Contribution, action.getActualPhase().getDescription(),
      action.getActualPhase().getYear(), action.getActualPhase().getUpkeep(),
      ProjectSectionStatusEnum.CONTRIBUTIONLP6.getStatus(), action);
  }

  private void validateContributionLp6(BaseAction action, ProjectLp6Contribution projectLp6Contribution) {

    // Validate Narrative
    if (!(this.isValidString(projectLp6Contribution.getNarrative())
      && this.wordCount(projectLp6Contribution.getNarrative()) <= 100)) {
      action.addMessage(action.getText("projects.LP6Contribution.narrativeContribution.readText"));
      action.getInvalidFields().put("input-project.projectLp6Contribution.narrative", InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate TopThreePartnershipsNarrative
    if (!(this.isValidString(projectLp6Contribution.getTopThreePartnershipsNarrative())
      && this.wordCount(projectLp6Contribution.getTopThreePartnershipsNarrative()) <= 100)) {
      action.addMessage(action.getText("projects.LP6Contribution.partnerships.readText"));
      action.getInvalidFields().put("input-project.projectLp6Contribution.topThreePartnershipsNarrative",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Working Across Flagships
    if (projectLp6Contribution.getWorkingAcrossFlagships() != null) {
      if (projectLp6Contribution.getWorkingAcrossFlagships()) {
        if (!(this.isValidString(projectLp6Contribution.getWorkingAcrossFlagshipsNarrative())
          && this.wordCount(projectLp6Contribution.getWorkingAcrossFlagshipsNarrative()) <= 100)) {
          action.addMessage(action.getText("projects.LP6Contribution.workingAcrossFlagships.question.readText"));
          action.getInvalidFields().put("input-project.projectLp6Contribution.workingAcrossFlagshipsNarrative",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    } else {
      action.addMessage(action.getText("projects.LP6Contribution.flagshipLevels"));
      action.getInvalidFields().put("input-project.projectLp6Contribution.workingAcrossFlagships",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Undertaking Efforts Leading
    if (projectLp6Contribution.getUndertakingEffortsLeading() != null) {
      if (projectLp6Contribution.getUndertakingEffortsLeading()) {
        if (!(this.isValidString(projectLp6Contribution.getUndertakingEffortsLeadingNarrative())
          && this.wordCount(projectLp6Contribution.getUndertakingEffortsLeadingNarrative()) <= 100)) {
          action.addMessage(action.getText("projects.LP6Contribution.positionCGIAR.question.readText"));
          action.getInvalidFields().put("input-project.projectLp6Contribution.undertakingEffortsLeadingNarrative",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    } else {
      action.addMessage(action.getText("projects.LP6Contribution.positionCGIAR"));
      action.getInvalidFields().put("input-project.projectLp6Contribution.undertakingEffortsLeading",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Providing Pathways
    if (projectLp6Contribution.getProvidingPathways() != null) {
      if (projectLp6Contribution.getProvidingPathways()) {
        if (!(this.isValidString(projectLp6Contribution.getProvidingPathwaysNarrative())
          && this.wordCount(projectLp6Contribution.getProvidingPathwaysNarrative()) <= 100)) {
          action.addMessage(action.getText("projects.LP6Contribution.innovativePathways.question.readText"));
          action.getInvalidFields().put("input-project.projectLp6Contribution.providingPathwaysNarrative",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    } else {
      action.addMessage(action.getText("projects.LP6Contribution.innovativePathways"));
      action.getInvalidFields().put("input-project.projectLp6Contribution.providingPathways",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Undertaking Efforts Csa
    if (projectLp6Contribution.getUndertakingEffortsCsa() != null) {
      if (projectLp6Contribution.getUndertakingEffortsCsa()) {
        if (!(this.isValidString(projectLp6Contribution.getUndertakingEffortsCsaNarrative())
          && this.wordCount(projectLp6Contribution.getUndertakingEffortsCsaNarrative()) <= 100)) {
          action.addMessage(action.getText("projects.LP6Contribution.scalingCSA.question.readText"));
          action.getInvalidFields().put("input-project.projectLp6Contribution.undertakingEffortsCsaNarrative",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    } else {
      action.addMessage(action.getText("projects.LP6Contribution.scalingCSA"));
      action.getInvalidFields().put("input-project.projectLp6Contribution.undertakingEffortsCsa",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    // Validate Initiative Related
    if (projectLp6Contribution.getInitiativeRelated() != null) {
      if (projectLp6Contribution.getInitiativeRelated()) {
        if (!(this.isValidString(projectLp6Contribution.getInitiativeRelatedNarrative())
          && this.wordCount(projectLp6Contribution.getInitiativeRelatedNarrative()) <= 100)) {
          action.addMessage(action.getText("projects.LP6Contribution.climateFinance.question.readText"));
          action.getInvalidFields().put("input-project.projectLp6Contribution.initiativeRelatedNarrative",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    } else {
      action.addMessage(action.getText("projects.LP6Contribution.climateFinance"));
      action.getInvalidFields().put("input-project.projectLp6Contribution.initiativeRelated",
        InvalidFieldsMessages.EMPTYFIELD);
    }

  }

  private void validateContributionLp6Locations(BaseAction action, ProjectLp6Contribution projectLp6Contribution) {
    // Deliverable Locations
    if (projectLp6Contribution.getGeographicScope() != null
      && projectLp6Contribution.getGeographicScope().getId() != null
      && projectLp6Contribution.getGeographicScope().getId() != -1
      && !projectLp6Contribution.getGeographicScope().getId().equals(action.getReportingIndGeographicScopeGlobal())) {


      if (projectLp6Contribution.getGeographicScope().getId().equals(action.getReportingIndGeographicScopeRegional())) {
        if (projectLp6Contribution.getRegions() == null || projectLp6Contribution.getRegions().isEmpty()) {
          action.addMessage(action.getText("projects.LP6Contribution.region"));
          action.getInvalidFields().put("list-project.projectLp6Contribution.regions",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }

      if (projectLp6Contribution.getGeographicScope().getId()
        .equals(action.getReportingIndGeographicScopeMultiNational())
        || projectLp6Contribution.getGeographicScope().getId().equals(action.getReportingIndGeographicScopeNational())
        || projectLp6Contribution.getGeographicScope().getId()
          .equals(action.getReportingIndGeographicScopeSubNational())) {
        if (projectLp6Contribution.getCountriesIds() == null || projectLp6Contribution.getCountriesIds().isEmpty()) {
          action.addMessage(action.getText("projects.LP6Contribution.countries"));
          action.getInvalidFields().put("input-project.projectLp6Contribution.countriesIds",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }
  }


}
