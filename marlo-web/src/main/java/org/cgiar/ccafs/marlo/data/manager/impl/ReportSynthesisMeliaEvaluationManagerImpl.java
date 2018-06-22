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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisMeliaEvaluationDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisMeliaEvaluationManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisMeliaEvaluationManagerImpl implements ReportSynthesisMeliaEvaluationManager {


  private ReportSynthesisMeliaEvaluationDAO reportSynthesisMeliaEvaluationDAO;
  // Managers


  @Inject
  public ReportSynthesisMeliaEvaluationManagerImpl(ReportSynthesisMeliaEvaluationDAO reportSynthesisMeliaEvaluationDAO) {
    this.reportSynthesisMeliaEvaluationDAO = reportSynthesisMeliaEvaluationDAO;


  }

  @Override
  public void deleteReportSynthesisMeliaEvaluation(long reportSynthesisMeliaEvaluationId) {

    reportSynthesisMeliaEvaluationDAO.deleteReportSynthesisMeliaEvaluation(reportSynthesisMeliaEvaluationId);
  }

  @Override
  public boolean existReportSynthesisMeliaEvaluation(long reportSynthesisMeliaEvaluationID) {

    return reportSynthesisMeliaEvaluationDAO.existReportSynthesisMeliaEvaluation(reportSynthesisMeliaEvaluationID);
  }

  @Override
  public List<ReportSynthesisMeliaEvaluation> findAll() {

    return reportSynthesisMeliaEvaluationDAO.findAll();

  }

  @Override
  public ReportSynthesisMeliaEvaluation getReportSynthesisMeliaEvaluationById(long reportSynthesisMeliaEvaluationID) {

    return reportSynthesisMeliaEvaluationDAO.find(reportSynthesisMeliaEvaluationID);
  }

  @Override
  public ReportSynthesisMeliaEvaluation saveReportSynthesisMeliaEvaluation(ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation) {

    return reportSynthesisMeliaEvaluationDAO.save(reportSynthesisMeliaEvaluation);
  }


}
