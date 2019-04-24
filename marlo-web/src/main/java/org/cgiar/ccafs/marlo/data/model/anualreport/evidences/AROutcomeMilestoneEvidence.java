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

package org.cgiar.ccafs.marlo.data.model.anualreport.evidences;

import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.RepIndMilestoneReason;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressCrossCuttingMarker;

import java.io.Serializable;
import java.util.List;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */


public class AROutcomeMilestoneEvidence implements Serializable {

  private static final long serialVersionUID = -8081297800002633467L;

  private CrpProgramOutcome crpProgramOutcome;
  private String outcomeProgress;
  private CrpMilestone crpMilestone;
  private Long milestonesStatus;
  private RepIndMilestoneReason repIndMilestoneReason;
  private String evidence;
  List<ReportSynthesisFlagshipProgressCrossCuttingMarker> crossCuttingMarkers;

  public List<ReportSynthesisFlagshipProgressCrossCuttingMarker> getCrossCuttingMarkers() {
    return crossCuttingMarkers;
  }

  public CrpMilestone getCrpMilestone() {
    return crpMilestone;
  }

  public CrpProgramOutcome getCrpProgramOutcome() {
    return crpProgramOutcome;
  }

  public String getEvidence() {
    return evidence;
  }

  public Long getMilestonesStatus() {
    return milestonesStatus;
  }

  public String getOutcomeProgress() {
    return outcomeProgress;
  }

  public RepIndMilestoneReason getRepIndMilestoneReason() {
    return repIndMilestoneReason;
  }

  public void setCrossCuttingMarkers(List<ReportSynthesisFlagshipProgressCrossCuttingMarker> crossCuttingMarkers) {
    this.crossCuttingMarkers = crossCuttingMarkers;
  }

  public void setCrpMilestone(CrpMilestone crpMilestone) {
    this.crpMilestone = crpMilestone;
  }

  public void setCrpProgramOutcome(CrpProgramOutcome crpProgramOutcome) {
    this.crpProgramOutcome = crpProgramOutcome;
  }

  public void setEvidence(String evidence) {
    this.evidence = evidence;
  }

  public void setMilestonesStatus(Long milestonesStatus) {
    this.milestonesStatus = milestonesStatus;
  }

  public void setOutcomeProgress(String outcomeProgress) {
    this.outcomeProgress = outcomeProgress;
  }

  public void setRepIndMilestoneReason(RepIndMilestoneReason repIndMilestoneReason) {
    this.repIndMilestoneReason = repIndMilestoneReason;
  }


}
