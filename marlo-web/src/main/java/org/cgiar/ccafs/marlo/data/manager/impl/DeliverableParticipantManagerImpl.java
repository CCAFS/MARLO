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


import org.cgiar.ccafs.marlo.data.dao.DeliverableParticipantDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableParticipantManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableParticipantManagerImpl implements DeliverableParticipantManager {


  private DeliverableParticipantDAO deliverableParticipantDAO;
  // Managers


  @Inject
  public DeliverableParticipantManagerImpl(DeliverableParticipantDAO deliverableParticipantDAO) {
    this.deliverableParticipantDAO = deliverableParticipantDAO;


  }

  @Override
  public void deleteDeliverableParticipant(long deliverableParticipantId) {

    deliverableParticipantDAO.deleteDeliverableParticipant(deliverableParticipantId);
  }

  @Override
  public boolean existDeliverableParticipant(long deliverableParticipantID) {

    return deliverableParticipantDAO.existDeliverableParticipant(deliverableParticipantID);
  }

  @Override
  public List<DeliverableParticipant> findAll() {

    return deliverableParticipantDAO.findAll();

  }

  @Override
  public List<DeliverableParticipant> findDeliverableParticipantByDeliverableAndPhase(Long deliverableID,
    Long phaseID) {
    return deliverableParticipantDAO.findDeliverableParticipantByDeliverableAndPhase(deliverableID, phaseID);
  }

  @Override
  public DeliverableParticipant getDeliverableParticipantById(long deliverableParticipantID) {

    return deliverableParticipantDAO.find(deliverableParticipantID);
  }

  @Override
  public DeliverableParticipant saveDeliverableParticipant(DeliverableParticipant deliverableParticipant) {

    return deliverableParticipantDAO.save(deliverableParticipant);
  }


}
