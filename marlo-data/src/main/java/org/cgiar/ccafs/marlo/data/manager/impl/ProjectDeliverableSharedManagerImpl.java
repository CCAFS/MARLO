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
import org.cgiar.ccafs.marlo.data.dao.ProjectDeliverableSharedDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectDeliverableSharedManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectDeliverableShared;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectDeliverableSharedManagerImpl implements ProjectDeliverableSharedManager {


  private ProjectDeliverableSharedDAO projectDeliverableSharedDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectDeliverableSharedManagerImpl(ProjectDeliverableSharedDAO projectDeliverableSharedDAO,
    PhaseDAO phaseDAO) {
    this.projectDeliverableSharedDAO = projectDeliverableSharedDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectDeliverableShared(long projectDeliverableSharedId) {

    ProjectDeliverableShared projectDeliverableShared =
      this.getProjectDeliverableSharedById(projectDeliverableSharedId);

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (projectDeliverableShared.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectDeliverableShared.getPhase().getNext() != null) {
      this.deleteProjectDeliverableSharedPhase(projectDeliverableShared.getPhase().getNext(),
        projectDeliverableShared.getDeliverable().getId(), projectDeliverableShared);
    }

    if (projectDeliverableShared.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (projectDeliverableShared.getPhase().getNext() != null
        && projectDeliverableShared.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = projectDeliverableShared.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectDeliverableSharedPhase(upkeepPhase, projectDeliverableShared.getDeliverable().getId(),
            projectDeliverableShared);
        }
      }
    }

    projectDeliverableSharedDAO.deleteProjectDeliverableShared(projectDeliverableSharedId);
  }

  public void deleteProjectDeliverableSharedPhase(Phase next, long innovationID,
    ProjectDeliverableShared projectDeliverableShared) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectDeliverableShared> projectDeliverableShareds = phase.getProjectDeliverableShareds().stream()
      .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == innovationID
        && c.getProject().getId().equals(projectDeliverableShared.getProject().getId()))
      .collect(Collectors.toList());
    for (ProjectDeliverableShared projectDeliverableSharedDB : projectDeliverableShareds) {
      projectDeliverableSharedDAO.deleteProjectDeliverableShared(projectDeliverableSharedDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectDeliverableSharedPhase(phase.getNext(), innovationID, projectDeliverableShared);
    }
  }

  @Override
  public boolean existProjectDeliverableShared(long projectDeliverableSharedID) {

    return projectDeliverableSharedDAO.existProjectDeliverableShared(projectDeliverableSharedID);
  }

  @Override
  public List<ProjectDeliverableShared> findAll() {

    return projectDeliverableSharedDAO.findAll();

  }

  @Override
  public List<ProjectDeliverableShared> getByDeliverable(long deliverableId, long phaseId) {
    return projectDeliverableSharedDAO.getByDeliverable(deliverableId, phaseId);
  }

  @Override
  public List<ProjectDeliverableShared> getByPhase(long phaseId) {
    return projectDeliverableSharedDAO.getByPhase(phaseId);
  }

  @Override
  public List<ProjectDeliverableShared> getByProjectAndPhase(long projectId, long phaseId) {
    return projectDeliverableSharedDAO.getByProjectAndPhase(projectId, phaseId);
  }

  @Override
  public ProjectDeliverableShared getProjectDeliverableSharedById(long projectDeliverableSharedID) {

    return projectDeliverableSharedDAO.find(projectDeliverableSharedID);
  }


  @Override
  public ProjectDeliverableShared saveProjectDeliverableShared(ProjectDeliverableShared projectDeliverableShared) {

    ProjectDeliverableShared shared = projectDeliverableSharedDAO.save(projectDeliverableShared);
    Phase currentPhase = shared.getPhase();

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (currentPhase.getDescription().equals(APConstants.PLANNING) && currentPhase.getNext() != null) {
      this.saveProjectDeliverableSharedPhase(currentPhase.getNext(), shared.getDeliverable().getId(),
        projectDeliverableShared);
    }

    if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
      if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
        Phase upkeepPhase = currentPhase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectDeliverableSharedPhase(upkeepPhase, shared.getDeliverable().getId(),
            projectDeliverableShared);
        }
      }
    }


    return shared;
  }

  public void saveProjectDeliverableSharedPhase(Phase next, long innovationID,
    ProjectDeliverableShared projectDeliverableShared) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectDeliverableShared> projectDeliverableShareds = phase.getProjectDeliverableShareds().stream()
      .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == innovationID
        && c.getProject().getId().equals(projectDeliverableShared.getProject().getId()))
      .collect(Collectors.toList());

    if (projectDeliverableShareds.isEmpty()) {
      ProjectDeliverableShared projectDeliverableSharedAdd = new ProjectDeliverableShared();
      projectDeliverableSharedAdd.setDeliverable(projectDeliverableShared.getDeliverable());
      projectDeliverableSharedAdd.setPhase(phase);
      projectDeliverableSharedAdd.setProject(projectDeliverableShared.getProject());
      projectDeliverableSharedDAO.save(projectDeliverableSharedAdd);
    }


    if (phase.getNext() != null) {
      this.saveProjectDeliverableSharedPhase(phase.getNext(), innovationID, projectDeliverableShared);
    }
  }


}
