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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisGovernanceDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisGovernance;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisGovernanceMySQLDAO extends AbstractMarloDAO<ReportSynthesisGovernance, Long> implements ReportSynthesisGovernanceDAO {


  @Inject
  public ReportSynthesisGovernanceMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisGovernance(long reportSynthesisGovernanceId) {
    ReportSynthesisGovernance reportSynthesisGovernance = this.find(reportSynthesisGovernanceId);
    reportSynthesisGovernance.setActive(false);
    this.update(reportSynthesisGovernance);
  }

  @Override
  public boolean existReportSynthesisGovernance(long reportSynthesisGovernanceID) {
    ReportSynthesisGovernance reportSynthesisGovernance = this.find(reportSynthesisGovernanceID);
    if (reportSynthesisGovernance == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisGovernance find(long id) {
    return super.find(ReportSynthesisGovernance.class, id);

  }

  @Override
  public List<ReportSynthesisGovernance> findAll() {
    String query = "from " + ReportSynthesisGovernance.class.getName() + " where is_active=1";
    List<ReportSynthesisGovernance> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisGovernance save(ReportSynthesisGovernance reportSynthesisGovernance) {
    if (reportSynthesisGovernance.getId() == null) {
      super.saveEntity(reportSynthesisGovernance);
    } else {
      reportSynthesisGovernance = super.update(reportSynthesisGovernance);
    }


    return reportSynthesisGovernance;
  }


}