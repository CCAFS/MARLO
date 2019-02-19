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


import org.cgiar.ccafs.marlo.data.dao.DeliverableCrossCuttingMarkerDAO;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrossCuttingMarker;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class DeliverableCrossCuttingMarkerManagerImpl implements DeliverableCrossCuttingMarkerManager {


  private DeliverableCrossCuttingMarkerDAO deliverableCrossCuttingMarkerDAO;
  // Managers


  @Inject
  public DeliverableCrossCuttingMarkerManagerImpl(DeliverableCrossCuttingMarkerDAO deliverableCrossCuttingMarkerDAO) {
    this.deliverableCrossCuttingMarkerDAO = deliverableCrossCuttingMarkerDAO;


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

    return deliverableCrossCuttingMarkerDAO.save(deliverableCrossCuttingMarker);
  }


}
