package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 8, 2019 3:50:56 PM by Hibernate Tools 5.3.6.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

public class ProjectPolicy extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -4753410776769726974L;

  @Expose
  private Project project;

  private ProjectPolicyInfo projectPolicyInfo;

  private Set<ProjectPolicyInfo> projectPolicyInfos = new HashSet<ProjectPolicyInfo>(0);

  private Set<ProjectPolicyCountry> ProjectPolicyCountries = new HashSet<ProjectPolicyCountry>(0);
  private Set<ProjectPolicyOwner> projectPolicyOwners = new HashSet<ProjectPolicyOwner>(0);
  private Set<ProjectPolicyCrp> projectPolicyCrps = new HashSet<ProjectPolicyCrp>(0);
  private Set<ProjectPolicySubIdo> projectPolicySubIdos = new HashSet<ProjectPolicySubIdo>(0);
  private Set<ProjectPolicyCrossCuttingMarker> projectPolicyCrossCuttingMarkers =
    new HashSet<ProjectPolicyCrossCuttingMarker>(0);

  private List<String> countriesIds = new ArrayList<>();
  private List<ProjectPolicyCountry> countries;
  private String countriesIdsText;

  private List<ProjectPolicyOwner> owners;
  private List<ProjectPolicyCrp> crps;
  private List<ProjectPolicySubIdo> subIdos;
  private List<ProjectPolicyCrossCuttingMarker> crossCuttingMarkers;

  private List<ProjectPolicyCountry> regions;

  private Set<ProjectExpectedStudyPolicy> projectExpectedStudyPolicies = new HashSet<ProjectExpectedStudyPolicy>(0);
  private List<ProjectExpectedStudyPolicy> evidences;


  public String getComposedName() {


    if (projectPolicyInfo != null) {
      return this.getId() + " - " + projectPolicyInfo.getTitle();
    } else {
      return "" + this.getId();
    }

  }


  public List<ProjectPolicyCountry> getCountries() {
    return countries;
  }


  public List<String> getCountriesIds() {
    return countriesIds;
  }


  public String getCountriesIdsText() {
    return countriesIdsText;
  }


  public List<ProjectPolicyCrossCuttingMarker> getCrossCuttingMarkers() {
    return crossCuttingMarkers;
  }


  public List<ProjectPolicyCrp> getCrps() {
    return crps;
  }

  public List<ProjectExpectedStudyPolicy> getEvidences() {
    return evidences;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public List<ProjectPolicyOwner> getOwners() {
    return owners;
  }


  public Project getProject() {
    return project;
  }


  public Set<ProjectExpectedStudyPolicy> getProjectExpectedStudyPolicies() {
    return projectExpectedStudyPolicies;
  }


  public Set<ProjectPolicyCountry> getProjectPolicyCountries() {
    return ProjectPolicyCountries;
  }


  public Set<ProjectPolicyCrossCuttingMarker> getProjectPolicyCrossCuttingMarkers() {
    return projectPolicyCrossCuttingMarkers;
  }


  public Set<ProjectPolicyCrp> getProjectPolicyCrps() {
    return projectPolicyCrps;
  }


  public ProjectPolicyInfo getProjectPolicyInfo() {
    return projectPolicyInfo;
  }


  public ProjectPolicyInfo getProjectPolicyInfo(Phase phase) {
    if (this.getProjectPolicyInfo() != null) {
      return this.getProjectPolicyInfo();
    } else {
      List<ProjectPolicyInfo> infos =
        projectPolicyInfos.stream().filter(c -> c != null && c.getPhase() != null && c.getPhase().getId() != null
          && c.getPhase().getId().longValue() == phase.getId()).collect(Collectors.toList());
      if (!infos.isEmpty()) {
        this.setProjectPolicyInfo(infos.get(0));
        return this.getProjectPolicyInfo();
      } else {
        return null;
      }
    }
  }


  public Set<ProjectPolicyInfo> getProjectPolicyInfos() {
    return projectPolicyInfos;
  }


  public Set<ProjectPolicyOwner> getProjectPolicyOwners() {
    return projectPolicyOwners;
  }


  public Set<ProjectPolicySubIdo> getProjectPolicySubIdos() {
    return projectPolicySubIdos;
  }


  public List<ProjectPolicyCountry> getRegions() {
    return regions;
  }

  public List<ProjectPolicySubIdo> getSubIdos() {
    return subIdos;
  }

  public void setCountries(List<ProjectPolicyCountry> countries) {
    this.countries = countries;
  }


  public void setCountriesIds(List<String> countriesIds) {
    this.countriesIds = countriesIds;
  }


  public void setCountriesIdsText(String countriesIdsText) {
    this.countriesIdsText = countriesIdsText;
  }


  public void setCrossCuttingMarkers(List<ProjectPolicyCrossCuttingMarker> crossCuttingMarkers) {
    this.crossCuttingMarkers = crossCuttingMarkers;
  }


  public void setCrps(List<ProjectPolicyCrp> crps) {
    this.crps = crps;
  }


  public void setEvidences(List<ProjectExpectedStudyPolicy> evidences) {
    this.evidences = evidences;
  }


  public void setOwners(List<ProjectPolicyOwner> owners) {
    this.owners = owners;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectExpectedStudyPolicies(Set<ProjectExpectedStudyPolicy> projectExpectedStudyPolicies) {
    this.projectExpectedStudyPolicies = projectExpectedStudyPolicies;
  }

  public void setProjectPolicyCountries(Set<ProjectPolicyCountry> projectPolicyCountries) {
    ProjectPolicyCountries = projectPolicyCountries;
  }

  public void
    setProjectPolicyCrossCuttingMarkers(Set<ProjectPolicyCrossCuttingMarker> projectPolicyCrossCuttingMarkers) {
    this.projectPolicyCrossCuttingMarkers = projectPolicyCrossCuttingMarkers;
  }

  public void setProjectPolicyCrps(Set<ProjectPolicyCrp> projectPolicyCrps) {
    this.projectPolicyCrps = projectPolicyCrps;
  }

  public void setProjectPolicyInfo(ProjectPolicyInfo projectPolicyInfo) {
    this.projectPolicyInfo = projectPolicyInfo;
  }

  public void setProjectPolicyInfos(Set<ProjectPolicyInfo> projectPolicyInfos) {
    this.projectPolicyInfos = projectPolicyInfos;
  }

  public void setProjectPolicyOwners(Set<ProjectPolicyOwner> projectPolicyOwners) {
    this.projectPolicyOwners = projectPolicyOwners;
  }

  public void setProjectPolicySubIdos(Set<ProjectPolicySubIdo> projectPolicySubIdos) {
    this.projectPolicySubIdos = projectPolicySubIdos;
  }

  public void setRegions(List<ProjectPolicyCountry> regions) {
    this.regions = regions;
  }

  public void setSubIdos(List<ProjectPolicySubIdo> subIdos) {
    this.subIdos = subIdos;
  }

  @Override
  public String toString() {
    return "ProjectPolicy [id=" + this.getId() + ", isActive=" + this.isActive() + "]";
  }

}

