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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyRelatedAllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRelatedAllianceLeverManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRelatedAllianceLever;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyRelatedAllianceLeverManagerImpl
  implements ProjectExpectedStudyRelatedAllianceLeverManager {


  private ProjectExpectedStudyRelatedAllianceLeverDAO projectExpectedStudyRelatedAllianceLeverDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyRelatedAllianceLeverManagerImpl(
    ProjectExpectedStudyRelatedAllianceLeverDAO projectExpectedStudyRelatedAllianceLeverDAO, PhaseDAO phaseDAO) {
    this.projectExpectedStudyRelatedAllianceLeverDAO = projectExpectedStudyRelatedAllianceLeverDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyRelatedAllianceLever(long projectExpectedStudyRelatedAllianceLeverId) {

    ProjectExpectedStudyRelatedAllianceLever projectExpectedStudyRelatedAllianceLever =
      this.getProjectExpectedStudyRelatedAllianceLeverById(projectExpectedStudyRelatedAllianceLeverId);
    Phase currentPhase = projectExpectedStudyRelatedAllianceLever.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyRelatedAllianceLeverPhase(currentPhase.getNext(),
        projectExpectedStudyRelatedAllianceLever);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudyRelatedAllianceLeverPhase(upkeepPhase.getNext(),
            projectExpectedStudyRelatedAllianceLever);
        }
      }
    }


    projectExpectedStudyRelatedAllianceLeverDAO
      .deleteProjectExpectedStudyRelatedAllianceLever(projectExpectedStudyRelatedAllianceLeverId);
  }

  public void deleteProjectExpectedStudyRelatedAllianceLeverPhase(Phase next,
    ProjectExpectedStudyRelatedAllianceLever projectExpectedStudyRelatedAllianceLever) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyRelatedAllianceLever> projectExpectedStudyRelatedAllianceLeverList =
      phase.getProjectExpectedStudyRelatedAllianceLever().stream()
        .filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId() == projectExpectedStudyRelatedAllianceLever.getProjectExpectedStudy()
            .getId()
          && c.getRelatedAllianceLever().getId() == projectExpectedStudyRelatedAllianceLever.getRelatedAllianceLever()
            .getId())
        .collect(Collectors.toList());
    for (ProjectExpectedStudyRelatedAllianceLever projectExpectedStudyRelatedAllianceLeverTmp : projectExpectedStudyRelatedAllianceLeverList) {
      projectExpectedStudyRelatedAllianceLeverDAO
        .deleteProjectExpectedStudyRelatedAllianceLever(projectExpectedStudyRelatedAllianceLeverTmp.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyRelatedAllianceLeverPhase(phase.getNext(),
        projectExpectedStudyRelatedAllianceLever);
    }
  }

  @Override
  public boolean existProjectExpectedStudyRelatedAllianceLever(long projectExpectedStudyRelatedAllianceLeverID) {

    return projectExpectedStudyRelatedAllianceLeverDAO
      .existProjectExpectedStudyRelatedAllianceLever(projectExpectedStudyRelatedAllianceLeverID);
  }

  @Override
  public List<ProjectExpectedStudyRelatedAllianceLever> findAll() {

    return projectExpectedStudyRelatedAllianceLeverDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyRelatedAllianceLever
    getProjectExpectedStudyRelatedAllianceLeverById(long projectExpectedStudyRelatedAllianceLeverID) {

    return projectExpectedStudyRelatedAllianceLeverDAO.find(projectExpectedStudyRelatedAllianceLeverID);
  }

  /**
   * Reply the information to the next Phases
   * 
   * @param next - The next Phase
   * @param projectExpectedStudyPrimaryAllianceLever - The project expected study primary alliance lever into the
   *        database.
   */
  public void saveInfoPhase(Phase next,
    ProjectExpectedStudyRelatedAllianceLever projectExpectedStudyRelatedAllianceLever) {

    Phase phase = phaseDAO.find(next.getId());
    List<ProjectExpectedStudyRelatedAllianceLever> projectExpectedStudyRelatedAllianceLeverList =
      phase.getProjectExpectedStudyRelatedAllianceLever().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().longValue() == projectExpectedStudyRelatedAllianceLever
          .getProjectExpectedStudy().getId()
          && c.getRelatedAllianceLever().getId() == projectExpectedStudyRelatedAllianceLever.getRelatedAllianceLever()
            .getId())
        .collect(Collectors.toList());
    if (projectExpectedStudyRelatedAllianceLeverList.isEmpty()) {

      ProjectExpectedStudyRelatedAllianceLever projectExpectedStudyRelatedAllianceLeverAdd =
        new ProjectExpectedStudyRelatedAllianceLever();

      projectExpectedStudyRelatedAllianceLeverAdd
        .setProjectExpectedStudy(projectExpectedStudyRelatedAllianceLever.getProjectExpectedStudy());
      projectExpectedStudyRelatedAllianceLeverAdd.setPhase(phase);
      projectExpectedStudyRelatedAllianceLeverAdd
        .setRelatedAllianceLever(projectExpectedStudyRelatedAllianceLever.getRelatedAllianceLever());

      projectExpectedStudyRelatedAllianceLeverDAO.save(projectExpectedStudyRelatedAllianceLeverAdd);
    }

    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectExpectedStudyRelatedAllianceLever);
    }
  }


  @Override
  public ProjectExpectedStudyRelatedAllianceLever saveProjectExpectedStudyRelatedAllianceLever(
    ProjectExpectedStudyRelatedAllianceLever projectExpectedStudyRelatedAllianceLever) {

    ProjectExpectedStudyRelatedAllianceLever sourceInfo =
      projectExpectedStudyRelatedAllianceLeverDAO.save(projectExpectedStudyRelatedAllianceLever);
    Phase phase = phaseDAO.find(sourceInfo.getPhase().getId());


    if (phase.getDescription().equals(APConstants.PLANNING)) {
      if (phase.getNext() != null) {
        this.saveInfoPhase(phase.getNext(), projectExpectedStudyRelatedAllianceLever);
      }
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInfoPhase(upkeepPhase, projectExpectedStudyRelatedAllianceLever);
        }
      }
    }


    return sourceInfo;


  }


}
