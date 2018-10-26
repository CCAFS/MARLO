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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIndicator;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisIndicatorMySQLDAO extends AbstractMarloDAO<ReportSynthesisIndicator, Long> implements ReportSynthesisIndicatorDAO {


  @Inject
  public ReportSynthesisIndicatorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisIndicator(long reportSynthesisIndicatorId) {
    ReportSynthesisIndicator reportSynthesisIndicator = this.find(reportSynthesisIndicatorId);
    reportSynthesisIndicator.setActive(false);
    this.update(reportSynthesisIndicator);
  }

  @Override
  public boolean existReportSynthesisIndicator(long reportSynthesisIndicatorID) {
    ReportSynthesisIndicator reportSynthesisIndicator = this.find(reportSynthesisIndicatorID);
    if (reportSynthesisIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisIndicator find(long id) {
    return super.find(ReportSynthesisIndicator.class, id);

  }

  @Override
  public List<ReportSynthesisIndicator> findAll() {
    String query = "from " + ReportSynthesisIndicator.class.getName() + " where is_active=1";
    List<ReportSynthesisIndicator> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisIndicator save(ReportSynthesisIndicator reportSynthesisIndicator) {
    if (reportSynthesisIndicator.getId() == null) {
      super.saveEntity(reportSynthesisIndicator);
    } else {
      reportSynthesisIndicator = super.update(reportSynthesisIndicator);
    }


    return reportSynthesisIndicator;
  }


}