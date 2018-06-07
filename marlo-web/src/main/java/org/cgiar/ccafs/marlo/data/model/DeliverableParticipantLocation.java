package org.cgiar.ccafs.marlo.data.model;


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class DeliverableParticipantLocation extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -5492622285870637617L;

  @Expose
  private DeliverableParticipant deliverableParticipant;
  @Expose
  private LocElement locElement;

  public DeliverableParticipantLocation() {
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
    DeliverableParticipantLocation other = (DeliverableParticipantLocation) obj;
    if (locElement == null) {
      if (other.locElement != null) {
        return false;
      }
    } else if (!locElement.getId().equals(other.locElement.getId())) {
      return false;
    }
    return true;
  }


  public DeliverableParticipant getDeliverableParticipant() {
    return deliverableParticipant;
  }


  public LocElement getLocElement() {
    return locElement;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public void setDeliverableParticipant(DeliverableParticipant deliverableParticipant) {
    this.deliverableParticipant = deliverableParticipant;
  }

  public void setLocElement(LocElement locElement) {
    this.locElement = locElement;
  }


  @Override
  public String toString() {
    return "DeliverableParticipantLocation [id=" + this.getId() + ", deliverableParticipant=" + deliverableParticipant
      + ", locElement=" + locElement + ", active=" + this.isActive() + "]";
  }


}

