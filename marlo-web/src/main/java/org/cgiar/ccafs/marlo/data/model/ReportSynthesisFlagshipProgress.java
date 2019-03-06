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

  /**
   * AR2018 Fields
   */
  @Expose
  private String overallProgress;
  @Expose
  private String progressByFlagships;
  @Expose
  private String detailedAnnex;
  @Expose
  private String expandedResearchAreas;
  @Expose
  private String droppedResearchLines;
  @Expose
  private String changedDirection;
  @Expose
  private String altmetricScore;


  public ReportSynthesisFlagshipProgress() {
  }


  public String getAltmetricScore() {
    return altmetricScore;
  }


  public String getChangedDirection() {
    return changedDirection;
  }


  public String getDetailedAnnex() {
    return detailedAnnex;
  }


  public String getDroppedResearchLines() {
    return droppedResearchLines;
  }


  public String getExpandedResearchAreas() {
    return expandedResearchAreas;
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


  public String getOverallProgress() {
    return overallProgress;
  }


  public String getProgressByFlagships() {
    return progressByFlagships;
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


  public void setAltmetricScore(String altmetricScore) {
    this.altmetricScore = altmetricScore;
  }

  public void setChangedDirection(String changedDirection) {
    this.changedDirection = changedDirection;
  }

  public void setDetailedAnnex(String detailedAnnex) {
    this.detailedAnnex = detailedAnnex;
  }


  public void setDroppedResearchLines(String droppedResearchLines) {
    this.droppedResearchLines = droppedResearchLines;
  }

  public void setExpandedResearchAreas(String expandedResearchAreas) {
    this.expandedResearchAreas = expandedResearchAreas;
  }

  public void setMilestones(List<ReportSynthesisFlagshipProgressMilestone> milestones) {
    this.milestones = milestones;
  }


  public void setOverallProgress(String overallProgress) {
    this.overallProgress = overallProgress;
  }

  public void setProgressByFlagships(String progressByFlagships) {
    this.progressByFlagships = progressByFlagships;
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

