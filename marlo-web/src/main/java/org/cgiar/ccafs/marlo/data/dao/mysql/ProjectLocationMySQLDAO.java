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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.ProjectLocationDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class ProjectLocationMySQLDAO implements ProjectLocationDAO {

  private StandardDAO dao;

  @Inject
  public ProjectLocationMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  /**
   * clone or update the location for next phases
   * 
   * @param next the next phase to clone
   * @param projecID the project id we are working
   * @param projectLocation the project location to clone
   */
  private void addProjectLoactionsDAO(Phase next, long projecID, ProjectLocation projectLocation) {
    Phase phase = dao.find(Phase.class, next.getId());
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
      dao.save(projectLocationAdd);

    }

    if (phase.getNext() != null) {
      this.addProjectLoactionsDAO(phase.getNext(), projecID, projectLocation);
    }


  }

  @Override
  public boolean deleteProjectLocation(long projectLocationId) {
    ProjectLocation projectLocation = this.find(projectLocationId);
    projectLocation.setActive(false);
    boolean result = dao.update(projectLocation);
    Phase currentPhase = dao.find(Phase.class, projectLocation.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {

      if (projectLocation.getPhase().getNext() != null) {
        this.deleteProjectLocationPhase(projectLocation.getPhase().getNext(), projectLocation.getProject().getId(),
          projectLocation);
      }
    }
    return result;

  }

  public void deleteProjectLocationPhase(Phase next, long projectID, ProjectLocation projectLocation) {
    Phase phase = dao.find(Phase.class, next.getId());
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
        this.dao.update(location);
      }
    }
    if (phase.getNext() != null) {
      this.deleteProjectLocationPhase(phase.getNext(), projectID, projectLocation);

    }


  }

  @Override
  public boolean existProjectLocation(long projectLocationID) {
    ProjectLocation projectLocation = this.find(projectLocationID);
    if (projectLocation == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectLocation find(long id) {
    return dao.find(ProjectLocation.class, id);

  }

  @Override
  public List<ProjectLocation> findAll() {
    String query = "from " + ProjectLocation.class.getName() + " where is_active=1";
    List<ProjectLocation> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<Map<String, Object>> getParentLocations(long projectId, String parentField) {
    String query = "select DISTINCT " + parentField + " from project_locations where project_id=" + projectId
      + " and is_active = 1 and " + parentField + " is not null";
    return dao.findCustomQuery(query);
  }

  @Override
  public ProjectLocation getProjectLocationByProjectAndLocElement(Long projectId, Long LocElementId) {
    String query = "from " + ProjectLocation.class.getName() + " where project_id='" + projectId
      + "' and loc_element_id='" + LocElementId + "'";
    List<ProjectLocation> list = dao.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public long save(ProjectLocation projectLocation) {
    if (projectLocation.getId() == null) {
      dao.save(projectLocation);
    } else {
      dao.update(projectLocation);
    }

    Phase currentPhase = dao.find(Phase.class, projectLocation.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (projectLocation.getPhase().getNext() != null) {
        this.addProjectLoactionsDAO(projectLocation.getPhase().getNext(), projectLocation.getProject().getId(),
          projectLocation);
      }
    }
    return projectLocation.getId();

  }
}