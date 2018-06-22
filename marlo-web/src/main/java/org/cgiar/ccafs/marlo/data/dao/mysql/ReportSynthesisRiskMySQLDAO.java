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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisRiskDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisRisk;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisRiskMySQLDAO extends AbstractMarloDAO<ReportSynthesisRisk, Long> implements ReportSynthesisRiskDAO {


  @Inject
  public ReportSynthesisRiskMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisRisk(long reportSynthesisRiskId) {
    ReportSynthesisRisk reportSynthesisRisk = this.find(reportSynthesisRiskId);
    reportSynthesisRisk.setActive(false);
    this.update(reportSynthesisRisk);
  }

  @Override
  public boolean existReportSynthesisRisk(long reportSynthesisRiskID) {
    ReportSynthesisRisk reportSynthesisRisk = this.find(reportSynthesisRiskID);
    if (reportSynthesisRisk == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisRisk find(long id) {
    return super.find(ReportSynthesisRisk.class, id);

  }

  @Override
  public List<ReportSynthesisRisk> findAll() {
    String query = "from " + ReportSynthesisRisk.class.getName() + " where is_active=1";
    List<ReportSynthesisRisk> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisRisk save(ReportSynthesisRisk reportSynthesisRisk) {
    if (reportSynthesisRisk.getId() == null) {
      super.saveEntity(reportSynthesisRisk);
    } else {
      reportSynthesisRisk = super.update(reportSynthesisRisk);
    }


    return reportSynthesisRisk;
  }


}