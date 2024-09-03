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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPrimaryStrategicOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPrimaryStrategicOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPrimaryStrategicOutcome;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyPrimaryStrategicOutcomeManagerImpl
  implements ProjectExpectedStudyPrimaryStrategicOutcomeManager {


  private ProjectExpectedStudyPrimaryStrategicOutcomeDAO projectExpectedStudyPrimaryStrategicOutcomeDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyPrimaryStrategicOutcomeManagerImpl(
    ProjectExpectedStudyPrimaryStrategicOutcomeDAO projectExpectedStudyPrimaryStrategicOutcomeDAO, PhaseDAO phaseDAO) {
    this.projectExpectedStudyPrimaryStrategicOutcomeDAO = projectExpectedStudyPrimaryStrategicOutcomeDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyPrimaryStrategicOutcome(long projectExpectedStudyPrimaryStrategicOutcomeId) {

    ProjectExpectedStudyPrimaryStrategicOutcome projectExpectedStudyPrimaryStrategicOutcome =
      this.getProjectExpectedStudyPrimaryStrategicOutcomeById(projectExpectedStudyPrimaryStrategicOutcomeId);
    Phase currentPhase = projectExpectedStudyPrimaryStrategicOutcome.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyPrimaryStrategicOutcomePhase(currentPhase.getNext(),
        projectExpectedStudyPrimaryStrategicOutcome);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyPrimaryStrategicOutcomePhase(upkeepPhase.getNext(),
            projectExpectedStudyPrimaryStrategicOutcome);
        }
      }
    }

    projectExpectedStudyPrimaryStrategicOutcomeDAO
      .deleteProjectExpectedStudyPrimaryStrategicOutcome(projectExpectedStudyPrimaryStrategicOutcomeId);
  }


  public void deleteProjectExpectedStudyPrimaryStrategicOutcomePhase(Phase next,
    ProjectExpectedStudyPrimaryStrategicOutcome projectExpectedStudyPrimaryStrategicOutcome) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyPrimaryStrategicOutcome> projectExpectedStudyPrimaryStrategicOutcomeList =
      phase.getProjectExpectedStudyPrimaryStrategicOutcome().stream()
        .filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId() == projectExpectedStudyPrimaryStrategicOutcome
            .getProjectExpectedStudy().getId()
          && c.getPrimaryStrategicOutcome().getId() == projectExpectedStudyPrimaryStrategicOutcome
            .getPrimaryStrategicOutcome().getId())
        .collect(Collectors.toList());
    for (ProjectExpectedStudyPrimaryStrategicOutcome projectExpectedStudyPrimaryStrategicOutcomeTmp : projectExpectedStudyPrimaryStrategicOutcomeList) {
      projectExpectedStudyPrimaryStrategicOutcomeDAO
        .deleteProjectExpectedStudyPrimaryStrategicOutcome(projectExpectedStudyPrimaryStrategicOutcomeTmp.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyPrimaryStrategicOutcomePhase(phase.getNext(),
        projectExpectedStudyPrimaryStrategicOutcome);
    }
  }


  @Override
  public boolean existProjectExpectedStudyPrimaryStrategicOutcome(long projectExpectedStudyPrimaryStrategicOutcomeID) {

    return projectExpectedStudyPrimaryStrategicOutcomeDAO
      .existProjectExpectedStudyPrimaryStrategicOutcome(projectExpectedStudyPrimaryStrategicOutcomeID);
  }

  @Override
  public List<ProjectExpectedStudyPrimaryStrategicOutcome> findAll() {

    return projectExpectedStudyPrimaryStrategicOutcomeDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyPrimaryStrategicOutcome
    getProjectExpectedStudyPrimaryStrategicOutcomeById(long projectExpectedStudyPrimaryStrategicOutcomeID) {

    return projectExpectedStudyPrimaryStrategicOutcomeDAO.find(projectExpectedStudyPrimaryStrategicOutcomeID);
  }

  /**
   * Reply the information to the next Phases
   * 
   * @param next - The next Phase
   * @param projectExpectedStudyPrimaryStrategicOutcome - The project expected study primary strategic outcome into the
   *        database.
   */
  public void saveInfoPhase(Phase next,
    ProjectExpectedStudyPrimaryStrategicOutcome projectExpectedStudyPrimaryStrategicOutcome) {

    Phase phase = phaseDAO.find(next.getId());
    List<ProjectExpectedStudyPrimaryStrategicOutcome> projectExpectedStudyPrimaryStrategicOutcomeList =
      phase.getProjectExpectedStudyPrimaryStrategicOutcome().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().longValue() == projectExpectedStudyPrimaryStrategicOutcome
          .getProjectExpectedStudy().getId()
          && c.getPrimaryStrategicOutcome().getId() == projectExpectedStudyPrimaryStrategicOutcome
            .getPrimaryStrategicOutcome().getId())
        .collect(Collectors.toList());
    if (projectExpectedStudyPrimaryStrategicOutcomeList.isEmpty()) {

      ProjectExpectedStudyPrimaryStrategicOutcome projectExpectedStudyPrimaryStrategicOutcomeAdd =
        new ProjectExpectedStudyPrimaryStrategicOutcome();

      projectExpectedStudyPrimaryStrategicOutcomeAdd
        .setProjectExpectedStudy(projectExpectedStudyPrimaryStrategicOutcome.getProjectExpectedStudy());
      projectExpectedStudyPrimaryStrategicOutcomeAdd.setPhase(phase);
      projectExpectedStudyPrimaryStrategicOutcomeAdd
        .setPrimaryStrategicOutcome(projectExpectedStudyPrimaryStrategicOutcome.getPrimaryStrategicOutcome());

      projectExpectedStudyPrimaryStrategicOutcomeDAO.save(projectExpectedStudyPrimaryStrategicOutcomeAdd);
    }

    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectExpectedStudyPrimaryStrategicOutcome);
    }
  }


  @Override
  public ProjectExpectedStudyPrimaryStrategicOutcome saveProjectExpectedStudyPrimaryStrategicOutcome(
    ProjectExpectedStudyPrimaryStrategicOutcome projectExpectedStudyPrimaryStrategicOutcome) {


    ProjectExpectedStudyPrimaryStrategicOutcome sourceInfo =
      projectExpectedStudyPrimaryStrategicOutcomeDAO.save(projectExpectedStudyPrimaryStrategicOutcome);
    Phase phase = phaseDAO.find(sourceInfo.getPhase().getId());


    if (phase.getDescription().equals(APConstants.PLANNING)) {
      if (phase.getNext() != null) {
        this.saveInfoPhase(phase.getNext(), projectExpectedStudyPrimaryStrategicOutcome);
      }
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInfoPhase(upkeepPhase, projectExpectedStudyPrimaryStrategicOutcome);
        }
      }
    }


    return sourceInfo;

  }


}
