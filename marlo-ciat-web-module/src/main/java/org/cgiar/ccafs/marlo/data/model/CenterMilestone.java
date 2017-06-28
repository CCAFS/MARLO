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
import java.util.Set;

import com.google.gson.annotations.Expose;


/**
 * Modified by @author nmatovu last on Oct 9, 2016
 */
public class CenterMilestone implements Serializable, IAuditLog {


  private static final long serialVersionUID = -338676464720877306L;


  @Expose
  private Long id;

  @Expose
  private String title;


  @Expose
  private Integer targetYear;

  @Expose
  private BigDecimal value;


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
  @Expose
  private CenterOutcome researchOutcome;
  @Expose
  private CenterTargetUnit targetUnit;
  @Expose
  private boolean impactPathway;
  private Set<CenterMonitoringMilestone> monitoringMilestones = new HashSet<CenterMonitoringMilestone>(0);

  public CenterMilestone() {
    super();
  }

  /**
   * @param title
   * @param targetYear
   * @param milestone
   * @param active
   * @param activeSince
   * @param impactOutcome
   */
  public CenterMilestone(String title, Integer targetYear, BigDecimal value, boolean active, Date activeSince,
    CenterOutcome researchOutcome) {
    super();
    this.title = title;
    this.targetYear = targetYear;
    this.active = active;
    this.activeSince = activeSince;
    this.researchOutcome = researchOutcome;
    this.value = value;
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
    CenterMilestone other = (CenterMilestone) obj;
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

  public String getModificationJustification() {
    return modificationJustification;
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  public Set<CenterMonitoringMilestone> getMonitoringMilestones() {
    return monitoringMilestones;
  }

  /**
   * @return the impactOutcome
   */
  public CenterOutcome getResearchOutcome() {
    return researchOutcome;
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

  /**
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  public BigDecimal getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
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


  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
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


  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }


  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }


  public void setMonitoringMilestones(Set<CenterMonitoringMilestone> monitoringMilestones) {
    this.monitoringMilestones = monitoringMilestones;
  }


  /**
   * @param impactOutcome the impactOutcome to set
   */
  public void setResearchOutcome(CenterOutcome researchOutcome) {
    this.researchOutcome = researchOutcome;
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


  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }


}
