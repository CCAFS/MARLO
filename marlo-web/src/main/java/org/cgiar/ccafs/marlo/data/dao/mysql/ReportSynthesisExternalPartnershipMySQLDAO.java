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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisExternalPartnershipDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnership;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisExternalPartnershipMySQLDAO extends AbstractMarloDAO<ReportSynthesisExternalPartnership, Long> implements ReportSynthesisExternalPartnershipDAO {


  @Inject
  public ReportSynthesisExternalPartnershipMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisExternalPartnership(long reportSynthesisExternalPartnershipId) {
    ReportSynthesisExternalPartnership reportSynthesisExternalPartnership = this.find(reportSynthesisExternalPartnershipId);
    reportSynthesisExternalPartnership.setActive(false);
    this.update(reportSynthesisExternalPartnership);
  }

  @Override
  public boolean existReportSynthesisExternalPartnership(long reportSynthesisExternalPartnershipID) {
    ReportSynthesisExternalPartnership reportSynthesisExternalPartnership = this.find(reportSynthesisExternalPartnershipID);
    if (reportSynthesisExternalPartnership == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisExternalPartnership find(long id) {
    return super.find(ReportSynthesisExternalPartnership.class, id);

  }

  @Override
  public List<ReportSynthesisExternalPartnership> findAll() {
    String query = "from " + ReportSynthesisExternalPartnership.class.getName() + " where is_active=1";
    List<ReportSynthesisExternalPartnership> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisExternalPartnership save(ReportSynthesisExternalPartnership reportSynthesisExternalPartnership) {
    if (reportSynthesisExternalPartnership.getId() == null) {
      super.saveEntity(reportSynthesisExternalPartnership);
    } else {
      reportSynthesisExternalPartnership = super.update(reportSynthesisExternalPartnership);
    }


    return reportSynthesisExternalPartnership;
  }


}