package org.cgiar.ccafs.marlo.data.model;


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class ProjectPartnerPartnershipResearchPhase extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 703420896373995889L;
  @Expose
  private ProjectPartnerPartnership projectPartnerPartnership;
  @Expose
  private RepIndPhaseResearchPartnership repIndPhaseResearchPartnership;

  public ProjectPartnerPartnershipResearchPhase() {
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    ProjectPartnerPartnershipResearchPhase other = (ProjectPartnerPartnershipResearchPhase) obj;
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


  public ProjectPartnerPartnership getProjectPartnerPartnership() {
    return projectPartnerPartnership;
  }

  public RepIndPhaseResearchPartnership getRepIndPhaseResearchPartnership() {
    return repIndPhaseResearchPartnership;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setProjectPartnerPartnership(ProjectPartnerPartnership projectPartnerPartnership) {
    this.projectPartnerPartnership = projectPartnerPartnership;
  }


  public void setRepIndPhaseResearchPartnership(RepIndPhaseResearchPartnership repIndPhaseResearchPartnership) {
    this.repIndPhaseResearchPartnership = repIndPhaseResearchPartnership;
  }


  @Override
  public String toString() {
    return "ProjectPartnerPartnershipResearchPhase [id=" + this.getId() + ", projectPartnerPartnership="
      + projectPartnerPartnership + ", repIndPhaseResearchPartnership=" + repIndPhaseResearchPartnership + ", active="
      + this.isActive() + "]";
  }


}

