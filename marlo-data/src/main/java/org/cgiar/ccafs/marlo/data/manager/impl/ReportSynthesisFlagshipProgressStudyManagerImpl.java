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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressStudyDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressStudyManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFlagshipProgressStudyManagerImpl implements ReportSynthesisFlagshipProgressStudyManager {


  private ReportSynthesisFlagshipProgressStudyDAO reportSynthesisFlagshipProgressStudyDAO;
  // Managers


  @Inject
  public ReportSynthesisFlagshipProgressStudyManagerImpl(ReportSynthesisFlagshipProgressStudyDAO reportSynthesisFlagshipProgressStudyDAO) {
    this.reportSynthesisFlagshipProgressStudyDAO = reportSynthesisFlagshipProgressStudyDAO;


  }

  @Override
  public void deleteReportSynthesisFlagshipProgressStudy(long reportSynthesisFlagshipProgressStudyId) {

    reportSynthesisFlagshipProgressStudyDAO.deleteReportSynthesisFlagshipProgressStudy(reportSynthesisFlagshipProgressStudyId);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressStudy(long reportSynthesisFlagshipProgressStudyID) {

    return reportSynthesisFlagshipProgressStudyDAO.existReportSynthesisFlagshipProgressStudy(reportSynthesisFlagshipProgressStudyID);
  }

  @Override
  public List<ReportSynthesisFlagshipProgressStudy> findAll() {

    return reportSynthesisFlagshipProgressStudyDAO.findAll();

  }

  @Override
  public ReportSynthesisFlagshipProgressStudy getReportSynthesisFlagshipProgressStudyById(long reportSynthesisFlagshipProgressStudyID) {

    return reportSynthesisFlagshipProgressStudyDAO.find(reportSynthesisFlagshipProgressStudyID);
  }

  @Override
  public ReportSynthesisFlagshipProgressStudy saveReportSynthesisFlagshipProgressStudy(ReportSynthesisFlagshipProgressStudy reportSynthesisFlagshipProgressStudy) {

    return reportSynthesisFlagshipProgressStudyDAO.save(reportSynthesisFlagshipProgressStudy);
  }


}
