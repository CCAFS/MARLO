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


import org.cgiar.ccafs.marlo.data.dao.IpProjectContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.IpProjectContributionManager;
import org.cgiar.ccafs.marlo.data.model.IpProjectContribution;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class IpProjectContributionManagerImpl implements IpProjectContributionManager {


  private IpProjectContributionDAO ipProjectContributionDAO;
  // Managers


  @Inject
  public IpProjectContributionManagerImpl(IpProjectContributionDAO ipProjectContributionDAO) {
    this.ipProjectContributionDAO = ipProjectContributionDAO;


  }

  @Override
  public boolean deleteIpProjectContribution(long ipProjectContributionId) {

    return ipProjectContributionDAO.deleteIpProjectContribution(ipProjectContributionId);
  }

  @Override
  public boolean existIpProjectContribution(long ipProjectContributionID) {

    return ipProjectContributionDAO.existIpProjectContribution(ipProjectContributionID);
  }

  @Override
  public List<IpProjectContribution> findAll() {

    return ipProjectContributionDAO.findAll();

  }

  @Override
  public IpProjectContribution getIpProjectContributionById(long ipProjectContributionID) {

    return ipProjectContributionDAO.find(ipProjectContributionID);
  }

  @Override
  public long saveIpProjectContribution(IpProjectContribution ipProjectContribution) {

    return ipProjectContributionDAO.save(ipProjectContribution);
  }


}
