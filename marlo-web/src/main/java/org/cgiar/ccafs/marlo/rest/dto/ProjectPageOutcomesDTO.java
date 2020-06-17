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

public class ProjectPageOutcomesDTO {

  @ApiModelProperty(notes = "Outcome Identifier", position = 1)
  private long outcomeID;

  @ApiModelProperty(notes = "Outcome name", position = 2)
  private String outcomeName;

  @ApiModelProperty(notes = "Flagship identifier", position = 3)
  private CrpProgramDTO flagship;

  @ApiModelProperty(notes = "Milestones List", position = 4)
  private List<ProjectPageMilestonesDTO> milestones;


  public CrpProgramDTO getFlagship() {
    return flagship;
  }


  public List<ProjectPageMilestonesDTO> getMilestones() {
    return milestones;
  }


  public long getOutcomeID() {
    return outcomeID;
  }


  public String getOutcomeName() {
    return outcomeName;
  }


  public void setFlagship(CrpProgramDTO flagship) {
    this.flagship = flagship;
  }


  public void setMilestones(List<ProjectPageMilestonesDTO> milestones) {
    this.milestones = milestones;
  }


  public void setOutcomeID(long outcomeID) {
    this.outcomeID = outcomeID;
  }


  public void setOutcomeName(String outcomeName) {
    this.outcomeName = outcomeName;
  }


}
