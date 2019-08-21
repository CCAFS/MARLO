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


import org.cgiar.ccafs.marlo.data.dao.DeliverablePartnerTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePartnerTypeManager;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnerType;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverablePartnerTypeManagerImpl implements DeliverablePartnerTypeManager {


  private DeliverablePartnerTypeDAO deliverablePartnerTypeDAO;
  // Managers


  @Inject
  public DeliverablePartnerTypeManagerImpl(DeliverablePartnerTypeDAO deliverablePartnerTypeDAO) {
    this.deliverablePartnerTypeDAO = deliverablePartnerTypeDAO;


  }

  @Override
  public void deleteDeliverablePartnerType(long deliverablePartnerTypeId) {

    deliverablePartnerTypeDAO.deleteDeliverablePartnerType(deliverablePartnerTypeId);
  }

  @Override
  public boolean existDeliverablePartnerType(long deliverablePartnerTypeID) {

    return deliverablePartnerTypeDAO.existDeliverablePartnerType(deliverablePartnerTypeID);
  }

  @Override
  public List<DeliverablePartnerType> findAll() {

    return deliverablePartnerTypeDAO.findAll();

  }

  @Override
  public DeliverablePartnerType getDeliverablePartnerTypeById(long deliverablePartnerTypeID) {

    return deliverablePartnerTypeDAO.find(deliverablePartnerTypeID);
  }

  @Override
  public DeliverablePartnerType saveDeliverablePartnerType(DeliverablePartnerType deliverablePartnerType) {

    return deliverablePartnerTypeDAO.save(deliverablePartnerType);
  }


}
