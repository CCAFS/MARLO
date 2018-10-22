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
import org.cgiar.ccafs.marlo.data.dao.DeliverableGeographicRegionDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableGeographicRegionManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableGeographicRegion;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableGeographicRegionManagerImpl implements DeliverableGeographicRegionManager {


  private DeliverableGeographicRegionDAO deliverableGeographicRegionDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public DeliverableGeographicRegionManagerImpl(DeliverableGeographicRegionDAO deliverableGeographicRegionDAO,
    PhaseDAO phaseDAO) {
    this.deliverableGeographicRegionDAO = deliverableGeographicRegionDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteDeliverableGeographicRegion(long deliverableGeographicRegionId) {

    DeliverableGeographicRegion deliverableGeographicRegion =
      this.getDeliverableGeographicRegionById(deliverableGeographicRegionId);
    Phase currentPhase = deliverableGeographicRegion.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteDeliverableGeographicRegionPhase(currentPhase.getNext(),
        deliverableGeographicRegion.getDeliverable().getId(), deliverableGeographicRegion);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteDeliverableGeographicRegionPhase(upkeepPhase.getNext(),
            deliverableGeographicRegion.getDeliverable().getId(), deliverableGeographicRegion);
        }
      }
    }

    deliverableGeographicRegionDAO.deleteDeliverableGeographicRegion(deliverableGeographicRegionId);
  }

  public void deleteDeliverableGeographicRegionPhase(Phase next, long deliverableID,
    DeliverableGeographicRegion deliverableGeographicRegion) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableGeographicRegion> deliverableGeographicRegions = phase.getDeliverableGeographicRegions().stream()
      .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == deliverableID
        && c.getLocElement().getId().equals(deliverableGeographicRegion.getLocElement().getId()))
      .collect(Collectors.toList());
    for (DeliverableGeographicRegion deliverableGeographicRegionDB : deliverableGeographicRegions) {
      deliverableGeographicRegionDAO.deleteDeliverableGeographicRegion(deliverableGeographicRegionDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteDeliverableGeographicRegionPhase(phase.getNext(), deliverableID, deliverableGeographicRegion);
    }
  }


  @Override
  public boolean existDeliverableGeographicRegion(long deliverableGeographicRegionID) {

    return deliverableGeographicRegionDAO.existDeliverableGeographicRegion(deliverableGeographicRegionID);
  }


  @Override
  public List<DeliverableGeographicRegion> findAll() {

    return deliverableGeographicRegionDAO.findAll();

  }

  @Override
  public DeliverableGeographicRegion getDeliverableGeographicRegionById(long deliverableGeographicRegionID) {

    return deliverableGeographicRegionDAO.find(deliverableGeographicRegionID);
  }

  @Override
  public List<DeliverableGeographicRegion> getDeliverableGeographicRegionbyPhase(long deliverableID, long phaseID) {
    return deliverableGeographicRegionDAO.getDeliverableGeographicRegionbyPhase(deliverableID, phaseID);
  }

  @Override
  public DeliverableGeographicRegion
    saveDeliverableGeographicRegion(DeliverableGeographicRegion deliverableGeographicRegion) {
    DeliverableGeographicRegion region = deliverableGeographicRegionDAO.save(deliverableGeographicRegion);
    Phase currentPhase = phaseDAO.find(region.getPhase().getId());

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.saveDeliverableGeographicRegionPhase(currentPhase.getNext(), region.getDeliverable().getId(),
        deliverableGeographicRegion);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveDeliverableGeographicRegionPhase(upkeepPhase, region.getDeliverable().getId(),
            deliverableGeographicRegion);
        }
      }
    }

    return region;
  }

  public void saveDeliverableGeographicRegionPhase(Phase next, long deliverableID,
    DeliverableGeographicRegion deliverableGeographicRegion) {
    Phase phase = phaseDAO.find(next.getId());

    List<DeliverableGeographicRegion> deliverableGeographicRegions = phase.getDeliverableGeographicRegions().stream()
      .filter(c -> c.getDeliverable().getId().longValue() == deliverableID
        && c.getLocElement().getId().equals(deliverableGeographicRegion.getLocElement().getId()))
      .collect(Collectors.toList());

    if (deliverableGeographicRegions.isEmpty()) {
      DeliverableGeographicRegion deliverableGeographicRegionAdd = new DeliverableGeographicRegion();
      deliverableGeographicRegionAdd.setDeliverable(deliverableGeographicRegion.getDeliverable());
      deliverableGeographicRegionAdd.setPhase(phase);
      deliverableGeographicRegionAdd.setLocElement(deliverableGeographicRegion.getLocElement());
      deliverableGeographicRegionDAO.save(deliverableGeographicRegionAdd);
    }

    if (phase.getNext() != null) {
      this.saveDeliverableGeographicRegionPhase(phase.getNext(), deliverableID, deliverableGeographicRegion);
    }
  }


}
