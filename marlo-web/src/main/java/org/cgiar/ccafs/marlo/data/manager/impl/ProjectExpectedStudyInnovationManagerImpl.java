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


import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyInnovationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInnovationManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyInnovationManagerImpl implements ProjectExpectedStudyInnovationManager {


  private ProjectExpectedStudyInnovationDAO projectExpectedStudyInnovationDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectExpectedStudyInnovationManagerImpl(ProjectExpectedStudyInnovationDAO projectExpectedStudyInnovationDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudyInnovationDAO = projectExpectedStudyInnovationDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectExpectedStudyInnovation(long projectExpectedStudyInnovationId) {

    ProjectExpectedStudyInnovation projectExpectedStudyInnovation =
      this.getProjectExpectedStudyInnovationById(projectExpectedStudyInnovationId);
    Phase currentPhase = projectExpectedStudyInnovation.getPhase();


    if (currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyInnovationPhase(currentPhase.getNext(),
        projectExpectedStudyInnovation.getProjectExpectedStudy().getId(), projectExpectedStudyInnovation);
    }


    projectExpectedStudyInnovationDAO.deleteProjectExpectedStudyInnovation(projectExpectedStudyInnovationId);
  }

  public void deleteProjectExpectedStudyInnovationPhase(Phase next, long expectedID,
    ProjectExpectedStudyInnovation projectExpectedStudyInnovation) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyInnovation> projectExpectedStudyInnovations =
      phase.getProjectExpectedStudyInnovations().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getProjectInnovation().getId().equals(projectExpectedStudyInnovation.getProjectInnovation().getId()))
        .collect(Collectors.toList());
    for (ProjectExpectedStudyInnovation projectExpectedStudyInnovationDB : projectExpectedStudyInnovations) {
      projectExpectedStudyInnovationDAO.deleteProjectExpectedStudyInnovation(projectExpectedStudyInnovationDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyInnovationPhase(phase.getNext(), expectedID, projectExpectedStudyInnovation);
    }
  }

  @Override
  public boolean existProjectExpectedStudyInnovation(long projectExpectedStudyInnovationID) {

    return projectExpectedStudyInnovationDAO.existProjectExpectedStudyInnovation(projectExpectedStudyInnovationID);
  }

  @Override
  public List<ProjectExpectedStudyInnovation> findAll() {

    return projectExpectedStudyInnovationDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyInnovation getProjectExpectedStudyInnovationById(long projectExpectedStudyInnovationID) {

    return projectExpectedStudyInnovationDAO.find(projectExpectedStudyInnovationID);
  }

  public void saveExpectedStudyInnovationPhase(Phase next, long expectedID,
    ProjectExpectedStudyInnovation projectExpectedStudyInnovation) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyInnovation> projectExpectedStudyInnovations =
      phase.getProjectExpectedStudyInnovations().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getProjectInnovation().getId().equals(projectExpectedStudyInnovation.getProjectInnovation().getId()))
        .collect(Collectors.toList());

    if (projectExpectedStudyInnovations.isEmpty()) {
      ProjectExpectedStudyInnovation projectExpectedStudyInnovationAdd = new ProjectExpectedStudyInnovation();
      projectExpectedStudyInnovationAdd
        .setProjectExpectedStudy(projectExpectedStudyInnovation.getProjectExpectedStudy());
      projectExpectedStudyInnovationAdd.setPhase(phase);
      projectExpectedStudyInnovationAdd.setProjectInnovation(projectExpectedStudyInnovation.getProjectInnovation());
      projectExpectedStudyInnovationDAO.save(projectExpectedStudyInnovationAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyInnovationPhase(phase.getNext(), expectedID, projectExpectedStudyInnovation);
    }
  }

  @Override
  public ProjectExpectedStudyInnovation
    saveProjectExpectedStudyInnovation(ProjectExpectedStudyInnovation projectExpectedStudyInnovation) {

    ProjectExpectedStudyInnovation projectExpectedStudyInnovationResult =
      projectExpectedStudyInnovationDAO.save(projectExpectedStudyInnovation);
    Phase currentPhase = projectExpectedStudyInnovationResult.getPhase();


    if (currentPhase.getNext() != null) {
      this.saveExpectedStudyInnovationPhase(currentPhase.getNext(),
        projectExpectedStudyInnovation.getProjectExpectedStudy().getId(), projectExpectedStudyInnovation);
    }


    return projectExpectedStudyInnovationResult;
  }


}
