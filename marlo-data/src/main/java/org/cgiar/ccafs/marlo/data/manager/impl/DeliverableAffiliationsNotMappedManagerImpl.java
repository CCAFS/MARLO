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
import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliationsNotMapped;

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

  @Inject
  public DeliverableAffiliationsNotMappedManagerImpl(
    DeliverableAffiliationsNotMappedDAO deliverableAffiliationsNotMappedDAO) {
    this.deliverableAffiliationsNotMappedDAO = deliverableAffiliationsNotMappedDAO;
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
  public DeliverableAffiliationsNotMapped
    getDeliverableAffiliationsNotMappedById(long deliverableAffiliationsNotMappedID) {
    return deliverableAffiliationsNotMappedDAO.find(deliverableAffiliationsNotMappedID);
  }

  @Override
  public DeliverableAffiliationsNotMapped
    saveDeliverableAffiliationsNotMapped(DeliverableAffiliationsNotMapped deliverableAffiliationsNotMapped) {
    return this.deliverableAffiliationsNotMappedDAO.save(deliverableAffiliationsNotMapped);
  }
}
