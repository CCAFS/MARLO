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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFundingUseSummaryDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFundingUseSummaryManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseSummary;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFundingUseSummaryManagerImpl implements ReportSynthesisFundingUseSummaryManager {


  private ReportSynthesisFundingUseSummaryDAO reportSynthesisFundingUseSummaryDAO;
  // Managers


  @Inject
  public ReportSynthesisFundingUseSummaryManagerImpl(ReportSynthesisFundingUseSummaryDAO reportSynthesisFundingUseSummaryDAO) {
    this.reportSynthesisFundingUseSummaryDAO = reportSynthesisFundingUseSummaryDAO;


  }

  @Override
  public void deleteReportSynthesisFundingUseSummary(long reportSynthesisFundingUseSummaryId) {

    reportSynthesisFundingUseSummaryDAO.deleteReportSynthesisFundingUseSummary(reportSynthesisFundingUseSummaryId);
  }

  @Override
  public boolean existReportSynthesisFundingUseSummary(long reportSynthesisFundingUseSummaryID) {

    return reportSynthesisFundingUseSummaryDAO.existReportSynthesisFundingUseSummary(reportSynthesisFundingUseSummaryID);
  }

  @Override
  public List<ReportSynthesisFundingUseSummary> findAll() {

    return reportSynthesisFundingUseSummaryDAO.findAll();

  }

  @Override
  public ReportSynthesisFundingUseSummary getReportSynthesisFundingUseSummaryById(long reportSynthesisFundingUseSummaryID) {

    return reportSynthesisFundingUseSummaryDAO.find(reportSynthesisFundingUseSummaryID);
  }

  @Override
  public ReportSynthesisFundingUseSummary saveReportSynthesisFundingUseSummary(ReportSynthesisFundingUseSummary reportSynthesisFundingUseSummary) {

    return reportSynthesisFundingUseSummaryDAO.save(reportSynthesisFundingUseSummary);
  }


}
