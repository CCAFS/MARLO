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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationProjectOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationProjectOutcomeManagerImpl implements ProjectInnovationProjectOutcomeManager {


  private ProjectInnovationProjectOutcomeDAO projectInnovationProjectOutcomeDAO;
  private ProjectInnovationManager projectInnovationManager;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectInnovationProjectOutcomeManagerImpl(
    ProjectInnovationProjectOutcomeDAO projectInnovationProjectOutcomeDAO, PhaseDAO phaseDAO,
    ProjectInnovationManager projectInnovationManager) {
    this.projectInnovationProjectOutcomeDAO = projectInnovationProjectOutcomeDAO;
    this.phaseDAO = phaseDAO;
    this.projectInnovationManager = projectInnovationManager;
  }

  @Override
  public void deleteProjectInnovationProjectOutcome(long projectInnovationProjectOutcomeId, long phaseId) {
    ProjectInnovationProjectOutcome projectInnovationProjectOutcome =
      projectInnovationProjectOutcomeDAO.find(projectInnovationProjectOutcomeId);
    projectInnovationProjectOutcomeDAO.deleteProjectInnovationProjectOutcome(projectInnovationProjectOutcomeId);
    Phase phase = phaseDAO.find(phaseId);
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.deleteProjectInnovationProjectOutcomePhase(projectInnovationProjectOutcome.getPhase().getNext(),
        projectInnovationProjectOutcome.getProjectInnovation().getId(), projectInnovationProjectOutcome);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectInnovationProjectOutcomePhase(upkeepPhase,
            projectInnovationProjectOutcome.getProjectInnovation().getId(), projectInnovationProjectOutcome);
        }
      }
    }
  }

  public void deleteProjectInnovationProjectOutcomePhase(Phase next, long innovationId,
    ProjectInnovationProjectOutcome projectInnovationProjectOutcome) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationProjectOutcome> innovationOutcomes = projectInnovationProjectOutcomeDAO.findAll().stream()
      .filter(c -> c.getProjectInnovation().getId().equals(innovationId) && c.getPhase().getId().equals(phase.getId())
        && c.getProjectOutcome().getId().equals(projectInnovationProjectOutcome.getProjectOutcome().getId()))
      .collect(Collectors.toList());

    // Get project outcomes phases
    ProjectInnovation innovation = projectInnovationManager.getProjectInnovationById(innovationId);
    Project project = innovation.getProject();

    List<ProjectOutcome> projectOutcomes = phase.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == project.getId()
        && c.getCrpProgramOutcome().getComposeID()
          .equals(projectInnovationProjectOutcome.getProjectOutcome().getCrpProgramOutcome().getComposeID()))
      .collect(Collectors.toList());

    if (projectOutcomes != null && !projectOutcomes.isEmpty() && projectOutcomes.get(0) != null) {
      if (innovationOutcomes.isEmpty()) {
        ProjectInnovationProjectOutcome innovationOutcomeAdd = new ProjectInnovationProjectOutcome();
        if (projectInnovationProjectOutcomeDAO.findAll().stream()
          .filter(po -> po.getPhase().getId().equals(phase.getId())
            && po.getProjectOutcome().getId().equals(projectOutcomes.get(0).getId())
            && po.getProjectInnovation().getId().equals(innovation.getId()))
          .collect(Collectors.toList()) != null) {
          List<ProjectInnovationProjectOutcome> listTemp = projectInnovationProjectOutcomeDAO.findAll().stream()
            .filter(po -> po.getPhase().getId().equals(phase.getId())
              && po.getProjectOutcome().getId().equals(projectOutcomes.get(0).getId())
              && po.getProjectInnovation().getId().equals(innovation.getId()))
            .collect(Collectors.toList());
          if (listTemp != null && !listTemp.isEmpty() && listTemp.get(0) != null) {
            innovationOutcomeAdd = listTemp.get(0);
            projectInnovationProjectOutcomeDAO.deleteProjectInnovationProjectOutcome(innovationOutcomeAdd.getId());
          }
        }
      }
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationProjectOutcomePhase(phase.getNext(), innovationId, projectInnovationProjectOutcome);
    }
  }

  @Override
  public boolean existProjectInnovationProjectOutcome(long projectInnovationProjectOutcomeID) {

    return projectInnovationProjectOutcomeDAO.existProjectInnovationProjectOutcome(projectInnovationProjectOutcomeID);
  }

  @Override
  public List<ProjectInnovationProjectOutcome> findAll() {

    return projectInnovationProjectOutcomeDAO.findAll();

  }

  @Override
  public ProjectInnovationProjectOutcome
    getProjectInnovationProjectOutcomeById(long projectInnovationProjectOutcomeID) {

    return projectInnovationProjectOutcomeDAO.find(projectInnovationProjectOutcomeID);
  }

  @Override
  public ProjectInnovationProjectOutcome
    saveProjectInnovationProjectOutcome(ProjectInnovationProjectOutcome projectInnovationProjectOutcome) {
    ProjectInnovationProjectOutcome innovationOutcome =
      projectInnovationProjectOutcomeDAO.save(projectInnovationProjectOutcome);
    Phase phase = phaseDAO.find(innovationOutcome.getPhase().getId());
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectInnovationProjectOutcomePhase(innovationOutcome.getPhase().getNext(),
        innovationOutcome.getProjectInnovation().getId(), projectInnovationProjectOutcome);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectInnovationProjectOutcomePhase(upkeepPhase, innovationOutcome.getProjectInnovation().getId(),
            projectInnovationProjectOutcome);
        }
      }
    }
    return projectInnovationProjectOutcomeDAO.save(projectInnovationProjectOutcome);
  }

  public void saveProjectInnovationProjectOutcomePhase(Phase next, long innovationId,
    ProjectInnovationProjectOutcome projectInnovationProjectOutcome) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationProjectOutcome> innovationOutcomes = projectInnovationProjectOutcomeDAO.findAll().stream()
      .filter(c -> c.getProjectInnovation().getId().equals(innovationId) && c.getPhase().getId().equals(phase.getId())
        && c.getProjectOutcome().getId().equals(projectInnovationProjectOutcome.getProjectOutcome().getId()))
      .collect(Collectors.toList());

    // Get project outcomes phases
    ProjectInnovation innovation = projectInnovationManager.getProjectInnovationById(innovationId);
    Project project = innovation.getProject();

    List<ProjectOutcome> projectOutcomes = phase.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == project.getId()
        && c.getCrpProgramOutcome().getComposeID()
          .equals(projectInnovationProjectOutcome.getProjectOutcome().getCrpProgramOutcome().getComposeID()))
      .collect(Collectors.toList());

    if (projectOutcomes != null && !projectOutcomes.isEmpty() && projectOutcomes.get(0) != null) {
      if (innovationOutcomes.isEmpty()) {
        ProjectInnovationProjectOutcome innovationProjectOutcomeAdd = new ProjectInnovationProjectOutcome();
        innovationProjectOutcomeAdd.setProjectInnovation(projectInnovationProjectOutcome.getProjectInnovation());
        innovationProjectOutcomeAdd.setPhase(phase);
        innovationProjectOutcomeAdd.setProjectOutcome(projectOutcomes.get(0));
        projectInnovationProjectOutcomeDAO.save(innovationProjectOutcomeAdd);
      }
    }

    if (phase.getNext() != null) {
      this.saveProjectInnovationProjectOutcomePhase(phase.getNext(), innovationId, projectInnovationProjectOutcome);
    }
  }
}
