package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class FeedbackStatus extends MarloBaseEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 6466188559245022941L;

  @Expose
  private String statusName;

  @Expose
  private String statusDescription;


  public FeedbackStatus() {
  }

  @Override
  public String getLogDeatil() {
    return null;
  }

  @Override
  public String getModificationJustification() {
    return null;
  }

  @Override
  public User getModifiedBy() {
    return null;
  }

  public String getStatusDescription() {
    return statusDescription;
  }

  public String getStatusName() {
    return statusName;
  }

  @Override
  public boolean isActive() {
    return false;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
  }

  public void setStatusDescription(String statusDescription) {
    this.statusDescription = statusDescription;
  }

  public void setStatusName(String statusName) {
    this.statusName = statusName;
  }
}

