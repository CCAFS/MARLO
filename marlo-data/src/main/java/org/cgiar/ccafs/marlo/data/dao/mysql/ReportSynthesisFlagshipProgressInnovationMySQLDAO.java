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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressInnovationDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisFlagshipProgressInnovationMySQLDAO
  extends AbstractMarloDAO<ReportSynthesisFlagshipProgressInnovation, Long>
  implements ReportSynthesisFlagshipProgressInnovationDAO {


  @Inject
  public ReportSynthesisFlagshipProgressInnovationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisFlagshipProgressInnovation(long reportSynthesisFlagshipProgressInnovationId) {
    ReportSynthesisFlagshipProgressInnovation reportSynthesisFlagshipProgressInnovation =
      this.find(reportSynthesisFlagshipProgressInnovationId);
    reportSynthesisFlagshipProgressInnovation.setActive(false);
    this.update(reportSynthesisFlagshipProgressInnovation);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressInnovation(long reportSynthesisFlagshipProgressInnovationID) {
    ReportSynthesisFlagshipProgressInnovation reportSynthesisFlagshipProgressInnovation =
      this.find(reportSynthesisFlagshipProgressInnovationID);
    if (reportSynthesisFlagshipProgressInnovation == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisFlagshipProgressInnovation find(long id) {
    return super.find(ReportSynthesisFlagshipProgressInnovation.class, id);

  }

  @Override
  public List<ReportSynthesisFlagshipProgressInnovation> findAll() {
    String query = "from " + ReportSynthesisFlagshipProgressInnovation.class.getName();
    List<ReportSynthesisFlagshipProgressInnovation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisFlagshipProgressInnovation
    getReportSynthesisFlagshipProgressInnovationByInnovationAndFlagshipProgress(long innovationId,
      long flagshipProgressId) {
    String query =
      "select rsfpi from ReportSynthesisFlagshipProgressInnovation rsfpi where rsfpi.projectInnovation.id = :innovationId "
        + "and rsfpi.reportSynthesisFlagshipProgress.id = :flagshipProgressId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("innovationId", innovationId);
    createQuery.setParameter("flagshipProgressId", flagshipProgressId);

    List<ReportSynthesisFlagshipProgressInnovation> resultList = super.findAll(createQuery);

    return (resultList != null && !resultList.isEmpty()) ? resultList.get(0) : null;
  }

  @Override
  public ReportSynthesisFlagshipProgressInnovation
    save(ReportSynthesisFlagshipProgressInnovation reportSynthesisFlagshipProgressInnovation) {
    if (reportSynthesisFlagshipProgressInnovation.getId() == null) {
      super.saveEntity(reportSynthesisFlagshipProgressInnovation);
    } else {
      reportSynthesisFlagshipProgressInnovation = super.update(reportSynthesisFlagshipProgressInnovation);
    }


    return reportSynthesisFlagshipProgressInnovation;
  }


}