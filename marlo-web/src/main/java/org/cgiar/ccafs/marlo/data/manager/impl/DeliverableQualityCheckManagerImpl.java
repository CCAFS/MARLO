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
import org.cgiar.ccafs.marlo.data.dao.DeliverableQualityCheckDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableQualityCheckManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableQualityCheckManagerImpl implements DeliverableQualityCheckManager {


  private DeliverableQualityCheckDAO deliverableQualityCheckDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public DeliverableQualityCheckManagerImpl(DeliverableQualityCheckDAO deliverableQualityCheckDAO, PhaseDAO phaseDAO) {
    this.deliverableQualityCheckDAO = deliverableQualityCheckDAO;
    this.phaseDAO = phaseDAO;
  }

  private void cloneDeliverableQualityCheck(DeliverableQualityCheck deliverableQualityCheck,
    DeliverableQualityCheck newDeliverableQualityCheck, Phase next) {
    newDeliverableQualityCheck.setDeliverable(deliverableQualityCheck.getDeliverable());
    newDeliverableQualityCheck.setPhase(next);
    newDeliverableQualityCheck.setDataDictionary(deliverableQualityCheck.getDataDictionary());
    newDeliverableQualityCheck.setDataTools(deliverableQualityCheck.getDataTools());
    newDeliverableQualityCheck.setFileAssurance(deliverableQualityCheck.getFileAssurance());
    newDeliverableQualityCheck.setFileDictionary(deliverableQualityCheck.getFileDictionary());
    newDeliverableQualityCheck.setFileTools(deliverableQualityCheck.getFileTools());
    newDeliverableQualityCheck.setLinkAssurance(deliverableQualityCheck.getLinkAssurance());
    newDeliverableQualityCheck.setLinkDictionary(deliverableQualityCheck.getLinkDictionary());
    newDeliverableQualityCheck.setLinkTools(deliverableQualityCheck.getLinkTools());
    newDeliverableQualityCheck.setQualityAssurance(deliverableQualityCheck.getQualityAssurance());
  }

  @Override
  public void deleteDeliverableQualityCheck(long deliverableQualityCheckId) {

    deliverableQualityCheckDAO.deleteDeliverableQualityCheck(deliverableQualityCheckId);
  }

  @Override
  public boolean existDeliverableQualityCheck(long deliverableQualityCheckID) {

    return deliverableQualityCheckDAO.existDeliverableQualityCheck(deliverableQualityCheckID);
  }

  @Override
  public List<DeliverableQualityCheck> findAll() {

    return deliverableQualityCheckDAO.findAll();

  }

  @Override
  public DeliverableQualityCheck getDeliverableQualityCheckByDeliverable(long deliverableID, long phaseID) {
    return deliverableQualityCheckDAO.findByDeliverable(deliverableID, phaseID);
  }

  @Override
  public DeliverableQualityCheck getDeliverableQualityCheckById(long deliverableQualityCheckID) {

    return deliverableQualityCheckDAO.find(deliverableQualityCheckID);
  }

  @Override
  public DeliverableQualityCheck saveDeliverableQualityCheck(DeliverableQualityCheck deliverableQualityCheck) {
    DeliverableQualityCheck deliverableQualityCheckResult = deliverableQualityCheckDAO.save(deliverableQualityCheck);
    Phase currentPhase = phaseDAO.find(deliverableQualityCheckResult.getPhase().getId());

    // Reporting
    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableQualityCheckPhase(deliverableQualityCheckResult, deliverableQualityCheck.getDeliverable(),
            upkeepPhase.getId());
        }
      }
    } else {
      // UpKeep
      if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getUpkeep()) {
        if (currentPhase.getNext() != null) {
          this.saveDeliverableQualityCheckPhase(deliverableQualityCheckResult, deliverableQualityCheck.getDeliverable(),
            currentPhase.getNext().getId());
        }
      }
    }

    return deliverableQualityCheckResult;
  }

  private void saveDeliverableQualityCheckPhase(DeliverableQualityCheck deliverableQualityCheck,
    Deliverable deliverable, Long phaseID) {
    Phase phase = phaseDAO.find(phaseID);
    DeliverableQualityCheck deliverableQualityCheckPhase =
      deliverableQualityCheckDAO.findQualityByPhaseAndDeliverable(phase, deliverableQualityCheck.getDeliverable());
    if (deliverableQualityCheckPhase != null) {
      this.cloneDeliverableQualityCheck(deliverableQualityCheck, deliverableQualityCheckPhase, phase);
      deliverableQualityCheckDAO.save(deliverableQualityCheckPhase);
    } else {
      DeliverableQualityCheck newDeliverableQualityCheck = new DeliverableQualityCheck();
      this.cloneDeliverableQualityCheck(deliverableQualityCheck, newDeliverableQualityCheck, phase);
      deliverableQualityCheckDAO.save(newDeliverableQualityCheck);
    }
    if (phase.getNext() != null) {
      this.saveDeliverableQualityCheckPhase(deliverableQualityCheck, deliverable, phase.getNext().getId());
    }

  }


}
