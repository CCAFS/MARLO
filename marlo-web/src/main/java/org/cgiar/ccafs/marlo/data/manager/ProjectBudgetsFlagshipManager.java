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

import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ProjectBudgetsFlagshipManager {


  /**
   * This method removes a specific projectBudgetsFlagship value from the database.
   * 
   * @param projectBudgetsFlagshipId is the projectBudgetsFlagship identifier.
   * @return true if the projectBudgetsFlagship was successfully deleted, false otherwise.
   */
  public void deleteProjectBudgetsFlagship(long projectBudgetsFlagshipId);


  /**
   * This method validate if the projectBudgetsFlagship identify with the given id exists in the system.
   * 
   * @param projectBudgetsFlagshipID is a projectBudgetsFlagship identifier.
   * @return true if the projectBudgetsFlagship exists, false otherwise.
   */
  public boolean existProjectBudgetsFlagship(long projectBudgetsFlagshipID);


  /**
   * This method gets a list of projectBudgetsFlagship that are active
   * 
   * @return a list from ProjectBudgetsFlagship null if no exist records
   */
  public List<ProjectBudgetsFlagship> findAll();


  /**
   * This method gets a projectBudgetsFlagship object by a given projectBudgetsFlagship identifier.
   * 
   * @param projectBudgetsFlagshipID is the projectBudgetsFlagship identifier.
   * @return a ProjectBudgetsFlagship object.
   */
  public ProjectBudgetsFlagship getProjectBudgetsFlagshipById(long projectBudgetsFlagshipID);

  /**
   * This method saves the information of the given projectBudgetsFlagship
   * 
   * @param projectBudgetsFlagship - is the projectBudgetsFlagship object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectBudgetsFlagship was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectBudgetsFlagship saveProjectBudgetsFlagship(ProjectBudgetsFlagship projectBudgetsFlagship);


}
