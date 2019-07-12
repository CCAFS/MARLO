package org.cgiar.ccafs.marlo.data.model;


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class ReportSynthesisFlagshipProgressStudy extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -2968533171743865055L;

  @Expose
  private ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress;

  @Expose
  private ProjectExpectedStudy projectExpectedStudy;

  public ReportSynthesisFlagshipProgressStudy() {
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
    ReportSynthesisFlagshipProgressStudy other = (ReportSynthesisFlagshipProgressStudy) obj;
    if (this.getProjectExpectedStudy() == null) {
      if (other.getProjectExpectedStudy() != null) {
        return false;
      }
    } else if (!this.getProjectExpectedStudy().equals(other.getProjectExpectedStudy())) {
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


  public ProjectExpectedStudy getProjectExpectedStudy() {
    return projectExpectedStudy;
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

  public void setProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy) {
    this.projectExpectedStudy = projectExpectedStudy;
  }


  public void setReportSynthesisFlagshipProgress(ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress) {
    this.reportSynthesisFlagshipProgress = reportSynthesisFlagshipProgress;
  }


}

