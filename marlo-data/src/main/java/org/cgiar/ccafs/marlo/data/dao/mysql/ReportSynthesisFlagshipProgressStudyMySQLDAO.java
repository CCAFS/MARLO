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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressStudyDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisFlagshipProgressStudyMySQLDAO extends
  AbstractMarloDAO<ReportSynthesisFlagshipProgressStudy, Long> implements ReportSynthesisFlagshipProgressStudyDAO {


  @Inject
  public ReportSynthesisFlagshipProgressStudyMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisFlagshipProgressStudy(long reportSynthesisFlagshipProgressStudyId) {
    ReportSynthesisFlagshipProgressStudy reportSynthesisFlagshipProgressStudy =
      this.find(reportSynthesisFlagshipProgressStudyId);
    reportSynthesisFlagshipProgressStudy.setActive(false);
    this.update(reportSynthesisFlagshipProgressStudy);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressStudy(long reportSynthesisFlagshipProgressStudyID) {
    ReportSynthesisFlagshipProgressStudy reportSynthesisFlagshipProgressStudy =
      this.find(reportSynthesisFlagshipProgressStudyID);
    if (reportSynthesisFlagshipProgressStudy == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisFlagshipProgressStudy find(long id) {
    return super.find(ReportSynthesisFlagshipProgressStudy.class, id);

  }

  @Override
  public List<ReportSynthesisFlagshipProgressStudy> findAll() {
    String query = "from " + ReportSynthesisFlagshipProgressStudy.class.getName();
    List<ReportSynthesisFlagshipProgressStudy> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisFlagshipProgressStudy
    getReportSynthesisFlagshipProgressStudyByStudyAndFlagshipProgress(long studyId, long flagshipProgressId) {
    String query =
      "select rsfps from ReportSynthesisFlagshipProgressStudy rsfps where rsfps.projectExpectedStudy.id = :studyId "
        + "and rsfps.reportSynthesisFlagshipProgress.id = :flagshipProgressId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("studyId", studyId);
    createQuery.setParameter("flagshipProgressId", flagshipProgressId);

    List<ReportSynthesisFlagshipProgressStudy> resultList = super.findAll(createQuery);

    return (resultList != null && !resultList.isEmpty()) ? resultList.get(0) : null;
  }

  @Override
  public ReportSynthesisFlagshipProgressStudy
    save(ReportSynthesisFlagshipProgressStudy reportSynthesisFlagshipProgressStudy) {
    if (reportSynthesisFlagshipProgressStudy.getId() == null) {
      super.saveEntity(reportSynthesisFlagshipProgressStudy);
    } else {
      reportSynthesisFlagshipProgressStudy = super.update(reportSynthesisFlagshipProgressStudy);
    }


    return reportSynthesisFlagshipProgressStudy;
  }
}