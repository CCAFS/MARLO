package org.cgiar.ccafs.marlo.data.model;
// Generated Apr 30, 2018 10:52:36 AM by Hibernate Tools 3.4.0.CR1

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * ProjectExpectedStudySrfTarget generated by hbm2java
 */
public class ProjectExpectedStudySrfTarget extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 1L;


  @Expose
  private SrfSloIndicator srfSloIndicator;

  @Expose
  private Phase phase;

  @Expose
  private ProjectExpectedStudy projectExpectedStudy;

  public ProjectExpectedStudySrfTarget() {
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
    ProjectExpectedStudySrfTarget other = (ProjectExpectedStudySrfTarget) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  @Override
  public String getModificationJustification() {
    return "";
  }


  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return u;
  }

  public Phase getPhase() {
    return phase;
  }

  public ProjectExpectedStudy getProjectExpectedStudy() {
    return projectExpectedStudy;
  }

  public SrfSloIndicator getSrfSloIndicator() {
    return srfSloIndicator;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  @Override
  public boolean isActive() {
    return true;
  }


  @Override
  public void setModifiedBy(User modifiedBy) {

  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy) {
    this.projectExpectedStudy = projectExpectedStudy;
  }

  public void setSrfSloIndicator(SrfSloIndicator srfSloIndicator) {
    this.srfSloIndicator = srfSloIndicator;
  }

}

