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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyInnovationManager {


  /**
   * This method removes a specific projectExpectedStudyInnovation value from the database.
   * 
   * @param projectExpectedStudyInnovationId is the projectExpectedStudyInnovation identifier.
   * @return true if the projectExpectedStudyInnovation was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyInnovation(long projectExpectedStudyInnovationId);


  /**
   * This method validate if the projectExpectedStudyInnovation identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyInnovationID is a projectExpectedStudyInnovation identifier.
   * @return true if the projectExpectedStudyInnovation exists, false otherwise.
   */
  public boolean existProjectExpectedStudyInnovation(long projectExpectedStudyInnovationID);


  /**
   * This method gets a list of projectExpectedStudyInnovation that are active
   * 
   * @return a list from ProjectExpectedStudyInnovation null if no exist records
   */
  public List<ProjectExpectedStudyInnovation> findAll();


  /**
   * This method gets a projectExpectedStudyInnovation object by a given projectExpectedStudyInnovation identifier.
   * 
   * @param projectExpectedStudyInnovationID is the projectExpectedStudyInnovation identifier.
   * @return a ProjectExpectedStudyInnovation object.
   */
  public ProjectExpectedStudyInnovation getProjectExpectedStudyInnovationById(long projectExpectedStudyInnovationID);

  /**
   * This method saves the information of the given projectExpectedStudyInnovation
   * 
   * @param projectExpectedStudyInnovation - is the projectExpectedStudyInnovation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyInnovation was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyInnovation saveProjectExpectedStudyInnovation(ProjectExpectedStudyInnovation projectExpectedStudyInnovation);


}
