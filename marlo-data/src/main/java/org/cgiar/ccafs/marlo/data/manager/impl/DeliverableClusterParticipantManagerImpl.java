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
import org.cgiar.ccafs.marlo.data.dao.DeliverableClusterParticipantDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableClusterParticipantManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableClusterParticipant;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableClusterParticipantManagerImpl implements DeliverableClusterParticipantManager {


  private DeliverableClusterParticipantDAO deliverableClusterParticipantDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public DeliverableClusterParticipantManagerImpl(DeliverableClusterParticipantDAO deliverableClusterParticipantDAO,
    PhaseDAO phaseDAO) {
    this.deliverableClusterParticipantDAO = deliverableClusterParticipantDAO;
    this.phaseDAO = phaseDAO;
  }

  private void cloneDeliverableClusterParticipant(DeliverableClusterParticipant deliverableClusterParticipant,
    DeliverableClusterParticipant newDeliverableClusterParticipantPhase, Phase phase) {
    newDeliverableClusterParticipantPhase.setDeliverable(deliverableClusterParticipant.getDeliverable());
    newDeliverableClusterParticipantPhase.setPhase(phase);
    newDeliverableClusterParticipantPhase.setProject(deliverableClusterParticipant.getProject());
    newDeliverableClusterParticipantPhase.setFemales(deliverableClusterParticipant.getFemales());
    newDeliverableClusterParticipantPhase.setAfrican(deliverableClusterParticipant.getAfrican());
    newDeliverableClusterParticipantPhase.setYouth(deliverableClusterParticipant.getYouth());
    newDeliverableClusterParticipantPhase.setParticipants(deliverableClusterParticipant.getParticipants());
  }

  @Override
  public void deleteDeliverableClusterParticipant(long deliverableClusterParticipantId) {

    DeliverableClusterParticipant deliverableClusterParticipant =
      this.getDeliverableClusterParticipantById(deliverableClusterParticipantId);

    if (deliverableClusterParticipant.getPhase().getDescription().equals(APConstants.PLANNING)
      && deliverableClusterParticipant.getPhase().getNext() != null) {
      this.deleteDeliverableClusterParticipantPhase(deliverableClusterParticipant.getPhase().getNext(),
        deliverableClusterParticipant.getDeliverable().getId(), deliverableClusterParticipant);
    }

    if (deliverableClusterParticipant.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (deliverableClusterParticipant.getPhase().getNext() != null
        && deliverableClusterParticipant.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = deliverableClusterParticipant.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteDeliverableClusterParticipantPhase(upkeepPhase,
            deliverableClusterParticipant.getDeliverable().getId(), deliverableClusterParticipant);
        }
      }
    }


    deliverableClusterParticipantDAO.deleteDeliverableClusterParticipant(deliverableClusterParticipantId);
  }

  public void deleteDeliverableClusterParticipantPhase(Phase next, long deliverableID,
    DeliverableClusterParticipant deliverableClusterParticipant) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableClusterParticipant> deliverableClusterParticipants =
      phase.getDeliverableClusterParticipants().stream()
        .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID
          && c.getProject().getId().equals(deliverableClusterParticipant.getProject().getId()))
        .collect(Collectors.toList());
    for (DeliverableClusterParticipant projectDeliverableSharedDB : deliverableClusterParticipants) {
      deliverableClusterParticipantDAO.deleteDeliverableClusterParticipant(projectDeliverableSharedDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteDeliverableClusterParticipantPhase(phase.getNext(), deliverableID, deliverableClusterParticipant);
    }
  }

  @Override
  public boolean existDeliverableClusterParticipant(long deliverableClusterParticipantID) {

    return deliverableClusterParticipantDAO.existDeliverableClusterParticipant(deliverableClusterParticipantID);
  }

  @Override
  public List<DeliverableClusterParticipant> findAll() {

    return deliverableClusterParticipantDAO.findAll();

  }

  @Override
  public List<DeliverableClusterParticipant> getDeliverableClusterParticipantByDeliverableAndPhase(long deliverableID,
    long phaseID) {

    return deliverableClusterParticipantDAO.getDeliverableClusterParticipantByDeliverableAndPhase(deliverableID,
      phaseID);
  }

  @Override
  public List<DeliverableClusterParticipant>
    getDeliverableClusterParticipantByDeliverableProjectPhase(long deliverableID, long projectID, long phaseID) {

    return deliverableClusterParticipantDAO.getDeliverableClusterParticipantByDeliverableProjectPhase(deliverableID,
      projectID, phaseID);
  }

  @Override
  public DeliverableClusterParticipant getDeliverableClusterParticipantById(long deliverableClusterParticipantID) {

    return deliverableClusterParticipantDAO.find(deliverableClusterParticipantID);
  }

  @Override
  public List<DeliverableClusterParticipant> getDeliverableClusterParticipantByProjectAndPhase(long projectID,
    long phaseID) {

    return deliverableClusterParticipantDAO.getDeliverableClusterParticipantByProjectAndPhase(projectID, phaseID);
  }

  @Override
  public DeliverableClusterParticipant
    saveDeliverableClusterParticipant(DeliverableClusterParticipant deliverableClusterParticipant) {
    DeliverableClusterParticipant deliverableClusterParticipantResult =
      deliverableClusterParticipantDAO.save(deliverableClusterParticipant);

    Phase currentPhase = phaseDAO.find(deliverableClusterParticipantResult.getPhase().getId());
    boolean isPublication = deliverableClusterParticipantResult.getDeliverable().getIsPublication() != null
      && deliverableClusterParticipantResult.getDeliverable().getIsPublication();

    if (currentPhase.getDescription().equals(APConstants.REPORTING) && !isPublication) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableClusterParticipantPhase(deliverableClusterParticipantResult,
            deliverableClusterParticipantResult.getDeliverable(), upkeepPhase.getId());
        }
      }
    } else {
      // UpKeep
      if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getUpkeep() && !isPublication) {
        if (currentPhase.getNext() != null) {
          this.saveDeliverableClusterParticipantPhase(deliverableClusterParticipantResult,
            deliverableClusterParticipantResult.getDeliverable(), currentPhase.getNext().getId());
        }
      }
    }

    return deliverableClusterParticipantResult;
  }

  private void saveDeliverableClusterParticipantPhase(DeliverableClusterParticipant deliverableClusterParticipantResult,
    Deliverable deliverable, Long phaseID) {
    Phase phase = phaseDAO.find(phaseID);
    DeliverableClusterParticipant deliverableClusterParticipantPhase = new DeliverableClusterParticipant();
    try {
      deliverableClusterParticipantPhase =
        deliverableClusterParticipantDAO.getDeliverableClusterParticipantByDeliverableProjectPhase(
          deliverableClusterParticipantResult.getDeliverable().getId(),
          deliverableClusterParticipantResult.getProject().getId(), phase.getId()).get(0);
    } catch (Exception e) {

    }
    if (deliverableClusterParticipantPhase != null) {
      this.cloneDeliverableClusterParticipant(deliverableClusterParticipantResult, deliverableClusterParticipantPhase,
        phase);
      deliverableClusterParticipantDAO.save(deliverableClusterParticipantPhase);
    } else {
      DeliverableClusterParticipant newDeliverableClusterParticipant = new DeliverableClusterParticipant();
      this.cloneDeliverableClusterParticipant(deliverableClusterParticipantResult, newDeliverableClusterParticipant,
        phase);
      deliverableClusterParticipantDAO.save(newDeliverableClusterParticipant);
    }
    if (phase.getNext() != null) {
      this.saveDeliverableClusterParticipantPhase(deliverableClusterParticipantResult, deliverable,
        phase.getNext().getId());
    }
  }
}
