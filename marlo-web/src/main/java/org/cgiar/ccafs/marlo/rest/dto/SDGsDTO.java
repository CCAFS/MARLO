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

public class SDGsDTO {

  @ApiModelProperty(notes = "SDG SMO Code", position = 1)
  @NotNull
  private long smoCode;

  @ApiModelProperty(notes = "SDG Short Name", position = 2)
  private String shortName;

  @ApiModelProperty(notes = "SDG Full Name", position = 3)
  private String fullName;

  public String getFullName() {
    return fullName;
  }

  public String getShortName() {
    return shortName;
  }

  public long getSmoCode() {
    return smoCode;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public void setSmoCode(long smoCode) {
    this.smoCode = smoCode;
  }


}
