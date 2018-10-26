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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisIndicatorGeneralDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIndicatorGeneral;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisIndicatorGeneralMySQLDAO extends AbstractMarloDAO<ReportSynthesisIndicatorGeneral, Long> implements ReportSynthesisIndicatorGeneralDAO {


  @Inject
  public ReportSynthesisIndicatorGeneralMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisIndicatorGeneral(long reportSynthesisIndicatorGeneralId) {
    ReportSynthesisIndicatorGeneral reportSynthesisIndicatorGeneral = this.find(reportSynthesisIndicatorGeneralId);
    reportSynthesisIndicatorGeneral.setActive(false);
    this.update(reportSynthesisIndicatorGeneral);
  }

  @Override
  public boolean existReportSynthesisIndicatorGeneral(long reportSynthesisIndicatorGeneralID) {
    ReportSynthesisIndicatorGeneral reportSynthesisIndicatorGeneral = this.find(reportSynthesisIndicatorGeneralID);
    if (reportSynthesisIndicatorGeneral == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisIndicatorGeneral find(long id) {
    return super.find(ReportSynthesisIndicatorGeneral.class, id);

  }

  @Override
  public List<ReportSynthesisIndicatorGeneral> findAll() {
    String query = "from " + ReportSynthesisIndicatorGeneral.class.getName() + " where is_active=1";
    List<ReportSynthesisIndicatorGeneral> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisIndicatorGeneral save(ReportSynthesisIndicatorGeneral reportSynthesisIndicatorGeneral) {
    if (reportSynthesisIndicatorGeneral.getId() == null) {
      super.saveEntity(reportSynthesisIndicatorGeneral);
    } else {
      reportSynthesisIndicatorGeneral = super.update(reportSynthesisIndicatorGeneral);
    }


    return reportSynthesisIndicatorGeneral;
  }


}