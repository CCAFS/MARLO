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

import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ProjectInnovationManager {


  /**
   * This method removes a specific projectInnovation value from the database.
   * 
   * @param projectInnovationId is the projectInnovation identifier.
   * @return true if the projectInnovation was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovation(long projectInnovationId);


  /**
   * This method validate if the projectInnovation identify with the given id exists in the system.
   * 
   * @param projectInnovationID is a projectInnovation identifier.
   * @return true if the projectInnovation exists, false otherwise.
   */
  public boolean existProjectInnovation(long projectInnovationID);


  /**
   * This method gets a list of projectInnovation that are active
   * 
   * @return a list from ProjectInnovation null if no exist records
   */
  public List<ProjectInnovation> findAll();


  /**
   * This method gets a projectInnovation object by a given projectInnovation identifier.
   * 
   * @param projectInnovationID is the projectInnovation identifier.
   * @return a ProjectInnovation object.
   */
  public ProjectInnovation getProjectInnovationById(long projectInnovationID);

  /**
   * This method saves the information of the given projectInnovation
   * 
   * @param projectInnovation - is the projectInnovation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovation was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovation saveProjectInnovation(ProjectInnovation projectInnovation);

  /**
   * This method saves the information of the given projectInnovation
   * 
   * @param projectInnovation - is the projectInnovation object with the new information to be added/updated.
   * @param section - the name of the map section.
   * @param relationsName - a List of set relations that the object have it.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovation was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovation saveProjectInnovation(ProjectInnovation projectInnovation, String section,
    List<String> relationsName, Phase phase);


}
