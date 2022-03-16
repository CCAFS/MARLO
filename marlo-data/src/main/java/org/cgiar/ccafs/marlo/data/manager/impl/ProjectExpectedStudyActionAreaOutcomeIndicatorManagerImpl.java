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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyActionAreaOutcomeIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyActionAreaOutcomeIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyActionAreaOutcomeIndicator;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyActionAreaOutcomeIndicatorManagerImpl
  implements ProjectExpectedStudyActionAreaOutcomeIndicatorManager {


  private ProjectExpectedStudyActionAreaOutcomeIndicatorDAO projectExpectedStudyActionAreaOutcomeIndicatorDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudyActionAreaOutcomeIndicatorManagerImpl(
    ProjectExpectedStudyActionAreaOutcomeIndicatorDAO projectExpectedStudyActionAreaOutcomeIndicatorDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudyActionAreaOutcomeIndicatorDAO = projectExpectedStudyActionAreaOutcomeIndicatorDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void
    deleteProjectExpectedStudyActionAreaOutcomeIndicator(long projectExpectedStudyActionAreaOutcomeIndicatorId) {

    ProjectExpectedStudyActionAreaOutcomeIndicator projectExpectedStudyActionAreaOutcomeIndicator =
      this.getProjectExpectedStudyActionAreaOutcomeIndicatorById(projectExpectedStudyActionAreaOutcomeIndicatorId);
    Phase currentPhase = projectExpectedStudyActionAreaOutcomeIndicator.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteProjectExpectedStudyActionAreaOutcomeIndicatorPhase(currentPhase.getNext(),
          projectExpectedStudyActionAreaOutcomeIndicator.getProjectExpectedStudy().getId(),
          projectExpectedStudyActionAreaOutcomeIndicator);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyActionAreaOutcomeIndicatorPhase(upkeepPhase,
            projectExpectedStudyActionAreaOutcomeIndicator.getProjectExpectedStudy().getId(),
            projectExpectedStudyActionAreaOutcomeIndicator);
        }
      }
    }

    projectExpectedStudyActionAreaOutcomeIndicatorDAO
      .deleteProjectExpectedStudyActionAreaOutcomeIndicator(projectExpectedStudyActionAreaOutcomeIndicatorId);
  }

  public void deleteProjectExpectedStudyActionAreaOutcomeIndicatorPhase(Phase next, long expectedID,
    ProjectExpectedStudyActionAreaOutcomeIndicator projectExpectedStudyActionAreaOutcomeIndicator) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyActionAreaOutcomeIndicator> projectExpectedStudyActionAreaOutcomeIndicators =
      phase.getProjectExpectedStudyActionAreaOutcomeIndicators().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getOutcomeIndicator().getId()
            .equals(projectExpectedStudyActionAreaOutcomeIndicator.getOutcomeIndicator().getId()))
        .collect(Collectors.toList());
    for (ProjectExpectedStudyActionAreaOutcomeIndicator projectExpectedStudyActionAreaOutcomeIndicatorDB : projectExpectedStudyActionAreaOutcomeIndicators) {
      projectExpectedStudyActionAreaOutcomeIndicatorDAO
        .deleteProjectExpectedStudyActionAreaOutcomeIndicator(projectExpectedStudyActionAreaOutcomeIndicatorDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyActionAreaOutcomeIndicatorPhase(phase.getNext(), expectedID,
        projectExpectedStudyActionAreaOutcomeIndicator);
    }
  }

  @Override
  public boolean
    existProjectExpectedStudyActionAreaOutcomeIndicator(long projectExpectedStudyActionAreaOutcomeIndicatorID) {

    return projectExpectedStudyActionAreaOutcomeIndicatorDAO
      .existProjectExpectedStudyActionAreaOutcomeIndicator(projectExpectedStudyActionAreaOutcomeIndicatorID);
  }

  @Override
  public List<ProjectExpectedStudyActionAreaOutcomeIndicator> findAll() {

    return projectExpectedStudyActionAreaOutcomeIndicatorDAO.findAll();

  }

  @Override
  public List<ProjectExpectedStudyActionAreaOutcomeIndicator>
    getAllStudyActionAreaOutcomeIndicatorsByStudy(Long studyId) {
    return this.projectExpectedStudyActionAreaOutcomeIndicatorDAO
      .getAllStudyActionAreaOutcomeIndicatorsByStudy(studyId.longValue());
  }

  @Override
  public ProjectExpectedStudyActionAreaOutcomeIndicator
    getProjectExpectedStudyActionAreaOutcomeIndicatorById(long projectExpectedStudyActionAreaOutcomeIndicatorID) {

    return projectExpectedStudyActionAreaOutcomeIndicatorDAO.find(projectExpectedStudyActionAreaOutcomeIndicatorID);
  }

  @Override
  public ProjectExpectedStudyActionAreaOutcomeIndicator getProjectExpectedStudyActionAreaOutcomeIndicatorByPhase(
    Long expectedID, Long actionAreaOutcomeIndicatorID, Long phaseID) {

    return projectExpectedStudyActionAreaOutcomeIndicatorDAO
      .getProjectExpectedStudyActionAreaOutcomeIndicatorByPhase(expectedID, actionAreaOutcomeIndicatorID, phaseID);
  }

  public void saveExpectedStudyActionAreaOutcomeIndicatorPhase(Phase next, long expectedID,
    ProjectExpectedStudyActionAreaOutcomeIndicator projectExpectedStudyActionAreaOutcomeIndicator) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyActionAreaOutcomeIndicator> projectExpectedStudyActionAreaOutcomeIndicators =
      phase.getProjectExpectedStudyActionAreaOutcomeIndicators().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID && c.getOutcomeIndicator().getId()
          .equals(projectExpectedStudyActionAreaOutcomeIndicator.getOutcomeIndicator().getId()))
        .collect(Collectors.toList());

    if (projectExpectedStudyActionAreaOutcomeIndicators.isEmpty()) {
      ProjectExpectedStudyActionAreaOutcomeIndicator projectExpectedStudyActionAreaOutcomeIndicatorAdd =
        new ProjectExpectedStudyActionAreaOutcomeIndicator();
      projectExpectedStudyActionAreaOutcomeIndicatorAdd
        .setProjectExpectedStudy(projectExpectedStudyActionAreaOutcomeIndicator.getProjectExpectedStudy());
      projectExpectedStudyActionAreaOutcomeIndicatorAdd.setPhase(phase);
      projectExpectedStudyActionAreaOutcomeIndicatorAdd
        .setOutcomeIndicator(projectExpectedStudyActionAreaOutcomeIndicator.getOutcomeIndicator());
      projectExpectedStudyActionAreaOutcomeIndicatorDAO.save(projectExpectedStudyActionAreaOutcomeIndicatorAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyActionAreaOutcomeIndicatorPhase(phase.getNext(), expectedID,
        projectExpectedStudyActionAreaOutcomeIndicator);
    }
  }

  @Override
  public ProjectExpectedStudyActionAreaOutcomeIndicator saveProjectExpectedStudyActionAreaOutcomeIndicator(
    ProjectExpectedStudyActionAreaOutcomeIndicator projectExpectedStudyActionAreaOutcomeIndicator) {

    ProjectExpectedStudyActionAreaOutcomeIndicator projectExpectedStudyActionAreaOutcomeIndicatorResult =
      projectExpectedStudyActionAreaOutcomeIndicatorDAO.save(projectExpectedStudyActionAreaOutcomeIndicator);
    Phase currentPhase = projectExpectedStudyActionAreaOutcomeIndicatorResult.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudyActionAreaOutcomeIndicatorPhase(currentPhase.getNext(),
          projectExpectedStudyActionAreaOutcomeIndicatorResult.getProjectExpectedStudy().getId(),
          projectExpectedStudyActionAreaOutcomeIndicator);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveExpectedStudyActionAreaOutcomeIndicatorPhase(upkeepPhase,
            projectExpectedStudyActionAreaOutcomeIndicatorResult.getProjectExpectedStudy().getId(),
            projectExpectedStudyActionAreaOutcomeIndicator);
        }
      }
    }

    return projectExpectedStudyActionAreaOutcomeIndicatorResult;
  }
}
