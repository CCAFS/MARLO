package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 28, 2019 11:12:18 AM by Hibernate Tools 5.3.6.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class GeneralStatus extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 5297413785337764324L;
  @Expose
  private String name;
  @Expose
  private Long iatiEquivalence;


  public GeneralStatus() {
  }

  public Long getIatiEquivalence() {
    return iatiEquivalence;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public String getName() {
    return name;
  }


  public void setIatiEquivalence(Long iatiEquivalence) {
    this.iatiEquivalence = iatiEquivalence;
  }


  public void setName(String name) {
    this.name = name;
  }


}

