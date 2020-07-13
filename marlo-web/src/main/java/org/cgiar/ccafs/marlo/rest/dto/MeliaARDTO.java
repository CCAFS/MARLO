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

public class MeliaARDTO {

  @ApiModelProperty(notes = "The Generated expected study id", position = 1)
  private Long id;

  @ApiModelProperty(notes = "Project ID", position = 2)
  private String projectID;


  @ApiModelProperty(notes = "Project expected studies other info", position = 2)
  private MeliaInfoDTO meliaInfo;

  @ApiModelProperty(notes = "CGIAR Sub-IDOs List", position = 3)
  private List<DefaultFieldPrimaryDTO> srfSubIdoList;


  @ApiModelProperty(notes = "Geographic scope code list", position = 3)
  private List<DefaultFieldDTO> geographicScopes;

  @ApiModelProperty(notes = "Country ISO code list", position = 4)
  private List<DefaultFieldDTO> countries;

  @ApiModelProperty(notes = "Region AN94 code list", position = 5)
  private List<DefaultFieldDTO> regions;

  @ApiModelProperty(notes = "CGIAR SLO target code list", position = 8)
  private List<DefaultFieldDTO> srfSloTargetList;


  public List<DefaultFieldDTO> getCountries() {
    return countries;
  }


  public List<DefaultFieldDTO> getGeographicScopes() {
    return geographicScopes;
  }


  public Long getId() {
    return id;
  }


  public MeliaInfoDTO getMeliaInfo() {
    return meliaInfo;
  }


  public String getProjectID() {
    return projectID;
  }


  public List<DefaultFieldDTO> getRegions() {
    return regions;
  }


  public List<DefaultFieldDTO> getSrfSloTargetList() {
    return srfSloTargetList;
  }


  public List<DefaultFieldPrimaryDTO> getSrfSubIdoList() {
    return srfSubIdoList;
  }


  public void setCountries(List<DefaultFieldDTO> countries) {
    this.countries = countries;
  }


  public void setGeographicScopes(List<DefaultFieldDTO> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setMeliaInfo(MeliaInfoDTO meliaInfo) {
    this.meliaInfo = meliaInfo;
  }


  public void setProjectID(String projectID) {
    this.projectID = projectID;
  }


  public void setRegions(List<DefaultFieldDTO> regions) {
    this.regions = regions;
  }


  public void setSrfSloTargetList(List<DefaultFieldDTO> srfSloTargetList) {
    this.srfSloTargetList = srfSloTargetList;
  }


  public void setSrfSubIdoList(List<DefaultFieldPrimaryDTO> srfSubIdoList) {
    this.srfSubIdoList = srfSubIdoList;
  }
}
