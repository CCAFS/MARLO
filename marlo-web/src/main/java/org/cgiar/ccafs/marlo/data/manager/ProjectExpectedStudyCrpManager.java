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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectExpectedStudyCrpManager {


  /**
   * This method removes a specific projectExpectedStudyCrp value from the database.
   * 
   * @param projectExpectedStudyCrpId is the projectExpectedStudyCrp identifier.
   * @return true if the projectExpectedStudyCrp was successfully deleted, false otherwise.
   */
  public void deleteProjectExpectedStudyCrp(long projectExpectedStudyCrpId);


  /**
   * This method validate if the projectExpectedStudyCrp identify with the given id exists in the system.
   * 
   * @param projectExpectedStudyCrpID is a projectExpectedStudyCrp identifier.
   * @return true if the projectExpectedStudyCrp exists, false otherwise.
   */
  public boolean existProjectExpectedStudyCrp(long projectExpectedStudyCrpID);


  /**
   * This method gets a list of projectExpectedStudyCrp that are active
   * 
   * @return a list from ProjectExpectedStudyCrp null if no exist records
   */
  public List<ProjectExpectedStudyCrp> findAll();


  /**
   * This method gets a projectExpectedStudyCrp object by a given projectExpectedStudyCrp identifier.
   * 
   * @param projectExpectedStudyCrpID is the projectExpectedStudyCrp identifier.
   * @return a ProjectExpectedStudyCrp object.
   */
  public ProjectExpectedStudyCrp getProjectExpectedStudyCrpById(long projectExpectedStudyCrpID);

  /**
   * This method saves the information of the given projectExpectedStudyCrp
   * 
   * @param projectExpectedStudyCrp - is the projectExpectedStudyCrp object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectExpectedStudyCrp was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectExpectedStudyCrp saveProjectExpectedStudyCrp(ProjectExpectedStudyCrp projectExpectedStudyCrp);


}
