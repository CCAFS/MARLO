package org.cgiar.ccafs.marlo.data.model;
// Generated Oct 27, 2016 3:39:29 PM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * FundingSource generated by hbm2java
 */
public class FundingSource implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -3854119067580692258L;


  @Expose
  private Long id;

  @Expose
  private User modifiedBy;


  @Expose
  private User createdBy;

  @Expose
  private Institution institution;


  @Expose
  private String description;

  @Expose
  private Date startDate;

  @Expose
  private Date endDate;

  @Expose
  private String financeCode;

  @Expose
  private String contactPersonName;

  @Expose
  private String contactPersonEmail;

  @Expose
  private Integer centerType;

  @Expose
  private BudgetType budgetType;

  @Expose
  private boolean active;

  @Expose
  private Date activeSince;

  @Expose
  private String modificationJustification;

  @Expose
  private Crp crp;

  private Set<FundingSourceBudget> fundingSourceBudgets = new HashSet<FundingSourceBudget>(0);

  private Set<ProjectBudget> projectBudgets = new HashSet<ProjectBudget>(0);

  public FundingSource() {
  }

  public FundingSource(User modifiedBy, boolean active, Date activeSince, String modificationJustification) {
    this.modifiedBy = modifiedBy;
    this.active = active;
    this.activeSince = activeSince;
    this.modificationJustification = modificationJustification;
  }

  public FundingSource(User modifiedBy, User createdBy, Institution institution, String description, Date startDate,
    Date endDate, String financeCode, String contactPersonName, String contactPersonEmail, Integer centerType,
    BudgetType type, boolean active, Date activeSince, String modificationJustification,
    Set<FundingSourceBudget> fundingSourceBudgets, Set<ProjectBudget> projectBudgets, Crp crp) {
    this.modifiedBy = modifiedBy;
    this.createdBy = createdBy;
    this.institution = institution;
    this.description = description;
    this.startDate = startDate;
    this.endDate = endDate;
    this.financeCode = financeCode;
    this.contactPersonName = contactPersonName;
    this.contactPersonEmail = contactPersonEmail;
    this.centerType = centerType;
    this.budgetType = type;
    this.active = active;
    this.activeSince = activeSince;
    this.modificationJustification = modificationJustification;
    this.fundingSourceBudgets = fundingSourceBudgets;
    this.projectBudgets = projectBudgets;
    this.crp = crp;
  }

  public Date getActiveSince() {
    return activeSince;
  }

  public BudgetType getBudgetType() {
    return budgetType;
  }

  public Integer getCenterType() {
    return centerType;
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


  public String getDescription() {
    return description;
  }


  public Date getEndDate() {
    return endDate;
  }


  public String getFinanceCode() {
    return financeCode;
  }


  public Set<FundingSourceBudget> getFundingSourceBudgets() {
    return fundingSourceBudgets;
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

  public void setBudgetType(BudgetType budgetType) {
    this.budgetType = budgetType;
  }

  public void setCenterType(Integer centerType) {
    this.centerType = centerType;
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

  public void setDescription(String description) {
    this.description = description;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public void setFinanceCode(String financeCode) {
    this.financeCode = financeCode;
  }

  public void setFundingSourceBudgets(Set<FundingSourceBudget> fundingSourceBudgets) {
    this.fundingSourceBudgets = fundingSourceBudgets;
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

  public void setProjectBudgets(Set<ProjectBudget> projectBudgets) {
    this.projectBudgets = projectBudgets;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }


}

