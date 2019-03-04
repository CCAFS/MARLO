/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.data.model;


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * @author Andrés Valencia
 */
public class ProjectBudgetExecution extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -3758446773441073234L;


  private Project project;
  @Expose
  private Institution institution;
  @Expose
  private Phase phase;
  @Expose
  private BudgetType budgetType;
  @Expose
  private int year;
  @Expose
  private Double actualExpenditure;

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    ProjectBudgetExecution other = (ProjectBudgetExecution) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public Double getActualExpenditure() {
    return actualExpenditure;
  }


  public BudgetType getBudgetType() {
    return budgetType;
  }

  public Institution getInstitution() {
    return institution;
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


  public void setActualExpenditure(Double actualExpenditure) {
    this.actualExpenditure = actualExpenditure;
  }


  public void setBudgetType(BudgetType budgetType) {
    this.budgetType = budgetType;
  }


  public void setInstitution(Institution institution) {
    this.institution = institution;
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
    return "ProjectBudget [id=" + this.getId() + ", project=" + project + ", institution=" + institution + ", phase="
      + phase + ", budget type=" + budgetType + ", year=" + year + ", actualExpenditure=" + actualExpenditure + "]";
  }


}

