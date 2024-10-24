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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectDeliverableSharedManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCenterManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationContributingOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSharedManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndContributionOfCrpManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndDegreeInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPhaseResearchPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndRegionManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.SrfIdoManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectDeliverableShared;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrpOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationDeliverable;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationShared;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.data.model.RepIndContributionOfCrp;
import org.cgiar.ccafs.marlo.data.model.RepIndDegreeInnovation;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationType;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.RepIndPhaseResearchPartnership;
import org.cgiar.ccafs.marlo.data.model.RepIndRegion;
import org.cgiar.ccafs.marlo.data.model.RepIndStageInnovation;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectInnovationAction extends BaseAction {

  private static final long serialVersionUID = 2025842196563364380L;
  // Logger
  private final Logger logger = LoggerFactory.getLogger(ProjectInnovationAction.class);
  // Managers
  private ProjectInnovationManager projectInnovationManager;
  private GlobalUnitManager globalUnitManager;
  private ProjectManager projectManager;
  private PhaseManager phaseManager;
  private RepIndPhaseResearchPartnershipManager repIndPhaseResearchPartnershipManager;
  private RepIndStageInnovationManager repIndStageInnovationManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private RepIndInnovationTypeManager repIndInnovationTypeManager;
  private RepIndRegionManager repIndRegionManager;
  private RepIndContributionOfCrpManager repIndContributionOfCrpManager;
  private RepIndDegreeInnovationManager repIndDegreeInnovationManager;
  private LocElementManager locElementManager;
  private DeliverableManager deriverableManager;
  private RepIndGenderYouthFocusLevelManager focusLevelManager;
  private ProjectInnovationInfoManager projectInnovationInfoManager;
  private ProjectInnovationCrpManager projectInnovationCrpManager;
  private ProjectInnovationOrganizationManager projectInnovationOrganizationManager;
  private ProjectInnovationDeliverableManager projectInnovationDeliverableManager;
  private ProjectInnovationCountryManager projectInnovationCountryManager;
  private ProjectInnovationContributingOrganizationManager projectInnovationContributingOrganizationManager;
  private RepIndOrganizationTypeManager repIndOrganizationTypeManager;
  private InstitutionManager institutionManager;
  private CrpMilestoneManager milestoneManager;
  private AuditLogManager auditLogManager;
  private DeliverableManager deliverableManager;
  private ProjectInnovationRegionManager projectInnovationRegionManager;
  private ProjectInnovationGeographicScopeManager projectInnovationGeographicScopeManager;
  private ProjectInnovationSharedManager projectInnovationSharedManager;
  private ProjectInnovationCenterManager projectInnovationCenterManager;
  private ProjectInnovationMilestoneManager projectInnovationMilestoneManager;
  private SrfSubIdoManager srfSubIdoManager;
  private ProjectInnovationSubIdoManager projectInnovationSubIdoManager;
  private SrfIdoManager srfIdoManager;
  private ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectDeliverableSharedManager projectDeliverableSharedManager;
  private ProjectOutcomeManager projectOutcomeManager;
  private ProjectInnovationProjectOutcomeManager projectInnovationProjectOutcomeManager;
  private FeedbackQACommentManager feedbackQACommentManager;
  private FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager;
  private ProjectInnovationCrpOutcomeManager projectInnovationCrpOutcomeManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;

  // Variables
  private long projectID;
  private long innovationID;
  private long subIdoPrimaryId;
  private long srfSubIdoPrimary;
  private long milestonePrimaryId;
  private long crpMilestonePrimary;
  private Project project;

  private ProjectInnovation innovation;

  private ProjectInnovation innovationDB;

  private GlobalUnit loggedCrp;

  private List<RepIndPhaseResearchPartnership> phaseResearchList;
  private List<RepIndStageInnovation> stageInnovationList;
  private String transaction;
  private List<RepIndGeographicScope> geographicScopeList;
  private List<RepIndInnovationType> innovationTypeList;
  private List<RepIndContributionOfCrp> contributionCrpList;
  private List<RepIndDegreeInnovation> degreeInnovationList;
  private List<RepIndRegion> regionList;
  private List<LocElement> countries;
  private List<LocElement> regions;
  private List<Institution> institutions;
  private List<ProjectExpectedStudy> expectedStudyList;
  private List<Deliverable> deliverableList;
  private List<GlobalUnit> crpList;
  private List<RepIndGenderYouthFocusLevel> focusLevelList;
  private List<RepIndOrganizationType> organizationTypeList;
  private List<Project> myProjects;
  private ProjectInnovationValidator validator;
  private Boolean clearLead;
  private List<Institution> centers;
  private List<CrpMilestone> milestones;
  private List<SrfSubIdo> subIdos;
  private List<SrfSubIdo> principalSubIdo;
  private List<SrfIdo> srfIdos;
  private HashMap<Long, String> idoList;
  private List<ProjectOutcome> projectOutcomes;
  private List<FeedbackQACommentableFields> feedbackComments;
  private List<CrpProgramOutcome> crpOutcomes;


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
    AuditLogManager auditLogManager, RepIndContributionOfCrpManager repIndContributionOfCrpManager,
    RepIndDegreeInnovationManager repIndDegreeInnovationManager, DeliverableManager deliverableManager,
    InstitutionManager institutionManager, CrpMilestoneManager milestoneManager,
    ProjectInnovationContributingOrganizationManager projectInnovationContributingOrganizationManager,
    ProjectInnovationRegionManager projectInnovationRegionManager,
    ProjectInnovationGeographicScopeManager projectInnovationGeographicScopeManager,
    ProjectInnovationSharedManager projectInnovationSharedManager,
    ProjectInnovationCenterManager projectInnovationCenterManager,
    ProjectInnovationMilestoneManager projectInnovationMilestoneManager, SrfSubIdoManager srfSubIdoManager,
    ProjectInnovationSubIdoManager projectInnovationSubIdoManager, SrfIdoManager srfIdoManager,
    ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager,
    ProjectDeliverableSharedManager projectDeliverableSharedManager, ProjectOutcomeManager projectOutcomeManager,
    ProjectInnovationProjectOutcomeManager projectInnovationProjectOutcomeManager,
    FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager,
    FeedbackQACommentManager feedbackQACommentManager,
    ProjectInnovationCrpOutcomeManager projectInnovationCrpOutcomeManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager) {
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
    this.repIndContributionOfCrpManager = repIndContributionOfCrpManager;
    this.repIndDegreeInnovationManager = repIndDegreeInnovationManager;
    this.deliverableManager = deliverableManager;
    this.institutionManager = institutionManager;
    this.milestoneManager = milestoneManager;
    this.projectInnovationContributingOrganizationManager = projectInnovationContributingOrganizationManager;
    this.projectInnovationRegionManager = projectInnovationRegionManager;
    this.projectInnovationGeographicScopeManager = projectInnovationGeographicScopeManager;
    this.projectInnovationSharedManager = projectInnovationSharedManager;
    this.projectInnovationCenterManager = projectInnovationCenterManager;
    this.projectInnovationMilestoneManager = projectInnovationMilestoneManager;
    this.projectExpectedStudyInnovationManager = projectExpectedStudyInnovationManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.projectInnovationSubIdoManager = projectInnovationSubIdoManager;
    this.srfIdoManager = srfIdoManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectDeliverableSharedManager = projectDeliverableSharedManager;
    this.projectOutcomeManager = projectOutcomeManager;
    this.projectInnovationProjectOutcomeManager = projectInnovationProjectOutcomeManager;
    this.feedbackQACommentableFieldsManager = feedbackQACommentableFieldsManager;
    this.feedbackQACommentManager = feedbackQACommentManager;
    this.projectInnovationCrpOutcomeManager = projectInnovationCrpOutcomeManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
  }

  /**
   * this is not functional, but is added to prevent a strange exception. Taken from DeliverableAction.
   */
  @Override
  public String cancel() {
    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {
      path.toFile().delete();
    }

    this.setDraft(false);

    if (this.getActionMessages().isEmpty()) {
      this.setActionMessages(null);
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    } else {
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    }

    return SUCCESS;
  }

  /**
   * Delete all LocElements Records when Geographic Scope is Global or NULL
   * 
   * @param policy
   * @param phase
   */
  public void deleteLocElements(ProjectInnovation innovation, Phase phase, boolean isCountry) {
    if (isCountry) {
      if (innovation.getProjectInnovationCountries() != null && innovation.getProjectInnovationCountries().size() > 0) {

        List<ProjectInnovationCountry> regionPrev = new ArrayList<>(innovation.getProjectInnovationCountries().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (ProjectInnovationCountry policyRegion : regionPrev) {

          projectInnovationCountryManager.deleteProjectInnovationCountry(policyRegion.getId());

        }
      }
    } else {
      if (innovation.getProjectInnovationRegions() != null && innovation.getProjectInnovationRegions().size() > 0) {

        List<ProjectInnovationRegion> regionPrev = new ArrayList<>(innovation.getProjectInnovationRegions().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (ProjectInnovationRegion policyRegion : regionPrev) {

          projectInnovationRegionManager.deleteProjectInnovationRegion(policyRegion.getId());

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
    String composedClassName = innovation.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatenate name and add the .json extension
    String autoSaveFile = innovation.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<Institution> getCenters() {
    return centers;
  }

  public List<RepIndContributionOfCrp> getContributionCrpList() {
    return contributionCrpList;
  }

  public List<LocElement> getCountries() {
    return countries;
  }

  @Override
  public List<GlobalUnit> getCrpList() {
    return crpList;
  }

  public long getCrpMilestonePrimary() {
    return crpMilestonePrimary;
  }

  public List<CrpProgramOutcome> getCrpOutcomes() {
    return crpOutcomes;
  }

  public List<RepIndDegreeInnovation> getDegreeInnovationList() {
    return degreeInnovationList;
  }

  public List<Deliverable> getDeliverableList() {
    return deliverableList;
  }

  public List<ProjectExpectedStudy> getExpectedStudyList() {
    return expectedStudyList;
  }

  public List<FeedbackQACommentableFields> getFeedbackComments() {
    return feedbackComments;
  }

  public List<RepIndGenderYouthFocusLevel> getFocusLevelList() {
    return focusLevelList;
  }

  public List<RepIndGeographicScope> getGeographicScopeList() {
    return geographicScopeList;
  }

  public HashMap<Long, String> getIdoList() {
    return idoList;
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

  public List<Institution> getInstitutions() {
    return institutions;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public long getMilestonePrimaryId() {
    return milestonePrimaryId;
  }

  public List<CrpMilestone> getMilestones() {
    return milestones;
  }

  public List<Project> getMyProjects() {
    return myProjects;
  }

  public List<RepIndOrganizationType> getOrganizationTypeList() {
    return organizationTypeList;
  }

  public List<RepIndPhaseResearchPartnership> getPhaseResearchList() {
    return phaseResearchList;
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

  public List<ProjectOutcome> getProjectOutcomes() {
    return projectOutcomes;
  }

  public List<RepIndRegion> getRegionList() {
    return regionList;
  }

  public List<LocElement> getRegions() {
    return regions;
  }

  public List<SrfIdo> getSrfIdos() {
    return srfIdos;
  }

  public long getSrfSubIdoPrimary() {
    return srfSubIdoPrimary;
  }

  public List<RepIndStageInnovation> getStageInnovationList() {
    return stageInnovationList;
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

  public Boolean isClearLead() {
    return clearLead;
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
      if (innovation.getProjectInnovationInfo() == null) {
        innovation.getProjectInnovationInfo(this.getActualPhase());
      }
      // load relations
      if (innovation.getProjectInnovationInfo() != null) {

        // load innovations next organizations
        if (innovation.getProjectInnovationOrganizations() != null) {
          for (ProjectInnovationOrganization projectOrganization : innovation.getProjectInnovationOrganizations()) {

            if (projectOrganization.getRepIndOrganizationType() != null
              && projectOrganization.getRepIndOrganizationType().getId() != null) {

              if (repIndOrganizationTypeManager
                .getRepIndOrganizationTypeById(projectOrganization.getRepIndOrganizationType().getId()) != null) {
                RepIndOrganizationType institution = repIndOrganizationTypeManager
                  .getRepIndOrganizationTypeById(projectOrganization.getRepIndOrganizationType().getId());
              }
            }
          }
        }

        // load PhaseResearchPartnership
        if (innovation.getProjectInnovationInfo().getRepIndPhaseResearchPartnership() != null
          && innovation.getProjectInnovationInfo().getRepIndPhaseResearchPartnership().getId() != null) {
          innovation.getProjectInnovationInfo().setRepIndPhaseResearchPartnership(
            repIndPhaseResearchPartnershipManager.getRepIndPhaseResearchPartnershipById(
              innovation.getProjectInnovationInfo().getRepIndPhaseResearchPartnership().getId()));
        }

        // load StageInnovation
        if (innovation.getProjectInnovationInfo().getRepIndStageInnovation() != null
          && innovation.getProjectInnovationInfo().getRepIndStageInnovation().getId() != null) {
          innovation.getProjectInnovationInfo().setRepIndStageInnovation(repIndStageInnovationManager
            .getRepIndStageInnovationById(innovation.getProjectInnovationInfo().getRepIndStageInnovation().getId()));
        }

        // load Region
        if (innovation.getProjectInnovationInfo().getRepIndRegion() != null
          && innovation.getProjectInnovationInfo().getRepIndRegion().getId() != null) {
          innovation.getProjectInnovationInfo().setRepIndRegion(
            repIndRegionManager.getRepIndRegionById(innovation.getProjectInnovationInfo().getRepIndRegion().getId()));
        }

        // load InnovationType
        if (innovation.getProjectInnovationInfo().getRepIndInnovationType() != null
          && innovation.getProjectInnovationInfo().getRepIndInnovationType().getId() != null) {
          innovation.getProjectInnovationInfo().setRepIndInnovationType(repIndInnovationTypeManager
            .getRepIndInnovationTypeById(innovation.getProjectInnovationInfo().getRepIndInnovationType().getId()));
        }

        // load DegreeInnovation
        if (innovation.getProjectInnovationInfo().getRepIndDegreeInnovation() != null
          && innovation.getProjectInnovationInfo().getRepIndDegreeInnovation().getId() != null) {
          innovation.getProjectInnovationInfo().setRepIndDegreeInnovation(repIndDegreeInnovationManager
            .getRepIndDegreeInnovationById(innovation.getProjectInnovationInfo().getRepIndDegreeInnovation().getId()));
        }

        // load leadOrganization
        if (innovation.getProjectInnovationInfo().getLeadOrganization() != null
          && innovation.getProjectInnovationInfo().getLeadOrganization().getId() != null) {
          innovation.getProjectInnovationInfo().setLeadOrganization(
            institutionManager.getInstitutionById(innovation.getProjectInnovationInfo().getLeadOrganization().getId()));
        }

        // load InnovationDeliverables
        if (innovation.getProjectInnovationDeliverables() != null
          && !innovation.getProjectInnovationDeliverables().isEmpty()) {
          for (ProjectInnovationDeliverable projectInnovationDeliverable : innovation
            .getProjectInnovationDeliverables()) {
            if (projectInnovationDeliverable.getDeliverable() != null
              && projectInnovationDeliverable.getDeliverable().getId() != null) {

              if (deliverableManager
                .getDeliverableById(projectInnovationDeliverable.getDeliverable().getId()) != null) {
                Deliverable deliverable =
                  deliverableManager.getDeliverableById(projectInnovationDeliverable.getDeliverable().getId());
                projectInnovationDeliverable.setDeliverable(deliverable);
                projectInnovationDeliverable.getDeliverable().getDeliverableInfo(this.getActualPhase());
              }
            }
          }
        }

        // load clear lead
        if (innovation.getProjectInnovationInfo().getClearLead() == null
          || innovation.getProjectInnovationInfo().getClearLead() == false) {
          clearLead = false;
        } else {
          clearLead = true;
        }

        // load contributionOrganization
        if (innovation.getProjectInnovationContributingOrganization() != null
          && !innovation.getProjectInnovationContributingOrganization().isEmpty()) {
          for (ProjectInnovationContributingOrganization projectInnovationContributingOrganization : innovation
            .getProjectInnovationContributingOrganization()) {

            if (projectInnovationContributingOrganization.getInstitution() != null
              && projectInnovationContributingOrganization.getInstitution().getId() != null) {

              if (institutionManager
                .getInstitutionById(projectInnovationContributingOrganization.getInstitution().getId()) != null) {
                Institution institution = institutionManager
                  .getInstitutionById(projectInnovationContributingOrganization.getInstitution().getId());
                projectInnovationContributingOrganization.setInstitution(institution);
              }
            }
          }
        }
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

        // Innovation Geographic Scope List AutoSave

        // Policy Geographic Scope List AutoSave
        boolean haveRegions = false;
        boolean haveCountries = false;

        if (innovation.getGeographicScopes() != null) {
          for (ProjectInnovationGeographicScope projectInnovationGeographicScope : innovation.getGeographicScopes()) {
            projectInnovationGeographicScope.setRepIndGeographicScope(repIndGeographicScopeManager
              .getRepIndGeographicScopeById(projectInnovationGeographicScope.getRepIndGeographicScope().getId()));

            if (projectInnovationGeographicScope.getRepIndGeographicScope().getId() == 2) {
              haveRegions = true;
            }

            if (projectInnovationGeographicScope.getRepIndGeographicScope().getId() != 1
              && projectInnovationGeographicScope.getRepIndGeographicScope().getId() != 2) {
              haveCountries = true;
            }

          }
        }

        if (haveRegions) {
          // Load Regions
          if (innovation.getRegions() != null) {
            for (ProjectInnovationRegion projectPolicyRegion : innovation.getRegions()) {
              projectPolicyRegion
                .setLocElement(locElementManager.getLocElementById(projectPolicyRegion.getLocElement().getId()));
            }
          }
        }

        if (haveCountries) {
          // Load Countries
          if (innovation.getCountriesIdsText() != null) {
            String[] countriesText = innovation.getCountriesIdsText().replace("[", "").replace("]", "").split(",");
            List<String> countries = new ArrayList<>();
            for (String value : Arrays.asList(countriesText)) {
              countries.add(value.trim());
            }
            innovation.setCountriesIds(countries);
          }
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

        // Innovation Contributing Institutions List Autosave
        if (innovation.getContributingOrganizations() != null && !innovation.getContributingOrganizations().isEmpty()) {
          for (ProjectInnovationContributingOrganization projectInnovationContributingOrganization : innovation
            .getContributingOrganizations()) {
            projectInnovationContributingOrganization.setInstitution(institutionManager
              .getInstitutionById(projectInnovationContributingOrganization.getInstitution().getId()));
          }
        }

        // Innovation Crp List Autosave
        if (innovation.getCrps() != null) {
          for (ProjectInnovationCrp projectInnovationCrp : innovation.getCrps()) {
            projectInnovationCrp
              .setGlobalUnit(globalUnitManager.getGlobalUnitById(projectInnovationCrp.getGlobalUnit().getId()));
          }
        }

        // Expected Study Innovations List Autosave
        if (this.innovation.getStudies() != null) {
          for (ProjectExpectedStudyInnovation projectExpectedStudyInnovation : this.innovation.getStudies()) {
            projectExpectedStudyInnovation.setProjectExpectedStudy(this.projectExpectedStudyManager
              .getProjectExpectedStudyById(projectExpectedStudyInnovation.getProjectExpectedStudy().getId()));
          }
        }

        // Innovation Center List Autosave

        if (innovation.getCenters() != null) {
          for (ProjectInnovationCenter projectInnovationCenter : innovation.getCenters()) {
            projectInnovationCenter
              .setInstitution(institutionManager.getInstitutionById(projectInnovationCenter.getInstitution().getId()));
          }
        }

        // Innovation Milestone List Autosave

        if (innovation.getMilestones() != null) {
          for (ProjectInnovationMilestone projectInnovationMilestone : innovation.getMilestones()) {
            projectInnovationMilestone.setCrpMilestone(
              (milestoneManager.getCrpMilestoneById(projectInnovationMilestone.getCrpMilestone().getId())));
          }
        }


        // SubIdos List Autosave

        if (innovation.getSubIdos() != null) {
          for (ProjectInnovationSubIdo projectInnovationSubIdo : innovation.getSubIdos()) {
            projectInnovationSubIdo
              .setSrfSubIdo(srfSubIdoManager.getSrfSubIdoById(projectInnovationSubIdo.getSrfSubIdo().getId()));
          }
        }


        // Innovation Shared Projects List Autosave
        if (this.innovation.getSharedInnovations() != null) {
          for (ProjectInnovationShared projectInnovationShared : this.innovation.getSharedInnovations()) {
            projectInnovationShared
              .setProject(this.projectManager.getProjectById(projectInnovationShared.getProject().getId()));
          }
        }

        this.setDraft(true);
      } else {
        this.setDraft(false);

        if (innovation.getProjectInnovationInfo() == null) {
          innovation.getProjectInnovationInfo(phase);
        }

        // Setup Geographic Scope
        if (innovation.getProjectInnovationGeographicScopes() != null) {
          innovation.setGeographicScopes(new ArrayList<>(innovation.getProjectInnovationGeographicScopes().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Innovation Countries List
        if (innovation.getProjectInnovationCountries() == null) {
          innovation.setCountries(new ArrayList<>());
        } else {
          List<ProjectInnovationCountry> countries =
            projectInnovationCountryManager.getInnovationCountrybyPhase(innovation.getId(), phase.getId());
          innovation.setCountries(countries);
        }

        if (innovation.getProjectInnovationRegions() == null) {
          innovation.setRegions(new ArrayList<>());
        } else {
          List<ProjectInnovationRegion> geographics =
            projectInnovationRegionManager.getInnovationRegionbyPhase(innovation.getId(), phase.getId());

          // Load Regions
          innovation.setRegions(geographics.stream().filter(sc -> sc.getLocElement().getLocElementType().getId() == 1)
            .collect(Collectors.toList()));
        }

        // Innovation Organization Type List
        if (innovation.getProjectInnovationOrganizations() != null) {
          innovation.setOrganizations(new ArrayList<>(innovation.getProjectInnovationOrganizations().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Innovation Deliverable List
        if (innovation.getProjectInnovationDeliverables() != null) {
          innovation.setDeliverables(new ArrayList<>(innovation.getProjectInnovationDeliverables().stream()
            .filter(d -> d.isActive() && d.getPhase().getId() == phase.getId()).collect(Collectors.toList())));
        }

        // Innovation Contributing organizations List
        if (innovation.getProjectInnovationContributingOrganization() != null) {
          innovation
            .setContributingOrganizations(new ArrayList<>(innovation.getProjectInnovationContributingOrganization()
              .stream().filter(d -> d.getPhase().getId().equals(phase.getId()))
              .sorted(
                (o1, o2) -> o1.getInstitution().getComposedName().compareTo(o2.getInstitution().getComposedName()))
              .collect(Collectors.toList())));
        }

        // Innovation Crp list
        if (innovation.getProjectInnovationCrps() != null) {
          innovation.setCrps(new ArrayList<>(innovation.getProjectInnovationCrps().stream()
            .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Innovation Center list
        if (innovation.getProjectInnovationCenters() != null) {
          innovation.setCenters(new ArrayList<>(innovation.getProjectInnovationCenters().stream()
            .filter(c -> c.isActive() && c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Innovation Milestone list
        if (innovation.getProjectInnovationMilestones() != null) {
          innovation.setMilestones(new ArrayList<>(innovation.getProjectInnovationMilestones().stream()
            .filter(o -> o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));

          // Get the ID of the principal Sub IDO if exist
          if (innovation.getMilestones() != null) {
            List<ProjectInnovationMilestone> projectPolicies = new ArrayList<ProjectInnovationMilestone>();

            projectPolicies = innovation
              .getMilestones().stream().filter(p -> p != null && p.isActive() && p.getPrimary() != null
                && p.getPrimary() && p.getPhase() != null && p.getPhase().getId().equals(phase.getId()))
              .collect(Collectors.toList());

            if (projectPolicies != null && projectPolicies.size() > 0 && projectPolicies.get(0) != null) {
              milestonePrimaryId = projectPolicies.get(0).getCrpMilestone().getId(); //
              crpMilestonePrimary = projectPolicies.get(0).getCrpMilestone().getId(); //
            }
          }
        }

        // Expected Study Project Outcome list
        if (innovation.getProjectInnovationProjectOutcomes() != null) {
          innovation.setProjectOutcomes(new ArrayList<>(innovation.getProjectInnovationProjectOutcomes().stream()
            .filter(o -> o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }
        // Expected Study crp Outcome list
        if (innovation.getProjectInnovationCrpOutcomes() != null) {
          innovation.setCrpOutcomes(new ArrayList<>(innovation.getProjectInnovationCrpOutcomes().stream()
            .filter(o -> o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // SubIdos List
        if (innovation.getProjectInnovationSubIdos() != null) {
          innovation.setSubIdos(new ArrayList<>(innovation.getProjectInnovationSubIdos().stream()
            .filter(o -> o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));

          // Get the ID of the principal Sub IDO if exist
          if (innovation.getSubIdos(phase) != null) {
            List<ProjectInnovationSubIdo> projectPolicies = new ArrayList<ProjectInnovationSubIdo>();

            projectPolicies = innovation.getSubIdos(phase).stream()
              .filter(p -> p != null && p.isActive() && p.getPrimary() != null && p.getPrimary())
              .collect(Collectors.toList());

            if (projectPolicies != null && projectPolicies.size() > 0 && projectPolicies.get(0) != null) {
              subIdoPrimaryId = projectPolicies.get(0).getSrfSubIdo().getId(); //
              srfSubIdoPrimary = projectPolicies.get(0).getSrfSubIdo().getId(); //
            }
          }

        }

        // Innovation shared Projects List
        if (this.innovation.getProjectInnovationShareds() != null) {
          this.innovation.setSharedInnovations(new ArrayList<>(this.innovation.getProjectInnovationShareds().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Expected Study Innovations List
        if (this.innovation.getProjectExpectedStudyInnovations() != null) {
          this.innovation.setStudies(new ArrayList<>(this.innovation.getProjectExpectedStudyInnovations().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
          // Get the ID of the principal Sub IDO if exist
          if (innovation.getMilestones() != null) {
            List<ProjectInnovationMilestone> projectPolicies = new ArrayList<ProjectInnovationMilestone>();

            projectPolicies = innovation
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
        if (innovation.getCountries() != null) {
          for (ProjectInnovationCountry country : innovation.getCountries()) {
            innovation.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
          }
        }
      }

      // Getting The list of countries
      countries = locElementManager.findAll().stream().filter(c -> c.getLocElementType().getId().intValue() == 2)
        .collect(Collectors.toList());

      // Getting the list of institution
      institutions = institutionManager.findAll().stream().filter(i -> i.isActive()).collect(Collectors.toList());

      // Regions for Geographic Scope Regional Selection
      regions = locElementManager.findAll().stream()
        .filter(c -> c.getLocElementType().getId().intValue() == 1 && c.isActive() && c.getIsoNumeric() != null)
        .collect(Collectors.toList());

      phaseResearchList = repIndPhaseResearchPartnershipManager.findAll();
      stageInnovationList = repIndStageInnovationManager.findAll();
      geographicScopeList = repIndGeographicScopeManager.findAll();
      innovationTypeList = repIndInnovationTypeManager.findAll();
      regionList = repIndRegionManager.findAll();
      focusLevelList = focusLevelManager.findAll();
      organizationTypeList = repIndOrganizationTypeManager.findAll();
      contributionCrpList = repIndContributionOfCrpManager.findAll();
      degreeInnovationList = repIndDegreeInnovationManager.findAll();
      // institutions & ppa partners
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

      List<ProjectExpectedStudy> allProjectStudies = new ArrayList<ProjectExpectedStudy>();

      // SubIdos List
      subIdos = srfSubIdoManager.findAll();
      this.expectedStudyList = new ArrayList<>();

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
        // Editable project studies: Current cycle year-1 will be editable except
        // Complete and Cancelled.
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

      if (evidences != null) {
        if (expectedStudyList != null && !expectedStudyList.isEmpty()) {
          expectedStudyList.addAll(evidences);
        } else {
          expectedStudyList = evidences;
        }
      }

      if (phase != null && phase.getDeliverableInfos() != null && project != null
        && phase.getDeliverableInfos().size() > 0) {
        List<DeliverableInfo> infos = phase.getDeliverableInfos().stream()
          .filter(c -> c != null && c.getDeliverable() != null && c.getDeliverable().getProject() != null
            && c.getDeliverable().getProject().equals(project) && c.getDeliverable().isActive())
          .collect(Collectors.toList());
        deliverableList = new ArrayList<>();
        for (DeliverableInfo deliverableInfo : infos) {
          Deliverable deliverable = deliverableInfo.getDeliverable();
          deliverable.setDeliverableInfo(deliverableInfo);

          deliverable.setTagTitle(deliverable.getComposedName());

          deliverableList.add(deliverable);
        }
      }
      try {
        // Load Shared deliverables
        List<ProjectDeliverableShared> deliverableShared = this.projectDeliverableSharedManager
          .getByProjectAndPhase(project.getId(), this.getActualPhase().getId()) != null
            ? this.projectDeliverableSharedManager.getByProjectAndPhase(project.getId(), this.getActualPhase().getId())
              .stream()
              .filter(px -> px.isActive() && px.getDeliverable().isActive()
                && px.getDeliverable().getDeliverableInfo(this.getActualPhase()) != null)
              .collect(Collectors.toList())
            : Collections.emptyList();

        if (deliverableShared != null && !deliverableShared.isEmpty()) {
          for (ProjectDeliverableShared deliverableS : deliverableShared) {
            if (!deliverableList.contains(deliverableS.getDeliverable())) {

              if (deliverableS.getDeliverable().getProject() != null
                && deliverableS.getDeliverable().getProject().getId() != null
                && !deliverableS.getDeliverable().getProject().getId().equals(projectID)) {
                DeliverableInfo deliverableInfo =
                  deliverableS.getDeliverable().getDeliverableInfo(this.getActualPhase());
                deliverableS.getDeliverable().setDeliverableInfo(deliverableInfo);

                deliverableS.getDeliverable().setTagTitle(
                  "<span class=\"label label-info\">From C" + deliverableS.getDeliverable().getProject().getId()
                    + "</span> " + deliverableS.getDeliverable().getComposedName());
              } else {
                deliverableS.getDeliverable().setTagTitle(deliverableS.getDeliverable().getComposedName());
              }

              deliverableList.add(deliverableS.getDeliverable());
            }
          }
        }
      } catch (Exception e) {
        logger.error("unable to get shared deliverables", e);
      }

      List<Project> projectSharedList = new ArrayList<>();
      if (innovation.getSharedInnovations() != null && innovation.getSharedInnovations().size() > 0) {
        for (ProjectInnovationShared sharedInnovation : innovation.getSharedInnovations()) {
          if (sharedInnovation != null && sharedInnovation.getProject() != null
            && sharedInnovation.getProject().getId() != null) {
            projectSharedList.add(sharedInnovation.getProject());
          }
        }


        // Get deliverable list for shared innovations projects
        if (projectSharedList != null && projectSharedList.size() > 0) {
          for (Project projectInnovationShared : projectSharedList) {
            if (phase != null && phase.getDeliverableInfos() != null && projectInnovationShared != null
              && phase.getDeliverableInfos().size() > 0) {
              List<DeliverableInfo> infos = phase.getDeliverableInfos().stream()
                .filter(c -> c != null && c.getDeliverable() != null && c.getDeliverable().getProject() != null
                  && c.getDeliverable().getProject().equals(projectInnovationShared) && c.getDeliverable().isActive())
                .collect(Collectors.toList());

              for (DeliverableInfo deliverableInfo : infos) {
                Deliverable deliverable = deliverableInfo.getDeliverable();
                deliverable.setDeliverableInfo(deliverableInfo);

                deliverable.setTagTitle(deliverable.getComposedName());

                deliverableList.add(deliverable);
              }
            }
          }
        }
      }


      /*
       * Get the milestone List
       */
      milestones = new ArrayList<>();
      projectOutcomes = new ArrayList<>();

      // Get outcomes list
      List<ProjectOutcome> projectOutcomesList = new ArrayList<>();
      projectOutcomesList = project.getProjectOutcomes().stream()
        .filter(
          po -> po.isActive() && po.getPhase() != null && po.getPhase().getId().equals(this.getActualPhase().getId()))
        .collect(Collectors.toList());

      if (projectOutcomesList != null) {
        crpOutcomes = new ArrayList<>();

        for (ProjectOutcome projectOutcome : projectOutcomesList) {
          projectOutcome.setMilestones(projectOutcome.getProjectMilestones().stream()
            .filter(
              m -> m != null && m.isActive() && m.getYear() != 0 && m.getYear() <= this.getActualPhase().getYear())
            .collect(Collectors.toList()));

          if (!this.crpOutcomes.contains(projectOutcome.getCrpProgramOutcome())) {
            this.crpOutcomes.add(projectOutcome.getCrpProgramOutcome());
          }

          if (projectOutcome.getMilestones() != null) {
            for (ProjectMilestone projectMilestone : projectOutcome.getMilestones()) {
              if (projectMilestone.getCrpMilestone() != null && projectMilestone.getCrpMilestone().isActive()) {
                milestones.add(projectMilestone.getCrpMilestone());
              }
            }
          }

          // Fill projectOutcomes List
          if (projectOutcome.getCrpProgramOutcome() != null
            && projectOutcome.getCrpProgramOutcome().getComposedName() != null) {
            projectOutcome.setComposedName(projectOutcome.getCrpProgramOutcome().getComposedName());
          } else {
            projectOutcome.setComposedName(projectOutcome.getId() + "");
          }
          projectOutcomes.add(projectOutcome);
        }
      }
      crpOutcomes.sort((k1, k2) -> k1.getId().compareTo(k2.getId()));

      // Shows the projects to create a shared link with their
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

      crpList = globalUnitManager.findAll().stream()
        .filter(gu -> gu.isActive() && (gu.getGlobalUnitType().getId() == 1 || gu.getGlobalUnitType().getId() == 3))
        .collect(Collectors.toList());

      List<ProjectInnovationCrp> tempPcrp = null;
      // Update crp list - Delete the actual crp from the list except if this crp was

      if (innovation.getCrps() != null && innovation.getCrps().stream()
        .filter(x -> x != null && x.getGlobalUnit().getId().equals(this.getCurrentGlobalUnit().getId())) != null) {
        tempPcrp = innovation.getCrps().stream()
          .filter(x -> x != null && x.getGlobalUnit().getId().equals(this.getCurrentGlobalUnit().getId()))
          .collect(Collectors.toList());
      }

      if (tempPcrp != null && tempPcrp.size() == 0 && this.getCurrentGlobalUnit() != null) {
        crpList.remove(this.getCurrentGlobalUnit());
      }

    }

    /*
     * get feedback comments
     */
    try {

      feedbackComments = new ArrayList<>();
      feedbackComments = feedbackQACommentableFieldsManager.findAll().stream()
        .filter(f -> f.getSectionName() != null && f.getSectionName().equals("innovation"))
        .collect(Collectors.toList());
      if (feedbackComments != null) {
        for (FeedbackQACommentableFields field : feedbackComments) {
          List<FeedbackQAComment> comments = new ArrayList<FeedbackQAComment>();
          comments = feedbackQACommentManager.findAll().stream()
            .filter(f -> f != null && f.getPhase() != null && f.getPhase().getId() != null
              && f.getPhase().getId().equals(this.getActualPhase().getId()) && f.getParentId() == innovation.getId()
              && f.getField() != null && f.getField().getId() != null && f.getField().getId().equals(field.getId()))
            .collect(Collectors.toList());
          field.setQaComments(comments);
        }
      }

    } catch (Exception e) {
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

      if (innovation.getContributingOrganizations() != null) {
        innovation.getContributingOrganizations().clear();
      }

      if (innovation.getGeographicScopes() != null) {
        innovation.getGeographicScopes().clear();
      }

      if (innovation.getRegions() != null) {
        innovation.getRegions().clear();
      }

      if (innovation.getSharedInnovations() != null) {
        innovation.getSharedInnovations().clear();
      }

      if (innovation.getCenters() != null) {
        innovation.getCenters().clear();
      }

      if (innovation.getMilestones() != null) {
        innovation.getMilestones().clear();
      }

      if (innovation.getSubIdos() != null) {
        innovation.getSubIdos().clear();
      }

      if (innovation.getStudies() != null) {
        innovation.getStudies().clear();
      }

      if (innovation.getProjectOutcomes() != null) {
        innovation.getProjectOutcomes().clear();
      }
      if (innovation.getCrpOutcomes() != null) {
        innovation.getCrpOutcomes().clear();
      }
      // HTTP Post info Values
      // innovation.getProjectInnovationInfo().setGenderFocusLevel(null);
      // innovation.getProjectInnovationInfo().setYouthFocusLevel(null);
      innovation.getProjectInnovationInfo().setProjectExpectedStudy(null);
      innovation.getProjectInnovationInfo().setRepIndPhaseResearchPartnership(null);
      innovation.getProjectInnovationInfo().setRepIndStageInnovation(null);
      innovation.getProjectInnovationInfo().setRepIndInnovationType(null);
      innovation.getProjectInnovationInfo().setRepIndRegion(null);
      innovation.getProjectInnovationInfo().setRepIndDegreeInnovation(null);
      innovation.getProjectInnovationInfo().setLeadOrganization(null);
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

      innovation.setProject(project);

      this.saveOrganizations(innovationDB, phase);
      this.saveDeliverables(innovationDB, phase);
      this.saveContributionOrganizations(innovationDB, phase);
      this.saveSubIdos(innovationDB, phase);
      this.saveCrps(innovationDB, phase);
      this.saveProjects(innovationDB, phase);
      this.saveCenters(innovationDB, phase);
      // this.saveMilestones(innovationDB, phase);
      this.saveStudies(innovationDB, phase);
      // this.saveProjectOutcomes(innovationDB, phase);
      this.saveCrpOutcomes(innovationDB, phase);
      this.saveGeographicScope(innovationDB, phase);

      boolean haveRegions = false;
      boolean haveCountries = false;

      if (innovation.getGeographicScopes() != null) {
        for (ProjectInnovationGeographicScope projectInnovationGeographicScope : innovation.getGeographicScopes()) {

          if (projectInnovationGeographicScope.getRepIndGeographicScope().getId() == 2) {
            haveRegions = true;
          }

          if (projectInnovationGeographicScope.getRepIndGeographicScope().getId() != 1
            && projectInnovationGeographicScope.getRepIndGeographicScope().getId() != 2) {
            haveCountries = true;
          }
        }
      }

      if (haveRegions) {
        // Save the Regions List
        this.saveRegions(innovationDB, phase);
      } else {
        this.deleteLocElements(innovationDB, phase, false);
      }

      if (haveCountries) {
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
      } else {
        this.deleteLocElements(innovationDB, phase, true);
      }

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_INNOVATION_INFOS_RELATION);
      relationsName.add(APConstants.PROJECT_INNOVATION_COUNTRY_RELATION);
      relationsName.add(APConstants.PROJECT_INNOVATION_ORGANIZATION_RELATION);
      relationsName.add(APConstants.PROJECT_INNOVATION_CRP_RELATION);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_CRP_RELATION);
      relationsName.add(APConstants.PROJECT_INNOVATION_CONTRIBUTING_ORGANIZATION_RELATION);
      relationsName.add(APConstants.PROJECT_INNOVATION_SHARED_RELATION);
      relationsName.add(APConstants.PROJECT_INNOVATION_CENTER_RELATION);
      relationsName.add(APConstants.PROJECT_INNOVATION_MILESTONE_RELATION);
      relationsName.add(APConstants.PROJECT_INNOVATION_SUB_IDO);
      relationsName.add(APConstants.PROJECT_INNOVATION_PROJECT_OUTCOMES);

      innovation.setModificationJustification(this.getJustification());

      innovation.getProjectInnovationInfo().setPhase(this.getActualPhase());
      innovation.getProjectInnovationInfo().setProjectInnovation(innovation);

      // Setup focusLevel
      if (innovation.getProjectInnovationInfo().getGenderFocusLevel() != null) {
        if (innovation.getProjectInnovationInfo().getGenderFocusLevel().getId() == -1) {
          innovation.getProjectInnovationInfo().setGenderFocusLevel(null);
        }
      }

      if (innovation.getProjectInnovationInfo().getYouthFocusLevel() != null) {
        if (innovation.getProjectInnovationInfo().getYouthFocusLevel().getId() == -1) {
          innovation.getProjectInnovationInfo().setYouthFocusLevel(null);
        }
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

      if (innovation.getProjectInnovationInfo().getRepIndDegreeInnovation() != null) {
        if (innovation.getProjectInnovationInfo().getRepIndDegreeInnovation().getId() == -1) {
          innovation.getProjectInnovationInfo().setRepIndDegreeInnovation(null);
        }
      }


      if (innovation.getProjectInnovationInfo().getRepIndInnovationType() != null
        && innovation.getProjectInnovationInfo().getRepIndInnovationType().getId() != null
        && innovation.getProjectInnovationInfo().getRepIndInnovationType().getId() != 6) {
        innovation.getProjectInnovationInfo().setOtherInnovationType("");
      }

      // If innovation type is different to genetic, the value of the field -number of
      // innovation- is set to null

      if (innovation.getProjectInnovationInfo().getRepIndInnovationType() != null
        && innovation.getProjectInnovationInfo().getRepIndInnovationType().getId() != null
        && innovation.getProjectInnovationInfo().getRepIndInnovationType().getId() != 1) {
        innovation.getProjectInnovationInfo().setInnovationNumber(null);
      }

      // NOTE -> FOR SOME REASON "CLEAR LEAD" MEANS "NOT A CLEAR LEAD", SO WE HAVE TO REVERSE THE CONDITIONAL
      if (/* NO */clearLead == null || /* NO */clearLead == false) {
        innovation.getProjectInnovationInfo().setClearLead(false);
        if (innovation.getProjectInnovationInfo().getLeadOrganization() != null) {
          if (innovation.getProjectInnovationInfo().getLeadOrganization().getId() == -1) {
            innovation.getProjectInnovationInfo().setLeadOrganization(null);
          }
        }
      } else {
        innovation.getProjectInnovationInfo().setClearLead(true);
        innovation.getProjectInnovationInfo().setLeadOrganization(null);
      }

      // End

      projectInnovationInfoManager.saveProjectInnovationInfo(innovation.getProjectInnovationInfo());
      /**
       * The following is required because we need to update something on
       * the @ProjectInnovation if we want a row created in the auditlog table.
       */
      this.setModificationJustification(innovation);
      projectInnovationManager.saveProjectInnovation(innovation, this.getActionName(), relationsName,
        this.getActualPhase());

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

  public void saveCenters(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationCenters() != null
      && projectInnovation.getProjectInnovationCenters().size() > 0) {

      List<ProjectInnovationCenter> centerPrev =
        new ArrayList<>(projectInnovation.getProjectInnovationCenters().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectInnovationCenter innovationCenter : centerPrev) {
        if (innovation.getCenters() == null || !innovation.getCenters().contains(innovationCenter)) {
          projectInnovationCenterManager.deleteProjectInnovationCenter(innovationCenter.getId());
        }
      }
    }

    // Save form Information
    if (innovation.getCenters() != null) {
      for (ProjectInnovationCenter innovationCenter : innovation.getCenters()) {
        if (innovationCenter.getId() == null) {
          ProjectInnovationCenter innovationCenterSave = new ProjectInnovationCenter();
          innovationCenterSave.setProjectInnovation(projectInnovation);
          innovationCenterSave.setPhase(phase);

          Institution institution = institutionManager.getInstitutionById(innovationCenter.getInstitution().getId());

          innovationCenterSave.setInstitution(institution);

          projectInnovationCenterManager.saveProjectInnovationCenter(innovationCenterSave);
          // This is to add innovationCenterSave to generate correct auditlog.
          innovation.getProjectInnovationCenters().add(innovationCenterSave);
        }
      }
    }
  }

  /**
   * Save Project Innovation contributing organizations
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveContributionOrganizations(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationContributingOrganization() != null
      && projectInnovation.getProjectInnovationContributingOrganization().size() > 0) {

      List<ProjectInnovationContributingOrganization> organizationPrev =
        new ArrayList<>(projectInnovation.getProjectInnovationContributingOrganization().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      if (organizationPrev != null) {
        for (ProjectInnovationContributingOrganization innovationOrganization : organizationPrev) {
          if (innovationOrganization != null && innovation.getContributingOrganizations() != null) {
            if (!innovation.getContributingOrganizations().contains(innovationOrganization)
              && innovationOrganization.getId() != -1) {
              projectInnovationContributingOrganizationManager
                .deleteProjectInnovationContributingOrganization(innovationOrganization.getId());
            }
          }
        }
      }
    }

    // Save form Information
    if (innovation.getContributingOrganizations() != null) {
      for (ProjectInnovationContributingOrganization innovationOrganization : innovation
        .getContributingOrganizations()) {
        if (innovationOrganization.getId() == null) {
          ProjectInnovationContributingOrganization innovationOrganizationSave =
            new ProjectInnovationContributingOrganization();
          innovationOrganizationSave.setProjectInnovation(projectInnovation);
          innovationOrganizationSave.setPhase(phase);

          Institution institution =
            institutionManager.getInstitutionById(innovationOrganization.getInstitution().getId());

          innovationOrganizationSave.setInstitution(institution);

          projectInnovationContributingOrganizationManager
            .saveProjectInnovationContributingOrganization(innovationOrganizationSave);
          // This is to add innovationOrganizationSave to generate correct auditlog.
          innovation.getProjectInnovationContributingOrganization().add(innovationOrganizationSave);
        }
      }
    }
  }

  /**
   * Save Expected Studies Crp Outcome Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveCrpOutcomes(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    try {
      if (projectInnovation.getProjectInnovationCrpOutcomes() != null
        && !projectInnovation.getProjectInnovationCrpOutcomes().isEmpty()) {

        List<ProjectInnovationCrpOutcome> outcomePrev =
          new ArrayList<>(projectInnovation.getProjectInnovationCrpOutcomes().stream()
            .filter(nu -> nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (ProjectInnovationCrpOutcome innovationOutcome : outcomePrev) {
          if (this.innovation.getCrpOutcomes() == null
            || !this.innovation.getCrpOutcomes().contains(innovationOutcome)) {
            this.projectInnovationCrpOutcomeManager.deleteProjectInnovationCrpOutcome(innovationOutcome.getId(),
              this.getActualPhase().getId());
          }
        }
      }
    } catch (Exception e) {
      logger.error("unable to delete crp outcome", e);
    }

    // Save form Information
    if (this.innovation.getCrpOutcomes() != null) {
      for (ProjectInnovationCrpOutcome innovationOutcome : this.innovation.getCrpOutcomes()) {
        ProjectInnovationCrpOutcome innovationOutcomeSave = new ProjectInnovationCrpOutcome();

        if (innovationOutcome != null) {
          // For new crp outcomes
          if (innovationOutcome.getId() == null) {
            innovationOutcomeSave.setProjectInnovation(projectInnovation);
            innovationOutcomeSave.setPhase(phase);
          } else {
            // For old crp outcomes
            try {
              if (innovationOutcome.getId() != null) {
                innovationOutcomeSave =
                  projectInnovationCrpOutcomeManager.getProjectInnovationCrpOutcomeById(innovationOutcome.getId());
              }
            } catch (Exception e) {
              logger.error("unable to get old crp outcome", e);
            }
          }

          if (innovationOutcome.getCrpOutcome() != null && innovationOutcome.getCrpOutcome().getId() != null) {
            CrpProgramOutcome outcome =
              crpProgramOutcomeManager.getCrpProgramOutcomeById(innovationOutcome.getCrpOutcome().getId());
            if (outcome != null) {
              innovationOutcomeSave.setCrpOutcome(outcome);
            }

            this.projectInnovationCrpOutcomeManager.saveProjectInnovationCrpOutcome(innovationOutcomeSave);
            // This is to add studyCrpSave to generate correct auditlog.
            if (!this.innovation.getProjectInnovationCrpOutcomes().contains(innovationOutcomeSave)) {
              this.innovation.getProjectInnovationCrpOutcomes().add(innovationOutcomeSave);
            }
          }
        }
      }
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
        .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectInnovationCrp innovationCrp : crpPrev) {
        if (innovation.getCrps() == null || !innovation.getCrps().contains(innovationCrp)) {
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
          // This is to add innovationCrpSave to generate correct auditlog.
          innovation.getProjectInnovationCrps().add(innovationCrpSave);
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
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectInnovationDeliverable innovationDeliverable : deliverablePrev) {
        if (innovation != null && innovationDeliverable != null && innovation.getDeliverables() != null
          && !innovation.getDeliverables().contains(innovationDeliverable)) {
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
          // This is to add innovationDeliverableSave to generate correct auditlog.
          innovation.getProjectInnovationDeliverables().add(innovationDeliverableSave);
        }
      }
    }
  }

  /**
   * Save Project Innovation Geographic Scope Information
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveGeographicScope(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationGeographicScopes() != null
      && projectInnovation.getProjectInnovationGeographicScopes().size() > 0) {

      List<ProjectInnovationGeographicScope> scopePrev =
        new ArrayList<>(projectInnovation.getProjectInnovationGeographicScopes().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectInnovationGeographicScope innovationScope : scopePrev) {
        if (innovation.getGeographicScopes() == null || !innovation.getGeographicScopes().contains(innovationScope)) {
          projectInnovationGeographicScopeManager.deleteProjectInnovationGeographicScope(innovationScope.getId());
        }
      }
    }

    // Save form Information
    if (innovation.getGeographicScopes() != null) {
      for (ProjectInnovationGeographicScope innovationScope : innovation.getGeographicScopes()) {
        if (innovationScope.getId() == null) {
          ProjectInnovationGeographicScope innovationScopeSave = new ProjectInnovationGeographicScope();
          innovationScopeSave.setProjectInnovation(projectInnovation);
          innovationScopeSave.setPhase(phase);

          RepIndGeographicScope repIndGeographicScope = repIndGeographicScopeManager
            .getRepIndGeographicScopeById(innovationScope.getRepIndGeographicScope().getId());

          innovationScopeSave.setRepIndGeographicScope(repIndGeographicScope);

          projectInnovationGeographicScopeManager.saveProjectInnovationGeographicScope(innovationScopeSave);
          // This is to add innovationCrpSave to generate correct auditlog.
          innovation.getProjectInnovationGeographicScopes().add(innovationScopeSave);
        }
      }
    }
  }

  /**
   * Save Project Innovation Milestone Information
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveMilestones(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationMilestones() != null
      && projectInnovation.getProjectInnovationMilestones().size() > 0) {

      List<ProjectInnovationMilestone> milestonePrev =
        new ArrayList<>(projectInnovation.getProjectInnovationMilestones().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectInnovationMilestone innovationMilestone : milestonePrev) {
        if (innovation.getMilestones() == null || !innovation.getMilestones().contains(innovationMilestone)) {
          projectInnovationMilestoneManager.deleteProjectInnovationMilestone(innovationMilestone.getId());
        }
      }
    }
    // Save policy milestones only if boolean 'has milestones' selection is true
    if (innovation.getProjectInnovationInfo().getHasMilestones() != null
      && innovation.getProjectInnovationInfo().getHasMilestones() == true) {

      // Save form Information
      if (innovation.getMilestones() != null) {
        for (ProjectInnovationMilestone innovationMilestone : innovation.getMilestones()) {
          if (innovationMilestone.getId() == null) {
            ProjectInnovationMilestone innovationMilestoneSave = new ProjectInnovationMilestone();
            innovationMilestoneSave.setProjectInnovation(projectInnovation);
            innovationMilestoneSave.setPhase(phase);
            innovationMilestoneSave.setPrimary(innovationMilestone.getPrimary());

            if (innovation.getMilestones() != null && innovation.getMilestones().size() == 1) {
              innovationMilestoneSave.setPrimary(true);
            }

            CrpMilestone milestone =
              milestoneManager.getCrpMilestoneById(innovationMilestone.getCrpMilestone().getId());
            innovationMilestoneSave.setCrpMilestone(milestone);

            projectInnovationMilestoneManager.saveProjectInnovationMilestone(innovationMilestoneSave);
            // This is to add innovationCenterSave to generate correct auditlog.
            innovation.getProjectInnovationMilestones().add(innovationMilestoneSave);
          } else {
            // if milestone already exist - save primary
            ProjectInnovationMilestone innovationMilestoneSave = new ProjectInnovationMilestone();
            innovationMilestoneSave =
              projectInnovationMilestoneManager.getProjectInnovationMilestoneById(innovationMilestone.getId());
            innovationMilestoneSave.setProjectInnovation(projectInnovation);
            innovationMilestoneSave.setPhase(phase);
            if (innovationMilestoneSave.getCrpMilestone() != null
              && innovationMilestoneSave.getCrpMilestone().getId() != null) {
              CrpMilestone milestone =
                milestoneManager.getCrpMilestoneById(innovationMilestone.getCrpMilestone().getId());
              innovationMilestoneSave.setCrpMilestone(milestone);
            }
            innovationMilestoneSave.setPrimary(innovationMilestone.getPrimary());

            if (innovation.getMilestones() != null && innovation.getMilestones().size() == 1) {
              innovationMilestoneSave.setPrimary(true);
            }

            projectInnovationMilestoneManager.saveProjectInnovationMilestone(innovationMilestoneSave);
            // This is to add innovationCenterSave to generate correct auditlog.
            innovation.getProjectInnovationMilestones().add(innovationMilestoneSave);

          }

        }
      }
    } else {
      // Delete all milestones for this policy
      if (innovation.getMilestones() != null && innovation.getMilestones().size() > 0) {
        for (ProjectInnovationMilestone innovationMilestone : innovation.getMilestones()) {
          try {
            CrpMilestone milestone = milestoneManager.getCrpMilestoneById(innovationMilestone.getId());
            if (milestone != null) {
              projectInnovationMilestoneManager.deleteProjectInnovationMilestone(innovationMilestone.getId());
              // This is to add innovationCenterSave to generate correct auditlog.
              innovation.getProjectInnovationMilestones()
                .remove(projectInnovationMilestoneManager.getProjectInnovationMilestoneById(innovationID));
            }
          } catch (Exception e) {

          }

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
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectInnovationOrganization innovationOrganization : organizationPrev) {
        if (innovation.getOrganizations() == null || !innovation.getOrganizations().contains(innovationOrganization)) {
          projectInnovationOrganizationManager.deleteProjectInnovationOrganization(innovationOrganization.getId());
        }
      }

      // Delete innovations organizations when stage is different to 4
      if (projectInnovation.getProjectInnovationInfo(this.getActualPhase()) != null
        && projectInnovation.getProjectInnovationInfo(this.getActualPhase()).getRepIndStageInnovation() != null
        && projectInnovation.getProjectInnovationInfo(this.getActualPhase()).getRepIndStageInnovation().getId() != 4) {
        for (ProjectInnovationOrganization innovationOrganization : organizationPrev) {
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
          // This is to add innovationOrganizationSave to generate correct auditlog.
          innovation.getProjectInnovationOrganizations().add(innovationOrganizationSave);
        }
      }
    }
  }

  /**
   * Save Expected Studies Project Outcome Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveProjectOutcomes(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationProjectOutcomes() != null
      && projectInnovation.getProjectInnovationProjectOutcomes().size() > 0) {

      List<ProjectInnovationProjectOutcome> outcomePrev =
        new ArrayList<>(projectInnovation.getProjectInnovationProjectOutcomes().stream()
          .filter(nu -> nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectInnovationProjectOutcome innovationOutcome : outcomePrev) {
        if (this.innovation.getProjectOutcomes() == null
          || !this.innovation.getProjectOutcomes().contains(innovationOutcome)) {
          this.projectInnovationProjectOutcomeManager.deleteProjectInnovationProjectOutcome(innovationOutcome.getId(),
            this.getActualPhase().getId());
        }
      }
    }

    // Save form Information
    if (this.innovation.getProjectOutcomes() != null) {
      for (ProjectInnovationProjectOutcome innovationOutcome : this.innovation.getProjectOutcomes()) {
        if (innovationOutcome.getId() == null) {
          ProjectInnovationProjectOutcome innovationOutcomeSave = new ProjectInnovationProjectOutcome();
          innovationOutcomeSave.setProjectInnovation(projectInnovation);
          innovationOutcomeSave.setPhase(phase);

          if (innovationOutcome.getProjectOutcome() != null && innovationOutcome.getProjectOutcome().getId() != null) {
            ProjectOutcome outcome =
              projectOutcomeManager.getProjectOutcomeById(innovationOutcome.getProjectOutcome().getId());
            innovationOutcomeSave.setProjectOutcome(outcome);

            this.projectInnovationProjectOutcomeManager.saveProjectInnovationProjectOutcome(innovationOutcomeSave);
            // This is to add studyCrpSave to generate correct auditlog.
            this.innovation.getProjectInnovationProjectOutcomes().add(innovationOutcomeSave);
          }
        }
      }
    }


  }

  /**
   * Save Innovations Shared Projects Information
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveProjects(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationShareds() != null
      && projectInnovation.getProjectInnovationShareds().size() > 0) {

      List<ProjectInnovationShared> projectPrev =
        new ArrayList<>(projectInnovation.getProjectInnovationShareds().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectInnovationShared innovationProject : projectPrev) {
        if (this.innovation.getSharedInnovations() == null
          || !this.innovation.getSharedInnovations().contains(innovationProject)) {
          this.projectInnovationSharedManager.deleteProjectInnovationShared(innovationProject.getId());
        }
      }
    }

    // Save form Information
    if (this.innovation.getSharedInnovations() != null) {
      for (ProjectInnovationShared innovationProject : this.innovation.getSharedInnovations()) {
        if (innovationProject.getId() == null) {
          ProjectInnovationShared innovationProjectSave = new ProjectInnovationShared();
          innovationProjectSave.setProjectInnovation(projectInnovation);
          innovationProjectSave.setPhase(phase);

          Project project = this.projectManager.getProjectById(innovationProject.getProject().getId());

          innovationProjectSave.setProject(project);

          this.projectInnovationSharedManager.saveProjectInnovationShared(innovationProjectSave);
          // This is to add studyProjectSave to generate correct
          // auditlog.
          this.innovation.getProjectInnovationShareds().add(innovationProjectSave);
        }
      }
    }

  }

  /**
   * Save Project Innovation Region Information
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveRegions(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationRegions() != null
      && projectInnovation.getProjectInnovationRegions().size() > 0) {

      List<ProjectInnovationRegion> regionPrev =
        new ArrayList<>(projectInnovation.getProjectInnovationRegions().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectInnovationRegion innovationRegion : regionPrev) {
        if (innovation.getRegions() == null || !innovation.getRegions().contains(innovationRegion)) {
          projectInnovationRegionManager.deleteProjectInnovationRegion(innovationRegion.getId());
        }
      }
    }

    // Save form Information
    if (innovation.getRegions() != null) {
      for (ProjectInnovationRegion innovationRegion : innovation.getRegions()) {
        if (innovationRegion.getId() == null) {
          ProjectInnovationRegion innovationRegionSave = new ProjectInnovationRegion();
          innovationRegionSave.setProjectInnovation(projectInnovation);
          innovationRegionSave.setPhase(phase);

          LocElement locElement = locElementManager.getLocElementById(innovationRegion.getLocElement().getId());

          innovationRegionSave.setLocElement(locElement);

          projectInnovationRegionManager.saveProjectInnovationRegion(innovationRegionSave);
          // This is to add innovationCrpSave to generate correct auditlog.
          innovation.getProjectInnovationRegions().add(innovationRegionSave);
        }
      }
    }
  }

  /**
   * Save Expected Studies Information
   * 
   * @param innovation
   * @param phase
   */
  public void saveStudies(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    if (projectInnovation.getProjectExpectedStudyInnovations() != null
      && projectInnovation.getProjectExpectedStudyInnovations().size() > 0) {
      List<ProjectExpectedStudyInnovation> studyPrev =
        new ArrayList<>(projectInnovation.getProjectExpectedStudyInnovations().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectExpectedStudyInnovation studyInnovation : studyPrev) {
        if (this.innovation.getStudies() == null || !this.innovation.getStudies().contains(studyInnovation)) {
          this.projectExpectedStudyInnovationManager.deleteProjectExpectedStudyInnovation(studyInnovation.getId());
        }
      }
    }

    // Save form Information
    if (this.innovation.getStudies() != null) {
      for (ProjectExpectedStudyInnovation studyInnovation : this.innovation.getStudies()) {
        if (studyInnovation.getId() == null) {
          ProjectExpectedStudyInnovation studyInnovationSave = new ProjectExpectedStudyInnovation();
          studyInnovationSave.setProjectInnovation(projectInnovation);
          studyInnovationSave.setPhase(phase);

          ProjectExpectedStudy projectExpectedStudy = this.projectExpectedStudyManager
            .getProjectExpectedStudyById(studyInnovation.getProjectExpectedStudy().getId());

          studyInnovationSave.setProjectExpectedStudy(projectExpectedStudy);

          this.projectExpectedStudyInnovationManager.saveProjectExpectedStudyInnovation(studyInnovationSave);
          // This is to add studyInnovationSave to generate correct
          // auditlog.
          this.innovation.getProjectExpectedStudyInnovations().add(studyInnovationSave);
        }
      }
    }
  }

  /**
   * Save Project Innovation SubIdos Information
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveSubIdos(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationSubIdos() != null
      && projectInnovation.getProjectInnovationSubIdos().size() > 0) {

      List<ProjectInnovationSubIdo> subIdoPrev =
        new ArrayList<>(projectInnovation.getProjectInnovationSubIdos().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectInnovationSubIdo innovationSubIdo : subIdoPrev) {
        if (innovation.getSubIdos() == null || !innovation.getSubIdos().contains(innovationSubIdo)) {
          projectInnovationSubIdoManager.deleteProjectInnovationSubIdo(innovationSubIdo.getId());
        }
      }
    }

    // Save form Information
    if (innovation.getSubIdos() != null) {
      for (ProjectInnovationSubIdo innovationSubIdo : innovation.getSubIdos()) {
        if (innovationSubIdo.getId() == null) {
          ProjectInnovationSubIdo innovationSubIdoSave = new ProjectInnovationSubIdo();
          innovationSubIdoSave.setProjectInnovation(projectInnovation);
          innovationSubIdoSave.setPhase(phase);
          innovationSubIdoSave.setPrimary(innovationSubIdo.getPrimary());

          if (innovation.getSubIdos() != null && innovation.getSubIdos().size() == 1) {
            innovationSubIdoSave.setPrimary(true);
          }

          SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoById(innovationSubIdo.getSrfSubIdo().getId());
          innovationSubIdoSave.setSrfSubIdo(srfSubIdo);

          projectInnovationSubIdoManager.saveProjectInnovationSubIdo(innovationSubIdoSave);
          // This is to add innovationCrpSave to generate correct auditlog.
          innovation.getProjectInnovationSubIdos().add(innovationSubIdoSave);
        } else {
          // if sub ido already exist - save primary
          ProjectInnovationSubIdo innovationSubIdoSave = new ProjectInnovationSubIdo();
          innovationSubIdoSave =
            projectInnovationSubIdoManager.getProjectInnovationSubIdoById(innovationSubIdo.getId());
          innovationSubIdoSave.setProjectInnovation(projectInnovation);
          innovationSubIdoSave.setPhase(phase);
          innovationSubIdoSave.setPrimary(innovationSubIdo.getPrimary());

          if (innovationSubIdo.getSrfSubIdo() != null && innovationSubIdo.getSrfSubIdo().getId() != null) {
            SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoById(innovationSubIdo.getSrfSubIdo().getId());
            innovationSubIdoSave.setSrfSubIdo(srfSubIdo);
          }

          if (innovation.getSubIdos() != null && innovation.getSubIdos().size() == 1) {
            innovationSubIdoSave.setPrimary(true);
          }

          projectInnovationSubIdoManager.saveProjectInnovationSubIdo(innovationSubIdoSave);
          // This is to add innovationCrpSave to generate correct auditlog.
          innovation.getProjectInnovationSubIdos().add(innovationSubIdoSave);

        }
      }
    }
  }

  public void setCenters(List<Institution> centers) {
    this.centers = centers;
  }

  public void setClearLead(Boolean clearLead) {
    this.clearLead = clearLead;
  }

  public void setContributionCrpList(List<RepIndContributionOfCrp> contributionCrpList) {
    this.contributionCrpList = contributionCrpList;
  }

  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
  }

  public void setCrpList(List<GlobalUnit> crpList) {
    this.crpList = crpList;
  }

  public void setCrpMilestonePrimary(long crpMilestonePrimary) {
    this.crpMilestonePrimary = crpMilestonePrimary;
  }

  public void setCrpOutcomes(List<CrpProgramOutcome> crpOutcomes) {
    this.crpOutcomes = crpOutcomes;
  }

  public void setDegreeInnovationList(List<RepIndDegreeInnovation> degreeInnovationList) {
    this.degreeInnovationList = degreeInnovationList;
  }

  public void setDeliverableList(List<Deliverable> deliverableList) {
    this.deliverableList = deliverableList;
  }

  public void setExpectedStudyList(List<ProjectExpectedStudy> expectedStudyList) {
    this.expectedStudyList = expectedStudyList;
  }

  public void setFeedbackComments(List<FeedbackQACommentableFields> feedbackComments) {
    this.feedbackComments = feedbackComments;
  }

  public void setFocusLevelList(List<RepIndGenderYouthFocusLevel> focusLevelList) {
    this.focusLevelList = focusLevelList;
  }

  public void setGeographicScopeList(List<RepIndGeographicScope> geographicScopeList) {
    this.geographicScopeList = geographicScopeList;
  }

  public void setIdoList(HashMap<Long, String> idoList) {
    this.idoList = idoList;
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

  public void setOrganizationTypeList(List<RepIndOrganizationType> organizationTypeList) {
    this.organizationTypeList = organizationTypeList;
  }

  public void setPhaseResearchList(List<RepIndPhaseResearchPartnership> phaseResearchList) {
    this.phaseResearchList = phaseResearchList;
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

  public void setProjectOutcomes(List<ProjectOutcome> projectOutcomes) {
    this.projectOutcomes = projectOutcomes;
  }

  public void setRegionList(List<RepIndRegion> regionList) {
    this.regionList = regionList;
  }

  public void setRegions(List<LocElement> regions) {
    this.regions = regions;
  }

  public void setSrfIdos(List<SrfIdo> srfIdos) {
    this.srfIdos = srfIdos;
  }

  public void setSrfSubIdoPrimary(long srfSubIdoPrimary) {
    this.srfSubIdoPrimary = srfSubIdoPrimary;
  }

  public void setStageInnovationList(List<RepIndStageInnovation> stageInnovationList) {
    this.stageInnovationList = stageInnovationList;
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
      // Change the parameters for the new way to validate the data
      validator.validate(this, project, innovation, clearLead, true, true, this.getActualPhase().getYear(),
        this.getActualPhase().getUpkeep());
    }
  }

}
