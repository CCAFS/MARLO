package org.cgiar.ccafs.marlo.data.model;
// Generated Sep 25, 2017 10:58:01 AM by Hibernate Tools 3.4.0.CR1


import java.util.Date;

import com.google.gson.annotations.Expose;


public class CenterFundingSyncType implements java.io.Serializable {


  private static final long serialVersionUID = -8457872961428509233L;

  @Expose
  private Long id;

  @Expose
  private User modifiedBy;

  @Expose
  private User createdBy;

  @Expose
  private String syncName;

  @Expose
  private boolean active;

  @Expose
  private Date activeSince;

  @Expose
  private String modificationJustification;


  public CenterFundingSyncType() {
  }


  public CenterFundingSyncType(Long id, User modifiedBy, User createdBy, String syncName, boolean active,
    Date activeSince, String modificationJustification) {
    super();
    this.id = id;
    this.modifiedBy = modifiedBy;
    this.createdBy = createdBy;
    this.syncName = syncName;
    this.active = active;
    this.activeSince = activeSince;
    this.modificationJustification = modificationJustification;
  }


  public Date getActiveSince() {
    return activeSince;
  }


  public User getCreatedBy() {
    return createdBy;
  }


  public Long getId() {
    return id;
  }


  public String getModificationJustification() {
    return modificationJustification;
  }


  public User getModifiedBy() {
    return modifiedBy;
  }

  public String getSyncName() {
    return syncName;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }


  public void setSyncName(String syncName) {
    this.syncName = syncName;
  }


}

