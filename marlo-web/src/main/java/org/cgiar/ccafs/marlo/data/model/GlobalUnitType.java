package org.cgiar.ccafs.marlo.data.model;
// Generated Oct 30, 2017 10:01:51 AM by Hibernate Tools 3.4.0.CR1


import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * @author Hermes Jimenez
 */
public class GlobalUnitType implements java.io.Serializable {


  private static final long serialVersionUID = 1393262857086220239L;

  @Expose
  private Long id;
  @Expose
  private User modifiedBy;
  @Expose
  private User createdBy;
  @Expose
  private String name;
  @Expose
  private boolean active;

  @Expose
  private Date activeSince;

  @Expose
  private String modificationJustification;
  private Set<GlobalUnit> globalUnits = new HashSet<GlobalUnit>(0);
  private List<GlobalUnit> globalUnitsList;

  private Set<Parameter> parameters = new HashSet<Parameter>(0);

  public GlobalUnitType() {
  }


  public GlobalUnitType(Long id, User modifiedBy, User createdBy, String name, boolean active, Date activeSince,
    String modificationJustification, Set<GlobalUnit> globalUnits) {
    super();
    this.id = id;
    this.modifiedBy = modifiedBy;
    this.createdBy = createdBy;
    this.name = name;
    this.active = active;
    this.activeSince = activeSince;
    this.modificationJustification = modificationJustification;
    this.globalUnits = globalUnits;
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

  public Date getActiveSince() {
    return activeSince;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public Set<GlobalUnit> getGlobalUnits() {
    return globalUnits;
  }

  public List<GlobalUnit> getGlobalUnitsList() {
    return globalUnitsList;
  }

  public Long getId() {
    return id;
  }

  public String getModificationJustification() {
    return modificationJustification;
  }

  public User getModifiedBy() {
    return modifiedBy;
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
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setGlobalUnits(Set<GlobalUnit> globalUnits) {
    this.globalUnits = globalUnits;
  }

  public void setGlobalUnitsList(List<GlobalUnit> globalUnitsList) {
    this.globalUnitsList = globalUnitsList;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setParameters(Set<Parameter> parameters) {
    this.parameters = parameters;
  }


}

