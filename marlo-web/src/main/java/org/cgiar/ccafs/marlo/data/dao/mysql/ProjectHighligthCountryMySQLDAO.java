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

import org.cgiar.ccafs.marlo.data.dao.ProjectHighligthCountryDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightCountry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectHighligthCountryMySQLDAO extends AbstractMarloDAO<ProjectHighlightCountry, Long>
  implements ProjectHighligthCountryDAO {


  @Inject
  public ProjectHighligthCountryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectHighligthCountry(long projectHighligthCountryId) {
    ProjectHighlightCountry projectHighlightCountry = this.find(projectHighligthCountryId);

    super.delete(projectHighlightCountry);
  }

  @Override
  public boolean existProjectHighligthCountry(long projectHighligthCountryID) {
    ProjectHighlightCountry projectHighlightCountry = this.find(projectHighligthCountryID);
    if (projectHighlightCountry == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectHighlightCountry find(long id) {
    return super.find(ProjectHighlightCountry.class, id);

  }

  @Override
  public List<ProjectHighlightCountry> findAll() {
    String query = "from " + ProjectHighlightCountry.class.getName() + " where is_active=1";
    List<ProjectHighlightCountry> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public List<ProjectHighlightCountry> getHighlightCountrybyPhase(long higlightID, long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT project_highlights_country.id as contryId FROM project_highlights ");
    query.append(
      "INNER JOIN project_highlights_country ON project_highlights_country.project_highlights_id = project_highlights.id ");
    query.append("INNER JOIN phases ON project_highlights_country.id_phase = phases.id ");
    query.append("WHERE project_highlights.id = ");
    query.append(higlightID);
    query.append(" AND phases.id = ");
    query.append(phaseID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<ProjectHighlightCountry> projectHighlightCountries = new ArrayList<ProjectHighlightCountry>();
    for (Map<String, Object> map : list) {
      ProjectHighlightCountry projectHighlightCountry = this.find(Long.parseLong(map.get("contryId").toString()));
      projectHighlightCountries.add(projectHighlightCountry);
    }
    return projectHighlightCountries;
  }

  @Override
  public ProjectHighlightCountry save(ProjectHighlightCountry projectHighlightCountry) {
    if (projectHighlightCountry.getId() == null) {
      super.saveEntity(projectHighlightCountry);
    } else {
      projectHighlightCountry = super.update(projectHighlightCountry);
    }


    return projectHighlightCountry;
  }


}