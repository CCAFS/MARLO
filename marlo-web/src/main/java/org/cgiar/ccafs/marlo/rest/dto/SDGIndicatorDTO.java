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

public class SDGIndicatorDTO {

  @ApiModelProperty(notes = "SDG Indicator Code", position = 1)
  @NotNull
  private Long id;

  @ApiModelProperty(notes = "UNSD Indicator Code", position = 2)
  private String unsdIndicatorCode;

  @ApiModelProperty(notes = "Indicator Code", position = 3)
  private String indicatorCode;

  @ApiModelProperty(notes = "Indicator Name", position = 4)
  private String indicatorName;

  @ApiModelProperty(notes = "SDG target identifier", position = 5)
  private SDGTargetDTO sdgTarget;


  public Long getId() {
    return id;
  }


  public String getIndicatorCode() {
    return indicatorCode;
  }


  public String getIndicatorName() {
    return indicatorName;
  }


  public SDGTargetDTO getSdgTarget() {
    return sdgTarget;
  }


  public String getUnsdIndicatorCode() {
    return unsdIndicatorCode;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setIndicatorCode(String indicatorCode) {
    this.indicatorCode = indicatorCode;
  }


  public void setIndicatorName(String indicatorName) {
    this.indicatorName = indicatorName;
  }


  public void setSdgTarget(SDGTargetDTO sdgTarget) {
    this.sdgTarget = sdgTarget;
  }


  public void setUnsdIndicatorCode(String unsdIndicatorCode) {
    this.unsdIndicatorCode = unsdIndicatorCode;
  }

}
