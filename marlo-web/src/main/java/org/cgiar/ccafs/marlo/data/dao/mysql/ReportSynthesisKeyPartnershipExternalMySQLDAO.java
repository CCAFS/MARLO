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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipExternalDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisKeyPartnershipExternalMySQLDAO extends
  AbstractMarloDAO<ReportSynthesisKeyPartnershipExternal, Long> implements ReportSynthesisKeyPartnershipExternalDAO {


  @Inject
  public ReportSynthesisKeyPartnershipExternalMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisKeyPartnershipExternal(long reportSynthesisKeyPartnershipExternalId) {
    ReportSynthesisKeyPartnershipExternal reportSynthesisKeyPartnershipExternal =
      this.find(reportSynthesisKeyPartnershipExternalId);
    this.delete(reportSynthesisKeyPartnershipExternal);
  }

  @Override
  public boolean existReportSynthesisKeyPartnershipExternal(long reportSynthesisKeyPartnershipExternalID) {
    ReportSynthesisKeyPartnershipExternal reportSynthesisKeyPartnershipExternal =
      this.find(reportSynthesisKeyPartnershipExternalID);
    if (reportSynthesisKeyPartnershipExternal == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisKeyPartnershipExternal find(long id) {
    return super.find(ReportSynthesisKeyPartnershipExternal.class, id);

  }

  @Override
  public List<ReportSynthesisKeyPartnershipExternal> findAll() {
    String query = "from " + ReportSynthesisKeyPartnershipExternal.class.getName();
    List<ReportSynthesisKeyPartnershipExternal> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisKeyPartnershipExternal
    save(ReportSynthesisKeyPartnershipExternal reportSynthesisKeyPartnershipExternal) {
    if (reportSynthesisKeyPartnershipExternal.getId() == null) {
      super.saveEntity(reportSynthesisKeyPartnershipExternal);
    } else {
      reportSynthesisKeyPartnershipExternal = super.update(reportSynthesisKeyPartnershipExternal);
    }


    return reportSynthesisKeyPartnershipExternal;
  }


}