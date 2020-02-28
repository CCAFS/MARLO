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

import io.swagger.annotations.ApiModelProperty;

public class NewSrfProgressTowardsTargetDTO {

  // TODO remove if right
  // @ApiModelProperty(notes = "SLO target", position = 2)
  // private SrfSloIndicatorTargetDTO srfSloIndicatorTargetDTO;
  @ApiModelProperty(notes = "SLO indicator target  ID", position = 2)
  private String srfSloIndicatorTargetId;

  @ApiModelProperty(notes = "Progress Towards Target narrative", position = 3)
  private String briefSummary;

  @ApiModelProperty(notes = "Progress Towards Target additional contribution", position = 4)
  private String additionalContribution;

  @ApiModelProperty(notes = "Phase - Year ", position = 5)
  private PhaseDTO phase;

  @ApiModelProperty(notes = "Flagship / Module", position = 1)
  private String flagshipProgramId;


  public String getAdditionalContribution() {
    return additionalContribution;
  }

  public String getBriefSummary() {
    return briefSummary;
  }

  public String getFlagshipProgramId() {
    return flagshipProgramId;
  }

  public PhaseDTO getPhase() {
    return phase;
  }

  // public SrfSloIndicatorTargetDTO getSrfSloIndicatorTargetDTO() {
  // return srfSloIndicatorTargetDTO;
  // }

  public String getSrfSloIndicatorTargetId() {
    return srfSloIndicatorTargetId;
  }


  public void setAdditionalContribution(String additionalContribution) {
    this.additionalContribution = additionalContribution;
  }

  public void setBriefSummary(String briefSummary) {
    this.briefSummary = briefSummary;
  }

  public void setFlagshipProgramId(String flagshipProgramId) {
    this.flagshipProgramId = flagshipProgramId;
  }

  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }

  // public void setSrfSloIndicatorTargetDTO(SrfSloIndicatorTargetDTO srfSloIndicatorTargetDTO) {
  // this.srfSloIndicatorTargetDTO = srfSloIndicatorTargetDTO;
  // }

  public void setSrfSloIndicatorTargetId(String srfSloIndicatorTargetId) {
    this.srfSloIndicatorTargetId = srfSloIndicatorTargetId;
  }
}
