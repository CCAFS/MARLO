package org.cgiar.ccafs.marlo.data.model;
// Generated Feb 2, 2018 8:27:14 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class PowbCrossCuttingDimension extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -7015424498651976768L;

  private PowbSynthesis powbSynthesis;

  @Expose
  private String summarize;


  @Expose
  private String assets;

  public PowbCrossCuttingDimension() {
  }

  public String getAssets() {
    return assets;
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

  public String getSummarize() {
    return summarize;
  }

  public void setAssets(String assets) {
    this.assets = assets;
  }

  public void setPowbSynthesis(PowbSynthesis powbSynthesis) {
    this.powbSynthesis = powbSynthesis;
  }

  public void setSummarize(String summarize) {
    this.summarize = summarize;
  }


}

