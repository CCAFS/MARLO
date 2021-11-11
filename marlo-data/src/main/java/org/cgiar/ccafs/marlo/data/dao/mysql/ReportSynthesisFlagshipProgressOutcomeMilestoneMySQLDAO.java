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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisFlagshipProgressOutcomeMilestoneMySQLDAO
  extends AbstractMarloDAO<ReportSynthesisFlagshipProgressOutcomeMilestone, Long>
  implements ReportSynthesisFlagshipProgressOutcomeMilestoneDAO {


  @Inject
  public ReportSynthesisFlagshipProgressOutcomeMilestoneMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void
    deleteReportSynthesisFlagshipProgressOutcomeMilestone(long reportSynthesisFlagshipProgressOutcomeMilestoneId) {
    ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone =
      this.find(reportSynthesisFlagshipProgressOutcomeMilestoneId);
    // reportSynthesisFlagshipProgressOutcomeMilestone.setActive(false);
    // this.update(reportSynthesisFlagshipProgressOutcomeMilestone);
    this.delete(reportSynthesisFlagshipProgressOutcomeMilestone);
  }

  @Override
  public boolean
    existReportSynthesisFlagshipProgressOutcomeMilestone(long reportSynthesisFlagshipProgressOutcomeMilestoneID) {
    ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone =
      this.find(reportSynthesisFlagshipProgressOutcomeMilestoneID);
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
  public List<ReportSynthesisFlagshipProgressOutcomeMilestone> getAllFlagshipProgressOutcomeMilestones(long phaseId) {
    String query = "SELECT fpom.* FROM report_synthesis_flagship_progress_outcome_milestones fpom "
      + "left join report_synthesis_flagship_progress_outcomes fpo on fpom.report_synthesis_outcome_id = fpo.id "
      + "left join report_synthesis rs on fpo.report_synthesis_flagship_progress_id = rs.id and rs.is_active "
      + "left join liaison_institutions li on rs.liaison_institution_id = li.id and li.crp_program is not null "
      + "where rs.id_phase = :phaseId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createSQLQuery(query)
      .addEntity(ReportSynthesisFlagshipProgressOutcomeMilestone.class);

    createQuery.setParameter("phaseId", phaseId);

    List<ReportSynthesisFlagshipProgressOutcomeMilestone> findAll = super.findAll(createQuery);

    return findAll;
  }

  @Override
  public ReportSynthesisFlagshipProgressOutcomeMilestone getMilestoneId(long progressID, long outcomeID) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT id as markerId FROM report_synthesis_flagship_progress_outcome_milestones ");
    query.append("WHERE report_synthesis_outcome_id = ");
    query.append(progressID);
    query.append(" AND crp_milestone_id = ");
    query.append(outcomeID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<ReportSynthesisFlagshipProgressOutcomeMilestone> reportSynthesisFlagshipProgressOutcomeMilestones =
      new ArrayList<ReportSynthesisFlagshipProgressOutcomeMilestone>();
    for (Map<String, Object> map : list) {
      String markerId = map.get("markerId").toString();
      long longMarkerId = Long.parseLong(markerId);
      ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone =
        this.find(longMarkerId);
      reportSynthesisFlagshipProgressOutcomeMilestones.add(reportSynthesisFlagshipProgressOutcomeMilestone);
    }
    if (reportSynthesisFlagshipProgressOutcomeMilestones.size() > 0) {
      return reportSynthesisFlagshipProgressOutcomeMilestones.get(0);
    } else {
      return null;
    }

  }

  @Override
  public ReportSynthesisFlagshipProgressOutcomeMilestone getReportSynthesisMilestoneFromOutcomeIdAndMilestoneId(
    long reportSynthesisFlagshipProgressId, long crpProgramOutcomeId, long crpMilestoneId) {
    String query = "select distinct rsfpom from ReportSynthesisFlagshipProgressOutcomeMilestone rsfpom "
      + "where rsfpom.reportSynthesisFlagshipProgressOutcome.reportSynthesisFlagshipProgress.id = :reportSynthesisFlagshipProgressId and "
      + "rsfpom.reportSynthesisFlagshipProgressOutcome.crpProgramOutcome.id = :crpProgramOutcomeId and "
      + "rsfpom.crpMilestone.id = :crpMilestoneId and rsfpom.active=true";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);

    createQuery.setParameter("reportSynthesisFlagshipProgressId", reportSynthesisFlagshipProgressId);
    createQuery.setParameter("crpProgramOutcomeId", crpProgramOutcomeId);
    createQuery.setParameter("crpMilestoneId", crpMilestoneId);

    List<ReportSynthesisFlagshipProgressOutcomeMilestone> findAll = super.findAll(createQuery);

    return findAll.isEmpty() ? null : findAll.get(0);
  }

  @Override
  public ReportSynthesisFlagshipProgressOutcomeMilestone
    save(ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone) {
    if (reportSynthesisFlagshipProgressOutcomeMilestone.getId() == null) {
      super.saveEntity(reportSynthesisFlagshipProgressOutcomeMilestone);
    } else {
      reportSynthesisFlagshipProgressOutcomeMilestone = super.update(reportSynthesisFlagshipProgressOutcomeMilestone);
    }


    return reportSynthesisFlagshipProgressOutcomeMilestone;
  }


}