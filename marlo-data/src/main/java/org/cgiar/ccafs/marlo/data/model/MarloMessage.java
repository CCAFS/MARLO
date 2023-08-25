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
// Generated Jun 8, 2016 11:23:28 AM by Hibernate Tools 4.3.1.Final

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * Auditlog generated by hbm2java
 */
public class MarloMessage extends MarloAuditableEntity implements java.io.Serializable {

  private static final long serialVersionUID = -5812698307148791654L;

  @Expose
  private String messageType;
  @Expose
  private String messageValue;
  @Expose
  private User modifiedBy;
  @Expose
  private User createdBy;
  @Expose
  private boolean active;
  @Expose
  private Date activeSince;


  public MarloMessage() {
  }

  @Override
  public Date getActiveSince() {
    return activeSince;
  }

  @Override
  public User getCreatedBy() {
    return createdBy;
  }

  public String getMessageType() {
    return this.messageType;
  }

  public String getMessageValue() {
    return this.messageValue;
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  @Override
  public boolean isActive() {
    return active;
  }


  @Override
  public void setActive(boolean active) {
    this.active = active;
  }


  @Override
  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  @Override
  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }


  public void setMessageValue(String messageValue) {
    this.messageValue = messageValue;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }
}
