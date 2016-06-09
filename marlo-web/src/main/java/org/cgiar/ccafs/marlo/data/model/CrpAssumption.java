/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.data.model;
// Generated May 26, 2016 9:42:28 AM by Hibernate Tools 4.3.1.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * CrpAssumption generated by hbm2java
 */
public class CrpAssumption implements java.io.Serializable, IAuditLog {

  /**
   * 
   */
  private static final long serialVersionUID = 5392601449095678314L;

  private @Expose Long id;


  private CrpOutcomeSubIdo crpOutcomeSubIdo;


  private @Expose String description;


  private @Expose boolean active;


  private @Expose User createdBy;


  private @Expose Date activeSince;


  private @Expose User modifiedBy;


  private @Expose String modificationJustification;

  public CrpAssumption() {
  }

  public CrpAssumption(CrpOutcomeSubIdo crpOutcomeSubIdo, String description) {
    this.crpOutcomeSubIdo = crpOutcomeSubIdo;
    this.description = description;
  }

  public Date getActiveSince() {
    return activeSince;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public CrpOutcomeSubIdo getCrpOutcomeSubIdo() {
    return this.crpOutcomeSubIdo;
  }

  public String getDescription() {
    return this.description;
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

  public String getModificationJustification() {
    return modificationJustification;
  }

  public User getModifiedBy() {
    return modifiedBy;
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

  public void setCrpOutcomeSubIdo(CrpOutcomeSubIdo crpOutcomeSubIdo) {
    this.crpOutcomeSubIdo = crpOutcomeSubIdo;
  }

  public void setDescription(String description) {
    this.description = description;
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


}

