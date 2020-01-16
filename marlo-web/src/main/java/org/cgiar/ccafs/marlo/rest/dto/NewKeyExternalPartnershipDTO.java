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

public class NewKeyExternalPartnershipDTO {


  @ApiModelProperty(notes = "Partnership / Main area", position = 3)
  private PartnershipMainAreaDTO partnershipMainArea;

  @ApiModelProperty(notes = "Flagship / Module", position = 2)
  private FlagshipProgramDTO flagshipProgram;


  public FlagshipProgramDTO getFlagshipProgram() {
    return flagshipProgram;
  }


  public PartnershipMainAreaDTO getPartnershipMainArea() {
    return partnershipMainArea;
  }


  public void setFlagshipProgram(FlagshipProgramDTO flagshipProgram) {
    this.flagshipProgram = flagshipProgram;
  }


  public void setPartnershipMainArea(PartnershipMainAreaDTO partnershipMainArea) {
    this.partnershipMainArea = partnershipMainArea;
  }


}
