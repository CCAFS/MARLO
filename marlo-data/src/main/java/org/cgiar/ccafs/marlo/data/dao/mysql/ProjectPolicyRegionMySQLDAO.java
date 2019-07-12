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

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyRegionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPolicyRegionMySQLDAO extends AbstractMarloDAO<ProjectPolicyRegion, Long>
  implements ProjectPolicyRegionDAO {


  @Inject
  public ProjectPolicyRegionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPolicyRegion(long projectPolicyRegionId) {
    ProjectPolicyRegion projectPolicyRegion = this.find(projectPolicyRegionId);
    this.delete(projectPolicyRegion);
  }

  @Override
  public boolean existProjectPolicyRegion(long projectPolicyRegionID) {
    ProjectPolicyRegion projectPolicyRegion = this.find(projectPolicyRegionID);
    if (projectPolicyRegion == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPolicyRegion find(long id) {
    return super.find(ProjectPolicyRegion.class, id);

  }

  @Override
  public List<ProjectPolicyRegion> findAll() {
    String query = "from " + ProjectPolicyRegion.class.getName();
    List<ProjectPolicyRegion> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectPolicyRegion> getPolicyRegionbyPhase(long policyID, long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT project_policy_regions.id as contryId FROM project_policies ");
    query
      .append("INNER JOIN project_policy_regions ON project_policy_regions.project_policy_id = project_policies.id ");
    query.append("INNER JOIN phases ON project_policy_regions.id_phase = phases.id ");
    query.append("WHERE project_policies.id = ");
    query.append(policyID);
    query.append(" AND phases.id = ");
    query.append(phaseID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<ProjectPolicyRegion> projectPolicyRegions = new ArrayList<ProjectPolicyRegion>();
    for (Map<String, Object> map : list) {
      String contryId = map.get("contryId").toString();
      long longContryId = Long.parseLong(contryId);
      ProjectPolicyRegion projectPolicyRegion = this.find(longContryId);
      projectPolicyRegions.add(projectPolicyRegion);
    }
    return projectPolicyRegions;
  }

  @Override
  public ProjectPolicyRegion save(ProjectPolicyRegion projectPolicyRegion) {
    if (projectPolicyRegion.getId() == null) {
      super.saveEntity(projectPolicyRegion);
    } else {
      projectPolicyRegion = super.update(projectPolicyRegion);
    }


    return projectPolicyRegion;
  }


}