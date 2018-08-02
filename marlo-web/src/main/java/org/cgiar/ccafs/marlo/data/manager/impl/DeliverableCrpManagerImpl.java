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
import org.cgiar.ccafs.marlo.data.dao.DeliverableCrpDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrpManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableCrpManagerImpl implements DeliverableCrpManager {


  private DeliverableCrpDAO deliverableCrpDAO;
  private PhaseDAO phaseDAO;
  private DeliverableManager deliverableManager;
  // Managers


  @Inject
  public DeliverableCrpManagerImpl(DeliverableCrpDAO deliverableCrpDAO, PhaseDAO phaseDAO,
    DeliverableManager deliverableManager) {
    this.deliverableCrpDAO = deliverableCrpDAO;
    this.phaseDAO = phaseDAO;
    this.deliverableManager = deliverableManager;
  }

  private void cloneDeliverableCrp(DeliverableCrp deliverableCrp, DeliverableCrp newdeliverableCrp, Phase phase) {
    newdeliverableCrp.setDeliverable(deliverableCrp.getDeliverable());
    newdeliverableCrp.setPhase(phase);
    newdeliverableCrp.setCrpProgram(deliverableCrp.getCrpProgram());
    newdeliverableCrp.setGlobalUnit(deliverableCrp.getGlobalUnit());
  }

  @Override
  public void deleteDeliverableCrp(long deliverableCrpId) {
    DeliverableCrp deliverableCrp = this.getDeliverableCrpById(deliverableCrpId);
    Phase currentPhase = phaseDAO.find(deliverableCrp.getPhase().getId());
    boolean isPublication =
      deliverableCrp.getDeliverable().getIsPublication() != null && deliverableCrp.getDeliverable().getIsPublication();

    if (currentPhase.getDescription().equals(APConstants.REPORTING) && !isPublication) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteDeliverableCrpPhase(upkeepPhase, deliverableCrp);
        }
      }
    } else {
      // UpKeep
      if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getUpkeep() && !isPublication) {
        if (currentPhase.getNext() != null) {
          this.deleteDeliverableCrpPhase(currentPhase.getNext(), deliverableCrp);
        }
      }
    }

    deliverableCrpDAO.deleteDeliverableCrp(deliverableCrpId);
  }

  private void deleteDeliverableCrpPhase(Phase next, DeliverableCrp deliverableCrp) {
    Phase phase = phaseDAO.find(next.getId());
    DeliverableCrp deliverableCrpPhase = deliverableCrpDAO.findDeliverableCrpByPhaseAndDeliverable(phase,
      deliverableCrp.getDeliverable(), deliverableCrp.getGlobalUnit(), deliverableCrp.getCrpProgram());
    if (deliverableCrpPhase != null) {
      deliverableCrpDAO.deleteDeliverableCrp(deliverableCrpPhase.getId());
    }
    if (phase.getNext() != null) {
      this.deleteDeliverableCrpPhase(phase.getNext(), deliverableCrp);
    }
  }

  @Override
  public boolean existDeliverableCrp(long deliverableCrpID) {

    return deliverableCrpDAO.existDeliverableCrp(deliverableCrpID);
  }

  @Override
  public List<DeliverableCrp> findAll() {

    return deliverableCrpDAO.findAll();

  }

  @Override
  public DeliverableCrp getDeliverableCrpById(long deliverableCrpID) {

    return deliverableCrpDAO.find(deliverableCrpID);
  }

  @Override
  public DeliverableCrp saveDeliverableCrp(DeliverableCrp deliverableCrp) {
    DeliverableCrp deliverableCrpResult = deliverableCrpDAO.save(deliverableCrp);
    Phase currentPhase = phaseDAO.find(deliverableCrpResult.getPhase().getId());
    boolean isPublication =
      deliverableManager.getDeliverableById(deliverableCrpResult.getDeliverable().getId()).getIsPublication() != null
        && deliverableManager.getDeliverableById(deliverableCrpResult.getDeliverable().getId()).getIsPublication();
    if (currentPhase.getDescription().equals(APConstants.REPORTING) && !isPublication) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableCrpPhase(deliverableCrpResult, deliverableCrpResult.getDeliverable(),
            upkeepPhase.getId());
        }
      }
    } else {
      // UpKeep
      if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getUpkeep() && !isPublication) {
        if (currentPhase.getNext() != null) {
          this.saveDeliverableCrpPhase(deliverableCrpResult, deliverableCrpResult.getDeliverable(),
            currentPhase.getNext().getId());
        }
      }
    }
    return deliverableCrpResult;
  }

  private void saveDeliverableCrpPhase(DeliverableCrp deliverableCrpResult, Deliverable deliverable, Long phaseID) {
    Phase phase = phaseDAO.find(phaseID);
    DeliverableCrp deliverableCrpPhase =
      deliverableCrpDAO.findDeliverableCrpByPhaseAndDeliverable(phase, deliverableCrpResult.getDeliverable(),
        deliverableCrpResult.getGlobalUnit(), deliverableCrpResult.getCrpProgram());

    if (deliverableCrpPhase != null) {
      this.cloneDeliverableCrp(deliverableCrpResult, deliverableCrpPhase, phase);
      deliverableCrpDAO.save(deliverableCrpPhase);
    } else {
      DeliverableCrp newDeliverableCrp = new DeliverableCrp();
      this.cloneDeliverableCrp(deliverableCrpResult, newDeliverableCrp, phase);
      deliverableCrpDAO.save(newDeliverableCrp);
    }
    if (phase.getNext() != null) {
      this.saveDeliverableCrpPhase(deliverableCrpResult, deliverable, phase.getNext().getId());
    }
  }


}
