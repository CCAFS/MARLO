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


import org.cgiar.ccafs.marlo.data.dao.DeliverableQualityCheckDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableQualityCheckManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class DeliverableQualityCheckManagerImpl implements DeliverableQualityCheckManager {


  private DeliverableQualityCheckDAO deliverableQualityCheckDAO;
  // Managers


  @Inject
  public DeliverableQualityCheckManagerImpl(DeliverableQualityCheckDAO deliverableQualityCheckDAO) {
    this.deliverableQualityCheckDAO = deliverableQualityCheckDAO;


  }

  @Override
  public void deleteDeliverableQualityCheck(long deliverableQualityCheckId) {

    deliverableQualityCheckDAO.deleteDeliverableQualityCheck(deliverableQualityCheckId);
  }

  @Override
  public boolean existDeliverableQualityCheck(long deliverableQualityCheckID) {

    return deliverableQualityCheckDAO.existDeliverableQualityCheck(deliverableQualityCheckID);
  }

  @Override
  public List<DeliverableQualityCheck> findAll() {

    return deliverableQualityCheckDAO.findAll();

  }

  @Override
  public DeliverableQualityCheck getDeliverableQualityCheckByDeliverable(long deliverableID, long phaseID) {
    return deliverableQualityCheckDAO.findByDeliverable(deliverableID, phaseID);
  }

  @Override
  public DeliverableQualityCheck getDeliverableQualityCheckById(long deliverableQualityCheckID) {

    return deliverableQualityCheckDAO.find(deliverableQualityCheckID);
  }

  @Override
  public DeliverableQualityCheck saveDeliverableQualityCheck(DeliverableQualityCheck deliverableQualityCheck) {

    return deliverableQualityCheckDAO.save(deliverableQualityCheck);
  }


}
