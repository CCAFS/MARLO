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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ActivityDAO;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class ActivityMySQLDAO implements ActivityDAO {

  private StandardDAO dao;

  @Inject
  public ActivityMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteActivity(long activityId) {
    Activity activity = this.find(activityId);
    activity.setActive(false);
    return this.save(activity) > 0;
  }

  @Override
  public boolean existActivity(long activityID) {
    Activity activity = this.find(activityID);
    if (activity == null) {
      return false;
    }
    return true;

  }

  @Override
  public Activity find(long id) {
    return dao.find(Activity.class, id);

  }

  @Override
  public List<Activity> findAll() {
    String query = "from " + Activity.class.getName() + " where is_active=1";
    List<Activity> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(Activity activity) {
    if (activity.getId() == null) {
      dao.save(activity);
    } else {
      dao.update(activity);
    }
    if (activity.getPhase().getNext() != null) {
      this.saveActvityPhase(activity.getPhase().getNext(), activity.getProject().getId(), activity);
    }

    return activity.getId();
  }

  public void saveActvityPhase(Phase next, long projecID, Activity activity) {
    Phase phase = dao.find(Phase.class, next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<Activity> activities =
        phase.getProjectActivites().stream().filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && c.getComposeID() == activity.getComposeID()).collect(Collectors.toList());
      if (activities.isEmpty()) {
        Activity activityAdd = new Activity();
        activityAdd.setActive(true);
        activityAdd.setActiveSince(activity.getActiveSince());
        activityAdd.setActivityProgress(activity.getActivityProgress());
        activityAdd.setActivityStatus(activity.getActivityStatus());
        activityAdd.setComposeID(activity.getComposeID());
        activityAdd.setCreatedBy(activity.getCreatedBy());
        activityAdd.setDescription(activity.getDescription());
        activityAdd.setEndDate(activity.getEndDate());
        activityAdd.setModificationJustification(activity.getModificationJustification());
        activityAdd.setModifiedBy(activity.getModifiedBy());
        activityAdd.setPhase(phase);
        activityAdd.setProject(activity.getProject());
        activityAdd.setProjectPartnerPerson(activity.getProjectPartnerPerson());
        activityAdd.setStartDate(activity.getStartDate());
        activityAdd.setTitle(activity.getTitle());
        dao.save(activityAdd);
        if (activityAdd.getComposeID() == null) {
          activity.setComposeID(activity.getProject().getId() + "-" + activityAdd.getId());
          activityAdd.setComposeID(activity.getComposeID());
          dao.update(activityAdd);
        }
        if (activity.getDeliverables() != null) {
          for (DeliverableActivity deliverableActivity : activity.getDeliverables()) {
            DeliverableActivity deliverableActivityAdd = new DeliverableActivity();
            deliverableActivityAdd.setActive(false);
            deliverableActivityAdd.setActiveSince(activity.getActiveSince());
            deliverableActivityAdd.setActivity(activityAdd);
            deliverableActivityAdd.setCreatedBy(activity.getCreatedBy());
            deliverableActivityAdd.setDeliverable(deliverableActivity.getDeliverable());
            deliverableActivityAdd.setModificationJustification(activity.getModificationJustification());
            deliverableActivityAdd.setModifiedBy(activity.getModifiedBy());
            dao.save(deliverableActivityAdd);
          }
        }
      } else {
        Activity activityAdd = activities.get(0);
        activityAdd.setActive(true);
        activityAdd.setActiveSince(activity.getActiveSince());
        activityAdd.setActivityProgress(activity.getActivityProgress());
        activityAdd.setActivityStatus(activity.getActivityStatus());
        activityAdd.setComposeID(activity.getComposeID());
        activityAdd.setCreatedBy(activity.getCreatedBy());
        activityAdd.setDescription(activity.getDescription());
        activityAdd.setEndDate(activity.getEndDate());
        activityAdd.setModificationJustification(activity.getModificationJustification());
        activityAdd.setModifiedBy(activity.getModifiedBy());
        activityAdd.setPhase(phase);
        activityAdd.setProject(activity.getProject());
        activityAdd.setProjectPartnerPerson(activity.getProjectPartnerPerson());
        activityAdd.setStartDate(activity.getStartDate());
        activityAdd.setTitle(activity.getTitle());
        dao.update(activityAdd);
        if (activity.getDeliverables() == null) {
          activity.setDeliverables(new ArrayList<DeliverableActivity>());
        }
        for (DeliverableActivity deliverableActivity : activity.getDeliverables()) {
          if (activityAdd.getDeliverableActivities().stream()
            .filter(c -> c.isActive() && c.getDeliverable().equals(deliverableActivity.getDeliverable()))
            .collect(Collectors.toList()).isEmpty()) {
            DeliverableActivity deliverableActivityAdd = new DeliverableActivity();
            deliverableActivityAdd.setActive(false);
            deliverableActivityAdd.setActiveSince(activity.getActiveSince());
            deliverableActivityAdd.setActivity(activityAdd);
            deliverableActivityAdd.setCreatedBy(activity.getCreatedBy());
            deliverableActivityAdd.setDeliverable(deliverableActivity.getDeliverable());
            deliverableActivityAdd.setModificationJustification(activity.getModificationJustification());
            deliverableActivityAdd.setModifiedBy(activity.getModifiedBy());
            dao.save(deliverableActivityAdd);
          }
        }


      }

    } else {
      if (phase.getNext() != null) {
        this.saveActvityPhase(phase.getNext(), projecID, activity);
      }
    }


  }


}