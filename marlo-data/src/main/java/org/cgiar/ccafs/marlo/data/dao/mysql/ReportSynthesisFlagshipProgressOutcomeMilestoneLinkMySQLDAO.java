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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestoneLink;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisFlagshipProgressOutcomeMilestoneLinkMySQLDAO
  extends AbstractMarloDAO<ReportSynthesisFlagshipProgressOutcomeMilestoneLink, Long>
  implements ReportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO {


  @Inject
  public ReportSynthesisFlagshipProgressOutcomeMilestoneLinkMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisFlagshipProgressOutcomeMilestoneLink(
    long reportSynthesisFlagshipProgressOutcomeMilestoneLinkId) {
    ReportSynthesisFlagshipProgressOutcomeMilestoneLink reportSynthesisFlagshipProgressOutcomeMilestoneLink =
      this.find(reportSynthesisFlagshipProgressOutcomeMilestoneLinkId);
    // reportSynthesisFlagshipProgressOutcomeMilestoneLink.setActive(false);
    // this.update(reportSynthesisFlagshipProgressOutcomeMilestoneLink);
    this.delete(reportSynthesisFlagshipProgressOutcomeMilestoneLink);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressOutcomeMilestoneLink(
    long reportSynthesisFlagshipProgressOutcomeMilestoneLinkID) {
    ReportSynthesisFlagshipProgressOutcomeMilestoneLink reportSynthesisFlagshipProgressOutcomeMilestoneLink =
      this.find(reportSynthesisFlagshipProgressOutcomeMilestoneLinkID);
    if (reportSynthesisFlagshipProgressOutcomeMilestoneLink == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisFlagshipProgressOutcomeMilestoneLink find(long id) {
    return super.find(ReportSynthesisFlagshipProgressOutcomeMilestoneLink.class, id);

  }

  @Override
  public List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink> findAll() {
    String query = "from " + ReportSynthesisFlagshipProgressOutcomeMilestoneLink.class.getName() + " where is_active=1";
    List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink>
    getLinksByProgressOutcomeMilestone(long progressOutcomeMilestoneId) {
    String query =
      "select rsfpoml from ReportSynthesisFlagshipProgressOutcomeMilestoneLink rsfpoml where reportSynthesisFlagshipProgressOutcomeMilestone.id = :progressOutcomeMilestoneId";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("progressOutcomeMilestoneId", progressOutcomeMilestoneId);

    List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink> reportSynthesisFlagshipProgressOutcomeMilestoneLinks =
      super.findAll(createQuery);

    return reportSynthesisFlagshipProgressOutcomeMilestoneLinks;
  }

  @Override
  public ReportSynthesisFlagshipProgressOutcomeMilestoneLink
    save(ReportSynthesisFlagshipProgressOutcomeMilestoneLink reportSynthesisFlagshipProgressOutcomeMilestoneLink) {
    if (reportSynthesisFlagshipProgressOutcomeMilestoneLink.getId() == null) {
      super.saveEntity(reportSynthesisFlagshipProgressOutcomeMilestoneLink);
    } else {
      reportSynthesisFlagshipProgressOutcomeMilestoneLink =
        super.update(reportSynthesisFlagshipProgressOutcomeMilestoneLink);
    }

    return reportSynthesisFlagshipProgressOutcomeMilestoneLink;
  }
}