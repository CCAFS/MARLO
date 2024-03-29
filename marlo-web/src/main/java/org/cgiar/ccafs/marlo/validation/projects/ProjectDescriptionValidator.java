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
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian David Garcia -CIAT/CCAFS
 */
@Named
public class ProjectDescriptionValidator extends BaseValidator {

  @Inject
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  @Inject
  public ProjectDescriptionValidator() {

  }

  private Path getAutoSaveFilePath(Project project, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.DESCRIPTION.getStatus().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + action.getActualPhase().getName() + "_"
      + action.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";


    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Project project, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(project, action.getCrpID(), action);

      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }

    this.validateDescription(action, project);

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(project, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      action.getActualPhase().getUpkeep(), ProjectSectionStatusEnum.DESCRIPTION.getStatus(), action);
  }

  public void validateDescription(BaseAction action, Project project) {
    if (!(this.isValidString(project.getProjecInfoPhase(action.getActualPhase()).getTitle())
      && this.wordCount(project.getProjecInfoPhase(action.getActualPhase()).getTitle()) <= 30)) {
      action.addMessage(action.getText("project.title"));
      action.getInvalidFields().put("input-project.projectInfo.title", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (!(this.isValidString(project.getProjecInfoPhase(action.getActualPhase()).getSummary())
      && this.wordCount(project.getProjecInfoPhase(action.getActualPhase()).getSummary()) <= 250)) {
      action.addMessage(action.getText("project.summary"));
      action.getInvalidFields().put("input-project.projectInfo.summary", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (project.getProjecInfoPhase(action.getActualPhase()).getLiaisonInstitution() != null) {
      if (project.getProjecInfoPhase(action.getActualPhase()).getLiaisonInstitution().getId() == -1) {
        action.addMessage(action.getText("project.liaisonInstitution"));
        action.getInvalidFields().put("input-project.projectInfo.liaisonInstitution.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("project.liaisonInstitution"));
      action.getInvalidFields().put("input-project.projectInfo.liaisonInstitution.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (action.getManagementClusterType() != null
      && project.getProjecInfoPhase(action.getActualPhase()).getClusterType() != null
      && (project.getProjecInfoPhase(action.getActualPhase()).getClusterType().getId() != action
        .getManagementClusterType().getId())) {
      if (project.getProjecInfoPhase(action.getActualPhase()).getClusterType() != null) {
        if (project.getProjecInfoPhase(action.getActualPhase()).getClusterType().getId() == -1) {
          action.addMessage(action.getText("project.clusterType"));
          action.getInvalidFields().put("input-project.projectInfo.clusterType.id", InvalidFieldsMessages.EMPTYFIELD);
        }
      } else {
        action.addMessage(action.getText("project.clusterType"));
        action.getInvalidFields().put("input-project.projectInfo.clusterType.id", InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    if (project.getProjecInfoPhase(action.getActualPhase()).getStartDate() == null) {
      action.addMessage(action.getText("project.startDate"));
      action.getInvalidFields().put("input-project.projectInfo.startDate", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (project.getProjecInfoPhase(action.getActualPhase()).getEndDate() == null) {
      action.addMessage(action.getText("project.endDate"));
      action.getInvalidFields().put("input-project.projectInfo.endDate", InvalidFieldsMessages.EMPTYFIELD);
    } else {
      Calendar cal = Calendar.getInstance();
      cal.setTime(project.getProjecInfoPhase(action.getActualPhase()).getEndDate());
      if (cal.get(Calendar.YEAR) < action.getActualPhase().getYear()) {
        action.addMessage(action.getText("project.endDate"));
        action.getInvalidFields().put("input-project.projectInfo.endDate", "Invalid Date");
      }
    }

    // Validation for Non Administrative projects
    if (!(project.getProjecInfoPhase(action.getActualPhase()).getAdministrative() != null
      && project.getProjecInfoPhase(action.getActualPhase()).getAdministrative().booleanValue() == true)) {

      if (project.getFlagshipValue() == null || project.getFlagshipValue().length() == 0) {
        action.addMessage(action.getText("projectDescription.flagships"));
        action.getInvalidFields().put("input-project.flagshipValue", InvalidFieldsMessages.EMPTYFIELD);
      }


      if (!action.isCenterGlobalUnit() && !action.isAiccra()) {
        if (project.getClusterActivities() != null) {
          if (project.getClusterActivities().size() == 0) {
            action.addMessage(action.getText("projectDescription.clusterActivities"));
            action.getInvalidFields().put("list-project.clusterActivities",
              action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Cluster of Activites"}));
          }
        } else {
          action.addMessage(action.getText("projectDescription.clusterActivities"));
          action.getInvalidFields().put("list-project.clusterActivities",
            action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Cluster of Activites"}));
        }
      }

      if (action.getSession().containsKey(APConstants.CRP_HAS_REGIONS)
        && action.getSession().get(APConstants.CRP_HAS_REGIONS).toString().equals("true")) {
        if ((project.getRegionsValue() == null || project.getRegionsValue().length() == 0)
          && (project.getProjecInfoPhase(action.getActualPhase()).getNoRegional() == null
            || project.getProjecInfoPhase(action.getActualPhase()).getNoRegional().booleanValue() == false)) {
          action.addMessage(action.getText("projectDescription.regions"));
          action.getInvalidFields().put("input-project.regionsValue", InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }

    // Validate Status Justification when is not on-going
    if (project.getProjecInfoPhase(action.getActualPhase()).getStatus() != null
      && project.getProjecInfoPhase(action.getActualPhase()).getStatusJustificationRequired()) {
      if (!(this.isValidString(project.getProjecInfoPhase(action.getActualPhase()).getStatusJustification())
        && this.wordCount(project.getProjecInfoPhase(action.getActualPhase()).getStatusJustification()) <= 100)) {
        action.addMessage(action.getText("project.statusJustification"));
        action.getInvalidFields().put("input-project.projectInfo.statusJustification",
          action.getText(InvalidFieldsMessages.EMPTYLIST));
      }
    }

    // validation for center projects
    if (action.isProjectCenter(project.getId())) {
      if (project.getCenterOutcomes() == null || project.getCenterOutcomes().isEmpty()) {
        action.addMessage(action.getText("projectDescription.researchOutcomes"));
        action.getInvalidFields().put("list-project.centerOutcomes", InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    // Cross cutting dimensions

    // Gender
    boolean genderValue = false;
    if (project.getProjecInfoPhase(action.getActualPhase()).getCrossCuttingGender() == null
      || project.getProjecInfoPhase(action.getActualPhase()).getCrossCuttingGender().booleanValue() == false) {
      genderValue = false;


      if (!(this.isValidString(project.getProjecInfoPhase(action.getActualPhase()).getDimension())
        && this.wordCount(project.getProjecInfoPhase(action.getActualPhase()).getDimension()) <= 50)) {
        action.addMessage(action.getText("project.projectInfo.dimension"));
        action.getInvalidFields().put("input-project.projectInfo.dimension", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      genderValue = true;
    }

    // Gende, Youth and N/A
    if ((genderValue == false)
      && (project.getProjecInfoPhase(action.getActualPhase()).getCrossCuttingYouth() == null
        || project.getProjecInfoPhase(action.getActualPhase()).getCrossCuttingYouth().booleanValue() == false)
      && (project.getProjecInfoPhase(action.getActualPhase()).getCrossCuttingNa() == null
        || project.getProjecInfoPhase(action.getActualPhase()).getCrossCuttingNa().booleanValue() == false)) {
      action.addMessage(action.getText("project.projectInfo.crossCuttingNa"));
      action.getInvalidFields().put("input-project.projectInfo.crossCuttingNa", InvalidFieldsMessages.EMPTYFIELD);
    }

  }

}
