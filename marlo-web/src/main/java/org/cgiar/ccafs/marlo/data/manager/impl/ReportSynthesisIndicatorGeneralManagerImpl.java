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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisIndicatorGeneralDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisIndicatorGeneralManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIndicatorGeneral;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisIndicatorGeneralManagerImpl implements ReportSynthesisIndicatorGeneralManager {


  private ReportSynthesisIndicatorGeneralDAO reportSynthesisIndicatorGeneralDAO;
  // Managers


  @Inject
  public ReportSynthesisIndicatorGeneralManagerImpl(ReportSynthesisIndicatorGeneralDAO reportSynthesisIndicatorGeneralDAO) {
    this.reportSynthesisIndicatorGeneralDAO = reportSynthesisIndicatorGeneralDAO;


  }

  @Override
  public void deleteReportSynthesisIndicatorGeneral(long reportSynthesisIndicatorGeneralId) {

    reportSynthesisIndicatorGeneralDAO.deleteReportSynthesisIndicatorGeneral(reportSynthesisIndicatorGeneralId);
  }

  @Override
  public boolean existReportSynthesisIndicatorGeneral(long reportSynthesisIndicatorGeneralID) {

    return reportSynthesisIndicatorGeneralDAO.existReportSynthesisIndicatorGeneral(reportSynthesisIndicatorGeneralID);
  }

  @Override
  public List<ReportSynthesisIndicatorGeneral> findAll() {

    return reportSynthesisIndicatorGeneralDAO.findAll();

  }

  @Override
  public ReportSynthesisIndicatorGeneral getReportSynthesisIndicatorGeneralById(long reportSynthesisIndicatorGeneralID) {

    return reportSynthesisIndicatorGeneralDAO.find(reportSynthesisIndicatorGeneralID);
  }

  @Override
  public ReportSynthesisIndicatorGeneral saveReportSynthesisIndicatorGeneral(ReportSynthesisIndicatorGeneral reportSynthesisIndicatorGeneral) {

    return reportSynthesisIndicatorGeneralDAO.save(reportSynthesisIndicatorGeneral);
  }


}
