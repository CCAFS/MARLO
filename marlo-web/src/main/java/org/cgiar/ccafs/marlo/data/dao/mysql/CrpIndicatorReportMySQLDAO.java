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

import org.cgiar.ccafs.marlo.data.dao.CrpIndicatorReportDAO;
import org.cgiar.ccafs.marlo.data.model.CrpIndicatorReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

public class CrpIndicatorReportMySQLDAO implements CrpIndicatorReportDAO {

  private StandardDAO dao;

  @Inject
  public CrpIndicatorReportMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCrpIndicatorReport(long crpIndicatorReportId) {
    CrpIndicatorReport crpIndicatorReport = this.find(crpIndicatorReportId);

    return dao.delete(crpIndicatorReport);
  }

  @Override
  public boolean existCrpIndicatorReport(long crpIndicatorReportID) {
    CrpIndicatorReport crpIndicatorReport = this.find(crpIndicatorReportID);
    if (crpIndicatorReport == null) {
      return false;
    }
    return true;

  }

  @Override
  public CrpIndicatorReport find(long id) {
    return dao.find(CrpIndicatorReport.class, id);

  }

  @Override
  public List<CrpIndicatorReport> findAll() {
    String query = "from " + CrpIndicatorReport.class.getName() + " where is_active=1";
    List<CrpIndicatorReport> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CrpIndicatorReport> getIndicatorReportsList(long leader, int year) {

    StringBuilder query = new StringBuilder();
    query.append("SELECT ir.id as id, ir.target, ir.actual, ir.support_links, ");
    query.append("ir.deviation, i.id as 'indicator_id', i.serial as 'indicator_serial', ");
    query.append("i.name as 'indicator_name', i.description as 'indicator_description', ");
    query.append("i.is_active as 'indicator_active', it.id as 'indicator_type_id', ");
    query.append("it.name as 'indicator_type_name', ir.next_target ");

    query.append("FROM `crp_indicators` i ");
    query.append("LEFT JOIN `crp_indicator_reports` ir ON i.id = ir.indicator_id AND ir.year = ");
    query.append(year);
    query.append(" AND ir.liaison_institution_id = ");
    query.append(leader);
    query.append(" LEFT JOIN `crp_indicator_types` it ON i.indicator_type_id = it.id  ");
    query.append(" where i.serial not in ('ind01','ind02','ind03','ind04','ind05','ind06')");
    query.append(" ORDER BY i.id  ");

    List<Map<String, Object>> rList = dao.findCustomQuery(query.toString());

    List<CrpIndicatorReport> indicatorReports = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {

        Long indReportID = Long.parseLong(map.get("id").toString());
        if (indReportID != null) {
          indicatorReports.add(this.find(indReportID));
        }

      }
    }

    return indicatorReports;
  }

  @Override
  public long save(CrpIndicatorReport crpIndicatorReport) {
    if (crpIndicatorReport.getId() == null) {
      dao.save(crpIndicatorReport);
    } else {
      dao.update(crpIndicatorReport);
    }


    return crpIndicatorReport.getId();
  }


}