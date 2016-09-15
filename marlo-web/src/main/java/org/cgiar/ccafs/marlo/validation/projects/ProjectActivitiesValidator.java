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
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectActivitiesValidator extends BaseValidator {

  BaseAction action;


  @Inject
  public ProjectActivitiesValidator() {
    // TODO Auto-generated constructor stub
  }

  public void validate(BaseAction action, Project project) {
    this.action = action;
    if (project.getOpenProjectActivities() != null) {
      for (Activity activity : project.getOpenProjectActivities()) {
        this.validateActivity(activity);
      }
    }

    if (project.getClosedProjectActivities() != null) {
      for (Activity activity : project.getOpenProjectActivities()) {
        this.validateActivity(activity);
      }
    }

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }
    if (action.isReportingActive()) {
      this.saveMissingFields(project, APConstants.REPORTING, action.getPlanningYear(), "partners");
    } else {
      this.saveMissingFields(project, APConstants.PLANNING, action.getPlanningYear(), "partners");
    }
  }

  public void validateActivity(Activity activity) {

    List<String> params = new ArrayList<>();
    params.add(String.valueOf(activity.getId()));

    if (!(this.isValidString(activity.getTitle()) && this.wordCount(activity.getTitle()) <= 15)) {
      this.addMessage(action.getText("activity.title", params));
    }

    if (!(this.isValidString(activity.getDescription()) && this.wordCount(activity.getDescription()) <= 150)) {
      this.addMessage(action.getText("activity.description", params));
    }

    if (activity.getStartDate() == null) {
      this.addMessage(action.getText("activity.startDate", params));
    }
    if (activity.getEndDate() == null) {
      this.addMessage(action.getText("activity.endDate", params));
    }

    if (activity.getProjectPartnerPerson() != null) {
      if (activity.getProjectPartnerPerson().getId() == -1) {
        this.addMessage(action.getText("activity.leader", params));
      }
    } else {
      this.addMessage(action.getText("activity.leader", params));
    }

    if (activity.getActivityStatus() != null) {
      if (activity.getActivityStatus() == -1) {
        this.addMessage(action.getText("activity.status", params));
      }
    } else {
      this.addMessage(action.getText("activity.status", params));
    }

    if (activity.getDeliverables() != null) {
      if (activity.getDeliverables().size() == 0) {
        this.addMessage(action.getText("activity.deliverable", params));
      }
    } else {
      this.addMessage(action.getText("activity.deliverable", params));
    }
  }
}
