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

import io.swagger.annotations.ApiModelProperty;

public class ProjectPolicyCrpDTO {


  @ApiModelProperty(notes = "CRPs/Platforms identifier", position = 140)
  private CGIAREntityDTO globalUnit;

  @ApiModelProperty(notes = "Phase (AR, POWB, UpKeep)", position = 5)
  private PhaseDTO phase;

  public CGIAREntityDTO getGlobalUnit() {
    return globalUnit;
  }

  public PhaseDTO getPhase() {
    return phase;
  }

  public void setGlobalUnit(CGIAREntityDTO globalUnit) {
    this.globalUnit = globalUnit;
  }

  public void setPhase(PhaseDTO phase) {
    this.phase = phase;
  }


}
