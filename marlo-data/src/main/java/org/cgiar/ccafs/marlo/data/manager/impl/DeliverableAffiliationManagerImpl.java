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


import org.cgiar.ccafs.marlo.data.dao.DeliverableAffiliationDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableAffiliationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataExternalSourcesManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliation;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataExternalSources;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableAffiliationManagerImpl implements DeliverableAffiliationManager {

  private DeliverableAffiliationDAO deliverableAffiliationDAO;

  // Managers
  private DeliverableManager deliverableManager;
  private DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager;
  private InstitutionManager institutionManager;

  @Inject
  public DeliverableAffiliationManagerImpl(DeliverableAffiliationDAO deliverableAffiliationDAO,
    DeliverableManager deliverableManager, InstitutionManager institutionManager,
    DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager) {
    this.deliverableAffiliationDAO = deliverableAffiliationDAO;
    this.deliverableManager = deliverableManager;
    this.deliverableMetadataExternalSourcesManager = deliverableMetadataExternalSourcesManager;
    this.institutionManager = institutionManager;
  }

  @Override
  public void deleteDeliverableAffiliation(long deliverableAffiliationId) {

    deliverableAffiliationDAO.deleteDeliverableAffiliation(deliverableAffiliationId);
  }

  @Override
  public boolean existDeliverableAffiliation(long deliverableAffiliationID) {

    return deliverableAffiliationDAO.existDeliverableAffiliation(deliverableAffiliationID);
  }

  @Override
  public List<DeliverableAffiliation> findAll() {

    return deliverableAffiliationDAO.findAll();

  }

  @Override
  public List<DeliverableAffiliation> findByPhaseAndDeliverable(Phase phase, Deliverable deliverable) {
    return deliverableAffiliationDAO.findByPhaseAndDeliverable(phase, deliverable);
  }

  @Override
  public DeliverableAffiliation getDeliverableAffiliationById(long deliverableAffiliationID) {

    return deliverableAffiliationDAO.find(deliverableAffiliationID);
  }

  @Override
  public void replicate(DeliverableAffiliation originalDeliverableAffiliation, Phase initialPhase) {
    Phase current = initialPhase;
    Deliverable deliverable = originalDeliverableAffiliation.getDeliverable();
    Institution institution =
      this.institutionManager.getInstitutionById(originalDeliverableAffiliation.getInstitution().getId());
    if (deliverable != null) {
      deliverable = this.deliverableManager.getDeliverableById(deliverable.getId());
    }

    while (current != null) {
      DeliverableAffiliation deliverableAffiliation = null;
      DeliverableMetadataExternalSources externalSources =
        this.deliverableMetadataExternalSourcesManager.findByPhaseAndDeliverable(current, deliverable);
      if (originalDeliverableAffiliation.getInstitution() != null
        && originalDeliverableAffiliation.getInstitution().getId() != null) {
        deliverableAffiliation = this.findByPhaseAndDeliverable(current, deliverable).stream()
          .filter(da -> da != null && da.getId() != null && da.getInstitution() != null
            && da.getInstitution().getId() != null && da.getInstitution().getId().equals(institution.getId())
            && da.getDeliverableMetadataExternalSources() != null
            && da.getDeliverableMetadataExternalSources().getId() != null
            && da.getDeliverableMetadataExternalSources().getId().equals(externalSources.getId()))
          .findFirst().orElse(null);
        if (deliverableAffiliation == null) {
          deliverableAffiliation = new DeliverableAffiliation();
        }
      }

      deliverableAffiliation.copyFields(originalDeliverableAffiliation);
      deliverableAffiliation.setPhase(current);
      deliverableAffiliation.setDeliverable(deliverable);
      deliverableAffiliation.setDeliverableMetadataExternalSources(externalSources);
      deliverableAffiliation.setInstitution(institution);

      deliverableAffiliation = this.deliverableAffiliationDAO.save(deliverableAffiliation);

      // LOG.debug(current.toString());
      current = current.getNext();
    }
  }

  @Override
  public DeliverableAffiliation saveDeliverableAffiliation(DeliverableAffiliation deliverableAffiliation) {

    return deliverableAffiliationDAO.save(deliverableAffiliation);
  }
}
