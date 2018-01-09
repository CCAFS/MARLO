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


import org.cgiar.ccafs.marlo.data.dao.CrpSubIdosContributionDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpSubIdosContributionManager;
import org.cgiar.ccafs.marlo.data.model.CrpSubIdosContribution;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class CrpSubIdosContributionManagerImpl implements CrpSubIdosContributionManager {


  private CrpSubIdosContributionDAO crpSubIdosContributionDAO;
  // Managers


  @Inject
  public CrpSubIdosContributionManagerImpl(CrpSubIdosContributionDAO crpSubIdosContributionDAO) {
    this.crpSubIdosContributionDAO = crpSubIdosContributionDAO;


  }

  @Override
  public void deleteCrpSubIdosContribution(long crpSubIdosContributionId) {

    crpSubIdosContributionDAO.deleteCrpSubIdosContribution(crpSubIdosContributionId);
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
  public CrpSubIdosContribution saveCrpSubIdosContribution(CrpSubIdosContribution crpSubIdosContribution) {

    return crpSubIdosContributionDAO.save(crpSubIdosContribution);
  }


}
