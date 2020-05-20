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

public class StatusPlannedMilestonesDTO {

  @ApiModelProperty(notes = "Milestone Identifier", position = 1)
  private MilestoneDTO milestone;

  @ApiModelProperty(notes = "Milestone Status", position = 2)
  private MilestoneStatusDTO status;

  @ApiModelProperty(notes = "Milestone Status Evidence Justification", position = 3)
  private String evidence;

  @ApiModelProperty(notes = "Milestone Status Evidence links", position = 4)
  private String evidenceLink;

  @ApiModelProperty(notes = "Milestone Cross Cutting Markers", position = 5)
  private List<CrosscuttingMarkersJustificationDTO> crossCuttingMarkerList;

  @ApiModelProperty(notes = "If status = 7. Other, please state", position = 6)
  public String otherJustification;


  public List<CrosscuttingMarkersJustificationDTO> getCrossCuttingMarkerList() {
    return crossCuttingMarkerList;
  }

  public String getEvidence() {
    return evidence;
  }

  public String getEvidenceLink() {
    return evidenceLink;
  }

  public MilestoneDTO getMilestone() {
    return milestone;
  }

  public String getOtherJustification() {
    return otherJustification;
  }

  public MilestoneStatusDTO getStatus() {
    return status;
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

  public void setMilestone(MilestoneDTO milestone) {
    this.milestone = milestone;
  }

  public void setOtherJustification(String otherJustification) {
    this.otherJustification = otherJustification;
  }

  public void setStatus(MilestoneStatusDTO status) {
    this.status = status;
  }

}
