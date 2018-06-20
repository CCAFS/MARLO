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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisMeliaEvaluationDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisMeliaEvaluationMySQLDAO extends AbstractMarloDAO<ReportSynthesisMeliaEvaluation, Long> implements ReportSynthesisMeliaEvaluationDAO {


  @Inject
  public ReportSynthesisMeliaEvaluationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisMeliaEvaluation(long reportSynthesisMeliaEvaluationId) {
    ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation = this.find(reportSynthesisMeliaEvaluationId);
    reportSynthesisMeliaEvaluation.setActive(false);
    this.update(reportSynthesisMeliaEvaluation);
  }

  @Override
  public boolean existReportSynthesisMeliaEvaluation(long reportSynthesisMeliaEvaluationID) {
    ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation = this.find(reportSynthesisMeliaEvaluationID);
    if (reportSynthesisMeliaEvaluation == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisMeliaEvaluation find(long id) {
    return super.find(ReportSynthesisMeliaEvaluation.class, id);

  }

  @Override
  public List<ReportSynthesisMeliaEvaluation> findAll() {
    String query = "from " + ReportSynthesisMeliaEvaluation.class.getName() + " where is_active=1";
    List<ReportSynthesisMeliaEvaluation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisMeliaEvaluation save(ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation) {
    if (reportSynthesisMeliaEvaluation.getId() == null) {
      super.saveEntity(reportSynthesisMeliaEvaluation);
    } else {
      reportSynthesisMeliaEvaluation = super.update(reportSynthesisMeliaEvaluation);
    }


    return reportSynthesisMeliaEvaluation;
  }


}