package org.cgiar.ccafs.marlo.data.model;
// Generated Oct 30, 2017 10:01:51 AM by Hibernate Tools 3.4.0.CR1


import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * @author Andres Valencia
 */
public class DeliverableRule extends MarloBaseEntity implements java.io.Serializable {

  private static final long serialVersionUID = -2007066958617057527L;

  @Expose
  private String name;
  @Expose
  private String description;

  private Set<DeliverableTypeRule> deliverableTypeRules = new HashSet<DeliverableTypeRule>(0);


  public DeliverableRule() {
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


  public Set<DeliverableTypeRule> getDeliverableTypeRules() {
    return deliverableTypeRules;
  }


  public String getDescription() {
    return description;
  }


  public String getName() {
    return name;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public void setDeliverableTypeRules(Set<DeliverableTypeRule> deliverableTypeRules) {
    this.deliverableTypeRules = deliverableTypeRules;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public void setName(String name) {
    this.name = name;
  }


  @Override
  public String toString() {
    return "DeliverableRules [id=" + this.getId() + ", name=" + name + ", description=" + description + "]";
  }


}

