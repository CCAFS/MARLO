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

import org.cgiar.ccafs.marlo.data.model.CenterMonitoringMilestone;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ICenterMonitoringMilestoneManager {


  /**
   * This method removes a specific monitoringMilestone value from the database.
   * 
   * @param monitoringMilestoneId is the monitoringMilestone identifier.
   * @return true if the monitoringMilestone was successfully deleted, false otherwise.
   */
  public void deleteMonitoringMilestone(long monitoringMilestoneId);


  /**
   * This method validate if the monitoringMilestone identify with the given id exists in the system.
   * 
   * @param monitoringMilestoneID is a monitoringMilestone identifier.
   * @return true if the monitoringMilestone exists, false otherwise.
   */
  public boolean existMonitoringMilestone(long monitoringMilestoneID);


  /**
   * This method gets a list of monitoringMilestone that are active
   * 
   * @return a list from CenterMonitoringMilestone null if no exist records
   */
  public List<CenterMonitoringMilestone> findAll();


  /**
   * This method gets a monitoringMilestone object by a given monitoringMilestone identifier.
   * 
   * @param monitoringMilestoneID is the monitoringMilestone identifier.
   * @return a CenterMonitoringMilestone object.
   */
  public CenterMonitoringMilestone getMonitoringMilestoneById(long monitoringMilestoneID);

  /**
   * This method gets a list of monitoringMilestones belongs of the user
   * 
   * @param userId - the user id
   * @return List of MonitoringMilestones or null if the user is invalid or not have roles.
   */
  public List<CenterMonitoringMilestone> getMonitoringMilestonesByUserId(Long userId);

  /**
   * This method saves the information of the given monitoringMilestone
   * 
   * @param monitoringMilestone - is the monitoringMilestone object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the monitoringMilestone was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterMonitoringMilestone saveMonitoringMilestone(CenterMonitoringMilestone monitoringMilestone);


}
