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
import org.cgiar.ccafs.marlo.data.dao.ProjectCenterOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectCenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectCenterOutcome;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectCenterOutcomeManagerImpl implements ProjectCenterOutcomeManager {


  private ProjectCenterOutcomeDAO projectCenterOutcomeDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectCenterOutcomeManagerImpl(ProjectCenterOutcomeDAO projectCenterOutcomeDAO, PhaseDAO phaseDAO) {
    this.projectCenterOutcomeDAO = projectCenterOutcomeDAO;
    this.phaseDAO = phaseDAO;

  }

  @Override
  public void deleteProjectCenterOutcome(long projectCenterOutcomeId) {
    ProjectCenterOutcome projectCenterOutcome = this.getProjectCenterOutcomeById(projectCenterOutcomeId);
    Phase currentPhase = phaseDAO.find(projectCenterOutcome.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectCenterOutcome.getPhase().getNext() != null) {
        this.deleteProjectCenterOutcomePhase(currentPhase, projectCenterOutcomeId, projectCenterOutcome);
      }
    }
    // projectCenterOutcomeDAO.deleteProjectCenterOutcome(projectCenterOutcomeId);
  }

  public void deleteProjectCenterOutcomePhase(Phase next, long projectCenterOutcomeID,
    ProjectCenterOutcome projectCenterOutcome) {
    Phase phase = phaseDAO.find(next.getId());
    List<ProjectCenterOutcome> projectCenterOutcomeList = phase.getProjectCenterOutcomes().stream()
      .filter(c -> c.isActive() && c.getProject().getId() == projectCenterOutcome.getProject().getId()
        && c.getCenterOutcome().getId() == projectCenterOutcome.getCenterOutcome().getId())
      .collect(Collectors.toList());
    for (ProjectCenterOutcome projectCenterOutcomeDB : projectCenterOutcomeList) {
      projectCenterOutcomeDAO.deleteProjectCenterOutcome(projectCenterOutcomeDB.getId());
    }
    if (phase.getNext() != null) {
      this.deleteProjectCenterOutcomePhase(next.getNext(), projectCenterOutcomeID, projectCenterOutcome);
    }
  }

  @Override
  public boolean existProjectCenterOutcome(long projectCenterOutcomeID) {

    return projectCenterOutcomeDAO.existProjectCenterOutcome(projectCenterOutcomeID);
  }

  @Override
  public List<ProjectCenterOutcome> findAll() {

    return projectCenterOutcomeDAO.findAll();

  }

  @Override
  public ProjectCenterOutcome getProjectCenterOutcomeById(long projectCenterOutcomeID) {

    return projectCenterOutcomeDAO.find(projectCenterOutcomeID);
  }

  public void saveInfoPhase(Phase next, long projectCenterOutcomeID, ProjectCenterOutcome projectCenterOutcome) {
    Phase phase = phaseDAO.find(next.getId());
    List<ProjectCenterOutcome> projectCenterOutcomeList = phase.getProjectCenterOutcomes().stream()
      .filter(c -> c.isActive() && c.getId().longValue() == projectCenterOutcomeID).collect(Collectors.toList());
    if (projectCenterOutcomeList.isEmpty()) {
      ProjectCenterOutcome projectCenterOutcomeAdd = new ProjectCenterOutcome();
      projectCenterOutcomeAdd.setProject(projectCenterOutcome.getProject());
      projectCenterOutcomeAdd.setCenterOutcome(projectCenterOutcome.getCenterOutcome());
      projectCenterOutcomeAdd.setPhase(phase);
      projectCenterOutcomeDAO.save(projectCenterOutcomeAdd);
    } else {

    }
    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectCenterOutcomeID, projectCenterOutcome);
    }
  }

  @Override
  public ProjectCenterOutcome saveProjectCenterOutcome(ProjectCenterOutcome projectCenterOutcome) {
    Phase phase = projectCenterOutcome.getPhase();
    ProjectCenterOutcome projectCenterOutcomeDB = projectCenterOutcomeDAO.save(projectCenterOutcome);

    if (phase.getDescription().equals(APConstants.PLANNING)) {
      if (phase.getNext() != null) {
        this.saveInfoPhase(phase.getNext(), projectCenterOutcome.getId(), projectCenterOutcome);
      }
    }
    return projectCenterOutcomeDB;
  }


}
