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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ProjectExpectedStudyManager {


  /**
   * This method removes a specific projectExpectedStudy value from the database.
   * 
   * @param projectExpectedStudyId is the projectExpectedStudy identifier.
   * @return true if the projectExpectedStudy was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudy(long projectExpectedStudyId);


  /**
   * This method validate if the projectExpectedStudy identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyID is a projectExpectedStudy identifier.
   * @return true if the projectExpectedStudy exists, false otherwise.
   */
  public boolean existProjectExpectedStudy(long projectExpectedStudyID);


  /**
   * This method gets a list of projectExpectedStudy that are active
   * 
   * @return a list from ProjectExpectedStudy null if no exist records
   */
  public List<ProjectExpectedStudy> findAll();


  /**
   * This method gets a projectExpectedStudy object by a given projectExpectedStudy identifier.
   * 
   * @param projectExpectedStudyID is the projectExpectedStudy identifier.
   * @return a ProjectExpectedStudy object.
   */
  public ProjectExpectedStudy getProjectExpectedStudyById(long projectExpectedStudyID);

  /**
   * This method saves the information of the given projectExpectedStudy
   * 
   * @param projectExpectedStudy - is the projectExpectedStudy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudy was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudy saveProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy);


}
