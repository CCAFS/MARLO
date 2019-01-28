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
import org.cgiar.ccafs.marlo.data.model.Phase;
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
  private ProjectLp6Contribution projectLp6Contribution;
  private ProjectLp6Contribution projectLp6ContributionDB;
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
  private List<Lp6ContributionGeographicScope> contributionSelectedCountries;
  private List<LocElement> repIndRegions;
  private List<LocElement> countries;
  private List<Deliverable> deliverables;
  private List<RepIndGeographicScope> geographicScopes;
  private List<LocElement> regions;


  private ProjectLP6Validator validator;

  @Inject
  public ProjectContributionToLP6Action(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    CrpProgramManager programManager, Lp6ContributionGeographicScopeManager lp6ContributionGeographicScopeManager,
    LiaisonUserManager liaisonUserManager, LiaisonInstitutionManager liaisonInstitutionManager, UserManager userManager,
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


  public void cleanNarrativeFields() {
    if (projectLp6Contribution.getInitiativeRelated() != null) {
      if (projectLp6Contribution.isInitiativeRelated() == false) {
        projectLp6Contribution.setInitiativeRelatedNarrative("");
      }
    }

    if (projectLp6Contribution.isProvidingPathways() != null) {
      if (projectLp6Contribution.isProvidingPathways() == false) {
        projectLp6Contribution.setProvidingPathwaysNarrative("");
      }
    }

    if (projectLp6Contribution.isUndertakingEffortsCsa() != null) {
      if (projectLp6Contribution.isUndertakingEffortsCsa() == false) {
        projectLp6Contribution.setUndertakingEffortsCsaNarrative("");
      }
    }

    if (projectLp6Contribution.isUndertakingEffortsLeading() != null) {
      if (projectLp6Contribution.isUndertakingEffortsLeading() == false) {
        projectLp6Contribution.setUndertakingEffortsLeadingNarrative("");
      }
    }

    if (projectLp6Contribution.isWorkingAcrossFlagships() != null) {
      if (projectLp6Contribution.isWorkingAcrossFlagships() == false) {
        projectLp6Contribution.setWorkingAcrossFlagshipsNarrative("");
      }
    }

  }

  /**
   * Delete all LocElements Records when Geographic Scope is Global or NULL
   * 
   * @param projectLp6Contribution
   * @param phase
   */
  public void deleteLocElements(ProjectLp6Contribution projectLp6, Phase phase) {
    if (projectLp6.getLp6ContributionGeographicScopes() != null
      && projectLp6.getLp6ContributionGeographicScopes().size() > 0) {

      List<Lp6ContributionGeographicScope> regionPrev = new ArrayList<>(projectLp6.getLp6ContributionGeographicScopes()
        .stream().filter(nu -> nu.isActive() && nu.getPhase().getId() == phase.getId()).collect(Collectors.toList()));

      for (Lp6ContributionGeographicScope projectRegion : regionPrev) {

        lp6ContributionGeographicScopeManager.deleteLp6ContributionGeographicScope(projectRegion.getId());

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
    String composedClassName = projectLp6Contribution.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatenate name and add the .json extension
    String autoSaveFile = projectLp6Contribution.getId() + "_" + composedClassName + "_"
      + this.getActualPhase().getName() + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<Lp6ContributionGeographicScope> getContributionSelectedCountries() {
    return contributionSelectedCountries;
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

  public ProjectLp6Contribution getProjectLp6Contribution() {
    return projectLp6Contribution;
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

    contributionSelectedCountries = new ArrayList<>();
    // TODO countriesIds = new ArrayList<String>();

    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    this.setProject(projectManager.getProjectById(projectID));

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

      this.setProjectLp6Contribution(
        projectLp6ContributionManager.findAll().stream().filter(c -> c.isActive() && c.getProject().getId() == projectID
          && c.getPhase().getId() == this.getActualPhase().getId()).collect(Collectors.toList()).get(0));

      projectLp6ContributionDB =
        projectLp6ContributionManager.findAll().stream().filter(c -> c.isActive() && c.getProject().getId() == projectID
          && c.getPhase().getId() == this.getActualPhase().getId()).collect(Collectors.toList()).get(0);
    }


    if (projectLp6Contribution != null) {

      if (projectLp6Contribution.getGeographicScope() != null
        && projectLp6Contribution.getGeographicScope().getId() != null) {
        projectLp6Contribution.setGeographicScope(repIndGeographicScopeManager
          .getRepIndGeographicScopeById(projectLp6Contribution.getGeographicScope().getId()));
      }

      // Get selected deliverables
      if (projectLp6Contribution.getProjectLp6ContributionDeliverable() == null) {
        projectLp6Contribution.setDeliverables(new ArrayList<>());
      } else {
        List<ProjectLp6ContributionDeliverable> deliverables = projectLp6ContributionDeliverableManager
          .getProjectLp6ContributionDeliverablebyPhase(projectLp6Contribution.getId(), this.getActualPhase().getId());
        projectLp6Contribution.setDeliverables(deliverables);
      }

      /*
       * if (projectLp6Contribution.getProjectLp6ContributionDeliverable() != null) {
       * projectLp6Contribution
       * .setDeliverables(new ArrayList<>(projectLp6Contribution.getProjectLp6ContributionDeliverable().stream()
       * .filter(o -> o.isActive() && o.getPhase().getId() == this.getActualPhase().getId())
       * .collect(Collectors.toList())));
       * }
       */
    }

    Path path = this.getAutoSaveFilePath();
    if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

      // Autosave File in
      BufferedReader reader = null;
      reader = new BufferedReader(new FileReader(path.toFile()));
      Gson gson = new GsonBuilder().create();
      JsonObject jReader = gson.fromJson(reader, JsonObject.class);
      reader.close();

      AutoSaveReader autoSaveReader = new AutoSaveReader();

      projectLp6Contribution = (ProjectLp6Contribution) autoSaveReader.readFromJson(jReader);


      // Project Lp6 contribution Scope List AutoSave
      if (projectLp6Contribution.getGeographicScope() != null) {
        if (projectLp6Contribution.getGeographicScope().getId() != null
          && projectLp6Contribution.getGeographicScope().getId() != -1) {
          // If the Geographic Scope is not Global
          if (projectLp6Contribution.getGeographicScope().getId() != 1) {
            if (projectLp6Contribution.getGeographicScope().getId() == 2) {
              // Load Regions
              if (projectLp6Contribution.getRegions() != null) {
                for (Lp6ContributionGeographicScope lp6ContributionGeographicScope : projectLp6Contribution
                  .getRegions()) {
                  lp6ContributionGeographicScope.setLocElement(
                    locElementManager.getLocElementById(lp6ContributionGeographicScope.getLocElement().getId()));
                }
              }
            } else {
              // Load Countries
              if (projectLp6Contribution.getCountriesIdsText() != null) {
                String[] countriesText =
                  projectLp6Contribution.getCountriesIdsText().replace("[", "").replace("]", "").split(",");
                List<String> countries = new ArrayList<>();
                for (String value : Arrays.asList(countriesText)) {
                  countries.add(value.trim());
                }
                projectLp6Contribution.setCountriesIds(countries);
              }
            }
          }
        }
      }


      this.setDraft(true);
    } else {
      this.setDraft(false);


      // Setup Geographic Scope
      if (projectLp6Contribution.getGeographicScope() != null) {
        // If the Geographic Scope is not Global
        if (projectLp6Contribution.getGeographicScope().getId() != 1) {

          // Project Lp6 contribution Countries List
          if (projectLp6Contribution.getLp6ContributionGeographicScopes() == null) {
            projectLp6Contribution.setCountries(new ArrayList<>());
            projectLp6Contribution.setRegions(new ArrayList<>());
          } else {
            List<Lp6ContributionGeographicScope> geographics = lp6ContributionGeographicScopeManager
              .getLp6ContributionGeographicScopebyPhase(projectLp6Contribution.getId(), this.getActualPhase().getId());
            if (projectLp6Contribution.getGeographicScope().getId() == 2) {
              // Load Regions
              projectLp6Contribution.setRegions(geographics.stream()
                .filter(sc -> sc.getLocElement().getLocElementType().getId() == 1).collect(Collectors.toList()));
            } else {
              // Load Countries
              projectLp6Contribution.setCountries(geographics.stream()
                .filter(sc -> sc.getLocElement().getLocElementType().getId() == 2).collect(Collectors.toList()));
            }
          }
        }
      }

    }

    if (!this.isDraft()) {
      if (projectLp6Contribution.getCountries() != null) {
        for (Lp6ContributionGeographicScope country : projectLp6Contribution.getCountries()) {
          projectLp6Contribution.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
        }
      }
    }

    // Getting The lists

    /*
     * Geographic scope for lp6 contribution
     */

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


    if (this.isHttpPost()) {
      if (projectLp6Contribution.getDeliverables() != null) {
        projectLp6Contribution.getDeliverables().clear();
      }

      if (projectLp6Contribution.getRegions() != null) {
        projectLp6Contribution.getRegions().clear();
      }

      if (projectLp6Contribution.getCountries() != null) {
        projectLp6Contribution.getCountries().clear();
      }

      // HTTP Post info Values
      projectLp6Contribution.setLp6ContributionGeographicScopes(null);
      projectLp6Contribution.setProjectLp6ContributionDeliverable(null);
    }

  }

  @Override
  public String save() {

    if (this.hasPermission("canEdit")) {

      Path path = this.getAutoSaveFilePath();

      // Save the actual project Lp6 contribution
      this.cleanNarrativeFields();

      // Save project Lp6 deliverables
      this.saveProjectDeliverables();

      // Save project Lp6 phase
      projectLp6Contribution.setPhase(this.getActualPhase());

      // Validate negative Values
      if (projectLp6Contribution.getGeographicScope() != null) {
        if (projectLp6Contribution.getGeographicScope().getId() == -1) {
          projectLp6Contribution.setGeographicScope(null);
          // delete all locElements in geographic scope
          this.deleteLocElements(projectLp6ContributionDB, this.getActualPhase());
        } else {


          // Save Geographic Scope Data

          if (projectLp6Contribution.getGeographicScope().getId() != 1) {
            if (projectLp6Contribution.getGeographicScope().getId() == 2) {
              // Save the Regions List (Lp6ProjectContributionCountry)
              this.saveRegions(projectLp6ContributionDB, this.getActualPhase());

            } else {

              // Save the Countries List (Lp6ProjectContributionCountry)
              if (projectLp6Contribution.getCountriesIds() != null
                || !projectLp6Contribution.getCountriesIds().isEmpty()) {

                List<Lp6ContributionGeographicScope> countries =
                  lp6ContributionGeographicScopeManager.getLp6ContributionGeographicScopebyPhase(
                    projectLp6Contribution.getId(), this.getActualPhase().getId());
                List<Lp6ContributionGeographicScope> countriesSave = new ArrayList<>();
                for (String countryIds : projectLp6Contribution.getCountriesIds()) {
                  Lp6ContributionGeographicScope countryInn = new Lp6ContributionGeographicScope();
                  countryInn.setLocElement(locElementManager.getLocElementByISOCode(countryIds));
                  countryInn.setProjectLp6Contribution(projectLp6Contribution);
                  countryInn.setPhase(this.getActualPhase());
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
              }
            }

          } else {
            // delete all locElements in geographic scope
            this.deleteLocElements(projectLp6ContributionDB, this.getActualPhase());
          }
        }
      }

      // Save the Countries List selected
      // this.saveGeographicScope2();


      projectLp6ContributionManager.saveProjectLp6Contribution(projectLp6Contribution);

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

  public void saveGeographicScope() {
    List<Lp6ContributionGeographicScope> countries = lp6ContributionGeographicScopeManager
      .getLp6ContributionGeographicScopebyPhase(projectLp6Contribution.getId(), this.getActualPhase().getId());
    List<Lp6ContributionGeographicScope> countriesSave = new ArrayList<>();

    if (projectLp6Contribution.getCountriesIds() != null && !projectLp6Contribution.getCountriesIds().isEmpty()) {

      for (String countryIds : projectLp6Contribution.getCountriesIds()) {
        Lp6ContributionGeographicScope countryInn = new Lp6ContributionGeographicScope();
        countryInn.setLocElement(locElementManager.getLocElementByISOCode(countryIds));
        countryInn.setPhase(this.getActualPhase());
        countryInn.setProjectLp6Contribution(projectLp6Contribution);
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

  public void saveProjectDeliverables() {
    List<ProjectLp6ContributionDeliverable> projectLp6ContributionDeliverables =
      projectLp6ContributionDeliverableManager
        .getProjectLp6ContributionDeliverablebyPhase(projectLp6Contribution.getId(), this.getActualPhase().getId());

    if (projectLp6Contribution.getDeliverables() != null && !projectLp6Contribution.getDeliverables().isEmpty()) {

      if (projectLp6ContributionDeliverables != null && !projectLp6ContributionDeliverables.isEmpty()) {

        for (ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable : projectLp6ContributionDeliverables) {

          if (!projectLp6Contribution.getDeliverables().contains(projectLp6ContributionDeliverable)) {
            // If the deliverable in bd is deleted in front end, it will be deleted from database
            projectLp6ContributionDeliverableManager
              .deleteProjectLp6ContributionDeliverable(projectLp6ContributionDeliverable.getId());
          }
        }
      }

      for (ProjectLp6ContributionDeliverable contributionDeliverables : projectLp6Contribution.getDeliverables()) {
        contributionDeliverables.setPhase(this.getActualPhase());
        contributionDeliverables.setProjectLp6Contribution(projectLp6Contribution);
        projectLp6ContributionDeliverableManager.saveProjectLp6ContributionDeliverable(contributionDeliverables);
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
   * @param phase
   */
  public void saveRegions(ProjectLp6Contribution projectLp6, Phase phase) {

    // Search and deleted form Information
    if (projectLp6.getLp6ContributionGeographicScopes() != null
      && projectLp6.getLp6ContributionGeographicScopes().size() > 0) {

      List<Lp6ContributionGeographicScope> regionPrev = new ArrayList<>(projectLp6.getLp6ContributionGeographicScopes()
        .stream().filter(nu -> nu.isActive() && nu.getPhase().getId() == phase.getId()).collect(Collectors.toList()));
      System.out.println("save regions " + regionPrev.size());
      for (Lp6ContributionGeographicScope projectRegion : regionPrev) {
        if (projectLp6Contribution.getRegions() == null
          || !projectLp6Contribution.getRegions().contains(projectRegion)) {
          lp6ContributionGeographicScopeManager.deleteLp6ContributionGeographicScope(projectRegion.getId());
        }
      }
    }

    // Save form Information
    if (projectLp6Contribution.getRegions() != null) {
      for (Lp6ContributionGeographicScope projectRegion : projectLp6Contribution.getRegions()) {
        if (projectRegion.getId() == null) {
          Lp6ContributionGeographicScope projectRegionSave = new Lp6ContributionGeographicScope();
          projectRegionSave.setProjectLp6Contribution(projectLp6);
          projectRegionSave.setPhase(phase);

          LocElement locElement = locElementManager.getLocElementById(projectRegion.getLocElement().getId());

          projectRegionSave.setLocElement(locElement);

          lp6ContributionGeographicScopeManager.saveLp6ContributionGeographicScope(projectRegionSave);
          // This is to add **** to generate correct auditlog.
          // projectLp6Contribution.getLp6ContributionGeographicScopes().add(projectRegionSave);
        }
      }
    }
  }

  public void setContributionSelectedCountries(List<Lp6ContributionGeographicScope> contributionSelectedCountries) {
    this.contributionSelectedCountries = contributionSelectedCountries;
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


  public void setProjectLp6Contribution(ProjectLp6Contribution projectLp6Contribution) {
    this.projectLp6Contribution = projectLp6Contribution;
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
      validator.validate(this, project, projectLp6Contribution, true);
    }
  }

}