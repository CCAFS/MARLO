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
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian David Garcia -CIAT/CCAFS
 */
@Named
public class ProjectDescriptionValidator extends BaseValidator {

  private final CrpManager crpManager;

  @Inject
  public ProjectDescriptionValidator(CrpManager crpManager) {
    this.crpManager = crpManager;
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
    // BaseValidator does not Clean this variables.. so before validate the section, it be clear these variables
    this.missingFields.setLength(0);
    this.validationMessage.setLength(0);
    action.setInvalidFields(new HashMap<>());
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
      this.saveMissingFields(project, APConstants.REPORTING, action.getReportingYear(),
        ProjectSectionStatusEnum.DESCRIPTION.getStatus());
    } else {
      this.saveMissingFields(project, APConstants.PLANNING, action.getPlanningYear(),
        ProjectSectionStatusEnum.DESCRIPTION.getStatus());
    }
  }

  public void validateDescription(BaseAction action, Project project) {
    if (!(this.isValidString(project.getTitle()) && this.wordCount(project.getTitle()) <= 30)) {
      this.addMessage(action.getText("project.title"));
      action.getInvalidFields().put("input-project.title", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (project.isProjectEditLeader()) {
      // Validate project summary
      if (!(this.isValidString(project.getSummary()) && this.wordCount(project.getSummary()) <= 250)) {
        this.addMessage(action.getText("project.summary"));
        action.getInvalidFields().put("input-project.summary", InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    if (project.getLiaisonUser() != null) {
      if (project.getLiaisonUser().getId() == -1) {
        this.addMessage(action.getText("project.liaisonUser"));
        action.getInvalidFields().put("input-project.liaisonUser.id", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(action.getText("project.liaisonUser"));
      action.getInvalidFields().put("input-project.liaisonUser.id", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (project.getLiaisonInstitution() != null) {
      if (project.getLiaisonInstitution().getId() == -1) {
        this.addMessage(action.getText("project.liaisonInstitution"));
        action.getInvalidFields().put("input-project.liaisonInstitution.id", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(action.getText("project.liaisonInstitution"));
      action.getInvalidFields().put("input-project.liaisonInstitution.id", InvalidFieldsMessages.EMPTYFIELD);
    }


    if (project.getStartDate() == null) {
      this.addMessage(action.getText("project.startDate"));
      action.getInvalidFields().put("input-project.startDate", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (project.getEndDate() == null) {
      this.addMessage(action.getText("project.endDate"));
      action.getInvalidFields().put("input-project.endDate", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (!(project.getAdministrative() != null && project.getAdministrative().booleanValue() == true)) {

      if (project.getFlagships() != null) {
        if (project.getFlagships().size() == 0) {
          if (project.getFlagshipValue() == null || project.getFlagshipValue().length() == 0) {
            this.addMessage(action.getText("projectDescription.flagships"));
            action.getInvalidFields().put("input-project.flagshipValue", InvalidFieldsMessages.EMPTYFIELD);
          }

        }
      } else {
        if (project.getFlagshipValue().length() == 0) {
          this.addMessage(action.getText("projectDescription.flagships"));
          action.getInvalidFields().put("input-project.flagshipValue", InvalidFieldsMessages.EMPTYFIELD);
        }

      }
    }

    if (!(project.getAdministrative() != null && project.getAdministrative().booleanValue() == true)) {

      if (action.getSession().containsKey(APConstants.CRP_HAS_REGIONS)
        && action.getSession().get(APConstants.CRP_HAS_REGIONS).toString().equals("true")) {
        if ((project.getRegionsValue() == null || project.getRegionsValue().length() == 0)
          && (project.getNoRegional() == null || project.getNoRegional().booleanValue() == false)) {
          this.addMessage(action.getText("projectDescription.regions"));
          action.getInvalidFields().put("input-project.regionsValue", InvalidFieldsMessages.EMPTYFIELD);


        }
      }
    }

    if (!action.isReportingActive()) {
      if (!(project.getAdministrative() != null && project.getAdministrative().booleanValue() == true)) {
        if (project.getClusterActivities() != null) {
          if (project.getClusterActivities().size() == 0) {
            this.addMessage(action.getText("projectDescription.clusterActivities"));
            action.getInvalidFields().put("list-project.clusterActivities",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Cluster of Activites"}));
          }
        } else {
          this.addMessage(action.getText("projectDescription.clusterActivities"));
          action.getInvalidFields().put("list-project.clusterActivities",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Cluster of Activites"}));
        }
      }

      if (project.isProjectEditLeader()) {
        if (!(this.isValidString(project.getGenderAnalysis()) && this.wordCount(project.getGenderAnalysis()) <= 100)) {
          this.addMessage(action.getText("project.genderAnalysis"));
          action.getInvalidFields().put("input-project.genderAnalysis", InvalidFieldsMessages.EMPTYFIELD);
        }

        if (project.getCrossCuttingGender() == null || project.getCrossCuttingGender().booleanValue() == false) {
          if (!(this.isValidString(project.getDimension()) && this.wordCount(project.getDimension()) <= 50)) {
            this.addMessage(action.getText("project.dimension"));
            action.getInvalidFields().put("input-project.dimension", InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      }
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
