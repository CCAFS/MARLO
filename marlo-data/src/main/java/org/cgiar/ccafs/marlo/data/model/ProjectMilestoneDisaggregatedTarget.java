package org.cgiar.ccafs.marlo.data.model;
// Generated Aug 28, 2017 2:21:11 PM by Hibernate Tools 4.3.1.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

public class ProjectMilestoneDisaggregatedTarget extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -8647145762226231826L;
  @Expose
  private ProjectMilestone projectMilestone;
  @Expose
  private DisaggregatedTargetCrpProgramOutcome disaggregatedTargetCrpProgramOutcome;
  @Expose
  private Phase phase;
  @Expose
  private BigDecimal expectedValue;
  @Expose
  private BigDecimal achievedValue;
  @Expose
  private String narrativeTarget;
  @Expose
  private String narrativeAchieved;
  @Expose
  private BigDecimal suggestedValue;

  public ProjectMilestoneDisaggregatedTarget() {
  }


  public BigDecimal getAchievedValue() {
    return achievedValue;
  }


  public DisaggregatedTargetCrpProgramOutcome getDisaggregatedTargetCrpProgramOutcome() {
    return disaggregatedTargetCrpProgramOutcome;
  }


  public BigDecimal getExpectedValue() {
    return expectedValue;
  }


  @Override
  public String getLogDeatil() {
    // TODO Auto-generated method stub
    return null;
  }


  public String getNarrativeAchieved() {
    return narrativeAchieved;
  }


  public String getNarrativeTarget() {
    return narrativeTarget;
  }


  public Phase getPhase() {
    return phase;
  }


  public ProjectMilestone getProjectMilestone() {
    return projectMilestone;
  }


  public BigDecimal getSuggestedValue() {
    return suggestedValue;
  }


  public void setAchievedValue(BigDecimal achievedValue) {
    this.achievedValue = achievedValue;
  }


  public void
    setDisaggregatedTargetCrpProgramOutcome(DisaggregatedTargetCrpProgramOutcome disaggregatedTargetCrpProgramOutcome) {
    this.disaggregatedTargetCrpProgramOutcome = disaggregatedTargetCrpProgramOutcome;
  }


  public void setExpectedValue(BigDecimal expectedValue) {
    this.expectedValue = expectedValue;
  }


  public void setNarrativeAchieved(String narrativeAchieved) {
    this.narrativeAchieved = narrativeAchieved;
  }


  public void setNarrativeTarget(String narrativeTarget) {
    this.narrativeTarget = narrativeTarget;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setProjectMilestone(ProjectMilestone projectMilestone) {
    this.projectMilestone = projectMilestone;
  }


  public void setSuggestedValue(BigDecimal suggestedValue) {
    this.suggestedValue = suggestedValue;
  }

}

