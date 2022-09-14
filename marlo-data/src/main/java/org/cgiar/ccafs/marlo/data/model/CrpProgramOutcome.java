/*****************************************************************
 * i * This file is part of Managing Agricultural Research for Learning &
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
package org.cgiar.ccafs.marlo.data.model;
// Generated May 26, 2016 9:42:28 AM by Hibernate Tools 4.3.1.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * CrpProgramOutcome generated by hbm2java
 */
public class CrpProgramOutcome extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  /**
   * 
   */
  private static final long serialVersionUID = 3014520962149158601L;


  @Expose
  private CrpProgram crpProgram;


  @Expose
  private SrfTargetUnit srfTargetUnit;

  @Expose
  private String description;
  @Expose
  private String acronym;
  @Expose
  private String instructions;

  @Expose
  private String indicator;
  @Expose
  private Integer year;
  @Expose
  private FileDB file;


  @Expose
  private BigDecimal value;


  private Set<CrpOutcomeSubIdo> crpOutcomeSubIdos = new HashSet<CrpOutcomeSubIdo>(0);

  private Set<CrpMilestone> crpMilestones = new HashSet<CrpMilestone>(0);


  private Set<ProjectOutcome> projectOutcomes = new HashSet<ProjectOutcome>(0);

  private Set<Deliverable> deliverables = new HashSet<Deliverable>(0);

  private Set<CrpClusterKeyOutputOutcome> crpClusterKeyOutputOutcomes = new HashSet<CrpClusterKeyOutputOutcome>(0);
  private Set<ProjectFurtherContribution> projectFurtherContributions = new HashSet<ProjectFurtherContribution>(0);
  private Set<CrpProgramOutcomeIndicator> crpProgramOutcomeIndicators = new HashSet<CrpProgramOutcomeIndicator>(0);

  @Expose
  private Phase phase;


  @Expose
  private String composeID;

  private List<CrpMilestone> milestones;

  private List<CrpOutcomeSubIdo> subIdos;

  private List<CrpProgramOutcomeIndicator> indicators;

  public CrpProgramOutcome() {
  }

  public void copyFields(CrpProgramOutcome other) {
    this.setActive(other.isActive());
    this.setActiveSince(other.getActiveSince());
    this.setComposeID(other.getComposeID());
    this.setCreatedBy(other.getCreatedBy());
    this.setCrpProgram(other.getCrpProgram());
    this.setDescription(other.getDescription());
    this.setAcronym(other.getAcronym());
    this.setFile(other.getFile());
    this.setIndicator(other.getIndicator());
    this.setModificationJustification(other.getModificationJustification());
    this.setModifiedBy(other.getModifiedBy());
    this.setSrfTargetUnit(other.getSrfTargetUnit());
    this.setValue(other.getValue());
    this.setYear(other.getYear());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    CrpProgramOutcome other = (CrpProgramOutcome) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public String getAcronym() {
    return acronym;
  }


  public String getComposedName() {
    return this.getCrpProgram().getAcronym() + " Outcome: " + description;
  }


  public String getComposeID() {
    if (composeID != null) {
      return composeID;
    } else {
      if (this.getId() != null && this.getCrpProgram() != null && this.getCrpProgram().getId() != null) {
        return this.getId() + "-" + this.getCrpProgram().getId();
      }
    }
    return null;
  }

  public Set<CrpClusterKeyOutputOutcome> getCrpClusterKeyOutputOutcomes() {
    return crpClusterKeyOutputOutcomes;
  }

  public Set<CrpMilestone> getCrpMilestones() {
    return this.crpMilestones;
  }

  public Set<CrpOutcomeSubIdo> getCrpOutcomeSubIdos() {
    return this.crpOutcomeSubIdos;
  }


  public CrpProgram getCrpProgram() {
    return this.crpProgram;
  }

  public Set<CrpProgramOutcomeIndicator> getCrpProgramOutcomeIndicators() {
    return crpProgramOutcomeIndicators;
  }


  public Set<Deliverable> getDeliverables() {
    return deliverables;
  }

  public String getDescription() {
    return this.description;
  }

  public FileDB getFile() {
    return file;
  }

  public String getIndicator() {
    return indicator;
  }

  public List<CrpProgramOutcomeIndicator> getIndicators() {
    return indicators;
  }

  public String getInstructions() {
    return instructions;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public List<CrpMilestone> getMilestones() {
    return milestones;
  }


  public String getPAcronym() {
    return this.getCrpProgram().getAcronym();
  }

  public Phase getPhase() {
    return phase;
  }

  public Set<ProjectFurtherContribution> getProjectFurtherContributions() {
    return projectFurtherContributions;
  }

  public Set<ProjectOutcome> getProjectOutcomes() {
    return projectOutcomes;
  }

  public SrfTargetUnit getSrfTargetUnit() {
    return this.srfTargetUnit;
  }

  public List<CrpOutcomeSubIdo> getSubIdos() {
    return subIdos;
  }

  public BigDecimal getValue() {
    return this.value;
  }

  public Integer getYear() {
    return this.year;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setComposeID(String composeID) {
    this.composeID = composeID;
  }


  public void setCrpClusterKeyOutputOutcomes(Set<CrpClusterKeyOutputOutcome> crpClusterKeyOutputOutcomes) {
    this.crpClusterKeyOutputOutcomes = crpClusterKeyOutputOutcomes;
  }


  public void setCrpMilestones(Set<CrpMilestone> crpMilestones) {
    this.crpMilestones = crpMilestones;
  }

  public void setCrpOutcomeSubIdos(Set<CrpOutcomeSubIdo> crpOutcomeSubIdos) {
    this.crpOutcomeSubIdos = crpOutcomeSubIdos;
  }

  public void setCrpProgram(CrpProgram crpProgram) {
    this.crpProgram = crpProgram;
  }

  public void setCrpProgramOutcomeIndicators(Set<CrpProgramOutcomeIndicator> crpProgramOutcomeIndicators) {
    this.crpProgramOutcomeIndicators = crpProgramOutcomeIndicators;
  }

  public void setDeliverables(Set<Deliverable> deliverables) {
    this.deliverables = deliverables;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setFile(FileDB file) {
    this.file = file;
  }

  public void setIndicator(String indicator) {
    this.indicator = indicator;
  }

  public void setIndicators(List<CrpProgramOutcomeIndicator> indicators) {
    this.indicators = indicators;
  }

  public void setInstructions(String instructions) {
    this.instructions = instructions;
  }

  public void setMilestones(List<CrpMilestone> milestones) {
    this.milestones = milestones;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProjectFurtherContributions(Set<ProjectFurtherContribution> projectFurtherContributions) {
    this.projectFurtherContributions = projectFurtherContributions;
  }

  public void setProjectOutcomes(Set<ProjectOutcome> projectOutcomes) {
    this.projectOutcomes = projectOutcomes;
  }

  public void setSrfTargetUnit(SrfTargetUnit srfTargetUnit) {
    this.srfTargetUnit = srfTargetUnit;
  }

  public void setSubIdos(List<CrpOutcomeSubIdo> subIdos) {
    this.subIdos = subIdos;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  @Override
  public String toString() {
    return "CrpProgramOutcome [id=" + this.getId() + ", crpProgram=" + crpProgram + ", srfTargetUnit=" + srfTargetUnit
      + ", description=" + description + ", year=" + year + ", value=" + value + "]";
  }
}

