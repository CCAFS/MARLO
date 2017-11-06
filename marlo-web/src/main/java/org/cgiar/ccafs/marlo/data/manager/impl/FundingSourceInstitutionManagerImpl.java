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


import org.cgiar.ccafs.marlo.data.dao.FundingSourceInstitutionDAO;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInstitutionManager;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class FundingSourceInstitutionManagerImpl implements FundingSourceInstitutionManager {


  private FundingSourceInstitutionDAO fundingSourceInstitutionDAO;
  // Managers


  @Inject
  public FundingSourceInstitutionManagerImpl(FundingSourceInstitutionDAO fundingSourceInstitutionDAO) {
    this.fundingSourceInstitutionDAO = fundingSourceInstitutionDAO;


  }

  @Override
  public void deleteFundingSourceInstitution(long fundingSourceInstitutionId) {

    fundingSourceInstitutionDAO.deleteFundingSourceInstitution(fundingSourceInstitutionId);
  }

  @Override
  public boolean existFundingSourceInstitution(long fundingSourceInstitutionID) {

    return fundingSourceInstitutionDAO.existFundingSourceInstitution(fundingSourceInstitutionID);
  }

  @Override
  public List<FundingSourceInstitution> findAll() {

    return fundingSourceInstitutionDAO.findAll();

  }

  @Override
  public FundingSourceInstitution getFundingSourceInstitutionById(long fundingSourceInstitutionID) {

    return fundingSourceInstitutionDAO.find(fundingSourceInstitutionID);
  }

  @Override
  public FundingSourceInstitution saveFundingSourceInstitution(FundingSourceInstitution fundingSourceInstitution) {

    return fundingSourceInstitutionDAO.save(fundingSourceInstitution);
  }


}
