package org.cgiar.ccafs.marlo.data.model;
// Generated Feb 14, 2018 10:35:32 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * CenterOutputsOutcome generated by hbm2java
 */
public class CenterOutputsOutcome implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 6009108029380683092L;

  @Expose
  private Long id;

  @Expose
  private User modifiedBy;

  @Expose
  private User createdBy;

  @Expose
  private CenterOutcome centerOutcome;

  @Expose
  private CenterOutput centerOutput;

  @Expose
  private boolean active;

  @Expose
  private Date activeSince;

  @Expose
  private String modificationJustification;


  public CenterOutputsOutcome() {
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    CenterOutputsOutcome other = (CenterOutputsOutcome) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }


  public Date getActiveSince() {
    return activeSince;
  }


  public CenterOutcome getCenterOutcome() {
    return centerOutcome;
  }


  public CenterOutput getCenterOutput() {
    return centerOutput;
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


  @Override
  public String getModificationJustification() {
    return modificationJustification;
  }


  @Override
  public User getModifiedBy() {
    return modifiedBy;
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

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  public void setCenterOutcome(CenterOutcome centerOutcome) {
    this.centerOutcome = centerOutcome;
  }

  public void setCenterOutput(CenterOutput centerOutput) {
    this.centerOutput = centerOutput;
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

  @Override
  public String toString() {
    return "CenterOutputsOutcome [id=" + id + "]";
  }


}

