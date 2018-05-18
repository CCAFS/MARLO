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


import org.cgiar.ccafs.marlo.data.dao.DeliverableParticipantLocationDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableParticipantLocationManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipantLocation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableParticipantLocationManagerImpl implements DeliverableParticipantLocationManager {


  private DeliverableParticipantLocationDAO deliverableParticipantLocationDAO;
  // Managers


  @Inject
  public DeliverableParticipantLocationManagerImpl(
    DeliverableParticipantLocationDAO deliverableParticipantLocationDAO) {
    this.deliverableParticipantLocationDAO = deliverableParticipantLocationDAO;


  }

  @Override
  public void deleteDeliverableParticipantLocation(long deliverableParticipantLocationId) {

    deliverableParticipantLocationDAO.deleteDeliverableParticipantLocation(deliverableParticipantLocationId);
  }

  @Override
  public boolean existDeliverableParticipantLocation(long deliverableParticipantLocationID) {

    return deliverableParticipantLocationDAO.existDeliverableParticipantLocation(deliverableParticipantLocationID);
  }

  @Override
  public List<DeliverableParticipantLocation> findAll() {

    return deliverableParticipantLocationDAO.findAll();

  }

  @Override
  public List<DeliverableParticipantLocation> findParticipantLocationsByParticipant(long deliverableParticipantId) {
    return deliverableParticipantLocationDAO.findParticipantLocationsByParticipant(deliverableParticipantId);
  }

  @Override
  public DeliverableParticipantLocation getDeliverableParticipantLocationById(long deliverableParticipantLocationID) {

    return deliverableParticipantLocationDAO.find(deliverableParticipantLocationID);
  }

  @Override
  public DeliverableParticipantLocation
    saveDeliverableParticipantLocation(DeliverableParticipantLocation deliverableParticipantLocation) {

    return deliverableParticipantLocationDAO.save(deliverableParticipantLocation);
  }

}
