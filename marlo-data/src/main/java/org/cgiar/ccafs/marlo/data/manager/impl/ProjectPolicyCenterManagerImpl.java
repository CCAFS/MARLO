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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyCenterDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCenterManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCenter;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ProjectPolicyCenterManagerImpl implements ProjectPolicyCenterManager {

  private ProjectPolicyCenterDAO projectPolicyCenterDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectPolicyCenterManagerImpl(ProjectPolicyCenterDAO projectPolicyCenterDAO, PhaseDAO phaseDAO) {
    this.projectPolicyCenterDAO = projectPolicyCenterDAO;
    this.phaseDAO = phaseDAO;
  }


  @Override
  public void deleteProjectPolicyCenter(long projectPolicyCenterId) {
    ProjectPolicyCenter projectPolicyCenter = this.getProjectPolicyCenterById(projectPolicyCenterId);

    if (projectPolicyCenter.getPhase().getNext() != null) {
      this.deleteProjectPolicyCenterPhase(projectPolicyCenter.getPhase().getNext(),
        projectPolicyCenter.getProjectPolicy().getId(), projectPolicyCenter);
    }

    // Conditions to Project Policy Works In AR phase and Upkeep Phase
    if (projectPolicyCenter.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectPolicyCenter.getPhase().getNext() != null) {
      this.deleteProjectPolicyCenterPhase(projectPolicyCenter.getPhase().getNext(),
        projectPolicyCenter.getProjectPolicy().getId(), projectPolicyCenter);
    }

    if (projectPolicyCenter.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (projectPolicyCenter.getPhase().getNext() != null
        && projectPolicyCenter.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = projectPolicyCenter.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectPolicyCenterPhase(upkeepPhase, projectPolicyCenter.getProjectPolicy().getId(),
            projectPolicyCenter);
        }
      }
    }
    projectPolicyCenterDAO.deleteProjectPolicyCenter(projectPolicyCenterId);
  }

  public void deleteProjectPolicyCenterPhase(Phase next, long policyID, ProjectPolicyCenter projectPolicyCenter) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyCenter> projectPolicyCenters = projectPolicyCenterDAO.findAll().stream()
      .filter(c -> c.isActive() && c.getPhase().getId().longValue() == phase.getId().longValue()
        && c.getProjectPolicy().getId().longValue() == policyID
        && c.getInstitution().getId().equals(projectPolicyCenter.getInstitution().getId()))
      .collect(Collectors.toList());

    for (ProjectPolicyCenter projectPolicyCenterDB : projectPolicyCenters) {
      projectPolicyCenterDAO.deleteProjectPolicyCenter(projectPolicyCenterDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectPolicyCenterPhase(phase.getNext(), policyID, projectPolicyCenter);
    }
  }


  @Override
  public boolean existProjectPolicyCenter(long projectPolicyCenterID) {
    return projectPolicyCenterDAO.existProjectPolicyCenter(projectPolicyCenterID);
  }


  @Override
  public List<ProjectPolicyCenter> findAll() {
    return projectPolicyCenterDAO.findAll();
  }


  @Override
  public ProjectPolicyCenter getProjectPolicyCenterById(long projectPolicyCenterID) {
    return projectPolicyCenterDAO.find(projectPolicyCenterID);
  }

  public void savePolicyCenterPhase(Phase next, long policyID, ProjectPolicyCenter projectPolicyCenter) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyCenter> projectPolicyCenters = projectPolicyCenterDAO.findAll().stream()
      .filter(c -> c.getProjectPolicy().getId().longValue() == policyID
        && c.getPhase().getId().longValue() == phase.getId().longValue()
        && c.getInstitution().getId().equals(projectPolicyCenter.getInstitution().getId()))
      .collect(Collectors.toList());

    if (projectPolicyCenters.isEmpty()) {
      ProjectPolicyCenter projectPolicyCenterAdd = new ProjectPolicyCenter();
      projectPolicyCenterAdd.setProjectPolicy(projectPolicyCenter.getProjectPolicy());
      projectPolicyCenterAdd.setPhase(phase);
      projectPolicyCenterAdd.setInstitution(projectPolicyCenter.getInstitution());
      projectPolicyCenterDAO.save(projectPolicyCenterAdd);
    }


    if (phase.getNext() != null) {
      this.savePolicyCenterPhase(phase.getNext(), policyID, projectPolicyCenter);
    }
  }

  @Override
  public ProjectPolicyCenter saveProjectPolicyCenter(ProjectPolicyCenter projectPolicyCenter) {
    ProjectPolicyCenter center = projectPolicyCenterDAO.save(projectPolicyCenter);

    Phase phase = phaseDAO.find(center.getPhase().getId());

    // Conditions to Project Policy Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.savePolicyCenterPhase(center.getPhase().getNext(), center.getProjectPolicy().getId(), projectPolicyCenter);
    }
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.savePolicyCenterPhase(upkeepPhase, center.getProjectPolicy().getId(), projectPolicyCenter);
        }
      }
    }
    return center;
  }


}
