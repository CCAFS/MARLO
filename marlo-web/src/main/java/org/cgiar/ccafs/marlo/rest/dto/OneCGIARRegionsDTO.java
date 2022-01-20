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

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class OneCGIARRegionsDTO {

  @ApiModelProperty(notes = "The Generated Region Type id", position = 1)
  private Long id;

  @ApiModelProperty(notes = "Region name", position = 2)
  private String name;

  @ApiModelProperty(notes = "Region Acronym", position = 3)
  private String acronym;


  @ApiModelProperty(notes = "Region Countries", position = 5)
  private List<CountryDTO> countries;


  public String getAcronym() {
    return acronym;
  }


  public List<CountryDTO> getCountries() {
    return countries;
  }


  public Long getId() {
    return id;
  }


  public String getName() {
    return name;
  }


  /*
   * public OneCGIARRegionTypeDTO getRegionType() {
   * return regionType;
   * }
   */


  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }


  public void setCountries(List<CountryDTO> countries) {
    this.countries = countries;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setName(String name) {
    this.name = name;
  }


  /*
   * public void setRegionType(OneCGIARRegionTypeDTO regionType) {
   * this.regionType = regionType;
   * }
   */


}
