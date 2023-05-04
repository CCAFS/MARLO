package org.cgiar.ccafs.marlo.data.model;


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class DeliverableClusterParticipant extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -4816363283736965901L;

  @Expose
  private Deliverable deliverable;
  @Expose
  private Phase phase;
  @Expose
  private Project project;
  @Expose
  private Double participants;
  @Expose
  private Double females;
  @Expose
  private Double african;
  @Expose
  private Double youth;


  public DeliverableClusterParticipant() {
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
    DeliverableClusterParticipant other = (DeliverableClusterParticipant) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public Double getAfrican() {
    return african;
  }


  public Deliverable getDeliverable() {
    return deliverable;
  }

  public Double getFemales() {
    return females;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public Double getMales() {
    if (this.participants != null && this.females != null) {
      return this.participants - females;
    }
    return new Double(0);
  }

  public Double getParticipants() {
    return participants;
  }

  public Phase getPhase() {
    return phase;
  }

  public Project getProject() {
    return project;
  }

  public Double getYouth() {
    return youth;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setAfrican(Double african) {
    this.african = african;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setFemales(Double females) {
    this.females = females;
  }

  public void setParticipants(Double participants) {
    this.participants = participants;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setYouth(Double youth) {
    this.youth = youth;
  }

  @Override
  public String toString() {
    return "DeliverableParticipant [id=" + this.getId() + ", deliverable=" + deliverable + ", phase=" + phase
      + ", active=" + this.isActive() + "]";
  }
}

