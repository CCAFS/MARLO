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

import org.cgiar.ccafs.marlo.data.model.ProjectLp6ContributionDeliverable;

import java.util.List;


public interface ProjectLp6ContributionDeliverableManager {


  /**
   * This method removes a specific projectInnovationDeliverable value from the database.
   * 
   * @param projectInnovationDeliverableId is the projectInnovationDeliverable identifier.
   * @return true if the projectInnovationDeliverable was successfully deleted, false otherwise.
   */
  public void deleteProjectLp6ContributionDeliverable(long projectLp6ContributionDeliverableID);


  /**
   * This method validate if the projectInnovationDeliverable identify with the given id exists in the system.
   * 
   * @param projectInnovationDeliverableID is a projectInnovationDeliverable identifier.
   * @return true if the projectInnovationDeliverable exists, false otherwise.
   */
  public boolean existProjectLp6ContributionDeliverable(long projectLp6ContributionDeliverableID);


  /**
   * This method gets a list of projectInnovationDeliverable that are active
   * 
   * @return a list from ProjectInnovationDeliverable null if no exist records
   */
  public List<ProjectLp6ContributionDeliverable> findAll();


  /**
   * This method gets a projectInnovationDeliverable object by a given projectInnovationDeliverable identifier.
   * 
   * @param projectInnovationDeliverableID is the projectInnovationDeliverable identifier.
   * @return a ProjectInnovationDeliverable object.
   */
  public ProjectLp6ContributionDeliverable
    getProjectLp6ContributionDeliverableById(long projectLp6ContributionDeliverableID);

  /**
   * This method saves the information of the given projectInnovationDeliverable
   * 
   * @param projectInnovationDeliverable - is the projectInnovationDeliverable object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectInnovationDeliverable was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectLp6ContributionDeliverable
    saveProjectLp6ContributionDeliverable(ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable);


}
