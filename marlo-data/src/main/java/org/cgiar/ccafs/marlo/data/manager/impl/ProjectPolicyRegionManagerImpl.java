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
import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyRegionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyRegionManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyRegion;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicyRegionManagerImpl implements ProjectPolicyRegionManager {


  private ProjectPolicyRegionDAO projectPolicyRegionDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectPolicyRegionManagerImpl(ProjectPolicyRegionDAO projectPolicyRegionDAO, PhaseDAO phaseDAO) {
    this.projectPolicyRegionDAO = projectPolicyRegionDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectPolicyRegion(long projectPolicyRegionId) {


    ProjectPolicyRegion projectPolicyRegion = this.getProjectPolicyRegionById(projectPolicyRegionId);


    Phase phase = null;

    if (projectPolicyRegion.getPhase().getNext() != null) {
      if (projectPolicyRegion.getPhase().getDescription().equals(APConstants.REPORTING)) {
        // upkeepPhase
        if (projectPolicyRegion.getPhase().getNext().getNext() != null) {
          phase = projectPolicyRegion.getPhase().getNext().getNext();
        }
      } else {
        // include AP.Constants.PLANNING and others
        phase = projectPolicyRegion.getPhase().getNext();
      }
    }

    if (phase != null) {
      this.deleteProjectPolicyRegionPhase(phase, projectPolicyRegion.getProjectPolicy().getId(), projectPolicyRegion);
    }

    projectPolicyRegionDAO.deleteProjectPolicyRegion(projectPolicyRegionId);
  }

  public void deleteProjectPolicyRegionPhase(Phase next, long policyID, ProjectPolicyRegion projectPolicyRegion) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyRegion> projectPolicyRegions =
      phase.getProjectPolicyRegions().stream()
        .filter(c -> c.isActive() && c.getProjectPolicy().getId().longValue() == policyID
          && c.getLocElement().getId().equals(projectPolicyRegion.getLocElement().getId()))
        .collect(Collectors.toList());
    for (ProjectPolicyRegion projectPolicyRegionDB : projectPolicyRegions) {
      projectPolicyRegionDAO.deleteProjectPolicyRegion(projectPolicyRegionDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectPolicyRegionPhase(phase.getNext(), policyID, projectPolicyRegion);
    }
  }

  @Override
  public boolean existProjectPolicyRegion(long projectPolicyRegionID) {

    return projectPolicyRegionDAO.existProjectPolicyRegion(projectPolicyRegionID);
  }

  @Override
  public List<ProjectPolicyRegion> findAll() {

    return projectPolicyRegionDAO.findAll();

  }

  @Override
  public List<ProjectPolicyRegion> getPolicyRegionbyPhase(long policyID, long phaseID) {
    return projectPolicyRegionDAO.getPolicyRegionbyPhase(policyID, phaseID);
  }

  @Override
  public ProjectPolicyRegion getProjectPolicyRegionById(long projectPolicyRegionID) {

    return projectPolicyRegionDAO.find(projectPolicyRegionID);
  }

  @Override
  public ProjectPolicyRegion getProjectPolicyRegionByPhase(long projectPolicyID, long regionID, long phaseID) {

    return projectPolicyRegionDAO.getProjectPolicyRegionByPhase(projectPolicyID, regionID, phaseID);
  }

  @Override
  public ProjectPolicyRegion saveProjectPolicyRegion(ProjectPolicyRegion projectPolicyRegion) {

    ProjectPolicyRegion region = projectPolicyRegionDAO.save(projectPolicyRegion);
    Phase phase = phaseDAO.find(region.getPhase().getId());


    // Conditions to Project Policy Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectPolicyRegionPhase(region.getPhase().getNext(), region.getProjectPolicy().getId(),
        projectPolicyRegion);
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectPolicyRegionPhase(upkeepPhase, region.getProjectPolicy().getId(), projectPolicyRegion);
        }
      }
    }

    return region;
  }

  public void saveProjectPolicyRegionPhase(Phase next, long policyID, ProjectPolicyRegion projectPolicyRegion) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyRegion> projectPolicyRegions =
      phase.getProjectPolicyRegions().stream()
        .filter(c -> c.getProjectPolicy().getId().longValue() == policyID
          && c.getLocElement().getId().equals(projectPolicyRegion.getLocElement().getId()))
        .collect(Collectors.toList());

    if (projectPolicyRegions.isEmpty()) {
      ProjectPolicyRegion projectPolicyRegionAdd = new ProjectPolicyRegion();
      projectPolicyRegionAdd.setProjectPolicy(projectPolicyRegion.getProjectPolicy());
      projectPolicyRegionAdd.setPhase(phase);
      projectPolicyRegionAdd.setLocElement(projectPolicyRegion.getLocElement());
      projectPolicyRegionDAO.save(projectPolicyRegionAdd);
    }


    if (phase.getNext() != null) {
      this.saveProjectPolicyRegionPhase(phase.getNext(), policyID, projectPolicyRegion);
    }
  }


}
