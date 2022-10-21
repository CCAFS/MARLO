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

import java.util.List;

import javax.inject.Inject;


public class TimelineManagementAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  private List<Timeline> timelineActivities;

  private final TimelineManager timelineManager;

  @Inject
  public TimelineManagementAction(APConfig config, TimelineManager timelineManager) {
    super(config);
    this.timelineManager = timelineManager;
  }

  public List<Timeline> getTimelineActivities() {
    return timelineActivities;
  }

  @Override
  public void prepare() throws Exception {

    timelineActivities = timelineManager.findAll();

    if (this.isHttpPost()) {
    }
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      return SUCCESS;
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

    }
  }

}