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
 * @author Manuel almanzar - CIAT/CCAFS
 */
public class CountryOfficeRequestDTO {

  @ApiModelProperty(notes = "The id of the country office request", position = 1)
  private Long id;

  @ApiModelProperty(notes = "Intitution", position = 9)
  InstitutionDTO institutionDTO;

  @ApiModelProperty(notes = "Country office request status", position = 5)
  private Boolean isAcepted;

  @ApiModelProperty(notes = "Reject justification", position = 6)
  private String rejectJustification;

  @ApiModelProperty(notes = "Country of office request", position = 7)
  private CountryDTO countryDTO;

  @ApiModelProperty(notes = "External user mail", position = 10)
  private String externalUserMail;

  @ApiModelProperty(notes = "External user name", position = 11)
  private String externalUserName;

  @ApiModelProperty(notes = "External user comments", position = 12)
  private String externalUserComments;


  public String getExternalUserComments() {
    return this.externalUserComments;
  }


  public String getExternalUserMail() {
    return this.externalUserMail;
  }


  public String getExternalUserName() {
    return this.externalUserName;
  }


  public Long getId() {
    return this.id;
  }


  public InstitutionDTO getInstitutionDTO() {
    return this.institutionDTO;
  }

  public Boolean getIsAcepted() {
    return this.isAcepted;
  }


  public String getRejectJustification() {
    return this.rejectJustification;
  }


  public void setExternalUserComments(String externalUserComments) {
    this.externalUserComments = externalUserComments;
  }


  public void setExternalUserMail(String externalUserMail) {
    this.externalUserMail = externalUserMail;
  }


  public void setExternalUserName(String externalUserName) {
    this.externalUserName = externalUserName;
  }


  public void setId(Long id) {
    this.id = id;
  }

  public void setInstitutionDTO(InstitutionDTO institutionDTO) {
    this.institutionDTO = institutionDTO;
  }


  public void setIsAcepted(Boolean isAcepted) {
    this.isAcepted = isAcepted;
  }


  public void setRejectJustification(String rejectJustification) {
    this.rejectJustification = rejectJustification;
  }


  public CountryDTO getCountryDTO() {
    return countryDTO;
  }


  public void setCountryDTO(CountryDTO countryDTO) {
    this.countryDTO = countryDTO;
  }


}
