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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ExpectedStudyProjectManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyFlagshipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySrfTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyInvestimentTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndRegionManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageProcessManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageStudyManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.StudyTypeManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyInvestimentType;
import org.cgiar.ccafs.marlo.data.model.RepIndRegion;
import org.cgiar.ccafs.marlo.data.model.RepIndStageProcess;
import org.cgiar.ccafs.marlo.data.model.RepIndStageStudy;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.StudyType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.projects.ProjectExpectedStudiesValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
public class ProjectExpectedStudiesAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 597647662288518417L;


  private ProjectExpectedStudiesValidator projectExpectedStudiesValidator;


  private ProjectExpectedStudyManager projectExpectedStudyManager;


  private AuditLogManager auditLogManager;

  private GlobalUnitManager crpManager;


  private GlobalUnit loggedCrp;

  private Project project;


  private long projectID;


  private long expectedID;


  private ProjectExpectedStudy expectedStudy;


  private ProjectExpectedStudy expectedStudyDB;


  private Map<Integer, String> statuses;


  private List<RepIndGeographicScope> geographicScopes;


  private List<RepIndRegion> regions;

  private List<RepIndOrganizationType> organizationTypes;


  private List<RepIndGenderYouthFocusLevel> focusLevels;


  private List<RepIndPolicyInvestimentType> policyInvestimentTypes;


  private List<RepIndStageProcess> stageProcesses;
  private List<RepIndStageStudy> stageStudies;
  private List<StudyType> studyTypes;
  private List<LocElement> countries;
  private List<SrfSubIdo> subIdos;
  private List<SrfSloIndicator> targets;
  private List<GlobalUnit> crps;
  private List<CrpProgram> flagshipList;
  private List<CrpProgram> regionList;
  private List<Institution> institutions;
  private List<Project> myProjects;
  private ProjectManager projectManager;
  private PhaseManager phaseManager;
  private SrfSloIndicatorManager srfSloIndicatorManager;
  private SrfSubIdoManager srfSubIdoManager;
  private CrpProgramManager crpProgramManager;
  private InstitutionManager institutionManager;


  private LocElementManager locElementManager;
  private StudyTypeManager studyTypeManager;
  private FileDBManager fileDBManager;
  private RepIndGeographicScopeManager geographicScopeManager;
  private RepIndRegionManager repIndRegionManager;
  private RepIndOrganizationTypeManager organizationTypeManager;
  private RepIndGenderYouthFocusLevelManager focusLevelManager;
  private RepIndPolicyInvestimentTypeManager investimentTypeManager;

  private RepIndStageProcessManager stageProcessManager;
  private RepIndStageStudyManager stageStudyManager;
  private ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;
  private ProjectExpectedStudySubIdoManager projectExpectedStudySubIdoManager;
  private ProjectExpectedStudyFlagshipManager projectExpectedStudyFlagshipManager;
  private ProjectExpectedStudyCrpManager projectExpectedStudyCrpManager;
  private ProjectExpectedStudyInstitutionManager projectExpectedStudyInstitutionManager;
  private ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager;
  private ProjectExpectedStudySrfTargetManager projectExpectedStudySrfTargetManager;
  private ExpectedStudyProjectManager expectedStudyProjectManager;
  private String transaction;

  @Inject
  public ProjectExpectedStudiesAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, SrfSloIndicatorManager srfSloIndicatorManager,
    SrfSubIdoManager srfSubIdoManager, AuditLogManager auditLogManager,
    ExpectedStudyProjectManager expectedStudyProjectManager,
    ProjectExpectedStudiesValidator projectExpectedStudiesValidator, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, InstitutionManager institutionManager, LocElementManager locElementManager,
    StudyTypeManager studyTypeManager, FileDBManager fileDBManager, RepIndGeographicScopeManager geographicScopeManager,
    RepIndRegionManager repIndRegionManager, RepIndOrganizationTypeManager organizationTypeManager,
    RepIndGenderYouthFocusLevelManager focusLevelManager, RepIndPolicyInvestimentTypeManager investimentTypeManager,
    RepIndStageProcessManager stageProcessManager, RepIndStageStudyManager stageStudyManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager,
    ProjectExpectedStudySubIdoManager projectExpectedStudySubIdoManager,
    ProjectExpectedStudyFlagshipManager projectExpectedStudyFlagshipManager,
    ProjectExpectedStudyCrpManager projectExpectedStudyCrpManager,
    ProjectExpectedStudyInstitutionManager projectExpectedStudyInstitutionManager,
    ProjectExpectedStudySrfTargetManager projectExpectedStudySrfTargetManager,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager) {
    super(config);
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.phaseManager = phaseManager;
    this.auditLogManager = auditLogManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.srfSloIndicatorManager = srfSloIndicatorManager;
    this.expectedStudyProjectManager = expectedStudyProjectManager;
    this.projectExpectedStudiesValidator = projectExpectedStudiesValidator;

    this.crpProgramManager = crpProgramManager;
    this.institutionManager = institutionManager;
    this.locElementManager = locElementManager;
    this.studyTypeManager = studyTypeManager;
    this.fileDBManager = fileDBManager;
    this.geographicScopeManager = geographicScopeManager;
    this.repIndRegionManager = repIndRegionManager;
    this.organizationTypeManager = organizationTypeManager;
    this.focusLevelManager = focusLevelManager;
    this.investimentTypeManager = investimentTypeManager;
    this.stageProcessManager = stageProcessManager;
    this.stageStudyManager = stageStudyManager;

    this.projectExpectedStudyInfoManager = projectExpectedStudyInfoManager;
    this.projectExpectedStudySubIdoManager = projectExpectedStudySubIdoManager;
    this.projectExpectedStudyFlagshipManager = projectExpectedStudyFlagshipManager;
    this.projectExpectedStudyCrpManager = projectExpectedStudyCrpManager;
    this.projectExpectedStudyInstitutionManager = projectExpectedStudyInstitutionManager;
    this.projectExpectedStudyCountryManager = projectExpectedStudyCountryManager;
    this.projectExpectedStudySrfTargetManager = projectExpectedStudySrfTargetManager;

  }

  private Path getAutoSaveFilePath() {
    String composedClassName = expectedStudy.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = expectedStudy.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<LocElement> getCountries() {
    return countries;
  }

  public List<GlobalUnit> getCrps() {
    return crps;
  }

  public long getExpectedID() {
    return expectedID;
  }

  public ProjectExpectedStudy getExpectedStudy() {
    return expectedStudy;
  }

  public List<CrpProgram> getFlagshipList() {
    return flagshipList;
  }

  public List<RepIndGenderYouthFocusLevel> getFocusLevels() {
    return focusLevels;
  }

  public List<RepIndGeographicScope> getGeographicScopes() {
    return geographicScopes;
  }

  public List<Institution> getInstitutions() {
    return institutions;
  }


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<Project> getMyProjects() {
    return myProjects;
  }

  public List<RepIndOrganizationType> getOrganizationTypes() {
    return organizationTypes;
  }

  public List<RepIndPolicyInvestimentType> getPolicyInvestimentTypes() {
    return policyInvestimentTypes;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public List<CrpProgram> getRegionList() {
    return regionList;
  }


  public List<RepIndRegion> getRegions() {
    return regions;
  }

  public List<RepIndStageProcess> getStageProcesses() {
    return stageProcesses;
  }

  public List<RepIndStageStudy> getStageStudies() {
    return stageStudies;
  }


  public Map<Integer, String> getStatuses() {
    return statuses;
  }

  public List<StudyType> getStudyTypes() {
    return studyTypes;
  }

  public List<SrfSubIdo> getSubIdos() {
    return subIdos;
  }

  public List<SrfSloIndicator> getTargets() {
    return targets;
  }

  public String getTransaction() {
    return transaction;
  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    expectedID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.EXPECTED_REQUEST_ID)));

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      ProjectExpectedStudy history = (ProjectExpectedStudy) auditLogManager.getHistory(transaction);

      if (history != null) {
        expectedStudy = history;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      expectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(expectedID);
    }
    if (expectedStudy != null) {


      Phase phase = phaseManager.getPhaseById(this.getActualPhase().getId());


      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();


        AutoSaveReader autoSaveReader = new AutoSaveReader();
        expectedStudy = (ProjectExpectedStudy) autoSaveReader.readFromJson(jReader);


        // Expected Study Countries List AutoSave
        if (expectedStudy.getCountriesIdsText() != null) {
          String[] countriesText = expectedStudy.getCountriesIdsText().replace("[", "").replace("]", "").split(",");
          List<String> countries = new ArrayList<>();
          for (String value : Arrays.asList(countriesText)) {
            countries.add(value.trim());
          }
          expectedStudy.setCountriesIds(countries);
        }

        // Expected Study SubIdo List Autosave
        if (expectedStudy.getSubIdos() != null) {
          for (ProjectExpectedStudySubIdo projectExpectedStudySubIdo : expectedStudy.getSubIdos()) {
            projectExpectedStudySubIdo
              .setSrfSubIdo(srfSubIdoManager.getSrfSubIdoById(projectExpectedStudySubIdo.getSrfSubIdo().getId()));
          }
        }

        // Expected Study Flagship List Autosave
        if (expectedStudy.getFlagships() != null) {
          for (ProjectExpectedStudyFlagship projectExpectedStudyFlagship : expectedStudy.getFlagships()) {
            projectExpectedStudyFlagship
              .setCrpProgram(crpProgramManager.getCrpProgramById(projectExpectedStudyFlagship.getCrpProgram().getId()));
          }
        }

        // Expected Study Regions List Autosave
        if (expectedStudy.getRegions() != null) {
          for (ProjectExpectedStudyFlagship projectExpectedStudyFlagship : expectedStudy.getRegions()) {
            projectExpectedStudyFlagship
              .setCrpProgram(crpProgramManager.getCrpProgramById(projectExpectedStudyFlagship.getCrpProgram().getId()));
          }
        }

        // Expected Study Crp List Autosave
        if (expectedStudy.getCrps() != null) {
          for (ProjectExpectedStudyCrp projectExpectedStudyCrp : expectedStudy.getCrps()) {
            projectExpectedStudyCrp
              .setGlobalUnit(crpManager.getGlobalUnitById(projectExpectedStudyCrp.getGlobalUnit().getId()));
          }
        }

        // Expected Study Institutions List Autosave
        if (expectedStudy.getInstitutions() != null) {
          for (ProjectExpectedStudyInstitution projectExpectedStudyInstitution : expectedStudy.getInstitutions()) {
            projectExpectedStudyInstitution.setInstitution(
              institutionManager.getInstitutionById(projectExpectedStudyInstitution.getInstitution().getId()));
          }
        }

        // Expected Study Srf Target List Autosave
        if (expectedStudy.getSrfTargets() != null) {
          for (ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget : expectedStudy.getSrfTargets()) {
            projectExpectedStudySrfTarget.setSrfSloIndicator(srfSloIndicatorManager
              .getSrfSloIndicatorById(projectExpectedStudySrfTarget.getSrfSloIndicator().getId()));
          }
        }

        // Expected Study Projects List Autosave
        if (expectedStudy.getProjects() != null) {
          for (ExpectedStudyProject expectedStudyProject : expectedStudy.getProjects()) {
            expectedStudyProject.setProject(projectManager.getProjectById(expectedStudyProject.getProject().getId()));
          }
        }

        // Study Type Autosave
        if (expectedStudy.getProjectExpectedStudyInfo().getStudyType() != null) {
          if (expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId() != null
            && expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId() != -1) {
            StudyType studyType =
              studyTypeManager.getStudyTypeById(expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId());
            expectedStudy.getProjectExpectedStudyInfo().setStudyType(studyType);
          }
        }


        this.setDraft(true);
      } else {
        this.setDraft(false);

        if (expectedStudy.getProjectExpectedStudyInfo() == null) {
          expectedStudy.getProjectExpectedStudyInfo(this.getActualPhase());
        }

        // Expected Study Countries List
        if (expectedStudy.getProjectExpectedStudyCountries() == null) {
          expectedStudy.setCountries(new ArrayList<>());
        } else {
          List<ProjectExpectedStudyCountry> countries = projectExpectedStudyCountryManager
            .getProjectExpectedStudyCountrybyPhase(expectedStudy.getId(), phase.getId());
          expectedStudy.setCountries(countries);
        }

        // Expected Study SubIdos List
        if (expectedStudy.getProjectExpectedStudySubIdos() != null) {
          expectedStudy.setSubIdos(new ArrayList<>(expectedStudy.getProjectExpectedStudySubIdos().stream()
            .filter(o -> o.isActive() && o.getPhase().getId() == phase.getId()).collect(Collectors.toList())));
        }

        // Expected Study Flagship List
        if (expectedStudy.getProjectExpectedStudyFlagships() != null) {
          expectedStudy.setFlagships(new ArrayList<>(expectedStudy.getProjectExpectedStudyFlagships().stream()
            .filter(o -> o.isActive() && o.getPhase().getId() == phase.getId()
              && o.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList())));
        }

        // Expected Study Regions List
        if (expectedStudy.getProjectExpectedStudyFlagships() != null) {
          expectedStudy.setRegions(new ArrayList<>(expectedStudy.getProjectExpectedStudyFlagships().stream()
            .filter(o -> o.isActive() && o.getPhase().getId() == phase.getId()
              && o.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList())));
        }

        // Expected Study Crp List
        if (expectedStudy.getProjectExpectedStudyCrps() != null) {
          expectedStudy.setCrps(new ArrayList<>(expectedStudy.getProjectExpectedStudyCrps().stream()
            .filter(o -> o.isActive() && o.getPhase().getId() == phase.getId()).collect(Collectors.toList())));
        }

        // Expected Study Institutions List
        if (expectedStudy.getProjectExpectedStudyInstitutions() != null) {
          expectedStudy.setInstitutions(new ArrayList<>(expectedStudy.getProjectExpectedStudyInstitutions().stream()
            .filter(o -> o.isActive() && o.getPhase().getId() == phase.getId()).collect(Collectors.toList())));
        }

        // Expected Study Srf Target List
        if (expectedStudy.getProjectExpectedStudySrfTargets() != null) {
          expectedStudy.setSrfTargets(new ArrayList<>(expectedStudy.getProjectExpectedStudySrfTargets().stream()
            .filter(o -> o.isActive() && o.getPhase().getId() == phase.getId()).collect(Collectors.toList())));
        }

        // Expected Study Projects List
        if (expectedStudy.getExpectedStudyProjects() != null) {
          expectedStudy.setProjects(new ArrayList<>(expectedStudy.getExpectedStudyProjects().stream()
            .filter(o -> o.isActive() && o.getPhase().getId() == phase.getId()).collect(Collectors.toList())));
        }

      }


      if (!this.isDraft()) {
        if (expectedStudy.getCountries() != null) {
          for (ProjectExpectedStudyCountry country : expectedStudy.getCountries()) {
            expectedStudy.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
          }
        }
      }

      // Setup Files
      if (expectedStudy.getProjectExpectedStudyInfo().getOutcomeFile() != null) {
        if (expectedStudy.getProjectExpectedStudyInfo().getOutcomeFile().getId() != null) {
          expectedStudy.getProjectExpectedStudyInfo().setOutcomeFile(
            fileDBManager.getFileDBById(expectedStudy.getProjectExpectedStudyInfo().getOutcomeFile().getId()));
        }
      }

      if (expectedStudy.getProjectExpectedStudyInfo().getReferencesFile() != null) {
        if (expectedStudy.getProjectExpectedStudyInfo().getReferencesFile().getId() != null) {
          expectedStudy.getProjectExpectedStudyInfo().setReferencesFile(
            fileDBManager.getFileDBById(expectedStudy.getProjectExpectedStudyInfo().getReferencesFile().getId()));
        }
      }


      // Getting The list
      statuses = new HashMap<>();
      List<ProjectStatusEnum> listStatus = Arrays.asList(ProjectStatusEnum.values());
      for (ProjectStatusEnum globalStatusEnum : listStatus) {
        statuses.put(Integer.parseInt(globalStatusEnum.getStatusId()), globalStatusEnum.getStatus());
      }

      countries = locElementManager.findAll().stream().filter(c -> c.getLocElementType().getId().intValue() == 2)
        .collect(Collectors.toList());

      geographicScopes = geographicScopeManager.findAll();
      regions = repIndRegionManager.findAll();
      organizationTypes = organizationTypeManager.findAll();
      // Focus levels and Too early to tell was removed
      focusLevels = focusLevelManager.findAll().stream().filter(f -> f.getId() != 4).collect(Collectors.toList());
      policyInvestimentTypes = investimentTypeManager.findAll();
      stageProcesses = stageProcessManager.findAll();
      stageStudies = stageStudyManager.findAll();
      studyTypes = studyTypeManager.findAll();
      subIdos = srfSubIdoManager.findAll();
      targets = srfSloIndicatorManager.findAll();

      myProjects = new ArrayList<>();
      for (ProjectPhase projectPhase : phase.getProjectPhases()) {
        if (projectPhase.getProject().getProjecInfoPhase(this.getActualPhase()) != null) {
          myProjects.add(projectPhase.getProject());
        }

        if (project != null) {
          myProjects.remove(project);
        }
      }

      crps = crpManager.findAll().stream()
        .filter(gu -> gu.isActive() && (gu.getGlobalUnitType().getId() == 1 || gu.getGlobalUnitType().getId() == 3))
        .collect(Collectors.toList());

      flagshipList = crpProgramManager.findAll().stream()
        .filter(p -> p.isActive() && p.getCrp() != null && p.getCrp().getId() == loggedCrp.getId()
          && p.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());

      regionList = crpProgramManager.findAll().stream()
        .filter(p -> p.isActive() && p.getCrp() != null && p.getCrp().getId() == loggedCrp.getId()
          && p.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());

      institutions = institutionManager.findAll().stream().filter(i -> i.isActive()).collect(Collectors.toList());

      expectedStudyDB = projectExpectedStudyManager.getProjectExpectedStudyById(expectedID);

      if (expectedStudyDB.getProject() != null) {
        projectID = expectedStudyDB.getProject().getId();
        project = projectManager.getProjectById(projectID);
        project.getProjecInfoPhase(phase);
      }

      if (project != null) {
        String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
        this.setBasePermission(this.getText(Permission.PROJECT_EXPECTED_STUDIES_BASE_PERMISSION, params));
      } else {
        String params[] = {loggedCrp.getAcronym(), expectedStudy.getId() + ""};
        this.setBasePermission(this.getText(Permission.STUDIES_BASE_PERMISSION, params));
      }
    }
    if (this.isHttpPost()) {

      // HTTP Post List
      if (expectedStudy.getCrps() != null) {
        expectedStudy.getCrps().clear();
      }

      if (expectedStudy.getSubIdos() != null) {
        expectedStudy.getSubIdos().clear();
      }

      if (expectedStudy.getFlagships() != null) {
        expectedStudy.getFlagships().clear();
      }

      if (expectedStudy.getRegions() != null) {
        expectedStudy.getRegions().clear();
      }

      if (expectedStudy.getInstitutions() != null) {
        expectedStudy.getInstitutions().clear();
      }

      if (expectedStudy.getSrfTargets() != null) {
        expectedStudy.getSrfTargets().clear();
      }

      if (expectedStudy.getProjects() != null) {
        expectedStudy.getProjects().clear();
      }

      // HTTP Post info Values
      expectedStudy.getProjectExpectedStudyInfo().setRepIndGeographicScope(null);
      expectedStudy.getProjectExpectedStudyInfo().setRepIndRegion(null);
      expectedStudy.getProjectExpectedStudyInfo().setRepIndOrganizationType(null);
      expectedStudy.getProjectExpectedStudyInfo().setRepIndPolicyInvestimentType(null);
      expectedStudy.getProjectExpectedStudyInfo().setRepIndStageProcess(null);
      expectedStudy.getProjectExpectedStudyInfo().setRepIndStageStudy(null);
      expectedStudy.getProjectExpectedStudyInfo().setStudyType(null);
      expectedStudy.getProjectExpectedStudyInfo().setStatus(null);


      expectedStudy.getProjectExpectedStudyInfo().setGenderLevel(null);
      expectedStudy.getProjectExpectedStudyInfo().setCapdevLevel(null);
      expectedStudy.getProjectExpectedStudyInfo().setYouthLevel(null);
      expectedStudy.getProjectExpectedStudyInfo().setRepIndStageStudy(null);

      expectedStudy.getProjectExpectedStudyInfo().setOutcomeFile(null);
      expectedStudy.getProjectExpectedStudyInfo().setReferencesFile(null);

    }


  }

  @Override
  public String save() {

    User user = this.getCurrentUser();

    if (this.hasPermission("canEdit") || user.getId().equals(expectedStudyDB.getCreatedBy().getId())) {

      Phase phase = this.getActualPhase();
      Path path = this.getAutoSaveFilePath();

      expectedStudy.setProject(project);

      this.saveCrps(expectedStudyDB, phase);
      this.saveFlagships(expectedStudyDB, phase);
      this.saveRegions(expectedStudyDB, phase);
      this.saveProjects(expectedStudyDB, phase);
      this.saveSubIdos(expectedStudyDB, phase);
      this.saveInstitutions(expectedStudyDB, phase);
      this.saveSrfTargets(expectedStudyDB, phase);


      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_PROJECTS_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_SUBIDOS_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_FLAGSHIP_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_CRP_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_INSTITUTION_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_COUNTRY_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_SRF_TARGET_RELATION);

      expectedStudy.setModificationJustification(this.getJustification());


      // Save the Countries List (ProjectExpectedStudyCountry)
      if (expectedStudy.getCountriesIds() != null || !expectedStudy.getCountriesIds().isEmpty()) {

        List<ProjectExpectedStudyCountry> countries = projectExpectedStudyCountryManager
          .getProjectExpectedStudyCountrybyPhase(expectedStudy.getId(), this.getActualPhase().getId());
        List<ProjectExpectedStudyCountry> countriesSave = new ArrayList<>();
        for (String countryIds : expectedStudy.getCountriesIds()) {
          ProjectExpectedStudyCountry countryInn = new ProjectExpectedStudyCountry();
          countryInn.setLocElement(locElementManager.getLocElementByISOCode(countryIds));
          countryInn.setProjectExpectedStudy(expectedStudy);
          countryInn.setPhase(this.getActualPhase());
          countriesSave.add(countryInn);
          if (!countries.contains(countryInn)) {
            projectExpectedStudyCountryManager.saveProjectExpectedStudyCountry(countryInn);
          }
        }

        for (ProjectExpectedStudyCountry projectExpectedStudyCountry : countries) {
          if (!countriesSave.contains(projectExpectedStudyCountry)) {
            projectExpectedStudyCountryManager.deleteProjectExpectedStudyCountry(projectExpectedStudyCountry.getId());
          }
        }

      }


      expectedStudy.getProjectExpectedStudyInfo().setPhase(this.getActualPhase());
      expectedStudy.getProjectExpectedStudyInfo().setProjectExpectedStudy(expectedStudyDB);

      // Save Files
      if (expectedStudy.getProjectExpectedStudyInfo().getOutcomeFile() != null) {
        if (expectedStudy.getProjectExpectedStudyInfo().getOutcomeFile().getId() == null) {
          expectedStudy.getProjectExpectedStudyInfo().setOutcomeFile(null);
        }
      } else {
        expectedStudy.getProjectExpectedStudyInfo()
          .setOutcomeFile(expectedStudy.getProjectExpectedStudyInfo().getOutcomeFile());
      }

      if (expectedStudy.getProjectExpectedStudyInfo().getReferencesFile() != null) {
        if (expectedStudy.getProjectExpectedStudyInfo().getReferencesFile().getId() == null) {
          expectedStudy.getProjectExpectedStudyInfo().setReferencesFile(null);
        }
      } else {
        expectedStudy.getProjectExpectedStudyInfo()
          .setReferencesFile(expectedStudy.getProjectExpectedStudyInfo().getReferencesFile());
      }

      // Setup focusLevel
      if (expectedStudy.getProjectExpectedStudyInfo().getGenderLevel() != null) {
        RepIndGenderYouthFocusLevel focusLevel = focusLevelManager
          .getRepIndGenderYouthFocusLevelById(expectedStudy.getProjectExpectedStudyInfo().getGenderLevel().getId());
        expectedStudy.getProjectExpectedStudyInfo().setGenderLevel(focusLevel);
      }

      if (expectedStudy.getProjectExpectedStudyInfo().getCapdevLevel() != null) {
        RepIndGenderYouthFocusLevel focusLevel = focusLevelManager
          .getRepIndGenderYouthFocusLevelById(expectedStudy.getProjectExpectedStudyInfo().getCapdevLevel().getId());
        expectedStudy.getProjectExpectedStudyInfo().setCapdevLevel(focusLevel);
      }

      if (expectedStudy.getProjectExpectedStudyInfo().getYouthLevel() != null) {
        RepIndGenderYouthFocusLevel focusLevel = focusLevelManager
          .getRepIndGenderYouthFocusLevelById(expectedStudy.getProjectExpectedStudyInfo().getYouthLevel().getId());
        expectedStudy.getProjectExpectedStudyInfo().setYouthLevel(focusLevel);
      }

      if (expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy() != null) {
        RepIndStageStudy stageStudy = stageStudyManager
          .getRepIndStageStudyById(expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId());
        expectedStudy.getProjectExpectedStudyInfo().setRepIndStageStudy(stageStudy);
      }

      // Validate negative Values
      if (expectedStudy.getProjectExpectedStudyInfo().getRepIndGeographicScope() != null) {
        if (expectedStudy.getProjectExpectedStudyInfo().getRepIndGeographicScope().getId() == -1) {
          expectedStudy.getProjectExpectedStudyInfo().setRepIndGeographicScope(null);
        }
      }

      if (expectedStudy.getProjectExpectedStudyInfo().getRepIndRegion() != null) {
        if (expectedStudy.getProjectExpectedStudyInfo().getRepIndRegion().getId() == -1) {
          expectedStudy.getProjectExpectedStudyInfo().setRepIndRegion(null);
        }
      }

      if (expectedStudy.getProjectExpectedStudyInfo().getRepIndOrganizationType() != null) {
        if (expectedStudy.getProjectExpectedStudyInfo().getRepIndOrganizationType().getId() == -1) {
          expectedStudy.getProjectExpectedStudyInfo().setRepIndOrganizationType(null);
        }
      }

      if (expectedStudy.getProjectExpectedStudyInfo().getRepIndPolicyInvestimentType() != null) {
        if (expectedStudy.getProjectExpectedStudyInfo().getRepIndPolicyInvestimentType().getId() == -1) {
          expectedStudy.getProjectExpectedStudyInfo().setRepIndPolicyInvestimentType(null);
        }
      }

      if (expectedStudy.getProjectExpectedStudyInfo().getRepIndStageProcess() != null) {
        if (expectedStudy.getProjectExpectedStudyInfo().getRepIndStageProcess().getId() == -1) {
          expectedStudy.getProjectExpectedStudyInfo().setRepIndStageProcess(null);
        }
      }

      if (expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy() != null) {
        if (expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId() == -1) {
          expectedStudy.getProjectExpectedStudyInfo().setRepIndStageStudy(null);
        }
      }

      if (expectedStudy.getProjectExpectedStudyInfo().getStudyType() != null) {
        if (expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId() == -1) {
          expectedStudy.getProjectExpectedStudyInfo().setStudyType(null);
        }
      }

      if (expectedStudy.getProjectExpectedStudyInfo().getStatus() != null) {
        if (expectedStudy.getProjectExpectedStudyInfo().getStatus() == -1) {
          expectedStudy.getProjectExpectedStudyInfo().setStatus(null);
        }
      }
      // End

      projectExpectedStudyInfoManager.saveProjectExpectedStudyInfo(expectedStudy.getProjectExpectedStudyInfo());
      /**
       * The following is required because we need to update something on the @ProjectExpectedStudy if we want a row
       * created in the auditlog table.
       */
      this.setModificationJustification(expectedStudy);
      projectExpectedStudyManager.save(expectedStudy, this.getActionName(), relationsName, this.getActualPhase());

      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (this.getInvalidFields() == null) {
          this.setInvalidFields(new HashMap<>());
        }
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
   * Save Expected Studies Crps Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveCrps(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudyCrps() != null
      && projectExpectedStudy.getProjectExpectedStudyCrps().size() > 0) {
      List<ProjectExpectedStudyCrp> crpPrev = new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyCrps()
        .stream().filter(nu -> nu.isActive() && nu.getPhase().getId() == phase.getId()).collect(Collectors.toList()));

      for (ProjectExpectedStudyCrp studyCrp : crpPrev) {
        if (expectedStudy.getCrps() == null || !expectedStudy.getCrps().contains(studyCrp)) {
          projectExpectedStudyCrpManager.deleteProjectExpectedStudyCrp(studyCrp.getId());
        }
      }
    }

    // Save form Information
    if (expectedStudy.getCrps() != null) {
      for (ProjectExpectedStudyCrp studyCrp : expectedStudy.getCrps()) {
        if (studyCrp.getId() == null) {
          ProjectExpectedStudyCrp studyCrpSave = new ProjectExpectedStudyCrp();
          studyCrpSave.setProjectExpectedStudy(projectExpectedStudy);
          studyCrpSave.setPhase(phase);

          GlobalUnit globalUnit = crpManager.getGlobalUnitById(studyCrp.getGlobalUnit().getId());

          studyCrpSave.setGlobalUnit(globalUnit);

          projectExpectedStudyCrpManager.saveProjectExpectedStudyCrp(studyCrpSave);
        }
      }
    }

  }

  /**
   * Save Expected Studies Flagships Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveFlagships(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudyFlagships() != null
      && projectExpectedStudy.getProjectExpectedStudyFlagships().size() > 0) {

      List<ProjectExpectedStudyFlagship> flagshipPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId() == phase.getId()
            && nu.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList()));

      for (ProjectExpectedStudyFlagship studyFlagship : flagshipPrev) {
        if (expectedStudy.getFlagships() == null || !expectedStudy.getFlagships().contains(studyFlagship)) {
          projectExpectedStudyFlagshipManager.deleteProjectExpectedStudyFlagship(studyFlagship.getId());
        }
      }
    }

    // Save form Information
    if (expectedStudy.getFlagships() != null) {
      for (ProjectExpectedStudyFlagship studyFlagship : expectedStudy.getFlagships()) {
        if (studyFlagship.getId() == null) {
          ProjectExpectedStudyFlagship studyFlagshipSave = new ProjectExpectedStudyFlagship();
          studyFlagshipSave.setProjectExpectedStudy(projectExpectedStudy);
          studyFlagshipSave.setPhase(phase);

          CrpProgram crpProgram = crpProgramManager.getCrpProgramById(studyFlagship.getCrpProgram().getId());

          studyFlagshipSave.setCrpProgram(crpProgram);

          projectExpectedStudyFlagshipManager.saveProjectExpectedStudyFlagship(studyFlagshipSave);
        }
      }
    }

  }

  /**
   * Save Expected Studies Institutions Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveInstitutions(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudyInstitutions() != null
      && projectExpectedStudy.getProjectExpectedStudyInstitutions().size() > 0) {

      List<ProjectExpectedStudyInstitution> institutionPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyInstitutions().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId() == phase.getId()).collect(Collectors.toList()));

      for (ProjectExpectedStudyInstitution studyInstitution : institutionPrev) {
        if (expectedStudy.getInstitutions() == null || !expectedStudy.getInstitutions().contains(studyInstitution)) {
          projectExpectedStudyInstitutionManager.deleteProjectExpectedStudyInstitution(studyInstitution.getId());
        }
      }
    }

    // Save form Information
    if (expectedStudy.getInstitutions() != null) {
      for (ProjectExpectedStudyInstitution studyInstitution : expectedStudy.getInstitutions()) {
        if (studyInstitution.getId() == null) {
          ProjectExpectedStudyInstitution studyInstitutionSave = new ProjectExpectedStudyInstitution();
          studyInstitutionSave.setProjectExpectedStudy(projectExpectedStudy);
          studyInstitutionSave.setPhase(phase);

          Institution institution = institutionManager.getInstitutionById(studyInstitution.getInstitution().getId());

          studyInstitutionSave.setInstitution(institution);

          projectExpectedStudyInstitutionManager.saveProjectExpectedStudyInstitution(studyInstitutionSave);
        }
      }
    }

  }

  /**
   * Save Expected Studies Projects Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveProjects(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getExpectedStudyProjects() != null
      && projectExpectedStudy.getExpectedStudyProjects().size() > 0) {

      List<ExpectedStudyProject> projectPrev = new ArrayList<>(projectExpectedStudy.getExpectedStudyProjects().stream()
        .filter(nu -> nu.isActive() && nu.getPhase().getId() == phase.getId()).collect(Collectors.toList()));

      for (ExpectedStudyProject studyProject : projectPrev) {
        if (expectedStudy.getProjects() == null || !expectedStudy.getProjects().contains(studyProject)) {
          expectedStudyProjectManager.deleteExpectedStudyProject(studyProject.getId());
        }
      }
    }

    // Save form Information
    if (expectedStudy.getProjects() != null) {
      for (ExpectedStudyProject studyProject : expectedStudy.getProjects()) {
        if (studyProject.getId() == null) {
          ExpectedStudyProject studyProjectSave = new ExpectedStudyProject();
          studyProjectSave.setProjectExpectedStudy(projectExpectedStudy);
          studyProjectSave.setPhase(phase);

          Project project = projectManager.getProjectById(studyProject.getProject().getId());

          studyProjectSave.setProject(project);

          expectedStudyProjectManager.saveExpectedStudyProject(studyProjectSave);
        }
      }
    }

  }

  /**
   * Save Expected Studies Regions Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveRegions(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudyFlagships() != null
      && projectExpectedStudy.getProjectExpectedStudyFlagships().size() > 0) {

      List<ProjectExpectedStudyFlagship> flagshipPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId() == phase.getId()
            && nu.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList()));

      for (ProjectExpectedStudyFlagship studyFlagship : flagshipPrev) {
        if (expectedStudy.getRegions() == null || !expectedStudy.getRegions().contains(studyFlagship)) {
          projectExpectedStudyFlagshipManager.deleteProjectExpectedStudyFlagship(studyFlagship.getId());
        }
      }
    }

    // Save form Information
    if (expectedStudy.getRegions() != null) {
      for (ProjectExpectedStudyFlagship studyFlagship : expectedStudy.getRegions()) {
        if (studyFlagship.getId() == null) {
          ProjectExpectedStudyFlagship studyFlagshipSave = new ProjectExpectedStudyFlagship();
          studyFlagshipSave.setProjectExpectedStudy(projectExpectedStudy);
          studyFlagshipSave.setPhase(phase);

          CrpProgram crpProgram = crpProgramManager.getCrpProgramById(studyFlagship.getCrpProgram().getId());

          studyFlagshipSave.setCrpProgram(crpProgram);

          projectExpectedStudyFlagshipManager.saveProjectExpectedStudyFlagship(studyFlagshipSave);
        }
      }
    }

  }

  /**
   * Save Expected Studies Srf Targets Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveSrfTargets(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudySrfTargets() != null
      && projectExpectedStudy.getProjectExpectedStudySrfTargets().size() > 0) {

      List<ProjectExpectedStudySrfTarget> targetPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySrfTargets().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId() == phase.getId()).collect(Collectors.toList()));

      for (ProjectExpectedStudySrfTarget studytarget : targetPrev) {
        if (expectedStudy.getSrfTargets() == null || !expectedStudy.getSrfTargets().contains(studytarget)) {
          projectExpectedStudySrfTargetManager.deleteProjectExpectedStudySrfTarget(studytarget.getId());
        }
      }
    }

    // Save form Information
    if (expectedStudy.getSrfTargets() != null) {
      for (ProjectExpectedStudySrfTarget studytarget : expectedStudy.getSrfTargets()) {
        if (studytarget.getId() == null) {
          ProjectExpectedStudySrfTarget studytargetSave = new ProjectExpectedStudySrfTarget();
          studytargetSave.setProjectExpectedStudy(projectExpectedStudy);
          studytargetSave.setPhase(phase);

          SrfSloIndicator sloIndicator =
            srfSloIndicatorManager.getSrfSloIndicatorById(studytarget.getSrfSloIndicator().getId());

          studytargetSave.setSrfSloIndicator(sloIndicator);

          projectExpectedStudySrfTargetManager.saveProjectExpectedStudySrfTarget(studytargetSave);
        }
      }
    }

  }

  /**
   * Save Expected Studies SubIdos Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveSubIdos(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudySubIdos() != null
      && projectExpectedStudy.getProjectExpectedStudySubIdos().size() > 0) {

      List<ProjectExpectedStudySubIdo> subIdoPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySubIdos().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId() == phase.getId()).collect(Collectors.toList()));

      for (ProjectExpectedStudySubIdo studySubIdo : subIdoPrev) {
        if (expectedStudy.getSubIdos() == null || !expectedStudy.getSubIdos().contains(studySubIdo)) {
          projectExpectedStudySubIdoManager.deleteProjectExpectedStudySubIdo(studySubIdo.getId());
        }
      }
    }

    // Save form Information
    if (expectedStudy.getSubIdos() != null) {
      for (ProjectExpectedStudySubIdo studySubIdo : expectedStudy.getSubIdos()) {
        if (studySubIdo.getId() == null) {
          ProjectExpectedStudySubIdo studySubIdoSave = new ProjectExpectedStudySubIdo();
          studySubIdoSave.setProjectExpectedStudy(projectExpectedStudy);
          studySubIdoSave.setPhase(phase);

          SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoById(studySubIdo.getSrfSubIdo().getId());

          studySubIdoSave.setSrfSubIdo(srfSubIdo);

          projectExpectedStudySubIdoManager.saveProjectExpectedStudySubIdo(studySubIdoSave);
        }
      }
    }

  }


  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
  }


  public void setCrps(List<GlobalUnit> crps) {
    this.crps = crps;
  }


  public void setExpectedID(long expectedID) {
    this.expectedID = expectedID;
  }

  public void setExpectedStudy(ProjectExpectedStudy expectedStudy) {
    this.expectedStudy = expectedStudy;
  }

  public void setFlagshipList(List<CrpProgram> flagshipList) {
    this.flagshipList = flagshipList;
  }

  public void setFocusLevels(List<RepIndGenderYouthFocusLevel> focusLevels) {
    this.focusLevels = focusLevels;
  }


  public void setGeographicScopes(List<RepIndGeographicScope> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }

  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMyProjects(List<Project> myProjects) {
    this.myProjects = myProjects;
  }

  public void setOrganizationTypes(List<RepIndOrganizationType> organizationTypes) {
    this.organizationTypes = organizationTypes;
  }

  public void setPolicyInvestimentTypes(List<RepIndPolicyInvestimentType> policyInvestimentTypes) {
    this.policyInvestimentTypes = policyInvestimentTypes;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setRegionList(List<CrpProgram> regionList) {
    this.regionList = regionList;
  }


  public void setRegions(List<RepIndRegion> regions) {
    this.regions = regions;
  }


  public void setStageProcesses(List<RepIndStageProcess> stageProcesses) {
    this.stageProcesses = stageProcesses;
  }


  public void setStageStudies(List<RepIndStageStudy> stageStudies) {
    this.stageStudies = stageStudies;
  }


  public void setStatuses(Map<Integer, String> statuses) {
    this.statuses = statuses;
  }


  public void setStudyTypes(List<StudyType> studyTypes) {
    this.studyTypes = studyTypes;
  }


  public void setSubIdos(List<SrfSubIdo> subIdos) {
    this.subIdos = subIdos;
  }

  public void setTargets(List<SrfSloIndicator> targets) {
    this.targets = targets;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      projectExpectedStudiesValidator.validate(this, project, expectedStudy, true);
    }
  }

}
