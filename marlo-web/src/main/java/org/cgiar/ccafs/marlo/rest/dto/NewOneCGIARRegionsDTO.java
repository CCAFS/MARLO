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

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class NewOneCGIARRegionsDTO {

  @ApiModelProperty(notes = "The Generated Region Type id", position = 1)
  private Long isoNumeric;

  @ApiModelProperty(notes = "Region name", position = 2)
  private String name;

  @ApiModelProperty(notes = "Region Acronym", position = 3)
  private String acronym;


  public String getAcronym() {
    return acronym;
  }

  public Long getIsoNumeric() {
    return isoNumeric;
  }

  public String getName() {
    return name;
  }


  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setIsoNumeric(Long isoNumeric) {
    this.isoNumeric = isoNumeric;
  }

  public void setName(String name) {
    this.name = name;
  }
}
