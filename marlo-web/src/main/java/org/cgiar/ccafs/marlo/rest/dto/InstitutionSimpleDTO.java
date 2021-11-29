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

import java.util.List;

import io.swagger.annotations.ApiModelProperty;

public class InstitutionSimpleDTO {

  @ApiModelProperty(notes = "The Generated Institution Code", position = 1)
  private Long code;
  @ApiModelProperty(notes = "The Institution Name", position = 2)
  private String name;
  @ApiModelProperty(notes = "The Institution Acronym", position = 3)
  private String acronym;
  @ApiModelProperty(notes = "The Institution Website", position = 4)
  private String websiteLink;
  @ApiModelProperty(notes = "The Institution type Identifier", position = 5)
  private Long institutionTypeId;
  @ApiModelProperty(notes = "The Institution type", position = 6)
  private String institutionType;
  @ApiModelProperty(notes = "HQ location", position = 7)
  private String hqLocation;
  @ApiModelProperty(notes = "HQ location", position = 8)
  private String hqLocationISOalpha2;
  @ApiModelProperty(notes = "Institutions Related List", position = 9)
  private List<InstitutionsRelatedDTO> institutionRelatedList;


  public String getAcronym() {
    return this.acronym;
  }


  public Long getCode() {
    return this.code;
  }


  public String getHqLocation() {
    return hqLocation;
  }


  public String getHqLocationISOalpha2() {
    return hqLocationISOalpha2;
  }


  public List<InstitutionsRelatedDTO> getInstitutionRelatedList() {
    return institutionRelatedList;
  }

  public String getInstitutionType() {
    return institutionType;
  }

  public Long getInstitutionTypeId() {
    return institutionTypeId;
  }


  public String getName() {
    return this.name;
  }


  public String getWebsiteLink() {
    return this.websiteLink;
  }


  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }


  public void setCode(Long code) {
    this.code = code;
  }


  public void setHqLocation(String hqLocation) {
    this.hqLocation = hqLocation;
  }


  public void setHqLocationISOalpha2(String hqLocationISOalpha2) {
    this.hqLocationISOalpha2 = hqLocationISOalpha2;
  }


  public void setInstitutionRelatedList(List<InstitutionsRelatedDTO> institutionRelatedList) {
    this.institutionRelatedList = institutionRelatedList;
  }


  public void setInstitutionType(String institutionType) {
    this.institutionType = institutionType;
  }


  public void setInstitutionTypeId(Long institutionTypeId) {
    this.institutionTypeId = institutionTypeId;
  }


  public void setName(String name) {
    this.name = name;
  }


  public void setWebsiteLink(String websiteLink) {
    this.websiteLink = websiteLink;
  }

}
