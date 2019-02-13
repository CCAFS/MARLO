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
import org.cgiar.ccafs.marlo.data.dao.ProjectPolicySubIdoDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicySubIdoManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicySubIdoManagerImpl implements ProjectPolicySubIdoManager {


  private ProjectPolicySubIdoDAO projectPolicySubIdoDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectPolicySubIdoManagerImpl(ProjectPolicySubIdoDAO projectPolicySubIdoDAO, PhaseDAO phaseDAO) {
    this.projectPolicySubIdoDAO = projectPolicySubIdoDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectPolicySubIdo(long projectPolicySubIdoId) {

    ProjectPolicySubIdo projectPolicySubIdo = this.getProjectPolicySubIdoById(projectPolicySubIdoId);

    if (projectPolicySubIdo.getPhase().getNext() != null) {
      this.deleteProjectPolicySubIdoPhase(projectPolicySubIdo.getPhase().getNext(),
        projectPolicySubIdo.getProjectPolicy().getId(), projectPolicySubIdo);
    }

    projectPolicySubIdoDAO.deleteProjectPolicySubIdo(projectPolicySubIdoId);
  }


  public void deleteProjectPolicySubIdoPhase(Phase next, long policyID, ProjectPolicySubIdo projectPolicySubIdo) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicySubIdo> projectPolicySubIdos = phase.getProjectPolicySubIdos().stream()
      .filter(c -> c.isActive() && c.getProjectPolicy().getId().longValue() == policyID
        && c.getSrfSubIdo().getId().equals(projectPolicySubIdo.getSrfSubIdo().getId()))
      .collect(Collectors.toList());

    for (ProjectPolicySubIdo projectPolicySubIdoDB : projectPolicySubIdos) {
      projectPolicySubIdoDAO.deleteProjectPolicySubIdo(projectPolicySubIdoDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectPolicySubIdoPhase(phase.getNext(), policyID, projectPolicySubIdo);
    }
  }

  @Override
  public boolean existProjectPolicySubIdo(long projectPolicySubIdoID) {

    return projectPolicySubIdoDAO.existProjectPolicySubIdo(projectPolicySubIdoID);
  }

  @Override
  public List<ProjectPolicySubIdo> findAll() {

    return projectPolicySubIdoDAO.findAll();

  }

  @Override
  public ProjectPolicySubIdo getProjectPolicySubIdoById(long projectPolicySubIdoID) {

    return projectPolicySubIdoDAO.find(projectPolicySubIdoID);
  }

  public void savePolicySubIdoPhase(Phase next, long policyID, ProjectPolicySubIdo projectPolicySubIdo) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicySubIdo> projectPolicySubIdos =
      phase.getProjectPolicySubIdos().stream().filter(c -> c.getProjectPolicy().getId().longValue() == policyID
        && c.getSrfSubIdo().getId().equals(projectPolicySubIdo.getSrfSubIdo().getId())).collect(Collectors.toList());

    if (projectPolicySubIdos.isEmpty()) {
      ProjectPolicySubIdo projectPolicySubIdoAdd = new ProjectPolicySubIdo();
      projectPolicySubIdoAdd.setProjectPolicy(projectPolicySubIdo.getProjectPolicy());
      projectPolicySubIdoAdd.setPhase(phase);
      projectPolicySubIdoAdd.setSrfSubIdo(projectPolicySubIdo.getSrfSubIdo());
      projectPolicySubIdoDAO.save(projectPolicySubIdoAdd);
    }


    if (phase.getNext() != null) {
      this.savePolicySubIdoPhase(phase.getNext(), policyID, projectPolicySubIdo);
    }
  }

  @Override
  public ProjectPolicySubIdo saveProjectPolicySubIdo(ProjectPolicySubIdo projectPolicySubIdo) {

    ProjectPolicySubIdo subIdo = projectPolicySubIdoDAO.save(projectPolicySubIdo);

    Phase phase = phaseDAO.find(subIdo.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (subIdo.getPhase().getNext() != null) {
        this.savePolicySubIdoPhase(projectPolicySubIdo.getPhase().getNext(),
          projectPolicySubIdo.getProjectPolicy().getId(), projectPolicySubIdo);
      }
    }
    return subIdo;
  }


}
