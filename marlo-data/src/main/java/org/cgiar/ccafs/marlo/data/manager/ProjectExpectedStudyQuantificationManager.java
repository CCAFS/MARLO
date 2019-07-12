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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyQuantification;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyQuantificationManager {


  /**
   * This method removes a specific projectExpectedStudyQuantification value from the database.
   * 
   * @param projectExpectedStudyQuantificationId is the projectExpectedStudyQuantification identifier.
   * @return true if the projectExpectedStudyQuantification was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyQuantification(long projectExpectedStudyQuantificationId);


  /**
   * This method validate if the projectExpectedStudyQuantification identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyQuantificationID is a projectExpectedStudyQuantification identifier.
   * @return true if the projectExpectedStudyQuantification exists, false otherwise.
   */
  public boolean existProjectExpectedStudyQuantification(long projectExpectedStudyQuantificationID);


  /**
   * This method gets a list of projectExpectedStudyQuantification that are active
   * 
   * @return a list from ProjectExpectedStudyQuantification null if no exist records
   */
  public List<ProjectExpectedStudyQuantification> findAll();


  /**
   * This method gets a projectExpectedStudyQuantification object by a given projectExpectedStudyQuantification identifier.
   * 
   * @param projectExpectedStudyQuantificationID is the projectExpectedStudyQuantification identifier.
   * @return a ProjectExpectedStudyQuantification object.
   */
  public ProjectExpectedStudyQuantification getProjectExpectedStudyQuantificationById(long projectExpectedStudyQuantificationID);

  /**
   * This method saves the information of the given projectExpectedStudyQuantification
   * 
   * @param projectExpectedStudyQuantification - is the projectExpectedStudyQuantification object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyQuantification was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyQuantification saveProjectExpectedStudyQuantification(ProjectExpectedStudyQuantification projectExpectedStudyQuantification);


}
