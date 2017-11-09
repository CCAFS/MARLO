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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.ActivityDAO;
import org.cgiar.ccafs.marlo.data.dao.DeliverableActivityDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.manager.ActivityManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ActivityManagerImpl implements ActivityManager {


  private ActivityDAO activityDAO;

  private PhaseDAO phaseDAO;
  private ProjectDAO projectDAO;
  private DeliverableActivityDAO deliverableActivityDAO;

  // Managers


  @Inject
  public ActivityManagerImpl(ActivityDAO activityDAO, PhaseDAO phaseDAO, ProjectDAO projectDAO,
    DeliverableActivityDAO deliverableActivityDAO) {
    this.activityDAO = activityDAO;
    this.phaseDAO = phaseDAO;
    this.projectDAO = projectDAO;
    this.deliverableActivityDAO = deliverableActivityDAO;
  }

  /**
   * clone the activity info
   * 
   * @param activityAdd activity to clone
   * @param activity base
   * @param phase the current phase
   */
  public void cloneActivity(Activity activityAdd, Activity activity, Phase phase) {
    activityAdd.setActive(true);
    activityAdd.setActiveSince(new Date());
    activityAdd.setActivityProgress(activity.getActivityProgress());
    activityAdd.setActivityStatus(activity.getActivityStatus());
    activityAdd.setComposeID(activity.getComposeID());
    activityAdd.setCreatedBy(activity.getCreatedBy());
    activityAdd.setDescription(activity.getDescription());
    activityAdd.setEndDate(activity.getEndDate());
    activityAdd.setModificationJustification(activity.getModificationJustification());
    activityAdd.setModifiedBy(activity.getCreatedBy());
    activityAdd.setPhase(phase);
    activityAdd.setProject(projectDAO.find(activity.getProject().getId()));
    activityAdd.setProjectPartnerPerson(activity.getProjectPartnerPerson());
    activityAdd.setStartDate(activity.getStartDate());
    activityAdd.setTitle(activity.getTitle());
  }

  /**
   * clone the cloneDeliverableActivity info
   * 
   * @param deliverableActivityAdd deliverableActivity to clone
   * @param deliverableActivity base deliverableActivity
   * @param activity the activity base
   * @param activityAdd the new activity base
   */
  public void cloneDeliverableActivity(DeliverableActivity deliverableActivityAdd,
    DeliverableActivity deliverableActivity, Activity activity, Activity activityAdd) {
    deliverableActivityAdd.setActive(false);
    deliverableActivityAdd.setActiveSince(activity.getActiveSince());
    deliverableActivityAdd.setActivity(activityAdd);
    deliverableActivityAdd.setCreatedBy(activity.getCreatedBy());
    deliverableActivityAdd.setDeliverable(deliverableActivity.getDeliverable());
    deliverableActivityAdd.setModificationJustification(activity.getModificationJustification());
    deliverableActivityAdd.setModifiedBy(activity.getModifiedBy());
  }

  public void deletActivityPhase(Phase next, long projecID, Activity activity) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<Activity> activities =
        phase.getProjectActivites().stream().filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && activity.getComposeID().equals(c.getComposeID())).collect(Collectors.toList());
      for (Activity activityDB : activities) {
        activityDB.setActive(false);
        activityDAO.save(activityDB);
      }
    }
    if (phase.getNext() != null) {
      this.deletActivityPhase(phase.getNext(), projecID, activity);

    }
  }

  @Override
  public void deleteActivity(long activityId) {
    activityDAO.deleteActivity(activityId);
       Activity activity = this.getActivityById(activityId);
    Phase currentPhase = phaseDAO.find(activity.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (activity.getPhase().getNext() != null) {
        this.deletActivityPhase(activity.getPhase().getNext(), activity.getProject().getId(), activity);
      }
    }
  }

  @Override
  public boolean existActivity(long activityID) {

    return activityDAO.existActivity(activityID);
  }

  @Override
  public List<Activity> findAll() {

    return activityDAO.findAll();

  }

  @Override
  public Activity getActivityById(long activityID) {

    return activityDAO.find(activityID);
  }


  @Override
  public Activity saveActivity(Activity activity) {

    Activity resultActivity = activityDAO.save(activity);
    Phase currentPhase = phaseDAO.find(activity.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (activity.getPhase().getNext() != null) {
        this.saveActvityPhase(activity.getPhase().getNext(), activity.getProject().getId(), activity);
      }
    }
    return resultActivity;
  }

  public void saveActvityPhase(Phase next, long projecID, Activity activity) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<Activity> activities =
        phase.getProjectActivites().stream().filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && c.getComposeID().equals(activity.getComposeID())).collect(Collectors.toList());
      if (activities.isEmpty()) {
        Activity activityAdd = new Activity();
        this.cloneActivity(activityAdd, activity, phase);
        activityDAO.save(activityAdd);
        if (activityAdd.getComposeID() == null) {
          activity.setComposeID(activity.getProject().getId() + "-" + activityAdd.getId());
          activityAdd.setComposeID(activity.getComposeID());
          activityDAO.save(activityAdd);
        }
        if (activity.getDeliverables() != null) {
          for (DeliverableActivity deliverableActivity : activity.getDeliverables()) {
            DeliverableActivity deliverableActivityAdd = new DeliverableActivity();
            this.cloneDeliverableActivity(deliverableActivityAdd, deliverableActivity, activity, activityAdd);
            deliverableActivityDAO.save(deliverableActivityAdd);
          }
        }
      } else {
        Activity activityAdd = activities.get(0);
        this.cloneActivity(activityAdd, activity, phase);
        activityDAO.save(activityAdd);
        if (activity.getDeliverables() == null) {
          activity.setDeliverables(new ArrayList<DeliverableActivity>());
        }
        for (DeliverableActivity deliverableActivity : activity.getDeliverables()) {
          if (activityAdd.getDeliverableActivities().stream()
            .filter(c -> c.isActive() && c.getDeliverable().equals(deliverableActivity.getDeliverable()))
            .collect(Collectors.toList()).isEmpty()) {
            DeliverableActivity deliverableActivityAdd = new DeliverableActivity();
            this.cloneDeliverableActivity(deliverableActivityAdd, deliverableActivity, activity, activityAdd);
            deliverableActivityDAO.save(deliverableActivityAdd);
          }
        }
        for (DeliverableActivity deliverableActivity : activity.getDeliverableActivities().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {
          if (activity.getDeliverables().stream()
            .filter(c -> c.getDeliverable().equals(deliverableActivity.getDeliverable())).collect(Collectors.toList())
            .isEmpty()) {
            deliverableActivity.setActive(false);
            deliverableActivityDAO.save(deliverableActivity);
          }

        }


      }

    }
    if (phase.getNext() != null) {
      this.saveActvityPhase(phase.getNext(), projecID, activity);
    }


  }
}
