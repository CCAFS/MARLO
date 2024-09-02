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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPrimaryAllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPrimaryAllianceLeverManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPrimaryAllianceLever;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyPrimaryAllianceLeverManagerImpl
  implements ProjectExpectedStudyPrimaryAllianceLeverManager {


  private ProjectExpectedStudyPrimaryAllianceLeverDAO projectExpectedStudyPrimaryAllianceLeverDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyPrimaryAllianceLeverManagerImpl(
    ProjectExpectedStudyPrimaryAllianceLeverDAO projectExpectedStudyPrimaryAllianceLeverDAO, PhaseDAO phaseDAO) {
    this.projectExpectedStudyPrimaryAllianceLeverDAO = projectExpectedStudyPrimaryAllianceLeverDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyPrimaryAllianceLever(long projectExpectedStudyPrimaryAllianceLeverId) {

    ProjectExpectedStudyPrimaryAllianceLever projectExpectedStudyPrimaryAllianceLever =
      this.getProjectExpectedStudyPrimaryAllianceLeverById(projectExpectedStudyPrimaryAllianceLeverId);
    Phase currentPhase = projectExpectedStudyPrimaryAllianceLever.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyPrimaryAllianceLeverPhase(currentPhase.getNext(),
        projectExpectedStudyPrimaryAllianceLever);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyPrimaryAllianceLeverPhase(upkeepPhase.getNext(),
            projectExpectedStudyPrimaryAllianceLever);
        }
      }
    }

    projectExpectedStudyPrimaryAllianceLeverDAO
      .deleteProjectExpectedStudyPrimaryAllianceLever(projectExpectedStudyPrimaryAllianceLeverId);
  }


  public void deleteProjectExpectedStudyPrimaryAllianceLeverPhase(Phase next,
    ProjectExpectedStudyPrimaryAllianceLever projectExpectedStudyPrimaryAllianceLever) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyPrimaryAllianceLever> projectExpectedStudyPrimaryAllianceLeverList =
      phase.getProjectExpectedStudyPrimaryAllianceLever().stream()
        .filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId() == projectExpectedStudyPrimaryAllianceLever.getProjectExpectedStudy()
            .getId()
          && c.getPrimaryAllianceLever().getId() == projectExpectedStudyPrimaryAllianceLever.getPrimaryAllianceLever()
            .getId())
        .collect(Collectors.toList());
    for (ProjectExpectedStudyPrimaryAllianceLever projectExpectedStudyPrimaryAllianceLeverTmp : projectExpectedStudyPrimaryAllianceLeverList) {
      projectExpectedStudyPrimaryAllianceLeverDAO
        .deleteProjectExpectedStudyPrimaryAllianceLever(projectExpectedStudyPrimaryAllianceLeverTmp.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyPrimaryAllianceLeverPhase(phase.getNext(),
        projectExpectedStudyPrimaryAllianceLever);
    }
  }

  @Override
  public boolean existProjectExpectedStudyPrimaryAllianceLever(long projectExpectedStudyPrimaryAllianceLeverID) {

    return projectExpectedStudyPrimaryAllianceLeverDAO
      .existProjectExpectedStudyPrimaryAllianceLever(projectExpectedStudyPrimaryAllianceLeverID);
  }

  @Override
  public List<ProjectExpectedStudyPrimaryAllianceLever> findAll() {

    return projectExpectedStudyPrimaryAllianceLeverDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyPrimaryAllianceLever
    getProjectExpectedStudyPrimaryAllianceLeverById(long projectExpectedStudyPrimaryAllianceLeverID) {

    return projectExpectedStudyPrimaryAllianceLeverDAO.find(projectExpectedStudyPrimaryAllianceLeverID);
  }


  /**
   * Reply the information to the next Phases
   * 
   * @param next - The next Phase
   * @param projectExpectedStudyPrimaryAllianceLever - The project expected study primary alliance lever into the
   *        database.
   */
  public void saveInfoPhase(Phase next,
    ProjectExpectedStudyPrimaryAllianceLever projectExpectedStudyPrimaryAllianceLever) {

    Phase phase = phaseDAO.find(next.getId());
    List<ProjectExpectedStudyPrimaryAllianceLever> projectExpectedStudyPrimaryAllianceLeverList =
      phase.getProjectExpectedStudyPrimaryAllianceLever().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().longValue() == projectExpectedStudyPrimaryAllianceLever
          .getProjectExpectedStudy().getId()
          && c.getPrimaryAllianceLever().getId() == projectExpectedStudyPrimaryAllianceLever.getPrimaryAllianceLever()
            .getId())
        .collect(Collectors.toList());
    if (projectExpectedStudyPrimaryAllianceLeverList.isEmpty()) {

      ProjectExpectedStudyPrimaryAllianceLever projectExpectedStudyPrimaryAllianceLeverAdd =
        new ProjectExpectedStudyPrimaryAllianceLever();

      projectExpectedStudyPrimaryAllianceLeverAdd
        .setProjectExpectedStudy(projectExpectedStudyPrimaryAllianceLever.getProjectExpectedStudy());
      projectExpectedStudyPrimaryAllianceLeverAdd.setPhase(phase);
      projectExpectedStudyPrimaryAllianceLeverAdd
        .setPrimaryAllianceLever(projectExpectedStudyPrimaryAllianceLever.getPrimaryAllianceLever());

      projectExpectedStudyPrimaryAllianceLeverDAO.save(projectExpectedStudyPrimaryAllianceLeverAdd);
    }

    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectExpectedStudyPrimaryAllianceLever);
    }
  }


  @Override
  public ProjectExpectedStudyPrimaryAllianceLever saveProjectExpectedStudyPrimaryAllianceLever(
    ProjectExpectedStudyPrimaryAllianceLever projectExpectedStudyPrimaryAllianceLever) {

    ProjectExpectedStudyPrimaryAllianceLever sourceInfo =
      projectExpectedStudyPrimaryAllianceLeverDAO.save(projectExpectedStudyPrimaryAllianceLever);
    Phase phase = phaseDAO.find(sourceInfo.getPhase().getId());


    if (phase.getDescription().equals(APConstants.PLANNING)) {
      if (phase.getNext() != null) {
        this.saveInfoPhase(phase.getNext(), projectExpectedStudyPrimaryAllianceLever);
      }
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInfoPhase(upkeepPhase, projectExpectedStudyPrimaryAllianceLever);
        }
      }
    }


    return sourceInfo;
  }


}
