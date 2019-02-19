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

import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;

import java.util.List;


public interface ProjectLp6ContributionManager {


  /**
   * This method removes a specific ProjectLp6Contribution value from the database.
   * 
   * @param ProjectLp6ContributionId is the ProjectLp6Contribution identifier.
   * @return true if the ProjectLp6Contribution was successfully deleted, false otherwise.
   */
  public void deleteProjectLp6Contribution(long projectLp6ContributionId);


  /**
   * This method validate if the ProjectLp6Contribution identify with the given id exists in the system.
   * 
   * @param projectLp6ContributionId is a projectScope identifier.
   * @return true if the ProjectLp6Contribution exists, false otherwise.
   */
  public boolean existProjectLp6Contribution(long projectLp6ContributionId);


  /**
   * This method gets a list of ProjectLp6Contribution that are active
   * 
   * @return a list from ProjectLp6Contribution null if no exist records
   */
  public List<ProjectLp6Contribution> findAll();


  /**
   * This method gets a ProjectLp6Contribution object by a given ProjectLp6Contribution identifier.
   * 
   * @param projectScopeID is the projectScope identifier.
   * @return a ProjectLp6Contribution object.
   */
  public ProjectLp6Contribution getProjectLp6ContributionById(long projectLp6ContributionId);

  /**
   * This method saves the information of the given projectScope
   * 
   * @param ProjectLp6Contribution - is the ProjectLp6Contribution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the ProjectLp6Contribution
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectLp6Contribution saveProjectLp6Contribution(ProjectLp6Contribution projectLp6Contribution);


}
