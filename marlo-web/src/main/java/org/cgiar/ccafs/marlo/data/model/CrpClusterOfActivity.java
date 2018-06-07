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
package org.cgiar.ccafs.marlo.data.model;
// Generated May 26, 2016 9:42:28 AM by Hibernate Tools 4.3.1.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * CrpClusterOfActivity generated by hbm2java
 */
public class CrpClusterOfActivity extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -6392709700346014366L;

  private List<ProjectClusterActivity> clusterActivities;

  private Set<CrpClusterActivityLeader> crpClusterActivityLeaders = new HashSet<CrpClusterActivityLeader>(0);

  private Set<CrpClusterKeyOutput> crpClusterKeyOutputs = new HashSet<CrpClusterKeyOutput>(0);

  private Set<ProjectBudgetsCluserActvity> projectBudgetsCluserActvities = new HashSet<ProjectBudgetsCluserActvity>(0);


  @Expose
  private CrpProgram crpProgram;


  @Expose
  private String description;

  @Expose
  private Phase phase;
  @Expose
  private String identifier;


  private List<CrpClusterKeyOutput> keyOutputs;


  private List<CrpClusterActivityLeader> leaders;

  private Set<ProjectClusterActivity> projectClusterActivities = new HashSet<ProjectClusterActivity>(0);

  public CrpClusterOfActivity() {
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
    CrpClusterOfActivity other = (CrpClusterOfActivity) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public List<ProjectClusterActivity> getClusterActivities() {
    return clusterActivities;
  }

  public String getComposedName() {
    if (this.getIdentifier() != null && !this.getIdentifier().isEmpty()) {
      return this.getIdentifier() + " : " + this.description;
    } else {
      return description;
    }
  }


  public Set<CrpClusterActivityLeader> getCrpClusterActivityLeaders() {
    return crpClusterActivityLeaders;
  }


  public Set<CrpClusterKeyOutput> getCrpClusterKeyOutputs() {
    return crpClusterKeyOutputs;
  }


  public CrpProgram getCrpProgram() {
    return this.crpProgram;
  }


  public String getDescription() {
    return description;
  }


  public String getIdentifier() {
    return identifier;
  }

  public List<CrpClusterKeyOutput> getKeyOutputs() {
    return keyOutputs;
  }

  public List<CrpClusterActivityLeader> getLeaders() {
    return leaders;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public Phase getPhase() {
    return phase;
  }

  public Set<ProjectBudgetsCluserActvity> getProjectBudgetsCluserActvities() {
    return projectBudgetsCluserActvities;
  }


  public Set<ProjectClusterActivity> getProjectClusterActivities() {
    return projectClusterActivities;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setClusterActivities(List<ProjectClusterActivity> clusterActivities) {
    this.clusterActivities = clusterActivities;
  }

  public void setCrpClusterActivityLeaders(Set<CrpClusterActivityLeader> crpClusterActivityLeaders) {
    this.crpClusterActivityLeaders = crpClusterActivityLeaders;
  }

  public void setCrpClusterKeyOutputs(Set<CrpClusterKeyOutput> crpClusterKeyOutputs) {
    this.crpClusterKeyOutputs = crpClusterKeyOutputs;
  }

  public void setCrpProgram(CrpProgram crpProgram) {
    this.crpProgram = crpProgram;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public void setKeyOutputs(List<CrpClusterKeyOutput> keyOutputs) {
    this.keyOutputs = keyOutputs;
  }

  public void setLeaders(List<CrpClusterActivityLeader> leaders) {
    this.leaders = leaders;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProjectBudgetsCluserActvities(Set<ProjectBudgetsCluserActvity> projectBudgetsCluserActvities) {
    this.projectBudgetsCluserActvities = projectBudgetsCluserActvities;
  }

  public void setProjectClusterActivities(Set<ProjectClusterActivity> projectClusterActivities) {
    this.projectClusterActivities = projectClusterActivities;
  }

  @Override
  public String toString() {
    return "CrpClusterOfActivity [crpProgram=" + crpProgram + ", description=" + description + ", id=" + this.getId()
      + ", identifier=" + identifier + "]";
  }


}

