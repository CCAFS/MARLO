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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;


/**
 * Modified by @author nmatovu last on Oct 9, 2016
 */
public class CenterImpact extends MarloAuditableEntity implements Serializable, IAuditLog {


  private static final long serialVersionUID = -5150082139088832748L;

  @Expose
  private String description;


  @Expose
  private Integer targetYear;


  @Expose
  private CrpProgram researchProgram;


  @Expose
  private String color;


  @Expose
  private String shortName;

  @Expose
  private CenterImpactStatement researchImpactStatement;


  @Expose
  private SrfSubIdo srfSubIdo;

  private Set<CenterOutcome> researchOutcomes = new HashSet<CenterOutcome>(0);


  private Set<CenterImpactObjective> researchImpactObjectives = new HashSet<>(0);

  private Set<CenterImpactBeneficiary> researchImpactBeneficiaries = new HashSet<CenterImpactBeneficiary>(0);

  private List<CenterObjective> objectives;


  private List<CenterImpactBeneficiary> beneficiaries;

  private String objectiveValue;


  private List<String> objectiveValueText;

  /**
   * 
   */
  public CenterImpact() {
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
    CenterImpact other = (CenterImpact) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public List<CenterImpactBeneficiary> getBeneficiaries() {
    return beneficiaries;
  }


  public String getColor() {
    return color;
  }


  public String getDescription() {
    return description;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public List<CenterObjective> getObjectives() {
    return objectives;
  }

  /**
   * @return an array of integers.
   */
  public long[] getObjectivesIds() {

    List<CenterObjective> researchObjectives = this.getObjectives();
    if (researchObjectives != null) {
      long[] ids = new long[researchObjectives.size()];
      for (int i = 0; i < ids.length; i++) {
        ids[i] = researchObjectives.get(i).getId();
      }
      return ids;
    }
    return null;
  }

  public String getObjectiveValue() {
    return objectiveValue;
  }

  public Set<CenterImpactBeneficiary> getResearchImpactBeneficiaries() {
    return researchImpactBeneficiaries;
  }

  public Set<CenterImpactObjective> getResearchImpactObjectives() {
    return researchImpactObjectives;
  }


  public CenterImpactStatement getResearchImpactStatement() {
    return researchImpactStatement;
  }

  /**
   * @return the researchOutcomes
   */
  public Set<CenterOutcome> getResearchOutcomes() {
    return researchOutcomes;
  }


  public CrpProgram getResearchProgram() {
    return researchProgram;
  }

  public String getShortName() {
    return shortName;
  }


  public SrfSubIdo getSrfSubIdo() {
    return srfSubIdo;
  }

  /**
   * @return the targetYear
   */
  public Integer getTargetYear() {
    return targetYear;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public void setBeneficiaries(List<CenterImpactBeneficiary> beneficiaries) {
    this.beneficiaries = beneficiaries;
  }

  public void setColor(String color) {
    this.color = color;
  }


  public void setDescription(String description) {
    this.description = description;
  }

  public void setObjectives(List<CenterObjective> objectives) {
    this.objectives = objectives;
  }

  public void setObjectiveValue(String objectiveValue) {
    this.objectiveValue = objectiveValue;
  }

  public void setResearchImpactBeneficiaries(Set<CenterImpactBeneficiary> researchImpactBeneficiaries) {
    this.researchImpactBeneficiaries = researchImpactBeneficiaries;
  }

  public void setResearchImpactObjectives(Set<CenterImpactObjective> researchImpactObjectives) {
    this.researchImpactObjectives = researchImpactObjectives;
  }

  public void setResearchImpactStatement(CenterImpactStatement researchImpactStatement) {
    this.researchImpactStatement = researchImpactStatement;
  }

  /**
   * @param researchOutcomes the researchOutcomes to set
   */
  public void setResearchOutcomes(Set<CenterOutcome> researchOutcomes) {
    this.researchOutcomes = researchOutcomes;
  }

  public void setResearchProgram(CrpProgram researchProgram) {
    this.researchProgram = researchProgram;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public void setSrfSubIdo(SrfSubIdo srfSubIdo) {
    this.srfSubIdo = srfSubIdo;
  }

  /**
   * @param targetYear the targetYear to set
   */
  public void setTargetYear(Integer targetYear) {
    this.targetYear = targetYear;
  }

}
