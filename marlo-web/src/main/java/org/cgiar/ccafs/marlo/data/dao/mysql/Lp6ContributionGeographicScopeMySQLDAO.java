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

import org.cgiar.ccafs.marlo.data.dao.Lp6ContributionGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.model.Lp6ContributionGeographicScope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class Lp6ContributionGeographicScopeMySQLDAO extends AbstractMarloDAO<Lp6ContributionGeographicScope, Long>
  implements Lp6ContributionGeographicScopeDAO {


  @Inject
  public Lp6ContributionGeographicScopeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteLp6ContributionGeographicScope(long lp6ContributionGeographicScopeID) {
    Lp6ContributionGeographicScope lp6ContributionGeographicScope = this.find(lp6ContributionGeographicScopeID);

    this.delete(lp6ContributionGeographicScope);
  }

  @Override
  public boolean existLp6ContributionGeographicScope(long lp6ContributionGeographicScopeID) {
    Lp6ContributionGeographicScope lp6ContributionGeographicScope = this.find(lp6ContributionGeographicScopeID);
    if (lp6ContributionGeographicScope == null) {
      return false;
    }
    return true;

  }

  @Override
  public Lp6ContributionGeographicScope find(long id) {
    return super.find(Lp6ContributionGeographicScope.class, id);

  }

  @Override
  public List<Lp6ContributionGeographicScope> findAll() {
    String query = "from " + Lp6ContributionGeographicScope.class.getName();
    List<Lp6ContributionGeographicScope> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<Lp6ContributionGeographicScope> getLp6ContributionGeographicScopebyPhase(long projectLp6ContributionID,
    long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT lp6_contribution_geographic_scope.id as contryId FROM project_lp6_contribution ");
    query.append(
      "INNER JOIN lp6_contribution_geographic_scope ON lp6_contribution_geographic_scope.lp6_contribution_id = project_lp6_contribution.id ");
    query.append("INNER JOIN phases ON lp6_contribution_geographic_scope.id_phase = phases.id ");
    query.append("WHERE project_lp6_contribution.id = ");
    query.append(projectLp6ContributionID);
    query.append(" AND phases.id = ");
    query.append(phaseID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<Lp6ContributionGeographicScope> projectLp6ContributionLocations =
      new ArrayList<Lp6ContributionGeographicScope>();
    for (Map<String, Object> map : list) {
      String contryId = map.get("contryId").toString();
      long longContryId = Long.parseLong(contryId);
      Lp6ContributionGeographicScope projectLp6Geographic = this.find(longContryId);
      projectLp6ContributionLocations.add(projectLp6Geographic);
    }
    return projectLp6ContributionLocations;
  }

  @Override
  public Lp6ContributionGeographicScope save(Lp6ContributionGeographicScope lp6ContributionGeographicScope) {
    if (lp6ContributionGeographicScope.getId() == null) {
      super.saveEntity(lp6ContributionGeographicScope);
    } else {
      lp6ContributionGeographicScope = super.update(lp6ContributionGeographicScope);
    }


    return lp6ContributionGeographicScope;
  }


}