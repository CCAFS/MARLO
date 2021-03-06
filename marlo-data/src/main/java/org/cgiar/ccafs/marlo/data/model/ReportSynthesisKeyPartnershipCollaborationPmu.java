package org.cgiar.ccafs.marlo.data.model;
// Generated Jun 20, 2018 1:50:25 PM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * ReportSynthesisMeliaStudy generated by hbm2java
 */
public class ReportSynthesisKeyPartnershipCollaborationPmu extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -8921060167406865061L;


  @Expose
  private ReportSynthesisKeyPartnership reportSynthesisKeyPartnership;


  @Expose
  private ReportSynthesisKeyPartnershipCollaboration reportSynthesisKeyPartnershipCollaboration;


  public ReportSynthesisKeyPartnershipCollaborationPmu() {
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
    ReportSynthesisKeyPartnershipCollaborationPmu other = (ReportSynthesisKeyPartnershipCollaborationPmu) obj;
    if (this.getReportSynthesisKeyPartnershipCollaboration() == null) {
      if (other.getReportSynthesisKeyPartnershipCollaboration() != null) {
        return false;
      }
    } else if (!this.getReportSynthesisKeyPartnershipCollaboration()
      .equals(other.getReportSynthesisKeyPartnershipCollaboration())) {
      return false;
    }
    if (this.getReportSynthesisKeyPartnership() == null) {
      if (other.getReportSynthesisKeyPartnership() != null) {
        return false;
      }
    } else if (!this.getReportSynthesisKeyPartnership().getId()
      .equals(other.getReportSynthesisKeyPartnership().getId())) {
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


  public ReportSynthesisKeyPartnership getReportSynthesisKeyPartnership() {
    return reportSynthesisKeyPartnership;
  }


  public ReportSynthesisKeyPartnershipCollaboration getReportSynthesisKeyPartnershipCollaboration() {
    return reportSynthesisKeyPartnershipCollaboration;
  }

  public void setReportSynthesisKeyPartnership(ReportSynthesisKeyPartnership reportSynthesisKeyPartnership) {
    this.reportSynthesisKeyPartnership = reportSynthesisKeyPartnership;
  }


  public void setReportSynthesisKeyPartnershipCollaboration(
    ReportSynthesisKeyPartnershipCollaboration reportSynthesisKeyPartnershipCollaboration) {
    this.reportSynthesisKeyPartnershipCollaboration = reportSynthesisKeyPartnershipCollaboration;
  }


}

