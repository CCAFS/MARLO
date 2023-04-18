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


import org.cgiar.ccafs.marlo.data.dao.DeliverableClusterParticipantDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableClusterParticipantManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableClusterParticipant;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableClusterParticipantManagerImpl implements DeliverableClusterParticipantManager {


  private DeliverableClusterParticipantDAO deliverableClusterParticipantDAO;
  // Managers


  @Inject
  public DeliverableClusterParticipantManagerImpl(DeliverableClusterParticipantDAO deliverableClusterParticipantDAO) {
    this.deliverableClusterParticipantDAO = deliverableClusterParticipantDAO;


  }

  @Override
  public void deleteDeliverableClusterParticipant(long deliverableClusterParticipantId) {

    deliverableClusterParticipantDAO.deleteDeliverableClusterParticipant(deliverableClusterParticipantId);
  }

  @Override
  public boolean existDeliverableClusterParticipant(long deliverableClusterParticipantID) {

    return deliverableClusterParticipantDAO.existDeliverableClusterParticipant(deliverableClusterParticipantID);
  }

  @Override
  public List<DeliverableClusterParticipant> findAll() {

    return deliverableClusterParticipantDAO.findAll();

  }

  @Override
  public DeliverableClusterParticipant getDeliverableClusterParticipantById(long deliverableClusterParticipantID) {

    return deliverableClusterParticipantDAO.find(deliverableClusterParticipantID);
  }

  @Override
  public DeliverableClusterParticipant saveDeliverableClusterParticipant(DeliverableClusterParticipant deliverableClusterParticipant) {

    return deliverableClusterParticipantDAO.save(deliverableClusterParticipant);
  }


}
