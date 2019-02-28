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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisSrfProgressDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgress;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisSrfProgressManagerImpl implements ReportSynthesisSrfProgressManager {


  private ReportSynthesisSrfProgressDAO reportSynthesisSrfProgressDAO;
  // Managers


  @Inject
  public ReportSynthesisSrfProgressManagerImpl(ReportSynthesisSrfProgressDAO reportSynthesisSrfProgressDAO) {
    this.reportSynthesisSrfProgressDAO = reportSynthesisSrfProgressDAO;


  }

  @Override
  public void deleteReportSynthesisSrfProgress(long reportSynthesisSrfProgressId) {

    reportSynthesisSrfProgressDAO.deleteReportSynthesisSrfProgress(reportSynthesisSrfProgressId);
  }

  @Override
  public boolean existReportSynthesisSrfProgress(long reportSynthesisSrfProgressID) {

    return reportSynthesisSrfProgressDAO.existReportSynthesisSrfProgress(reportSynthesisSrfProgressID);
  }

  @Override
  public List<ReportSynthesisSrfProgress> findAll() {

    return reportSynthesisSrfProgressDAO.findAll();

  }

  @Override
  public ReportSynthesisSrfProgress getReportSynthesisSrfProgressById(long reportSynthesisSrfProgressID) {

    return reportSynthesisSrfProgressDAO.find(reportSynthesisSrfProgressID);
  }

  @Override
  public ReportSynthesisSrfProgress saveReportSynthesisSrfProgress(ReportSynthesisSrfProgress reportSynthesisSrfProgress) {

    return reportSynthesisSrfProgressDAO.save(reportSynthesisSrfProgress);
  }


}
