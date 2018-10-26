package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 5, 2017 7:38:48 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;

public class DeliverableIntellectualAsset extends MarloBaseEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 7443280243651199690L;

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
  private RepIndFillingType fillingType;
  @Expose
  private RepIndPatentStatus patentStatus;
  @Expose
  private Integer patentType;
  @Expose
  private String varietyName;
  @Expose
  private Integer status;
  @Expose
  private LocElement country;
  @Expose
  private Double appRegNumber;
  @Expose
  private String breederCrop;
  @Expose
  private Date dateFilling;
  @Expose
  private Date dateRegistration;
  @Expose
  private Date dateExpiry;
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
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
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


  public Double getAppRegNumber() {
    return appRegNumber;
  }


  public String getBreederCrop() {
    return breederCrop;
  }


  public LocElement getCountry() {
    return country;
  }


  public Date getDateExpiry() {
    return dateExpiry;
  }


  public Date getDateFilling() {
    return dateFilling;
  }


  public Date getDateRegistration() {
    return dateRegistration;
  }


  public Deliverable getDeliverable() {
    return deliverable;
  }

  public RepIndFillingType getFillingType() {
    return fillingType;
  }

  public Boolean getHasPatentPvp() {
    return hasPatentPvp;
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

  public RepIndPatentStatus getPatentStatus() {
    return patentStatus;
  }

  public Integer getPatentType() {
    return patentType;
  }

  public String getPatentTypeName() {
    try {
      if (this.patentType != null) {
        return DeliverableIntellectualAssetPantentTypeEnum.getValue(this.patentType).getType() != null
          ? DeliverableIntellectualAssetPantentTypeEnum.getValue(this.patentType).getType() : "";
      } else {
        return "";
      }
    } catch (Exception e) {
      return "";
    }
  }

  public Phase getPhase() {
    return phase;
  }

  public String getPublicCommunication() {
    return publicCommunication;
  }


  public Integer getStatus() {
    return status;
  }

  public String getStatusName(Phase phase) {
    try {
      if (this.status != null) {
        return ProjectStatusEnum.getValue(this.status).getStatus() != null
          ? ProjectStatusEnum.getValue(this.status).getStatus() : "";
      } else {
        return "";
      }
    } catch (Exception e) {
      return "";
    }
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


  public String getVarietyName() {
    return varietyName;
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


  public void setAdditionalInformation(String additionalInformation) {
    this.additionalInformation = additionalInformation;
  }


  public void setApplicant(String applicant) {
    this.applicant = applicant;
  }


  public void setAppRegNumber(Double appRegNumber) {
    this.appRegNumber = appRegNumber;
  }


  public void setBreederCrop(String breederCrop) {
    this.breederCrop = breederCrop;
  }

  public void setCountry(LocElement country) {
    this.country = country;
  }

  public void setDateExpiry(Date dateExpiry) {
    this.dateExpiry = dateExpiry;
  }

  public void setDateFilling(Date dateFilling) {
    this.dateFilling = dateFilling;
  }

  public void setDateRegistration(Date dateRegistration) {
    this.dateRegistration = dateRegistration;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }


  public void setFillingType(RepIndFillingType fillingType) {
    this.fillingType = fillingType;
  }


  public void setHasPatentPvp(Boolean hasPatentPvp) {
    this.hasPatentPvp = hasPatentPvp;
  }


  public void setLink(String link) {
    this.link = link;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {


  }


  public void setPatentStatus(RepIndPatentStatus patentStatus) {
    this.patentStatus = patentStatus;
  }


  public void setPatentType(Integer patentType) {
    this.patentType = patentType;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setPublicCommunication(String publicCommunication) {
    this.publicCommunication = publicCommunication;
  }


  public void setStatus(Integer status) {
    this.status = status;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setType(Integer type) {
    this.type = type;
  }


  public void setVarietyName(String varietyName) {
    this.varietyName = varietyName;
  }


  @Override
  public String toString() {
    return "DeliverableIntellectualAsset [id=" + this.getId() + ", deliverable=" + deliverable + ", phase=" + phase
      + ", hasPatentPvp=" + hasPatentPvp + ", type=" + this.getTypeName() + "]";
  }

}