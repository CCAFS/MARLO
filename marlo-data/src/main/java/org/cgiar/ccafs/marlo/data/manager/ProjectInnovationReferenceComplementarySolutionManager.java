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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceComplementarySolution;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationReferenceComplementarySolutionManager {


  /**
   * This method removes a specific projectInnovationReferenceComplementarySolution value from the database.
   * 
   * @param projectInnovationReferenceComplementarySolutionId is the projectInnovationReferenceComplementarySolution
   *        identifier.
   * @return true if the projectInnovationReferenceComplementarySolution was successfully deleted, false otherwise.
   */
  public void
    deleteProjectInnovationReferenceComplementarySolution(long projectInnovationReferenceComplementarySolutionId);


  /**
   * This method validate if the projectInnovationReferenceComplementarySolution identify with the given id exists in
   * the system.
   * 
   * @param projectInnovationReferenceComplementarySolutionID is a projectInnovationReferenceComplementarySolution
   *        identifier.
   * @return true if the projectInnovationReferenceComplementarySolution exists, false otherwise.
   */
  public boolean
    existProjectInnovationReferenceComplementarySolution(long projectInnovationReferenceComplementarySolutionID);


  /**
   * This method gets a list of projectInnovationReferenceComplementarySolution that are active
   * 
   * @return a list from ProjectInnovationReferenceComplementarySolution null if no exist records
   */
  public List<ProjectInnovationReferenceComplementarySolution> findAll();


  /**
   * This method gets a projectInnovationReferenceComplementarySolution object by a given
   * projectInnovationReferenceComplementarySolution identifier.
   * 
   * @param projectInnovationReferenceComplementarySolutionID is the projectInnovationReferenceComplementarySolution
   *        identifier.
   * @return a ProjectInnovationReferenceComplementarySolution object.
   */
  public ProjectInnovationReferenceComplementarySolution
    getProjectInnovationReferenceComplementarySolutionById(long projectInnovationReferenceComplementarySolutionID);

  /**
   * This method gets a projectInnovationReferenceComplementarySolution list by a given projectInnovation.
   * 
   * @param projectInnovationID is the projectInnovation identifier.
   * @param PhaseID is the phase identifier.
   * @return a ProjectInnovationReference object.
   */
  public List<ProjectInnovationReferenceComplementarySolution>
    getProjectInnovationReferenceComplementarySolutionByPhaseAndInnovation(long phaseID, long innovationID);

  /**
   * This method saves the information of the given projectInnovationReferenceComplementarySolution
   * 
   * @param projectInnovationReferenceComplementarySolution - is the projectInnovationReferenceComplementarySolution
   *        object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectInnovationReferenceComplementarySolution was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationReferenceComplementarySolution saveProjectInnovationReferenceComplementarySolution(
    ProjectInnovationReferenceComplementarySolution projectInnovationReferenceComplementarySolution);

}
