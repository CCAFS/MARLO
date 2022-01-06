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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyQuantificationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyQuantificationManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyQuantification;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyQuantificationManagerImpl implements ProjectExpectedStudyQuantificationManager {


  private ProjectExpectedStudyQuantificationDAO projectExpectedStudyQuantificationDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudyQuantificationManagerImpl(
    ProjectExpectedStudyQuantificationDAO projectExpectedStudyQuantificationDAO, PhaseDAO phaseDAO) {
    this.projectExpectedStudyQuantificationDAO = projectExpectedStudyQuantificationDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectExpectedStudyQuantification(long projectExpectedStudyQuantificationId) {

    ProjectExpectedStudyQuantification projectExpectedStudyQuantification =
      this.getProjectExpectedStudyQuantificationById(projectExpectedStudyQuantificationId);
    Phase currentPhase = projectExpectedStudyQuantification.getPhase();


    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteProjectExpectedStudyQuiantificationPhase(currentPhase.getNext(),
          projectExpectedStudyQuantification.getProjectExpectedStudy().getId(), projectExpectedStudyQuantification);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyQuiantificationPhase(upkeepPhase,
            projectExpectedStudyQuantification.getProjectExpectedStudy().getId(), projectExpectedStudyQuantification);
        }
      }
    }

    projectExpectedStudyQuantificationDAO
      .deleteProjectExpectedStudyQuantification(projectExpectedStudyQuantificationId);
  }

  public void deleteProjectExpectedStudyQuiantificationPhase(Phase next, long expectedID,
    ProjectExpectedStudyQuantification projectExpectedStudyQuantification) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyQuantification> projectExpectedStudyQuantifications =
      phase.getProjectExpectedStudyQuantifications().stream()
        .filter(c -> c != null && c.isActive() && c.getProjectExpectedStudy() != null && expectedID != 0
          && c.getProjectExpectedStudy().getId().longValue() == expectedID && c.getNumber() != null
          && c.getNumber().equals(projectExpectedStudyQuantification.getNumber()) && c.getComments() != null
          && c.getComments().equals(projectExpectedStudyQuantification.getComments()) && c.getTargetUnit() != null
          && c.getTargetUnit().equals(projectExpectedStudyQuantification.getTargetUnit())
          && c.getTypeQuantification() != null && c.getTypeQuantification() != null
          && projectExpectedStudyQuantification != null
          && projectExpectedStudyQuantification.getTypeQuantification() != null
          && c.getTypeQuantification().equals(projectExpectedStudyQuantification.getTypeQuantification()))
        .collect(Collectors.toList());

    for (ProjectExpectedStudyQuantification projectExpectedStudyQuantificationDB : projectExpectedStudyQuantifications) {
      projectExpectedStudyQuantificationDAO
        .deleteProjectExpectedStudyQuantification(projectExpectedStudyQuantificationDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyQuiantificationPhase(phase.getNext(), expectedID,
        projectExpectedStudyQuantification);
    }
  }

  @Override
  public boolean existProjectExpectedStudyQuantification(long projectExpectedStudyQuantificationID) {

    return projectExpectedStudyQuantificationDAO
      .existProjectExpectedStudyQuantification(projectExpectedStudyQuantificationID);
  }

  @Override
  public List<ProjectExpectedStudyQuantification> findAll() {

    return projectExpectedStudyQuantificationDAO.findAll();

  }

  @Override
  public List<ProjectExpectedStudyQuantification> getAllStudyQuantificationsByStudy(Long studyId) {
    return this.projectExpectedStudyQuantificationDAO.getAllStudyQuantificationsByStudy(studyId.longValue());
  }

  @Override
  public ProjectExpectedStudyQuantification
    getProjectExpectedStudyQuantificationById(long projectExpectedStudyQuantificationID) {

    return projectExpectedStudyQuantificationDAO.find(projectExpectedStudyQuantificationID);
  }

  @Override
  public ProjectExpectedStudyQuantification getProjectExpectedStudyQuantificationByPhase(Long expectedID,
    String typeQuantification, Long number, String targetUnit, Long phaseID) {

    return projectExpectedStudyQuantificationDAO.getProjectExpectedStudyQuantificationByPhase(expectedID,
      typeQuantification, number, targetUnit, phaseID);
  }

  public void saveExpectedStudyQuantificationPhase(Phase next, long expectedID,
    ProjectExpectedStudyQuantification projectExpectedStudyQuantification) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyQuantification> projectExpectedStudyQuantifications =
      phase.getProjectExpectedStudyQuantifications().stream()
        .filter(c -> c.isActive() && expectedID != 0 && c.getProjectExpectedStudy() != null
          && c.getProjectExpectedStudy().getId() != null
          && c.getProjectExpectedStudy().getId().longValue() == expectedID && c.getNumber() != 0
          && projectExpectedStudyQuantification != null && projectExpectedStudyQuantification.getNumber() != null
          && c.getNumber() == projectExpectedStudyQuantification.getNumber() && c.getComments() != null
          && projectExpectedStudyQuantification.getComments() != null
          && c.getComments().equals(projectExpectedStudyQuantification.getComments()) && c.getTargetUnit() != null
          && projectExpectedStudyQuantification.getTargetUnit() != null
          && c.getTargetUnit().equals(projectExpectedStudyQuantification.getTargetUnit())
          && c.getTypeQuantification() != null && projectExpectedStudyQuantification.getTypeQuantification() != null
          && c.getTypeQuantification().equals(projectExpectedStudyQuantification.getTypeQuantification()))
        .collect(Collectors.toList());

    if (projectExpectedStudyQuantifications.isEmpty()) {
      ProjectExpectedStudyQuantification projectExpectedStudyQuantificationAdd =
        new ProjectExpectedStudyQuantification();
      projectExpectedStudyQuantificationAdd
        .setProjectExpectedStudy(projectExpectedStudyQuantification.getProjectExpectedStudy());
      projectExpectedStudyQuantificationAdd.setPhase(phase);
      projectExpectedStudyQuantificationAdd.setNumber(projectExpectedStudyQuantification.getNumber());
      projectExpectedStudyQuantificationAdd.setComments(projectExpectedStudyQuantification.getComments());
      projectExpectedStudyQuantificationAdd.setTargetUnit(projectExpectedStudyQuantification.getTargetUnit());
      projectExpectedStudyQuantificationAdd
        .setTypeQuantification(projectExpectedStudyQuantification.getTypeQuantification());
      projectExpectedStudyQuantificationDAO.save(projectExpectedStudyQuantificationAdd);
    } else {
      ProjectExpectedStudyQuantification projectExpectedStudyQuantificationAdd =
        new ProjectExpectedStudyQuantification();
      projectExpectedStudyQuantificationAdd
        .setProjectExpectedStudy(projectExpectedStudyQuantification.getProjectExpectedStudy());
      projectExpectedStudyQuantificationAdd.setPhase(phase);
      projectExpectedStudyQuantificationAdd.setNumber(projectExpectedStudyQuantification.getNumber());
      projectExpectedStudyQuantificationAdd.setComments(projectExpectedStudyQuantification.getComments());
      projectExpectedStudyQuantificationAdd.setTargetUnit(projectExpectedStudyQuantification.getTargetUnit());
      projectExpectedStudyQuantificationAdd
        .setTypeQuantification(projectExpectedStudyQuantification.getTypeQuantification());
      projectExpectedStudyQuantificationDAO.save(projectExpectedStudyQuantificationAdd);

      for (ProjectExpectedStudyQuantification projectExpectedStudyQuantificationDel : projectExpectedStudyQuantifications) {
        try {
          projectExpectedStudyQuantificationDAO
            .deleteProjectExpectedStudyQuantification(projectExpectedStudyQuantificationDel.getId());
        } catch (Exception e) {
          // TODO: handle exception
        }
      }
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyQuantificationPhase(phase.getNext(), expectedID, projectExpectedStudyQuantification);
    }
  }

  @Override
  public ProjectExpectedStudyQuantification
    saveProjectExpectedStudyQuantification(ProjectExpectedStudyQuantification projectExpectedStudyQuantification) {

    ProjectExpectedStudyQuantification projectExpectedStudyQuantificationResult =
      projectExpectedStudyQuantificationDAO.save(projectExpectedStudyQuantification);
    Phase currentPhase = projectExpectedStudyQuantificationResult.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudyQuantificationPhase(currentPhase.getNext(),
          projectExpectedStudyQuantification.getProjectExpectedStudy().getId(), projectExpectedStudyQuantification);
      }
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveExpectedStudyQuantificationPhase(upkeepPhase,
            projectExpectedStudyQuantification.getProjectExpectedStudy().getId(), projectExpectedStudyQuantification);
        }
      }
    }

    return projectExpectedStudyQuantificationResult;
  }
}
