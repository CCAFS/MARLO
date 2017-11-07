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
import org.cgiar.ccafs.marlo.data.dao.ProjectBudgetsCluserActvityDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetsCluserActvityManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsCluserActvity;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectBudgetsCluserActvityManagerImpl implements ProjectBudgetsCluserActvityManager {


  private ProjectBudgetsCluserActvityDAO projectBudgetsCluserActvityDAO;
  // Managers
  private PhaseDAO phaseDAO;
  private ProjectDAO projectDAO;


  @Inject
  public ProjectBudgetsCluserActvityManagerImpl(ProjectBudgetsCluserActvityDAO projectBudgetsCluserActvityDAO,
    PhaseDAO phaseDAO, ProjectDAO projectDAO) {
    this.projectBudgetsCluserActvityDAO = projectBudgetsCluserActvityDAO;
    this.phaseDAO = phaseDAO;
    this.projectDAO = projectDAO;
  }

  public void cloneBudget(ProjectBudgetsCluserActvity projectBudgetAdd, ProjectBudgetsCluserActvity budget,
    Phase phase) {
    projectBudgetAdd.setActive(true);
    projectBudgetAdd.setActiveSince(new Date());
    projectBudgetAdd.setModificationJustification(budget.getModificationJustification());
    projectBudgetAdd.setModifiedBy(budget.getCreatedBy());
    projectBudgetAdd.setCreatedBy(budget.getCreatedBy());
    projectBudgetAdd.setPhase(phase);
    projectBudgetAdd.setProject(projectDAO.find(budget.getProject().getId()));
    projectBudgetAdd.setAmount(budget.getAmount());
    projectBudgetAdd.setBudgetType(budget.getBudgetType());
    projectBudgetAdd.setGenderPercentage(budget.getGenderPercentage());
    projectBudgetAdd.setYear(budget.getYear());
    projectBudgetAdd.setCrpClusterOfActivity(budget.getCrpClusterOfActivity());


  }

  public void deletBudgetPhase(Phase next, long projecID, ProjectBudgetsCluserActvity projectBudget) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<ProjectBudgetsCluserActvity> budgets = phase.getProjectBudgetsActivities().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && c.getCrpClusterOfActivity().getId().equals(projectBudget.getCrpClusterOfActivity().getId())
          && c.getYear() == projectBudget.getYear() && c.getPhase() != null)
        .collect(Collectors.toList());
      for (ProjectBudgetsCluserActvity projectBudgetDB : budgets) {
        projectBudgetDB.setActive(false);
        projectBudgetsCluserActvityDAO.save(projectBudgetDB);
      }
    }
    if (phase.getNext() != null) {
      this.deletBudgetPhase(phase.getNext(), projecID, projectBudget);

    }
  }

  @Override
  public boolean deleteProjectBudgetsCluserActvity(long projectBudgetsCluserActvityId) {

    boolean resultProjectBudget =
      projectBudgetsCluserActvityDAO.deleteProjectBudgetsCluserActvity(projectBudgetsCluserActvityId);
    ProjectBudgetsCluserActvity projectBudgetsCluserActvity =
      this.getProjectBudgetsCluserActvityById(projectBudgetsCluserActvityId);
    Phase currentPhase = phaseDAO.find(projectBudgetsCluserActvity.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectBudgetsCluserActvity.getPhase().getNext() != null) {
        this.deletBudgetPhase(projectBudgetsCluserActvity.getPhase().getNext(),
          projectBudgetsCluserActvity.getProject().getId(), projectBudgetsCluserActvity);
      }
    }
    return resultProjectBudget;
  }

  @Override
  public boolean existProjectBudgetsCluserActvity(long projectBudgetsCluserActvityID) {

    return projectBudgetsCluserActvityDAO.existProjectBudgetsCluserActvity(projectBudgetsCluserActvityID);
  }

  @Override
  public List<ProjectBudgetsCluserActvity> findAll() {

    return projectBudgetsCluserActvityDAO.findAll();

  }

  @Override
  public ProjectBudgetsCluserActvity getProjectBudgetsCluserActvityById(long projectBudgetsCluserActvityID) {

    return projectBudgetsCluserActvityDAO.find(projectBudgetsCluserActvityID);
  }

  public void saveBudgetPhase(Phase next, long projecID, ProjectBudgetsCluserActvity projectBudget) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<ProjectBudgetsCluserActvity> budgets = phase.getProjectBudgetsActivities().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && c.getCrpClusterOfActivity().getId().equals(projectBudget.getCrpClusterOfActivity().getId())
          && c.getYear() == projectBudget.getYear() && c.getPhase() != null)
        .collect(Collectors.toList());
      if (budgets.isEmpty()) {
        ProjectBudgetsCluserActvity budgetAdd = new ProjectBudgetsCluserActvity();
        this.cloneBudget(budgetAdd, projectBudget, phase);
        projectBudgetsCluserActvityDAO.save(budgetAdd);
      } else {
        ProjectBudgetsCluserActvity budgetAdd = budgets.get(0);
        this.cloneBudget(budgetAdd, projectBudget, phase);
        projectBudgetsCluserActvityDAO.save(budgetAdd);
      }

    }
    if (phase.getNext() != null) {
      this.saveBudgetPhase(phase.getNext(), projecID, projectBudget);
    }


  }

  @Override
  public long saveProjectBudgetsCluserActvity(ProjectBudgetsCluserActvity projectBudgetsCluserActvity) {

    long resultProjectBudget = projectBudgetsCluserActvityDAO.save(projectBudgetsCluserActvity);
    Phase currentPhase = phaseDAO.find(projectBudgetsCluserActvity.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectBudgetsCluserActvity.getPhase().getNext() != null) {
        this.saveBudgetPhase(projectBudgetsCluserActvity.getPhase().getNext(),
          projectBudgetsCluserActvity.getProject().getId(), projectBudgetsCluserActvity);
      }
    }
    return resultProjectBudget;
  }

}
