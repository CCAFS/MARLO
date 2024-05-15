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

import org.cgiar.ccafs.marlo.data.model.ProjectMilestoneDisaggregatedTarget;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectMilestoneDisaggregatedTargetManager {


  /**
   * This method removes a specific projectMilestoneDisaggregatedTarget value from the database.
   * 
   * @param projectMilestoneDisaggregatedTargetId is the projectMilestoneDisaggregatedTarget identifier.
   * @return true if the projectMilestoneDisaggregatedTarget was successfully deleted, false otherwise.
   */
  public void deleteProjectMilestoneDisaggregatedTarget(long projectMilestoneDisaggregatedTargetId);


  /**
   * This method validate if the projectMilestoneDisaggregatedTarget identify with the given id exists in the system.
   * 
   * @param projectMilestoneDisaggregatedTargetID is a projectMilestoneDisaggregatedTarget identifier.
   * @return true if the projectMilestoneDisaggregatedTarget exists, false otherwise.
   */
  public boolean existProjectMilestoneDisaggregatedTarget(long projectMilestoneDisaggregatedTargetID);


  /**
   * This method gets a list of projectMilestoneDisaggregatedTarget that are active
   * 
   * @return a list from ProjectMilestoneDisaggregatedTarget null if no exist records
   */
  public List<ProjectMilestoneDisaggregatedTarget> findAll();


  /**
   * This method gets a projectMilestoneDisaggregatedTarget object by a given projectMilestoneDisaggregatedTarget identifier.
   * 
   * @param projectMilestoneDisaggregatedTargetID is the projectMilestoneDisaggregatedTarget identifier.
   * @return a ProjectMilestoneDisaggregatedTarget object.
   */
  public ProjectMilestoneDisaggregatedTarget getProjectMilestoneDisaggregatedTargetById(long projectMilestoneDisaggregatedTargetID);

  /**
   * This method saves the information of the given projectMilestoneDisaggregatedTarget
   * 
   * @param projectMilestoneDisaggregatedTarget - is the projectMilestoneDisaggregatedTarget object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectMilestoneDisaggregatedTarget was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectMilestoneDisaggregatedTarget saveProjectMilestoneDisaggregatedTarget(ProjectMilestoneDisaggregatedTarget projectMilestoneDisaggregatedTarget);


}
