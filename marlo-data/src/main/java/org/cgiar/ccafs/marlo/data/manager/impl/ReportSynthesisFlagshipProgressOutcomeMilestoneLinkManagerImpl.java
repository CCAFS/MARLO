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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestoneLink;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManagerImpl
  implements ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager {


  private ReportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO reportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO;
  // Managers
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private PhaseManager phaseManager;
  private CrpMilestoneManager crpMilestoneManager;

  @Inject
  public ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManagerImpl(
    ReportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO reportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO,
    CrpProgramOutcomeManager crpProgramOutcomeManager, PhaseManager phaseManager,
    CrpMilestoneManager crpMilestoneManager) {
    this.reportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO =
      reportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.phaseManager = phaseManager;
    this.crpMilestoneManager = crpMilestoneManager;
  }

  @Override
  public void deleteReportSynthesisFlagshipProgressOutcomeMilestoneLink(
    long reportSynthesisFlagshipProgressOutcomeMilestoneLinkId) {
    reportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO
      .deleteReportSynthesisFlagshipProgressOutcomeMilestoneLink(reportSynthesisFlagshipProgressOutcomeMilestoneLinkId);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressOutcomeMilestoneLink(
    long reportSynthesisFlagshipProgressOutcomeMilestoneLinkID) {
    return reportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO
      .existReportSynthesisFlagshipProgressOutcomeMilestoneLink(reportSynthesisFlagshipProgressOutcomeMilestoneLinkID);
  }

  @Override
  public List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink> findAll() {
    return reportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO.findAll();
  }

  @Override
  public List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink>
    getLinksByProgressOutcomeMilestone(long progressMilestoneOutcomeId) {
    return reportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO
      .getLinksByProgressOutcomeMilestone(progressMilestoneOutcomeId);
  }

  @Override
  public ReportSynthesisFlagshipProgressOutcomeMilestoneLink getReportSynthesisFlagshipProgressOutcomeMilestoneLinkById(
    long reportSynthesisFlagshipProgressOutcomeMilestoneLinkID) {

    return reportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO
      .find(reportSynthesisFlagshipProgressOutcomeMilestoneLinkID);
  }

  @Override
  public ReportSynthesisFlagshipProgressOutcomeMilestoneLink saveReportSynthesisFlagshipProgressOutcomeMilestoneLink(
    ReportSynthesisFlagshipProgressOutcomeMilestoneLink reportSynthesisFlagshipProgressOutcomeMilestoneLink) {

    reportSynthesisFlagshipProgressOutcomeMilestoneLink =
      reportSynthesisFlagshipProgressOutcomeMilestoneLinkDAO.save(reportSynthesisFlagshipProgressOutcomeMilestoneLink);

    return reportSynthesisFlagshipProgressOutcomeMilestoneLink;
  }
}