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
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectActivitiesValidator extends BaseValidator {

  BaseAction action;


  @Inject
  private CrpManager crpManager;

  @Inject
  public ProjectActivitiesValidator() {
    // TODO Auto-generated constructor stub
  }

  private Path getAutoSaveFilePath(Project project, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.ACTIVITIES.getStatus().replace("/", "_");
    String autoSaveFile =
      project.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, Project project, boolean saving) {
    this.action = action;
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(project, action.getCrpID());

      if (path.toFile().exists()) {
        this.addMissingField("draft");
      }
    }


    if (project.getProjectActivities() != null) {
      int i = 0;
      for (Activity activity : project.getProjectActivities()) {
        if (activity != null) {
          this.validateActivity(activity, i, "projectActivities");
        }

        i++;
      }
    }

    /*
     * if (project.getClosedProjectActivities() != null) {
     * int i = 0;
     * for (Activity activity : project.getClosedProjectActivities()) {
     * this.validateActivity(activity, i, "closedProjectActivities");
     * i++;
     * }
     * }
     */

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }
    if (action.isReportingActive()) {
      this.saveMissingFields(project, APConstants.REPORTING, action.getPlanningYear(),
        ProjectSectionStatusEnum.ACTIVITIES.getStatus());
    } else {
      this.saveMissingFields(project, APConstants.PLANNING, action.getPlanningYear(),
        ProjectSectionStatusEnum.ACTIVITIES.getStatus());
    }
  }

  public void validateActivity(Activity activity, int index, String listName) {

    List<String> params = new ArrayList<>();
    params.add(String.valueOf(activity.getId()));

    if (!(this.isValidString(activity.getTitle()) && this.wordCount(activity.getTitle()) <= 15)) {
      this.addMessage(action.getText("activity.title", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].title",
        InvalidFieldsMessages.EMPTYFIELD);

    }

    if (!(this.isValidString(activity.getDescription()) && this.wordCount(activity.getDescription()) <= 150)) {
      this.addMessage(action.getText("activity.description", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].description",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (activity.getStartDate() == null) {
      this.addMessage(action.getText("activity.startDate", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].startDate",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (activity.getEndDate() == null) {
      this.addMessage(action.getText("activity.endDate", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].endDate",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (activity.getProjectPartnerPerson() != null) {
      if (activity.getProjectPartnerPerson().getId().intValue() == -1) {
        this.addMessage(action.getText("activity.leader", params));
        action.getInvalidFields().put("input-project." + listName + "[" + index + "].projectPartnerPerson.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(action.getText("activity.leader", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].projectPartnerPerson.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (activity.getActivityStatus() != null) {
      if (activity.getActivityStatus() == -1) {
        this.addMessage(action.getText("activity.status", params));
        action.getInvalidFields().put("input-project." + listName + "[" + index + "].status",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(action.getText("activity.status", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].status",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (activity.getDeliverables() != null) {
      if (activity.getDeliverables().size() == 0) {
        this.addMessage(action.getText("activity.deliverable", params));
        action.getInvalidFields().put("list-project." + listName + "[" + index + "].deliverables",
          action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Deliverables"}));
      }
    } else {
      this.addMessage(action.getText("activity.deliverable", params));
      action.getInvalidFields().put("list-project." + listName + "[" + index + "].deliverables",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Deliverables"}));
    }
  }
}
