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

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class UserAutenticationDTO {

  @ApiModelProperty(notes = "User Identifier", position = 1)
  @NotNull
  public Long id;

  @ApiModelProperty(notes = "First Name", position = 2)
  public String first_name;

  @ApiModelProperty(notes = "Last Name", position = 3)
  public String last_name;

  @ApiModelProperty(notes = "Email", position = 4)
  public String email;

  @ApiModelProperty(notes = "Is authenticated", position = 5)
  public boolean authenticated;

  @ApiModelProperty(notes = "Can the user accept/reject partner requests?", position = 6)
  public boolean canAccessPartnerRequests;

  public boolean getCanAccessPartnerRequests() {
    return canAccessPartnerRequests;
  }

  public String getEmail() {
    return email;
  }

  public String getFirst_name() {
    return first_name;
  }

  public Long getId() {
    return id;
  }

  public String getLast_name() {
    return last_name;
  }

  public boolean isAuthenticated() {
    return authenticated;
  }

  public void setAuthenticated(boolean authenticated) {
    this.authenticated = authenticated;
  }

  public void setCanAccessPartnerRequests(boolean canAccessPartnerRequests) {
    this.canAccessPartnerRequests = canAccessPartnerRequests;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setFirst_name(String first_name) {
    this.first_name = first_name;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setLast_name(String last_name) {
    this.last_name = last_name;
  }

}
