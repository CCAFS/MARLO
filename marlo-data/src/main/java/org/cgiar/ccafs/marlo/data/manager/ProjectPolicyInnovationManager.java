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

import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInnovation;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectPolicyInnovationManager {


  /**
   * This method removes a specific projectPolicyInnovation value from the database.
   * 
   * @param projectPolicyInnovationId is the projectPolicyInnovation identifier.
   * @return true if the projectPolicyInnovation was successfully deleted, false otherwise.
   */
  public void deleteProjectPolicyInnovation(long projectPolicyInnovationId);


  /**
   * This method validate if the projectPolicyInnovation identify with the given id exists in the system.
   * 
   * @param projectPolicyInnovationID is a projectPolicyInnovation identifier.
   * @return true if the projectPolicyInnovation exists, false otherwise.
   */
  public boolean existProjectPolicyInnovation(long projectPolicyInnovationID);


  /**
   * This method gets a list of projectPolicyInnovation that are active
   * 
   * @return a list from ProjectPolicyInnovation null if no exist records
   */
  public List<ProjectPolicyInnovation> findAll();


  /**
   * This method gets a projectPolicyInnovation object by a given projectPolicyInnovation identifier.
   * 
   * @param projectPolicyInnovationID is the projectPolicyInnovation identifier.
   * @return a ProjectPolicyInnovation object.
   */
  public ProjectPolicyInnovation getProjectPolicyInnovationById(long projectPolicyInnovationID);

  /**
   * This method saves the information of the given projectPolicyInnovation
   * 
   * @param projectPolicyInnovation - is the projectPolicyInnovation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectPolicyInnovation was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPolicyInnovation saveProjectPolicyInnovation(ProjectPolicyInnovation projectPolicyInnovation);


}
