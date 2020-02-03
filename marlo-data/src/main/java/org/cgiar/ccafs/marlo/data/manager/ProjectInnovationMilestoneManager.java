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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationMilestone;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationMilestoneManager {


  /**
   * This method removes a specific projectInnovationMilestone value from the database.
   * 
   * @param projectInnovationMilestoneId is the projectInnovationMilestone identifier.
   * @return true if the projectInnovationMilestone was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationMilestone(long projectInnovationMilestoneId);


  /**
   * This method validate if the projectInnovationMilestone identify with the given id exists in the system.
   * 
   * @param projectInnovationMilestoneID is a projectInnovationMilestone identifier.
   * @return true if the projectInnovationMilestone exists, false otherwise.
   */
  public boolean existProjectInnovationMilestone(long projectInnovationMilestoneID);


  /**
   * This method gets a list of projectInnovationMilestone that are active
   * 
   * @return a list from ProjectInnovationMilestone null if no exist records
   */
  public List<ProjectInnovationMilestone> findAll();


  /**
   * This method gets a projectInnovationMilestone object by a given projectInnovationMilestone identifier.
   * 
   * @param projectInnovationMilestoneID is the projectInnovationMilestone identifier.
   * @return a ProjectInnovationMilestone object.
   */
  public ProjectInnovationMilestone getProjectInnovationMilestoneById(long projectInnovationMilestoneID);

  /**
   * This method saves the information of the given projectInnovationMilestone
   * 
   * @param projectInnovationMilestone - is the projectInnovationMilestone object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationMilestone was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationMilestone saveProjectInnovationMilestone(ProjectInnovationMilestone projectInnovationMilestone);


}
