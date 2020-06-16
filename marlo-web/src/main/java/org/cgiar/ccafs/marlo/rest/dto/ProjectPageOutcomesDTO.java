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

  @ApiModelProperty(notes = "Outcome", position = 1)
  private DefaultFieldDTO outcome;

  @ApiModelProperty(notes = "Milestones List", position = 2)
  private List<MilestoneNameDTO> milestones;


  public List<MilestoneNameDTO> getMilestones() {
    return milestones;
  }


  public DefaultFieldDTO getOutcome() {
    return outcome;
  }


  public void setMilestones(List<MilestoneNameDTO> milestones) {
    this.milestones = milestones;
  }


  public void setOutcome(DefaultFieldDTO outcome) {
    this.outcome = outcome;
  }


}
