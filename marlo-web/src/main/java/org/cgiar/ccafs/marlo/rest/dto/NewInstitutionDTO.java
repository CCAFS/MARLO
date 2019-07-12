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

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;


@ApiModel(description = "DTO to send a new partner request to CLARISA")
public class NewInstitutionDTO {

  @NotNull
  @NotBlank
  @ApiModelProperty(notes = "The Institution Name", required = true, position = 1)
  private String name;

  @ApiModelProperty(notes = "The Institution Acronym", position = 2)
  private String acronym;

  @ApiModelProperty(notes = "The Institution Website", position = 3)
  private String websiteLink;

  @NotNull
  @ApiModelProperty(notes = "The Institution type code", required = true, position = 4)
  private String institutionTypeCode;

  @NotNull
  @ApiModelProperty(notes = "Headquarter country iso Alpha 2 ", required = true, position = 5)
  private String hqCountryIso;

  @NotNull
  @NotBlank
  @Email
  @ApiModelProperty(notes = "Mail of the external user who is requesting the institution", required = true,
    position = 6)
  private String externalUserMail;

  @ApiModelProperty(notes = "Name of the external user who is requesting the institution", position = 7)
  private String externalUserName;

  @ApiModelProperty(notes = "comments from the external user", position = 8)
  private String externalUserComments;

  public String getAcronym() {
    return this.acronym;
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


  public String getHqCountryIso() {
    return this.hqCountryIso;
  }

  public String getInstitutionTypeCode() {
    return this.institutionTypeCode;
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

  public void setExternalUserComments(String externalUserComments) {
    this.externalUserComments = externalUserComments;
  }


  public void setExternalUserMail(String externalUserMail) {
    this.externalUserMail = externalUserMail;
  }

  public void setExternalUserName(String externalUserName) {
    this.externalUserName = externalUserName;
  }


  public void setHqCountryIso(String hqCountryIso) {
    this.hqCountryIso = hqCountryIso;
  }


  public void setInstitutionTypeCode(String institutionTypeCode) {
    this.institutionTypeCode = institutionTypeCode;
  }


  public void setName(String name) {
    this.name = name;
  }


  public void setWebsiteLink(String websiteLink) {
    this.websiteLink = websiteLink;
  }

}
