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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class NewStatusPlannedMilestoneDTO {

  @ApiModelProperty(notes = "Flagship SMO code", position = 1)
  private String crpProgramCode;

  @ApiModelProperty(notes = "Outcome SMO code", position = 2)
  private String crpOutcomeCode;

  @ApiModelProperty(notes = "Milestone SMO code", position = 3)
  private String milestoneCode;

  @ApiModelProperty(notes = "Milestone Status", position = 4)
  private List<NewCrosscuttingMarkersSynthesisDTO> crosscuttinmarkerList;

  @ApiModelProperty(notes = "Milestone Status", position = 5)
  private Long status;

  @ApiModelProperty(notes = "Milestone Evidence status", position = 6)
  private String evidence;

  @ApiModelProperty(notes = "Milestone Evidence Link", position = 7)
  private String linkEvidence;

  @ApiModelProperty(notes = "Milestone Evidence Links", position = 7)
  private List<EvidenceLinkDTO> evidenceLinks;


  @ApiModelProperty(notes = "Year value for extended milestone", position = 8)
  private int extendedYear;


  @ApiModelProperty(notes = "Reason for extended or canceled milestone", position = 9)
  private Long mainReason;

  @ApiModelProperty(notes = "If main reason = 7. Other, please state", position = 10)
  public String otherReason;

  @ApiModelProperty(notes = "Phase (AR, POWB, UpKeep)", position = 11)
  private PhaseDTO phase;

  public List<NewCrosscuttingMarkersSynthesisDTO> getCrosscuttinmarkerList() {
    return crosscuttinmarkerList;
  }

  public String getCrpOutcomeCode() {
    return crpOutcomeCode;
  }


  public String getCrpProgramCode() {
    return crpProgramCode;
  }

  public String getEvidence() {
    return evidence;
  }

  public List<EvidenceLinkDTO> getEvidenceLinks() {
    return evidenceLinks;
  }

  public int getExtendedYear() {
    return extendedYear;
  }

  public String getLinkEvidence() {
    return linkEvidence;
  }

  public Long getMainReason() {
    return mainReason;
  }

  public String getMilestoneCode() {
    return milestoneCode;
  }

  public String getOtherReason() {
    return otherReason;
  }

  public PhaseDTO getPhase() {
    return phase;
  }

  public Long getStatus() {
    return status;
  }

  public void setCrosscuttinmarkerList(List<NewCrosscuttingMarkersSynthesisDTO> crosscuttinmarkerList) {
    this.crosscuttinmarkerList = crosscuttinmarkerList;
  }


  public void setCrpOutcomeCode(String crpOutcomeCode) {
    this.crpOutcomeCode = crpOutcomeCode;
  }

  public void setCrpProgramCode(String crpProgramCode) {
    this.crpProgramCode = crpProgramCode;
  }

  public void setEvidence(String evidence) {
    this.evidence = evidence;
  }

  public void setEvidenceLinks(List<EvidenceLinkDTO> evidenceLinks) {
    this.evidenceLinks = evidenceLinks;
  }

  public void setExtendedYear(int extendedYear) {
    this.extendedYear = extendedYear;
  }

  public void setLinkEvidence(String linkEvidence) {
    this.linkEvidence = linkEvidence;
  }

  public void setMainReason(Long mainReason) {
    this.mainReason = mainReason;
  }

  public void setMilestoneCode(String milestoneCode) {
    this.milestoneCode = milestoneCode;
  }

  public void setOtherReason(String otherJustification) {
    this.otherReason = otherJustification;
  }

  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }

  public void setStatus(Long status) {
    this.status = status;
  }

}
