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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyInitiativeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInitiativeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInitiative;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyInitiativeManagerImpl implements ProjectExpectedStudyInitiativeManager {


  private ProjectExpectedStudyInitiativeDAO projectExpectedStudyInitiativeDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudyInitiativeManagerImpl(ProjectExpectedStudyInitiativeDAO projectExpectedStudyInitiativeDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudyInitiativeDAO = projectExpectedStudyInitiativeDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyInitiative(long projectExpectedStudyInitiativeId) {

    ProjectExpectedStudyInitiative projectExpectedStudyInitiative =
      this.getProjectExpectedStudyInitiativeById(projectExpectedStudyInitiativeId);
    Phase currentPhase = projectExpectedStudyInitiative.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteProjectExpectedStudyInitiativePhase(currentPhase.getNext(),
          projectExpectedStudyInitiative.getProjectExpectedStudy().getId(), projectExpectedStudyInitiative);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyInitiativePhase(upkeepPhase,
            projectExpectedStudyInitiative.getProjectExpectedStudy().getId(), projectExpectedStudyInitiative);
        }
      }
    }

    projectExpectedStudyInitiativeDAO.deleteProjectExpectedStudyInitiative(projectExpectedStudyInitiativeId);
  }

  public void deleteProjectExpectedStudyInitiativePhase(Phase next, long expectedID,
    ProjectExpectedStudyInitiative projectExpectedStudyInitiative) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyInitiative> projectExpectedStudyInitiatives =
      phase.getProjectExpectedStudyInitiatives().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getInitiative().getId().equals(projectExpectedStudyInitiative.getInitiative().getId()))
        .collect(Collectors.toList());
    for (ProjectExpectedStudyInitiative projectExpectedStudyInitiativeDB : projectExpectedStudyInitiatives) {
      projectExpectedStudyInitiativeDAO.deleteProjectExpectedStudyInitiative(projectExpectedStudyInitiativeDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyInitiativePhase(phase.getNext(), expectedID, projectExpectedStudyInitiative);
    }
  }

  @Override
  public boolean existProjectExpectedStudyInitiative(long projectExpectedStudyInitiativeID) {

    return projectExpectedStudyInitiativeDAO.existProjectExpectedStudyInitiative(projectExpectedStudyInitiativeID);
  }

  @Override
  public List<ProjectExpectedStudyInitiative> findAll() {

    return projectExpectedStudyInitiativeDAO.findAll();

  }

  @Override
  public List<ProjectExpectedStudyInitiative> getAllStudyInitiativesByStudy(Long studyId) {
    return this.projectExpectedStudyInitiativeDAO.getAllStudyInitiativesByStudy(studyId.longValue());
  }

  @Override
  public ProjectExpectedStudyInitiative getProjectExpectedStudyInitiativeById(long projectExpectedStudyInitiativeID) {

    return projectExpectedStudyInitiativeDAO.find(projectExpectedStudyInitiativeID);
  }

  @Override
  public ProjectExpectedStudyInitiative getProjectExpectedStudyInitiativeByPhase(Long expectedID, Long initiativeID,
    Long phaseID) {

    return projectExpectedStudyInitiativeDAO.getProjectExpectedStudyInitiativeByPhase(expectedID, initiativeID,
      phaseID);
  }

  public void saveExpectedStudyInitiativePhase(Phase next, long expectedID,
    ProjectExpectedStudyInitiative projectExpectedStudyInitiative) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyInitiative> projectExpectedStudyInitiatives =
      phase.getProjectExpectedStudyInitiatives().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getInitiative().getId().equals(projectExpectedStudyInitiative.getInitiative().getId()))
        .collect(Collectors.toList());

    if (projectExpectedStudyInitiatives.isEmpty()) {
      ProjectExpectedStudyInitiative projectExpectedStudyInitiativeAdd = new ProjectExpectedStudyInitiative();
      projectExpectedStudyInitiativeAdd
        .setProjectExpectedStudy(projectExpectedStudyInitiative.getProjectExpectedStudy());
      projectExpectedStudyInitiativeAdd.setPhase(phase);
      projectExpectedStudyInitiativeAdd.setInitiative(projectExpectedStudyInitiative.getInitiative());
      projectExpectedStudyInitiativeDAO.save(projectExpectedStudyInitiativeAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyInitiativePhase(phase.getNext(), expectedID, projectExpectedStudyInitiative);
    }
  }

  @Override
  public ProjectExpectedStudyInitiative
    saveProjectExpectedStudyInitiative(ProjectExpectedStudyInitiative projectExpectedStudyInitiative) {

    ProjectExpectedStudyInitiative projectExpectedStudyInitiativeResult =
      projectExpectedStudyInitiativeDAO.save(projectExpectedStudyInitiative);
    Phase currentPhase = projectExpectedStudyInitiativeResult.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudyInitiativePhase(currentPhase.getNext(),
          projectExpectedStudyInitiativeResult.getProjectExpectedStudy().getId(), projectExpectedStudyInitiative);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveExpectedStudyInitiativePhase(upkeepPhase,
            projectExpectedStudyInitiativeResult.getProjectExpectedStudy().getId(), projectExpectedStudyInitiative);
        }
      }
    }

    return projectExpectedStudyInitiativeResult;
  }
}
