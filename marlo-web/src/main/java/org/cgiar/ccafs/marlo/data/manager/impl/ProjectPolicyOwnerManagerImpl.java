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
import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyOwnerDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyOwnerManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyOwner;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicyOwnerManagerImpl implements ProjectPolicyOwnerManager {


  private ProjectPolicyOwnerDAO projectPolicyOwnerDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectPolicyOwnerManagerImpl(ProjectPolicyOwnerDAO projectPolicyOwnerDAO, PhaseDAO phaseDAO) {
    this.projectPolicyOwnerDAO = projectPolicyOwnerDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectPolicyOwner(long projectPolicyOwnerId) {

    ProjectPolicyOwner projectPolicyOwner = this.getProjectPolicyOwnerById(projectPolicyOwnerId);

    if (projectPolicyOwner.getPhase().getNext() != null) {
      this.deleteProjectPolicyOwnerPhase(projectPolicyOwner.getPhase().getNext(),
        projectPolicyOwner.getProjectPolicy().getId(), projectPolicyOwner);
    }

    projectPolicyOwnerDAO.deleteProjectPolicyOwner(projectPolicyOwnerId);
  }

  public void deleteProjectPolicyOwnerPhase(Phase next, long policyID, ProjectPolicyOwner projectPolicyOwner) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyOwner> projectPolicyOwners = phase.getProjectPolicyOwners().stream()
      .filter(c -> c.isActive() && c.getProjectPolicy().getId().longValue() == policyID
        && c.getRepIndPolicyType().getId().equals(projectPolicyOwner.getRepIndPolicyType().getId()))
      .collect(Collectors.toList());

    for (ProjectPolicyOwner projectPolicyOwnerDB : projectPolicyOwners) {
      projectPolicyOwnerDAO.deleteProjectPolicyOwner(projectPolicyOwnerDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectPolicyOwnerPhase(phase.getNext(), policyID, projectPolicyOwner);
    }
  }


  @Override
  public boolean existProjectPolicyOwner(long projectPolicyOwnerID) {

    return projectPolicyOwnerDAO.existProjectPolicyOwner(projectPolicyOwnerID);
  }

  @Override
  public List<ProjectPolicyOwner> findAll() {

    return projectPolicyOwnerDAO.findAll();

  }

  @Override
  public ProjectPolicyOwner getProjectPolicyOwnerById(long projectPolicyOwnerID) {

    return projectPolicyOwnerDAO.find(projectPolicyOwnerID);
  }

  public void savePolicyOwnerPhase(Phase next, long policyID, ProjectPolicyOwner projectPolicyOwner) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyOwner> projectPolicyOwners = phase.getProjectPolicyOwners().stream()
      .filter(c -> c.getProjectPolicy().getId().longValue() == policyID
        && c.getRepIndPolicyType().getId().equals(projectPolicyOwner.getRepIndPolicyType().getId()))
      .collect(Collectors.toList());

    if (projectPolicyOwners.isEmpty()) {
      ProjectPolicyOwner projectPolicyOwnerAdd = new ProjectPolicyOwner();
      projectPolicyOwnerAdd.setProjectPolicy(projectPolicyOwner.getProjectPolicy());
      projectPolicyOwnerAdd.setPhase(phase);
      projectPolicyOwnerAdd.setRepIndPolicyType(projectPolicyOwner.getRepIndPolicyType());
      projectPolicyOwnerDAO.save(projectPolicyOwnerAdd);
    }


    if (phase.getNext() != null) {
      this.savePolicyOwnerPhase(phase.getNext(), policyID, projectPolicyOwner);
    }
  }

  @Override
  public ProjectPolicyOwner saveProjectPolicyOwner(ProjectPolicyOwner projectPolicyOwner) {

    ProjectPolicyOwner owner = projectPolicyOwnerDAO.save(projectPolicyOwner);

    Phase phase = phaseDAO.find(owner.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (owner.getPhase().getNext() != null) {
        this.savePolicyOwnerPhase(owner.getPhase().getNext(), owner.getProjectPolicy().getId(), projectPolicyOwner);
      }
    }
    return owner;
  }


}
