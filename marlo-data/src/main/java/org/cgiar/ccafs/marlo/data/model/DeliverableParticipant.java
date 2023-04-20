package org.cgiar.ccafs.marlo.data.model;


import org.cgiar.ccafs.marlo.data.IAuditLog;

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
  private Double african;
  @Expose
  private Boolean estimateAfrican;
  @Expose
  private Double africanPercentage;
  @Expose
  private Double youth;
  @Expose
  private Boolean estimateYouth;
  @Expose
  private Double youthPercentage;
  @Expose
  private String focus;
  @Expose
  private String likelyOutcomes;
  @Expose
  private RepIndTypeParticipant repIndTypeParticipant;
  @Expose
  private RepIndTrainingTerm repIndTrainingTerm;
  @Expose
  private Double males;

  // Deliverable own Trainees contribution
  private Double totalOwnTrainess = new Double(0);
  private Double totalOwnFemales = new Double(0);
  private Double totalOwnAfricans = new Double(0);
  private Double totalOwnYouth = new Double(0);

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


  public Double getAfrican() {
    return african;
  }


  public Double getAfricanPercentage() {
    return africanPercentage;
  }

  public Deliverable getDeliverable() {
    return deliverable;
  }

  public Boolean getDontKnowFemale() {
    return dontKnowFemale;
  }


  public Boolean getEstimateAfrican() {
    return estimateAfrican;
  }


  public Boolean getEstimateFemales() {
    return estimateFemales;
  }


  public Boolean getEstimateParticipants() {
    return estimateParticipants;
  }


  public Boolean getEstimateYouth() {
    return estimateYouth;
  }


  public String getEventActivityName() {
    return eventActivityName;
  }


  public Double getFemales() {
    return females;
  }


  public String getFocus() {
    return focus;
  }

  public Boolean getHasParticipants() {
    return hasParticipants;
  }


  public String getLikelyOutcomes() {
    return likelyOutcomes;
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

  public RepIndTrainingTerm getRepIndTrainingTerm() {
    return repIndTrainingTerm;
  }

  public RepIndTypeActivity getRepIndTypeActivity() {
    return repIndTypeActivity;
  }


  public RepIndTypeParticipant getRepIndTypeParticipant() {
    return repIndTypeParticipant;
  }


  public Double getTotalOwnAfricans() {
    return totalOwnAfricans;
  }

  public Double getTotalOwnFemales() {
    return totalOwnFemales;
  }

  public Double getTotalOwnTrainess() {
    return totalOwnTrainess;
  }

  public Double getTotalOwnYouth() {
    return totalOwnYouth;
  }


  public Double getYouth() {
    return youth;
  }

  public Double getYouthPercentage() {
    return youthPercentage;
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


  public void setAfrican(Double african) {
    this.african = african;
  }

  public void setAfricanPercentage(Double africanPercentage) {
    this.africanPercentage = africanPercentage;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setDontKnowFemale(Boolean dontKnowFemale) {
    this.dontKnowFemale = dontKnowFemale;
  }

  public void setEstimateAfrican(Boolean estimateAfrican) {
    this.estimateAfrican = estimateAfrican;
  }

  public void setEstimateFemales(Boolean estimateFemales) {
    this.estimateFemales = estimateFemales;
  }

  public void setEstimateParticipants(Boolean estimateParticipants) {
    this.estimateParticipants = estimateParticipants;
  }

  public void setEstimateYouth(Boolean estimateYouth) {
    this.estimateYouth = estimateYouth;
  }

  public void setEventActivityName(String eventActivityName) {
    this.eventActivityName = eventActivityName;
  }

  public void setFemales(Double females) {
    this.females = females;
  }

  public void setFocus(String focus) {
    this.focus = focus;
  }

  public void setHasParticipants(Boolean hasParticipants) {
    this.hasParticipants = hasParticipants;
  }

  public void setLikelyOutcomes(String likelyOutcomes) {
    this.likelyOutcomes = likelyOutcomes;
  }

  public void setMales(Double males) {
    this.males = males;
  }

  public void setParticipants(Double participants) {
    this.participants = participants;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setRepIndTrainingTerm(RepIndTrainingTerm repIndTrainingTerm) {
    this.repIndTrainingTerm = repIndTrainingTerm;
  }

  public void setRepIndTypeActivity(RepIndTypeActivity repIndTypeActivity) {
    this.repIndTypeActivity = repIndTypeActivity;
  }

  public void setRepIndTypeParticipant(RepIndTypeParticipant repIndTypeParticipant) {
    this.repIndTypeParticipant = repIndTypeParticipant;
  }

  public void setTotalOwnAfricans(Double totalOwnAfricans) {
    this.totalOwnAfricans = totalOwnAfricans;
  }

  public void setTotalOwnFemales(Double totalOwnFemales) {
    this.totalOwnFemales = totalOwnFemales;
  }

  public void setTotalOwnTrainess(Double totalOwnTrainess) {
    this.totalOwnTrainess = totalOwnTrainess;
  }

  public void setTotalOwnYouth(Double totalOwnYouth) {
    this.totalOwnYouth = totalOwnYouth;
  }

  public void setYouth(Double youth) {
    this.youth = youth;
  }

  public void setYouthPercentage(Double youthPercentage) {
    this.youthPercentage = youthPercentage;
  }

  @Override
  public String toString() {
    return "DeliverableParticipant [id=" + this.getId() + ", deliverable=" + deliverable + ", phase=" + phase
      + ", hasParticipants=" + hasParticipants + ", eventActivityName=" + eventActivityName + ", active="
      + this.isActive() + "]";
  }

}

