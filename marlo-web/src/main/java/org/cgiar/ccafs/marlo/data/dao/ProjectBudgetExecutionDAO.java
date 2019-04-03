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

import org.cgiar.ccafs.marlo.data.model.ProjectBudgetExecution;

import java.util.List;


public interface ProjectBudgetExecutionDAO {

  /**
   * This method removes a specific projectBudgetExecution value from the database.
   * 
   * @param projectBudgetExecutionId is the projectBudgetExecution identifier.
   * @return true if the projectBudgetExecution was successfully deleted, false otherwise.
   */
  public void deleteProjectBudgetExecution(long projectBudgetExecutionId);

  /**
   * This method validate if the projectBudgetExecution identify with the given id exists in the system.
   * 
   * @param projectBudgetExecutionID is a projectBudgetExecution identifier.
   * @return true if the projectBudgetExecution exists, false otherwise.
   */
  public boolean existProjectBudgetExecution(long projectBudgetExecutionID);

  /**
   * This method gets a projectBudgetExecution object by a given projectBudgetExecution identifier.
   * 
   * @param projectBudgetExecutionID is the projectBudgetExecution identifier.
   * @return a ProjectBudgetExecution object.
   */
  public ProjectBudgetExecution find(long id);

  /**
   * This method gets a list of projectBudgetExecution that are active
   * 
   * @return a list from ProjectBudgetExecution null if no exist records
   */
  public List<ProjectBudgetExecution> findAll();

  /**
   * This method gets a list of projectBudgetExecution by project year and phase that are active
   * 
   * @return a list from ProjectBudgetExecution null if no exist records
   */
  public List<ProjectBudgetExecution> findAllByParameters(long projectId, int year, long phaseId);

  /**
   * This method gets a list of projectBudgetExecution by project year phase and budgetType that are active
   * 
   * @return a list from ProjectBudgetExecution null if no exist records
   */
  public List<ProjectBudgetExecution> findAllByParameters(long projectId, int year, long phaseId, long budgetTypeId);

  /**
   * This method saves the information of the given projectBudgetExecution
   * 
   * @param projectBudgetExecution - is the projectBudgetExecution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectBudgetExecution
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectBudgetExecution save(ProjectBudgetExecution projectBudgetExecution);
}
