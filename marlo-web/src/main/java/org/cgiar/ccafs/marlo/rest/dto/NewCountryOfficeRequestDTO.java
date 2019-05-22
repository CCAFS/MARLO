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

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

public class NewCountryOfficeRequestDTO {

  @ApiModelProperty(notes = "Institution code")
  @NotNull
  private Long institutionCode;

  @ApiModelProperty(notes = "Country Alpha iso code of Office")
  @NotNull
  private String countryIso;

  @ApiModelProperty(notes = "Mail of the external user who is requesting the Office Location", position = 6)
  @NotNull
  private String externalUserMail;
  @ApiModelProperty(notes = "Name of the external user who is requesting the  Office Location", position = 7)
  private String externalUserName;
  @ApiModelProperty(notes = "Comments from the external user", position = 8)
  private String externalUserComments;

  public String getCountryIso() {
    return this.countryIso;
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

  public Long getInstitutionCode() {
    return this.institutionCode;
  }

  public void setCountryIso(String countryIso) {
    this.countryIso = countryIso;
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

  public void setInstitutionCode(Long institutionCode) {
    this.institutionCode = institutionCode;
  }


}
