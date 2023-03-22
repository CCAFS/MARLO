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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyCrpOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCrpOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrpOutcome;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyCrpOutcomeManagerImpl implements ProjectExpectedStudyCrpOutcomeManager {


  private ProjectExpectedStudyCrpOutcomeDAO projectExpectedStudyCrpOutcomeDAO;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyCrpOutcomeManagerImpl(ProjectExpectedStudyCrpOutcomeDAO projectExpectedStudyCrpOutcomeDAO,
    PhaseDAO phaseDAO, ProjectExpectedStudyManager projectExpectedStudyManager) {
    this.projectExpectedStudyCrpOutcomeDAO = projectExpectedStudyCrpOutcomeDAO;
    this.phaseDAO = phaseDAO;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
  }

  @Override
  public void deleteProjectExpectedStudyCrpOutcome(long projectExpectedStudyCrpOutcomeId) {

    projectExpectedStudyCrpOutcomeDAO.deleteProjectExpectedStudyCrpOutcome(projectExpectedStudyCrpOutcomeId);
  }

  @Override
  public void deleteProjectExpectedStudyCrpOutcome(long projectExpectedStudyCrpOutcomeId, long phaseId) {

    ProjectExpectedStudyCrpOutcome projectExpectedStudyCrpOutcome =
      projectExpectedStudyCrpOutcomeDAO.find(projectExpectedStudyCrpOutcomeId);
    projectExpectedStudyCrpOutcomeDAO.deleteProjectExpectedStudyCrpOutcome(projectExpectedStudyCrpOutcomeId);
    Phase phase = phaseDAO.find(phaseId);
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.deleteProjectExpectedStudyCrpOutcomePhase(projectExpectedStudyCrpOutcome.getPhase().getNext(),
        projectExpectedStudyCrpOutcome.getProjectExpectedStudy().getId(), projectExpectedStudyCrpOutcome);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyCrpOutcomePhase(upkeepPhase,
            projectExpectedStudyCrpOutcome.getProjectExpectedStudy().getId(), projectExpectedStudyCrpOutcome);
        }
      }
    }
  }

  public void deleteProjectExpectedStudyCrpOutcomePhase(Phase next, long projectExpectedStudyId,
    ProjectExpectedStudyCrpOutcome projectExpectedStudyCrpOutcome) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyCrpOutcome> projectExpectedStudyCrpOutcomeOutcomes =
      projectExpectedStudyCrpOutcomeDAO.findAll().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().equals(projectExpectedStudyId)
          && c.getPhase().getId().equals(phase.getId())
          && c.getCrpOutcome().getId().equals(projectExpectedStudyCrpOutcome.getCrpOutcome().getId()))
        .collect(Collectors.toList());

    // Get CRP outcomes phases
    ProjectExpectedStudy projectExpectedStudy =
      projectExpectedStudyManager.getProjectExpectedStudyById(projectExpectedStudyId);
    Project project = projectExpectedStudy.getProject();

    List<CrpProgramOutcome> crpOutcomes = phase.getOutcomes().stream()
      .filter(
        c -> c.isActive() && c.getComposeID().equals(projectExpectedStudyCrpOutcome.getCrpOutcome().getComposeID()))
      .collect(Collectors.toList());

    if (crpOutcomes != null && !crpOutcomes.isEmpty() && crpOutcomes.get(0) != null) {
      if (projectExpectedStudyCrpOutcomeOutcomes.isEmpty()) {
        ProjectExpectedStudyCrpOutcome expectedCrpOutcomeAdd = new ProjectExpectedStudyCrpOutcome();
        if (projectExpectedStudyCrpOutcomeDAO.findAll().stream()
          .filter(po -> po.getPhase().getId().equals(phase.getId())
            && po.getCrpOutcome().getId().equals(crpOutcomes.get(0).getId())
            && po.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()))
          .collect(Collectors.toList()) != null) {
          List<ProjectExpectedStudyCrpOutcome> listTemp = projectExpectedStudyCrpOutcomeDAO.findAll().stream()
            .filter(po -> po.getPhase().getId().equals(phase.getId())
              && po.getCrpOutcome().getId().equals(crpOutcomes.get(0).getId())
              && po.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()))
            .collect(Collectors.toList());
          if (listTemp != null && !listTemp.isEmpty() && listTemp.get(0) != null) {
            expectedCrpOutcomeAdd = listTemp.get(0);
            projectExpectedStudyCrpOutcomeDAO.deleteProjectExpectedStudyCrpOutcome(expectedCrpOutcomeAdd.getId());
          }
        }
      }
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyCrpOutcomePhase(phase.getNext(), projectExpectedStudyId,
        projectExpectedStudyCrpOutcome);
    }
  }

  @Override
  public boolean existProjectExpectedStudyCrpOutcome(long projectExpectedStudyCrpOutcomeID) {

    return projectExpectedStudyCrpOutcomeDAO.existProjectExpectedStudyCrpOutcome(projectExpectedStudyCrpOutcomeID);
  }

  @Override
  public List<ProjectExpectedStudyCrpOutcome> findAll() {

    return projectExpectedStudyCrpOutcomeDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyCrpOutcome getProjectExpectedStudyCrpOutcomeById(long projectExpectedStudyCrpOutcomeID) {

    return projectExpectedStudyCrpOutcomeDAO.find(projectExpectedStudyCrpOutcomeID);
  }

  @Override
  public ProjectExpectedStudyCrpOutcome
    saveProjectExpectedStudyCrpOutcome(ProjectExpectedStudyCrpOutcome projectExpectedStudyCrpOutcome) {

    ProjectExpectedStudyCrpOutcome projectExpectedStudyOutcome =
      projectExpectedStudyCrpOutcomeDAO.save(projectExpectedStudyCrpOutcome);
    Phase phase = phaseDAO.find(projectExpectedStudyOutcome.getPhase().getId());
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectExpectedStudyCrpOutcomePhase(projectExpectedStudyOutcome.getPhase().getNext(),
        projectExpectedStudyOutcome.getProjectExpectedStudy().getId(), projectExpectedStudyCrpOutcome);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectExpectedStudyCrpOutcomePhase(upkeepPhase,
            projectExpectedStudyOutcome.getProjectExpectedStudy().getId(), projectExpectedStudyCrpOutcome);
        }
      }
    }
    return projectExpectedStudyOutcome;
  }

  public void saveProjectExpectedStudyCrpOutcomePhase(Phase next, long projectExpectedStudyId,
    ProjectExpectedStudyCrpOutcome projectExpectedStudyCrpOutcome) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyCrpOutcome> projectExpectedStudyOutcomes =
      projectExpectedStudyCrpOutcomeDAO.findAll().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().equals(projectExpectedStudyId)
          && c.getPhase().getId().equals(phase.getId()) && c.getCrpOutcome() != null
          && c.getCrpOutcome().getComposeID() != null
          && projectExpectedStudyCrpOutcome.getCrpOutcome().getComposeID() != null
          && c.getCrpOutcome().getComposeID().equals(projectExpectedStudyCrpOutcome.getCrpOutcome().getComposeID()))
        .collect(Collectors.toList());

    // Get project outcomes phases
    List<CrpProgramOutcome> crpOutcomes = phase.getOutcomes().stream()
      .filter(
        c -> c.isActive() && c.getComposeID().equals(projectExpectedStudyCrpOutcome.getCrpOutcome().getComposeID()))
      .collect(Collectors.toList());

    if (crpOutcomes != null && !crpOutcomes.isEmpty() && crpOutcomes.get(0) != null) {
      if (projectExpectedStudyOutcomes.isEmpty()) {
        ProjectExpectedStudyCrpOutcome projectExpectedStudyCrpOutcomeAdd = new ProjectExpectedStudyCrpOutcome();
        projectExpectedStudyCrpOutcomeAdd
          .setProjectExpectedStudy(projectExpectedStudyCrpOutcome.getProjectExpectedStudy());
        projectExpectedStudyCrpOutcomeAdd.setPhase(phase);
        projectExpectedStudyCrpOutcomeAdd.setCrpOutcome(crpOutcomes.get(0));
        projectExpectedStudyCrpOutcomeDAO.save(projectExpectedStudyCrpOutcomeAdd);
      }
    }

    if (phase.getNext() != null) {
      this.saveProjectExpectedStudyCrpOutcomePhase(phase.getNext(), projectExpectedStudyId,
        projectExpectedStudyCrpOutcome);
    }
  }


}
