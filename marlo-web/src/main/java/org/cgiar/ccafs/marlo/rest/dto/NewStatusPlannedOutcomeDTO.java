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

public class NewStatusPlannedOutcomeDTO {

  @ApiModelProperty(notes = "Flagship SMO code", position = 1)
  private String crpProgramCode;

  @ApiModelProperty(notes = "Outcome SMO code", position = 2)
  private String crpOutcomeCode;


  @ApiModelProperty(notes = "Narrative", position = 3)
  private String sumary;


  @ApiModelProperty(notes = "Status Milestones List", position = 4)
  private List<NewStatusPlannedMilestoneDTO> statusMilestoneList;

  @ApiModelProperty(notes = "Phase (AR, POWB, UpKeep)", position = 1)
  private PhaseDTO phase;


  public String getCrpOutcomeCode() {
    return crpOutcomeCode;
  }


  public String getCrpProgramCode() {
    return crpProgramCode;
  }


  public PhaseDTO getPhase() {
    return phase;
  }


  public List<NewStatusPlannedMilestoneDTO> getStatusMilestoneList() {
    return statusMilestoneList;
  }


  public String getSumary() {
    return sumary;
  }


  public void setCrpOutcomeCode(String crpOutcomeCode) {
    this.crpOutcomeCode = crpOutcomeCode;
  }


  public void setCrpProgramCode(String crpProgramCode) {
    this.crpProgramCode = crpProgramCode;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }

  public void setStatusMilestoneList(List<NewStatusPlannedMilestoneDTO> statusMilestoneList) {
    this.statusMilestoneList = statusMilestoneList;
  }

  public void setSumary(String sumary) {
    this.sumary = sumary;
  }


}
