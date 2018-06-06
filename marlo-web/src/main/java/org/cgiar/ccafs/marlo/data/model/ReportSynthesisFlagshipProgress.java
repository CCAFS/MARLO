package org.cgiar.ccafs.marlo.data.model;
// Generated Jun 6, 2018 11:11:29 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;


public class ReportSynthesisFlagshipProgress implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 7688189402530792639L;

  @Expose
  private Long id;

  @Expose
  private User modifiedBy;

  private ReportSynthesis reportSynthesis;

  @Expose
  private User createdBy;

  @Expose
  private String summary;

  @Expose
  private boolean active;

  @Expose
  private Date activeSince;

  @Expose
  private String modificationJustification;


  private Set<ReportSynthesisFlagshipProgressMilestone> reportSynthesisFlagshipProgressMilestones =
    new HashSet<ReportSynthesisFlagshipProgressMilestone>(0);


  public ReportSynthesisFlagshipProgress() {
  }


  public Date getActiveSince() {
    return activeSince;
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


  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }


  public Set<ReportSynthesisFlagshipProgressMilestone> getReportSynthesisFlagshipProgressMilestones() {
    return reportSynthesisFlagshipProgressMilestones;
  }


  public String getSummary() {
    return summary;
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

  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }

  public void setReportSynthesisFlagshipProgressMilestones(
    Set<ReportSynthesisFlagshipProgressMilestone> reportSynthesisFlagshipProgressMilestones) {
    this.reportSynthesisFlagshipProgressMilestones = reportSynthesisFlagshipProgressMilestones;
  }


  public void setSummary(String summary) {
    this.summary = summary;
  }


}

