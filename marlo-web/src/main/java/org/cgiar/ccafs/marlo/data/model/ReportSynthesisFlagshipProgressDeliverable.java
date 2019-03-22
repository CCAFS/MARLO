package org.cgiar.ccafs.marlo.data.model;


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class ReportSynthesisFlagshipProgressDeliverable extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 5753585834156363912L;

  @Expose
  private ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress;

  @Expose
  private Deliverable deliverable;


  public ReportSynthesisFlagshipProgressDeliverable() {
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
    ReportSynthesisFlagshipProgressDeliverable other = (ReportSynthesisFlagshipProgressDeliverable) obj;
    if (this.getDeliverable() == null) {
      if (other.getDeliverable() != null) {
        return false;
      }
    } else if (!this.getDeliverable().equals(other.getDeliverable())) {
      return false;
    }
    if (this.getReportSynthesisFlagshipProgress() == null) {
      if (other.getReportSynthesisFlagshipProgress() != null) {
        return false;
      }
    } else if (!this.getReportSynthesisFlagshipProgress().getId()
      .equals(other.getReportSynthesisFlagshipProgress().getId())) {
      return false;
    }
    return true;
  }


  public Deliverable getDeliverable() {
    return deliverable;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public ReportSynthesisFlagshipProgress getReportSynthesisFlagshipProgress() {
    return reportSynthesisFlagshipProgress;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }


  public void setReportSynthesisFlagshipProgress(ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress) {
    this.reportSynthesisFlagshipProgress = reportSynthesisFlagshipProgress;
  }


}

