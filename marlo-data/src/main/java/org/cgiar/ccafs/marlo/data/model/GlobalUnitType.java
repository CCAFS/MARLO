package org.cgiar.ccafs.marlo.data.model;
// Generated Oct 30, 2017 10:01:51 AM by Hibernate Tools 3.4.0.CR1


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * @author Hermes Jimenez
 */
public class GlobalUnitType extends MarloAuditableEntity implements java.io.Serializable {


  private static final long serialVersionUID = 1393262857086220239L;

  @Expose
  private String name;

  private Set<GlobalUnit> globalUnits = new HashSet<GlobalUnit>(0);
  private List<GlobalUnit> globalUnitsList;
  private boolean visible;


  private Set<Parameter> parameters = new HashSet<Parameter>(0);


  public GlobalUnitType() {
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    GlobalUnitType other = (GlobalUnitType) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public Set<GlobalUnit> getGlobalUnits() {
    return globalUnits;
  }

  public List<GlobalUnit> getGlobalUnitsList() {
    return globalUnitsList;
  }

  public String getName() {
    return name;
  }

  public Set<Parameter> getParameters() {
    return parameters;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public boolean isVisible() {
    return visible;
  }

  public void setGlobalUnits(Set<GlobalUnit> globalUnits) {
    this.globalUnits = globalUnits;
  }

  public void setGlobalUnitsList(List<GlobalUnit> globalUnitsList) {
    this.globalUnitsList = globalUnitsList;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setParameters(Set<Parameter> parameters) {
    this.parameters = parameters;
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }


}

