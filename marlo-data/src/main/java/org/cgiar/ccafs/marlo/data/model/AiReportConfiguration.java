package org.cgiar.ccafs.marlo.data.model;
// Generated Apr 30, 2018 10:52:36 AM by Hibernate Tools 3.4.0.CR1

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class AiReportConfiguration extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -7806050142645120199L;

  @Expose
  private String reportTitle;
  @Expose
  private String reportDescription;
  @Expose
  private String buttonLabel;
  @Expose
  private String buttonLink;

  public AiReportConfiguration() {
  }

  public String getButtonLabel() {
    return buttonLabel;
  }

  public String getButtonLink() {
    return buttonLink;
  }

  @Override
  public String getLogDeatil() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getReportDescription() {
    return reportDescription;
  }

  public String getReportTitle() {
    return reportTitle;
  }

  public void setButtonLabel(String buttonLabel) {
    this.buttonLabel = buttonLabel;
  }

  public void setButtonLink(String buttonLink) {
    this.buttonLink = buttonLink;
  }

  public void setReportDescription(String reportDescription) {
    this.reportDescription = reportDescription;
  }

  public void setReportTitle(String reportTitle) {
    this.reportTitle = reportTitle;
  }
}