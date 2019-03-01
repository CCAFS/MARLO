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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisExpenditureCategoryDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisExpenditureCategoryManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExpenditureCategory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisExpenditureCategoryManagerImpl implements ReportSynthesisExpenditureCategoryManager {


  private ReportSynthesisExpenditureCategoryDAO reportSynthesisExpenditureCategoryDAO;
  // Managers


  @Inject
  public ReportSynthesisExpenditureCategoryManagerImpl(ReportSynthesisExpenditureCategoryDAO reportSynthesisExpenditureCategoryDAO) {
    this.reportSynthesisExpenditureCategoryDAO = reportSynthesisExpenditureCategoryDAO;


  }

  @Override
  public void deleteReportSynthesisExpenditureCategory(long reportSynthesisExpenditureCategoryId) {

    reportSynthesisExpenditureCategoryDAO.deleteReportSynthesisExpenditureCategory(reportSynthesisExpenditureCategoryId);
  }

  @Override
  public boolean existReportSynthesisExpenditureCategory(long reportSynthesisExpenditureCategoryID) {

    return reportSynthesisExpenditureCategoryDAO.existReportSynthesisExpenditureCategory(reportSynthesisExpenditureCategoryID);
  }

  @Override
  public List<ReportSynthesisExpenditureCategory> findAll() {

    return reportSynthesisExpenditureCategoryDAO.findAll();

  }

  @Override
  public ReportSynthesisExpenditureCategory getReportSynthesisExpenditureCategoryById(long reportSynthesisExpenditureCategoryID) {

    return reportSynthesisExpenditureCategoryDAO.find(reportSynthesisExpenditureCategoryID);
  }

  @Override
  public ReportSynthesisExpenditureCategory saveReportSynthesisExpenditureCategory(ReportSynthesisExpenditureCategory reportSynthesisExpenditureCategory) {

    return reportSynthesisExpenditureCategoryDAO.save(reportSynthesisExpenditureCategory);
  }


}
