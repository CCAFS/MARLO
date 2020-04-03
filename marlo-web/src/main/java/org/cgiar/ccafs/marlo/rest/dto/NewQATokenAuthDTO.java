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
 * @author Luis Benavides - CIAT/CCAFS
 */
public class NewQATokenAuthDTO {

  @ApiModelProperty(notes = "Crp id value of Smo code", position = 2)
  private String smocode;

  @ApiModelProperty(notes = "Username", position = 4)
  private String username;

  @ApiModelProperty(notes = "Email", position = 5)
  private String email;

  @ApiModelProperty(notes = "Name", position = 6)
  private String name;


  public String getEmail() {
    return email;
  }


  public String getName() {
    return name;
  }


  public String getSmocode() {
    return smocode;
  }


  public String getUsername() {
    return username;
  }


  public void setEmail(String email) {
    this.email = email;
  }


  public void setName(String name) {
    this.name = name;
  }


  public void setSmocode(String smocode) {
    this.smocode = smocode;
  }


  public void setUsername(String username) {
    this.username = username;
  }


}
