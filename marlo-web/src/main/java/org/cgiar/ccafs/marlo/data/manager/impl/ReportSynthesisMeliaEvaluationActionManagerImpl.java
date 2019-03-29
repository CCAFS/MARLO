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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisMeliaEvaluationActionDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaEvaluationActionManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluationAction;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisMeliaEvaluationActionManagerImpl implements ReportSynthesisMeliaEvaluationActionManager {


  private ReportSynthesisMeliaEvaluationActionDAO reportSynthesisMeliaEvaluationActionDAO;
  // Managers


  @Inject
  public ReportSynthesisMeliaEvaluationActionManagerImpl(ReportSynthesisMeliaEvaluationActionDAO reportSynthesisMeliaEvaluationActionDAO) {
    this.reportSynthesisMeliaEvaluationActionDAO = reportSynthesisMeliaEvaluationActionDAO;


  }

  @Override
  public void deleteReportSynthesisMeliaEvaluationAction(long reportSynthesisMeliaEvaluationActionId) {

    reportSynthesisMeliaEvaluationActionDAO.deleteReportSynthesisMeliaEvaluationAction(reportSynthesisMeliaEvaluationActionId);
  }

  @Override
  public boolean existReportSynthesisMeliaEvaluationAction(long reportSynthesisMeliaEvaluationActionID) {

    return reportSynthesisMeliaEvaluationActionDAO.existReportSynthesisMeliaEvaluationAction(reportSynthesisMeliaEvaluationActionID);
  }

  @Override
  public List<ReportSynthesisMeliaEvaluationAction> findAll() {

    return reportSynthesisMeliaEvaluationActionDAO.findAll();

  }

  @Override
  public ReportSynthesisMeliaEvaluationAction getReportSynthesisMeliaEvaluationActionById(long reportSynthesisMeliaEvaluationActionID) {

    return reportSynthesisMeliaEvaluationActionDAO.find(reportSynthesisMeliaEvaluationActionID);
  }

  @Override
  public ReportSynthesisMeliaEvaluationAction saveReportSynthesisMeliaEvaluationAction(ReportSynthesisMeliaEvaluationAction reportSynthesisMeliaEvaluationAction) {

    return reportSynthesisMeliaEvaluationActionDAO.save(reportSynthesisMeliaEvaluationAction);
  }


}
