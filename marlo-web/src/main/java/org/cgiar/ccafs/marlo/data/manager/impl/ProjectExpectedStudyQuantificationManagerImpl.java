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


    if (currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyQuiantificationPhase(currentPhase.getNext(),
        projectExpectedStudyQuantification.getProjectExpectedStudy().getId(), projectExpectedStudyQuantification);
    }


    projectExpectedStudyQuantificationDAO
      .deleteProjectExpectedStudyQuantification(projectExpectedStudyQuantificationId);
  }

  public void deleteProjectExpectedStudyQuiantificationPhase(Phase next, long expectedID,
    ProjectExpectedStudyQuantification projectExpectedStudyQuantification) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyQuantification> projectExpectedStudyQuantifications =
      phase.getProjectExpectedStudyQuantifications().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID)
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
  public ProjectExpectedStudyQuantification
    getProjectExpectedStudyQuantificationById(long projectExpectedStudyQuantificationID) {

    return projectExpectedStudyQuantificationDAO.find(projectExpectedStudyQuantificationID);
  }

  public void saveExpectedStudyQuantificationPhase(Phase next, long expectedID,
    ProjectExpectedStudyQuantification projectExpectedStudyQuantification) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyQuantification> projectExpectedStudyQuantifications =
      phase.getProjectExpectedStudyQuantifications().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID)
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

    return projectExpectedStudyQuantificationResult;
  }


}
