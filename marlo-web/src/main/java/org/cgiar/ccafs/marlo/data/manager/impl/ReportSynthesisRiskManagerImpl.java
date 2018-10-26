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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisRiskDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisRiskManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisRisk;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisRiskManagerImpl implements ReportSynthesisRiskManager {


  private ReportSynthesisRiskDAO reportSynthesisRiskDAO;
  // Managers


  @Inject
  public ReportSynthesisRiskManagerImpl(ReportSynthesisRiskDAO reportSynthesisRiskDAO) {
    this.reportSynthesisRiskDAO = reportSynthesisRiskDAO;


  }

  @Override
  public void deleteReportSynthesisRisk(long reportSynthesisRiskId) {

    reportSynthesisRiskDAO.deleteReportSynthesisRisk(reportSynthesisRiskId);
  }

  @Override
  public boolean existReportSynthesisRisk(long reportSynthesisRiskID) {

    return reportSynthesisRiskDAO.existReportSynthesisRisk(reportSynthesisRiskID);
  }

  @Override
  public List<ReportSynthesisRisk> findAll() {

    return reportSynthesisRiskDAO.findAll();

  }

  @Override
  public ReportSynthesisRisk getReportSynthesisRiskById(long reportSynthesisRiskID) {

    return reportSynthesisRiskDAO.find(reportSynthesisRiskID);
  }

  @Override
  public ReportSynthesisRisk saveReportSynthesisRisk(ReportSynthesisRisk reportSynthesisRisk) {

    return reportSynthesisRiskDAO.save(reportSynthesisRisk);
  }


}
