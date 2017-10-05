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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.FundingSourceBudgetDAO;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class FundingSourceBudgetMySQLDAO implements FundingSourceBudgetDAO {

  private StandardDAO dao;

  @Inject
  public FundingSourceBudgetMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  public void cloneFundingSourceBudget(FundingSourceBudget fundingSourceBudgetAdd,
    FundingSourceBudget fundingSourceBudget, Phase phase) {
    fundingSourceBudgetAdd.setFundingSource(fundingSourceBudget.getFundingSource());
    fundingSourceBudgetAdd.setActive(true);
    fundingSourceBudgetAdd.setActiveSince(new Date());
    fundingSourceBudgetAdd.setCreatedBy(fundingSourceBudget.getCreatedBy());
    fundingSourceBudgetAdd.setModificationJustification(fundingSourceBudget.getModificationJustification());
    fundingSourceBudgetAdd.setYear(fundingSourceBudget.getYear());
    fundingSourceBudgetAdd.setPhase(phase);

  }

  @Override
  public boolean deleteFundingSourceBudget(long fundingSourceBudgetId) {
    FundingSourceBudget fundingSourceBudget = this.find(fundingSourceBudgetId);
    fundingSourceBudget.setActive(false);
    boolean result = dao.update(fundingSourceBudget);
    Phase currentPhase = dao.find(Phase.class, fundingSourceBudget.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {

      if (fundingSourceBudget.getPhase().getNext() != null) {
        this.deletFundingSourceBudgetPhase(fundingSourceBudget.getPhase().getNext(),
          fundingSourceBudget.getFundingSource().getId(), fundingSourceBudget);
      }
    }
    return result;

  }

  public void deletFundingSourceBudgetPhase(Phase next, long fundingSourceID, FundingSourceBudget fundingSourceBudget) {
    Phase phase = dao.find(Phase.class, next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<FundingSourceBudget> budgets = phase.getFundingSourceBudgets().stream()
        .filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
          && c.getYear().equals(fundingSourceBudget.getYear()))
        .collect(Collectors.toList());
      for (FundingSourceBudget fundingSourceBudgetDB : budgets) {
        fundingSourceBudgetDB.setActive(false);
        dao.update(fundingSourceBudgetDB);
      }
    }
    if (phase.getNext() != null) {
      this.deletFundingSourceBudgetPhase(phase.getNext(), fundingSourceID, fundingSourceBudget);

    }
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

    String query = "from " + FundingSourceBudget.class.getName() + " where funding_source_id= "
      + fundingSourceBudget.getFundingSource().getId() + " and year= " + fundingSourceBudget.getYear()
      + " and is_active=1";
    List<FundingSourceBudget> list = dao.findAll(query);
    if (list.size() > 0) {
      fundingSourceBudget.setId(list.get(0).getId());
      dao.update(fundingSourceBudget);
    } else {
      dao.save(fundingSourceBudget);
    }
    Phase currentPhase = dao.find(Phase.class, fundingSourceBudget.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (fundingSourceBudget.getPhase().getNext() != null) {
        if (fundingSourceBudget.getYear() != null) {
          this.saveFundingSourceBudgetPhase(fundingSourceBudget.getPhase().getNext(),
            fundingSourceBudget.getFundingSource().getId(), fundingSourceBudget);
        }

      }
    }

    return fundingSourceBudget.getId();
  }

  public void saveFundingSourceBudgetPhase(Phase next, long fundingSourceID, FundingSourceBudget fundingSourceBudget) {
    Phase phase = dao.find(Phase.class, next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<FundingSourceBudget> budgets = phase.getFundingSourceBudgets()
        .stream().filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
          && c.getYear() != null && c.getYear().intValue() == fundingSourceBudget.getYear().intValue())
        .collect(Collectors.toList());
      if (budgets.isEmpty()) {
        FundingSourceBudget fundingSourceBudgetAdd = new FundingSourceBudget();
        this.cloneFundingSourceBudget(fundingSourceBudget, fundingSourceBudgetAdd, phase);
        dao.save(fundingSourceBudgetAdd);
      } else {
        FundingSourceBudget fundingSourceBudgetAdd = budgets.get(0);
        this.cloneFundingSourceBudget(fundingSourceBudget, fundingSourceBudgetAdd, phase);
        dao.update(fundingSourceBudgetAdd);
      }

    }
    if (phase.getNext() != null) {
      if (fundingSourceBudget.getYear() != null) {
        this.saveFundingSourceBudgetPhase(phase.getNext(), fundingSourceID, fundingSourceBudget);
      }

    }


  }
}