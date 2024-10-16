package org.cgiar.ccafs.marlo.data.model;
// Generated Sep 18, 2017 3:03:15 PM by Hibernate Tools 5.2.3.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Calendar;
import java.util.Date;

import com.google.gson.annotations.Expose;


/**
 * FundingSourceInfo generated by hbm2java
 */
public class FundingSourceInfo extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  /**
   * 
   */
  private static final long serialVersionUID = 7728001542598152285L;

  @Expose
  private String title;

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
  private FileDB file;

  @Expose
  private Institution directDonor;

  @Expose
  private Institution originalDonor;

  @Expose
  private Institution leadCenter;

  @Expose
  private Integer status;


  @Expose
  private BudgetType budgetType;

  @Expose
  private PartnerDivision partnerDivision;

  @Expose
  private boolean global;

  @Expose
  private Boolean w1w2;

  @Expose
  private Phase phase;

  @Expose
  private FundingSource fundingSource;
  @Expose
  private Boolean synced;
  @Expose
  private Date syncedDate;

  @Expose
  private Date extensionDate;

  @Expose
  private Double grantAmount;

  @Expose
  private FileDB fileResearch;

  @Expose
  private Boolean hasFileResearch;


  public FundingSourceInfo() {
  }

  public void copyFields(FundingSourceInfo other) {
    this.setActive(other.isActive());
    this.setActiveSince(other.getActiveSince());
    this.setBudgetType(other.getBudgetType());
    this.setContactPersonEmail(other.getContactPersonEmail());
    this.setContactPersonName(other.getContactPersonName());
    this.setCreatedBy(other.getCreatedBy());
    this.setDescription(other.getDescription());
    this.setDirectDonor(other.getDirectDonor());
    this.setEndDate(other.getEndDate());
    this.setExtensionDate(other.getExtensionDate());
    this.setFile(other.getFile());
    this.setFileResearch(other.getFileResearch());
    this.setFinanceCode(other.getFinanceCode());
    this.setFundingSource(other.getFundingSource());
    this.setGlobal(other.isGlobal());
    this.setGrantAmount(other.getGrantAmount());
    this.setHasFileResearch(other.getHasFileResearch());
    this.setLeadCenter(other.getLeadCenter());
    this.setModificationJustification(other.getModificationJustification());
    this.setModifiedBy(other.getModifiedBy());
    this.setOriginalDonor(other.getOriginalDonor());
    this.setPartnerDivision(other.getPartnerDivision());
    this.setPhase(other.getPhase());
    this.setStartDate(other.getStartDate());
    this.setStatus(other.getStatus());
    this.setSynced(other.getSynced());
    this.setSyncedDate(other.getSyncedDate());
    this.setTitle(other.getTitle());
    this.setW1w2(other.getW1w2());
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
    FundingSourceInfo other = (FundingSourceInfo) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public BudgetType getBudgetType() {
    return budgetType;
  }

  public String getComposedName() {
    try {
      return "<b> (FS" + this.fundingSource.getId() + ") " + this.getBudgetType().getName() + "</b> - " + this.title;
    } catch (Exception e) {
      return "<b> (FS" + this.fundingSource.getId() + ") </b> - " + this.title;
    }
  }


  public String getContactPersonEmail() {
    return contactPersonEmail;
  }


  public String getContactPersonName() {
    return contactPersonName;
  }

  public String getDescription() {
    return description;
  }

  public Institution getDirectDonor() {
    return directDonor;
  }

  public Date getEndDate() {
    return endDate;
  }


  public Date getExtensionDate() {
    return extensionDate;
  }


  public FileDB getFile() {
    return file;
  }


  public FileDB getFileResearch() {
    return fileResearch;
  }


  public String getFinanceCode() {
    return financeCode;
  }

  public FundingSource getFundingSource() {
    return fundingSource;
  }


  public Double getGrantAmount() {
    return grantAmount;
  }

  public Boolean getHasFileResearch() {
    return hasFileResearch;
  }


  public Institution getLeadCenter() {
    return leadCenter;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return u;
  }

  public Institution getOriginalDonor() {
    return originalDonor;
  }

  public PartnerDivision getPartnerDivision() {
    return partnerDivision;
  }

  public Phase getPhase() {
    return phase;
  }


  public Date getStartDate() {
    return startDate;
  }


  public Integer getStatus() {
    return status;
  }

  public String getStatusName() {
    if (status != null && status.intValue() != -1) {
      FundingStatusEnum statusEnum = FundingStatusEnum.getValue(status.intValue());
      if (statusEnum != null) {
        return statusEnum.getStatus();
      }
    }
    return "";
  }

  public Boolean getSynced() {
    return synced;
  }


  public Date getSyncedDate() {
    return syncedDate;
  }

  public String getTitle() {
    return title;
  }

  public Boolean getW1w2() {
    return w1w2;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  @Override
  public boolean isActive() {
    return true;
  }


  public boolean isGlobal() {
    return global;
  }


  public void setBudgetType(BudgetType budgetType) {
    this.budgetType = budgetType;
  }

  public void setContactPersonEmail(String contactPersonEmail) {
    this.contactPersonEmail = contactPersonEmail;
  }

  public void setContactPersonName(String contactPersonName) {
    this.contactPersonName = contactPersonName;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setDirectDonor(Institution institution) {
    this.directDonor = institution;
  }


  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }


  public void setExtensionDate(Date extensionDate) {
    this.extensionDate = extensionDate;
  }


  public void setFile(FileDB file) {
    this.file = file;
  }


  public void setFileResearch(FileDB fileResearch) {
    this.fileResearch = fileResearch;
  }

  public void setFinanceCode(String financeCode) {
    this.financeCode = financeCode;
  }

  public void setFundingSource(FundingSource fundingSource) {
    this.fundingSource = fundingSource;
  }

  public void setGlobal(boolean global) {
    this.global = global;
  }

  public void setGrantAmount(Double grantAmount) {
    this.grantAmount = grantAmount;
  }

  public void setHasFileResearch(Boolean hasFileResearch) {
    this.hasFileResearch = hasFileResearch;
  }

  public void setLeadCenter(Institution leadCenter) {
    this.leadCenter = leadCenter;
  }

  public void setOriginalDonor(Institution originalDonor) {
    this.originalDonor = originalDonor;
  }

  public void setPartnerDivision(PartnerDivision partnerDivision) {
    this.partnerDivision = partnerDivision;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }


  public void setStatus(Integer status) {
    this.status = status;
  }


  public void setSynced(Boolean synced) {
    this.synced = synced;
  }


  public void setSyncedDate(Date syncedDate) {
    this.syncedDate = syncedDate;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setW1w2(Boolean w1w2) {
    this.w1w2 = w1w2;
  }


  public void updateFundingSourceInfo(FundingSourceInfo fundingSourceInfoUpdate, Phase phase) {
    this.setBudgetType(fundingSourceInfoUpdate.getBudgetType());
    this.setEndDate(fundingSourceInfoUpdate.getEndDate());
    this.setStartDate(fundingSourceInfoUpdate.getStartDate());
    this.setStatus(fundingSourceInfoUpdate.getStatus());

    Calendar cal = Calendar.getInstance();
    if (fundingSourceInfoUpdate.getEndDate() != null) {
      cal.setTime(fundingSourceInfoUpdate.getEndDate());
      if (cal.get(Calendar.YEAR) < phase.getYear()) {
        if (fundingSourceInfoUpdate.getStatus() != null && fundingSourceInfoUpdate.getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
          this.setStatus(Integer.parseInt(ProjectStatusEnum.Complete.getStatusId()));
        }

      }
    }

    this.setTitle(fundingSourceInfoUpdate.getTitle());
    this.setDescription(fundingSourceInfoUpdate.getDescription());
    this.setContactPersonEmail(fundingSourceInfoUpdate.getContactPersonEmail());
    this.setContactPersonName(fundingSourceInfoUpdate.getContactPersonName());
    this.setFile(fundingSourceInfoUpdate.getFile());
    this.setFinanceCode(fundingSourceInfoUpdate.getFinanceCode());
    this.setGlobal(fundingSourceInfoUpdate.isGlobal());
    this.setDirectDonor(fundingSourceInfoUpdate.getDirectDonor());
    this.setLeadCenter(fundingSourceInfoUpdate.getLeadCenter());
    this.setPartnerDivision(fundingSourceInfoUpdate.getPartnerDivision());
    this.setW1w2(fundingSourceInfoUpdate.getW1w2());
    this.setFileResearch(fundingSourceInfoUpdate.getFileResearch());
    this.setHasFileResearch(fundingSourceInfoUpdate.getHasFileResearch());
    this.setSynced(fundingSourceInfoUpdate.getSynced());
    this.setExtensionDate(fundingSourceInfoUpdate.getExtensionDate());
    this.setSyncedDate(fundingSourceInfoUpdate.getSyncedDate());
    this.setGrantAmount(fundingSourceInfoUpdate.getGrantAmount());
  }

}

