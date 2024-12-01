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
import org.cgiar.ccafs.marlo.data.manager.ActorManager;
import org.cgiar.ccafs.marlo.data.manager.AllianceLeverManager;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ImpactAreaManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.IntellectualPropertyRightsInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectDeliverableSharedManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationActorManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationAllianceLeversManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationAllianceOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCenterManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationContributingOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationImpactAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationOrganizationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationPartnerTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationPartnershipPersonManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationReferenceComplementarySolutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationReferenceManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationReferenceUrlManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSDGManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSharedManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationToolCategoryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndContributionOfCrpManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndDegreeInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationNatureManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPhaseResearchPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndRegionManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ScalingReadinessManager;
import org.cgiar.ccafs.marlo.data.manager.SdgManager;
import org.cgiar.ccafs.marlo.data.manager.SrfIdoManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.ToolFunctionCategoryManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Actor;
import org.cgiar.ccafs.marlo.data.model.AllianceLever;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ImpactArea;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.IntellectualPropertyRightsInstitution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectDeliverableShared;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationActor;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationAllianceLevers;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationAllianceOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrpOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationDeliverable;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationImpactArea;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnerType;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnership;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnershipPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReference;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceComplementarySolution;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceUrl;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSDG;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationShared;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationToolCategory;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.data.model.RepIndContributionOfCrp;
import org.cgiar.ccafs.marlo.data.model.RepIndDegreeInnovation;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationNature;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationType;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.RepIndPhaseResearchPartnership;
import org.cgiar.ccafs.marlo.data.model.RepIndRegion;
import org.cgiar.ccafs.marlo.data.model.RepIndStageInnovation;
import org.cgiar.ccafs.marlo.data.model.ScalingReadiness;
import org.cgiar.ccafs.marlo.data.model.Sdg;
import org.cgiar.ccafs.marlo.data.model.SrfIdo;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.ToolFunctionCategory;
import org.cgiar.ccafs.marlo.data.model.User;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectInnovationAction extends BaseAction {

  private static final long serialVersionUID = 2025842196563364380L;
  private static final long[] EMPTY_ARRAY = {};
  private static HashMap<String, String> isSaving = new HashMap<>();

  public static HashMap<String, String> getIsSaving() {
    return isSaving;
  }

  public static void setIsSaving(HashMap<String, String> isSaving) {
    ProjectInnovationAction.isSaving = isSaving;
  }

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
  private RepIndInnovationNatureManager repIndInnovationNatureManager;
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
  private ProjectInnovationPartnershipManager projectInnovationPartnershipManager;
  private ProjectInnovationPartnerTypeManager projectInnovationPartnerTypeManager;
  private ProjectInnovationPartnershipPersonManager projectInnovationPartnershipPersonManager;
  private ProjectPartnerManager projectPartnerManager;
  private AllianceLeverManager allianceLeverManager;
  private UserManager userManager;
  private SdgManager sdgManager;
  private ImpactAreaManager impactAreaManager;
  private ProjectInnovationSDGManager projectInnovationSDGManager;
  private ProjectInnovationAllianceLeversManager projectInnovationAllianceLeversManager;
  private ProjectInnovationImpactAreaManager projectInnovationImpactAreaManager;
  private IntellectualPropertyRightsInstitutionManager intellectualPropertyRightsInstitutionManager;
  private ScalingReadinessManager scalingReadinessManager;
  private ProjectInnovationReferenceManager projectInnovationReferenceManager;
  private ProjectInnovationReferenceUrlManager projectInnovationReferenceUrlManager;
  private ProjectInnovationReferenceComplementarySolutionManager projectInnovationReferenceComplementarySolutionManager;
  private ActorManager actorManager;
  private InstitutionTypeManager institutionTypeManager;
  private ProjectInnovationAllianceOrganizationManager projectInnovationAllianceOrganizationManager;

  private ProjectInnovationActorManager projectInnovationActorManager;
  private ProjectInnovationToolCategoryManager projectInnovationToolCategoryManager;
  private ToolFunctionCategoryManager toolFunctionCategoryManager;
  private DeliverableTypeManager deliverableTypeManager;
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
  private List<RepIndInnovationNature> innovationNatureList;
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
  private Boolean isManagingPartnerPersonRequerid;
  private List<Institution> centers;
  private List<CrpMilestone> milestones;
  private List<SrfSubIdo> subIdos;
  private List<SrfSubIdo> principalSubIdo;
  private List<SrfIdo> srfIdos;
  private HashMap<Long, String> idoList;
  private List<ProjectOutcome> projectOutcomes;
  private List<FeedbackQACommentableFields> feedbackComments;
  private List<CrpProgramOutcome> crpOutcomes;
  private List<ProjectPartner> partners;
  private List<ProjectPartnerPerson> partnerPersons;
  private List<Institution> partnerInstitutions;
  private List<AllianceLever> allianceLeverList;
  private List<Sdg> sdgList;
  private List<ImpactArea> impactAreaList;
  private List<IntellectualPropertyRightsInstitution> intellectualInstitutionsList;
  private List<ScalingReadiness> scalingReadinessList;

  private List<Actor> actorList;

  private List<InstitutionType> institutionTypeList;

  private List<ToolFunctionCategory> toolCategoryList;
  private List<DeliverableType> deliverableTypeParent;
  private List<DeliverableType> deliverableSubTypes;

  @Inject
  public ProjectInnovationAction(APConfig config, GlobalUnitManager globalUnitManager,
    ProjectInnovationManager projectInnovationManager, ProjectManager projectManager, PhaseManager phaseManager,
    RepIndPhaseResearchPartnershipManager repIndPhaseResearchPartnershipManager,
    RepIndStageInnovationManager repIndStageInnovationManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager, RepIndInnovationTypeManager repIndInnovationTypeManager,
    RepIndInnovationNatureManager repIndInnovationNatureManager, RepIndRegionManager repIndRegionManager,
    LocElementManager locElementManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    DeliverableManager deriverableManager, RepIndGenderYouthFocusLevelManager focusLevelManager,
    ProjectInnovationInfoManager projectInnovationInfoManager, ProjectInnovationCrpManager projectInnovationCrpManager,
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
    CrpProgramOutcomeManager crpProgramOutcomeManager,
    ProjectInnovationPartnershipManager projectInnovationPartnershipManager,
    ProjectInnovationPartnerTypeManager projectInnovationPartnerTypeManager,
    ProjectInnovationPartnershipPersonManager projectInnovationPartnershipPersonManager,
    ProjectPartnerManager projectPartnerManager, AllianceLeverManager allianceLeverManager, UserManager userManager,
    SdgManager sdgManager, ProjectInnovationAllianceLeversManager projectInnovationAllianceLeversManager,
    ProjectInnovationSDGManager projectInnovationSDGManager, ImpactAreaManager impactAreaManager,
    ProjectInnovationImpactAreaManager projectInnovationImpactAreaManager,
    IntellectualPropertyRightsInstitutionManager intellectualPropertyRightsInstitutionManager,
    ScalingReadinessManager scalingReadinessManager,
    ProjectInnovationReferenceManager projectInnovationReferenceManager, ActorManager actorManager,
    InstitutionTypeManager institutionTypeManager,
    ProjectInnovationAllianceOrganizationManager projectInnovationAllianceOrganizationManager,
    ProjectInnovationReferenceUrlManager projectInnovationReferenceUrlManager,
    ProjectInnovationReferenceComplementarySolutionManager projectInnovationReferenceComplementarySolutionManager,
    ProjectInnovationActorManager projectInnovationActorManager,
    ToolFunctionCategoryManager toolFunctionCategoryManager,
    ProjectInnovationToolCategoryManager projectInnovationToolCategoryManager,
    DeliverableTypeManager deliverableTypeManager) {
    super(config);
    this.projectInnovationManager = projectInnovationManager;
    this.globalUnitManager = globalUnitManager;
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.repIndPhaseResearchPartnershipManager = repIndPhaseResearchPartnershipManager;
    this.repIndStageInnovationManager = repIndStageInnovationManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.repIndInnovationTypeManager = repIndInnovationTypeManager;
    this.repIndInnovationNatureManager = repIndInnovationNatureManager;
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
    this.projectInnovationPartnershipManager = projectInnovationPartnershipManager;
    this.projectInnovationPartnerTypeManager = projectInnovationPartnerTypeManager;
    this.projectInnovationPartnershipPersonManager = projectInnovationPartnershipPersonManager;
    this.projectPartnerManager = projectPartnerManager;
    this.allianceLeverManager = allianceLeverManager;
    this.userManager = userManager;
    this.sdgManager = sdgManager;
    this.projectInnovationAllianceLeversManager = projectInnovationAllianceLeversManager;
    this.projectInnovationSDGManager = projectInnovationSDGManager;
    this.impactAreaManager = impactAreaManager;
    this.projectInnovationImpactAreaManager = projectInnovationImpactAreaManager;
    this.intellectualPropertyRightsInstitutionManager = intellectualPropertyRightsInstitutionManager;
    this.scalingReadinessManager = scalingReadinessManager;
    this.projectInnovationReferenceManager = projectInnovationReferenceManager;
    this.actorManager = actorManager;
    this.institutionTypeManager = institutionTypeManager;
    this.projectInnovationAllianceOrganizationManager = projectInnovationAllianceOrganizationManager;
    this.projectInnovationReferenceUrlManager = projectInnovationReferenceUrlManager;
    this.projectInnovationReferenceComplementarySolutionManager =
      projectInnovationReferenceComplementarySolutionManager;
    this.projectInnovationActorManager = projectInnovationActorManager;
    this.toolFunctionCategoryManager = toolFunctionCategoryManager;
    this.projectInnovationToolCategoryManager = projectInnovationToolCategoryManager;
    this.deliverableTypeManager = deliverableTypeManager;

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

  public void fillDeliverableSubTypes(int deliverableType) {
    DeliverableType typeDB = deliverableTypeManager.getDeliverableTypeById(deliverableType);
    // deliverable.getDeliverableInfo(this.getActualPhase()).setDeliverableType(typeDB);
    Long deliverableTypeParentId = typeDB.getDeliverableCategory().getId();

    deliverableSubTypes = new ArrayList<>(
      deliverableTypeManager.findAll().stream().filter(dt -> dt.isActive() && dt.getDeliverableCategory() != null
        && dt.getDeliverableCategory().getId() == deliverableTypeParentId).collect(Collectors.toList()));
  }

  public List<Actor> getActorList() {
    return actorList;
  }

  public List<AllianceLever> getAllianceLeverList() {
    return allianceLeverList;
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

  public List<Map<String, Object>> getDeliverablesSubTypes(long deliverableTypeID) {
    List<Map<String, Object>> subTypes = new ArrayList<>();
    Map<String, Object> keyOutput;

    DeliverableType deliverableType = deliverableTypeManager.getDeliverableTypeById(deliverableTypeID);
    if (deliverableType != null) {
      if (deliverableType.getDeliverableTypes() != null) {
        for (DeliverableType deliverableSubType : deliverableType.getDeliverableTypes().stream()
          .collect(Collectors.toList())) {
          keyOutput = new HashMap<String, Object>();
          keyOutput.put("id", deliverableSubType.getId());
          keyOutput.put("name", deliverableSubType.getName());
          keyOutput.put("description", deliverableSubType.getDescription());
          keyOutput.put("fair", deliverableSubType.getFair());
          subTypes.add(keyOutput);
        }
      }
    }
    return subTypes;

  }

  public List<DeliverableType> getDeliverableSubTypes() {
    return deliverableSubTypes;
  }

  public List<DeliverableType> getDeliverableTypeParent() {
    return deliverableTypeParent;
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

  public List<ImpactArea> getImpactAreaList() {
    return impactAreaList;
  }

  public ProjectInnovation getInnovation() {
    return innovation;
  }

  public long getInnovationID() {
    return innovationID;
  }

  public List<RepIndInnovationNature> getInnovationNatureList() {
    return innovationNatureList;
  }

  public List<RepIndInnovationType> getInnovationTypeList() {
    return innovationTypeList;
  }

  public List<Institution> getInstitutions() {
    return institutions;
  }

  public List<InstitutionType> getInstitutionTypeList() {
    return institutionTypeList;
  }

  public List<IntellectualPropertyRightsInstitution> getIntellectualInstitutionsList() {
    return intellectualInstitutionsList;
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

  public List<Institution> getPartnerInstitutions() {
    return partnerInstitutions;
  }

  public List<ProjectPartnerPerson> getPartnerPersons() {
    return partnerPersons;
  }

  public List<ProjectPartner> getPartners() {
    return partners;
  }

  /**
   * @return an array of integers.
   */
  public long[] getPersonsIds(ProjectInnovationPartnership projectInnovationPartnership) {
    if (projectInnovationPartnership != null) {
      final List<ProjectInnovationPartnershipPerson> pPersons = projectInnovationPartnership.getPartnershipPersons()
        .stream().filter(pp -> (pp.getUser() != null) && (pp.getUser().getId() != null) && (pp.getUser().getId() > 0))
        .collect(Collectors.toList());
      if (pPersons != null) {
        final long[] ids = new long[pPersons.size()];
        for (int i = 0; i < ids.length; i++) {
          if ((pPersons.get(i).getUser() != null) && (pPersons.get(i).getUser().getId() != null)) {
            ids[i] = pPersons.get(i).getUser().getId();
          }
        }
        return ids;
      }
    }

    return EMPTY_ARRAY;
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

  public List<ScalingReadiness> getScalingReadinessList() {
    return scalingReadinessList;
  }

  public List<Sdg> getSdgList() {
    return sdgList;
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

  public List<ToolFunctionCategory> getToolCategoryList() {
    return toolCategoryList;
  }

  public String getTransaction() {
    return transaction;
  }

  /**
   * This method gets a list of users
   *
   * @param institutionId institution identifier
   * @return User list
   */
  public List<User> getUserList(Long institutionId) {

    final List<User> users = new ArrayList<>();
    List<ProjectPartner> partnersTmp = new ArrayList<>();
    try {
      partnersTmp = this.projectPartnerManager.findAllByPhaseProjectAndInstitution(this.projectID,
        this.getActualPhase().getId(), institutionId);
    } catch (final Exception e) {
      this.logger.error("unable to get partners");
    }
    if ((partnersTmp != null) && !partnersTmp.isEmpty()) {
      final ProjectPartner projectPartner = partnersTmp.get(0);
      final List<ProjectPartnerPerson> partnerPersons = new ArrayList<>(projectPartner.getProjectPartnerPersons()
        .stream().filter(ProjectPartnerPerson::isActive).collect(Collectors.toList()));
      for (final ProjectPartnerPerson projectPartnerPerson : partnerPersons) {

        users.add(projectPartnerPerson.getUser());
      }
    }

    return users;
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
              && projectInnovationContributingOrganization.getInstitution().getId() != null && institutionManager
                .getInstitutionById(projectInnovationContributingOrganization.getInstitution().getId()) != null) {
              Institution institution = institutionManager
                .getInstitutionById(projectInnovationContributingOrganization.getInstitution().getId());
              projectInnovationContributingOrganization.setInstitution(institution);
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

            if (projectPolicies != null && !projectPolicies.isEmpty() && projectPolicies.get(0) != null) {
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

            if (projectPolicies != null && !projectPolicies.isEmpty() && projectPolicies.get(0) != null) {
              subIdoPrimaryId = projectPolicies.get(0).getSrfSubIdo().getId(); //
              srfSubIdoPrimary = projectPolicies.get(0).getSrfSubIdo().getId(); //
            }
          }
        }

        // Expected Study projectInnovationPartnerships List
        if (this.innovation.getProjectInnovationPartnerships() != null) {

          final List<ProjectInnovationPartnership> deList = this.innovation.getProjectInnovationPartnerships().stream()
            .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId()) && dp
              .getProjectInnovationPartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
            .collect(Collectors.toList());

          if ((deList != null) && !deList.isEmpty()) {
            try {
              Collections.sort(deList, (p1, p2) -> p1.getInstitution().getId().compareTo(p2.getInstitution().getId()));
            } catch (final Exception e) {
              this.logger.error("unable to sort dlist", e);
            }
            this.innovation.setPartnerships(new ArrayList<>());
            for (final ProjectInnovationPartnership projectInnovationPartnership : deList) {

              if (projectInnovationPartnership.getProjectInnovationPartnershipPersons() != null) {
                final List<ProjectInnovationPartnershipPerson> partnershipPersons =
                  new ArrayList<>(projectInnovationPartnership.getProjectInnovationPartnershipPersons().stream()
                    .filter(ProjectInnovationPartnershipPerson::isActive).collect(Collectors.toList()));
                projectInnovationPartnership.setPartnershipPersons(partnershipPersons);
              }
              this.innovation.getPartnerships().add(projectInnovationPartnership);
            }

          }
        }

        // Expected Study projectInnovationPartnerships List (Institutions)
        // Expected Study projectInnovationPartnerships List (Centers)

        /*
         * if (this.innovation.getProjectInnovationPartnerships() != null) {
         * final List<ProjectInnovationPartnership> deList = this.innovation.getProjectInnovationPartnerships().stream()
         * .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId()) && dp
         * .getProjectInnovationPartnerType().getId().equals(APConstants.INNOVATION_PARTNERSHIP_TYPE_INSTITUTION))
         * .collect(Collectors.toList());
         * if ((deList != null) && !deList.isEmpty()) {
         * try {
         * Collections.sort(deList, (p1, p2) -> p1.getInstitution().getId().compareTo(p2.getInstitution().getId()));
         * } catch (final Exception e) {
         * this.logger.error("unable to sort dlist", e);
         * }
         * this.innovation.setInstitutions(new ArrayList<>());
         * for (final ProjectInnovationPartnership projectInnovationPartnership : deList) {
         * this.innovation.getInstitutions().add(projectInnovationPartnership);
         * }
         * }
         * }
         * if (this.innovation.getProjectInnovationPartnerships() != null) {
         * final List<ProjectInnovationPartnership> deList = this.innovation.getProjectInnovationPartnerships().stream()
         * .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
         * && dp.getProjectInnovationPartnerType().getId().equals(APConstants.INNOVATION_PARTNERSHIP_TYPE_CENTER))
         * .collect(Collectors.toList());
         * if ((deList != null) && !deList.isEmpty()) {
         * try {
         * Collections.sort(deList, (p1, p2) -> p1.getInstitution().getId().compareTo(p2.getInstitution().getId()));
         * } catch (final Exception e) {
         * this.logger.error("unable to sort dlist", e);
         * }
         * this.innovation.setCenters(new ArrayList<>());
         * for (final ProjectInnovationPartnership projectInnovationPartnership : deList) {
         * this.innovation.getCenters().add(projectInnovationPartnership);
         * }
         * }
         * }
         */

        // Innovations Alliance levers
        if (innovation.getProjectInnovationAllianceLevers() != null) {
          innovation.setAllianceLevers(new ArrayList<>(innovation.getProjectInnovationAllianceLevers().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Innovations SDGs
        if (innovation.getProjectInnovationSDGs() != null) {
          innovation.setSdgs(new ArrayList<>(innovation.getProjectInnovationSDGs().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Innovations impact area
        if (innovation.getProjectInnovationImpactAreas() != null) {
          innovation.setImpactAreas(new ArrayList<>(innovation.getProjectInnovationImpactAreas().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Innovations alliance organizations
        if (innovation.getProjectInnovationAllianceOrganizations() != null) {
          innovation
            .setAllianceOrganizations(new ArrayList<>(innovation.getProjectInnovationAllianceOrganizations().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Innovations actors
        if (innovation.getProjectInnovationActors() != null) {
          innovation.setActors(new ArrayList<>(innovation.getProjectInnovationActors().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Innovations tool categories
        if (innovation.getProjectInnovationToolCategories() != null) {
          innovation.setToolCategories(new ArrayList<>(innovation.getProjectInnovationToolCategories().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Innovations references
        if (innovation.getProjectInnovationReferences() != null) {
          innovation.setReferences(new ArrayList<>(innovation.getProjectInnovationReferences().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Innovations references URL
        if (innovation.getProjectInnovationReferenceUrls() != null) {
          innovation.setReferenceUrls(new ArrayList<>(innovation.getProjectInnovationReferenceUrls().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Innovations references Complementary solutions
        if (innovation.getProjectInnovationReferenceComplementarySolutions() != null) {
          innovation.setReferenceComplementarySolutions(
            new ArrayList<>(innovation.getProjectInnovationReferenceComplementarySolutions().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
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

            if (projectPolicies != null && !projectPolicies.isEmpty() && projectPolicies.get(0) != null) {
              milestonePrimaryId = projectPolicies.get(0).getCrpMilestone().getId(); //
              crpMilestonePrimary = projectPolicies.get(0).getCrpMilestone().getId(); //
            }
          }
        }
      }

      this.allianceLeverList = this.allianceLeverManager.findAll();
      this.partners = new ArrayList<>();
      this.partnerInstitutions = new ArrayList<>();
      this.isManagingPartnerPersonRequerid = this.hasSpecificities(APConstants.CRP_MANAGING_PARTNERS_CONTACT_PERSONS);
      this.sdgList = this.sdgManager.findAll();
      this.impactAreaList = this.impactAreaManager.findAll();
      this.intellectualInstitutionsList = this.intellectualPropertyRightsInstitutionManager.findAll();
      this.scalingReadinessList = this.scalingReadinessManager.findAll();
      this.actorList = this.actorManager.findAll();
      this.toolCategoryList = this.toolFunctionCategoryManager.findAll();
      this.toolCategoryList.sort((o1, o2) -> {
        try {
          int num1 = Integer.parseInt(o1.getDescription());
          int num2 = Integer.parseInt(o2.getDescription());
          return Integer.compare(num1, num2);
        } catch (NumberFormatException | NullPointerException e) {
          // Handle invalid or null descriptions; treat as "infinite" for sorting.
          return 0;
        }
      });

      boolean has_specific_management_deliverables =
        this.hasSpecificities(APConstants.CRP_HAS_SPECIFIC_MANAGEMENT_DELIVERABLE_TYPES);

      deliverableTypeParent = new ArrayList<>(
        deliverableTypeManager.findAll().stream().filter(dt -> dt.isActive() && dt.getDeliverableCategory() == null
          && dt.getCrp() == null && !dt.getAdminType().booleanValue()).collect(Collectors.toList()));

      deliverableTypeParent.addAll(new ArrayList<>(deliverableTypeManager.findAll().stream()
        .filter(dt -> dt.isActive() && dt.getDeliverableCategory() == null && dt.getCrp() != null
          && dt.getCrp().getId().longValue() == loggedCrp.getId().longValue() && !dt.getAdminType().booleanValue())
        .collect(Collectors.toList())));

      if (project.getProjecInfoPhase(this.getActualPhase()).getAdministrative() != null
        && project.getProjecInfoPhase(this.getActualPhase()).getAdministrative().booleanValue()) {

        deliverableTypeParent
          .addAll(deliverableTypeManager.findAll().stream()
            .filter(dt -> dt.isActive() && dt.getDeliverableCategory() == null && dt.getCrp() == null
              && dt.getAdminType().booleanValue() && !has_specific_management_deliverables)
            .collect(Collectors.toList()));

        deliverableTypeParent.addAll(new ArrayList<>(deliverableTypeManager.findAll().stream()
          .filter(dt -> dt.isActive() && dt.getDeliverableCategory() == null && dt.getCrp() != null
            && dt.getCrp().getId().longValue() == loggedCrp.getId().longValue() && dt.getAdminType().booleanValue())
          .collect(Collectors.toList())));
      }
      /*
       * deliverableSubTypes = new ArrayList<>(
       * deliverableTypeManager.findAll().stream().filter(dt -> dt.isActive() && dt.getDeliverableCategory() != null
       * && dt.getDeliverableCategory().getId() == deliverableTypeParentId).collect(Collectors.toList()));
       */
      deliverableSubTypes = new ArrayList<>(deliverableTypeManager.findAll().stream()
        .filter(dt -> dt.isActive() && dt.getDeliverableCategory() != null).collect(Collectors.toList()));

      try {
        this.institutionTypeList =
          Optional.ofNullable(this.institutionTypeManager.findAll()).orElse(Collections.emptyList()).stream()
            .filter(it -> it != null && it.getSource() != null && it.getSource() == 1 && it.getParent() == null)
            .collect(Collectors.toList());
      } catch (Exception e) {
        Log.error("error getting institution types " + e);
      }

      // Order SDG list by ID
      if (this.sdgList != null) {
        this.sdgList = this.sdgList.stream().filter(sdg -> sdg != null && sdg.getId() != null)
          .sorted(Comparator.comparing(Sdg::getId)).collect(Collectors.toList());
      }

      final List<ProjectPartner> partnersTmp = this.projectPartnerManager
        .findAllByPhaseProject(this.innovation.getProject().getId(), this.getActualPhase().getId());

      if (partnersTmp != null) {
        for (final ProjectPartner partner : partnersTmp) {
          final List<ProjectPartnerPerson> persons = partner.getProjectPartnerPersons().stream()
            .filter(ProjectPartnerPerson::isActive).collect(Collectors.toList());
          if (!this.isManagingPartnerPersonRequerid) {
            this.partners.add(partner);
            this.partnerInstitutions.add(partner.getInstitution());
          } else {
            if (!persons.isEmpty()) {
              this.partners.add(partner);
              this.partnerInstitutions.add(partner.getInstitution());
            }
          }
        }
        this.partnerPersons = new ArrayList<>();

        this.partnerPersons =
          this.partners.stream().flatMap(e -> e.getProjectPartnerPersons().stream()).collect(Collectors.toList());
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
      institutions = institutionManager.findAll().stream().collect(Collectors.toList());

      // Regions for Geographic Scope Regional Selection
      regions = locElementManager.findAll().stream()
        .filter(c -> c.getLocElementType().getId().intValue() == 1 && c.isActive() && c.getIsoNumeric() != null)
        .collect(Collectors.toList());

      phaseResearchList = repIndPhaseResearchPartnershipManager.findAll();
      stageInnovationList = repIndStageInnovationManager.findAll();
      geographicScopeList = repIndGeographicScopeManager.findAll();
      innovationTypeList = repIndInnovationTypeManager.findAll();
      innovationNatureList = repIndInnovationNatureManager.findAll();
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

      List<ProjectExpectedStudy> allProjectStudies = new ArrayList<>();
      // SubIdos List
      subIdos = srfSubIdoManager.findAll();
      this.expectedStudyList = new ArrayList<>();

      // Load Studies
      List<ProjectExpectedStudy> studies = project.getProjectExpectedStudies().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudyInfo(this.getActualPhase()) != null)
        .collect(Collectors.toList());
      if (studies != null && !studies.isEmpty()) {
        allProjectStudies.addAll(studies);
      }

      // Load Shared studies
      List<ExpectedStudyProject> expectedStudyProject = new ArrayList<>(project.getExpectedStudyProjects().stream()
        .filter(px -> px.isActive() && px.getPhase().getId().equals(this.getActualPhase().getId())
          && px.getProjectExpectedStudy().isActive()
          && px.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getActualPhase()) != null)
        .collect(Collectors.toList()));
      if (expectedStudyProject != null && !expectedStudyProject.isEmpty()) {
        for (ExpectedStudyProject expectedStudy : expectedStudyProject) {
          if (!allProjectStudies.contains(expectedStudy.getProjectExpectedStudy())) {
            allProjectStudies.add(expectedStudy.getProjectExpectedStudy());
          }
        }
      }

      if (allProjectStudies != null && !allProjectStudies.isEmpty()) {
        // Editable project studies: Current cycle year-1 will be editable except
        // Complete and Cancelled.
        // Every study of the current cycle year will be editable
        expectedStudyList = new ArrayList<>();
        expectedStudyList = allProjectStudies.stream()
          .filter(ex -> ex.isActive() && ex.getProjectExpectedStudyInfo(phase) != null
            && ex.getProjectExpectedStudyInfo().getStudyType() != null
            && ex.getProjectExpectedStudyInfo().getStudyType().getId().intValue() == 1 && ex.getProject() != null)
          .collect(Collectors.toList());
      }
      List<ProjectExpectedStudy> evidences = projectExpectedStudyManager.getStudiesByPhase(phase).stream()
        .filter(s -> s != null && s.getProject() == null).collect(Collectors.toList());
      /*
       * List<ProjectExpectedStudy> evidences = projectExpectedStudyManager.findAll().stream()
       * .filter(s -> s != null && s.getProject() == null && s.getProjectExpectedStudyInfo(this.getActualPhase()) !=
       * null
       * && s.getProjectExpectedStudyInfo().getPhase() != null
       * && s.getProjectExpectedStudyInfo().getPhase().getId().equals(this.getActualPhase().getId()))
       * .collect(Collectors.toList());
       */
      if (evidences != null) {
        if (expectedStudyList != null && !expectedStudyList.isEmpty()) {
          expectedStudyList.addAll(evidences);
        } else {
          expectedStudyList = evidences;
        }
      }

      if (phase != null && phase.getDeliverableInfos() != null && project != null
        && !phase.getDeliverableInfos().isEmpty()) {
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
      if (innovation.getSharedInnovations() != null && !innovation.getSharedInnovations().isEmpty()) {
        for (ProjectInnovationShared sharedInnovation : innovation.getSharedInnovations()) {
          if (sharedInnovation != null && sharedInnovation.getProject() != null
            && sharedInnovation.getProject().getId() != null) {
            projectSharedList.add(sharedInnovation.getProject());
          }
        }


        // Get deliverable list for shared innovations projects
        if (projectSharedList != null && !projectSharedList.isEmpty()) {
          for (Project projectInnovationShared : projectSharedList) {
            if (phase != null && phase.getDeliverableInfos() != null && projectInnovationShared != null
              && !phase.getDeliverableInfos().isEmpty()) {
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
      if (this.hasSpecificities(this.feedbackModule())) {
        feedbackComments = new ArrayList<>();
        feedbackComments = feedbackQACommentableFieldsManager.findAll().stream()
          .filter(f -> f.getSectionName() != null && f.getSectionName().equals("innovation"))
          .collect(Collectors.toList());
        if (feedbackComments != null) {
          for (FeedbackQACommentableFields field : feedbackComments) {
            List<FeedbackQAComment> comments = new ArrayList<FeedbackQAComment>();
            feedbackQACommentManager.findAllByPhase(this.getActualPhase().getId()).stream()
              .filter(f -> f != null && f.getParentId() == innovation.getId() && f.getField() != null
                && f.getField().getId() != null && f.getField().getId().equals(field.getId()))
              .collect(Collectors.toList());

            /*
             * comments = feedbackQACommentManager.findAll().stream()
             * .filter(f -> f != null && f.getPhase() != null && f.getPhase().getId() != null
             * && f.getPhase().getId().equals(this.getActualPhase().getId()) && f.getParentId() == innovation.getId()
             * && f.getField() != null && f.getField().getId() != null && f.getField().getId().equals(field.getId()))
             * .collect(Collectors.toList());
             */
            field.setQaComments(comments);
          }
        }
      }
    } catch (Exception e) {
      Log.error("error getting feedback comments " + e);
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
      if (innovation.getPartnerships() != null) {
        innovation.getPartnerships().clear();
      }
      if (innovation.getSdgs() != null) {
        innovation.getSdgs().clear();
      }
      if (innovation.getAllianceLevers() != null) {
        innovation.getAllianceLevers().clear();
      }
      if (innovation.getReferences() != null) {
        innovation.getReferences().clear();
      }
      if (innovation.getReferenceUrls() != null) {
        innovation.getReferenceUrls().clear();
      }
      if (innovation.getReferenceComplementarySolutions() != null) {
        innovation.getReferenceComplementarySolutions().clear();
      }
      if (innovation.getImpactAreas() != null) {
        innovation.getImpactAreas().clear();
      }
      if (innovation.getAllianceLevers() != null) {
        innovation.getAllianceLevers().clear();
      }
      if (innovation.getAllianceOrganizations() != null) {
        innovation.getAllianceOrganizations().clear();
      }
      if (innovation.getActors() != null) {
        innovation.getActors().clear();
      }
      if (deliverableTypeParent != null) {
        deliverableTypeParent.clear();
      }
      // HTTP Post info Values
      // innovation.getProjectInnovationInfo().setGenderFocusLevel(null);
      // innovation.getProjectInnovationInfo().setYouthFocusLevel(null);
      try {
        innovation.getProjectInnovationInfo().setProjectExpectedStudy(null);
        innovation.getProjectInnovationInfo().setRepIndPhaseResearchPartnership(null);
        innovation.getProjectInnovationInfo().setRepIndStageInnovation(null);
        innovation.getProjectInnovationInfo().setRepIndInnovationNature(null);
        innovation.getProjectInnovationInfo().setRepIndInnovationType(null);
        innovation.getProjectInnovationInfo().setRepIndInnovationNature(null);
        innovation.getProjectInnovationInfo().setRepIndRegion(null);
        innovation.getProjectInnovationInfo().setRepIndDegreeInnovation(null);
        innovation.getProjectInnovationInfo().setLeadOrganization(null);
        innovation.getProjectInnovationInfo().setIntellectualPropertyInstitution(null);
      } catch (Exception e) {
        logger.error("unable to clean info properties", e);
      }
    }

    // SrfIDO
    idoList = new HashMap<>();
    srfIdos = new ArrayList<>();
    for (SrfIdo srfIdo : srfIdoManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
      idoList.put(srfIdo.getId(), srfIdo.getDescription());

      srfIdo.setSubIdos(srfIdo.getSrfSubIdos().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      srfIdos.add(srfIdo);
    }

    this.validateTabs();
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      ProjectInnovationAction.getIsSaving().put("" + innovationID, "1");

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
      this.saveProjectInnovationPartnership(innovationDB, phase);
      this.saveAllianceLevers(innovationDB, phase);
      this.saveSDGs(innovationDB, phase);
      this.saveImpactAreas(innovationDB, phase);
      this.saveRegions(innovationDB, phase);
      this.saveReferences(innovationDB, phase);
      this.saveReferenceUrls(innovationDB, phase);
      this.saveReferenceComplementarySolution(innovationDB, phase);
      this.saveAllianceOrganizations(innovationDB, phase);
      this.saveActors(innovationDB, phase);
      this.saveToolCategories(innovationDB, phase);

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
      if (innovation.getProjectInnovationInfo().getGenderFocusLevel() != null
        && innovation.getProjectInnovationInfo().getGenderFocusLevel().getId() == -1) {
        innovation.getProjectInnovationInfo().setGenderFocusLevel(null);
      }

      if (innovation.getProjectInnovationInfo().getYouthFocusLevel() != null
        && innovation.getProjectInnovationInfo().getYouthFocusLevel().getId() == -1) {
        innovation.getProjectInnovationInfo().setYouthFocusLevel(null);
      }
      // End

      // Validate negative Values
      if (innovation.getProjectInnovationInfo().getProjectExpectedStudy() != null
        && innovation.getProjectInnovationInfo().getProjectExpectedStudy().getId() == -1) {
        innovation.getProjectInnovationInfo().setProjectExpectedStudy(null);
      }

      if (innovation.getProjectInnovationInfo().getRepIndPhaseResearchPartnership() != null
        && innovation.getProjectInnovationInfo().getRepIndPhaseResearchPartnership().getId() == -1) {
        innovation.getProjectInnovationInfo().setRepIndPhaseResearchPartnership(null);
      }

      if (innovation.getProjectInnovationInfo().getRepIndStageInnovation() != null
        && innovation.getProjectInnovationInfo().getRepIndStageInnovation().getId() == -1) {
        innovation.getProjectInnovationInfo().setRepIndStageInnovation(null);
      }

      if (innovation.getProjectInnovationInfo().getRepIndInnovationType() != null
        && innovation.getProjectInnovationInfo().getRepIndInnovationType().getId() == -1) {
        innovation.getProjectInnovationInfo().setRepIndInnovationType(null);
      }

      if (innovation.getProjectInnovationInfo().getRepIndInnovationNature() != null
        && innovation.getProjectInnovationInfo().getRepIndInnovationNature().getId() == -1) {
        innovation.getProjectInnovationInfo().setRepIndInnovationNature(null);
      }

      if (innovation.getProjectInnovationInfo().getRepIndRegion() != null
        && innovation.getProjectInnovationInfo().getRepIndRegion().getId() == -1) {
        innovation.getProjectInnovationInfo().setRepIndRegion(null);
      }

      if (innovation.getProjectInnovationInfo().getRepIndDegreeInnovation() != null
        && innovation.getProjectInnovationInfo().getRepIndDegreeInnovation().getId() == -1) {
        innovation.getProjectInnovationInfo().setRepIndDegreeInnovation(null);
      }

      if (innovation.getProjectInnovationInfo().getIntellectualPropertyInstitution() != null
        && innovation.getProjectInnovationInfo().getIntellectualPropertyInstitution().getId() == -1) {
        innovation.getProjectInnovationInfo().setIntellectualPropertyInstitution(null);
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
        if (innovation.getProjectInnovationInfo().getLeadOrganization() != null
          && innovation.getProjectInnovationInfo().getLeadOrganization().getId() == -1) {
          innovation.getProjectInnovationInfo().setLeadOrganization(null);
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

  /**
   * Save Project Innovation Actors
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveActors(ProjectInnovation projectInnovation, Phase phase) {
    // Search and deleted form Information
    try {
      if (projectInnovation.getProjectInnovationActors() != null
        && !projectInnovation.getProjectInnovationActors().isEmpty()) {

        /*
         * List<ProjectInnovationActor> actorPrev = new
         * ArrayList<>(projectInnovation.getProjectInnovationActors().stream()
         * .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
         */
        List<ProjectInnovationActor> actorPrev = projectInnovationActorManager
          .getProjectInnovationActorByInnovationAndPhase(innovationID, this.getActualPhase().getId());
        try {
          for (ProjectInnovationActor actor : actorPrev) {

            if ((actor.getId() != null)
              && (innovation.getActors() == null || !innovation.getActors().contains(actor))) {
              if (projectInnovationActorManager.existProjectInnovationActor(actor.getId())) {
                projectInnovationActorManager.deleteProjectInnovationActor(actor.getId());
              }
            }
          }
        } catch (Exception e) {
          Log.error("error deleting actor " + e);
        }
      }

      // Save form Information
      if (innovation.getActors() != null && !innovation.getActors().isEmpty()) {
        for (ProjectInnovationActor innovationActor : innovation.getActors()) {
          if (innovationActor.getId() != null && innovationActor.getId() == -1) {
            innovationActor.setId(null);
          }

          if (innovationActor.getActor() != null && innovationActor.getActor().getId() != null
            && innovationActor.getActor().getId() == -1) {
            innovationActor.setActor(null);
          }
          ProjectInnovationActor innovationActorSave = new ProjectInnovationActor();

          try {
            if (innovationActor.getId() != null) {
              innovationActorSave =
                projectInnovationActorManager.getProjectInnovationActorById(innovationActor.getId());
            }
          } catch (Exception e) {
            logger.error("unable to get old actors", e);
          }

          innovationActorSave.setWomenYouth(innovationActor.getWomenYouth());
          innovationActorSave.setWomenNotYouth(innovationActor.getWomenNotYouth());
          innovationActorSave.setMenYouth(innovationActor.getMenYouth());
          innovationActorSave.setMenNotYouth(innovationActor.getMenNotYouth());
          innovationActorSave.setNonbinaryYouth(innovationActor.getNonbinaryYouth());
          innovationActorSave.setNonbinaryNotYouth(innovationActor.getNonbinaryNotYouth());
          innovationActorSave.setActor(innovationActor.getActor());
          innovationActorSave.setProjectInnovation(projectInnovation);
          innovationActorSave.setPhase(phase);

          projectInnovationActorManager.saveProjectInnovationActor(innovationActorSave);
          // This is to add innovationActorSave to generate correct auditlog.
          innovation.getProjectInnovationActors().add(innovationActorSave);

        }
      }
    } catch (Exception e) {
      Log.error("Error saving actors " + e);
    }
  }

  /**
   * Save Project Innovation Alliance Levers
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveAllianceLevers(ProjectInnovation projectInnovation, Phase phase) {
    try {
      // Search and deleted form Information
      if (projectInnovation.getProjectInnovationAllianceLevers() != null
        && !projectInnovation.getProjectInnovationAllianceLevers().isEmpty()) {
        /*
         * List<ProjectInnovationAllianceLevers> allianceLeverPrev =
         * new ArrayList<>(projectInnovation.getProjectInnovationAllianceLevers().stream()
         * .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));
         */
        List<ProjectInnovationAllianceLevers> allianceLeverPrev = new ArrayList<>(projectInnovationAllianceLeversManager
          .getProjectInnovationAllianceLeversByInnovationAndPhase(innovationID, this.getActualPhase().getId())).stream()
            .filter(a -> a != null && a.getAllianceLever() != null && a.getAllianceLever().getId() != null)
            .collect(Collectors.toList());

        try {
          for (ProjectInnovationAllianceLevers allianceLever : allianceLeverPrev) {
            if (allianceLeverPrev != null && (allianceLever.getId() != null || allianceLever.getId() != -1)
              && (innovation.getAllianceLevers() == null || !innovation.getAllianceLevers().contains(allianceLever))) {
              projectInnovationAllianceLeversManager.deleteProjectInnovationAllianceLevers(allianceLever.getId());
            }
          }
        } catch (Exception e) {
          Log.error("error deleting alliance levers " + e);
        }
      }

      // Save form Information
      if (innovation.getAllianceLevers() != null) {
        for (ProjectInnovationAllianceLevers innovationAllianceLever : innovation.getAllianceLevers()) {

          if (innovationAllianceLever.getId() != null && innovationAllianceLever.getId() == -1) {
            innovationAllianceLever.setId(null);
          }
          ProjectInnovationAllianceLevers innovationAllianceLeverSave = new ProjectInnovationAllianceLevers();

          try {
            if (innovationAllianceLever.getId() != null) {
              innovationAllianceLeverSave = projectInnovationAllianceLeversManager
                .getProjectInnovationAllianceLeversById(innovationAllianceLever.getId());
            }
          } catch (Exception e) {
            logger.error("unable to get old actors", e);
          }
          innovationAllianceLeverSave.setAllianceLever(innovationAllianceLever.getAllianceLever());
          innovationAllianceLeverSave.setProjectInnovation(projectInnovation);
          innovationAllianceLeverSave.setPhase(phase);

          projectInnovationAllianceLeversManager.saveProjectInnovationAllianceLevers(innovationAllianceLeverSave);
          // This is to add innovationAllianceLeverSave to generate correct auditlog.
          innovation.getProjectInnovationAllianceLevers().add(innovationAllianceLeverSave);
        }
      }

    } catch (Exception e) {
      Log.error("Error saving alliance levers " + e);
    }
  }

  /**
   * Save Project Innovation Alliance Organizations
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveAllianceOrganizations(ProjectInnovation projectInnovation, Phase phase) {
    // Search and deleted form Information
    try {
      if (projectInnovation.getProjectInnovationAllianceOrganizations() != null
        && !projectInnovation.getProjectInnovationAllianceOrganizations().isEmpty()) {

        List<ProjectInnovationAllianceOrganization> allianceOrganizationPrev =
          new ArrayList<>(projectInnovation.getProjectInnovationAllianceOrganizations().stream()
            .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (ProjectInnovationAllianceOrganization allianceOrganization : allianceOrganizationPrev) {
          if ((allianceOrganization.getId() != null || allianceOrganization.getId() != -1)
            && (innovation.getAllianceOrganizations() == null
              || !innovation.getAllianceOrganizations().contains(allianceOrganization))) {
            projectInnovationAllianceOrganizationManager
              .deleteProjectInnovationAllianceOrganization(allianceOrganization.getId());
          }
        }
      }

      // Save form Information
      if (innovation.getAllianceOrganizations() != null) {
        for (ProjectInnovationAllianceOrganization innovationAllianceOrganization : innovation
          .getAllianceOrganizations()) {

          if (innovationAllianceOrganization.getId() != null && innovationAllianceOrganization.getId() == -1) {
            innovationAllianceOrganization.setId(null);
          }

          if (innovationAllianceOrganization.getInstitutionType() != null
            && innovationAllianceOrganization.getInstitutionType().getId() != null
            && innovationAllianceOrganization.getInstitutionType().getId() == -1) {
            innovationAllianceOrganization.setInstitutionType(null);
          }

          ProjectInnovationAllianceOrganization innovationAllianceOrganizationSave =
            new ProjectInnovationAllianceOrganization();
          try {
            if (innovationAllianceOrganization.getId() != null) {
              innovationAllianceOrganizationSave = projectInnovationAllianceOrganizationManager
                .getProjectInnovationAllianceOrganizationById(innovationAllianceOrganization.getId());
            }
          } catch (Exception e) {
            logger.error("unable to get old actors", e);
          }

          innovationAllianceOrganizationSave.setInstitutionType(innovationAllianceOrganization.getInstitutionType());
          innovationAllianceOrganizationSave.setOrganizationName(innovationAllianceOrganization.getOrganizationName());
          innovationAllianceOrganizationSave.setScalingPartner(innovationAllianceOrganization.getScalingPartner());
          innovationAllianceOrganizationSave.setProjectInnovation(projectInnovation);
          innovationAllianceOrganizationSave.setPhase(phase);

          projectInnovationAllianceOrganizationManager
            .saveProjectInnovationAllianceOrganization(innovationAllianceOrganizationSave);
          // This is to add innovationAllianceOrganizationSave to generate correct auditlog.
          innovation.getProjectInnovationAllianceOrganizations().add(innovationAllianceOrganizationSave);

        }
      }
    } catch (Exception e) {
      Log.error("error saving actors " + e);
    }

  }

  public void saveCenters(ProjectInnovation projectInnovation, Phase phase) {

    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationCenters() != null
      && !projectInnovation.getProjectInnovationCenters().isEmpty()) {

      List<ProjectInnovationCenter> centerPrev =
        new ArrayList<>(projectInnovation.getProjectInnovationCenters().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectInnovationCenter innovationCenter : centerPrev) {
        if ((innovationCenter.getId() != null || innovationCenter.getId() != -1) && innovation.getCenters() == null
          || !innovation.getCenters().contains(innovationCenter)) {
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
      && !projectInnovation.getProjectInnovationContributingOrganization().isEmpty()) {

      List<ProjectInnovationContributingOrganization> organizationPrev =
        new ArrayList<>(projectInnovation.getProjectInnovationContributingOrganization().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      if (organizationPrev != null) {
        for (ProjectInnovationContributingOrganization innovationOrganization : organizationPrev) {
          if (innovationOrganization != null && innovation.getContributingOrganizations() != null
            && !innovation.getContributingOrganizations().contains(innovationOrganization)
            && innovationOrganization.getId() != -1) {
            projectInnovationContributingOrganizationManager
              .deleteProjectInnovationContributingOrganization(innovationOrganization.getId());
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
      && !projectInnovation.getProjectInnovationCrps().isEmpty()) {

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
      && !projectInnovation.getProjectInnovationDeliverables().isEmpty()) {

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
      && !projectInnovation.getProjectInnovationGeographicScopes().isEmpty()) {

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
   * Save Project Innovation Impact Area
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveImpactAreas(ProjectInnovation projectInnovation, Phase phase) {
    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationImpactAreas() != null
      && !projectInnovation.getProjectInnovationImpactAreas().isEmpty()) {

      List<ProjectInnovationImpactArea> impactAreaPrev =
        new ArrayList<>(projectInnovation.getProjectInnovationImpactAreas().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectInnovationImpactArea impactArea : impactAreaPrev) {
        if (innovation.getImpactAreas() == null || !innovation.getImpactAreas().contains(impactArea)) {
          projectInnovationImpactAreaManager.deleteProjectInnovationImpactArea(impactArea.getId());
        }
      }
    }

    // Save form Information
    if (innovation.getImpactAreas() != null) {
      for (ProjectInnovationImpactArea innovationImpactArea : innovation.getImpactAreas()) {
        if (innovationImpactArea.getId() == null) {
          ProjectInnovationImpactArea innovationImpactAreaSave = new ProjectInnovationImpactArea();
          innovationImpactAreaSave.setImpactArea(innovationImpactArea.getImpactArea());
          innovationImpactAreaSave.setProjectInnovation(projectInnovation);
          innovationImpactAreaSave.setPhase(phase);

          projectInnovationImpactAreaManager.saveProjectInnovationImpactArea(innovationImpactAreaSave);
          // This is to add innovationImpactAreaSave to generate correct auditlog.
          innovation.getProjectInnovationImpactAreas().add(innovationImpactAreaSave);
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
      && !projectInnovation.getProjectInnovationMilestones().isEmpty()) {

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
      && innovation.getProjectInnovationInfo().getHasMilestones()) {

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
      if (innovation.getMilestones() != null && !innovation.getMilestones().isEmpty()) {
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
      && !projectInnovation.getProjectInnovationOrganizations().isEmpty()) {

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
        if (innovationOrganization.getId() != null && innovationOrganization.getId() == -1) {
          innovationOrganization.setId(null);
        }
        if (innovationOrganization.getRepIndOrganizationType() != null
          && innovationOrganization.getRepIndOrganizationType().getId() != null
          && innovationOrganization.getRepIndOrganizationType().getId() == -1) {
          innovationOrganization.setRepIndOrganizationType(null);
        }
        ProjectInnovationOrganization innovationOrganizationSave = new ProjectInnovationOrganization();
        try {
          if (innovationOrganization.getId() != null) {
            innovationOrganizationSave =
              projectInnovationOrganizationManager.getProjectInnovationOrganizationById(innovationOrganization.getId());
          }
        } catch (Exception e) {
          logger.error("unable to get old innovation", e);
        }

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

  /**
   * 2024/10/23 save Deliverable Partnership Responsible
   *
   * @param project innovation
   * @param phase
   */
  public void saveProjectInnovationPartnership(ProjectInnovation projectInnovation, Phase phase) {

    if ((projectInnovation.getProjectInnovationPartnerships() != null)
      && (!projectInnovation.getProjectInnovationPartnerships().isEmpty())) {
      List<ProjectInnovationPartnership> projectInnovationPartnershipCustom = null;
      try {
        projectInnovationPartnershipCustom = this.projectInnovationPartnershipManager
          .findByInnovationAndPhase(projectInnovation.getId(), this.getActualPhase().getId());

      } catch (final Exception e) {
        this.logger.info(e.getMessage());
      }
      List<ProjectInnovationPartnership> projectInnovationPartnershipPrev = null;
      if ((projectInnovationPartnershipCustom != null) && !projectInnovationPartnershipCustom.isEmpty()) {
        projectInnovationPartnershipPrev = projectInnovationPartnershipCustom.stream()
          .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId()) && dp
            .getProjectInnovationPartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
          .collect(Collectors.toList());
      }
      try {
        // 2024/07/22 conditional was added to avoid exception by null data
        if ((projectInnovationPartnershipPrev != null) && !projectInnovationPartnershipPrev.isEmpty()) {
          for (final ProjectInnovationPartnership projectInnovationPartnership : projectInnovationPartnershipPrev) {
            if ((this.innovation.getPartnerships() == null) || ((this.innovation.getPartnerships() != null)
              && !this.innovation.getPartnerships().contains(projectInnovationPartnership))) {
              this.projectInnovationPartnershipManager
                .deleteProjectInnovationPartnership(projectInnovationPartnership.getId());
            }
          }
        }
      } catch (final Exception e) {
        this.logger.error("unable to delete deliverable user partnership in saveProjectExpectedPartnership function  ",
          e.getMessage());
      }
    }

    final ProjectInnovationPartnerType projectInnovationPartnerType = this.projectInnovationPartnerTypeManager
      .getProjectInnovationPartnerTypeById(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE);
    if (this.innovation.getPartnerships() != null) {
      for (final ProjectInnovationPartnership projectInnovationPartnership : this.innovation.getPartnerships()) {
        if (projectInnovationPartnership.getId() != null) {
          ProjectInnovationPartnership projectInnovationPartnershipSave = this.projectInnovationPartnershipManager
            .getProjectInnovationPartnershipById(projectInnovationPartnership.getId());

          if (projectInnovationPartnership.getInstitution().getId() != null) {
            if (projectInnovationPartnership.getInstitution().getId() != -1) {
              final Institution institution =
                this.institutionManager.getInstitutionById(projectInnovationPartnership.getInstitution().getId());
              projectInnovationPartnershipSave.setInstitution(institution);

              if (projectInnovationPartnership.getPartnershipPersons() != null) {
                projectInnovationPartnershipSave
                  .setPartnershipPersons(projectInnovationPartnership.getPartnershipPersons());
              }
              projectInnovationPartnershipSave = this.projectInnovationPartnershipManager
                .saveProjectInnovationPartnership(projectInnovationPartnershipSave);
              this.saveProjectInnovationPartnershipsPersons(projectInnovationPartnership,
                projectInnovationPartnershipSave);
            } else {
              this.projectInnovationPartnershipManager
                .deleteProjectInnovationPartnership(projectInnovationPartnership.getId());
            }
          }

        } else {
          ProjectInnovationPartnership projectInnovationPartnershipSave = new ProjectInnovationPartnership();
          projectInnovationPartnershipSave.setPhase(this.getActualPhase());
          projectInnovationPartnershipSave.setProjectInnovation(projectInnovation);
          projectInnovationPartnershipSave.setCreatedBy(this.getCurrentUser());
          projectInnovationPartnershipSave.setProjectInnovationPartnerType(projectInnovationPartnerType);

          if ((projectInnovationPartnership.getInstitution() != null)
            && (projectInnovationPartnership.getInstitution().getId() != null)
            && projectInnovationPartnership.getInstitution().getId() != -1) {
            final Institution institution =
              this.institutionManager.getInstitutionById(projectInnovationPartnership.getInstitution().getId());
            projectInnovationPartnershipSave.setInstitution(institution);


            if (projectInnovationPartnership.getPartnershipPersons() != null) {
              projectInnovationPartnershipSave
                .setPartnershipPersons(projectInnovationPartnership.getPartnershipPersons());
            }

            projectInnovationPartnershipSave = this.projectInnovationPartnershipManager
              .saveProjectInnovationPartnership(projectInnovationPartnershipSave);
            this.saveProjectInnovationPartnershipsPersons(projectInnovationPartnership,
              projectInnovationPartnershipSave);
          }

        }
      }
    }
  }

  /*
   * @param projectInnovationPartnership (front-end element to save)
   * @param projectInnovationPartnershipDB (previous element form DB)
   */
  private void saveProjectInnovationPartnershipsPersons(ProjectInnovationPartnership projectInnovationPartnership,
    ProjectInnovationPartnership projectInnovationPartnershipDB) {

    if ((projectInnovationPartnershipDB.getProjectInnovationPartnershipPersons() != null)
      && !projectInnovationPartnershipDB.getProjectInnovationPartnershipPersons().isEmpty()) {

      final List<ProjectInnovationPartnershipPerson> projectInnovationPartnershipsPersonPrev =
        projectInnovationPartnershipDB.getProjectInnovationPartnershipPersons().stream()
          .filter(ProjectInnovationPartnershipPerson::isActive).collect(Collectors.toList());

      for (final ProjectInnovationPartnershipPerson projectInnovationPartnershipsPerson : projectInnovationPartnershipsPersonPrev) {
        if ((projectInnovationPartnership.getPartnershipPersons() == null)
          || !projectInnovationPartnership.getPartnershipPersons().contains(projectInnovationPartnershipsPerson)) {
          this.projectInnovationPartnershipPersonManager
            .deleteProjectInnovationPartnershipPerson(projectInnovationPartnershipsPerson.getId());
        }
      }
    }

    if (projectInnovationPartnership.getPartnershipPersons() != null) {
      for (final ProjectInnovationPartnershipPerson person : projectInnovationPartnership.getPartnershipPersons()) {
        if (person.getId() != null) {
          final ProjectInnovationPartnershipPerson projectInnovationPartnershipsPersonNew =
            this.projectInnovationPartnershipPersonManager.getProjectInnovationPartnershipPersonById(person.getId());

          if ((person.getUser() != null) && (person.getUser().getId() != null)) {
            if (!person.getUser().getId().equals(projectInnovationPartnershipsPersonNew.getUser().getId())) {
              projectInnovationPartnershipsPersonNew.setUser(this.userManager.getUser(person.getUser().getId()));
              this.projectInnovationPartnershipPersonManager
                .saveProjectInnovationPartnershipPerson(projectInnovationPartnershipsPersonNew);
            }
          } else {
            this.projectInnovationPartnershipPersonManager.deleteProjectInnovationPartnershipPerson(person.getId());
          }
        } else {
          if ((person.getUser() != null) && (person.getUser().getId() != null)) {
            final ProjectInnovationPartnershipPerson projectInnovationPartnershipsPersonNew =
              new ProjectInnovationPartnershipPerson();
            projectInnovationPartnershipsPersonNew.setUser(this.userManager.getUser(person.getUser().getId()));
            projectInnovationPartnershipsPersonNew.setProjectInnovationPartnership(projectInnovationPartnershipDB);
            this.projectInnovationPartnershipPersonManager
              .saveProjectInnovationPartnershipPerson(projectInnovationPartnershipsPersonNew);
          }
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
      && !projectInnovation.getProjectInnovationProjectOutcomes().isEmpty()) {

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
      && !projectInnovation.getProjectInnovationShareds().isEmpty()) {

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
   * Save Expected Studies References Information
   * 
   * @param projectInnovation
   * @param phase
   */
  private void saveReferenceComplementarySolution(ProjectInnovation projectInnovation, Phase phase) {
    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationReferenceComplementarySolutions() != null) {
      final List<ProjectInnovationReferenceComplementarySolution> referencesPrev =
        new ArrayList<>(projectInnovation.getProjectInnovationReferenceComplementarySolutions().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectInnovationReferenceComplementarySolution innovationReference : referencesPrev) {
        if ((this.innovation.getProjectInnovationReferenceComplementarySolutions() == null)
          || !this.innovation.getReferenceComplementarySolutions().contains(innovationReference)) {
          this.projectInnovationReferenceComplementarySolutionManager
            .deleteProjectInnovationReferenceComplementarySolution(innovationReference.getId());
        }
      }
    }

    // Save form Information
    if (this.innovation.getReferenceComplementarySolutions() != null) {
      for (final ProjectInnovationReferenceComplementarySolution innovationReference : this.innovation
        .getReferenceComplementarySolutions()) {

        if (innovationReference.getId() != null && innovationReference.getId() == -1) {
          innovationReference.setId(null);
        }

        ProjectInnovationReferenceComplementarySolution innovationReferenceSave =
          new ProjectInnovationReferenceComplementarySolution();
        if (innovationReference.getId() != null) {
          innovationReferenceSave = this.projectInnovationReferenceComplementarySolutionManager
            .getProjectInnovationReferenceComplementarySolutionById(innovationReference.getId());
        }

        innovationReferenceSave.setProjectInnovation(projectInnovation);
        innovationReferenceSave.setPhase(phase);
        innovationReferenceSave.setReference(innovationReference.getReference());
        innovationReferenceSave.setLink(innovationReference.getLink());
        innovationReferenceSave.setEvidenceByDeliverable(innovationReference.getEvidenceByDeliverable());
        if (innovationReference.getDeliverable() != null && innovationReference.getDeliverable().getId() != null
          && innovationReference.getDeliverable().getId() == -1) {
          innovationReference.setDeliverable(null);
        }
        innovationReferenceSave.setDeliverable(innovationReference.getDeliverable());
        if (innovationReference.getDeliverableType() != null && innovationReference.getDeliverableType().getId() != null
          && innovationReference.getDeliverableType().getId() == -1) {
          innovationReference.setDeliverableType(null);
        }
        innovationReferenceSave.setDeliverableType(innovationReference.getDeliverableType());

        this.projectInnovationReferenceComplementarySolutionManager
          .saveProjectInnovationReferenceComplementarySolution(innovationReferenceSave);
        // This is to add innovationReferenceSave to generate correct
        // auditlog.
        this.innovation.getProjectInnovationReferenceComplementarySolutions().add(innovationReferenceSave);

      }
    }
  }

  /**
   * Save Expected Studies References Information
   * 
   * @param projectInnovation
   * @param phase
   */
  private void saveReferences(ProjectInnovation projectInnovation, Phase phase) {
    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationReferences() != null) {
      final List<ProjectInnovationReference> referencesPrev =
        new ArrayList<>(projectInnovation.getProjectInnovationReferences().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectInnovationReference studyReference : referencesPrev) {
        if ((this.innovation.getReferences() == null) || !this.innovation.getReferences().contains(studyReference)) {
          this.projectInnovationReferenceManager.deleteProjectInnovationReference(studyReference.getId());
        }
      }
    }

    // Save form Information
    if (this.innovation.getReferences() != null) {
      for (final ProjectInnovationReference studyReference : this.innovation.getReferences()) {
        if (studyReference.getId() != null && studyReference.getId() == -1) {
          studyReference.setId(null);
        }
        ProjectInnovationReference studyReferenceSave = new ProjectInnovationReference();
        if (studyReference.getId() != null) {
          studyReferenceSave =
            this.projectInnovationReferenceManager.getProjectInnovationReferenceById(studyReference.getId());
        }
        studyReferenceSave.setProjectInnovation(projectInnovation);
        studyReferenceSave.setPhase(phase);
        studyReferenceSave.setReference(studyReference.getReference());
        studyReferenceSave.setLink(studyReference.getLink());
        boolean externalAutor = false;
        if (studyReference.getEvidenceByDeliverable() != null) {
          externalAutor = true;
        }
        studyReferenceSave.setExternalAuthor(externalAutor);
        studyReferenceSave.setEvidenceByDeliverable(studyReference.getEvidenceByDeliverable());
        if (studyReference.getDeliverable() != null && studyReference.getDeliverable().getId() != null
          && studyReference.getDeliverable().getId() == -1) {
          studyReference.setDeliverable(null);
        }
        studyReferenceSave.setDeliverable(studyReference.getDeliverable());
        if (studyReference.getDeliverableType() != null && studyReference.getDeliverableType().getId() != null
          && studyReference.getDeliverableType().getId() == -1) {
          studyReference.setDeliverableType(null);
        } else {
          if (studyReference.getDeliverableType() != null && studyReference.getDeliverableType().getId() != null
            && deliverableTypeManager.existDeliverableType(studyReference.getDeliverableType().getId())) {
            studyReferenceSave.setDeliverableType(studyReference.getDeliverableType());
          }
        }
        this.projectInnovationReferenceManager.saveProjectInnovationReference(studyReferenceSave);
        // This is to add studyReferenceSave to generate correct
        // auditlog.
        this.innovation.getProjectInnovationReferences().add(studyReferenceSave);
      }
    }
  }

  /**
   * Save Expected Studies ReferenceUrls Information
   * 
   * @param projectInnovation
   * @param phase
   */
  private void saveReferenceUrls(ProjectInnovation projectInnovation, Phase phase) {
    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationReferenceUrls() != null) {
      final List<ProjectInnovationReferenceUrl> referencesPrev =
        new ArrayList<>(projectInnovation.getProjectInnovationReferenceUrls().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectInnovationReferenceUrl innovationReferenceUrl : referencesPrev) {
        if ((this.innovation.getReferenceUrls() == null)
          || !this.innovation.getReferenceUrls().contains(innovationReferenceUrl)) {
          this.projectInnovationReferenceUrlManager.deleteProjectInnovationReferenceUrl(innovationReferenceUrl.getId());
        }
      }
    }

    // Save form Information
    if (this.innovation.getReferenceUrls() != null) {
      for (final ProjectInnovationReferenceUrl innovationReferenceUrl : this.innovation.getReferenceUrls()) {

        if (innovationReferenceUrl.getId() != null && innovationReferenceUrl.getId() == -1) {
          innovationReferenceUrl.setId(null);
        }

        ProjectInnovationReferenceUrl innovationReferenceUrlSave = new ProjectInnovationReferenceUrl();
        if (innovationReferenceUrl.getId() != null) {
          innovationReferenceUrlSave = this.projectInnovationReferenceUrlManager
            .getProjectInnovationReferenceUrlById(innovationReferenceUrl.getId());
        }

        innovationReferenceUrlSave.setProjectInnovation(projectInnovation);
        innovationReferenceUrlSave.setPhase(phase);
        innovationReferenceUrlSave.setReference(innovationReferenceUrl.getReference());
        innovationReferenceUrlSave.setLink(innovationReferenceUrl.getLink());

        // innovationReferenceUrlSave.setInnovationType(innovationReferenceUrl.getInnovationType());
        // innovationReferenceUrlSave.setAdditionalArticleType(innovationReferenceUrl.getAdditionalArticleType());
        // innovationReferenceUrlSave.setDatasetType(innovationReferenceUrl.getDatasetType());
        innovationReferenceUrlSave.setEvidenceByDeliverable(innovationReferenceUrl.getEvidenceByDeliverable());
        if (innovationReferenceUrl.getDeliverable() != null && innovationReferenceUrl.getDeliverable().getId() != null
          && innovationReferenceUrl.getDeliverable().getId() == -1) {
          innovationReferenceUrl.setDeliverable(null);
        }
        innovationReferenceUrlSave.setDeliverable(innovationReferenceUrl.getDeliverable());

        if (innovationReferenceUrl.getDeliverableType() != null
          && innovationReferenceUrl.getDeliverableType().getId() != null
          && innovationReferenceUrl.getDeliverableType().getId() == -1) {
          innovationReferenceUrl.setDeliverableType(null);
        }

        innovationReferenceUrlSave.setDeliverableType(innovationReferenceUrl.getDeliverableType());

        this.projectInnovationReferenceUrlManager.saveProjectInnovationReferenceUrl(innovationReferenceUrlSave);
        // This is to add innovationReferenceUrlSave to generate correct
        // auditlog.
        this.innovation.getProjectInnovationReferenceUrls().add(innovationReferenceUrlSave);

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
      && !projectInnovation.getProjectInnovationRegions().isEmpty()) {

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
   * Save Project Innovation SDG
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveSDGs(ProjectInnovation projectInnovation, Phase phase) {
    try {
      // Search and deleted form Information
      if (projectInnovation.getProjectInnovationSDGs() != null
        && !projectInnovation.getProjectInnovationSDGs().isEmpty()) {

        List<ProjectInnovationSDG> sdgPrev = new ArrayList<>(projectInnovation.getProjectInnovationSDGs().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (ProjectInnovationSDG sdg : sdgPrev) {
          if ((innovation.getSdgs() == null || !innovation.getSdgs().contains(sdg)) && sdg.getId() != null) {
            projectInnovationSDGManager.deleteProjectInnovationSDG(sdg.getId());
          }
        }
      }

      // Save form Information
      if (innovation.getSdgs() != null) {
        for (ProjectInnovationSDG innovationSdg : innovation.getSdgs()) {
          if (innovationSdg.getId() == null) {
            if (innovationSdg.getSdg() != null) {
              ProjectInnovationSDG innovationSdgSave = new ProjectInnovationSDG();
              innovationSdgSave.setSdg(innovationSdg.getSdg());
              innovationSdgSave.setPhase(this.getActualPhase());
              innovationSdgSave.setProjectInnovation(projectInnovation);
              innovationSdgSave.setPhase(phase);

              projectInnovationSDGManager.saveProjectInnovationSDG(innovationSdgSave);
              // This is to add innovationSdgSave to generate correct auditlog.
              innovation.getProjectInnovationSDGs().add(innovationSdgSave);
            }
          }
        }
      }
    } catch (Exception e) {
      Log.error("error in sdg save process " + e);
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
      && !projectInnovation.getProjectExpectedStudyInnovations().isEmpty()) {
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
      && !projectInnovation.getProjectInnovationSubIdos().isEmpty()) {

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

  /**
   * Save Project Innovation ToolCategories
   * 
   * @param projectInnovation
   * @param phase
   */
  public void saveToolCategories(ProjectInnovation projectInnovation, Phase phase) {
    // Search and deleted form Information
    if (projectInnovation.getProjectInnovationToolCategories() != null
      && !projectInnovation.getProjectInnovationToolCategories().isEmpty()) {

      List<ProjectInnovationToolCategory> toolPrev =
        new ArrayList<>(projectInnovation.getProjectInnovationToolCategories().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (ProjectInnovationToolCategory actor : toolPrev) {
        if (innovation.getToolCategories() == null || !innovation.getToolCategories().contains(actor)) {
          projectInnovationToolCategoryManager.deleteProjectInnovationToolCategory(actor.getId());
        }
      }
    }

    // Save form Information
    if (innovation.getToolCategories() != null) {
      for (ProjectInnovationToolCategory innovationToolCategory : innovation.getToolCategories()) {
        ProjectInnovationToolCategory innovationToolCategorySave = new ProjectInnovationToolCategory();
        try {
          if (innovationToolCategory.getId() != null) {
            innovationToolCategorySave =
              projectInnovationToolCategoryManager.getProjectInnovationToolCategoryById(innovationToolCategory.getId());
          }
        } catch (Exception e) {
          logger.error("unable to get old actors", e);
        }
        innovationToolCategorySave.setToolCategory(innovationToolCategory.getToolCategory());
        innovationToolCategorySave.setProjectInnovation(projectInnovation);
        innovationToolCategorySave.setPhase(phase);
        innovationToolCategorySave.setOtherNarrative(innovationToolCategory.getOtherNarrative());

        projectInnovationToolCategoryManager.saveProjectInnovationToolCategory(innovationToolCategorySave);
        // This is to add innovationToolCategorySave to generate correct auditlog.
        innovation.getProjectInnovationToolCategories().add(innovationToolCategorySave);

      }
    }
  }

  public void setActorList(List<Actor> actorList) {
    this.actorList = actorList;
  }

  public void setAllianceLeverList(List<AllianceLever> allianceLeverList) {
    this.allianceLeverList = allianceLeverList;
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

  public void setDeliverableSubTypes(List<DeliverableType> deliverableSubTypes) {
    this.deliverableSubTypes = deliverableSubTypes;
  }

  public void setDeliverableTypeParent(List<DeliverableType> deliverableTypeParent) {
    this.deliverableTypeParent = deliverableTypeParent;
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

  public void setImpactAreaList(List<ImpactArea> impactAreaList) {
    this.impactAreaList = impactAreaList;
  }

  public void setInnovation(ProjectInnovation innovation) {
    this.innovation = innovation;
  }

  public void setInnovationID(long innovationID) {
    this.innovationID = innovationID;
  }

  public void setInnovationNatureList(List<RepIndInnovationNature> innovationNatureList) {
    this.innovationNatureList = innovationNatureList;
  }

  public void setInnovationTypeList(List<RepIndInnovationType> innovationTypeList) {
    this.innovationTypeList = innovationTypeList;
  }

  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setInstitutionTypeList(List<InstitutionType> institutionTypeList) {
    this.institutionTypeList = institutionTypeList;
  }

  public void
    setIntellectualInstitutionsList(List<IntellectualPropertyRightsInstitution> intellectualInstitutionsList) {
    this.intellectualInstitutionsList = intellectualInstitutionsList;
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

  public void setPartnerInstitutions(List<Institution> partnerInstitutions) {
    this.partnerInstitutions = partnerInstitutions;
  }

  public void setPartnerPersons(List<ProjectPartnerPerson> partnerPersons) {
    this.partnerPersons = partnerPersons;
  }

  public void setPartners(List<ProjectPartner> partners) {
    this.partners = partners;
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

  public void setScalingReadinessList(List<ScalingReadiness> scalingReadinessList) {
    this.scalingReadinessList = scalingReadinessList;
  }

  public void setSdgList(List<Sdg> sdgList) {
    this.sdgList = sdgList;
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

  public void setToolCategoryList(List<ToolFunctionCategory> toolCategoryList) {
    this.toolCategoryList = toolCategoryList;
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

  public void validateTabs() {
    // the next code allows execute the validation process
    String valueSaving;
    valueSaving = ProjectInnovationAction.getIsSaving().get(innovationID + "");
    if (valueSaving != null) {

      String value = "0";
      value = BaseAction.getIsInnovationGeneralInformationCompleteMap().get(innovationID + "");
      if (value != null && value.equals("1")) {
        this.setInnovationGeneralInformationComplete(false);
      } else {
        this.setInnovationGeneralInformationComplete(true);
      }

      value = BaseAction.getIsInnovationAllianceAlignmentCompleteMap().get(innovationID + "");
      if (value != null && value.equals("1")) {
        this.setInnovationAllianceAlignmentComplete(false);
      } else {
        this.setInnovationAllianceAlignmentComplete(true);
      }

      value = BaseAction.getIsInnovationOneCgiarAlignmentCompleteMap().get(innovationID + "");
      if (value != null && value.equals("1")) {
        this.setInnovationOneCgiarAlignmentComplete(false);
      } else {
        this.setInnovationOneCgiarAlignmentComplete(true);
      }

      value = BaseAction.getIsInnovationOneCgiarAlignmentCompleteMap().get(innovationID + "");
      if (value != null && value.equals("1")) {
        this.setInnovationOneCgiarAlignmentComplete(false);
      } else {
        this.setInnovationOneCgiarAlignmentComplete(true);
      }

      value = BaseAction.getIsInnovationOneCgiarAlignmentCompleteMap().get(innovationID + "");
      if (value != null && value.equals("1")) {
        this.setInnovationOneCgiarAlignmentComplete(false);
      } else {
        this.setInnovationOneCgiarAlignmentComplete(true);
      }

      value = BaseAction.getIsInnovationRightsCompleteMap().get(innovationID + "");
      if (value != null && value.equals("1")) {
        this.setInnovationRightsComplete(false);
      } else {
        this.setInnovationRightsComplete(true);
      }

      ProjectInnovationAction.getIsSaving().remove(innovationID + "");
    }
  }
}