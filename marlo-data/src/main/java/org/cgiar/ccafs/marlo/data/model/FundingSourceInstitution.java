package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

// Generated Jan 6, 2017 8:46:55 AM by Hibernate Tools 4.3.1.Final


/**
 * FundingSourceInstitutions generated by hbm2java
 */
public class FundingSourceInstitution extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  /**
   * 
   */
  private static final long serialVersionUID = -3682242088869157772L;


  private FundingSource fundingSource;

  @Expose
  private Institution institution;

  @Expose
  private Phase phase;

  private Boolean isChecked;


  public FundingSourceInstitution() {
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
    FundingSourceInstitution other = (FundingSourceInstitution) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public FundingSource getFundingSource() {
    return this.fundingSource;
  }

  public Institution getInstitution() {
    return this.institution;
  }


  public Boolean getIsChecked() {
    return isChecked;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  @Override
  public String getModificationJustification() {
    // TODO Auto-generated method stub
    return "";
  }

  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return u;
  }

  public Phase getPhase() {
    return phase;
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

  public void setFundingSource(FundingSource fundingSource) {
    this.fundingSource = fundingSource;
  }

  public void setInstitution(Institution institution) {
    this.institution = institution;
  }

  public void setIsChecked(Boolean isChecked) {
    this.isChecked = isChecked;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {

  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  @Override
  public String toString() {
    return "FundingSourceInstitution [id=" + this.getId() + ", fundingSource=" + fundingSource + ", institution="
      + institution + "]";
  }


}

