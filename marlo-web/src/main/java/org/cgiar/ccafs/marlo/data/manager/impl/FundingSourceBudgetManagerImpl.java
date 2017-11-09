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


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.FundingSourceBudgetDAO;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceBudgetManager;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class FundingSourceBudgetManagerImpl implements FundingSourceBudgetManager {


  private FundingSourceBudgetDAO fundingSourceBudgetDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public FundingSourceBudgetManagerImpl(FundingSourceBudgetDAO fundingSourceBudgetDAO, PhaseDAO phaseDAO) {
    this.fundingSourceBudgetDAO = fundingSourceBudgetDAO;
    this.phaseDAO = phaseDAO;


  }

  public void cloneFundingSourceBudget(FundingSourceBudget fundingSourceBudgetAdd,
    FundingSourceBudget fundingSourceBudget, Phase phase) {
    fundingSourceBudgetAdd.setFundingSource(fundingSourceBudget.getFundingSource());
    fundingSourceBudgetAdd.setActive(true);
    fundingSourceBudgetAdd.setActiveSince(new Date());
    fundingSourceBudgetAdd.setCreatedBy(fundingSourceBudget.getCreatedBy());
    fundingSourceBudgetAdd.setBudget(fundingSourceBudget.getBudget());
    fundingSourceBudgetAdd.setModifiedBy(fundingSourceBudget.getCreatedBy());
    fundingSourceBudgetAdd.setModificationJustification(fundingSourceBudget.getModificationJustification());
    fundingSourceBudgetAdd.setYear(fundingSourceBudget.getYear());
    fundingSourceBudgetAdd.setPhase(phase);

  }

  @Override
  public void deleteFundingSourceBudget(long fundingSourceBudgetId) {

    fundingSourceBudgetDAO.deleteFundingSourceBudget(fundingSourceBudgetId);
    FundingSourceBudget fundingSourceBudget = this.getFundingSourceBudgetById(fundingSourceBudgetId);
    Phase currentPhase = phaseDAO.find(fundingSourceBudget.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {

      if (fundingSourceBudget.getPhase().getNext() != null) {
        this.deletFundingSourceBudgetPhase(fundingSourceBudget.getPhase().getNext(),
          fundingSourceBudget.getFundingSource().getId(), fundingSourceBudget);
      }
    }
  }

  public void deletFundingSourceBudgetPhase(Phase next, long fundingSourceID, FundingSourceBudget fundingSourceBudget) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<FundingSourceBudget> budgets = phase.getFundingSourceBudgets().stream()
        .filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
          && c.getYear().equals(fundingSourceBudget.getYear()))
        .collect(Collectors.toList());
      for (FundingSourceBudget fundingSourceBudgetDB : budgets) {
        fundingSourceBudgetDB.setActive(false);
        fundingSourceBudgetDAO.save(fundingSourceBudgetDB);
      }
    }
    if (phase.getNext() != null) {
      this.deletFundingSourceBudgetPhase(phase.getNext(), fundingSourceID, fundingSourceBudget);

    }

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

    FundingSourceBudget budget = fundingSourceBudgetDAO.save(fundingSourceBudget);
    Phase currentPhase = phaseDAO.find(fundingSourceBudget.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (fundingSourceBudget.getPhase().getNext() != null) {
        if (fundingSourceBudget.getYear() != null) {
          this.saveFundingSourceBudgetPhase(fundingSourceBudget.getPhase().getNext(),
            fundingSourceBudget.getFundingSource().getId(), fundingSourceBudget);
        }

      }
    }

    return budget;

  }

  public void saveFundingSourceBudgetPhase(Phase next, long fundingSourceID, FundingSourceBudget fundingSourceBudget) {
    Phase phase = phaseDAO.find(next.getId());
    if (phase.getEditable() != null && phase.getEditable()) {
      List<FundingSourceBudget> budgets = phase.getFundingSourceBudgets()
        .stream().filter(c -> c.isActive() && c.getFundingSource().getId().longValue() == fundingSourceID
          && c.getYear() != null && c.getYear().intValue() == fundingSourceBudget.getYear().intValue())
        .collect(Collectors.toList());
      if (budgets.isEmpty()) {
        FundingSourceBudget fundingSourceBudgetAdd = new FundingSourceBudget();
        this.cloneFundingSourceBudget(fundingSourceBudgetAdd, fundingSourceBudget, phase);
        fundingSourceBudgetDAO.save(fundingSourceBudgetAdd);
      } else {
        FundingSourceBudget fundingSourceBudgetAdd = budgets.get(0);
        this.cloneFundingSourceBudget(fundingSourceBudgetAdd, fundingSourceBudget, phase);
        fundingSourceBudgetDAO.save(fundingSourceBudgetAdd);
      }

    }
    if (phase.getNext() != null) {
      if (fundingSourceBudget.getYear() != null) {
        this.saveFundingSourceBudgetPhase(phase.getNext(), fundingSourceID, fundingSourceBudget);
      }

    }


  }

}
