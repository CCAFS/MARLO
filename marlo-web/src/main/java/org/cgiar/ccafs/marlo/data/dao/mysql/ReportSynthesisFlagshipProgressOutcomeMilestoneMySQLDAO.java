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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressOutcomeMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisFlagshipProgressOutcomeMilestoneMySQLDAO extends AbstractMarloDAO<ReportSynthesisFlagshipProgressOutcomeMilestone, Long> implements ReportSynthesisFlagshipProgressOutcomeMilestoneDAO {


  @Inject
  public ReportSynthesisFlagshipProgressOutcomeMilestoneMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisFlagshipProgressOutcomeMilestone(long reportSynthesisFlagshipProgressOutcomeMilestoneId) {
    ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone = this.find(reportSynthesisFlagshipProgressOutcomeMilestoneId);
    reportSynthesisFlagshipProgressOutcomeMilestone.setActive(false);
    this.update(reportSynthesisFlagshipProgressOutcomeMilestone);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressOutcomeMilestone(long reportSynthesisFlagshipProgressOutcomeMilestoneID) {
    ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone = this.find(reportSynthesisFlagshipProgressOutcomeMilestoneID);
    if (reportSynthesisFlagshipProgressOutcomeMilestone == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisFlagshipProgressOutcomeMilestone find(long id) {
    return super.find(ReportSynthesisFlagshipProgressOutcomeMilestone.class, id);

  }

  @Override
  public List<ReportSynthesisFlagshipProgressOutcomeMilestone> findAll() {
    String query = "from " + ReportSynthesisFlagshipProgressOutcomeMilestone.class.getName() + " where is_active=1";
    List<ReportSynthesisFlagshipProgressOutcomeMilestone> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisFlagshipProgressOutcomeMilestone save(ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone) {
    if (reportSynthesisFlagshipProgressOutcomeMilestone.getId() == null) {
      super.saveEntity(reportSynthesisFlagshipProgressOutcomeMilestone);
    } else {
      reportSynthesisFlagshipProgressOutcomeMilestone = super.update(reportSynthesisFlagshipProgressOutcomeMilestone);
    }


    return reportSynthesisFlagshipProgressOutcomeMilestone;
  }


}