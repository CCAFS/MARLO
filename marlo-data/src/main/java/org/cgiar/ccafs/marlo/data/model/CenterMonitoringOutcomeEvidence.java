package org.cgiar.ccafs.marlo.data.model;
// Generated Mar 27, 2017 9:38:19 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * CenterMonitoringOutcomeEvidence generated by hbm2java
 */
public class CenterMonitoringOutcomeEvidence extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -5850591332279864292L;

  @Expose
  private CenterMonitoringOutcome monitoringOutcome;

  @Expose
  private String evidenceLink;


  public CenterMonitoringOutcomeEvidence() {
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    CenterMonitoringOutcomeEvidence other = (CenterMonitoringOutcomeEvidence) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public String getEvidenceLink() {
    return evidenceLink;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public CenterMonitoringOutcome getMonitoringOutcome() {
    return monitoringOutcome;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public void setEvidenceLink(String evidenceLink) {
    this.evidenceLink = evidenceLink;
  }


  public void setMonitoringOutcome(CenterMonitoringOutcome monitoringOutcome) {
    this.monitoringOutcome = monitoringOutcome;
  }


  @Override
  public String toString() {
    return "CenterMonitoringOutcomeEvidence [id=" + this.getId() + ", monitoringOutcome=" + monitoringOutcome
      + ", evidenceLink=" + evidenceLink + "]";
  }


}

