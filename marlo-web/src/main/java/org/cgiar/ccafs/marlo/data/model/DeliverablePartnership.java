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

import com.google.gson.annotations.Expose;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverablePartnership extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -9215235396324769222L;

  @Expose
  private ProjectPartnerPerson projectPartnerPerson;

  @Expose
  private Deliverable deliverable;


  @Expose
  private Phase phase;
  @Expose
  private String partnerType;

  @Expose
  private PartnerDivision partnerDivision;

  @Expose
  private ProjectPartner projectPartner;


  public DeliverablePartnership() {
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
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public String getComposedName() {
    return this.getProjectPartnerPerson().getComposedCompleteName();
  }

  public Deliverable getDeliverable() {
    return deliverable;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public PartnerDivision getPartnerDivision() {
    return partnerDivision;
  }

  public String getPartnerType() {
    return partnerType;
  }


  public Phase getPhase() {
    return phase;
  }


  public ProjectPartner getProjectPartner() {
    return projectPartner;
  }


  public ProjectPartnerPerson getProjectPartnerPerson() {
    return projectPartnerPerson;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }


  public void setPartnerDivision(PartnerDivision partnerDivision) {
    this.partnerDivision = partnerDivision;
  }

  public void setPartnerType(String partnerType) {
    this.partnerType = partnerType;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setProjectPartner(ProjectPartner projectPartner) {
    this.projectPartner = projectPartner;
  }


  public void setProjectPartnerPerson(ProjectPartnerPerson projectPartnerPerson) {
    this.projectPartnerPerson = projectPartnerPerson;
  }


  @Override
  public String toString() {
    return "DeliverablePartnership [id=" + this.getId() + ", deliverable=" + deliverable + ", partnerType="
      + partnerType + ", partnerDivision=" + partnerDivision + "]";
  }

}
