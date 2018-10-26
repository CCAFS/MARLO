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

import org.cgiar.ccafs.marlo.data.dao.ProjectHighligthTypeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectHighligthTypeMySQLDAO extends AbstractMarloDAO<ProjectHighlightType, Long>
  implements ProjectHighligthTypeDAO {


  @Inject
  public ProjectHighligthTypeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectHighligthType(long projectHighligthTypeId) {
    ProjectHighlightType projectHighlightType = this.find(projectHighligthTypeId);

    super.delete(projectHighlightType);
  }

  @Override
  public boolean existProjectHighligthType(long projectHighligthTypeID) {
    ProjectHighlightType projectHighlightType = this.find(projectHighligthTypeID);
    if (projectHighlightType == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectHighlightType find(long id) {
    return super.find(ProjectHighlightType.class, id);

  }

  @Override
  public List<ProjectHighlightType> findAll() {
    String query = "from " + ProjectHighlightType.class.getName() + " where is_active=1";
    List<ProjectHighlightType> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectHighlightType> getHighlightTypebyPhase(long higlightID, long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT project_highlights_types.id as typeId FROM project_highlights ");
    query.append(
      "INNER JOIN project_highlights_types ON project_highlights_types.project_highlights_id = project_highlights.id ");
    query.append("INNER JOIN phases ON project_highlights_types.id_phase = phases.id ");
    query.append("WHERE project_highlights.id = ");
    query.append(higlightID);
    query.append(" AND phases.id = ");
    query.append(phaseID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<ProjectHighlightType> projectHighlightTypes = new ArrayList<ProjectHighlightType>();
    for (Map<String, Object> map : list) {
      ProjectHighlightType projectHighlightType = this.find(Long.parseLong(map.get("typeId").toString()));
      projectHighlightTypes.add(projectHighlightType);
    }
    return projectHighlightTypes;

  }

  @Override
  public ProjectHighlightType save(ProjectHighlightType projectHighlightType) {
    if (projectHighlightType.getId() == null) {
      super.saveEntity(projectHighlightType);
    } else {
      projectHighlightType = super.update(projectHighlightType);
    }


    return projectHighlightType;
  }


}