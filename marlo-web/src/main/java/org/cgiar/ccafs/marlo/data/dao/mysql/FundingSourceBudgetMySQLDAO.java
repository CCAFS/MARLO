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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.FundingSourceBudgetDAO;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;

import java.util.List;

import com.google.inject.Inject;

public class FundingSourceBudgetMySQLDAO implements FundingSourceBudgetDAO {

  private StandardDAO dao;

  @Inject
  public FundingSourceBudgetMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteFundingSourceBudget(long fundingSourceBudgetId) {
    FundingSourceBudget fundingSourceBudget = this.find(fundingSourceBudgetId);
    fundingSourceBudget.setActive(false);
    return this.save(fundingSourceBudget) > 0;
  }

  @Override
  public boolean existFundingSourceBudget(long fundingSourceBudgetID) {
    FundingSourceBudget fundingSourceBudget = this.find(fundingSourceBudgetID);
    if (fundingSourceBudget == null) {
      return false;
    }
    return true;

  }

  @Override
  public FundingSourceBudget find(long id) {
    return dao.find(FundingSourceBudget.class, id);

  }

  @Override
  public List<FundingSourceBudget> findAll() {
    String query = "from " + FundingSourceBudget.class.getName() + " where is_active=1";
    List<FundingSourceBudget> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public FundingSourceBudget getByFundingSourceAndYear(long fundingSourceID, int year) {
    String query = "from " + FundingSourceBudget.class.getName() + " where funding_source_id= " + fundingSourceID
      + " and year= " + year + " and is_active=1";
    List<FundingSourceBudget> list = dao.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public long save(FundingSourceBudget fundingSourceBudget) {
    if (fundingSourceBudget.getId() == null) {
      dao.save(fundingSourceBudget);
    } else {
      dao.update(fundingSourceBudget);
    }
    return fundingSourceBudget.getId();
  }


}