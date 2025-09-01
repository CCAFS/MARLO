package org.cgiar.ccafs.marlo.data.model;
// Generated Jun 27, 2017 2:55:00 PM by Hibernate Tools 4.3.1.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;
import java.util.List;

import com.google.gson.annotations.Expose;

public class Portfolio extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -3820243690705823369L;

  @Expose
  private String name;
  @Expose
  private Date startDate;
  @Expose
  private Date endDate;
  @Expose
  private GlobalUnit globalUnit;

  private List<PortfolioPhase> portfolioPhases;
  private List<Long> selectedPhases;

  public Portfolio() {
  }

  public Date getEndDate() {
    return endDate;
  }

  public GlobalUnit getGlobalUnit() {
    return globalUnit;
  }

  @Override
  public String getLogDeatil() {
    // TODO Auto-generated method stub
    return null;
  }

  public String getName() {
    return name;
  }

  public List<PortfolioPhase> getPortfolioPhases() {
    return portfolioPhases;
  }

  public List<Long> getSelectedPhases() {
    return selectedPhases;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public void setGlobalUnit(GlobalUnit globalUnit) {
    this.globalUnit = globalUnit;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPortfolioPhases(List<PortfolioPhase> portfolioPhases) {
    this.portfolioPhases = portfolioPhases;
  }

  public void setSelectedPhases(List<Long> selectedPhases) {
    this.selectedPhases = selectedPhases;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }
}