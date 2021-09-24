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

import org.cgiar.ccafs.marlo.data.dto.InnovationHomeDTO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;

import java.util.List;


public interface ProjectInnovationDAO {

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
   * This method gets a projectInnovation object by a given projectInnovation identifier.
   * 
   * @param projectInnovationID is the projectInnovation identifier.
   * @return a ProjectInnovation object.
   */
  public ProjectInnovation find(long id);

  /**
   * This method gets a list of projectInnovation that are active
   * 
   * @return a list from ProjectInnovation null if no exist records
   */
  public List<ProjectInnovation> findAll();


  /**
   * This method gets a list of projectInnovations that are active by a given phase
   * 
   * @return a list from ProjectInnovation null if no exist records
   */
  public List<ProjectInnovation> getInnovationsByPhase(Phase phase);

  /**
   * Gets a list of all the innovations from a project planned for the phase's year
   * NOTE: this method is meant to be used by the Home Dashboard table
   * 
   * @param phaseId the Phase identifier
   * @param projectId the Project identifier
   * @return a list of InnovationHomeDTO or empty
   */
  public List<InnovationHomeDTO> getInnovationsByProjectAndPhaseHome(long phaseId, long projectId);

  public Boolean isInnovationExcluded(Long innovationId, Long phaseId);

  /**
   * This method saves the information of the given projectInnovation
   * 
   * @param projectInnovation - is the projectInnovation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovation was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovation save(ProjectInnovation projectInnovation);

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
  public ProjectInnovation save(ProjectInnovation projectInnovation, String section, List<String> relationsName,
    Phase phase);
}
