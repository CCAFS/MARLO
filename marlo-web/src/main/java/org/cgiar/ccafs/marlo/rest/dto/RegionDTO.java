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

public class RegionDTO {

  @ApiModelProperty(notes = "The ISO Region code")
  private Long UM49Code;

  @ApiModelProperty(notes = "Region Name")
  private String name;

  @ApiModelProperty(notes = "Parent Region")
  private ParentRegionDTO parentRegion;

  public String getName() {
    return this.name;
  }

  public ParentRegionDTO getParentRegion() {
    return this.parentRegion;
  }

  public Long getUM49Code() {
    return this.UM49Code;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setParentRegion(ParentRegionDTO parentRegion) {
    this.parentRegion = parentRegion;
  }

  public void setUM49Code(Long uM49Code) {
    this.UM49Code = uM49Code;
  }

}
