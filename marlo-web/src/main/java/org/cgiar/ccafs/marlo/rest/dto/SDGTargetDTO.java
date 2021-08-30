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

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class SDGTargetDTO {

  @ApiModelProperty(notes = "SDG target Code", position = 1)
  @NotNull
  private Long id;

  @ApiModelProperty(notes = "SDG target Code", position = 2)
  private String sdgTargetCode;

  @ApiModelProperty(notes = "SDG target Description", position = 3)
  private String sdgTarget;

  @ApiModelProperty(notes = "SDG identifier", position = 4)
  private SDGsDTO sdg;


  public Long getId() {
    return id;
  }


  public SDGsDTO getSdg() {
    return sdg;
  }


  public String getSdgTarget() {
    return sdgTarget;
  }


  public String getSdgTargetCode() {
    return sdgTargetCode;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setSdg(SDGsDTO sdg) {
    this.sdg = sdg;
  }


  public void setSdgTarget(String sdgTarget) {
    this.sdgTarget = sdgTarget;
  }


  public void setSdgTargetCode(String sdgTargetCode) {
    this.sdgTargetCode = sdgTargetCode;
  }
}
