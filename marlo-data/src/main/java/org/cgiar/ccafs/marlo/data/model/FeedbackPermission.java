package org.cgiar.ccafs.marlo.data.model;
// Generated Apr 30, 2018 10:52:36 AM by Hibernate Tools 3.4.0.CR1

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;

public class FeedbackPermission extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -7806050142645120199L;

  @Expose
  private String name;
  @Expose
  private String description;

  public FeedbackPermission() {
  }

  public Map<String, Object> convertToMap() {
    ObjectMapper oMapper = new ObjectMapper();
    Map<String, Object> map = oMapper.convertValue(this, Map.class);

    return map;
  }

  public String getDescription() {
    return description;
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

  public String getName() {
    return name;
  }

  @Override
  public boolean isActive() {
    // TODO Auto-generated method stub
    return false;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
    // TODO Auto-generated method stub

  }

  public void setName(String name) {
    this.name = name;
  }

}