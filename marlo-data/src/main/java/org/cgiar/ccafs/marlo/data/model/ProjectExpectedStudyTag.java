package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class ProjectExpectedStudyTag extends MarloBaseEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 6466188559245022941L;

  @Expose
  private String tagName;
  @Expose
  private String tagDescription;

  public ProjectExpectedStudyTag() {
  }

  @Override
  public String getLogDeatil() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getModificationJustification() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public User getModifiedBy() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getTagDescription() {
    return tagDescription;
  }

  public String getTagName() {
    return tagName;
  }

  @Override
  public boolean isActive() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
    // TODO Auto-generated method stub
  }

  public void setTagDescription(String tagDescription) {
    this.tagDescription = tagDescription;
  }

  public void setTagName(String tagName) {
    this.tagName = tagName;
  }
}

