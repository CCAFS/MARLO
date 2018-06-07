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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrpProgressStudyDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressStudy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisCrpProgressStudyMySQLDAO extends AbstractMarloDAO<ReportSynthesisCrpProgressStudy, Long> implements ReportSynthesisCrpProgressStudyDAO {


  @Inject
  public ReportSynthesisCrpProgressStudyMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisCrpProgressStudy(long reportSynthesisCrpProgressStudyId) {
    ReportSynthesisCrpProgressStudy reportSynthesisCrpProgressStudy = this.find(reportSynthesisCrpProgressStudyId);
    reportSynthesisCrpProgressStudy.setActive(false);
    this.update(reportSynthesisCrpProgressStudy);
  }

  @Override
  public boolean existReportSynthesisCrpProgressStudy(long reportSynthesisCrpProgressStudyID) {
    ReportSynthesisCrpProgressStudy reportSynthesisCrpProgressStudy = this.find(reportSynthesisCrpProgressStudyID);
    if (reportSynthesisCrpProgressStudy == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisCrpProgressStudy find(long id) {
    return super.find(ReportSynthesisCrpProgressStudy.class, id);

  }

  @Override
  public List<ReportSynthesisCrpProgressStudy> findAll() {
    String query = "from " + ReportSynthesisCrpProgressStudy.class.getName() + " where is_active=1";
    List<ReportSynthesisCrpProgressStudy> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisCrpProgressStudy save(ReportSynthesisCrpProgressStudy reportSynthesisCrpProgressStudy) {
    if (reportSynthesisCrpProgressStudy.getId() == null) {
      super.saveEntity(reportSynthesisCrpProgressStudy);
    } else {
      reportSynthesisCrpProgressStudy = super.update(reportSynthesisCrpProgressStudy);
    }


    return reportSynthesisCrpProgressStudy;
  }


}