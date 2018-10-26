package org.cgiar.ccafs.marlo.data.model;


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class ProjectPartnerPartnership extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 703420896373995889L;
  @Expose
  private ProjectPartner projectPartner;
  @Expose
  private String mainArea;
  @Expose
  private RepIndGeographicScope geographicScope;
  @Expose
  private RepIndRegion region;

  private Set<ProjectPartnerPartnershipLocation> projectPartnerPartnershipLocations =
    new HashSet<ProjectPartnerPartnershipLocation>(0);
  private List<ProjectPartnerPartnershipLocation> partnershipLocations = new ArrayList<>();
  private List<String> partnershipLocationsIsos = new ArrayList<>();
  private String partnershipLocationsIsosText;

  private Set<ProjectPartnerPartnershipResearchPhase> projectPartnerPartnershipResearchPhases =
    new HashSet<ProjectPartnerPartnershipResearchPhase>(0);
  private List<ProjectPartnerPartnershipResearchPhase> partnershipResearchPhases = new ArrayList<>();
  private List<Long> researchPhasesIds = new ArrayList<>();
  private String researchPhasesIdsText;


  public ProjectPartnerPartnership() {
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    ProjectPartnerPartnership other = (ProjectPartnerPartnership) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public RepIndGeographicScope getGeographicScope() {
    return geographicScope;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public String getMainArea() {
    return mainArea;
  }


  public List<ProjectPartnerPartnershipLocation> getPartnershipLocations() {
    return partnershipLocations;
  }


  public List<String> getPartnershipLocationsIsos() {
    return partnershipLocationsIsos;
  }

  public String getPartnershipLocationsIsosText() {
    return partnershipLocationsIsosText;
  }

  public List<ProjectPartnerPartnershipResearchPhase> getPartnershipResearchPhases() {
    return partnershipResearchPhases;
  }


  public ProjectPartner getProjectPartner() {
    return projectPartner;
  }


  public Set<ProjectPartnerPartnershipLocation> getProjectPartnerPartnershipLocations() {
    return projectPartnerPartnershipLocations;
  }


  public Set<ProjectPartnerPartnershipResearchPhase> getProjectPartnerPartnershipResearchPhases() {
    return projectPartnerPartnershipResearchPhases;
  }


  public RepIndRegion getRegion() {
    return region;
  }


  public List<Long> getResearchPhasesIds() {
    return researchPhasesIds;
  }

  public String getResearchPhasesIdsText() {
    return researchPhasesIdsText;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setGeographicScope(RepIndGeographicScope geographicScope) {
    this.geographicScope = geographicScope;
  }

  public void setMainArea(String mainArea) {
    this.mainArea = mainArea;
  }

  public void setPartnershipLocations(List<ProjectPartnerPartnershipLocation> partnershipLocations) {
    this.partnershipLocations = partnershipLocations;
  }


  public void setPartnershipLocationsIsos(List<String> partnershipLocationsIsos) {
    this.partnershipLocationsIsos = partnershipLocationsIsos;
  }


  public void setPartnershipLocationsIsosText(String partnershipLocationsIsosText) {
    this.partnershipLocationsIsosText = partnershipLocationsIsosText;
  }


  public void setPartnershipResearchPhases(List<ProjectPartnerPartnershipResearchPhase> partnershipResearchPhases) {
    this.partnershipResearchPhases = partnershipResearchPhases;
  }


  public void setProjectPartner(ProjectPartner projectPartner) {
    this.projectPartner = projectPartner;
  }


  public void
    setProjectPartnerPartnershipLocations(Set<ProjectPartnerPartnershipLocation> projectPartnerPartnershipLocations) {
    this.projectPartnerPartnershipLocations = projectPartnerPartnershipLocations;
  }


  public void setProjectPartnerPartnershipResearchPhases(
    Set<ProjectPartnerPartnershipResearchPhase> projectPartnerPartnershipResearchPhases) {
    this.projectPartnerPartnershipResearchPhases = projectPartnerPartnershipResearchPhases;
  }


  public void setRegion(RepIndRegion region) {
    this.region = region;
  }


  public void setResearchPhasesIds(List<Long> researchPhasesIds) {
    this.researchPhasesIds = researchPhasesIds;
  }


  public void setResearchPhasesIdsText(String researchPhasesIdsText) {
    this.researchPhasesIdsText = researchPhasesIdsText;
  }


  @Override
  public String toString() {
    return "ProjectPartnerPartnership [id=" + this.getId() + ", projectPartner=" + projectPartner + ", mainArea="
      + mainArea + ", geographicScope=" + geographicScope + ", region=" + region + ", active=" + this.isActive() + "]";
  }


}

