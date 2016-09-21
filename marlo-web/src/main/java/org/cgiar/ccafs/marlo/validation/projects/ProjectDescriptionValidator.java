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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.inject.Inject;

/**
 * @author Christian David Garcia -CIAT/CCAFS
 */
public class ProjectDescriptionValidator extends BaseValidator

{

  @Inject
  private CrpManager crpManager;

  @Inject
  public ProjectDescriptionValidator() {

  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.DESCRIPTION.getStatus().replace("/", "_");
    String autoSaveFile =
      project.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Project project, boolean saving) {


    if (!saving) {
      Path path = this.getAutoSaveFilePath(project, action.getCrpID());

      if (path.toFile().exists()) {
        this.addMissingField("draft");
      }
    }

    this.validateDescription(action, project);

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }

    if (action.isReportingActive()) {
      this.saveMissingFields(project, APConstants.REPORTING, action.getPlanningYear(),
        ProjectSectionStatusEnum.DESCRIPTION.getStatus());
    } else {
      this.saveMissingFields(project, APConstants.PLANNING, action.getPlanningYear(),
        ProjectSectionStatusEnum.DESCRIPTION.getStatus());
    }
  }

  public void validateDescription(BaseAction action, Project project) {
    if (!(this.isValidString(project.getTitle()) && this.wordCount(project.getTitle()) <= 20)) {
      this.addMessage(action.getText("project.title"));
    }

    if (!(this.isValidString(project.getSummary()) && this.wordCount(project.getSummary()) <= 150)) {
      this.addMessage(action.getText("project.summary"));
    }

    if (project.getLiaisonUser() != null) {
      if (project.getLiaisonUser().getId() == -1) {
        this.addMessage(action.getText("project.liaisonUser"));
      }
    } else {
      this.addMessage(action.getText("project.liaisonUser"));
    }

    if (project.getLiaisonInstitution() != null) {
      if (project.getLiaisonInstitution().getId() == -1) {
        this.addMessage(action.getText("project.liaisonInstitution"));
      }
    } else {
      this.addMessage(action.getText("project.liaisonInstitution"));
    }


    if (project.getStartDate() == null) {
      this.addMessage(action.getText("project.startDate"));
    }
    if (project.getEndDate() == null) {
      this.addMessage(action.getText("project.endDate"));
    }


    if (project.getFlagships() != null) {
      if (project.getFlagships().size() == 0) {
        this.addMessage(action.getText("projectDescription.flagships"));
      }
    } else {
      this.addMessage(action.getText("projectDescription.flagships"));
    }

    /*
     * if (project.getRegions() != null) {
     * if (project.getRegions().size() == 0) {
     * this.addMessage(action.getText("projectDescription.regions"));
     * }
     * } else {
     * this.addMessage(action.getText("projectDescription.regions"));
     * }
     */
    if (project.getClusterActivities() != null) {
      if (project.getClusterActivities().size() == 0) {
        this.addMessage(action.getText("projectDescription.clusterActivities"));
      }
    } else {
      this.addMessage(action.getText("projectDescription.clusterActivities"));
    }

    /*
     * if (project.getScopes() != null) {
     * if (project.getScopes().size() == 0) {
     * this.addMessage(action.getText("projectDescription.scope"));
     * }
     * } else {
     * this.addMessage(action.getText("projectDescription.scope"));
     * }
     */
  }

}
