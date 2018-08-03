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


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.DeliverableParticipantDAO;
import org.cgiar.ccafs.marlo.data.dao.DeliverableParticipantLocationDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableParticipantLocationManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipantLocation;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableParticipantLocationManagerImpl implements DeliverableParticipantLocationManager {


  private DeliverableParticipantLocationDAO deliverableParticipantLocationDAO;
  private DeliverableParticipantDAO deliverableParticipantDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public DeliverableParticipantLocationManagerImpl(DeliverableParticipantLocationDAO deliverableParticipantLocationDAO,
    PhaseDAO phaseDAO, DeliverableParticipantDAO deliverableParticipantDAO) {
    this.deliverableParticipantLocationDAO = deliverableParticipantLocationDAO;
    this.deliverableParticipantDAO = deliverableParticipantDAO;
    this.phaseDAO = phaseDAO;
  }

  private void cloneDeliverableParticipantLocation(DeliverableParticipantLocation deliverableParticipantLocationAdd,
    DeliverableParticipantLocation deliverableParticipantLocation, DeliverableParticipant deliverableParticipantPhase) {
    deliverableParticipantLocationAdd.setLocElement(deliverableParticipantLocation.getLocElement());
    deliverableParticipantLocationAdd.setDeliverableParticipant(deliverableParticipantPhase);

  }

  @Override
  public void deleteDeliverableParticipantLocation(long deliverableParticipantLocationId) {
    DeliverableParticipantLocation deliverableParticipantLocation =
      this.getDeliverableParticipantLocationById(deliverableParticipantLocationId);

    Phase currentPhase = phaseDAO.find(deliverableParticipantLocation.getDeliverableParticipant().getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteDeliverableParticipantLocationPhase(currentPhase.getNext(),
          deliverableParticipantLocation.getDeliverableParticipant().getDeliverable(),
          deliverableParticipantLocation.getLocElement());
      }
    }
    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteDeliverableParticipantLocationPhase(upkeepPhase,
            deliverableParticipantLocation.getDeliverableParticipant().getDeliverable(),
            deliverableParticipantLocation.getLocElement());
        }
      }
    }

    deliverableParticipantLocationDAO.deleteDeliverableParticipantLocation(deliverableParticipantLocationId);
  }

  public void deleteDeliverableParticipantLocationPhase(Phase next, Deliverable deliverable, LocElement locElement) {
    Phase phase = phaseDAO.find(next.getId());

    // Find participant in next phase
    DeliverableParticipant deliverableParticipantPhase =
      deliverableParticipantDAO.findDeliverableParticipantByPhaseAndDeliverable(phase, deliverable);
    if (deliverableParticipantPhase != null) {
      // Find participantLocation in next phase
      List<DeliverableParticipantLocation> deliverableParticipantLocations =
        deliverableParticipantPhase.getDeliverableParticipantLocations().stream()
          .filter(pl -> pl.isActive() && pl.getLocElement() != null && pl.getLocElement().equals(locElement))
          .collect(Collectors.toList());

      // Delete next phase locations found
      if (deliverableParticipantLocations != null && !deliverableParticipantLocations.isEmpty()) {
        for (DeliverableParticipantLocation deliverableParticipantLocation : deliverableParticipantLocations) {
          if (deliverableParticipantLocation != null && deliverableParticipantLocation.getId() != null) {
            deliverableParticipantLocationDAO
              .deleteDeliverableParticipantLocation(deliverableParticipantLocation.getId());
          }
        }
      }
    }


    if (phase.getNext() != null) {
      this.deleteDeliverableParticipantLocationPhase(phase.getNext(), deliverable, locElement);
    }
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

    DeliverableParticipantLocation deliverableParticipantLocationResult =
      deliverableParticipantLocationDAO.save(deliverableParticipantLocation);
    Phase currentPhase =
      phaseDAO.find(deliverableParticipantLocationResult.getDeliverableParticipant().getPhase().getId());
    boolean isPublication =
      deliverableParticipantLocationResult.getDeliverableParticipant().getDeliverable().getIsPublication() != null
        && deliverableParticipantLocationResult.getDeliverableParticipant().getDeliverable().getIsPublication();

    if (currentPhase.getDescription().equals(APConstants.REPORTING) && !isPublication) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableParticipantLocationPhase(deliverableParticipantLocationResult, upkeepPhase.getId());
        }
      }
    } else {
      // UpKeep
      if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getUpkeep() && !isPublication) {
        if (currentPhase.getNext() != null) {
          this.saveDeliverableParticipantLocationPhase(deliverableParticipantLocationResult,
            currentPhase.getNext().getId());
        }
      }
    }

    return deliverableParticipantLocationResult;
  }

  private void saveDeliverableParticipantLocationPhase(DeliverableParticipantLocation deliverableParticipantLocation,
    Long next) {
    Phase phase = phaseDAO.find(next);

    // Find participant in next phase
    DeliverableParticipant deliverableParticipantPhase =
      deliverableParticipantDAO.findDeliverableParticipantByPhaseAndDeliverable(phase,
        deliverableParticipantLocation.getDeliverableParticipant().getDeliverable());

    if (deliverableParticipantPhase != null) {
      // Find participantLocation in next phase
      List<DeliverableParticipantLocation> deliverableParticipantLocations = deliverableParticipantPhase
        .getDeliverableParticipantLocations().stream().filter(pl -> pl.isActive() && pl.getLocElement() != null
          && pl.getLocElement().equals(deliverableParticipantLocation.getLocElement()))
        .collect(Collectors.toList());

      // Create new Participant Location
      if (deliverableParticipantLocations == null || deliverableParticipantLocations.isEmpty()) {
        DeliverableParticipantLocation deliverableParticipantLocationAdd = new DeliverableParticipantLocation();
        this.cloneDeliverableParticipantLocation(deliverableParticipantLocationAdd, deliverableParticipantLocation,
          deliverableParticipantPhase);
        deliverableParticipantLocationDAO.save(deliverableParticipantLocationAdd);
      }
    }

    if (phase.getNext() != null) {
      this.saveDeliverableParticipantLocationPhase(deliverableParticipantLocation, phase.getNext().getId());
    }
  }

}
