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


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectBudgetManagerImpl implements ProjectBudgetManager {


  private ProjectBudgetDAO projectBudgetDAO;
  private PhaseDAO phaseDAO;
  private ProjectDAO projectDAO;

  // Managers


  @Inject
  public ProjectBudgetManagerImpl(ProjectBudgetDAO projectBudgetDAO, PhaseDAO phaseDAO, ProjectDAO projectDAO) {
    this.projectBudgetDAO = projectBudgetDAO;
    this.phaseDAO = phaseDAO;
    this.projectDAO = projectDAO;

  }

  @Override
  public String amountByBudgetType(long institutionId, int year, long budgetType, long projectId, Integer coFinancing,
    long idPhase) {
    return projectBudgetDAO.amountByBudgetType(institutionId, year, budgetType, projectId, coFinancing, idPhase);
  }

  public void cloneBudget(ProjectBudget projectBudgetAdd, ProjectBudget budget, Phase phase) {
    projectBudgetAdd.setActive(true);
    projectBudgetAdd.setActiveSince(new Date());
    projectBudgetAdd.setModificationJustification(budget.getModificationJustification());
    projectBudgetAdd.setModifiedBy(budget.getCreatedBy());
    projectBudgetAdd.setCreatedBy(budget.getCreatedBy());
    projectBudgetAdd.setPhase(phase);
    projectBudgetAdd.setProject(projectDAO.find(budget.getProject().getId()));
    projectBudgetAdd.setAmount(budget.getAmount());
    projectBudgetAdd.setBudgetType(budget.getBudgetType());
    projectBudgetAdd.setFundingSource(budget.getFundingSource());
    projectBudgetAdd.setGenderPercentage(budget.getGenderPercentage());
    projectBudgetAdd.setGenderValue(budget.getGenderValue());
    projectBudgetAdd.setInstitution(budget.getInstitution());
    projectBudgetAdd.setYear(budget.getYear());


  }

  @Override
  public ProjectBudget copyProjectBudget(ProjectBudget projectBudget, Phase phase) {
    ProjectBudget budgetAdd = new ProjectBudget();
    this.cloneBudget(budgetAdd, projectBudget, phase);
    budgetAdd = projectBudgetDAO.save(budgetAdd);
    return budgetAdd;
  }

  public void deletBudgetPhase(Phase next, long projecID, ProjectBudget projectBudget) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<ProjectBudget> budgets = phase.getProjectBudgets().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && c.getFundingSource().getId().equals(projectBudget.getFundingSource().getId())
          && c.getYear() == projectBudget.getYear() && c.getPhase() != null
          && c.getInstitution().getId().equals(projectBudget.getInstitution().getId()))
        .collect(Collectors.toList());
      for (ProjectBudget projectBudgetDB : budgets) {
        projectBudgetDB.setActive(false);
        projectBudgetDAO.save(projectBudgetDB);
      }
    }
    if (phase.getNext() != null) {
      this.deletBudgetPhase(phase.getNext(), projecID, projectBudget);

    }
  }

  @Override
  public void deleteProjectBudget(long projectBudgetId) {


    ProjectBudget projectBudget = this.getProjectBudgetById(projectBudgetId);
    projectBudget.setActive(false);
    projectBudgetDAO.save(projectBudget);
    Phase currentPhase = phaseDAO.find(projectBudget.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectBudget.getPhase().getNext() != null) {
        this.deletBudgetPhase(projectBudget.getPhase().getNext(), projectBudget.getProject().getId(), projectBudget);
      }
    }

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

  public void saveBudgetPhase(Phase next, long projecID, ProjectBudget projectBudget) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<ProjectBudget> budgets = phase.getProjectBudgets().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && c.getFundingSource().getId().equals(projectBudget.getFundingSource().getId())
          && c.getYear() == projectBudget.getYear() && c.getPhase() != null
          && c.getInstitution().getId().equals(projectBudget.getInstitution().getId()))
        .collect(Collectors.toList());
      if (budgets.isEmpty()) {
        ProjectBudget budgetAdd = new ProjectBudget();
        this.cloneBudget(budgetAdd, projectBudget, phase);
        projectBudgetDAO.save(budgetAdd);
      } else {
        ProjectBudget budgetAdd = budgets.get(0);
        this.cloneBudget(budgetAdd, projectBudget, phase);
        projectBudgetDAO.save(budgetAdd);
      }

    }
    if (phase.getNext() != null) {
      this.saveBudgetPhase(phase.getNext(), projecID, projectBudget);
    }


  }

  @Override
  public ProjectBudget saveProjectBudget(ProjectBudget projectBudget) {

    ProjectBudget resultBudget = projectBudgetDAO.save(projectBudget);
    Phase currentPhase = phaseDAO.find(projectBudget.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectBudget.getPhase().getNext() != null) {
        this.saveBudgetPhase(projectBudget.getPhase().getNext(), projectBudget.getProject().getId(), projectBudget);
      }
    }
    return resultBudget;
  }
}
