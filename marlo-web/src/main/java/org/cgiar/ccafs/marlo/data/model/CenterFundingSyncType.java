package org.cgiar.ccafs.marlo.data.model;
// Generated Sep 25, 2017 10:58:01 AM by Hibernate Tools 3.4.0.CR1


import com.google.gson.annotations.Expose;


public class CenterFundingSyncType extends MarloAuditableEntity implements java.io.Serializable {


  private static final long serialVersionUID = -8457872961428509233L;

  @Expose
  private String syncName;

  public CenterFundingSyncType() {
  }


  public String getSyncName() {
    return syncName;
  }


  public void setSyncName(String syncName) {
    this.syncName = syncName;
  }


}

