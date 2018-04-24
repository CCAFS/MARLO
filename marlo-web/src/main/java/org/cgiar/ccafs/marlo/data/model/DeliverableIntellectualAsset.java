package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 5, 2017 7:38:48 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class DeliverableIntellectualAsset implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 7443280243651199690L;

  @Expose
  private Long id;
  @Expose
  private Deliverable deliverable;
  @Expose
  private Phase phase;
  @Expose
  private Boolean hasPatentPvp;
  @Expose
  private String applicant;
  @Expose
  private Integer type;
  @Expose
  private String title;
  @Expose
  private String additionalInformation;
  @Expose
  private String link;
  @Expose
  private String publicCommunication;


  public DeliverableIntellectualAsset() {
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
    DeliverableIntellectualAsset other = (DeliverableIntellectualAsset) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }


  public String getAdditionalInformation() {
    return additionalInformation;
  }


  public String getApplicant() {
    return applicant;
  }


  public Deliverable getDeliverable() {
    return deliverable;
  }


  public Boolean getHasPatentPvp() {
    return hasPatentPvp;
  }


  @Override
  public Long getId() {
    return id;
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

  public String getPublicCommunication() {
    return publicCommunication;
  }

  public String getTitle() {
    return title;
  }

  public Integer getType() {
    return type;
  }

  public String getTypeName() {
    try {
      if (this.type != null) {
        return DeliverableIntellectualAssetTypeEnum.getValue(this.type).getType() != null
          ? DeliverableIntellectualAssetTypeEnum.getValue(this.type).getType() : "";
      } else {
        return "";
      }
    } catch (Exception e) {
      return "";
    }
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
    return true;
  }

  public void setAdditionalInformation(String additionalInformation) {
    this.additionalInformation = additionalInformation;
  }


  public void setApplicant(String applicant) {
    this.applicant = applicant;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }


  public void setHasPatentPvp(Boolean hasPatentPvp) {
    this.hasPatentPvp = hasPatentPvp;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setLink(String link) {
    this.link = link;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setPublicCommunication(String publicCommunication) {
    this.publicCommunication = publicCommunication;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setType(Integer type) {
    this.type = type;
  }


  @Override
  public String toString() {
    return "DeliverableIntellectualAsset [id=" + id + ", deliverable=" + deliverable + ", phase=" + phase
      + ", hasPatentPvp=" + hasPatentPvp + ", type=" + this.getTypeName() + "]";
  }

}