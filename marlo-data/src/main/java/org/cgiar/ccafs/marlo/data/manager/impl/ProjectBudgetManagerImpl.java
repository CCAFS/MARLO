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

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectBudgetManagerImpl implements ProjectBudgetManager {


  private ProjectBudgetDAO projectBudgetDAO;
  private PhaseDAO phaseDAO;
  private ProjectDAO projectDAO;


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

  /**
   * Clone budget from budget to new projectBudgetAdd
   * 
   * @param projectBudgetAdd
   * @param budget
   * @param phase
   */

  public void cloneBudget(ProjectBudget projectBudgetAdd, ProjectBudget budget, Phase phase) {
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

  /**
   * Clone budget from budget to new projectBudgetAdd with no budget and year of the phase
   * 
   * @param projectBudgetAdd
   * @param budget
   * @param phase
   */
  public void cloneBudgetInBlank(ProjectBudget projectBudgetAdd, ProjectBudget budget, Phase phase) {
    projectBudgetAdd.setPhase(phase);
    projectBudgetAdd.setProject(projectDAO.find(budget.getProject().getId()));
    projectBudgetAdd.setAmount(0.0);
    projectBudgetAdd.setBudgetType(budget.getBudgetType());
    projectBudgetAdd.setFundingSource(budget.getFundingSource());
    projectBudgetAdd.setGenderPercentage(0.0);
    projectBudgetAdd.setGenderValue(0.0);
    projectBudgetAdd.setInstitution(budget.getInstitution());
    projectBudgetAdd.setYear(budget.getYear() + 1);
  }

  @Override
  public ProjectBudget copyProjectBudget(ProjectBudget projectBudget, Phase phase) {
    // Check if budget doesn't exists yet
    List<ProjectBudget> budgets = phase.getProjectBudgets().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projectBudget.getProject().getId().longValue()
        && c.getFundingSource().getId().equals(projectBudget.getFundingSource().getId())
        && c.getYear() == projectBudget.getYear() && c.getPhase() != null
        && c.getInstitution().getId().equals(projectBudget.getInstitution().getId()))
      .collect(Collectors.toList());
    if (budgets.isEmpty()) {
      ProjectBudget budgetAdd = new ProjectBudget();
      this.cloneBudget(budgetAdd, projectBudget, phase);
      budgetAdd = projectBudgetDAO.save(budgetAdd);
      return budgetAdd;
    }
    return null;
  }

  public void deletBudgetPhase(Phase next, long projecID, ProjectBudget projectBudget) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectBudget> budgets = phase.getProjectBudgets().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
        && c.getFundingSource().getId().equals(projectBudget.getFundingSource().getId())
        && c.getYear() >= projectBudget.getYear() && c.getPhase() != null
        && c.getInstitution().getId().equals(projectBudget.getInstitution().getId()))
      .collect(Collectors.toList());
    for (ProjectBudget projectBudgetDB : budgets) {
      projectBudgetDB.setActive(false);
      projectBudgetDAO.save(projectBudgetDB);
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
    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.deletBudgetPhase(upkeepPhase, projectBudget.getProject().getId(), projectBudget);
    // }
    // }
    // }

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
  public List<ProjectBudget> findBudgetByInstitutionProjectAndPhase(Long institutionId, Long projectId, Long phaseId) {
    return this.projectBudgetDAO.findBudgetByInstitutionProjectAndPhase(institutionId.longValue(),
      projectId.longValue(), phaseId.longValue());
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
  public List<ProjectBudget> getProjectBudgetByPhaseAndYear(long institutionID, int year, long budgetTypeId,
    long projectId, long fundingSourceId, long idPhase) {
    return projectBudgetDAO.getProjectBudgetByPhaseAndYear(institutionID, year, budgetTypeId, projectId,
      fundingSourceId, idPhase);
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

    // Save budget of current year
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

    // Save budgets of current_year+1 with budget 0
    List<ProjectBudget> budgetsNextYear = phase.getProjectBudgets().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
        && c.getFundingSource().getId().equals(projectBudget.getFundingSource().getId())
        && c.getYear() == projectBudget.getYear() + 1 && c.getPhase() != null
        && c.getInstitution().getId().equals(projectBudget.getInstitution().getId()))
      .collect(Collectors.toList());
    if (budgetsNextYear.isEmpty()) {
      ProjectBudget budgetAdd = new ProjectBudget();
      this.cloneBudgetInBlank(budgetAdd, projectBudget, phase);
      projectBudgetDAO.save(budgetAdd);
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
    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.saveBudgetPhase(upkeepPhase, projectBudget.getProject().getId(), projectBudget);
    // }
    // }
    // }
    return resultBudget;
  }

}
