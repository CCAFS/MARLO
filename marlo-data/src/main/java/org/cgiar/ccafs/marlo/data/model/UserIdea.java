package org.cgiar.ccafs.marlo.data.model;
// Generated Jun 27, 2017 2:55:00 PM by Hibernate Tools 4.3.1.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class UserIdea extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -3820243690705823369L;

  @Expose
  private String question;
  @Expose
  private String answer;

  public UserIdea() {
  }

  public String getAnswer() {
    return answer;
  }

  @Override
  public String getLogDeatil() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getQuestion() {
    return question;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public void setQuestion(String question) {
    this.question = question;
  }
}