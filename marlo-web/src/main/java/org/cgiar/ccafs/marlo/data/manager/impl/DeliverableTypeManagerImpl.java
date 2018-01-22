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


import org.cgiar.ccafs.marlo.data.dao.DeliverableTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableTypeManagerImpl implements DeliverableTypeManager {


  private DeliverableTypeDAO deliverableTypeDAO;
  // Managers


  @Inject
  public DeliverableTypeManagerImpl(DeliverableTypeDAO deliverableTypeDAO) {
    this.deliverableTypeDAO = deliverableTypeDAO;


  }

  @Override
  public void deleteDeliverableType(long deliverableTypeId) {

    deliverableTypeDAO.deleteDeliverableType(deliverableTypeId);
  }

  @Override
  public boolean existDeliverableType(long deliverableTypeID) {

    return deliverableTypeDAO.existDeliverableType(deliverableTypeID);
  }

  @Override
  public List<DeliverableType> findAll() {

    return deliverableTypeDAO.findAll();

  }

  @Override
  public DeliverableType getDeliverableTypeById(long deliverableTypeID) {

    return deliverableTypeDAO.find(deliverableTypeID);
  }

  @Override
  public List<DeliverableType> getSubDeliverableType(Long deliverableId) {
    return deliverableTypeDAO.getSubDeliverableType(deliverableId);
  }

  @Override
  public DeliverableType saveDeliverableType(DeliverableType deliverableType) {

    return deliverableTypeDAO.save(deliverableType);
  }


}
