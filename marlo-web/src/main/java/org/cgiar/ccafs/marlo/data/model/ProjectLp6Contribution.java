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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;


public class ProjectLp6Contribution extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 1054158126894011294L;

  @Expose
  private Project project;

  private Phase phase;

  private Boolean contribution;

  private String narrative;

  private String workingAcrossFlagshipsNarrative;

  private Boolean workingAcrossFlagships;

  private String undertakingEffortsLeadingNarrative;

  private Boolean undertakingEffortsLeading;

  private String topThreePartnershipsNarrative;

  private Boolean providingPathways;

  private String providingPathwaysNarrative;

  private String undertakingEffortsCsaNarrative;

  private Boolean undertakingEffortsCsa;

  private String initiativeRelatedNarrative;

  private Boolean initiativeRelated;

  private Set<ProjectLp6ContributionDeliverable> projectLp6ContributionDeliverable =
    new HashSet<ProjectLp6ContributionDeliverable>(0);

  private List<ProjectLp6ContributionDeliverable> deliverables;

  private Set<Lp6ContributionGeographicScope> lp6ContributionGeographicScopes =
    new HashSet<Lp6ContributionGeographicScope>(0);

  private List<String> countriesIds = new ArrayList<>();

  private List<Lp6ContributionGeographicScope> countries;


  private String countriesIdsText;


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


  public Boolean getContribution() {
    return contribution;
  }


  public List<Lp6ContributionGeographicScope> getCountries() {
    return countries;
  }


  public List<String> getCountriesIds() {
    return countriesIds;
  }

  public String getCountriesIdsText() {
    return countriesIdsText;
  }

  public List<ProjectLp6ContributionDeliverable> getDeliverables() {
    return deliverables;
  }


  public Boolean getInitiativeRelated() {
    return initiativeRelated;
  }


  public String getInitiativeRelatedNarrative() {
    return initiativeRelatedNarrative;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();

    sb.append("Id : ").append(this.getId());


    return sb.toString();
  }


  public Set<Lp6ContributionGeographicScope> getLp6ContributionGeographicScopes() {
    return lp6ContributionGeographicScopes;
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


  public Set<ProjectLp6ContributionDeliverable> getProjectLp6ContributionDeliverable() {
    return projectLp6ContributionDeliverable;
  }


  public Boolean getProvidingPathways() {
    return providingPathways;
  }

  public String getProvidingPathwaysNarrative() {
    return providingPathwaysNarrative;
  }


  public String getTopThreePartnershipsNarrative() {
    return topThreePartnershipsNarrative;
  }

  public Boolean getUndertakingEffortsCsa() {
    return undertakingEffortsCsa;
  }


  public String getUndertakingEffortsCsaNarrative() {
    return undertakingEffortsCsaNarrative;
  }

  public Boolean getUndertakingEffortsLeading() {
    return undertakingEffortsLeading;
  }


  public String getUndertakingEffortsLeadingNarrative() {
    return undertakingEffortsLeadingNarrative;
  }


  public Boolean getWorkingAcrossFlagships() {
    return workingAcrossFlagships;
  }

  public String getWorkingAcrossFlagshipsNarrative() {
    return workingAcrossFlagshipsNarrative;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public Boolean isContribution() {
    return contribution;
  }


  public Boolean isInitiativeRelated() {
    return initiativeRelated;
  }


  public Boolean isProvidingPathways() {
    return providingPathways;
  }


  public Boolean isUndertakingEffortsCsa() {
    return undertakingEffortsCsa;
  }

  public Boolean isUndertakingEffortsLeading() {
    return undertakingEffortsLeading;
  }


  public Boolean isWorkingAcrossFlagships() {
    return workingAcrossFlagships;
  }


  public void setContribution(Boolean contribution) {
    this.contribution = contribution;
  }


  public void setCountries(List<Lp6ContributionGeographicScope> countries) {
    this.countries = countries;
  }


  public void setCountriesIds(List<String> countriesIds) {
    this.countriesIds = countriesIds;
  }


  public void setCountriesIdsText(String countriesIdsText) {
    this.countriesIdsText = countriesIdsText;
  }


  public void setDeliverables(List<ProjectLp6ContributionDeliverable> deliverables) {
    this.deliverables = deliverables;
  }


  public void setInitiativeRelated(Boolean initiativeRelated) {
    this.initiativeRelated = initiativeRelated;
  }


  public void setInitiativeRelatedNarrative(String initiativeRelatedNarrative) {
    this.initiativeRelatedNarrative = initiativeRelatedNarrative;
  }


  public void setLp6ContributionGeographicScopes(Set<Lp6ContributionGeographicScope> lp6ContributionGeographicScopes) {
    this.lp6ContributionGeographicScopes = lp6ContributionGeographicScopes;
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


  public void
    setProjectLp6ContributionDeliverable(Set<ProjectLp6ContributionDeliverable> projectLp6ContributionDeliverable) {
    this.projectLp6ContributionDeliverable = projectLp6ContributionDeliverable;
  }

  public void setProvidingPathways(Boolean providingPathways) {
    this.providingPathways = providingPathways;
  }

  public void setProvidingPathwaysNarrative(String providingPathwaysNarrative) {
    this.providingPathwaysNarrative = providingPathwaysNarrative;
  }


  public void setTopThreePartnershipsNarrative(String topThreePartnershipsNarrative) {
    this.topThreePartnershipsNarrative = topThreePartnershipsNarrative;
  }


  public void setUndertakingEffortsCsa(Boolean undertakingEffortsCsa) {
    this.undertakingEffortsCsa = undertakingEffortsCsa;
  }


  public void setUndertakingEffortsCsaNarrative(String undertakingEffortsCsaNarrative) {
    this.undertakingEffortsCsaNarrative = undertakingEffortsCsaNarrative;
  }


  public void setUndertakingEffortsLeading(Boolean undertakingEffortsLeading) {
    this.undertakingEffortsLeading = undertakingEffortsLeading;
  }


  public void setUndertakingEffortsLeadingNarrative(String undertakingEffortsLeadingNarrative) {
    this.undertakingEffortsLeadingNarrative = undertakingEffortsLeadingNarrative;
  }


  public void setWorkingAcrossFlagships(Boolean workingAcrossFlagships) {
    this.workingAcrossFlagships = workingAcrossFlagships;
  }


  public void setWorkingAcrossFlagshipsNarrative(String workingAcrossFlagshipsNarrative) {
    this.workingAcrossFlagshipsNarrative = workingAcrossFlagshipsNarrative;
  }

  @Override
  public String toString() {
    return "ProjectLp6Contribution [id=" + this.getId() + ", project=" + project + ", phase=" + phase
      + ", contribution=" + contribution + "]";
  }

}
