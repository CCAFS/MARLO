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

package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.TimelineManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Timeline;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.superadmin.TimelineManagementValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;


public class TimelineManagementAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  private List<Timeline> timelineActivities;

  private final TimelineManager timelineManager;
  private TimelineManagementValidator validator;
  private GlobalUnitManager globalUnitManager;

  @Inject
  public TimelineManagementAction(APConfig config, TimelineManager timelineManager,
    TimelineManagementValidator validator, GlobalUnitManager globalUnitManager) {
    super(config);
    this.timelineManager = timelineManager;
    this.validator = validator;
    this.globalUnitManager = globalUnitManager;
  }

  public List<Timeline> getTimelineActivities() {
    return timelineActivities;
  }

  @Override
  public void prepare() throws Exception {
    GlobalUnit globalUnit = this.getCurrentGlobalUnit();
    if (globalUnit != null && globalUnit.getId() != null) {
      timelineActivities = timelineManager.findAllByGlobalUnit(globalUnit.getId());
    } else {
      timelineActivities = Collections.emptyList();
    }

    if (this.isHttpPost()) {
      // For POST: Will bind timeline activities manually from request in save()
      timelineActivities = new ArrayList<>();
    }
  }

  /**
   * Manually bind timeline activities from HTTP request parameters.
   * This is necessary because Struts 6 cannot automatically populate lists when items are deleted.
   */
  private void bindTimelineActivitiesFromRequest() {
    timelineActivities = new ArrayList<>();
    int index = 0;
    boolean hasMore = true;

    while (hasMore) {
      String idParam = this.getRequest().getParameter("timelineActivities[" + index + "].id");

      if (idParam != null && !idParam.trim().isEmpty()) {
        try {
          Timeline timeline = new Timeline();

          // ID
          Long id = Long.parseLong(idParam);
          timeline.setId(id);

          // Description
          String description = this.getRequest().getParameter("timelineActivities[" + index + "].description");
          timeline.setDescription(description);

          // Start Date
          String startDate = this.getRequest().getParameter("timelineActivities[" + index + "].startDate");
          if (startDate != null && !startDate.trim().isEmpty()) {
            try {
              timeline.setStartDate(java.sql.Date.valueOf(startDate));
            } catch (Exception e) {
              // Silent fail for invalid dates
            }
          }

          // End Date
          String endDate = this.getRequest().getParameter("timelineActivities[" + index + "].endDate");
          if (endDate != null && !endDate.trim().isEmpty()) {
            try {
              timeline.setEndDate(java.sql.Date.valueOf(endDate));
            } catch (Exception e) {
              // Silent fail for invalid dates
            }
          }

          // Order
          String orderParam = this.getRequest().getParameter("timelineActivities[" + index + "].order");
          if (orderParam != null && !orderParam.trim().isEmpty()) {
            try {
              timeline.setOrder(Double.parseDouble(orderParam));
            } catch (NumberFormatException e) {
              // Silent fail
            }
          }

          timelineActivities.add(timeline);
          index++;
        } catch (Exception e) {
          hasMore = false;
        }
      } else {
        hasMore = false;
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {
      // Manually bind timeline activities from request parameters
      bindTimelineActivitiesFromRequest();

      if (timelineActivities != null && !timelineActivities.isEmpty()) {

        // Save/update all timeline activities from the form first
        for (Timeline activity : timelineActivities) {
          // New Activity
          Timeline timeLineSave = new Timeline();

          if (activity.getId() != null) {
            timeLineSave = timelineManager.getTimelineById(activity.getId());
          }

          boolean hasAnyRequiredField =
            (activity.getDescription() != null && !activity.getDescription().trim().isEmpty())
              || activity.getStartDate() != null || activity.getEndDate() != null;
          if (!hasAnyRequiredField) {
            continue;
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
          if (activity.getGlobalUnit() != null) {
            timeLineSave.setGlobalUnit(
              globalUnitManager.getGlobalUnitById(activity.getGlobalUnit().getId()));
          } else {
            timeLineSave.setGlobalUnit(this.getCurrentGlobalUnit());
          }
          timeLineSave.setOrder(activity.getOrder());

          timelineManager.saveTimeline(timeLineSave);
        }

        // Then delete timeline activities not present in the form
        final Set<Long> keepIds = (timelineActivities == null) ? Collections.emptySet()
          : timelineActivities.stream().map(Timeline::getId).filter(Objects::nonNull)
            .collect(Collectors.toSet());

        final GlobalUnit crp = getCurrentCrp();
        if (crp != null && crp.getId() != null) {
          final List<Timeline> existing = timelineManager.findAllByGlobalUnit(crp.getId());
          if (existing != null && !existing.isEmpty()) {
            for (Timeline timeline : existing) {
              if (timeline != null && timeline.getId() != null && !keepIds.contains(timeline.getId())) {
                timelineManager.deleteTimeline(timeline.getId());
              }
            }
          }
        }
      } else {
        try {
          GlobalUnit crp = this.getCurrentCrp();
          if (crp != null && crp.getId() != null) {
            List<Timeline> existing = timelineManager.findAllByGlobalUnit(crp.getId());
            if (existing != null && !existing.isEmpty()) {
              for (Timeline t : existing) {
                if (t != null && t.getId() != null) {
                  timelineManager.deleteTimeline(t.getId());
                }
              }
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
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