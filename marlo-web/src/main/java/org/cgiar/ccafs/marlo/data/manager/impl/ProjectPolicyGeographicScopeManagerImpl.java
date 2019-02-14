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
import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicyGeographicScopeManagerImpl implements ProjectPolicyGeographicScopeManager {


  private ProjectPolicyGeographicScopeDAO projectPolicyGeographicScopeDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectPolicyGeographicScopeManagerImpl(ProjectPolicyGeographicScopeDAO projectPolicyGeographicScopeDAO,
    PhaseDAO phaseDAO) {
    this.projectPolicyGeographicScopeDAO = projectPolicyGeographicScopeDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectPolicyGeographicScope(long projectPolicyGeographicScopeId) {

    ProjectPolicyGeographicScope projectPolicyGeographicScope =
      this.getProjectPolicyGeographicScopeById(projectPolicyGeographicScopeId);

    if (projectPolicyGeographicScope.getPhase().getNext() != null) {
      this.deleteProjectPolicyGeographicScopePhase(projectPolicyGeographicScope.getPhase().getNext(),
        projectPolicyGeographicScope.getProjectPolicy().getId(), projectPolicyGeographicScope);
    }

    projectPolicyGeographicScopeDAO.deleteProjectPolicyGeographicScope(projectPolicyGeographicScopeId);
  }

  public void deleteProjectPolicyGeographicScopePhase(Phase next, long policyID,
    ProjectPolicyGeographicScope projectPolicyGeographicScope) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyGeographicScope> projectPolicyGeographicScopes = phase.getProjectPolicyGeographicScopes().stream()
      .filter(c -> c.isActive() && c.getProjectPolicy().getId().longValue() == policyID
        && c.getRepIndGeographicScope().getId().equals(projectPolicyGeographicScope.getRepIndGeographicScope().getId()))
      .collect(Collectors.toList());

    for (ProjectPolicyGeographicScope projectPolicyGeographicScopeDB : projectPolicyGeographicScopes) {
      projectPolicyGeographicScopeDAO.deleteProjectPolicyGeographicScope(projectPolicyGeographicScopeDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectPolicyGeographicScopePhase(phase.getNext(), policyID, projectPolicyGeographicScope);
    }
  }

  @Override
  public boolean existProjectPolicyGeographicScope(long projectPolicyGeographicScopeID) {

    return projectPolicyGeographicScopeDAO.existProjectPolicyGeographicScope(projectPolicyGeographicScopeID);
  }

  @Override
  public List<ProjectPolicyGeographicScope> findAll() {

    return projectPolicyGeographicScopeDAO.findAll();

  }

  @Override
  public ProjectPolicyGeographicScope getProjectPolicyGeographicScopeById(long projectPolicyGeographicScopeID) {

    return projectPolicyGeographicScopeDAO.find(projectPolicyGeographicScopeID);
  }

  @Override
  public ProjectPolicyGeographicScope
    saveProjectPolicyGeographicScope(ProjectPolicyGeographicScope projectPolicyGeographicScope) {

    ProjectPolicyGeographicScope geographicScope = projectPolicyGeographicScopeDAO.save(projectPolicyGeographicScope);

    Phase phase = phaseDAO.find(geographicScope.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (geographicScope.getPhase().getNext() != null) {
        this.saveProjectPolicyGeographicScopePhase(projectPolicyGeographicScope.getPhase().getNext(),
          projectPolicyGeographicScope.getProjectPolicy().getId(), projectPolicyGeographicScope);
      }
    }
    return geographicScope;
  }

  public void saveProjectPolicyGeographicScopePhase(Phase next, long policyID,
    ProjectPolicyGeographicScope projectPolicyGeographicScope) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyGeographicScope> projectPolicyGeographicScopes = phase.getProjectPolicyGeographicScopes().stream()
      .filter(c -> c.getProjectPolicy().getId().longValue() == policyID
        && c.getRepIndGeographicScope().getId().equals(projectPolicyGeographicScope.getRepIndGeographicScope().getId()))
      .collect(Collectors.toList());

    if (projectPolicyGeographicScopes.isEmpty()) {
      ProjectPolicyGeographicScope projectPolicyGeographicScopeAdd = new ProjectPolicyGeographicScope();
      projectPolicyGeographicScopeAdd.setProjectPolicy(projectPolicyGeographicScope.getProjectPolicy());
      projectPolicyGeographicScopeAdd.setPhase(phase);
      projectPolicyGeographicScopeAdd.setRepIndGeographicScope(projectPolicyGeographicScope.getRepIndGeographicScope());
      projectPolicyGeographicScopeDAO.save(projectPolicyGeographicScopeAdd);
    }


    if (phase.getNext() != null) {
      this.saveProjectPolicyGeographicScopePhase(phase.getNext(), policyID, projectPolicyGeographicScope);
    }
  }


}
