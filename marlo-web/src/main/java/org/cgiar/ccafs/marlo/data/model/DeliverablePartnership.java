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
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverablePartnership implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -9215235396324769222L;


  @Expose
  private Long id;


  @Expose
  private User modifiedBy;


  @Expose
  private User createdBy;

  @Expose
  private ProjectPartnerPerson projectPartnerPerson;


  @Expose
  private Deliverable deliverable;


  @Expose
  private String partnerType;
  @Expose
  private boolean active;


  @Expose
  private Date activeSince;


  @Expose
  private String modificationJustification;


  @Expose
  private PartnerDivision partnerDivision;


  public DeliverablePartnership() {
  }


  public DeliverablePartnership(User modifiedBy, User createdBy, ProjectPartnerPerson projectPartnerPerson,
    Deliverable deliverable, String partnerType, boolean active, Date activeSince, String modificationJustification) {
    this.modifiedBy = modifiedBy;
    this.createdBy = createdBy;
    this.projectPartnerPerson = projectPartnerPerson;
    this.deliverable = deliverable;
    this.partnerType = partnerType;
    this.active = active;
    this.activeSince = activeSince;
    this.modificationJustification = modificationJustification;
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
    DeliverablePartnership other = (DeliverablePartnership) obj;
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


  public Deliverable getDeliverable() {
    return deliverable;
  }


  @Override
  public Long getId() {
    return id;
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


  public PartnerDivision getPartnerDivision() {
    return partnerDivision;
  }


  public String getPartnerType() {
    return partnerType;
  }


  public ProjectPartnerPerson getProjectPartnerPerson() {
    return projectPartnerPerson;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
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


  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
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


  public void setPartnerDivision(PartnerDivision partnerDivision) {
    this.partnerDivision = partnerDivision;
  }


  public void setPartnerType(String partnerType) {
    this.partnerType = partnerType;
  }


  public void setProjectPartnerPerson(ProjectPartnerPerson projectPartnerPerson) {
    this.projectPartnerPerson = projectPartnerPerson;
  }

}
