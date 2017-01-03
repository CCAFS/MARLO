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

import java.util.List;

import com.google.inject.Inject;

public class IpProjectContributionOverviewMySQLDAO implements IpProjectContributionOverviewDAO {

  private StandardDAO dao;

  @Inject
  public IpProjectContributionOverviewMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteIpProjectContributionOverview(long ipProjectContributionOverviewId) {
    IpProjectContributionOverview ipProjectContributionOverview = this.find(ipProjectContributionOverviewId);
    ipProjectContributionOverview.setActive(false);
    return this.save(ipProjectContributionOverview) > 0;
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
    return dao.find(IpProjectContributionOverview.class, id);

  }

  @Override
  public List<IpProjectContributionOverview> findAll() {
    String query = "from " + IpProjectContributionOverview.class.getName() + " where is_active=1";
    List<IpProjectContributionOverview> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(IpProjectContributionOverview ipProjectContributionOverview) {
    if (ipProjectContributionOverview.getId() == null) {
      dao.save(ipProjectContributionOverview);
    } else {
      dao.update(ipProjectContributionOverview);
    }


    return ipProjectContributionOverview.getId();
  }


}