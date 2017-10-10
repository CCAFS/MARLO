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

import org.cgiar.ccafs.marlo.data.dao.mysql.ProjectBudgetMySQLDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(ProjectBudgetMySQLDAO.class)
public interface ProjectBudgetDAO {

  /**
   * Gets the budget amount for a specific institution , year and budget type.
   * 
   * @param institutionId
   * @param year
   * @param budgetType
   * @param projectId
   * @return
   */
  public String amountByBudgetType(long institutionId, int year, long budgetType, long projectId, Integer coFinancing,
    long idPhase);

  /**
   * Gets the budget amount for a specific funding source and year
   * 
   * @param fundingSourceID - Funding Source ID
   * @param year - Specific year
   * @return a Budget amount.
   */
  public String amountByFundingSource(long fundingSourceID, int year, long idPhase);

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
   * This method gets a projectBudget object by a given projectBudget identifier.
   * 
   * @param projectBudgetID is the projectBudget identifier.
   * @return a ProjectBudget object.
   */
  public ProjectBudget find(long id);


  /**
   * This method gets a list of projectBudget that are active
   * 
   * @return a list from ProjectBudget null if no exist records
   */
  public List<ProjectBudget> findAll();

  /**
   * gets a list of ProjectBudget of a specific parameters
   * 
   * @param institutionID
   * @param year
   * @param budgetTypeId
   * @param projectId
   * @return a list from ProjectBudget null if no exist records
   */
  public List<ProjectBudget> getByParameters(long institutionID, int year, long budgetTypeId, long projectId,
    Integer coFinancing, long idPhase);

  public double getTotalBudget(long projetId, long phaseID, int type, int year);

  /**
   * This method saves the information of the given projectBudget
   * 
   * @param projectBudget - is the projectBudget object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectBudget was
   *         updated
   *         or -1 is some error occurred.
   */
  public long save(ProjectBudget projectBudget);
}
