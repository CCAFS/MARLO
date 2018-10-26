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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyCountryDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyCountryMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyCountry, Long>
  implements ProjectExpectedStudyCountryDAO {


  @Inject
  public ProjectExpectedStudyCountryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyCountry(long projectExpectedStudyCountryId) {
    ProjectExpectedStudyCountry projectExpectedStudyCountry = this.find(projectExpectedStudyCountryId);
    this.delete(projectExpectedStudyCountry);
  }

  @Override
  public boolean existProjectExpectedStudyCountry(long projectExpectedStudyCountryID) {
    ProjectExpectedStudyCountry projectExpectedStudyCountry = this.find(projectExpectedStudyCountryID);
    if (projectExpectedStudyCountry == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyCountry find(long id) {
    return super.find(ProjectExpectedStudyCountry.class, id);

  }

  @Override
  public List<ProjectExpectedStudyCountry> findAll() {
    String query = "from " + ProjectExpectedStudyCountry.class.getName();
    List<ProjectExpectedStudyCountry> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectExpectedStudyCountry> getProjectExpectedStudyCountrybyPhase(long expectedID, long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT project_expected_study_countries.id as contryId FROM project_expected_studies ");
    query.append(
      "INNER JOIN project_expected_study_countries ON project_expected_study_countries.expected_id = project_expected_studies.id ");
    query.append("INNER JOIN phases ON project_expected_study_countries.id_phase = phases.id ");
    query.append("WHERE project_expected_studies.id = ");
    query.append(expectedID);
    query.append(" AND phases.id = ");
    query.append(phaseID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<ProjectExpectedStudyCountry> projectExpectedStudyCountries = new ArrayList<ProjectExpectedStudyCountry>();
    for (Map<String, Object> map : list) {
      String contryId = map.get("contryId").toString();
      long longContryId = Long.parseLong(contryId);
      ProjectExpectedStudyCountry projectExpectedStudyCountry = this.find(longContryId);
      projectExpectedStudyCountries.add(projectExpectedStudyCountry);
    }
    return projectExpectedStudyCountries;
  }

  @Override
  public ProjectExpectedStudyCountry save(ProjectExpectedStudyCountry projectExpectedStudyCountry) {
    if (projectExpectedStudyCountry.getId() == null) {
      super.saveEntity(projectExpectedStudyCountry);
    } else {
      projectExpectedStudyCountry = super.update(projectExpectedStudyCountry);
    }


    return projectExpectedStudyCountry;
  }


}