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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyInstitutionManager {


  /**
   * This method removes a specific projectExpectedStudyInstitution value from the database.
   * 
   * @param projectExpectedStudyInstitutionId is the projectExpectedStudyInstitution identifier.
   * @return true if the projectExpectedStudyInstitution was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyInstitution(long projectExpectedStudyInstitutionId);


  /**
   * This method validate if the projectExpectedStudyInstitution identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyInstitutionID is a projectExpectedStudyInstitution identifier.
   * @return true if the projectExpectedStudyInstitution exists, false otherwise.
   */
  public boolean existProjectExpectedStudyInstitution(long projectExpectedStudyInstitutionID);


  /**
   * This method gets a list of projectExpectedStudyInstitution that are active
   * 
   * @return a list from ProjectExpectedStudyInstitution null if no exist records
   */
  public List<ProjectExpectedStudyInstitution> findAll();


  /**
   * This method gets a projectExpectedStudyInstitution object by a given projectExpectedStudyInstitution identifier.
   * 
   * @param projectExpectedStudyInstitutionID is the projectExpectedStudyInstitution identifier.
   * @return a ProjectExpectedStudyInstitution object.
   */
  public ProjectExpectedStudyInstitution getProjectExpectedStudyInstitutionById(long projectExpectedStudyInstitutionID);

  /**
   * This method saves the information of the given projectExpectedStudyInstitution
   * 
   * @param projectExpectedStudyInstitution - is the projectExpectedStudyInstitution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyInstitution was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyInstitution saveProjectExpectedStudyInstitution(ProjectExpectedStudyInstitution projectExpectedStudyInstitution);


}
