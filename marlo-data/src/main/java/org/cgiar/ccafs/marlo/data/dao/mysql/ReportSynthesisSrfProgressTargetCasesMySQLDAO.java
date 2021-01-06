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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisSrfProgressTargetCasesDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetCases;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisSrfProgressTargetCasesMySQLDAO extends
  AbstractMarloDAO<ReportSynthesisSrfProgressTargetCases, Long> implements ReportSynthesisSrfProgressTargetCasesDAO {


  @Inject
  public ReportSynthesisSrfProgressTargetCasesMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisSrfProgressTargetCases(long reportSynthesisSrfProgressTargetCasesId) {
    ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTargetCases =
      this.find(reportSynthesisSrfProgressTargetCasesId);
    reportSynthesisSrfProgressTargetCases.setActive(false);
    this.update(reportSynthesisSrfProgressTargetCases);
  }

  @Override
  public boolean existReportSynthesisSrfProgressTargetCases(long reportSynthesisSrfProgressTargetCasesID) {
    ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTargetCases =
      this.find(reportSynthesisSrfProgressTargetCasesID);
    if (reportSynthesisSrfProgressTargetCases == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisSrfProgressTargetCases find(long id) {
    return super.find(ReportSynthesisSrfProgressTargetCases.class, id);

  }

  @Override
  public List<ReportSynthesisSrfProgressTargetCases> findAll() {
    String query = "from " + ReportSynthesisSrfProgressTargetCases.class.getName() + " where is_active=1";
    List<ReportSynthesisSrfProgressTargetCases> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ReportSynthesisSrfProgressTargetCases> getReportSynthesisSrfProgressId(long synthesisID,
    long srfTargetID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT id as targetId FROM report_synthesis_srf_progress_targets_cases");
    query.append(" WHERE report_synthesis_srf_progress_id = ");
    query.append(synthesisID);
    query.append(" AND srf_slo_indicator_targets_id = ");
    query.append(srfTargetID);

    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<ReportSynthesisSrfProgressTargetCases> reportSynthesisSrfProgressTargetsCases =
      new ArrayList<ReportSynthesisSrfProgressTargetCases>();
    for (Map<String, Object> map : list) {
      String targetId = map.get("targetId").toString();
      long longTargetId = Long.parseLong(targetId);
      ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTargetCases = this.find(longTargetId);
      reportSynthesisSrfProgressTargetsCases.add(reportSynthesisSrfProgressTargetCases);
    }
    if (reportSynthesisSrfProgressTargetsCases.size() > 0) {
      return reportSynthesisSrfProgressTargetsCases;
    } else {
      return null;
    }

  }

  @Override
  public ReportSynthesisSrfProgressTargetCases
    save(ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTargetCases) {
    if (reportSynthesisSrfProgressTargetCases.getId() == null) {
      super.saveEntity(reportSynthesisSrfProgressTargetCases);
    } else {
      reportSynthesisSrfProgressTargetCases = super.update(reportSynthesisSrfProgressTargetCases);
    }


    return reportSynthesisSrfProgressTargetCases;
  }


}