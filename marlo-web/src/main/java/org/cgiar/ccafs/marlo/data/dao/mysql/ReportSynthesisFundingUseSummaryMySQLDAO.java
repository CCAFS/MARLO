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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFundingUseSummaryDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseSummary;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisFundingUseSummaryMySQLDAO extends AbstractMarloDAO<ReportSynthesisFundingUseSummary, Long> implements ReportSynthesisFundingUseSummaryDAO {


  @Inject
  public ReportSynthesisFundingUseSummaryMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisFundingUseSummary(long reportSynthesisFundingUseSummaryId) {
    ReportSynthesisFundingUseSummary reportSynthesisFundingUseSummary = this.find(reportSynthesisFundingUseSummaryId);
    reportSynthesisFundingUseSummary.setActive(false);
    this.update(reportSynthesisFundingUseSummary);
  }

  @Override
  public boolean existReportSynthesisFundingUseSummary(long reportSynthesisFundingUseSummaryID) {
    ReportSynthesisFundingUseSummary reportSynthesisFundingUseSummary = this.find(reportSynthesisFundingUseSummaryID);
    if (reportSynthesisFundingUseSummary == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisFundingUseSummary find(long id) {
    return super.find(ReportSynthesisFundingUseSummary.class, id);

  }

  @Override
  public List<ReportSynthesisFundingUseSummary> findAll() {
    String query = "from " + ReportSynthesisFundingUseSummary.class.getName() + " where is_active=1";
    List<ReportSynthesisFundingUseSummary> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisFundingUseSummary save(ReportSynthesisFundingUseSummary reportSynthesisFundingUseSummary) {
    if (reportSynthesisFundingUseSummary.getId() == null) {
      super.saveEntity(reportSynthesisFundingUseSummary);
    } else {
      reportSynthesisFundingUseSummary = super.update(reportSynthesisFundingUseSummary);
    }


    return reportSynthesisFundingUseSummary;
  }


}