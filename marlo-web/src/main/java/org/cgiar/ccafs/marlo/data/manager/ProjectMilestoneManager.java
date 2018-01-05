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

import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ProjectMilestoneManager {


  /**
   * This method removes a specific projectMilestone value from the database.
   * 
   * @param projectMilestoneId is the projectMilestone identifier.
   * @return true if the projectMilestone was successfully deleted, false otherwise.
   */
  public void deleteProjectMilestone(long projectMilestoneId);


  /**
   * This method validate if the projectMilestone identify with the given id exists in the system.
   * 
   * @param projectMilestoneID is a projectMilestone identifier.
   * @return true if the projectMilestone exists, false otherwise.
   */
  public boolean existProjectMilestone(long projectMilestoneID);


  /**
   * This method gets a list of projectMilestone that are active
   * 
   * @return a list from ProjectMilestone null if no exist records
   */
  public List<ProjectMilestone> findAll();


  /**
   * This method gets a projectMilestone object by a given projectMilestone identifier.
   * 
   * @param projectMilestoneID is the projectMilestone identifier.
   * @return a ProjectMilestone object.
   */
  public ProjectMilestone getProjectMilestoneById(long projectMilestoneID);

  /**
   * This method saves the information of the given projectMilestone
   * 
   * @param projectMilestone - is the projectMilestone object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectMilestone was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectMilestone saveProjectMilestone(ProjectMilestone projectMilestone);


}
