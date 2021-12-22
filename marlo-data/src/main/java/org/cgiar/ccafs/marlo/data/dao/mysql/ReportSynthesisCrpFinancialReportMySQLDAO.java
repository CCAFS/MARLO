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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrpFinancialReportDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpFinancialReport;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisCrpFinancialReportMySQLDAO extends AbstractMarloDAO<ReportSynthesisCrpFinancialReport, Long>
  implements ReportSynthesisCrpFinancialReportDAO {


  @Inject
  public ReportSynthesisCrpFinancialReportMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisCrpFinancialReport(long reportSynthesisCrpFinancialReportId) {
    ReportSynthesisCrpFinancialReport reportSynthesisCrpFinancialReport =
      this.find(reportSynthesisCrpFinancialReportId);
    reportSynthesisCrpFinancialReport.setActive(false);
    this.update(reportSynthesisCrpFinancialReport);
  }

  @Override
  public boolean existReportSynthesisCrpFinancialReport(long reportSynthesisCrpFinancialReportID) {
    ReportSynthesisCrpFinancialReport reportSynthesisCrpFinancialReport =
      this.find(reportSynthesisCrpFinancialReportID);
    if (reportSynthesisCrpFinancialReport == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisCrpFinancialReport find(long id) {
    return super.find(ReportSynthesisCrpFinancialReport.class, id);

  }

  @Override
  public List<ReportSynthesisCrpFinancialReport> findAll() {
    String query = "from " + ReportSynthesisCrpFinancialReport.class.getName() + " where is_active=1";
    List<ReportSynthesisCrpFinancialReport> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisCrpFinancialReport findByReportSynthesis(long reportSynthesisId) {
    String query =
      "from " + ReportSynthesisCrpFinancialReport.class.getName() + " where report_synthesis_id=" + reportSynthesisId;
    List<ReportSynthesisCrpFinancialReport> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }

    return null;
  }

  @Override
  public ReportSynthesisCrpFinancialReport save(ReportSynthesisCrpFinancialReport reportSynthesisCrpFinancialReport) {
    if (reportSynthesisCrpFinancialReport.getId() == null) {
      super.saveEntity(reportSynthesisCrpFinancialReport);
    } else {
      reportSynthesisCrpFinancialReport = super.update(reportSynthesisCrpFinancialReport);
    }


    return reportSynthesisCrpFinancialReport;
  }

}