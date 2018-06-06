package org.cgiar.ccafs.marlo.data.model;

import com.google.gson.annotations.Expose;

public class RepIndFillingType extends MarloBaseEntity implements java.io.Serializable {

  private static final long serialVersionUID = -8684565312361702239L;

  @Expose
  private String name;
  @Expose
  private String description;


  public RepIndFillingType() {
  }


  public String getDescription() {
    return description;
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
    return "RepIndFillingType [id=" + this.getId() + ", name=" + name + ", description=" + description + "]";
  }


}

