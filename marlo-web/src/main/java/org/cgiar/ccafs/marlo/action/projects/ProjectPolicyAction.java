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
import org.cgiar.ccafs.marlo.data.manager.CgiarCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PolicyMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSharedManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCenterManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyOwnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicySdgTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicySubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyInvestimentTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageProcessManager;
import org.cgiar.ccafs.marlo.data.manager.SdgTargetsManager;
import org.cgiar.ccafs.marlo.data.manager.SrfIdoManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PolicyMilestone;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationShared;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyOwner;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySdgTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyInvestimentType;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyType;
import org.cgiar.ccafs.marlo.data.model.RepIndStageProcess;
import org.cgiar.ccafs.marlo.data.model.SdgTargets;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.projects.ProjectPolicyValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class ProjectPolicyAction extends BaseAction {

  private static final long serialVersionUID = 597647662288518417L;

  // Managers
  private GlobalUnitManager globalUnitManager;
  private ProjectPolicyManager projectPolicyManager;
  private ProjectPolicyInfoManager projectPolicyInfoManager;
  private ProjectManager projectManager;
  private PhaseManager phaseManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private LocElementManager locElementManager;
  private RepIndGenderYouthFocusLevelManager focusLevelManager;
  private RepIndOrganizationTypeManager repIndOrganizationTypeManager;
  private RepIndPolicyInvestimentTypeManager repIndPolicyInvestimentTypeManager;
  private RepIndStageProcessManager repIndStageProcessManager;
  private RepIndPolicyTypeManager repIndPolicyTypeManager;
  private SrfSubIdoManager srfSubIdoManager;
  private AuditLogManager auditLogManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;
  private ProjectPolicyCountryManager projectPolicyCountryManager;
  private ProjectPolicyOwnerManager projectPolicyOwnerManager;
  private ProjectPolicyCrpManager projectPolicyCrpManager;
  private ProjectPolicySubIdoManager projectPolicySubIdoManager;
  private ProjectPolicyCrossCuttingMarkerManager projectPolicyCrossCuttingMarkerManager;
  private ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager;
  private ProjectPolicyInnovationManager projectPolicyInnovationManager;
  private ProjectInnovationManager projectInnovationManager;
  private ProjectPolicyGeographicScopeManager projectPolicyGeographicScopeManager;
  private ProjectPolicyRegionManager projectPolicyRegionManager;
  private PolicyMilestoneManager policyMilestoneManager;
  private CrpMilestoneManager crpMilestoneManager;
  private ProjectPolicyValidator validator;
  private ProjectPolicyCenterManager projectPolicyCenterManager;
  private InstitutionManager institutionManager;
  private SrfIdoManager srfIdoManager;
  private ProjectInnovationSharedManager projectInnovationSharedManager;

  // Alliance managers
  private ProjectPolicySdgTargetManager projectPolicySdgTargetManager;
  private SdgTargetsManager sdgTargetsManager;

  // Variables
  private GlobalUnit loggedCrp;
  private Project project;
  private long projectID;
  private long policyID;
  private long subIdoPrimaryId;
  private long srfSubIdoPrimary;
  private long milestonePrimaryId;
  private long crpMilestonePrimary;
  private ProjectPolicy policy;
  private ProjectPolicy policyDB;
  private List<RepIndGeographicScope> geographicScopes;
  private List<LocElement> regions;
  private List<RepIndOrganizationType> organizationTypes;
  private List<RepIndGenderYouthFocusLevel> focusLevels;
  private List<RepIndPolicyInvestimentType> policyInvestimentTypes;
  private List<RepIndStageProcess> stageProcesses;
  private List<RepIndPolicyType> policyTypes;
  private List<LocElement> countries;
  private List<SrfSubIdo> subIdos;
  private List<SrfSubIdo> principalSubIdo;
  private List<GlobalUnit> crps;
  private List<ProjectExpectedStudy> expectedStudyList;
  private List<CgiarCrossCuttingMarker> cgiarCrossCuttingMarkers;
  private List<ProjectInnovation> innovationList;
  private List<CrpMilestone> milestoneList;
  private List<SrfIdo> srfIdos;

  private String transaction;
  private List<Institution> centers;
  private HashMap<Long, String> idoList;

  private List<SdgTargets> sdgTargetList;

  @Inject
  public ProjectPolicyAction(APConfig config, GlobalUnitManager globalUnitManager,
    ProjectPolicyManager projectPolicyManager, ProjectPolicyInfoManager projectPolicyInfoManager,
    ProjectManager projectManager, PhaseManager phaseManager, RepIndGeographicScopeManager repIndGeographicScopeManager,
    LocElementManager locElementManager, RepIndGenderYouthFocusLevelManager focusLevelManager,
    RepIndOrganizationTypeManager repIndOrganizationTypeManager,
    RepIndPolicyInvestimentTypeManager repIndPolicyInvestimentTypeManager,
    RepIndStageProcessManager repIndStageProcessManager, RepIndPolicyTypeManager repIndPolicyTypeManager,
    SrfSubIdoManager srfSubIdoManager, AuditLogManager auditLogManager,
    ProjectExpectedStudyManager projectExpectedStudyManager,
    CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager,
    ProjectPolicyCountryManager projectPolicyCountryManager, ProjectPolicyOwnerManager projectPolicyOwnerManager,
    ProjectPolicyCrpManager projectPolicyCrpManager, ProjectPolicySubIdoManager projectPolicySubIdoManager,
    ProjectPolicyCrossCuttingMarkerManager projectPolicyCrossCuttingMarkerManager, ProjectPolicyValidator validator,
    ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager,
    ProjectInnovationManager projectInnovationManager, ProjectPolicyInnovationManager projectPolicyInnovationManager,
    ProjectPolicyGeographicScopeManager projectPolicyGeographicScopeManager,
    ProjectPolicyRegionManager projectPolicyRegionManager, PolicyMilestoneManager policyMilestoneManager,
    CrpMilestoneManager crpMilestoneManager, ProjectPolicyCenterManager projectPolicyCenterManager,
    InstitutionManager institutionManager, SrfIdoManager srfIdoManager,
    ProjectInnovationSharedManager projectInnovationSharedManager,
    ProjectPolicySdgTargetManager projectPolicySdgTargetManager, SdgTargetsManager sdgTargetsManager) {
    super(config);
    this.globalUnitManager = globalUnitManager;
    this.projectPolicyManager = projectPolicyManager;
    this.projectPolicyInfoManager = projectPolicyInfoManager;
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.locElementManager = locElementManager;
    this.focusLevelManager = focusLevelManager;
    this.repIndOrganizationTypeManager = repIndOrganizationTypeManager;
    this.repIndPolicyInvestimentTypeManager = repIndPolicyInvestimentTypeManager;
    this.repIndStageProcessManager = repIndStageProcessManager;
    this.repIndPolicyTypeManager = repIndPolicyTypeManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.auditLogManager = auditLogManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.cgiarCrossCuttingMarkerManager = cgiarCrossCuttingMarkerManager;
    this.projectPolicyCountryManager = projectPolicyCountryManager;
    this.projectPolicyOwnerManager = projectPolicyOwnerManager;
    this.projectPolicyCrpManager = projectPolicyCrpManager;
    this.projectPolicySubIdoManager = projectPolicySubIdoManager;
    this.projectPolicyCrossCuttingMarkerManager = projectPolicyCrossCuttingMarkerManager;
    this.validator = validator;
    this.projectExpectedStudyPolicyManager = projectExpectedStudyPolicyManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectPolicyInnovationManager = projectPolicyInnovationManager;
    this.projectPolicyGeographicScopeManager = projectPolicyGeographicScopeManager;
    this.projectPolicyRegionManager = projectPolicyRegionManager;
    this.policyMilestoneManager = policyMilestoneManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.projectPolicyCenterManager = projectPolicyCenterManager;
    this.institutionManager = institutionManager;
    this.srfIdoManager = srfIdoManager;
    this.projectInnovationSharedManager = projectInnovationSharedManager;
    this.projectPolicySdgTargetManager = projectPolicySdgTargetManager;
    this.sdgTargetsManager = sdgTargetsManager;
  }

  /**
   * Delete all LocElements Records when Geographic Scope is Global or NULL
   * 
   * @param policy
   * @param phase
   */
  public void deleteLocElements(ProjectPolicy policy, Phase phase, boolean isCountry) {
    if (isCountry) {
      if (policy.getProjectPolicyCountries() != null && policy.getProjectPolicyCountries().size() > 0) {

        List<ProjectPolicyCountry> regionPrev = new ArrayList<>(policy.getProjectPolicyCountries().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (ProjectPolicyCountry policyRegion : regionPrev) {

          projectPolicyCountryManager.deleteProjectPolicyCountry(policyRegion.getId());

        }
      }
    } else {
      if (policy.getProjectPolicyRegions() != null && policy.getProjectPolicyRegions().size() > 0) {

        List<ProjectPolicyRegion> regionPrev = new ArrayList<>(policy.getProjectPolicyRegions().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (ProjectPolicyRegion policyRegion : regionPrev) {

          projectPolicyRegionManager.deleteProjectPolicyRegion(policyRegion.getId());

        }

      }
    }
  }

  /**
   * The name of the autosave file is constructed and the path is searched
   * 
   * @return Auto save file path
   */
  private Path getAutoSaveFilePath() {
    // get the class simple name
    String composedClassName = policy.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatenate name and add the .json extension
    String autoSaveFile = policy.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<Institution> getCenters() {
    return centers;
  }

  public List<CgiarCrossCuttingMarker> getCgiarCrossCuttingMarkers() {
    return cgiarCrossCuttingMarkers;
  }

  public List<LocElement> getCountries() {
    return countries;
  }

  public long getCrpMilestonePrimary() {
    return crpMilestonePrimary;
  }

  public List<GlobalUnit> getCrps() {
    return crps;
  }

  public List<ProjectExpectedStudy> getExpectedStudyList() {
    return expectedStudyList;
  }

  public List<RepIndGenderYouthFocusLevel> getFocusLevels() {
    return focusLevels;
  }

  public List<RepIndGeographicScope> getGeographicScopes() {
    return geographicScopes;
  }

  public List<ProjectInnovation> getInnovationList() {
    return innovationList;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<CrpMilestone> getMilestoneList() {
    return milestoneList;
  }

  public long getMilestonePrimaryId() {
    return milestonePrimaryId;
  }

  public List<RepIndOrganizationType> getOrganizationTypes() {
    return organizationTypes;
  }

  public ProjectPolicy getPolicy() {
    return policy;
  }

  /**
   * Get the information for the Cross Cutting marker in the form
   * 
   * @param markerID
   * @return
   */
  public ProjectPolicyCrossCuttingMarker getPolicyCrossCuttingMarker(long markerID) {
    ProjectPolicyCrossCuttingMarker crossCuttingMarker = new ProjectPolicyCrossCuttingMarker();
    if (this.isDraft()) {
      // Cgiar Cross Cutting Markers Autosave
      if (policy.getCrossCuttingMarkers() != null) {
        for (ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker : policy.getCrossCuttingMarkers()) {
          if (projectPolicyCrossCuttingMarker.getCgiarCrossCuttingMarker().getId() == markerID) {
            crossCuttingMarker = projectPolicyCrossCuttingMarker;
          }
        }
      }
    } else {
      crossCuttingMarker = projectPolicyCrossCuttingMarkerManager.getPolicyCrossCountryMarkerId(policyID, markerID,
        this.getActualPhase().getId());
    }
    if (crossCuttingMarker != null) {
      return crossCuttingMarker;
    } else {
      return null;
    }
  }

  public long getPolicyID() {
    return policyID;
  }

  public List<RepIndPolicyInvestimentType> getPolicyInvestimentTypes() {
    return policyInvestimentTypes;
  }

  public List<RepIndPolicyType> getPolicyTypes() {
    return policyTypes;
  }

  public List<SrfSubIdo> getPrincipalSubIdo() {
    return principalSubIdo;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public List<LocElement> getRegions() {
    return regions;
  }

  public List<SdgTargets> getSdgTargetList() {
    return sdgTargetList;
  }

  public List<SrfIdo> getSrfIdos() {
    return srfIdos;
  }

  public long getSrfSubIdoPrimary() {
    return srfSubIdoPrimary;
  }

  public List<RepIndStageProcess> getStageProcesses() {
    return stageProcesses;
  }

  public long getSubIdoPrimaryId() {
    return subIdoPrimaryId;
  }

  public List<SrfSubIdo> getSubIdos() {
    return subIdos;
  }

  public String getTransaction() {
    return transaction;
  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = globalUnitManager.getGlobalUnitById(loggedCrp.getId());

    policyID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.POLICY_REQUEST_ID)));

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      ProjectPolicy history = (ProjectPolicy) auditLogManager.getHistory(transaction);

      if (history != null) {
        policy = history;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }
      if (policy.getProjectPolicyInfo() == null) {
        policy.getProjectPolicyInfo(this.getActualPhase());
      }
      // load relations
      if (policy.getProjectPolicyInfo() != null) {

        // load Policy Investment Type
        if (policy.getProjectPolicyInfo().getRepIndPolicyInvestimentType() != null
          && policy.getProjectPolicyInfo().getRepIndPolicyInvestimentType().getId() != null) {
          policy.getProjectPolicyInfo()
            .setRepIndPolicyInvestimentType(repIndPolicyInvestimentTypeManager.getRepIndPolicyInvestimentTypeById(
              policy.getProjectPolicyInfo().getRepIndPolicyInvestimentType().getId()));
        }

        // load Level Of Maturity Type
        if (policy.getProjectPolicyInfo().getRepIndStageProcess() != null
          && policy.getProjectPolicyInfo().getRepIndStageProcess().getId() != null) {
          policy.getProjectPolicyInfo().setRepIndStageProcess(repIndStageProcessManager
            .getRepIndStageProcessById(policy.getProjectPolicyInfo().getRepIndStageProcess().getId()));
        }

      }

    } else {
      policy = projectPolicyManager.getProjectPolicyById(policyID);

    }

    if (policy != null) {

      projectID = policy.getProject().getId();
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

        policy = (ProjectPolicy) autoSaveReader.readFromJson(jReader);

        // Policy Geographic Scope List AutoSave
        boolean haveRegions = false;
        boolean haveCountries = false;

        if (policy.getGeographicScopes() != null) {
          for (ProjectPolicyGeographicScope projectPolicyGeographicScope : policy.getGeographicScopes()) {
            projectPolicyGeographicScope.setRepIndGeographicScope(repIndGeographicScopeManager
              .getRepIndGeographicScopeById(projectPolicyGeographicScope.getRepIndGeographicScope().getId()));

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
          // Load Regions
          if (policy.getRegions() != null) {
            for (ProjectPolicyRegion projectPolicyRegion : policy.getRegions()) {
              projectPolicyRegion
                .setLocElement(locElementManager.getLocElementById(projectPolicyRegion.getLocElement().getId()));
            }
          }
        }

        if (haveCountries) {
          // Load Countries
          if (policy.getCountriesIdsText() != null) {
            String[] countriesText = policy.getCountriesIdsText().replace("[", "").replace("]", "").split(",");
            List<String> countries = new ArrayList<>();
            for (String value : Arrays.asList(countriesText)) {
              countries.add(value.trim());
            }
            policy.setCountriesIds(countries);
          }
        }

        // Policy Type ( Whose Policy is This ? ) List Autosave
        if (policy.getOwners() != null) {
          for (ProjectPolicyOwner projectPolicyOwner : policy.getOwners()) {
            projectPolicyOwner.setRepIndPolicyType(
              repIndPolicyTypeManager.getRepIndPolicyTypeById(projectPolicyOwner.getRepIndPolicyType().getId()));
          }
        }

        // Centers List Autosave
        if (policy.getCenters() != null) {
          for (ProjectPolicyCenter projectPolicyCenter : policy.getCenters()) {
            projectPolicyCenter
              .setInstitution(institutionManager.getInstitutionById(projectPolicyCenter.getInstitution().getId()));
          }
        }

        // Crp List Autosave
        if (policy.getCrps() != null) {
          for (ProjectPolicyCrp projectPolicyCrp : policy.getCrps()) {
            projectPolicyCrp
              .setGlobalUnit(globalUnitManager.getGlobalUnitById(projectPolicyCrp.getGlobalUnit().getId()));
          }
        }


        // SubIdos List Autosave
        if (policy.getSubIdos() != null) {
          for (ProjectPolicySubIdo projectPolicySubIdo : policy.getSubIdos()) {
            projectPolicySubIdo
              .setSrfSubIdo(srfSubIdoManager.getSrfSubIdoById(projectPolicySubIdo.getSrfSubIdo().getId()));
          }
        }

        // Innovations List Autosave
        if (policy.getInnovations() != null) {
          for (ProjectPolicyInnovation projectPolicyInnovation : policy.getInnovations()) {
            projectPolicyInnovation.setProjectInnovation(projectInnovationManager
              .getProjectInnovationById(projectPolicyInnovation.getProjectInnovation().getId()));
          }
        }

        // Milestones List Autosave
        if (policy.getMilestones() != null) {
          for (PolicyMilestone policyMilestone : policy.getMilestones()) {
            policyMilestone
              .setCrpMilestone(crpMilestoneManager.getCrpMilestoneById(policyMilestone.getCrpMilestone().getId()));
          }
        }

        // Evidences List Autosave
        if (policy.getEvidences() != null) {
          for (ProjectExpectedStudyPolicy projectPolicyEvidence : policy.getEvidences()) {
            projectPolicyEvidence.setProjectExpectedStudy(projectExpectedStudyManager
              .getProjectExpectedStudyById(projectPolicyEvidence.getProjectExpectedStudy().getId()));
          }
        }

        // Cgiar Cross Cutting Markers Autosave
        if (policy.getCrossCuttingMarkers() != null) {
          for (ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker : policy.getCrossCuttingMarkers()) {
            projectPolicyCrossCuttingMarker.setCgiarCrossCuttingMarker(cgiarCrossCuttingMarkerManager
              .getCgiarCrossCuttingMarkerById(projectPolicyCrossCuttingMarker.getCgiarCrossCuttingMarker().getId()));
            if (projectPolicyCrossCuttingMarker.getRepIndGenderYouthFocusLevel() != null) {
              if (projectPolicyCrossCuttingMarker.getRepIndGenderYouthFocusLevel().getId() != -1) {
                projectPolicyCrossCuttingMarker
                  .setRepIndGenderYouthFocusLevel(focusLevelManager.getRepIndGenderYouthFocusLevelById(
                    projectPolicyCrossCuttingMarker.getRepIndGenderYouthFocusLevel().getId()));
              }
            }
          }
        }

        // Policy SDG Targets List Autosave
        if (this.policy.getSdgTargets() != null) {
          for (ProjectPolicySdgTarget projectPolicySdgTarget : this.policy.getSdgTargets()) {
            if (projectPolicySdgTarget != null && projectPolicySdgTarget.getSdgTarget() != null
              && projectPolicySdgTarget.getSdgTarget().getId() != null) {
              projectPolicySdgTarget
                .setSdgTarget(this.sdgTargetsManager.getSDGTargetsById(projectPolicySdgTarget.getSdgTarget().getId()));
            }
          }
        }

        this.setDraft(true);

      } else {
        this.setDraft(false);

        if (policy.getProjectPolicyInfo() == null) {
          policy.getProjectPolicyInfo(phase);
        }

        // Setup Geographic Scope
        if (policy.getProjectPolicyGeographicScopes() != null) {
          policy.setGeographicScopes(new ArrayList<>(policy.getProjectPolicyGeographicScopes().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Policy Countries List
        if (policy.getProjectPolicyCountries() == null) {
          policy.setCountries(new ArrayList<>());

        } else {
          List<ProjectPolicyCountry> geographics =
            projectPolicyCountryManager.getPolicyCountrybyPhase(policy.getId(), phase.getId());

          // Load Countries
          policy.setCountries(geographics.stream().filter(sc -> sc.getLocElement().getLocElementType().getId() == 2)
            .collect(Collectors.toList()));

        }

        if (policy.getProjectPolicyRegions() == null) {
          policy.setRegions(new ArrayList<>());
        } else {
          List<ProjectPolicyRegion> geographics =
            projectPolicyRegionManager.getPolicyRegionbyPhase(policy.getId(), phase.getId());
          // Insitutions List Autosave
          if (policy.getCenters() != null) {
            for (ProjectPolicyCenter projectPolicyCenter : policy.getCenters()) {
              projectPolicyCenter
                .setInstitution(institutionManager.getInstitutionById(projectPolicyCenter.getInstitution().getId()));
            }
          }

          // Load Regions
          policy.setRegions(geographics.stream().filter(sc -> sc.getLocElement().getLocElementType().getId() == 1)
            .collect(Collectors.toList()));
        }

        // Policy Type ( Whose Policy is This ? ) List
        if (policy.getProjectPolicyOwners() != null) {
          policy.setOwners(new ArrayList<>(policy.getProjectPolicyOwners().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Crp List
        if (policy.getProjectPolicyCrps() != null) {
          policy.setCrps(new ArrayList<>(policy.getProjectPolicyCrps().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // SubIdos List
        if (policy.getProjectPolicySubIdos() != null) {
          policy.setSubIdos(new ArrayList<>(policy.getProjectPolicySubIdos().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));

          // Get the ID of the principal Sub IDO if exist
          if (policy.getSubIdos(phase) != null) {
            List<ProjectPolicySubIdo> projectPolicies = new ArrayList<ProjectPolicySubIdo>();

            projectPolicies = policy.getSubIdos(phase).stream()
              .filter(p -> p != null && p.isActive() && p.getPrimary() != null && p.getPrimary())
              .collect(Collectors.toList());

            if (projectPolicies != null && projectPolicies.size() > 0 && projectPolicies.get(0) != null) {
              subIdoPrimaryId = projectPolicies.get(0).getSrfSubIdo().getId(); //
              srfSubIdoPrimary = projectPolicies.get(0).getSrfSubIdo().getId(); //
            }
          }

        }

        // Innovations List
        if (policy.getProjectPolicyInnovations() != null) {
          policy.setInnovations(new ArrayList<>(policy.getProjectPolicyInnovations().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Policy Milestones
        if (policy.getPolicyMilestones() != null) {
          policy.setMilestones(new ArrayList<>(policy.getPolicyMilestones().stream()
            .filter(o -> o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));

          // Get the ID of the principal Sub IDO if exist
          if (policy.getMilestones() != null) {
            List<PolicyMilestone> projectPolicies = new ArrayList<PolicyMilestone>();

            projectPolicies = policy
              .getMilestones().stream().filter(p -> p != null && p.isActive() && p.getPrimary() != null
                && p.getPrimary() && p.getPhase() != null && p.getPhase().getId().equals(phase.getId()))
              .collect(Collectors.toList());

            if (projectPolicies != null && projectPolicies.size() > 0 && projectPolicies.get(0) != null) {
              milestonePrimaryId = projectPolicies.get(0).getCrpMilestone().getId(); //
              crpMilestonePrimary = projectPolicies.get(0).getCrpMilestone().getId(); //
            }
          }
        }

        // Evidence List
        if (policy.getProjectExpectedStudyPolicies() != null) {
          policy.setEvidences(new ArrayList<>(policy.getProjectExpectedStudyPolicies().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Cgiar Cross Cutting Markers List
        if (policy.getCrossCuttingMarkers() != null) {
          policy.setCrossCuttingMarkers(new ArrayList<>(policy.getProjectPolicyCrossCuttingMarkers().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Load Information (SDG Targets) for Alliance Global unit - Just for active specificity
        if (this.hasSpecificities(APConstants.CRP_ENABLE_NEXUS_LEVER_SDG_FIELDS)) {
          // Policy SDG Targets List
          if (this.policy.getProjectPolicySdgTargets() != null) {
            this.policy.setSdgTargets(new ArrayList<>(this.policy.getProjectPolicySdgTargets().stream()
              .filter(o -> o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
          }
        }

      }

      if (!this.isDraft()) {
        if (policy.getCountries() != null) {
          for (ProjectPolicyCountry country : policy.getCountries()) {
            policy.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
          }
        }
      }

      // Getting The lists

      // Geographic Scope List
      geographicScopes = repIndGeographicScopeManager.findAll();

      // Regions for Geographic Scope Regional Selection
      regions = locElementManager.findAll().stream()
        .filter(c -> c.getLocElementType().getId().intValue() == 1 && c.isActive() && c.getIsoNumeric() != null)
        .collect(Collectors.toList());

      // Countries for Geographic Scope National - Multinational Selection
      countries = locElementManager.findAll().stream()
        .filter(c -> c.getLocElementType().getId().intValue() == 2 && c.isActive()).collect(Collectors.toList());

      // Organization Type List
      organizationTypes = repIndOrganizationTypeManager.findAll();

      // Level of Maturity List
      stageProcesses = new ArrayList<>(repIndStageProcessManager.findAll().stream()
        .filter(p -> p.getRepIndStageStudy() != null).collect(Collectors.toList())); // TODO change version.

      // Policy/Investment Type List
      policyInvestimentTypes = repIndPolicyInvestimentTypeManager.findAll();

      // Whose Policy is This ? List
      policyTypes = repIndPolicyTypeManager.findAll();

      // SubIdos List
      subIdos = srfSubIdoManager.findAll();

      // Cross Cutting Values List
      focusLevels = focusLevelManager.findAll();

      // Cross Cutting Markers
      cgiarCrossCuttingMarkers = cgiarCrossCuttingMarkerManager.findAll();

      // institutions
      List<Institution> centersTemp = new ArrayList<Institution>();
      List<ProjectPartner> projectPartnerList = project.getProjectPartners().stream()
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

      if (this.hasSpecificities(APConstants.CRP_ENABLE_NEXUS_LEVER_SDG_FIELDS)) {
        // SGD Targets
        sdgTargetList = sdgTargetsManager.findAll();
      }

      Project projectL = projectManager.getProjectById(projectID);

      // Get the innovations List
      innovationList = new ArrayList<>();

      List<ProjectInnovation> innovations =
        projectL.getProjectInnovations().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      for (ProjectInnovation projectInnovation : innovations) {
        if (projectInnovation.getProjectInnovationInfo(this.getActualPhase()) != null) {
          innovationList.add(projectInnovation);
        }
      }

      // Institutions List
      if (policy.getProjectPolicyCenters() != null) {
        policy.setCenters(new ArrayList<>(policy.getProjectPolicyCenters().stream()
          .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
      }

      /*
       * Get the milestone List
       */
      milestoneList = new ArrayList<>();

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
                milestoneList.add(projectMilestone.getCrpMilestone());
              }
            }
          }

          // Add 'N/A' to milestone list dropdown if there is not selected any milestone
          /*
           * CrpMilestone milestoneTemp = new CrpMilestone();
           * milestoneTemp.setTitle("Not Applicable"); if(policy.getMilestones()== null) {
           * milestoneList.add(milestoneTemp); }
           */
        }
      }

      /*
       * Update 4/25/2019 Adding Shared Project Innovation in the lists.
       */
      List<ProjectInnovationShared> innovationsShared = projectInnovationSharedManager.findAll().stream()
        .filter(p -> p != null && p.getId() != null && p.getProject() != null && p.getProject().getId() != null
          && p.getProject().equals(this.project) && p.isActive() && p.getPhase() != null && p.getPhase().getId() != null
          && p.getPhase().equals(this.getActualPhase()) && p.getProjectInnovation() != null
          && p.getProjectInnovation().getId() != null && p.getProjectInnovation().isActive()
          && p.getProjectInnovation().getProjectInnovationInfo(this.getActualPhase()) != null)
        .collect(Collectors.toList());
      /*
       * List<ProjectInnovationShared> innovationShareds = new
       * ArrayList<>(project.getProjectInnovationShareds().stream()
       * .filter(px -> px.isActive() && px.getPhase().getId() == this.getActualPhase().getId()
       * && px.getProjectInnovation().isActive()
       * && px.getProjectInnovation().getProjectInnovationInfo(this.getActualPhase()) != null)
       * .collect(Collectors.toList()));
       */
      if (innovationsShared != null && innovationsShared.size() > 0) {
        for (ProjectInnovationShared innovationShared : innovationsShared) {
          if (!innovationList.contains(innovationShared.getProjectInnovation())) {
            this.innovationList.add(innovationShared.getProjectInnovation());
          }
        }
      }

      List<ProjectExpectedStudy> allProjectStudies = new ArrayList<ProjectExpectedStudy>();

      // Load Studies
      List<ProjectExpectedStudy> studies = project.getProjectExpectedStudies().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudyInfo(this.getActualPhase()) != null)
        .collect(Collectors.toList());
      if (studies != null && studies.size() > 0) {
        allProjectStudies.addAll(studies);
      }

      // Load Shared studies
      List<ExpectedStudyProject> expectedStudyProject = new ArrayList<>(project.getExpectedStudyProjects().stream()
        .filter(px -> px.isActive() && px.getPhase().getId().equals(this.getActualPhase().getId())
          && px.getProjectExpectedStudy().isActive()
          && px.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getActualPhase()) != null)
        .collect(Collectors.toList()));
      if (expectedStudyProject != null && expectedStudyProject.size() > 0) {
        for (ExpectedStudyProject expectedStudy : expectedStudyProject) {
          if (!allProjectStudies.contains(expectedStudy.getProjectExpectedStudy())) {
            allProjectStudies.add(expectedStudy.getProjectExpectedStudy());
          }
        }
      }

      if (allProjectStudies != null && allProjectStudies.size() > 0) {
        // Every study of the current cycle year will be editable
        expectedStudyList = new ArrayList<ProjectExpectedStudy>();
        expectedStudyList = allProjectStudies.stream()
          .filter(ex -> ex.isActive() && ex.getProjectExpectedStudyInfo(phase) != null
            && ex.getProjectExpectedStudyInfo().getStudyType() != null
            && ex.getProjectExpectedStudyInfo().getStudyType().getId().intValue() == 1 && ex.getProject() != null)
          .collect(Collectors.toList());
      }

      List<ProjectExpectedStudy> evidences = projectExpectedStudyManager.findAll().stream()
        .filter(s -> s != null && s.getProject() == null && s.getProjectExpectedStudyInfo(this.getActualPhase()) != null
          && s.getProjectExpectedStudyInfo().getPhase() != null
          && s.getProjectExpectedStudyInfo().getPhase().getId().equals(this.getActualPhase().getId()))
        .collect(Collectors.toList());
      if (evidences != null && !evidences.isEmpty()) {
        if (expectedStudyList != null && !expectedStudyList.isEmpty()) {
          expectedStudyList.addAll(evidences);
        } else {
          expectedStudyList = evidences;
        }
      }

      // Crps/Platforms List
      crps = globalUnitManager.findAll().stream()
        .filter(gu -> gu.isActive() && (gu.getGlobalUnitType().getId() == 1 || gu.getGlobalUnitType().getId() == 3))
        .collect(Collectors.toList());

      List<ProjectPolicyCrp> tempPcrp = null;
      // Update crp list - Delete the actual crp from the list except if this crp was

      if (policy.getCrps() != null && policy.getCrps().stream()
        .filter(x -> x != null && x.getGlobalUnit().getId().equals(this.getCurrentGlobalUnit().getId())) != null) {
        tempPcrp = policy.getCrps().stream()
          .filter(x -> x != null && x.getGlobalUnit().getId().equals(this.getCurrentGlobalUnit().getId()))
          .collect(Collectors.toList());
      }

      if (tempPcrp != null && tempPcrp.size() == 0 && this.getCurrentGlobalUnit() != null) {
        crps.remove(this.getCurrentGlobalUnit());
      }
    }

    policyDB = projectPolicyManager.getProjectPolicyById(policyID);

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_POLICIES_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (policy.getCountries() != null) {
        policy.getCountries().clear();
      }

      if (policy.getRegions() != null) {
        policy.getRegions().clear();
      }

      if (policy.getOwners() != null) {
        policy.getOwners().clear();
      }

      if (policy.getCrps() != null) {
        policy.getCrps().clear();
      }

      if (policy.getSubIdos() != null) {
        policy.getSubIdos().clear();
      }

      if (policy.getEvidences() != null) {
        policy.getEvidences().clear();
      }

      if (policy.getInnovations() != null) {
        policy.getInnovations().clear();
      }

      if (policy.getMilestones() != null) {
        policy.getMilestones().clear();
      }

      if (policy.getCrossCuttingMarkers() != null) {
        policy.getCrossCuttingMarkers().clear();
      }

      if (policy.getGeographicScopes() != null) {
        policy.getGeographicScopes().clear();
      }

      if (policy.getCenters() != null) {
        policy.getCenters().clear();
      }

      if (policy.getSdgTargets() != null) {
        policy.getSdgTargets().clear();
      }

      // HTTP Post info Values
      policy.getProjectPolicyInfo().setRepIndPolicyInvestimentType(null);
      policy.getProjectPolicyInfo().setRepIndStageProcess(null);

    }

    // SrfIDO
    idoList = new HashMap<>();
    srfIdos = new ArrayList<>();
    for (SrfIdo srfIdo : srfIdoManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
      idoList.put(srfIdo.getId(), srfIdo.getDescription());

      srfIdo.setSubIdos(srfIdo.getSrfSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      srfIdos.add(srfIdo);
    }

  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {
      Phase phase = this.getActualPhase();

      Path path = this.getAutoSaveFilePath();

      policy.setProject(project);
      this.saveCrps(policyDB, phase, this.hasSpecificities(APConstants.CRP_ENABLE_NEXUS_LEVER_SDG_FIELDS));
      this.saveOwners(policyDB, phase);
      this.saveSubIdos(policyDB, phase);
      this.saveCrossCutting(policyDB, phase);
      this.saveInnovations(policyDB, phase);
      this.saveEvidence(policyDB, phase);
      if (!this.hasSpecificities(APConstants.CRP_ENABLE_NEXUS_LEVER_SDG_FIELDS)) {
        this.saveMilestones(policyDB, phase);
        this.saveCenters(policyDB, phase);
      }

      // Save Geographic Scope Data
      this.saveGeographicScopes(policyDB, phase);

      if (this.hasSpecificities(APConstants.CRP_ENABLE_NEXUS_LEVER_SDG_FIELDS)) {
        this.saveSdgTargets(this.policyDB, phase);
      }

      boolean haveRegions = false;
      boolean haveCountries = false;

      if (policy.getGeographicScopes() != null) {
        for (ProjectPolicyGeographicScope projectPolicyGeographicScope : policy.getGeographicScopes()) {

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
        this.saveRegions(policyDB, phase);
      } else {
        this.deleteLocElements(policyDB, phase, false);
      }

      if (haveCountries) {

        // Save the Countries List (ProjectPolicyCountry)
        if (policy.getCountriesIds() != null || !policy.getCountriesIds().isEmpty()) {

          List<ProjectPolicyCountry> countries =
            projectPolicyCountryManager.getPolicyCountrybyPhase(policy.getId(), this.getActualPhase().getId());
          List<ProjectPolicyCountry> countriesSave = new ArrayList<>();
          for (String countryIds : policy.getCountriesIds()) {
            ProjectPolicyCountry countryInn = new ProjectPolicyCountry();
            countryInn.setLocElement(locElementManager.getLocElementByISOCode(countryIds));
            countryInn.setProjectPolicy(policy);
            countryInn.setPhase(this.getActualPhase());
            countriesSave.add(countryInn);
            if (!countries.contains(countryInn)) {
              projectPolicyCountryManager.saveProjectPolicyCountry(countryInn);
            }
          }

          for (ProjectPolicyCountry projectPolicyCountry : countries) {
            if (!countriesSave.contains(projectPolicyCountry)) {
              projectPolicyCountryManager.deleteProjectPolicyCountry(projectPolicyCountry.getId());
            }
          }
        }
      } else {
        this.deleteLocElements(policyDB, phase, true);
      }

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_POLICY_INFOS_RELATION);
      relationsName.add(APConstants.PROJECT_POLICY_COUNTRY_RELATION);
      relationsName.add(APConstants.PROJECT_POLICY_CRP_RELATION);
      relationsName.add(APConstants.PROJECT_POLICY_OWNER_RELATION);
      relationsName.add(APConstants.PROJECT_POLICY_SUB_IDO_RELATION);
      relationsName.add(APConstants.PROJECT_POLICY_CENTERS_RELATION);
      relationsName.add(APConstants.PROJECT_POLICY_REGION_RELATION);
      relationsName.add(APConstants.PROJECT_POLICY_INNOVATION_RELATION);
      relationsName.add(APConstants.PROJECT_POLICY_MILESTONE_RELATION);
      relationsName.add(APConstants.PROJECT_POLICY_CROSS_CUTTING_RELATION);
      relationsName.add(APConstants.PROJECT_POLICY_EVIDENCE_RELATION);
      relationsName.add(APConstants.PROJECT_POLICY_CENTER_RELATION);

      policy.setModificationJustification(this.getJustification());

      // Save Project Policy Info
      policy.getProjectPolicyInfo().setPhase(this.getActualPhase());
      policy.getProjectPolicyInfo().setProjectPolicy(policy);

      if (policy.getProjectPolicyInfo().getRepIndPolicyInvestimentType() != null) {
        if (policy.getProjectPolicyInfo().getRepIndPolicyInvestimentType().getId() == -1) {
          policy.getProjectPolicyInfo().setRepIndPolicyInvestimentType(null);
        } else {
          if (policy.getProjectPolicyInfo().getRepIndPolicyInvestimentType() != null
            && policy.getProjectPolicyInfo().getRepIndPolicyInvestimentType().getId() != null
            && policy.getProjectPolicyInfo().getRepIndPolicyInvestimentType().getId().longValue() != 3) {
            policy.getProjectPolicyInfo().setAmount(null);
          }
        }
      }

      if (policy.getProjectPolicyInfo().getRepIndStageProcess() != null) {
        if (policy.getProjectPolicyInfo().getRepIndStageProcess().getId() == -1) {
          policy.getProjectPolicyInfo().setRepIndStageProcess(null);
        }
      }

      projectPolicyInfoManager.saveProjectPolicyInfo(policy.getProjectPolicyInfo());
      /**
       * The following is required because we need to update something on
       * the @ProjectInnovation if we want a row created in the auditlog table.
       */
      // this.setModificationJustification(policy);
      projectPolicyManager.saveProjectPolicy(policy, this.getActionName(), relationsName, this.getActualPhase());

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
    } else

    {

      return NOT_AUTHORIZED;
    }

  }

  /**
   * Save Project Policy Center Information
   * 
   * @param projectPolicy
   * @param phase
   */
  public void saveCenters(ProjectPolicy projectPolicy, Phase phase) {

    // Search and deleted form Information
    if (projectPolicy.getProjectPolicyCenters() != null && projectPolicy.getProjectPolicyCenters().size() > 0) {

      List<ProjectPolicyCenter> centerPrev = new ArrayList<>(projectPolicy.getProjectPolicyCenters().stream()
        .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectPolicyCenter policyCenter : centerPrev) {
        if (policy.getCenters() == null || !policy.getCenters().contains(policyCenter)) {
          projectPolicyCenterManager.deleteProjectPolicyCenter(policyCenter.getId());
        }
      }
    }

    // Save form Information
    if (policy.getCenters() != null) {
      for (ProjectPolicyCenter policyCenter : policy.getCenters()) {
        if (policyCenter.getId() == null) {
          ProjectPolicyCenter policyCenterSave = new ProjectPolicyCenter();
          policyCenterSave.setProjectPolicy(projectPolicy);
          policyCenterSave.setPhase(phase);

          Institution institution = institutionManager.getInstitutionById(policyCenter.getInstitution().getId());

          policyCenterSave.setInstitution(institution);

          projectPolicyCenterManager.saveProjectPolicyCenter(policyCenterSave);
          // This is to add innovationCrpSave to generate correct auditlog.
          policy.getProjectPolicyCenters().add(policyCenterSave);
        }
      }
    }
  }

  /**
   * Save Project Policy CrossCutting Information
   * 
   * @param projectPolicy
   * @param phase
   */
  public void saveCrossCutting(ProjectPolicy projectPolicy, Phase phase) {

    // // Search and deleted form Information
    // if (projectPolicy.getProjectPolicyCrossCuttingMarkers() != null
    // && projectPolicy.getProjectPolicyCrossCuttingMarkers().size() > 0) {
    //
    // List<ProjectPolicyCrossCuttingMarker> crossCuttingPrev =
    // new ArrayList<>(projectPolicy.getProjectPolicyCrossCuttingMarkers().stream()
    // .filter(nu -> nu.isActive() && nu.getPhase().getId() ==
    // phase.getId()).collect(Collectors.toList()));
    //
    // for (ProjectPolicyCrossCuttingMarker crossCuttingOwner : crossCuttingPrev) {
    // if (policy.getCrossCuttingMarkers() == null ||
    // !policy.getCrossCuttingMarkers().contains(crossCuttingOwner)) {
    // projectPolicyCrossCuttingMarkerManager.deleteProjectPolicyCrossCuttingMarker(crossCuttingOwner.getId());
    // }
    // }
    // }

    // Save form Information
    if (policy.getCrossCuttingMarkers() != null) {
      for (ProjectPolicyCrossCuttingMarker crossCuttingOwner : policy.getCrossCuttingMarkers()) {
        if (crossCuttingOwner.getId() == null) {
          ProjectPolicyCrossCuttingMarker crossCuttingOwnerSave = new ProjectPolicyCrossCuttingMarker();
          crossCuttingOwnerSave.setProjectPolicy(projectPolicy);
          crossCuttingOwnerSave.setPhase(phase);

          CgiarCrossCuttingMarker cgiarCrossCuttingMarker = cgiarCrossCuttingMarkerManager
            .getCgiarCrossCuttingMarkerById(crossCuttingOwner.getCgiarCrossCuttingMarker().getId());

          crossCuttingOwnerSave.setCgiarCrossCuttingMarker(cgiarCrossCuttingMarker);

          if (crossCuttingOwner.getRepIndGenderYouthFocusLevel() != null) {
            if (crossCuttingOwner.getRepIndGenderYouthFocusLevel().getId() != null
              && crossCuttingOwner.getRepIndGenderYouthFocusLevel().getId() != -1) {
              RepIndGenderYouthFocusLevel focusLevel = focusLevelManager
                .getRepIndGenderYouthFocusLevelById(crossCuttingOwner.getRepIndGenderYouthFocusLevel().getId());
              crossCuttingOwnerSave.setRepIndGenderYouthFocusLevel(focusLevel);
            } else {
              crossCuttingOwnerSave.setRepIndGenderYouthFocusLevel(null);
            }
          } else {
            crossCuttingOwnerSave.setRepIndGenderYouthFocusLevel(null);
          }

          projectPolicyCrossCuttingMarkerManager.saveProjectPolicyCrossCuttingMarker(crossCuttingOwnerSave);
          // This is to add innovationCrpSave to generate correct auditlog.
          policy.getProjectPolicyCrossCuttingMarkers().add(crossCuttingOwnerSave);
        } else {
          boolean hasChanges = false;
          ProjectPolicyCrossCuttingMarker crossCuttingOwnerSave =
            projectPolicyCrossCuttingMarkerManager.getProjectPolicyCrossCuttingMarkerById(crossCuttingOwner.getId());

          if (crossCuttingOwner.getRepIndGenderYouthFocusLevel() != null) {
            if (crossCuttingOwner.getRepIndGenderYouthFocusLevel().getId() != null
              && crossCuttingOwner.getRepIndGenderYouthFocusLevel().getId() != -1) {

              if (crossCuttingOwnerSave.getRepIndGenderYouthFocusLevel() != null) {
                if (crossCuttingOwner.getRepIndGenderYouthFocusLevel().getId() != crossCuttingOwnerSave
                  .getRepIndGenderYouthFocusLevel().getId()) {
                  RepIndGenderYouthFocusLevel focusLevel = focusLevelManager
                    .getRepIndGenderYouthFocusLevelById(crossCuttingOwner.getRepIndGenderYouthFocusLevel().getId());
                  crossCuttingOwnerSave.setRepIndGenderYouthFocusLevel(focusLevel);
                  hasChanges = true;
                }
              } else {
                RepIndGenderYouthFocusLevel focusLevel = focusLevelManager
                  .getRepIndGenderYouthFocusLevelById(crossCuttingOwner.getRepIndGenderYouthFocusLevel().getId());
                crossCuttingOwnerSave.setRepIndGenderYouthFocusLevel(focusLevel);
                hasChanges = true;
              }

            } else {
              crossCuttingOwnerSave.setRepIndGenderYouthFocusLevel(null);
              hasChanges = true;
            }
          } else {
            crossCuttingOwnerSave.setRepIndGenderYouthFocusLevel(null);
            hasChanges = true;
          }

          if (hasChanges) {
            projectPolicyCrossCuttingMarkerManager.saveProjectPolicyCrossCuttingMarker(crossCuttingOwnerSave);
          }
          // This is to add innovationCrpSave to generate correct auditlog.
          policy.getProjectPolicyCrossCuttingMarkers().add(crossCuttingOwnerSave);

        }
      }
    }
  }

  /**
   * Save Project Policy Crp Information
   * 
   * @param projectPolicy
   * @param phase
   */
  public void saveCrps(ProjectPolicy projectPolicy, Phase phase, boolean isAlliance) {

    // Search and deleted form Information
    if (projectPolicy.getProjectPolicyCrps() != null && projectPolicy.getProjectPolicyCrps().size() > 0) {

      List<ProjectPolicyCrp> crpPrev = new ArrayList<>(projectPolicy.getProjectPolicyCrps().stream()
        .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectPolicyCrp policyCrp : crpPrev) {
        if (policy.getCrps() == null || !policy.getCrps().contains(policyCrp)) {
          projectPolicyCrpManager.deleteProjectPolicyCrp(policyCrp.getId());
        }
      }

      // only alliance, if radiobutton selected not "yes"
      if (isAlliance && this.policy.getProjectPolicyInfo(phase) != null
        && (this.policy.getProjectPolicyInfo(phase).getHasLegacyCrpContribution() == null
          || !this.policy.getProjectPolicyInfo(phase).getHasLegacyCrpContribution())) {
        for (ProjectPolicyCrp policyCrp : projectPolicy.getProjectPolicyCrps().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())) {
          this.projectPolicyCrpManager.deleteProjectPolicyCrp(policyCrp.getId());
        }
      }
    }

    // Save form Information
    if (!isAlliance || (isAlliance && this.policy.getProjectPolicyInfo(phase) != null
      && BooleanUtils.isTrue(this.policy.getProjectPolicyInfo(phase).getHasLegacyCrpContribution()))) {
      boolean hasCurrentCrp = false;
      // Save form Information
      if (policy.getCrps() != null) {
        for (ProjectPolicyCrp policyCrp : policy.getCrps()) {
          GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(policyCrp.getGlobalUnit().getId());
          hasCurrentCrp = hasCurrentCrp || (globalUnit != null && this.getCurrentCrp().equals(globalUnit));
          if (policyCrp.getId() == null) {
            ProjectPolicyCrp policyCrpSave = new ProjectPolicyCrp();
            policyCrpSave.setProjectPolicy(projectPolicy);
            policyCrpSave.setPhase(phase);

            policyCrpSave.setGlobalUnit(globalUnit);

            projectPolicyCrpManager.saveProjectPolicyCrp(policyCrpSave);
            // This is to add innovationCrpSave to generate correct auditlog.
            policy.getProjectPolicyCrps().add(policyCrpSave);
          }
        }

        if (!hasCurrentCrp && !this.hasSpecificities(APConstants.CRP_ENABLE_NEXUS_LEVER_SDG_FIELDS)) {
          ProjectPolicyCrp policyCrpSave = new ProjectPolicyCrp();
          policyCrpSave.setProjectPolicy(projectPolicy);
          policyCrpSave.setPhase(phase);

          GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(this.getCurrentCrp().getId());

          policyCrpSave.setGlobalUnit(globalUnit);

          projectPolicyCrpManager.saveProjectPolicyCrp(policyCrpSave);
          // This is to add innovationCrpSave to generate correct auditlog.
          policy.getProjectPolicyCrps().add(policyCrpSave);
        }
      }
    }
  }

  /**
   * Save Policy-Evidence Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveEvidence(ProjectPolicy projectPolicy, Phase phase) {

    // Search and deleted form Information
    if (projectPolicy.getProjectExpectedStudyPolicies() != null
      && projectPolicy.getProjectExpectedStudyPolicies().size() > 0) {
      List<ProjectExpectedStudyPolicy> policyPrev =
        new ArrayList<>(projectPolicy.getProjectExpectedStudyPolicies().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectExpectedStudyPolicy studyPolicy : policyPrev) {
        if (policy.getEvidences() == null || !policy.getEvidences().contains(studyPolicy)) {
          projectExpectedStudyPolicyManager.deleteProjectExpectedStudyPolicy(studyPolicy.getId());
        }
      }
    }

    // Save form Information
    if (policy.getEvidences() != null) {
      for (ProjectExpectedStudyPolicy studyPolicy : policy.getEvidences()) {
        if (studyPolicy.getId() == null) {
          ProjectExpectedStudyPolicy studyPolicySave = new ProjectExpectedStudyPolicy();
          studyPolicySave.setProjectPolicy(projectPolicy);
          studyPolicySave.setPhase(phase);

          ProjectExpectedStudy expectedStudy =
            projectExpectedStudyManager.getProjectExpectedStudyById(studyPolicy.getProjectExpectedStudy().getId());

          studyPolicySave.setProjectExpectedStudy(expectedStudy);

          projectExpectedStudyPolicyManager.saveProjectExpectedStudyPolicy(studyPolicySave);
          // This is to add studyLinkSave to generate correct auditlog.
          expectedStudy.getProjectExpectedStudyPolicies().add(studyPolicySave);
        }
      }
    }
  }

  /**
   * Save Project Policy Geographic Scopes Information
   * 
   * @param projectPolicy
   * @param phase
   */
  public void saveGeographicScopes(ProjectPolicy projectPolicy, Phase phase) {

    // Search and deleted form Information
    if (projectPolicy.getProjectPolicyGeographicScopes() != null
      && projectPolicy.getProjectPolicyGeographicScopes().size() > 0) {

      List<ProjectPolicyGeographicScope> scopePrev =
        new ArrayList<>(projectPolicy.getProjectPolicyGeographicScopes().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectPolicyGeographicScope scope : scopePrev) {
        if (policy.getGeographicScopes() == null || !policy.getGeographicScopes().contains(scope)) {
          projectPolicyGeographicScopeManager.deleteProjectPolicyGeographicScope(scope.getId());
        }
      }
    }

    // Save form Information
    if (policy.getGeographicScopes() != null) {
      for (ProjectPolicyGeographicScope scope : policy.getGeographicScopes()) {
        if (scope.getId() == null) {
          ProjectPolicyGeographicScope scopeSave = new ProjectPolicyGeographicScope();
          scopeSave.setProjectPolicy(projectPolicy);
          scopeSave.setPhase(phase);

          RepIndGeographicScope geoScope =
            repIndGeographicScopeManager.getRepIndGeographicScopeById(scope.getRepIndGeographicScope().getId());

          scopeSave.setRepIndGeographicScope(geoScope);

          projectPolicyGeographicScopeManager.saveProjectPolicyGeographicScope(scopeSave);
          // This is to add to generate correct auditlog.
          policy.getProjectPolicyGeographicScopes().add(scopeSave);
        }
      }
    }
  }

  /**
   * Save Project Policy Innovations Information
   * 
   * @param projectPolicy
   * @param phase
   */
  public void saveInnovations(ProjectPolicy projectPolicy, Phase phase) {

    // Search and deleted form Information
    if (projectPolicy.getProjectPolicyInnovations() != null && projectPolicy.getProjectPolicyInnovations().size() > 0) {

      List<ProjectPolicyInnovation> innovationPrev =
        new ArrayList<>(projectPolicy.getProjectPolicyInnovations().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectPolicyInnovation policyInnovation : innovationPrev) {
        if (policy.getInnovations() == null || !policy.getInnovations().contains(policyInnovation)) {
          projectPolicyInnovationManager.deleteProjectPolicyInnovation(policyInnovation.getId());
        }
      }
    }

    // Save form Information
    if (policy.getInnovations() != null) {
      for (ProjectPolicyInnovation policyInnovation : policy.getInnovations()) {
        if (policyInnovation.getId() == null) {
          ProjectPolicyInnovation policyInnovationSave = new ProjectPolicyInnovation();
          policyInnovationSave.setProjectPolicy(projectPolicy);
          policyInnovationSave.setPhase(phase);

          ProjectInnovation innovation =
            projectInnovationManager.getProjectInnovationById(policyInnovation.getProjectInnovation().getId());

          policyInnovationSave.setProjectInnovation(innovation);

          projectPolicyInnovationManager.saveProjectPolicyInnovation(policyInnovationSave);
          // This is to add innovationCrpSave to generate correct auditlog.
          policy.getProjectPolicyInnovations().add(policyInnovationSave);
        }
      }
    }
  }

  /**
   * Save Project Policy Milestone Information
   * 
   * @param projectPolicy
   * @param phase
   */
  public void saveMilestones(ProjectPolicy projectPolicy, Phase phase) {

    // Search and deleted form Information
    if (projectPolicy.getPolicyMilestones() != null && projectPolicy.getPolicyMilestones().size() > 0) {

      List<PolicyMilestone> milestonePrev = new ArrayList<>(projectPolicy.getPolicyMilestones().stream()
        .filter(nu -> nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
      for (PolicyMilestone policyMilestone : milestonePrev) {
        if (policy.getMilestones() == null || !policy.getMilestones().contains(policyMilestone)) {
          policyMilestoneManager.deletePolicyMilestone(policyMilestone.getId());
        }
      }
    }

    // Save policy milestones only if boolean 'has milestones' selection is true
    if (policy.getProjectPolicyInfo().getHasMilestones() != null
      && policy.getProjectPolicyInfo().getHasMilestones() == true) {

      // Policy Milestones
      if (policy.getMilestones() != null) {

        for (PolicyMilestone policyMilestone : policy.getMilestones()) {
          if (policyMilestone.getId() == null) {
            PolicyMilestone policyMilestoneSave = new PolicyMilestone();
            policyMilestoneSave.setPolicy(projectPolicy);
            policyMilestoneSave.setPhase(phase);
            policyMilestoneSave.setPrimary(policyMilestone.getPrimary());

            if (policy.getMilestones() != null && policy.getMilestones().size() == 1) {
              policyMilestoneSave.setPrimary(true);
            }

            CrpMilestone milestone = crpMilestoneManager.getCrpMilestoneById(policyMilestone.getCrpMilestone().getId());
            policyMilestoneSave.setCrpMilestone(milestone);

            policyMilestoneManager.savePolicyMilestone(policyMilestoneSave);
            // This is to add milestoneCrpSave to generate correct auditlog.
            policy.getPolicyMilestones().add(policyMilestoneSave);
          } else {
            // if milestone already exist - save primary
            PolicyMilestone policyMilestoneSave = new PolicyMilestone();
            policyMilestoneSave = policyMilestoneManager.getPolicyMilestoneById(policyMilestone.getId());
            policyMilestoneSave.setPolicy(projectPolicy);
            policyMilestoneSave.setPhase(phase);
            if (policyMilestoneSave.getCrpMilestone() != null
              && policyMilestoneSave.getCrpMilestone().getId() != null) {
              CrpMilestone milestone =
                crpMilestoneManager.getCrpMilestoneById(policyMilestone.getCrpMilestone().getId());
              policyMilestoneSave.setCrpMilestone(milestone);
            }
            policyMilestoneSave.setPrimary(policyMilestone.getPrimary());

            if (policy.getMilestones() != null && policy.getMilestones().size() == 1) {
              policyMilestoneSave.setPrimary(true);
            }

            policyMilestoneManager.savePolicyMilestone(policyMilestoneSave);
            // This is to add milestoneCrpSave to generate correct auditlog.
            policy.getPolicyMilestones().add(policyMilestoneSave);

          }
        }
      }
    } else {
      // Delete all milestones for this policy
      if (policy.getMilestones() != null && policy.getMilestones().size() > 0) {
        for (PolicyMilestone policyMilestone : policy.getMilestones()) {
          try {
            CrpMilestone milestone = crpMilestoneManager.getCrpMilestoneById(policyMilestone.getId());
            if (milestone != null) {
              policyMilestoneManager.deletePolicyMilestone(policyMilestone.getId());
              // This is to add milestoneCrpSave to generate correct auditlog.
              policy.getPolicyMilestones()
                .remove(policyMilestoneManager.getPolicyMilestoneById(policyMilestone.getId()));
            }
          } catch (Exception e) {

          }

        }
      }
    }
  }

  /**
   * Save Project Policy Owner Information
   * 
   * @param projectPolicy
   * @param phase
   */
  public void saveOwners(ProjectPolicy projectPolicy, Phase phase) {

    // Search and deleted form Information
    if (projectPolicy.getProjectPolicyOwners() != null && projectPolicy.getProjectPolicyOwners().size() > 0) {

      List<ProjectPolicyOwner> ownerPrev = new ArrayList<>(projectPolicy.getProjectPolicyOwners().stream()
        .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectPolicyOwner policyOwner : ownerPrev) {
        if (policy.getOwners() == null || !policy.getOwners().contains(policyOwner)) {
          projectPolicyOwnerManager.deleteProjectPolicyOwner(policyOwner.getId());
        }
      }
    }

    // Save form Information
    if (policy.getOwners() != null) {
      for (ProjectPolicyOwner policyOwner : policy.getOwners()) {
        if (policyOwner.getId() == null) {
          ProjectPolicyOwner policyOwnerSave = new ProjectPolicyOwner();
          policyOwnerSave.setProjectPolicy(projectPolicy);
          policyOwnerSave.setPhase(phase);

          RepIndPolicyType repIndPolicyType =
            repIndPolicyTypeManager.getRepIndPolicyTypeById(policyOwner.getRepIndPolicyType().getId());

          policyOwnerSave.setRepIndPolicyType(repIndPolicyType);

          projectPolicyOwnerManager.saveProjectPolicyOwner(policyOwnerSave);
          // This is to add innovationCrpSave to generate correct auditlog.
          policy.getProjectPolicyOwners().add(policyOwnerSave);
        }
      }
    }
  }

  /**
   * Save Project Policy Geographic Scope When Regions is Selected Information
   * 
   * @param projectPolicy
   * @param phase
   */
  public void saveRegions(ProjectPolicy projectPolicy, Phase phase) {

    // Search and deleted form Information
    if (projectPolicy.getProjectPolicyRegions() != null && projectPolicy.getProjectPolicyRegions().size() > 0) {

      List<ProjectPolicyRegion> regionPrev = new ArrayList<>(projectPolicy.getProjectPolicyRegions().stream()
        .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectPolicyRegion policyRegion : regionPrev) {
        if (policy.getRegions() == null || !policy.getRegions().contains(policyRegion)) {
          projectPolicyRegionManager.deleteProjectPolicyRegion(policyRegion.getId());
        }
      }
    }

    // Save form Information
    if (policy.getRegions() != null) {
      for (ProjectPolicyRegion policyRegion : policy.getRegions()) {
        if (policyRegion.getId() == null) {
          ProjectPolicyRegion policyRegionSave = new ProjectPolicyRegion();
          policyRegionSave.setProjectPolicy(projectPolicy);
          policyRegionSave.setPhase(phase);

          LocElement locElement = locElementManager.getLocElementById(policyRegion.getLocElement().getId());

          policyRegionSave.setLocElement(locElement);

          projectPolicyRegionManager.saveProjectPolicyRegion(policyRegionSave);
          // This is to add innovationCrpSave to generate correct auditlog.
          policy.getProjectPolicyRegions().add(policyRegionSave);
        }
      }
    }
  }

  private void saveSdgTargets(ProjectPolicy projectPolicy, Phase phase) {
    // Search and deleted form Information
    if (projectPolicy.getProjectPolicySdgTargets() != null && !projectPolicy.getProjectPolicySdgTargets().isEmpty()) {
      List<ProjectPolicySdgTarget> sdgTargetPrev = new ArrayList<>(projectPolicy.getProjectPolicySdgTargets().stream()
        .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectPolicySdgTarget policySdgTarget : sdgTargetPrev) {
        if (this.policy.getSdgTargets() == null
          || !this.policy.getSdgTargets().contains(policySdgTarget) && policySdgTarget.getId() != null) {
          this.projectPolicySdgTargetManager.deleteProjectPolicySdgTarget(policySdgTarget.getId());
        }
      }
    }

    // Save form Information
    if (this.policy.getSdgTargets() != null) {
      for (ProjectPolicySdgTarget policySdgTarget : this.policy.getSdgTargets()) {
        if (policySdgTarget != null && policySdgTarget.getSdgTarget() != null
          && policySdgTarget.getSdgTarget().getId() != null) {
          if (policySdgTarget.getId() == null) {
            ProjectPolicySdgTarget policySdgTargetSave = new ProjectPolicySdgTarget();
            policySdgTargetSave.setProjectPolicy(projectPolicy);
            policySdgTargetSave.setPhase(phase);

            SdgTargets sdgTarget = this.sdgTargetsManager.getSDGTargetsById(policySdgTarget.getSdgTarget().getId());

            if (sdgTarget != null) {
              policySdgTargetSave.setSdgTarget(sdgTarget);

              policySdgTargetSave = this.projectPolicySdgTargetManager.saveProjectPolicySdgTarget(policySdgTargetSave);
              this.projectPolicySdgTargetManager.replicate(policySdgTargetSave, phase.getNext());
              // This is to add policyLinkSave to generate correct
              // auditlog.
              this.policy.getProjectPolicySdgTargets().add(policySdgTargetSave);
            }
          }
        }
      }
    }
  }

  /**
   * Save Project Policy SubIdos Information
   * 
   * @param projectPolicy
   * @param phase
   */
  public void saveSubIdos(ProjectPolicy projectPolicy, Phase phase) {

    // Search and deleted form Information
    if (projectPolicy.getProjectPolicySubIdos() != null && projectPolicy.getProjectPolicySubIdos().size() > 0) {

      List<ProjectPolicySubIdo> subIdoPrev = new ArrayList<>(projectPolicy.getProjectPolicySubIdos().stream()
        .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectPolicySubIdo policySubIdo : subIdoPrev) {
        if (policy.getSubIdos() == null || !policy.getSubIdos().contains(policySubIdo)) {
          projectPolicySubIdoManager.deleteProjectPolicySubIdo(policySubIdo.getId());
        }
      }
    }

    // Save form Information
    if (policy.getSubIdos() != null) {
      for (ProjectPolicySubIdo policySubIdo : policy.getSubIdos()) {
        if (policySubIdo.getId() == null) {
          ProjectPolicySubIdo policySubIdoSave = new ProjectPolicySubIdo();
          policySubIdoSave.setProjectPolicy(projectPolicy);
          policySubIdoSave.setPhase(phase);
          policySubIdoSave.setPrimary(policySubIdo.getPrimary());

          if (policy.getSubIdos() != null && policy.getSubIdos().size() == 1) {
            policySubIdoSave.setPrimary(true);
          }

          SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoById(policySubIdo.getSrfSubIdo().getId());
          policySubIdoSave.setSrfSubIdo(srfSubIdo);

          policySubIdoSave = projectPolicySubIdoManager.saveProjectPolicySubIdo(policySubIdoSave);
          // This is to add innovationCrpSave to generate correct auditlog.
          policy.getProjectPolicySubIdos().add(policySubIdoSave);
        } else {
          // if sub ido already exist - save primary
          ProjectPolicySubIdo policySubIdoSave = new ProjectPolicySubIdo();
          policySubIdoSave = projectPolicySubIdoManager.getProjectPolicySubIdoById(policySubIdo.getId());

          policySubIdoSave.setProjectPolicy(projectPolicy);
          policySubIdoSave.setPhase(phase);
          policySubIdoSave.setPrimary(policySubIdo.getPrimary());

          if (policySubIdo.getSrfSubIdo() != null && policySubIdo.getSrfSubIdo().getId() != null) {
            SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoById(policySubIdo.getSrfSubIdo().getId());
            policySubIdoSave.setSrfSubIdo(srfSubIdo);
          }

          if (policy.getSubIdos() != null && policy.getSubIdos().size() == 1) {
            policySubIdoSave.setPrimary(true);
          }
          policySubIdoSave = projectPolicySubIdoManager.saveProjectPolicySubIdo(policySubIdoSave);
          // This is to add innovationCrpSave to generate correct auditlog.
          policy.getProjectPolicySubIdos().add(policySubIdoSave);

        }
      }
    }
  }

  public void setCgiarCrossCuttingMarkers(List<CgiarCrossCuttingMarker> cgiarCrossCuttingMarkers) {
    this.cgiarCrossCuttingMarkers = cgiarCrossCuttingMarkers;
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

  public void setExpectedStudyList(List<ProjectExpectedStudy> expectedStudyList) {
    this.expectedStudyList = expectedStudyList;
  }

  public void setFocusLevels(List<RepIndGenderYouthFocusLevel> focusLevels) {
    this.focusLevels = focusLevels;
  }

  public void setGeographicScopes(List<RepIndGeographicScope> geographicScopes) {
    this.geographicScopes = geographicScopes;
  }

  public void setInnovationList(List<ProjectInnovation> innovationList) {
    this.innovationList = innovationList;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMilestoneList(List<CrpMilestone> milestoneList) {
    this.milestoneList = milestoneList;
  }

  public void setMilestonePrimaryId(long milestonePrimaryId) {
    this.milestonePrimaryId = milestonePrimaryId;
  }

  public void setOrganizationTypes(List<RepIndOrganizationType> organizationTypes) {
    this.organizationTypes = organizationTypes;
  }

  public void setPolicy(ProjectPolicy policy) {
    this.policy = policy;
  }

  public void setPolicyID(long policyID) {
    this.policyID = policyID;
  }

  public void setPolicyInvestimentTypes(List<RepIndPolicyInvestimentType> policyInvestimentTypes) {
    this.policyInvestimentTypes = policyInvestimentTypes;
  }

  public void setPolicyTypes(List<RepIndPolicyType> policyTypes) {
    this.policyTypes = policyTypes;
  }

  public void setPrincipalSubIdo(List<SrfSubIdo> principalSubIdo) {
    this.principalSubIdo = principalSubIdo;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setRegions(List<LocElement> regions) {
    this.regions = regions;
  }

  public void setSdgTargetList(List<SdgTargets> sdgTargetList) {
    this.sdgTargetList = sdgTargetList;
  }

  public void setSrfIdos(List<SrfIdo> srfIdos) {
    this.srfIdos = srfIdos;
  }

  public void setSrfSubIdoPrimary(long srfSubIdoPrimary) {
    this.srfSubIdoPrimary = srfSubIdoPrimary;
  }

  public void setStageProcesses(List<RepIndStageProcess> stageProcesses) {
    this.stageProcesses = stageProcesses;
  }

  public void setSubIdoPrimaryId(long subIdoPrimaryId) {
    this.subIdoPrimaryId = subIdoPrimaryId;
  }

  public void setSubIdos(List<SrfSubIdo> subIdos) {
    this.subIdos = subIdos;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, project, policy, true);
    }
  }

}
