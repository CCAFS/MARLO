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
import org.cgiar.ccafs.marlo.data.dao.ProjectLocationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectLocationManagerImpl implements ProjectLocationManager {


  private ProjectLocationDAO projectLocationDAO;
  // Managers
  private PhaseDAO phaseMySQLDAO;

  @Inject
  public ProjectLocationManagerImpl(ProjectLocationDAO projectLocationDAO, PhaseDAO phaseMySQLDAO) {
    this.projectLocationDAO = projectLocationDAO;
    this.phaseMySQLDAO = phaseMySQLDAO;


  }

  /**
   * clone or update the location for next phases
   * 
   * @param next the next phase to clone
   * @param projecID the project id we are working
   * @param projectLocation the project location to clone
   */
  private void addProjectLoactionsDAO(Phase next, long projecID, ProjectLocation projectLocation) {
    Phase phase = phaseMySQLDAO.find(next.getId());
    boolean hasLocElement = false;
    if (projectLocation.getLocElement() != null) {
      hasLocElement = true;
    }
    List<ProjectLocation> locations = new ArrayList<ProjectLocation>();

    if (hasLocElement) {
      locations.addAll(phase.getProjectLocations().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID && c.getLocElement() != null
          && projectLocation.getLocElement().getId().longValue() == c.getLocElement().getId().longValue())
        .collect(Collectors.toList()));
    } else {
      locations.addAll(phase.getProjectLocations().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID && c.getLocElementType() != null
          && projectLocation.getLocElementType().getId().longValue() == c.getLocElementType().getId().longValue())
        .collect(Collectors.toList()));
    }


    if (phase.getEditable() != null && phase.getEditable() && locations.isEmpty()) {
      ProjectLocation projectLocationAdd = new ProjectLocation();
      projectLocationAdd.setActive(true);
      projectLocationAdd.setActiveSince(projectLocation.getActiveSince());
      projectLocationAdd.setCreatedBy(projectLocation.getCreatedBy());
      projectLocationAdd.setLocElement(projectLocation.getLocElement());
      projectLocationAdd.setLocElementType(projectLocation.getLocElementType());
      projectLocationAdd.setModificationJustification(projectLocation.getModificationJustification());
      projectLocationAdd.setModifiedBy(projectLocation.getModifiedBy());
      projectLocationAdd.setPhase(phase);
      projectLocationAdd.setProject(projectLocation.getProject());
      projectLocationDAO.save(projectLocationAdd);

    }

    if (phase.getNext() != null) {
      this.addProjectLoactionsDAO(phase.getNext(), projecID, projectLocation);
    }


  }

  @Override
  public void deleteProjectLocation(long projectLocationId) {

    projectLocationDAO.deleteProjectLocation(projectLocationId);
    ProjectLocation projectLocation = this.getProjectLocationById(projectLocationId);
    Phase currentPhase = phaseMySQLDAO.find(projectLocation.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {

      if (projectLocation.getPhase().getNext() != null) {
        this.deleteProjectLocationPhase(projectLocation.getPhase().getNext(), projectLocation.getProject().getId(),
          projectLocation);
      }
    }
 
  }

  public void deleteProjectLocationPhase(Phase next, long projectID, ProjectLocation projectLocation) {
    Phase phase = phaseMySQLDAO.find(next.getId());
    boolean hasLocElement = false;
    if (projectLocation.getLocElement() != null) {
      hasLocElement = true;
    }
    if (phase.getEditable() != null && phase.getEditable()) {

      List<ProjectLocation> locations = new ArrayList<ProjectLocation>();

      if (hasLocElement) {
        locations.addAll(phase.getProjectLocations().stream()
          .filter(c -> c.isActive() && c.getProject().getId().longValue() == projectID && c.getLocElement() != null
            && projectLocation.getLocElement().getId().longValue() == c.getLocElement().getId().longValue())
          .collect(Collectors.toList()));
      } else {
        locations.addAll(phase.getProjectLocations().stream()
          .filter(c -> c.isActive() && c.getProject().getId().longValue() == projectID && c.getLocElementType() != null
            && projectLocation.getLocElementType().getId().longValue() == c.getLocElementType().getId().longValue())
          .collect(Collectors.toList()));
      }
      for (ProjectLocation location : locations) {
        location.setActive(false);
        projectLocationDAO.save(location);
      }
    }
    if (phase.getNext() != null) {
      this.deleteProjectLocationPhase(phase.getNext(), projectID, projectLocation);

    }


  }

  @Override
  public boolean existProjectLocation(long projectLocationID) {

    return projectLocationDAO.existProjectLocation(projectLocationID);
  }

  @Override
  public List<ProjectLocation> findAll() {

    return projectLocationDAO.findAll();

  }

  @Override
  public List<Map<String, Object>> getParentLocations(long projectId, String parentField) {

    return projectLocationDAO.getParentLocations(projectId, parentField);
  }

  @Override
  public ProjectLocation getProjectLocationById(long projectLocationID) {

    return projectLocationDAO.find(projectLocationID);
  }

  @Override
  public ProjectLocation getProjectLocationByProjectAndLocElement(Long projectId, Long LocElementId) {
    return projectLocationDAO.getProjectLocationByProjectAndLocElement(projectId, LocElementId);
  }

  @Override
  public ProjectLocation saveProjectLocation(ProjectLocation projectLocation) {

    ProjectLocation resultProjectLocation = projectLocationDAO.save(projectLocation);
    Phase currentPhase = phaseMySQLDAO.find(projectLocation.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectLocation.getPhase().getNext() != null) {
        this.addProjectLoactionsDAO(projectLocation.getPhase().getNext(), projectLocation.getProject().getId(),
          projectLocation);
      }
    }
    return resultProjectLocation;
  }

}
