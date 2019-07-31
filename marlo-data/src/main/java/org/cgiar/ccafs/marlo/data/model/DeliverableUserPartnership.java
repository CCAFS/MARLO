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
public class DeliverableUserPartnership extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 317194942715084413L;

  @Expose
  private User user;
  @Expose
  private Deliverable deliverable;
  @Expose
  private Phase phase;
  @Expose
  private DeliverablePartnerType deliverablePartnerType;
  @Expose
  private PartnerDivision partnerDivision;
  @Expose
  private Institution institution;


  public DeliverableUserPartnership() {
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
    DeliverableUserPartnership other = (DeliverableUserPartnership) obj;
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
    if (this.getUser() != null) {
      return this.getUser().getComposedCompleteName();
    } else {
      return "";
    }
  }


  public Deliverable getDeliverable() {
    return deliverable;
  }

  public DeliverablePartnerType getDeliverablePartnerType() {
    return deliverablePartnerType;
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

  public PartnerDivision getPartnerDivision() {
    return partnerDivision;
  }

  public Phase getPhase() {
    return phase;
  }


  public User getUser() {
    return user;
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

  public void setDeliverablePartnerType(DeliverablePartnerType deliverablePartnerType) {
    this.deliverablePartnerType = deliverablePartnerType;
  }


  public void setInstitution(Institution institution) {
    this.institution = institution;
  }


  public void setPartnerDivision(PartnerDivision partnerDivision) {
    this.partnerDivision = partnerDivision;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setUser(User user) {
    this.user = user;
  }


  @Override
  public String toString() {
    return "DeliverableUserPartnership [id=" + this.getId() + ", deliverable=" + deliverable + ", partnerDivision="
      + partnerDivision + "]";
  }

}
