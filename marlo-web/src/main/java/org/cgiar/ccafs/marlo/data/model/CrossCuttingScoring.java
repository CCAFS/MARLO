package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 22, 2018 2:38:07 PM by Hibernate Tools 5.2.5.Final

import com.google.gson.annotations.Expose;


public class CrossCuttingScoring extends MarloBaseEntity implements java.io.Serializable {


  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Expose
  private String description;
  @Expose
  private String completeDescription;
  private GlobalUnit crp;

  public CrossCuttingScoring() {
  }

  public String getCompleteDescription() {
    return this.completeDescription;
  }

  public GlobalUnit getCrp() {
    return crp;
  }

  public String getDescription() {
    return this.description;
  }

  public void setCompleteDescription(String completeDescription) {
    this.completeDescription = completeDescription;
  }

  public void setCrp(GlobalUnit crp) {
    this.crp = crp;
  }


  public void setDescription(String description) {
    this.description = description;
  }

}

