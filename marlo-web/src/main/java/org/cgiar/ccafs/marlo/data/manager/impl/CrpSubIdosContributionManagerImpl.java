/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.CrpSubIdosContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpSubIdosContributionManager;
import org.cgiar.ccafs.marlo.data.model.CrpSubIdosContribution;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CrpSubIdosContributionManagerImpl implements CrpSubIdosContributionManager {


  private CrpSubIdosContributionDAO crpSubIdosContributionDAO;
  // Managers


  @Inject
  public CrpSubIdosContributionManagerImpl(CrpSubIdosContributionDAO crpSubIdosContributionDAO) {
    this.crpSubIdosContributionDAO = crpSubIdosContributionDAO;


  }

  @Override
  public boolean deleteCrpSubIdosContribution(long crpSubIdosContributionId) {

    return crpSubIdosContributionDAO.deleteCrpSubIdosContribution(crpSubIdosContributionId);
  }

  @Override
  public boolean existCrpSubIdosContribution(long crpSubIdosContributionID) {

    return crpSubIdosContributionDAO.existCrpSubIdosContribution(crpSubIdosContributionID);
  }

  @Override
  public List<CrpSubIdosContribution> findAll() {

    return crpSubIdosContributionDAO.findAll();

  }

  @Override
  public CrpSubIdosContribution getCrpSubIdosContributionById(long crpSubIdosContributionID) {

    return crpSubIdosContributionDAO.find(crpSubIdosContributionID);
  }

  @Override
  public long saveCrpSubIdosContribution(CrpSubIdosContribution crpSubIdosContribution) {

    return crpSubIdosContributionDAO.save(crpSubIdosContribution);
  }


}
