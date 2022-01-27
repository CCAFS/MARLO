package org.cgiar.ccafs.marlo.data.model;
// Generated Oct 30, 2017 10:01:51 AM by Hibernate Tools 3.4.0.CR1

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * @author Hermes Jimenez
 */
public class GlobalUnit extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

	private static final long serialVersionUID = -3398924354219701873L;

	@Expose
	private GlobalUnitType globalUnitType;

	@Expose
	private String name;
	@Expose
	private String acronym;

	@Expose
	private boolean marlo;

	@Expose
	private boolean login;
	private String acronymValid;

	@Expose
	private String smoCode;

	@Expose
	private Institution institution;

	private String financialCode;

	private Set<CenterLeader> centerLeaders = new HashSet<CenterLeader>(0);

	private Set<CrpProgram> crpPrograms = new HashSet<CrpProgram>(0);

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

	private Set<CenterProjectFundingSource> centerProjectFundingSources = new HashSet<CenterProjectFundingSource>(0);

	private Set<CrpUser> crpUsers = new HashSet<CrpUser>(0);

	private Set<CrpsSiteIntegration> crpsSitesIntegrations = new HashSet<CrpsSiteIntegration>(0);

	private Set<CrpLocElementType> crpLocElementTypes = new HashSet<CrpLocElementType>(0);

	private Set<CustomParameter> customParameters = new HashSet<CustomParameter>(0);

	private Set<LiaisonUser> liasonUsers = new HashSet<LiaisonUser>(0);

	private Set<Role> roles = new HashSet<Role>(0);

	private Set<GlobalUnitProject> globalUnitProjects = new HashSet<GlobalUnitProject>(0);

	private Set<FundingSource> fundingSources = new HashSet<FundingSource>(0);

	private Set<Deliverable> deliverables = new HashSet<Deliverable>(0);

	// Variables add for Crp Class
	private boolean hasRegions; // Used by CrpAdminManagmentAction

	private List<UserRole> programManagmenTeam; // Used by
												// CrpAdminManagmentAction

	private List<LocElementType> locationCustomElementTypes; // Used by
																// CrpLocationsAction

	private List<LocElementType> locationElementTypes; // Used by
														// CrpLocationsAction

	private List<CustomLevelSelect> customLevels; // Used by CrpLocationsAction

	private List<CrpPpaPartner> crpInstitutionsPartners; // Used by
															// CrpPpaPartnersAction

	private List<CrpsSiteIntegration> siteIntegrations; // Used by
														// CrpSiteIntegrationAction

	private List<TargetUnitSelect> targetUnits; // Used by CrpTargetUnitsAction

	private List<Deliverable> deliverablesList; // Used by PublicationListAction

	private List<CustomParameter> parameters; // Used by CrpParametersAction

	public GlobalUnit() {
		// Default Constructor
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		GlobalUnit other = (GlobalUnit) obj;
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

	public String getAcronymValid() {
		return acronymValid;
	}

	public Set<CenterArea> getCenterAreas() {
		return centerAreas;
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

	public String getComposedName() {
		if (this.getAcronym() != null) {
			if (this.getAcronym().length() != 0) {
				try {
					return this.getAcronym() + " - " + this.getName();
				} catch (Exception e) {
					return this.getName();
				}
			}
			return this.getName();
		}
		return this.getName();
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

	public String getFinancialCode() {
		return financialCode;
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

	public Institution getInstitution() {
		return institution;
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

	@Override
	public String getLogDeatil() {
		StringBuilder sb = new StringBuilder();
		sb.append("Id : ").append(this.getId());
		return sb.toString();
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

	public Set<Role> getRoles() {
		return roles;
	}

	public List<CrpsSiteIntegration> getSiteIntegrations() {
		return siteIntegrations;
	}

	public String getSmoCode() {
		return smoCode;
	}

	public List<TargetUnitSelect> getTargetUnits() {
		return targetUnits;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.getId() == null ? 0 : this.getId().hashCode());
		return result;
	}

	public boolean isCenterType() {
		return this.globalUnitType.getId().intValue() == 4;
	}

	public boolean isCrpType() {
		// CRP or Platform
		return this.globalUnitType.getId().intValue() == 1 || this.globalUnitType.getId().intValue() == 3;
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

	public boolean isNewCenterType() {
		return this.globalUnitType.getId().intValue() == 5;
	}

	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	public void setAcronymValid(String acronymValid) {
		this.acronymValid = acronymValid;
	}

	public void setCenterAreas(Set<CenterArea> centerAreas) {
		this.centerAreas = centerAreas;
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

	public void setFinancialCode(String financialCode) {
		this.financialCode = financialCode;
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

	public void setInstitution(Institution institution) {
		this.institution = institution;
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

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public void setSiteIntegrations(List<CrpsSiteIntegration> siteIntegrations) {
		this.siteIntegrations = siteIntegrations;
	}

	public void setSmoCode(String smoCode) {
		this.smoCode = smoCode;
	}

	public void setTargetUnits(List<TargetUnitSelect> targetUnits) {
		this.targetUnits = targetUnits;
	}

	@Override
	public String toString() {
		return "GlobalUnit [id=" + this.getId() + ", globalUnitType=" + globalUnitType + ", name=" + name + ", acronym="
				+ acronym + ", active=" + this.isActive() + ", marlo=" + marlo + ", login=" + login + "]";
	}

}
