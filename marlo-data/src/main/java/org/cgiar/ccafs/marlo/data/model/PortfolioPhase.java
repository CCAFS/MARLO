package org.cgiar.ccafs.marlo.data.model;
// Generated Jun 27, 2017 2:55:00 PM by Hibernate Tools 4.3.1.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

public class PortfolioPhase extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -3820243690705823369L;

  @Expose
  private Portfolio portfolio;
  @Expose
  private Phase phase;


  public PortfolioPhase() {
  }

  @Override
  public String getLogDeatil() {
    // TODO Auto-generated method stub
    return null;
  }

  public Phase getPhase() {
    return phase;
  }

  public Portfolio getPortfolio() {
    return portfolio;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setPortfolio(Portfolio portfolio) {
    this.portfolio = portfolio;
  }
}