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
import org.cgiar.ccafs.marlo.data.dao.DeliverableDisseminationDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDisseminationManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableDisseminationManagerImpl implements DeliverableDisseminationManager {


  private DeliverableDisseminationDAO deliverableDisseminationDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public DeliverableDisseminationManagerImpl(DeliverableDisseminationDAO deliverableDisseminationDAO,
    PhaseDAO phaseDAO) {
    this.deliverableDisseminationDAO = deliverableDisseminationDAO;
    this.phaseDAO = phaseDAO;


  }

  private void cloneDeliverableDissemination(DeliverableDissemination deliverableDissemination,
    DeliverableDissemination newDeliverableDissemination, Phase next) {
    newDeliverableDissemination.setDeliverable(deliverableDissemination.getDeliverable());
    newDeliverableDissemination.setPhase(next);
    newDeliverableDissemination.setIsOpenAccess(deliverableDissemination.getIsOpenAccess());
    newDeliverableDissemination.setIntellectualProperty(deliverableDissemination.getIntellectualProperty());
    newDeliverableDissemination.setLimitedExclusivity(deliverableDissemination.getLimitedExclusivity());
    newDeliverableDissemination.setRestrictedUseAgreement(deliverableDissemination.getRestrictedUseAgreement());
    newDeliverableDissemination.setRestrictedAccessUntil(deliverableDissemination.getRestrictedAccessUntil());
    newDeliverableDissemination.setEffectiveDateRestriction(deliverableDissemination.getEffectiveDateRestriction());
    newDeliverableDissemination.setRestrictedEmbargoed(deliverableDissemination.getRestrictedEmbargoed());
    newDeliverableDissemination.setNotDisseminated(deliverableDissemination.getNotDisseminated());
    newDeliverableDissemination.setAlreadyDisseminated(deliverableDissemination.getAlreadyDisseminated());
    newDeliverableDissemination.setDisseminationChannel(deliverableDissemination.getDisseminationChannel());
    newDeliverableDissemination.setDisseminationUrl(deliverableDissemination.getDisseminationUrl());
    newDeliverableDissemination.setDisseminationChannelName(deliverableDissemination.getDisseminationChannelName());
    newDeliverableDissemination.setSynced(deliverableDissemination.getSynced());
    newDeliverableDissemination.setConfidential(deliverableDissemination.getConfidential());
    newDeliverableDissemination.setConfidentialUrl(deliverableDissemination.getConfidentialUrl());
    newDeliverableDissemination.setArticleUrl(deliverableDissemination.getArticleUrl());
    newDeliverableDissemination.setHasDOI(deliverableDissemination.getHasDOI());
  }

  @Override
  public void deleteDeliverableDissemination(long deliverableDisseminationId) {

    deliverableDisseminationDAO.deleteDeliverableDissemination(deliverableDisseminationId);
  }

  @Override
  public boolean existDeliverableDissemination(long deliverableDisseminationID) {

    return deliverableDisseminationDAO.existDeliverableDissemination(deliverableDisseminationID);
  }

  @Override
  public List<DeliverableDissemination> findAll() {

    return deliverableDisseminationDAO.findAll();

  }

  @Override
  public DeliverableDissemination findDisseminationByPhaseAndDeliverable(Phase phase, Deliverable deliverable) {
    return deliverableDisseminationDAO.findDisseminationByPhaseAndDeliverable(phase, deliverable);
  }

  @Override
  public DeliverableDissemination getDeliverableDisseminationById(long deliverableDisseminationID) {

    return deliverableDisseminationDAO.find(deliverableDisseminationID);
  }

  @Override
  public DeliverableDissemination saveDeliverableDissemination(DeliverableDissemination deliverableDissemination) {
    DeliverableDissemination deliverableDisseminationResult =
      deliverableDisseminationDAO.save(deliverableDissemination);
    Phase currentPhase = phaseDAO.find(deliverableDisseminationResult.getPhase().getId());
    boolean isPublication = deliverableDisseminationResult.getDeliverable().getIsPublication() != null
      && deliverableDisseminationResult.getDeliverable().getIsPublication();

    if (currentPhase.getDescription().equals(APConstants.REPORTING) && !isPublication) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableDisseminationPhase(deliverableDisseminationResult,
            deliverableDissemination.getDeliverable(), upkeepPhase.getId());
        }
      }
    } else {
      if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getUpkeep() && !isPublication) {
        if (currentPhase.getNext() != null) {
          this.saveDeliverableDisseminationPhase(deliverableDisseminationResult,
            deliverableDissemination.getDeliverable(), currentPhase.getNext().getId());
        }
      }
    }

    return deliverableDisseminationResult;
  }

  private void saveDeliverableDisseminationPhase(DeliverableDissemination deliverableDissemination,
    Deliverable deliverable, Long phaseID) {
    Phase phase = phaseDAO.find(phaseID);
    DeliverableDissemination deliverableDisseminationPhase = deliverableDisseminationDAO
      .findDisseminationByPhaseAndDeliverable(phase, deliverableDissemination.getDeliverable());
    if (deliverableDisseminationPhase != null) {
      this.cloneDeliverableDissemination(deliverableDissemination, deliverableDisseminationPhase, phase);
      deliverableDisseminationDAO.save(deliverableDisseminationPhase);
    } else {
      DeliverableDissemination newDeliverableDissemination = new DeliverableDissemination();
      this.cloneDeliverableDissemination(deliverableDissemination, newDeliverableDissemination, phase);
      deliverableDisseminationDAO.save(newDeliverableDissemination);
    }
    if (phase.getNext() != null) {
      this.saveDeliverableDisseminationPhase(deliverableDissemination, deliverable, phase.getNext().getId());
    }

  }


}
