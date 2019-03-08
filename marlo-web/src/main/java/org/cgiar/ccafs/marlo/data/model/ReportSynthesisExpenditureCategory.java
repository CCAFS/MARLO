package org.cgiar.ccafs.marlo.data.model;
// Generated Feb 5, 2018 11:32:15 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;


public class ReportSynthesisExpenditureCategory extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -9128609415353939198L;

  @Expose
  private String name;
  @Expose
  private String description;

  public ReportSynthesisExpenditureCategory() {
  }


  public String getDescription() {
    return description;
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


  public void setDescription(String description) {
    this.description = description;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "ReportSynthesisExpenditureCategory [Id=" + this.getId() + ", isActive=" + this.isActive() + ", name=" + name
      + ", description=" + description + "]";
  }
}

