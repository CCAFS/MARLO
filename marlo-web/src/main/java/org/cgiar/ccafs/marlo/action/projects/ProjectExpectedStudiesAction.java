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
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.EvidenceTagManager;
import org.cgiar.ccafs.marlo.data.manager.ExpectedStudyProjectManager;
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCenterManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyFlagshipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyLinkManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyQuantificationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySrfTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
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
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.EvidenceTag;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLink;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyQuantification;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationShared;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyInvestimentType;
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
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

  // Managers
  private ProjectExpectedStudyManager projectExpectedStudyManager;

  private AuditLogManager auditLogManager;

  private GlobalUnitManager crpManager;
  private ProjectManager projectManager;
  private PhaseManager phaseManager;
  private SrfSloIndicatorManager srfSloIndicatorManager;
  private SrfSubIdoManager srfSubIdoManager;
  private CrpProgramManager crpProgramManager;
  private InstitutionManager institutionManager;
  private LocElementManager locElementManager;
  private StudyTypeManager studyTypeManager;
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
  private ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager;
  private ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager;
  private GeneralStatusManager generalStatusManager;
  private CrpMilestoneManager milestoneManager;

  // AR 2018 Managers
  private EvidenceTagManager evidenceTagManager;
  private ProjectExpectedStudyLinkManager projectExpectedStudyLinkManager;
  private ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager;
  private ProjectExpectedStudyQuantificationManager projectExpectedStudyQuantificationManager;
  private ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager;
  private ProjectInnovationManager projectInnovationManager;
  private ProjectPolicyManager projectPolicyManager;

  // AR 2019 Managers
  private ProjectExpectedStudyCenterManager projectExpectedStudyCenterManager;
  private ProjectExpectedStudyMilestoneManager projectExpectedStudyMilestoneManager;

  // Variables
  private ProjectExpectedStudiesValidator projectExpectedStudiesValidator;
  private GlobalUnit loggedCrp;

  private Project project;
  private long projectID;
  private long expectedID;
  private long subIdoPrimaryId;
  private long srfSubIdoPrimary;
  private long milestonePrimaryId;
  private long crpMilestonePrimary;
  private ProjectExpectedStudy expectedStudy;
  private ProjectExpectedStudy expectedStudyDB;
  private List<GeneralStatus> statuses;
  private List<RepIndGeographicScope> geographicScopes;
  private List<LocElement> regions;
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
  private String transaction;

  // AR 2018 Sel-List
  private List<EvidenceTag> tags;
  private List<ProjectPolicy> policyList;
  private List<ProjectInnovation> innovationsList;
  // AR 2019 Sel-List
  private List<Institution> centers;
  private List<CrpMilestone> milestones;
  private int newExpectedYear;

  @Inject
  public ProjectExpectedStudiesAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, SrfSloIndicatorManager srfSloIndicatorManager,
    SrfSubIdoManager srfSubIdoManager, AuditLogManager auditLogManager,
    ExpectedStudyProjectManager expectedStudyProjectManager,
    ProjectExpectedStudiesValidator projectExpectedStudiesValidator, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, InstitutionManager institutionManager, LocElementManager locElementManager,
    StudyTypeManager studyTypeManager, RepIndGeographicScopeManager geographicScopeManager,
    RepIndRegionManager repIndRegionManager, RepIndOrganizationTypeManager organizationTypeManager,
    RepIndGenderYouthFocusLevelManager focusLevelManager, RepIndPolicyInvestimentTypeManager investimentTypeManager,
    RepIndStageProcessManager stageProcessManager, RepIndStageStudyManager stageStudyManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager,
    ProjectExpectedStudySubIdoManager projectExpectedStudySubIdoManager,
    ProjectExpectedStudyFlagshipManager projectExpectedStudyFlagshipManager,
    ProjectExpectedStudyCrpManager projectExpectedStudyCrpManager,
    ProjectExpectedStudyInstitutionManager projectExpectedStudyInstitutionManager,
    ProjectExpectedStudySrfTargetManager projectExpectedStudySrfTargetManager,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager,
    ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager, GeneralStatusManager generalStatusManager,
    EvidenceTagManager evidenceTagManager, ProjectExpectedStudyLinkManager projectExpectedStudyLinkManager,
    ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager,
    ProjectExpectedStudyQuantificationManager projectExpectedStudyQuantificationManager,
    ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager,
    ProjectInnovationManager projectInnovationManager, ProjectPolicyManager projectPolicyManager,
    ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager,
    ProjectExpectedStudyCenterManager projectExpectedStudyCenterManager, CrpMilestoneManager milestoneManager,
    ProjectExpectedStudyMilestoneManager projectExpectedStudyMilestoneManager) {
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
    this.projectExpectedStudyRegionManager = projectExpectedStudyRegionManager;

    this.generalStatusManager = generalStatusManager;

    this.evidenceTagManager = evidenceTagManager;
    this.projectExpectedStudyLinkManager = projectExpectedStudyLinkManager;
    this.projectExpectedStudyPolicyManager = projectExpectedStudyPolicyManager;
    this.projectExpectedStudyQuantificationManager = projectExpectedStudyQuantificationManager;
    this.projectExpectedStudyInnovationManager = projectExpectedStudyInnovationManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectPolicyManager = projectPolicyManager;
    this.projectExpectedStudyGeographicScopeManager = projectExpectedStudyGeographicScopeManager;

    this.projectExpectedStudyCenterManager = projectExpectedStudyCenterManager;
    this.milestoneManager = milestoneManager;
    this.projectExpectedStudyMilestoneManager = projectExpectedStudyMilestoneManager;
  }

  /**
   * Delete all LocElements Records when Geographic Scope is Global or NULL
   * 
   * @param policy
   * @param phase
   */
  public void deleteLocElements(ProjectExpectedStudy study, Phase phase, boolean isCountry) {
    if (isCountry) {
      if (this.expectedStudy.getProjectExpectedStudyCountries() != null
        && this.expectedStudy.getProjectExpectedStudyCountries().size() > 0) {

        List<ProjectExpectedStudyCountry> regionPrev =
          new ArrayList<>(this.expectedStudy.getProjectExpectedStudyCountries().stream()
            .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (ProjectExpectedStudyCountry region : regionPrev) {
          this.projectExpectedStudyCountryManager.deleteProjectExpectedStudyCountry(region.getId());
        }
      }
    } else {
      if (this.expectedStudy.getProjectExpectedStudyRegions() != null
        && this.expectedStudy.getProjectExpectedStudyRegions().size() > 0) {

        List<ProjectExpectedStudyRegion> regionPrev =
          new ArrayList<>(this.expectedStudy.getProjectExpectedStudyRegions().stream()
            .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (ProjectExpectedStudyRegion policyRegion : regionPrev) {

          this.projectExpectedStudyRegionManager.deleteProjectExpectedStudyRegion(policyRegion.getId());

        }

      }
    }
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = this.expectedStudy.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = this.expectedStudy.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(this.config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<Institution> getCenters() {
    return centers;
  }

  public List<LocElement> getCountries() {
    return this.countries;
  }

  public long getCrpMilestonePrimary() {
    return crpMilestonePrimary;
  }

  public List<GlobalUnit> getCrps() {
    return this.crps;
  }

  public long getExpectedID() {
    return this.expectedID;
  }

  public ProjectExpectedStudy getExpectedStudy() {
    return this.expectedStudy;
  }

  public List<CrpProgram> getFlagshipList() {
    return this.flagshipList;
  }

  public List<RepIndGenderYouthFocusLevel> getFocusLevels() {
    return this.focusLevels;
  }

  public List<RepIndGeographicScope> getGeographicScopes() {
    return this.geographicScopes;
  }

  public List<ProjectInnovation> getInnovationsList() {
    return this.innovationsList;
  }

  public List<Institution> getInstitutions() {
    return this.institutions;
  }

  public GlobalUnit getLoggedCrp() {
    return this.loggedCrp;
  }

  public long getMilestonePrimaryId() {
    return milestonePrimaryId;
  }

  public List<CrpMilestone> getMilestones() {
    return milestones;
  }

  public List<Project> getMyProjects() {
    return this.myProjects;
  }

  public int getNewExpectedYear() {
    return newExpectedYear;
  }

  public List<RepIndOrganizationType> getOrganizationTypes() {
    return this.organizationTypes;
  }

  public String getPath() {
    return this.config.getDownloadURL() + "/" + this.getStudiesSourceFolder().replace('\\', '/');
  }

  public List<RepIndPolicyInvestimentType> getPolicyInvestimentTypes() {
    return this.policyInvestimentTypes;
  }

  public List<ProjectPolicy> getPolicyList() {
    return this.policyList;
  }

  public Project getProject() {
    return this.project;
  }

  public long getProjectID() {
    return this.projectID;
  }

  public List<CrpProgram> getRegionList() {
    return this.regionList;
  }

  public List<LocElement> getRegions() {
    return this.regions;
  }

  public long getSrfSubIdoPrimary() {
    return srfSubIdoPrimary;
  }

  public List<RepIndStageProcess> getStageProcesses() {
    return this.stageProcesses;
  }

  public List<RepIndStageStudy> getStageStudies() {
    return this.stageStudies;
  }

  public List<GeneralStatus> getStatuses() {
    return this.statuses;
  }

  private String getStudiesSourceFolder() {
    return APConstants.STUDIES_FOLDER.concat(File.separator).concat(this.getCrpSession()).concat(File.separator)
      .concat(File.separator).concat(this.getCrpSession() + "_")
      .concat(ProjectSectionStatusEnum.EXPECTEDSTUDY.getStatus()).concat(File.separator);
  }

  public List<StudyType> getStudyTypes() {
    return this.studyTypes;
  }

  public long getSubIdoPrimaryId() {
    return subIdoPrimaryId;
  }

  public List<SrfSubIdo> getSubIdos() {
    return this.subIdos;
  }

  public List<EvidenceTag> getTags() {
    return this.tags;
  }

  public List<SrfSloIndicator> getTargets() {
    return this.targets;
  }

  public String getTransaction() {
    return this.transaction;
  }

  @Override
  public void prepare() throws Exception {

    this.loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    this.loggedCrp = this.crpManager.getGlobalUnitById(this.loggedCrp.getId());
    this.setPhaseID(this.getActualPhase().getId());

    this.expectedID =
      Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.EXPECTED_REQUEST_ID)));

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      this.transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      ProjectExpectedStudy history = (ProjectExpectedStudy) this.auditLogManager.getHistory(this.transaction);

      if (history != null) {
        this.expectedStudy = history;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }
      if (this.expectedStudy.getProjectExpectedStudyInfo() == null) {
        this.expectedStudy.getProjectExpectedStudyInfo(this.getActualPhase());
      }
      // Load ExpectedStudyInfo relations
      if (this.expectedStudy.getProjectExpectedStudyInfo() != null) {

        // Load StudyType
        if (this.expectedStudy.getProjectExpectedStudyInfo().getStudyType() != null
          && this.expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId() != null) {
          this.expectedStudy.getProjectExpectedStudyInfo().setStudyType(this.studyTypeManager
            .getStudyTypeById(this.expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId()));
        }

        // Load OrganizationType
        if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndOrganizationType() != null
          && this.expectedStudy.getProjectExpectedStudyInfo().getRepIndOrganizationType().getId() != null) {
          this.expectedStudy.getProjectExpectedStudyInfo()
            .setRepIndOrganizationType(this.organizationTypeManager.getRepIndOrganizationTypeById(
              this.expectedStudy.getProjectExpectedStudyInfo().getRepIndOrganizationType().getId()));
        }

        // Load OrganizationType
        if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageProcess() != null
          && this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageProcess().getId() != null) {
          this.expectedStudy.getProjectExpectedStudyInfo()
            .setRepIndStageProcess(this.stageProcessManager.getRepIndStageProcessById(
              this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageProcess().getId()));
        }

        // Load StageStudy
        if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy() != null
          && this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId() != null) {
          this.expectedStudy.getProjectExpectedStudyInfo().setRepIndStageStudy(this.stageStudyManager
            .getRepIndStageStudyById(this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId()));
        }

        // Load Status
        if (this.expectedStudy.getProjectExpectedStudyInfo().getStatus() != null
          && this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId() != null) {
          this.expectedStudy.getProjectExpectedStudyInfo().setStatus(this.generalStatusManager
            .getGeneralStatusById(this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId()));
        }

        // REMOVED FOR AR 2020
        // Load evidence Tags
        /*
         * if (this.expectedStudy.getProjectExpectedStudyInfo().getEvidenceTag() != null
         * && this.expectedStudy.getProjectExpectedStudyInfo().getEvidenceTag().getId() != null) {
         * this.expectedStudy.getProjectExpectedStudyInfo().setEvidenceTag(this.evidenceTagManager
         * .getEvidenceTagById(this.expectedStudy.getProjectExpectedStudyInfo().getEvidenceTag().getId()));
         * }
         */

        // Load new Expected Year
        if (this.expectedStudy.getProjectExpectedStudyInfo().getStatus() != null
          && this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId() != null
          && this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId() == 4
          && this.expectedStudy.getProjectExpectedStudyInfo().getYear() != 0) {
          newExpectedYear = this.expectedStudy.getProjectExpectedStudyInfo().getYear();
        }

      }
    } else {
      this.expectedStudy = this.projectExpectedStudyManager.getProjectExpectedStudyById(this.expectedID);
    }

    if (this.expectedStudy != null) {

      Phase phase = this.phaseManager.getPhaseById(this.getActualPhase().getId());

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();

        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();

        AutoSaveReader autoSaveReader = new AutoSaveReader();
        this.expectedStudy = (ProjectExpectedStudy) autoSaveReader.readFromJson(jReader);

        // Policy Geographic Scope List AutoSave
        boolean haveRegions = false;
        boolean haveCountries = false;

        if (this.expectedStudy != null && this.expectedStudy.getGeographicScopes() != null
          && !this.expectedStudy.getGeographicScopes().isEmpty()
          && this.expectedStudy.getGeographicScopes().size() > 0) {
          for (ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope : this.expectedStudy
            .getGeographicScopes()) {
            if (projectExpectedStudyGeographicScope.getRepIndGeographicScope() != null) {

              projectExpectedStudyGeographicScope.setRepIndGeographicScope(this.geographicScopeManager
                .getRepIndGeographicScopeById(projectExpectedStudyGeographicScope.getRepIndGeographicScope().getId()));

              if (projectExpectedStudyGeographicScope.getRepIndGeographicScope().getId() == 2) {
                haveRegions = true;
              }
              if (projectExpectedStudyGeographicScope.getRepIndGeographicScope().getId() != 1
                && projectExpectedStudyGeographicScope.getRepIndGeographicScope().getId() != 2) {
                haveCountries = true;
              }
            }
          }
        }

        if (haveRegions) {
          // Load Regions
          // Expected Study Geographic Regions List Autosave
          if (this.expectedStudy.getStudyRegions() != null) {
            for (ProjectExpectedStudyRegion projectExpectedStudyRegion : this.expectedStudy.getStudyRegions()) {
              if (projectExpectedStudyRegion != null && projectExpectedStudyRegion.getLocElement() != null
                && projectExpectedStudyRegion.getLocElement().getId() != null) {
                projectExpectedStudyRegion.setLocElement(
                  this.locElementManager.getLocElementById(projectExpectedStudyRegion.getLocElement().getId()));
              }
            }
          }
        }

        if (haveCountries) {
          // Load Countries
          // Expected Study Countries List AutoSave
          if (this.expectedStudy.getCountriesIdsText() != null) {
            String[] countriesText =
              this.expectedStudy.getCountriesIdsText().replace("[", "").replace("]", "").split(",");
            List<String> countries = new ArrayList<>();
            for (String value : Arrays.asList(countriesText)) {
              countries.add(value.trim());
            }
            this.expectedStudy.setCountriesIds(countries);
          }
        }

        // Expected Study SubIdo List Autosave
        if (this.expectedStudy.getSubIdos() != null && !this.expectedStudy.getSubIdos().isEmpty()
          && this.expectedStudy.getSubIdos().size() > 0) {
          for (ProjectExpectedStudySubIdo projectExpectedStudySubIdo : this.expectedStudy.getSubIdos()) {
            if (projectExpectedStudySubIdo != null && projectExpectedStudySubIdo.getSrfSubIdo() != null
              && projectExpectedStudySubIdo.getSrfSubIdo().getId() != null) {
              projectExpectedStudySubIdo.setSrfSubIdo(
                this.srfSubIdoManager.getSrfSubIdoById(projectExpectedStudySubIdo.getSrfSubIdo().getId()));
            }
          }
        }

        // Expected Study Flagship List Autosave
        if (this.expectedStudy.getFlagships() != null && !this.expectedStudy.getFlagships().isEmpty()) {
          for (ProjectExpectedStudyFlagship projectExpectedStudyFlagship : this.expectedStudy.getFlagships()) {
            if (projectExpectedStudyFlagship != null && projectExpectedStudyFlagship.getCrpProgram() != null
              && projectExpectedStudyFlagship.getCrpProgram().getId() != null) {
              projectExpectedStudyFlagship.setCrpProgram(
                this.crpProgramManager.getCrpProgramById(projectExpectedStudyFlagship.getCrpProgram().getId()));
            }
          }
        }

        // Expected Study Regions (Flagships) List Autosave
        if (this.expectedStudy.getRegions() != null && !this.expectedStudy.getRegions().isEmpty()) {
          for (ProjectExpectedStudyFlagship projectExpectedStudyFlagship : this.expectedStudy.getRegions()) {
            if (projectExpectedStudyFlagship != null && projectExpectedStudyFlagship.getCrpProgram() != null
              && projectExpectedStudyFlagship.getCrpProgram().getId() != null) {
              projectExpectedStudyFlagship.setCrpProgram(
                this.crpProgramManager.getCrpProgramById(projectExpectedStudyFlagship.getCrpProgram().getId()));
            }
          }
        }

        // Expected Study Crp List Autosave
        if (this.expectedStudy.getCrps() != null && !this.expectedStudy.getCrps().isEmpty()) {
          for (ProjectExpectedStudyCrp projectExpectedStudyCrp : this.expectedStudy.getCrps()) {
            if (projectExpectedStudyCrp != null && projectExpectedStudyCrp.getGlobalUnit() != null
              && projectExpectedStudyCrp.getGlobalUnit().getId() != null) {
              projectExpectedStudyCrp
                .setGlobalUnit(this.crpManager.getGlobalUnitById(projectExpectedStudyCrp.getGlobalUnit().getId()));
            }
          }
        }

        // Expected Study Center List Autosave
        if (this.expectedStudy.getCenters() != null && !this.expectedStudy.getCenters().isEmpty()) {
          for (ProjectExpectedStudyCenter projectExpectedStudyCenter : this.expectedStudy.getCenters()) {
            if (projectExpectedStudyCenter != null && projectExpectedStudyCenter.getInstitution() != null
              && projectExpectedStudyCenter.getInstitution().getId() != null) {
              projectExpectedStudyCenter.setInstitution(
                this.institutionManager.getInstitutionById(projectExpectedStudyCenter.getInstitution().getId()));
            }
          }
        }

        // Innovation Milestone List Autosave

        if (this.expectedStudy.getMilestones() != null) {
          for (ProjectExpectedStudyMilestone projectExpectedStudyMilestone : this.expectedStudy.getMilestones()) {
            if (projectExpectedStudyMilestone != null && projectExpectedStudyMilestone.getCrpMilestone() != null
              && projectExpectedStudyMilestone.getCrpMilestone().getId() != null) {
              projectExpectedStudyMilestone.setCrpMilestone(
                (milestoneManager.getCrpMilestoneById(projectExpectedStudyMilestone.getCrpMilestone().getId())));
            }
          }
        }

        // Expected Study Institutions List Autosave
        if (this.expectedStudy.getInstitutions() != null) {
          for (ProjectExpectedStudyInstitution projectExpectedStudyInstitution : this.expectedStudy.getInstitutions()) {
            if (projectExpectedStudyInstitution != null && projectExpectedStudyInstitution.getInstitution() != null
              && projectExpectedStudyInstitution.getInstitution().getId() != null) {
              projectExpectedStudyInstitution.setInstitution(
                this.institutionManager.getInstitutionById(projectExpectedStudyInstitution.getInstitution().getId()));
            }
          }
        }

        // Expected Study Srf Target List Autosave
        if (this.expectedStudy.getSrfTargets() != null) {
          for (ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget : this.expectedStudy.getSrfTargets()) {
            if (projectExpectedStudySrfTarget != null && projectExpectedStudySrfTarget.getSrfSloIndicator() != null
              && projectExpectedStudySrfTarget.getSrfSloIndicator().getId() != null) {
              projectExpectedStudySrfTarget.setSrfSloIndicator(this.srfSloIndicatorManager
                .getSrfSloIndicatorById(projectExpectedStudySrfTarget.getSrfSloIndicator().getId()));
            }
          }
        }

        // Expected Study Projects List Autosave
        if (this.expectedStudy.getProjects() != null) {
          for (ExpectedStudyProject expectedStudyProject : this.expectedStudy.getProjects()) {
            if (expectedStudyProject != null && expectedStudyProject.getProject() != null
              && expectedStudyProject.getProject().getId() != null) {
              expectedStudyProject
                .setProject(this.projectManager.getProjectById(expectedStudyProject.getProject().getId()));
            }
          }
        }

        // Expected Study Innovations List Autosave
        if (this.expectedStudy.getInnovations() != null) {
          for (ProjectExpectedStudyInnovation projectExpectedStudyInnovation : this.expectedStudy.getInnovations()) {
            if (projectExpectedStudyInnovation != null && projectExpectedStudyInnovation.getProjectInnovation() != null
              && projectExpectedStudyInnovation.getProjectInnovation().getId() != null) {
              projectExpectedStudyInnovation.setProjectInnovation(this.projectInnovationManager
                .getProjectInnovationById(projectExpectedStudyInnovation.getProjectInnovation().getId()));
            }
          }
        }

        // Expected Study Policies List Autosave
        if (this.expectedStudy.getPolicies() != null) {
          for (ProjectExpectedStudyPolicy projectExpectedStudyPolicy : this.expectedStudy.getPolicies()) {
            if (projectExpectedStudyPolicy != null && projectExpectedStudyPolicy.getProjectPolicy() != null
              && projectExpectedStudyPolicy.getProjectPolicy().getId() != null) {
              projectExpectedStudyPolicy.setProjectPolicy(
                this.projectPolicyManager.getProjectPolicyById(projectExpectedStudyPolicy.getProjectPolicy().getId()));
            }
          }
        }

        // Study Type Autosave
        if (this.expectedStudy.getProjectExpectedStudyInfo().getStudyType() != null) {
          if (this.expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId() != null
            && this.expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId() != -1) {
            StudyType studyType = this.studyTypeManager
              .getStudyTypeById(this.expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId());
            this.expectedStudy.getProjectExpectedStudyInfo().setStudyType(studyType);
          }
        }

        this.setDraft(true);
      } else {
        this.setDraft(false);

        if (this.expectedStudy.getProjectExpectedStudyInfo() == null) {
          this.expectedStudy.getProjectExpectedStudyInfo(phase);
        }

        // Setup Geographic Scope
        if (this.expectedStudy.getProjectExpectedStudyGeographicScopes() != null) {
          this.expectedStudy
            .setGeographicScopes(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyGeographicScopes().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Expected Study Countries List
        if (this.expectedStudy.getProjectExpectedStudyCountries() == null) {
          this.expectedStudy.setCountries(new ArrayList<>());
        } else {
          List<ProjectExpectedStudyCountry> countries = this.projectExpectedStudyCountryManager
            .getProjectExpectedStudyCountrybyPhase(this.expectedStudy.getId(), phase.getId()).stream()
            .filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId() == 2)
            .collect(Collectors.toList());
          this.expectedStudy.setCountries(countries);
        }

        if (this.expectedStudy.getProjectExpectedStudyRegions() == null) {
          this.expectedStudy.setStudyRegions(new ArrayList<>());
        } else {
          List<ProjectExpectedStudyRegion> geographics = this.projectExpectedStudyRegionManager
            .getProjectExpectedStudyRegionbyPhase(this.expectedStudy.getId(), phase.getId());

          // Load Regions
          this.expectedStudy.setStudyRegions(geographics.stream()
            .filter(sc -> sc.getLocElement().getLocElementType().getId() == 1).collect(Collectors.toList()));
        }

        // Expected Study SubIdos List
        if (this.expectedStudy.getProjectExpectedStudySubIdos() != null) {
          this.expectedStudy.setSubIdos(new ArrayList<>(this.expectedStudy.getProjectExpectedStudySubIdos().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));

          // Get the ID of the principal Sub IDO if exist
          if (expectedStudy.getSubIdos(phase) != null) {
            List<ProjectExpectedStudySubIdo> projectPolicies = new ArrayList<ProjectExpectedStudySubIdo>();

            projectPolicies = expectedStudy.getSubIdos(phase).stream()
              .filter(p -> p != null && p.isActive() && p.getPrimary() != null && p.getPrimary())
              .collect(Collectors.toList());

            if (projectPolicies != null && projectPolicies.size() > 0 && projectPolicies.get(0) != null) {
              subIdoPrimaryId = projectPolicies.get(0).getSrfSubIdo().getId(); //
              srfSubIdoPrimary = projectPolicies.get(0).getSrfSubIdo().getId(); //
            }
          }

        }

        // Expected Study Flagship List
        if (this.expectedStudy.getProjectExpectedStudyFlagships() != null) {
          this.expectedStudy.setFlagships(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyFlagships().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())
              && o.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList())));
        }

        // Expected Study Regions List
        if (this.expectedStudy.getProjectExpectedStudyFlagships() != null) {
          this.expectedStudy.setRegions(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyFlagships().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())
              && o.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList())));
        }

        // Expected Study Crp List
        if (this.expectedStudy.getProjectExpectedStudyCrps() != null) {
          this.expectedStudy.setCrps(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyCrps().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Expected Study Center List
        if (this.expectedStudy.getProjectExpectedStudyCenters() != null) {
          this.expectedStudy.setCenters(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyCenters().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Innovation Milestone list
        if (this.expectedStudy.getProjectExpectedStudyMilestones() != null) {
          this.expectedStudy.setMilestones(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyMilestones()
            .stream().filter(o -> o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Expected Study Institutions List
        if (this.expectedStudy.getProjectExpectedStudyInstitutions() != null) {
          this.expectedStudy
            .setInstitutions(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyInstitutions().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Expected Study Srf Target List
        if (this.expectedStudy.getProjectExpectedStudySrfTargets() != null) {
          this.expectedStudy
            .setSrfTargets(new ArrayList<>(this.expectedStudy.getProjectExpectedStudySrfTargets().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Expected Study Projects List
        if (this.expectedStudy.getExpectedStudyProjects() != null) {
          this.expectedStudy.setProjects(new ArrayList<>(this.expectedStudy.getExpectedStudyProjects().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Expected Study Link List
        if (this.expectedStudy.getProjectExpectedStudyLinks() != null) {
          this.expectedStudy.setLinks(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyLinks().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Expected Study Policies List
        if (this.expectedStudy.getProjectExpectedStudyPolicies() != null) {
          this.expectedStudy.setPolicies(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyPolicies().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Expected Study Quantifications List
        if (this.expectedStudy.getProjectExpectedStudyQuantifications() != null) {
          this.expectedStudy
            .setQuantifications(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyQuantifications().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Expected Study Innovations List
        if (this.expectedStudy.getProjectExpectedStudyInnovations() != null) {
          this.expectedStudy
            .setInnovations(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyInnovations().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
          // Get the ID of the principal Sub IDO if exist
          if (expectedStudy.getMilestones() != null) {
            List<ProjectExpectedStudyMilestone> projectPolicies = new ArrayList<ProjectExpectedStudyMilestone>();

            projectPolicies = expectedStudy
              .getMilestones().stream().filter(p -> p != null && p.isActive() && p.getPrimary() != null
                && p.getPrimary() && p.getPhase() != null && p.getPhase().getId().equals(phase.getId()))
              .collect(Collectors.toList());

            if (projectPolicies != null && projectPolicies.size() > 0 && projectPolicies.get(0) != null) {
              milestonePrimaryId = projectPolicies.get(0).getCrpMilestone().getId(); //
              crpMilestonePrimary = projectPolicies.get(0).getCrpMilestone().getId(); //
            }
          }
        }

      }

      if (!this.isDraft()) {
        if (this.expectedStudy.getCountries() != null) {
          for (ProjectExpectedStudyCountry country : this.expectedStudy.getCountries()) {
            this.expectedStudy.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
          }
        }
      }

      // Getting The list
      this.statuses = this.generalStatusManager.findByTable(APConstants.PROJECT_EXPECTED_STUDIES_TABLE);

      this.countries = this.locElementManager.findAll().stream()
        .filter(c -> c.getLocElementType().getId().intValue() == 2 && c.isActive()).collect(Collectors.toList());

      this.geographicScopes = this.geographicScopeManager.findAll();
      this.regions = this.locElementManager.findAll().stream()
        .filter(c -> c.getLocElementType().getId().intValue() == 1 && c.isActive() && c.getIsoNumeric() != null)
        .collect(Collectors.toList());
      this.organizationTypes = this.organizationTypeManager.findAll();
      // Focus levels and Too early to tell was removed
      this.focusLevels = this.focusLevelManager.findAll().stream().collect(Collectors.toList());
      this.policyInvestimentTypes = this.investimentTypeManager.findAll();
      this.stageProcesses = this.stageProcessManager.findAll();
      this.stageStudies = this.stageStudyManager.findAll();
      this.studyTypes = this.studyTypeManager.findAll();
      if (this.expectedStudy.getProjectExpectedStudyInfo() != null
        && this.expectedStudy.getProjectExpectedStudyInfo().getId() != null
        && this.expectedStudy.getProjectExpectedStudyInfo().getId().longValue() != 1) {
        this.studyTypes.removeIf(st -> st.getId() == 1);
      }
      this.subIdos = this.srfSubIdoManager.findAll();
      this.targets = this.srfSloIndicatorManager.findAll();

      // institutions
      Project projectTemp = null;
      if (this.expectedStudy.getProject() != null) {
        projectTemp = projectManager.getProjectById(this.expectedStudy.getProject().getId());
      }
      if (projectTemp == null) {
        // is a sumplementary evidence
        centers = institutionManager.findAll().stream()
          .filter(c -> c.isPPA(this.getActualPhase().getCrp().getId(), this.getActualPhase())
            || c.getInstitutionType().getId().longValue() == APConstants.INSTITUTION_CGIAR_CENTER_TYPE)
          .collect(Collectors.toList());
      } else {
        List<Institution> centersTemp = new ArrayList<Institution>();
        List<ProjectPartner> projectPartnerList = projectTemp.getProjectPartners().stream()
          .filter(c -> c != null && c.isActive() && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList());
        for (ProjectPartner projectPartner : projectPartnerList) {
          if (projectPartner.getInstitution() != null && projectPartner.getInstitution().getId() != null) {
            Institution institution = institutionManager.getInstitutionById(projectPartner.getInstitution().getId());
            if (institution != null && (institution.isPPA(this.getActualPhase().getCrp().getId(), this.getActualPhase())
              || institution.getInstitutionType().getId().longValue() == APConstants.INSTITUTION_CGIAR_CENTER_TYPE)) {
              centersTemp.add(institution);
            }
          }
        }
        centers = centersTemp;
      }


      this.tags = this.evidenceTagManager.findAll();
      this.innovationsList = new ArrayList<>();
      this.policyList = new ArrayList<>();

      // Expected Study Projects List
      if (this.expectedStudy.getExpectedStudyProjects() != null) {
        List<ExpectedStudyProject> expectedStudyProjects =
          new ArrayList<>(this.expectedStudy.getExpectedStudyProjects().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (ExpectedStudyProject expectedStudyProject : expectedStudyProjects) {

          Project sharedProject = expectedStudyProject.getProject();

          List<ProjectInnovation> sharedInnovations =
            sharedProject.getProjectInnovations().stream().filter(c -> c.isActive()).collect(Collectors.toList());
          if (sharedInnovations != null) {
            for (ProjectInnovation projectInnovation : sharedInnovations) {
              if (projectInnovation.getProjectInnovationInfo(this.getActualPhase()) != null) {
                this.innovationsList.add(projectInnovation);
              }
            }
          }

          List<ProjectPolicy> sharedPolicies =
            sharedProject.getProjectPolicies().stream().filter(c -> c.isActive()).collect(Collectors.toList());
          for (ProjectPolicy projectPolicy : sharedPolicies) {
            if (projectPolicy.getProjectPolicyInfo(this.getActualPhase()) != null) {
              this.policyList.add(projectPolicy);
            }
          }

        }
      }

      this.myProjects = new ArrayList<>();
      for (ProjectPhase projectPhase : phase.getProjectPhases()) {
        if (projectPhase.getProject().getProjecInfoPhase(this.getActualPhase()) != null) {
          this.myProjects.add(projectPhase.getProject());
        }

        if (this.project != null) {
          this.myProjects.remove(this.project);
        }
      }

      if (this.myProjects != null && !this.myProjects.isEmpty()) {
        this.myProjects.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
      }

      this.crps = this.crpManager.findAll().stream()
        .filter(gu -> gu.isActive() && (gu.getGlobalUnitType().getId() == 1 || gu.getGlobalUnitType().getId() == 3))
        .collect(Collectors.toList());

      List<ProjectExpectedStudyCrp> tempPcrp = null;
      // Update crp list - Delete the actual crp from the list except if this crp was

      if (expectedStudy.getCrps() != null && expectedStudy.getCrps().stream()
        .filter(x -> x != null && x.getGlobalUnit().getId().equals(this.getCurrentGlobalUnit().getId())) != null) {
        tempPcrp = expectedStudy.getCrps().stream()
          .filter(x -> x != null && x.getGlobalUnit().getId().equals(this.getCurrentGlobalUnit().getId()))
          .collect(Collectors.toList());
      }

      if (tempPcrp != null && tempPcrp.size() == 0 && this.getCurrentGlobalUnit() != null) {
        crps.remove(this.getCurrentGlobalUnit());
      }

      this.flagshipList = this.crpProgramManager.findAll().stream()
        .filter(p -> p.isActive() && p.getCrp() != null && p.getCrp().getId() == this.loggedCrp.getId()
          && p.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());

      this.regionList = this.crpProgramManager.findAll().stream()
        .filter(p -> p.isActive() && p.getCrp() != null && p.getCrp().getId() == this.loggedCrp.getId()
          && p.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());

      this.institutions =
        this.institutionManager.findAll().stream().filter(i -> i.isActive()).collect(Collectors.toList());

      this.expectedStudyDB = this.projectExpectedStudyManager.getProjectExpectedStudyById(this.expectedID);

      if (this.expectedStudyDB.getProject() != null) {
        this.projectID = this.expectedStudyDB.getProject().getId();
        this.project = this.projectManager.getProjectById(this.projectID);
        this.project.getProjecInfoPhase(phase);
      }

      if (this.project != null) {
        Project projectL = this.projectManager.getProjectById(this.projectID);

        // Get the innovations List
        // this.innovationsList = new ArrayList<>();

        /*
         * Get the milestone List
         */
        milestones = new ArrayList<>();

        // Get outcomes list
        List<ProjectOutcome> projectOutcomesList = new ArrayList<>();
        projectOutcomesList = projectL.getProjectOutcomes().stream()
          .filter(
            po -> po.isActive() && po.getPhase() != null && po.getPhase().getId().equals(this.getActualPhase().getId()))
          .collect(Collectors.toList());

        if (projectOutcomesList != null) {

          for (ProjectOutcome projectOutcome : projectOutcomesList) {
            projectOutcome.setMilestones(projectOutcome.getProjectMilestones().stream()
              .filter(
                m -> m != null && m.isActive() && m.getYear() != 0 && m.getYear() <= this.getActualPhase().getYear())
              .collect(Collectors.toList()));

            if (projectOutcome.getMilestones() != null) {
              for (ProjectMilestone projectMilestone : projectOutcome.getMilestones()) {
                if (projectMilestone.getCrpMilestone() != null && projectMilestone.getCrpMilestone().isActive()) {
                  milestones.add(projectMilestone.getCrpMilestone());
                }
              }
            }
          }
        }

        List<ProjectInnovation> innovations =
          projectL.getProjectInnovations().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        for (ProjectInnovation projectInnovation : innovations) {
          if (projectInnovation.getProjectInnovationInfo(this.getActualPhase()) != null) {
            this.innovationsList.add(projectInnovation);
          }
        }

        List<ProjectPolicy> policies =
          projectL.getProjectPolicies().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        for (ProjectPolicy projectPolicy : policies) {
          if (projectPolicy.getProjectPolicyInfo(this.getActualPhase()) != null) {
            this.policyList.add(projectPolicy);
          }
        }

        /*
         * Update 4/25/2019 Adding Shared Project Innovation in the lists.
         */
        List<ProjectInnovationShared> innovationShareds =
          new ArrayList<>(this.project.getProjectInnovationShareds().stream()
            .filter(px -> px.isActive() && px.getPhase().getId() == this.getActualPhase().getId()
              && px.getProjectInnovation().isActive()
              && px.getProjectInnovation().getProjectInnovationInfo(this.getActualPhase()) != null)
            .collect(Collectors.toList()));
        if (innovationShareds != null && innovationShareds.size() > 0) {
          for (ProjectInnovationShared innovationShared : innovationShareds) {
            if (!this.innovationsList.contains(innovationShared.getProjectInnovation())) {
              this.innovationsList.add(innovationShared.getProjectInnovation());
            }
          }
        }
      } else {
        // get the milestones list
        Set<String> milestonesTemp = new HashSet<>();
        // Get the innovations List
        this.innovationsList = new ArrayList<>();
        // Get the policies List
        this.policyList = new ArrayList<>();
        final int year = this.getActualPhase().getYear();

        if (this.myProjects != null && !this.myProjects.isEmpty()) {
          for (Project projectL : this.myProjects) {
            List<ProjectMilestone> projectMilestones =
              projectL.getProjectOutcomes().stream().filter(po -> po != null && po.getId() != null && po.isActive())
                .flatMap(po -> po.getProjectMilestones().stream())
                .filter(pi -> pi != null && pi.getId() != null && pi.isActive() && pi.getCrpMilestone() != null
                  && pi.getCrpMilestone().getId() != null && pi.getCrpMilestone().isActive()
                  && pi.getCrpMilestone().getComposeID() != null && pi.getYear() != 0 && pi.getYear() <= year)
                .distinct().collect(Collectors.toList());
            for (ProjectMilestone projectMilestone : projectMilestones) {
              milestonesTemp.add(projectMilestone.getCrpMilestone().getComposeID());
            }

            List<ProjectInnovation> innovations =
              projectL.getProjectInnovations().stream().filter(c -> c.isActive()).collect(Collectors.toList());
            for (ProjectInnovation projectInnovation : innovations) {
              if (projectInnovation.getProjectInnovationInfo(this.getActualPhase()) != null) {
                this.innovationsList.add(projectInnovation);
              }
            }

            List<ProjectPolicy> policies =
              projectL.getProjectPolicies().stream().filter(c -> c.isActive()).collect(Collectors.toList());
            for (ProjectPolicy projectPolicy : policies) {
              if (projectPolicy.getProjectPolicyInfo(this.getActualPhase()) != null) {
                this.policyList.add(projectPolicy);
              }
            }
          }
        }

        this.milestones = new ArrayList<>();
        for (String milestoneComposedId : milestonesTemp) {
          CrpMilestone milestone = this.milestoneManager.getCrpMilestoneByPhase(milestoneComposedId, this.getPhaseID());
          if (milestone != null) {
            this.milestones.add(milestone);
          }
        }
      }

      if (this.project != null) {
        String params[] = {this.loggedCrp.getAcronym(), this.project.getId() + ""};
        this.setBasePermission(this.getText(Permission.PROJECT_EXPECTED_STUDIES_BASE_PERMISSION, params));
      } else {
        String params[] = {this.loggedCrp.getAcronym(), this.expectedStudy.getId() + ""};
        this.setBasePermission(this.getText(Permission.STUDIES_BASE_PERMISSION, params));
      }
    }
    if (this.isHttpPost()) {

      // HTTP Post List
      if (this.expectedStudy.getCrps() != null) {
        this.expectedStudy.getCrps().clear();
      }

      if (this.expectedStudy.getSubIdos() != null) {
        this.expectedStudy.getSubIdos().clear();
      }

      if (this.expectedStudy.getFlagships() != null) {
        this.expectedStudy.getFlagships().clear();
      }

      if (this.expectedStudy.getRegions() != null) {
        this.expectedStudy.getRegions().clear();
      }

      if (this.expectedStudy.getInstitutions() != null) {
        this.expectedStudy.getInstitutions().clear();
      }

      if (this.expectedStudy.getSrfTargets() != null) {
        this.expectedStudy.getSrfTargets().clear();
      }

      if (this.expectedStudy.getProjects() != null) {
        this.expectedStudy.getProjects().clear();
      }

      if (this.expectedStudy.getStudyRegions() != null) {
        this.expectedStudy.getStudyRegions().clear();
      }

      if (this.expectedStudy.getLinks() != null) {
        this.expectedStudy.getLinks().clear();
      }

      if (this.expectedStudy.getPolicies() != null) {
        this.expectedStudy.getPolicies().clear();
      }

      if (this.expectedStudy.getInnovations() != null) {
        this.expectedStudy.getInnovations().clear();
      }

      if (this.expectedStudy.getQuantifications() != null) {
        this.expectedStudy.getQuantifications().clear();
      }

      if (this.expectedStudy.getGeographicScopes() != null) {
        this.expectedStudy.getGeographicScopes().clear();
      }

      if (this.expectedStudy.getCenters() != null) {
        this.expectedStudy.getCenters().clear();
      }

      if (this.expectedStudy.getMilestones() != null) {
        this.expectedStudy.getMilestones().clear();
      }

      // HTTP Post info Values
      this.expectedStudy.getProjectExpectedStudyInfo().setRepIndRegion(null);
      this.expectedStudy.getProjectExpectedStudyInfo().setRepIndOrganizationType(null);
      this.expectedStudy.getProjectExpectedStudyInfo().setRepIndPolicyInvestimentType(null);
      this.expectedStudy.getProjectExpectedStudyInfo().setRepIndStageProcess(null);
      this.expectedStudy.getProjectExpectedStudyInfo().setRepIndStageStudy(null);
      this.expectedStudy.getProjectExpectedStudyInfo().setStudyType(null);
      this.expectedStudy.getProjectExpectedStudyInfo().setStatus(null);

      this.expectedStudy.getProjectExpectedStudyInfo().setGenderLevel(null);
      this.expectedStudy.getProjectExpectedStudyInfo().setCapdevLevel(null);
      this.expectedStudy.getProjectExpectedStudyInfo().setYouthLevel(null);
      this.expectedStudy.getProjectExpectedStudyInfo().setClimateChangeLevel(null);
      this.expectedStudy.getProjectExpectedStudyInfo().setRepIndStageStudy(null);

      this.expectedStudy.getProjectExpectedStudyInfo().setOutcomeFile(null);
      this.expectedStudy.getProjectExpectedStudyInfo().setReferencesFile(null);
      this.expectedStudy.getProjectExpectedStudyInfo().setStatus(null);

      this.expectedStudy.getProjectExpectedStudyInfo().setEvidenceTag(null);

    }

  }

  @Override
  public String save() {

    User user = this.getCurrentUser();

    if (this.hasPermission("canEdit") || user.getId().equals(this.expectedStudyDB.getCreatedBy().getId())) {

      Phase phase = this.getActualPhase();
      Path path = this.getAutoSaveFilePath();

      this.expectedStudy.setProject(this.project);

      this.saveCrps(this.expectedStudyDB, phase);
      this.saveFlagships(this.expectedStudyDB, phase);
      this.saveRegions(this.expectedStudyDB, phase);
      this.saveProjects(this.expectedStudyDB, phase);
      this.saveSubIdos(this.expectedStudyDB, phase);
      this.saveInstitutions(this.expectedStudyDB, phase);
      this.saveSrfTargets(this.expectedStudyDB, phase);

      // AR 2018 Save Relations
      this.savePolicies(this.expectedStudyDB, phase);
      this.saveLink(this.expectedStudyDB, phase);
      this.saveInnovations(this.expectedStudyDB, phase);
      this.saveQuantifications(this.expectedStudyDB, phase);

      // AR 2019 Save
      this.saveCenters(this.expectedStudyDB, phase);
      this.saveMilestones(this.expectedStudyDB, phase);

      // Save Geographic Scope Data
      this.saveGeographicScopes(this.expectedStudyDB, phase);

      boolean haveRegions = false;
      boolean haveCountries = false;
      if (this.expectedStudy.getGeographicScopes() != null) {
        for (ProjectExpectedStudyGeographicScope projectPolicyGeographicScope : this.expectedStudy
          .getGeographicScopes()) {

          if (projectPolicyGeographicScope.getRepIndGeographicScope().getId() == 2) {
            haveRegions = true;
          }

          if (projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 1
            && projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 2) {
            haveCountries = true;
          }
        }
      }

      if (haveRegions) {
        // Save the Regions List
        this.saveStudyRegions(this.expectedStudyDB, phase);
      } else {
        this.deleteLocElements(this.expectedStudyDB, phase, false);
      }

      if (haveCountries) {
        // Save the Countries List (ProjectExpectedStudyCountry)
        if (this.expectedStudy.getCountriesIds() != null || !this.expectedStudy.getCountriesIds().isEmpty()) {

          List<ProjectExpectedStudyCountry> countries = this.projectExpectedStudyCountryManager
            .getProjectExpectedStudyCountrybyPhase(this.expectedStudy.getId(), phase.getId()).stream()
            .filter(le -> le != null && le.isActive() && le.getLocElement() != null
              && le.getLocElement().getLocElementType() != null && le.getLocElement().getLocElementType().getId() == 2)
            .collect(Collectors.toList());
          List<ProjectExpectedStudyCountry> countriesSave = new ArrayList<>();
          for (String countryIds : this.expectedStudy.getCountriesIds()) {
            ProjectExpectedStudyCountry countryInn = new ProjectExpectedStudyCountry();
            countryInn.setLocElement(this.locElementManager.getLocElementByISOCode(countryIds));
            countryInn.setProjectExpectedStudy(this.expectedStudy);
            countryInn.setPhase(this.getActualPhase());
            countriesSave.add(countryInn);
            if (!countries.contains(countryInn)) {
              this.projectExpectedStudyCountryManager.saveProjectExpectedStudyCountry(countryInn);
            }
          }
          for (ProjectExpectedStudyCountry projectExpectedStudyCountry : countries) {
            if (!countriesSave.contains(projectExpectedStudyCountry)) {
              this.projectExpectedStudyCountryManager
                .deleteProjectExpectedStudyCountry(projectExpectedStudyCountry.getId());
            }
          }
        }
      } else {
        this.deleteLocElements(this.expectedStudyDB, phase, true);
      }

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_PROJECTS_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_INFOS_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_SUBIDOS_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_FLAGSHIP_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_CRP_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_INSTITUTION_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_COUNTRY_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_SRF_TARGET_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_GEOGRAPHIC_SCOPE);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_CENTER_RELATION);
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_MILESTONE_RELATION);

      this.expectedStudy.setModificationJustification(this.getJustification());

      this.expectedStudy.getProjectExpectedStudyInfo().setPhase(this.getActualPhase());
      this.expectedStudy.getProjectExpectedStudyInfo().setProjectExpectedStudy(this.expectedStudy);

      // Save Files
      if (this.expectedStudy.getProjectExpectedStudyInfo().getOutcomeFile() != null) {
        if (this.expectedStudy.getProjectExpectedStudyInfo().getOutcomeFile().getId() == null) {
          this.expectedStudy.getProjectExpectedStudyInfo().setOutcomeFile(null);
        }
      } else {
        this.expectedStudy.getProjectExpectedStudyInfo()
          .setOutcomeFile(this.expectedStudy.getProjectExpectedStudyInfo().getOutcomeFile());
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getReferencesFile() != null) {
        if (this.expectedStudy.getProjectExpectedStudyInfo().getReferencesFile().getId() == null) {
          this.expectedStudy.getProjectExpectedStudyInfo().setReferencesFile(null);
        }
      } else {
        this.expectedStudy.getProjectExpectedStudyInfo()
          .setReferencesFile(this.expectedStudy.getProjectExpectedStudyInfo().getReferencesFile());
      }

      // Save COVID Analysis
      if (this.expectedStudy.getProjectExpectedStudyInfo().getHasCovidAnalysis() == null) {
        this.expectedStudy.getProjectExpectedStudyInfo().setHasCovidAnalysis(false);
      }

      // Setup new expected year
      if (this.expectedStudy.getProjectExpectedStudyInfo().getStatus() != null
        && this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId() != null
        && this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId() == 4 && newExpectedYear != 0) {
        this.expectedStudy.getProjectExpectedStudyInfo().setYear(newExpectedYear);
      }

      // Setup focusLevel
      if (this.expectedStudy.getProjectExpectedStudyInfo().getGenderLevel() != null) {
        RepIndGenderYouthFocusLevel focusLevel = this.focusLevelManager.getRepIndGenderYouthFocusLevelById(
          this.expectedStudy.getProjectExpectedStudyInfo().getGenderLevel().getId());
        this.expectedStudy.getProjectExpectedStudyInfo().setGenderLevel(focusLevel);
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getCapdevLevel() != null) {
        RepIndGenderYouthFocusLevel focusLevel = this.focusLevelManager.getRepIndGenderYouthFocusLevelById(
          this.expectedStudy.getProjectExpectedStudyInfo().getCapdevLevel().getId());
        this.expectedStudy.getProjectExpectedStudyInfo().setCapdevLevel(focusLevel);
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getYouthLevel() != null) {
        RepIndGenderYouthFocusLevel focusLevel = this.focusLevelManager
          .getRepIndGenderYouthFocusLevelById(this.expectedStudy.getProjectExpectedStudyInfo().getYouthLevel().getId());
        this.expectedStudy.getProjectExpectedStudyInfo().setYouthLevel(focusLevel);
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getClimateChangeLevel() != null) {
        RepIndGenderYouthFocusLevel focusLevel = this.focusLevelManager.getRepIndGenderYouthFocusLevelById(
          this.expectedStudy.getProjectExpectedStudyInfo().getClimateChangeLevel().getId());
        this.expectedStudy.getProjectExpectedStudyInfo().setClimateChangeLevel(focusLevel);
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy() != null) {
        if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId() == -1) {
          this.expectedStudy.getProjectExpectedStudyInfo().setRepIndStageStudy(null);
        }
      }

      // Validate negative Values
      if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndRegion() != null) {
        if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndRegion().getId() == -1) {
          this.expectedStudy.getProjectExpectedStudyInfo().setRepIndRegion(null);
        }
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndOrganizationType() != null) {
        if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndOrganizationType().getId() == -1) {
          this.expectedStudy.getProjectExpectedStudyInfo().setRepIndOrganizationType(null);
        }
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndPolicyInvestimentType() != null) {
        if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndPolicyInvestimentType().getId() == -1) {
          this.expectedStudy.getProjectExpectedStudyInfo().setRepIndPolicyInvestimentType(null);
        }
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageProcess() != null) {
        if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageProcess().getId() == -1) {
          this.expectedStudy.getProjectExpectedStudyInfo().setRepIndStageProcess(null);
        }
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy() != null) {
        if (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId() == -1) {
          this.expectedStudy.getProjectExpectedStudyInfo().setRepIndStageStudy(null);
        }
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getStudyType() != null) {
        if (this.expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId() == -1) {
          this.expectedStudy.getProjectExpectedStudyInfo().setStudyType(null);
        }
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getStatus() != null) {
        if (this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId() == -1) {
          this.expectedStudy.getProjectExpectedStudyInfo().setStatus(null);
        }
      }

      // REMOVED FOR AR 2020
      /*
       * if (this.expectedStudy.getProjectExpectedStudyInfo().getEvidenceTag() != null) {
       * if (this.expectedStudy.getProjectExpectedStudyInfo().getEvidenceTag().getId() == -1) {
       * this.expectedStudy.getProjectExpectedStudyInfo().setEvidenceTag(null);
       * }
       * }
       * // End
       */
      // FOR AR 2020 and onwards: the OICRs will ALWAYS be public

      if (this.expectedStudy.getProjectExpectedStudyInfo().getIsPublic() == null) {
        this.expectedStudy.getProjectExpectedStudyInfo().setIsPublic(true);
      }

      this.projectExpectedStudyInfoManager
        .saveProjectExpectedStudyInfo(this.expectedStudy.getProjectExpectedStudyInfo());
      this.expectedStudy.getProjectExpectedStudyInfos().add(this.expectedStudy.getProjectExpectedStudyInfo());
      /**
       * The following is required because we need to update something on
       * the @ProjectExpectedStudy if we want a row created in the auditlog table.
       */
      this.setModificationJustification(this.expectedStudy);
      this.projectExpectedStudyManager.save(this.expectedStudy, this.getActionName(), relationsName,
        this.getActualPhase());

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
   * Save Expected Studies Centers/PPA partners Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveCenters(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudyCenters() != null
      && projectExpectedStudy.getProjectExpectedStudyCenters().size() > 0) {
      List<ProjectExpectedStudyCenter> centerPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyCenters().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectExpectedStudyCenter studyCenter : centerPrev) {
        if (this.expectedStudy.getCenters() == null || !this.expectedStudy.getCenters().contains(studyCenter)) {
          this.projectExpectedStudyCenterManager.deleteProjectExpectedStudyCenter(studyCenter.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getCenters() != null) {
      for (ProjectExpectedStudyCenter studyCenter : this.expectedStudy.getCenters()) {
        if (studyCenter.getId() == null) {
          ProjectExpectedStudyCenter studyCenterSave = new ProjectExpectedStudyCenter();
          studyCenterSave.setProjectExpectedStudy(projectExpectedStudy);
          studyCenterSave.setPhase(phase);

          Institution institution = this.institutionManager.getInstitutionById(studyCenter.getInstitution().getId());

          studyCenterSave.setInstitution(institution);

          this.projectExpectedStudyCenterManager.saveProjectExpectedStudyCenter(studyCenterSave);
          // This is to add studyCrpSave to generate correct auditlog.
          this.expectedStudy.getProjectExpectedStudyCenters().add(studyCenterSave);
        }
      }
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
      List<ProjectExpectedStudyCrp> crpPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyCrps().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectExpectedStudyCrp studyCrp : crpPrev) {
        if (this.expectedStudy.getCrps() == null || !this.expectedStudy.getCrps().contains(studyCrp)) {
          this.projectExpectedStudyCrpManager.deleteProjectExpectedStudyCrp(studyCrp.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getCrps() != null) {
      for (ProjectExpectedStudyCrp studyCrp : this.expectedStudy.getCrps()) {
        if (studyCrp.getId() == null) {
          ProjectExpectedStudyCrp studyCrpSave = new ProjectExpectedStudyCrp();
          studyCrpSave.setProjectExpectedStudy(projectExpectedStudy);
          studyCrpSave.setPhase(phase);

          GlobalUnit globalUnit = this.crpManager.getGlobalUnitById(studyCrp.getGlobalUnit().getId());

          studyCrpSave.setGlobalUnit(globalUnit);

          this.projectExpectedStudyCrpManager.saveProjectExpectedStudyCrp(studyCrpSave);
          // This is to add studyCrpSave to generate correct auditlog.
          this.expectedStudy.getProjectExpectedStudyCrps().add(studyCrpSave);
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
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())
            && nu.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList()));

      for (ProjectExpectedStudyFlagship studyFlagship : flagshipPrev) {
        if (this.expectedStudy.getFlagships() == null || !this.expectedStudy.getFlagships().contains(studyFlagship)) {
          this.projectExpectedStudyFlagshipManager.deleteProjectExpectedStudyFlagship(studyFlagship.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getFlagships() != null) {
      for (ProjectExpectedStudyFlagship studyFlagship : this.expectedStudy.getFlagships()) {
        if (studyFlagship.getId() == null) {
          ProjectExpectedStudyFlagship studyFlagshipSave = new ProjectExpectedStudyFlagship();
          studyFlagshipSave.setProjectExpectedStudy(projectExpectedStudy);
          studyFlagshipSave.setPhase(phase);

          CrpProgram crpProgram = this.crpProgramManager.getCrpProgramById(studyFlagship.getCrpProgram().getId());

          studyFlagshipSave.setCrpProgram(crpProgram);

          this.projectExpectedStudyFlagshipManager.saveProjectExpectedStudyFlagship(studyFlagshipSave);
          // This is to add studyFlagshipSave to generate correct
          // auditlog.
          this.expectedStudy.getProjectExpectedStudyFlagships().add(studyFlagshipSave);
        }
      }
    }

  }

  /**
   * Save Expected Studies Geographic Scopes Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveGeographicScopes(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudyGeographicScopes() != null
      && projectExpectedStudy.getProjectExpectedStudyGeographicScopes().size() > 0) {

      List<ProjectExpectedStudyGeographicScope> scopePrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyGeographicScopes().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectExpectedStudyGeographicScope studyScope : scopePrev) {
        if (this.expectedStudy.getGeographicScopes() == null
          || !this.expectedStudy.getGeographicScopes().contains(studyScope)) {
          this.projectExpectedStudyGeographicScopeManager.deleteProjectExpectedStudyGeographicScope(studyScope.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getGeographicScopes() != null) {
      for (ProjectExpectedStudyGeographicScope studyScope : this.expectedStudy.getGeographicScopes()) {
        if (studyScope.getId() == null) {
          ProjectExpectedStudyGeographicScope studyScopeSave = new ProjectExpectedStudyGeographicScope();
          studyScopeSave.setProjectExpectedStudy(projectExpectedStudy);
          studyScopeSave.setPhase(phase);

          RepIndGeographicScope geoScope =
            this.geographicScopeManager.getRepIndGeographicScopeById(studyScope.getRepIndGeographicScope().getId());

          studyScopeSave.setRepIndGeographicScope(geoScope);

          this.projectExpectedStudyGeographicScopeManager.saveProjectExpectedStudyGeographicScope(studyScopeSave);
          // This is to add to generate correct auditlog.
          this.expectedStudy.getProjectExpectedStudyGeographicScopes().add(studyScopeSave);
        }
      }
    }

  }

  /**
   * Save Expected Studies Innovations Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveInnovations(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudyInnovations() != null
      && projectExpectedStudy.getProjectExpectedStudyInnovations().size() > 0) {
      List<ProjectExpectedStudyInnovation> innovationPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyInnovations().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectExpectedStudyInnovation studyInnovation : innovationPrev) {
        if (this.expectedStudy.getInnovations() == null
          || !this.expectedStudy.getInnovations().contains(studyInnovation)) {
          this.projectExpectedStudyInnovationManager.deleteProjectExpectedStudyInnovation(studyInnovation.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getInnovations() != null) {
      for (ProjectExpectedStudyInnovation studyInnovation : this.expectedStudy.getInnovations()) {
        if (studyInnovation.getId() == null) {
          ProjectExpectedStudyInnovation studyInnovationSave = new ProjectExpectedStudyInnovation();
          studyInnovationSave.setProjectExpectedStudy(projectExpectedStudy);
          studyInnovationSave.setPhase(phase);

          ProjectInnovation projectInnovation =
            this.projectInnovationManager.getProjectInnovationById(studyInnovation.getProjectInnovation().getId());

          studyInnovationSave.setProjectInnovation(projectInnovation);

          this.projectExpectedStudyInnovationManager.saveProjectExpectedStudyInnovation(studyInnovationSave);
          // This is to add studyInnovationSave to generate correct
          // auditlog.
          this.expectedStudy.getProjectExpectedStudyInnovations().add(studyInnovationSave);
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
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectExpectedStudyInstitution studyInstitution : institutionPrev) {
        if (this.expectedStudy.getInstitutions() == null
          || !this.expectedStudy.getInstitutions().contains(studyInstitution)) {
          this.projectExpectedStudyInstitutionManager.deleteProjectExpectedStudyInstitution(studyInstitution.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getInstitutions() != null) {
      for (ProjectExpectedStudyInstitution studyInstitution : this.expectedStudy.getInstitutions()) {
        if (studyInstitution.getId() == null) {
          ProjectExpectedStudyInstitution studyInstitutionSave = new ProjectExpectedStudyInstitution();
          studyInstitutionSave.setProjectExpectedStudy(projectExpectedStudy);
          studyInstitutionSave.setPhase(phase);

          Institution institution =
            this.institutionManager.getInstitutionById(studyInstitution.getInstitution().getId());

          studyInstitutionSave.setInstitution(institution);

          this.projectExpectedStudyInstitutionManager.saveProjectExpectedStudyInstitution(studyInstitutionSave);
          // This is to add studySubIdoSave to generate correct
          // auditlog.
          this.expectedStudy.getProjectExpectedStudyInstitutions().add(studyInstitutionSave);
        }
      }
    }

  }

  /**
   * Save Expected Studies Link Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveLink(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudyLinks() != null
      && projectExpectedStudy.getProjectExpectedStudyLinks().size() > 0) {
      List<ProjectExpectedStudyLink> linkPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyLinks().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectExpectedStudyLink studyLink : linkPrev) {
        if (this.expectedStudy.getLinks() == null || !this.expectedStudy.getLinks().contains(studyLink)) {
          this.projectExpectedStudyLinkManager.deleteProjectExpectedStudyLink(studyLink.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getLinks() != null) {
      for (ProjectExpectedStudyLink studyLink : this.expectedStudy.getLinks()) {
        if (studyLink.getId() == null) {
          ProjectExpectedStudyLink studyLinkSave = new ProjectExpectedStudyLink();
          studyLinkSave.setProjectExpectedStudy(projectExpectedStudy);
          studyLinkSave.setPhase(phase);
          studyLinkSave.setLink(studyLink.getLink());

          this.projectExpectedStudyLinkManager.saveProjectExpectedStudyLink(studyLinkSave);
          // This is to add studyLinkSave to generate correct
          // auditlog.
          this.expectedStudy.getProjectExpectedStudyLinks().add(studyLinkSave);
        } else {
          ProjectExpectedStudyLink studyLinkSave =
            this.projectExpectedStudyLinkManager.getProjectExpectedStudyLinkById(studyLink.getId());
          studyLinkSave.setProjectExpectedStudy(projectExpectedStudy);
          studyLinkSave.setPhase(phase);
          studyLinkSave.setLink(studyLink.getLink());

          this.projectExpectedStudyLinkManager.saveProjectExpectedStudyLink(studyLinkSave);
          // This is to add studyLinkSave to generate correct
          // auditlog.
          this.expectedStudy.getProjectExpectedStudyLinks().add(studyLinkSave);
        }
      }
    }
  }

  /**
   * Save Expected Studies Milestone Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveMilestones(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudyMilestones() != null
      && projectExpectedStudy.getProjectExpectedStudyMilestones().size() > 0) {

      List<ProjectExpectedStudyMilestone> milestonePrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyMilestones().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectExpectedStudyMilestone studyMilestone : milestonePrev) {
        if (this.expectedStudy.getMilestones() == null
          || !this.expectedStudy.getMilestones().contains(studyMilestone)) {
          this.projectExpectedStudyMilestoneManager.deleteProjectExpectedStudyMilestone(studyMilestone.getId());
        }
      }
    }
    // Save policy milestones only if boolean 'has milestones' selection is true
    if (this.expectedStudy.getProjectExpectedStudyInfo().getHasMilestones() != null
      && this.expectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == true) {

      // Save form Information
      if (this.expectedStudy.getMilestones() != null) {
        for (ProjectExpectedStudyMilestone studyMilestone : this.expectedStudy.getMilestones()) {
          if (studyMilestone.getId() == null) {
            ProjectExpectedStudyMilestone studyMilestoneSave = new ProjectExpectedStudyMilestone();
            studyMilestoneSave.setProjectExpectedStudy(projectExpectedStudy);
            studyMilestoneSave.setPhase(phase);
            studyMilestoneSave.setPrimary(studyMilestone.getPrimary());

            if (expectedStudy.getMilestones() != null && expectedStudy.getMilestones().size() == 1) {
              studyMilestoneSave.setPrimary(true);
            }

            if (studyMilestone.getCrpMilestone() != null && studyMilestone.getCrpMilestone().getId() != null) {
              CrpMilestone milestone = milestoneManager.getCrpMilestoneById(studyMilestone.getCrpMilestone().getId());
              studyMilestoneSave.setCrpMilestone(milestone);

              this.projectExpectedStudyMilestoneManager.saveProjectExpectedStudyMilestone(studyMilestoneSave);
              // This is to add studyCrpSave to generate correct auditlog.
              this.expectedStudy.getProjectExpectedStudyMilestones().add(studyMilestoneSave);
            }
          } else {
            // if milestone already exist - save primary
            ProjectExpectedStudyMilestone studyMilestoneSave = new ProjectExpectedStudyMilestone();
            studyMilestoneSave =
              projectExpectedStudyMilestoneManager.getProjectExpectedStudyMilestoneById(studyMilestone.getId());
            studyMilestoneSave.setProjectExpectedStudy(projectExpectedStudy);
            studyMilestoneSave.setPhase(phase);
            studyMilestoneSave.setPrimary(studyMilestone.getPrimary());

            if (studyMilestone.getCrpMilestone() != null && studyMilestone.getCrpMilestone().getId() != null) {
              CrpMilestone milestone = milestoneManager.getCrpMilestoneById(studyMilestone.getCrpMilestone().getId());
              studyMilestoneSave.setCrpMilestone(milestone);
            }
            if (expectedStudy.getMilestones() != null && expectedStudy.getMilestones().size() == 1) {
              studyMilestoneSave.setPrimary(true);
            }

            projectExpectedStudyMilestoneManager.saveProjectExpectedStudyMilestone(studyMilestoneSave);
            // This is to add studyCrpSave to generate correct auditlog.
            this.expectedStudy.getProjectExpectedStudyMilestones().add(studyMilestoneSave);

          }
        }
      }
    } else {
      // Delete all milestones for this policy
      if (this.expectedStudy.getMilestones() != null && this.expectedStudy.getMilestones().size() > 0) {
        for (ProjectExpectedStudyMilestone studyMilestone : this.expectedStudy.getMilestones()) {
          try {
            CrpMilestone milestone = milestoneManager.getCrpMilestoneById(studyMilestone.getId());
            if (milestone != null) {
              projectExpectedStudyMilestoneManager.deleteProjectExpectedStudyMilestone(studyMilestone.getId());
              // This is to add studyCrpSave to generate correct auditlog.
              this.expectedStudy.getProjectExpectedStudyMilestones().remove(
                projectExpectedStudyMilestoneManager.getProjectExpectedStudyMilestoneById(studyMilestone.getId()));
            }
          } catch (Exception e) {

          }

        }
      }
    }

  }

  /**
   * Save Expected Studies Policies Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void savePolicies(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudyPolicies() != null
      && projectExpectedStudy.getProjectExpectedStudyPolicies().size() > 0) {
      List<ProjectExpectedStudyPolicy> policyPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyPolicies().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectExpectedStudyPolicy studyPolicy : policyPrev) {
        if (this.expectedStudy.getPolicies() == null || !this.expectedStudy.getPolicies().contains(studyPolicy)) {
          this.projectExpectedStudyPolicyManager.deleteProjectExpectedStudyPolicy(studyPolicy.getId());
        }
      }


      // Delete prev studies policies if the question is not
      if (expectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()) != null
        && expectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getIsContribution() != null
        && expectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getIsContribution() == false) {
        for (ProjectExpectedStudyPolicy studyPolicy : policyPrev) {
          this.projectExpectedStudyPolicyManager.deleteProjectExpectedStudyPolicy(studyPolicy.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getPolicies() != null) {
      for (ProjectExpectedStudyPolicy studyPolicy : this.expectedStudy.getPolicies()) {
        if (studyPolicy.getId() == null) {
          ProjectExpectedStudyPolicy studyPolicySave = new ProjectExpectedStudyPolicy();
          studyPolicySave.setProjectExpectedStudy(projectExpectedStudy);
          studyPolicySave.setPhase(phase);

          ProjectPolicy projectPolicy =
            this.projectPolicyManager.getProjectPolicyById(studyPolicy.getProjectPolicy().getId());

          studyPolicySave.setProjectPolicy(projectPolicy);

          this.projectExpectedStudyPolicyManager.saveProjectExpectedStudyPolicy(studyPolicySave);
          // This is to add studyLinkSave to generate correct
          // auditlog.
          this.expectedStudy.getProjectExpectedStudyPolicies().add(studyPolicySave);
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
        .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ExpectedStudyProject studyProject : projectPrev) {
        if (this.expectedStudy.getProjects() == null || !this.expectedStudy.getProjects().contains(studyProject)) {
          this.expectedStudyProjectManager.deleteExpectedStudyProject(studyProject.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getProjects() != null) {
      for (ExpectedStudyProject studyProject : this.expectedStudy.getProjects()) {
        if (studyProject.getId() == null) {
          ExpectedStudyProject studyProjectSave = new ExpectedStudyProject();
          studyProjectSave.setProjectExpectedStudy(projectExpectedStudy);
          studyProjectSave.setPhase(phase);

          Project project = this.projectManager.getProjectById(studyProject.getProject().getId());

          studyProjectSave.setProject(project);

          this.expectedStudyProjectManager.saveExpectedStudyProject(studyProjectSave);
          // This is to add studyProjectSave to generate correct
          // auditlog.
          this.expectedStudy.getExpectedStudyProjects().add(studyProjectSave);
        }
      }
    }

  }

  /**
   * Save Expected Studies Quantification Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveQuantifications(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudyQuantifications() != null
      && projectExpectedStudy.getProjectExpectedStudyQuantifications().size() > 0) {
      List<ProjectExpectedStudyQuantification> quantificationPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyQuantifications().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectExpectedStudyQuantification studyQuantification : quantificationPrev) {
        if (this.expectedStudy.getQuantifications() == null
          || !this.expectedStudy.getQuantifications().contains(studyQuantification)) {
          this.projectExpectedStudyQuantificationManager
            .deleteProjectExpectedStudyQuantification(studyQuantification.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getQuantifications() != null) {
      for (ProjectExpectedStudyQuantification studyQuantification : this.expectedStudy.getQuantifications()) {
        if (studyQuantification.getId() == null) {
          ProjectExpectedStudyQuantification studyQuantificationSave = new ProjectExpectedStudyQuantification();
          studyQuantificationSave.setProjectExpectedStudy(projectExpectedStudy);
          studyQuantificationSave.setPhase(phase);

          // Default Values for type Quantification.
          if (studyQuantification.getTypeQuantification() != null) {
            studyQuantificationSave.setTypeQuantification(studyQuantification.getTypeQuantification());
          } else {
            studyQuantificationSave.setTypeQuantification("A");
          }

          studyQuantificationSave.setNumber(studyQuantification.getNumber());
          studyQuantificationSave.setComments(studyQuantification.getComments());
          studyQuantificationSave.setTargetUnit(studyQuantification.getTargetUnit());

          this.projectExpectedStudyQuantificationManager
            .saveProjectExpectedStudyQuantification(studyQuantificationSave);
          // This is to add studyQuantificationSave to generate
          // correct auditlog.
          this.expectedStudy.getProjectExpectedStudyQuantifications().add(studyQuantificationSave);
        } else {
          ProjectExpectedStudyQuantification studyQuantificationSave = this.projectExpectedStudyQuantificationManager
            .getProjectExpectedStudyQuantificationById(studyQuantification.getId());

          // Default Values for type Quantification.
          if (studyQuantification.getTypeQuantification() != null) {
            studyQuantificationSave.setTypeQuantification(studyQuantification.getTypeQuantification());
          } else {
            studyQuantificationSave.setTypeQuantification("A");
          }
          studyQuantificationSave.setNumber(studyQuantification.getNumber());
          studyQuantificationSave.setComments(studyQuantification.getComments());
          studyQuantificationSave.setTargetUnit(studyQuantification.getTargetUnit());

          this.projectExpectedStudyQuantificationManager
            .saveProjectExpectedStudyQuantification(studyQuantificationSave);
          // This is to add studyQuantificationSave to generate
          // correct auditlog.
          this.expectedStudy.getProjectExpectedStudyQuantifications().add(studyQuantificationSave);
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
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())
            && nu.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList()));

      for (ProjectExpectedStudyFlagship studyFlagship : flagshipPrev) {
        if (this.expectedStudy.getRegions() == null || !this.expectedStudy.getRegions().contains(studyFlagship)) {
          this.projectExpectedStudyFlagshipManager.deleteProjectExpectedStudyFlagship(studyFlagship.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getRegions() != null) {
      for (ProjectExpectedStudyFlagship studyFlagship : this.expectedStudy.getRegions()) {
        if (studyFlagship.getId() == null) {
          ProjectExpectedStudyFlagship studyFlagshipSave = new ProjectExpectedStudyFlagship();
          studyFlagshipSave.setProjectExpectedStudy(projectExpectedStudy);
          studyFlagshipSave.setPhase(phase);

          CrpProgram crpProgram = this.crpProgramManager.getCrpProgramById(studyFlagship.getCrpProgram().getId());

          studyFlagshipSave.setCrpProgram(crpProgram);

          this.projectExpectedStudyFlagshipManager.saveProjectExpectedStudyFlagship(studyFlagshipSave);
          // This is to add studyFlagshipSave to generate correct
          // auditlog.
          this.expectedStudy.getProjectExpectedStudyFlagships().add(studyFlagshipSave);
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
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectExpectedStudySrfTarget studytarget : targetPrev) {
        if (this.expectedStudy.getSrfTargets() == null || !this.expectedStudy.getSrfTargets().contains(studytarget)) {
          this.projectExpectedStudySrfTargetManager.deleteProjectExpectedStudySrfTarget(studytarget.getId());
        }
      }

      // Delete previous srf targets if the answer of the question is not
      if (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()) != null
        && projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getIsSrfTarget() != null
        && (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getIsSrfTarget()
          .equals("targetsOptionNo")
          || projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getIsSrfTarget()
            .equals("targetsOptionTooEarlyToSay"))) {
        for (ProjectExpectedStudySrfTarget studytarget : targetPrev) {
          this.projectExpectedStudySrfTargetManager.deleteProjectExpectedStudySrfTarget(studytarget.getId());

        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getSrfTargets() != null) {
      for (ProjectExpectedStudySrfTarget studytarget : this.expectedStudy.getSrfTargets()) {
        if (studytarget.getId() == null) {
          ProjectExpectedStudySrfTarget studytargetSave = new ProjectExpectedStudySrfTarget();
          studytargetSave.setProjectExpectedStudy(projectExpectedStudy);
          studytargetSave.setPhase(phase);

          SrfSloIndicator sloIndicator =
            this.srfSloIndicatorManager.getSrfSloIndicatorById(studytarget.getSrfSloIndicator().getId());

          studytargetSave.setSrfSloIndicator(sloIndicator);

          this.projectExpectedStudySrfTargetManager.saveProjectExpectedStudySrfTarget(studytargetSave);
          // This is to add studytargetSave to generate correct
          // auditlog.
          this.expectedStudy.getProjectExpectedStudySrfTargets().add(studytargetSave);
        }
      }
    }

  }

  /**
   * Save Expected Studies Geographic Regions Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveStudyRegions(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    List<ProjectExpectedStudyRegion> regionPrev = this.projectExpectedStudyRegionManager
      .getProjectExpectedStudyRegionbyPhase(this.expectedStudy.getId(), phase.getId()).stream()
      .filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId().equals(1L))
      .collect(Collectors.toList());

    // Search and deleted form Information
    if (regionPrev != null && regionPrev.size() > 0) {
      for (ProjectExpectedStudyRegion studyRegion : regionPrev) {
        if (this.expectedStudy.getStudyRegions() == null
          || !this.expectedStudy.getStudyRegions().contains(studyRegion)) {
          this.projectExpectedStudyRegionManager.deleteProjectExpectedStudyRegion(studyRegion.getId());
        }
      }
    }

    if (this.expectedStudy.getStudyRegions() != null) {
      for (ProjectExpectedStudyRegion studyRegion : this.expectedStudy.getStudyRegions()) {
        if (studyRegion.getId() == null) {
          ProjectExpectedStudyRegion studyRegionSave = new ProjectExpectedStudyRegion();
          studyRegionSave.setProjectExpectedStudy(projectExpectedStudy);
          studyRegionSave.setPhase(phase);

          LocElement locElement = this.locElementManager.getLocElementById(studyRegion.getLocElement().getId());

          studyRegionSave.setLocElement(locElement);

          this.projectExpectedStudyRegionManager.saveProjectExpectedStudyRegion(studyRegionSave);
          // This is to add studyFlagshipSave to generate correct
          // auditlog.
          this.expectedStudy.getProjectExpectedStudyRegions().add(studyRegionSave);
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
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectExpectedStudySubIdo studySubIdo : subIdoPrev) {
        if (this.expectedStudy.getSubIdos() == null || !this.expectedStudy.getSubIdos().contains(studySubIdo)) {
          this.projectExpectedStudySubIdoManager.deleteProjectExpectedStudySubIdo(studySubIdo.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getSubIdos() != null) {
      for (ProjectExpectedStudySubIdo studySubIdo : this.expectedStudy.getSubIdos()) {
        if (studySubIdo.getId() == null) {
          ProjectExpectedStudySubIdo studySubIdoSave = new ProjectExpectedStudySubIdo();
          studySubIdoSave.setProjectExpectedStudy(projectExpectedStudy);
          studySubIdoSave.setPhase(phase);
          studySubIdoSave.setPrimary(studySubIdo.getPrimary());

          if (studySubIdo.getSrfSubIdo() != null && studySubIdo.getSrfSubIdo().getId() != null) {
            SrfSubIdo srfSubIdo = this.srfSubIdoManager.getSrfSubIdoById(studySubIdo.getSrfSubIdo().getId());
            studySubIdoSave.setSrfSubIdo(srfSubIdo);
          }

          if (expectedStudy.getSubIdos() != null && expectedStudy.getSubIdos().size() == 1) {
            studySubIdoSave.setPrimary(true);
          }

          this.projectExpectedStudySubIdoManager.saveProjectExpectedStudySubIdo(studySubIdoSave);
          // This is to add studySubIdoSave to generate correct
          // auditlog.
          this.expectedStudy.getProjectExpectedStudySubIdos().add(studySubIdoSave);
        } else {
          // if sub ido already exist - save primary
          ProjectExpectedStudySubIdo studySubIdoSave = new ProjectExpectedStudySubIdo();
          studySubIdoSave = projectExpectedStudySubIdoManager.getProjectExpectedStudySubIdoById(studySubIdo.getId());

          studySubIdoSave.setProjectExpectedStudy(projectExpectedStudy);
          studySubIdoSave.setPhase(phase);
          studySubIdoSave.setPrimary(studySubIdo.getPrimary());

          if (studySubIdo.getSrfSubIdo() != null && studySubIdo.getSrfSubIdo().getId() != null) {
            SrfSubIdo srfSubIdo = this.srfSubIdoManager.getSrfSubIdoById(studySubIdo.getSrfSubIdo().getId());
            studySubIdoSave.setSrfSubIdo(srfSubIdo);
          }

          if (expectedStudy.getSubIdos() != null && expectedStudy.getSubIdos().size() == 1) {
            studySubIdoSave.setPrimary(true);
          }
          projectExpectedStudySubIdoManager.saveProjectExpectedStudySubIdo(studySubIdoSave);

        }
      }
    }

  }

  public void setCenters(List<Institution> centers) {
    this.centers = centers;
  }

  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
  }

  public void setCrpMilestonePrimary(long crpMilestonePrimary) {
    this.crpMilestonePrimary = crpMilestonePrimary;
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

  public void setInnovationsList(List<ProjectInnovation> innovationsList) {
    this.innovationsList = innovationsList;
  }

  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMilestonePrimaryId(long milestonePrimaryId) {
    this.milestonePrimaryId = milestonePrimaryId;
  }

  public void setMilestones(List<CrpMilestone> milestones) {
    this.milestones = milestones;
  }

  public void setMyProjects(List<Project> myProjects) {
    this.myProjects = myProjects;
  }

  public void setNewExpectedYear(int newExpectedYear) {
    this.newExpectedYear = newExpectedYear;
  }

  public void setOrganizationTypes(List<RepIndOrganizationType> organizationTypes) {
    this.organizationTypes = organizationTypes;
  }

  public void setPolicyInvestimentTypes(List<RepIndPolicyInvestimentType> policyInvestimentTypes) {
    this.policyInvestimentTypes = policyInvestimentTypes;
  }

  public void setPolicyList(List<ProjectPolicy> policyList) {
    this.policyList = policyList;
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

  public void setRegions(List<LocElement> regions) {
    this.regions = regions;
  }

  public void setSrfSubIdoPrimary(long srfSubIdoPrimary) {
    this.srfSubIdoPrimary = srfSubIdoPrimary;
  }

  public void setStageProcesses(List<RepIndStageProcess> stageProcesses) {
    this.stageProcesses = stageProcesses;
  }

  public void setStageStudies(List<RepIndStageStudy> stageStudies) {
    this.stageStudies = stageStudies;
  }

  public void setStatuses(List<GeneralStatus> statuses) {
    this.statuses = statuses;
  }

  public void setStudyTypes(List<StudyType> studyTypes) {
    this.studyTypes = studyTypes;
  }

  public void setSubIdoPrimaryId(long subIdoPrimaryId) {
    this.subIdoPrimaryId = subIdoPrimaryId;
  }

  public void setSubIdos(List<SrfSubIdo> subIdos) {
    this.subIdos = subIdos;
  }

  public void setTags(List<EvidenceTag> tags) {
    this.tags = tags;
  }

  public void setTargets(List<SrfSloIndicator> targets) {
    this.targets = targets;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (this.save) {
      this.projectExpectedStudiesValidator.validate(this, this.project, this.expectedStudy, true);
    }
  }

}
