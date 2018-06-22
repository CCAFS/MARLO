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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisMeliaStudyDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaStudyManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaStudy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisMeliaStudyManagerImpl implements ReportSynthesisMeliaStudyManager {


  private ReportSynthesisMeliaStudyDAO reportSynthesisMeliaStudyDAO;
  // Managers


  @Inject
  public ReportSynthesisMeliaStudyManagerImpl(ReportSynthesisMeliaStudyDAO reportSynthesisMeliaStudyDAO) {
    this.reportSynthesisMeliaStudyDAO = reportSynthesisMeliaStudyDAO;


  }

  @Override
  public void deleteReportSynthesisMeliaStudy(long reportSynthesisMeliaStudyId) {

    reportSynthesisMeliaStudyDAO.deleteReportSynthesisMeliaStudy(reportSynthesisMeliaStudyId);
  }

  @Override
  public boolean existReportSynthesisMeliaStudy(long reportSynthesisMeliaStudyID) {

    return reportSynthesisMeliaStudyDAO.existReportSynthesisMeliaStudy(reportSynthesisMeliaStudyID);
  }

  @Override
  public List<ReportSynthesisMeliaStudy> findAll() {

    return reportSynthesisMeliaStudyDAO.findAll();

  }

  @Override
  public ReportSynthesisMeliaStudy getReportSynthesisMeliaStudyById(long reportSynthesisMeliaStudyID) {

    return reportSynthesisMeliaStudyDAO.find(reportSynthesisMeliaStudyID);
  }

  @Override
  public ReportSynthesisMeliaStudy saveReportSynthesisMeliaStudy(ReportSynthesisMeliaStudy reportSynthesisMeliaStudy) {

    return reportSynthesisMeliaStudyDAO.save(reportSynthesisMeliaStudy);
  }


}
