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
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableShfrmPriorityActionManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableShfrmPriorityAction;
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
public class DeliverableShfrmPriorityActionManagerImpl implements DeliverableShfrmPriorityActionManager {

  private final Logger logger = LoggerFactory.getLogger(DeliverableShfrmPriorityActionManagerImpl.class);

  // Managers
  private DeliverableShfrmPriorityActionDAO deliverableShfrmPriorityActionDAO;
  private PhaseDAO phaseDAO;


  @Inject
  public DeliverableShfrmPriorityActionManagerImpl(DeliverableShfrmPriorityActionDAO deliverableShfrmPriorityActionDAO,
    PhaseDAO phaseDAO) {
    this.deliverableShfrmPriorityActionDAO = deliverableShfrmPriorityActionDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteDeliverableShfrmPriorityAction(long deliverableShfrmPriorityActionId) {

    deliverableShfrmPriorityActionDAO.deleteDeliverableShfrmPriorityAction(deliverableShfrmPriorityActionId);
  }

  @Override
  public boolean existDeliverableShfrmPriorityAction(long deliverableShfrmPriorityActionID) {

    return deliverableShfrmPriorityActionDAO.existDeliverableShfrmPriorityAction(deliverableShfrmPriorityActionID);
  }

  @Override
  public List<DeliverableShfrmPriorityAction> findAll() {

    return deliverableShfrmPriorityActionDAO.findAll();

  }

  @Override
  public List<DeliverableShfrmPriorityAction> findByDeliverableAndPhase(long deliverableId, long phaseId) {

    return deliverableShfrmPriorityActionDAO.findByDeliverableAndPhase(deliverableId, phaseId);

  }

  @Override
  public DeliverableShfrmPriorityAction getDeliverableShfrmPriorityActionById(long deliverableShfrmPriorityActionID) {

    return deliverableShfrmPriorityActionDAO.find(deliverableShfrmPriorityActionID);
  }

  @Override
  public DeliverableShfrmPriorityAction
    saveDeliverableShfrmPriorityAction(DeliverableShfrmPriorityAction deliverableShfrmPriorityAction) {

    DeliverableShfrmPriorityAction deliverableShfrmPriorityActionResult =
      deliverableShfrmPriorityActionDAO.save(deliverableShfrmPriorityAction);
    Phase currentPhase = phaseDAO.find(deliverableShfrmPriorityActionResult.getPhase().getId());

    if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {

      this.saveDeliverableShfrmPriorityActionPhase(currentPhase.getNext(),
        deliverableShfrmPriorityActionResult.getDeliverable().getId(), deliverableShfrmPriorityActionResult);
    }
    return deliverableShfrmPriorityActionResult;
  }

  public void saveDeliverableShfrmPriorityActionPhase(Phase next, long deliverableId,
    DeliverableShfrmPriorityAction deliverableShfrmPriorityAction) {
    Phase phase = phaseDAO.find(next.getId());

    DeliverableShfrmPriorityAction deliverableShfrmPriorityActionAdd = new DeliverableShfrmPriorityAction();
    deliverableShfrmPriorityActionAdd.setDeliverable(deliverableShfrmPriorityAction.getDeliverable());
    deliverableShfrmPriorityActionAdd.setPhase(phase);
    deliverableShfrmPriorityActionAdd.setShfrmPriorityAction(deliverableShfrmPriorityAction.getShfrmPriorityAction());
    deliverableShfrmPriorityActionDAO.save(deliverableShfrmPriorityActionAdd);

    if (phase.getNext() != null) {
      this.saveDeliverableShfrmPriorityActionPhase(phase.getNext(), deliverableId, deliverableShfrmPriorityAction);
    }
  }

}
