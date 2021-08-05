package org.cgiar.ccafs.marlo.data.model;

import com.google.gson.annotations.Expose;

public class CgiarCrossCuttingMarker extends MarloBaseEntity implements java.io.Serializable {

  private static final long serialVersionUID = 3653055687263122006L;

  @Expose
  private String name;

  public CgiarCrossCuttingMarker() {
  }

  public CgiarCrossCuttingMarker(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setName(String name) {
    this.name = name;
  }


}

