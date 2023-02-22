/*
 * This file is part of Managing Agricultural Research for Learning&*Outcomes Platform(MARLO).
 ** MARLO is free software:you can redistribute it and/or modify
 ** it under the terms of the GNU General Public License as published by
 ** the Free Software Foundation,either version 3 of the License,or*at your option)any later version.
 ** MARLO is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY;without even the implied warranty of*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See
 * the
 ** GNU General Public License for more details.
 ** You should have received a copy of the GNU General Public License
 ** along with MARLO.If not,see<http:// www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.TimelineManager;
import org.cgiar.ccafs.marlo.data.model.Timeline;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.superadmin.TimelineManagementValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;


public class TimelineManagementAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  private List<Timeline> timelineActivities;

  private final TimelineManager timelineManager;
  private TimelineManagementValidator validator;

  @Inject
  public TimelineManagementAction(APConfig config, TimelineManager timelineManager,
    TimelineManagementValidator validator) {
    super(config);
    this.timelineManager = timelineManager;
    this.validator = validator;
  }

  public List<Timeline> getTimelineActivities() {
    return timelineActivities;
  }

  @Override
  public void prepare() throws Exception {

    timelineActivities = timelineManager.findAll();

    if (this.isHttpPost()) {
      timelineActivities.clear();
    }
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (timelineActivities != null && !timelineActivities.isEmpty()) {

        List<Long> IDs =
          timelineActivities.stream().map(Timeline::getId).filter(Objects::nonNull).collect(Collectors.toList());

        timelineManager.findAll().stream()
          .filter(activityDB -> activityDB.getId() != null && !IDs.contains(activityDB.getId())).map(Timeline::getId)
          .forEach(timelineManager::deleteTimeline);


        for (Timeline activity : timelineActivities) {

          // New Activity
          Timeline timeLineSave = new Timeline();

          if (activity.getId() != null) {
            timeLineSave = timelineManager.getTimelineById(activity.getId());
          }
          if (activity.getDescription() != null) {
            timeLineSave.setDescription(activity.getDescription());
          }
          if (activity.getStartDate() != null) {
            timeLineSave.setStartDate(activity.getStartDate());
          }
          if (activity.getEndDate() != null) {
            timeLineSave.setEndDate(activity.getEndDate());
          }
          timeLineSave.setOrder(activity.getOrder());

          timelineManager.saveTimeline(timeLineSave);

        }
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }
        } else {
          this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        return SUCCESS;
      } else {
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }

    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setTimelineActivities(List<Timeline> timelineActivities) {
    this.timelineActivities = timelineActivities;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, timelineActivities);
    }
  }

}