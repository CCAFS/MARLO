package org.cgiar.ccafs.marlo.data.model;


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;

public class DeliverableParticipant implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -4816363283736965901L;

  @Expose
  private Long id;
  @Expose
  private Deliverable deliverable;
  @Expose
  private Phase phase;
  @Expose
  private Boolean hasParticipants;
  @Expose
  private String eventActivityName;
  @Expose
  private RepIndTypeActivity repIndTypeActivity;
  @Expose
  private String academicDegree;
  @Expose
  private Double participants;
  @Expose
  private Boolean estimateParticipants;
  @Expose
  private Double females;
  @Expose
  private Boolean estimateFemales;
  @Expose
  private Boolean dontKnowFemale;
  @Expose
  private RepIndTypeParticipant repIndTypeParticipant;
  @Expose
  private RepIndGeographicScope repIndGeographicScope;
  @Expose
  private RepIndRegion repIndRegion;
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


  public DeliverableParticipant() {
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
    DeliverableParticipant other = (DeliverableParticipant) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }


  public String getAcademicDegree() {
    return academicDegree;
  }


  public Date getActiveSince() {
    return this.activeSince;
  }


  public User getCreatedBy() {
    return createdBy;
  }


  public Deliverable getDeliverable() {
    return deliverable;
  }


  public Boolean getDontKnowFemale() {
    return dontKnowFemale;
  }


  public Boolean getEstimateFemales() {
    return estimateFemales;
  }


  public Boolean getEstimateParticipants() {
    return estimateParticipants;
  }


  public String getEventActivityName() {
    return eventActivityName;
  }


  public Double getFemales() {
    return females;
  }


  public Boolean getHasParticipants() {
    return hasParticipants;
  }


  @Override
  public Long getId() {
    return this.id;
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


  public Double getParticipants() {
    return participants;
  }


  public Phase getPhase() {
    return phase;
  }


  public RepIndGeographicScope getRepIndGeographicScope() {
    return repIndGeographicScope;
  }


  public RepIndRegion getRepIndRegion() {
    return repIndRegion;
  }


  public RepIndTypeActivity getRepIndTypeActivity() {
    return repIndTypeActivity;
  }


  public RepIndTypeParticipant getRepIndTypeParticipant() {
    return repIndTypeParticipant;
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


  public void setAcademicDegree(String academicDegree) {
    this.academicDegree = academicDegree;
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


  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setDontKnowFemale(Boolean dontKnowFemale) {
    this.dontKnowFemale = dontKnowFemale;
  }

  public void setEstimateFemales(Boolean estimateFemales) {
    this.estimateFemales = estimateFemales;
  }

  public void setEstimateParticipants(Boolean estimateParticipants) {
    this.estimateParticipants = estimateParticipants;
  }

  public void setEventActivityName(String eventActivityName) {
    this.eventActivityName = eventActivityName;
  }

  public void setFemales(Double females) {
    this.females = females;
  }


  public void setHasParticipants(Boolean hasParticipants) {
    this.hasParticipants = hasParticipants;
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


  public void setParticipants(Double participants) {
    this.participants = participants;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setRepIndGeographicScope(RepIndGeographicScope repIndGeographicScope) {
    this.repIndGeographicScope = repIndGeographicScope;
  }


  public void setRepIndRegion(RepIndRegion repIndRegion) {
    this.repIndRegion = repIndRegion;
  }

  public void setRepIndTypeActivity(RepIndTypeActivity repIndTypeActivity) {
    this.repIndTypeActivity = repIndTypeActivity;
  }


  public void setRepIndTypeParticipant(RepIndTypeParticipant repIndTypeParticipant) {
    this.repIndTypeParticipant = repIndTypeParticipant;
  }


  @Override
  public String toString() {
    return "DeliverableParticipant [id=" + id + ", deliverable=" + deliverable + ", phase=" + phase
      + ", hasParticipants=" + hasParticipants + ", eventActivityName=" + eventActivityName + ", active=" + active
      + "]";
  }


}

