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

  private Set<SectionStatus> sectionStatuses = new HashSet<SectionStatus>(0);


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
  private List<ProjectPolicyRegion> regions;
  private Set<ProjectExpectedStudyPolicy> projectExpectedStudyPolicies = new HashSet<ProjectExpectedStudyPolicy>(0);

  private List<ProjectExpectedStudyPolicy> evidences;

  private Set<ProjectPolicyInnovation> projectPolicyInnovations = new HashSet<ProjectPolicyInnovation>(0);

  private List<ProjectPolicyInnovation> innovations;

  private Set<PolicyMilestone> policyMilestones = new HashSet<PolicyMilestone>(0);
  private List<PolicyMilestone> milestones;

  private Set<ProjectPolicyGeographicScope> projectPolicyGeographicScopes =
    new HashSet<ProjectPolicyGeographicScope>(0);


  private List<ProjectPolicyGeographicScope> geographicScopes;

  private Set<ProjectPolicyRegion> projectPolicyRegions = new HashSet<ProjectPolicyRegion>(0);

  // AR2018 Field
  private List<LiaisonInstitution> selectedFlahsgips;

  // AR2019 Field
  private List<ProjectPolicyCenter> centers;
  private Set<ProjectPolicyCenter> projectPolicyCenters = new HashSet<ProjectPolicyCenter>(0);

  // Alliance fields
  private List<ProjectPolicySdgTarget> sdgTargets;
  private Set<ProjectPolicySdgTarget> projectPolicySdgTargets = new HashSet<>(0);

  public List<ProjectPolicyCenter> getCenters() {
    return centers;
  }

  public String getComposedName() {
    if ((projectPolicyInfo != null) && (projectPolicyInfo.getTitle() != null)
      && (projectPolicyInfo.getTitle().trim().length() > 0)) {
      return this.getId() + " - " + projectPolicyInfo.getTitle();
    } else {
      return "" + this.getId() + " - Untitled";
    }
  }

  public String getComposedNameAlternative() {
    if ((projectPolicyInfo != null)) {
      if ((projectPolicyInfo.getTitle() != null) && (projectPolicyInfo.getTitle().trim().length() > 0)) {
        return "(" + projectPolicyInfo.getYear() + ") ID" + this.getId() + " - " + projectPolicyInfo.getTitle();
      } else {
        return "(" + projectPolicyInfo.getYear() + ") ID" + this.getId() + " - Untitled";
      }

    } else {
      return this.getId() + " - Untitled";
    }
  }

  public List<ProjectPolicyCountry> getCountries() {
    return countries;
  }

  public List<ProjectPolicyCountry> getCountries(Phase phase) {
    return new ArrayList<>(this.getProjectPolicyCountries().stream()
      .filter(pc -> pc.isActive() && pc.getPhase().equals(phase)).collect(Collectors.toList()));
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


  public List<ProjectPolicyCrossCuttingMarker> getCrossCuttingMarkers(Phase phase) {
    return new ArrayList<>(this.getProjectPolicyCrossCuttingMarkers().stream()
      .filter(pc -> pc.isActive() && pc.getPhase().equals(phase)).collect(Collectors.toList()));
  }

  public List<ProjectPolicyCrp> getCrps() {
    return crps;
  }

  public List<ProjectExpectedStudyPolicy> getEvidences() {
    return evidences;
  }

  public List<ProjectExpectedStudyPolicy> getEvidences(Phase phase) {
    return new ArrayList<>(this.getProjectExpectedStudyPolicies().stream()
      .filter(pp -> pp.isActive() && pp.getPhase().equals(phase) && pp.getProjectExpectedStudy() != null
        && pp.getProjectExpectedStudy().getProjectExpectedStudyInfo(phase) != null)
      .collect(Collectors.toList()));
  }

  public List<ProjectPolicyGeographicScope> getGeographicScopes() {
    return geographicScopes;
  }

  public List<ProjectPolicyGeographicScope> getGeographicScopes(Phase phase) {
    return new ArrayList<>(this.getProjectPolicyGeographicScopes().stream()
      .filter(pg -> pg.isActive() && pg.getPhase().equals(phase)).collect(Collectors.toList()));
  }

  public List<ProjectPolicyInnovation> getInnovations() {
    return innovations;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public List<PolicyMilestone> getMilestones() {
    return milestones;
  }


  public List<ProjectPolicyOwner> getOwners() {
    return owners;
  }

  public List<ProjectPolicyOwner> getOwners(Phase phase) {
    return new ArrayList<>(this.getProjectPolicyOwners().stream()
      .filter(po -> po.isActive() && po.getPhase().equals(phase)).collect(Collectors.toList()));
  }

  public Set<PolicyMilestone> getPolicyMilestones() {
    return policyMilestones;
  }


  public Project getProject() {
    return project;
  }


  public Set<ProjectExpectedStudyPolicy> getProjectExpectedStudyPolicies() {
    return projectExpectedStudyPolicies;
  }


  public Set<ProjectPolicyCenter> getProjectPolicyCenters() {
    return projectPolicyCenters;
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


  public Set<ProjectPolicyGeographicScope> getProjectPolicyGeographicScopes() {
    return projectPolicyGeographicScopes;
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


  public Set<ProjectPolicyInnovation> getProjectPolicyInnovations() {
    return projectPolicyInnovations;
  }


  public Set<ProjectPolicyOwner> getProjectPolicyOwners() {
    return projectPolicyOwners;
  }

  public Set<ProjectPolicyRegion> getProjectPolicyRegions() {
    return projectPolicyRegions;
  }


  public Set<ProjectPolicySdgTarget> getProjectPolicySdgTargets() {
    return projectPolicySdgTargets;
  }


  public Set<ProjectPolicySubIdo> getProjectPolicySubIdos() {
    return projectPolicySubIdos;
  }


  public List<ProjectPolicyRegion> getRegions() {
    return regions;
  }


  public List<ProjectPolicyRegion> getRegions(Phase phase) {
    return new ArrayList<>(this.getProjectPolicyRegions().stream()
      .filter(pr -> pr.isActive() && pr.getPhase().equals(phase)).collect(Collectors.toList()));
  }


  public List<ProjectPolicySdgTarget> getSdgTargets() {
    return sdgTargets;
  }


  public Set<SectionStatus> getSectionStatuses() {
    return sectionStatuses;
  }

  public List<LiaisonInstitution> getSelectedFlahsgips() {
    return selectedFlahsgips;
  }

  public List<ProjectPolicySubIdo> getSubIdos() {
    return subIdos;
  }


  /**
   * Get SubIdo list from DB (from ProjectPolicySubIdo set)
   * 
   * @param phase
   * @return SubIdo List of current phase
   */
  public List<ProjectPolicySubIdo> getSubIdos(Phase phase) {
    return new ArrayList<>(this.getProjectPolicySubIdos().stream()
      .filter(ps -> ps.isActive() && ps.getPhase().equals(phase)).collect(Collectors.toList()));
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setCenters(List<ProjectPolicyCenter> centers) {
    this.centers = centers;
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

  public void setGeographicScopes(List<ProjectPolicyGeographicScope> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }


  public void setInnovations(List<ProjectPolicyInnovation> innovations) {
    this.innovations = innovations;
  }


  public void setMilestones(List<PolicyMilestone> milestones) {
    this.milestones = milestones;
  }


  public void setOwners(List<ProjectPolicyOwner> owners) {
    this.owners = owners;
  }


  public void setPolicyMilestones(Set<PolicyMilestone> policyMilestones) {
    this.policyMilestones = policyMilestones;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectExpectedStudyPolicies(Set<ProjectExpectedStudyPolicy> projectExpectedStudyPolicies) {
    this.projectExpectedStudyPolicies = projectExpectedStudyPolicies;
  }


  public void setProjectPolicyCenters(Set<ProjectPolicyCenter> projectPolicyCenters) {
    this.projectPolicyCenters = projectPolicyCenters;
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

  public void setProjectPolicyGeographicScopes(Set<ProjectPolicyGeographicScope> projectPolicyGeographicScopes) {
    this.projectPolicyGeographicScopes = projectPolicyGeographicScopes;
  }

  public void setProjectPolicyInfo(ProjectPolicyInfo projectPolicyInfo) {
    this.projectPolicyInfo = projectPolicyInfo;
  }

  public void setProjectPolicyInfos(Set<ProjectPolicyInfo> projectPolicyInfos) {
    this.projectPolicyInfos = projectPolicyInfos;
  }

  public void setProjectPolicyInnovations(Set<ProjectPolicyInnovation> projectPolicyInnovations) {
    this.projectPolicyInnovations = projectPolicyInnovations;
  }

  public void setProjectPolicyOwners(Set<ProjectPolicyOwner> projectPolicyOwners) {
    this.projectPolicyOwners = projectPolicyOwners;
  }

  public void setProjectPolicyRegions(Set<ProjectPolicyRegion> projectPolicyRegions) {
    this.projectPolicyRegions = projectPolicyRegions;
  }

  public void setProjectPolicySdgTargets(Set<ProjectPolicySdgTarget> projectPolicySdgTargets) {
    this.projectPolicySdgTargets = projectPolicySdgTargets;
  }

  public void setProjectPolicySubIdos(Set<ProjectPolicySubIdo> projectPolicySubIdos) {
    this.projectPolicySubIdos = projectPolicySubIdos;
  }

  public void setRegions(List<ProjectPolicyRegion> regions) {
    this.regions = regions;
  }


  public void setSdgTargets(List<ProjectPolicySdgTarget> sdgTargets) {
    this.sdgTargets = sdgTargets;
  }


  public void setSectionStatuses(Set<SectionStatus> sectionStatuses) {
    this.sectionStatuses = sectionStatuses;
  }


  public void setSelectedFlahsgips(List<LiaisonInstitution> selectedFlahsgips) {
    this.selectedFlahsgips = selectedFlahsgips;
  }


  public void setSubIdos(List<ProjectPolicySubIdo> subIdos) {
    this.subIdos = subIdos;
  }


  @Override
  public String toString() {
    return "ProjectPolicy [id=" + this.getId() + ", isActive=" + this.isActive() + "]";
  }

}

