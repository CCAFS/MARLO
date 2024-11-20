package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class ProjectInnovationReferenceUrl extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


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
  private Boolean innovationType;
  @Expose
  private Boolean additionalArticleType;
  @Expose
  private Boolean datasetType;
  @Expose
  private Deliverable deliverable;

  public ProjectInnovationReferenceUrl() {
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

    ProjectInnovationReferenceUrl other = (ProjectInnovationReferenceUrl) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }

    return true;
  }

  public Boolean getAdditionalArticleType() {
    return additionalArticleType;
  }

  public Boolean getDatasetType() {
    return datasetType;
  }

  public Deliverable getDeliverable() {
    return deliverable;
  }

  public Boolean getEvidenceByDeliverable() {
    return evidenceByDeliverable;
  }


  public Boolean getInnovationType() {
    return innovationType;
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

  public Phase getPhase() {
    return phase;
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

  @Override
  public boolean isActive() {
    return true;
  }

  public void setAdditionalArticleType(Boolean additionalArticleType) {
    this.additionalArticleType = additionalArticleType;
  }

  public void setDatasetType(Boolean datasetType) {
    this.datasetType = datasetType;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setEvidenceByDeliverable(Boolean evidenceByDeliverable) {
    this.evidenceByDeliverable = evidenceByDeliverable;
  }

  public void setInnovationType(Boolean innovationType) {
    this.innovationType = innovationType;
  }

  public void setLink(String link) {
    this.link = link;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {

  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProjectInnovation(ProjectInnovation projectInnovation) {
    this.projectInnovation = projectInnovation;
  }

  public void setReference(String link) {
    this.reference = link;
  }
}