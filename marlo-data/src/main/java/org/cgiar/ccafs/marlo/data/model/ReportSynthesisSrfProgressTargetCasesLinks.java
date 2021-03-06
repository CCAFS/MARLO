package org.cgiar.ccafs.marlo.data.model;
// Generated Feb 27, 2019 2:52:09 PM by Hibernate Tools 5.3.6.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * ReportSynthesisSrfProgressTargets generated by hbm2java
 */
public class ReportSynthesisSrfProgressTargetCasesLinks extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 1867695696627239380L;

  @Expose
  private ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTargetCases;


  @Expose
  private String linkName;


  @Expose
  private String link;


  public ReportSynthesisSrfProgressTargetCasesLinks() {
  }

  public String getLink() {
    return link;
  }

  public String getLinkName() {
    return linkName;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public ReportSynthesisSrfProgressTargetCases getReportSynthesisSrfProgressTargetCases() {
    return reportSynthesisSrfProgressTargetCases;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public void setLinkName(String linkName) {
    this.linkName = linkName;
  }

  public void setReportSynthesisSrfProgressTargetCases(
    ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTargetCases) {
    this.reportSynthesisSrfProgressTargetCases = reportSynthesisSrfProgressTargetCases;
  }

}

