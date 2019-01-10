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

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;


public class ProjectLp6Contribution extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 1054158126894011294L;

  @Expose
  private Project project;

  private Phase phase;

  private boolean contribution;

  private String narrative;

  private String workingAcrossFlagships;

  private boolean isWorkingAcrossFlagships;

  private String undertakingEffortsLeading;

  private boolean isUndertakingEffortsLeading;

  private String topThreePartnerships;

  private boolean isProvidingPathways;

  private String undertakingEffortsCsa;

  private boolean isUndertakingEffortsCsa;

  private String initiative_related;

  private boolean isInitiativeRelated;


  public ProjectLp6Contribution() {
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
    ProjectLp6Contribution other = (ProjectLp6Contribution) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public String getInitiative_related() {
    return initiative_related;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();

    sb.append("Id : ").append(this.getId());


    return sb.toString();
  }

  public String getNarrative() {
    return narrative;
  }

  public Phase getPhase() {
    return phase;
  }

  public Project getProject() {
    return project;
  }


  public String getTopThreePartnerships() {
    return topThreePartnerships;
  }

  public String getUndertakingEffortsCsa() {
    return undertakingEffortsCsa;
  }

  public String getUndertakingEffortsLeading() {
    return undertakingEffortsLeading;
  }

  public String getWorkingAcrossFlagships() {
    return workingAcrossFlagships;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public boolean isContribution() {
    return contribution;
  }


  public boolean isInitiativeRelated() {
    return isInitiativeRelated;
  }


  public boolean isProvidingPathways() {
    return isProvidingPathways;
  }


  public boolean isUndertakingEffortsCsa() {
    return isUndertakingEffortsCsa;
  }


  public boolean isUndertakingEffortsLeading() {
    return isUndertakingEffortsLeading;
  }


  public boolean isWorkingAcrossFlagships() {
    return isWorkingAcrossFlagships;
  }


  public void setContribution(boolean contribution) {
    this.contribution = contribution;
  }


  public void setInitiative_related(String initiative_related) {
    this.initiative_related = initiative_related;
  }


  public void setInitiativeRelated(boolean isInitiativeRelated) {
    this.isInitiativeRelated = isInitiativeRelated;
  }


  public void setNarrative(String narrative) {
    this.narrative = narrative;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProvidingPathways(boolean isProvidingPathways) {
    this.isProvidingPathways = isProvidingPathways;
  }


  public void setTopThreePartnerships(String topThreePartnerships) {
    this.topThreePartnerships = topThreePartnerships;
  }


  public void setUndertakingEffortsCsa(boolean isUndertakingEffortsCsa) {
    this.isUndertakingEffortsCsa = isUndertakingEffortsCsa;
  }


  public void setUndertakingEffortsCsa(String undertakingEffortsCsa) {
    this.undertakingEffortsCsa = undertakingEffortsCsa;
  }


  public void setUndertakingEffortsLeading(boolean isUndertakingEffortsLeading) {
    this.isUndertakingEffortsLeading = isUndertakingEffortsLeading;
  }


  public void setUndertakingEffortsLeading(String undertakingEffortsLeading) {
    this.undertakingEffortsLeading = undertakingEffortsLeading;
  }


  public void setWorkingAcrossFlagships(boolean isWorkingAcrossFlagships) {
    this.isWorkingAcrossFlagships = isWorkingAcrossFlagships;
  }


  public void setWorkingAcrossFlagships(String workingAcrossFlagships) {
    this.workingAcrossFlagships = workingAcrossFlagships;
  }

  @Override
  public String toString() {
    return "ProjectLp6Contribution [id=" + this.getId() + ", project=" + project + ", phase=" + phase
      + ", contribution=" + contribution + "]";
  }

}
