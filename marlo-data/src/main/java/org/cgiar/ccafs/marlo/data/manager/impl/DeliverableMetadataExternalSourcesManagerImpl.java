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

import org.cgiar.ccafs.marlo.data.dao.DeliverableMetadataExternalSourcesDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataExternalSourcesManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataExternalSources;
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
public class DeliverableMetadataExternalSourcesManagerImpl implements DeliverableMetadataExternalSourcesManager {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(DeliverableMetadataExternalSourcesManagerImpl.class);

  // DAO
  private DeliverableMetadataExternalSourcesDAO deliverableMetadataExternalSourceDAO;

  // Managers
  private DeliverableManager deliverableManager;

  @Inject
  public DeliverableMetadataExternalSourcesManagerImpl(
    DeliverableMetadataExternalSourcesDAO deliverableMetadataExternalSourceDAO, DeliverableManager deliverableManager) {
    this.deliverableMetadataExternalSourceDAO = deliverableMetadataExternalSourceDAO;
    this.deliverableManager = deliverableManager;
  }

  @Override
  public void deleteDeliverableMetadataExternalSources(long deliverableMetadataExternalSourceId) {
    deliverableMetadataExternalSourceDAO.deleteDeliverableMetadataExternalSources(deliverableMetadataExternalSourceId);
  }

  @Override
  public boolean existDeliverableMetadataExternalSources(long deliverableMetadataExternalSourceID) {
    return deliverableMetadataExternalSourceDAO
      .existDeliverableMetadataExternalSources(deliverableMetadataExternalSourceID);
  }

  @Override
  public List<DeliverableMetadataExternalSources> findAll() {
    return deliverableMetadataExternalSourceDAO.findAll();
  }

  @Override
  public DeliverableMetadataExternalSources findByPhaseAndDeliverable(Phase phase, Deliverable deliverable) {
    return this.deliverableMetadataExternalSourceDAO.findByPhaseAndDeliverable(phase, deliverable);
  }

  @Override
  public DeliverableMetadataExternalSources
    getDeliverableMetadataExternalSourcesById(long deliverableMetadataExternalSourceID) {
    return deliverableMetadataExternalSourceDAO.find(deliverableMetadataExternalSourceID);
  }

  @Override
  public void replicate(DeliverableMetadataExternalSources originalDeliverableMetadataExternalSource,
    Phase initialPhase) {
    Phase current = initialPhase;
    Deliverable deliverable = originalDeliverableMetadataExternalSource.getDeliverable();
    if (deliverable != null) {
      deliverable = this.deliverableManager.getDeliverableById(deliverable.getId());
    }

    while (current != null) {
      DeliverableMetadataExternalSources deliverableMetadataExternalSource =
        this.findByPhaseAndDeliverable(current, deliverable);
      if (deliverableMetadataExternalSource == null) {
        deliverableMetadataExternalSource = new DeliverableMetadataExternalSources();
      }

      deliverableMetadataExternalSource.copyFields(deliverableMetadataExternalSource);
      deliverableMetadataExternalSource.setPhase(current);
      deliverableMetadataExternalSource.setDeliverable(deliverable);

      deliverableMetadataExternalSource =
        this.deliverableMetadataExternalSourceDAO.save(deliverableMetadataExternalSource);

      // LOG.debug(current.toString());
      current = current.getNext();
    }
  }

  @Override
  public DeliverableMetadataExternalSources
    saveDeliverableMetadataExternalSources(DeliverableMetadataExternalSources deliverableMetadataExternalSource) {
    return this.deliverableMetadataExternalSourceDAO.save(deliverableMetadataExternalSource);
  }
}
