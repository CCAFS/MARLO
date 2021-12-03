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
import org.cgiar.ccafs.marlo.data.dao.DeliverablePublicationMetadataDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePublicationMetadataManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverablePublicationMetadataManagerImpl implements DeliverablePublicationMetadataManager {


  // Managers
  private DeliverablePublicationMetadataDAO deliverablePublicationMetadataDAO;
  private PhaseDAO phaseDAO;


  @Inject
  public DeliverablePublicationMetadataManagerImpl(DeliverablePublicationMetadataDAO deliverablePublicationMetadataDAO,
    PhaseDAO phaseDAO) {
    this.deliverablePublicationMetadataDAO = deliverablePublicationMetadataDAO;
    this.phaseDAO = phaseDAO;
  }

  private void cloneDeliverablePublicationMetadata(DeliverablePublicationMetadata deliverablePublicationMetadata,
    DeliverablePublicationMetadata newDeliverablePublicationMetadataPhase, Phase phase) {
    newDeliverablePublicationMetadataPhase.setPhase(phase);
    newDeliverablePublicationMetadataPhase.setDeliverable(deliverablePublicationMetadata.getDeliverable());
    newDeliverablePublicationMetadataPhase.setVolume(deliverablePublicationMetadata.getVolume());
    newDeliverablePublicationMetadataPhase.setIssue(deliverablePublicationMetadata.getIssue());
    newDeliverablePublicationMetadataPhase.setPages(deliverablePublicationMetadata.getPages());
    newDeliverablePublicationMetadataPhase.setJournal(deliverablePublicationMetadata.getJournal());
    newDeliverablePublicationMetadataPhase.setIsiPublication(deliverablePublicationMetadata.getIsiPublication());
    newDeliverablePublicationMetadataPhase.setNasr(deliverablePublicationMetadata.getNasr());
    newDeliverablePublicationMetadataPhase.setCoAuthor(deliverablePublicationMetadata.getCoAuthor());
    newDeliverablePublicationMetadataPhase
      .setPublicationAcknowledge(deliverablePublicationMetadata.getPublicationAcknowledge());

  }

  @Override
  public void deleteDeliverablePublicationMetadata(long deliverablePublicationMetadataId) {

    deliverablePublicationMetadataDAO.deleteDeliverablePublicationMetadata(deliverablePublicationMetadataId);
  }

  @Override
  public boolean existDeliverablePublicationMetadata(long deliverablePublicationMetadataID) {

    return deliverablePublicationMetadataDAO.existDeliverablePublicationMetadata(deliverablePublicationMetadataID);
  }

  @Override
  public List<DeliverablePublicationMetadata> findAll() {

    return deliverablePublicationMetadataDAO.findAll();

  }

  @Override
  public DeliverablePublicationMetadata findPublicationMetadataByPhaseAndDeliverable(Phase phase,
    Deliverable deliverable) {

    return deliverablePublicationMetadataDAO.findPublicationMetadataByPhaseAndDeliverable(phase, deliverable);
  }

  @Override
  public DeliverablePublicationMetadata getDeliverablePublicationMetadataById(long deliverablePublicationMetadataID) {

    return deliverablePublicationMetadataDAO.find(deliverablePublicationMetadataID);
  }

  @Override
  public DeliverablePublicationMetadata
    saveDeliverablePublicationMetadata(DeliverablePublicationMetadata deliverablePublicationMetadata) {
    DeliverablePublicationMetadata deliverablePublicationMetadataResult =
      deliverablePublicationMetadataDAO.save(deliverablePublicationMetadata);

    Phase currentPhase = phaseDAO.find(deliverablePublicationMetadataResult.getPhase().getId());
    boolean isPublication = deliverablePublicationMetadataResult.getDeliverable().getIsPublication() != null
      && deliverablePublicationMetadataResult.getDeliverable().getIsPublication();

    if (currentPhase.getDescription().equals(APConstants.REPORTING) && !isPublication) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverablePublicationMetadataPhase(deliverablePublicationMetadataResult,
            deliverablePublicationMetadata.getDeliverable(), upkeepPhase.getId());
        }
      }
    } else {
      // UpKeep
      if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getUpkeep() && !isPublication) {
        if (currentPhase.getNext() != null) {
          this.saveDeliverablePublicationMetadataPhase(deliverablePublicationMetadataResult,
            deliverablePublicationMetadata.getDeliverable(), currentPhase.getNext().getId());
        }
      }
    }

    return deliverablePublicationMetadataResult;
  }

  private void saveDeliverablePublicationMetadataPhase(DeliverablePublicationMetadata deliverablePublicationMetadata,
    Deliverable deliverable, Long phaseID) {
    Phase phase = phaseDAO.find(phaseID);
    DeliverablePublicationMetadata deliverablePublicationMetadataPhase = deliverablePublicationMetadataDAO
      .findPublicationMetadataByPhaseAndDeliverable(phase, deliverablePublicationMetadata.getDeliverable());
    if (deliverablePublicationMetadataPhase != null) {
      this.cloneDeliverablePublicationMetadata(deliverablePublicationMetadata, deliverablePublicationMetadataPhase,
        phase);
      deliverablePublicationMetadataDAO.save(deliverablePublicationMetadataPhase);
    } else {
      DeliverablePublicationMetadata newDeliverablePublicationMetadata = new DeliverablePublicationMetadata();
      this.cloneDeliverablePublicationMetadata(deliverablePublicationMetadata, newDeliverablePublicationMetadata,
        phase);
      deliverablePublicationMetadataDAO.save(newDeliverablePublicationMetadata);
    }
    if (phase.getNext() != null) {
      this.saveDeliverablePublicationMetadataPhase(deliverablePublicationMetadata, deliverable,
        phase.getNext().getId());
    }
  }


}
