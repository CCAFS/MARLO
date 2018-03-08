package org.cgiar.ccafs.marlo.data.model;
// Generated Feb 2, 2018 8:27:14 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;

public class PowbCrossCuttingDimension implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -7015424498651976768L;


  @Expose
  private Long id;


  private PowbSynthesis powbSynthesis;


  @Expose
  private User modifiedBy;


  @Expose
  private User createdBy;


  @Expose
  private String summarize;


  @Expose
  private String assets;


  @Expose
  private boolean active;


  @Expose
  private Date activeSince;


  @Expose
  private String modificationJustification;


  public PowbCrossCuttingDimension() {
  }


  public Date getActiveSince() {
    return activeSince;
  }


  public String getAssets() {
    return assets;
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

  public PowbSynthesis getPowbSynthesis() {
    return powbSynthesis;
  }

  public String getSummarize() {
    return summarize;
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

  public void setAssets(String assets) {
    this.assets = assets;
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

  public void setPowbSynthesis(PowbSynthesis powbSynthesis) {
    this.powbSynthesis = powbSynthesis;
  }

  public void setSummarize(String summarize) {
    this.summarize = summarize;
  }


}

