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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.IpProjectContributionOverviewDAO;
import org.cgiar.ccafs.marlo.data.manager.IpProjectContributionOverviewManager;
import org.cgiar.ccafs.marlo.data.model.IpProjectContributionOverview;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class IpProjectContributionOverviewManagerImpl implements IpProjectContributionOverviewManager {


  private IpProjectContributionOverviewDAO ipProjectContributionOverviewDAO;
  // Managers


  @Inject
  public IpProjectContributionOverviewManagerImpl(IpProjectContributionOverviewDAO ipProjectContributionOverviewDAO) {
    this.ipProjectContributionOverviewDAO = ipProjectContributionOverviewDAO;


  }

  @Override
  public void deleteIpProjectContributionOverview(long ipProjectContributionOverviewId) {

    ipProjectContributionOverviewDAO.deleteIpProjectContributionOverview(ipProjectContributionOverviewId);
  }

  @Override
  public boolean existIpProjectContributionOverview(long ipProjectContributionOverviewID) {

    return ipProjectContributionOverviewDAO.existIpProjectContributionOverview(ipProjectContributionOverviewID);
  }

  @Override
  public List<IpProjectContributionOverview> findAll() {

    return ipProjectContributionOverviewDAO.findAll();

  }

  @Override
  public IpProjectContributionOverview getIpProjectContributionOverviewById(long ipProjectContributionOverviewID) {

    return ipProjectContributionOverviewDAO.find(ipProjectContributionOverviewID);
  }

  @Override
  public List<IpProjectContributionOverview> getProjectContributionOverviewsSytnhesis(long mogId, int year,
    long program) {
    return ipProjectContributionOverviewDAO.getProjectContributionOverviewsSynthesis(mogId, year, program);
  }

  @Override
  public List<IpProjectContributionOverview> getProjectContributionOverviewsSytnhesisGlobal(long mogId, int year,
    long program) {
    return ipProjectContributionOverviewDAO.getProjectContributionOverviewsSynthesisGlobal(mogId, year, program);
  }

  @Override
  public IpProjectContributionOverview saveIpProjectContributionOverview(IpProjectContributionOverview ipProjectContributionOverview) {

    return ipProjectContributionOverviewDAO.save(ipProjectContributionOverview);
  }


}
