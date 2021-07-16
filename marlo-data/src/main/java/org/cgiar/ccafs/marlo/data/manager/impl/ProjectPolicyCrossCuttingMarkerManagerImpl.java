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
import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyCrossCuttingMarkerDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrossCuttingMarker;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicyCrossCuttingMarkerManagerImpl implements ProjectPolicyCrossCuttingMarkerManager {


  private ProjectPolicyCrossCuttingMarkerDAO projectPolicyCrossCuttingMarkerDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectPolicyCrossCuttingMarkerManagerImpl(
    ProjectPolicyCrossCuttingMarkerDAO projectPolicyCrossCuttingMarkerDAO, PhaseDAO phaseDAO) {
    this.projectPolicyCrossCuttingMarkerDAO = projectPolicyCrossCuttingMarkerDAO;
    this.phaseDAO = phaseDAO;
  }


  public void deleteProjectCrossCuttingMarkerPhase(Phase next, long policyID,
    ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyCrossCuttingMarker> projectPolicyCrossCuttingMarkers =
      phase.getProjectPolicyCrossCuttingMarkers().stream()
        .filter(
          c -> c.isActive() && c.getProjectPolicy().getId().longValue() == policyID
            && c.getCgiarCrossCuttingMarker().getId()
              .equals(projectPolicyCrossCuttingMarker.getCgiarCrossCuttingMarker().getId()))
        .collect(Collectors.toList());

    for (ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarkerDB : projectPolicyCrossCuttingMarkers) {
      projectPolicyCrossCuttingMarkerDAO
        .deleteProjectPolicyCrossCuttingMarker(projectPolicyCrossCuttingMarkerDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectCrossCuttingMarkerPhase(phase.getNext(), policyID, projectPolicyCrossCuttingMarker);
    }


  }


  @Override
  public void deleteProjectPolicyCrossCuttingMarker(long projectPolicyCrossCuttingMarkerId) {

    ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker =
      this.getProjectPolicyCrossCuttingMarkerById(projectPolicyCrossCuttingMarkerId);

    // Conditions to Project Policy Works In AR phase and Upkeep Phase
    if (projectPolicyCrossCuttingMarker.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectPolicyCrossCuttingMarker.getPhase().getNext() != null) {
      this.deleteProjectCrossCuttingMarkerPhase(projectPolicyCrossCuttingMarker.getPhase().getNext(),
        projectPolicyCrossCuttingMarker.getProjectPolicy().getId(), projectPolicyCrossCuttingMarker);
    }

    if (projectPolicyCrossCuttingMarker.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (projectPolicyCrossCuttingMarker.getPhase().getNext() != null
        && projectPolicyCrossCuttingMarker.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = projectPolicyCrossCuttingMarker.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectCrossCuttingMarkerPhase(upkeepPhase,
            projectPolicyCrossCuttingMarker.getProjectPolicy().getId(), projectPolicyCrossCuttingMarker);
        }
      }
    }

    projectPolicyCrossCuttingMarkerDAO.deleteProjectPolicyCrossCuttingMarker(projectPolicyCrossCuttingMarkerId);
  }

  @Override
  public boolean existProjectPolicyCrossCuttingMarker(long projectPolicyCrossCuttingMarkerID) {

    return projectPolicyCrossCuttingMarkerDAO.existProjectPolicyCrossCuttingMarker(projectPolicyCrossCuttingMarkerID);
  }

  @Override
  public List<ProjectPolicyCrossCuttingMarker> findAll() {

    return projectPolicyCrossCuttingMarkerDAO.findAll();

  }

  @Override
  public List<ProjectPolicyCrossCuttingMarker> getAllPolicyCrossCuttingMarkersByPolicy(Long policyId) {
    return projectPolicyCrossCuttingMarkerDAO.getAllPolicyCrossCuttingMarkersByPolicy(policyId.longValue());
  }

  @Override
  public ProjectPolicyCrossCuttingMarker getPolicyCrossCountryMarkerId(long policyID, long cgiarCrossCuttingMarkerID,
    long phaseID) {
    return projectPolicyCrossCuttingMarkerDAO.getPolicyCrossCountryMarkerId(policyID, cgiarCrossCuttingMarkerID,
      phaseID);
  }

  @Override
  public ProjectPolicyCrossCuttingMarker
    getProjectPolicyCrossCuttingMarkerById(long projectPolicyCrossCuttingMarkerID) {

    return projectPolicyCrossCuttingMarkerDAO.find(projectPolicyCrossCuttingMarkerID);
  }

  public void savePolicyCrossCuttingMarkerAddPhase(Phase next, long policyID,
    ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyCrossCuttingMarker> projectPolicyCrossCuttingMarkers =
      phase.getProjectPolicyCrossCuttingMarkers().stream()
        .filter(
          c -> c.isActive() && c.getProjectPolicy().getId().longValue() == policyID
            && c.getCgiarCrossCuttingMarker().getId()
              .equals(projectPolicyCrossCuttingMarker.getCgiarCrossCuttingMarker().getId()))
        .collect(Collectors.toList());

    if (projectPolicyCrossCuttingMarkers.isEmpty()) {
      ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarkerAdd = new ProjectPolicyCrossCuttingMarker();
      projectPolicyCrossCuttingMarkerAdd.setProjectPolicy(projectPolicyCrossCuttingMarker.getProjectPolicy());
      projectPolicyCrossCuttingMarkerAdd.setPhase(phase);
      projectPolicyCrossCuttingMarkerAdd
        .setRepIndGenderYouthFocusLevel(projectPolicyCrossCuttingMarker.getRepIndGenderYouthFocusLevel());
      projectPolicyCrossCuttingMarkerAdd
        .setCgiarCrossCuttingMarker(projectPolicyCrossCuttingMarker.getCgiarCrossCuttingMarker());
      projectPolicyCrossCuttingMarkerDAO.save(projectPolicyCrossCuttingMarkerAdd);
    } else {
      ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarkerAdd =
        this.getProjectPolicyCrossCuttingMarkerById(projectPolicyCrossCuttingMarkers.get(0).getId());
      projectPolicyCrossCuttingMarkerAdd
        .setRepIndGenderYouthFocusLevel(projectPolicyCrossCuttingMarker.getRepIndGenderYouthFocusLevel());
      projectPolicyCrossCuttingMarkerDAO.save(projectPolicyCrossCuttingMarkerAdd);
    }

    if (phase.getNext() != null) {
      this.savePolicyCrossCuttingMarkerAddPhase(phase.getNext(), policyID, projectPolicyCrossCuttingMarker);
    }
  }


  @Override
  public ProjectPolicyCrossCuttingMarker
    saveProjectPolicyCrossCuttingMarker(ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker) {

    ProjectPolicyCrossCuttingMarker marker = projectPolicyCrossCuttingMarkerDAO.save(projectPolicyCrossCuttingMarker);
    Phase phase = phaseDAO.find(marker.getPhase().getId());

    // Conditions to Project Policy Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.savePolicyCrossCuttingMarkerAddPhase(marker.getPhase().getNext(), marker.getProjectPolicy().getId(),
        projectPolicyCrossCuttingMarker);
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.savePolicyCrossCuttingMarkerAddPhase(upkeepPhase, marker.getProjectPolicy().getId(),
            projectPolicyCrossCuttingMarker);
        }
      }
    }

    return marker;

  }

}
