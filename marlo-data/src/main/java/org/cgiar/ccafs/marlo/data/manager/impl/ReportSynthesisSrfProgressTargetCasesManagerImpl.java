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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisSrfProgressTargetCasesDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetCasesManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetCases;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisSrfProgressTargetCasesManagerImpl implements ReportSynthesisSrfProgressTargetCasesManager {


  private ReportSynthesisSrfProgressTargetCasesDAO reportSynthesisSrfProgressTargetCasesDAO;
  // Managers


  @Inject
  public ReportSynthesisSrfProgressTargetCasesManagerImpl(ReportSynthesisSrfProgressTargetCasesDAO reportSynthesisSrfProgressTargetCasesDAO) {
    this.reportSynthesisSrfProgressTargetCasesDAO = reportSynthesisSrfProgressTargetCasesDAO;


  }

  @Override
  public void deleteReportSynthesisSrfProgressTargetCases(long reportSynthesisSrfProgressTargetCasesId) {

    reportSynthesisSrfProgressTargetCasesDAO.deleteReportSynthesisSrfProgressTargetCases(reportSynthesisSrfProgressTargetCasesId);
  }

  @Override
  public boolean existReportSynthesisSrfProgressTargetCases(long reportSynthesisSrfProgressTargetCasesID) {

    return reportSynthesisSrfProgressTargetCasesDAO.existReportSynthesisSrfProgressTargetCases(reportSynthesisSrfProgressTargetCasesID);
  }

  @Override
  public List<ReportSynthesisSrfProgressTargetCases> findAll() {

    return reportSynthesisSrfProgressTargetCasesDAO.findAll();

  }

  @Override
  public ReportSynthesisSrfProgressTargetCases getReportSynthesisSrfProgressTargetCasesById(long reportSynthesisSrfProgressTargetCasesID) {

    return reportSynthesisSrfProgressTargetCasesDAO.find(reportSynthesisSrfProgressTargetCasesID);
  }

  @Override
  public ReportSynthesisSrfProgressTargetCases saveReportSynthesisSrfProgressTargetCases(ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTargetCases) {

    return reportSynthesisSrfProgressTargetCasesDAO.save(reportSynthesisSrfProgressTargetCases);
  }


}
