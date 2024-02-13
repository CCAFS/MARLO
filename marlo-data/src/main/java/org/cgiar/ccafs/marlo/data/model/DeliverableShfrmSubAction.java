package org.cgiar.ccafs.marlo.data.model;
// Generated Jun 27, 2017 2:55:00 PM by Hibernate Tools 4.3.1.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * ProjectInfo generated by hbm2java
 */
public class DeliverableShfrmSubAction extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -3820243690705823369L;

  @Expose
  private DeliverableShfrmPriorityAction deliverableShfrmPriorityAction;
  @Expose
  private ShfrmSubAction shfrmSubAction;
  @Expose
  private Phase phase;

  public DeliverableShfrmSubAction() {
  }

  public DeliverableShfrmPriorityAction getDeliverableShfrmPriorityAction() {
    return deliverableShfrmPriorityAction;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public Phase getPhase() {
    return phase;
  }

  public ShfrmSubAction getShfrmSubAction() {
    return shfrmSubAction;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  public void setDeliverableShfrmPriorityAction(DeliverableShfrmPriorityAction deliverableShfrmPriorityAction) {
    this.deliverableShfrmPriorityAction = deliverableShfrmPriorityAction;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setShfrmSubAction(ShfrmSubAction shfrmSubAction) {
    this.shfrmSubAction = shfrmSubAction;
  }
}