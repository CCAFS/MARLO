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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressMilestone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisFlagshipProgressMilestoneMySQLDAO
  extends AbstractMarloDAO<ReportSynthesisFlagshipProgressMilestone, Long>
  implements ReportSynthesisFlagshipProgressMilestoneDAO {


  @Inject
  public ReportSynthesisFlagshipProgressMilestoneMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisFlagshipProgressMilestone(long reportSynthesisFlagshipProgressMilestoneId) {
    ReportSynthesisFlagshipProgressMilestone reportSynthesisFlagshipProgressMilestone =
      this.find(reportSynthesisFlagshipProgressMilestoneId);
    reportSynthesisFlagshipProgressMilestone.setActive(false);
    this.update(reportSynthesisFlagshipProgressMilestone);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressMilestone(long reportSynthesisFlagshipProgressMilestoneID) {
    ReportSynthesisFlagshipProgressMilestone reportSynthesisFlagshipProgressMilestone =
      this.find(reportSynthesisFlagshipProgressMilestoneID);
    if (reportSynthesisFlagshipProgressMilestone == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisFlagshipProgressMilestone find(long id) {
    return super.find(ReportSynthesisFlagshipProgressMilestone.class, id);

  }

  @Override
  public List<ReportSynthesisFlagshipProgressMilestone> findAll() {
    String query = "from " + ReportSynthesisFlagshipProgressMilestone.class.getName() + " where is_active=1";
    List<ReportSynthesisFlagshipProgressMilestone> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ReportSynthesisFlagshipProgressMilestone> findByProgram(long crpProgramID) {
    String query =
      "select distinct pp from ReportSynthesisFlagshipProgressMilestone as pp inner join pp.reportSynthesisFlagshipProgress.reportSynthesis as reportSynthesis "
        + " inner join reportSynthesis.liaisonInstitution liai" + " where liai.crpProgram.id = :crpProgramID ";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("crpProgramID", crpProgramID);
    List<ReportSynthesisFlagshipProgressMilestone> flagshipProgressMilestones = createQuery.list();
    return flagshipProgressMilestones;
  }

  @Override
  public ReportSynthesisFlagshipProgressMilestone
    save(ReportSynthesisFlagshipProgressMilestone reportSynthesisFlagshipProgressMilestone) {
    if (reportSynthesisFlagshipProgressMilestone.getId() == null) {
      super.saveEntity(reportSynthesisFlagshipProgressMilestone);
    } else {
      reportSynthesisFlagshipProgressMilestone = super.update(reportSynthesisFlagshipProgressMilestone);
    }


    return reportSynthesisFlagshipProgressMilestone;
  }


}