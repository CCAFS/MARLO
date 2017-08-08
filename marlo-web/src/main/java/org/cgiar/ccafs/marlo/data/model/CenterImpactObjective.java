package org.cgiar.ccafs.marlo.data.model;
// Generated Oct 13, 2016 1:24:28 PM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;


public class CenterImpactObjective implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -8979609328318401070L;


  @Expose
  private Long id;

  @Expose
  private User modifiedBy;


  @Expose
  private CenterObjective researchObjective;

  @Expose
  private User createdBy;

  @Expose
  private CenterImpact researchImpact;

  @Expose
  private boolean active;

  @Expose
  private Date activeSince;

  @Expose
  private String modificationJustification;

  public CenterImpactObjective() {
  }

  public CenterImpactObjective(CenterObjective researchObjective, CenterImpact researchImpact, boolean active) {
    this.researchObjective = researchObjective;
    this.researchImpact = researchImpact;
    this.active = active;
  }

  public CenterImpactObjective(User modifiedBy, CenterObjective researchObjective, User createdBy,
    CenterImpact researchImpact, boolean active, Date activeSince, String modificationJustification) {
    this.modifiedBy = modifiedBy;
    this.researchObjective = researchObjective;
    this.createdBy = createdBy;
    this.researchImpact = researchImpact;
    this.active = active;
    this.activeSince = activeSince;
    this.modificationJustification = modificationJustification;
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
    CenterImpactObjective other = (CenterImpactObjective) obj;
    if (this.getResearchImpact() == null) {
      if (other.getResearchImpact() != null) {
        return false;
      }
    } else if (!this.getResearchImpact().equals(other.getResearchImpact())) {
      return false;
    }
    if (this.getResearchObjective() == null) {
      if (other.getResearchObjective() != null) {
        return false;
      }
    } else if (!this.getResearchObjective().getId().equals(other.getResearchObjective().getId())) {
      return false;
    }
    return true;
  }


  public Date getActiveSince() {
    return this.activeSince;
  }


  public User getCreatedBy() {
    return createdBy;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public String getModificationJustification() {
    return this.modificationJustification;
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  public CenterImpact getResearchImpact() {
    return researchImpact;
  }

  public CenterObjective getResearchObjective() {
    return researchObjective;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (active ? 1231 : 1237);
    result = prime * result + ((researchImpact == null) ? 0 : researchImpact.hashCode());
    result = prime * result + ((researchObjective == null) ? 0 : researchObjective.hashCode());
    return result;
  }

  @Override
  public boolean isActive() {
    return active;
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


  public void setId(Long id) {
    this.id = id;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setResearchImpact(CenterImpact researchImpact) {
    this.researchImpact = researchImpact;
  }

  public void setResearchObjective(CenterObjective researchObjective) {
    this.researchObjective = researchObjective;
  }


}

