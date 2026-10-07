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
import org.cgiar.ccafs.marlo.data.manager.DeliverableActivityManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableActivityManagerImpl implements DeliverableActivityManager {

  private static final Logger LOG = LoggerFactory.getLogger(DeliverableActivityManagerImpl.class);

  private DeliverableActivityDAO deliverableActivityDAO;
  private ActivityDAO ActivityDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public DeliverableActivityManagerImpl(DeliverableActivityDAO deliverableActivityDAO, PhaseDAO phaseDAO,
    ActivityDAO activityDAO) {
    this.deliverableActivityDAO = deliverableActivityDAO;
    this.phaseDAO = phaseDAO;
    this.ActivityDAO = activityDAO;
  }

  @Override
  @Transactional
  public void deleteDeliverableActivity(long deliverableActivityId) {

    DeliverableActivity deliverableActivity = this.getDeliverableActivityById(deliverableActivityId);

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (deliverableActivity.getPhase().getDescription().equals(APConstants.PLANNING)
      && deliverableActivity.getPhase().getNext() != null) {
      this.deleteDeliverableActivityPhase(deliverableActivity.getPhase().getNext(),
        deliverableActivity.getDeliverable().getId(), deliverableActivity);
    }

    if (deliverableActivity.getPhase().getDescription().equals(APConstants.REPORTING)
      && deliverableActivity.getPhase().getNext() != null
      && deliverableActivity.getPhase().getNext().getNext() != null) {
      Phase upkeepPhase = deliverableActivity.getPhase().getNext().getNext();
      if (upkeepPhase != null) {
        this.deleteDeliverableActivityPhase(upkeepPhase, deliverableActivity.getDeliverable().getId(),
          deliverableActivity);
      }
    }

    deliverableActivityDAO.deleteDeliverableActivity(deliverableActivityId);
  }

  @Transactional
  public void deleteDeliverableActivityPhase(Phase next, long deliverableID, DeliverableActivity deliverableActivity) {
    Phase phase = phaseDAO.find(next.getId());

    // The link of this phase hangs from the activity's copy in this phase; older replications attached it to the
    // activity of the saved phase instead, so both are deactivated
    Set<Long> activityIds = new LinkedHashSet<>();
    Activity activityInPhase = this.activityInPhase(deliverableActivity.getActivity(), phase);
    if (activityInPhase != null && activityInPhase.getId() != null) {
      activityIds.add(activityInPhase.getId());
    }
    if (deliverableActivity.getActivity() != null && deliverableActivity.getActivity().getId() != null) {
      activityIds.add(deliverableActivity.getActivity().getId());
    }
    for (Long activityId : activityIds) {
      for (DeliverableActivity deliverableActivityDB : deliverableActivityDAO
        .getDeliverableActivitiesByDeliverableIDActivityAndPhase(deliverableID, activityId, phase.getId())) {
        deliverableActivityDAO.deleteDeliverableActivity(deliverableActivityDB.getId());
      }
    }

    if (phase.getNext() != null) {
      this.deleteDeliverableActivityPhase(phase.getNext(), deliverableID, deliverableActivity);
    }
  }

  @Override
  public boolean existDeliverableActivity(long deliverableActivityID) {

    return deliverableActivityDAO.existDeliverableActivity(deliverableActivityID);
  }

  @Override
  public List<DeliverableActivity> findAll() {

    return deliverableActivityDAO.findAll();

  }

  @Override
  public DeliverableActivity findByDeliverableAndActivitie(long deliverableId, long activityId) {
    return deliverableActivityDAO.findByDeliverableAndActivitie(deliverableId, activityId);
  }

  @Override
  public List<DeliverableActivity> getDeliverableActivitiesByDeliverableID(long deliverableID) {
    return deliverableActivityDAO.getDeliverableActivitiesByDeliverableID(deliverableID);
  }

  @Override
  public List<DeliverableActivity> getDeliverableActivitiesByDeliverableIDActivityAndPhase(long deliverableID,
    long activityID, long phaseId) {
    return deliverableActivityDAO.getDeliverableActivitiesByDeliverableIDActivityAndPhase(deliverableID, activityID,
      phaseId);
  }

  @Override
  public List<DeliverableActivity> getDeliverableActivitiesByDeliverableIDAndPhase(long deliverableID, long phaseId) {
    return deliverableActivityDAO.getDeliverableActivitiesByDeliverableIDAndPhase(deliverableID, phaseId);
  }


  @Override
  public DeliverableActivity getDeliverableActivityById(long deliverableActivityID) {

    return deliverableActivityDAO.find(deliverableActivityID);
  }

  @Override
  public DeliverableActivity saveDeliverableActivity(DeliverableActivity deliverableActivity) {

    DeliverableActivity activity = deliverableActivityDAO.save(deliverableActivity);
    Phase phase = phaseDAO.find(activity.getPhase().getId());

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveDeliverableActivityPhase(activity.getPhase().getNext(), activity.getDeliverable().getId(),
        deliverableActivity);
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableActivityPhase(upkeepPhase, activity.getDeliverable().getId(), deliverableActivity);
        }
      }
    }
    return activity;
  }

  /**
   * The copy of the activity in the given phase, found by its composed id; the activity itself when that phase has no
   * copy or the lookup fails.
   */
  private Activity activityInPhase(Activity activity, Phase phase) {
    if (activity == null || activity.getComposeID() == null) {
      return activity;
    }
    try {
      return ActivityDAO.getActivitiesByComposedID(activity.getComposeID(), phase.getId()).stream().findFirst()
        .orElse(activity);
    } catch (Exception e) {
      LOG.warn("Could not find the copy of activity {} in phase {}", activity.getComposeID(), phase.getId(), e);
      return activity;
    }
  }

  private void saveDeliverableActivityPhase(Phase next, Long deliverableID, DeliverableActivity deliverableActivity) {
    Phase phase = phaseDAO.find(next.getId());
    /*
     * List<DeliverableActivity> deliverableActivityPrev =
     * deliverableActivityDAO.getDeliverableActivitiesByDeliverableIDAndPhase(deliverableID, phase.getId()).stream()
     * .filter(
     * r -> r.getActivity() != null && r.getActivity().getId() != null && deliverableActivity.getActivity() != null
     * && r.getActivity().getId().equals(deliverableActivity.getActivity().getId()))
     * .collect(Collectors.toList());
     */
    // The link is looked up on the activity it is created on, the activity's copy in this phase: looking it up on
    // the activity of the saved phase never found it, and every save added the link again
    Activity activityInPhase = this.activityInPhase(deliverableActivity.getActivity(), phase);
    List<DeliverableActivity> deliverableActivityPrev = activityInPhase == null || activityInPhase.getId() == null
      ? Collections.emptyList() : deliverableActivityDAO
        .getDeliverableActivitiesByDeliverableIDActivityAndPhase(deliverableID, activityInPhase.getId(), phase.getId());

    if (activityInPhase != null && deliverableActivityPrev.isEmpty()) {
      DeliverableActivity deliverableActivitysAdd = new DeliverableActivity();
      deliverableActivitysAdd.setDeliverable(deliverableActivity.getDeliverable());
      deliverableActivitysAdd.setPhase(phase);
      deliverableActivitysAdd.setActivity(activityInPhase);
      deliverableActivityDAO.save(deliverableActivitysAdd);
    }
    if (phase.getNext() != null) {
      this.saveDeliverableActivityPhase(phase.getNext(), deliverableID, deliverableActivity);
    }
  }


}
