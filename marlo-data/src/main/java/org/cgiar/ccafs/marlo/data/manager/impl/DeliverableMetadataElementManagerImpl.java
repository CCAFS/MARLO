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
import org.cgiar.ccafs.marlo.data.dao.DeliverableMetadataElementDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataElementManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableMetadataElementManagerImpl implements DeliverableMetadataElementManager {


  private DeliverableMetadataElementDAO deliverableMetadataElementDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public DeliverableMetadataElementManagerImpl(DeliverableMetadataElementDAO deliverableMetadataElementDAO,
    PhaseDAO phaseDAO) {
    this.deliverableMetadataElementDAO = deliverableMetadataElementDAO;
    this.phaseDAO = phaseDAO;
  }

  private void cloneDeliverableMetadata(DeliverableMetadataElement deliverableMetadataElement,
    DeliverableMetadataElement newDeliverableMetadataElement, Phase phase) {
    newDeliverableMetadataElement.setDeliverable(deliverableMetadataElement.getDeliverable());
    newDeliverableMetadataElement.setPhase(phase);
    newDeliverableMetadataElement.setElementValue(deliverableMetadataElement.getElementValue());
    newDeliverableMetadataElement.setHide(deliverableMetadataElement.getHide());
  }

  @Override
  public void deleteDeliverableMetadataElement(long deliverableMetadataElementId) {

    deliverableMetadataElementDAO.deleteDeliverableMetadataElement(deliverableMetadataElementId);
  }

  @Override
  public boolean existDeliverableMetadataElement(long deliverableMetadataElementID) {

    return deliverableMetadataElementDAO.existDeliverableMetadataElement(deliverableMetadataElementID);
  }

  @Override
  public List<DeliverableMetadataElement> findAll() {

    return deliverableMetadataElementDAO.findAll();

  }

  @Override
  public List<DeliverableMetadataElement> findAllByPhaseAndDeliverable(Phase phase, Deliverable deliverable) {

    return deliverableMetadataElementDAO.findAllByPhaseAndDeliverable(phase.getId(), deliverable.getId());
  }

  @Override
  public DeliverableMetadataElement findMetadataElementByPhaseAndDeliverable(Phase phase, Deliverable deliverable,
    long metadataElementId) {
    return this.deliverableMetadataElementDAO.findMetadataElementByPhaseAndDeliverable(phase.getId(),
      deliverable.getId(), metadataElementId);
  }

  @Override
  public DeliverableMetadataElement getDeliverableMetadataElementById(long deliverableMetadataElementID) {

    return deliverableMetadataElementDAO.find(deliverableMetadataElementID);
  }

  @Override
  public DeliverableMetadataElement
    saveDeliverableMetadataElement(DeliverableMetadataElement deliverableMetadataElement) {
    DeliverableMetadataElement deliverableMetadataResult =
      deliverableMetadataElementDAO.save(deliverableMetadataElement);

    Phase currentPhase = phaseDAO.find(deliverableMetadataResult.getPhase().getId());
    boolean isPublication = deliverableMetadataResult.getDeliverable().getIsPublication() != null
      && deliverableMetadataResult.getDeliverable().getIsPublication();

    if (currentPhase.getDescription().equals(APConstants.REPORTING) && !isPublication) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableMetadataPhase(deliverableMetadataResult, deliverableMetadataResult.getDeliverable(),
            upkeepPhase.getId());
        }
      }
    } else {
      // UpKeep
      if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getUpkeep() && !isPublication) {
        if (currentPhase.getNext() != null) {
          this.saveDeliverableMetadataPhase(deliverableMetadataResult, deliverableMetadataResult.getDeliverable(),
            currentPhase.getNext().getId());
        }
      }
    }

    return deliverableMetadataResult;
  }

  private void saveDeliverableMetadataPhase(DeliverableMetadataElement deliverableMetadataResult,
    Deliverable deliverable, Long phaseID) {
    Phase phase = phaseDAO.find(phaseID);
    DeliverableMetadataElement deliverableMetadataElementPhase =
      deliverableMetadataElementDAO.findMetadataElementByPhaseAndDeliverable(phase.getId(),
        deliverableMetadataResult.getDeliverable().getId(), deliverableMetadataResult.getMetadataElement().getId());

    if (deliverableMetadataElementPhase != null) {
      this.cloneDeliverableMetadata(deliverableMetadataResult, deliverableMetadataElementPhase, phase);
      deliverableMetadataElementDAO.save(deliverableMetadataElementPhase);
    } else {
      DeliverableMetadataElement newDeliverableMetadataElement = new DeliverableMetadataElement();
      this.cloneDeliverableMetadata(deliverableMetadataResult, newDeliverableMetadataElement, phase);
      newDeliverableMetadataElement.setMetadataElement(deliverableMetadataResult.getMetadataElement());
      deliverableMetadataElementDAO.save(newDeliverableMetadataElement);
    }
    if (phase.getNext() != null) {
      this.saveDeliverableMetadataPhase(deliverableMetadataResult, deliverable, phase.getNext().getId());
    }

  }

}
