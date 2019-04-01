package org.cgiar.ccafs.marlo.data.model;
// Generated May 24, 2018 5:28:55 PM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class ReportSynthesisNarrative extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 7026165672280994382L;


  private ReportSynthesis reportSynthesis;

  @Expose
  private String narrative;


  public ReportSynthesisNarrative() {
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public String getNarrative() {
    return narrative;
  }


  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }


  public void setNarrative(String narrative) {
    this.narrative = narrative;
  }


  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }


}

