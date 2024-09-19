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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudySdgAllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySdgAllianceLeverManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySdgAllianceLever;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudySdgAllianceLeverManagerImpl implements ProjectExpectedStudySdgAllianceLeverManager {


  private ProjectExpectedStudySdgAllianceLeverDAO projectExpectedStudySdgAllianceLeverDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectExpectedStudySdgAllianceLeverManagerImpl(
    ProjectExpectedStudySdgAllianceLeverDAO projectExpectedStudySdgAllianceLeverDAO, PhaseDAO phaseDAO) {
    this.projectExpectedStudySdgAllianceLeverDAO = projectExpectedStudySdgAllianceLeverDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudySdgAllianceLever(long projectExpectedStudySdgAllianceLeverId) {


    ProjectExpectedStudySdgAllianceLever projectExpectedStudySdgAllianceLever =
      this.getProjectExpectedStudySdgAllianceLeverById(projectExpectedStudySdgAllianceLeverId);
    Phase currentPhase = projectExpectedStudySdgAllianceLever.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudySdgAllianceLeverPhase(currentPhase.getNext(),
        projectExpectedStudySdgAllianceLever);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectExpectedStudySdgAllianceLeverPhase(upkeepPhase.getNext(),
            projectExpectedStudySdgAllianceLever);
        }
      }
    }

    projectExpectedStudySdgAllianceLeverDAO
      .deleteProjectExpectedStudySdgAllianceLever(projectExpectedStudySdgAllianceLeverId);
  }

  public void deleteProjectExpectedStudySdgAllianceLeverPhase(Phase next,
    ProjectExpectedStudySdgAllianceLever projectExpectedStudySdgAllianceLever) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudySdgAllianceLever> projectExpectedStudySdgAllianceLeverList = phase
      .getProjectExpectedStudySdgAllianceLevers().stream()
      .filter(c -> c.isActive()
        && c.getProjectExpectedStudy().getId() == projectExpectedStudySdgAllianceLever.getProjectExpectedStudy().getId()
        && c.getAllianceLever() == projectExpectedStudySdgAllianceLever.getAllianceLever()
        && c.getsDGContribution() == projectExpectedStudySdgAllianceLever.getsDGContribution()
        && c.getIsPrimary() == projectExpectedStudySdgAllianceLever.getIsPrimary())
      .collect(Collectors.toList());
    for (ProjectExpectedStudySdgAllianceLever projectExpectedStudySdgAllianceLeverTmp : projectExpectedStudySdgAllianceLeverList) {
      projectExpectedStudySdgAllianceLeverDAO
        .deleteProjectExpectedStudySdgAllianceLever(projectExpectedStudySdgAllianceLeverTmp.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudySdgAllianceLeverPhase(phase.getNext(), projectExpectedStudySdgAllianceLever);
    }
  }

  @Override
  public boolean existProjectExpectedStudySdgAllianceLever(long projectExpectedStudySdgAllianceLeverID) {

    return projectExpectedStudySdgAllianceLeverDAO
      .existProjectExpectedStudySdgAllianceLever(projectExpectedStudySdgAllianceLeverID);
  }

  @Override
  public List<ProjectExpectedStudySdgAllianceLever> findAll() {

    return projectExpectedStudySdgAllianceLeverDAO.findAll();

  }

  @Override
  public ProjectExpectedStudySdgAllianceLever
    getProjectExpectedStudySdgAllianceLeverById(long projectExpectedStudySdgAllianceLeverID) {

    return projectExpectedStudySdgAllianceLeverDAO.find(projectExpectedStudySdgAllianceLeverID);
  }

  /**
   * Reply the information to the next Phases
   * 
   * @param next - The next Phase
   * @param projectExpectedStudySdgAllianceLever - The project expected study publication SdgAllianceLever into the
   *        database.
   */
  public void saveInfoPhase(Phase next, ProjectExpectedStudySdgAllianceLever projectExpectedStudySdgAllianceLever) {

    Phase phase = phaseDAO.find(next.getId());
    List<ProjectExpectedStudySdgAllianceLever> projectExpectedStudySdgAllianceLeverList =
      phase.getProjectExpectedStudySdgAllianceLevers().stream()
        .filter(c -> c.getProjectExpectedStudy().getId().longValue() == projectExpectedStudySdgAllianceLever
          .getProjectExpectedStudy().getId()
          && c.getAllianceLever() == projectExpectedStudySdgAllianceLever.getAllianceLever()
          && c.getsDGContribution() == projectExpectedStudySdgAllianceLever.getsDGContribution()
          && c.getIsPrimary() == projectExpectedStudySdgAllianceLever.getIsPrimary())
        .collect(Collectors.toList());
    if (projectExpectedStudySdgAllianceLeverList.isEmpty()) {

      ProjectExpectedStudySdgAllianceLever projectExpectedStudySdgAllianceLeverAdd =
        new ProjectExpectedStudySdgAllianceLever();

      projectExpectedStudySdgAllianceLeverAdd
        .setProjectExpectedStudy(projectExpectedStudySdgAllianceLever.getProjectExpectedStudy());
      projectExpectedStudySdgAllianceLeverAdd.setPhase(phase);
      projectExpectedStudySdgAllianceLeverAdd.setAllianceLever(projectExpectedStudySdgAllianceLever.getAllianceLever());
      projectExpectedStudySdgAllianceLeverAdd
        .setsDGContribution(projectExpectedStudySdgAllianceLever.getsDGContribution());
      projectExpectedStudySdgAllianceLeverAdd.setLeverComments(projectExpectedStudySdgAllianceLever.getLeverComments());
      projectExpectedStudySdgAllianceLeverAdd.setIsPrimary(projectExpectedStudySdgAllianceLever.getIsPrimary());


      projectExpectedStudySdgAllianceLeverDAO.save(projectExpectedStudySdgAllianceLeverAdd);
    }

    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectExpectedStudySdgAllianceLever);
    }
  }


  @Override
  public ProjectExpectedStudySdgAllianceLever saveProjectExpectedStudySdgAllianceLever(
    ProjectExpectedStudySdgAllianceLever projectExpectedStudySdgAllianceLever) {


    ProjectExpectedStudySdgAllianceLever sourceInfo =
      projectExpectedStudySdgAllianceLeverDAO.save(projectExpectedStudySdgAllianceLever);
    Phase phase = phaseDAO.find(sourceInfo.getPhase().getId());

    if (phase.getDescription().equals(APConstants.PLANNING)) {
      if (phase.getNext() != null) {
        this.saveInfoPhase(phase.getNext(), projectExpectedStudySdgAllianceLever);
      }
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInfoPhase(upkeepPhase, projectExpectedStudySdgAllianceLever);
        }
      }
    }


    return sourceInfo;
  }


}
