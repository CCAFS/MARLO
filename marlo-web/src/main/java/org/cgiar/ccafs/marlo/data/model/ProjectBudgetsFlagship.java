package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 23, 2018 9:31:16 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;


public class ProjectBudgetsFlagship extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -7900520122724765838L;

  @Expose
  private Phase phase;

  @Expose
  private BudgetType budgetType;

  @Expose
  private CrpProgram crpProgram;

  private Project project;

  @Expose
  private Double amount;

  @Expose
  private int year;

  public ProjectBudgetsFlagship() {
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
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public Double getAmount() {
    return amount;
  }

  public BudgetType getBudgetType() {
    return budgetType;
  }

  public CrpProgram getCrpProgram() {
    return crpProgram;
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
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public void setBudgetType(BudgetType budgetType) {
    this.budgetType = budgetType;
  }

  public void setCrpProgram(CrpProgram crpProgram) {
    this.crpProgram = crpProgram;
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
    return "ProjectBudgetsFlagship [id=" + this.getId() + ", budgetType=" + budgetType + ", crpProgram=" + crpProgram
      + ", project=" + project + ", amount=" + amount + ", year=" + year + "]";
  }

}

