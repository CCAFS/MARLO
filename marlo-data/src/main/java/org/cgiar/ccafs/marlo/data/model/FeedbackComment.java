package org.cgiar.ccafs.marlo.data.model;
// Generated Apr 30, 2018 10:52:36 AM by Hibernate Tools 3.4.0.CR1

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.Expose;

/**
 * ProjectExpectedStudyInfo generated by hbm2java
 */
public class FeedbackComment extends MarloBaseEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -7806050142645120199L;

  @Expose
  private String comment;

  @Expose
  private User user;

  @Expose
  private Date commentDate;

  public FeedbackComment() {
  }

  public FeedbackComment(String comment, User user, Date commentDate) {
    super();
    this.comment = comment;
    this.user = user;
    this.commentDate = commentDate;
  }

  public Map<String, Object> convertToMap() {
    ObjectMapper oMapper = new ObjectMapper();
    Map<String, Object> map = oMapper.convertValue(this, Map.class);

    return map;
  }

  public String getComment() {
    return comment;
  }

  public Date getCommentDate() {
    return commentDate;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  @Override
  public String getModificationJustification() {
    return "";
  }

  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return u;
  }

  public User getUser() {
    return user;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public void setCommentDate(Date commentDate) {
    this.commentDate = commentDate;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
    // TODO Auto-generated method stub
  }

  public void setUser(User user) {
    this.user = user;
  }

}

