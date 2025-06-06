package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class ProjectInnovationReference extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -8855879487687610305L;
  @Expose
  private Phase phase;
  @Expose
  private ProjectInnovation projectInnovation;
  @Expose
  private String reference;
  @Expose
  private String link;
  @Expose
  private Boolean evidenceByDeliverable;
  @Expose
  private Boolean externalAuthor;
  @Expose
  private Deliverable deliverable;
  @Expose
  private DeliverableType deliverableType;
  @Expose
  private Boolean gender;
  @Expose
  private Boolean climateChange;
  @Expose
  private Boolean nutrition;
  @Expose
  private Boolean environmental;
  @Expose
  private Boolean poverty;
  @Expose
  private Boolean innovationReadiness;
  @Expose
  private String evidenceSource;

  public ProjectInnovationReference() {
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

    ProjectInnovationReference other = (ProjectInnovationReference) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }

    return true;
  }

  public Boolean getClimateChange() {
    return climateChange;
  }

  public Deliverable getDeliverable() {
    return deliverable;
  }

  public DeliverableType getDeliverableType() {
    return deliverableType;
  }

  public Boolean getEnvironmental() {
    return environmental;
  }

  public Boolean getEvidenceByDeliverable() {
    return evidenceByDeliverable;
  }

  public String getEvidenceSource() {
    return evidenceSource;
  }

  public Boolean getExternalAuthor() {
    return externalAuthor;
  }

  public Boolean getGender() {
    return gender;
  }

  public Boolean getInnovationReadiness() {
    return innovationReadiness;
  }

  public String getLink() {
    return link;
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

  public Boolean getNutrition() {
    return nutrition;
  }

  public Phase getPhase() {
    return phase;
  }

  public Boolean getPoverty() {
    return poverty;
  }

  public ProjectInnovation getProjectInnovation() {
    return projectInnovation;
  }

  public String getReference() {
    return reference;
  }

  @Override
  /*
   * WARNING: because the way hashCode() is implemented on Phase and ProjectExpectedStudy there is a possibility a clash
   * will happen. Let's pray it does not happen...
   */
  public int hashCode() {
    HashCodeBuilder hashBuilder = new HashCodeBuilder();
    hashBuilder.append(this.phase);
    hashBuilder.append(this.projectInnovation);
    hashBuilder.append(this.reference);
    return hashBuilder.hashCode();
  }

  public void setClimateChange(Boolean climateChange) {
    this.climateChange = climateChange;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setDeliverableType(DeliverableType deliverableType) {
    this.deliverableType = deliverableType;
  }

  public void setEnvironmental(Boolean environmental) {
    this.environmental = environmental;
  }

  public void setEvidenceByDeliverable(Boolean evidenceByDeliverable) {
    this.evidenceByDeliverable = evidenceByDeliverable;
  }

  public void setEvidenceSource(String evidenceSource) {
    this.evidenceSource = evidenceSource;
  }

  public void setExternalAuthor(Boolean externalAuthor) {
    this.externalAuthor = externalAuthor;
  }

  public void setGender(Boolean gender) {
    this.gender = gender;
  }

  public void setInnovationReadiness(Boolean innovationReadiness) {
    this.innovationReadiness = innovationReadiness;
  }

  public void setLink(String link) {
    this.link = link;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {

  }

  public void setNutrition(Boolean nutrition) {
    this.nutrition = nutrition;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setPoverty(Boolean poverty) {
    this.poverty = poverty;
  }

  public void setProjectInnovation(ProjectInnovation projectInnovation) {
    this.projectInnovation = projectInnovation;
  }

  public void setReference(String link) {
    this.reference = link;
  }
}