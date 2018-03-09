package org.cgiar.ccafs.marlo.data.model;
// Generated Feb 5, 2018 11:32:15 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * PowbEvidence generated by hbm2java
 */
public class PowbFinancialPlannedBudget implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -8817130891128431256L;


  @Expose
  private Long id;

  @Expose
  private PowbSynthesis powbSynthesis;

  @Expose
  private PowbExpenditureAreas powbExpenditureArea;

  @Expose
  private LiaisonInstitution liaisonInstitution;

  @Expose
  private Double w1w2;

  @Expose
  private Double w3Bilateral;

  @Expose
  private Double centerFunds;


  @Expose
  private Double carry;


  @Expose
  private String comments;


  @Expose
  private boolean active;


  @Expose
  private Date activeSince;


  @Expose
  private User createdBy;


  @Expose
  private User modifiedBy;


  @Expose
  private String modificationJustification;


  private boolean editBudgets = true;

  public PowbFinancialPlannedBudget() {
  }

  public Date getActiveSince() {
    return activeSince;
  }

  public Double getCarry() {
    return carry;
  }

  public Double getCenterFunds() {
    return centerFunds;
  }


  public String getComments() {
    return comments;
  }


  public User getCreatedBy() {
    return createdBy;
  }


  @Override
  public Long getId() {
    return id;
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


  @Override
  public String getModificationJustification() {
    return modificationJustification;
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  public PowbExpenditureAreas getPowbExpenditureArea() {
    return powbExpenditureArea;
  }

  public PowbSynthesis getPowbSynthesis() {
    return powbSynthesis;
  }


  public Double getTotalPlannedBudget() {
    Double w1w2T;
    Double w3BilateralT;
    w1w2T = w1w2 == null ? 0.0 : w1w2;
    w3BilateralT = w3Bilateral == null ? 0.0 : w3Bilateral;
    if (w1w2T + w3BilateralT != 0) {
      return Math.round(((w1w2T + w3BilateralT)) * 10.0) / 10.0;
    } else {
      return 0.0;
    }
  }


  public Double getW1w2() {
    return w1w2;
  }

  public Double getW3Bilateral() {
    return w3Bilateral;
  }

  @Override
  public boolean isActive() {
    return active;
  }


  public boolean isEditBudgets() {
    return editBudgets;
  }

  public void setActive(boolean active) {
    this.active = active;
  }


  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }


  public void setCarry(Double carry) {
    this.carry = carry;
  }

  public void setCenterFunds(Double centerFunds) {
    this.centerFunds = centerFunds;
  }


  public void setComments(String comments) {
    this.comments = comments;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setEditBudgets(boolean editBudgets) {
    this.editBudgets = editBudgets;
  }

  public void setId(Long id) {
    this.id = id;
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


  public void setPowbExpenditureArea(PowbExpenditureAreas powbExpenditureArea) {
    this.powbExpenditureArea = powbExpenditureArea;
  }


  public void setPowbSynthesis(PowbSynthesis powbSynthesis) {
    this.powbSynthesis = powbSynthesis;
  }


  public void setW1w2(Double w1w2) {
    this.w1w2 = w1w2;
  }


  public void setW3Bilateral(Double w3Bilateral) {
    this.w3Bilateral = w3Bilateral;
  }


  @Override
  public String toString() {
    return "PowbFinancialPlannedBudget [id=" + id + ", powbSynthesis=" + powbSynthesis + ", powbExpenditureArea="
      + powbExpenditureArea + ", liaisonInstitution=" + liaisonInstitution + ", w1w2=" + w1w2 + ", w3Bilateral="
      + w3Bilateral + ", comments=" + comments + "]";
  }

}

