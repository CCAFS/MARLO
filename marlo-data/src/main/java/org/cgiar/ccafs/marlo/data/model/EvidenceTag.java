package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 30, 2019 10:06:29 AM by Hibernate Tools 5.3.6.Final

import com.google.gson.annotations.Expose;

public class EvidenceTag extends MarloBaseEntity implements java.io.Serializable {

  private static final long serialVersionUID = -5138885788171559952L;
  @Expose
  private String name;

  public EvidenceTag() {
  }


  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


}

