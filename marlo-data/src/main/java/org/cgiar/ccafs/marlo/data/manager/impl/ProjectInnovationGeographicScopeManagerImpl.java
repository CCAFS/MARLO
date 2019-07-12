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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationGeographicScopeManagerImpl implements ProjectInnovationGeographicScopeManager {


  private ProjectInnovationGeographicScopeDAO projectInnovationGeographicScopeDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectInnovationGeographicScopeManagerImpl(
    ProjectInnovationGeographicScopeDAO projectInnovationGeographicScopeDAO, PhaseDAO phaseDAO) {
    this.projectInnovationGeographicScopeDAO = projectInnovationGeographicScopeDAO;
    this.phaseDAO = phaseDAO;

  }

  @Override
  public void deleteProjectInnovationGeographicScope(long projectInnovationGeographicScopeId) {

    ProjectInnovationGeographicScope projectInnovationGeographicScope =
      this.getProjectInnovationGeographicScopeById(projectInnovationGeographicScopeId);

    if (projectInnovationGeographicScope.getPhase().getNext() != null) {
      this.deleteProjectInnovationGeographicScopePhase(projectInnovationGeographicScope.getPhase().getNext(),
        projectInnovationGeographicScope.getProjectInnovation().getId(), projectInnovationGeographicScope);
    }

    projectInnovationGeographicScopeDAO.deleteProjectInnovationGeographicScope(projectInnovationGeographicScopeId);
  }

  public void deleteProjectInnovationGeographicScopePhase(Phase next, long innovationID,
    ProjectInnovationGeographicScope projectInnovationGeographicScope) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationGeographicScope> projectInnovationGeographicScopes = phase
      .getProjectInnovationGeographicScopes().stream()
      .filter(c -> c.isActive() && c.getProjectInnovation().getId().longValue() == innovationID && c
        .getRepIndGeographicScope().getId().equals(projectInnovationGeographicScope.getRepIndGeographicScope().getId()))
      .collect(Collectors.toList());
    for (ProjectInnovationGeographicScope projectInnovationGeographicScopeDB : projectInnovationGeographicScopes) {
      projectInnovationGeographicScopeDAO
        .deleteProjectInnovationGeographicScope(projectInnovationGeographicScopeDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationGeographicScopePhase(phase.getNext(), innovationID, projectInnovationGeographicScope);
    }
  }

  @Override
  public boolean existProjectInnovationGeographicScope(long projectInnovationGeographicScopeID) {

    return projectInnovationGeographicScopeDAO
      .existProjectInnovationGeographicScope(projectInnovationGeographicScopeID);
  }

  @Override
  public List<ProjectInnovationGeographicScope> findAll() {

    return projectInnovationGeographicScopeDAO.findAll();

  }

  @Override
  public ProjectInnovationGeographicScope
    getProjectInnovationGeographicScopeById(long projectInnovationGeographicScopeID) {

    return projectInnovationGeographicScopeDAO.find(projectInnovationGeographicScopeID);
  }

  public void saveInnovationGeographicScopePhase(Phase next, long innovationid,
    ProjectInnovationGeographicScope projectInnovationGeographicScope) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationGeographicScope> projectInnovationGeographicScopes =
      phase.getProjectInnovationGeographicScopes().stream()
        .filter(c -> c.getProjectInnovation().getId().longValue() == innovationid && c.getRepIndGeographicScope()
          .getId().equals(projectInnovationGeographicScope.getRepIndGeographicScope().getId()))
        .collect(Collectors.toList());

    if (projectInnovationGeographicScopes.isEmpty()) {
      ProjectInnovationGeographicScope projectInnovationGeographicScopeAdd = new ProjectInnovationGeographicScope();
      projectInnovationGeographicScopeAdd.setProjectInnovation(projectInnovationGeographicScope.getProjectInnovation());
      projectInnovationGeographicScopeAdd.setPhase(phase);
      projectInnovationGeographicScopeAdd
        .setRepIndGeographicScope(projectInnovationGeographicScope.getRepIndGeographicScope());
      projectInnovationGeographicScopeDAO.save(projectInnovationGeographicScopeAdd);
    }


    if (phase.getNext() != null) {
      this.saveInnovationGeographicScopePhase(phase.getNext(), innovationid, projectInnovationGeographicScope);
    }
  }

  @Override
  public ProjectInnovationGeographicScope
    saveProjectInnovationGeographicScope(ProjectInnovationGeographicScope projectInnovationGeographicScope) {

    ProjectInnovationGeographicScope scope = projectInnovationGeographicScopeDAO.save(projectInnovationGeographicScope);

    Phase phase = phaseDAO.find(scope.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (scope.getPhase().getNext() != null) {
        this.saveInnovationGeographicScopePhase(scope.getPhase().getNext(), scope.getProjectInnovation().getId(),
          projectInnovationGeographicScope);
      }
    }
    return scope;
  }


}
