package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 30, 2019 10:06:29 AM by Hibernate Tools 5.3.6.Final

import com.google.gson.annotations.Expose;

public class GeneralAcronym extends MarloAuditableEntity implements java.io.Serializable {

  private static final long serialVersionUID = -5138885788171559952L;
  @Expose
  private String Acronym;

  @Expose
  private String Description;

  public String getAcronym() {
    return this.Acronym;
  }

  public String getDescription() {
    return this.Description;
  }

  public void setAcronym(String acronym) {
    this.Acronym = acronym;
  }

  public void setDescription(String description) {
    this.Description = description;
  }

}

