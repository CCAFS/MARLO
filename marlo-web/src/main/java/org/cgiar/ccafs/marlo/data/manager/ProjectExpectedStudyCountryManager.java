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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyCountryManager {


  /**
   * This method removes a specific projectExpectedStudyCountry value from the database.
   * 
   * @param projectExpectedStudyCountryId is the projectExpectedStudyCountry identifier.
   * @return true if the projectExpectedStudyCountry was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyCountry(long projectExpectedStudyCountryId);


  /**
   * This method validate if the projectExpectedStudyCountry identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyCountryID is a projectExpectedStudyCountry identifier.
   * @return true if the projectExpectedStudyCountry exists, false otherwise.
   */
  public boolean existProjectExpectedStudyCountry(long projectExpectedStudyCountryID);


  /**
   * This method gets a list of projectExpectedStudyCountry that are active
   * 
   * @return a list from ProjectExpectedStudyCountry null if no exist records
   */
  public List<ProjectExpectedStudyCountry> findAll();

  /**
   * This method gets a projectExpectedStudyCountry object by a given projectExpectedStudyCountry identifier.
   * 
   * @param projectExpectedStudyCountryID is the projectExpectedStudyCountry identifier.
   * @return a ProjectExpectedStudyCountry object.
   */
  public ProjectExpectedStudyCountry getProjectExpectedStudyCountryById(long projectExpectedStudyCountryID);


  /**
   * This method gets a projectExpectedStudyCountry object list by a given expected Study and phase identifier.
   * 
   * @param expectedID is the expected Study identifier.
   * @param phaseID is the phase identifier.
   * @return a projectExpectedStudyCountry object list.
   */
  public List<ProjectExpectedStudyCountry> getProjectExpectedStudyCountrybyPhase(long expectedID, long phaseID);

  /**
   * This method saves the information of the given projectExpectedStudyCountry
   * 
   * @param projectExpectedStudyCountry - is the projectExpectedStudyCountry object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyCountry was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyCountry
    saveProjectExpectedStudyCountry(ProjectExpectedStudyCountry projectExpectedStudyCountry);


}
