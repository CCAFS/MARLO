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

package org.cgiar.ccafs.marlo.rest.dto;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class SrfProgressTowardsDTO {

  @ApiModelProperty(notes = "The Generated identification Code", position = 1)
  private Long code;

  @ApiModelProperty(notes = "SRF progress targets", position = 2)
  private List<SrfProgressTowardsTargetDTO> srfSloTargets;

  @ApiModelProperty(notes = "Progress Towards summary", position = 3)
  private String summary;

  @ApiModelProperty(notes = "Phase - Year ", position = 4)
  private PhaseDTO phase;

  public Long getCode() {
    return code;
  }

  public PhaseDTO getPhase() {
    return phase;
  }

  public List<SrfProgressTowardsTargetDTO> getSrfSloTargets() {
    return srfSloTargets;
  }

  public String getSummary() {
    return summary;
  }


  public void setCode(Long code) {
    this.code = code;
  }

  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }

  public void setSrfSloTargets(List<SrfProgressTowardsTargetDTO> srfSloTargets) {
    this.srfSloTargets = srfSloTargets;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

}
