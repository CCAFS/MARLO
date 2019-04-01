package org.cgiar.ccafs.marlo.data.model;
// Generated Jun 20, 2018 1:50:25 PM by Hibernate Tools 3.4.0.CR1

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class ReportSynthesisMeliaEvaluationAction extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -5287996707368548665L;

  @Expose
  private ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation;

  @Expose
  private String actions;

  @Expose
  private String textWhom;

  @Expose
  private String textWhen;


  public ReportSynthesisMeliaEvaluationAction() {
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
    ReportSynthesisMeliaEvaluationAction other = (ReportSynthesisMeliaEvaluationAction) obj;
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


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public ReportSynthesisMeliaEvaluation getReportSynthesisMeliaEvaluation() {
    return reportSynthesisMeliaEvaluation;
  }


  public String getTextWhen() {
    return textWhen;
  }


  public String getTextWhom() {
    return textWhom;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
      prime * result + ((reportSynthesisMeliaEvaluation == null) ? 0 : reportSynthesisMeliaEvaluation.hashCode());
    return result;
  }


  public void setActions(String actions) {
    this.actions = actions;
  }


  public void setReportSynthesisMeliaEvaluation(ReportSynthesisMeliaEvaluation reportSynthesisMeliaEvaluation) {
    this.reportSynthesisMeliaEvaluation = reportSynthesisMeliaEvaluation;
  }


  public void setTextWhen(String textWhen) {
    this.textWhen = textWhen;
  }


  public void setTextWhom(String textWhom) {
    this.textWhom = textWhom;
  }


}

