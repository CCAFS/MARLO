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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyInvestimentTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageProcessManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyInvestimentType;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyType;
import org.cgiar.ccafs.marlo.data.model.RepIndStageProcess;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

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

  // Variables
  private GlobalUnit loggedCrp;
  private Project project;
  private long projectID;
  private long policyID;
  private long year;
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
  private List<GlobalUnit> crps;
  private List<ProjectExpectedStudy> expectedStudyList;
  private String transaction;


  @Inject
  public ProjectPolicyAction(APConfig config, GlobalUnitManager globalUnitManager,
    ProjectPolicyManager projectPolicyManager, ProjectPolicyInfoManager projectPolicyInfoManager,
    ProjectManager projectManager, PhaseManager phaseManager, RepIndGeographicScopeManager repIndGeographicScopeManager,
    LocElementManager locElementManager, RepIndGenderYouthFocusLevelManager focusLevelManager,
    RepIndOrganizationTypeManager repIndOrganizationTypeManager,
    RepIndPolicyInvestimentTypeManager repIndPolicyInvestimentTypeManager,
    RepIndStageProcessManager repIndStageProcessManager, RepIndPolicyTypeManager repIndPolicyTypeManager,
    SrfSubIdoManager srfSubIdoManager, AuditLogManager auditLogManager,
    ProjectExpectedStudyManager projectExpectedStudyManager) {
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
  }

  public List<LocElement> getCountries() {
    return countries;
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

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }


  public List<RepIndOrganizationType> getOrganizationTypes() {
    return organizationTypes;
  }


  public ProjectPolicy getPolicy() {
    return policy;
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

  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }


  public List<LocElement> getRegions() {
    return regions;
  }

  public List<RepIndStageProcess> getStageProcesses() {
    return stageProcesses;
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

        // // load PhaseResearchPartnership
        // if (policy.getProjectPolicyInfo().getRepIndOrganizationType() != null
        // && policy.getProjectPolicyInfo().getRepIndOrganizationType().getId() != null) {
        // policy.getProjectPolicyInfo().setRepIndPhaseResearchPartnership(
        // repIndPhaseResearchPartnershipManager.getRepIndPhaseResearchPartnershipById(
        // innovation.getProjectInnovationInfo().getRepIndPhaseResearchPartnership().getId()));
        // }
        //
        // // load StageInnovation
        // if (innovation.getProjectInnovationInfo().getRepIndStageInnovation() != null
        // && innovation.getProjectInnovationInfo().getRepIndStageInnovation().getId() != null) {
        // innovation.getProjectInnovationInfo().setRepIndStageInnovation(repIndStageInnovationManager
        // .getRepIndStageInnovationById(innovation.getProjectInnovationInfo().getRepIndStageInnovation().getId()));
        // }
        //
        // // load GeographicScope
        // if (innovation.getProjectInnovationInfo().getRepIndGeographicScope() != null
        // && innovation.getProjectInnovationInfo().getRepIndGeographicScope().getId() != null) {
        // innovation.getProjectInnovationInfo().setRepIndGeographicScope(repIndGeographicScopeManager
        // .getRepIndGeographicScopeById(innovation.getProjectInnovationInfo().getRepIndGeographicScope().getId()));
        // }
        //
        // // load Region
        // if (innovation.getProjectInnovationInfo().getRepIndRegion() != null
        // && innovation.getProjectInnovationInfo().getRepIndRegion().getId() != null) {
        // innovation.getProjectInnovationInfo().setRepIndRegion(
        // repIndRegionManager.getRepIndRegionById(innovation.getProjectInnovationInfo().getRepIndRegion().getId()));
        // }
        //
        // // load InnovationType
        // if (innovation.getProjectInnovationInfo().getRepIndInnovationType() != null
        // && innovation.getProjectInnovationInfo().getRepIndInnovationType().getId() != null) {
        // innovation.getProjectInnovationInfo().setRepIndInnovationType(repIndInnovationTypeManager
        // .getRepIndInnovationTypeById(innovation.getProjectInnovationInfo().getRepIndInnovationType().getId()));
        // }
        //
        // // load ContributionOfCrp
        // if (innovation.getProjectInnovationInfo().getRepIndContributionOfCrp() != null
        // && innovation.getProjectInnovationInfo().getRepIndContributionOfCrp().getId() != null) {
        // innovation.getProjectInnovationInfo()
        // .setRepIndContributionOfCrp(repIndContributionOfCrpManager.getRepIndContributionOfCrpById(
        // innovation.getProjectInnovationInfo().getRepIndContributionOfCrp().getId()));
        // }
        //
        // // load DegreeInnovation
        // if (innovation.getProjectInnovationInfo().getRepIndDegreeInnovation() != null
        // && innovation.getProjectInnovationInfo().getRepIndDegreeInnovation().getId() != null) {
        // innovation.getProjectInnovationInfo().setRepIndDegreeInnovation(repIndDegreeInnovationManager
        // .getRepIndDegreeInnovationById(innovation.getProjectInnovationInfo().getRepIndDegreeInnovation().getId()));
        // }
        //
        // // load InnovationDeliverables
        // if (innovation.getProjectInnovationDeliverables() != null
        // && !innovation.getProjectInnovationDeliverables().isEmpty()) {
        // for (ProjectInnovationDeliverable projectInnovationDeliverable : innovation
        // .getProjectInnovationDeliverables()) {
        // if (projectInnovationDeliverable.getDeliverable() != null
        // && projectInnovationDeliverable.getDeliverable().getId() != null) {
        //
        // if (deliverableManager
        // .getDeliverableById(projectInnovationDeliverable.getDeliverable().getId()) != null) {
        // Deliverable deliverable =
        // deliverableManager.getDeliverableById(projectInnovationDeliverable.getDeliverable().getId());
        // projectInnovationDeliverable.setDeliverable(deliverable);
        // projectInnovationDeliverable.getDeliverable().getDeliverableInfo(this.getActualPhase());
        // }
        //
        // }
        //
        // }
        // }

      }

    } else {
      policy = projectPolicyManager.getProjectPolicyById(policyID);

    }

    if (policy != null) {

      projectID = policy.getProject().getId();
      project = projectManager.getProjectById(projectID);


      Phase phase = phaseManager.getPhaseById(this.getActualPhase().getId());
      project.getProjecInfoPhase(phase);

      // Getting The list
      countries = locElementManager.findAll().stream()
        .filter(c -> c.getLocElementType().getId().intValue() == 2 && c.isActive()).collect(Collectors.toList());

      geographicScopes = repIndGeographicScopeManager.findAll();
      regions = locElementManager.findAll().stream()
        .filter(c -> c.getLocElementType().getId().intValue() == 1 && c.isActive() && c.getIsoNumeric() != null)
        .collect(Collectors.toList());

      organizationTypes = repIndOrganizationTypeManager.findAll();

      stageProcesses = new ArrayList<>(repIndStageProcessManager.findAll().stream()
        .filter(p -> p.getYear() == this.getCurrentCycleYear()).collect(Collectors.toList()));

      policyInvestimentTypes = repIndPolicyInvestimentTypeManager.findAll();

      policyTypes = repIndPolicyTypeManager.findAll();

      subIdos = srfSubIdoManager.findAll();

      focusLevels = focusLevelManager.findAll();
      expectedStudyList = new ArrayList<>();
      List<ProjectExpectedStudy> expectedStudies = projectExpectedStudyManager.findAll().stream()
        .filter(ex -> ex.isActive() && ex.getProjectExpectedStudyInfo(phase) != null
          && ex.getProjectExpectedStudyInfo().getStudyType() != null
          && ex.getProjectExpectedStudyInfo().getStudyType().getId().intValue() == 1 && ex.getProject() != null
          && ex.getProject().getId() == project.getId())
        .collect(Collectors.toList());
      for (ProjectExpectedStudy study : expectedStudies) {
        expectedStudyList.add(study);
      }

      crps = globalUnitManager.findAll().stream()
        .filter(gu -> gu.isActive() && (gu.getGlobalUnitType().getId() == 1 || gu.getGlobalUnitType().getId() == 3))
        .collect(Collectors.toList());

    }

    policyDB = projectPolicyManager.getProjectPolicyById(policyID);

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_POLICIES_BASE_PERMISSION, params));


  }

  @Override
  public String save() {

    return NOT_AUTHORIZED;
  }

  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
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


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
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


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setRegions(List<LocElement> regions) {
    this.regions = regions;
  }

  public void setStageProcesses(List<RepIndStageProcess> stageProcesses) {
    this.stageProcesses = stageProcesses;
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

    }
  }

}
