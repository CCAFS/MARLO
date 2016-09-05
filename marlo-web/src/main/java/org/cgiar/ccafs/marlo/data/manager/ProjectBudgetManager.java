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

import org.cgiar.ccafs.marlo.data.manager.impl.ProjectBudgetManagerImpl;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(ProjectBudgetManagerImpl.class)
public interface ProjectBudgetManager {


  /**
   * This method removes a specific projectBudget value from the database.
   * 
   * @param projectBudgetId is the projectBudget identifier.
   * @return true if the projectBudget was successfully deleted, false otherwise.
   */
  public boolean deleteProjectBudget(long projectBudgetId);


  /**
   * This method validate if the projectBudget identify with the given id exists in the system.
   * 
   * @param projectBudgetID is a projectBudget identifier.
   * @return true if the projectBudget exists, false otherwise.
   */
  public boolean existProjectBudget(long projectBudgetID);


  /**
   * This method gets a list of projectBudget that are active
   * 
   * @return a list from ProjectBudget null if no exist records
   */
  public List<ProjectBudget> findAll();


  /**
   * This method gets a projectBudget object by a given projectBudget identifier.
   * 
   * @param projectBudgetID is the projectBudget identifier.
   * @return a ProjectBudget object.
   */
  public ProjectBudget getProjectBudgetById(long projectBudgetID);

  /**
   * This method saves the information of the given projectBudget
   * 
   * @param projectBudget - is the projectBudget object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectBudget was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveProjectBudget(ProjectBudget projectBudget);


}
