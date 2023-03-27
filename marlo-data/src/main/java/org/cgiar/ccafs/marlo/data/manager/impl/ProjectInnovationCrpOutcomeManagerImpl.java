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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationCrpOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrpOutcome;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationCrpOutcomeManagerImpl implements ProjectInnovationCrpOutcomeManager {


  private ProjectInnovationCrpOutcomeDAO projectInnovationCrpOutcomeDAO;
  private ProjectInnovationManager projectInnovationManager;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectInnovationCrpOutcomeManagerImpl(ProjectInnovationCrpOutcomeDAO projectInnovationCrpOutcomeDAO,
    PhaseDAO phaseDAO, ProjectInnovationManager projectInnovationManager) {
    this.projectInnovationCrpOutcomeDAO = projectInnovationCrpOutcomeDAO;
    this.phaseDAO = phaseDAO;
    this.projectInnovationManager = projectInnovationManager;
  }

  @Override
  public void deleteProjectInnovationCrpOutcome(long projectInnovationCrpOutcomeId, long phaseId) {

    ProjectInnovationCrpOutcome projectInnovationCrpOutcome =
      projectInnovationCrpOutcomeDAO.find(projectInnovationCrpOutcomeId);
    projectInnovationCrpOutcomeDAO.deleteProjectInnovationCrpOutcome(projectInnovationCrpOutcomeId);
    Phase phase = phaseDAO.find(phaseId);
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.deleteProjectInnovationCrpOutcomePhase(projectInnovationCrpOutcome.getPhase().getNext(),
        projectInnovationCrpOutcome.getProjectInnovation().getId(), projectInnovationCrpOutcome);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectInnovationCrpOutcomePhase(upkeepPhase,
            projectInnovationCrpOutcome.getProjectInnovation().getId(), projectInnovationCrpOutcome);
        }
      }
    }
  }

  public void deleteProjectInnovationCrpOutcomePhase(Phase next, long innovationId,
    ProjectInnovationCrpOutcome projectInnovationCrpOutcome) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationCrpOutcome> innovationOutcomes = projectInnovationCrpOutcomeDAO.findAll().stream()
      .filter(c -> c.getProjectInnovation().getId().equals(innovationId) && c.getPhase().getId().equals(phase.getId())
        && c.getCrpOutcome().getId().equals(projectInnovationCrpOutcome.getCrpOutcome().getId()))
      .collect(Collectors.toList());

    // Get project outcomes phases
    ProjectInnovation innovation = projectInnovationManager.getProjectInnovationById(innovationId);
    Project project = innovation.getProject();

    List<CrpProgramOutcome> crpOutcomes = phase.getOutcomes().stream()
      .filter(c -> c.isActive() && c.getComposeID().equals(projectInnovationCrpOutcome.getCrpOutcome().getComposeID()))
      .collect(Collectors.toList());

    if (crpOutcomes != null && !crpOutcomes.isEmpty() && crpOutcomes.get(0) != null) {
      if (innovationOutcomes.isEmpty()) {
        ProjectInnovationCrpOutcome innovationOutcomeAdd = new ProjectInnovationCrpOutcome();
        if (projectInnovationCrpOutcomeDAO.findAll().stream()
          .filter(po -> po.getPhase().getId().equals(phase.getId())
            && po.getCrpOutcome().getId().equals(crpOutcomes.get(0).getId())
            && po.getProjectInnovation().getId().equals(innovation.getId()))
          .collect(Collectors.toList()) != null) {
          List<ProjectInnovationCrpOutcome> listTemp = projectInnovationCrpOutcomeDAO.findAll().stream()
            .filter(po -> po.getPhase().getId().equals(phase.getId())
              && po.getCrpOutcome().getId().equals(crpOutcomes.get(0).getId())
              && po.getProjectInnovation().getId().equals(innovation.getId()))
            .collect(Collectors.toList());
          if (listTemp != null && !listTemp.isEmpty() && listTemp.get(0) != null) {
            innovationOutcomeAdd = listTemp.get(0);
            projectInnovationCrpOutcomeDAO.deleteProjectInnovationCrpOutcome(innovationOutcomeAdd.getId());
          }
        }
      }
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationCrpOutcomePhase(phase.getNext(), innovationId, projectInnovationCrpOutcome);
    }
  }

  @Override
  public boolean existProjectInnovationCrpOutcome(long projectInnovationCrpOutcomeID) {

    return projectInnovationCrpOutcomeDAO.existProjectInnovationCrpOutcome(projectInnovationCrpOutcomeID);
  }

  @Override
  public List<ProjectInnovationCrpOutcome> findAll() {

    return projectInnovationCrpOutcomeDAO.findAll();

  }

  @Override
  public ProjectInnovationCrpOutcome getProjectInnovationCrpOutcomeById(long projectInnovationCrpOutcomeID) {

    return projectInnovationCrpOutcomeDAO.find(projectInnovationCrpOutcomeID);
  }

  @Override
  public ProjectInnovationCrpOutcome
    saveProjectInnovationCrpOutcome(ProjectInnovationCrpOutcome projectInnovationCrpOutcome) {
    ProjectInnovationCrpOutcome innovationOutcome = projectInnovationCrpOutcomeDAO.save(projectInnovationCrpOutcome);
    Phase phase = phaseDAO.find(innovationOutcome.getPhase().getId());
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectInnovationCrpOutcomePhase(innovationOutcome.getPhase().getNext(),
        innovationOutcome.getProjectInnovation().getId(), projectInnovationCrpOutcome);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectInnovationCrpOutcomePhase(upkeepPhase, innovationOutcome.getProjectInnovation().getId(),
            projectInnovationCrpOutcome);
        }
      }
    }
    return projectInnovationCrpOutcomeDAO.save(projectInnovationCrpOutcome);
  }

  public void saveProjectInnovationCrpOutcomePhase(Phase next, long innovationId,
    ProjectInnovationCrpOutcome projectInnovationCrpOutcome) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationCrpOutcome> innovationOutcomes = projectInnovationCrpOutcomeDAO.findAll().stream()
      .filter(c -> c.getProjectInnovation().getId().equals(innovationId) && c.getPhase().getId().equals(phase.getId())
        && c.getCrpOutcome() != null && c.getCrpOutcome().getComposeID() != null
        && projectInnovationCrpOutcome.getCrpOutcome().getComposeID() != null
        && c.getCrpOutcome().getComposeID().equals(projectInnovationCrpOutcome.getCrpOutcome().getComposeID()))
      .collect(Collectors.toList());

    // Get project outcomes phases
    List<CrpProgramOutcome> crpOutcomes = phase.getOutcomes().stream()
      .filter(c -> c.isActive() && c.getComposeID().equals(projectInnovationCrpOutcome.getCrpOutcome().getComposeID()))
      .collect(Collectors.toList());

    if (crpOutcomes != null && !crpOutcomes.isEmpty() && crpOutcomes.get(0) != null) {
      if (innovationOutcomes.isEmpty()) {
        ProjectInnovationCrpOutcome innovationProjectOutcomeAdd = new ProjectInnovationCrpOutcome();
        innovationProjectOutcomeAdd.setPhase(phase);
        innovationProjectOutcomeAdd.setCrpOutcome(crpOutcomes.get(0));
        innovationProjectOutcomeAdd.setProjectInnovation(projectInnovationCrpOutcome.getProjectInnovation());
        projectInnovationCrpOutcomeDAO.save(innovationProjectOutcomeAdd);
      }
    }

    if (phase.getNext() != null) {
      this.saveProjectInnovationCrpOutcomePhase(phase.getNext(), innovationId, projectInnovationCrpOutcome);
    }
  }

}
