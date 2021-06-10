package org.cgiar.ccafs.marlo.data.model;
// Generated Dec 5, 2017 9:55:32 AM by Hibernate Tools 4.3.1.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * CrpProgramOutcomeIndicator generated by hbm2java
 */
public class CrpProgramOutcomeIndicator extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  /**
   * 
   */
  private static final long serialVersionUID = 7944322202304652899L;

  private CrpProgramOutcome crpProgramOutcome;
  @Expose
  private String indicator;
  @Expose
  private String composeID;
  @Expose
  private Phase phase;

  public CrpProgramOutcomeIndicator() {
  }

  public CrpProgramOutcomeIndicator(Long id) {
    this.setId(id);
  }


  public void copyFields(CrpProgramOutcomeIndicator other) {
    this.setActive(other.isActive());
    this.setComposeID(other.getComposeID());
    // this.setCrpProgramOutcome(other.getCrpProgramOutcome());
    this.setActiveSince(other.getActiveSince());
    this.setIndicator(other.getIndicator());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    CrpProgramOutcomeIndicator other = (CrpProgramOutcomeIndicator) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public String getComposeID() {
    return composeID;
  }


  public CrpProgramOutcome getCrpProgramOutcome() {
    return crpProgramOutcome;
  }


  public String getIndicator() {
    return indicator;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public Phase getPhase() {
    return phase;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setComposeID(String composeID) {
    this.composeID = composeID;
  }

  public void setCrpProgramOutcome(CrpProgramOutcome crpProgramOutcome) {
    this.crpProgramOutcome = crpProgramOutcome;
  }

  public void setIndicator(String indicator) {
    this.indicator = indicator;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }

}

