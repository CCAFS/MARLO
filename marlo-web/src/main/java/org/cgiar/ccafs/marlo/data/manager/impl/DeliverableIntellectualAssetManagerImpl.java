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
import org.cgiar.ccafs.marlo.data.dao.DeliverableIntellectualAssetDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableIntellectualAssetManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableIntellectualAssetManagerImpl implements DeliverableIntellectualAssetManager {


  private DeliverableIntellectualAssetDAO deliverableIntellectualAssetDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public DeliverableIntellectualAssetManagerImpl(DeliverableIntellectualAssetDAO deliverableIntellectualAssetDAO,
    PhaseDAO phaseDAO) {
    this.deliverableIntellectualAssetDAO = deliverableIntellectualAssetDAO;
    this.phaseDAO = phaseDAO;


  }

  private void cloneDeliverableIntellectualAsset(DeliverableIntellectualAsset deliverableDissemination,
    DeliverableIntellectualAsset newDeliverableIntellectualAsset, Phase next) {
    newDeliverableIntellectualAsset.setDeliverable(deliverableDissemination.getDeliverable());
    newDeliverableIntellectualAsset.setPhase(next);
    newDeliverableIntellectualAsset.setAdditionalInformation(deliverableDissemination.getAdditionalInformation());
    newDeliverableIntellectualAsset.setApplicant(deliverableDissemination.getApplicant());
    newDeliverableIntellectualAsset.setLink(deliverableDissemination.getLink());
    newDeliverableIntellectualAsset.setPublicCommunication(deliverableDissemination.getPublicCommunication());
    newDeliverableIntellectualAsset.setTitle(deliverableDissemination.getTitle());
    newDeliverableIntellectualAsset.setType(deliverableDissemination.getType());
    newDeliverableIntellectualAsset.setHasPatentPvp(deliverableDissemination.getHasPatentPvp());
    newDeliverableIntellectualAsset.setFillingType(deliverableDissemination.getFillingType());
    newDeliverableIntellectualAsset.setPatentStatus(deliverableDissemination.getPatentStatus());
    newDeliverableIntellectualAsset.setPatentType(deliverableDissemination.getPatentType());
    newDeliverableIntellectualAsset.setDateExpiry(deliverableDissemination.getDateExpiry());
    newDeliverableIntellectualAsset.setDateFilling(deliverableDissemination.getDateFilling());
    newDeliverableIntellectualAsset.setDateRegistration(deliverableDissemination.getDateRegistration());
    newDeliverableIntellectualAsset.setVarietyName(deliverableDissemination.getVarietyName());
    newDeliverableIntellectualAsset.setStatus(deliverableDissemination.getStatus());
    newDeliverableIntellectualAsset.setCountry(deliverableDissemination.getCountry());
    newDeliverableIntellectualAsset.setAppRegNumber(deliverableDissemination.getAppRegNumber());
    newDeliverableIntellectualAsset.setBreederCrop(deliverableDissemination.getBreederCrop());
  }

  @Override
  public void deleteDeliverableIntellectualAsset(long deliverableIntellectualAssetId) {

    deliverableIntellectualAssetDAO.deleteDeliverableIntellectualAsset(deliverableIntellectualAssetId);
  }

  @Override
  public boolean existDeliverableIntellectualAsset(long deliverableIntellectualAssetID) {

    return deliverableIntellectualAssetDAO.existDeliverableIntellectualAsset(deliverableIntellectualAssetID);
  }

  @Override
  public List<DeliverableIntellectualAsset> findAll() {

    return deliverableIntellectualAssetDAO.findAll();

  }

  @Override
  public DeliverableIntellectualAsset getDeliverableIntellectualAssetById(long deliverableIntellectualAssetID) {

    return deliverableIntellectualAssetDAO.find(deliverableIntellectualAssetID);
  }

  @Override
  public DeliverableIntellectualAsset
    saveDeliverableIntellectualAsset(DeliverableIntellectualAsset deliverableIntellectualAsset) {
    DeliverableIntellectualAsset deliverableIntellectualAssetResult =
      deliverableIntellectualAssetDAO.save(deliverableIntellectualAsset);

    Phase currentPhase = phaseDAO.find(deliverableIntellectualAssetResult.getPhase().getId());
    boolean isPublication = deliverableIntellectualAssetResult.getDeliverable().getIsPublication() != null
      && deliverableIntellectualAssetResult.getDeliverable().getIsPublication();

    if (currentPhase.getDescription().equals(APConstants.REPORTING) && !isPublication) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableIntellectualAssetPhase(deliverableIntellectualAssetResult,
            deliverableIntellectualAsset.getDeliverable(), upkeepPhase.getId());
        }
      }
    } else {
      // UpKeep
      if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getUpkeep() && !isPublication) {
        if (currentPhase.getNext() != null) {
          this.saveDeliverableIntellectualAssetPhase(deliverableIntellectualAssetResult,
            deliverableIntellectualAsset.getDeliverable(), currentPhase.getNext().getId());
        }
      }
    }

    return deliverableIntellectualAssetResult;
  }

  private void saveDeliverableIntellectualAssetPhase(DeliverableIntellectualAsset deliverableIntellectualAsset,
    Deliverable deliverable, Long phaseID) {
    Phase phase = phaseDAO.find(phaseID);
    DeliverableIntellectualAsset deliverableDisseminationPhase = deliverableIntellectualAssetDAO
      .findIntellectualAssetByPhaseAndDeliverable(phase, deliverableIntellectualAsset.getDeliverable());
    if (deliverableDisseminationPhase != null) {
      this.cloneDeliverableIntellectualAsset(deliverableIntellectualAsset, deliverableDisseminationPhase, phase);
      deliverableIntellectualAssetDAO.save(deliverableDisseminationPhase);
    } else {
      DeliverableIntellectualAsset newDeliverableIntellectualAsset = new DeliverableIntellectualAsset();
      this.cloneDeliverableIntellectualAsset(deliverableIntellectualAsset, newDeliverableIntellectualAsset, phase);
      deliverableIntellectualAssetDAO.save(newDeliverableIntellectualAsset);
    }
    if (phase.getNext() != null) {
      this.saveDeliverableIntellectualAssetPhase(deliverableIntellectualAsset, deliverable, phase.getNext().getId());
    }
  }


}
