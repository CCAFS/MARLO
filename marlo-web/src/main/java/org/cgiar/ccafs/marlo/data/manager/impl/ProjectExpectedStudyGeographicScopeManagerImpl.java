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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyGeographicScopeManagerImpl implements ProjectExpectedStudyGeographicScopeManager {


  private ProjectExpectedStudyGeographicScopeDAO projectExpectedStudyGeographicScopeDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectExpectedStudyGeographicScopeManagerImpl(
    ProjectExpectedStudyGeographicScopeDAO projectExpectedStudyGeographicScopeDAO, PhaseDAO phaseDAO) {
    this.projectExpectedStudyGeographicScopeDAO = projectExpectedStudyGeographicScopeDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectExpectedStudyGeographicScope(long projectExpectedStudyGeographicScopeId) {

    ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope =
      this.getProjectExpectedStudyGeographicScopeById(projectExpectedStudyGeographicScopeId);
    Phase currentPhase = projectExpectedStudyGeographicScope.getPhase();


    if (currentPhase.getNext() != null) {
      this.deleteProjectExpectedStudyGeographicScopePhase(currentPhase.getNext(),
        projectExpectedStudyGeographicScope.getProjectExpectedStudy().getId(), projectExpectedStudyGeographicScope);
    }


    projectExpectedStudyGeographicScopeDAO
      .deleteProjectExpectedStudyGeographicScope(projectExpectedStudyGeographicScopeId);
  }

  public void deleteProjectExpectedStudyGeographicScopePhase(Phase next, long expectedID,
    ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyGeographicScope> projectExpectedStudyGeographicScopes =
      phase.getProjectExpectedStudyGeographicScopes().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getRepIndGeographicScope().getId()
            .equals(projectExpectedStudyGeographicScope.getRepIndGeographicScope().getId()))
        .collect(Collectors.toList());
    for (ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScopeDB : projectExpectedStudyGeographicScopes) {
      projectExpectedStudyGeographicScopeDAO
        .deleteProjectExpectedStudyGeographicScope(projectExpectedStudyGeographicScopeDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyGeographicScopePhase(phase.getNext(), expectedID,
        projectExpectedStudyGeographicScope);
    }
  }

  @Override
  public boolean existProjectExpectedStudyGeographicScope(long projectExpectedStudyGeographicScopeID) {

    return projectExpectedStudyGeographicScopeDAO
      .existProjectExpectedStudyGeographicScope(projectExpectedStudyGeographicScopeID);
  }

  @Override
  public List<ProjectExpectedStudyGeographicScope> findAll() {

    return projectExpectedStudyGeographicScopeDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyGeographicScope
    getProjectExpectedStudyGeographicScopeById(long projectExpectedStudyGeographicScopeID) {

    return projectExpectedStudyGeographicScopeDAO.find(projectExpectedStudyGeographicScopeID);
  }

  public void saveExpectedStudyGeographicScopePhase(Phase next, long expectedID,
    ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyGeographicScope> projectExpectedStudyGeographicScopes =
      phase.getProjectExpectedStudyGeographicScopes().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
          && c.getRepIndGeographicScope().getId()
            .equals(projectExpectedStudyGeographicScope.getRepIndGeographicScope().getId()))
        .collect(Collectors.toList());

    if (projectExpectedStudyGeographicScopes.isEmpty()) {
      ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScopeAdd =
        new ProjectExpectedStudyGeographicScope();
      projectExpectedStudyGeographicScopeAdd
        .setProjectExpectedStudy(projectExpectedStudyGeographicScope.getProjectExpectedStudy());
      projectExpectedStudyGeographicScopeAdd.setPhase(phase);
      projectExpectedStudyGeographicScopeAdd
        .setRepIndGeographicScope(projectExpectedStudyGeographicScope.getRepIndGeographicScope());
      projectExpectedStudyGeographicScopeDAO.save(projectExpectedStudyGeographicScopeAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyGeographicScopePhase(phase.getNext(), expectedID, projectExpectedStudyGeographicScope);
    }
  }

  @Override
  public ProjectExpectedStudyGeographicScope
    saveProjectExpectedStudyGeographicScope(ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope) {

    ProjectExpectedStudyGeographicScope scope =
      projectExpectedStudyGeographicScopeDAO.save(projectExpectedStudyGeographicScope);
    Phase currentPhase = scope.getPhase();


    if (currentPhase.getNext() != null) {
      this.saveExpectedStudyGeographicScopePhase(currentPhase.getNext(),
        projectExpectedStudyGeographicScope.getProjectExpectedStudy().getId(), projectExpectedStudyGeographicScope);
    }


    return scope;
  }


}
