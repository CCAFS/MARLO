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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyProjectOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyProjectOutcomeManagerImpl implements ProjectExpectedStudyProjectOutcomeManager {


  private ProjectExpectedStudyProjectOutcomeDAO projectExpectedStudyProjectOutcomeDAO;
  ProjectExpectedStudyManager projectExpectedStudyManager;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectExpectedStudyProjectOutcomeManagerImpl(
    ProjectExpectedStudyProjectOutcomeDAO projectExpectedStudyProjectOutcomeDAO, PhaseDAO phaseDAO,
    ProjectExpectedStudyManager projectExpectedStudyManager) {
    this.projectExpectedStudyProjectOutcomeDAO = projectExpectedStudyProjectOutcomeDAO;
    this.phaseDAO = phaseDAO;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
  }

  @Override
  public void deleteProjectExpectedStudyProjectOutcome(long projectExpectedStudyProjectOutcomeId, long phaseId) {
    ProjectExpectedStudyProjectOutcome projectExpectedStudyProjectOutcome =
      projectExpectedStudyProjectOutcomeDAO.find(projectExpectedStudyProjectOutcomeId);
    projectExpectedStudyProjectOutcomeDAO
      .deleteProjectExpectedStudyProjectOutcome(projectExpectedStudyProjectOutcomeId);
    Phase phase = phaseDAO.find(phaseId);
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.deleteProjectExpectedStudyProjectOutcomePhase(projectExpectedStudyProjectOutcome.getPhase().getNext(),
        projectExpectedStudyProjectOutcome.getProjectExpectedStudy().getId(), projectExpectedStudyProjectOutcome);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyProjectOutcomePhase(upkeepPhase,
            projectExpectedStudyProjectOutcome.getProjectExpectedStudy().getId(), projectExpectedStudyProjectOutcome);
        }
      }
    }
  }

  public void deleteProjectExpectedStudyProjectOutcomePhase(Phase next, long expectedStudyId,
    ProjectExpectedStudyProjectOutcome projectExpectedStudyProjectOutcome) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyProjectOutcome> expectedOutcomes = projectExpectedStudyProjectOutcomeDAO.findAll().stream()
      .filter(
        c -> c.getProjectExpectedStudy().getId().equals(expectedStudyId) && c.getPhase().getId().equals(phase.getId())
          && c.getProjectOutcome().getId().equals(projectExpectedStudyProjectOutcome.getProjectOutcome().getId()))
      .collect(Collectors.toList());

    // Get project outcomes phases
    ProjectExpectedStudy expectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(expectedStudyId);
    Project project = expectedStudy.getProject();

    List<ProjectOutcome> projectOutcomes = phase.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == project.getId()
        && c.getCrpProgramOutcome().getComposeID()
          .equals(projectExpectedStudyProjectOutcome.getProjectOutcome().getCrpProgramOutcome().getComposeID()))
      .collect(Collectors.toList());

    if (projectOutcomes != null && !projectOutcomes.isEmpty() && projectOutcomes.get(0) != null) {
      if (expectedOutcomes.isEmpty()) {
        ProjectExpectedStudyProjectOutcome expectedProjectOutcomeAdd = new ProjectExpectedStudyProjectOutcome();
        if (projectExpectedStudyProjectOutcomeDAO.findAll().stream()
          .filter(po -> po.getPhase().getId().equals(phase.getId())
            && po.getProjectOutcome().getId().equals(projectOutcomes.get(0).getId())
            && po.getProjectExpectedStudy().getId().equals(expectedStudy.getId()))
          .collect(Collectors.toList()) != null) {
          List<ProjectExpectedStudyProjectOutcome> listTemp = projectExpectedStudyProjectOutcomeDAO.findAll().stream()
            .filter(po -> po.getPhase().getId().equals(phase.getId())
              && po.getProjectOutcome().getId().equals(projectOutcomes.get(0).getId())
              && po.getProjectExpectedStudy().getId().equals(expectedStudy.getId()))
            .collect(Collectors.toList());
          if (listTemp != null && !listTemp.isEmpty() && listTemp.get(0) != null) {
            expectedProjectOutcomeAdd = listTemp.get(0);
            projectExpectedStudyProjectOutcomeDAO
              .deleteProjectExpectedStudyProjectOutcome(expectedProjectOutcomeAdd.getId());
          }
        }
      }
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyProjectOutcomePhase(phase.getNext(), expectedStudyId,
        projectExpectedStudyProjectOutcome);
    }
  }

  @Override
  public boolean existProjectExpectedStudyProjectOutcome(long projectExpectedStudyProjectOutcomeID) {

    return projectExpectedStudyProjectOutcomeDAO
      .existProjectExpectedStudyProjectOutcome(projectExpectedStudyProjectOutcomeID);
  }

  @Override
  public List<ProjectExpectedStudyProjectOutcome> findAll() {

    return projectExpectedStudyProjectOutcomeDAO.findAll();
  }

  @Override
  public ProjectExpectedStudyProjectOutcome
    getProjectExpectedStudyProjectOutcomeById(long projectExpectedStudyProjectOutcomeID) {

    return projectExpectedStudyProjectOutcomeDAO.find(projectExpectedStudyProjectOutcomeID);
  }

  @Override
  public ProjectExpectedStudyProjectOutcome
    saveProjectExpectedStudyProjectOutcome(ProjectExpectedStudyProjectOutcome projectExpectedStudyProjectOutcome) {
    ProjectExpectedStudyProjectOutcome expectedOutcome =
      projectExpectedStudyProjectOutcomeDAO.save(projectExpectedStudyProjectOutcome);
    Phase phase = phaseDAO.find(expectedOutcome.getPhase().getId());
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectExpectedStudyProjectOutcomePhase(expectedOutcome.getPhase().getNext(),
        expectedOutcome.getProjectExpectedStudy().getId(), projectExpectedStudyProjectOutcome);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectExpectedStudyProjectOutcomePhase(upkeepPhase,
            expectedOutcome.getProjectExpectedStudy().getId(), projectExpectedStudyProjectOutcome);
        }
      }
    }
    return expectedOutcome;
  }

  public void saveProjectExpectedStudyProjectOutcomePhase(Phase next, long expectedStudyId,
    ProjectExpectedStudyProjectOutcome projectExpectedStudyProjectOutcome) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyProjectOutcome> expectedOutcomes = projectExpectedStudyProjectOutcomeDAO.findAll().stream()
      .filter(
        c -> c.getProjectExpectedStudy().getId().equals(expectedStudyId) && c.getPhase().getId().equals(phase.getId())
          && c.getProjectOutcome().getId().equals(projectExpectedStudyProjectOutcome.getProjectOutcome().getId()))
      .collect(Collectors.toList());

    // Get project outcomes phases
    ProjectExpectedStudy expectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(expectedStudyId);
    Project project = expectedStudy.getProject();

    List<ProjectOutcome> projectOutcomes = phase.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getProject().getId().longValue() == project.getId()
        && c.getCrpProgramOutcome().getComposeID()
          .equals(projectExpectedStudyProjectOutcome.getProjectOutcome().getCrpProgramOutcome().getComposeID()))
      .collect(Collectors.toList());

    if (projectOutcomes != null && !projectOutcomes.isEmpty() && projectOutcomes.get(0) != null) {
      if (expectedOutcomes.isEmpty()) {
        ProjectExpectedStudyProjectOutcome deliverableProjectOutcomeAdd = new ProjectExpectedStudyProjectOutcome();
        deliverableProjectOutcomeAdd
          .setProjectExpectedStudy(projectExpectedStudyProjectOutcome.getProjectExpectedStudy());
        deliverableProjectOutcomeAdd.setPhase(phase);
        deliverableProjectOutcomeAdd.setProjectOutcome(projectOutcomes.get(0));
        projectExpectedStudyProjectOutcomeDAO.save(deliverableProjectOutcomeAdd);
      }
    }

    if (phase.getNext() != null) {
      this.saveProjectExpectedStudyProjectOutcomePhase(phase.getNext(), expectedStudyId,
        projectExpectedStudyProjectOutcome);
    }
  }
}
