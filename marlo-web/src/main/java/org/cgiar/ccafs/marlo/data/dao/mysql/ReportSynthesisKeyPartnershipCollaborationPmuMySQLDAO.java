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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipCollaborationPmuDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaborationPmu;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisKeyPartnershipCollaborationPmuMySQLDAO extends AbstractMarloDAO<ReportSynthesisKeyPartnershipCollaborationPmu, Long> implements ReportSynthesisKeyPartnershipCollaborationPmuDAO {


  @Inject
  public ReportSynthesisKeyPartnershipCollaborationPmuMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisKeyPartnershipCollaborationPmu(long reportSynthesisKeyPartnershipCollaborationPmuId) {
    ReportSynthesisKeyPartnershipCollaborationPmu reportSynthesisKeyPartnershipCollaborationPmu = this.find(reportSynthesisKeyPartnershipCollaborationPmuId);
    reportSynthesisKeyPartnershipCollaborationPmu.setActive(false);
    this.update(reportSynthesisKeyPartnershipCollaborationPmu);
  }

  @Override
  public boolean existReportSynthesisKeyPartnershipCollaborationPmu(long reportSynthesisKeyPartnershipCollaborationPmuID) {
    ReportSynthesisKeyPartnershipCollaborationPmu reportSynthesisKeyPartnershipCollaborationPmu = this.find(reportSynthesisKeyPartnershipCollaborationPmuID);
    if (reportSynthesisKeyPartnershipCollaborationPmu == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisKeyPartnershipCollaborationPmu find(long id) {
    return super.find(ReportSynthesisKeyPartnershipCollaborationPmu.class, id);

  }

  @Override
  public List<ReportSynthesisKeyPartnershipCollaborationPmu> findAll() {
    String query = "from " + ReportSynthesisKeyPartnershipCollaborationPmu.class.getName() + " where is_active=1";
    List<ReportSynthesisKeyPartnershipCollaborationPmu> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisKeyPartnershipCollaborationPmu save(ReportSynthesisKeyPartnershipCollaborationPmu reportSynthesisKeyPartnershipCollaborationPmu) {
    if (reportSynthesisKeyPartnershipCollaborationPmu.getId() == null) {
      super.saveEntity(reportSynthesisKeyPartnershipCollaborationPmu);
    } else {
      reportSynthesisKeyPartnershipCollaborationPmu = super.update(reportSynthesisKeyPartnershipCollaborationPmu);
    }


    return reportSynthesisKeyPartnershipCollaborationPmu;
  }


}