package org.cgiar.ccafs.marlo.data.model;


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class DeliverableParticipant extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -4816363283736965901L;

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

  private Set<DeliverableParticipantLocation> deliverableParticipantLocations =
    new HashSet<DeliverableParticipantLocation>(0);
  private List<DeliverableParticipantLocation> participantLocations = new ArrayList<>();
  private List<String> participantLocationsIsos = new ArrayList<>();
  private String participantLocationsIsosText;


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
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public String getAcademicDegree() {
    return academicDegree;
  }

  public Deliverable getDeliverable() {
    return deliverable;
  }


  public Set<DeliverableParticipantLocation> getDeliverableParticipantLocations() {
    return deliverableParticipantLocations;
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
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public List<DeliverableParticipantLocation> getParticipantLocations() {
    return participantLocations;
  }


  public List<String> getParticipantLocationsIsos() {
    return participantLocationsIsos;
  }


  public String getParticipantLocationsIsosText() {
    return participantLocationsIsosText;
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
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public void setAcademicDegree(String academicDegree) {
    this.academicDegree = academicDegree;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }


  public void setDeliverableParticipantLocations(Set<DeliverableParticipantLocation> deliverableParticipantLocations) {
    this.deliverableParticipantLocations = deliverableParticipantLocations;
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


  public void setParticipantLocations(List<DeliverableParticipantLocation> participantLocations) {
    this.participantLocations = participantLocations;
  }


  public void setParticipantLocationsIsos(List<String> participantLocationsIsos) {
    this.participantLocationsIsos = participantLocationsIsos;
  }


  public void setParticipantLocationsIsosText(String participantLocationsIsosText) {
    this.participantLocationsIsosText = participantLocationsIsosText;
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
    return "DeliverableParticipant [id=" + this.getId() + ", deliverable=" + deliverable + ", phase=" + phase
      + ", hasParticipants=" + hasParticipants + ", eventActivityName=" + eventActivityName + ", active="
      + this.isActive() + "]";
  }


}

