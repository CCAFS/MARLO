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

import org.cgiar.ccafs.marlo.data.model.ProjectDeliverableShared;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectDeliverableSharedManager {


  /**
   * This method removes a specific projectDeliverableShared value from the database.
   * 
   * @param projectDeliverableSharedId is the projectDeliverableShared identifier.
   * @return true if the projectDeliverableShared was successfully deleted, false otherwise.
   */
  public void deleteProjectDeliverableShared(long projectDeliverableSharedId);


  /**
   * This method validate if the projectDeliverableShared identify with the given id exists in the system.
   * 
   * @param projectDeliverableSharedID is a projectDeliverableShared identifier.
   * @return true if the projectDeliverableShared exists, false otherwise.
   */
  public boolean existProjectDeliverableShared(long projectDeliverableSharedID);


  /**
   * This method gets a list of projectDeliverableShared that are active
   * 
   * @return a list from ProjectDeliverableShared null if no exist records
   */
  public List<ProjectDeliverableShared> findAll();


  public List<ProjectDeliverableShared> getByDeliverable(long deliverableId, long phaseId);

  public List<ProjectDeliverableShared> getByPhase(long phaseId);

  public List<ProjectDeliverableShared> getByProjectAndPhase(long projectId, long phaseId);

  /**
   * This method gets a projectDeliverableShared object by a given projectDeliverableShared identifier.
   * 
   * @param projectDeliverableSharedID is the projectDeliverableShared identifier.
   * @return a ProjectDeliverableShared object.
   */
  public ProjectDeliverableShared getProjectDeliverableSharedById(long projectDeliverableSharedID);


  /**
   * This method saves the information of the given projectDeliverableShared
   * 
   * @param projectDeliverableShared - is the projectDeliverableShared object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectDeliverableShared
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectDeliverableShared saveProjectDeliverableShared(ProjectDeliverableShared projectDeliverableShared);


}
