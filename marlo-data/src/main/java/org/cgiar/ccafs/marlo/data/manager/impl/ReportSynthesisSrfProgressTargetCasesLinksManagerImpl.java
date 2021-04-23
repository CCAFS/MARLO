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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisSrfProgressTargetCasesLinksDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetCasesLinksManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetCasesLinks;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisSrfProgressTargetCasesLinksManagerImpl implements ReportSynthesisSrfProgressTargetCasesLinksManager {


  private ReportSynthesisSrfProgressTargetCasesLinksDAO reportSynthesisSrfProgressTargetCasesLinksDAO;
  // Managers


  @Inject
  public ReportSynthesisSrfProgressTargetCasesLinksManagerImpl(ReportSynthesisSrfProgressTargetCasesLinksDAO reportSynthesisSrfProgressTargetCasesLinksDAO) {
    this.reportSynthesisSrfProgressTargetCasesLinksDAO = reportSynthesisSrfProgressTargetCasesLinksDAO;


  }

  @Override
  public void deleteReportSynthesisSrfProgressTargetCasesLinks(long reportSynthesisSrfProgressTargetCasesLinksId) {

    reportSynthesisSrfProgressTargetCasesLinksDAO.deleteReportSynthesisSrfProgressTargetCasesLinks(reportSynthesisSrfProgressTargetCasesLinksId);
  }

  @Override
  public boolean existReportSynthesisSrfProgressTargetCasesLinks(long reportSynthesisSrfProgressTargetCasesLinksID) {

    return reportSynthesisSrfProgressTargetCasesLinksDAO.existReportSynthesisSrfProgressTargetCasesLinks(reportSynthesisSrfProgressTargetCasesLinksID);
  }

  @Override
  public List<ReportSynthesisSrfProgressTargetCasesLinks> findAll() {

    return reportSynthesisSrfProgressTargetCasesLinksDAO.findAll();

  }

  @Override
  public ReportSynthesisSrfProgressTargetCasesLinks getReportSynthesisSrfProgressTargetCasesLinksById(long reportSynthesisSrfProgressTargetCasesLinksID) {

    return reportSynthesisSrfProgressTargetCasesLinksDAO.find(reportSynthesisSrfProgressTargetCasesLinksID);
  }

  @Override
  public ReportSynthesisSrfProgressTargetCasesLinks saveReportSynthesisSrfProgressTargetCasesLinks(ReportSynthesisSrfProgressTargetCasesLinks reportSynthesisSrfProgressTargetCasesLinks) {

    return reportSynthesisSrfProgressTargetCasesLinksDAO.save(reportSynthesisSrfProgressTargetCasesLinks);
  }


}
