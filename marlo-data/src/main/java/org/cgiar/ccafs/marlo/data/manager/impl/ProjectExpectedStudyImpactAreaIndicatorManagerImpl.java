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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyImpactAreaIndicatorDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyImpactAreaIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyImpactAreaIndicator;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyImpactAreaIndicatorManagerImpl
  implements ProjectExpectedStudyImpactAreaIndicatorManager {


  private ProjectExpectedStudyImpactAreaIndicatorDAO projectExpectedStudyImpactAreaIndicatorDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudyImpactAreaIndicatorManagerImpl(
    ProjectExpectedStudyImpactAreaIndicatorDAO projectExpectedStudyImpactAreaIndicatorDAO, PhaseDAO phaseDAO) {
    this.projectExpectedStudyImpactAreaIndicatorDAO = projectExpectedStudyImpactAreaIndicatorDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyImpactAreaIndicator(long projectExpectedStudyImpactAreaIndicatorId) {

    ProjectExpectedStudyImpactAreaIndicator projectExpectedStudyImpactAreaIndicator =
      this.getProjectExpectedStudyImpactAreaIndicatorById(projectExpectedStudyImpactAreaIndicatorId);
    Phase currentPhase = projectExpectedStudyImpactAreaIndicator.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteProjectExpectedStudyImpactAreaIndicatorPhase(currentPhase.getNext(),
          projectExpectedStudyImpactAreaIndicator.getProjectExpectedStudy().getId(),
          projectExpectedStudyImpactAreaIndicator);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyImpactAreaIndicatorPhase(upkeepPhase,
            projectExpectedStudyImpactAreaIndicator.getProjectExpectedStudy().getId(),
            projectExpectedStudyImpactAreaIndicator);
        }
      }
    }

    projectExpectedStudyImpactAreaIndicatorDAO
      .deleteProjectExpectedStudyImpactAreaIndicator(projectExpectedStudyImpactAreaIndicatorId);
  }

  public void deleteProjectExpectedStudyImpactAreaIndicatorPhase(Phase next, long expectedID,
    ProjectExpectedStudyImpactAreaIndicator projectExpectedStudyImpactAreaIndicator) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyImpactAreaIndicator> projectExpectedStudyImpactAreaIndicators =
      phase.getProjectExpectedStudyImpactAreaIndicators().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getImpactAreaIndicator().getId()
            .equals(projectExpectedStudyImpactAreaIndicator.getImpactAreaIndicator().getId()))
        .collect(Collectors.toList());
    for (ProjectExpectedStudyImpactAreaIndicator projectExpectedStudyImpactAreaIndicatorDB : projectExpectedStudyImpactAreaIndicators) {
      projectExpectedStudyImpactAreaIndicatorDAO
        .deleteProjectExpectedStudyImpactAreaIndicator(projectExpectedStudyImpactAreaIndicatorDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyImpactAreaIndicatorPhase(phase.getNext(), expectedID,
        projectExpectedStudyImpactAreaIndicator);
    }
  }

  @Override
  public boolean existProjectExpectedStudyImpactAreaIndicator(long projectExpectedStudyImpactAreaIndicatorID) {

    return projectExpectedStudyImpactAreaIndicatorDAO
      .existProjectExpectedStudyImpactAreaIndicator(projectExpectedStudyImpactAreaIndicatorID);
  }

  @Override
  public List<ProjectExpectedStudyImpactAreaIndicator> findAll() {

    return projectExpectedStudyImpactAreaIndicatorDAO.findAll();

  }

  @Override
  public List<ProjectExpectedStudyImpactAreaIndicator> getAllStudyImpactAreaIndicatorsByStudy(Long studyId) {
    return this.projectExpectedStudyImpactAreaIndicatorDAO.getAllStudyImpactAreaIndicatorsByStudy(studyId.longValue());
  }

  @Override
  public ProjectExpectedStudyImpactAreaIndicator
    getProjectExpectedStudyImpactAreaIndicatorById(long projectExpectedStudyImpactAreaIndicatorID) {

    return projectExpectedStudyImpactAreaIndicatorDAO.find(projectExpectedStudyImpactAreaIndicatorID);
  }

  @Override
  public ProjectExpectedStudyImpactAreaIndicator getProjectExpectedStudyImpactAreaIndicatorByPhase(Long expectedID,
    Long impactAreaIndicatorID, Long phaseID) {

    return projectExpectedStudyImpactAreaIndicatorDAO.getProjectExpectedStudyImpactAreaIndicatorByPhase(expectedID,
      impactAreaIndicatorID, phaseID);
  }

  public void saveExpectedStudyImpactAreaIndicatorPhase(Phase next, long expectedID,
    ProjectExpectedStudyImpactAreaIndicator projectExpectedStudyImpactAreaIndicator) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyImpactAreaIndicator> projectExpectedStudyImpactAreaIndicators =
      phase.getProjectExpectedStudyImpactAreaIndicators().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID && c.getImpactAreaIndicator().getId()
          .equals(projectExpectedStudyImpactAreaIndicator.getImpactAreaIndicator().getId()))
        .collect(Collectors.toList());

    if (projectExpectedStudyImpactAreaIndicators.isEmpty()) {
      ProjectExpectedStudyImpactAreaIndicator projectExpectedStudyImpactAreaIndicatorAdd =
        new ProjectExpectedStudyImpactAreaIndicator();
      projectExpectedStudyImpactAreaIndicatorAdd
        .setProjectExpectedStudy(projectExpectedStudyImpactAreaIndicator.getProjectExpectedStudy());
      projectExpectedStudyImpactAreaIndicatorAdd.setPhase(phase);
      projectExpectedStudyImpactAreaIndicatorAdd
        .setImpactAreaIndicator(projectExpectedStudyImpactAreaIndicator.getImpactAreaIndicator());
      projectExpectedStudyImpactAreaIndicatorDAO.save(projectExpectedStudyImpactAreaIndicatorAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyImpactAreaIndicatorPhase(phase.getNext(), expectedID,
        projectExpectedStudyImpactAreaIndicator);
    }
  }

  @Override
  public ProjectExpectedStudyImpactAreaIndicator saveProjectExpectedStudyImpactAreaIndicator(
    ProjectExpectedStudyImpactAreaIndicator projectExpectedStudyImpactAreaIndicator) {

    ProjectExpectedStudyImpactAreaIndicator projectExpectedStudyImpactAreaIndicatorResult =
      projectExpectedStudyImpactAreaIndicatorDAO.save(projectExpectedStudyImpactAreaIndicator);
    Phase currentPhase = projectExpectedStudyImpactAreaIndicatorResult.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudyImpactAreaIndicatorPhase(currentPhase.getNext(),
          projectExpectedStudyImpactAreaIndicatorResult.getProjectExpectedStudy().getId(),
          projectExpectedStudyImpactAreaIndicator);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveExpectedStudyImpactAreaIndicatorPhase(upkeepPhase,
            projectExpectedStudyImpactAreaIndicatorResult.getProjectExpectedStudy().getId(),
            projectExpectedStudyImpactAreaIndicator);
        }
      }
    }

    return projectExpectedStudyImpactAreaIndicatorResult;
  }
}
