package org.cgiar.ccafs.marlo.data.model;
// Generated Feb 2, 2018 8:27:14 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class PowbManagementRisk extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -1269295436737945689L;

  private PowbSynthesis powbSynthesis;

  @Expose
  private String highlight;

  public PowbManagementRisk() {
  }

  public String getHighlight() {
    return highlight;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public PowbSynthesis getPowbSynthesis() {
    return powbSynthesis;
  }

  public void setHighlight(String highlight) {
    this.highlight = highlight;
  }

  public void setPowbSynthesis(PowbSynthesis powbSynthesis) {
    this.powbSynthesis = powbSynthesis;
  }

}

