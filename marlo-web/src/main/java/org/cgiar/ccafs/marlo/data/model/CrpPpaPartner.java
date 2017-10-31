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

public class CrpPpaPartner implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 9208364810110651075L;


  @Expose
  private Long id;

  @Expose
  private Institution institution;
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

  private Crp crp;

  private GlobalUnit globalUnit;


  public CrpPpaPartner() {
  }


  public CrpPpaPartner(Institution institution, Crp crp) {
    this.institution = institution;
    this.crp = crp;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    CrpPpaPartner other = (CrpPpaPartner) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }


  public Date getActiveSince() {
    return activeSince;
  }

  public User getCreatedBy() {
    return createdBy;
  }


  public Crp getCrp() {
    return crp;
  }

  public GlobalUnit getGlobalUnit() {
    return globalUnit;
  }

  @Override
  public Long getId() {
    return this.id;
  }

  public Institution getInstitution() {
    return institution;
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

  public void setCrp(Crp crp) {
    this.crp = crp;
  }

  public void setGlobalUnit(GlobalUnit globalUnit) {
    this.globalUnit = globalUnit;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setInstitution(Institution institution) {
    this.institution = institution;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  @Override
  public String toString() {
    return id.toString();
  }

}

