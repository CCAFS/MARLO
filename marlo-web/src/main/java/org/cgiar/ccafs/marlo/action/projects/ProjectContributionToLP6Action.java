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

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterOfActivityManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.Lp6ContributionGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetsCluserActvityManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectCenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectClusterActivityManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.impl.CenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Lp6ContributionGeographicScope;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6ContributionDeliverable;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectDescriptionValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProjectContributionToLP6Action extends BaseAction {


  private static final long serialVersionUID = -793652591843623397L;


  private static final Logger LOG = LoggerFactory.getLogger(ProjectContributionToLP6Action.class);

  // Managers
  private ProjectManager projectManager;
  private String narrativeLP6Contribution;
  private Boolean isWorkingAcrossFlagships;
  private String workingAcrossFlagshipsNarrative;
  private Boolean isUndertakingEffortsLeading;
  private String undertakingEffortsLeadingNarrative;
  private Boolean isProvidingPathways;
  private String providingPathwaysNarrative;
  private String top3Partnerships;
  private Boolean isUndertakingEffortsCSA;
  private String undertakingEffortsCSANarrative;
  private Boolean isInitiativeRelated;
  private String initiativeRelatedNarrative;
  private List<RepIndGeographicScope> repIndGeographicScopes;
  private ProjectLp6ContributionManager projectLp6ContributionManager;
  private Lp6ContributionGeographicScopeManager lp6ContributionGeographicScopeManager;
  private ProjectLp6Contribution projectLp6Contribution;
  private Lp6ContributionGeographicScope lp6ContributionGeographicScope;
  private ProjectLp6ContributionDeliverableManager projectLp6ContributionDeliverableManager;
  private ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable;
  private GlobalUnitManager crpManager;
  private CrpProgramManager programManager;
  private AuditLogManager auditLogManager;
  private String transaction;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private LocElementManager locElementManager;
  private DeliverableManager deliverableManager;
  private DeliverableInfoManager deliverableInfoManager;
  private Deliverable deliverable;


  // Front-end
  private long projectID;
  private GlobalUnit loggedCrp;
  private Project project;
  private List<CrpProgram> programFlagships;
  private List<CrpProgram> regionFlagships;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<ProjectLp6ContributionDeliverable> selectedDeliverables;
  private List<Lp6ContributionGeographicScope> contributionSelectedCountries;
  private List<LocElement> repIndRegions;
  private List<LocElement> countries;
  private List<String> countriesIds;
  private List<Deliverable> deliverables;


  private ProjectDescriptionValidator validator;

  @Inject
  public ProjectContributionToLP6Action(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    CrpProgramManager programManager, Lp6ContributionGeographicScopeManager lp6ContributionGeographicScopeManager,
    LiaisonUserManager liaisonUserManager, LiaisonInstitutionManager liaisonInstitutionManager, UserManager userManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager, SectionStatusManager sectionStatusManager,
    ProjectFocusManager projectFocusManager, AuditLogManager auditLogManager, ProjectDescriptionValidator validator,
    ProjectClusterActivityManager projectClusterActivityManager,
    CrpClusterOfActivityManager crpClusterOfActivityManager, LocElementTypeManager locationManager,
    ProjectScopeManager projectLocationManager, HistoryComparator historyComparator,
    ProjectInfoManager projectInfoManagerManager, ProjectBudgetsCluserActvityManager projectBudgetsCluserActvityManager,
    GlobalUnitProjectManager globalUnitProjectManager, CenterOutcomeManager centerOutcomeManager,
    ProjectCenterOutcomeManager projectCenterOutcomeManager,
    ProjectLp6ContributionDeliverableManager projectLp6ContributionDeliverableManager,
    ProjectLp6ContributionManager projectLp6ContributionManager, LocElementManager locElementManager,
    DeliverableManager deliverableManager, DeliverableInfoManager deliverableInfoManager) {
    super(config);
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.projectManager = projectManager;
    this.projectLp6ContributionManager = projectLp6ContributionManager;
    this.programManager = programManager;
    this.crpManager = crpManager;
    this.projectManager = projectManager;
    this.validator = validator;
    this.auditLogManager = auditLogManager;
    this.locElementManager = locElementManager;
    this.deliverableManager = deliverableManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.projectLp6ContributionDeliverableManager = projectLp6ContributionDeliverableManager;
    this.lp6ContributionGeographicScopeManager = lp6ContributionGeographicScopeManager;
  }


  private String getAnualReportRelativePath() {
    return config.getProjectsBaseFolder(loggedCrp.getAcronym()) + File.separator + project.getId() + File.separator
      + config.getAnualReportFolder();
  }


  public String getAnualReportURL() {
    return config.getDownloadURL() + "/" + this.getAnualReportRelativePath().replace('\\', '/');
  }


  private void getAutoSaveFilePath() {
    // return path
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    // return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<Lp6ContributionGeographicScope> getContributionSelectedCountries() {
    return contributionSelectedCountries;
  }


  public List<LocElement> getCountries() {
    return countries;
  }

  public List<String> getCountriesIds() {
    return countriesIds;
  }


  public List<Deliverable> getDeliverables() {
    return deliverables;
  }

  public String getInitiativeRelatedNarrative() {
    return initiativeRelatedNarrative;
  }

  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public Lp6ContributionGeographicScope getLp6ContributionGeographicScope() {
    return lp6ContributionGeographicScope;
  }

  public String getNarrativeLP6Contribution() {
    return narrativeLP6Contribution;
  }

  public List<CrpProgram> getProgramFlagships() {
    return programFlagships;
  }

  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }

  public ProjectLp6Contribution getProjectLp6Contribution() {
    return projectLp6Contribution;
  }

  public String getProvidingPathwaysNarrative() {
    return providingPathwaysNarrative;
  }

  public List<CrpProgram> getRegionFlagships() {
    return regionFlagships;
  }

  public List<RepIndGeographicScope> getRepIndGeographicScopes() {
    return repIndGeographicScopes;
  }

  public List<LocElement> getRepIndRegions() {
    return repIndRegions;
  }

  public List<ProjectLp6ContributionDeliverable> getSelectedDeliverables() {
    return selectedDeliverables;
  }


  public String getTop3Partnerships() {
    return top3Partnerships;
  }

  public String getTransaction() {
    return transaction;
  }

  public String getUndertakingEffortsCSANarrative() {
    return undertakingEffortsCSANarrative;
  }

  public String getUndertakingEffortsLeadingNarrative() {
    return undertakingEffortsLeadingNarrative;
  }

  public String getWorkingAcrossFlagshipsNarrative() {
    return workingAcrossFlagshipsNarrative;
  }

  private String getWorkplanRelativePath() {
    return config.getProjectsBaseFolder(loggedCrp.getAcronym()) + File.separator + project.getId() + File.separator
      + config.getProjectWorkplanFolder() + File.separator;
  }

  public String getWorkplanURL() {
    return config.getDownloadURL() + "/" + this.getWorkplanRelativePath().replace('\\', '/');
  }

  public Boolean isInitiativeRelated() {
    return isInitiativeRelated;
  }

  public Boolean isProvidingPathways() {
    return isProvidingPathways;
  }

  public Boolean isUndertakingEffortsCSA() {
    return isUndertakingEffortsCSA;
  }

  public Boolean isUndertakingEffortsLeading() {
    return isUndertakingEffortsLeading;
  }


  public Boolean isWorkingAcrossFlagships() {
    return isWorkingAcrossFlagships;
  }

  @Override
  public void prepare() throws Exception {

    contributionSelectedCountries = new ArrayList<>();
    // TODO countriesIds = new ArrayList<String>();
    selectedDeliverables = new ArrayList<ProjectLp6ContributionDeliverable>();


    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
      this.setProject(projectManager.getProjectById(projectID));
    } catch (Exception e) {
      LOG.error("unable to parse projectID", e);
    }

    /*
     * Geographic scope for lp6 contribution
     */
    this.setRepIndGeographicScopes(repIndGeographicScopeManager.findAll().stream()
      .sorted((g1, g2) -> g1.getName().compareTo(g2.getName())).collect(Collectors.toList()));
    repIndRegions = locElementManager.findAll().stream()
      .filter(c -> c.getLocElementType().getId().intValue() == 1 && c.isActive() && c.getIsoNumeric() != null)
      .collect(Collectors.toList());
    this.setCountries(locElementManager.findAll().stream()
      .filter(c -> c.isActive() && c.getLocElementType().getId() == 2).collect(Collectors.toList()));


    /*
     * List of deliverables for the actual project and phase
     */
    deliverables = new ArrayList<>();
    if (project.getDeliverables() != null) {
      List<DeliverableInfo> infos =
        deliverableInfoManager.getDeliverablesInfoByProjectAndPhase(this.getActualPhase(), project);
      deliverables = new ArrayList<>();
      if (infos != null && !infos.isEmpty()) {
        for (DeliverableInfo deliverableInfo : infos) {
          Deliverable deliverable = deliverableInfo.getDeliverable();
          deliverable.setDeliverableInfo(deliverableInfo);
          deliverables.add(deliverable);
        }
      }
    }

    /*
     * Get the actual projectLp6Contribution
     */
    if (this.getActualPhase() != null && projectID != 0) {
      try {
        this.setProjectLp6Contribution(projectLp6ContributionManager.findAll().stream().filter(c -> c.isActive()
          && c.getProject().getId() == projectID && c.getPhase().getId() == this.getActualPhase().getId())
          .collect(Collectors.toList()).get(0));
      } catch (Exception e) {
        LOG.error("error setting projectLp6 contribution " + e);

      }
    }

    // if (projectLp6Contribution != null) {
    // narrativeLP6Contribution = projectLp6Contribution.getNarrative();
    // workingAcrossFlagshipsNarrative = projectLp6Contribution.getWorkingAcrossFlagshipsNarrative();
    // isWorkingAcrossFlagships = projectLp6Contribution.isWorkingAcrossFlagships();
    // isUndertakingEffortsCSA = projectLp6Contribution.isUndertakingEffortsCsa();
    // undertakingEffortsLeadingNarrative = projectLp6Contribution.getUndertakingEffortsLeadingNarrative();
    // isUndertakingEffortsLeading = projectLp6Contribution.isUndertakingEffortsLeading();
    // top3Partnerships = projectLp6Contribution.getTopThreePartnershipsNarrative();
    // isInitiativeRelated = projectLp6Contribution.isInitiativeRelated();
    // initiativeRelatedNarrative = projectLp6Contribution.getInitiativeRelatedNarrative();
    // providingPathwaysNarrative = projectLp6Contribution.getProvidingPathwaysNarrative();
    // isProvidingPathways = projectLp6Contribution.isProvidingPathways();
    // undertakingEffortsCSANarrative = projectLp6Contribution.getUndertakingEffortsCsaNarrative();
    // }


    if (projectLp6Contribution != null) {

      // Get selected deliverables
      if (projectLp6Contribution.getProjectLp6ContributionDeliverable() != null) {
        projectLp6Contribution
          .setDeliverables(new ArrayList<>(projectLp6Contribution.getProjectLp6ContributionDeliverable().stream()
            .filter(o -> o.isActive() && o.getPhase().getId() == this.getActualPhase().getId())
            .collect(Collectors.toList())));
      }

    }

    // get selected countries
    // Policy Countries List
    if (projectLp6Contribution.getLp6ContributionGeographicScopes() == null) {
      projectLp6Contribution.setCountries(new ArrayList<>());
    } else {
      List<Lp6ContributionGeographicScope> countries = lp6ContributionGeographicScopeManager
        .getLp6ContributionGeographicScopebyPhase(projectLp6Contribution.getId(), this.getActualPhase().getId());
      projectLp6Contribution.setCountries(countries);
    }


    try {
      if (!this.isDraft()) {
        if (countries != null) {
          for (LocElement country : countries) {
            projectLp6Contribution.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
          }
        }
      }
    } catch (Exception e) {
      LOG.error("error getting contriesIds " + e);
    }
  }

  @Override
  public String save() {

    if (this.hasPermission("canEdit")) {

      try {


        if (projectLp6Contribution == null) {
          projectLp6Contribution = new ProjectLp6Contribution();
        }

        // projectLp6Contribution.setNarrative(projectLp6Contribution.getNarrative());
        // projectLp6Contribution.setWorkingAcrossFlagshipsNarrative(workingAcrossFlagshipsNarrative);
        // projectLp6Contribution.setWorkingAcrossFlagships(isWorkingAcrossFlagships);
        // projectLp6Contribution.setUndertakingEffortsCsa(isUndertakingEffortsCSA);
        // projectLp6Contribution.setUndertakingEffortsLeadingNarrative(undertakingEffortsLeadingNarrative);
        // projectLp6Contribution.setUndertakingEffortsLeading(isUndertakingEffortsLeading);
        // projectLp6Contribution.setTopThreePartnershipsNarrative(top3Partnerships);
        // projectLp6Contribution.setInitiativeRelated(isInitiativeRelated);
        // projectLp6Contribution.setInitiativeRelatedNarrative(initiativeRelatedNarrative);
        // projectLp6Contribution.setProvidingPathways(isProvidingPathways);
        // projectLp6Contribution.setProvidingPathwaysNarrative(providingPathwaysNarrative);
        // projectLp6Contribution.setUndertakingEffortsCsaNarrative(undertakingEffortsCSANarrative);

        projectLp6ContributionManager.saveProjectLp6Contribution(projectLp6Contribution);

        // Save the Countries List (ProjectExpectedStudyCountry)
        if (countriesIds != null && !countriesIds.isEmpty()) {

          List<Lp6ContributionGeographicScope> countriesSave = new ArrayList<>();

          for (String countryIds : projectLp6Contribution.getCountriesIds()) {
            Lp6ContributionGeographicScope countryInn = new Lp6ContributionGeographicScope();
            countryInn.setLocElement(locElementManager.getLocElementByISOCode(countryIds));
            countryInn.setPhase(this.getActualPhase());
            countryInn.setProjectLp6Contribution(projectLp6Contribution);
            countriesSave.add(countryInn);
            if (contributionSelectedCountries != null) {
              if (!contributionSelectedCountries.contains(countryInn)) {
                lp6ContributionGeographicScopeManager.saveLp6ContributionGeographicScope(countryInn);
              }
            } else {
              lp6ContributionGeographicScopeManager.saveLp6ContributionGeographicScope(countryInn);
            }

          }

          for (Lp6ContributionGeographicScope lp6ContributionGeographicScope : contributionSelectedCountries) {
            if (!countriesSave.contains(lp6ContributionGeographicScope)) {
              lp6ContributionGeographicScopeManager
                .deleteLp6ContributionGeographicScope(lp6ContributionGeographicScope.getId());
            }
          }


        }

        // save project Lp6 Contribution deliverable
        // if (selectedDeliverables != null && !selectedDeliverables.isEmpty()) {
        // for (ProjectLp6ContributionDeliverable selectedDeliverable : selectedDeliverables) {
        // projectLp6ContributionDeliverable = new ProjectLp6ContributionDeliverable();
        // projectLp6ContributionDeliverable.setPhase(this.getActualPhase());
        // projectLp6ContributionDeliverable.setProjectLp6Contribution(projectLp6Contribution);
        // projectLp6ContributionDeliverable.setDeliverable(selectedDeliverable.getDeliverable());
        // projectLp6ContributionDeliverableManager
        // .saveProjectLp6ContributionDeliverable(projectLp6ContributionDeliverable);
        // }
        // }

      } catch (Exception e) {
        LOG.error("saving error", e);
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }


      /*
       * Path path = this.getAutoSaveFilePath();
       * if (path.toFile().exists()) {
       * path.toFile().delete();
       * }
       */
      /*
       * Collection<String> messages = this.getActionMessages();
       * if (!this.getInvalidFields().isEmpty()) {
       * this.setActionMessages(null);
       * List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
       * for (String key : keys) {
       * this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
       * }
       * } else {
       * this.addActionMessage("message:" + this.getText("saving.saved"));
       * }
       */
      return SUCCESS;

    } else {
      // no permissions to edit
      return NOT_AUTHORIZED;
    }

  }


  public void setContributionSelectedCountries(List<Lp6ContributionGeographicScope> contributionSelectedCountries) {
    this.contributionSelectedCountries = contributionSelectedCountries;
  }


  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
  }

  public void setCountriesIds(List<String> countriesIds) {
    this.countriesIds = countriesIds;
  }


  public void setDeliverables(List<Deliverable> deliverables) {
    this.deliverables = deliverables;
  }

  public void setInitiativeRelated(Boolean isInitiativeRelated) {
    this.isInitiativeRelated = isInitiativeRelated;
  }


  public void setInitiativeRelatedNarrative(String initiativeRelatedNarrative) {
    this.initiativeRelatedNarrative = initiativeRelatedNarrative;
  }

  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setLp6ContributionGeographicScope(Lp6ContributionGeographicScope lp6ContributionGeographicScope) {
    this.lp6ContributionGeographicScope = lp6ContributionGeographicScope;
  }


  public void setNarrativeLP6Contribution(String narrativeLP6Contribution) {
    this.narrativeLP6Contribution = narrativeLP6Contribution;
  }


  public void setProgramFlagships(List<CrpProgram> programFlagships) {
    this.programFlagships = programFlagships;
  }

  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectLp6Contribution(ProjectLp6Contribution projectLp6Contribution) {
    this.projectLp6Contribution = projectLp6Contribution;
  }

  public void setProvidingPathways(Boolean isProvidingPathways) {
    this.isProvidingPathways = isProvidingPathways;
  }


  public void setProvidingPathwaysNarrative(String providingPathwaysNarrative) {
    this.providingPathwaysNarrative = providingPathwaysNarrative;
  }


  public void setRegionFlagships(List<CrpProgram> regionFlagships) {
    this.regionFlagships = regionFlagships;
  }


  public void setRepIndGeographicScopes(List<RepIndGeographicScope> repIndGeographicScopes) {
    this.repIndGeographicScopes = repIndGeographicScopes;
  }


  public void setRepIndRegions(List<LocElement> repIndRegions) {
    this.repIndRegions = repIndRegions;
  }

  public void setSelectedDeliverables(List<ProjectLp6ContributionDeliverable> selectedDeliverables) {
    this.selectedDeliverables = selectedDeliverables;
  }

  public void setTop3Partnerships(String top3Partnerships) {
    this.top3Partnerships = top3Partnerships;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  public void setUndertakingEffortsCSA(Boolean isUndertakingEffortsCSA) {
    this.isUndertakingEffortsCSA = isUndertakingEffortsCSA;
  }

  public void setUndertakingEffortsCSANarrative(String undertakingEffortsCSANarrative) {
    this.undertakingEffortsCSANarrative = undertakingEffortsCSANarrative;
  }


  public void setUndertakingEffortsLeading(Boolean isUndertakingEffortsLeading) {
    this.isUndertakingEffortsLeading = isUndertakingEffortsLeading;
  }

  public void setUndertakingEffortsLeadingNarrative(String undertakingEffortsLeadingNarrative) {
    this.undertakingEffortsLeadingNarrative = undertakingEffortsLeadingNarrative;
  }

  public void setWorkingAcrossFlagships(Boolean isWorkingAcrossFlagships) {
    this.isWorkingAcrossFlagships = isWorkingAcrossFlagships;
  }

  public void setWorkingAcrossFlagshipsNarrative(String workingAcrossFlagshipsNarrative) {
    this.workingAcrossFlagshipsNarrative = workingAcrossFlagshipsNarrative;
  }


  @Override
  public void validate() {
    // if is saving call the validator to check for the missing fields
    if (save) {
      validator.validate(this, project, true);
    }
  }

}