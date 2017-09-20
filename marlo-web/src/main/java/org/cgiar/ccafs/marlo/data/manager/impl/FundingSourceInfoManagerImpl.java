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


import org.cgiar.ccafs.marlo.data.dao.FundingSourceInfoDAO;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInfoManager;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInfo;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class FundingSourceInfoManagerImpl implements FundingSourceInfoManager {


  private FundingSourceInfoDAO fundingSourceInfoDAO;
  // Managers


  @Inject
  public FundingSourceInfoManagerImpl(FundingSourceInfoDAO fundingSourceInfoDAO) {
    this.fundingSourceInfoDAO = fundingSourceInfoDAO;


  }

  @Override
  public boolean deleteFundingSourceInfo(long fundingSourceInfoId) {

    return fundingSourceInfoDAO.deleteFundingSourceInfo(fundingSourceInfoId);
  }

  @Override
  public boolean existFundingSourceInfo(long fundingSourceInfoID) {

    return fundingSourceInfoDAO.existFundingSourceInfo(fundingSourceInfoID);
  }

  @Override
  public List<FundingSourceInfo> findAll() {

    return fundingSourceInfoDAO.findAll();

  }

  @Override
  public FundingSourceInfo getFundingSourceInfoById(long fundingSourceInfoID) {

    return fundingSourceInfoDAO.find(fundingSourceInfoID);
  }

  @Override
  public long saveFundingSourceInfo(FundingSourceInfo fundingSourceInfo) {

    return fundingSourceInfoDAO.save(fundingSourceInfo);
  }


}
