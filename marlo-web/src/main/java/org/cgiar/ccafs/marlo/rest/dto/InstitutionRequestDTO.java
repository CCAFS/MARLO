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
public class InstitutionRequestDTO {

  @ApiModelProperty(notes = "The id of the Partner Request", position = 1)
  private Long id;

  @ApiModelProperty(notes = "Name of institution", position = 2)
  private String partnerName;

  @ApiModelProperty(notes = "Acronym of institution", position = 3)
  private String acronym;
  @ApiModelProperty(notes = "Web Page of institution", position = 4)
  private String webPage;

  @ApiModelProperty(notes = "Partner Request Status", position = 5)
  private String requestStatus;

  @ApiModelProperty(notes = "Reject justification", position = 6)
  private String requestJustification;

  @ApiModelProperty(notes = "Country of partner request headquarter", position = 7)
  private CountryDTO countryDTO;

  @ApiModelProperty(notes = "Institution type", position = 8)
  private InstitutionTypeDTO institutionTypeDTO;

  @ApiModelProperty(notes = "Intitution created", position = 9)
  private InstitutionDTO institutionDTO;

  @ApiModelProperty(notes = "External user mail", position = 10)
  private String externalUserMail;

  @ApiModelProperty(notes = "External user name", position = 11)
  private String externalUserName;

  @ApiModelProperty(notes = "External user comments", position = 12)
  private String externalUserComments;

  public String getAcronym() {
    return this.acronym;
  }

  public CountryDTO getCountryDTO() {
    return this.countryDTO;
  }

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

  public InstitutionTypeDTO getInstitutionTypeDTO() {
    return this.institutionTypeDTO;
  }


  public String getPartnerName() {
    return this.partnerName;
  }


  public String getRequestJustification() {
    return this.requestJustification;
  }


  public String getRequestStatus() {
    return this.requestStatus;
  }

  public String getWebPage() {
    return this.webPage;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setCountryDTO(CountryDTO countryDTO) {
    this.countryDTO = countryDTO;
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

  public void setInstitutionTypeDTO(InstitutionTypeDTO institutionTypeDTO) {
    this.institutionTypeDTO = institutionTypeDTO;
  }

  public void setPartnerName(String partnerName) {
    this.partnerName = partnerName;
  }


  public void setRequestJustification(String requestJustification) {
    this.requestJustification = requestJustification;
  }

  public void setRequestStatus(String requestStatus) {
    this.requestStatus = requestStatus;
  }

  public void setWebPage(String webPage) {
    this.webPage = webPage;
  }

}
