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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisSrfProgressTargetDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisSrfProgressTargetMySQLDAO extends AbstractMarloDAO<ReportSynthesisSrfProgressTarget, Long> implements ReportSynthesisSrfProgressTargetDAO {


  @Inject
  public ReportSynthesisSrfProgressTargetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisSrfProgressTarget(long reportSynthesisSrfProgressTargetId) {
    ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTarget = this.find(reportSynthesisSrfProgressTargetId);
    reportSynthesisSrfProgressTarget.setActive(false);
    this.update(reportSynthesisSrfProgressTarget);
  }

  @Override
  public boolean existReportSynthesisSrfProgressTarget(long reportSynthesisSrfProgressTargetID) {
    ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTarget = this.find(reportSynthesisSrfProgressTargetID);
    if (reportSynthesisSrfProgressTarget == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisSrfProgressTarget find(long id) {
    return super.find(ReportSynthesisSrfProgressTarget.class, id);

  }

  @Override
  public List<ReportSynthesisSrfProgressTarget> findAll() {
    String query = "from " + ReportSynthesisSrfProgressTarget.class.getName() + " where is_active=1";
    List<ReportSynthesisSrfProgressTarget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisSrfProgressTarget save(ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTarget) {
    if (reportSynthesisSrfProgressTarget.getId() == null) {
      super.saveEntity(reportSynthesisSrfProgressTarget);
    } else {
      reportSynthesisSrfProgressTarget = super.update(reportSynthesisSrfProgressTarget);
    }


    return reportSynthesisSrfProgressTarget;
  }


}