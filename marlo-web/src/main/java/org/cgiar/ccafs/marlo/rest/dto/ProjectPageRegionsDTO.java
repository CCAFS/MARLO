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

public class ProjectPageRegionsDTO {

  @ApiModelProperty(notes = "ID Region")
  @NotNull
  private Long UM49code;


  @ApiModelProperty(notes = "Region Name")
  @NotNull
  private String name;


  public String getName() {
    return name;
  }


  public Long getUM49code() {
    return UM49code;
  }


  public void setName(String name) {
    this.name = name;
  }


  public void setUM49code(Long uM49code) {
    UM49code = uM49code;
  }

}
