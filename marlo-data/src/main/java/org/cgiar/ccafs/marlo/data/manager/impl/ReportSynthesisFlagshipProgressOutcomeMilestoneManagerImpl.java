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


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressOutcomeMilestoneDAO;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeMilestoneManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFlagshipProgressOutcomeMilestoneManagerImpl
  implements ReportSynthesisFlagshipProgressOutcomeMilestoneManager {


  private ReportSynthesisFlagshipProgressOutcomeMilestoneDAO reportSynthesisFlagshipProgressOutcomeMilestoneDAO;
  // Managers
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private PhaseManager phaseManager;
  private CrpMilestoneManager crpMilestoneManager;


  @Inject
  public ReportSynthesisFlagshipProgressOutcomeMilestoneManagerImpl(
    ReportSynthesisFlagshipProgressOutcomeMilestoneDAO reportSynthesisFlagshipProgressOutcomeMilestoneDAO,
    CrpProgramOutcomeManager crpProgramOutcomeManager, PhaseManager phaseManager,
    CrpMilestoneManager crpMilestoneManager) {
    this.reportSynthesisFlagshipProgressOutcomeMilestoneDAO = reportSynthesisFlagshipProgressOutcomeMilestoneDAO;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.phaseManager = phaseManager;
    this.crpMilestoneManager = crpMilestoneManager;
  }

  @Override
  public void
    deleteReportSynthesisFlagshipProgressOutcomeMilestone(long reportSynthesisFlagshipProgressOutcomeMilestoneId) {

    reportSynthesisFlagshipProgressOutcomeMilestoneDAO
      .deleteReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisFlagshipProgressOutcomeMilestoneId);
  }

  @Override
  public boolean
    existReportSynthesisFlagshipProgressOutcomeMilestone(long reportSynthesisFlagshipProgressOutcomeMilestoneID) {

    return reportSynthesisFlagshipProgressOutcomeMilestoneDAO
      .existReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisFlagshipProgressOutcomeMilestoneID);
  }

  @Override
  public List<ReportSynthesisFlagshipProgressOutcomeMilestone> findAll() {

    return reportSynthesisFlagshipProgressOutcomeMilestoneDAO.findAll();

  }

  @Override
  public ReportSynthesisFlagshipProgressOutcomeMilestone getMilestoneId(long progressID, long outcomeID) {
    return reportSynthesisFlagshipProgressOutcomeMilestoneDAO.getMilestoneId(progressID, outcomeID);
  }

  @Override
  public ReportSynthesisFlagshipProgressOutcomeMilestone
    getReportSynthesisFlagshipProgressOutcomeMilestoneById(long reportSynthesisFlagshipProgressOutcomeMilestoneID) {

    return reportSynthesisFlagshipProgressOutcomeMilestoneDAO.find(reportSynthesisFlagshipProgressOutcomeMilestoneID);
  }

  @Override
  public ReportSynthesisFlagshipProgressOutcomeMilestone getReportSynthesisMilestoneFromOutcomeIdAndMilestoneId(
    long reportSynthesisFlagshipProgressId, long crpProgramOutcomeId, long crpMilestoneId) {
    return this.reportSynthesisFlagshipProgressOutcomeMilestoneDAO
      .getReportSynthesisMilestoneFromOutcomeIdAndMilestoneId(reportSynthesisFlagshipProgressId, crpProgramOutcomeId,
        crpMilestoneId);
  }

  @Override
  public ReportSynthesisFlagshipProgressOutcomeMilestone saveReportSynthesisFlagshipProgressOutcomeMilestone(
    ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone) {

    reportSynthesisFlagshipProgressOutcomeMilestone =
      reportSynthesisFlagshipProgressOutcomeMilestoneDAO.save(reportSynthesisFlagshipProgressOutcomeMilestone);

    // Update the Milestone Status and Extended Year (if applies)
    this.updateMilestoneStatusAndExtendedYear(reportSynthesisFlagshipProgressOutcomeMilestone.getCrpMilestone(),
      reportSynthesisFlagshipProgressOutcomeMilestone.getMilestonesStatus(),
      reportSynthesisFlagshipProgressOutcomeMilestone.getExtendedYear());

    return reportSynthesisFlagshipProgressOutcomeMilestone;
  }


  /**
   * Updates the milestone's status and extended year (if applies)
   * 
   * @param crpMilestone the milestone to be updated
   * @param status the new status
   * @param extendedYear the new extended year (if applies)
   */
  private void updateMilestoneStatusAndExtendedYear(CrpMilestone crpMilestone, GeneralStatus status,
    Integer extendedYear) {
    CrpProgramOutcome crpProgramOutcome =
      crpProgramOutcomeManager.getCrpProgramOutcomeById(crpMilestone.getCrpProgramOutcome().getId());
    Phase phase = phaseManager.getPhaseById(crpProgramOutcome.getPhase().getId());
    if (StringUtils.equalsIgnoreCase(phase.getDescription(), APConstants.REPORTING)) {
      // in theory should always happen
      phase = phase.getNext().getNext();
    }

    crpMilestone = crpMilestoneManager.getCrpMilestoneByPhase(crpMilestone.getComposeID(), phase.getId());
    crpMilestone.setMilestonesStatus(status);
    if (status != null && StringUtils.containsIgnoreCase(status.getName(), "xtended")) {
      crpMilestone.setExtendedYear(extendedYear);
    }

    crpMilestone = crpMilestoneManager.saveCrpMilestone(crpMilestone);
    if (phase.getNext() != null) {
      crpMilestoneManager.replicate(crpMilestone, phase.getNext());
    }
  }
}