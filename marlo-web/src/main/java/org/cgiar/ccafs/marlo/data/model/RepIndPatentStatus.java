package org.cgiar.ccafs.marlo.data.model;

import com.google.gson.annotations.Expose;

public class RepIndPatentStatus extends MarloBaseEntity implements java.io.Serializable {

  private static final long serialVersionUID = 2819694070212644038L;

  @Expose
  private String name;
  @Expose
  private String description;


  public RepIndPatentStatus() {
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
    return "RepIndPatentStatus [id=" + this.getId() + ", name=" + name + ", description=" + description + "]";
  }


}

