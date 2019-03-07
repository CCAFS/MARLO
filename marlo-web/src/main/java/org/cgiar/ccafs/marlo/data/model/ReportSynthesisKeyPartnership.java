package org.cgiar.ccafs.marlo.data.model;
// Generated Mar 7, 2019 7:42:16 AM by Hibernate Tools 5.3.6.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * ReportSynthesisKeyPartnerships generated by hbm2java
 */
public class ReportSynthesisKeyPartnership extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -5667515346500430493L;

  private ReportSynthesis reportSynthesis;


  @Expose
  private String summary;


  private Set<ReportSynthesisKeyPartnershipExternal> reportSynthesisKeyPartnershipExternals =
    new HashSet<ReportSynthesisKeyPartnershipExternal>(0);

  private List<ReportSynthesisKeyPartnershipExternal> partnerships;


  public ReportSynthesisKeyPartnership() {
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
    ReportSynthesisKeyPartnership other = (ReportSynthesisKeyPartnership) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
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


  public List<ReportSynthesisKeyPartnershipExternal> getPartnerships() {
    return partnerships;
  }


  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }


  public Set<ReportSynthesisKeyPartnershipExternal> getReportSynthesisKeyPartnershipExternals() {
    return reportSynthesisKeyPartnershipExternals;
  }

  public String getSummary() {
    return summary;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setPartnerships(List<ReportSynthesisKeyPartnershipExternal> partnerships) {
    this.partnerships = partnerships;
  }


  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }


  public void setReportSynthesisKeyPartnershipExternals(
    Set<ReportSynthesisKeyPartnershipExternal> reportSynthesisKeyPartnershipExternals) {
    this.reportSynthesisKeyPartnershipExternals = reportSynthesisKeyPartnershipExternals;
  }


  public void setSummary(String summary) {
    this.summary = summary;
  }


}

