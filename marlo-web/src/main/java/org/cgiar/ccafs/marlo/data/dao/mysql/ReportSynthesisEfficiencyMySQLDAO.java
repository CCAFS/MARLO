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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisEfficiencyDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisEfficiency;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisEfficiencyMySQLDAO extends AbstractMarloDAO<ReportSynthesisEfficiency, Long> implements ReportSynthesisEfficiencyDAO {


  @Inject
  public ReportSynthesisEfficiencyMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisEfficiency(long reportSynthesisEfficiencyId) {
    ReportSynthesisEfficiency reportSynthesisEfficiency = this.find(reportSynthesisEfficiencyId);
    reportSynthesisEfficiency.setActive(false);
    this.update(reportSynthesisEfficiency);
  }

  @Override
  public boolean existReportSynthesisEfficiency(long reportSynthesisEfficiencyID) {
    ReportSynthesisEfficiency reportSynthesisEfficiency = this.find(reportSynthesisEfficiencyID);
    if (reportSynthesisEfficiency == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisEfficiency find(long id) {
    return super.find(ReportSynthesisEfficiency.class, id);

  }

  @Override
  public List<ReportSynthesisEfficiency> findAll() {
    String query = "from " + ReportSynthesisEfficiency.class.getName() + " where is_active=1";
    List<ReportSynthesisEfficiency> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisEfficiency save(ReportSynthesisEfficiency reportSynthesisEfficiency) {
    if (reportSynthesisEfficiency.getId() == null) {
      super.saveEntity(reportSynthesisEfficiency);
    } else {
      reportSynthesisEfficiency = super.update(reportSynthesisEfficiency);
    }


    return reportSynthesisEfficiency;
  }


}