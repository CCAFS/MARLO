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
import java.util.Set;

import com.google.gson.annotations.Expose;


/**
 * Modified by @author nmatovu last on Oct 9, 2016
 */
public class CenterMilestone extends MarloAuditableEntity implements Serializable, IAuditLog {


  private static final long serialVersionUID = -338676464720877306L;

  @Expose
  private String title;


  @Expose
  private Integer targetYear;

  @Expose
  private BigDecimal value;

  @Expose
  private CenterOutcome researchOutcome;
  @Expose
  private CenterTargetUnit targetUnit;
  @Expose
  private boolean impactPathway;
  @Expose
  private SrfTargetUnit srfTargetUnit;
  private Set<CenterMonitoringMilestone> monitoringMilestones = new HashSet<CenterMonitoringMilestone>(0);

  public CenterMilestone() {
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
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
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
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public boolean isImpactPathway() {
    return impactPathway;
  }


  public void setImpactPathway(boolean impactPathway) {
    this.impactPathway = impactPathway;
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


  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }


  public void setValue(BigDecimal value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "CenterMilestone [id=" + this.getId() + ", title=" + title + ", targetYear=" + targetYear + ", value="
      + value + "]";
  }


}
