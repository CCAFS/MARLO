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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;

import java.util.List;


/**
 * @author CCAFS
 */

public interface CgiarCrossCuttingMarkerManager {


  /**
   * This method removes a specific cgiarCrossCuttingMarker value from the database.
   * 
   * @param cgiarCrossCuttingMarkerId is the cgiarCrossCuttingMarker identifier.
   * @return true if the cgiarCrossCuttingMarker was successfully deleted, false otherwise.
   */
  public void deleteCgiarCrossCuttingMarker(long cgiarCrossCuttingMarkerId);


  /**
   * This method validate if the cgiarCrossCuttingMarker identify with the given id exists in the system.
   * 
   * @param cgiarCrossCuttingMarkerID is a cgiarCrossCuttingMarker identifier.
   * @return true if the cgiarCrossCuttingMarker exists, false otherwise.
   */
  public boolean existCgiarCrossCuttingMarker(long cgiarCrossCuttingMarkerID);


  /**
   * This method gets a list of cgiarCrossCuttingMarker that are active
   * 
   * @return a list from CgiarCrossCuttingMarker null if no exist records
   */
  public List<CgiarCrossCuttingMarker> findAll();


  /**
   * This method gets a cgiarCrossCuttingMarker object by a given cgiarCrossCuttingMarker identifier.
   * 
   * @param cgiarCrossCuttingMarkerID is the cgiarCrossCuttingMarker identifier.
   * @return a CgiarCrossCuttingMarker object.
   */
  public CgiarCrossCuttingMarker getCgiarCrossCuttingMarkerById(long cgiarCrossCuttingMarkerID);

  /**
   * This method saves the information of the given cgiarCrossCuttingMarker
   * 
   * @param cgiarCrossCuttingMarker - is the cgiarCrossCuttingMarker object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the cgiarCrossCuttingMarker was
   *         updated
   *         or -1 is some error occurred.
   */
  public CgiarCrossCuttingMarker saveCgiarCrossCuttingMarker(CgiarCrossCuttingMarker cgiarCrossCuttingMarker);


}
