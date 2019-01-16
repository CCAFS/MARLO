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

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyCrossCuttingMarkerDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrossCuttingMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPolicyCrossCuttingMarkerMySQLDAO extends AbstractMarloDAO<ProjectPolicyCrossCuttingMarker, Long>
  implements ProjectPolicyCrossCuttingMarkerDAO {


  @Inject
  public ProjectPolicyCrossCuttingMarkerMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPolicyCrossCuttingMarker(long projectPolicyCrossCuttingMarkerId) {
    ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker = this.find(projectPolicyCrossCuttingMarkerId);
    this.delete(projectPolicyCrossCuttingMarker.getId());
  }

  @Override
  public boolean existProjectPolicyCrossCuttingMarker(long projectPolicyCrossCuttingMarkerID) {
    ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker = this.find(projectPolicyCrossCuttingMarkerID);
    if (projectPolicyCrossCuttingMarker == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPolicyCrossCuttingMarker find(long id) {
    return super.find(ProjectPolicyCrossCuttingMarker.class, id);

  }

  @Override
  public List<ProjectPolicyCrossCuttingMarker> findAll() {
    String query = "from " + ProjectPolicyCrossCuttingMarker.class.getName();
    List<ProjectPolicyCrossCuttingMarker> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectPolicyCrossCuttingMarker getPolicyCrossCountryMarkerId(long policyID, long cgiarCrossCuttingMarkerID,
    long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT id as markerId FROM project_policy_cross_cutting_markers ");
    query.append("WHERE project_policy_id = ");
    query.append(policyID);
    query.append(" AND cgiar_cross_cutting_marker_id = ");
    query.append(cgiarCrossCuttingMarkerID);
    query.append(" AND id_phase = ");
    query.append(phaseID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<ProjectPolicyCrossCuttingMarker> projectPolicyCrossCuttingMarkers =
      new ArrayList<ProjectPolicyCrossCuttingMarker>();
    for (Map<String, Object> map : list) {
      String markerId = map.get("markerId").toString();
      long longMarkerId = Long.parseLong(markerId);
      ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker = this.find(longMarkerId);
      projectPolicyCrossCuttingMarkers.add(projectPolicyCrossCuttingMarker);
    }
    if (projectPolicyCrossCuttingMarkers.size() > 0) {
      return projectPolicyCrossCuttingMarkers.get(0);
    } else {
      return null;
    }

  }

  @Override
  public ProjectPolicyCrossCuttingMarker save(ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker) {
    if (projectPolicyCrossCuttingMarker.getId() == null) {
      super.saveEntity(projectPolicyCrossCuttingMarker);
    } else {
      projectPolicyCrossCuttingMarker = super.update(projectPolicyCrossCuttingMarker);
    }


    return projectPolicyCrossCuttingMarker;
  }


}