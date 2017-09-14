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

/**
 * 
 */
package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;


/**
 * Modified by @author nmatovu last on Oct 9, 2016
 */
public class CenterOutcome implements Serializable, IAuditLog {


  private static final long serialVersionUID = -5206789836821862166L;


  @Expose
  private Long id;

  @Expose
  private String description;

  @Expose
  private Integer targetYear;

  @Expose
  private BigDecimal value;

  @Expose
  private CenterImpact researchImpact;


  @Expose
  private boolean active;

  @Expose
  private Date activeSince;


  @Expose
  private CenterTopic researchTopic;


  @Expose
  private User createdBy;


  @Expose
  private User modifiedBy;

  @Expose
  private CenterTargetUnit targetUnit;


  @Expose
  private String modificationJustification;

  @Expose
  private boolean impactPathway;


  @Expose
  private BigDecimal baseline;

  @Expose
  private String shortName;


  private Set<CenterMilestone> researchMilestones = new HashSet<CenterMilestone>(0);

  private Set<CenterOutput> researchOutputs = new HashSet<CenterOutput>(0);


  private Set<CenterSectionStatus> sectionStatuses = new HashSet<CenterSectionStatus>(0);

  private Set<CenterMonitoringOutcome> monitoringOutcomes = new HashSet<CenterMonitoringOutcome>(0);

  private List<CenterMilestone> milestones;


  private List<CenterMonitoringOutcome> monitorings;

  public CenterOutcome() {
    super();
    // TODO Auto-generated constructor stub
  }

  /**
   * @param description
   * @param targetYear
   * @param outcome
   * @param researchImpact
   * @param active
   * @param activeSince
   * @param researchTopic
   */
  public CenterOutcome(String description, Integer targetYear, BigDecimal value, CenterImpact researchImpact,
    boolean active, Date activeSince, CenterTopic researchTopic) {
    super();
    this.description = description;
    this.targetYear = targetYear;
    this.researchImpact = researchImpact;
    this.active = active;
    this.activeSince = activeSince;
    this.researchTopic = researchTopic;
    this.value = value;
  }

  public Date getActiveSince() {
    return activeSince;
  }

  public BigDecimal getBaseline() {
    return baseline;
  }

  public String getComposedName() {
    return "OC" + this.id + "- " + (this.description != null ? this.description : "title not defined");
  }

  public User getCreatedBy() {
    return createdBy;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return the id
   */
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

  public List<CenterMilestone> getMilestones() {
    return milestones;
  }


  @Override
  public String getModificationJustification() {
    return modificationJustification;
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  public Set<CenterMonitoringOutcome> getMonitoringOutcomes() {
    return monitoringOutcomes;
  }

  public List<CenterMonitoringOutcome> getMonitorings() {
    return monitorings;
  }

  /**
   * @return the researchImpact
   */
  public CenterImpact getResearchImpact() {
    return researchImpact;
  }

  public Set<CenterMilestone> getResearchMilestones() {
    return researchMilestones;
  }

  public Set<CenterOutput> getResearchOutputs() {
    return researchOutputs;
  }

  /**
   * @return the researchTopic
   */
  public CenterTopic getResearchTopic() {
    return researchTopic;
  }

  public Set<CenterSectionStatus> getSectionStatuses() {
    return sectionStatuses;
  }

  public String getShortName() {
    return shortName;
  }

  public CenterTargetUnit getTargetUnit() {
    return targetUnit;
  }

  /**
   * @return the targetYear
   */
  public Integer getTargetYear() {
    return targetYear;
  }

  public BigDecimal getValue() {
    return value;
  }

  /**
   * @return the active
   */
  @Override
  public boolean isActive() {
    return active;
  }

  public boolean isImpactPathway() {
    return impactPathway;
  }

  /**
   * @param active the active to set
   */
  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  public void setBaseline(BigDecimal baseline) {
    this.baseline = baseline;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }


  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  public void setImpactPathway(boolean impactPathway) {
    this.impactPathway = impactPathway;
  }

  public void setMilestones(List<CenterMilestone> milestones) {
    this.milestones = milestones;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }


  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }


  public void setMonitoringOutcomes(Set<CenterMonitoringOutcome> monitoringOutcomes) {
    this.monitoringOutcomes = monitoringOutcomes;
  }


  public void setMonitorings(List<CenterMonitoringOutcome> monitorings) {
    this.monitorings = monitorings;
  }


  /**
   * @param researchImpact the researchImpact to set
   */
  public void setResearchImpact(CenterImpact researchImpact) {
    this.researchImpact = researchImpact;
  }


  public void setResearchMilestones(Set<CenterMilestone> researchMilestones) {
    this.researchMilestones = researchMilestones;
  }


  public void setResearchOutputs(Set<CenterOutput> researchOutputs) {
    this.researchOutputs = researchOutputs;
  }


  /**
   * @param researchTopic the researchTopic to set
   */
  public void setResearchTopic(CenterTopic researchTopic) {
    this.researchTopic = researchTopic;
  }

  public void setSectionStatuses(Set<CenterSectionStatus> sectionStatuses) {
    this.sectionStatuses = sectionStatuses;
  }


  public void setShortName(String shortName) {
    this.shortName = shortName;
  }


  public void setTargetUnit(CenterTargetUnit targetUnit) {
    this.targetUnit = targetUnit;
  }

  /**
   * @param targetYear the targetYear to set
   */
  public void setTargetYear(Integer targetYear) {
    this.targetYear = targetYear;
  }


  public void setValue(BigDecimal value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "CenterOutcome [id=" + id + ", description=" + description + ", shortName=" + shortName + "]";
  }

}
