package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 23, 2018 9:31:16 AM by Hibernate Tools 3.4.0.CR1


import java.util.Date;

import com.google.gson.annotations.Expose;


public class ProjectBudgetsFlagship implements java.io.Serializable {

  private static final long serialVersionUID = -7900520122724765838L;

  @Expose
  private Long id;

  @Expose
  private Phase phase;

  @Expose
  private User modifiedBy;

  @Expose
  private User createdBy;

  @Expose
  private BudgetType budgetType;

  @Expose
  private CrpProgram crpProgram;

  private Project project;

  @Expose
  private Double amount;

  @Expose
  private int year;

  @Expose
  private boolean active;

  @Expose
  private Date activeSince;

  @Expose
  private String modificationJustification;


  public ProjectBudgetsFlagship() {
  }

  public ProjectBudgetsFlagship(Long id, Phase phase, User modifiedBy, User createdBy, BudgetType budgetType,
    CrpProgram crpProgram, Project project, Double amount, int year, boolean active, Date activeSince,
    String modificationJustification) {
    super();
    this.id = id;
    this.phase = phase;
    this.modifiedBy = modifiedBy;
    this.createdBy = createdBy;
    this.budgetType = budgetType;
    this.crpProgram = crpProgram;
    this.project = project;
    this.amount = amount;
    this.year = year;
    this.active = active;
    this.activeSince = activeSince;
    this.modificationJustification = modificationJustification;
  }

  public Date getActiveSince() {
    return activeSince;
  }

  public Double getAmount() {
    return amount;
  }

  public BudgetType getBudgetType() {
    return budgetType;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public CrpProgram getCrpProgram() {
    return crpProgram;
  }

  public Long getId() {
    return id;
  }

  public String getModificationJustification() {
    return modificationJustification;
  }

  public User getModifiedBy() {
    return modifiedBy;
  }

  public Phase getPhase() {
    return phase;
  }

  public Project getProject() {
    return project;
  }

  public int getYear() {
    return year;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public void setBudgetType(BudgetType budgetType) {
    this.budgetType = budgetType;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setCrpProgram(CrpProgram crpProgram) {
    this.crpProgram = crpProgram;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setYear(int year) {
    this.year = year;
  }


}

