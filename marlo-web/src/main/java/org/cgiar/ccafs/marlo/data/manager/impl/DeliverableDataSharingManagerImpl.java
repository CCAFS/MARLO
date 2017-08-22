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


import org.cgiar.ccafs.marlo.data.dao.DeliverableDataSharingDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDataSharingManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharing;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class DeliverableDataSharingManagerImpl implements DeliverableDataSharingManager {


  private DeliverableDataSharingDAO deliverableDataSharingDAO;
  // Managers


  @Inject
  public DeliverableDataSharingManagerImpl(DeliverableDataSharingDAO deliverableDataSharingDAO) {
    this.deliverableDataSharingDAO = deliverableDataSharingDAO;


  }

  @Override
  public void deleteDeliverableDataSharing(long deliverableDataSharingId) {

    deliverableDataSharingDAO.deleteDeliverableDataSharing(deliverableDataSharingId);
  }

  @Override
  public boolean existDeliverableDataSharing(long deliverableDataSharingID) {

    return deliverableDataSharingDAO.existDeliverableDataSharing(deliverableDataSharingID);
  }

  @Override
  public List<DeliverableDataSharing> findAll() {

    return deliverableDataSharingDAO.findAll();

  }

  @Override
  public DeliverableDataSharing getDeliverableDataSharingById(long deliverableDataSharingID) {

    return deliverableDataSharingDAO.find(deliverableDataSharingID);
  }

  @Override
  public DeliverableDataSharing saveDeliverableDataSharing(DeliverableDataSharing deliverableDataSharing) {

    return deliverableDataSharingDAO.save(deliverableDataSharing);
  }


}
