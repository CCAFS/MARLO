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


import org.cgiar.ccafs.marlo.data.dao.DeliverableShfrmPriorityActionDAO;
import org.cgiar.ccafs.marlo.data.dao.DeliverableShfrmSubActionDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableShfrmSubActionManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableShfrmPriorityAction;
import org.cgiar.ccafs.marlo.data.model.DeliverableShfrmSubAction;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CCAFS
 */
@Named
public class DeliverableShfrmSubActionManagerImpl implements DeliverableShfrmSubActionManager {

  private final Logger logger = LoggerFactory.getLogger(DeliverableShfrmSubActionManagerImpl.class);

  // Managers
  private DeliverableShfrmSubActionDAO deliverableShfrmSubActionDAO;
  private DeliverableShfrmPriorityActionDAO deliverableShfrmPriorityActionDAO;
  private PhaseDAO phaseDAO;

  @Inject
  public DeliverableShfrmSubActionManagerImpl(DeliverableShfrmSubActionDAO deliverableShfrmSubActionDAO,
    PhaseDAO phaseDAO, DeliverableShfrmPriorityActionDAO deliverableShfrmPriorityActionDAO) {
    this.deliverableShfrmSubActionDAO = deliverableShfrmSubActionDAO;
    this.deliverableShfrmPriorityActionDAO = deliverableShfrmPriorityActionDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteDeliverableShfrmSubAction(long deliverableShfrmSubActionId) {

    DeliverableShfrmSubAction deliverableShfrmSubAction =
      this.getDeliverableShfrmSubActionById(deliverableShfrmSubActionId);
    Phase currentPhase = phaseDAO.find(deliverableShfrmSubAction.getPhase().getId());

    if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
      this.deleteDeliverableShfrmSubActionPhase(currentPhase.getNext(), deliverableShfrmSubAction);
    }

    deliverableShfrmSubActionDAO.deleteDeliverableShfrmSubAction(deliverableShfrmSubActionId);
  }

  public void deleteDeliverableShfrmSubActionPhase(Phase next, DeliverableShfrmSubAction deliverableShfrmSubAction) {
    Phase phase = phaseDAO.find(next.getId());

    DeliverableShfrmPriorityAction deliverablePriorityActionPhase =
      this.findDeliverablePriorityActionByDeliverablePriorityActionAndPhase(deliverableShfrmSubAction, phase);
    DeliverableShfrmSubAction deliverableShfrmSubActionDelete = new DeliverableShfrmSubAction();

    if (deliverablePriorityActionPhase != null && deliverablePriorityActionPhase.getId() != null) {
      deliverableShfrmSubActionDelete =
        deliverableShfrmSubActionDAO.findByPriorityActionPhaseAndSubAction(deliverablePriorityActionPhase.getId(),
          phase.getId(), deliverableShfrmSubAction.getShfrmSubAction().getId()).get(0);
    }
    if (deliverableShfrmSubActionDelete != null) {
      deliverableShfrmSubActionDAO.deleteDeliverableShfrmSubAction(deliverableShfrmSubActionDelete.getId());
    }
    if (phase.getNext() != null) {
      this.deleteDeliverableShfrmSubActionPhase(phase.getNext(), deliverableShfrmSubAction);
    }
  }

  @Override
  public boolean existDeliverableShfrmSubAction(long deliverableShfrmSubActionID) {

    return deliverableShfrmSubActionDAO.existDeliverableShfrmSubAction(deliverableShfrmSubActionID);
  }

  @Override
  public List<DeliverableShfrmSubAction> findAll() {

    return deliverableShfrmSubActionDAO.findAll();

  }

  @Override
  public List<DeliverableShfrmSubAction> findByPriorityActionAndPhase(long priorityActionId, long phaseId) {

    return deliverableShfrmSubActionDAO.findByPriorityActionAndPhase(priorityActionId, phaseId);

  }

  @Override
  public List<DeliverableShfrmSubAction> findByPriorityActionPhaseAndSubAction(long priorityActionId, long phaseId,
    long shfrmSubActionId) {
    return deliverableShfrmSubActionDAO.findByPriorityActionPhaseAndSubAction(priorityActionId, phaseId,
      shfrmSubActionId);
  }

  public DeliverableShfrmPriorityAction findDeliverablePriorityActionByDeliverablePriorityActionAndPhase(
    DeliverableShfrmSubAction deliverableShfrmSubAction, Phase phase) {
    DeliverableShfrmPriorityAction deliverableshfrmPriorityActionPhase = null;
    try {
      if (phase != null && phase.getId() != null && deliverableShfrmSubAction != null
        && deliverableShfrmSubAction.getDeliverableShfrmPriorityAction() != null
        && deliverableShfrmSubAction.getDeliverableShfrmPriorityAction().getShfrmPriorityAction() != null
        && deliverableShfrmSubAction.getDeliverableShfrmPriorityAction().getShfrmPriorityAction().getId() != null
        && deliverableShfrmSubAction.getDeliverableShfrmPriorityAction().getDeliverable() != null
        && deliverableShfrmSubAction.getDeliverableShfrmPriorityAction().getDeliverable().getId() != null) {
        deliverableshfrmPriorityActionPhase = deliverableShfrmPriorityActionDAO.findByDeliverablePriorityActionAndPhase(
          deliverableShfrmSubAction.getDeliverableShfrmPriorityAction().getDeliverable().getId(),
          deliverableShfrmSubAction.getDeliverableShfrmPriorityAction().getShfrmPriorityAction().getId(), phase.getId())
          .get(0);
      }
    } catch (Exception e) {
      logger.error("error getting deliverableShfrmPriorityActionPhase in subActionsavePhase: " + e);
    }
    return deliverableshfrmPriorityActionPhase;
  }

  @Override
  public DeliverableShfrmSubAction getDeliverableShfrmSubActionById(long deliverableShfrmSubActionID) {

    return deliverableShfrmSubActionDAO.find(deliverableShfrmSubActionID);
  }

  @Override
  public DeliverableShfrmSubAction saveDeliverableShfrmSubAction(DeliverableShfrmSubAction deliverableShfrmSubAction) {

    DeliverableShfrmSubAction deliverableShfrmSubActionResult =
      deliverableShfrmSubActionDAO.save(deliverableShfrmSubAction);
    Phase currentPhase = phaseDAO.find(deliverableShfrmSubActionResult.getPhase().getId());

    if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {

      this.saveDeliverableShfrmSubActionPhase(currentPhase.getNext(), deliverableShfrmSubActionResult);
    }
    return deliverableShfrmSubActionResult;
  }

  public void saveDeliverableShfrmSubActionPhase(Phase next, DeliverableShfrmSubAction deliverableShfrmSubAction) {
    Phase phase = phaseDAO.find(next.getId());
    DeliverableShfrmSubAction deliverableShfrmSubActionPhase = new DeliverableShfrmSubAction();


    // get priority action
    DeliverableShfrmPriorityAction deliverableShfrmPriorityActionPhase = null;
    try {
      deliverableShfrmPriorityActionPhase =
        this.findDeliverablePriorityActionByDeliverablePriorityActionAndPhase(deliverableShfrmSubAction, phase);
    } catch (Exception e) {
      logger.error("error getting deliverable priority action " + e);
    }

    try {
      if (deliverableShfrmPriorityActionPhase != null && deliverableShfrmPriorityActionPhase.getId() != null) {
        deliverableShfrmSubActionPhase = deliverableShfrmSubActionDAO
          .findByPriorityActionPhaseAndSubAction(deliverableShfrmPriorityActionPhase.getId(), phase.getId(),
            deliverableShfrmSubAction.getShfrmSubAction().getId())
          .get(0);
      }
    } catch (Exception e) {
      logger.error("error getting deliverableShfrmSubActionPhase: " + e);
    }

    if (deliverableShfrmPriorityActionPhase != null) {
      if (deliverableShfrmSubActionPhase != null) {
        DeliverableShfrmSubAction deliverableShfrmSubActionAdd = deliverableShfrmSubActionPhase;
        deliverableShfrmSubActionAdd.setPhase(phase);
        deliverableShfrmSubActionAdd.setShfrmSubAction(deliverableShfrmSubAction.getShfrmSubAction());
        deliverableShfrmSubActionAdd.setDeliverableShfrmPriorityAction(deliverableShfrmPriorityActionPhase);
        deliverableShfrmSubActionDAO.save(deliverableShfrmSubActionAdd);
      } else {
        DeliverableShfrmSubAction deliverableShfrmSubActionAdd = new DeliverableShfrmSubAction();
        deliverableShfrmSubActionAdd.setPhase(phase);
        deliverableShfrmSubActionAdd.setShfrmSubAction(deliverableShfrmSubAction.getShfrmSubAction());
        deliverableShfrmSubActionAdd.setDeliverableShfrmPriorityAction(deliverableShfrmPriorityActionPhase);
        deliverableShfrmSubActionDAO.save(deliverableShfrmSubActionAdd);
      }
    }
    if (phase.getNext() != null) {
      this.saveDeliverableShfrmSubActionPhase(phase.getNext(), deliverableShfrmSubAction);
    }
  }

}
