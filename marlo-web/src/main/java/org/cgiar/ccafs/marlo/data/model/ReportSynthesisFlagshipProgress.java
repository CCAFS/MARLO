package org.cgiar.ccafs.marlo.data.model;
// Generated Jun 6, 2018 11:11:29 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;


public class ReportSynthesisFlagshipProgress extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 7688189402530792639L;


  private ReportSynthesis reportSynthesis;


  @Expose
  private String summary;

  private Set<ReportSynthesisFlagshipProgressMilestone> reportSynthesisFlagshipProgressMilestones =
    new HashSet<ReportSynthesisFlagshipProgressMilestone>(0);

  private List<ReportSynthesisFlagshipProgressMilestone> milestones;

  public ReportSynthesisFlagshipProgress() {
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public List<ReportSynthesisFlagshipProgressMilestone> getMilestones() {
    return milestones;
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

  public void setMilestones(List<ReportSynthesisFlagshipProgressMilestone> milestones) {
    this.milestones = milestones;
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

