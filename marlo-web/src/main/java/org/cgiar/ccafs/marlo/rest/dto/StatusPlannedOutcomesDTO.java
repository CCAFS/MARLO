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

public class StatusPlannedOutcomesDTO {

  @ApiModelProperty(notes = "Flagship/Moodule Identifier", position = 1)
  private CrpProgramDTO crpProgram;

  @ApiModelProperty(notes = "Outcome Identifier", position = 2)
  private OutcomeDTO outcome;

  @ApiModelProperty(notes = "Outcome Sumary", position = 3)
  private String summary;

  @ApiModelProperty(notes = "Milestones Status", position = 4)
  private List<StatusPlannedMilestonesDTO> milestones;


  @ApiModelProperty(notes = "Phase year/section", position = 5)
  private PhaseDTO phase;


  public CrpProgramDTO getCrpProgram() {
    return crpProgram;
  }


  public List<StatusPlannedMilestonesDTO> getMilestones() {
    return milestones;
  }


  public OutcomeDTO getOutcome() {
    return outcome;
  }


  public PhaseDTO getPhase() {
    return phase;
  }


  public String getSummary() {
    return summary;
  }


  public void setCrpProgram(CrpProgramDTO crpProgram) {
    this.crpProgram = crpProgram;
  }


  public void setMilestones(List<StatusPlannedMilestonesDTO> milestones) {
    this.milestones = milestones;
  }


  public void setOutcome(OutcomeDTO outcome) {
    this.outcome = outcome;
  }


  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


  public void setSummary(String sumary) {
    this.summary = sumary;
  }

}
