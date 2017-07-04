package org.cgiar.ccafs.marlo.data.model;
// Generated May 24, 2017 8:20:55 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;


public class CenterProjectFundingSource implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 9058588729187270692L;

  @Expose
  private Long id;

  @Expose
  private User modifiedBy;

  @Expose
  private User createdBy;

  @Expose
  private Crp crp;

  @Expose
  private CenterProject project;

  @Expose
  private String title;

  @Expose
  private CenterFundingSourceType fundingSourceType;

  @Expose
  private boolean active;

  @Expose
  private Date activeSince;

  @Expose
  private String modificationJustification;


  public CenterProjectFundingSource() {
  }


  public CenterProjectFundingSource(Long id, User modifiedBy, User createdBy, Crp crp, CenterProject project, String title,
    CenterFundingSourceType fundingSourceType, boolean active, Date activeSince, String modificationJustification) {
    super();
    this.id = id;
    this.modifiedBy = modifiedBy;
    this.createdBy = createdBy;
    this.crp = crp;
    this.project = project;
    this.title = title;
    this.fundingSourceType = fundingSourceType;
    this.active = active;
    this.activeSince = activeSince;
    this.modificationJustification = modificationJustification;
  }


  public Date getActiveSince() {
    return activeSince;
  }


  public User getCreatedBy() {
    return createdBy;
  }


  public Crp getCrp() {
    return crp;
  }


  public CenterFundingSourceType getFundingSourceType() {
    return fundingSourceType;
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
    return modificationJustification;
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  public CenterProject getProject() {
    return project;
  }

  public String getTitle() {
    return title;
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

  public void setCrp(Crp crp) {
    this.crp = crp;
  }

  public void setFundingSourceType(CenterFundingSourceType fundingSourceType) {
    this.fundingSourceType = fundingSourceType;
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

  public void setProject(CenterProject project) {
    this.project = project;
  }


  public void setTitle(String title) {
    this.title = title;
  }


}

