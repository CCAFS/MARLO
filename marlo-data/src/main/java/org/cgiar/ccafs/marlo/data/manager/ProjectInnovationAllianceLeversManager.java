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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationAllianceLevers;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationAllianceLeversManager {


  /**
   * This method removes a specific projectInnovationAllianceLevers value from the database.
   * 
   * @param projectInnovationAllianceLeversId is the projectInnovationAllianceLevers identifier.
   * @return true if the projectInnovationAllianceLevers was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationAllianceLevers(long projectInnovationAllianceLeversId);


  /**
   * This method validate if the projectInnovationAllianceLevers identify with the given id exists in the system.
   * 
   * @param projectInnovationAllianceLeversID is a projectInnovationAllianceLevers identifier.
   * @return true if the projectInnovationAllianceLevers exists, false otherwise.
   */
  public boolean existProjectInnovationAllianceLevers(long projectInnovationAllianceLeversID);


  /**
   * This method gets a list of projectInnovationAllianceLevers that are active
   * 
   * @return a list from ProjectInnovationAllianceLevers null if no exist records
   */
  public List<ProjectInnovationAllianceLevers> findAll();


  /**
   * This method gets a projectInnovationAllianceLevers object by a given projectInnovationAllianceLevers identifier.
   * 
   * @param projectInnovationAllianceLeversID is the projectInnovationAllianceLevers identifier.
   * @return a ProjectInnovationAllianceLevers object.
   */
  public ProjectInnovationAllianceLevers getProjectInnovationAllianceLeversById(long projectInnovationAllianceLeversID);

  /**
   * This method saves the information of the given projectInnovationAllianceLevers
   * 
   * @param projectInnovationAllianceLevers - is the projectInnovationAllianceLevers object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationAllianceLevers was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationAllianceLevers saveProjectInnovationAllianceLevers(ProjectInnovationAllianceLevers projectInnovationAllianceLevers);


}
