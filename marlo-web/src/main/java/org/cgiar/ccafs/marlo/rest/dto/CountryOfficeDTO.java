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
 * @author Mralmanzar - CIAT/CCAFS
 */
public class CountryOfficeDTO {

  @ApiModelProperty(notes = "The ISO country code")
  private Long code;

  @ApiModelProperty(notes = "The ISO Alpha 2 letters code")
  private String isoAlpha2;

  @ApiModelProperty(notes = "Country Name")
  private String name;

  @ApiModelProperty(notes = "Is the headquarter Office Country")
  private String isHeadquarter;

  @ApiModelProperty(notes = "Region")
  private RegionDTO regionDTO;

  public Long getCode() {
    return this.code;
  }

  public String getIsoAlpha2() {
    return this.isoAlpha2;
  }

  public String getName() {
    return this.name;
  }

  public RegionDTO getRegionDTO() {
    return this.regionDTO;
  }

  public void setCode(Long code) {
    this.code = code;
  }

  public void setIsoAlpha2(String isoAlpha2) {
    this.isoAlpha2 = isoAlpha2;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRegionDTO(RegionDTO regionDTO) {
    this.regionDTO = regionDTO;
  }

  public String getIsHeadquarter() {
    return isHeadquarter;
  }

  public void setIsHeadquarter(String isHeadquarter) {
    this.isHeadquarter = isHeadquarter;
  }

}
