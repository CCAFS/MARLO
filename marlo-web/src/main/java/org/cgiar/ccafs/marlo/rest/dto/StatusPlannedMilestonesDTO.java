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

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

public class StatusPlannedMilestonesDTO {

  @ApiModelProperty(notes = "Milestone Identifier", position = 1)
  private MilestoneDTO milestone;


  @ApiModelProperty(notes = "Reported Milestone Status", position = 3)
  private MilestoneStatusDTO reportedStatus;

  @ApiModelProperty(notes = "Reported Milestone extended year", position = 4)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Integer reportedExtendedYear;

  @ApiModelProperty(notes = "Milestone Status Evidence Justification", position = 5)
  private String evidence;


  @ApiModelProperty(notes = "Milestone Status Evidence links", position = 6)
  private String evidenceLink;

  @ApiModelProperty(notes = "Milestone Status Evidence link list", position = 6)
  private List<EvidenceLinkDTO> evidenceLinkList;


  @ApiModelProperty(notes = "Milestone Cross Cutting Markers", position = 7)
  private List<CrosscuttingMarkersJustificationDTO> crossCuttingMarkerList;


  @ApiModelProperty(notes = "Main reason for Milestone Status", position = 8)
  public MilestoneReasonDTO reason;


  @ApiModelProperty(notes = "If main reason = 7. Other, please state", position = 9)
  public String otherReason;

  public List<CrosscuttingMarkersJustificationDTO> getCrossCuttingMarkerList() {
    return crossCuttingMarkerList;
  }


  public String getEvidence() {
    return evidence;
  }


  public String getEvidenceLink() {
    return evidenceLink;
  }


  public List<EvidenceLinkDTO> getEvidenceLinkList() {
    return evidenceLinkList;
  }


  public MilestoneDTO getMilestone() {
    return milestone;
  }

  public String getOtherReason() {
    return otherReason;
  }

  public MilestoneReasonDTO getReason() {
    return reason;
  }


  public Integer getReportedExtendedYear() {
    return reportedExtendedYear;
  }

  public MilestoneStatusDTO getReportedStatus() {
    return reportedStatus;
  }


  public void setCrossCuttingMarkerList(List<CrosscuttingMarkersJustificationDTO> crossCuttingMarkerList) {
    this.crossCuttingMarkerList = crossCuttingMarkerList;
  }


  public void setEvidence(String evidence) {
    this.evidence = evidence;
  }

  public void setEvidenceLink(String evidenceLink) {
    this.evidenceLink = evidenceLink;
  }


  public void setEvidenceLinkList(List<EvidenceLinkDTO> evidenceLinkList) {
    this.evidenceLinkList = evidenceLinkList;
  }


  public void setMilestone(MilestoneDTO milestone) {
    this.milestone = milestone;
  }

  public void setOtherReason(String otherJustification) {
    this.otherReason = otherJustification;
  }

  public void setReason(MilestoneReasonDTO reason) {
    this.reason = reason;
  }


  public void setReportedExtendedYear(Integer reportedExtendedYear) {
    this.reportedExtendedYear = reportedExtendedYear;
  }


  public void setReportedStatus(MilestoneStatusDTO reportedStatus) {
    this.reportedStatus = reportedStatus;
  }


}
