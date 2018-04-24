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
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPhaseResearchPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndRegionManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageInnovationManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationDeliverable;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationOrganization;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationType;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.RepIndPhaseResearchPartnership;
import org.cgiar.ccafs.marlo.data.model.RepIndRegion;
import org.cgiar.ccafs.marlo.data.model.RepIndStageInnovation;
import org.cgiar.ccafs.marlo.data.model.TypeExpectedStudiesEnum;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.projects.ProjectInnovationValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectInnovationAction extends BaseAction {


  private static final long serialVersionUID = 2025842196563364380L;


  private long projectID;

  private long innovationID;


  private Project project;

  private ProjectInnovation innovation;

  private ProjectInnovation innovationDB;
  private GlobalUnit loggedCrp;
  private List<RepIndPhaseResearchPartnership> phaseResearchList;
  private List<RepIndStageInnovation> stageInnovationList;
  private String transaction;

  private List<RepIndGeographicScope> geographicScopeList;
  private List<RepIndInnovationType> innovationTypeList;
  private List<RepIndRegion> regionList;
  private List<LocElement> countries;
  private List<ProjectExpectedStudy> expectedStudyList;
  private List<Deliverable> deliverableList;
  private List<GlobalUnit> crpList;
  private List<RepIndGenderYouthFocusLevel> focusLevelList;
  private List<RepIndOrganizationType> organizationTypeList;
  private ProjectInnovationManager projectInnovationManager;
  private GlobalUnitManager globalUnitManager;

  private ProjectManager projectManager;
  private PhaseManager phaseManager;
  private RepIndPhaseResearchPartnershipManager repIndPhaseResearchPartnershipManager;
  private RepIndStageInnovationManager repIndStageInnovationManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private RepIndInnovationTypeManager repIndInnovationTypeManager;
  private RepIndRegionManager repIndRegionManager;
  private LocElementManager locElementManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private DeliverableManager deriverableManager;
  private RepIndGenderYouthFocusLevelManager focusLevelManager;
  private ProjectInnovationInfoManager projectInnovationInfoManager;
  private ProjectInnovationCrpManager projectInnovationCrpManager;
  private ProjectInnovationOrganizationManager projectInnovationOrganizationManager;
  private ProjectInnovationDeliverableManager projectInnovationDeliverableManager;
  private ProjectInnovationCountryManager projectInnovationCountryManager;
  private RepIndOrganizationTypeManager repIndOrganizationTypeManager;
  private ProjectInnovationValidator validator;
  private AuditLogManager auditLogManager;

  @Inject
  public ProjectInnovationAction(APConfig config, GlobalUnitManager globalUnitManager,
    ProjectInnovationManager projectInnovationManager, ProjectManager projectManager, PhaseManager phaseManager,
    RepIndPhaseResearchPartnershipManager repIndPhaseResearchPartnershipManager,
    RepIndStageInnovationManager repIndStageInnovationManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager, RepIndInnovationTypeManager repIndInnovationTypeManager,
    RepIndRegionManager repIndRegionManager, LocElementManager locElementManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, DeliverableManager deriverableManager,
    RepIndGenderYouthFocusLevelManager focusLevelManager, ProjectInnovationInfoManager projectInnovationInfoManager,
    ProjectInnovationCrpManager projectInnovationCrpManager,
    ProjectInnovationOrganizationManager projectInnovationOrganizationManager,
    ProjectInnovationDeliverableManager projectInnovationDeliverableManager,
    ProjectInnovationCountryManager projectInnovationCountryManager,
    RepIndOrganizationTypeManager repIndOrganizationTypeManager, ProjectInnovationValidator validator,
    AuditLogManager auditLogManager) {
    super(config);
    this.projectInnovationManager = projectInnovationManager;
    this.globalUnitManager = globalUnitManager;
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.repIndPhaseResearchPartnershipManager = repIndPhaseResearchPartnershipManager;
    this.repIndStageInnovationManager = repIndStageInnovationManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.repIndInnovationTypeManager = repIndInnovationTypeManager;
    this.repIndRegionManager = repIndRegionManager;
    this.locElementManager = locElementManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.deriverableManager = deriverableManager;
    this.focusLevelManager = focusLevelManager;
    this.projectInnovationInfoManager = projectInnovationInfoManager;
    this.projectInnovationCrpManager = projectInnovationCrpManager;
    this.projectInnovationOrganizationManager = projectInnovationOrganizationManager;
    this.projectInnovationDeliverableManager = projectInnovationDeliverableManager;
    this.projectInnovationCountryManager = projectInnovationCountryManager;
    this.repIndOrganizationTypeManager = repIndOrganizationTypeManager;
    this.validator = validator;
    this.auditLogManager = auditLogManager;


  }

  /**
   * The name of the autosave file is constructed and the path is searched
   * 
   * @return Auto save file path
   */
  private Path getAutoSaveFilePath() {
    // get the class simple name
    String composedClassName = innovation.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatenate name and add the .json extension
    String autoSaveFile = innovation.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<LocElement> getCountries() {
    return countries;
  }

  @Override
  public List<GlobalUnit> getCrpList() {
    return crpList;
  }


  public List<Deliverable> getDeliverableList() {
    return deliverableList;
  }


  public List<ProjectExpectedStudy> getExpectedStudyList() {
    return expectedStudyList;
  }

  public List<RepIndGenderYouthFocusLevel> getFocusLevelList() {
    return focusLevelList;
  }

  public List<RepIndGeographicScope> getGeographicScopeList() {
    return geographicScopeList;
  }

  public ProjectInnovation getInnovation() {
    return innovation;
  }

  public long getInnovationID() {
    return innovationID;
  }


  public List<RepIndInnovationType> getInnovationTypeList() {
    return innovationTypeList;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<RepIndOrganizationType> getOrganizationTypeList() {
    return organizationTypeList;
  }

  public List<RepIndPhaseResearchPartnership> getPhaseResearchList() {
    return phaseResearchList;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public List<RepIndRegion> getRegionList() {
    return regionList;
  }

  public List<RepIndStageInnovation> getStageInnovationList() {
    return stageInnovationList;
  }

  public String getTransaction() {
    return transaction;
  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = globalUnitManager.getGlobalUnitById(loggedCrp.getId());

    innovationID =
      Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.INNOVATION_REQUEST_ID)));

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      ProjectInnovation history = (ProjectInnovation) auditLogManager.getHistory(transaction);

      if (history != null) {
        innovation = history;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      innovation = projectInnovationManager.getProjectInnovationById(innovationID);

    }


    if (innovation != null) {
      projectID = innovation.getProject().getId();
      project = projectManager.getProjectById(projectID);


      Phase phase = phaseManager.getPhaseById(this.getActualPhase().getId());
      project.getProjecInfoPhase(phase);


      Path path = this.getAutoSaveFilePath();
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        // Autosave File in
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        innovation = (ProjectInnovation) autoSaveReader.readFromJson(jReader);

        // Innovation Countries List AutoSave
        if (innovation.getCountriesIdsText() != null) {
          String[] countriesText = innovation.getCountriesIdsText().replace("[", "").replace("]", "").split(",");
          List<String> countries = new ArrayList<>();
          for (String value : Arrays.asList(countriesText)) {
            countries.add(value.trim());
          }
          innovation.setCountriesIds(countries);
        }

        // Innovation Organization Type List Autosave
        if (innovation.getOrganizations() != null) {
          for (ProjectInnovationOrganization projectInnovationOrganization : innovation.getOrganizations()) {
            projectInnovationOrganization.setRepIndOrganizationType(repIndOrganizationTypeManager
              .getRepIndOrganizationTypeById(projectInnovationOrganization.getRepIndOrganizationType().getId()));
          }
        }

        // Innovation Deliverable List Autosave
        if (innovation.getDeliverables() != null) {
          for (ProjectInnovationDeliverable projectInnovationDeliverable : innovation.getDeliverables()) {
            projectInnovationDeliverable.setDeliverable(
              deriverableManager.getDeliverableById(projectInnovationDeliverable.getDeliverable().getId()));
          }
        }

        // Innovation Crp List Autosave
        if (innovation.getCrps() != null) {
          for (ProjectInnovationCrp projectInnovationCrp : innovation.getCrps()) {
            projectInnovationCrp
              .setGlobalUnit(globalUnitManager.getGlobalUnitById(projectInnovationCrp.getGlobalUnit().getId()));
          }
        }

        this.setDraft(true);

      } else {

        this.setDraft(false);

        if (innovation.getProjectInnovationInfo() == null) {
          innovation.getProjectInnovationInfo(phase);
        }

        // Innovation Countries List
        if (innovation.getProjectInnovationCountries() == null) {
          innovation.setCountries(new ArrayList<>());
        } else {
          List<ProjectInnovationCountry> countries =
            projectInnovationCountryManager.getInnovationCountrybyPhase(innovation.getId(), phase.getId());
          innovation.setCountries(countries);
        }

        // Innovation Organization Type List
        if (innovation.getProjectInnovationOrganizations() != null) {
          innovation.setOrganizations(new ArrayList<>(innovation.getProjectInnovationOrganizations().stream()
            .filter(o -> o.isActive() && o.getPhase().getId() == phase.getId()).collect(Collectors.toList())));
        }

        // Innovation Deliverable List
        if (innovation.getProjectInnovationDeliverables() != null) {
          innovation.setDeliverables(new ArrayList<>(innovation.getProjectInnovationDeliverables().stream()
            .filter(d -> d.isActive() && d.getPhase().getId() == phase.getId()).collect(Collectors.toList())));
        }

        // Innovation Crp list
        if (innovation.getProjectInnovationCrps() != null) {
          innovation.setCrps(new ArrayList<>(innovation.getProjectInnovationCrps().stream()
            .filter(c -> c.isActive() && c.getPhase().getId() == phase.getId()).collect(Collectors.toList())));
        }
      }

      if (!this.isDraft()) {
        if (innovation.getCountries() != null) {
          for (ProjectInnovationCountry country : innovation.getCountries()) {
            innovation.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
          }
        }
      }

      // Getting The list
      countries = locElementManager.findAll().stream().filter(c -> c.getLocElementType().getId().intValue() == 2)
        .collect(Collectors.toList());


      phaseResearchList = repIndPhaseResearchPartnershipManager.findAll();
      stageInnovationList = repIndStageInnovationManager.findAll();
      geographicScopeList = repIndGeographicScopeManager.findAll();
      innovationTypeList = repIndInnovationTypeManager.findAll();
      regionList = repIndRegionManager.findAll();
      focusLevelList = focusLevelManager.findAll();
      organizationTypeList = repIndOrganizationTypeManager.findAll();


      expectedStudyList = projectExpectedStudyManager.findAll().stream()
        .filter(ex -> ex.isActive() && ex.getType() != null
          && ex.getType() == TypeExpectedStudiesEnum.OUTCOMECASESTUDY.getId() && ex.getPhase().getId() == phase.getId())
        .collect(Collectors.toList());

      List<DeliverableInfo> infos = phase
        .getDeliverableInfos().stream().filter(c -> c.getDeliverable().getProject() != null
          && c.getDeliverable().getProject().equals(project) && c.getDeliverable().isActive())
        .collect(Collectors.toList());
      deliverableList = new ArrayList<>();
      for (DeliverableInfo deliverableInfo : infos) {
        Deliverable deliverable = deliverableInfo.getDeliverable();
        deliverable.setDeliverableInfo(deliverableInfo);
        deliverableList.add(deliverable);
      }


      crpList = globalUnitManager.findAll().stream()
        .filter(gu -> gu.isActive() && (gu.getGlobalUnitType().getId() == 1 || gu.getGlobalUnitType().getId() == 3))
        .collect(Collectors.toList());

    }

    innovationDB = projectInnovationManager.getProjectInnovationById(innovationID);

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_INNOVATIONS_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (innovation.getCountries() != null) {
        innovation.getCountries().clear();
      }

      if (innovation.getOrganizations() != null) {
        innovation.getOrganizations().clear();
      }

      if (innovation.getCrps() != null) {
        innovation.getCrps().clear();
      }

      if (innovation.getDeliverables() != null) {
        innovation.getDeliverables().clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      Phase phase = this.getActualPhase();

      Path path = this.getAutoSaveFilePath();

      innovation.setProject(project);

      this.saveOrganizations(innovationDB, phase);
      this.saveDeliverables(innovationDB, phase);
      this.saveCrps(innovationDB, phase);


      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_INNOVATION_COUNTRY_RELATION);
      relationsName.add(APConstants.PROJECT_INNOVATION_ORGANIZATION_RELATION);
      relationsName.add(APConstants.PROJECT_INNOVATION_CRP_RELATION);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_CRP_RELATION);

      innovation.setActiveSince(new Date());
      innovation.setModifiedBy(this.getCurrentUser());
      innovation.setModificationJustification(this.getJustification());
      innovation.setCreatedBy(innovationDB.getCreatedBy());
      innovation.setActive(true);

      // Save the Countries List (ProjectInnovationcountry)
      if (innovation.getCountriesIds() != null || !innovation.getCountriesIds().isEmpty()) {

        List<ProjectInnovationCountry> countries = projectInnovationCountryManager
          .getInnovationCountrybyPhase(innovation.getId(), this.getActualPhase().getId());
        List<ProjectInnovationCountry> countriesSave = new ArrayList<>();
        for (String countryIds : innovation.getCountriesIds()) {
          ProjectInnovationCountry countryInn = new ProjectInnovationCountry();
          countryInn.setLocElement(locElementManager.getLocElementByISOCode(countryIds));
          countryInn.setProjectInnovation(innovation);
          countryInn.setPhase(this.getActualPhase());
          countriesSave.add(countryInn);
          if (!countries.contains(countryInn)) {
            projectInnovationCountryManager.saveProjectInnovationCountry(countryInn);
          }
        }

        for (ProjectInnovationCountry projectInnovationCountry : countries) {
          if (!countriesSave.contains(projectInnovationCountry)) {
            projectInnovationCountryManager.deleteProjectInnovationCountry(projectInnovationCountry.getId());
          }
        }

      }


      innovation.getProjectInnovationInfo().setPhase(this.getActualPhase());
      innovation.getProjectInnovationInfo().setProjectInnovation(innovation);

      // Setup focusLevel
      if (innovation.getProjectInnovationInfo().getGenderFocusLevel() != null) {
        RepIndGenderYouthFocusLevel focusLevel = focusLevelManager
          .getRepIndGenderYouthFocusLevelById(innovation.getProjectInnovationInfo().getGenderFocusLevel().getId());
        innovation.getProjectInnovationInfo().setGenderFocusLevel(focusLevel);
      }

      if (innovation.getProjectInnovationInfo().getYouthFocusLevel() != null) {
        RepIndGenderYouthFocusLevel focusLevel = focusLevelManager
          .getRepIndGenderYouthFocusLevelById(innovation.getProjectInnovationInfo().getYouthFocusLevel().getId());
        innovation.getProjectInnovationInfo().setYouthFocusLevel(focusLevel);
      }
      // End

      // Validate negative Values
      if (innovation.getProjectInnovationInfo().getProjectExpectedStudy() != null) {
        if (innovation.getProjectInnovationInfo().getProjectExpectedStudy().getId() == -1) {
          innovation.getProjectInnovationInfo().setProjectExpectedStudy(null);
        }
      }

      if (innovation.getProjectInnovationInfo().getRepIndPhaseResearchPartnership() != null) {
        if (innovation.getProjectInnovationInfo().getRepIndPhaseResearchPartnership().getId() == -1) {
          innovation.getProjectInnovationInfo().setRepIndPhaseResearchPartnership(null);
        }
      }

      if (innovation.getProjectInnovationInfo().getRepIndStageInnovation() != null) {
        if (innovation.getProjectInnovationInfo().getRepIndStageInnovation().getId() == -1) {
          innovation.getProjectInnovationInfo().setRepIndStageInnovation(null);
        }
      }

      if (innovation.getProjectInnovationInfo().getRepIndGeographicScope() != null) {
        if (innovation.getProjectInnovationInfo().getRepIndGeographicScope().getId() == -1) {
          innovation.getProjectInnovationInfo().setRepIndGeographicScope(null);
        }
      }

      if (innovation.getProjectInnovationInfo().getRepIndInnovationType() != null) {
        if (innovation.getProjectInnovationInfo().getRepIndInnovationType().getId() == -1) {
          innovation.getProjectInnovationInfo().setRepIndInnovationType(null);
        }
      }

      if (innovation.getProjectInnovationInfo().getRepIndRegion() != null) {
        if (innovation.getProjectInnovationInfo().getRepIndRegion().getId() == -1) {
          innovation.getProjectInnovationInfo().setRepIndRegion(null);
        }
      }
      // End

      projectInnovationInfoManager.saveProjectInnovationInfo(innovation.getProjectInnovationInfo());
      projectInnovationManager.saveProjectInnovation(innovation, this.getActionName(), relationsName);

      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
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
   * Save Project Innovation Crp Information
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveCrps(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationCrps() != null
      && projectInnovation.getProjectInnovationCrps().size() > 0) {

      List<ProjectInnovationCrp> crpPrev = new ArrayList<>(projectInnovation.getProjectInnovationCrps().stream()
        .filter(nu -> nu.isActive() && nu.getPhase().getId() == phase.getId()).collect(Collectors.toList()));

      for (ProjectInnovationCrp innovationCrp : crpPrev) {
        if (!innovation.getCrps().contains(innovationCrp)) {
          projectInnovationCrpManager.deleteProjectInnovationCrp(innovationCrp.getId());
        }
      }
    }

    // Save form Information
    if (innovation.getCrps() != null) {
      for (ProjectInnovationCrp innovationCrp : innovation.getCrps()) {
        if (innovationCrp.getId() == null) {
          ProjectInnovationCrp innovationCrpSave = new ProjectInnovationCrp();
          innovationCrpSave.setProjectInnovation(projectInnovation);
          innovationCrpSave.setPhase(phase);

          GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(innovationCrp.getGlobalUnit().getId());

          innovationCrpSave.setGlobalUnit(globalUnit);

          projectInnovationCrpManager.saveProjectInnovationCrp(innovationCrpSave);
        }
      }
    }
  }

  public void saveDeliverables(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationDeliverables() != null
      && projectInnovation.getProjectInnovationDeliverables().size() > 0) {

      List<ProjectInnovationDeliverable> deliverablePrev =
        new ArrayList<>(projectInnovation.getProjectInnovationDeliverables().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId() == phase.getId()).collect(Collectors.toList()));

      for (ProjectInnovationDeliverable innovationDeliverable : deliverablePrev) {
        if (!innovation.getDeliverables().contains(innovationDeliverable)) {
          projectInnovationDeliverableManager.deleteProjectInnovationDeliverable(innovationDeliverable.getId());
        }
      }
    }

    // Save form Information
    if (innovation.getDeliverables() != null) {
      for (ProjectInnovationDeliverable innovationDeliverable : innovation.getDeliverables()) {
        if (innovationDeliverable.getId() == null) {
          ProjectInnovationDeliverable innovationDeliverableSave = new ProjectInnovationDeliverable();
          innovationDeliverableSave.setProjectInnovation(projectInnovation);
          innovationDeliverableSave.setPhase(phase);

          Deliverable deliverable =
            deriverableManager.getDeliverableById(innovationDeliverable.getDeliverable().getId());

          innovationDeliverableSave.setDeliverable(deliverable);

          projectInnovationDeliverableManager.saveProjectInnovationDeliverable(innovationDeliverableSave);
        }
      }
    }
  }

  /**
   * Save Project Innovation Organization Information
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveOrganizations(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationOrganizations() != null
      && projectInnovation.getProjectInnovationOrganizations().size() > 0) {

      List<ProjectInnovationOrganization> organizationPrev =
        new ArrayList<>(projectInnovation.getProjectInnovationOrganizations().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId() == phase.getId()).collect(Collectors.toList()));

      for (ProjectInnovationOrganization innovationOrganization : organizationPrev) {
        if (!innovation.getOrganizations().contains(innovationOrganization)) {
          projectInnovationOrganizationManager.deleteProjectInnovationOrganization(innovationOrganization.getId());
        }
      }
    }

    // Save form Information
    if (innovation.getOrganizations() != null) {
      for (ProjectInnovationOrganization innovationOrganization : innovation.getOrganizations()) {
        if (innovationOrganization.getId() == null) {
          ProjectInnovationOrganization innovationOrganizationSave = new ProjectInnovationOrganization();
          innovationOrganizationSave.setProjectInnovation(projectInnovation);
          innovationOrganizationSave.setPhase(phase);

          RepIndOrganizationType repIndOrganizationType = repIndOrganizationTypeManager
            .getRepIndOrganizationTypeById(innovationOrganization.getRepIndOrganizationType().getId());

          innovationOrganizationSave.setRepIndOrganizationType(repIndOrganizationType);

          projectInnovationOrganizationManager.saveProjectInnovationOrganization(innovationOrganizationSave);
        }
      }
    }
  }

  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
  }


  public void setCrpList(List<GlobalUnit> crpList) {
    this.crpList = crpList;
  }

  public void setDeliverableList(List<Deliverable> deliverableList) {
    this.deliverableList = deliverableList;
  }

  public void setExpectedStudyList(List<ProjectExpectedStudy> expectedStudyList) {
    this.expectedStudyList = expectedStudyList;
  }

  public void setFocusLevelList(List<RepIndGenderYouthFocusLevel> focusLevelList) {
    this.focusLevelList = focusLevelList;
  }

  public void setGeographicScopeList(List<RepIndGeographicScope> geographicScopeList) {
    this.geographicScopeList = geographicScopeList;
  }

  public void setInnovation(ProjectInnovation innovation) {
    this.innovation = innovation;
  }

  public void setInnovationID(long innovationID) {
    this.innovationID = innovationID;
  }

  public void setInnovationTypeList(List<RepIndInnovationType> innovationTypeList) {
    this.innovationTypeList = innovationTypeList;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setOrganizationTypeList(List<RepIndOrganizationType> organizationTypeList) {
    this.organizationTypeList = organizationTypeList;
  }

  public void setPhaseResearchList(List<RepIndPhaseResearchPartnership> phaseResearchList) {
    this.phaseResearchList = phaseResearchList;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setRegionList(List<RepIndRegion> regionList) {
    this.regionList = regionList;
  }

  public void setStageInnovationList(List<RepIndStageInnovation> stageInnovationList) {
    this.stageInnovationList = stageInnovationList;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, project, innovation, true);
    }
  }


}
