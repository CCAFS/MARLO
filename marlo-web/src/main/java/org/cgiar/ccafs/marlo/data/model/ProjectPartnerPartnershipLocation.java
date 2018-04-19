package org.cgiar.ccafs.marlo.data.model;


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;

public class ProjectPartnerPartnershipLocation implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -278370693125364161L;
  @Expose
  private Long id;
  @Expose
  private ProjectPartnerPartnership projectPartnerPartnership;
  @Expose
  private LocElement location;


  @Expose
  private boolean active;
  @Expose
  private Date activeSince;
  @Expose
  private User createdBy;
  @Expose
  private User modifiedBy;
  @Expose
  private String modificationJustification;


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

  public Date getActiveSince() {
    return activeSince;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  @Override
  public Long getId() {
    return id;
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


  @Override
  public String getModificationJustification() {
    return modificationJustification;
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  public ProjectPartnerPartnership getProjectPartnerPartnership() {
    return projectPartnerPartnership;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
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

  public void setId(Long id) {
    this.id = id;
  }


  public void setLocation(LocElement location) {
    this.location = location;
  }


  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }


  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }


  public void setProjectPartnerPartnership(ProjectPartnerPartnership projectPartnerPartnership) {
    this.projectPartnerPartnership = projectPartnerPartnership;
  }


  @Override
  public String toString() {
    return "ProjectPartnerPartnershipLocation [id=" + id + ", projectPartnerPartnership=" + projectPartnerPartnership
      + ", location=" + location + ", active=" + active + "]";
  }


}

