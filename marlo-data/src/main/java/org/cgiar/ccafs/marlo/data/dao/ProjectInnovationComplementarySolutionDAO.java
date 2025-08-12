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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationComplementarySolution;

import java.util.List;


public interface ProjectInnovationComplementarySolutionDAO {

  /**
   * This method removes a specific projectInnovationComplementarySolution value from the database.
   * 
   * @param projectInnovationComplementarySolutionId is the projectInnovationComplementarySolution identifier.
   * @return true if the projectInnovationComplementarySolution was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationComplementarySolution(long projectInnovationComplementarySolutionId);

  /**
   * This method validate if the projectInnovationComplementarySolution identify with the given id exists in the system.
   * 
   * @param projectInnovationComplementarySolutionID is a projectInnovationComplementarySolution identifier.
   * @return true if the projectInnovationComplementarySolution exists, false otherwise.
   */
  public boolean existProjectInnovationComplementarySolution(long projectInnovationComplementarySolutionID);

  /**
   * This method gets a projectInnovationComplementarySolution object by a given projectInnovationComplementarySolution
   * identifier.
   * 
   * @param projectInnovationComplementarySolutionID is the projectInnovationComplementarySolution identifier.
   * @return a ProjectInnovationComplementarySolution object.
   */
  public ProjectInnovationComplementarySolution find(long id);

  /**
   * This method gets a list of projectInnovationComplementarySolution that are active
   * 
   * @return a list from ProjectInnovationComplementarySolution null if no exist records
   */
  public List<ProjectInnovationComplementarySolution> findAll();


  /**
   * This method gets a list of projectInnovationComplementarySolution by a given innovation identifier.
   * 
   * @param innovationID is a innovation identifier.
   * @return a list from ProjectInnovationComplementarySolution null if no exist records
   */
  public List<ProjectInnovationComplementarySolution>
    getProjectInnovationComplementarySolutionByInnovation(long innovationID);

  /**
   * This method gets a list of projectInnovationComplementarySolution by a given phase and innovation identifier.
   * 
   * @param innovationID is a innovation identifier.
   * @param phaseID is a phase identifier.
   * @return a list from ProjectInnovationComplementarySolution null if no exist records
   */
  public List<ProjectInnovationComplementarySolution>
    getProjectInnovationComplementarySolutionByInnovationAndPhase(long innovationID, long phaseID);

  /**
   * This method saves the information of the given projectInnovationComplementarySolution
   * 
   * @param projectInnovationComplementarySolution - is the projectInnovationComplementarySolution object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectInnovationComplementarySolution was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationComplementarySolution
    save(ProjectInnovationComplementarySolution projectInnovationComplementarySolution);
}
