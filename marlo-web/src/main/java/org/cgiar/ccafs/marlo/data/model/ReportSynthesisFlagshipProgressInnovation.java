package org.cgiar.ccafs.marlo.data.model;


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class ReportSynthesisFlagshipProgressInnovation extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 7296298801369810469L;

  @Expose
  private ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress;

  @Expose
  private ProjectInnovation projectInnovation;


  public ReportSynthesisFlagshipProgressInnovation() {
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
    ReportSynthesisFlagshipProgressInnovation other = (ReportSynthesisFlagshipProgressInnovation) obj;
    if (this.getProjectInnovation() == null) {
      if (other.getProjectInnovation() != null) {
        return false;
      }
    } else if (!this.getProjectInnovation().equals(other.getProjectInnovation())) {
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


  public ProjectInnovation getProjectInnovation() {
    return projectInnovation;
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


  public void setProjectInnovation(ProjectInnovation projectInnovation) {
    this.projectInnovation = projectInnovation;
  }


  public void setReportSynthesisFlagshipProgress(ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress) {
    this.reportSynthesisFlagshipProgress = reportSynthesisFlagshipProgress;
  }


}

