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
import org.cgiar.ccafs.marlo.data.dao.ProjectPartnerPersonDAO;
import org.cgiar.ccafs.marlo.data.manager.ActivityManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableActivityManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ActivityManagerImpl implements ActivityManager {


  private ActivityDAO activityDAO;
  private PhaseDAO phaseDAO;
  private ProjectDAO projectDAO;
  private DeliverableActivityDAO deliverableActivityDAO;
  private DeliverableActivityManager deliverableActivityManager;
  private ProjectPartnerPersonDAO projectPartnerPersonDAO;


  @Inject
  public ActivityManagerImpl(ActivityDAO activityDAO, PhaseDAO phaseDAO, ProjectDAO projectDAO,
    DeliverableActivityDAO deliverableActivityDAO, ProjectPartnerPersonDAO projectPartnerPersonDAO,
    DeliverableActivityManager deliverableActivityManager) {
    this.activityDAO = activityDAO;
    this.phaseDAO = phaseDAO;
    this.projectDAO = projectDAO;
    this.deliverableActivityDAO = deliverableActivityDAO;
    this.projectPartnerPersonDAO = projectPartnerPersonDAO;
    this.deliverableActivityManager = deliverableActivityManager;
  }

  /**
   * clone the activity info
   * 
   * @param activityAdd activity to clone
   * @param activity base
   * @param phase the current phase
   */
  public void cloneActivity(Activity activityAdd, Activity activity, Phase phase) {
    activityAdd.setActivityProgress(activity.getActivityProgress());
    activityAdd.setActivityStatus(activity.getActivityStatus());
    activityAdd.setComposeID(activity.getComposeID());
    activityAdd.setDescription(activity.getDescription());
    activityAdd.setEndDate(activity.getEndDate());
    activityAdd.setPhase(phase);
    activityAdd.setProject(projectDAO.find(activity.getProject().getId()));
    activityAdd.setProjectPartnerPerson(this.getPartnerPerson(phase, activity.getProjectPartnerPerson()));
    activityAdd.setStartDate(activity.getStartDate());
    activityAdd.setTitle(activity.getTitle());
    activityAdd.setActivityTitle(activity.getActivityTitle());
  }

  /**
   * clone the cloneDeliverableActivity info
   * 
   * @param deliverableActivityAdd deliverableActivity to clone
   * @param deliverableActivity base deliverableActivity
   * @param activityAdd the new activity base
   */
  public void cloneDeliverableActivity(DeliverableActivity deliverableActivityAdd,
    DeliverableActivity deliverableActivity, Activity activityAdd, Phase phase) {
    deliverableActivityAdd.setActivity(activityAdd);
    deliverableActivityAdd.setDeliverable(deliverableActivity.getDeliverable());
    deliverableActivityAdd.setPhase(phase);
  }

  @Override
  public Activity copyActivity(Activity activity, Phase phase) {

    Activity activityAdd = new Activity();
    this.cloneActivity(activityAdd, activity, phase);
    activityAdd = activityDAO.save(activityAdd);
    if (activityAdd.getComposeID() == null) {
      activity.setComposeID(activity.getProject().getId() + "-" + activityAdd.getId());
      activityAdd.setComposeID(activity.getComposeID());

      activityAdd = activityDAO.save(activityAdd);
    }
    if (activity.getDeliverables() != null) {
      for (DeliverableActivity deliverableActivity : activity.getDeliverables()) {
        DeliverableActivity deliverableActivityAdd = new DeliverableActivity();
        this.cloneDeliverableActivity(deliverableActivityAdd, deliverableActivity, activityAdd, phase);
        deliverableActivityDAO.save(deliverableActivityAdd);
      }
    }
    return activityAdd;
  }

  public void deletActivityPhase(Phase next, long projecID, Activity activity) {
    Phase phase = phaseDAO.find(next.getId());

    List<Activity> activities =
      phase.getProjectActivites().stream().filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
        && activity.getComposeID().equals(c.getComposeID())).collect(Collectors.toList());
    if (activities != null) {
      for (Activity activityDB : activities) {
        activityDAO.deleteActivity(activityDB.getId());
      }
    }

    if (phase.getNext() != null) {
      this.deletActivityPhase(phase.getNext(), projecID, activity);

    }
  }

  @Override
  public void deleteActivity(long activityId) {

    Activity activity = this.getActivityById(activityId);
    activityDAO.deleteActivity(activityId);

    Phase currentPhase = phaseDAO.find(activity.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (activity.getPhase().getNext() != null) {
        this.deletActivityPhase(activity.getPhase().getNext(), activity.getProject().getId(), activity);
      }
    }
    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.deletActivityPhase(upkeepPhase, activity.getProject().getId(), activity);
    // }
    // }
    // }
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
  public List<Activity> getActivitiesByProject(long projectId, long phaseId) {
    return activityDAO.getActivitiesByProject(projectId, phaseId);
  }

  @Override
  public Activity getActivityById(long activityID) {

    return activityDAO.find(activityID);
  }

  private ProjectPartnerPerson getPartnerPerson(Phase phase, ProjectPartnerPerson projectPartnerPerson) {
    if (projectPartnerPerson != null) {
      projectPartnerPerson = projectPartnerPersonDAO.find(projectPartnerPerson.getId());

      List<Map<String, Object>> result = projectPartnerPersonDAO.findPartner(
        projectPartnerPerson.getProjectPartner().getInstitution().getId(), phase.getId(),
        projectPartnerPerson.getProjectPartner().getProject().getId(), projectPartnerPerson.getUser().getId());
      if (result.size() > 0) {
        Long id = Long.parseLong(result.get(0).get("id").toString());
        return projectPartnerPersonDAO.find(id);
      }
    }

    return null;

  }


  @Override
  public Activity saveActivity(Activity activity) {

    Activity resultActivity = activityDAO.save(activity);
    if (resultActivity.getComposeID() == null || resultActivity.getComposeID().isEmpty()) {
      resultActivity.setComposeID(resultActivity.getProject().getId() + "-" + resultActivity.getId());
      resultActivity = activityDAO.save(resultActivity);
    }

    Phase currentPhase = phaseDAO.find(activity.getPhase().getId());

    this.saveCurrentPhaseDeliverables(resultActivity, activity.getDeliverables(), currentPhase);

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveActvityPhase(currentPhase.getNext(), resultActivity.getProject().getId(), resultActivity);
      }
    }

    // Uncomment this line to allow reporting replication to upkeep
    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveActvityPhase(upkeepPhase, activity.getProject().getId(), activity);
        }
      }
    }
    return resultActivity;
  }

  /**
   * Save activities for next phase
   * 
   * @param next
   * @param projecID
   * @param activity
   */
  public void saveActvityPhase(Phase next, long projecID, Activity activity) {
    Phase phase = phaseDAO.find(next.getId());

    List<Activity> activities =
      phase.getProjectActivites().stream().filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
        && c.getComposeID().equals(activity.getComposeID())).collect(Collectors.toList());

    // Create new Activity
    if (activities == null || activities.isEmpty()) {
      Activity activityAdd = new Activity();
      this.cloneActivity(activityAdd, activity, phase);
      activityDAO.save(activityAdd);
      if (activityAdd.getComposeID() == null || activityAdd.getComposeID().isEmpty()) {
        activity.setComposeID(activity.getProject().getId() + "-" + activityAdd.getId());
        activityAdd.setComposeID(activity.getComposeID());
        activityDAO.save(activityAdd);
      }
      this.saveCurrentPhaseDeliverables(activityAdd, activity.getDeliverables(), phase);
    } else {
      // Update activity

      Activity activityAdd = activities.get(0);
      this.cloneActivity(activityAdd, activity, phase);
      activityDAO.save(activityAdd);
      this.saveCurrentPhaseDeliverables(activityAdd, activity.getDeliverables(), phase);
    }

    if (phase.getNext() != null) {
      this.saveActvityPhase(phase.getNext(), projecID, activity);
    }


  }

  /**
   * Save/Delete activityDeliverable of the current phase
   */
  private void saveCurrentPhaseDeliverables(Activity activityUI, List<DeliverableActivity> deliverableActivitiesUI,
    Phase currentPhase) {
    List<DeliverableActivity> deliverableActivitiesDB =
      activityUI.getDeliverableActivities().stream().filter(da -> da.isActive()).collect(Collectors.toList());
    if (deliverableActivitiesUI != null) {

      // Delete deliverableActivities distinct from DB
      for (DeliverableActivity deliverableActivity : deliverableActivitiesDB) {
        if (!deliverableActivitiesUI.contains(deliverableActivity)) {
          deliverableActivityManager.deleteDeliverableActivity(deliverableActivity.getId());
        }
      }

      // Add deliverableActivity if not exist
      for (DeliverableActivity deliverableActivity : deliverableActivitiesUI) {

        if (deliverableActivity.getId() == null || deliverableActivity.getId() == -1) {
          // New DeliverableActivity
          DeliverableActivity deliverableActivityNew = new DeliverableActivity();
          this.cloneDeliverableActivity(deliverableActivityNew, deliverableActivity, activityUI, currentPhase);
          deliverableActivityManager.saveDeliverableActivity(deliverableActivityNew);
          // This is to add DeliverableActivity to generate correct auditlog.
          activityUI.getDeliverableActivities().add(deliverableActivityNew);
        } else {
          // Check if already exists in DB, then save
          List<DeliverableActivity> deliverableActivities = currentPhase.getDeliverableActivities().stream()
            .filter(da -> da.isActive() && da.getActivity() != null
              && da.getActivity().getId().longValue() == activityUI.getId().longValue()
              && da.getDeliverable().equals(deliverableActivity.getDeliverable()))
            .collect(Collectors.toList());
          if (deliverableActivities == null || deliverableActivities.isEmpty()) {
            DeliverableActivity deliverableActivityAdd = new DeliverableActivity();
            this.cloneDeliverableActivity(deliverableActivityAdd, deliverableActivity, activityUI, currentPhase);
            deliverableActivityDAO.save(deliverableActivityAdd);
          }
        }

      }
    } else {
      // delete all from db
      if (deliverableActivitiesDB != null && !deliverableActivitiesDB.isEmpty()) {
        for (DeliverableActivity deliverableActivity : deliverableActivitiesDB) {
          deliverableActivityManager.deleteDeliverableActivity(deliverableActivity.getId());
        }
      }

    }

  }
}
