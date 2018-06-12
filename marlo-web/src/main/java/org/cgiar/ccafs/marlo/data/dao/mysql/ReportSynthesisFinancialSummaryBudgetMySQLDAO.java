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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFinancialSummaryBudgetDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummaryBudget;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisFinancialSummaryBudgetMySQLDAO extends
  AbstractMarloDAO<ReportSynthesisFinancialSummaryBudget, Long> implements ReportSynthesisFinancialSummaryBudgetDAO {


  @Inject
  public ReportSynthesisFinancialSummaryBudgetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisFinancialSummaryBudget(long reportSynthesisFinancialSummaryBudgetId) {
    ReportSynthesisFinancialSummaryBudget reportSynthesisFinancialSummaryBudget =
      this.find(reportSynthesisFinancialSummaryBudgetId);
    reportSynthesisFinancialSummaryBudget.setActive(false);
    this.update(reportSynthesisFinancialSummaryBudget);
  }

  @Override
  public boolean existReportSynthesisFinancialSummaryBudget(long reportSynthesisFinancialSummaryBudgetID) {
    ReportSynthesisFinancialSummaryBudget reportSynthesisFinancialSummaryBudget =
      this.find(reportSynthesisFinancialSummaryBudgetID);
    if (reportSynthesisFinancialSummaryBudget == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisFinancialSummaryBudget find(long id) {
    return super.find(ReportSynthesisFinancialSummaryBudget.class, id);

  }

  @Override
  public List<ReportSynthesisFinancialSummaryBudget> findAll() {
    String query = "from " + ReportSynthesisFinancialSummaryBudget.class.getName() + " where is_active=1";
    List<ReportSynthesisFinancialSummaryBudget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public double getTotalW1W2ActualExpenditure(long reportSynthesisId) {
    String query =
      "SELECT SUM(w1_actual) AS amount FROM report_synthesis_financial_summary_budgets b INNER JOIN report_synthesis_financial_summaries fs ON fs.id = "
        + reportSynthesisId + " AND fs.is_active WHERE b.report_synthesis_financial_summary_id = " + reportSynthesisId
        + " AND b.is_active";

    List<Map<String, Object>> list = super.findCustomQuery(query);
    if (list.size() > 0) {
      Map<String, Object> result = list.get(0);
      if (result.get("amount") != null) {
        return Double.valueOf(result.get("amount").toString());
      }
    }
    return 0;
  }

  @Override
  public ReportSynthesisFinancialSummaryBudget
    save(ReportSynthesisFinancialSummaryBudget reportSynthesisFinancialSummaryBudget) {
    if (reportSynthesisFinancialSummaryBudget.getId() == null) {
      super.saveEntity(reportSynthesisFinancialSummaryBudget);
    } else {
      reportSynthesisFinancialSummaryBudget = super.update(reportSynthesisFinancialSummaryBudget);
    }


    return reportSynthesisFinancialSummaryBudget;
  }

}