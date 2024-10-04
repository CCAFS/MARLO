package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 22, 2018 1:53:05 PM by Hibernate Tools 4.3.1.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

/**
 * ProjectExpectedStudy generated by hbm2java
 */
public class ProjectExpectedStudy extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 8398283484560886533L;

  @Expose
  private Long phase;

  private Project project;

  @Expose
  private SrfSubIdo srfSubIdo;

  @Expose
  private String composedId;

  @Expose
  private String topicStudy;

  @Expose
  private Integer scope;

  @Expose
  private Integer type;

  @Expose
  private String otherType;

  @Expose
  private SrfSloIndicator srfSloIndicator;

  @Expose
  private String comments;

  @Expose
  private Integer year;

  private CaseStudy casesStudy;

  private String commentStatus;

  private AllianceLever allianceLever;
  private List<AllianceLever> allianceLevers;
  private ImpactArea impactArea;


  // Work to Table in POWB 2019
  private List<LiaisonInstitution> selectedFlahsgips;


  private Set<ExpectedStudyProject> expectedStudyProjects = new HashSet<ExpectedStudyProject>(0);

  private List<ExpectedStudyProject> projects;

  // New Reporting @HJ
  private Set<SectionStatus> sectionStatuses = new HashSet<SectionStatus>(0);

  private Set<ProjectExpectedStudyInfo> projectExpectedStudyInfos = new HashSet<ProjectExpectedStudyInfo>(0);

  private Set<ProjectExpectedStudySubIdo> projectExpectedStudySubIdos = new HashSet<ProjectExpectedStudySubIdo>(0);

  private Set<ProjectExpectedStudyFlagship> projectExpectedStudyFlagships =
    new HashSet<ProjectExpectedStudyFlagship>(0);

  private Set<ProjectExpectedStudyCrp> projectExpectedStudyCrps = new HashSet<ProjectExpectedStudyCrp>(0);

  private Set<ProjectExpectedStudyInstitution> projectExpectedStudyInstitutions =
    new HashSet<ProjectExpectedStudyInstitution>(0);

  private Set<ProjectExpectedStudyCountry> projectExpectedStudyCountries = new HashSet<ProjectExpectedStudyCountry>(0);

  private Set<ProjectExpectedStudyRegion> projectExpectedStudyRegions = new HashSet<ProjectExpectedStudyRegion>(0);

  private Set<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargets =
    new HashSet<ProjectExpectedStudySrfTarget>(0);

  private ProjectExpectedStudyInfo projectExpectedStudyInfo;

  private List<ProjectExpectedStudySubIdo> subIdos;

  private List<ProjectExpectedStudyFlagship> flagships;

  private List<ProjectExpectedStudyCrp> crps;

  private List<ProjectExpectedStudyPartnership> institutions;


  private List<ProjectExpectedStudyCountry> countries;


  private List<String> countriesIds = new ArrayList<>();


  private String countriesIdsText;

  private List<ProjectExpectedStudySrfTarget> srfTargets;

  private List<ProjectExpectedStudyFlagship> regions;


  private List<ProjectExpectedStudyRegion> studyRegions;


  // AR 2018 Lists
  private Set<ProjectExpectedStudyLink> projectExpectedStudyLinks = new HashSet<ProjectExpectedStudyLink>(0);

  private List<ProjectExpectedStudyLink> links;


  private Set<ProjectExpectedStudyPolicy> projectExpectedStudyPolicies = new HashSet<ProjectExpectedStudyPolicy>(0);


  private List<ProjectExpectedStudyPolicy> policies;


  private Set<ProjectExpectedStudyQuantification> projectExpectedStudyQuantifications =
    new HashSet<ProjectExpectedStudyQuantification>(0);

  private List<ProjectExpectedStudyQuantification> quantifications;

  private Set<ProjectExpectedStudyInnovation> projectExpectedStudyInnovations =
    new HashSet<ProjectExpectedStudyInnovation>(0);

  private List<ProjectExpectedStudyInnovation> innovations;


  private Set<ProjectExpectedStudyGeographicScope> projectExpectedStudyGeographicScopes =
    new HashSet<ProjectExpectedStudyGeographicScope>(0);


  private List<ProjectExpectedStudyGeographicScope> geographicScopes;

  private String geographicScopeString;


  // reporting 2019 field
  private List<ProjectExpectedStudyPartnership> centers;

  private Set<ProjectExpectedStudyCenter> projectExpectedStudyCenters = new HashSet<ProjectExpectedStudyCenter>(0);

  private List<ProjectExpectedStudyMilestone> milestones;

  private Set<ProjectExpectedStudyMilestone> projectExpectedStudyMilestones =
    new HashSet<ProjectExpectedStudyMilestone>(0);

  private List<ProjectExpectedStudyProjectOutcome> projectOutcomes;

  private Set<ProjectExpectedStudyProjectOutcome> projectExpectedStudyProjectOutcomes =
    new HashSet<ProjectExpectedStudyProjectOutcome>(0);

  private List<ProjectExpectedStudyCrpOutcome> crpOutcomes;

  private Set<ProjectExpectedStudyCrpOutcome> projectExpectedStudyCrpOutcomes =
    new HashSet<ProjectExpectedStudyCrpOutcome>(0);

  // AR 2021
  private Set<ProjectExpectedStudyReference> projectExpectedStudyReferences = new HashSet<>(0);

  private List<ProjectExpectedStudyReference> references;

  // clarisa field
  private String pdfLink;

  private Set<ProjectExpectedStudyPublication> projectExpectedStudyPublications = new HashSet<>(0);
  private List<ProjectExpectedStudyPublication> publications;

  private Set<ProjectExpectedStudySdgAllianceLever> projectExpectedStudySdgAllianceLevers = new HashSet<>(0);


  private List<ProjectExpectedStudySdgAllianceLever> sdgAllianceLevers;

  private Set<ProjectExpectedStudyAllianceLeversOutcome> projectExpectedStudyAllianceLeversOutcomes = new HashSet<>(0);


  private List<ProjectExpectedStudyAllianceLeversOutcome> allianceLeversOutcomes;

  private Set<ProjectExpectedStudyPartnership> projectExpectedStudyPartnerships = new HashSet<>(0);


  private List<ProjectExpectedStudyPartnership> partnerships;

  private Set<ProjectExpectedStudyGlobalTarget> projectExpectedStudyGlobalTargets = new HashSet<>(0);


  private List<ProjectExpectedStudyGlobalTarget> globalTargets;

  private Set<ProjectExpectedStudyImpactArea> projectExpectedStudyImpactAreas = new HashSet<>(0);


  private List<ProjectExpectedStudyImpactArea> impactAreas;


  public ProjectExpectedStudy() {
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
    ProjectExpectedStudy other = (ProjectExpectedStudy) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public AllianceLever getAllianceLever() {
    return allianceLever;
  }


  public List<AllianceLever> getAllianceLevers() {
    return allianceLevers;
  }


  public List<ProjectExpectedStudyAllianceLeversOutcome> getAllianceLeversOutcomes() {
    return allianceLeversOutcomes;
  }


  public CaseStudy getCasesStudy() {
    return casesStudy;
  }


  public List<ProjectExpectedStudyPartnership> getCenters() {
    return centers;
  }

  public String getComments() {
    return comments;
  }


  public String getCommentStatus() {
    return commentStatus;
  }


  public String getComposedId() {
    return composedId;
  }


  public String getComposedIdentifier() {
    String name = "S" + this.getId();
    if (this.getProjectExpectedStudyInfo() != null && this.getProjectExpectedStudyInfo().getStudyType() != null
      && this.getProjectExpectedStudyInfo().getStudyType().getId().intValue() == 1) {
      name = "OICR" + this.getId();
    }
    return name;
  }


  public String getComposedName() {
    String composedName = this.getComposedIdentifier();
    if (this.getProjectExpectedStudyInfo() != null) {
      if (this.getProjectExpectedStudyInfo().getTitle() != null) {
        composedName = composedName + " - " + this.getProjectExpectedStudyInfo().getTitle();
      } else {
        composedName = composedName + " - Undefined";
      }
    }
    return composedName;
  }


  public String getComposedNameAlternative() {
    if ((this.getProjectExpectedStudyInfo() != null)) {
      if ((this.getProjectExpectedStudyInfo().getTitle() != null)
        && (this.getProjectExpectedStudyInfo().getTitle().trim().length() > 0)) {
        return "(" + this.getProjectExpectedStudyInfo().getYear() + ") ID" + this.getId() + " - "
          + this.getProjectExpectedStudyInfo().getTitle();
      } else {
        return "(" + this.getProjectExpectedStudyInfo().getYear() + ") ID" + this.getId() + " - Untitled";
      }
    } else {
      return "" + this.getId() + " - Untitled";
    }
  }

  public List<ProjectExpectedStudyCountry> getCountries() {
    return countries;
  }


  public List<String> getCountriesIds() {
    return countriesIds;
  }


  public String getCountriesIdsText() {
    return countriesIdsText;
  }


  public List<ProjectExpectedStudyCrpOutcome> getCrpOutcomes() {
    return crpOutcomes;
  }


  public List<ProjectExpectedStudyCrp> getCrps() {
    return crps;
  }


  public Set<ExpectedStudyProject> getExpectedStudyProjects() {
    return expectedStudyProjects;
  }


  public List<ProjectExpectedStudyFlagship> getFlagships() {
    return flagships;
  }


  public List<ProjectExpectedStudyGeographicScope> getGeographicScopes() {
    return geographicScopes;
  }


  public String getGeographicScopeString() {
    return geographicScopeString;
  }


  public List<ProjectExpectedStudyGlobalTarget> getGlobalTargets() {
    return globalTargets;
  }

  public ImpactArea getImpactArea() {
    return impactArea;
  }


  public List<ProjectExpectedStudyImpactArea> getImpactAreas() {
    return impactAreas;
  }


  public List<ProjectExpectedStudyInnovation> getInnovations() {
    return innovations;
  }


  public List<ProjectExpectedStudyPartnership> getInstitutions() {
    return institutions;
  }

  public List<ProjectExpectedStudyLink> getLinks() {
    return links;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public List<ProjectExpectedStudyMilestone> getMilestones() {
    return milestones;
  }


  public String getOtherType() {
    return otherType;
  }


  public List<ProjectExpectedStudyPartnership> getPartnerships() {
    return partnerships;
  }


  public String getPdfLink() {
    return pdfLink;
  }

  public Long getPhase() {
    return phase;
  }

  public List<ProjectExpectedStudyPolicy> getPolicies() {
    return policies;
  }

  public Project getProject() {
    return project;
  }

  public Set<ProjectExpectedStudyAllianceLeversOutcome> getProjectExpectedStudyAllianceLeversOutcomes() {
    return projectExpectedStudyAllianceLeversOutcomes;
  }

  public Set<ProjectExpectedStudyCenter> getProjectExpectedStudyCenters() {
    return projectExpectedStudyCenters;
  }

  public Set<ProjectExpectedStudyCountry> getProjectExpectedStudyCountries() {
    return projectExpectedStudyCountries;
  }

  public Set<ProjectExpectedStudyCrpOutcome> getProjectExpectedStudyCrpOutcomes() {
    return projectExpectedStudyCrpOutcomes;
  }

  public Set<ProjectExpectedStudyCrp> getProjectExpectedStudyCrps() {
    return projectExpectedStudyCrps;
  }


  public Set<ProjectExpectedStudyFlagship> getProjectExpectedStudyFlagships() {
    return projectExpectedStudyFlagships;
  }

  public Set<ProjectExpectedStudyGeographicScope> getProjectExpectedStudyGeographicScopes() {
    return projectExpectedStudyGeographicScopes;
  }

  public Set<ProjectExpectedStudyGlobalTarget> getProjectExpectedStudyGlobalTargets() {
    return projectExpectedStudyGlobalTargets;
  }

  public Set<ProjectExpectedStudyImpactArea> getProjectExpectedStudyImpactAreas() {
    return projectExpectedStudyImpactAreas;
  }

  public ProjectExpectedStudyInfo getProjectExpectedStudyInfo() {
    return projectExpectedStudyInfo;
  }

  public ProjectExpectedStudyInfo getProjectExpectedStudyInfo(Phase phase) {
    if (this.getProjectExpectedStudyInfo() != null) {
      return this.getProjectExpectedStudyInfo();
    } else {
      List<ProjectExpectedStudyInfo> infos =
        projectExpectedStudyInfos.stream().filter(c -> c != null && c.getPhase() != null && c.getPhase().getId() != null
          && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
      if (!infos.isEmpty()) {
        this.setProjectExpectedStudyInfo(infos.get(0));
        return this.getProjectExpectedStudyInfo();
      } else {
        return null;
      }
    }
  }

  public Set<ProjectExpectedStudyInfo> getProjectExpectedStudyInfos() {
    return projectExpectedStudyInfos;
  }

  public Set<ProjectExpectedStudyInnovation> getProjectExpectedStudyInnovations() {
    return projectExpectedStudyInnovations;
  }

  public Set<ProjectExpectedStudyInstitution> getProjectExpectedStudyInstitutions() {
    return projectExpectedStudyInstitutions;
  }

  public Set<ProjectExpectedStudyLink> getProjectExpectedStudyLinks() {
    return projectExpectedStudyLinks;
  }


  public Set<ProjectExpectedStudyMilestone> getProjectExpectedStudyMilestones() {
    return projectExpectedStudyMilestones;
  }


  public Set<ProjectExpectedStudyPartnership> getProjectExpectedStudyPartnerships() {
    return projectExpectedStudyPartnerships;
  }

  public Set<ProjectExpectedStudyPolicy> getProjectExpectedStudyPolicies() {
    return projectExpectedStudyPolicies;
  }

  public Set<ProjectExpectedStudyProjectOutcome> getProjectExpectedStudyProjectOutcomes() {
    return projectExpectedStudyProjectOutcomes;
  }

  public Set<ProjectExpectedStudyPublication> getProjectExpectedStudyPublications() {
    return projectExpectedStudyPublications;
  }

  public Set<ProjectExpectedStudyQuantification> getProjectExpectedStudyQuantifications() {
    return projectExpectedStudyQuantifications;
  }

  public Set<ProjectExpectedStudyReference> getProjectExpectedStudyReferences() {
    return projectExpectedStudyReferences;
  }

  public Set<ProjectExpectedStudyRegion> getProjectExpectedStudyRegions() {
    return projectExpectedStudyRegions;
  }

  public Set<ProjectExpectedStudySdgAllianceLever> getProjectExpectedStudySdgAllianceLevers() {
    return projectExpectedStudySdgAllianceLevers;
  }

  public Set<ProjectExpectedStudySrfTarget> getProjectExpectedStudySrfTargets() {
    return projectExpectedStudySrfTargets;
  }


  public Set<ProjectExpectedStudySubIdo> getProjectExpectedStudySubIdos() {
    return projectExpectedStudySubIdos;
  }

  public List<ProjectExpectedStudyProjectOutcome> getProjectOutcomes() {
    return projectOutcomes;
  }

  public List<ExpectedStudyProject> getProjects() {
    return projects;
  }

  public List<ProjectExpectedStudyPublication> getPublications() {
    return publications;
  }

  public List<ProjectExpectedStudyQuantification> getQuantifications() {
    return quantifications;
  }

  public List<ProjectExpectedStudyReference> getReferences() {
    return references;
  }

  public List<ProjectExpectedStudyFlagship> getRegions() {
    return regions;
  }

  public Integer getScope() {
    return scope;
  }

  public String getScopeName() {
    if (scope == null || scope.intValue() == -1) {
      return "";
    }
    return GlobalScopeEnum.getValue(scope.intValue()).getType();

  }

  public List<ProjectExpectedStudySdgAllianceLever> getSdgAllianceLevers() {
    return sdgAllianceLevers;
  }


  public Set<SectionStatus> getSectionStatuses() {
    return sectionStatuses;
  }


  public List<LiaisonInstitution> getSelectedFlahsgips() {
    return selectedFlahsgips;
  }

  public SrfSloIndicator getSrfSloIndicator() {
    return srfSloIndicator;
  }


  public SrfSubIdo getSrfSubIdo() {
    return srfSubIdo;
  }

  public List<ProjectExpectedStudySrfTarget> getSrfTargets() {
    return srfTargets;
  }

  public List<ProjectExpectedStudySrfTarget> getSrfTargets(Phase phase) {
    return new ArrayList<>(this.getProjectExpectedStudySrfTargets().stream()
      .filter(ps -> ps.isActive() && ps.getPhase().equals(phase)).collect(Collectors.toList()));
  }


  public List<ProjectExpectedStudyRegion> getStudyRegions() {
    return studyRegions;
  }


  public List<ProjectExpectedStudySubIdo> getSubIdos() {
    return subIdos;
  }

  public List<ProjectExpectedStudySubIdo> getSubIdos(Phase phase) {
    return new ArrayList<>(this.getProjectExpectedStudySubIdos().stream()
      .filter(ps -> ps.isActive() && ps.getPhase().equals(phase)).collect(Collectors.toList()));
  }

  public String getTopicStudy() {
    return topicStudy;
  }


  public Integer getType() {
    return type;
  }


  public String getTypeName() {
    if (type == null || type.intValue() == -1) {
      return "";
    }
    return TypeExpectedStudiesEnum.getValue(type.intValue()).getType();

  }

  public Integer getYear() {
    return year;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setAllianceLever(AllianceLever allianceLever) {
    this.allianceLever = allianceLever;
  }

  public void setAllianceLevers(List<AllianceLever> allianceLevers) {
    this.allianceLevers = allianceLevers;
  }

  public void setAllianceLeversOutcomes(List<ProjectExpectedStudyAllianceLeversOutcome> allianceLeversOutcomes) {
    this.allianceLeversOutcomes = allianceLeversOutcomes;
  }

  public void setCasesStudy(CaseStudy casesStudy) {
    this.casesStudy = casesStudy;
  }

  public void setCenters(List<ProjectExpectedStudyPartnership> centers) {
    this.centers = centers;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public void setCommentStatus(String commentStatus) {
    this.commentStatus = commentStatus;
  }

  public void setComposedId(String composedId) {
    this.composedId = composedId;
  }

  public void setCountries(List<ProjectExpectedStudyCountry> countries) {
    this.countries = countries;
  }

  public void setCountriesIds(List<String> countriesIds) {
    this.countriesIds = countriesIds;
  }

  public void setCountriesIdsText(String countriesIdsText) {
    this.countriesIdsText = countriesIdsText;
  }

  public void setCrpOutcomes(List<ProjectExpectedStudyCrpOutcome> crpOutcomes) {
    this.crpOutcomes = crpOutcomes;
  }

  public void setCrps(List<ProjectExpectedStudyCrp> crps) {
    this.crps = crps;
  }

  public void setExpectedStudyProjects(Set<ExpectedStudyProject> expectedStudyProjects) {
    this.expectedStudyProjects = expectedStudyProjects;
  }

  public void setFlagships(List<ProjectExpectedStudyFlagship> flagships) {
    this.flagships = flagships;
  }

  public void setGeographicScopes(List<ProjectExpectedStudyGeographicScope> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }

  public void setGeographicScopeString(String geographicScopeString) {
    this.geographicScopeString = geographicScopeString;
  }

  public void setGlobalTargets(List<ProjectExpectedStudyGlobalTarget> globalTargets) {
    this.globalTargets = globalTargets;
  }

  public void setImpactArea(ImpactArea impactArea) {
    this.impactArea = impactArea;
  }

  public void setImpactAreas(List<ProjectExpectedStudyImpactArea> impactAreas) {
    this.impactAreas = impactAreas;
  }

  public void setInnovations(List<ProjectExpectedStudyInnovation> innovations) {
    this.innovations = innovations;
  }

  public void setInstitutions(List<ProjectExpectedStudyPartnership> institutions) {
    this.institutions = institutions;
  }

  public void setLinks(List<ProjectExpectedStudyLink> links) {
    this.links = links;
  }

  public void setMilestones(List<ProjectExpectedStudyMilestone> milestones) {
    this.milestones = milestones;
  }

  public void setOtherType(String otherType) {
    this.otherType = otherType;
  }


  public void setPartnerships(List<ProjectExpectedStudyPartnership> partnerships) {
    this.partnerships = partnerships;
  }

  public void setPdfLink(String pdfLink) {
    this.pdfLink = pdfLink;
  }

  public void setPhase(Long phase) {
    this.phase = phase;
  }

  public void setPolicies(List<ProjectExpectedStudyPolicy> policies) {
    this.policies = policies;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectExpectedStudyAllianceLeversOutcomes(
    Set<ProjectExpectedStudyAllianceLeversOutcome> projectExpectedStudyAllianceLeversOutcomes) {
    this.projectExpectedStudyAllianceLeversOutcomes = projectExpectedStudyAllianceLeversOutcomes;
  }

  public void setProjectExpectedStudyCenters(Set<ProjectExpectedStudyCenter> projectExpectedStudyCenters) {
    this.projectExpectedStudyCenters = projectExpectedStudyCenters;
  }

  public void setProjectExpectedStudyCountries(Set<ProjectExpectedStudyCountry> projectExpectedStudyCountries) {
    this.projectExpectedStudyCountries = projectExpectedStudyCountries;
  }

  public void setProjectExpectedStudyCrpOutcomes(Set<ProjectExpectedStudyCrpOutcome> projectExpectedStudyCrpOutcomes) {
    this.projectExpectedStudyCrpOutcomes = projectExpectedStudyCrpOutcomes;
  }

  public void setProjectExpectedStudyCrps(Set<ProjectExpectedStudyCrp> projectExpectedStudyCrps) {
    this.projectExpectedStudyCrps = projectExpectedStudyCrps;
  }

  public void setProjectExpectedStudyFlagships(Set<ProjectExpectedStudyFlagship> projectExpectedStudyFlagships) {
    this.projectExpectedStudyFlagships = projectExpectedStudyFlagships;
  }


  public void setProjectExpectedStudyGeographicScopes(
    Set<ProjectExpectedStudyGeographicScope> projectExpectedStudyGeographicScopes) {
    this.projectExpectedStudyGeographicScopes = projectExpectedStudyGeographicScopes;
  }

  public void
    setProjectExpectedStudyGlobalTargets(Set<ProjectExpectedStudyGlobalTarget> projectExpectedStudyGlobalTargets) {
    this.projectExpectedStudyGlobalTargets = projectExpectedStudyGlobalTargets;
  }

  public void setProjectExpectedStudyImpactAreas(Set<ProjectExpectedStudyImpactArea> projectExpectedStudyImpactAreas) {
    this.projectExpectedStudyImpactAreas = projectExpectedStudyImpactAreas;
  }

  public void setProjectExpectedStudyInfo(ProjectExpectedStudyInfo projectExpectedStudyInfo) {
    this.projectExpectedStudyInfo = projectExpectedStudyInfo;
  }

  public void setProjectExpectedStudyInfos(Set<ProjectExpectedStudyInfo> projectExpectedStudyInfos) {
    this.projectExpectedStudyInfos = projectExpectedStudyInfos;
  }

  public void setProjectExpectedStudyInnovations(Set<ProjectExpectedStudyInnovation> projectExpectedStudyInnovations) {
    this.projectExpectedStudyInnovations = projectExpectedStudyInnovations;
  }

  public void
    setProjectExpectedStudyInstitutions(Set<ProjectExpectedStudyInstitution> projectExpectedStudyInstitutions) {
    this.projectExpectedStudyInstitutions = projectExpectedStudyInstitutions;
  }


  public void setProjectExpectedStudyLinks(Set<ProjectExpectedStudyLink> projectExpectedStudyLinks) {
    this.projectExpectedStudyLinks = projectExpectedStudyLinks;
  }

  public void setProjectExpectedStudyMilestones(Set<ProjectExpectedStudyMilestone> projectExpectedStudyMilestones) {
    this.projectExpectedStudyMilestones = projectExpectedStudyMilestones;
  }

  public void
    setProjectExpectedStudyPartnerships(Set<ProjectExpectedStudyPartnership> projectExpectedStudyPartnerships) {
    this.projectExpectedStudyPartnerships = projectExpectedStudyPartnerships;
  }

  public void setProjectExpectedStudyPolicies(Set<ProjectExpectedStudyPolicy> projectExpectedStudyPolicies) {
    this.projectExpectedStudyPolicies = projectExpectedStudyPolicies;
  }

  public void setProjectExpectedStudyProjectOutcomes(
    Set<ProjectExpectedStudyProjectOutcome> projectExpectedStudyProjectOutcomes) {
    this.projectExpectedStudyProjectOutcomes = projectExpectedStudyProjectOutcomes;
  }

  public void
    setProjectExpectedStudyPublications(Set<ProjectExpectedStudyPublication> projectExpectedStudyPublications) {
    this.projectExpectedStudyPublications = projectExpectedStudyPublications;
  }


  public void setProjectExpectedStudyQuantifications(
    Set<ProjectExpectedStudyQuantification> projectExpectedStudyQuantifications) {
    this.projectExpectedStudyQuantifications = projectExpectedStudyQuantifications;
  }


  public void setProjectExpectedStudyReferences(Set<ProjectExpectedStudyReference> projectExpectedStudyReferences) {
    this.projectExpectedStudyReferences = projectExpectedStudyReferences;
  }

  public void setProjectExpectedStudyRegions(Set<ProjectExpectedStudyRegion> projectExpectedStudyRegions) {
    this.projectExpectedStudyRegions = projectExpectedStudyRegions;
  }


  public void setProjectExpectedStudySdgAllianceLevers(
    Set<ProjectExpectedStudySdgAllianceLever> projectExpectedStudySdgAllianceLevers) {
    this.projectExpectedStudySdgAllianceLevers = projectExpectedStudySdgAllianceLevers;
  }

  public void setProjectExpectedStudySrfTargets(Set<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargets) {
    this.projectExpectedStudySrfTargets = projectExpectedStudySrfTargets;
  }


  public void setProjectExpectedStudySubIdos(Set<ProjectExpectedStudySubIdo> projectExpectedStudySubIdos) {
    this.projectExpectedStudySubIdos = projectExpectedStudySubIdos;
  }

  public void setProjectOutcomes(List<ProjectExpectedStudyProjectOutcome> projectOutcomes) {
    this.projectOutcomes = projectOutcomes;
  }

  public void setProjects(List<ExpectedStudyProject> projects) {
    this.projects = projects;
  }

  public void setPublications(List<ProjectExpectedStudyPublication> publications) {
    this.publications = publications;
  }

  public void setQuantifications(List<ProjectExpectedStudyQuantification> quantifications) {
    this.quantifications = quantifications;
  }


  public void setReferences(List<ProjectExpectedStudyReference> references) {
    this.references = references;
  }

  public void setRegions(List<ProjectExpectedStudyFlagship> regions) {
    this.regions = regions;
  }

  public void setScope(Integer scope) {
    this.scope = scope;
  }

  public void setSdgAllianceLevers(List<ProjectExpectedStudySdgAllianceLever> sdgAllianceLevers) {
    this.sdgAllianceLevers = sdgAllianceLevers;
  }

  public void setSectionStatuses(Set<SectionStatus> sectionStatuses) {
    this.sectionStatuses = sectionStatuses;
  }

  public void setSelectedFlahsgips(List<LiaisonInstitution> selectedFlahsgips) {
    this.selectedFlahsgips = selectedFlahsgips;
  }

  public void setSrfSloIndicator(SrfSloIndicator srfSloIndicator) {
    this.srfSloIndicator = srfSloIndicator;
  }

  public void setSrfSubIdo(SrfSubIdo srfSubIdo) {
    this.srfSubIdo = srfSubIdo;
  }

  public void setSrfTargets(List<ProjectExpectedStudySrfTarget> srfTargets) {
    this.srfTargets = srfTargets;
  }

  public void setStudyRegions(List<ProjectExpectedStudyRegion> studyRegions) {
    this.studyRegions = studyRegions;
  }

  public void setSubIdos(List<ProjectExpectedStudySubIdo> subIdos) {
    this.subIdos = subIdos;
  }

  public void setTopicStudy(String topicStudy) {
    this.topicStudy = topicStudy;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  @Override
  public String toString() {
    return "Id [" + this.getId() + "]" + "ProjectExpectedStudy [projectExpectedStudyInfo=" + projectExpectedStudyInfo
      + "]";
  }

}
