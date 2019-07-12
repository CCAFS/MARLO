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
import org.cgiar.ccafs.marlo.data.dao.DeliverableCrossCuttingMarkerDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableCrossCuttingMarkerManagerImpl implements DeliverableCrossCuttingMarkerManager {


  private DeliverableCrossCuttingMarkerDAO deliverableCrossCuttingMarkerDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public DeliverableCrossCuttingMarkerManagerImpl(DeliverableCrossCuttingMarkerDAO deliverableCrossCuttingMarkerDAO,
    PhaseDAO phaseDAO) {
    this.deliverableCrossCuttingMarkerDAO = deliverableCrossCuttingMarkerDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteDeliverableCrossCuttingMarker(long deliverableCrossCuttingMarkerId) {

    deliverableCrossCuttingMarkerDAO.deleteDeliverableCrossCuttingMarker(deliverableCrossCuttingMarkerId);
  }

  @Override
  public boolean existDeliverableCrossCuttingMarker(long deliverableCrossCuttingMarkerID) {

    return deliverableCrossCuttingMarkerDAO.existDeliverableCrossCuttingMarker(deliverableCrossCuttingMarkerID);
  }

  @Override
  public List<DeliverableCrossCuttingMarker> findAll() {

    return deliverableCrossCuttingMarkerDAO.findAll();

  }

  @Override
  public DeliverableCrossCuttingMarker getDeliverableCrossCuttingMarkerById(long deliverableCrossCuttingMarkerID) {

    return deliverableCrossCuttingMarkerDAO.find(deliverableCrossCuttingMarkerID);
  }

  @Override
  public DeliverableCrossCuttingMarker getDeliverableCrossCuttingMarkerId(long deliverableID,
    long cgiarCrossCuttingMarkerID, long phaseID) {
    return deliverableCrossCuttingMarkerDAO.getDeliverableCrossCountryMarkerId(deliverableID, cgiarCrossCuttingMarkerID,
      phaseID);
  }

  @Override
  public DeliverableCrossCuttingMarker
    saveDeliverableCrossCuttingMarker(DeliverableCrossCuttingMarker deliverableCrossCuttingMarker) {

    DeliverableCrossCuttingMarker marker = deliverableCrossCuttingMarkerDAO.save(deliverableCrossCuttingMarker);

    Phase currentPhase = phaseDAO.find(deliverableCrossCuttingMarker.getPhase().getId());
    boolean isPublication = deliverableCrossCuttingMarker.getDeliverable().getIsPublication() != null
      && deliverableCrossCuttingMarker.getDeliverable().getIsPublication();
    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null
      && !isPublication) {
      if (deliverableCrossCuttingMarker.getPhase().getNext() != null) {
        this.saveDeliverableCrossCuttingMarkerAddPhase(deliverableCrossCuttingMarker.getPhase().getNext(),
          deliverableCrossCuttingMarker.getDeliverable().getId(), deliverableCrossCuttingMarker);
      }
    }
    if (currentPhase.getDescription().equals(APConstants.REPORTING) && !isPublication) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableCrossCuttingMarkerAddPhase(upkeepPhase,
            deliverableCrossCuttingMarker.getDeliverable().getId(), deliverableCrossCuttingMarker);
        }
      }
    }

    return marker;
  }

  public void saveDeliverableCrossCuttingMarkerAddPhase(Phase next, long deliverableID,
    DeliverableCrossCuttingMarker deliverableCrossCuttingMarker) {

    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableCrossCuttingMarker> deliverableCrossCuttingMarkers =
      phase.getDeliverableCrossCuttingMarkers().stream()
        .filter(
          c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID && c.getCgiarCrossCuttingMarker()
            .getId().equals(deliverableCrossCuttingMarker.getCgiarCrossCuttingMarker().getId()))
        .collect(Collectors.toList());

    if (deliverableCrossCuttingMarkers.isEmpty()) {
      DeliverableCrossCuttingMarker deliverableCrossCuttingMarkerAdd = new DeliverableCrossCuttingMarker();
      deliverableCrossCuttingMarkerAdd.setDeliverable(deliverableCrossCuttingMarker.getDeliverable());
      deliverableCrossCuttingMarkerAdd.setPhase(phase);
      deliverableCrossCuttingMarkerAdd
        .setRepIndGenderYouthFocusLevel(deliverableCrossCuttingMarker.getRepIndGenderYouthFocusLevel());
      deliverableCrossCuttingMarkerAdd
        .setCgiarCrossCuttingMarker(deliverableCrossCuttingMarker.getCgiarCrossCuttingMarker());
      deliverableCrossCuttingMarkerDAO.save(deliverableCrossCuttingMarkerAdd);
    }

    if (phase.getNext() != null) {
      this.saveDeliverableCrossCuttingMarkerAddPhase(phase.getNext(), deliverableID, deliverableCrossCuttingMarker);
    }
  }


}
