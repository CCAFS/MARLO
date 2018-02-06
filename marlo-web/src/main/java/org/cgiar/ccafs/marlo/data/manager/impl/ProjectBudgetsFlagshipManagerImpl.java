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
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

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
    projectBudgetAdd.setActive(true);
    projectBudgetAdd.setActiveSince(new Date());
    projectBudgetAdd.setModificationJustification(budget.getModificationJustification());
    projectBudgetAdd.setModifiedBy(budget.getCreatedBy());
    projectBudgetAdd.setCreatedBy(budget.getCreatedBy());
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

  @Override
  public void deleteProjectBudgetsFlagship(long projectBudgetsFlagshipId) {

    projectBudgetsFlagshipDAO.deleteProjectBudgetsFlagship(projectBudgetsFlagshipId);
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
   * Save the ProjectBudgetsFlagship data in the next ProjectBudgetsFlagship Phase.
   * 
   * @param next
   * @param projecID
   * @param projectBudget
   */
  public void saveBudgetPhase(Phase next, long projecID, ProjectBudgetsFlagship projectBudget) {
    Phase phase = phaseDAO.find(next.getId());
    List<ProjectBudgetsFlagship> budgets = phase.getProjectBudgetsFlagships().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
        && c.getCrpProgram().getId().equals(projectBudget.getCrpProgram().getId())
        && c.getYear() == projectBudget.getYear() && c.getPhase() != null)
      .collect(Collectors.toList());
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
  public ProjectBudgetsFlagship saveProjectBudgetsFlagship(ProjectBudgetsFlagship projectBudgetsFlagship) {
    ProjectBudgetsFlagship resultProjectBudget = projectBudgetsFlagshipDAO.save(projectBudgetsFlagship);
    Phase currentPhase = phaseDAO.find(projectBudgetsFlagship.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectBudgetsFlagship.getPhase().getNext() != null) {
        this.saveBudgetPhase(projectBudgetsFlagship.getPhase().getNext(), projectBudgetsFlagship.getProject().getId(),
          projectBudgetsFlagship);
      }
    }
    return resultProjectBudget;
  }


}
