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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;


public class ProjectLp6Contribution extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 1054158126894011294L;

  @Expose
  private Project project;
  @Expose
  private Phase phase;
  @Expose
  private Boolean contribution;
  @Expose
  private String narrative;
  @Expose
  private String workingAcrossFlagshipsNarrative;
  @Expose
  private Boolean workingAcrossFlagships;
  @Expose
  private String undertakingEffortsLeadingNarrative;
  @Expose
  private Boolean undertakingEffortsLeading;
  @Expose
  private String topThreePartnershipsNarrative;
  @Expose
  private Boolean providingPathways;
  @Expose
  private String providingPathwaysNarrative;
  @Expose
  private String undertakingEffortsCsaNarrative;
  @Expose
  private Boolean undertakingEffortsCsa;
  @Expose
  private String initiativeRelatedNarrative;
  @Expose
  private Boolean initiativeRelated;
  @Expose
  private String geographicScopeNarrative;


  private Set<ProjectLp6ContributionDeliverable> projectLp6ContributionDeliverable =
    new HashSet<ProjectLp6ContributionDeliverable>(0);


  private List<ProjectLp6ContributionDeliverable> deliverables;

  private Set<SectionStatus> sectionStatuses = new HashSet<SectionStatus>(0);


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

  public List<ProjectLp6ContributionDeliverable> getDeliverables() {
    return deliverables;
  }


  public String getGeographicScopeNarrative() {
    return geographicScopeNarrative;
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


  public Set<SectionStatus> getSectionStatuses() {
    return sectionStatuses;
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


  public void setDeliverables(List<ProjectLp6ContributionDeliverable> deliverables) {
    this.deliverables = deliverables;
  }


  public void setGeographicScopeNarrative(String geographicScopeNarrative) {
    this.geographicScopeNarrative = geographicScopeNarrative;
  }


  public void setInitiativeRelated(Boolean initiativeRelated) {
    this.initiativeRelated = initiativeRelated;
  }


  public void setInitiativeRelatedNarrative(String initiativeRelatedNarrative) {
    this.initiativeRelatedNarrative = initiativeRelatedNarrative;
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
    setProjectLp6ContributionDeliverable(Set<ProjectLp6ContributionDeliverable> projectLp6ContributionDeliverables) {
    this.projectLp6ContributionDeliverable = projectLp6ContributionDeliverables;
  }


  public void setProvidingPathways(Boolean providingPathways) {
    this.providingPathways = providingPathways;
  }


  public void setProvidingPathwaysNarrative(String providingPathwaysNarrative) {
    this.providingPathwaysNarrative = providingPathwaysNarrative;
  }


  public void setSectionStatuses(Set<SectionStatus> sectionStatuses) {
    this.sectionStatuses = sectionStatuses;
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

  /**
   * Add the save information to reply the next Phase
   * 
   * @param projectInnovationInfoUpdate - a ProjectInnovationInfo object.
   * @param phase - The next Phase
   */
  public void updateProjectLp6Contribution(ProjectLp6Contribution projectLp6ContributionUpdate, Phase phase) {
    this.setPhase(phase);
    this.setContribution(projectLp6ContributionUpdate.getContribution());
    this.setInitiativeRelated(projectLp6ContributionUpdate.getInitiativeRelated());
    this.setInitiativeRelatedNarrative(projectLp6ContributionUpdate.getInitiativeRelatedNarrative());
    this.setNarrative(projectLp6ContributionUpdate.getNarrative());
    this.setProject(projectLp6ContributionUpdate.getProject());
    this.setProvidingPathways(projectLp6ContributionUpdate.getProvidingPathways());
    this.setProvidingPathwaysNarrative(projectLp6ContributionUpdate.getProvidingPathwaysNarrative());
    this.setTopThreePartnershipsNarrative(projectLp6ContributionUpdate.getTopThreePartnershipsNarrative());
    this.setUndertakingEffortsCsa(projectLp6ContributionUpdate.getUndertakingEffortsCsa());
    this.setUndertakingEffortsCsaNarrative(projectLp6ContributionUpdate.getUndertakingEffortsCsaNarrative());
    this.setUndertakingEffortsLeading(projectLp6ContributionUpdate.getUndertakingEffortsLeading());
    this.setUndertakingEffortsLeadingNarrative(projectLp6ContributionUpdate.getUndertakingEffortsLeadingNarrative());
    this.setWorkingAcrossFlagships(projectLp6ContributionUpdate.getWorkingAcrossFlagships());
    this.setWorkingAcrossFlagshipsNarrative(projectLp6ContributionUpdate.getWorkingAcrossFlagshipsNarrative());
    this.setGeographicScopeNarrative(projectLp6ContributionUpdate.getGeographicScopeNarrative());
  }

}
