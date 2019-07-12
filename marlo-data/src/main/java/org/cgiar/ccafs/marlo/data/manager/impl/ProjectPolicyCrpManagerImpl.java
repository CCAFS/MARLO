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
import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyCrpDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCrpManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrp;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicyCrpManagerImpl implements ProjectPolicyCrpManager {


  private ProjectPolicyCrpDAO projectPolicyCrpDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectPolicyCrpManagerImpl(ProjectPolicyCrpDAO projectPolicyCrpDAO, PhaseDAO phaseDAO) {
    this.projectPolicyCrpDAO = projectPolicyCrpDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectPolicyCrp(long projectPolicyCrpId) {

    ProjectPolicyCrp projectPolicyCrp = this.getProjectPolicyCrpById(projectPolicyCrpId);

    if (projectPolicyCrp.getPhase().getNext() != null) {
      this.deleteProjectPolicyCrpPhase(projectPolicyCrp.getPhase().getNext(),
        projectPolicyCrp.getProjectPolicy().getId(), projectPolicyCrp);
    }

    projectPolicyCrpDAO.deleteProjectPolicyCrp(projectPolicyCrpId);
  }

  public void deleteProjectPolicyCrpPhase(Phase next, long policyID, ProjectPolicyCrp projectPolicyCrp) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyCrp> projectPolicyCrps = phase.getProjectPolicyCrps().stream()
      .filter(c -> c.isActive() && c.getProjectPolicy().getId().longValue() == policyID
        && c.getGlobalUnit().getId().equals(projectPolicyCrp.getGlobalUnit().getId()))
      .collect(Collectors.toList());

    for (ProjectPolicyCrp projectPolicyCrpDB : projectPolicyCrps) {
      projectPolicyCrpDAO.deleteProjectPolicyCrp(projectPolicyCrpDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectPolicyCrpPhase(phase.getNext(), policyID, projectPolicyCrp);
    }
  }

  @Override
  public boolean existProjectPolicyCrp(long projectPolicyCrpID) {

    return projectPolicyCrpDAO.existProjectPolicyCrp(projectPolicyCrpID);
  }

  @Override
  public List<ProjectPolicyCrp> findAll() {

    return projectPolicyCrpDAO.findAll();

  }

  @Override
  public ProjectPolicyCrp getProjectPolicyCrpById(long projectPolicyCrpID) {

    return projectPolicyCrpDAO.find(projectPolicyCrpID);
  }


  public void savePolicyCrpPhase(Phase next, long policyID, ProjectPolicyCrp projectPolicyCrp) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyCrp> projectPolicyCrps =
      phase.getProjectPolicyCrps().stream().filter(c -> c.getProjectPolicy().getId().longValue() == policyID
        && c.getGlobalUnit().getId().equals(projectPolicyCrp.getGlobalUnit().getId())).collect(Collectors.toList());

    if (projectPolicyCrps.isEmpty()) {
      ProjectPolicyCrp projectPolicyCrpAdd = new ProjectPolicyCrp();
      projectPolicyCrpAdd.setProjectPolicy(projectPolicyCrp.getProjectPolicy());
      projectPolicyCrpAdd.setPhase(phase);
      projectPolicyCrpAdd.setGlobalUnit(projectPolicyCrp.getGlobalUnit());
      projectPolicyCrpDAO.save(projectPolicyCrpAdd);
    }


    if (phase.getNext() != null) {
      this.savePolicyCrpPhase(phase.getNext(), policyID, projectPolicyCrp);
    }
  }

  @Override
  public ProjectPolicyCrp saveProjectPolicyCrp(ProjectPolicyCrp projectPolicyCrp) {

    ProjectPolicyCrp crp = projectPolicyCrpDAO.save(projectPolicyCrp);

    Phase phase = phaseDAO.find(crp.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (crp.getPhase().getNext() != null) {
        this.savePolicyCrpPhase(crp.getPhase().getNext(), crp.getProjectPolicy().getId(), projectPolicyCrp);
      }
    }
    return crp;
  }


}
