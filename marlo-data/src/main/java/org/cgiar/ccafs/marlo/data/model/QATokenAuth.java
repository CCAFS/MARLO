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

package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public class QATokenAuth extends MarloBaseEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -7505443728447619578L;

  @Expose
  private Date createdAt;

  @Expose
  private Date updatedAt;

  @Expose
  private Long crpId;

  @Expose
  private String token;

  @Expose
  private Date expirationDate;

  @Expose
  private String username;

  @Expose
  private String email;

  @Expose
  private String name;

  @Expose
  private Long appUser;

  public Long getAppUser() {
    return appUser;
  }


  public Date getCreatedAt() {
    return createdAt;
  }


  public Long getCrpId() {
    return crpId;
  }


  public String getEmail() {
    return email;
  }


  public Date getExpirationDate() {
    return expirationDate;
  }


  @Override
  public String getLogDeatil() {
    return null;
  }


  @Override
  public String getModificationJustification() {
    return "";
  }


  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return u;
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


  @Override
  public boolean isActive() {
    return true;
  }


  public void setAppUser(Long appUser) {
    this.appUser = appUser;
  }


  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }


  public void setCrpId(Long crpId) {
    this.crpId = crpId;
  }


  public void setEmail(String email) {
    this.email = email;
  }


  public void setExpirationDate(Date expirationDate) {
    this.expirationDate = expirationDate;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
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
