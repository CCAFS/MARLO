package org.cgiar.ccafs.marlo.data.model;


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class ProjectPartnerPartnershipLocation extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -278370693125364161L;
  @Expose
  private ProjectPartnerPartnership projectPartnerPartnership;
  @Expose
  private LocElement location;

  public ProjectPartnerPartnershipLocation() {
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    ProjectPartnerPartnershipLocation other = (ProjectPartnerPartnershipLocation) obj;
    if (location == null) {
      if (other.location != null) {
        return false;
      }
    } else if (!location.getId().equals(other.location.getId())) {
      return false;
    }
    return true;
  }

  public LocElement getLocation() {
    return location;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public ProjectPartnerPartnership getProjectPartnerPartnership() {
    return projectPartnerPartnership;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public void setLocation(LocElement location) {
    this.location = location;
  }


  public void setProjectPartnerPartnership(ProjectPartnerPartnership projectPartnerPartnership) {
    this.projectPartnerPartnership = projectPartnerPartnership;
  }


  @Override
  public String toString() {
    return "ProjectPartnerPartnershipLocation [id=" + this.getId() + ", projectPartnerPartnership="
      + projectPartnerPartnership + ", location=" + location + ", active=" + this.isActive() + "]";
  }


}

