package org.cgiar.ccafs.marlo.data.model;
// Generated Aug 28, 2017 2:21:11 PM by Hibernate Tools 4.3.1.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

public class DisaggregatedTargetCrpMilestone extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -8647145762226231826L;
  @Expose
  private CrpMilestone crpMilestone;
  @Expose
  private DisaggregatedTargetCrpProgramOutcome disaggregatedTargetCrpProgramOutcome;
  @Expose
  private Phase phase;
  @Expose
  private BigDecimal targetValue;

  public DisaggregatedTargetCrpMilestone() {
  }


  public CrpMilestone getCrpMilestone() {
    return crpMilestone;
  }


  public DisaggregatedTargetCrpProgramOutcome getDisaggregatedTargetCrpProgramOutcome() {
    return disaggregatedTargetCrpProgramOutcome;
  }


  @Override
  public String getLogDeatil() {
    // TODO Auto-generated method stub
    return null;
  }


  public Phase getPhase() {
    return phase;
  }

  public BigDecimal getTargetValue() {
    return targetValue;
  }


  public void setCrpMilestone(CrpMilestone crpMilestone) {
    this.crpMilestone = crpMilestone;
  }


  public void
    setDisaggregatedTargetCrpProgramOutcome(DisaggregatedTargetCrpProgramOutcome disaggregatedTargetCrpProgramOutcome) {
    this.disaggregatedTargetCrpProgramOutcome = disaggregatedTargetCrpProgramOutcome;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setTargetValue(BigDecimal targetValue) {
    this.targetValue = targetValue;
  }

}

