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

/**
 * 
 */
package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * Modified by @author nmatovu last on Sep 29, 2016
 */
public class CenterUser implements IAuditLog {


  private static final long serialVersionUID = 3078405268131406743L;


  @Expose
  private Long id;

  @Expose
  private Center researchCenter;

  @Expose
  private GlobalUnit globalUnit;
  @Expose
  private User user;
  @Expose
  private boolean active;
  @Expose
  private User createdBy;

  @Expose
  private Date activeSince;

  @Expose
  private User modifiedBy;

  @Expose
  private String modificationJustification;

  public CenterUser() {
  }

  public CenterUser(Center researchCenter, User users) {
    this.researchCenter = researchCenter;
    this.user = users;
  }

  public Date getActiveSince() {
    return activeSince;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public GlobalUnit getGlobalUnit() {
    return globalUnit;
  }

  @Override
  public Long getId() {
    return this.id;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  @Override
  public String getModificationJustification() {
    return modificationJustification;
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  public Center getResearchCenter() {
    return this.researchCenter;
  }

  public User getUser() {
    return this.user;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setGlobalUnit(GlobalUnit globalUnit) {
    this.globalUnit = globalUnit;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setResearchCenter(Center researchCenter) {
    this.researchCenter = researchCenter;
  }

  public void setUser(User users) {
    this.user = users;
  }

  @Override
  public String toString() {
    return id.toString();
  }

}
