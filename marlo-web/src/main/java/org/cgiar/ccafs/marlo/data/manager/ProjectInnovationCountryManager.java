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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;

import java.util.List;


/**
 * @author Hermes Jimenez
 */

public interface ProjectInnovationCountryManager {


  /**
   * This method removes a specific projectInnovationCountry value from the database.
   * 
   * @param projectInnovationCountryId is the projectInnovationCountry identifier.
   * @return true if the projectInnovationCountry was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationCountry(long projectInnovationCountryId);


  /**
   * This method validate if the projectInnovationCountry identify with the given id exists in the system.
   * 
   * @param projectInnovationCountryID is a projectInnovationCountry identifier.
   * @return true if the projectInnovationCountry exists, false otherwise.
   */
  public boolean existProjectInnovationCountry(long projectInnovationCountryID);


  /**
   * This method gets a list of projectInnovationCountry that are active
   * 
   * @return a list from ProjectInnovationCountry null if no exist records
   */
  public List<ProjectInnovationCountry> findAll();


  /**
   * This method gets a projectInnovationCountry object list by a given innovation and phase identifier.
   * 
   * @param innovationID is the innovation identifier.
   * @param phaseID is the phase identifier.
   * @return a projectInnovationCountry object list.
   */
  public List<ProjectInnovationCountry> getInnovationCountrybyPhase(long innovationID, long phaseID);

  /**
   * This method gets a projectInnovationCountry object by a given projectInnovationCountry identifier.
   * 
   * @param projectInnovationCountryID is the projectInnovationCountry identifier.
   * @return a ProjectInnovationCountry object.
   */
  public ProjectInnovationCountry getProjectInnovationCountryById(long projectInnovationCountryID);

  /**
   * This method saves the information of the given projectInnovationCountry
   * 
   * @param projectInnovationCountry - is the projectInnovationCountry object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationCountry
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationCountry saveProjectInnovationCountry(ProjectInnovationCountry projectInnovationCountry);


}
