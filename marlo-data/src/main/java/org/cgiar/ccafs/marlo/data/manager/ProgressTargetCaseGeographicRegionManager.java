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

import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicRegion;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProgressTargetCaseGeographicRegionManager {


  /**
   * This method removes a specific progressTargetCaseGeographicRegion value from the database.
   * 
   * @param progressTargetCaseGeographicRegionId is the progressTargetCaseGeographicRegion identifier.
   * @return true if the progressTargetCaseGeographicRegion was successfully deleted, false otherwise.
   */
  public void deleteProgressTargetCaseGeographicRegion(long progressTargetCaseGeographicRegionId);


  /**
   * This method validate if the progressTargetCaseGeographicRegion identify with the given id exists in the system.
   * 
   * @param progressTargetCaseGeographicRegionID is a progressTargetCaseGeographicRegion identifier.
   * @return true if the progressTargetCaseGeographicRegion exists, false otherwise.
   */
  public boolean existProgressTargetCaseGeographicRegion(long progressTargetCaseGeographicRegionID);


  /**
   * This method gets a list of progressTargetCaseGeographicRegion that are active
   * 
   * @return a list from ProgressTargetCaseGeographicRegion null if no exist records
   */
  public List<ProgressTargetCaseGeographicRegion> findAll();

  public List<ProgressTargetCaseGeographicRegion> findGeographicRegionByTargetCase(long targetCaseID);


  /**
   * This method gets a progressTargetCaseGeographicRegion object by a given progressTargetCaseGeographicRegion
   * identifier.
   * 
   * @param progressTargetCaseGeographicRegionID is the progressTargetCaseGeographicRegion identifier.
   * @return a ProgressTargetCaseGeographicRegion object.
   */
  public ProgressTargetCaseGeographicRegion
    getProgressTargetCaseGeographicRegionById(long progressTargetCaseGeographicRegionID);

  /**
   * This method saves the information of the given progressTargetCaseGeographicRegion
   * 
   * @param progressTargetCaseGeographicRegion - is the progressTargetCaseGeographicRegion object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         progressTargetCaseGeographicRegion was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProgressTargetCaseGeographicRegion
    saveProgressTargetCaseGeographicRegion(ProgressTargetCaseGeographicRegion progressTargetCaseGeographicRegion);


}
