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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudySrfTargetDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySrfTargetManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudySrfTargetManagerImpl implements ProjectExpectedStudySrfTargetManager {


  private ProjectExpectedStudySrfTargetDAO projectExpectedStudySrfTargetDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudySrfTargetManagerImpl(ProjectExpectedStudySrfTargetDAO projectExpectedStudySrfTargetDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudySrfTargetDAO = projectExpectedStudySrfTargetDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudySrfTarget(long projectExpectedStudySrfTargetId) {

    ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget =
      this.getProjectExpectedStudySrfTargetById(projectExpectedStudySrfTargetId);
    Phase currentPhase = projectExpectedStudySrfTarget.getPhase();


    if (currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudySrfTargetPhase(currentPhase.getNext(),
        projectExpectedStudySrfTarget.getProjectExpectedStudy().getId(), projectExpectedStudySrfTarget);
    }

    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.deleteProjectExpectedStudySrfTargetPhase(upkeepPhase,
    // projectExpectedStudySrfTarget.getProjectExpectedStudy().getId(), projectExpectedStudySrfTarget);
    // }
    // }
    // }


    projectExpectedStudySrfTargetDAO.deleteProjectExpectedStudySrfTarget(projectExpectedStudySrfTargetId);
  }

  public void deleteProjectExpectedStudySrfTargetPhase(Phase next, long expectedID,
    ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargets =
      phase.getProjectExpectedStudySrfTargets().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getSrfSloIndicator().getId().equals(projectExpectedStudySrfTarget.getSrfSloIndicator().getId()))
        .collect(Collectors.toList());
    for (ProjectExpectedStudySrfTarget projectExpectedStudySrfTargetDB : projectExpectedStudySrfTargets) {
      projectExpectedStudySrfTargetDAO.deleteProjectExpectedStudySrfTarget(projectExpectedStudySrfTargetDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudySrfTargetPhase(phase.getNext(), expectedID, projectExpectedStudySrfTarget);
    }
  }

  @Override
  public boolean existProjectExpectedStudySrfTarget(long projectExpectedStudySrfTargetID) {

    return projectExpectedStudySrfTargetDAO.existProjectExpectedStudySrfTarget(projectExpectedStudySrfTargetID);
  }

  @Override
  public List<ProjectExpectedStudySrfTarget> findAll() {

    return projectExpectedStudySrfTargetDAO.findAll();

  }

  @Override
  public ProjectExpectedStudySrfTarget getProjectExpectedStudySrfTargetById(long projectExpectedStudySrfTargetID) {

    return projectExpectedStudySrfTargetDAO.find(projectExpectedStudySrfTargetID);
  }

  public void saveExpectedStudySrfTargetPhase(Phase next, long expectedID,
    ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargets =
      phase.getProjectExpectedStudySrfTargets().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getSrfSloIndicator().getId().equals(projectExpectedStudySrfTarget.getSrfSloIndicator().getId()))
        .collect(Collectors.toList());

    if (projectExpectedStudySrfTargets.isEmpty()) {
      ProjectExpectedStudySrfTarget projectExpectedStudySrfTargetAdd = new ProjectExpectedStudySrfTarget();
      projectExpectedStudySrfTargetAdd.setProjectExpectedStudy(projectExpectedStudySrfTarget.getProjectExpectedStudy());
      projectExpectedStudySrfTargetAdd.setPhase(phase);
      projectExpectedStudySrfTargetAdd.setSrfSloIndicator(projectExpectedStudySrfTarget.getSrfSloIndicator());
      projectExpectedStudySrfTargetDAO.save(projectExpectedStudySrfTargetAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudySrfTargetPhase(phase.getNext(), expectedID, projectExpectedStudySrfTarget);
    }
  }

  @Override
  public ProjectExpectedStudySrfTarget
    saveProjectExpectedStudySrfTarget(ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget) {

    ProjectExpectedStudySrfTarget srfTarget = projectExpectedStudySrfTargetDAO.save(projectExpectedStudySrfTarget);
    Phase currentPhase = srfTarget.getPhase();


    if (currentPhase.getNext() != null) {
      this.saveExpectedStudySrfTargetPhase(currentPhase.getNext(), srfTarget.getProjectExpectedStudy().getId(),
        projectExpectedStudySrfTarget);
    }

    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.saveExpectedStudySrfTargetPhase(upkeepPhase, srfTarget.getProjectExpectedStudy().getId(),
    // projectExpectedStudySrfTarget);
    // }
    // }
    // }

    return srfTarget;
  }


}
