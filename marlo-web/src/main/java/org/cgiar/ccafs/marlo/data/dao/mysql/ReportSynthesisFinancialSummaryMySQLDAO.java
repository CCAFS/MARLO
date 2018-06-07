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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFinancialSummaryDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummary;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisFinancialSummaryMySQLDAO extends AbstractMarloDAO<ReportSynthesisFinancialSummary, Long> implements ReportSynthesisFinancialSummaryDAO {


  @Inject
  public ReportSynthesisFinancialSummaryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisFinancialSummary(long reportSynthesisFinancialSummaryId) {
    ReportSynthesisFinancialSummary reportSynthesisFinancialSummary = this.find(reportSynthesisFinancialSummaryId);
    reportSynthesisFinancialSummary.setActive(false);
    this.update(reportSynthesisFinancialSummary);
  }

  @Override
  public boolean existReportSynthesisFinancialSummary(long reportSynthesisFinancialSummaryID) {
    ReportSynthesisFinancialSummary reportSynthesisFinancialSummary = this.find(reportSynthesisFinancialSummaryID);
    if (reportSynthesisFinancialSummary == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisFinancialSummary find(long id) {
    return super.find(ReportSynthesisFinancialSummary.class, id);

  }

  @Override
  public List<ReportSynthesisFinancialSummary> findAll() {
    String query = "from " + ReportSynthesisFinancialSummary.class.getName() + " where is_active=1";
    List<ReportSynthesisFinancialSummary> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisFinancialSummary save(ReportSynthesisFinancialSummary reportSynthesisFinancialSummary) {
    if (reportSynthesisFinancialSummary.getId() == null) {
      super.saveEntity(reportSynthesisFinancialSummary);
    } else {
      reportSynthesisFinancialSummary = super.update(reportSynthesisFinancialSummary);
    }


    return reportSynthesisFinancialSummary;
  }


}