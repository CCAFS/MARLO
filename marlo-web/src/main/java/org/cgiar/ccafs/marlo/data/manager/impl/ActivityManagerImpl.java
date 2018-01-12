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
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;

import java.util.ArrayList;
import java.util.Date;
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

  private ProjectPartnerPersonDAO projectPartnerPersonDAO;
  // Managers


  @Inject
  public ActivityManagerImpl(ActivityDAO activityDAO, PhaseDAO phaseDAO, ProjectDAO projectDAO,
    DeliverableActivityDAO deliverableActivityDAO, ProjectPartnerPersonDAO projectPartnerPersonDAO) {
    this.activityDAO = activityDAO;
    this.phaseDAO = phaseDAO;
    this.projectDAO = projectDAO;
    this.deliverableActivityDAO = deliverableActivityDAO;
    this.projectPartnerPersonDAO = projectPartnerPersonDAO;
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
    activityAdd.setProjectPartnerPerson(this.getPartnerPerson(phase, activity.getProjectPartnerPerson()));
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
    DeliverableActivity deliverableActivity, Activity activity, Activity activityAdd, Phase phase) {
    deliverableActivityAdd.setActive(true);
    deliverableActivityAdd.setActiveSince(activity.getActiveSince());
    deliverableActivityAdd.setActivity(activityAdd);
    deliverableActivityAdd.setCreatedBy(activity.getCreatedBy());
    deliverableActivityAdd.setDeliverable(deliverableActivity.getDeliverable());
    deliverableActivityAdd.setModificationJustification(activity.getModificationJustification());
    deliverableActivityAdd.setModifiedBy(activity.getModifiedBy());
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
        this.cloneDeliverableActivity(deliverableActivityAdd, deliverableActivity, activity, activityAdd, phase);
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
    for (Activity activityDB : activities) {
      activityDB.setActive(false);
      activityDAO.save(activityDB);
    }

    if (phase.getNext() != null) {
      this.deletActivityPhase(phase.getNext(), projecID, activity);

    }
  }

  @Override
  public void deleteActivity(long activityId) {

    Activity activity = this.getActivityById(activityId);
    activity.setActive(false);
    activity = activityDAO.save(activity);
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
          this.cloneDeliverableActivity(deliverableActivityAdd, deliverableActivity, activity, activityAdd, phase);
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
          this.cloneDeliverableActivity(deliverableActivityAdd, deliverableActivity, activity, activityAdd, phase);
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

    if (phase.getNext() != null)

    {
      this.saveActvityPhase(phase.getNext(), projecID, activity);
    }


  }
}
