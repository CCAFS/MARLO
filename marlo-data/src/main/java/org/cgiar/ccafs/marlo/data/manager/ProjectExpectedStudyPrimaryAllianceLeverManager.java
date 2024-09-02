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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPrimaryAllianceLever;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyPrimaryAllianceLeverManager {


  /**
   * This method removes a specific projectExpectedStudyPrimaryAllianceLever value from the database.
   * 
   * @param projectExpectedStudyPrimaryAllianceLeverId is the projectExpectedStudyPrimaryAllianceLever identifier.
   * @return true if the projectExpectedStudyPrimaryAllianceLever was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyPrimaryAllianceLever(long projectExpectedStudyPrimaryAllianceLeverId);


  /**
   * This method validate if the projectExpectedStudyPrimaryAllianceLever identify with the given id exists in the
   * system.
   * 
   * @param projectExpectedStudyPrimaryAllianceLeverID is a projectExpectedStudyPrimaryAllianceLever identifier.
   * @return true if the projectExpectedStudyPrimaryAllianceLever exists, false otherwise.
   */
  public boolean existProjectExpectedStudyPrimaryAllianceLever(long projectExpectedStudyPrimaryAllianceLeverID);


  /**
   * This method gets a list of projectExpectedStudyPrimaryAllianceLever that are active
   * 
   * @return a list from ProjectExpectedStudyPrimaryAllianceLever null if no exist records
   */
  public List<ProjectExpectedStudyPrimaryAllianceLever> findAll();


  /**
   * This method gets a projectExpectedStudyPrimaryAllianceLever object by a given
   * projectExpectedStudyPrimaryAllianceLever identifier.
   * 
   * @param projectExpectedStudyPrimaryAllianceLeverID is the projectExpectedStudyPrimaryAllianceLever identifier.
   * @return a ProjectExpectedStudyPrimaryAllianceLever object.
   */
  public ProjectExpectedStudyPrimaryAllianceLever
    getProjectExpectedStudyPrimaryAllianceLeverById(long projectExpectedStudyPrimaryAllianceLeverID);


  /**
   * This method saves the information of the given projectExpectedStudyPrimaryAllianceLever
   * 
   * @param projectExpectedStudyPrimaryAllianceLever - is the projectExpectedStudyPrimaryAllianceLever object with the
   *        new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectExpectedStudyPrimaryAllianceLever was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyPrimaryAllianceLever saveProjectExpectedStudyPrimaryAllianceLever(
    ProjectExpectedStudyPrimaryAllianceLever projectExpectedStudyPrimaryAllianceLever);


}
