package org.cgiar.ccafs.marlo.data.model;
// Generated Jun 20, 2018 1:50:25 PM by Hibernate Tools 3.4.0.CR1

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * ReportSynthesisMeliaEvaluation generated by hbm2java
 */
public class ReportSynthesisMeliaEvaluation extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -5287996707368548665L;


  @Expose
  private ReportSynthesisMelia reportSynthesisMelia;


  @Expose
  private String nameEvaluation;


  @Expose
  private String recommendation;

  @Expose
  private String managementResponse;

  @Expose
  private Integer status;

  @Expose
  private String actions;

  @Expose
  private String textWhom;

  @Expose
  private String textWhen;

  @Expose
  private String comments;


  public ReportSynthesisMeliaEvaluation() {
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
    ReportSynthesisMeliaEvaluation other = (ReportSynthesisMeliaEvaluation) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public String getActions() {
    return actions;
  }


  public String getComments() {
    return comments;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public String getManagementResponse() {
    return managementResponse;
  }

  public String getNameEvaluation() {
    return nameEvaluation;
  }

  public String getRecommendation() {
    return recommendation;
  }


  public ReportSynthesisMelia getReportSynthesisMelia() {
    return reportSynthesisMelia;
  }


  public Integer getStatus() {
    return status;
  }

  public String getTextWhen() {
    return textWhen;
  }


  public String getTextWhom() {
    return textWhom;
  }


  public void setActions(String actions) {
    this.actions = actions;
  }


  public void setComments(String comments) {
    this.comments = comments;
  }


  public void setManagementResponse(String managementResponse) {
    this.managementResponse = managementResponse;
  }


  public void setNameEvaluation(String nameEvaluation) {
    this.nameEvaluation = nameEvaluation;
  }


  public void setRecommendation(String recommendation) {
    this.recommendation = recommendation;
  }

  public void setReportSynthesisMelia(ReportSynthesisMelia reportSynthesisMelia) {
    this.reportSynthesisMelia = reportSynthesisMelia;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public void setTextWhen(String textWhen) {
    this.textWhen = textWhen;
  }

  public void setTextWhom(String textWhom) {
    this.textWhom = textWhom;
  }


}

