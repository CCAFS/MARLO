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
package org.cgiar.ccafs.marlo.data.service.impl;


import org.cgiar.ccafs.marlo.data.dao.ICenterFundingSourceTypeDAO;
import org.cgiar.ccafs.marlo.data.model.CenterFundingSourceType;
import org.cgiar.ccafs.marlo.data.service.ICenterFundingSourceTypeService;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class CenterFundingSourceTypeService implements ICenterFundingSourceTypeService {


  private ICenterFundingSourceTypeDAO fundingSourceTypeDAO;

  // Managers


  @Inject
  public CenterFundingSourceTypeService(ICenterFundingSourceTypeDAO fundingSourceTypeDAO) {
    this.fundingSourceTypeDAO = fundingSourceTypeDAO;


  }

  @Override
  public boolean deleteFundingSourceType(long fundingSourceTypeId) {

    return fundingSourceTypeDAO.deleteFundingSourceType(fundingSourceTypeId);
  }

  @Override
  public boolean existFundingSourceType(long fundingSourceTypeID) {

    return fundingSourceTypeDAO.existFundingSourceType(fundingSourceTypeID);
  }

  @Override
  public List<CenterFundingSourceType> findAll() {

    return fundingSourceTypeDAO.findAll();

  }

  @Override
  public CenterFundingSourceType getFundingSourceTypeById(long fundingSourceTypeID) {

    return fundingSourceTypeDAO.find(fundingSourceTypeID);
  }

  @Override
  public List<CenterFundingSourceType> getFundingSourceTypesByUserId(Long userId) {
    return fundingSourceTypeDAO.getFundingSourceTypesByUserId(userId);
  }

  @Override
  public long saveFundingSourceType(CenterFundingSourceType fundingSourceType) {

    return fundingSourceTypeDAO.save(fundingSourceType);
  }


}
