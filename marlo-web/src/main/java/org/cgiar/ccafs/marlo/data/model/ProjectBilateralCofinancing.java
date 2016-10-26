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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * @author hjimenez
 */
public class ProjectBilateralCofinancing implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -5725787908570331052L;


  @Expose
  private Long id;

  @Expose
  private Institution institution;


  @Expose
  private LiaisonInstitution liaisonInstitution;

  @Expose
  private User createdBy;


  @Expose
  private User modifiedBy;

  @Expose
  private String title;


  @Expose
  private Date startDate;


  @Expose
  private Date endDate;


  @Expose
  private Integer agreement;


  @Expose
  private boolean active;


  @Expose
  private Date activeSince;


  @Expose
  private String modificationJustification;


  @Expose
  private Long budget;


  @Expose
  private String financeCode;
  @Expose
  private String contactPersonName;
  @Expose
  private String contactPersonEmail;
  @Expose
  private Integer cofundedMode;
  private Crp crp;

  private Set<ProjectBudget> projectBudgets = new HashSet<ProjectBudget>(0);

  private List<ProjectBudget> budgets;

  public Date getActiveSince() {
    return activeSince;
  }

  public Integer getAgreement() {
    return agreement;
  }

  public Long getBudget() {
    return budget;
  }

  public List<ProjectBudget> getBudgets() {
    return budgets;
  }

  public Integer getCofundedMode() {
    return cofundedMode;
  }

  public String getContactPersonEmail() {
    return contactPersonEmail;
  }

  public String getContactPersonName() {
    return contactPersonName;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public Crp getCrp() {
    return crp;
  }

  public Date getEndDate() {
    return endDate;
  }

  public String getFinanceCode() {
    return financeCode;
  }

  @Override
  public Long getId() {
    return id;
  }

  public Institution getInstitution() {
    return institution;
  }

  public LiaisonInstitution getLiaisonInstitution() {
    return liaisonInstitution;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public String getModificationJustification() {
    return modificationJustification;
  }


  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  public Set<ProjectBudget> getProjectBudgets() {
    return projectBudgets;
  }

  public Date getStartDate() {
    return startDate;
  }

  public String getStatusName() {
    if (this.agreement != null) {
      return AgreementStatusEnum.getValue(this.agreement).getStatus() != null
        ? AgreementStatusEnum.getValue(this.agreement).getStatus() : "";
    } else {
      return "";
    }
  }

  public String getTitle() {
    return title;
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

  public void setAgreement(Integer agreement) {
    this.agreement = agreement;
  }

  public void setBudget(Long budget) {
    this.budget = budget;
  }

  public void setBudgets(List<ProjectBudget> budgets) {
    this.budgets = budgets;
  }

  public void setCofundedMode(Integer cofundedMode) {
    this.cofundedMode = cofundedMode;
  }

  public void setContactPersonEmail(String contactPersonEmail) {
    this.contactPersonEmail = contactPersonEmail;
  }

  public void setContactPersonName(String contactPersonName) {
    this.contactPersonName = contactPersonName;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setCrp(Crp crp) {
    this.crp = crp;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public void setFinanceCode(String financeCode) {
    this.financeCode = financeCode;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setInstitution(Institution institution) {
    this.institution = institution;
  }

  public void setLiaisonInstitution(LiaisonInstitution liaisonInstitution) {
    this.liaisonInstitution = liaisonInstitution;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setProjectBudgets(Set<ProjectBudget> projectBudgets) {
    this.projectBudgets = projectBudgets;
  }


  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}

