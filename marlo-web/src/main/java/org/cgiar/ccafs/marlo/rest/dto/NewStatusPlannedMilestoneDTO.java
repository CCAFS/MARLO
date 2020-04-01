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

  @ApiModelProperty(notes = "Milestone SMO code", position = 1)
  private String milestoneCode;

  @ApiModelProperty(notes = "Milestone Status", position = 2)
  private List<NewCrosscuttingMarkersSynthesisDTO> crosscuttinmarkerList;

  @ApiModelProperty(notes = "Milestone Status", position = 3)
  private Long status;

  @ApiModelProperty(notes = "Milestone Evidence", position = 4)
  private String evidence;

  @ApiModelProperty(notes = "Milestone Evidence Link", position = 5)
  private String linkEvidence;


  public List<NewCrosscuttingMarkersSynthesisDTO> getCrosscuttinmarkerList() {
    return crosscuttinmarkerList;
  }


  public String getEvidence() {
    return evidence;
  }


  public String getLinkEvidence() {
    return linkEvidence;
  }


  public String getMilestoneCode() {
    return milestoneCode;
  }


  public Long getStatus() {
    return status;
  }


  public void setCrosscuttinmarkerList(List<NewCrosscuttingMarkersSynthesisDTO> crosscuttinmarkerList) {
    this.crosscuttinmarkerList = crosscuttinmarkerList;
  }


  public void setEvidence(String evidence) {
    this.evidence = evidence;
  }


  public void setLinkEvidence(String linkEvidence) {
    this.linkEvidence = linkEvidence;
  }


  public void setMilestoneCode(String milestoneCode) {
    this.milestoneCode = milestoneCode;
  }


  public void setStatus(Long status) {
    this.status = status;
  }


}
