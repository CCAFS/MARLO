package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;


public class ProjectDeliverableShared extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -6439487165967165797L;


  @Expose
  private Phase phase;


  @Expose
  private Deliverable deliverable;


  @Expose
  private Project project;

  public ProjectDeliverableShared() {
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    ProjectDeliverableShared other = (ProjectDeliverableShared) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public Deliverable getDeliverable() {
    return deliverable;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public Phase getPhase() {
    return phase;
  }


  public Project getProject() {
    return project;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProject(Project project) {
    this.project = project;
  }

}

