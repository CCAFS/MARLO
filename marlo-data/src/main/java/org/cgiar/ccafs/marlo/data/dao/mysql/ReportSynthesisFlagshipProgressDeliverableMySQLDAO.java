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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressDeliverableDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressDeliverable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

@Named
public class ReportSynthesisFlagshipProgressDeliverableMySQLDAO
  extends AbstractMarloDAO<ReportSynthesisFlagshipProgressDeliverable, Long>
  implements ReportSynthesisFlagshipProgressDeliverableDAO {


  @Inject
  public ReportSynthesisFlagshipProgressDeliverableMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisFlagshipProgressDeliverable(long reportSynthesisFlagshipProgressDeliverableId) {
    ReportSynthesisFlagshipProgressDeliverable reportSynthesisFlagshipProgressDeliverable =
      this.find(reportSynthesisFlagshipProgressDeliverableId);
    reportSynthesisFlagshipProgressDeliverable.setActive(false);
    this.update(reportSynthesisFlagshipProgressDeliverable);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressDeliverable(long reportSynthesisFlagshipProgressDeliverableID) {
    ReportSynthesisFlagshipProgressDeliverable reportSynthesisFlagshipProgressDeliverable =
      this.find(reportSynthesisFlagshipProgressDeliverableID);
    if (reportSynthesisFlagshipProgressDeliverable == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisFlagshipProgressDeliverable find(long id) {
    return super.find(ReportSynthesisFlagshipProgressDeliverable.class, id);

  }

  @Override
  public List<ReportSynthesisFlagshipProgressDeliverable> findAll() {
    String query = "from " + ReportSynthesisFlagshipProgressDeliverable.class.getName() + " where is_active=1";
    List<ReportSynthesisFlagshipProgressDeliverable> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisFlagshipProgressDeliverable getByFlagshipProgressAndDeliverable(long deliverableId,
    long progressId) {
    String query = "select distinct rsfpd from ReportSynthesisFlagshipProgressDeliverable rsfpd "
      + "where reportSynthesisFlagshipProgress.id = :progressId and deliverable.id = :deliverableId order by activeSince desc";
    Query<ReportSynthesisFlagshipProgressDeliverable> createQuery =
      this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("progressId", progressId);
    createQuery.setParameter("deliverableId", deliverableId);
    // equivalent to LIMIT 1
    createQuery.setMaxResults(1);

    List<ReportSynthesisFlagshipProgressDeliverable> resultAll = super.findAll(createQuery);
    ReportSynthesisFlagshipProgressDeliverable result = null;

    if (resultAll != null && !resultAll.isEmpty()) {
      result = resultAll.get(0);
    }

    return result;
  }

  @Override
  public ReportSynthesisFlagshipProgressDeliverable
    save(ReportSynthesisFlagshipProgressDeliverable reportSynthesisFlagshipProgressDeliverable) {
    if (reportSynthesisFlagshipProgressDeliverable.getId() == null) {
      super.saveEntity(reportSynthesisFlagshipProgressDeliverable);
    } else {
      reportSynthesisFlagshipProgressDeliverable = super.update(reportSynthesisFlagshipProgressDeliverable);
    }


    return reportSynthesisFlagshipProgressDeliverable;
  }


}