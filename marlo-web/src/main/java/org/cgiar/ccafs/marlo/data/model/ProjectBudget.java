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

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * @author hjimenez
 */
public class ProjectBudget implements java.io.Serializable, IAuditLog {


  /**
   * 
   */
  private static final long serialVersionUID = -6117852720583204865L;


  @Expose
  private Long id;

  @Expose
  private Institution institution;

  private Project project;
  @Expose
  private User createdBy;

  @Expose
  private User modifiedBy;
  @Expose
  private Double amount;
  @Expose
  private BudgetType budgetType;
  @Expose
  private int year;
  @Expose
  private Double genderPercentage;
  @Expose
  private Double genderValue;
  @Expose
  private boolean active;
  @Expose
  private FundingSource fundingSource;
  @Expose
  private Date activeSince;

  @Expose
  private Phase phase;


  @Expose
  private String modificationJustification;


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
    ProjectBudget other = (ProjectBudget) obj;
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

  public FundingSource getFundingSource() {
    return fundingSource;
  }

  public Double getGenderPercentage() {
    return genderPercentage;
  }

  public Double getGenderValue() {
    return genderValue;
  }

  @Override
  public Long getId() {
    return id;
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

  public void setFundingSource(FundingSource fundingSource) {
    this.fundingSource = fundingSource;
  }


  public void setGenderPercentage(Double genderPercentage) {
    this.genderPercentage = genderPercentage;
  }

  public void setGenderValue(Double genderValue) {
    this.genderValue = genderValue;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setInstitution(Institution institution) {
    this.institution = institution;
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

