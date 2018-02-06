package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 23, 2018 9:31:16 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;


public class ProjectBudgetsFlagship implements java.io.Serializable, IAuditLog {

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

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    ProjectBudgetsFlagship other = (ProjectBudgetsFlagship) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
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

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  @Override
  public String getModificationJustification() {
    return modificationJustification;
  }

  @Override
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
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

  @Override
  public String toString() {
    return "ProjectBudgetsFlagship [id=" + id + ", budgetType=" + budgetType + ", crpProgram=" + crpProgram
      + ", project=" + project + ", amount=" + amount + ", year=" + year + "]";
  }

}

