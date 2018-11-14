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

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class LocElementDTO {


  @ApiModelProperty(notes = "The Generated SRF IDO ID")
  private Long id;


  @ApiModelProperty(notes = "The Generated SRF IDO ID")
  private String isoAlpha2;


  @ApiModelProperty(notes = "The Generated SRF IDO ID")
  private String name;


  public Long getId() {
    return id;
  }


  public String getIsoAlpha2() {
    return isoAlpha2;
  }


  public String getName() {
    return name;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setIsoAlpha2(String isoAlpha2) {
    this.isoAlpha2 = isoAlpha2;
  }

  public void setName(String name) {
    this.name = name;
  }


}
