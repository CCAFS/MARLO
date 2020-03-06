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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisMeliaActionStudyDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaActionStudyManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaActionStudy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisMeliaActionStudyManagerImpl implements ReportSynthesisMeliaActionStudyManager {


  private ReportSynthesisMeliaActionStudyDAO reportSynthesisMeliaActionStudyDAO;
  // Managers


  @Inject
  public ReportSynthesisMeliaActionStudyManagerImpl(ReportSynthesisMeliaActionStudyDAO reportSynthesisMeliaActionStudyDAO) {
    this.reportSynthesisMeliaActionStudyDAO = reportSynthesisMeliaActionStudyDAO;


  }

  @Override
  public void deleteReportSynthesisMeliaActionStudy(long reportSynthesisMeliaActionStudyId) {

    reportSynthesisMeliaActionStudyDAO.deleteReportSynthesisMeliaActionStudy(reportSynthesisMeliaActionStudyId);
  }

  @Override
  public boolean existReportSynthesisMeliaActionStudy(long reportSynthesisMeliaActionStudyID) {

    return reportSynthesisMeliaActionStudyDAO.existReportSynthesisMeliaActionStudy(reportSynthesisMeliaActionStudyID);
  }

  @Override
  public List<ReportSynthesisMeliaActionStudy> findAll() {

    return reportSynthesisMeliaActionStudyDAO.findAll();

  }

  @Override
  public ReportSynthesisMeliaActionStudy getReportSynthesisMeliaActionStudyById(long reportSynthesisMeliaActionStudyID) {

    return reportSynthesisMeliaActionStudyDAO.find(reportSynthesisMeliaActionStudyID);
  }

  @Override
  public ReportSynthesisMeliaActionStudy saveReportSynthesisMeliaActionStudy(ReportSynthesisMeliaActionStudy reportSynthesisMeliaActionStudy) {

    return reportSynthesisMeliaActionStudyDAO.save(reportSynthesisMeliaActionStudy);
  }


}
