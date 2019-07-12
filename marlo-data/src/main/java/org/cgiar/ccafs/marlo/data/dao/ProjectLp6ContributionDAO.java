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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;

import java.util.List;


public interface ProjectLp6ContributionDAO {

  /**
   * This method removes a specific projectLp6Contribution value from the database.
   * 
   * @param projectLp6ContributionId is the projectScope identifier.
   * @return true if the projectLp6Contribution was successfully deleted, false otherwise.
   */
  public void deleteProjectLp6Contribution(long projectLp6ContributionId);

  /**
   * This method validate if the projectLp6Contribution identify with the given id exists in the system.
   * 
   * @param projectLp6ContributionID is a projectLp6Contribution identifier.
   * @return true if the projectLp6Contribution exists, false otherwise.
   */
  public boolean existProjectLp6Contribution(long projectLp6ContributionId);

  /**
   * This method gets a projectLp6Contribution object by a given projectLp6Contribution identifier.
   * 
   * @param projectLp6ContributionID is the projectLp6Contribution identifier.
   * @return a projectLp6Contribution object.
   */
  public ProjectLp6Contribution find(long id);

  /**
   * This method gets a list of projectLp6Contribution that are active
   * 
   * @return a list from projectLp6Contribution null if no exist records
   */
  public List<ProjectLp6Contribution> findAll();


  /**
   * This method saves the information of the given projectScope
   * 
   * @param projectLp6Contribution - is the projectLp6Contribution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectLp6Contribution
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectLp6Contribution save(ProjectLp6Contribution projectLp6Contribution);
}
