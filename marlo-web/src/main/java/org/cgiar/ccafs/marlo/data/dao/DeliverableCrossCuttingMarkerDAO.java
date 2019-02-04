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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.DeliverableCrossCuttingMarker;

import java.util.List;


public interface DeliverableCrossCuttingMarkerDAO {

  /**
   * This method removes a specific deliverableCrossCuttingMarker value from the database.
   * 
   * @param deliverableCrossCuttingMarkerId is the deliverableCrossCuttingMarker identifier.
   * @return true if the deliverableCrossCuttingMarker was successfully deleted, false otherwise.
   */
  public void deleteDeliverableCrossCuttingMarker(long deliverableCrossCuttingMarkerId);

  /**
   * This method validate if the deliverableCrossCuttingMarker identify with the given id exists in the system.
   * 
   * @param deliverableCrossCuttingMarkerID is a deliverableCrossCuttingMarker identifier.
   * @return true if the deliverableCrossCuttingMarker exists, false otherwise.
   */
  public boolean existDeliverableCrossCuttingMarker(long deliverableCrossCuttingMarkerID);

  /**
   * This method gets a deliverableCrossCuttingMarker object by a given deliverableCrossCuttingMarker identifier.
   * 
   * @param deliverableCrossCuttingMarkerID is the deliverableCrossCuttingMarker identifier.
   * @return a DeliverableCrossCuttingMarker object.
   */
  public DeliverableCrossCuttingMarker find(long id);

  /**
   * This method gets a list of deliverableCrossCuttingMarker that are active
   * 
   * @return a list from DeliverableCrossCuttingMarker null if no exist records
   */
  public List<DeliverableCrossCuttingMarker> findAll();


  /**
   * @param deliverableID
   * @param cgiarCrossCuttingMarkerID
   * @param phaseID
   * @return
   */
  public DeliverableCrossCuttingMarker getDeliverableCrossCountryMarkerId(long deliverableID,
    long cgiarCrossCuttingMarkerID, long phaseID);

  /**
   * This method saves the information of the given deliverableCrossCuttingMarker
   * 
   * @param deliverableCrossCuttingMarker - is the deliverableCrossCuttingMarker object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         deliverableCrossCuttingMarker was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableCrossCuttingMarker save(DeliverableCrossCuttingMarker deliverableCrossCuttingMarker);
}
