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

import org.cgiar.ccafs.marlo.data.model.ProjectNextuser;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ProjectNextuserManager {


  /**
   * This method removes a specific projectNextuser value from the database.
   * 
   * @param projectNextuserId is the projectNextuser identifier.
   * @return true if the projectNextuser was successfully deleted, false otherwise.
   */
  public void deleteProjectNextuser(long projectNextuserId);


  /**
   * This method validate if the projectNextuser identify with the given id exists in the system.
   * 
   * @param projectNextuserID is a projectNextuser identifier.
   * @return true if the projectNextuser exists, false otherwise.
   */
  public boolean existProjectNextuser(long projectNextuserID);


  /**
   * This method gets a list of projectNextuser that are active
   * 
   * @return a list from ProjectNextuser null if no exist records
   */
  public List<ProjectNextuser> findAll();


  /**
   * This method gets a projectNextuser object by a given projectNextuser identifier.
   * 
   * @param projectNextuserID is the projectNextuser identifier.
   * @return a ProjectNextuser object.
   */
  public ProjectNextuser getProjectNextuserById(long projectNextuserID);

  /**
   * This method saves the information of the given projectNextuser
   * 
   * @param projectNextuser - is the projectNextuser object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectNextuser was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectNextuser saveProjectNextuser(ProjectNextuser projectNextuser);


}
