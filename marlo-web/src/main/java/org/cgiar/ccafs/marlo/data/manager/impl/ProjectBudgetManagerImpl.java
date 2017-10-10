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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectBudgetManagerImpl implements ProjectBudgetManager {


  private ProjectBudgetDAO projectBudgetDAO;
  // Managers


  @Inject
  public ProjectBudgetManagerImpl(ProjectBudgetDAO projectBudgetDAO) {
    this.projectBudgetDAO = projectBudgetDAO;


  }

  @Override
  public String amountByBudgetType(long institutionId, int year, long budgetType, long projectId, Integer coFinancing,
    long idPhase) {
    return projectBudgetDAO.amountByBudgetType(institutionId, year, budgetType, projectId, coFinancing, idPhase);
  }

  @Override
  public boolean deleteProjectBudget(long projectBudgetId) {

    return projectBudgetDAO.deleteProjectBudget(projectBudgetId);
  }

  @Override
  public boolean existProjectBudget(long projectBudgetID) {

    return projectBudgetDAO.existProjectBudget(projectBudgetID);
  }

  @Override
  public List<ProjectBudget> findAll() {

    return projectBudgetDAO.findAll();

  }

  @Override
  public List<ProjectBudget> getByParameters(long institutionID, int year, long budgetTypeId, long projectId,
    Integer coFinancing, long idPhase) {
    return projectBudgetDAO.getByParameters(institutionID, year, budgetTypeId, projectId, coFinancing, idPhase);
  }

  @Override
  public ProjectBudget getProjectBudgetById(long projectBudgetID) {

    return projectBudgetDAO.find(projectBudgetID);
  }

  @Override
  public double getReaminingAmount(long fundingSourceID, int year, double budget, long idPhase) {
    String amount = projectBudgetDAO.amountByFundingSource(fundingSourceID, year, idPhase);
    if (amount != null) {
      double dAmount = Double.parseDouble(amount);
      return budget - dAmount;
    }
    return 0;
  }

  @Override
  public double getTotalBudget(long projetId, long phaseID, int type, int year) {
    return projectBudgetDAO.getTotalBudget(projetId, phaseID, type, year);
  }


  @Override
  public long saveProjectBudget(ProjectBudget projectBudget) {

    return projectBudgetDAO.save(projectBudget);
  }
}
