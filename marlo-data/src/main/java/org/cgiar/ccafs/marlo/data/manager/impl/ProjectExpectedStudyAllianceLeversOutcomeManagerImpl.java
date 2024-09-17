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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyAllianceLeversOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyAllianceLeversOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyAllianceLeversOutcome;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyAllianceLeversOutcomeManagerImpl
  implements ProjectExpectedStudyAllianceLeversOutcomeManager {


  private ProjectExpectedStudyAllianceLeversOutcomeDAO projectExpectedStudyAllianceLeversOutcomeDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyAllianceLeversOutcomeManagerImpl(
    ProjectExpectedStudyAllianceLeversOutcomeDAO projectExpectedStudyAllianceLeversOutcomeDAO, PhaseDAO phaseDAO) {
    this.projectExpectedStudyAllianceLeversOutcomeDAO = projectExpectedStudyAllianceLeversOutcomeDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyAllianceLeversOutcome(long projectExpectedStudyAllianceLeversOutcomeId) {

    ProjectExpectedStudyAllianceLeversOutcome projectExpectedStudyAllianceLeversOutcome =
      this.getProjectExpectedStudyAllianceLeversOutcomeById(projectExpectedStudyAllianceLeversOutcomeId);
    Phase currentPhase = projectExpectedStudyAllianceLeversOutcome.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyAllianceLeversOutcomePhase(currentPhase.getNext(),
        projectExpectedStudyAllianceLeversOutcome);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyAllianceLeversOutcomePhase(upkeepPhase.getNext(),
            projectExpectedStudyAllianceLeversOutcome);
        }
      }
    }
    projectExpectedStudyAllianceLeversOutcomeDAO
      .deleteProjectExpectedStudyAllianceLeversOutcome(projectExpectedStudyAllianceLeversOutcomeId);
  }

  public void deleteProjectExpectedStudyAllianceLeversOutcomePhase(Phase next,
    ProjectExpectedStudyAllianceLeversOutcome projectExpectedStudyAllianceLeversOutcome) {
    Phase phase = phaseDAO.find(next.getId());


    System.out.println(
      " linea 83" + projectExpectedStudyAllianceLeversOutcome.getAllianceLeverOutcome().getAllianceLever().getId());
    System.out.println(" linea 84" + projectExpectedStudyAllianceLeversOutcome.getAllianceLeverOutcome().getId());
    List<ProjectExpectedStudyAllianceLeversOutcome> projectExpectedStudyAllianceLeversOutcomeList =
      phase.getProjectExpectedStudyAllianceLeversOutcomes().stream()
        .filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId() == projectExpectedStudyAllianceLeversOutcome.getProjectExpectedStudy()
            .getId()
          && c.getAllianceLever().getId() == projectExpectedStudyAllianceLeversOutcome.getAllianceLeverOutcome()
            .getAllianceLever().getId()
          && c.getAllianceLeverOutcome().getId() == projectExpectedStudyAllianceLeversOutcome.getAllianceLeverOutcome()
            .getId())
        .collect(Collectors.toList());
    for (ProjectExpectedStudyAllianceLeversOutcome projectExpectedStudyAllianceLeversOutcomeTmp : projectExpectedStudyAllianceLeversOutcomeList) {
      projectExpectedStudyAllianceLeversOutcomeDAO
        .deleteProjectExpectedStudyAllianceLeversOutcome(projectExpectedStudyAllianceLeversOutcomeTmp.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyAllianceLeversOutcomePhase(phase.getNext(),
        projectExpectedStudyAllianceLeversOutcome);
    }
  }

  @Override
  public boolean existProjectExpectedStudyAllianceLeversOutcome(long projectExpectedStudyAllianceLeversOutcomeID) {

    return projectExpectedStudyAllianceLeversOutcomeDAO
      .existProjectExpectedStudyAllianceLeversOutcome(projectExpectedStudyAllianceLeversOutcomeID);
  }

  @Override
  public List<ProjectExpectedStudyAllianceLeversOutcome> findAll() {

    return projectExpectedStudyAllianceLeversOutcomeDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyAllianceLeversOutcome
    getProjectExpectedStudyAllianceLeversOutcomeById(long projectExpectedStudyAllianceLeversOutcomeID) {

    return projectExpectedStudyAllianceLeversOutcomeDAO.find(projectExpectedStudyAllianceLeversOutcomeID);
  }

  /**
   * Reply the information to the next Phases
   * 
   * @param next - The next Phase
   * @param projectExpectedStudyAllianceLeversOutcome - The project expected study AllianceLeversOutcome into the
   *        database.
   */
  public void saveInfoPhase(Phase next,
    ProjectExpectedStudyAllianceLeversOutcome projectExpectedStudyAllianceLeversOutcome) {

    Phase phase = phaseDAO.find(next.getId());
    List<ProjectExpectedStudyAllianceLeversOutcome> projectExpectedStudyAllianceLeversOutcomeList =
      phase.getProjectExpectedStudyAllianceLeversOutcomes().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().longValue() == projectExpectedStudyAllianceLeversOutcome
          .getProjectExpectedStudy().getId()
          && c.getAllianceLever() == projectExpectedStudyAllianceLeversOutcome.getAllianceLeverOutcome()
            .getAllianceLever()
          && c.getAllianceLeverOutcome() == projectExpectedStudyAllianceLeversOutcome.getAllianceLeverOutcome())
        .collect(Collectors.toList());
    if (projectExpectedStudyAllianceLeversOutcomeList.isEmpty()) {

      ProjectExpectedStudyAllianceLeversOutcome projectExpectedStudyAllianceLeversOutcomeAdd =
        new ProjectExpectedStudyAllianceLeversOutcome();

      projectExpectedStudyAllianceLeversOutcomeAdd
        .setProjectExpectedStudy(projectExpectedStudyAllianceLeversOutcome.getProjectExpectedStudy());
      projectExpectedStudyAllianceLeversOutcomeAdd.setPhase(phase);
      projectExpectedStudyAllianceLeversOutcomeAdd
        .setAllianceLever(projectExpectedStudyAllianceLeversOutcome.getAllianceLever());
      projectExpectedStudyAllianceLeversOutcomeAdd
        .setAllianceLeverOutcome(projectExpectedStudyAllianceLeversOutcome.getAllianceLeverOutcome());


      projectExpectedStudyAllianceLeversOutcomeDAO.save(projectExpectedStudyAllianceLeversOutcomeAdd);
    }

    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectExpectedStudyAllianceLeversOutcome);
    }
  }


  @Override
  public ProjectExpectedStudyAllianceLeversOutcome saveProjectExpectedStudyAllianceLeversOutcome(
    ProjectExpectedStudyAllianceLeversOutcome projectExpectedStudyAllianceLeversOutcome) {

    ProjectExpectedStudyAllianceLeversOutcome sourceInfo =
      projectExpectedStudyAllianceLeversOutcomeDAO.save(projectExpectedStudyAllianceLeversOutcome);
    Phase phase = phaseDAO.find(sourceInfo.getPhase().getId());

    if (phase.getDescription().equals(APConstants.PLANNING)) {
      if (phase.getNext() != null) {
        this.saveInfoPhase(phase.getNext(), projectExpectedStudyAllianceLeversOutcome);
      }
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInfoPhase(upkeepPhase, projectExpectedStudyAllianceLeversOutcome);
        }
      }
    }


    return sourceInfo;


  }


}
