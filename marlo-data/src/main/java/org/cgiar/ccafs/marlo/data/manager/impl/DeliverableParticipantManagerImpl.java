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
import org.cgiar.ccafs.marlo.data.dao.DeliverableParticipantDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableParticipantManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableParticipantManagerImpl implements DeliverableParticipantManager {


  private DeliverableParticipantDAO deliverableParticipantDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public DeliverableParticipantManagerImpl(DeliverableParticipantDAO deliverableParticipantDAO, PhaseDAO phaseDAO) {
    this.deliverableParticipantDAO = deliverableParticipantDAO;
    this.phaseDAO = phaseDAO;


  }

  private void cloneDeliverableParticipant(DeliverableParticipant deliverableParticipant,
    DeliverableParticipant newDeliverableParticipantPhase, Phase phase) {
    newDeliverableParticipantPhase.setDeliverable(deliverableParticipant.getDeliverable());
    newDeliverableParticipantPhase.setPhase(phase);
    newDeliverableParticipantPhase.setHasParticipants(deliverableParticipant.getHasParticipants());
    newDeliverableParticipantPhase.setEventActivityName(deliverableParticipant.getEventActivityName());
    newDeliverableParticipantPhase.setRepIndTypeActivity(deliverableParticipant.getRepIndTypeActivity());
    newDeliverableParticipantPhase.setAcademicDegree(deliverableParticipant.getAcademicDegree());
    newDeliverableParticipantPhase.setParticipants(deliverableParticipant.getParticipants());
    newDeliverableParticipantPhase.setEstimateParticipants(deliverableParticipant.getEstimateParticipants());
    newDeliverableParticipantPhase.setFemales(deliverableParticipant.getFemales());
    newDeliverableParticipantPhase.setEstimateFemales(deliverableParticipant.getEstimateFemales());
    newDeliverableParticipantPhase.setDontKnowFemale(deliverableParticipant.getDontKnowFemale());
    newDeliverableParticipantPhase.setRepIndTypeParticipant(deliverableParticipant.getRepIndTypeParticipant());
    newDeliverableParticipantPhase.setRepIndTrainingTerm(deliverableParticipant.getRepIndTrainingTerm());
    newDeliverableParticipantPhase.setAfrican(deliverableParticipant.getAfrican());
    newDeliverableParticipantPhase.setEstimateAfrican(deliverableParticipant.getEstimateAfrican());
    newDeliverableParticipantPhase.setAfricanPercentage(deliverableParticipant.getAfricanPercentage());
    newDeliverableParticipantPhase.setYouth(deliverableParticipant.getYouth());
    newDeliverableParticipantPhase.setEstimateYouth(deliverableParticipant.getEstimateYouth());
    newDeliverableParticipantPhase.setYouthPercentage(deliverableParticipant.getYouthPercentage());
    newDeliverableParticipantPhase.setFocus(deliverableParticipant.getFocus());
    newDeliverableParticipantPhase.setLikelyOutcomes(deliverableParticipant.getLikelyOutcomes());
  }

  @Override
  public void deleteDeliverableParticipant(long deliverableParticipantId) {

    deliverableParticipantDAO.deleteDeliverableParticipant(deliverableParticipantId);
  }

  @Override
  public boolean existDeliverableParticipant(long deliverableParticipantID) {

    return deliverableParticipantDAO.existDeliverableParticipant(deliverableParticipantID);
  }

  @Override
  public List<DeliverableParticipant> findAll() {

    return deliverableParticipantDAO.findAll();

  }

  @Override
  public List<DeliverableParticipant> findDeliverableParticipantByDeliverableAndPhase(Long deliverableID,
    Long phaseID) {
    return deliverableParticipantDAO.findDeliverableParticipantByDeliverableAndPhase(deliverableID, phaseID);
  }

  @Override
  public DeliverableParticipant getDeliverableParticipantById(long deliverableParticipantID) {

    return deliverableParticipantDAO.find(deliverableParticipantID);
  }

  @Override
  public List<DeliverableParticipant> getDeliverableParticipantByPhase(Phase phase) {
    return deliverableParticipantDAO.getDeliverableParticipantByPhase(phase);
  }

  @Override
  public List<DeliverableParticipant> getDeliverableParticipantByPhaseAndProject(Phase phase, long projectID) {
    return deliverableParticipantDAO.getDeliverableParticipantByPhaseAndProject(phase, projectID);
  }

  @Override
  public DeliverableParticipant saveDeliverableParticipant(DeliverableParticipant deliverableParticipant) {
    DeliverableParticipant deliverableParticipantResult = deliverableParticipantDAO.save(deliverableParticipant);

    Phase currentPhase = phaseDAO.find(deliverableParticipantResult.getPhase().getId());
    boolean isPublication = deliverableParticipantResult.getDeliverable().getIsPublication() != null
      && deliverableParticipantResult.getDeliverable().getIsPublication();

    if (currentPhase.getDescription().equals(APConstants.REPORTING) && !isPublication) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableParticipantPhase(deliverableParticipantResult,
            deliverableParticipantResult.getDeliverable(), upkeepPhase.getId());
        }
      }
    } else {
      // UpKeep
      if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getUpkeep() && !isPublication) {
        if (currentPhase.getNext() != null) {
          this.saveDeliverableParticipantPhase(deliverableParticipantResult,
            deliverableParticipantResult.getDeliverable(), currentPhase.getNext().getId());
        }
      }
    }

    return deliverableParticipantResult;
  }

  private void saveDeliverableParticipantPhase(DeliverableParticipant deliverableParticipantResult,
    Deliverable deliverable, Long phaseID) {
    Phase phase = phaseDAO.find(phaseID);
    DeliverableParticipant deliverableParticipantPhase = deliverableParticipantDAO
      .findDeliverableParticipantByPhaseAndDeliverable(phase, deliverableParticipantResult.getDeliverable());
    if (deliverableParticipantPhase != null) {
      this.cloneDeliverableParticipant(deliverableParticipantResult, deliverableParticipantPhase, phase);
      deliverableParticipantDAO.save(deliverableParticipantPhase);
    } else {
      DeliverableParticipant newDeliverableParticipant = new DeliverableParticipant();
      this.cloneDeliverableParticipant(deliverableParticipantResult, newDeliverableParticipant, phase);
      deliverableParticipantDAO.save(newDeliverableParticipant);
    }
    if (phase.getNext() != null) {
      this.saveDeliverableParticipantPhase(deliverableParticipantResult, deliverable, phase.getNext().getId());
    }
  }


}
