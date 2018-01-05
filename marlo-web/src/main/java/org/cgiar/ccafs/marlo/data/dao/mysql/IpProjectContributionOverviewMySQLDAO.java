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

import org.cgiar.ccafs.marlo.data.dao.IpProjectContributionOverviewDAO;
import org.cgiar.ccafs.marlo.data.model.IpProjectContributionOverview;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class IpProjectContributionOverviewMySQLDAO extends AbstractMarloDAO<IpProjectContributionOverview, Long>
  implements IpProjectContributionOverviewDAO {


  @Inject
  public IpProjectContributionOverviewMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteIpProjectContributionOverview(long ipProjectContributionOverviewId) {
    IpProjectContributionOverview ipProjectContributionOverview = this.find(ipProjectContributionOverviewId);
    ipProjectContributionOverview.setActive(false);
    this.save(ipProjectContributionOverview);
  }

  @Override
  public boolean existIpProjectContributionOverview(long ipProjectContributionOverviewID) {
    IpProjectContributionOverview ipProjectContributionOverview = this.find(ipProjectContributionOverviewID);
    if (ipProjectContributionOverview == null) {
      return false;
    }
    return true;

  }

  @Override
  public IpProjectContributionOverview find(long id) {
    return super.find(IpProjectContributionOverview.class, id);

  }

  @Override
  public List<IpProjectContributionOverview> findAll() {
    String query = "from " + IpProjectContributionOverview.class.getName() + " where is_active=1";
    List<IpProjectContributionOverview> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<IpProjectContributionOverview> getProjectContributionOverviewsSynthesis(long mogId, int year,
    long program) {
    StringBuilder query = new StringBuilder();
    query.append("select * from ip_project_contribution_overviews where YEAR= ");
    query.append(year + " and output_id=" + mogId
      + " and is_active=1 and project_id in (select project_id from project_focuses where program_id=" + program + ")");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());

    List<IpProjectContributionOverview> contributionOverviews = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        IpProjectContributionOverview contributionOverview = this.find(Long.parseLong(map.get("id").toString()));
        contributionOverviews.add(contributionOverview);
      }
    }

    return contributionOverviews;
  }

  @Override
  public List<IpProjectContributionOverview> getProjectContributionOverviewsSynthesisGlobal(long mogId, int year,
    long program) {
    StringBuilder query = new StringBuilder();
    query.append("select * from ip_project_contribution_overviews where YEAR= ");
    query.append(year + " and output_id=" + mogId
      + " and is_active=1 and project_id in (SELECT projects.id FROM projects INNER JOIN ip_global_projects "
      + "ON ip_global_projects.project_id = projects.id WHERE projects.is_active = 1 AND projects.reporting = 1 "
      + "AND projects.is_location_global = 1)");


    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());

    List<IpProjectContributionOverview> contributionOverviews = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        IpProjectContributionOverview contributionOverview = this.find(Long.parseLong(map.get("id").toString()));
        contributionOverviews.add(contributionOverview);
      }
    }

    return contributionOverviews;
  }

  @Override
  public IpProjectContributionOverview save(IpProjectContributionOverview ipProjectContributionOverview) {
    if (ipProjectContributionOverview.getId() == null) {
      super.saveEntity(ipProjectContributionOverview);
    } else {
      ipProjectContributionOverview = super.update(ipProjectContributionOverview);
    }


    return ipProjectContributionOverview;
  }


}