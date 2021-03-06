package org.cgiar.ccafs.marlo.data.model;
// default package
// Generated Jun 6, 2017 2:29:49 PM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * CapdevLocations generated by hbm2java
 */
public class CapdevLocations extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Expose
  private LocElement locElement;

  @Expose
  private CapacityDevelopment capacityDevelopment;

  public CapdevLocations() {
  }


  public CapacityDevelopment getCapacityDevelopment() {
    return this.capacityDevelopment;
  }


  public LocElement getLocElement() {
    return this.locElement;
  }


  @Override
  public String getLogDeatil() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public void setCapacityDevelopment(CapacityDevelopment capacityDevelopment) {
    this.capacityDevelopment = capacityDevelopment;
  }

  public void setLocElement(LocElement locElement) {
    this.locElement = locElement;
  }


}

