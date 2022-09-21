package org.cgiar.ccafs.marlo.data.model;
// Generated Apr 30, 2018 10:52:36 AM by Hibernate Tools 3.4.0.CR1

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;

public class FeedbackRolesPermission extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -7806050142645120199L;

  @Expose
  private String description;
  @Expose
  private Role role;
  @Expose
  private FeedbackPermission feedbackPermission;
  @Expose
  private ClusterType clusterType;

  public FeedbackRolesPermission() {
  }

  public Map<String, Object> convertToMap() {
    ObjectMapper oMapper = new ObjectMapper();
    Map<String, Object> map = oMapper.convertValue(this, Map.class);

    return map;
  }

  public ClusterType getClusterType() {
    return clusterType;
  }

  public String getDescription() {
    return description;
  }

  public FeedbackPermission getFeedbackPermission() {
    return feedbackPermission;
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

  public Role getRole() {
    return role;
  }

  @Override
  public boolean isActive() {
    // TODO Auto-generated method stub
    return false;
  }


  public void setClusterType(ClusterType clusterType) {
    this.clusterType = clusterType;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public void setFeedbackPermission(FeedbackPermission feedbackPermission) {
    this.feedbackPermission = feedbackPermission;
  }


  @Override
  public void setModifiedBy(User modifiedBy) {
    // TODO Auto-generated method stub

  }


  public void setRole(Role role) {
    this.role = role;
  }

}