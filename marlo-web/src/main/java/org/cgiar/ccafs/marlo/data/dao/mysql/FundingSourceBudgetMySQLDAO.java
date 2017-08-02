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
import org.hibernate.SessionFactory;

public class FundingSourceBudgetMySQLDAO extends AbstractMarloDAO implements FundingSourceBudgetDAO {


  @Inject
  public FundingSourceBudgetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
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
    return super.find(FundingSourceBudget.class, id);

  }

  @Override
  public List<FundingSourceBudget> findAll() {
    String query = "from " + FundingSourceBudget.class.getName() + " where is_active=1";
    List<FundingSourceBudget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public FundingSourceBudget getByFundingSourceAndYear(long fundingSourceID, int year) {
    String query = "from " + FundingSourceBudget.class.getName() + " where funding_source_id= " + fundingSourceID
      + " and year= " + year + " and is_active=1";
    List<FundingSourceBudget> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public long save(FundingSourceBudget fundingSourceBudget) {

    String query = "from " + FundingSourceBudget.class.getName() + " where funding_source_id= "
      + fundingSourceBudget.getFundingSource().getId() + " and year= " + fundingSourceBudget.getYear()
      + " and is_active=1";
    List<FundingSourceBudget> list = super.findAll(query);
    if (list.size() > 0) {
      fundingSourceBudget.setId(list.get(0).getId());
      super.update(fundingSourceBudget);
    } else {
      super.save(fundingSourceBudget);
    }


    return fundingSourceBudget.getId();
  }


}