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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyGlobalTargetDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyGlobalTargetManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGlobalTarget;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyGlobalTargetManagerImpl implements ProjectExpectedStudyGlobalTargetManager {


  private ProjectExpectedStudyGlobalTargetDAO projectExpectedStudyGlobalTargetDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyGlobalTargetManagerImpl(
    ProjectExpectedStudyGlobalTargetDAO projectExpectedStudyGlobalTargetDAO, PhaseDAO phaseDAO) {
    this.projectExpectedStudyGlobalTargetDAO = projectExpectedStudyGlobalTargetDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyGlobalTarget(long projectExpectedStudyGlobalTargetId) {

    ProjectExpectedStudyGlobalTarget projectExpectedStudyGlobalTarget =
      this.getProjectExpectedStudyGlobalTargetById(projectExpectedStudyGlobalTargetId);
    Phase currentPhase = projectExpectedStudyGlobalTarget.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyGlobalTargetPhase(currentPhase.getNext(), projectExpectedStudyGlobalTarget);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyGlobalTargetPhase(upkeepPhase.getNext(), projectExpectedStudyGlobalTarget);
        }
      }
    }


    projectExpectedStudyGlobalTargetDAO.deleteProjectExpectedStudyGlobalTarget(projectExpectedStudyGlobalTargetId);
  }

  public void deleteProjectExpectedStudyGlobalTargetPhase(Phase next,
    ProjectExpectedStudyGlobalTarget projectExpectedStudyGlobalTarget) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyGlobalTarget> projectExpectedStudyGlobalTargetList =
      phase.getProjectExpectedStudyGlobalTargets().stream()
        .filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId() == projectExpectedStudyGlobalTarget.getProjectExpectedStudy().getId()
          && c.getGlobalTarget().getId() == projectExpectedStudyGlobalTarget.getGlobalTarget().getId())
        .collect(Collectors.toList());
    System.out.println(" linea 86 " + phase.getProjectExpectedStudyGlobalTargets().size());
    System.out.println(" linea 87 " + projectExpectedStudyGlobalTargetList.size());
    for (ProjectExpectedStudyGlobalTarget projectExpectedStudyGlobalTargetTmp : projectExpectedStudyGlobalTargetList) {
      projectExpectedStudyGlobalTargetDAO
        .deleteProjectExpectedStudyGlobalTarget(projectExpectedStudyGlobalTargetTmp.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyGlobalTargetPhase(phase.getNext(), projectExpectedStudyGlobalTarget);
    }
  }

  @Override
  public boolean existProjectExpectedStudyGlobalTarget(long projectExpectedStudyGlobalTargetID) {

    return projectExpectedStudyGlobalTargetDAO
      .existProjectExpectedStudyGlobalTarget(projectExpectedStudyGlobalTargetID);
  }

  @Override
  public List<ProjectExpectedStudyGlobalTarget> findAll() {

    return projectExpectedStudyGlobalTargetDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyGlobalTarget findByExpectedAndGlobalAndPhase(long expectedId, long targetId,
    long phaseId) {

    return projectExpectedStudyGlobalTargetDAO.findByExpectedAndGlobalAndPhase(expectedId, targetId, phaseId);

  }

  @Override
  public ProjectExpectedStudyGlobalTarget
    getProjectExpectedStudyGlobalTargetById(long projectExpectedStudyGlobalTargetID) {

    return projectExpectedStudyGlobalTargetDAO.find(projectExpectedStudyGlobalTargetID);
  }


  /**
   * Reply the information to the next Phases
   * 
   * @param next - The next Phase
   * @param projectExpectedStudyGlobalTarget - The project expected study Globaltarget into the
   *        database.
   */
  public void saveInfoPhase(Phase next, ProjectExpectedStudyGlobalTarget projectExpectedStudyGlobalTarget) {

    Phase phase = phaseDAO.find(next.getId());
    List<ProjectExpectedStudyGlobalTarget> projectExpectedStudyGlobalTargetList =
      phase.getProjectExpectedStudyGlobalTargets().stream()
        .filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId() == projectExpectedStudyGlobalTarget.getProjectExpectedStudy().getId()
          && c.getGlobalTarget().getId() == projectExpectedStudyGlobalTarget.getGlobalTarget().getId())
        .collect(Collectors.toList());
    if (projectExpectedStudyGlobalTargetList.isEmpty()) {

      ProjectExpectedStudyGlobalTarget projectExpectedStudyGlobalTargetAdd = new ProjectExpectedStudyGlobalTarget();

      projectExpectedStudyGlobalTargetAdd
        .setProjectExpectedStudy(projectExpectedStudyGlobalTarget.getProjectExpectedStudy());
      projectExpectedStudyGlobalTargetAdd.setPhase(phase);
      projectExpectedStudyGlobalTargetAdd.setGlobalTarget(projectExpectedStudyGlobalTarget.getGlobalTarget());


      projectExpectedStudyGlobalTargetDAO.save(projectExpectedStudyGlobalTargetAdd);
    }

    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectExpectedStudyGlobalTarget);
    }
  }

  @Override
  public ProjectExpectedStudyGlobalTarget
    saveProjectExpectedStudyGlobalTarget(ProjectExpectedStudyGlobalTarget projectExpectedStudyGlobalTarget) {
    ProjectExpectedStudyGlobalTarget sourceInfo =
      projectExpectedStudyGlobalTargetDAO.save(projectExpectedStudyGlobalTarget);

    Phase phase = phaseDAO.find(sourceInfo.getPhase().getId());

    if (phase.getDescription().equals(APConstants.PLANNING)) {
      if (phase.getNext() != null) {
        this.saveInfoPhase(phase.getNext(), projectExpectedStudyGlobalTarget);
      }
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInfoPhase(upkeepPhase, projectExpectedStudyGlobalTarget);
        }
      }
    }


    return sourceInfo;

  }


}
