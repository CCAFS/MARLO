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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressOutcomeDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFlagshipProgressOutcomeManagerImpl
  implements ReportSynthesisFlagshipProgressOutcomeManager {


  private ReportSynthesisFlagshipProgressOutcomeDAO reportSynthesisFlagshipProgressOutcomeDAO;
  // Managers


  @Inject
  public ReportSynthesisFlagshipProgressOutcomeManagerImpl(
    ReportSynthesisFlagshipProgressOutcomeDAO reportSynthesisFlagshipProgressOutcomeDAO) {
    this.reportSynthesisFlagshipProgressOutcomeDAO = reportSynthesisFlagshipProgressOutcomeDAO;


  }

  @Override
  public void deleteReportSynthesisFlagshipProgressOutcome(long reportSynthesisFlagshipProgressOutcomeId) {

    reportSynthesisFlagshipProgressOutcomeDAO
      .deleteReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcomeId);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressOutcome(long reportSynthesisFlagshipProgressOutcomeID) {

    return reportSynthesisFlagshipProgressOutcomeDAO
      .existReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcomeID);
  }

  @Override
  public List<ReportSynthesisFlagshipProgressOutcome> findAll() {

    return reportSynthesisFlagshipProgressOutcomeDAO.findAll();

  }

  @Override
  public ReportSynthesisFlagshipProgressOutcome getOutcomeId(long progressID, long outcomeID) {
    return reportSynthesisFlagshipProgressOutcomeDAO.getOutcomeId(progressID, outcomeID);
  }


  @Override
  public ReportSynthesisFlagshipProgressOutcome
    getReportSynthesisFlagshipProgressOutcomeById(long reportSynthesisFlagshipProgressOutcomeID) {

    return reportSynthesisFlagshipProgressOutcomeDAO.find(reportSynthesisFlagshipProgressOutcomeID);
  }

  @Override
  public ReportSynthesisFlagshipProgressOutcome saveReportSynthesisFlagshipProgressOutcome(
    ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome) {

    return reportSynthesisFlagshipProgressOutcomeDAO.save(reportSynthesisFlagshipProgressOutcome);
  }


}
