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

public class NewProgressTowardsSRFTargetDTO {

  @ApiModelProperty(notes = "SLO target", position = 2)
  private SrfSloTargetDTO srfSloTarget;
  @ApiModelProperty(notes = "Progress Towards narrative narrative", position = 3)
  private String narrative;
  @ApiModelProperty(notes = "Phase - Year ", position = 4)
  private PhaseDTO phase;


  public String getNarrative() {
    return narrative;
  }


  public PhaseDTO getPhase() {
    return phase;
  }

  public SrfSloTargetDTO getSrfSloTarget() {
    return srfSloTarget;
  }

  public void setNarrative(String narrative) {
    this.narrative = narrative;
  }

  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }

  public void setSrfSloTarget(SrfSloTargetDTO srfSloTarget) {
    this.srfSloTarget = srfSloTarget;
  }

}
