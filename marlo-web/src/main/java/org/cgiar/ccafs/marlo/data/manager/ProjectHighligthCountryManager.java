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

import org.cgiar.ccafs.marlo.data.model.ProjectHighlightCountry;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ProjectHighligthCountryManager {


  /**
   * This method removes a specific projectHighligthCountry value from the database.
   * 
   * @param projectHighligthCountryId is the projectHighligthCountry identifier.
   * @return true if the projectHighligthCountry was successfully deleted, false otherwise.
   */
  public void deleteProjectHighligthCountry(long projectHighligthCountryId);


  /**
   * This method validate if the projectHighligthCountry identify with the given id exists in the system.
   * 
   * @param projectHighligthCountryID is a projectHighligthCountry identifier.
   * @return true if the projectHighligthCountry exists, false otherwise.
   */
  public boolean existProjectHighligthCountry(long projectHighligthCountryID);


  /**
   * This method gets a list of projectHighligthCountry that are active
   * 
   * @return a list from ProjectHighlightCountry null if no exist records
   */
  public List<ProjectHighlightCountry> findAll();


  /**
   * This method gets a projectHighligthCountry object by a given projectHighligthCountry identifier.
   * 
   * @param projectHighligthCountryID is the projectHighligthCountry identifier.
   * @return a ProjectHighlightCountry object.
   */
  public ProjectHighlightCountry getProjectHighligthCountryById(long projectHighligthCountryID);

  /**
   * This method saves the information of the given projectHighligthCountry
   * 
   * @param projectHighlightCountry - is the projectHighligthCountry object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectHighligthCountry
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectHighlightCountry saveProjectHighligthCountry(ProjectHighlightCountry projectHighlightCountry);


}
