package org.cgiar.ccafs.marlo.data.model;
// Generated Feb 5, 2018 11:32:15 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

/**
 * PowbEvidencePlannedStudy generated by hbm2java
 */
public class PowbEvidencePlannedStudy implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -6163736717151680012L;


  private Long id;


  private User modifiedBy;


  private SrfSloIndicator srfSloIndicator;


  private User createdBy;


  private SrfSubIdo srfSubIdo;


  private PowbEvidence powbEvidence;


  private String plannedTopic;


  private Integer geographicScope;


  private String comments;


  private boolean active;


  private Date activeSince;


  private String modificationJustification;


  public PowbEvidencePlannedStudy() {
  }


  public Date getActiveSince() {
    return activeSince;
  }


  public String getComments() {
    return comments;
  }


  public User getCreatedBy() {
    return createdBy;
  }


  public Integer getGeographicScope() {
    return geographicScope;
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


  public String getPlannedTopic() {
    return plannedTopic;
  }


  public PowbEvidence getPowbEvidence() {
    return powbEvidence;
  }


  public SrfSloIndicator getSrfSloIndicator() {
    return srfSloIndicator;
  }

  public SrfSubIdo getSrfSubIdo() {
    return srfSubIdo;
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

  public void setComments(String comments) {
    this.comments = comments;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setGeographicScope(Integer geographicScope) {
    this.geographicScope = geographicScope;
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

  public void setPlannedTopic(String plannedTopic) {
    this.plannedTopic = plannedTopic;
  }

  public void setPowbEvidence(PowbEvidence powbEvidence) {
    this.powbEvidence = powbEvidence;
  }

  public void setSrfSloIndicator(SrfSloIndicator srfSloIndicator) {
    this.srfSloIndicator = srfSloIndicator;
  }


  public void setSrfSubIdo(SrfSubIdo srfSubIdo) {
    this.srfSubIdo = srfSubIdo;
  }


}

