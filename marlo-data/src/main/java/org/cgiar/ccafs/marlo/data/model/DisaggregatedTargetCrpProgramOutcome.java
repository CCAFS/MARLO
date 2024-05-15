package org.cgiar.ccafs.marlo.data.model;
// Generated Aug 28, 2017 2:21:11 PM by Hibernate Tools 4.3.1.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

public class DisaggregatedTargetCrpProgramOutcome extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -8647145762226231826L;
  @Expose
  private SrfTargetUnit srfTargetUnit;
  @Expose
  private CrpProgramOutcome crpProgramOutcome;
  @Expose
  private Phase phase;
  @Expose
  private String targetName;
  @Expose
  private String targetDescription;
  @Expose
  private DisaggregatedTargetsBusinessRule disaggregatedTargetBussinessRule;
  @Expose
  private BigDecimal targetValue;

  public DisaggregatedTargetCrpProgramOutcome() {
  }

  public CrpProgramOutcome getCrpProgramOutcome() {
    return crpProgramOutcome;
  }

  public DisaggregatedTargetsBusinessRule getDisaggregatedTargetBussinessRule() {
    return disaggregatedTargetBussinessRule;
  }

  @Override
  public String getLogDeatil() {
    // TODO Auto-generated method stub
    return null;
  }

  public Phase getPhase() {
    return phase;
  }

  public SrfTargetUnit getSrfTargetUnit() {
    return srfTargetUnit;
  }

  public String getTargetDescription() {
    return targetDescription;
  }

  public String getTargetName() {
    return targetName;
  }

  public BigDecimal getTargetValue() {
    return targetValue;
  }

  public void setCrpProgramOutcome(CrpProgramOutcome crpProgramOutcome) {
    this.crpProgramOutcome = crpProgramOutcome;
  }

  public void setDisaggregatedTargetBussinessRule(DisaggregatedTargetsBusinessRule disaggregatedTargetBussinessRule) {
    this.disaggregatedTargetBussinessRule = disaggregatedTargetBussinessRule;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setSrfTargetUnit(SrfTargetUnit srfTargetUnit) {
    this.srfTargetUnit = srfTargetUnit;
  }

  public void setTargetDescription(String targetDescription) {
    this.targetDescription = targetDescription;
  }

  public void setTargetName(String targetName) {
    this.targetName = targetName;
  }

  public void setTargetValue(BigDecimal targetValue) {
    this.targetValue = targetValue;
  }

}

