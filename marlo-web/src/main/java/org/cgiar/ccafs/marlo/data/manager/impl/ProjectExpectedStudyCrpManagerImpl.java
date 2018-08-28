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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyCrpDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCrpManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyCrpManagerImpl implements ProjectExpectedStudyCrpManager {


  private ProjectExpectedStudyCrpDAO projectExpectedStudyCrpDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudyCrpManagerImpl(ProjectExpectedStudyCrpDAO projectExpectedStudyCrpDAO, PhaseDAO phaseDAO) {
    this.projectExpectedStudyCrpDAO = projectExpectedStudyCrpDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyCrp(long projectExpectedStudyCrpId) {

    ProjectExpectedStudyCrp projectExpectedStudyCrp = this.getProjectExpectedStudyCrpById(projectExpectedStudyCrpId);
    Phase currentPhase = projectExpectedStudyCrp.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteProjectExpectedStudyCrpPhase(currentPhase.getNext(),
          projectExpectedStudyCrp.getProjectExpectedStudy().getId(), projectExpectedStudyCrp);
      }
    }
    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.deleteProjectExpectedStudyCrpPhase(upkeepPhase,
    // projectExpectedStudyCrp.getProjectExpectedStudy().getId(), projectExpectedStudyCrp);
    // }
    // }
    // }


    projectExpectedStudyCrpDAO.deleteProjectExpectedStudyCrp(projectExpectedStudyCrpId);
  }

  public void deleteProjectExpectedStudyCrpPhase(Phase next, long expectedID,
    ProjectExpectedStudyCrp projectExpectedStudyCrp) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyCrp> projectExpectedStudyCrps = phase.getProjectExpectedStudyCrps().stream()
      .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getGlobalUnit().getId().equals(projectExpectedStudyCrp.getGlobalUnit().getId()))
      .collect(Collectors.toList());
    for (ProjectExpectedStudyCrp projectExpectedStudyCrpDB : projectExpectedStudyCrps) {
      projectExpectedStudyCrpDAO.deleteProjectExpectedStudyCrp(projectExpectedStudyCrpDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyCrpPhase(phase.getNext(), expectedID, projectExpectedStudyCrp);
    }
  }

  @Override
  public boolean existProjectExpectedStudyCrp(long projectExpectedStudyCrpID) {

    return projectExpectedStudyCrpDAO.existProjectExpectedStudyCrp(projectExpectedStudyCrpID);
  }

  @Override
  public List<ProjectExpectedStudyCrp> findAll() {

    return projectExpectedStudyCrpDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyCrp getProjectExpectedStudyCrpById(long projectExpectedStudyCrpID) {

    return projectExpectedStudyCrpDAO.find(projectExpectedStudyCrpID);
  }

  public void saveExpectedStudyCrpPhase(Phase next, long expectedID, ProjectExpectedStudyCrp projectExpectedStudyCrp) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyCrp> projectExpectedStudyCrps = phase.getProjectExpectedStudyCrps().stream()
      .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getGlobalUnit().getId().equals(projectExpectedStudyCrp.getGlobalUnit().getId()))
      .collect(Collectors.toList());

    if (projectExpectedStudyCrps.isEmpty()) {
      ProjectExpectedStudyCrp projectExpectedStudyCrpAdd = new ProjectExpectedStudyCrp();
      projectExpectedStudyCrpAdd.setProjectExpectedStudy(projectExpectedStudyCrp.getProjectExpectedStudy());
      projectExpectedStudyCrpAdd.setPhase(phase);
      projectExpectedStudyCrpAdd.setGlobalUnit(projectExpectedStudyCrp.getGlobalUnit());
      projectExpectedStudyCrpDAO.save(projectExpectedStudyCrpAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyCrpPhase(phase.getNext(), expectedID, projectExpectedStudyCrp);
    }
  }

  @Override
  public ProjectExpectedStudyCrp saveProjectExpectedStudyCrp(ProjectExpectedStudyCrp projectExpectedStudyCrp) {

    ProjectExpectedStudyCrp projectExpectedStudyCrpResult = projectExpectedStudyCrpDAO.save(projectExpectedStudyCrp);
    Phase currentPhase = projectExpectedStudyCrpResult.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudyCrpPhase(currentPhase.getNext(),
          projectExpectedStudyCrpResult.getProjectExpectedStudy().getId(), projectExpectedStudyCrp);
      }
    }
    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.saveExpectedStudyCrpPhase(upkeepPhase, projectExpectedStudyCrpResult.getProjectExpectedStudy().getId(),
    // projectExpectedStudyCrp);
    // }
    // }
    // }

    return projectExpectedStudyCrpResult;
  }


}
