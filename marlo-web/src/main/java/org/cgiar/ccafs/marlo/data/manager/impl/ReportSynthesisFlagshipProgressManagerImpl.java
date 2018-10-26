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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFlagshipProgressManagerImpl implements ReportSynthesisFlagshipProgressManager {


  private ReportSynthesisFlagshipProgressDAO reportSynthesisFlagshipProgressDAO;
  // Managers


  @Inject
  public ReportSynthesisFlagshipProgressManagerImpl(ReportSynthesisFlagshipProgressDAO reportSynthesisFlagshipProgressDAO) {
    this.reportSynthesisFlagshipProgressDAO = reportSynthesisFlagshipProgressDAO;


  }

  @Override
  public void deleteReportSynthesisFlagshipProgress(long reportSynthesisFlagshipProgressId) {

    reportSynthesisFlagshipProgressDAO.deleteReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressId);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgress(long reportSynthesisFlagshipProgressID) {

    return reportSynthesisFlagshipProgressDAO.existReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressID);
  }

  @Override
  public List<ReportSynthesisFlagshipProgress> findAll() {

    return reportSynthesisFlagshipProgressDAO.findAll();

  }

  @Override
  public ReportSynthesisFlagshipProgress getReportSynthesisFlagshipProgressById(long reportSynthesisFlagshipProgressID) {

    return reportSynthesisFlagshipProgressDAO.find(reportSynthesisFlagshipProgressID);
  }

  @Override
  public ReportSynthesisFlagshipProgress saveReportSynthesisFlagshipProgress(ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress) {

    return reportSynthesisFlagshipProgressDAO.save(reportSynthesisFlagshipProgress);
  }


}
