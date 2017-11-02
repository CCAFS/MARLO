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


import org.cgiar.ccafs.marlo.data.dao.FundingSourceBudgetDAO;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceBudgetManager;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class FundingSourceBudgetManagerImpl implements FundingSourceBudgetManager {


  private FundingSourceBudgetDAO fundingSourceBudgetDAO;
  // Managers


  @Inject
  public FundingSourceBudgetManagerImpl(FundingSourceBudgetDAO fundingSourceBudgetDAO) {
    this.fundingSourceBudgetDAO = fundingSourceBudgetDAO;


  }

  @Override
  public void deleteFundingSourceBudget(long fundingSourceBudgetId) {

    fundingSourceBudgetDAO.deleteFundingSourceBudget(fundingSourceBudgetId);
  }

  @Override
  public boolean existFundingSourceBudget(long fundingSourceBudgetID) {

    return fundingSourceBudgetDAO.existFundingSourceBudget(fundingSourceBudgetID);
  }

  @Override
  public List<FundingSourceBudget> findAll() {

    return fundingSourceBudgetDAO.findAll();

  }

  @Override
  public FundingSourceBudget getByFundingSourceAndYear(long fundingSourceID, int year) {
    return fundingSourceBudgetDAO.getByFundingSourceAndYear(fundingSourceID, year);
  }

  @Override
  public FundingSourceBudget getFundingSourceBudgetById(long fundingSourceBudgetID) {

    return fundingSourceBudgetDAO.find(fundingSourceBudgetID);
  }

  @Override
  public FundingSourceBudget saveFundingSourceBudget(FundingSourceBudget fundingSourceBudget) {

    return fundingSourceBudgetDAO.save(fundingSourceBudget);
  }


}
