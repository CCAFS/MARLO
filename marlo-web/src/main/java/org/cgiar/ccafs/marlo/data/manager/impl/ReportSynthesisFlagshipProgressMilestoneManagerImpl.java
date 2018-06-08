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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressMilestoneDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressMilestoneManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressMilestone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFlagshipProgressMilestoneManagerImpl
  implements ReportSynthesisFlagshipProgressMilestoneManager {


  private ReportSynthesisFlagshipProgressMilestoneDAO reportSynthesisFlagshipProgressMilestoneDAO;
  // Managers


  @Inject
  public ReportSynthesisFlagshipProgressMilestoneManagerImpl(
    ReportSynthesisFlagshipProgressMilestoneDAO reportSynthesisFlagshipProgressMilestoneDAO) {
    this.reportSynthesisFlagshipProgressMilestoneDAO = reportSynthesisFlagshipProgressMilestoneDAO;


  }

  @Override
  public void deleteReportSynthesisFlagshipProgressMilestone(long reportSynthesisFlagshipProgressMilestoneId) {

    reportSynthesisFlagshipProgressMilestoneDAO
      .deleteReportSynthesisFlagshipProgressMilestone(reportSynthesisFlagshipProgressMilestoneId);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressMilestone(long reportSynthesisFlagshipProgressMilestoneID) {

    return reportSynthesisFlagshipProgressMilestoneDAO
      .existReportSynthesisFlagshipProgressMilestone(reportSynthesisFlagshipProgressMilestoneID);
  }

  @Override
  public List<ReportSynthesisFlagshipProgressMilestone> findAll() {

    return reportSynthesisFlagshipProgressMilestoneDAO.findAll();

  }

  @Override
  public List<ReportSynthesisFlagshipProgressMilestone> findByProgram(long crpProgramID) {
    return reportSynthesisFlagshipProgressMilestoneDAO.findByProgram(crpProgramID);
  }

  @Override
  public ReportSynthesisFlagshipProgressMilestone
    getReportSynthesisFlagshipProgressMilestoneById(long reportSynthesisFlagshipProgressMilestoneID) {

    return reportSynthesisFlagshipProgressMilestoneDAO.find(reportSynthesisFlagshipProgressMilestoneID);
  }

  @Override
  public ReportSynthesisFlagshipProgressMilestone saveReportSynthesisFlagshipProgressMilestone(
    ReportSynthesisFlagshipProgressMilestone reportSynthesisFlagshipProgressMilestone) {

    return reportSynthesisFlagshipProgressMilestoneDAO.save(reportSynthesisFlagshipProgressMilestone);
  }


}
