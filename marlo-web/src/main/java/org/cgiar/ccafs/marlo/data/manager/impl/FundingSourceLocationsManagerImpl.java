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


import org.cgiar.ccafs.marlo.data.dao.FundingSourceLocationsDAO;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceLocationsManager;
import org.cgiar.ccafs.marlo.data.model.FundingSourceLocation;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class FundingSourceLocationsManagerImpl implements FundingSourceLocationsManager {


  private FundingSourceLocationsDAO fundingSourceLocationsDAO;
  // Managers


  @Inject
  public FundingSourceLocationsManagerImpl(FundingSourceLocationsDAO fundingSourceLocationsDAO) {
    this.fundingSourceLocationsDAO = fundingSourceLocationsDAO;


  }

  @Override
  public void deleteFundingSourceLocations(long fundingSourceLocationsId) {

    fundingSourceLocationsDAO.deleteFundingSourceLocations(fundingSourceLocationsId);
  }

  @Override
  public boolean existFundingSourceLocations(long fundingSourceLocationsID) {

    return fundingSourceLocationsDAO.existFundingSourceLocations(fundingSourceLocationsID);
  }

  @Override
  public List<FundingSourceLocation> findAll() {

    return fundingSourceLocationsDAO.findAll();

  }

  @Override
  public FundingSourceLocation getFundingSourceLocationsById(long fundingSourceLocationsID) {

    return fundingSourceLocationsDAO.find(fundingSourceLocationsID);
  }

  @Override
  public FundingSourceLocation saveFundingSourceLocations(FundingSourceLocation fundingSourceLocations) {

    return fundingSourceLocationsDAO.save(fundingSourceLocations);
  }


}
