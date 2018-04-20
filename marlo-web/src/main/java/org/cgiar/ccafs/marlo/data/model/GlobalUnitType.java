package org.cgiar.ccafs.marlo.data.model;
// Generated Oct 30, 2017 10:01:51 AM by Hibernate Tools 3.4.0.CR1


import java.util.HashSet;
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

  private Set<Parameter> parameters = new HashSet<Parameter>(0);

  public GlobalUnitType() {
  }

  public Set<GlobalUnit> getGlobalUnits() {
    return globalUnits;
  }

  public String getName() {
    return name;
  }

  public Set<Parameter> getParameters() {
    return parameters;
  }

  public void setGlobalUnits(Set<GlobalUnit> globalUnits) {
    this.globalUnits = globalUnits;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setParameters(Set<Parameter> parameters) {
    this.parameters = parameters;
  }


}

