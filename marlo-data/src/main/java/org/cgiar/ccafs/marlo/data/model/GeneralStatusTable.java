package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 28, 2019 11:12:18 AM by Hibernate Tools 5.3.6.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class GeneralStatusTable extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 5297413785337764324L;
  @Expose
  private String tableName;
  @Expose
  private GeneralStatus generalStatus;


  public GeneralStatusTable() {
  }


  public GeneralStatus getGeneralStatus() {
    return this.generalStatus;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public String getTableName() {
    return this.tableName;
  }


  public void setGeneralStatus(GeneralStatus generalStatus) {
    this.generalStatus = generalStatus;
  }


  public void setTableName(String tableName) {
    this.tableName = tableName;
  }

}

