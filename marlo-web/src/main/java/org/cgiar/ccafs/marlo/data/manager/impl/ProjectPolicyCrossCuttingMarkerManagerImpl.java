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
        .filter(c -> c.isActive() && c.getProjectPolicy().getId().longValue() == policyID
          && c.getCgiarCrossCuttingMarker().getId()
            .equals(projectPolicyCrossCuttingMarker.getCgiarCrossCuttingMarker().getId())
          && c.getRepIndGenderYouthFocusLevel().getId()
            .equals(projectPolicyCrossCuttingMarker.getRepIndGenderYouthFocusLevel().getId()))
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

    if (projectPolicyCrossCuttingMarker.getPhase().getNext() != null) {
      this.deleteProjectCrossCuttingMarkerPhase(projectPolicyCrossCuttingMarker.getPhase().getNext(),
        projectPolicyCrossCuttingMarker.getProjectPolicy().getId(), projectPolicyCrossCuttingMarker);
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
        .filter(c -> c.isActive() && c.getProjectPolicy().getId().longValue() == policyID
          && c.getCgiarCrossCuttingMarker().getId()
            .equals(projectPolicyCrossCuttingMarker.getCgiarCrossCuttingMarker().getId())
          && c.getRepIndGenderYouthFocusLevel().getId()
            .equals(projectPolicyCrossCuttingMarker.getRepIndGenderYouthFocusLevel().getId()))
        .collect(Collectors.toList());

    if (projectPolicyCrossCuttingMarkers.isEmpty()) {
      ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarkerAdd = new ProjectPolicyCrossCuttingMarker();
      projectPolicyCrossCuttingMarkerAdd.setProjectPolicy(projectPolicyCrossCuttingMarkerAdd.getProjectPolicy());
      projectPolicyCrossCuttingMarkerAdd.setPhase(phase);
      projectPolicyCrossCuttingMarkerAdd
        .setRepIndGenderYouthFocusLevel(projectPolicyCrossCuttingMarkerAdd.getRepIndGenderYouthFocusLevel());
      projectPolicyCrossCuttingMarkerAdd
        .setCgiarCrossCuttingMarker(projectPolicyCrossCuttingMarkerAdd.getCgiarCrossCuttingMarker());
      projectPolicyCrossCuttingMarkerDAO.save(projectPolicyCrossCuttingMarkerAdd);
    }

    if (phase.getNext() != null) {
      this.savePolicyCrossCuttingMarkerAddPhase(phase.getNext(), policyID, projectPolicyCrossCuttingMarker);
    }
  }


  @Override
  public ProjectPolicyCrossCuttingMarker
    saveProjectPolicyCrossCuttingMarker(ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker) {

    return projectPolicyCrossCuttingMarkerDAO.save(projectPolicyCrossCuttingMarker);
  }


}
