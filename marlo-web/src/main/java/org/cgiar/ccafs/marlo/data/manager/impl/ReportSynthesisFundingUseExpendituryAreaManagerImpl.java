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
import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFundingUseExpendituryAreaDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFundingUseExpendituryAreaManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseExpendituryArea;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFundingUseExpendituryAreaManagerImpl
  implements ReportSynthesisFundingUseExpendituryAreaManager {


  private ReportSynthesisFundingUseExpendituryAreaDAO reportSynthesisFundingUseExpendituryAreaDAO;
  private ReportSynthesisFinancialSummaryBudgetDAO reportSynthesisFinancialSummaryBudgetDAO;
  // Managers


  @Inject
  public ReportSynthesisFundingUseExpendituryAreaManagerImpl(
    ReportSynthesisFundingUseExpendituryAreaDAO reportSynthesisFundingUseExpendituryAreaDAO,
    ReportSynthesisFinancialSummaryBudgetDAO reportSynthesisFinancialSummaryBudgetDAO) {
    this.reportSynthesisFundingUseExpendituryAreaDAO = reportSynthesisFundingUseExpendituryAreaDAO;
    this.reportSynthesisFinancialSummaryBudgetDAO = reportSynthesisFinancialSummaryBudgetDAO;


  }

  @Override
  public void deleteReportSynthesisFundingUseExpendituryArea(long reportSynthesisFundingUseExpendituryAreaId) {

    reportSynthesisFundingUseExpendituryAreaDAO
      .deleteReportSynthesisFundingUseExpendituryArea(reportSynthesisFundingUseExpendituryAreaId);
  }

  @Override
  public boolean existReportSynthesisFundingUseExpendituryArea(long reportSynthesisFundingUseExpendituryAreaID) {

    return reportSynthesisFundingUseExpendituryAreaDAO
      .existReportSynthesisFundingUseExpendituryArea(reportSynthesisFundingUseExpendituryAreaID);
  }

  @Override
  public List<ReportSynthesisFundingUseExpendituryArea> findAll() {

    return reportSynthesisFundingUseExpendituryAreaDAO.findAll();

  }

  @Override
  public ReportSynthesisFundingUseExpendituryArea
    getReportSynthesisFundingUseExpendituryAreaById(long reportSynthesisFundingUseExpendituryAreaID) {

    return reportSynthesisFundingUseExpendituryAreaDAO.find(reportSynthesisFundingUseExpendituryAreaID);
  }

  @Override
  public double getTotalEstimatedOfW1W2ActualExpenditure(long reportSynthesisId) {
    double totalW1W2 = reportSynthesisFinancialSummaryBudgetDAO.getTotalW1W2ActualExpenditure(reportSynthesisId);
    double totalPercentaje = this.getTotalW1W2Percentage(reportSynthesisId);
    return totalW1W2 * totalPercentaje / 100;
  }

  @Override
  public double getTotalW1W2Percentage(long reportSynthesisId) {
    return reportSynthesisFundingUseExpendituryAreaDAO.getTotalW1W2Percentage(reportSynthesisId);
  }

  @Override
  public ReportSynthesisFundingUseExpendituryArea saveReportSynthesisFundingUseExpendituryArea(
    ReportSynthesisFundingUseExpendituryArea reportSynthesisFundingUseExpendituryArea) {

    return reportSynthesisFundingUseExpendituryAreaDAO.save(reportSynthesisFundingUseExpendituryArea);
  }


}
