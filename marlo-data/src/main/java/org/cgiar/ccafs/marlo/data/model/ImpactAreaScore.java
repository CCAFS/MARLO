package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 22, 2018 2:38:07 PM by Hibernate Tools 5.2.5.Final

import com.google.gson.annotations.Expose;

public class ImpactAreaScore extends MarloBaseEntity implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  @Expose
  private String description;
  @Expose
  private String completeDescription;

  public ImpactAreaScore() {
  }

  public String getCompleteDescription() {
    return this.completeDescription;
  }

  public String getDescription() {
    return this.description;
  }

  public void setCompleteDescription(String completeDescription) {
    this.completeDescription = completeDescription;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}