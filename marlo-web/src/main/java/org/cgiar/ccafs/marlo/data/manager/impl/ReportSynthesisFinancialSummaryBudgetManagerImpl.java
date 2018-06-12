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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFinancialSummaryBudgetDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFinancialSummaryBudgetManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummaryBudget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFinancialSummaryBudgetManagerImpl implements ReportSynthesisFinancialSummaryBudgetManager {


  private ReportSynthesisFinancialSummaryBudgetDAO reportSynthesisFinancialSummaryBudgetDAO;
  // Managers


  @Inject
  public ReportSynthesisFinancialSummaryBudgetManagerImpl(
    ReportSynthesisFinancialSummaryBudgetDAO reportSynthesisFinancialSummaryBudgetDAO) {
    this.reportSynthesisFinancialSummaryBudgetDAO = reportSynthesisFinancialSummaryBudgetDAO;


  }

  @Override
  public void deleteReportSynthesisFinancialSummaryBudget(long reportSynthesisFinancialSummaryBudgetId) {

    reportSynthesisFinancialSummaryBudgetDAO
      .deleteReportSynthesisFinancialSummaryBudget(reportSynthesisFinancialSummaryBudgetId);
  }

  @Override
  public boolean existReportSynthesisFinancialSummaryBudget(long reportSynthesisFinancialSummaryBudgetID) {

    return reportSynthesisFinancialSummaryBudgetDAO
      .existReportSynthesisFinancialSummaryBudget(reportSynthesisFinancialSummaryBudgetID);
  }

  @Override
  public List<ReportSynthesisFinancialSummaryBudget> findAll() {

    return reportSynthesisFinancialSummaryBudgetDAO.findAll();

  }

  @Override
  public ReportSynthesisFinancialSummaryBudget
    getReportSynthesisFinancialSummaryBudgetById(long reportSynthesisFinancialSummaryBudgetID) {

    return reportSynthesisFinancialSummaryBudgetDAO.find(reportSynthesisFinancialSummaryBudgetID);
  }

  @Override
  public double getTotalW1W2ActualExpenditure(long reportSynthesisId) {
    return reportSynthesisFinancialSummaryBudgetDAO.getTotalW1W2ActualExpenditure(reportSynthesisId);
  }

  @Override
  public ReportSynthesisFinancialSummaryBudget saveReportSynthesisFinancialSummaryBudget(
    ReportSynthesisFinancialSummaryBudget reportSynthesisFinancialSummaryBudget) {

    return reportSynthesisFinancialSummaryBudgetDAO.save(reportSynthesisFinancialSummaryBudget);
  }


}
