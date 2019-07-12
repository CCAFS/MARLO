package org.cgiar.ccafs.marlo.data.model;
// Generated Oct 30, 2017 10:01:51 AM by Hibernate Tools 3.4.0.CR1


import com.google.gson.annotations.Expose;

/**
 * @author Andres Valencia
 */
public class DeliverableTypeRule extends MarloBaseEntity implements java.io.Serializable {

  private static final long serialVersionUID = -2007066958617057527L;

  @Expose
  private DeliverableType deliverableType;
  @Expose
  private DeliverableRule deliverableRule;


  public DeliverableTypeRule() {
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    Project other = (Project) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public DeliverableRule getDeliverableRule() {
    return deliverableRule;
  }


  public DeliverableType getDeliverableType() {
    return deliverableType;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public void setDeliverableRule(DeliverableRule deliverableRule) {
    this.deliverableRule = deliverableRule;
  }


  public void setDeliverableType(DeliverableType deliverableType) {
    this.deliverableType = deliverableType;
  }


  @Override
  public String toString() {
    return "DeliverableTypeRule [id=" + this.getId() + ", deliverableType=" + deliverableType + ", deliverableRule="
      + deliverableRule + "]";
  }

}

