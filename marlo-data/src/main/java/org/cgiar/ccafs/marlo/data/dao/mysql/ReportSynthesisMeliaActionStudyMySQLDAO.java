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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisMeliaActionStudyDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaActionStudy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisMeliaActionStudyMySQLDAO extends AbstractMarloDAO<ReportSynthesisMeliaActionStudy, Long> implements ReportSynthesisMeliaActionStudyDAO {


  @Inject
  public ReportSynthesisMeliaActionStudyMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisMeliaActionStudy(long reportSynthesisMeliaActionStudyId) {
    ReportSynthesisMeliaActionStudy reportSynthesisMeliaActionStudy = this.find(reportSynthesisMeliaActionStudyId);
    reportSynthesisMeliaActionStudy.setActive(false);
    this.update(reportSynthesisMeliaActionStudy);
  }

  @Override
  public boolean existReportSynthesisMeliaActionStudy(long reportSynthesisMeliaActionStudyID) {
    ReportSynthesisMeliaActionStudy reportSynthesisMeliaActionStudy = this.find(reportSynthesisMeliaActionStudyID);
    if (reportSynthesisMeliaActionStudy == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisMeliaActionStudy find(long id) {
    return super.find(ReportSynthesisMeliaActionStudy.class, id);

  }

  @Override
  public List<ReportSynthesisMeliaActionStudy> findAll() {
    String query = "from " + ReportSynthesisMeliaActionStudy.class.getName() + " where is_active=1";
    List<ReportSynthesisMeliaActionStudy> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisMeliaActionStudy save(ReportSynthesisMeliaActionStudy reportSynthesisMeliaActionStudy) {
    if (reportSynthesisMeliaActionStudy.getId() == null) {
      super.saveEntity(reportSynthesisMeliaActionStudy);
    } else {
      reportSynthesisMeliaActionStudy = super.update(reportSynthesisMeliaActionStudy);
    }


    return reportSynthesisMeliaActionStudy;
  }


}