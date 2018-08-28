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
import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectLeverageDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectLeverageManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectLeverage;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectLeverageManagerImpl implements ProjectLeverageManager {


  private ProjectLeverageDAO projectLeverageDAO;
  private ProjectDAO projectDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectLeverageManagerImpl(ProjectLeverageDAO projectLeverageDAO, PhaseDAO phaseDAO, ProjectDAO projectDAO) {
    this.projectLeverageDAO = projectLeverageDAO;
    this.phaseDAO = phaseDAO;
    this.projectDAO = projectDAO;
  }

  private void cloneLeverage(ProjectLeverage projectLeverageAdd, ProjectLeverage projectLeverage, Phase phase) {
    projectLeverageAdd.setComposeID(projectLeverage.getComposeID());
    projectLeverageAdd.setPhase(phase);
    projectLeverageAdd.setProject(projectDAO.find(projectLeverage.getProject().getId()));
    projectLeverageAdd.setTitle(projectLeverage.getTitle());
    projectLeverageAdd.setBudget(projectLeverage.getBudget());
    projectLeverageAdd.setIpProgram(projectLeverage.getIpProgram());
    projectLeverageAdd.setCrpProgram(projectLeverage.getCrpProgram());
    projectLeverageAdd.setInstitution(projectLeverage.getInstitution());
    projectLeverageAdd.setYear(projectLeverage.getYear());
  }

  @Override
  public void deleteProjectLeverage(long projectLeverageId) {

    ProjectLeverage projectLeverage = this.getProjectLeverageById(projectLeverageId);
    projectLeverage.setActive(false);
    projectLeverage = projectLeverageDAO.save(projectLeverage);
    Phase currentPhase = phaseDAO.find(projectLeverage.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (projectLeverage.getPhase().getNext() != null) {
        this.deletLeveragePhase(projectLeverage.getPhase().getNext(), projectLeverage.getProject().getId(),
          projectLeverage);
      }
    }

    projectLeverageDAO.deleteProjectLeverage(projectLeverageId);
  }

  private void deletLeveragePhase(Phase next, Long projecID, ProjectLeverage projectLeverage) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      List<ProjectLeverage> leverages =
        phase.getProjectLeverages().stream().filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && projectLeverage.getComposeID().equals(c.getComposeID())).collect(Collectors.toList());
      for (ProjectLeverage leverageDB : leverages) {
        leverageDB.setActive(false);
        projectLeverageDAO.save(leverageDB);
      }
    }

    if (phase.getNext() != null) {
      this.deletLeveragePhase(phase.getNext(), projecID, projectLeverage);

    }
  }

  @Override
  public boolean existProjectLeverage(long projectLeverageID) {

    return projectLeverageDAO.existProjectLeverage(projectLeverageID);
  }

  @Override
  public List<ProjectLeverage> findAll() {

    return projectLeverageDAO.findAll();

  }

  @Override
  public ProjectLeverage getProjectLeverageById(long projectLeverageID) {

    return projectLeverageDAO.find(projectLeverageID);
  }

  @Override
  public ProjectLeverage saveProjectLeverage(ProjectLeverage projectLeverage) {
    ProjectLeverage resultProjectLeverage = projectLeverageDAO.save(projectLeverage);
    Phase currentPhase = phaseDAO.find(projectLeverage.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (projectLeverage.getPhase().getNext() != null) {
        this.saveProjectLeveragePhase(projectLeverage.getPhase().getNext(), projectLeverage.getProject().getId(),
          projectLeverage);
      }
    }

    return resultProjectLeverage;
  }

  private void saveProjectLeveragePhase(Phase next, Long projecID, ProjectLeverage projectLeverage) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {

      List<ProjectLeverage> leverages =
        phase.getProjectLeverages().stream().filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID
          && c.getComposeID().equals(projectLeverage.getComposeID())).collect(Collectors.toList());

      if (leverages.isEmpty()) {
        ProjectLeverage projectLeverageAdd = new ProjectLeverage();
        this.cloneLeverage(projectLeverageAdd, projectLeverage, phase);
        projectLeverageAdd = projectLeverageDAO.save(projectLeverageAdd);
        if (projectLeverageAdd.getComposeID() == null || projectLeverageAdd.getComposeID().length() == 0) {
          projectLeverage.setComposeID(projectLeverage.getProject().getId() + "-" + projectLeverageAdd.getId());
          projectLeverageAdd.setComposeID(projectLeverage.getComposeID());
          projectLeverageDAO.save(projectLeverage);
          projectLeverageDAO.save(projectLeverageAdd);
        }

      } else {
        ProjectLeverage projectLeverageAdd = leverages.get(0);
        this.cloneLeverage(projectLeverageAdd, projectLeverage, phase);
        projectLeverageDAO.save(projectLeverageAdd);
      }
    }

    if (phase.getNext() != null) {
      this.saveProjectLeveragePhase(phase.getNext(), projecID, projectLeverage);
    }

  }


}
