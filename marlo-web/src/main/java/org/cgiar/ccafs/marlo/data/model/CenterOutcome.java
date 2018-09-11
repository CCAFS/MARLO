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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;


/**
 * Modified by @author nmatovu last on Oct 9, 2016
 */
public class CenterOutcome extends MarloAuditableEntity implements Serializable, IAuditLog {


  private static final long serialVersionUID = -5206789836821862166L;

  @Expose
  private String description;


  @Expose
  private Integer targetYear;

  @Expose
  private BigDecimal value;


  @Expose
  private CenterImpact researchImpact;

  @Expose
  private CenterTopic researchTopic;

  @Expose
  private CenterTargetUnit targetUnit;

  @Expose
  private boolean impactPathway;


  @Expose
  private String shortName;

  @Expose
  private SrfTargetUnit srfTargetUnit;


  private Set<CenterMilestone> researchMilestones = new HashSet<CenterMilestone>(0);

  private Set<CenterSectionStatus> sectionStatuses = new HashSet<CenterSectionStatus>(0);

  private Set<CenterMonitoringOutcome> monitoringOutcomes = new HashSet<CenterMonitoringOutcome>(0);

  private List<CenterMilestone> milestones;

  private List<CenterMonitoringOutcome> monitorings;


  private Set<CenterOutputsOutcome> centerOutputsOutcomes = new HashSet<CenterOutputsOutcome>(0);

  public CenterOutcome() {
  }


  public Set<CenterOutputsOutcome> getCenterOutputsOutcomes() {
    return centerOutputsOutcomes;
  }

  public String getComposedName() {
    return "OC" + this.getId() + "- " + (this.description != null ? this.description : "title not defined");
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }


  public String getListComposedName() {

    String name = "";

    if (this.getResearchTopic() != null) {
      name = name + this.getResearchTopic().getResearchProgram().getName();

      if (this.getDescription() != null) {
        name = name + "- " + this.getDescription();
      }
    }

    return name;
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

  public SrfTargetUnit getSrfTargetUnit() {
    return srfTargetUnit;
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


  public boolean isImpactPathway() {
    return impactPathway;
  }

  public void setCenterOutputsOutcomes(Set<CenterOutputsOutcome> centerOutputsOutcomes) {
    this.centerOutputsOutcomes = centerOutputsOutcomes;
  }


  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  public void setImpactPathway(boolean impactPathway) {
    this.impactPathway = impactPathway;
  }

  public void setMilestones(List<CenterMilestone> milestones) {
    this.milestones = milestones;
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


  public void setSrfTargetUnit(SrfTargetUnit srfTargetUnit) {
    this.srfTargetUnit = srfTargetUnit;
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
    return "CenterOutcome [id=" + this.getId() + ", description=" + description + ", shortName=" + shortName + "]";
  }

}
