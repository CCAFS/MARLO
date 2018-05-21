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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrpProgressDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgress;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisCrpProgressManagerImpl implements ReportSynthesisCrpProgressManager {


  private ReportSynthesisCrpProgressDAO reportSynthesisCrpProgressDAO;
  // Managers


  @Inject
  public ReportSynthesisCrpProgressManagerImpl(ReportSynthesisCrpProgressDAO reportSynthesisCrpProgressDAO) {
    this.reportSynthesisCrpProgressDAO = reportSynthesisCrpProgressDAO;


  }

  @Override
  public void deleteReportSynthesisCrpProgress(long reportSynthesisCrpProgressId) {

    reportSynthesisCrpProgressDAO.deleteReportSynthesisCrpProgress(reportSynthesisCrpProgressId);
  }

  @Override
  public boolean existReportSynthesisCrpProgress(long reportSynthesisCrpProgressID) {

    return reportSynthesisCrpProgressDAO.existReportSynthesisCrpProgress(reportSynthesisCrpProgressID);
  }

  @Override
  public List<ReportSynthesisCrpProgress> findAll() {

    return reportSynthesisCrpProgressDAO.findAll();

  }

  @Override
  public ReportSynthesisCrpProgress getReportSynthesisCrpProgressById(long reportSynthesisCrpProgressID) {

    return reportSynthesisCrpProgressDAO.find(reportSynthesisCrpProgressID);
  }

  @Override
  public ReportSynthesisCrpProgress saveReportSynthesisCrpProgress(ReportSynthesisCrpProgress reportSynthesisCrpProgress) {

    return reportSynthesisCrpProgressDAO.save(reportSynthesisCrpProgress);
  }


}
