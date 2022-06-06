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
public class FeedbackQAComment extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -7806050142645120199L;

  @Expose
  private Phase phase;
  @Expose
  private FeedbackQACommentableFields field;
  @Expose
  private long parentId;
  @Expose
  private String comment;
  @Expose
  private String fieldValue;
  @Expose
  private String status;
  @Expose
  private FeedbackQAReply reply;
  @Expose
  private User user;
  @Expose
  private User userApproval;
  @Expose
  private Date commentDate;
  @Expose
  private Date approvalDate;
  @Expose
  private Project project;
  @Expose
  private String link;
  @Expose
  private String fieldDescription;
  @Expose
  private String parentFieldDescription;

  public FeedbackQAComment() {
  }

  public Map<String, Object> convertToMap() {
    ObjectMapper oMapper = new ObjectMapper();
    Map<String, Object> map = oMapper.convertValue(this, Map.class);

    return map;
  }

  public Date getApprovalDate() {
    return approvalDate;
  }

  public String getComment() {
    return comment;
  }

  public Date getCommentDate() {
    return commentDate;
  }

  public FeedbackQACommentableFields getField() {
    return field;
  }

  public String getFieldDescription() {
    return fieldDescription;
  }

  public String getFieldValue() {
    return fieldValue;
  }

  public String getLink() {
    return link;
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

  public String getParentFieldDescription() {
    return parentFieldDescription;
  }

  public long getParentId() {
    return parentId;
  }

  public Phase getPhase() {
    return phase;
  }

  public Project getProject() {
    return project;
  }

  public FeedbackQAReply getReply() {
    return reply;
  }

  public String getStatus() {
    return status;
  }

  public User getUser() {
    return user;
  }

  public User getUserApproval() {
    return userApproval;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  public void setApprovalDate(Date approvalDate) {
    this.approvalDate = approvalDate;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public void setCommentDate(Date commentDate) {
    this.commentDate = commentDate;
  }

  public void setField(FeedbackQACommentableFields field) {
    this.field = field;
  }

  public void setFieldDescription(String fieldDescription) {
    this.fieldDescription = fieldDescription;
  }

  public void setFieldValue(String fieldValue) {
    this.fieldValue = fieldValue;
  }

  public void setLink(String link) {
    this.link = link;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
    // TODO Auto-generated method stub
  }

  public void setParentFieldDescription(String parentFieldDescription) {
    this.parentFieldDescription = parentFieldDescription;
  }

  public void setParentId(long parentId) {
    this.parentId = parentId;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setReply(FeedbackQAReply reply) {
    this.reply = reply;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setUserApproval(User userApproval) {
    this.userApproval = userApproval;
  }
}

