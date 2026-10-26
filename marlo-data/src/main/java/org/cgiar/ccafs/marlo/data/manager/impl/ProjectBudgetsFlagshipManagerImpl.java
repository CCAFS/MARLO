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
import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetsFlagshipDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetsFlagshipManager;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectBudgetsFlagshipManagerImpl implements ProjectBudgetsFlagshipManager {


  private ProjectBudgetsFlagshipDAO projectBudgetsFlagshipDAO;
  // Managers
  private PhaseDAO phaseDAO;
  private ProjectDAO projectDAO;


  @Inject
  public ProjectBudgetsFlagshipManagerImpl(ProjectBudgetsFlagshipDAO projectBudgetsFlagshipDAO, PhaseDAO phaseDAO,
    ProjectDAO projectDAO) {
    this.projectBudgetsFlagshipDAO = projectBudgetsFlagshipDAO;
    this.phaseDAO = phaseDAO;
    this.projectDAO = projectDAO;
  }

  /**
   * Clone the ProjectBudgetsFlagship data information of a phase
   * 
   * @param projectBudgetAdd
   * @param budget
   * @param phase
   */
  public void cloneBudget(ProjectBudgetsFlagship projectBudgetAdd, ProjectBudgetsFlagship budget, Phase phase) {
    projectBudgetAdd.setPhase(phase);
    projectBudgetAdd.setProject(projectDAO.find(budget.getProject().getId()));
    projectBudgetAdd.setAmount(budget.getAmount());
    projectBudgetAdd.setBudgetType(budget.getBudgetType());
    projectBudgetAdd.setYear(budget.getYear());
    projectBudgetAdd.setCrpProgram(budget.getCrpProgram());
  }


  @Override
  public ProjectBudgetsFlagship copyProjectBudgetsFlagship(ProjectBudgetsFlagship projectBudgetsFlagship, Phase phase) {
    ProjectBudgetsFlagship budgetAdd = new ProjectBudgetsFlagship();
    this.cloneBudget(budgetAdd, projectBudgetsFlagship, phase);
    budgetAdd = projectBudgetsFlagshipDAO.save(budgetAdd);
    return budgetAdd;
  }

  /**
   * Deactivate, in the given phase and every later one, the copies of a budget that a Planning save replicated.
   *
   * @param next the first phase to clean
   * @param projectID the project of the budget
   * @param projectBudget the budget deactivated in the current phase
   */
  public void deleteBudgetPhase(Phase next, long projectID, ProjectBudgetsFlagship projectBudget) {
    if (next == null || next.getId() == null) {
      return;
    }
    Phase phase = phaseDAO.find(next.getId());
    if (phase == null || phase.getProjectBudgetsFlagships() == null) {
      return;
    }
    List<ProjectBudgetsFlagship> budgets = phase.getProjectBudgetsFlagships().stream()
      .filter(c -> c.isActive() && this.isCopyOf(c, projectID, projectBudget)).collect(Collectors.toList());
    for (ProjectBudgetsFlagship budgetDB : budgets) {
      budgetDB.setActive(false);
      projectBudgetsFlagshipDAO.save(budgetDB);
    }

    if (phase.getNext() != null) {
      this.deleteBudgetPhase(phase.getNext(), projectID, projectBudget);
    }
  }

  @Override
  @Transactional
  public void deleteProjectBudgetsFlagship(long projectBudgetsFlagshipId) {
    projectBudgetsFlagshipDAO.deleteProjectBudgetsFlagship(projectBudgetsFlagshipId);

    // Planning replicates every save to the later phases, so a deletion has to reach the same copies.
    ProjectBudgetsFlagship projectBudget = projectBudgetsFlagshipDAO.find(projectBudgetsFlagshipId);
    if (projectBudget == null || projectBudget.getPhase() == null || projectBudget.getPhase().getId() == null
      || projectBudget.getProject() == null || projectBudget.getProject().getId() == null) {
      return;
    }
    Phase currentPhase = phaseDAO.find(projectBudget.getPhase().getId());
    if (currentPhase != null && APConstants.PLANNING.equals(currentPhase.getDescription())
      && currentPhase.getNext() != null) {
      this.deleteBudgetPhase(currentPhase.getNext(), projectBudget.getProject().getId(), projectBudget);
    }
  }

  @Override
  public boolean existProjectBudgetsFlagship(long projectBudgetsFlagshipID) {

    return projectBudgetsFlagshipDAO.existProjectBudgetsFlagship(projectBudgetsFlagshipID);
  }

  @Override
  public List<ProjectBudgetsFlagship> findAll() {

    return projectBudgetsFlagshipDAO.findAll();

  }

  @Override
  public ProjectBudgetsFlagship getProjectBudgetsFlagshipById(long projectBudgetsFlagshipID) {

    return projectBudgetsFlagshipDAO.find(projectBudgetsFlagshipID);
  }

  /**
   * Tell whether a budget of a later phase is the replicated copy of the given one: same project, flagship, year and
   * budget type. The budget type is part of the key because a flagship holds one row per type and year.
   */
  private boolean isCopyOf(ProjectBudgetsFlagship candidate, long projectID, ProjectBudgetsFlagship budget) {
    return candidate.getPhase() != null && candidate.getProject() != null
      && candidate.getProject().getId() != null && candidate.getProject().getId().longValue() == projectID
      && candidate.getYear() == budget.getYear() && this.sameId(candidate.getCrpProgram(), budget.getCrpProgram())
      && this.sameId(candidate.getBudgetType(), budget.getBudgetType());
  }

  private boolean sameId(CrpProgram a, CrpProgram b) {
    return a != null && b != null && a.getId() != null && a.getId().equals(b.getId());
  }

  private boolean sameId(BudgetType a, BudgetType b) {
    return a != null && b != null && a.getId() != null && a.getId().equals(b.getId());
  }

  /**
   * Save the ProjectBudgetsFlagship data in the next ProjectBudgetsFlagship Phase.
   * 
   * @param next
   * @param projecID
   * @param projectBudget
   */
  public void saveBudgetPhase(Phase next, long projecID, ProjectBudgetsFlagship projectBudget) {
    if (next == null || next.getId() == null) {
      return;
    }
    Phase phase = phaseDAO.find(next.getId());
    if (phase == null || phase.getProjectBudgetsFlagships() == null) {
      return;
    }
    List<ProjectBudgetsFlagship> budgets = phase.getProjectBudgetsFlagships().stream()
      .filter(c -> c.isActive() && this.isCopyOf(c, projecID, projectBudget)).collect(Collectors.toList());
    if (budgets.isEmpty()) {
      ProjectBudgetsFlagship budgetAdd = new ProjectBudgetsFlagship();
      this.cloneBudget(budgetAdd, projectBudget, phase);
      projectBudgetsFlagshipDAO.save(budgetAdd);
    } else {
      ProjectBudgetsFlagship budgetAdd = budgets.get(0);
      this.cloneBudget(budgetAdd, projectBudget, phase);
      projectBudgetsFlagshipDAO.save(budgetAdd);
    }

    if (phase.getNext() != null) {
      this.saveBudgetPhase(phase.getNext(), projecID, projectBudget);
    }


  }

  @Override
  @Transactional
  public ProjectBudgetsFlagship saveProjectBudgetsFlagship(ProjectBudgetsFlagship projectBudgetsFlagship) {
    ProjectBudgetsFlagship resultProjectBudget = projectBudgetsFlagshipDAO.save(projectBudgetsFlagship);
    // Only a budget of a known phase and project can be replicated
    if (projectBudgetsFlagship.getPhase() == null || projectBudgetsFlagship.getPhase().getId() == null
      || projectBudgetsFlagship.getProject() == null || projectBudgetsFlagship.getProject().getId() == null) {
      return resultProjectBudget;
    }
    Phase currentPhase = phaseDAO.find(projectBudgetsFlagship.getPhase().getId());
    if (currentPhase != null && APConstants.PLANNING.equals(currentPhase.getDescription())) {
      if (projectBudgetsFlagship.getPhase().getNext() != null) {
        this.saveBudgetPhase(projectBudgetsFlagship.getPhase().getNext(), projectBudgetsFlagship.getProject().getId(),
          projectBudgetsFlagship);
      }
    }
    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.saveBudgetPhase(upkeepPhase, projectBudgetsFlagship.getProject().getId(), projectBudgetsFlagship);
    // }
    // }
    // }
    return resultProjectBudget;
  }


}
