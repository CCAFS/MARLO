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

import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicCountry;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProgressTargetCaseGeographicCountryManager {


  /**
   * This method removes a specific progressTargetCaseGeographicCountry value from the database.
   * 
   * @param progressTargetCaseGeographicCountryId is the progressTargetCaseGeographicCountry identifier.
   * @return true if the progressTargetCaseGeographicCountry was successfully deleted, false otherwise.
   */
  public void deleteProgressTargetCaseGeographicCountry(long progressTargetCaseGeographicCountryId);


  /**
   * This method validate if the progressTargetCaseGeographicCountry identify with the given id exists in the system.
   * 
   * @param progressTargetCaseGeographicCountryID is a progressTargetCaseGeographicCountry identifier.
   * @return true if the progressTargetCaseGeographicCountry exists, false otherwise.
   */
  public boolean existProgressTargetCaseGeographicCountry(long progressTargetCaseGeographicCountryID);


  /**
   * This method gets a list of progressTargetCaseGeographicCountry that are active
   * 
   * @return a list from ProgressTargetCaseGeographicCountry null if no exist records
   */
  public List<ProgressTargetCaseGeographicCountry> findAll();


  public List<ProgressTargetCaseGeographicCountry> findGeographicCountryByTargetCase(long targetCaseID);


  /**
   * This method gets a progressTargetCaseGeographicCountry object by a given progressTargetCaseGeographicCountry
   * identifier.
   * 
   * @param progressTargetCaseGeographicCountryID is the progressTargetCaseGeographicCountry identifier.
   * @return a ProgressTargetCaseGeographicCountry object.
   */
  public ProgressTargetCaseGeographicCountry
    getProgressTargetCaseGeographicCountryById(long progressTargetCaseGeographicCountryID);

  /**
   * This method saves the information of the given progressTargetCaseGeographicCountry
   * 
   * @param progressTargetCaseGeographicCountry - is the progressTargetCaseGeographicCountry object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         progressTargetCaseGeographicCountry was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProgressTargetCaseGeographicCountry
    saveProgressTargetCaseGeographicCountry(ProgressTargetCaseGeographicCountry progressTargetCaseGeographicCountry);


}
