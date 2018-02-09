package org.cgiar.ccafs.marlo.data.model;
// Generated Sep 18, 2017 3:03:15 PM by Hibernate Tools 5.2.3.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;


/**
 * FundingSourceInfo generated by hbm2java
 */
public class PowbSynthesis implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 3684598637198995229L;


  @Expose
  private Long id;


  @Expose
  private Phase phase;


  @Expose
  private boolean active;


  @Expose
  private User createdBy;


  @Expose
  private Date activeSince;


  @Expose
  private User modifiedBy;


  @Expose
  private LiaisonInstitution liaisonInstitution;


  @Expose
  private PowbToc powbToc;


  @Expose
  private PowbFlagshipPlans powbFlagshipPlans;


  @Expose
  private PowbEvidence powbEvidence;


  @Expose
  private CrossCuttingDimensions crossCutting;

  private Set<SectionStatus> sectionStatuses = new HashSet<SectionStatus>(0);

  private Set<PowbExpectedCrpProgress> powbExpectedCrpProgresses = new HashSet<PowbExpectedCrpProgress>(0);


  private List<PowbExpectedCrpProgress> expectedCrpProgresses;

  @Expose
  private PowbMonitoringEvaluationLearning powbMonitoringEvaluationLearning;

  @Expose
  private PowbCrpStaffing crpStaffing;

  private Set<PowbSynthesisCrpStaffingCategory> powbSynthesisCrpStaffingCategory =
    new HashSet<PowbSynthesisCrpStaffingCategory>(0);


  public PowbSynthesis() {
  }

  public PowbSynthesis(Long id, Phase phase, boolean active, Date activeSince, User modifiedBy,
    LiaisonInstitution liaisonInstitution) {
    this.id = id;
    this.phase = phase;
    this.active = active;
    this.activeSince = activeSince;
    this.modifiedBy = modifiedBy;
    this.liaisonInstitution = liaisonInstitution;
  }

  public PowbSynthesis(Long id, Phase phase, boolean active, User createdBy, Date activeSince, User modifiedBy,
    LiaisonInstitution liaisonInstitution) {
    this.id = id;
    this.phase = phase;
    this.active = active;
    this.createdBy = createdBy;
    this.activeSince = activeSince;
    this.modifiedBy = modifiedBy;
    this.liaisonInstitution = liaisonInstitution;
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
    PowbSynthesis other = (PowbSynthesis) obj;
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


  public User getCreatedBy() {
    return createdBy;
  }


  public CrossCuttingDimensions getCrossCutting() {
    return crossCutting;
  }

  public PowbCrpStaffing getCrpStaffing() {
    return crpStaffing;
  }

  public List<PowbExpectedCrpProgress> getExpectedCrpProgresses() {
    return expectedCrpProgresses;
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
    return "";
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }


  public Phase getPhase() {
    return phase;
  }

  public PowbEvidence getPowbEvidence() {
    return powbEvidence;
  }


  public Set<PowbExpectedCrpProgress> getPowbExpectedCrpProgresses() {
    return powbExpectedCrpProgresses;
  }

  public PowbFlagshipPlans getPowbFlagshipPlans() {
    return powbFlagshipPlans;
  }

  public PowbMonitoringEvaluationLearning getPowbMonitoringEvaluationLearning() {
    return powbMonitoringEvaluationLearning;
  }


  public Set<PowbSynthesisCrpStaffingCategory> getPowbSynthesisCrpStaffingCategory() {
    return powbSynthesisCrpStaffingCategory;
  }

  public PowbToc getPowbToc() {
    return powbToc;
  }


  public Set<SectionStatus> getSectionStatuses() {
    return sectionStatuses;
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
    return true;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }


  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }


  public void setCrossCutting(CrossCuttingDimensions crossCutting) {
    this.crossCutting = crossCutting;
  }

  public void setCrpStaffing(PowbCrpStaffing crpStaffing) {
    this.crpStaffing = crpStaffing;
  }

  public void setExpectedCrpProgresses(List<PowbExpectedCrpProgress> expectedCrpProgresses) {
    this.expectedCrpProgresses = expectedCrpProgresses;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setLiaisonInstitution(LiaisonInstitution liaisonInstitution) {
    this.liaisonInstitution = liaisonInstitution;
  }

  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setPowbEvidence(PowbEvidence powbEvidence) {
    this.powbEvidence = powbEvidence;
  }

  public void setPowbExpectedCrpProgresses(Set<PowbExpectedCrpProgress> powbExpectedCrpProgresses) {
    this.powbExpectedCrpProgresses = powbExpectedCrpProgresses;
  }


  public void setPowbFlagshipPlans(PowbFlagshipPlans powbFlagshipPlans) {
    this.powbFlagshipPlans = powbFlagshipPlans;
  }


  public void setPowbMonitoringEvaluationLearning(PowbMonitoringEvaluationLearning powbMonitoringEvaluationLearning) {
    this.powbMonitoringEvaluationLearning = powbMonitoringEvaluationLearning;
  }

  public void
    setPowbSynthesisCrpStaffingCategory(Set<PowbSynthesisCrpStaffingCategory> powbSynthesisCrpStaffingCategory) {
    this.powbSynthesisCrpStaffingCategory = powbSynthesisCrpStaffingCategory;
  }


  public void setPowbToc(PowbToc powbToc) {
    this.powbToc = powbToc;
  }


  public void setSectionStatuses(Set<SectionStatus> sectionStatuses) {
    this.sectionStatuses = sectionStatuses;
  }


  @Override
  public String toString() {
    return "PowbSynthesis [id=" + id + ", phase=" + phase + ", active=" + active + ", liaisonInstitution="
      + liaisonInstitution + "]";
  }


}