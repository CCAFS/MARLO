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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrpFinancialReportDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpFinancialReportManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpFinancialReport;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisCrpFinancialReportManagerImpl implements ReportSynthesisCrpFinancialReportManager {

  private ReportSynthesisCrpFinancialReportDAO reportSynthesisCrpFinancialReportDAO;


  @Inject
  public ReportSynthesisCrpFinancialReportManagerImpl(
    ReportSynthesisCrpFinancialReportDAO reportSynthesisCrpFinancialReportDAO) {
    this.reportSynthesisCrpFinancialReportDAO = reportSynthesisCrpFinancialReportDAO;
  }

  @Override
  public void deleteReportSynthesisCrpFinancialReport(long reportSynthesisCrpFinancialReportId) {
    reportSynthesisCrpFinancialReportDAO.deleteReportSynthesisCrpFinancialReport(reportSynthesisCrpFinancialReportId);
  }

  @Override
  public boolean existReportSynthesisCrpFinancialReport(long reportSynthesisCrpFinancialReportID) {
    return reportSynthesisCrpFinancialReportDAO
      .existReportSynthesisCrpFinancialReport(reportSynthesisCrpFinancialReportID);
  }

  @Override
  public List<ReportSynthesisCrpFinancialReport> findAll() {
    return reportSynthesisCrpFinancialReportDAO.findAll();
  }

  @Override
  public ReportSynthesisCrpFinancialReport findByReportSynthesis(long reportSynthesisId) {
    return reportSynthesisCrpFinancialReportDAO.findByReportSynthesis(reportSynthesisId);
  }

  @Override
  public ReportSynthesisCrpFinancialReport
    getReportSynthesisCrpFinancialReportById(long reportSynthesisCrpFinancialReportID) {
    return reportSynthesisCrpFinancialReportDAO.find(reportSynthesisCrpFinancialReportID);
  }

  @Override
  public ReportSynthesisCrpFinancialReport
    saveReportSynthesisCrpFinancialReport(ReportSynthesisCrpFinancialReport reportSynthesisCrpFinancialReport) {
    return reportSynthesisCrpFinancialReportDAO.save(reportSynthesisCrpFinancialReport);
  }

}
