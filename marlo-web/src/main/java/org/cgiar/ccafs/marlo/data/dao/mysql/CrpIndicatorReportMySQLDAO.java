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

import java.util.List;

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
  public long save(CrpIndicatorReport crpIndicatorReport) {
    if (crpIndicatorReport.getId() == null) {
      dao.save(crpIndicatorReport);
    } else {
      dao.update(crpIndicatorReport);
    }


    return crpIndicatorReport.getId();
  }


}