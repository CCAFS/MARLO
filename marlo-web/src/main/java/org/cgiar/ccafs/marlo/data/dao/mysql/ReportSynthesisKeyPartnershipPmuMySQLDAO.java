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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipPmuDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipPmu;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisKeyPartnershipPmuMySQLDAO extends AbstractMarloDAO<ReportSynthesisKeyPartnershipPmu, Long> implements ReportSynthesisKeyPartnershipPmuDAO {


  @Inject
  public ReportSynthesisKeyPartnershipPmuMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisKeyPartnershipPmu(long reportSynthesisKeyPartnershipPmuId) {
    ReportSynthesisKeyPartnershipPmu reportSynthesisKeyPartnershipPmu = this.find(reportSynthesisKeyPartnershipPmuId);
    reportSynthesisKeyPartnershipPmu.setActive(false);
    this.update(reportSynthesisKeyPartnershipPmu);
  }

  @Override
  public boolean existReportSynthesisKeyPartnershipPmu(long reportSynthesisKeyPartnershipPmuID) {
    ReportSynthesisKeyPartnershipPmu reportSynthesisKeyPartnershipPmu = this.find(reportSynthesisKeyPartnershipPmuID);
    if (reportSynthesisKeyPartnershipPmu == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisKeyPartnershipPmu find(long id) {
    return super.find(ReportSynthesisKeyPartnershipPmu.class, id);

  }

  @Override
  public List<ReportSynthesisKeyPartnershipPmu> findAll() {
    String query = "from " + ReportSynthesisKeyPartnershipPmu.class.getName() + " where is_active=1";
    List<ReportSynthesisKeyPartnershipPmu> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisKeyPartnershipPmu save(ReportSynthesisKeyPartnershipPmu reportSynthesisKeyPartnershipPmu) {
    if (reportSynthesisKeyPartnershipPmu.getId() == null) {
      super.saveEntity(reportSynthesisKeyPartnershipPmu);
    } else {
      reportSynthesisKeyPartnershipPmu = super.update(reportSynthesisKeyPartnershipPmu);
    }


    return reportSynthesisKeyPartnershipPmu;
  }


}