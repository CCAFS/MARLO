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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyTag;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyTagManager {


  /**
   * This method removes a specific projectExpectedStudyTag value from the database.
   * 
   * @param projectExpectedStudyTagId is the projectExpectedStudyTag identifier.
   * @return true if the projectExpectedStudyTag was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyTag(long projectExpectedStudyTagId);


  /**
   * This method validate if the projectExpectedStudyTag identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyTagID is a projectExpectedStudyTag identifier.
   * @return true if the projectExpectedStudyTag exists, false otherwise.
   */
  public boolean existProjectExpectedStudyTag(long projectExpectedStudyTagID);


  /**
   * This method gets a list of projectExpectedStudyTag that are active
   * 
   * @return a list from ProjectExpectedStudyTag null if no exist records
   */
  public List<ProjectExpectedStudyTag> findAll();


  /**
   * This method gets a projectExpectedStudyTag object by a given projectExpectedStudyTag identifier.
   * 
   * @param projectExpectedStudyTagID is the projectExpectedStudyTag identifier.
   * @return a ProjectExpectedStudyTag object.
   */
  public ProjectExpectedStudyTag getProjectExpectedStudyTagById(long projectExpectedStudyTagID);

  /**
   * This method saves the information of the given projectExpectedStudyTag
   * 
   * @param projectExpectedStudyTag - is the projectExpectedStudyTag object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyTag was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyTag saveProjectExpectedStudyTag(ProjectExpectedStudyTag projectExpectedStudyTag);


}
