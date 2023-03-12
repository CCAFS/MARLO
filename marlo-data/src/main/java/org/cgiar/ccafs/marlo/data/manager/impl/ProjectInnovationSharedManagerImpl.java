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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationSharedDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSharedManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationShared;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationSharedManagerImpl implements ProjectInnovationSharedManager {


  private ProjectInnovationSharedDAO projectInnovationSharedDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectInnovationSharedManagerImpl(ProjectInnovationSharedDAO projectInnovationSharedDAO, PhaseDAO phaseDAO) {
    this.projectInnovationSharedDAO = projectInnovationSharedDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectInnovationShared(long projectInnovationSharedId) {

    ProjectInnovationShared projectInnovationShared = this.getProjectInnovationSharedById(projectInnovationSharedId);

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (projectInnovationShared.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectInnovationShared.getPhase().getNext() != null) {
      this.deleteProjectInnovationSharedPhase(projectInnovationShared.getPhase().getNext(),
        projectInnovationShared.getProjectInnovation().getId(), projectInnovationShared);
    }

    if (projectInnovationShared.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (projectInnovationShared.getPhase().getNext() != null
        && projectInnovationShared.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = projectInnovationShared.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectInnovationSharedPhase(upkeepPhase, projectInnovationShared.getProjectInnovation().getId(),
            projectInnovationShared);
        }
      }
    }

    projectInnovationSharedDAO.deleteProjectInnovationShared(projectInnovationSharedId);
  }

  public void deleteProjectInnovationSharedPhase(Phase next, long innovationID,
    ProjectInnovationShared projectInnovationShared) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationShared> projectInnovationShareds = phase.getProjectInnovationShareds().stream()
      .filter(c -> c.isActive() && c.getProjectInnovation().getId().longValue() == innovationID
        && c.getProject().getId().equals(projectInnovationShared.getProject().getId()))
      .collect(Collectors.toList());
    for (ProjectInnovationShared projectInnovationSharedDB : projectInnovationShareds) {
      projectInnovationSharedDAO.deleteProjectInnovationShared(projectInnovationSharedDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationSharedPhase(phase.getNext(), innovationID, projectInnovationShared);
    }
  }

  @Override
  public boolean existProjectInnovationShared(long projectInnovationSharedID) {

    return projectInnovationSharedDAO.existProjectInnovationShared(projectInnovationSharedID);
  }

  @Override
  public List<ProjectInnovationShared> findAll() {

    return projectInnovationSharedDAO.findAll();

  }

  @Override
  public List<ProjectInnovationShared> getByProjectAndPhase(long projectId, long phaseId) {
    return projectInnovationSharedDAO.getByProjectAndPhase(projectId, phaseId);

  }

  @Override
  public ProjectInnovationShared getProjectInnovationSharedById(long projectInnovationSharedID) {

    return projectInnovationSharedDAO.find(projectInnovationSharedID);
  }

  @Override
  public ProjectInnovationShared saveProjectInnovationShared(ProjectInnovationShared projectInnovationShared) {

    ProjectInnovationShared shared = projectInnovationSharedDAO.save(projectInnovationShared);
    Phase currentPhase = shared.getPhase();

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.saveProjectInnovationSharedPhase(currentPhase.getNext(), shared.getProjectInnovation().getId(),
        projectInnovationShared);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectInnovationSharedPhase(upkeepPhase, shared.getProjectInnovation().getId(),
            projectInnovationShared);
        }
      }
    }


    return shared;
  }

  public void saveProjectInnovationSharedPhase(Phase next, long innovationID,
    ProjectInnovationShared projectInnovationShared) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationShared> projectInnovationShareds = phase.getProjectInnovationShareds().stream()
      .filter(c -> c.isActive() && c.getProjectInnovation().getId().longValue() == innovationID
        && c.getProject().getId().equals(projectInnovationShared.getProject().getId()))
      .collect(Collectors.toList());

    if (projectInnovationShareds.isEmpty()) {
      ProjectInnovationShared projectInnovationSharedAdd = new ProjectInnovationShared();
      projectInnovationSharedAdd.setProjectInnovation(projectInnovationShared.getProjectInnovation());
      projectInnovationSharedAdd.setPhase(phase);
      projectInnovationSharedAdd.setProject(projectInnovationShared.getProject());
      projectInnovationSharedDAO.save(projectInnovationSharedAdd);
    }


    if (phase.getNext() != null) {
      this.saveProjectInnovationSharedPhase(phase.getNext(), innovationID, projectInnovationShared);
    }
  }


}
