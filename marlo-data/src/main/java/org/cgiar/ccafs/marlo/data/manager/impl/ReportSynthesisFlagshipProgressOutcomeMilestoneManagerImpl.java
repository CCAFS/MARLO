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
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

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
  public ReportSynthesisFlagshipProgressOutcomeMilestone saveReportSynthesisFlagshipProgressOutcomeMilestone(
    ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone) {

    reportSynthesisFlagshipProgressOutcomeMilestone =
      reportSynthesisFlagshipProgressOutcomeMilestoneDAO.save(reportSynthesisFlagshipProgressOutcomeMilestone);

    // Update the Milestone Status
    this.updateMilestoneStatus(reportSynthesisFlagshipProgressOutcomeMilestone.getCrpMilestone(),
      reportSynthesisFlagshipProgressOutcomeMilestone.getMilestonesStatus());

    return reportSynthesisFlagshipProgressOutcomeMilestone;
  }

  /**
   * @param crpProgramOutcome
   * @param crpMilestone
   * @param next
   */
  private void updateMilestonePhase(CrpProgramOutcome crpProgramOutcome, CrpMilestone crpMilestone, Phase next) {
    Phase phase = phaseManager.getPhaseById(next.getId());
    List<CrpProgramOutcome> outcomes = phase.getOutcomes().stream()
      .filter(c -> c.isActive() && c.getCrpProgram().getId().equals(crpProgramOutcome.getCrpProgram().getId())
        && c.getComposeID().equals(crpProgramOutcome.getComposeID()))
      .collect(Collectors.toList());

    if (!outcomes.isEmpty()) {

      CrpProgramOutcome outcomePhase = outcomes.get(0);

      List<CrpMilestone> milestones = outcomePhase.getCrpMilestones().stream()
        .filter(m -> m.isActive() && m.getComposeID().equals(crpMilestone.getComposeID())).collect(Collectors.toList());
      if (!milestones.isEmpty()) {
        CrpMilestone milestonePhase = milestones.get(0);

        milestonePhase.setMilestonesStatus(crpMilestone.getMilestonesStatus());

        if (crpMilestone.getMilestonesStatus().getId().equals(new Long(4))) {
          milestonePhase.setYear(crpMilestone.getYear() + 1);
        }

        crpMilestoneManager.saveCrpMilestone(milestonePhase);

        if (phase.getNext() != null) {
          this.updateMilestonePhase(crpProgramOutcome, crpMilestone, phase.getNext());
        }


      }

    }


  }

  /**
   * @param crpMilestone
   * @param status
   */
  private void updateMilestoneStatus(CrpMilestone crpMilestone, GeneralStatus status) {

    crpMilestone = crpMilestoneManager.getCrpMilestoneById(crpMilestone.getId());
    crpMilestone.setMilestonesStatus(status);
    crpMilestone = crpMilestoneManager.saveCrpMilestone(crpMilestone);

    CrpProgramOutcome crpProgramOutcome =
      crpProgramOutcomeManager.getCrpProgramOutcomeById(crpMilestone.getCrpProgramOutcome().getId());

    Phase phase = phaseManager.getPhaseById(crpProgramOutcome.getPhase().getId());

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        this.updateMilestonePhase(crpProgramOutcome, crpMilestone, phase.getNext());
      }
    }

  }


}
