package org.cgiar.ccafs.marlo.data.model;

import com.google.gson.annotations.Expose;

public class DeliverablePartnerType extends MarloBaseEntity implements java.io.Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 4377835325344967449L;
  @Expose
  private String name;


  public DeliverablePartnerType() {
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "DeliverablePartnerType [id=" + this.getId() + ", name=" + name + "]";
  }


}

