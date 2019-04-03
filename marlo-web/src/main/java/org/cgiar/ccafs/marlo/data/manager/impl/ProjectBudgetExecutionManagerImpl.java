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
import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetExecutionDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetExecutionManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetExecution;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectBudgetExecutionManagerImpl implements ProjectBudgetExecutionManager {


  private ProjectBudgetExecutionDAO projectBudgetExecutionDAO;
  private PhaseDAO phaseDAO;
  private ProjectDAO projectDAO;


  @Inject
  public ProjectBudgetExecutionManagerImpl(ProjectBudgetExecutionDAO projectBudgetExecutionDAO, PhaseDAO phaseDAO,
    ProjectDAO projectDAO) {
    this.projectBudgetExecutionDAO = projectBudgetExecutionDAO;
    this.phaseDAO = phaseDAO;
    this.projectDAO = projectDAO;
  }

  /**
   * Clone budget from budgetExecution to new projectBudgetExecutionAdd
   * 
   * @param projectBudgetExecutionAdd
   * @param budgetExecution
   * @param phase
   */

  public void cloneBudgetExecution(ProjectBudgetExecution projectBudgetExecutionAdd,
    ProjectBudgetExecution budgetExecution, Phase phase) {
    projectBudgetExecutionAdd.setProject(projectDAO.find(budgetExecution.getProject().getId()));
    projectBudgetExecutionAdd.setInstitution(budgetExecution.getInstitution());
    projectBudgetExecutionAdd.setPhase(phase);
    projectBudgetExecutionAdd.setBudgetType(budgetExecution.getBudgetType());
    projectBudgetExecutionAdd.setYear(budgetExecution.getYear());
    projectBudgetExecutionAdd.setActualExpenditure(budgetExecution.getActualExpenditure());
  }

  private void deleteBudgetExecutionPhase(Phase next, Long projectID, ProjectBudgetExecution projectBudgetExecution) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectBudgetExecution> budgetExecutions = phase.getProjectBudgetExecutions().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projectID
        && c.getYear() == projectBudgetExecution.getYear() && c.getPhase() != null
        && c.getInstitution().getId().equals(projectBudgetExecution.getInstitution().getId())
        && c.getBudgetType().getId().equals(projectBudgetExecution.getBudgetType().getId()))
      .collect(Collectors.toList());
    for (ProjectBudgetExecution projectBudgetExecutionDB : budgetExecutions) {
      projectBudgetExecutionDAO.deleteProjectBudgetExecution(projectBudgetExecutionDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteBudgetExecutionPhase(phase.getNext(), projectID, projectBudgetExecution);
    }

  }

  @Override
  public void deleteProjectBudgetExecution(long projectBudgetExecutionId) {

    projectBudgetExecutionDAO.deleteProjectBudgetExecution(projectBudgetExecutionId);
    ProjectBudgetExecution projectBudgetExecution = this.getProjectBudgetExecutionById(projectBudgetExecutionId);
    Phase currentPhase = phaseDAO.find(projectBudgetExecution.getPhase().getId());

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteBudgetExecutionPhase(currentPhase.getNext(), projectBudgetExecution.getProject().getId(),
        projectBudgetExecution);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteBudgetExecutionPhase(upkeepPhase, projectBudgetExecution.getProject().getId(),
            projectBudgetExecution);
        }
      }
    }

  }

  @Override
  public boolean existProjectBudgetExecution(long projectBudgetExecutionID) {

    return projectBudgetExecutionDAO.existProjectBudgetExecution(projectBudgetExecutionID);
  }

  @Override
  public List<ProjectBudgetExecution> findAll() {

    return projectBudgetExecutionDAO.findAll();

  }

  @Override
  public List<ProjectBudgetExecution> findAllByParameters(long projectId, int year, long phaseId) {
    return projectBudgetExecutionDAO.findAllByParameters(projectId, year, phaseId);
  }

  @Override
  public List<ProjectBudgetExecution> findAllByParameters(long projectId, int year, long phaseId, long budgetTypeId) {
    return projectBudgetExecutionDAO.findAllByParameters(projectId, year, phaseId);
  }


  @Override
  public ProjectBudgetExecution getProjectBudgetExecutionById(long projectBudgetExecutionID) {

    return projectBudgetExecutionDAO.find(projectBudgetExecutionID);
  }

  @Override
  public double getTotalProjectBudgetExecution(long projectId, int year, long phaseId) {
    double total = 0.0;
    List<ProjectBudgetExecution> projectBudgetExecutions = this.findAllByParameters(projectId, year, phaseId);
    if (projectBudgetExecutions != null && !projectBudgetExecutions.isEmpty()) {
      for (ProjectBudgetExecution projectBudgetExecution : projectBudgetExecutions) {
        total += projectBudgetExecution.getActualExpenditure();
      }
    }
    return total;
  }

  @Override
  public double getTotalProjectBudgetExecution(long projectId, int year, long phaseId, long budgetTypeId) {
    double total = 0.0;
    List<ProjectBudgetExecution> projectBudgetExecutions =
      this.findAllByParameters(projectId, year, phaseId, budgetTypeId);
    if (projectBudgetExecutions != null && !projectBudgetExecutions.isEmpty()) {
      for (ProjectBudgetExecution projectBudgetExecution : projectBudgetExecutions) {
        total += projectBudgetExecution.getActualExpenditure();
      }
    }
    return total;
  }

  private void saveBudgetExecutionPhase(Phase next, Long projectID, ProjectBudgetExecution projectBudgetExecution) {
    Phase phase = phaseDAO.find(next.getId());

    // Save BudgetExecution of current year
    List<ProjectBudgetExecution> budgetExecutions = phase.getProjectBudgetExecutions().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projectID
        && c.getYear() == projectBudgetExecution.getYear() && c.getPhase() != null
        && c.getInstitution().getId().equals(projectBudgetExecution.getInstitution().getId())
        && c.getBudgetType().getId().equals(projectBudgetExecution.getBudgetType().getId()))
      .collect(Collectors.toList());
    if (budgetExecutions.isEmpty()) {
      ProjectBudgetExecution budgetExecutionAdd = new ProjectBudgetExecution();
      this.cloneBudgetExecution(budgetExecutionAdd, projectBudgetExecution, phase);
      projectBudgetExecutionDAO.save(budgetExecutionAdd);
    } else {
      ProjectBudgetExecution budgetExecutionAdd = budgetExecutions.get(0);
      this.cloneBudgetExecution(budgetExecutionAdd, projectBudgetExecution, phase);
      projectBudgetExecutionDAO.save(budgetExecutionAdd);
    }

    if (phase.getNext() != null) {
      this.saveBudgetExecutionPhase(phase.getNext(), projectID, projectBudgetExecution);
    }

  }

  @Override
  public ProjectBudgetExecution saveProjectBudgetExecution(ProjectBudgetExecution projectBudgetExecution) {
    ProjectBudgetExecution resultBudgetExecution = projectBudgetExecutionDAO.save(projectBudgetExecution);
    Phase currentPhase = phaseDAO.find(projectBudgetExecution.getPhase().getId());

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.saveBudgetExecutionPhase(currentPhase.getNext(), projectBudgetExecution.getProject().getId(),
        projectBudgetExecution);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveBudgetExecutionPhase(upkeepPhase, projectBudgetExecution.getProject().getId(),
            projectBudgetExecution);
        }
      }
    }

    return resultBudgetExecution;
  }


}
