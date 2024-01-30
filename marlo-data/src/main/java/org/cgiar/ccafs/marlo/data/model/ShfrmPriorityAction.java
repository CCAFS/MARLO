package org.cgiar.ccafs.marlo.data.model;
// Generated Jun 27, 2017 2:55:00 PM by Hibernate Tools 4.3.1.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * ProjectInfo generated by hbm2java
 */
public class ShfrmPriorityAction extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -3820243690705823369L;

  @Expose
  private String name;
  @Expose
  private String description;

  private Set<ShfrmSubAction> shfrmSubAction = new HashSet<ShfrmSubAction>(0);
  private List<ShfrmSubAction> shfrmSubActions = new ArrayList<ShfrmSubAction>();

  public ShfrmPriorityAction() {
  }

  public String getDescription() {
    return description;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public String getName() {
    return name;
  }

  public Set<ShfrmSubAction> getShfrmSubAction() {
    return shfrmSubAction;
  }

  public List<ShfrmSubAction> getShfrmSubActions() {
    return shfrmSubActions;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setShfrmSubAction(Set<ShfrmSubAction> shfrmSubAction) {
    this.shfrmSubAction = shfrmSubAction;
  }

  public void setShfrmSubActions(List<ShfrmSubAction> shfrmSubActions) {
    this.shfrmSubActions = shfrmSubActions;
  }

}
