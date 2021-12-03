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

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

public class InstitutionDTO {

  @ApiModelProperty(notes = "The Generated Institution Code")
  private Long code;
  @ApiModelProperty(notes = "The Institution Name")
  @NotNull
  private String name;
  @ApiModelProperty(notes = "The Institution Acronym")
  private String acronym;
  @ApiModelProperty(notes = "The Institution Website")
  private String websiteLink;

  @ApiModelProperty(notes = "The date that was added the Institution")
  private Date added;

  @ApiModelProperty(notes = "The Institution type")
  @NotNull
  private InstitutionTypeDTO institutionType;
  @NotEmpty
  @ApiModelProperty(notes = "List of countries where are offices")
  private List<CountryOfficeDTO> countryOfficeDTO;


  public String getAcronym() {
    return this.acronym;
  }

  public Date getAdded() {
    return this.added;
  }

  public Long getCode() {
    return this.code;
  }


  public List<CountryOfficeDTO> getCountryOfficeDTO() {
    return this.countryOfficeDTO;
  }

  public InstitutionTypeDTO getInstitutionType() {
    return this.institutionType;
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


  public void setAdded(Date added) {
    this.added = added;
  }


  public void setCode(Long code) {
    this.code = code;
  }

  public void setCountryOfficeDTO(List<CountryOfficeDTO> countriesDTOs) {
    this.countryOfficeDTO = countriesDTOs;
  }

  public void setInstitutionType(InstitutionTypeDTO institutionType) {
    this.institutionType = institutionType;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setWebsiteLink(String websiteLink) {
    this.websiteLink = websiteLink;
  }

}
