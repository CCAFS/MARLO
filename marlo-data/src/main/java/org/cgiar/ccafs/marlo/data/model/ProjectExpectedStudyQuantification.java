package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 31, 2019 9:21:00 AM by Hibernate Tools 5.3.6.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;


public class ProjectExpectedStudyQuantification extends MarloBaseEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -5503987634103885825L;

  @Expose
  private Phase phase;
  @Expose
  private ProjectExpectedStudy projectExpectedStudy;

  @Expose
  private String typeQuantification;


  @Expose
  private Long number;


  @Expose
  private String targetUnit;


  @Expose
  private String comments;


  public ProjectExpectedStudyQuantification() {
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
    ProjectExpectedStudyQuantification other = (ProjectExpectedStudyQuantification) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public String getComments() {
    return comments;
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


  public Long getNumber() {
    return number;
  }


  public Phase getPhase() {
    return phase;
  }

  public ProjectExpectedStudy getProjectExpectedStudy() {
    return projectExpectedStudy;
  }

  public String getTargetUnit() {
    return targetUnit;
  }

  public String getTypeQuantification() {
    return typeQuantification;
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

  public void setComments(String comments) {
    this.comments = comments;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {

  }

  public void setNumber(Long number) {
    this.number = number;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy) {
    this.projectExpectedStudy = projectExpectedStudy;
  }

  public void setTargetUnit(String targetUnit) {
    this.targetUnit = targetUnit;
  }

  public void setTypeQuantification(String typeQuantification) {
    this.typeQuantification = typeQuantification;
  }


}

