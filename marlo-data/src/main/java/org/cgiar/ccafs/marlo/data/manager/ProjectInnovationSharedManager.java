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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationShared;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationSharedManager {


  /**
   * This method removes a specific projectInnovationShared value from the database.
   * 
   * @param projectInnovationSharedId is the projectInnovationShared identifier.
   * @return true if the projectInnovationShared was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationShared(long projectInnovationSharedId);


  /**
   * This method validate if the projectInnovationShared identify with the given id exists in the system.
   * 
   * @param projectInnovationSharedID is a projectInnovationShared identifier.
   * @return true if the projectInnovationShared exists, false otherwise.
   */
  public boolean existProjectInnovationShared(long projectInnovationSharedID);


  /**
   * This method gets a list of projectInnovationShared that are active
   * 
   * @return a list from ProjectInnovationShared null if no exist records
   */
  public List<ProjectInnovationShared> findAll();


  public List<ProjectInnovationShared> getByProjectAndPhase(long projectId, long phaseId);

  /**
   * This method gets a projectInnovationShared object by a given projectInnovationShared identifier.
   * 
   * @param projectInnovationSharedID is the projectInnovationShared identifier.
   * @return a ProjectInnovationShared object.
   */
  public ProjectInnovationShared getProjectInnovationSharedById(long projectInnovationSharedID);

  /**
   * This method saves the information of the given projectInnovationShared
   * 
   * @param projectInnovationShared - is the projectInnovationShared object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationShared
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationShared saveProjectInnovationShared(ProjectInnovationShared projectInnovationShared);


}
