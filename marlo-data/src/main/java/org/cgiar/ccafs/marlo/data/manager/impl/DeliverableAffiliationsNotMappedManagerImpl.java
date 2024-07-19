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

import org.cgiar.ccafs.marlo.data.dao.DeliverableAffiliationsNotMappedDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableAffiliationsNotMappedManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataExternalSourcesManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliationsNotMapped;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataExternalSources;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Named
public class DeliverableAffiliationsNotMappedManagerImpl implements DeliverableAffiliationsNotMappedManager {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(DeliverableAffiliationsNotMappedManagerImpl.class);

  // DAO
  private DeliverableAffiliationsNotMappedDAO deliverableAffiliationsNotMappedDAO;

  // Manager
  private DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager;
  private InstitutionManager institutionManager;

  @Inject
  public DeliverableAffiliationsNotMappedManagerImpl(
    DeliverableAffiliationsNotMappedDAO deliverableAffiliationsNotMappedDAO,
    DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager,
    InstitutionManager institutionManager) {
    this.deliverableAffiliationsNotMappedDAO = deliverableAffiliationsNotMappedDAO;
    this.deliverableMetadataExternalSourcesManager = deliverableMetadataExternalSourcesManager;
    this.institutionManager = institutionManager;
  }

  @Override
  public void deleteDeliverableAffiliationsNotMapped(long deliverableAffiliationsNotMappedId) {
    deliverableAffiliationsNotMappedDAO.deleteDeliverableAffiliationsNotMapped(deliverableAffiliationsNotMappedId);
  }

  @Override
  public boolean existDeliverableAffiliationsNotMapped(long deliverableAffiliationsNotMappedID) {
    return deliverableAffiliationsNotMappedDAO
      .existDeliverableAffiliationsNotMapped(deliverableAffiliationsNotMappedID);
  }

  @Override
  public List<DeliverableAffiliationsNotMapped> findAll() {
    return deliverableAffiliationsNotMappedDAO.findAll();
  }

  @Override
  public List<DeliverableAffiliationsNotMapped> findBydeliverableMetadataExternalSourcesId(long externalSourceId) {
    return deliverableAffiliationsNotMappedDAO.findBydeliverableMetadataExternalSourcesId(externalSourceId);
  }

  @Override
  public DeliverableAffiliationsNotMapped
    getDeliverableAffiliationsNotMappedById(long deliverableAffiliationsNotMappedID) {
    return deliverableAffiliationsNotMappedDAO.find(deliverableAffiliationsNotMappedID);
  }

  @Override
  public void replicate(DeliverableAffiliationsNotMapped originalDeliverableAffiliationsNotMapped, Phase initialPhase) {
    Phase current = initialPhase;
    Institution institution = new Institution();
    if (originalDeliverableAffiliationsNotMapped != null
      && originalDeliverableAffiliationsNotMapped.getPossibleInstitution() != null
      && originalDeliverableAffiliationsNotMapped.getPossibleInstitution().getId() != null) {
      institution = this.institutionManager
        .getInstitutionById(originalDeliverableAffiliationsNotMapped.getPossibleInstitution().getId());
    }

    while (current != null) {
      DeliverableAffiliationsNotMapped deliverableAffiliationNotMapped = null;
      DeliverableMetadataExternalSources externalSources =
        this.deliverableMetadataExternalSourcesManager.findByPhaseAndDeliverable(current,
          originalDeliverableAffiliationsNotMapped.getDeliverableMetadataExternalSources().getDeliverable());

      if (originalDeliverableAffiliationsNotMapped.getDeliverableMetadataExternalSources() != null
        && originalDeliverableAffiliationsNotMapped.getDeliverableMetadataExternalSources().getId() != null) {
        deliverableAffiliationNotMapped = this.findAll().stream()
          .filter(dmes -> dmes != null && dmes.getId() != null && dmes.getDeliverableMetadataExternalSources() != null
            && dmes.getDeliverableMetadataExternalSources().getId() != null
            && dmes.getDeliverableMetadataExternalSources().getId().equals(externalSources.getId())
            && dmes.getPossibleInstitution() != null && dmes.getPossibleInstitution().getId() != null
            && originalDeliverableAffiliationsNotMapped != null
            && originalDeliverableAffiliationsNotMapped.getPossibleInstitution() != null
            && originalDeliverableAffiliationsNotMapped.getPossibleInstitution().getId() != null
            && dmes.getPossibleInstitution().getId()
              .equals(originalDeliverableAffiliationsNotMapped.getPossibleInstitution().getId()))
          .findFirst().orElse(null);
        if (deliverableAffiliationNotMapped == null) {
          deliverableAffiliationNotMapped = new DeliverableAffiliationsNotMapped();
        }
      }

      deliverableAffiliationNotMapped.copyFields(originalDeliverableAffiliationsNotMapped);
      deliverableAffiliationNotMapped.setDeliverableMetadataExternalSources(externalSources);
      deliverableAffiliationNotMapped.setPossibleInstitution(institution);

      deliverableAffiliationNotMapped = this.deliverableAffiliationsNotMappedDAO.save(deliverableAffiliationNotMapped);

      // LOG.debug(current.toString());
      current = current.getNext();
    }
  }

  @Override
  public DeliverableAffiliationsNotMapped
    saveDeliverableAffiliationsNotMapped(DeliverableAffiliationsNotMapped deliverableAffiliationsNotMapped) {
    return this.deliverableAffiliationsNotMappedDAO.save(deliverableAffiliationsNotMapped);
  }
}
