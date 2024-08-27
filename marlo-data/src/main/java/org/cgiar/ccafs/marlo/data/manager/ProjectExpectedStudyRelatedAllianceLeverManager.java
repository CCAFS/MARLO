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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRelatedAllianceLever;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyRelatedAllianceLeverManager {


  /**
   * This method removes a specific projectExpectedStudyRelatedAllianceLever value from the database.
   * 
   * @param projectExpectedStudyRelatedAllianceLeverId is the projectExpectedStudyRelatedAllianceLever identifier.
   * @return true if the projectExpectedStudyRelatedAllianceLever was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyRelatedAllianceLever(long projectExpectedStudyRelatedAllianceLeverId);


  /**
   * This method validate if the projectExpectedStudyRelatedAllianceLever identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyRelatedAllianceLeverID is a projectExpectedStudyRelatedAllianceLever identifier.
   * @return true if the projectExpectedStudyRelatedAllianceLever exists, false otherwise.
   */
  public boolean existProjectExpectedStudyRelatedAllianceLever(long projectExpectedStudyRelatedAllianceLeverID);


  /**
   * This method gets a list of projectExpectedStudyRelatedAllianceLever that are active
   * 
   * @return a list from ProjectExpectedStudyRelatedAllianceLever null if no exist records
   */
  public List<ProjectExpectedStudyRelatedAllianceLever> findAll();


  /**
   * This method gets a projectExpectedStudyRelatedAllianceLever object by a given projectExpectedStudyRelatedAllianceLever identifier.
   * 
   * @param projectExpectedStudyRelatedAllianceLeverID is the projectExpectedStudyRelatedAllianceLever identifier.
   * @return a ProjectExpectedStudyRelatedAllianceLever object.
   */
  public ProjectExpectedStudyRelatedAllianceLever getProjectExpectedStudyRelatedAllianceLeverById(long projectExpectedStudyRelatedAllianceLeverID);

  /**
   * This method saves the information of the given projectExpectedStudyRelatedAllianceLever
   * 
   * @param projectExpectedStudyRelatedAllianceLever - is the projectExpectedStudyRelatedAllianceLever object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyRelatedAllianceLever was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyRelatedAllianceLever saveProjectExpectedStudyRelatedAllianceLever(ProjectExpectedStudyRelatedAllianceLever projectExpectedStudyRelatedAllianceLever);


}
