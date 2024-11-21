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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReference;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationReferenceManager {


  /**
   * This method removes a specific projectInnovationReference value from the database.
   * 
   * @param projectInnovationReferenceId is the projectInnovationReference identifier.
   * @return true if the projectInnovationReference was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationReference(long projectInnovationReferenceId);


  /**
   * This method validate if the projectInnovationReference identify with the given id exists in the system.
   * 
   * @param projectInnovationReferenceID is a projectInnovationReference identifier.
   * @return true if the projectInnovationReference exists, false otherwise.
   */
  public boolean existProjectInnovationReference(long projectInnovationReferenceID);


  /**
   * This method gets a list of projectInnovationReference that are active
   * 
   * @return a list from ProjectInnovationReference null if no exist records
   */
  public List<ProjectInnovationReference> findAll();


  /**
   * This method gets a projectInnovationReference object by a given projectInnovationReference identifier.
   * 
   * @param projectInnovationReferenceID is the projectInnovationReference identifier.
   * @return a ProjectInnovationReference object.
   */
  public ProjectInnovationReference getProjectInnovationReferenceById(long projectInnovationReferenceID);

  /**
   * This method gets a projectInnovationReference list by a given projectInnovationReference identifier.
   * 
   * @param projectInnovationID is the projectInnovationReference identifier.
   * @param PhaseID is the phase identifier.
   * @return a ProjectInnovationReference object.
   */
  public List<ProjectInnovationReference> getProjectInnovationReferenceByPhaseAndInnovation(long phaseID,
    long innovationID);

  /**
   * This method saves the information of the given projectInnovationReference
   * 
   * @param projectInnovationReference - is the projectInnovationReference object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectInnovationReference was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationReference
    saveProjectInnovationReference(ProjectInnovationReference projectInnovationReference);


}
