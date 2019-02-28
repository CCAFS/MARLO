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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisSrfProgressDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgress;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisSrfProgressMySQLDAO extends AbstractMarloDAO<ReportSynthesisSrfProgress, Long> implements ReportSynthesisSrfProgressDAO {


  @Inject
  public ReportSynthesisSrfProgressMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisSrfProgress(long reportSynthesisSrfProgressId) {
    ReportSynthesisSrfProgress reportSynthesisSrfProgress = this.find(reportSynthesisSrfProgressId);
    reportSynthesisSrfProgress.setActive(false);
    this.update(reportSynthesisSrfProgress);
  }

  @Override
  public boolean existReportSynthesisSrfProgress(long reportSynthesisSrfProgressID) {
    ReportSynthesisSrfProgress reportSynthesisSrfProgress = this.find(reportSynthesisSrfProgressID);
    if (reportSynthesisSrfProgress == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisSrfProgress find(long id) {
    return super.find(ReportSynthesisSrfProgress.class, id);

  }

  @Override
  public List<ReportSynthesisSrfProgress> findAll() {
    String query = "from " + ReportSynthesisSrfProgress.class.getName() + " where is_active=1";
    List<ReportSynthesisSrfProgress> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisSrfProgress save(ReportSynthesisSrfProgress reportSynthesisSrfProgress) {
    if (reportSynthesisSrfProgress.getId() == null) {
      super.saveEntity(reportSynthesisSrfProgress);
    } else {
      reportSynthesisSrfProgress = super.update(reportSynthesisSrfProgress);
    }


    return reportSynthesisSrfProgress;
  }


}