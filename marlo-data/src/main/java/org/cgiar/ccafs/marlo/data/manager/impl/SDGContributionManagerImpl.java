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


import org.cgiar.ccafs.marlo.data.dao.SDGContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.SDGContributionManager;
import org.cgiar.ccafs.marlo.data.model.SDGContribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class SDGContributionManagerImpl implements SDGContributionManager {


  private SDGContributionDAO sDGContributionDAO;
  // Managers


  @Inject
  public SDGContributionManagerImpl(SDGContributionDAO sDGContributionDAO) {
    this.sDGContributionDAO = sDGContributionDAO;


  }

  @Override
  public void deleteSDGContribution(long sDGContributionId) {

    sDGContributionDAO.deleteSDGContribution(sDGContributionId);
  }

  @Override
  public boolean existSDGContribution(long sDGContributionID) {

    return sDGContributionDAO.existSDGContribution(sDGContributionID);
  }

  @Override
  public List<SDGContribution> findAll() {

    return sDGContributionDAO.findAll();

  }

  @Override
  public SDGContribution getSDGContributionById(long sDGContributionID) {

    return sDGContributionDAO.find(sDGContributionID);
  }

  @Override
  public SDGContribution saveSDGContribution(SDGContribution sDGContribution) {

    return sDGContributionDAO.save(sDGContribution);
  }


}
