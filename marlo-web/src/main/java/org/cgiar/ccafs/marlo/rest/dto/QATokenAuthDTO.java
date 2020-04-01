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

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public class QATokenAuthDTO {

  @ApiModelProperty(notes = "QA Token id", position = 1)
  private Long id;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date updatedAt;

  @ApiModelProperty(notes = "Crp id", position = 2)
  private String crpId;

  @ApiModelProperty(notes = "Token", position = 3)
  private String token;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private Date expirationDate;

  @ApiModelProperty(notes = "Username", position = 4)
  private String username;

  @ApiModelProperty(notes = "Email", position = 5)
  private String email;

  @ApiModelProperty(notes = "Name", position = 6)
  private String name;

  @ApiModelProperty(notes = "App user", position = 7)
  private Long appUser;


  public Long getAppUser() {
    return appUser;
  }


  public Date getCreatedAt() {
    return createdAt;
  }


  public String getCrpId() {
    return crpId;
  }


  public String getEmail() {
    return email;
  }


  public Date getExpirationDate() {
    return expirationDate;
  }


  public Long getId() {
    return id;
  }


  public String getName() {
    return name;
  }


  public String getToken() {
    return token;
  }


  public Date getUpdatedAt() {
    return updatedAt;
  }


  public String getUsername() {
    return username;
  }


  public void setAppUser(Long appUser) {
    this.appUser = appUser;
  }


  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }


  public void setCrpId(String crpId) {
    this.crpId = crpId;
  }


  public void setEmail(String email) {
    this.email = email;
  }


  public void setExpirationDate(Date expirationDate) {
    this.expirationDate = expirationDate;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setName(String name) {
    this.name = name;
  }


  public void setToken(String token) {
    this.token = token;
  }


  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }


  public void setUsername(String username) {
    this.username = username;
  }


}
