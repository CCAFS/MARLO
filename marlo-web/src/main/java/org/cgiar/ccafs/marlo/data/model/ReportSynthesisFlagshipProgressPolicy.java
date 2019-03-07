package org.cgiar.ccafs.marlo.data.model;
// Generated Feb 5, 2018 11:32:15 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class ReportSynthesisFlagshipProgressPolicy extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -7456331738975066225L;

  @Expose
  private ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress;

  @Expose
  private ProjectPolicy projectPolicy;

  public ReportSynthesisFlagshipProgressPolicy() {
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
    ReportSynthesisFlagshipProgressPolicy other = (ReportSynthesisFlagshipProgressPolicy) obj;
    if (this.getProjectPolicy() == null) {
      if (other.getProjectPolicy() != null) {
        return false;
      }
    } else if (!this.getProjectPolicy().equals(other.getProjectPolicy())) {
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


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public ProjectPolicy getProjectPolicy() {
    return projectPolicy;
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

  public void setProjectPolicy(ProjectPolicy projectPolicy) {
    this.projectPolicy = projectPolicy;
  }


  public void setReportSynthesisFlagshipProgress(ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress) {
    this.reportSynthesisFlagshipProgress = reportSynthesisFlagshipProgress;
  }


}

