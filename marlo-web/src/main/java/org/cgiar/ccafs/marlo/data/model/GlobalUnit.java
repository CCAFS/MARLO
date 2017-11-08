package org.cgiar.ccafs.marlo.data.model;
// Generated Oct 30, 2017 10:01:51 AM by Hibernate Tools 3.4.0.CR1


import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Hermes Jimenez
 */
public class GlobalUnit implements java.io.Serializable {


  private static final long serialVersionUID = -3398924354219701873L;


  private Long id;

  private GlobalUnitType globalUnitType;


  private User modifiedBy;

  private User createdBy;


  private String name;

  private String acronym;


  private boolean active;

  private Date activeSince;


  private String modificationJustification;

  private boolean marlo;


  private boolean login;

  private Set<CenterLeader> centerLeaders = new HashSet<CenterLeader>(0);


  private Set<CrpProgram> crpPrograms = new HashSet<CrpProgram>(0);

  private Set<Project> projects = new HashSet<Project>(0);


  private Set<CrpTargetUnit> crpTargetUnits = new HashSet<CrpTargetUnit>(0);

  private Set<CenterArea> centerAreas = new HashSet<CenterArea>(0);


  private Set<DeliverableType> deliverableTypes = new HashSet<DeliverableType>(0);

  private Set<LocElementType> locElementTypes = new HashSet<LocElementType>(0);


  private Set<LocElement> locElements = new HashSet<LocElement>(0);

  private Set<CrpPpaPartner> crpPpaPartners = new HashSet<CrpPpaPartner>(0);


  private Set<CrpSubIdosContribution> crpSubIdosContributions = new HashSet<CrpSubIdosContribution>(0);

  private Set<LiaisonInstitution> liaisonInstitutions = new HashSet<LiaisonInstitution>(0);


  private Set<CenterObjective> centerObjectives = new HashSet<CenterObjective>(0);

  private Set<Phase> phases = new HashSet<Phase>(0);


  private Set<CenterCustomParameter> centerCustomParameters = new HashSet<CenterCustomParameter>(0);

  private Set<CenterProjectFundingSource> centerProjectFundingSources = new HashSet<CenterProjectFundingSource>(0);


  private Set<CenterRole> centerRoles = new HashSet<CenterRole>(0);

  private Set<CrpUser> crpUsers = new HashSet<CrpUser>(0);


  private Set<CrpsSiteIntegration> crpsSitesIntegrations = new HashSet<CrpsSiteIntegration>(0);

  private Set<CrpLocElementType> crpLocElementTypes = new HashSet<CrpLocElementType>(0);


  private Set<CustomParameter> customParameters = new HashSet<CustomParameter>(0);


  private Set<LiaisonUser> liasonUsers = new HashSet<LiaisonUser>(0);


  private Set<CenterUser> centerUsers = new HashSet<CenterUser>(0);

  private Set<Role> roles = new HashSet<Role>(0);

  private Set<GlobalUnitProject> globalUnitProjects = new HashSet<GlobalUnitProject>(0);

  private Set<FundingSource> fundingSources = new HashSet<FundingSource>(0);

  private Set<Deliverable> deliverables = new HashSet<Deliverable>(0);

  // Variables add for Crp Class
  private boolean hasRegions; // Used by CrpAdminManagmentAction

  private List<UserRole> programManagmenTeam; // Used by CrpAdminManagmentAction

  private List<LocElementType> locationCustomElementTypes; // Used by CrpLocationsAction

  private List<LocElementType> locationElementTypes; // Used by CrpLocationsAction

  private List<CustomLevelSelect> customLevels; // Used by CrpLocationsAction

  private List<CrpPpaPartner> crpInstitutionsPartners; // Used by CrpPpaPartnersAction

  private List<CrpsSiteIntegration> siteIntegrations; // Used by CrpSiteIntegrationAction

  private List<TargetUnitSelect> targetUnits; // Used by CrpTargetUnitsAction

  private List<Deliverable> deliverablesList; // Used by PublicationListAction

  private List<CustomParameter> parameters; // Used by CrpParametersAction

  public GlobalUnit() {
    // Default Constructor
  }

  public GlobalUnit(Long id, GlobalUnitType globalUnitType, User modifiedBy, User createdBy, String name,
    String acronym, boolean active, Date activeSince, String modificationJustification, boolean marlo, boolean login,
    Set<CenterLeader> centerLeaders, Set<CrpProgram> crpPrograms, Set<Project> projects,
    Set<CrpTargetUnit> crpTargetUnits, Set<CenterArea> centerAreas, Set<DeliverableType> deliverableTypes,
    Set<LocElementType> locElementTypes, Set<LocElement> locElements, Set<CrpPpaPartner> crpPpaPartners,
    Set<CrpSubIdosContribution> crpSubIdosContributions, Set<LiaisonInstitution> liaisonInstitutions,
    Set<CenterObjective> centerObjectives, Set<Phase> phases, Set<CenterCustomParameter> centerCustomParameters,
    Set<CenterProjectFundingSource> centerProjectFundingSources, Set<CenterRole> centerRoles, Set<CrpUser> crpUsers,
    Set<CrpsSiteIntegration> crpsSitesIntegrations, Set<CrpLocElementType> crpLocElementTypes,
    Set<CustomParameter> customParameters, Set<LiaisonUser> liasonUsers, Set<CenterUser> centerUsers, Set<Role> roles) {
    super();
    this.id = id;
    this.globalUnitType = globalUnitType;
    this.modifiedBy = modifiedBy;
    this.createdBy = createdBy;
    this.name = name;
    this.acronym = acronym;
    this.active = active;
    this.activeSince = activeSince;
    this.modificationJustification = modificationJustification;
    this.marlo = marlo;
    this.login = login;
    this.centerLeaders = centerLeaders;
    this.crpPrograms = crpPrograms;
    this.projects = projects;
    this.crpTargetUnits = crpTargetUnits;
    this.centerAreas = centerAreas;
    this.deliverableTypes = deliverableTypes;
    this.locElementTypes = locElementTypes;
    this.locElements = locElements;
    this.crpPpaPartners = crpPpaPartners;
    this.crpSubIdosContributions = crpSubIdosContributions;
    this.liaisonInstitutions = liaisonInstitutions;
    this.centerObjectives = centerObjectives;
    this.phases = phases;
    this.centerCustomParameters = centerCustomParameters;
    this.centerProjectFundingSources = centerProjectFundingSources;
    this.centerRoles = centerRoles;
    this.crpUsers = crpUsers;
    this.crpsSitesIntegrations = crpsSitesIntegrations;
    this.crpLocElementTypes = crpLocElementTypes;
    this.customParameters = customParameters;
    this.liasonUsers = liasonUsers;
    this.centerUsers = centerUsers;
    this.roles = roles;
  }

  public String getAcronym() {
    return acronym;
  }

  public Date getActiveSince() {
    return activeSince;
  }

  public Set<CenterArea> getCenterAreas() {
    return centerAreas;
  }

  public Set<CenterCustomParameter> getCenterCustomParameters() {
    return centerCustomParameters;
  }

  public Set<CenterLeader> getCenterLeaders() {
    return centerLeaders;
  }

  public Set<CenterObjective> getCenterObjectives() {
    return centerObjectives;
  }


  public Set<CenterProjectFundingSource> getCenterProjectFundingSources() {
    return centerProjectFundingSources;
  }

  public Set<CenterRole> getCenterRoles() {
    return centerRoles;
  }

  public Set<CenterUser> getCenterUsers() {
    return centerUsers;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public List<CrpPpaPartner> getCrpInstitutionsPartners() {
    return crpInstitutionsPartners;
  }

  public Set<CrpLocElementType> getCrpLocElementTypes() {
    return crpLocElementTypes;
  }

  public Set<CrpPpaPartner> getCrpPpaPartners() {
    return crpPpaPartners;
  }

  public Set<CrpProgram> getCrpPrograms() {
    return crpPrograms;
  }

  public Set<CrpsSiteIntegration> getCrpsSitesIntegrations() {
    return crpsSitesIntegrations;
  }

  public Set<CrpSubIdosContribution> getCrpSubIdosContributions() {
    return crpSubIdosContributions;
  }

  public Set<CrpTargetUnit> getCrpTargetUnits() {
    return crpTargetUnits;
  }

  public Set<CrpUser> getCrpUsers() {
    return crpUsers;
  }

  public List<CustomLevelSelect> getCustomLevels() {
    return customLevels;
  }

  public Set<CustomParameter> getCustomParameters() {
    return customParameters;
  }

  public Set<Deliverable> getDeliverables() {
    return deliverables;
  }

  public List<Deliverable> getDeliverablesList() {
    return deliverablesList;
  }

  public Set<DeliverableType> getDeliverableTypes() {
    return deliverableTypes;
  }

  public Set<FundingSource> getFundingSources() {
    return fundingSources;
  }

  public Set<GlobalUnitProject> getGlobalUnitProjects() {
    return globalUnitProjects;
  }

  public GlobalUnitType getGlobalUnitType() {
    return globalUnitType;
  }

  public Long getId() {
    return id;
  }

  public Set<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public Set<LiaisonUser> getLiasonUsers() {
    return liasonUsers;
  }

  public List<LocElementType> getLocationCustomElementTypes() {
    return locationCustomElementTypes;
  }


  public List<LocElementType> getLocationElementTypes() {
    return locationElementTypes;
  }

  public Set<LocElement> getLocElements() {
    return locElements;
  }

  public Set<LocElementType> getLocElementTypes() {
    return locElementTypes;
  }

  public String getModificationJustification() {
    return modificationJustification;
  }

  public User getModifiedBy() {
    return modifiedBy;
  }

  public String getName() {
    return name;
  }

  public List<CustomParameter> getParameters() {
    return parameters;
  }

  public Set<Phase> getPhases() {
    return phases;
  }

  public List<UserRole> getProgramManagmenTeam() {
    return programManagmenTeam;
  }

  public Set<Project> getProjects() {
    return projects;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public List<CrpsSiteIntegration> getSiteIntegrations() {
    return siteIntegrations;
  }

  public List<TargetUnitSelect> getTargetUnits() {
    return targetUnits;
  }

  public boolean isActive() {
    return active;
  }

  public boolean isHasRegions() {
    return hasRegions;
  }

  public boolean isLogin() {
    return login;
  }

  public boolean isMarlo() {
    return marlo;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  public void setCenterAreas(Set<CenterArea> centerAreas) {
    this.centerAreas = centerAreas;
  }

  public void setCenterCustomParameters(Set<CenterCustomParameter> centerCustomParameters) {
    this.centerCustomParameters = centerCustomParameters;
  }


  public void setCenterLeaders(Set<CenterLeader> centerLeaders) {
    this.centerLeaders = centerLeaders;
  }

  public void setCenterObjectives(Set<CenterObjective> centerObjectives) {
    this.centerObjectives = centerObjectives;
  }

  public void setCenterProjectFundingSources(Set<CenterProjectFundingSource> centerProjectFundingSources) {
    this.centerProjectFundingSources = centerProjectFundingSources;
  }

  public void setCenterRoles(Set<CenterRole> centerRoles) {
    this.centerRoles = centerRoles;
  }

  public void setCenterUsers(Set<CenterUser> centerUsers) {
    this.centerUsers = centerUsers;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setCrpInstitutionsPartners(List<CrpPpaPartner> crpInstitutionsPartners) {
    this.crpInstitutionsPartners = crpInstitutionsPartners;
  }

  public void setCrpLocElementTypes(Set<CrpLocElementType> crpLocElementTypes) {
    this.crpLocElementTypes = crpLocElementTypes;
  }

  public void setCrpPpaPartners(Set<CrpPpaPartner> crpPpaPartners) {
    this.crpPpaPartners = crpPpaPartners;
  }

  public void setCrpPrograms(Set<CrpProgram> crpPrograms) {
    this.crpPrograms = crpPrograms;
  }

  public void setCrpsSitesIntegrations(Set<CrpsSiteIntegration> crpsSitesIntegrations) {
    this.crpsSitesIntegrations = crpsSitesIntegrations;
  }

  public void setCrpSubIdosContributions(Set<CrpSubIdosContribution> crpSubIdosContributions) {
    this.crpSubIdosContributions = crpSubIdosContributions;
  }

  public void setCrpTargetUnits(Set<CrpTargetUnit> crpTargetUnits) {
    this.crpTargetUnits = crpTargetUnits;
  }

  public void setCrpUsers(Set<CrpUser> crpUsers) {
    this.crpUsers = crpUsers;
  }

  public void setCustomLevels(List<CustomLevelSelect> customLevels) {
    this.customLevels = customLevels;
  }

  public void setCustomParameters(Set<CustomParameter> customParameters) {
    this.customParameters = customParameters;
  }

  public void setDeliverables(Set<Deliverable> deliverables) {
    this.deliverables = deliverables;
  }

  public void setDeliverablesList(List<Deliverable> deliverablesList) {
    this.deliverablesList = deliverablesList;
  }


  public void setDeliverableTypes(Set<DeliverableType> deliverableTypes) {
    this.deliverableTypes = deliverableTypes;
  }

  public void setFundingSources(Set<FundingSource> fundingSources) {
    this.fundingSources = fundingSources;
  }

  public void setGlobalUnitProjects(Set<GlobalUnitProject> globalUnitProjects) {
    this.globalUnitProjects = globalUnitProjects;
  }

  public void setGlobalUnitType(GlobalUnitType globalUnitType) {
    this.globalUnitType = globalUnitType;
  }

  public void setHasRegions(boolean hasRegions) {
    this.hasRegions = hasRegions;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setLiaisonInstitutions(Set<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setLiasonUsers(Set<LiaisonUser> liasonUsers) {
    this.liasonUsers = liasonUsers;
  }


  public void setLocationCustomElementTypes(List<LocElementType> locationCustomElementTypes) {
    this.locationCustomElementTypes = locationCustomElementTypes;
  }

  public void setLocationElementTypes(List<LocElementType> locationElementTypes) {
    this.locationElementTypes = locationElementTypes;
  }

  public void setLocElements(Set<LocElement> locElements) {
    this.locElements = locElements;
  }

  public void setLocElementTypes(Set<LocElementType> locElementTypes) {
    this.locElementTypes = locElementTypes;
  }

  public void setLogin(boolean login) {
    this.login = login;
  }

  public void setMarlo(boolean marlo) {
    this.marlo = marlo;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setParameters(List<CustomParameter> parameters) {
    this.parameters = parameters;
  }

  public void setPhases(Set<Phase> phases) {
    this.phases = phases;
  }

  public void setProgramManagmenTeam(List<UserRole> programManagmenTeam) {
    this.programManagmenTeam = programManagmenTeam;
  }

  public void setProjects(Set<Project> projects) {
    this.projects = projects;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public void setSiteIntegrations(List<CrpsSiteIntegration> siteIntegrations) {
    this.siteIntegrations = siteIntegrations;
  }

  public void setTargetUnits(List<TargetUnitSelect> targetUnits) {
    this.targetUnits = targetUnits;
  }


}

