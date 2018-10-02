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


import org.cgiar.ccafs.marlo.data.dao.DeliverableLocationDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableLocationManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableLocation;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableLocationManagerImpl implements DeliverableLocationManager {


  private DeliverableLocationDAO deliverableLocationDAO;
  private PhaseDAO phaseDAO;

  @Inject
  public DeliverableLocationManagerImpl(DeliverableLocationDAO deliverableLocationDAO, PhaseDAO phaseDAO) {
    this.deliverableLocationDAO = deliverableLocationDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteDeliverableLocation(long deliverableLocationId) {
    DeliverableLocation deliverableLocation = this.getDeliverableLocationById(deliverableLocationId);

    if (deliverableLocation.getPhase().getNext() != null) {
      this.deleteDeliverableLocationPhase(deliverableLocation.getPhase().getNext(),
        deliverableLocation.getDeliverable().getId(), deliverableLocation);
    }
    deliverableLocationDAO.deleteDeliverableLocation(deliverableLocationId);
  }

  private void deleteDeliverableLocationPhase(Phase next, Long deliverableID, DeliverableLocation deliverableLocation) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableLocation> deliverableLocations =
      phase.getDeliverableLocations().stream()
        .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID
          && c.getLocElement().getId().equals(deliverableLocation.getLocElement().getId()))
        .collect(Collectors.toList());
    for (DeliverableLocation deliverableLocationDB : deliverableLocations) {
      deliverableLocationDAO.deleteDeliverableLocation(deliverableLocationDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteDeliverableLocationPhase(phase.getNext(), deliverableID, deliverableLocation);
    }
  }

  @Override
  public boolean existDeliverableLocation(long deliverableLocationID) {

    return deliverableLocationDAO.existDeliverableLocation(deliverableLocationID);
  }

  @Override
  public List<DeliverableLocation> findAll() {

    return deliverableLocationDAO.findAll();

  }

  @Override
  public DeliverableLocation getDeliverableLocationById(long deliverableLocationID) {

    return deliverableLocationDAO.find(deliverableLocationID);
  }

  @Override
  public List<DeliverableLocation> getDeliverableLocationbyPhase(long deliverableID, long phaseID) {
    return deliverableLocationDAO.getDeliverableLocationbyPhase(deliverableID, phaseID);
  }

  @Override
  public DeliverableLocation saveDeliverableLocation(DeliverableLocation deliverableLocation) {

    DeliverableLocation location = deliverableLocationDAO.save(deliverableLocation);

    if (location.getPhase().getNext() != null) {
      this.saveDeliverableLocationPhase(location.getPhase().getNext(), location.getDeliverable().getId(),
        deliverableLocation);
    }

    return location;
  }

  private void saveDeliverableLocationPhase(Phase next, Long deliverableID, DeliverableLocation deliverableLocation) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableLocation> deliverableLocations =
      phase.getDeliverableLocations().stream()
        .filter(c -> c.getDeliverable().getId().longValue() == deliverableID
          && c.getLocElement().getId().equals(deliverableLocation.getLocElement().getId()))
        .collect(Collectors.toList());

    if (deliverableLocations.isEmpty()) {
      DeliverableLocation deliverableLocationAdd = new DeliverableLocation();
      deliverableLocationAdd.setDeliverable(deliverableLocation.getDeliverable());
      deliverableLocationAdd.setPhase(phase);
      deliverableLocationAdd.setLocElement(deliverableLocation.getLocElement());
      deliverableLocationDAO.save(deliverableLocationAdd);
    }

    if (phase.getNext() != null) {
      this.saveDeliverableLocationPhase(phase.getNext(), deliverableID, deliverableLocation);
    }

  }

}
