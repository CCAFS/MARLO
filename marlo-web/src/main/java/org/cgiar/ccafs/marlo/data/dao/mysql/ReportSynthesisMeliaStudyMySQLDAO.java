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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisMeliaStudyDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaStudy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisMeliaStudyMySQLDAO extends AbstractMarloDAO<ReportSynthesisMeliaStudy, Long> implements ReportSynthesisMeliaStudyDAO {


  @Inject
  public ReportSynthesisMeliaStudyMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisMeliaStudy(long reportSynthesisMeliaStudyId) {
    ReportSynthesisMeliaStudy reportSynthesisMeliaStudy = this.find(reportSynthesisMeliaStudyId);
    reportSynthesisMeliaStudy.setActive(false);
    this.update(reportSynthesisMeliaStudy);
  }

  @Override
  public boolean existReportSynthesisMeliaStudy(long reportSynthesisMeliaStudyID) {
    ReportSynthesisMeliaStudy reportSynthesisMeliaStudy = this.find(reportSynthesisMeliaStudyID);
    if (reportSynthesisMeliaStudy == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisMeliaStudy find(long id) {
    return super.find(ReportSynthesisMeliaStudy.class, id);

  }

  @Override
  public List<ReportSynthesisMeliaStudy> findAll() {
    String query = "from " + ReportSynthesisMeliaStudy.class.getName() + " where is_active=1";
    List<ReportSynthesisMeliaStudy> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisMeliaStudy save(ReportSynthesisMeliaStudy reportSynthesisMeliaStudy) {
    if (reportSynthesisMeliaStudy.getId() == null) {
      super.saveEntity(reportSynthesisMeliaStudy);
    } else {
      reportSynthesisMeliaStudy = super.update(reportSynthesisMeliaStudy);
    }


    return reportSynthesisMeliaStudy;
  }


}