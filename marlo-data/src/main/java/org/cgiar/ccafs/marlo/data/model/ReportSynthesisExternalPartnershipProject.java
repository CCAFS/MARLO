package org.cgiar.ccafs.marlo.data.model;
// Generated Jun 8, 2018 3:52:45 PM by Hibernate Tools 3.4.0.CR1

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * ReportSynthesisExternalPartnershipProject generated by hbm2java
 */
public class ReportSynthesisExternalPartnershipProject extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -7837272359744465561L;


  @Expose
  private ProjectPartnerPartnership projectPartnerPartnership;


  @Expose
  private ReportSynthesisExternalPartnership reportSynthesisExternalPartnership;


  public ReportSynthesisExternalPartnershipProject() {
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
    ReportSynthesisExternalPartnershipProject other = (ReportSynthesisExternalPartnershipProject) obj;
    if (projectPartnerPartnership == null) {
      if (other.projectPartnerPartnership != null) {
        return false;
      }
    } else if (!this.getProjectPartnerPartnership().equals(other.getProjectPartnerPartnership())) {
      return false;
    }
    if (reportSynthesisExternalPartnership == null) {
      if (other.reportSynthesisExternalPartnership != null) {
        return false;
      }
    } else if (!this.getReportSynthesisExternalPartnership().equals(other.getReportSynthesisExternalPartnership())) {
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


  public ProjectPartnerPartnership getProjectPartnerPartnership() {
    return projectPartnerPartnership;
  }


  public ReportSynthesisExternalPartnership getReportSynthesisExternalPartnership() {
    return reportSynthesisExternalPartnership;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((projectPartnerPartnership == null) ? 0 : projectPartnerPartnership.hashCode());
    result = prime * result
      + ((reportSynthesisExternalPartnership == null) ? 0 : reportSynthesisExternalPartnership.hashCode());
    return result;
  }


  public void setProjectPartnerPartnership(ProjectPartnerPartnership projectPartnerPartnership) {
    this.projectPartnerPartnership = projectPartnerPartnership;
  }


  public void
    setReportSynthesisExternalPartnership(ReportSynthesisExternalPartnership reportSynthesisExternalPartnership) {
    this.reportSynthesisExternalPartnership = reportSynthesisExternalPartnership;
  }


}

