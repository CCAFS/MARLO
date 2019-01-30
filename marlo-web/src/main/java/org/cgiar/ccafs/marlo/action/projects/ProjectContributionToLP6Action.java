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
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectLP6Validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProjectContributionToLP6Action extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;
  private static final Logger LOG = LoggerFactory.getLogger(ProjectContributionToLP6Action.class);

  // Managers
  private ProjectManager projectManager;
  private List<RepIndGeographicScope> repIndGeographicScopes;
  private ProjectLp6ContributionManager projectLp6ContributionManager;
  private Lp6ContributionGeographicScopeManager lp6ContributionGeographicScopeManager;
  private ProjectLp6Contribution projectLp6ContributionDB;
  private Lp6ContributionGeographicScope lp6ContributionGeographicScope;
  private ProjectLp6ContributionDeliverableManager projectLp6ContributionDeliverableManager;
  private GlobalUnitManager crpManager;
  private AuditLogManager auditLogManager;
  private String transaction;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private LocElementManager locElementManager;
  private DeliverableManager deliverableManager;
  private DeliverableInfoManager deliverableInfoManager;

  // Front-end
  private long projectID;
  private GlobalUnit loggedCrp;
  private Project project;
  private List<CrpProgram> programFlagships;
  private List<CrpProgram> regionFlagships;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<LocElement> repIndRegions;
  private List<LocElement> countries;
  private List<Deliverable> deliverables;
  private List<RepIndGeographicScope> geographicScopes;
  private List<LocElement> regions;
  private ProjectLP6Validator validator;

  @Inject
  public ProjectContributionToLP6Action(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    Lp6ContributionGeographicScopeManager lp6ContributionGeographicScopeManager, LiaisonUserManager liaisonUserManager,
    LiaisonInstitutionManager liaisonInstitutionManager, UserManager userManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager, SectionStatusManager sectionStatusManager,
    ProjectFocusManager projectFocusManager, AuditLogManager auditLogManager, ProjectLP6Validator validator,
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


  public void cleanNarrativeFields() {
    if (project.getProjectLp6Contribution().getInitiativeRelated() != null
      && project.getProjectLp6Contribution().isInitiativeRelated() == false) {
      project.getProjectLp6Contribution().setInitiativeRelatedNarrative("");
    }

    if (project.getProjectLp6Contribution().isProvidingPathways() != null
      && project.getProjectLp6Contribution().isProvidingPathways() == false) {
      project.getProjectLp6Contribution().setProvidingPathwaysNarrative("");
    }

    if (project.getProjectLp6Contribution().isUndertakingEffortsCsa() != null
      && project.getProjectLp6Contribution().isUndertakingEffortsCsa() == false) {
      project.getProjectLp6Contribution().setUndertakingEffortsCsaNarrative("");
    }

    if (project.getProjectLp6Contribution().isUndertakingEffortsLeading() != null
      && project.getProjectLp6Contribution().isUndertakingEffortsLeading() == false) {
      project.getProjectLp6Contribution().setUndertakingEffortsLeadingNarrative("");
    }

    if (project.getProjectLp6Contribution().isWorkingAcrossFlagships() != null
      && project.getProjectLp6Contribution().isWorkingAcrossFlagships() == false) {
      project.getProjectLp6Contribution().setWorkingAcrossFlagshipsNarrative("");
    }

  }

  /**
   * Delete all LocElements Records when Geographic Scope is Global or NULL
   */
  public void deleteLocElements() {

    List<Lp6ContributionGeographicScope> lp6ContributionGeographicScopes =
      new ArrayList<>(projectLp6ContributionDB.getLp6ContributionGeographicScopes().stream()
        .filter(nu -> nu.isActive() && nu.getPhase().getId() == this.getActualPhase().getId())
        .collect(Collectors.toList()));

    if (lp6ContributionGeographicScopes != null && !lp6ContributionGeographicScopes.isEmpty()) {
      for (Lp6ContributionGeographicScope lp6ContributionGeographicScope : lp6ContributionGeographicScopes) {
        lp6ContributionGeographicScopeManager
          .deleteLp6ContributionGeographicScope(lp6ContributionGeographicScope.getId());
      }
    }
  }

  private String getAnualReportRelativePath() {
    return config.getProjectsBaseFolder(loggedCrp.getAcronym()) + File.separator + project.getId() + File.separator
      + config.getAnualReportFolder();
  }


  public String getAnualReportURL() {
    return config.getDownloadURL() + "/" + this.getAnualReportRelativePath().replace('\\', '/');
  }

  private Path getAutoSaveFilePath() {
    // get the class simple name
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatenate name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public List<LocElement> getCountries() {
    return countries;
  }

  public List<Deliverable> getDeliverables() {
    return deliverables;
  }

  public List<RepIndGeographicScope> getGeographicScopes() {
    return geographicScopes;
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


  public List<CrpProgram> getProgramFlagships() {
    return programFlagships;
  }


  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }

  public List<CrpProgram> getRegionFlagships() {
    return regionFlagships;
  }

  public List<LocElement> getRegions() {
    return regions;
  }

  public List<RepIndGeographicScope> getRepIndGeographicScopes() {
    return repIndGeographicScopes;
  }

  public List<LocElement> getRepIndRegions() {
    return repIndRegions;
  }

  public String getTransaction() {
    return transaction;
  }

  private String getWorkplanRelativePath() {
    return config.getProjectsBaseFolder(loggedCrp.getAcronym()) + File.separator + project.getId() + File.separator
      + config.getProjectWorkplanFolder() + File.separator;
  }


  public String getWorkplanURL() {
    return config.getDownloadURL() + "/" + this.getWorkplanRelativePath().replace('\\', '/');
  }

  @Override
  public void prepare() throws Exception {

    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    } catch (Exception e) {
      LOG.error("unable to parse projectID", e);
    }
    project = projectManager.getProjectById(projectID);

    // We check that you have a TRANSACTION_ID to know if it is history version
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {
      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);

      if (history != null) {
        project = history;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }
    } else {
      // get project info for DB
      project = projectManager.getProjectById(projectID);
    }

    if (project != null) {
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        // Autosave File
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();

        AutoSaveReader autoSaveReader = new AutoSaveReader();
        project = (Project) autoSaveReader.readFromJson(jReader);
        Project projectDb = projectManager.getProjectById(project.getId());
        project.getProjectInfo().setPhase(projectDb.getProjecInfoPhase(this.getActualPhase()).getPhase());
        project.getProjectInfo()
          .setProjectEditLeader(projectDb.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader());
        project.getProjectInfo()
          .setAdministrative(projectDb.getProjecInfoPhase(this.getActualPhase()).getAdministrative());

        if (project.getProjectLp6Contribution() != null) {
          // Project Lp6 contribution Scope List AutoSave
          if (project.getProjectLp6Contribution().getGeographicScope() != null) {
            if (project.getProjectLp6Contribution().getGeographicScope().getId() != null
              && project.getProjectLp6Contribution().getGeographicScope().getId() != -1) {
              // If the Geographic Scope is not Global
              if (!project.getProjectLp6Contribution().getGeographicScope().getId()
                .equals(this.getReportingIndGeographicScopeGlobal())) {
                if (project.getProjectLp6Contribution().getGeographicScope().getId()
                  .equals(this.getReportingIndGeographicScopeRegional())) {
                  // Load Regions
                  if (project.getProjectLp6ContributionRegions() != null) {
                    for (Lp6ContributionGeographicScope lp6ContributionGeographicScope : project
                      .getProjectLp6ContributionRegions()) {
                      lp6ContributionGeographicScope.setLocElement(
                        locElementManager.getLocElementById(lp6ContributionGeographicScope.getLocElement().getId()));
                    }
                    if (project.getProjectLp6Contribution().getRegions() == null
                      || project.getProjectLp6Contribution().getRegions().isEmpty()) {
                      project.getProjectLp6Contribution().setRegions(new ArrayList<>());
                    }
                    project.getProjectLp6Contribution().getRegions().addAll(project.getProjectLp6ContributionRegions());
                  }
                } else {
                  // Load Countries
                  if (project.getProjectLp6ContributionCountriesIdsText() != null) {
                    String[] countriesText =
                      project.getProjectLp6ContributionCountriesIdsText().replace("[", "").replace("]", "").split(",");
                    List<String> countries = new ArrayList<>();
                    for (String value : Arrays.asList(countriesText)) {
                      countries.add(value.trim());
                    }
                    project.getProjectLp6Contribution().getCountriesIds().addAll(countries);
                  }
                }
              }
            }
          }

          if (project.getProjectLp6Contribution().getDeliverables() == null) {
            project.getProjectLp6Contribution().setDeliverables(new ArrayList<>());
          }
          // Load Deliverable list from Autosave to LP6Deliverables
          if (project.getProjectLp6ContributionDeliverables() != null
            && !project.getProjectLp6ContributionDeliverables().isEmpty()) {
            for (ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable : project
              .getProjectLp6ContributionDeliverables()) {
              if (projectLp6ContributionDeliverable.getDeliverable() != null
                && projectLp6ContributionDeliverable.getDeliverable().getId() != null) {
                Deliverable deliverable =
                  deliverableManager.getDeliverableById(projectLp6ContributionDeliverable.getDeliverable().getId());
                ProjectLp6ContributionDeliverable autoSaveProjectLp6ContributionDeliverable =
                  new ProjectLp6ContributionDeliverable();
                autoSaveProjectLp6ContributionDeliverable.setDeliverable(deliverable);
                autoSaveProjectLp6ContributionDeliverable.setModifiedBy(this.getCurrentUser());
                autoSaveProjectLp6ContributionDeliverable.setPhase(this.getActualPhase());
                autoSaveProjectLp6ContributionDeliverable
                  .setProjectLp6Contribution(project.getProjectLp6Contribution());
                project.getProjectLp6Contribution().getDeliverables().add(autoSaveProjectLp6ContributionDeliverable);
              }

            }
          }
        }
        this.setDraft(true);
      } else {
        this.setDraft(false);
        /*
         * Get the actual projectLp6Contribution
         */
        project.setProjectLp6Contribution(projectLp6ContributionManager.findAll().stream().filter(c -> c.isActive()
          && c.getProject().getId() == projectID && c.getPhase().getId() == this.getActualPhase().getId())
          .collect(Collectors.toList()).get(0));

        if (project.getProjectLp6Contribution() != null) {
          // Get selected deliverables
          if (project.getProjectLp6Contribution().getDeliverables() == null) {
            project.getProjectLp6Contribution().setDeliverables(new ArrayList<>());
          }
          List<ProjectLp6ContributionDeliverable> deliverables =
            projectLp6ContributionDeliverableManager.getProjectLp6ContributionDeliverablebyPhase(
              project.getProjectLp6Contribution().getId(), this.getActualPhase().getId());
          project.getProjectLp6Contribution().getDeliverables().addAll(deliverables);

          // Setup Geographic Scope
          if (project.getProjectLp6Contribution().getGeographicScope() != null) {
            // If the Geographic Scope is not Global
            if (!project.getProjectLp6Contribution().getGeographicScope().getId()
              .equals(this.getReportingIndGeographicScopeGlobal())) {

              // Project Lp6 contribution Countries List
              if (project.getProjectLp6Contribution().getLp6ContributionGeographicScopes() == null) {
                project.getProjectLp6Contribution().setCountries(new ArrayList<>());
                project.getProjectLp6Contribution().setRegions(new ArrayList<>());
              } else {
                List<Lp6ContributionGeographicScope> geographics =
                  lp6ContributionGeographicScopeManager.getLp6ContributionGeographicScopebyPhase(
                    project.getProjectLp6Contribution().getId(), this.getActualPhase().getId());
                if (project.getProjectLp6Contribution().getGeographicScope().getId()
                  .equals(this.getReportingIndGeographicScopeRegional())) {
                  // Load Regions
                  project.getProjectLp6Contribution().setRegions(geographics.stream()
                    .filter(sc -> sc.getLocElement().getLocElementType().getId() == 1).collect(Collectors.toList()));
                } else {
                  // Load Countries
                  project.getProjectLp6Contribution().setCountries(geographics.stream()
                    .filter(sc -> sc.getLocElement().getLocElementType().getId() == 2).collect(Collectors.toList()));
                }
              }
            }
          }
        }
      }
    }

    // Getting The lists

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

    // Geographic scope for lp6 contribution
    repIndRegions = locElementManager.findAll().stream()
      .filter(c -> c.getLocElementType().getId().intValue() == 1 && c.isActive() && c.getIsoNumeric() != null)
      .collect(Collectors.toList());

    // Geographic Scope List
    geographicScopes = repIndGeographicScopeManager.findAll();

    // Regions for Geographic Scope Regional Selection
    regions = locElementManager.findAll().stream()
      .filter(c -> c.getLocElementType().getId().intValue() == 1 && c.isActive() && c.getIsoNumeric() != null)
      .collect(Collectors.toList());

    // Countries for Geographic Scope National - Multinational Selection
    countries = locElementManager.findAll().stream()
      .filter(c -> c.getLocElementType().getId().intValue() == 2 && c.isActive()).collect(Collectors.toList());

    projectLp6ContributionDB = projectLp6ContributionManager.findAll().stream().filter(
      c -> c.isActive() && c.getProject().getId() == projectID && c.getPhase().getId() == this.getActualPhase().getId())
      .collect(Collectors.toList()).get(0);

    if (!this.isDraft()) {
      if (project.getProjectLp6Contribution().getCountries() != null) {
        for (Lp6ContributionGeographicScope country : project.getProjectLp6Contribution().getCountries()) {
          project.getProjectLp6Contribution().getCountriesIds().add(country.getLocElement().getIsoAlpha2());
        }
      }
    }

    if (this.isHttpPost()) {
      if (project.getProjectLp6Contribution().getDeliverables() != null) {
        project.getProjectLp6Contribution().getDeliverables().clear();
      }

      if (project.getProjectLp6Contribution().getRegions() != null) {
        project.getProjectLp6Contribution().getRegions().clear();
      }

      if (project.getProjectLp6Contribution().getCountries() != null) {
        project.getProjectLp6Contribution().getCountries().clear();
      }

      project.getProjectLp6Contribution().setGeographicScope(null);
    }

  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {
      project.getProjectLp6Contribution().setProject(project);
      project.getProjectLp6Contribution().setPhase(this.getActualPhase());
      // Clean the actual project Lp6 contribution
      this.cleanNarrativeFields();

      // Save project Lp6 relations
      this.saveProjectDeliverables();
      this.saveGeographicScopes();

      projectLp6ContributionManager.saveProjectLp6Contribution(project.getProjectLp6Contribution());
      project.getProjectLp6Contributions().add(project.getProjectLp6Contribution());
      Path path = this.getAutoSaveFilePath();

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_CONTRIBUTION_LP6_RELATION);
      /**
       * The following is required because we need to update something on the @Project if we want a row
       * created in the auditlog table.
       */

      this.setModificationJustification(project);
      // projectManager.saveProject(project, this.getActionName(), relationsName, this.getActualPhase());

      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }

        } else {
          this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        return SUCCESS;
      } else {
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }
    } else {


      return NOT_AUTHORIZED;
    }

  }

  /**
   * Save the Countries List
   */
  private void saveCountries() {

    List<Lp6ContributionGeographicScope> lp6ContributionGeographicScopes =
      new ArrayList<>(projectLp6ContributionDB.getLp6ContributionGeographicScopes().stream()
        .filter(lg -> lg.isActive() && lg.getPhase().getId() == this.getActualPhase().getId())
        .collect(Collectors.toList()));

    if (project.getProjectLp6Contribution().getCountriesIds() != null
      || !project.getProjectLp6Contribution().getCountriesIds().isEmpty()) {

      List<Lp6ContributionGeographicScope> countriesSave = new ArrayList<>();
      for (String countryIds : project.getProjectLp6Contribution().getCountriesIds()) {
        Lp6ContributionGeographicScope countryInn = new Lp6ContributionGeographicScope();
        countryInn.setLocElement(locElementManager.getLocElementByISOCode(countryIds));
        countryInn.setProjectLp6Contribution(project.getProjectLp6Contribution());
        countryInn.setPhase(this.getActualPhase());
        countriesSave.add(countryInn);
        if (!lp6ContributionGeographicScopes.contains(countryInn)) {
          lp6ContributionGeographicScopeManager.saveLp6ContributionGeographicScope(countryInn);
          project.getProjectLp6Contribution().getLp6ContributionGeographicScopes().add(countryInn);
        }
      }

      for (Lp6ContributionGeographicScope lp6ContributionGeographicScope : lp6ContributionGeographicScopes) {
        if (!countriesSave.contains(lp6ContributionGeographicScope)) {
          lp6ContributionGeographicScopeManager
            .deleteLp6ContributionGeographicScope(lp6ContributionGeographicScope.getId());
        }
      }
    }

  }


  public void saveGeographicScope() {
    List<Lp6ContributionGeographicScope> countries =
      lp6ContributionGeographicScopeManager.getLp6ContributionGeographicScopebyPhase(
        project.getProjectLp6Contribution().getId(), this.getActualPhase().getId());
    List<Lp6ContributionGeographicScope> countriesSave = new ArrayList<>();

    if (project.getProjectLp6Contribution().getCountriesIds() != null
      && !project.getProjectLp6Contribution().getCountriesIds().isEmpty()) {

      for (String countryIds : project.getProjectLp6Contribution().getCountriesIds()) {
        Lp6ContributionGeographicScope countryInn = new Lp6ContributionGeographicScope();
        countryInn.setLocElement(locElementManager.getLocElementByISOCode(countryIds));
        countryInn.setPhase(this.getActualPhase());
        countryInn.setProjectLp6Contribution(project.getProjectLp6Contribution());
        countriesSave.add(countryInn);
        if (!countries.contains(countryInn)) {
          lp6ContributionGeographicScopeManager.saveLp6ContributionGeographicScope(countryInn);
        }
      }

      for (Lp6ContributionGeographicScope lp6ContributionGeographicScope : countries) {
        if (!countriesSave.contains(lp6ContributionGeographicScope)) {
          lp6ContributionGeographicScopeManager
            .deleteLp6ContributionGeographicScope(lp6ContributionGeographicScope.getId());
        }
      }
    } else {
      // If the selected countries is empty, it will be deleted from database
      if (countries != null && !countries.isEmpty()) {
        for (Lp6ContributionGeographicScope lp6ContributionGeographicScope : countries) {
          lp6ContributionGeographicScopeManager
            .deleteLp6ContributionGeographicScope(lp6ContributionGeographicScope.getId());
        }
      }
    }
  }

  private void saveGeographicScopes() {
    if (project.getProjectLp6Contribution().getGeographicScope() != null) {

      if (project.getProjectLp6Contribution().getGeographicScope().getId() == -1) {
        project.getProjectLp6Contribution().setGeographicScope(null);
        this.deleteLocElements();

      } else {

        // Save Geographic Scope Data
        if (!project.getProjectLp6Contribution().getGeographicScope().getId()
          .equals(this.getReportingIndGeographicScopeGlobal())) {

          // Save the Regions List
          if (project.getProjectLp6Contribution().getGeographicScope().getId()
            .equals(this.getReportingIndGeographicScopeRegional())) {
            this.saveRegions();
          } else {

            // Save the Countries List
            this.saveCountries();
          }
        } else {
          this.deleteLocElements();
        }
      }
    }
  }


  public void saveProjectDeliverables() {
    List<ProjectLp6ContributionDeliverable> projectLp6ContributionDeliverables =
      projectLp6ContributionDB.getProjectLp6ContributionDeliverable().stream()
        .filter(pd -> pd.isActive() && pd.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());

    if (project.getProjectLp6Contribution().getDeliverables() != null
      && !project.getProjectLp6Contribution().getDeliverables().isEmpty()) {

      if (projectLp6ContributionDeliverables != null && !projectLp6ContributionDeliverables.isEmpty()) {

        for (ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable : projectLp6ContributionDeliverables) {

          if (!project.getProjectLp6Contribution().getDeliverables().contains(projectLp6ContributionDeliverable)) {
            // If the deliverable in bd is deleted in front end, it will be deleted from database
            projectLp6ContributionDeliverableManager
              .deleteProjectLp6ContributionDeliverable(projectLp6ContributionDeliverable.getId());
          }
        }
      }

      for (ProjectLp6ContributionDeliverable contributionDeliverables : project.getProjectLp6Contribution()
        .getDeliverables()) {
        if (contributionDeliverables.getId() == null) {
          contributionDeliverables.setPhase(this.getActualPhase());
          contributionDeliverables.setProjectLp6Contribution(project.getProjectLp6Contribution());
          contributionDeliverables =
            projectLp6ContributionDeliverableManager.saveProjectLp6ContributionDeliverable(contributionDeliverables);
          project.getProjectLp6Contribution().getProjectLp6ContributionDeliverable().add(contributionDeliverables);
        }
      }
    } else {
      // If the list of selected deliverables is empty
      if (projectLp6ContributionDeliverables != null && !projectLp6ContributionDeliverables.isEmpty()) {
        for (ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable : projectLp6ContributionDeliverables) {
          projectLp6ContributionDeliverableManager
            .deleteProjectLp6ContributionDeliverable(projectLp6ContributionDeliverable.getId());
        }
      }
    }
  }

  /**
   * Save Project Lp6 Geographic Scope When Regions is Selected Information
   * 
   * @param projectLp6Contribution
   */
  public void saveRegions() {

    List<Lp6ContributionGeographicScope> lp6ContributionGeographicScopes =
      new ArrayList<>(projectLp6ContributionDB.getLp6ContributionGeographicScopes().stream()
        .filter(lg -> lg.isActive() && lg.getPhase().getId() == this.getActualPhase().getId())
        .collect(Collectors.toList()));

    // Search and delete form Information
    if (lp6ContributionGeographicScopes != null && !lp6ContributionGeographicScopes.isEmpty()) {
      for (Lp6ContributionGeographicScope lp6ContributionGeographicScope : lp6ContributionGeographicScopes) {
        if (project.getProjectLp6Contribution().getRegions() == null
          || !project.getProjectLp6Contribution().getRegions().contains(lp6ContributionGeographicScope)) {
          lp6ContributionGeographicScopeManager
            .deleteLp6ContributionGeographicScope(lp6ContributionGeographicScope.getId());
        }
      }
    }

    // Save form Information
    if (project.getProjectLp6Contribution().getRegions() != null) {
      for (Lp6ContributionGeographicScope projectRegion : project.getProjectLp6Contribution().getRegions()) {
        if (projectRegion.getId() == null) {
          Lp6ContributionGeographicScope projectRegionSave = new Lp6ContributionGeographicScope();
          projectRegionSave.setProjectLp6Contribution(projectLp6ContributionDB);
          projectRegionSave.setPhase(this.getActualPhase());
          LocElement locElement = locElementManager.getLocElementById(projectRegion.getLocElement().getId());
          projectRegionSave.setLocElement(locElement);
          projectRegionSave =
            lp6ContributionGeographicScopeManager.saveLp6ContributionGeographicScope(projectRegionSave);
          project.getProjectLp6Contribution().getLp6ContributionGeographicScopes().add(projectRegionSave);
        }
      }
    }
  }


  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
  }


  public void setDeliverables(List<Deliverable> deliverables) {
    this.deliverables = deliverables;
  }

  public void setGeographicScopes(List<RepIndGeographicScope> geographicScopes) {
    this.geographicScopes = geographicScopes;
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

  public void setProgramFlagships(List<CrpProgram> programFlagships) {
    this.programFlagships = programFlagships;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setRegionFlagships(List<CrpProgram> regionFlagships) {
    this.regionFlagships = regionFlagships;
  }

  public void setRegions(List<LocElement> regions) {
    this.regions = regions;
  }


  public void setRepIndGeographicScopes(List<RepIndGeographicScope> repIndGeographicScopes) {
    this.repIndGeographicScopes = repIndGeographicScopes;
  }


  public void setRepIndRegions(List<LocElement> repIndRegions) {
    this.repIndRegions = repIndRegions;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


  @Override
  public void validate() {
    // if is saving call the validator to check for the missing fields
    if (save) {
      validator.validate(this, project, project.getProjectLp6Contribution(), true);
    }
  }

}