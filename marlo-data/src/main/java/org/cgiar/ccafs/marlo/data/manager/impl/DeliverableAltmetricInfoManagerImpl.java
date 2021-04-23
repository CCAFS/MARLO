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

import org.cgiar.ccafs.marlo.data.dao.DeliverableAltmetricInfoDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableAltmetricInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableAltmetricInfo;
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
public class DeliverableAltmetricInfoManagerImpl implements DeliverableAltmetricInfoManager {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(DeliverableAltmetricInfoManagerImpl.class);

  // DAO
  private DeliverableAltmetricInfoDAO deliverableAltmetricInfoDAO;

  // Managers
  private DeliverableManager deliverableManager;

  @Inject
  public DeliverableAltmetricInfoManagerImpl(DeliverableAltmetricInfoDAO deliverableAltmetricInfoDAO,
    DeliverableManager deliverableManager) {
    this.deliverableAltmetricInfoDAO = deliverableAltmetricInfoDAO;
    this.deliverableManager = deliverableManager;
  }

  @Override
  public void deleteDeliverableAltmetricInfo(long deliverableAltmetricInfoId) {
    deliverableAltmetricInfoDAO.deleteDeliverableAltmetricInfo(deliverableAltmetricInfoId);
  }

  @Override
  public boolean existDeliverableAltmetricInfo(long deliverableAltmetricInfoID) {
    return deliverableAltmetricInfoDAO.existDeliverableAltmetricInfo(deliverableAltmetricInfoID);
  }

  @Override
  public List<DeliverableAltmetricInfo> findAll() {
    return deliverableAltmetricInfoDAO.findAll();
  }

  @Override
  public DeliverableAltmetricInfo findByPhaseAndDeliverable(Phase phase, Deliverable deliverable) {
    return this.deliverableAltmetricInfoDAO.findByPhaseAndDeliverable(phase, deliverable);
  }

  @Override
  public DeliverableAltmetricInfo getDeliverableAltmetricInfoById(long deliverableAltmetricInfoID) {
    return deliverableAltmetricInfoDAO.find(deliverableAltmetricInfoID);
  }

  @Override
  public void replicate(DeliverableAltmetricInfo originalDeliverableMetadataExternalSource, Phase initialPhase) {
    Phase current = initialPhase;
    Deliverable deliverable = originalDeliverableMetadataExternalSource.getDeliverable();
    if (deliverable != null) {
      deliverable = this.deliverableManager.getDeliverableById(deliverable.getId());
    }

    while (current != null) {
      DeliverableAltmetricInfo deliverableAltmetricInfo = this.findByPhaseAndDeliverable(current, deliverable);
      if (deliverableAltmetricInfo == null) {
        deliverableAltmetricInfo = new DeliverableAltmetricInfo();
      }

      deliverableAltmetricInfo.copyFields(originalDeliverableMetadataExternalSource);
      deliverableAltmetricInfo.setPhase(current);
      deliverableAltmetricInfo.setDeliverable(deliverable);

      deliverableAltmetricInfo = this.deliverableAltmetricInfoDAO.save(deliverableAltmetricInfo);

      // LOG.debug(current.toString());
      current = current.getNext();
    }
  }

  @Override
  public DeliverableAltmetricInfo saveDeliverableAltmetricInfo(DeliverableAltmetricInfo deliverableAltmetricInfo) {
    return this.deliverableAltmetricInfoDAO.save(deliverableAltmetricInfo);
  }
}