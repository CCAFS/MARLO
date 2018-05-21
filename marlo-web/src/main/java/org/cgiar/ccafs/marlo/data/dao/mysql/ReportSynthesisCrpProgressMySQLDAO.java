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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrpProgressDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgress;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisCrpProgressMySQLDAO extends AbstractMarloDAO<ReportSynthesisCrpProgress, Long> implements ReportSynthesisCrpProgressDAO {


  @Inject
  public ReportSynthesisCrpProgressMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisCrpProgress(long reportSynthesisCrpProgressId) {
    ReportSynthesisCrpProgress reportSynthesisCrpProgress = this.find(reportSynthesisCrpProgressId);
    reportSynthesisCrpProgress.setActive(false);
    this.update(reportSynthesisCrpProgress);
  }

  @Override
  public boolean existReportSynthesisCrpProgress(long reportSynthesisCrpProgressID) {
    ReportSynthesisCrpProgress reportSynthesisCrpProgress = this.find(reportSynthesisCrpProgressID);
    if (reportSynthesisCrpProgress == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisCrpProgress find(long id) {
    return super.find(ReportSynthesisCrpProgress.class, id);

  }

  @Override
  public List<ReportSynthesisCrpProgress> findAll() {
    String query = "from " + ReportSynthesisCrpProgress.class.getName() + " where is_active=1";
    List<ReportSynthesisCrpProgress> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisCrpProgress save(ReportSynthesisCrpProgress reportSynthesisCrpProgress) {
    if (reportSynthesisCrpProgress.getId() == null) {
      super.saveEntity(reportSynthesisCrpProgress);
    } else {
      reportSynthesisCrpProgress = super.update(reportSynthesisCrpProgress);
    }


    return reportSynthesisCrpProgress;
  }


}