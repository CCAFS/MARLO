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

import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrossCuttingMarker;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectPolicyCrossCuttingMarkerManager {


  /**
   * This method removes a specific projectPolicyCrossCuttingMarker value from the database.
   * 
   * @param projectPolicyCrossCuttingMarkerId is the projectPolicyCrossCuttingMarker identifier.
   * @return true if the projectPolicyCrossCuttingMarker was successfully deleted, false otherwise.
   */
  public void deleteProjectPolicyCrossCuttingMarker(long projectPolicyCrossCuttingMarkerId);


  /**
   * This method validate if the projectPolicyCrossCuttingMarker identify with the given id exists in the system.
   * 
   * @param projectPolicyCrossCuttingMarkerID is a projectPolicyCrossCuttingMarker identifier.
   * @return true if the projectPolicyCrossCuttingMarker exists, false otherwise.
   */
  public boolean existProjectPolicyCrossCuttingMarker(long projectPolicyCrossCuttingMarkerID);


  /**
   * This method gets a list of projectPolicyCrossCuttingMarker that are active
   * 
   * @return a list from ProjectPolicyCrossCuttingMarker null if no exist records
   */
  public List<ProjectPolicyCrossCuttingMarker> findAll();


  /**
   * @param policyID
   * @param cgiarCrossCuttingMarkerID
   * @param phaseID
   * @return
   */
  public ProjectPolicyCrossCuttingMarker getPolicyCrossCountryMarkerId(long policyID, long cgiarCrossCuttingMarkerID,
    long phaseID);

  /**
   * This method gets a projectPolicyCrossCuttingMarker object by a given projectPolicyCrossCuttingMarker identifier.
   * 
   * @param projectPolicyCrossCuttingMarkerID is the projectPolicyCrossCuttingMarker identifier.
   * @return a ProjectPolicyCrossCuttingMarker object.
   */
  public ProjectPolicyCrossCuttingMarker getProjectPolicyCrossCuttingMarkerById(long projectPolicyCrossCuttingMarkerID);

  /**
   * This method saves the information of the given projectPolicyCrossCuttingMarker
   * 
   * @param projectPolicyCrossCuttingMarker - is the projectPolicyCrossCuttingMarker object with the new information to
   *        be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectPolicyCrossCuttingMarker was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPolicyCrossCuttingMarker
    saveProjectPolicyCrossCuttingMarker(ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker);


}
