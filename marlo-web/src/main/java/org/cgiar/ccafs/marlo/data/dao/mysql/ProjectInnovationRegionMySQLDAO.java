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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationRegionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationRegionMySQLDAO extends AbstractMarloDAO<ProjectInnovationRegion, Long>
  implements ProjectInnovationRegionDAO {


  @Inject
  public ProjectInnovationRegionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationRegion(long projectInnovationRegionId) {
    ProjectInnovationRegion projectInnovationRegion = this.find(projectInnovationRegionId);
    this.delete(projectInnovationRegion);
  }

  @Override
  public boolean existProjectInnovationRegion(long projectInnovationRegionID) {
    ProjectInnovationRegion projectInnovationRegion = this.find(projectInnovationRegionID);
    if (projectInnovationRegion == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationRegion find(long id) {
    return super.find(ProjectInnovationRegion.class, id);

  }

  @Override
  public List<ProjectInnovationRegion> findAll() {
    String query = "from " + ProjectInnovationRegion.class.getName();
    List<ProjectInnovationRegion> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectInnovationRegion> getInnovationRegionbyPhase(long innovationID, long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT project_innovation_regions.id as contryId FROM project_innovations ");
    query.append(
      "INNER JOIN project_innovation_regions ON project_innovation_regions.project_innovation_id = project_innovations.id ");
    query.append("INNER JOIN phases ON project_innovation_regions.id_phase = phases.id ");
    query.append("WHERE project_innovations.id = ");
    query.append(innovationID);
    query.append(" AND phases.id = ");
    query.append(phaseID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<ProjectInnovationRegion> projectInnovationCountries = new ArrayList<ProjectInnovationRegion>();
    for (Map<String, Object> map : list) {
      String contryId = map.get("contryId").toString();
      long longContryId = Long.parseLong(contryId);
      ProjectInnovationRegion projectInnovationCountry = this.find(longContryId);
      projectInnovationCountries.add(projectInnovationCountry);
    }
    return projectInnovationCountries;
  }

  @Override
  public ProjectInnovationRegion save(ProjectInnovationRegion projectInnovationRegion) {
    if (projectInnovationRegion.getId() == null) {
      super.saveEntity(projectInnovationRegion);
    } else {
      projectInnovationRegion = super.update(projectInnovationRegion);
    }


    return projectInnovationRegion;
  }

}