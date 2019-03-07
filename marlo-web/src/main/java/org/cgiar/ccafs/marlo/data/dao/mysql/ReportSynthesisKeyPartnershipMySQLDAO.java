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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnership;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisKeyPartnershipMySQLDAO extends AbstractMarloDAO<ReportSynthesisKeyPartnership, Long> implements ReportSynthesisKeyPartnershipDAO {


  @Inject
  public ReportSynthesisKeyPartnershipMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisKeyPartnership(long reportSynthesisKeyPartnershipId) {
    ReportSynthesisKeyPartnership reportSynthesisKeyPartnership = this.find(reportSynthesisKeyPartnershipId);
    reportSynthesisKeyPartnership.setActive(false);
    this.update(reportSynthesisKeyPartnership);
  }

  @Override
  public boolean existReportSynthesisKeyPartnership(long reportSynthesisKeyPartnershipID) {
    ReportSynthesisKeyPartnership reportSynthesisKeyPartnership = this.find(reportSynthesisKeyPartnershipID);
    if (reportSynthesisKeyPartnership == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisKeyPartnership find(long id) {
    return super.find(ReportSynthesisKeyPartnership.class, id);

  }

  @Override
  public List<ReportSynthesisKeyPartnership> findAll() {
    String query = "from " + ReportSynthesisKeyPartnership.class.getName() + " where is_active=1";
    List<ReportSynthesisKeyPartnership> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisKeyPartnership save(ReportSynthesisKeyPartnership reportSynthesisKeyPartnership) {
    if (reportSynthesisKeyPartnership.getId() == null) {
      super.saveEntity(reportSynthesisKeyPartnership);
    } else {
      reportSynthesisKeyPartnership = super.update(reportSynthesisKeyPartnership);
    }


    return reportSynthesisKeyPartnership;
  }


}