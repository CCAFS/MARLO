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

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyCountryDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPolicyCountryMySQLDAO extends AbstractMarloDAO<ProjectPolicyCountry, Long>
  implements ProjectPolicyCountryDAO {


  @Inject
  public ProjectPolicyCountryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPolicyCountry(long projectPolicyCountryId) {
    ProjectPolicyCountry projectPolicyCountry = this.find(projectPolicyCountryId);
    this.delete(projectPolicyCountry.getId());
  }

  @Override
  public boolean existProjectPolicyCountry(long projectPolicyCountryID) {
    ProjectPolicyCountry projectPolicyCountry = this.find(projectPolicyCountryID);
    if (projectPolicyCountry == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPolicyCountry find(long id) {
    return super.find(ProjectPolicyCountry.class, id);

  }

  @Override
  public List<ProjectPolicyCountry> findAll() {
    String query = "from " + ProjectPolicyCountry.class.getName();
    List<ProjectPolicyCountry> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectPolicyCountry> getPolicyCountrybyPhase(long policyID, long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT project_policy_countries.id as contryId FROM project_policies ");
    query.append(
      "INNER JOIN project_policy_countries ON project_policy_countries.project_policy_id = project_policies.id ");
    query.append("INNER JOIN phases ON project_policy_countries.id_phase = phases.id ");
    query.append("WHERE project_policies.id = ");
    query.append(policyID);
    query.append(" AND phases.id = ");
    query.append(phaseID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<ProjectPolicyCountry> projectPolicyCountries = new ArrayList<ProjectPolicyCountry>();
    for (Map<String, Object> map : list) {
      String contryId = map.get("contryId").toString();
      long longContryId = Long.parseLong(contryId);
      ProjectPolicyCountry projectPolicyCountry = this.find(longContryId);
      projectPolicyCountries.add(projectPolicyCountry);
    }
    return projectPolicyCountries;
  }

  @Override
  public ProjectPolicyCountry save(ProjectPolicyCountry projectPolicyCountry) {
    if (projectPolicyCountry.getId() == null) {
      super.saveEntity(projectPolicyCountry);
    } else {
      projectPolicyCountry = super.update(projectPolicyCountry);
    }


    return projectPolicyCountry;
  }


}