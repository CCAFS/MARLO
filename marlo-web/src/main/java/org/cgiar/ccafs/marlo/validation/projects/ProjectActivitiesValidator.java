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
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import com.ibm.icu.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ProjectActivitiesValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  List<Long> activityTitleIds;

  private final Logger logger = LoggerFactory.getLogger(ProjectActivitiesValidator.class);


  @Inject
  public ProjectActivitiesValidator(GlobalUnitManager crpManager) {
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(Project project, long crpID, BaseAction action) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.ACTIVITIES.getStatus().replace("/", "_");
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


    if (project.getProjectActivities() != null) {
      int i = 0;
      activityTitleIds = new ArrayList<>();
      for (Activity activity : project.getProjectActivities()) {

        if (activity != null && activity.getActivityStatus() != null) {
          if (activity.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || (activity.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId()))
            || (activity.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId()))) {
            this.validateActivity(activity, i, "projectActivities", action);
          }

        }

        i++;
      }
    }

    // Missing Deliverables activities
    List<Deliverable> deliverablesMissingActivity = new ArrayList<>();
    List<Deliverable> prevMissingActivity = new ArrayList<>();

    try {
      prevMissingActivity = project.getCurrentDeliverables(action.getActualPhase());

      if (prevMissingActivity != null && !prevMissingActivity.isEmpty()) {
        prevMissingActivity = prevMissingActivity.stream()
          .filter(d -> d != null && d.getDeliverableInfo(action.getActualPhase()).getStatus() != null
            && d.getDeliverableInfo(action.getActualPhase()).getStatus() != 5)
          .collect(Collectors.toList());
      }
    } catch (Exception e) {
      logger.error("unable to get deliverables without activities", e);
      prevMissingActivity = new ArrayList<>();
    }


    prevMissingActivity.stream()
      .filter(
        (deliverable) -> (deliverable.getDeliverableActivities().isEmpty()
          || deliverable.getDeliverableActivities().stream().filter(
            da -> da.isActive()).collect(
              Collectors.toList())
            .isEmpty()
          || deliverable.getDeliverableActivities().stream()
            .filter(da -> da.getPhase().getId().equals(action.getActualPhase().getId()) && da.getActivity().isActive()
              && da.isActive())
            .collect(Collectors.toList()).isEmpty()))
      .forEachOrdered((_item) -> {
        deliverablesMissingActivity.add(_item);
      });


    if (deliverablesMissingActivity != null && !deliverablesMissingActivity.isEmpty()) {
      action.addMessage(action.getText("missingDeliverableActivity", "deliverable.missing.activity"));
      action.getInvalidFields().put("list-deliverable.missing.activity.alert", InvalidFieldsMessages.EMPTYFIELD);
    }


    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(project, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      action.getActualPhase().getUpkeep(), ProjectSectionStatusEnum.ACTIVITIES.getStatus(), action);

  }

  public void validateActivity(Activity activity, int index, String listName, BaseAction action) {

    List<String> params = new ArrayList<>();
    params.add(String.valueOf(activity.getId()));

    if (!action.isAiccra()) {
      if (!(this.isValidString(activity.getTitle()) && this.wordCount(activity.getTitle()) <= 30)) {
        action.addMessage(action.getText("activity.title", params));
        action.getInvalidFields().put("input-project." + listName + "[" + index + "].title",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    if (!(this.isValidString(activity.getDescription()) && this.wordCount(activity.getDescription()) <= 150)) {
      action.addMessage(action.getText("activity.description", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].description",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (activity.getStartDate() == null) {
      action.addMessage(action.getText("activity.startDate", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].startDate",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (activity.getEndDate() == null) {
      action.addMessage(action.getText("activity.endDate", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].endDate",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (activity.getProjectPartnerPerson() != null) {
      if (activity.getProjectPartnerPerson().getId().intValue() == -1) {
        action.addMessage(action.getText("activity.leader", params));
        action.getInvalidFields().put("input-project." + listName + "[" + index + "].projectPartnerPerson.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      action.addMessage(action.getText("activity.leader", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].projectPartnerPerson.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (action.isAiccra()) {
      if (activity.getActivityTitle() != null) {
        if (activity.getActivityTitle().getId().intValue() == -1) {
          action.addMessage(action.getText("activity.title", params));
          action.getInvalidFields().put("input-project." + listName + "[" + index + "].activityTitle.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      } else {
        action.addMessage(action.getText("activity.title", params));
        action.getInvalidFields().put("input-project." + listName + "[" + index + "].activityTitle.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      if (activity.getActivityTitle() != null && activity.getActivityTitle().getId() != null) {
        if (activityTitleIds != null && !activityTitleIds.isEmpty()
          && activityTitleIds.contains(activity.getActivityTitle().getId())) {
          action.addMessage(action.getText("activity.title", params));
          action.getInvalidFields().put("input-project." + listName + "[" + index + "].activityTitle.id",
            InvalidFieldsMessages.WRONGVALUE);
        } else {
          activityTitleIds.add(activity.getActivityTitle().getId());
        }
      }
    }

    if (activity.getActivityStatus() != null) {
      if (activity.getActivityStatus() == -1) {
        action.addMessage(action.getText("activity.status", params));
        action.getInvalidFields().put("input-project." + listName + "[" + index + "].status",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      if (action.isReportingActive()) {
        if (activity.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
          Calendar cal = Calendar.getInstance();
          cal.set(Calendar.YEAR, action.getCurrentCycleYear());
          cal.set(Calendar.MONTH, 11); // 11 = december
          cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve
          if (activity.getEndDate() != null && activity.getEndDate().compareTo(cal.getTime()) <= 0) {
            action.addMessage(action.getText("activity.status", params));
            action.getInvalidFields().put("input-project." + listName + "[" + index + "].activityStatus",
              InvalidFieldsMessages.EMPTYFIELD);
          }
        }
      }


      if (action.isReportingActive()) {
        if (activity.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
          || activity.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())
          || activity.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())
          || activity.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId())) {

          if (!(this.isValidString(activity.getActivityProgress())
            && this.wordCount(activity.getActivityProgress()) <= 150)) {
            action.addMessage(action.getText("activity.statusProgress", params));
            action.getInvalidFields().put("input-project." + listName + "[" + index + "].activityProgress",
              InvalidFieldsMessages.EMPTYFIELD);
          }

        }
      }


    } else {
      action.addMessage(action.getText("activity.status", params));
      action.getInvalidFields().put("input-project." + listName + "[" + index + "].activityStatus",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    /*
     * if (activity.getDeliverables() != null) {
     * if (activity.getDeliverables().size() == 0) {
     * action.addMessage(action.getText("activity.deliverable", params));
     * action.getInvalidFields().put("list-project." + listName + "[" + index + "].deliverables",
     * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Deliverables"}));
     * }
     * } else {
     * action.addMessage(action.getText("activity.deliverable", params));
     * action.getInvalidFields().put("list-project." + listName + "[" + index + "].deliverables",
     * action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Deliverables"}));
     * }
     */
  }
}
