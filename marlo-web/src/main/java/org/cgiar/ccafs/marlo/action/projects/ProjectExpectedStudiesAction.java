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
import org.cgiar.ccafs.marlo.data.manager.AllianceLeverManager;
import org.cgiar.ccafs.marlo.data.manager.AllianceLeverOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.AllianceLeversSdgContributionManager;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.EvidenceTagManager;
import org.cgiar.ccafs.marlo.data.manager.ExpectedStudyProjectManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalTargetManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ImpactAreaManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyAllianceLeversOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCenterManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCrpOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyFlagshipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyLinkManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPartnerTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPartnershipsPersonManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPublicationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyQuantificationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyReferenceManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySdgAllianceLeverManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySrfTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyTagManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.QuantificationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyInvestimentTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndRegionManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageProcessManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageStudyManager;
import org.cgiar.ccafs.marlo.data.manager.SDGContributionManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.StudyTypeManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.AllianceLever;
import org.cgiar.ccafs.marlo.data.model.AllianceLeverOutcome;
import org.cgiar.ccafs.marlo.data.model.AllianceLeversSdgContribution;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.EvidenceTag;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ImpactArea;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyAllianceLeversOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCenter;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrpOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGlobalTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLink;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnerType;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnership;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPartnershipsPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPublication;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyQuantification;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyReference;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySdgAllianceLever;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyTag;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationShared;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.QuantificationType;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyInvestimentType;
import org.cgiar.ccafs.marlo.data.model.RepIndStageProcess;
import org.cgiar.ccafs.marlo.data.model.RepIndStageStudy;
import org.cgiar.ccafs.marlo.data.model.SDGContribution;
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
import java.util.Collections;
import java.util.Comparator;
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
import org.hibernate.exception.LockAcquisitionException;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
public class ProjectExpectedStudiesAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 597647662288518417L;
  private static final long[] EMPTY_ARRAY = {};
  private final Logger logger = LoggerFactory.getLogger(ProjectExpectedStudiesAction.class);


  // Managers
  private final ProjectExpectedStudyManager projectExpectedStudyManager;

  private final AuditLogManager auditLogManager;

  private final GlobalUnitManager crpManager;
  private final ProjectManager projectManager;
  private final PhaseManager phaseManager;
  private final SrfSloIndicatorManager srfSloIndicatorManager;
  private final SrfSubIdoManager srfSubIdoManager;
  private final CrpProgramManager crpProgramManager;
  private final InstitutionManager institutionManager;
  private final LocElementManager locElementManager;
  private final StudyTypeManager studyTypeManager;
  private final RepIndGeographicScopeManager geographicScopeManager;
  private final RepIndRegionManager repIndRegionManager;
  private final RepIndOrganizationTypeManager organizationTypeManager;
  private final RepIndGenderYouthFocusLevelManager focusLevelManager;
  private final RepIndPolicyInvestimentTypeManager investimentTypeManager;
  private final RepIndStageProcessManager stageProcessManager;
  private final RepIndStageStudyManager stageStudyManager;
  private final ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;
  private final ProjectExpectedStudySubIdoManager projectExpectedStudySubIdoManager;
  private final ProjectExpectedStudyFlagshipManager projectExpectedStudyFlagshipManager;
  private final ProjectExpectedStudyCrpManager projectExpectedStudyCrpManager;
  private final ProjectExpectedStudyInstitutionManager projectExpectedStudyInstitutionManager;
  private final ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager;
  private final ProjectExpectedStudySrfTargetManager projectExpectedStudySrfTargetManager;
  private final ExpectedStudyProjectManager expectedStudyProjectManager;
  private final ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager;
  private final ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager;
  private final GeneralStatusManager generalStatusManager;
  private final CrpMilestoneManager milestoneManager;
  private final ProjectOutcomeManager projectOutcomeManager;
  private final CrpProgramOutcomeManager crpProgramOutcomeManager;


  // AR 2018 Managers
  private final EvidenceTagManager evidenceTagManager;
  private final ProjectExpectedStudyLinkManager projectExpectedStudyLinkManager;
  private final ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager;
  private final ProjectExpectedStudyQuantificationManager projectExpectedStudyQuantificationManager;
  private final ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager;
  private final ProjectInnovationManager projectInnovationManager;
  private final ProjectPolicyManager projectPolicyManager;

  // AR 2019 Managers
  private final ProjectExpectedStudyCenterManager projectExpectedStudyCenterManager;
  private final ProjectExpectedStudyMilestoneManager projectExpectedStudyMilestoneManager;
  private final ProjectExpectedStudyProjectOutcomeManager projectExpectedStudyProjectOutcomeManager;
  private final ProjectExpectedStudyCrpOutcomeManager projectExpectedStudyCrpOutcomeManager;
  private final FeedbackQACommentManager feedbackQACommentManager;
  private final FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager;

  // AR 2022 Managers
  private final ProjectExpectedStudyReferenceManager projectExpectedStudyReferenceManager;
  private final ProjectExpectedStudyTagManager projectExpectedStudyTagManager;
  private final QuantificationTypeManager quantificationTypeManager;
  private final ProjectExpectedStudyPublicationManager projectExpectedStudyPublicationManager;
  private final AllianceLeverManager allianceLeverManager;

  private final ProjectExpectedStudySdgAllianceLeverManager projectExpectedStudySdgAllianceLeverManager;
  private final ProjectExpectedStudyAllianceLeversOutcomeManager projectExpectedStudyAllianceLeversOutcomeManager;
  private final ProjectPartnerManager projectPartnerManager;
  private final ProjectExpectedStudyPartnershipManager projectExpectedStudyPartnershipManager;
  private final ProjectExpectedStudyPartnerTypeManager projectExpectedStudyPartnerTypeManager;
  private final ProjectExpectedStudyPartnershipsPersonManager projectExpectedStudyPartnershipsPersonManager;
  private final UserManager userManager;
  private final SDGContributionManager sDGContributionManager;
  private final AllianceLeverOutcomeManager allianceLeverOutcomeManager;
  private final AllianceLeversSdgContributionManager allianceLeversSdgContributionManager;
  private final ImpactAreaManager impactAreaManager;
  private final GlobalTargetManager globalTargetManager;


  // Variables
  private final ProjectExpectedStudiesValidator projectExpectedStudiesValidator;
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
  private List<FeedbackQACommentableFields> feedbackComments;
  private String transaction;
  private String tag;
  private int previousYear;
  private int previousMaturityID;
  private int previousTagID;

  // AR 2018 Sel-List
  private List<EvidenceTag> tags;
  private List<ProjectPolicy> policyList;
  private List<ProjectInnovation> innovationsList;
  // AR 2019 Sel-List
  private List<Institution> centers;
  private List<CrpMilestone> milestones;
  private int newExpectedYear;
  private List<ProjectOutcome> projectOutcomes;
  private List<CrpProgramOutcome> crpOutcomes;
  private List<ProjectExpectedStudyTag> tagList;
  private List<QuantificationType> quantificationTypes;

  private List<AllianceLever> allianceLeverList;

  private List<ProjectPartner> partners;


  private List<ProjectPartnerPerson> partnerPersons;


  private List<Institution> partnerInstitutions;
  private Boolean isManagingPartnerPersonRequerid;
  private List<ImpactArea> impactAreasList;

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
    ProjectExpectedStudyMilestoneManager projectExpectedStudyMilestoneManager,
    ProjectOutcomeManager projectOutcomeManager,
    ProjectExpectedStudyProjectOutcomeManager projectExpectedStudyProjectOutcomeManager,
    FeedbackQACommentManager feedbackQACommentManager,
    FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager,
    ProjectExpectedStudyReferenceManager projectExpectedStudyReferenceManager,
    ProjectExpectedStudyCrpOutcomeManager projectExpectedStudyCrpOutcomeManager,
    ProjectExpectedStudyTagManager projectExpectedStudyTagManager, QuantificationTypeManager quantificationTypeManager,
    ProjectExpectedStudyPublicationManager projectExpectedStudyPublicationManager,
    AllianceLeverManager allianceLeverManager,
    ProjectExpectedStudySdgAllianceLeverManager projectExpectedStudySdgAllianceLeverManager,
    ProjectExpectedStudyAllianceLeversOutcomeManager projectExpectedStudyAllianceLeversOutcomeManager,
    ProjectPartnerManager projectPartnerManager,
    ProjectExpectedStudyPartnershipManager projectExpectedStudyPartnershipManager,
    ProjectExpectedStudyPartnerTypeManager projectExpectedStudyPartnerTypeManager,
    ProjectExpectedStudyPartnershipsPersonManager projectExpectedStudyPartnershipsPersonManager,
    UserManager userManager, SDGContributionManager sDGContributionManager,
    AllianceLeverOutcomeManager allianceLeverOutcomeManager,
    AllianceLeversSdgContributionManager allianceLeversSdgContributionManager, ImpactAreaManager impactAreaManager,
    GlobalTargetManager globalTargetManager) {
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
    this.projectOutcomeManager = projectOutcomeManager;
    this.projectExpectedStudyProjectOutcomeManager = projectExpectedStudyProjectOutcomeManager;
    this.feedbackQACommentManager = feedbackQACommentManager;
    this.feedbackQACommentableFieldsManager = feedbackQACommentableFieldsManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.projectExpectedStudyCrpOutcomeManager = projectExpectedStudyCrpOutcomeManager;
    this.projectExpectedStudyReferenceManager = projectExpectedStudyReferenceManager;
    this.projectExpectedStudyTagManager = projectExpectedStudyTagManager;
    this.quantificationTypeManager = quantificationTypeManager;
    this.projectExpectedStudyPublicationManager = projectExpectedStudyPublicationManager;
    this.allianceLeverManager = allianceLeverManager;
    this.projectExpectedStudySdgAllianceLeverManager = projectExpectedStudySdgAllianceLeverManager;
    this.projectExpectedStudyAllianceLeversOutcomeManager = projectExpectedStudyAllianceLeversOutcomeManager;
    this.projectPartnerManager = projectPartnerManager;
    this.projectExpectedStudyPartnershipManager = projectExpectedStudyPartnershipManager;
    this.projectExpectedStudyPartnerTypeManager = projectExpectedStudyPartnerTypeManager;
    this.projectExpectedStudyPartnershipsPersonManager = projectExpectedStudyPartnershipsPersonManager;
    this.userManager = userManager;
    this.sDGContributionManager = sDGContributionManager;
    this.allianceLeverOutcomeManager = allianceLeverOutcomeManager;
    this.allianceLeversSdgContributionManager = allianceLeversSdgContributionManager;
    this.impactAreaManager = impactAreaManager;
    this.globalTargetManager = globalTargetManager;
  }

  /**
   * Delete all LocElements Records when Geographic Scope is Global or NULL
   * 
   * @param policy
   * @param phase
   */
  public void deleteLocElements(ProjectExpectedStudy study, Phase phase, boolean isCountry) {
    if (isCountry) {
      if ((this.expectedStudy.getProjectExpectedStudyCountries() != null)
        && (this.expectedStudy.getProjectExpectedStudyCountries().size() > 0)) {

        final List<ProjectExpectedStudyCountry> regionPrev =
          new ArrayList<>(this.expectedStudy.getProjectExpectedStudyCountries().stream()
            .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (final ProjectExpectedStudyCountry region : regionPrev) {
          this.projectExpectedStudyCountryManager.deleteProjectExpectedStudyCountry(region.getId());
        }
      }
    } else {
      if ((this.expectedStudy.getProjectExpectedStudyRegions() != null)
        && (this.expectedStudy.getProjectExpectedStudyRegions().size() > 0)) {

        final List<ProjectExpectedStudyRegion> regionPrev =
          new ArrayList<>(this.expectedStudy.getProjectExpectedStudyRegions().stream()
            .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (final ProjectExpectedStudyRegion policyRegion : regionPrev) {

          this.projectExpectedStudyRegionManager.deleteProjectExpectedStudyRegion(policyRegion.getId());

        }

      }
    }
  }


  public void fillAllianceLevers() {
    try {
      if (this.expectedStudy.getAllianceLever() != null) {
        List<SDGContribution> sDGContributionList = new ArrayList<>();
        sDGContributionList = this.sDGContributionManager.findSDGcontributionByExpectedPhaseAndLever(
          this.getActualPhase().getId(), this.expectedStudy.getId(), this.expectedStudy.getAllianceLever().getId(), 1);
        this.expectedStudy.getAllianceLever().setSdgContributions(sDGContributionList);

        List<AllianceLeverOutcome> allianceLeverOutcomeList = new ArrayList<>();
        allianceLeverOutcomeList = this.allianceLeverOutcomeManager.findAllianceLeverOutcomeByExpectedPhaseAndLever(
          this.getActualPhase().getId(), this.expectedStudy.getId(), this.expectedStudy.getAllianceLever().getId());
        this.expectedStudy.getAllianceLever().setLeverOutcomes(allianceLeverOutcomeList);
      }


      if ((this.expectedStudy.getAllianceLevers() != null) && !this.expectedStudy.getAllianceLevers().isEmpty()) {
        for (final AllianceLever allianLever : this.expectedStudy.getAllianceLevers()) {
          List<SDGContribution> sDGContributionList = new ArrayList<>();
          sDGContributionList = this.sDGContributionManager.findSDGcontributionByExpectedPhaseAndLever(
            this.getActualPhase().getId(), this.expectedStudy.getId(), allianLever.getId(), 0);
          allianLever.setSdgContributions(sDGContributionList);
        }
      }

      for (SDGContribution alo : this.expectedStudy.getAllianceLever().getSdgContributions()) {

      }
    } catch (final Exception e) {
      Log.error(" error in fillallianceLevers function " + e.getMessage());
    }

  }


  public List<AllianceLever> getAllianceLeverList() {
    return allianceLeverList;
  }


  private Path getAutoSaveFilePath() {
    final String composedClassName = this.expectedStudy.getClass().getSimpleName();
    // get the action name and replace / for _
    final String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    final String autoSaveFile = this.expectedStudy.getId() + "_" + composedClassName + "_"
      + this.getActualPhase().getName() + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(this.config.getAutoSaveFolder() + autoSaveFile);
  }


  public List<Institution> getCenters() {
    return this.centers;
  }


  public List<LocElement> getCountries() {
    return this.countries;
  }


  public long getCrpMilestonePrimary() {
    return this.crpMilestonePrimary;
  }


  public List<CrpProgramOutcome> getCrpOutcomes() {
    return this.crpOutcomes;
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


  public List<FeedbackQACommentableFields> getFeedbackComments() {
    return this.feedbackComments;
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


  public List<ImpactArea> getImpactAreasList() {
    return impactAreasList;
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
    return this.milestonePrimaryId;
  }

  public List<CrpMilestone> getMilestones() {
    return this.milestones;
  }

  public List<Project> getMyProjects() {
    return this.myProjects;
  }

  public int getNewExpectedYear() {
    return this.newExpectedYear;
  }

  public List<RepIndOrganizationType> getOrganizationTypes() {
    return this.organizationTypes;
  }

  public List<ProjectPartnerPerson> getPartnerPersons() {
    return this.partnerPersons;
  }

  public List<ProjectPartner> getPartners() {
    return this.partners;
  }

  public String getPath() {
    return this.config.getDownloadURL() + "/" + this.getStudiesSourceFolder().replace('\\', '/');
  }

  /**
   * @return an array of integers.
   */
  public long[] getPersonsIds(ProjectExpectedStudyPartnership projectExpectedStudyPartnership) {
    if (projectExpectedStudyPartnership != null) {
      final List<ProjectExpectedStudyPartnershipsPerson> pPersons =
        projectExpectedStudyPartnership.getPartnershipPersons().stream()
          .filter(pp -> (pp.getUser() != null) && (pp.getUser().getId() != null) && (pp.getUser().getId() > 0))
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

  public List<ProjectOutcome> getProjectOutcomes() {
    return this.projectOutcomes;
  }


  public List<QuantificationType> getQuantificationTypes() {
    return this.quantificationTypes;
  }

  public List<CrpProgram> getRegionList() {
    return this.regionList;
  }

  public List<LocElement> getRegions() {
    return this.regions;
  }

  public long getSrfSubIdoPrimary() {
    return this.srfSubIdoPrimary;
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
    return this.subIdoPrimaryId;
  }

  public List<SrfSubIdo> getSubIdos() {
    return this.subIdos;
  }

  public String getTag() {
    return this.tag;
  }

  public List<ProjectExpectedStudyTag> getTagList() {
    return this.tagList;
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

  @Override
  public void prepare() throws Exception {


    this.loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    this.loggedCrp = this.crpManager.getGlobalUnitById(this.loggedCrp.getId());
    this.setPhaseID(this.getActualPhase().getId());

    this.expectedID =
      Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.EXPECTED_REQUEST_ID)));

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      this.transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      final ProjectExpectedStudy history = (ProjectExpectedStudy) this.auditLogManager.getHistory(this.transaction);

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
        if ((this.expectedStudy.getProjectExpectedStudyInfo().getStudyType() != null)
          && (this.expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId() != null)) {
          this.expectedStudy.getProjectExpectedStudyInfo().setStudyType(this.studyTypeManager
            .getStudyTypeById(this.expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId()));
        }

        // Load OrganizationType
        if ((this.expectedStudy.getProjectExpectedStudyInfo().getRepIndOrganizationType() != null)
          && (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndOrganizationType().getId() != null)) {
          this.expectedStudy.getProjectExpectedStudyInfo()
            .setRepIndOrganizationType(this.organizationTypeManager.getRepIndOrganizationTypeById(
              this.expectedStudy.getProjectExpectedStudyInfo().getRepIndOrganizationType().getId()));
        }

        // Load OrganizationType
        if ((this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageProcess() != null)
          && (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageProcess().getId() != null)) {
          this.expectedStudy.getProjectExpectedStudyInfo()
            .setRepIndStageProcess(this.stageProcessManager.getRepIndStageProcessById(
              this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageProcess().getId()));
        }

        // Load StageStudy
        if ((this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy() != null)
          && (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId() != null)) {
          this.expectedStudy.getProjectExpectedStudyInfo().setRepIndStageStudy(this.stageStudyManager
            .getRepIndStageStudyById(this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId()));
        }

        /*
         * Set RepIndStageStudy composed name
         */
        if (this.isAFPhase(this.getActualPhase().getId())) {
          if ((this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getName() != null)
            && (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getDescriptionAF() != null)) {

            this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy()
              .setComposedName("<b>" + this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getName()
                + "</b>" + this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getDescriptionAF());
          }
        } else {
          if ((this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getName() != null)
            && (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getDescription() != null)) {
            this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy()
              .setComposedName("<b>" + this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getName()
                + "</b>" + this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getDescription());
          }
        }

        // Load Status
        if ((this.expectedStudy.getProjectExpectedStudyInfo().getStatus() != null)
          && (this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId() != null)) {
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
        if ((this.expectedStudy.getProjectExpectedStudyInfo().getStatus() != null)
          && (this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId() != null)
          && (this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId() == 4)
          && (this.expectedStudy.getProjectExpectedStudyInfo().getYear() > 0)) {
          this.newExpectedYear = this.expectedStudy.getProjectExpectedStudyInfo().getYear();
        }

      }
    } else {
      this.expectedStudy = this.projectExpectedStudyManager.getProjectExpectedStudyById(this.expectedID);
    }

    if (this.expectedStudy != null) {

      final Phase phase = this.phaseManager.getPhaseById(this.getActualPhase().getId());

      final Path path = null;//
      this.getAutoSaveFilePath();


      if ((path != null) && path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        final Gson gson = new GsonBuilder().create();

        final JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();

        final AutoSaveReader autoSaveReader = new AutoSaveReader();
        this.expectedStudy = (ProjectExpectedStudy) autoSaveReader.readFromJson(jReader);

        // Policy Geographic Scope List AutoSave
        boolean haveRegions = false;
        boolean haveCountries = false;

        if ((this.expectedStudy != null) && (this.expectedStudy.getGeographicScopes() != null)
          && !this.expectedStudy.getGeographicScopes().isEmpty()
          && (this.expectedStudy.getGeographicScopes().size() > 0)) {
          for (final ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope : this.expectedStudy
            .getGeographicScopes()) {
            if (projectExpectedStudyGeographicScope.getRepIndGeographicScope() != null) {

              projectExpectedStudyGeographicScope.setRepIndGeographicScope(this.geographicScopeManager
                .getRepIndGeographicScopeById(projectExpectedStudyGeographicScope.getRepIndGeographicScope().getId()));

              if (projectExpectedStudyGeographicScope.getRepIndGeographicScope().getId() == 2) {
                haveRegions = true;
              }
              if ((projectExpectedStudyGeographicScope.getRepIndGeographicScope().getId() != 1)
                && (projectExpectedStudyGeographicScope.getRepIndGeographicScope().getId() != 2)) {
                haveCountries = true;
              }
            }
          }
        }

        if (haveRegions) {
          // Load Regions
          // Expected Study Geographic Regions List Autosave
          if (this.expectedStudy.getStudyRegions() != null) {
            for (final ProjectExpectedStudyRegion projectExpectedStudyRegion : this.expectedStudy.getStudyRegions()) {
              if ((projectExpectedStudyRegion != null) && (projectExpectedStudyRegion.getLocElement() != null)
                && (projectExpectedStudyRegion.getLocElement().getId() != null)) {
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
            final String[] countriesText =
              this.expectedStudy.getCountriesIdsText().replace("[", "").replace("]", "").split(",");
            final List<String> countries = new ArrayList<>();
            for (final String value : Arrays.asList(countriesText)) {
              countries.add(value.trim());
            }
            this.expectedStudy.setCountriesIds(countries);
          }
        }

        // Expected Study SubIdo List Autosave
        if ((this.expectedStudy.getSubIdos() != null) && !this.expectedStudy.getSubIdos().isEmpty()
          && (this.expectedStudy.getSubIdos().size() > 0)) {
          for (final ProjectExpectedStudySubIdo projectExpectedStudySubIdo : this.expectedStudy.getSubIdos()) {
            if ((projectExpectedStudySubIdo != null) && (projectExpectedStudySubIdo.getSrfSubIdo() != null)
              && (projectExpectedStudySubIdo.getSrfSubIdo().getId() != null)) {
              projectExpectedStudySubIdo.setSrfSubIdo(
                this.srfSubIdoManager.getSrfSubIdoById(projectExpectedStudySubIdo.getSrfSubIdo().getId()));
            }
          }
        }

        // Expected Study Flagship List Autosave
        if ((this.expectedStudy.getFlagships() != null) && !this.expectedStudy.getFlagships().isEmpty()) {
          for (final ProjectExpectedStudyFlagship projectExpectedStudyFlagship : this.expectedStudy.getFlagships()) {
            if ((projectExpectedStudyFlagship != null) && (projectExpectedStudyFlagship.getCrpProgram() != null)
              && (projectExpectedStudyFlagship.getCrpProgram().getId() != null)) {
              projectExpectedStudyFlagship.setCrpProgram(
                this.crpProgramManager.getCrpProgramById(projectExpectedStudyFlagship.getCrpProgram().getId()));
            }
          }
        }

        // Expected Study Regions (Flagships) List Autosave
        if ((this.expectedStudy.getRegions() != null) && !this.expectedStudy.getRegions().isEmpty()) {
          for (final ProjectExpectedStudyFlagship projectExpectedStudyFlagship : this.expectedStudy.getRegions()) {
            if ((projectExpectedStudyFlagship != null) && (projectExpectedStudyFlagship.getCrpProgram() != null)
              && (projectExpectedStudyFlagship.getCrpProgram().getId() != null)) {
              projectExpectedStudyFlagship.setCrpProgram(
                this.crpProgramManager.getCrpProgramById(projectExpectedStudyFlagship.getCrpProgram().getId()));
            }
          }
        }

        // Expected Study Crp List Autosave
        if ((this.expectedStudy.getCrps() != null) && !this.expectedStudy.getCrps().isEmpty()) {
          for (final ProjectExpectedStudyCrp projectExpectedStudyCrp : this.expectedStudy.getCrps()) {
            if ((projectExpectedStudyCrp != null) && (projectExpectedStudyCrp.getGlobalUnit() != null)
              && (projectExpectedStudyCrp.getGlobalUnit().getId() != null)) {
              projectExpectedStudyCrp
                .setGlobalUnit(this.crpManager.getGlobalUnitById(projectExpectedStudyCrp.getGlobalUnit().getId()));
            }
          }
        }

        // Expected Study Center List Autosave
        if ((this.expectedStudy.getCenters() != null) && !this.expectedStudy.getCenters().isEmpty()) {
          for (final ProjectExpectedStudyCenter projectExpectedStudyCenter : this.expectedStudy.getCenters()) {
            if ((projectExpectedStudyCenter != null) && (projectExpectedStudyCenter.getInstitution() != null)
              && (projectExpectedStudyCenter.getInstitution().getId() != null)) {
              projectExpectedStudyCenter.setInstitution(
                this.institutionManager.getInstitutionById(projectExpectedStudyCenter.getInstitution().getId()));
            }
          }
        }

        // Innovation Milestone List Autosave

        if (this.expectedStudy.getMilestones() != null) {
          for (final ProjectExpectedStudyMilestone projectExpectedStudyMilestone : this.expectedStudy.getMilestones()) {
            if ((projectExpectedStudyMilestone != null) && (projectExpectedStudyMilestone.getCrpMilestone() != null)
              && (projectExpectedStudyMilestone.getCrpMilestone().getId() != null)) {
              projectExpectedStudyMilestone.setCrpMilestone(
                (this.milestoneManager.getCrpMilestoneById(projectExpectedStudyMilestone.getCrpMilestone().getId())));
            }
          }
        }

        // Expected Study Institutions List Autosave
        if (this.expectedStudy.getInstitutions() != null) {
          for (final ProjectExpectedStudyInstitution projectExpectedStudyInstitution : this.expectedStudy
            .getInstitutions()) {
            if ((projectExpectedStudyInstitution != null) && (projectExpectedStudyInstitution.getInstitution() != null)
              && (projectExpectedStudyInstitution.getInstitution().getId() != null)) {
              projectExpectedStudyInstitution.setInstitution(
                this.institutionManager.getInstitutionById(projectExpectedStudyInstitution.getInstitution().getId()));
            }
          }
        }

        // Expected Study Srf Target List Autosave
        if (this.expectedStudy.getSrfTargets() != null) {
          for (final ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget : this.expectedStudy.getSrfTargets()) {
            if ((projectExpectedStudySrfTarget != null) && (projectExpectedStudySrfTarget.getSrfSloIndicator() != null)
              && (projectExpectedStudySrfTarget.getSrfSloIndicator().getId() != null)) {
              projectExpectedStudySrfTarget.setSrfSloIndicator(this.srfSloIndicatorManager
                .getSrfSloIndicatorById(projectExpectedStudySrfTarget.getSrfSloIndicator().getId()));
            }
          }
        }

        // Expected Study Projects List Autosave
        if (this.expectedStudy.getProjects() != null) {
          for (final ExpectedStudyProject expectedStudyProject : this.expectedStudy.getProjects()) {
            if ((expectedStudyProject != null) && (expectedStudyProject.getProject() != null)
              && (expectedStudyProject.getProject().getId() != null)) {
              expectedStudyProject
                .setProject(this.projectManager.getProjectById(expectedStudyProject.getProject().getId()));
            }
          }
        }

        // Expected Study Innovations List Autosave
        if (this.expectedStudy.getInnovations() != null) {
          for (final ProjectExpectedStudyInnovation projectExpectedStudyInnovation : this.expectedStudy
            .getInnovations()) {
            if ((projectExpectedStudyInnovation != null)
              && (projectExpectedStudyInnovation.getProjectInnovation() != null)
              && (projectExpectedStudyInnovation.getProjectInnovation().getId() != null)) {
              projectExpectedStudyInnovation.setProjectInnovation(this.projectInnovationManager
                .getProjectInnovationById(projectExpectedStudyInnovation.getProjectInnovation().getId()));
            }
          }
        }

        // Expected Study Policies List Autosave
        if (this.expectedStudy.getPolicies() != null) {
          for (final ProjectExpectedStudyPolicy projectExpectedStudyPolicy : this.expectedStudy.getPolicies()) {
            if ((projectExpectedStudyPolicy != null) && (projectExpectedStudyPolicy.getProjectPolicy() != null)
              && (projectExpectedStudyPolicy.getProjectPolicy().getId() != null)) {
              projectExpectedStudyPolicy.setProjectPolicy(
                this.projectPolicyManager.getProjectPolicyById(projectExpectedStudyPolicy.getProjectPolicy().getId()));
            }
          }
        }

        // Study Type Autosave
        if (this.expectedStudy.getProjectExpectedStudyInfo().getStudyType() != null) {
          if ((this.expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId() != null)
            && (this.expectedStudy.getProjectExpectedStudyInfo().getStudyType().getId() != -1)) {
            final StudyType studyType = this.studyTypeManager
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
          final List<ProjectExpectedStudyCountry> countries = this.projectExpectedStudyCountryManager
            .getProjectExpectedStudyCountrybyPhase(this.expectedStudy.getId(), phase.getId()).stream()
            .filter(le -> le.isActive() && (le.getLocElement().getLocElementType().getId() == 2))
            .collect(Collectors.toList());
          this.expectedStudy.setCountries(countries);
        }

        if (this.expectedStudy.getProjectExpectedStudyRegions() == null) {
          this.expectedStudy.setStudyRegions(new ArrayList<>());
        } else {
          final List<ProjectExpectedStudyRegion> geographics = this.projectExpectedStudyRegionManager
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
          if (this.expectedStudy.getSubIdos(phase) != null) {
            List<ProjectExpectedStudySubIdo> projectPolicies = new ArrayList<ProjectExpectedStudySubIdo>();

            projectPolicies = this.expectedStudy.getSubIdos(phase).stream()
              .filter(p -> (p != null) && p.isActive() && (p.getPrimary() != null) && p.getPrimary())
              .collect(Collectors.toList());

            if ((projectPolicies != null) && (projectPolicies.size() > 0) && (projectPolicies.get(0) != null)) {
              this.subIdoPrimaryId = projectPolicies.get(0).getSrfSubIdo().getId(); //
              this.srfSubIdoPrimary = projectPolicies.get(0).getSrfSubIdo().getId(); //
            }
          }

        }

        // Expected Study Flagship List
        if (this.expectedStudy.getProjectExpectedStudyFlagships() != null) {
          this.expectedStudy.setFlagships(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyFlagships().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())
              && (o.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()))
            .collect(Collectors.toList())));
        }

        // Expected Study Regions List
        if (this.expectedStudy.getProjectExpectedStudyFlagships() != null) {
          this.expectedStudy.setRegions(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyFlagships().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())
              && (o.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()))
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

        // Expected Study Project Outcome list
        if (this.expectedStudy.getProjectExpectedStudyProjectOutcomes() != null) {
          this.expectedStudy
            .setProjectOutcomes(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyProjectOutcomes().stream()
              .filter(o -> o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
        }

        // Expected Study Crp Outcome list
        if (this.expectedStudy.getProjectExpectedStudyCrpOutcomes() != null) {
          this.expectedStudy.setCrpOutcomes(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyCrpOutcomes()
            .stream().filter(o -> o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
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

        // Expected Study Reference List
        if (this.expectedStudy.getProjectExpectedStudyReferences() != null) {
          this.expectedStudy
            .setReferences(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyReferences().stream()
              .filter(
                o -> (o != null) && (o.getId() != null) && o.isActive() && o.getPhase().getId().equals(phase.getId()))
              .sorted((o1, o2) -> Comparator.comparing(ProjectExpectedStudyReference::getId).compare(o1, o2))
              .collect(Collectors.toList())));
        }

        // Expected Study Innovations List
        if (this.expectedStudy.getProjectExpectedStudyInnovations() != null) {
          this.expectedStudy
            .setInnovations(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyInnovations().stream()
              .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList())));
          // Get the ID of the principal Sub IDO if exist
          if (this.expectedStudy.getMilestones() != null) {
            List<ProjectExpectedStudyMilestone> projectPolicies = new ArrayList<ProjectExpectedStudyMilestone>();

            projectPolicies = this.expectedStudy
              .getMilestones().stream().filter(p -> (p != null) && p.isActive() && (p.getPrimary() != null)
                && p.getPrimary() && (p.getPhase() != null) && p.getPhase().getId().equals(phase.getId()))
              .collect(Collectors.toList());

            if ((projectPolicies != null) && !projectPolicies.isEmpty() && (projectPolicies.get(0) != null)) {
              this.milestonePrimaryId = projectPolicies.get(0).getCrpMilestone().getId(); //
              this.crpMilestonePrimary = projectPolicies.get(0).getCrpMilestone().getId(); //
            }
          }
        }


        // Expected Study Publications List
        if (this.expectedStudy.getProjectExpectedStudyPublications() != null) {
          this.expectedStudy
            .setPublications(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyPublications().stream()
              .filter(
                o -> (o != null) && (o.getId() != null) && o.isActive() && o.getPhase().getId().equals(phase.getId()))
              .collect(Collectors.toList())));
        }

        // Expected Study SdgAllianceLever List
        if (this.expectedStudy.getProjectExpectedStudySdgAllianceLevers() != null) {
          this.expectedStudy.setSdgAllianceLevers(
            (new ArrayList<>(this.expectedStudy.getProjectExpectedStudySdgAllianceLevers().stream()
              .filter(
                o -> (o != null) && (o.getId() != null) && o.isActive() && o.getPhase().getId().equals(phase.getId()))
              .collect(Collectors.toList()))));

          // Set Alliance lever to study object
          /*
           * if (this.expectedStudy.getSdgAllianceLevers() != null
           * && this.expectedStudy.getSdgAllianceLevers().get(0) != null
           * && this.expectedStudy.getSdgAllianceLevers().get(0).getAllianceLever() != null) {
           * this.expectedStudy.setAllianceLever(this.expectedStudy.getSdgAllianceLevers().get(0).getAllianceLever());
           * }
           */

          this.expectedStudy.setAllianceLevers(new ArrayList<>());
          for (final ProjectExpectedStudySdgAllianceLever projectExpectedStudySdgAllianceLeverTmp : this.expectedStudy
            .getSdgAllianceLevers()) {
            if (projectExpectedStudySdgAllianceLeverTmp.getAllianceLever() != null) {
              if ((projectExpectedStudySdgAllianceLeverTmp.getIsPrimary() != null)
                && projectExpectedStudySdgAllianceLeverTmp.getIsPrimary()) {
                this.expectedStudy.setAllianceLever(projectExpectedStudySdgAllianceLeverTmp.getAllianceLever());
                this.expectedStudy.getAllianceLever()
                  .setLeverComments(projectExpectedStudySdgAllianceLeverTmp.getLeverComments());
              } else {
                if (!this.expectedStudy.getAllianceLevers()
                  .contains(projectExpectedStudySdgAllianceLeverTmp.getAllianceLever())) {
                  AllianceLever leverTmp = projectExpectedStudySdgAllianceLeverTmp.getAllianceLever();
                  leverTmp.setLeverComments(projectExpectedStudySdgAllianceLeverTmp.getLeverComments());
                  this.expectedStudy.getAllianceLevers().add(leverTmp);
                }
              }
            }
          }
        }


        // Expected Study allianceLeversOutcomes List
        if (this.expectedStudy.getProjectExpectedStudyAllianceLeversOutcomes() != null) {
          this.expectedStudy.setAllianceLeversOutcomes(
            (new ArrayList<>(this.expectedStudy.getProjectExpectedStudyAllianceLeversOutcomes().stream()
              .filter(
                o -> (o != null) && (o.getId() != null) && o.isActive() && o.getPhase().getId().equals(phase.getId()))
              .collect(Collectors.toList()))));
          if (this.expectedStudy.getAllianceLever() == null) {
            for (final ProjectExpectedStudyAllianceLeversOutcome projectExpectedStudyAllianceLeversOutcomesTmp : this.expectedStudy
              .getAllianceLeversOutcomes()) {
              this.expectedStudy.setAllianceLever(projectExpectedStudyAllianceLeversOutcomesTmp.getAllianceLever());
            }
          }
        }

        if (this.expectedStudy.getAllianceLever() != null) {

          // Fill alliance lever object with sdg contributions saved elements
          if (this.expectedStudy.getAllianceLever().getLeverSdgContributions() != null) {
            for (final AllianceLeversSdgContribution leverSdgContribution : this.expectedStudy.getAllianceLever()
              .getLeverSdgContributions()) {

              if ((leverSdgContribution != null) && (leverSdgContribution.getsDGContribution() != null)) {
                this.expectedStudy.getAllianceLever().getSdgContributions()
                  .add(leverSdgContribution.getsDGContribution());
              }
            }
          }

          // Fill alliance lever list with sdg contributions saved elements
          if (this.expectedStudy.getAllianceLevers() != null) {
            for (final AllianceLever allianceLeverObject : this.expectedStudy.getAllianceLevers()) {

              if ((allianceLeverObject != null) && (allianceLeverObject.getLeverSdgContributions() != null)) {
                for (final AllianceLeversSdgContribution leverSdgContribution : allianceLeverObject
                  .getLeverSdgContributions()) {
                  if ((leverSdgContribution != null) && (leverSdgContribution.getsDGContribution() != null)) {
                    allianceLeverObject.getSdgContributions().add(leverSdgContribution.getsDGContribution());
                  }
                }
              }
            }
          }

          if (this.expectedStudy.getAllianceLeversOutcomes() != null) {
            for (final ProjectExpectedStudyAllianceLeversOutcome allianceLeverOutcome : this.expectedStudy
              .getAllianceLeversOutcomes()) {
              if ((allianceLeverOutcome != null) && (allianceLeverOutcome.getAllianceLeverOutcome() != null)) {
                this.expectedStudy.getAllianceLever().setLeverOutcomes(new ArrayList<>());
                this.expectedStudy.getAllianceLever().getLeverOutcomes()
                  .add(allianceLeverOutcome.getAllianceLeverOutcome());
              }
            }
          }
        }

        // this function set the sdg and outocmes related to the levers
        this.fillAllianceLevers();


        // Expected Study projectExpectedStudyPartnerships List
        if (this.expectedStudy.getProjectExpectedStudyPartnerships() != null) {

          final List<ProjectExpectedStudyPartnership> deList =
            this.expectedStudy.getProjectExpectedStudyPartnerships().stream()
              .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId())
                && dp.getProjectExpectedStudyPartnerType().getId()
                  .equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
              .collect(Collectors.toList());

          if ((deList != null) && !deList.isEmpty()) {
            try {
              Collections.sort(deList, (p1, p2) -> p1.getInstitution().getId().compareTo(p2.getInstitution().getId()));
            } catch (final Exception e) {
              this.logger.error("unable to sort dlist", e);
            }
            this.expectedStudy.setPartnerships(new ArrayList<>());
            for (final ProjectExpectedStudyPartnership projectExpectedStudyPartnership : deList) {

              if (projectExpectedStudyPartnership.getProjectExpectedStudyPartnershipsPersons() != null) {
                final List<ProjectExpectedStudyPartnershipsPerson> partnershipPersons =
                  new ArrayList<>(projectExpectedStudyPartnership.getProjectExpectedStudyPartnershipsPersons().stream()
                    .filter(ProjectExpectedStudyPartnershipsPerson::isActive).collect(Collectors.toList()));
                projectExpectedStudyPartnership.setPartnershipPersons(partnershipPersons);
              }
              this.expectedStudy.getPartnerships().add(projectExpectedStudyPartnership);
            }

          }
        }

        // Expected Study impact areas
        if (this.expectedStudy.getProjectExpectedStudyImpactAreas() != null) {
          this.expectedStudy
            .setImpactAreas(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyImpactAreas().stream()
              .filter(
                o -> (o != null) && (o.getId() != null) && o.isActive() && o.getPhase().getId().equals(phase.getId()))
              .collect(Collectors.toList())));
          if (this.expectedStudy.getImpactAreas() != null && !this.expectedStudy.getImpactAreas().isEmpty()) {
            this.expectedStudy.setImpactArea(this.expectedStudy.getImpactAreas().get(0).getImpactArea());
          }
        }

        // Expected Study global target
        if (this.expectedStudy.getProjectExpectedStudyGlobalTargets() != null) {
          this.expectedStudy
            .setGlobalTargets(new ArrayList<>(this.expectedStudy.getProjectExpectedStudyGlobalTargets().stream()
              .filter(
                o -> (o != null) && (o.getId() != null) && o.isActive() && o.getPhase().getId().equals(phase.getId()))
              .collect(Collectors.toList())));
          if (this.expectedStudy.getImpactAreas() != null && !this.expectedStudy.getImpactAreas().isEmpty()) {
            this.expectedStudy.getImpactArea().setGlobalTargets(new ArrayList<>());
            for (ProjectExpectedStudyGlobalTarget projectExpectedStudyGlobalTargetTmp : this.expectedStudy
              .getGlobalTargets()) {
              this.expectedStudy.getImpactArea().getGlobalTargets()
                .add(projectExpectedStudyGlobalTargetTmp.getGlobalTarget());
            }
          }
        }


        this.partners = new ArrayList<>();
        this.partnerInstitutions = new ArrayList<>();
        this.isManagingPartnerPersonRequerid = this.hasSpecificities(APConstants.CRP_MANAGING_PARTNERS_CONTACT_PERSONS);


        final List<ProjectPartner> partnersTmp = this.projectPartnerManager
          .findAllByPhaseProject(this.expectedStudy.getProject().getId(), this.getActualPhase().getId());

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


      }


      if (!this.isDraft()) {
        if (this.expectedStudy.getCountries() != null) {
          for (final ProjectExpectedStudyCountry country : this.expectedStudy.getCountries()) {
            this.expectedStudy.getCountriesIds().add(country.getLocElement().getIsoAlpha2());
          }
        }
      }

      // Getting The list
      this.statuses = this.generalStatusManager.findByTable(APConstants.PROJECT_EXPECTED_STUDIES_TABLE);

      // cgamboa 17/04/2024 the query will be call once
      final List<LocElement> LocElementTemp = this.locElementManager.findAll();

      // this.locElementManager.findAll() is changed by LocElementTemp
      this.countries = LocElementTemp.stream()
        .filter(c -> (c.getLocElementType().getId().intValue() == 2) && c.isActive()).collect(Collectors.toList());


      this.geographicScopes = this.geographicScopeManager.findAll();

      // this.locElementManager.findAll() is changed by LocElementTemp
      this.regions = LocElementTemp.stream()
        .filter(c -> (c.getLocElementType().getId().intValue() == 1) && c.isActive() && (c.getIsoNumeric() != null))
        .collect(Collectors.toList());


      this.organizationTypes = this.organizationTypeManager.findAll();
      // Focus levels and Too early to tell was removed
      this.focusLevels = this.focusLevelManager.findAll().stream().collect(Collectors.toList());
      this.policyInvestimentTypes = this.investimentTypeManager.findAll();
      this.stageProcesses = this.stageProcessManager.findAll();
      this.stageStudies = this.stageStudyManager.findAll();
      this.studyTypes = this.studyTypeManager.findAll();
      if ((this.expectedStudy.getProjectExpectedStudyInfo() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getId() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getId().longValue() != 1)) {
        this.studyTypes.removeIf(st -> st.getId() == 1);
      }
      this.subIdos = this.srfSubIdoManager.findAll();
      this.targets = this.srfSloIndicatorManager.findAll();

      /*
       * Set RepIndStageStudy composed name
       */
      if ((this.stageStudies != null) && !this.stageStudies.isEmpty()) {
        for (final RepIndStageStudy stageStudy : this.stageStudies) {

          if (stageStudy != null) {
            if (this.isAFPhase(this.getActualPhase().getId())) {
              if ((stageStudy.getName() != null) && (stageStudy.getDescriptionAF() != null)) {
                stageStudy.setComposedName(stageStudy.getName() + ": " + stageStudy.getDescriptionAF());
              }
            } else {
              if ((stageStudy.getName() != null) && (stageStudy.getDescription() != null)) {
                stageStudy.setComposedName(stageStudy.getName() + ": " + stageStudy.getDescription());
              }
            }
          }
        }
      }

      // institutions
      Project projectTemp = null;
      if (this.expectedStudy.getProject() != null) {
        projectTemp = this.projectManager.getProjectById(this.expectedStudy.getProject().getId());
      }
      if (projectTemp == null) {
        // is a sumplementary evidence
        this.centers = this.institutionManager.findAll().stream()
          .filter(c -> c.isPPA(this.getActualPhase().getCrp().getId(), this.getActualPhase())
            || (c.getInstitutionType().getId().longValue() == APConstants.INSTITUTION_CGIAR_CENTER_TYPE))
          .collect(Collectors.toList());
      } else {
        final List<Institution> centersTemp = new ArrayList<Institution>();
        final List<ProjectPartner> projectPartnerList = projectTemp.getProjectPartners().stream()
          .filter(c -> (c != null) && c.isActive() && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList());
        for (final ProjectPartner projectPartner : projectPartnerList) {
          if ((projectPartner.getInstitution() != null) && (projectPartner.getInstitution().getId() != null)) {
            final Institution institution =
              this.institutionManager.getInstitutionById(projectPartner.getInstitution().getId());
            if ((institution != null) && (institution.isPPA(this.getActualPhase().getCrp().getId(),
              this.getActualPhase())
              || (institution.getInstitutionType().getId().longValue() == APConstants.INSTITUTION_CGIAR_CENTER_TYPE))) {
              centersTemp.add(institution);
            }
          }
        }
        this.centers = centersTemp;
      }

      this.tags = this.evidenceTagManager.findAll();
      this.innovationsList = new ArrayList<>();
      this.policyList = new ArrayList<>();
      this.tagList = this.projectExpectedStudyTagManager.findAll();
      this.impactAreasList = this.impactAreaManager.findAllCustom();
      for (ImpactArea impactArea : this.impactAreasList) {
        impactArea.setGlobalTargets(this.globalTargetManager.findAllByImpactArea(impactArea.getId()));
      }

      this.quantificationTypes = this.quantificationTypeManager.findAll();
      this.allianceLeverList = this.allianceLeverManager.findAll();


      // get otucomes alliance lever list
      if (this.allianceLeverList != null) {
        for (final AllianceLever allianceLeverTmp : this.allianceLeverList) {
          if (allianceLeverTmp.getAllianceLeverOutcomes() != null) {
            allianceLeverTmp.setOutcomes(new ArrayList<>(allianceLeverTmp.getAllianceLeverOutcomes().stream()
              .filter(o -> (o != null) && (o.getId() != null) && o.isActive()).collect(Collectors.toList())));
          }
        }
      }

      // get sdg alliance lever list
      if (this.allianceLeverList != null) {
        for (final AllianceLever allianceLeverTmp : this.allianceLeverList) {
          if (this.expectedStudy.getProjectExpectedStudySdgAllianceLevers() != null) {

            allianceLeverTmp.setLeverSdgContributions(
              this.allianceLeversSdgContributionManager.findAllByLeverId(allianceLeverTmp.getId()));
            allianceLeverTmp
              .setSdgContributions(this.sDGContributionManager.findSDGcontributionByLever(allianceLeverTmp.getId()));
          }
        }

      }


      // Expected Study Projects List
      if (this.expectedStudy.getExpectedStudyProjects() != null) {
        final List<ExpectedStudyProject> expectedStudyProjects =
          new ArrayList<>(this.expectedStudy.getExpectedStudyProjects().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (final ExpectedStudyProject expectedStudyProject : expectedStudyProjects) {

          final Project sharedProject = expectedStudyProject.getProject();

          final List<ProjectInnovation> sharedInnovations = sharedProject.getProjectInnovations().stream()
            .filter(ProjectInnovation::isActive).collect(Collectors.toList());
          if (sharedInnovations != null) {
            for (final ProjectInnovation projectInnovation : sharedInnovations) {
              if (projectInnovation.getProjectInnovationInfo(this.getActualPhase()) != null) {
                this.innovationsList.add(projectInnovation);
              }
            }
          }

          final List<ProjectPolicy> sharedPolicies =
            sharedProject.getProjectPolicies().stream().filter(ProjectPolicy::isActive).collect(Collectors.toList());
          for (final ProjectPolicy projectPolicy : sharedPolicies) {
            if (projectPolicy.getProjectPolicyInfo(this.getActualPhase()) != null) {
              this.policyList.add(projectPolicy);
            }
          }

        }
      }


      this.myProjects = new ArrayList<>();
      for (final ProjectPhase projectPhase : phase.getProjectPhases()) {
        if (projectPhase.getProject().getProjecInfoPhase(this.getActualPhase()) != null) {
          this.myProjects.add(projectPhase.getProject());
        }

        if (this.project != null) {
          this.myProjects.remove(this.project);
        }
      }

      if ((this.myProjects != null) && !this.myProjects.isEmpty()) {
        this.myProjects.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
      }

      this.crps = this.crpManager.findAll().stream()
        .filter(gu -> gu.isActive() && ((gu.getGlobalUnitType().getId() == 1) || (gu.getGlobalUnitType().getId() == 3)))
        .collect(Collectors.toList());


      List<ProjectExpectedStudyCrp> tempPcrp = null;
      // Update crp list - Delete the actual crp from the list except if this crp was

      if ((this.expectedStudy.getCrps() != null) && (this.expectedStudy.getCrps().stream()
        .filter(x -> (x != null) && x.getGlobalUnit().getId().equals(this.getCurrentGlobalUnit().getId())) != null)) {
        tempPcrp = this.expectedStudy.getCrps().stream()
          .filter(x -> (x != null) && x.getGlobalUnit().getId().equals(this.getCurrentGlobalUnit().getId()))
          .collect(Collectors.toList());
      }

      if ((tempPcrp != null) && (tempPcrp.size() == 0) && (this.getCurrentGlobalUnit() != null)) {
        this.crps.remove(this.getCurrentGlobalUnit());
      }


      this.flagshipList = this.crpProgramManager.findAll().stream()
        .filter(p -> p.isActive() && (p.getCrp() != null) && (p.getCrp().getId() == this.loggedCrp.getId())
          && (p.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()))
        .collect(Collectors.toList());

      this.regionList = this.crpProgramManager.findAll().stream()
        .filter(p -> p.isActive() && (p.getCrp() != null) && (p.getCrp().getId() == this.loggedCrp.getId())
          && (p.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()))
        .collect(Collectors.toList());


      this.institutions =
        this.institutionManager.findAll().stream().filter(Institution::isActive).collect(Collectors.toList());


      this.expectedStudyDB = this.projectExpectedStudyManager.getProjectExpectedStudyById(this.expectedID);

      if (this.expectedStudyDB.getProject() != null) {
        this.projectID = this.expectedStudyDB.getProject().getId();
        this.project = this.projectManager.getProjectById(this.projectID);
        this.project.getProjecInfoPhase(phase);
      }


      if (this.project != null) {
        final Project projectL = this.projectManager.getProjectById(this.projectID);

        // Get the innovations List
        // this.innovationsList = new ArrayList<>();

        /*
         * Get the milestone List
         */
        this.milestones = new ArrayList<>();
        this.projectOutcomes = new ArrayList<>();


        // Get outcomes list
        List<ProjectOutcome> projectOutcomesList = new ArrayList<>();
        projectOutcomesList = projectL.getProjectOutcomes().stream().filter(
          po -> po.isActive() && (po.getPhase() != null) && po.getPhase().getId().equals(this.getActualPhase().getId()))
          .collect(Collectors.toList());

        if (projectOutcomesList != null) {
          this.crpOutcomes = new ArrayList<>();

          for (final ProjectOutcome projectOutcome : projectOutcomesList) {
            projectOutcome.setMilestones(projectOutcome.getProjectMilestones().stream().filter(m -> (m != null)
              && m.isActive() && (m.getYear() != 0) && (m.getYear() <= this.getActualPhase().getYear()))
              .collect(Collectors.toList()));

            if (!this.crpOutcomes.contains(projectOutcome.getCrpProgramOutcome())) {
              this.crpOutcomes.add(projectOutcome.getCrpProgramOutcome());
            }

            if (projectOutcome.getMilestones() != null) {
              for (final ProjectMilestone projectMilestone : projectOutcome.getMilestones()) {
                if ((projectMilestone.getCrpMilestone() != null) && projectMilestone.getCrpMilestone().isActive()) {
                  this.milestones.add(projectMilestone.getCrpMilestone());
                }
              }
            }

            // Fill projectOutcomes List
            if ((projectOutcome.getCrpProgramOutcome() != null)
              && (projectOutcome.getCrpProgramOutcome().getComposedName() != null)) {
              projectOutcome.setComposedName(projectOutcome.getCrpProgramOutcome().getComposedName());
            } else {
              projectOutcome.setComposedName(projectOutcome.getId() + "");
            }
            this.projectOutcomes.add(projectOutcome);
          }
        }
        this.crpOutcomes.sort((k1, k2) -> k1.getId().compareTo(k2.getId()));

        final List<ProjectInnovation> innovations =
          projectL.getProjectInnovations().stream().filter(ProjectInnovation::isActive).collect(Collectors.toList());
        for (final ProjectInnovation projectInnovation : innovations) {
          if (projectInnovation.getProjectInnovationInfo(this.getActualPhase()) != null) {
            this.innovationsList.add(projectInnovation);
          }
        }

        final List<ProjectPolicy> policies =
          projectL.getProjectPolicies().stream().filter(ProjectPolicy::isActive).collect(Collectors.toList());
        for (final ProjectPolicy projectPolicy : policies) {
          if (projectPolicy.getProjectPolicyInfo(this.getActualPhase()) != null) {
            this.policyList.add(projectPolicy);
          }
        }

        /*
         * Update 4/25/2019 Adding Shared Project Innovation in the lists.
         */
        final List<ProjectInnovationShared> innovationShareds =
          new ArrayList<>(this.project.getProjectInnovationShareds().stream()
            .filter(px -> px.isActive() && (px.getPhase().getId() == this.getActualPhase().getId())
              && px.getProjectInnovation().isActive()
              && (px.getProjectInnovation().getProjectInnovationInfo(this.getActualPhase()) != null))
            .collect(Collectors.toList()));
        if ((innovationShareds != null) && (innovationShareds.size() > 0)) {
          for (final ProjectInnovationShared innovationShared : innovationShareds) {
            if (!this.innovationsList.contains(innovationShared.getProjectInnovation())) {
              this.innovationsList.add(innovationShared.getProjectInnovation());
            }
          }
        }
      } else {
        // get the milestones list
        final Set<String> milestonesTemp = new HashSet<>();
        // Get the innovations List
        this.innovationsList = new ArrayList<>();
        // Get the policies List
        this.policyList = new ArrayList<>();
        final int year = this.getActualPhase().getYear();

        if ((this.myProjects != null) && !this.myProjects.isEmpty()) {
          for (final Project projectL : this.myProjects) {
            final List<ProjectMilestone> projectMilestones =
              projectL.getProjectOutcomes().stream().filter(po -> (po != null) && (po.getId() != null) && po.isActive())
                .flatMap(po -> po.getProjectMilestones().stream())
                .filter(pi -> (pi != null) && (pi.getId() != null) && pi.isActive() && (pi.getCrpMilestone() != null)
                  && (pi.getCrpMilestone().getId() != null) && pi.getCrpMilestone().isActive()
                  && (pi.getCrpMilestone().getComposeID() != null) && (pi.getYear() != 0) && (pi.getYear() <= year))
                .distinct().collect(Collectors.toList());
            for (final ProjectMilestone projectMilestone : projectMilestones) {
              milestonesTemp.add(projectMilestone.getCrpMilestone().getComposeID());
            }

            final List<ProjectInnovation> innovations = projectL.getProjectInnovations().stream()
              .filter(ProjectInnovation::isActive).collect(Collectors.toList());
            for (final ProjectInnovation projectInnovation : innovations) {
              if (projectInnovation.getProjectInnovationInfo(this.getActualPhase()) != null) {
                this.innovationsList.add(projectInnovation);
              }
            }

            final List<ProjectPolicy> policies =
              projectL.getProjectPolicies().stream().filter(ProjectPolicy::isActive).collect(Collectors.toList());
            for (final ProjectPolicy projectPolicy : policies) {
              if (projectPolicy.getProjectPolicyInfo(this.getActualPhase()) != null) {
                this.policyList.add(projectPolicy);
              }
            }
          }
        }

        this.milestones = new ArrayList<>();
        for (final String milestoneComposedId : milestonesTemp) {
          final CrpMilestone milestone =
            this.milestoneManager.getCrpMilestoneByPhase(milestoneComposedId, this.getPhaseID());
          if (milestone != null) {
            this.milestones.add(milestone);
          }
        }
      }

      // Load new Expected Year
      if ((this.expectedStudy.getProjectExpectedStudyInfo().getStatus() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId() == 4)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getYear() > 0)) {
        this.newExpectedYear = this.expectedStudy.getProjectExpectedStudyInfo().getYear();
      }

      // Load OICR tag
      if ((this.expectedStudy.getProjectExpectedStudyInfo() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getTag() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getTag().getTagName() != null)) {
        this.tag = this.expectedStudy.getProjectExpectedStudyInfo(phase).getTag().getTagName();
      }

      // Set previous values
      this.previousYear = this.expectedStudy.getProjectExpectedStudyInfo().getYear().intValue();

      if ((this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId() != null)) {
        this.previousMaturityID =
          this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId().intValue();
      } else {
        this.previousMaturityID = 0;
      }

      if ((this.expectedStudy.getProjectExpectedStudyInfo().getTag() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getTag().getId() != null)) {
        this.previousTagID = this.expectedStudy.getProjectExpectedStudyInfo().getTag().getId().intValue();
      } else {
        this.previousTagID = 0;
      }

      /*
       * get feedback comments
       */
      try {

        this.feedbackComments = new ArrayList<>();
        this.feedbackComments = this.feedbackQACommentableFieldsManager.findAll().stream()
          .filter(f -> (f.getSectionName() != null) && f.getSectionName().equals("study")).collect(Collectors.toList());
        if (this.feedbackComments != null) {
          final List<FeedbackQAComment> FeedbackQACommentToSearchComments =
            this.feedbackQACommentManager.findAllByPhase(this.getActualPhase().getId());
          if (FeedbackQACommentToSearchComments != null) {
            for (final FeedbackQACommentableFields field : this.feedbackComments) {
              List<FeedbackQAComment> comments = new ArrayList<FeedbackQAComment>();
              // cgamboa 08/05/2024 feedbackQACommentManager.findAll() is changed by FeedbackQACommentToSearchComments
              comments = FeedbackQACommentToSearchComments.stream()
                .filter(f -> (f != null) && (f.getPhase() != null) && (f.getPhase().getId() != null)
                  && f.getPhase().getId().equals(this.getActualPhase().getId())
                  && (f.getParentId() == this.expectedStudy.getId()) && (f.getField() != null)
                  && (f.getField().getId() != null) && f.getField().getId().equals(field.getId()))
                .collect(Collectors.toList());
              field.setQaComments(comments);
            }
          }
        }

      } catch (final Exception e) {
      }


      if (this.project != null) {
        final String params[] = {this.loggedCrp.getAcronym(), this.project.getId() + ""};
        this.setBasePermission(this.getText(Permission.PROJECT_EXPECTED_STUDIES_BASE_PERMISSION, params));
      } else {
        final String params[] = {this.loggedCrp.getAcronym(), this.expectedStudy.getId() + ""};
        this.setBasePermission(this.getText(Permission.STUDIES_BASE_PERMISSION, params));
      }
    }
    if (this.isHttpPost())

    {

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

      if (this.expectedStudy.getProjectOutcomes() != null) {
        this.expectedStudy.getProjectOutcomes().clear();
      }

      if (this.expectedStudy.getCrpOutcomes() != null) {
        this.expectedStudy.getCrpOutcomes().clear();
      }

      if (this.expectedStudy.getReferences() != null) {
        this.expectedStudy.getReferences().clear();
      }

      if (this.expectedStudy.getPublications() != null) {
        this.expectedStudy.getPublications().clear();
      }

      if (this.expectedStudy.getAllianceLevers() != null) {
        this.expectedStudy.getAllianceLevers().clear();
      }

      if (this.expectedStudy.getAllianceLeversOutcomes() != null) {
        this.expectedStudy.getAllianceLeversOutcomes().clear();
      }

      if (this.expectedStudy.getSdgAllianceLevers() != null) {
        this.expectedStudy.getSdgAllianceLevers().clear();
      }

      if (this.expectedStudy.getPartnerships() != null) {
        this.expectedStudy.getPartnerships().clear();
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
      this.expectedStudy.getProjectExpectedStudyInfo().setTag(null);
      this.expectedStudy.setAllianceLever(null);
    }


  }

  @Override
  public String save() {

    final User user = this.getCurrentUser();

    if (this.hasPermission("canEdit") || user.getId().equals(this.expectedStudyDB.getCreatedBy().getId())) {

      final Phase phase = this.getActualPhase();
      final Path path = this.getAutoSaveFilePath();

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

      this.savePublications(this.expectedStudyDB, phase);
      this.saveAllianceLever(this.expectedStudyDB, phase);
      this.saveSingleAllianceLever(expectedStudy, phase);
      this.saveProjectExpectedPartnership(this.expectedStudyDB, phase);

      // AR 2019 Save
      this.saveCenters(this.expectedStudyDB, phase);
      // this.saveMilestones(this.expectedStudyDB, phase);
      // this.saveProjectOutcomes(this.expectedStudyDB, phase);

      // AR2022 Save
      this.saveReferences(this.expectedStudyDB, phase);

      // try fixing a particular issue
      if (this.expectedStudy.getProjectExpectedStudyInfo(phase) != null) {
        this.expectedStudy.getProjectExpectedStudyInfo(phase)
          .setReferencesText(this.expectedStudyDB.getProjectExpectedStudyInfo(phase).getReferencesText());
      }

      this.saveCrpOutcomes(this.expectedStudyDB, phase);

      // Save Geographic Scope Data
      this.saveGeographicScopes(this.expectedStudyDB, phase);


      boolean haveRegions = false;
      boolean haveCountries = false;
      if (this.expectedStudy.getGeographicScopes() != null) {
        for (final ProjectExpectedStudyGeographicScope projectPolicyGeographicScope : this.expectedStudy
          .getGeographicScopes()) {

          if (projectPolicyGeographicScope.getRepIndGeographicScope().getId() == 2) {
            haveRegions = true;
          }

          if ((projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 1)
            && (projectPolicyGeographicScope.getRepIndGeographicScope().getId() != 2)) {
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
        if ((this.expectedStudy.getCountriesIds() != null) || !this.expectedStudy.getCountriesIds().isEmpty()) {

          final List<ProjectExpectedStudyCountry> countries = this.projectExpectedStudyCountryManager
            .getProjectExpectedStudyCountrybyPhase(this.expectedStudy.getId(), phase.getId()).stream()
            .filter(le -> (le != null) && le.isActive() && (le.getLocElement() != null)
              && (le.getLocElement().getLocElementType() != null)
              && (le.getLocElement().getLocElementType().getId() == 2))
            .collect(Collectors.toList());
          final List<ProjectExpectedStudyCountry> countriesSave = new ArrayList<>();
          for (final String countryIds : this.expectedStudy.getCountriesIds()) {
            final ProjectExpectedStudyCountry countryInn = new ProjectExpectedStudyCountry();
            countryInn.setLocElement(this.locElementManager.getLocElementByISOCode(countryIds));
            countryInn.setProjectExpectedStudy(this.expectedStudy);
            countryInn.setPhase(this.getActualPhase());
            countriesSave.add(countryInn);
            if (!countries.contains(countryInn)) {
              this.projectExpectedStudyCountryManager.saveProjectExpectedStudyCountry(countryInn);
            }
          }
          for (final ProjectExpectedStudyCountry projectExpectedStudyCountry : countries) {
            if (!countriesSave.contains(projectExpectedStudyCountry)) {
              this.projectExpectedStudyCountryManager
                .deleteProjectExpectedStudyCountry(projectExpectedStudyCountry.getId());
            }
          }
        }
      } else {
        this.deleteLocElements(this.expectedStudyDB, phase, true);
      }

      final List<String> relationsName = new ArrayList<>();
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
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_PROJECT_OUTCOME_RELATION);

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
      if ((this.expectedStudy.getProjectExpectedStudyInfo().getStatus() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getStatus().getId() == 4) && (this.newExpectedYear != 0)) {
        this.expectedStudy.getProjectExpectedStudyInfo().setYear(this.newExpectedYear);
      }

      // Setup focusLevel
      if (this.expectedStudy.getProjectExpectedStudyInfo().getGenderLevel() != null) {
        final RepIndGenderYouthFocusLevel focusLevel = this.focusLevelManager.getRepIndGenderYouthFocusLevelById(
          this.expectedStudy.getProjectExpectedStudyInfo().getGenderLevel().getId());
        this.expectedStudy.getProjectExpectedStudyInfo().setGenderLevel(focusLevel);
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getCapdevLevel() != null) {
        final RepIndGenderYouthFocusLevel focusLevel = this.focusLevelManager.getRepIndGenderYouthFocusLevelById(
          this.expectedStudy.getProjectExpectedStudyInfo().getCapdevLevel().getId());
        this.expectedStudy.getProjectExpectedStudyInfo().setCapdevLevel(focusLevel);
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getYouthLevel() != null) {
        final RepIndGenderYouthFocusLevel focusLevel = this.focusLevelManager
          .getRepIndGenderYouthFocusLevelById(this.expectedStudy.getProjectExpectedStudyInfo().getYouthLevel().getId());
        this.expectedStudy.getProjectExpectedStudyInfo().setYouthLevel(focusLevel);
      }

      if (this.expectedStudy.getProjectExpectedStudyInfo().getClimateChangeLevel() != null) {
        final RepIndGenderYouthFocusLevel focusLevel = this.focusLevelManager.getRepIndGenderYouthFocusLevelById(
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

      if ((this.expectedStudy.getProjectExpectedStudyInfo().getTag() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getTag().getId() == -1)) {
        this.expectedStudy.getProjectExpectedStudyInfo().setTag(null);
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

      // Allow manual editing of OICRS tag field by super admins if specificity is enabled. This disables automatic
      // operation for super admins.
      if (!this.canAccessSuperAdmin() || !this.hasSpecificities(APConstants.OICR_TAG_FIELD_MANUAL_MANAGE_ACTIVE)) {
        this.validateOICRTag(this.expectedStudyDB, phase);
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

      if ((this.getUrl() == null) || this.getUrl().isEmpty()) {
        final Collection<String> messages = this.getActionMessages();
        if (this.getInvalidFields() == null) {
          this.setInvalidFields(new HashMap<>());
        }
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          final List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (final String key : keys) {
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
   * Save primary alliance lever Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveAllianceLever(ProjectExpectedStudy projectExpectedStudy, Phase phase) {
    try {
      // delete data
      if ((projectExpectedStudy.getProjectExpectedStudySdgAllianceLevers() != null)
        && !projectExpectedStudy.getProjectExpectedStudySdgAllianceLevers().isEmpty()) {
        final List<ProjectExpectedStudySdgAllianceLever> sdgAllianceLeverPrev =
          new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySdgAllianceLevers().stream()
            .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (final ProjectExpectedStudySdgAllianceLever sdgAllianceLever : sdgAllianceLeverPrev) {

          this.projectExpectedStudySdgAllianceLeverManager
            .deleteProjectExpectedStudySdgAllianceLever(sdgAllianceLever.getId());

        }
      }
      // save data
      if (this.expectedStudy.getAllianceLever() != null
        && this.expectedStudy.getAllianceLever().getSdgContributions() != null) {
        for (SDGContribution sDGContributionTmp : this.expectedStudy.getAllianceLever().getSdgContributions()) {
          if (sDGContributionTmp != null) {
            final ProjectExpectedStudySdgAllianceLever sdgAllianceLeverSave =
              new ProjectExpectedStudySdgAllianceLever();
            sdgAllianceLeverSave.setProjectExpectedStudy(projectExpectedStudy);
            sdgAllianceLeverSave.setPhase(phase);
            sdgAllianceLeverSave.setAllianceLever(this.expectedStudy.getAllianceLever());
            sdgAllianceLeverSave.setsDGContribution(sDGContributionTmp);
            sdgAllianceLeverSave.setIsPrimary(true);
            this.projectExpectedStudySdgAllianceLeverManager
              .saveProjectExpectedStudySdgAllianceLever(sdgAllianceLeverSave);

            // This is to add studyQuantificationSave to generate
            // correct auditlog.
            this.expectedStudy.getProjectExpectedStudySdgAllianceLevers().add(sdgAllianceLeverSave);
          }
        }
      }

      if (this.expectedStudy.getAllianceLevers() != null) {
        for (AllianceLever allianceLeverTmp : this.expectedStudy.getAllianceLevers()) {

          if (allianceLeverTmp != null && allianceLeverTmp.getSdgContributions() != null) {
            for (SDGContribution sDGContributionTmp : allianceLeverTmp.getSdgContributions()) {

              if (sDGContributionTmp != null) {

                final ProjectExpectedStudySdgAllianceLever sdgAllianceLeverSave =
                  new ProjectExpectedStudySdgAllianceLever();
                sdgAllianceLeverSave.setProjectExpectedStudy(projectExpectedStudy);
                sdgAllianceLeverSave.setPhase(phase);
                sdgAllianceLeverSave.setAllianceLever(allianceLeverTmp);
                sdgAllianceLeverSave.setsDGContribution(sDGContributionTmp);
                sdgAllianceLeverSave.setIsPrimary(false);
                this.projectExpectedStudySdgAllianceLeverManager
                  .saveProjectExpectedStudySdgAllianceLever(sdgAllianceLeverSave);

                // This is to add studyQuantificationSave to generate
                // correct auditlog.
                this.expectedStudy.getProjectExpectedStudySdgAllianceLevers().add(sdgAllianceLeverSave);
              }


            }
          }


        }
      }
      this.saveAllianceLeversOutcomes(projectExpectedStudy, phase);


    } catch (Exception e) {
      logger.info(" error in saveAllianceLever function " + e.getMessage());
    }


  }

  /**
   * Save Expected Studies AllianceLeversOutcomes Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveAllianceLeversOutcomes(ProjectExpectedStudy projectExpectedStudy, Phase phase) {


    // delete outcomes data
    // Search and deleted form Information
    if ((projectExpectedStudy.getProjectExpectedStudyAllianceLeversOutcomes() != null)
      && !projectExpectedStudy.getProjectExpectedStudyAllianceLeversOutcomes().isEmpty()) {
      final List<ProjectExpectedStudyAllianceLeversOutcome> allianceLeversOutcomePrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyAllianceLeversOutcomes().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudyAllianceLeversOutcome allianceLeversOutcome : allianceLeversOutcomePrev) {

        this.projectExpectedStudyAllianceLeversOutcomeManager
          .deleteProjectExpectedStudyAllianceLeversOutcome(allianceLeversOutcome.getId());

      }
    }

    /// save outcomes
    if (this.expectedStudy.getAllianceLever() != null
      && this.expectedStudy.getAllianceLever().getLeverOutcomes() != null) {
      for (AllianceLeverOutcome allianceLeverOutcomeTmp : this.expectedStudy.getAllianceLever().getLeverOutcomes()) {
        if (allianceLeverOutcomeTmp != null) {
          final ProjectExpectedStudyAllianceLeversOutcome allianceLeverOutcomeSave =
            new ProjectExpectedStudyAllianceLeversOutcome();
          allianceLeverOutcomeSave.setProjectExpectedStudy(projectExpectedStudy);
          allianceLeverOutcomeSave.setPhase(phase);
          allianceLeverOutcomeSave.setAllianceLever(this.expectedStudy.getAllianceLever());
          allianceLeverOutcomeSave.setAllianceLeverOutcome(allianceLeverOutcomeTmp);
          this.projectExpectedStudyAllianceLeversOutcomeManager
            .saveProjectExpectedStudyAllianceLeversOutcome(allianceLeverOutcomeSave);
          // This is to add studyQuantificationSave to generate
          // correct auditlog.
          this.expectedStudy.getProjectExpectedStudyAllianceLeversOutcomes().add(allianceLeverOutcomeSave);
        }
      }

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
    if ((projectExpectedStudy.getProjectExpectedStudyCenters() != null)
      && (projectExpectedStudy.getProjectExpectedStudyCenters().size() > 0)) {
      final List<ProjectExpectedStudyCenter> centerPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyCenters().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudyCenter studyCenter : centerPrev) {
        if ((this.expectedStudy.getCenters() == null) || !this.expectedStudy.getCenters().contains(studyCenter)) {
          this.projectExpectedStudyCenterManager.deleteProjectExpectedStudyCenter(studyCenter.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getCenters() != null) {
      for (final ProjectExpectedStudyCenter studyCenter : this.expectedStudy.getCenters()) {
        if (studyCenter.getId() == null) {
          final ProjectExpectedStudyCenter studyCenterSave = new ProjectExpectedStudyCenter();
          studyCenterSave.setProjectExpectedStudy(projectExpectedStudy);
          studyCenterSave.setPhase(phase);

          final Institution institution =
            this.institutionManager.getInstitutionById(studyCenter.getInstitution().getId());

          studyCenterSave.setInstitution(institution);

          this.projectExpectedStudyCenterManager.saveProjectExpectedStudyCenter(studyCenterSave);
          // This is to add studyCrpSave to generate correct auditlog.
          this.expectedStudy.getProjectExpectedStudyCenters().add(studyCenterSave);
        }
      }
    }

  }


  /**
   * Save study Crp Program Outcome Information
   * 
   * @param ProjectExpectedStudy
   * @param phase
   */
  public void saveCrpOutcomes(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    try {
      if ((projectExpectedStudy.getProjectExpectedStudyCrpOutcomes() != null)
        && !projectExpectedStudy.getProjectExpectedStudyCrpOutcomes().isEmpty()) {

        final List<ProjectExpectedStudyCrpOutcome> outcomePrev =
          new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyCrpOutcomes().stream()
            .filter(nu -> nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

        for (final ProjectExpectedStudyCrpOutcome studyOutcome : outcomePrev) {
          if ((this.expectedStudy.getCrpOutcomes() == null)
            || !this.expectedStudy.getCrpOutcomes().contains(studyOutcome)) {
            this.projectExpectedStudyCrpOutcomeManager.deleteProjectExpectedStudyCrpOutcome(studyOutcome.getId(),
              this.getActualPhase().getId());
          }
        }
      }
    } catch (final Exception e) {
      this.logger.error("unable to delete crp outcome", e);
    }

    // Save form Information
    if (this.expectedStudy.getCrpOutcomes() != null) {
      for (final ProjectExpectedStudyCrpOutcome studyOutcome : this.expectedStudy.getCrpOutcomes()) {
        ProjectExpectedStudyCrpOutcome projectExpectedStudyOutcomeSave = new ProjectExpectedStudyCrpOutcome();

        if (studyOutcome != null) {
          // For new crp outcomes
          if (studyOutcome.getId() == null) {
            projectExpectedStudyOutcomeSave.setProjectExpectedStudy(projectExpectedStudy);
            projectExpectedStudyOutcomeSave.setPhase(phase);
          } else {
            // For old crp outcomes
            try {
              if (studyOutcome.getId() != null) {
                projectExpectedStudyOutcomeSave = this.projectExpectedStudyCrpOutcomeManager
                  .getProjectExpectedStudyCrpOutcomeById(studyOutcome.getId());
              }
            } catch (final Exception e) {
              this.logger.error("unable to get old crp outcome", e);
            }
          }

          if ((studyOutcome.getCrpOutcome() != null) && (studyOutcome.getCrpOutcome().getId() != null)) {
            final CrpProgramOutcome outcome =
              this.crpProgramOutcomeManager.getCrpProgramOutcomeById(studyOutcome.getCrpOutcome().getId());
            if (outcome != null) {
              projectExpectedStudyOutcomeSave.setCrpOutcome(outcome);
            }

            this.projectExpectedStudyCrpOutcomeManager
              .saveProjectExpectedStudyCrpOutcome(projectExpectedStudyOutcomeSave);
            // This is to add studyCrpSave to generate correct auditlog.
            if (!this.expectedStudy.getProjectExpectedStudyCrpOutcomes().contains(projectExpectedStudyOutcomeSave)) {
              this.expectedStudy.getProjectExpectedStudyCrpOutcomes().add(projectExpectedStudyOutcomeSave);
            }
          }
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
    if ((projectExpectedStudy.getProjectExpectedStudyCrps() != null)
      && (projectExpectedStudy.getProjectExpectedStudyCrps().size() > 0)) {
      final List<ProjectExpectedStudyCrp> crpPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyCrps().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudyCrp studyCrp : crpPrev) {
        if ((this.expectedStudy.getCrps() == null) || !this.expectedStudy.getCrps().contains(studyCrp)) {
          this.projectExpectedStudyCrpManager.deleteProjectExpectedStudyCrp(studyCrp.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getCrps() != null) {
      for (final ProjectExpectedStudyCrp studyCrp : this.expectedStudy.getCrps()) {
        if (studyCrp.getId() == null) {
          final ProjectExpectedStudyCrp studyCrpSave = new ProjectExpectedStudyCrp();
          studyCrpSave.setProjectExpectedStudy(projectExpectedStudy);
          studyCrpSave.setPhase(phase);

          final GlobalUnit globalUnit = this.crpManager.getGlobalUnitById(studyCrp.getGlobalUnit().getId());

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
    if ((projectExpectedStudy.getProjectExpectedStudyFlagships() != null)
      && (projectExpectedStudy.getProjectExpectedStudyFlagships().size() > 0)) {

      final List<ProjectExpectedStudyFlagship> flagshipPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())
            && (nu.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()))
          .collect(Collectors.toList()));

      for (final ProjectExpectedStudyFlagship studyFlagship : flagshipPrev) {
        if ((this.expectedStudy.getFlagships() == null) || !this.expectedStudy.getFlagships().contains(studyFlagship)) {
          this.projectExpectedStudyFlagshipManager.deleteProjectExpectedStudyFlagship(studyFlagship.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getFlagships() != null) {
      for (final ProjectExpectedStudyFlagship studyFlagship : this.expectedStudy.getFlagships()) {
        if (studyFlagship.getId() == null) {
          final ProjectExpectedStudyFlagship studyFlagshipSave = new ProjectExpectedStudyFlagship();
          studyFlagshipSave.setProjectExpectedStudy(projectExpectedStudy);
          studyFlagshipSave.setPhase(phase);

          final CrpProgram crpProgram = this.crpProgramManager.getCrpProgramById(studyFlagship.getCrpProgram().getId());

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
    if ((projectExpectedStudy.getProjectExpectedStudyGeographicScopes() != null)
      && (projectExpectedStudy.getProjectExpectedStudyGeographicScopes().size() > 0)) {

      final List<ProjectExpectedStudyGeographicScope> scopePrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyGeographicScopes().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudyGeographicScope studyScope : scopePrev) {
        if ((this.expectedStudy.getGeographicScopes() == null)
          || !this.expectedStudy.getGeographicScopes().contains(studyScope)) {
          this.projectExpectedStudyGeographicScopeManager.deleteProjectExpectedStudyGeographicScope(studyScope.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getGeographicScopes() != null) {
      for (final ProjectExpectedStudyGeographicScope studyScope : this.expectedStudy.getGeographicScopes()) {
        if (studyScope.getId() == null) {
          final ProjectExpectedStudyGeographicScope studyScopeSave = new ProjectExpectedStudyGeographicScope();
          studyScopeSave.setProjectExpectedStudy(projectExpectedStudy);
          studyScopeSave.setPhase(phase);

          final RepIndGeographicScope geoScope =
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
    if ((projectExpectedStudy.getProjectExpectedStudyInnovations() != null)
      && (projectExpectedStudy.getProjectExpectedStudyInnovations().size() > 0)) {
      final List<ProjectExpectedStudyInnovation> innovationPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyInnovations().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudyInnovation studyInnovation : innovationPrev) {
        if ((this.expectedStudy.getInnovations() == null)
          || !this.expectedStudy.getInnovations().contains(studyInnovation)) {
          this.projectExpectedStudyInnovationManager.deleteProjectExpectedStudyInnovation(studyInnovation.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getInnovations() != null) {
      for (final ProjectExpectedStudyInnovation studyInnovation : this.expectedStudy.getInnovations()) {
        if (studyInnovation.getId() == null) {
          final ProjectExpectedStudyInnovation studyInnovationSave = new ProjectExpectedStudyInnovation();
          studyInnovationSave.setProjectExpectedStudy(projectExpectedStudy);
          studyInnovationSave.setPhase(phase);

          final ProjectInnovation projectInnovation =
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
    if ((projectExpectedStudy.getProjectExpectedStudyInstitutions() != null)
      && (projectExpectedStudy.getProjectExpectedStudyInstitutions().size() > 0)) {

      final List<ProjectExpectedStudyInstitution> institutionPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyInstitutions().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudyInstitution studyInstitution : institutionPrev) {
        if ((this.expectedStudy.getInstitutions() == null)
          || !this.expectedStudy.getInstitutions().contains(studyInstitution)) {
          this.projectExpectedStudyInstitutionManager.deleteProjectExpectedStudyInstitution(studyInstitution.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getInstitutions() != null) {
      for (final ProjectExpectedStudyInstitution studyInstitution : this.expectedStudy.getInstitutions()) {
        if (studyInstitution.getId() == null) {
          final ProjectExpectedStudyInstitution studyInstitutionSave = new ProjectExpectedStudyInstitution();
          studyInstitutionSave.setProjectExpectedStudy(projectExpectedStudy);
          studyInstitutionSave.setPhase(phase);

          final Institution institution =
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
    if ((projectExpectedStudy.getProjectExpectedStudyLinks() != null)
      && (projectExpectedStudy.getProjectExpectedStudyLinks().size() > 0)) {
      final List<ProjectExpectedStudyLink> linkPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyLinks().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudyLink studyLink : linkPrev) {
        if ((this.expectedStudy.getLinks() == null) || !this.expectedStudy.getLinks().contains(studyLink)) {
          this.projectExpectedStudyLinkManager.deleteProjectExpectedStudyLink(studyLink.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getLinks() != null) {
      for (final ProjectExpectedStudyLink studyLink : this.expectedStudy.getLinks()) {
        if (studyLink.getId() == null) {
          final ProjectExpectedStudyLink studyLinkSave = new ProjectExpectedStudyLink();
          studyLinkSave.setProjectExpectedStudy(projectExpectedStudy);
          studyLinkSave.setPhase(phase);
          studyLinkSave.setLink(studyLink.getLink());

          this.projectExpectedStudyLinkManager.saveProjectExpectedStudyLink(studyLinkSave);
          // This is to add studyLinkSave to generate correct
          // auditlog.
          this.expectedStudy.getProjectExpectedStudyLinks().add(studyLinkSave);
        } else {
          final ProjectExpectedStudyLink studyLinkSave =
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
    if ((projectExpectedStudy.getProjectExpectedStudyMilestones() != null)
      && (projectExpectedStudy.getProjectExpectedStudyMilestones().size() > 0)) {

      final List<ProjectExpectedStudyMilestone> milestonePrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyMilestones().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudyMilestone studyMilestone : milestonePrev) {
        if ((this.expectedStudy.getMilestones() == null)
          || !this.expectedStudy.getMilestones().contains(studyMilestone)) {
          this.projectExpectedStudyMilestoneManager.deleteProjectExpectedStudyMilestone(studyMilestone.getId());
        }
      }
    }
    // Save policy milestones only if boolean 'has milestones' selection is true
    if ((this.expectedStudy.getProjectExpectedStudyInfo().getHasMilestones() != null)
      && (this.expectedStudy.getProjectExpectedStudyInfo().getHasMilestones() == true)) {

      // Save form Information
      if (this.expectedStudy.getMilestones() != null) {
        for (final ProjectExpectedStudyMilestone studyMilestone : this.expectedStudy.getMilestones()) {
          if (studyMilestone.getId() == null) {
            final ProjectExpectedStudyMilestone studyMilestoneSave = new ProjectExpectedStudyMilestone();
            studyMilestoneSave.setProjectExpectedStudy(projectExpectedStudy);
            studyMilestoneSave.setPhase(phase);
            studyMilestoneSave.setPrimary(studyMilestone.getPrimary());

            if ((this.expectedStudy.getMilestones() != null) && (this.expectedStudy.getMilestones().size() == 1)) {
              studyMilestoneSave.setPrimary(true);
            }

            if ((studyMilestone.getCrpMilestone() != null) && (studyMilestone.getCrpMilestone().getId() != null)) {
              final CrpMilestone milestone =
                this.milestoneManager.getCrpMilestoneById(studyMilestone.getCrpMilestone().getId());
              studyMilestoneSave.setCrpMilestone(milestone);

              this.projectExpectedStudyMilestoneManager.saveProjectExpectedStudyMilestone(studyMilestoneSave);
              // This is to add studyCrpSave to generate correct auditlog.
              this.expectedStudy.getProjectExpectedStudyMilestones().add(studyMilestoneSave);
            }
          } else {
            // if milestone already exist - save primary
            ProjectExpectedStudyMilestone studyMilestoneSave = new ProjectExpectedStudyMilestone();
            studyMilestoneSave =
              this.projectExpectedStudyMilestoneManager.getProjectExpectedStudyMilestoneById(studyMilestone.getId());
            studyMilestoneSave.setProjectExpectedStudy(projectExpectedStudy);
            studyMilestoneSave.setPhase(phase);
            studyMilestoneSave.setPrimary(studyMilestone.getPrimary());

            if ((studyMilestone.getCrpMilestone() != null) && (studyMilestone.getCrpMilestone().getId() != null)) {
              final CrpMilestone milestone =
                this.milestoneManager.getCrpMilestoneById(studyMilestone.getCrpMilestone().getId());
              studyMilestoneSave.setCrpMilestone(milestone);
            }
            if ((this.expectedStudy.getMilestones() != null) && (this.expectedStudy.getMilestones().size() == 1)) {
              studyMilestoneSave.setPrimary(true);
            }

            this.projectExpectedStudyMilestoneManager.saveProjectExpectedStudyMilestone(studyMilestoneSave);
            // This is to add studyCrpSave to generate correct auditlog.
            this.expectedStudy.getProjectExpectedStudyMilestones().add(studyMilestoneSave);

          }
        }
      }
    } else {
      // Delete all milestones for this policy
      if ((this.expectedStudy.getMilestones() != null) && (this.expectedStudy.getMilestones().size() > 0)) {
        for (final ProjectExpectedStudyMilestone studyMilestone : this.expectedStudy.getMilestones()) {
          try {
            final CrpMilestone milestone = this.milestoneManager.getCrpMilestoneById(studyMilestone.getId());
            if (milestone != null) {
              this.projectExpectedStudyMilestoneManager.deleteProjectExpectedStudyMilestone(studyMilestone.getId());
              // This is to add studyCrpSave to generate correct auditlog.
              this.expectedStudy.getProjectExpectedStudyMilestones().remove(
                this.projectExpectedStudyMilestoneManager.getProjectExpectedStudyMilestoneById(studyMilestone.getId()));
            }
          } catch (final Exception e) {

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
    if ((projectExpectedStudy.getProjectExpectedStudyPolicies() != null)
      && (projectExpectedStudy.getProjectExpectedStudyPolicies().size() > 0)) {
      final List<ProjectExpectedStudyPolicy> policyPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyPolicies().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudyPolicy studyPolicy : policyPrev) {
        if ((this.expectedStudy.getPolicies() == null) || !this.expectedStudy.getPolicies().contains(studyPolicy)) {
          this.projectExpectedStudyPolicyManager.deleteProjectExpectedStudyPolicy(studyPolicy.getId());
        }
      }


      // Delete prev studies policies if the question is not
      if ((this.expectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()) != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getIsContribution() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getIsContribution() == false)) {
        for (final ProjectExpectedStudyPolicy studyPolicy : policyPrev) {
          this.projectExpectedStudyPolicyManager.deleteProjectExpectedStudyPolicy(studyPolicy.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getPolicies() != null) {
      for (final ProjectExpectedStudyPolicy studyPolicy : this.expectedStudy.getPolicies()) {
        if (studyPolicy.getId() == null) {
          final ProjectExpectedStudyPolicy studyPolicySave = new ProjectExpectedStudyPolicy();
          studyPolicySave.setProjectExpectedStudy(projectExpectedStudy);
          studyPolicySave.setPhase(phase);

          final ProjectPolicy projectPolicy =
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
   * 08/01 save Deliverable Partnership Responsible
   *
   * @param deliverable
   */
  public void saveProjectExpectedPartnership(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    if ((projectExpectedStudy.getProjectExpectedStudyPartnerships() != null)
      && (projectExpectedStudy.getProjectExpectedStudyPartnerships().size() > 0)) {
      List<ProjectExpectedStudyPartnership> projectExpectedStudyPartnershipCustom = null;
      try {
        projectExpectedStudyPartnershipCustom = this.projectExpectedStudyPartnershipManager
          .findByExpectedAndPhase(projectExpectedStudy.getId(), this.getActualPhase().getId());

      } catch (final Exception e) {
        // TODO: handle exception
        this.logger.info(e.getMessage());
      }
      List<ProjectExpectedStudyPartnership> projectExpectedStudyPartnershipPrev = null;
      if ((projectExpectedStudyPartnershipCustom != null) && !projectExpectedStudyPartnershipCustom.isEmpty()) {
        projectExpectedStudyPartnershipPrev = projectExpectedStudyPartnershipCustom.stream()
          .filter(dp -> dp.isActive() && dp.getPhase().getId().equals(this.getActualPhase().getId()) && dp
            .getProjectExpectedStudyPartnerType().getId().equals(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE))
          .collect(Collectors.toList());
      }
      try {
        // 2024/07/22 conditional was added to avoid exception by null data
        if ((projectExpectedStudyPartnershipPrev != null) && !projectExpectedStudyPartnershipPrev.isEmpty()) {
          for (final ProjectExpectedStudyPartnership projectExpectedStudyPartnership : projectExpectedStudyPartnershipPrev) {
            if ((this.expectedStudy.getPartnerships() == null) || ((this.expectedStudy.getPartnerships() != null)
              && !this.expectedStudy.getPartnerships().contains(projectExpectedStudyPartnership))) {
              this.projectExpectedStudyPartnershipManager
                .deleteProjectExpectedStudyPartnership(projectExpectedStudyPartnership.getId());
            }
          }
        }
      } catch (final Exception e) {
        this.logger.error("unable to delete deliverable user partnership in saveProjectExpectedPartnership function  ",
          e.getMessage());
      }

    }

    final ProjectExpectedStudyPartnerType projectExpectedStudyPartnerType = this.projectExpectedStudyPartnerTypeManager
      .getProjectExpectedStudyPartnerTypeById(APConstants.DELIVERABLE_PARTNERSHIP_TYPE_RESPONSIBLE);
    if (this.expectedStudy.getPartnerships() != null) {
      for (final ProjectExpectedStudyPartnership projectExpectedStudyPartnership : this.expectedStudy
        .getPartnerships()) {
        if (projectExpectedStudyPartnership.getId() != null) {
          ProjectExpectedStudyPartnership projectExpectedStudyPartnershipSave =
            this.projectExpectedStudyPartnershipManager
              .getProjectExpectedStudyPartnershipById(projectExpectedStudyPartnership.getId());

          if (projectExpectedStudyPartnership.getInstitution().getId() != null) {
            if (projectExpectedStudyPartnership.getInstitution().getId() != -1) {
              final Institution institution =
                this.institutionManager.getInstitutionById(projectExpectedStudyPartnership.getInstitution().getId());
              projectExpectedStudyPartnershipSave.setInstitution(institution);

              if (projectExpectedStudyPartnership.getPartnershipPersons() != null) {
                projectExpectedStudyPartnershipSave
                  .setPartnershipPersons(projectExpectedStudyPartnership.getPartnershipPersons());
              }
              projectExpectedStudyPartnershipSave = this.projectExpectedStudyPartnershipManager
                .saveProjectExpectedStudyPartnership(projectExpectedStudyPartnershipSave);
              this.saveProjectExpectedstudyPartnershipsPersons(projectExpectedStudyPartnership,
                projectExpectedStudyPartnershipSave);
            } else {
              this.projectExpectedStudyPartnershipManager
                .deleteProjectExpectedStudyPartnership(projectExpectedStudyPartnership.getId());
            }
          }

        } else {
          ProjectExpectedStudyPartnership projectExpectedStudyPartnershipSave = new ProjectExpectedStudyPartnership();
          projectExpectedStudyPartnershipSave.setPhase(this.getActualPhase());
          projectExpectedStudyPartnershipSave.setProjectExpectedStudy(projectExpectedStudy);
          projectExpectedStudyPartnershipSave.setCreatedBy(this.getCurrentUser());
          projectExpectedStudyPartnershipSave.setProjectExpectedStudyPartnerType(projectExpectedStudyPartnerType);

          if ((projectExpectedStudyPartnership.getInstitution() != null)
            && (projectExpectedStudyPartnership.getInstitution().getId() != null)) {
            if (projectExpectedStudyPartnership.getInstitution().getId() != -1) {
              final Institution institution =
                this.institutionManager.getInstitutionById(projectExpectedStudyPartnership.getInstitution().getId());
              projectExpectedStudyPartnershipSave.setInstitution(institution);


              if (projectExpectedStudyPartnership.getPartnershipPersons() != null) {
                projectExpectedStudyPartnershipSave
                  .setPartnershipPersons(projectExpectedStudyPartnership.getPartnershipPersons());
              }

              projectExpectedStudyPartnershipSave = this.projectExpectedStudyPartnershipManager
                .saveProjectExpectedStudyPartnership(projectExpectedStudyPartnershipSave);
              this.saveProjectExpectedstudyPartnershipsPersons(projectExpectedStudyPartnership,
                projectExpectedStudyPartnershipSave);
            }
          }

        }
      }


    }

  }

  private void saveProjectExpectedstudyPartnershipsPersons(
    ProjectExpectedStudyPartnership projectExpectedStudyPartnership,
    ProjectExpectedStudyPartnership projectExpectedStudyPartnershipDB) {

    if ((projectExpectedStudyPartnershipDB.getProjectExpectedStudyPartnershipsPersons() != null)
      && !projectExpectedStudyPartnershipDB.getProjectExpectedStudyPartnershipsPersons().isEmpty()) {

      final List<ProjectExpectedStudyPartnershipsPerson> projectExpectedStudyPartnershipsPersonPrev =
        projectExpectedStudyPartnershipDB.getProjectExpectedStudyPartnershipsPersons().stream()
          .filter(ProjectExpectedStudyPartnershipsPerson::isActive).collect(Collectors.toList());

      for (final ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPerson : projectExpectedStudyPartnershipsPersonPrev) {
        if ((projectExpectedStudyPartnership.getPartnershipPersons() == null) || !projectExpectedStudyPartnership
          .getPartnershipPersons().contains(projectExpectedStudyPartnershipsPerson)) {
          this.projectExpectedStudyPartnershipsPersonManager
            .deleteProjectExpectedStudyPartnershipsPerson(projectExpectedStudyPartnershipsPerson.getId());
        }
      }

    }
    if (projectExpectedStudyPartnership.getPartnershipPersons() != null) {
      for (final ProjectExpectedStudyPartnershipsPerson person : projectExpectedStudyPartnership
        .getPartnershipPersons()) {
        if (person.getId() != null) {
          final ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPersonNew =
            this.projectExpectedStudyPartnershipsPersonManager
              .getProjectExpectedStudyPartnershipsPersonById(person.getId());

          if ((person.getUser() != null) && (person.getUser().getId() != null)) {
            if (!person.getUser().getId().equals(projectExpectedStudyPartnershipsPersonNew.getUser().getId())) {
              projectExpectedStudyPartnershipsPersonNew.setUser(this.userManager.getUser(person.getUser().getId()));
              this.projectExpectedStudyPartnershipsPersonManager
                .saveProjectExpectedStudyPartnershipsPerson(projectExpectedStudyPartnershipsPersonNew);
            }
          } else {
            this.projectExpectedStudyPartnershipsPersonManager
              .deleteProjectExpectedStudyPartnershipsPerson(person.getId());
          }
        } else {
          if ((person.getUser() != null) && (person.getUser().getId() != null)) {
            final ProjectExpectedStudyPartnershipsPerson projectExpectedStudyPartnershipsPersonNew =
              new ProjectExpectedStudyPartnershipsPerson();
            projectExpectedStudyPartnershipsPersonNew.setUser(this.userManager.getUser(person.getUser().getId()));
            projectExpectedStudyPartnershipsPersonNew
              .setProjectExpectedStudyPartnership(projectExpectedStudyPartnershipDB);
            this.projectExpectedStudyPartnershipsPersonManager
              .saveProjectExpectedStudyPartnershipsPerson(projectExpectedStudyPartnershipsPersonNew);
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
  public void saveProjectOutcomes(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if ((projectExpectedStudy.getProjectExpectedStudyProjectOutcomes() != null)
      && (projectExpectedStudy.getProjectExpectedStudyProjectOutcomes().size() > 0)) {

      final List<ProjectExpectedStudyProjectOutcome> outcomePrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyProjectOutcomes().stream()
          .filter(nu -> nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudyProjectOutcome studyOutcome : outcomePrev) {
        if ((this.expectedStudy.getProjectOutcomes() == null)
          || !this.expectedStudy.getProjectOutcomes().contains(studyOutcome)) {
          this.projectExpectedStudyProjectOutcomeManager.deleteProjectExpectedStudyProjectOutcome(studyOutcome.getId(),
            this.getActualPhase().getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getProjectOutcomes() != null) {
      for (final ProjectExpectedStudyProjectOutcome studyOutcome : this.expectedStudy.getProjectOutcomes()) {
        if (studyOutcome.getId() == null) {
          final ProjectExpectedStudyProjectOutcome studyOutcomeSave = new ProjectExpectedStudyProjectOutcome();
          studyOutcomeSave.setProjectExpectedStudy(projectExpectedStudy);
          studyOutcomeSave.setPhase(phase);

          if ((studyOutcome.getProjectOutcome() != null) && (studyOutcome.getProjectOutcome().getId() != null)) {
            final ProjectOutcome outcome =
              this.projectOutcomeManager.getProjectOutcomeById(studyOutcome.getProjectOutcome().getId());
            studyOutcomeSave.setProjectOutcome(outcome);

            this.projectExpectedStudyProjectOutcomeManager.saveProjectExpectedStudyProjectOutcome(studyOutcomeSave);
            // This is to add studyCrpSave to generate correct auditlog.
            this.expectedStudy.getProjectExpectedStudyProjectOutcomes().add(studyOutcomeSave);
          }
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
    if ((projectExpectedStudy.getExpectedStudyProjects() != null)
      && (projectExpectedStudy.getExpectedStudyProjects().size() > 0)) {

      final List<ExpectedStudyProject> projectPrev =
        new ArrayList<>(projectExpectedStudy.getExpectedStudyProjects().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ExpectedStudyProject studyProject : projectPrev) {
        if ((this.expectedStudy.getProjects() == null) || !this.expectedStudy.getProjects().contains(studyProject)) {
          this.expectedStudyProjectManager.deleteExpectedStudyProject(studyProject.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getProjects() != null) {
      for (final ExpectedStudyProject studyProject : this.expectedStudy.getProjects()) {
        if (studyProject.getId() == null) {
          final ExpectedStudyProject studyProjectSave = new ExpectedStudyProject();
          studyProjectSave.setProjectExpectedStudy(projectExpectedStudy);
          studyProjectSave.setPhase(phase);

          final Project project = this.projectManager.getProjectById(studyProject.getProject().getId());

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
   * Save Expected Studies Publications
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void savePublications(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if ((projectExpectedStudy.getProjectExpectedStudyPublications() != null)
      && !projectExpectedStudy.getProjectExpectedStudyPublications().isEmpty()) {
      final List<ProjectExpectedStudyPublication> publicationPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyPublications().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudyPublication publication : publicationPrev) {
        if ((this.expectedStudy.getPublications() == null)
          || !this.expectedStudy.getPublications().contains(publication)) {
          this.projectExpectedStudyPublicationManager.deleteProjectExpectedStudyPublication(publication.getId());
        }
      }
    }


    // Save form Information
    if (this.expectedStudy.getPublications() != null) {
      for (final ProjectExpectedStudyPublication publication : this.expectedStudy.getPublications()) {
        if (publication.getId() == null) {
          final ProjectExpectedStudyPublication studyPublicationSave = new ProjectExpectedStudyPublication();
          studyPublicationSave.setProjectExpectedStudy(projectExpectedStudy);
          studyPublicationSave.setPhase(phase);
          studyPublicationSave.setName(publication.getName());
          studyPublicationSave.setPosition(publication.getPosition());
          studyPublicationSave.setAffiliation(publication.getAffiliation());


          this.projectExpectedStudyPublicationManager.saveProjectExpectedStudyPublication(studyPublicationSave);
          // This is to add studyQuantificationSave to generate
          // correct auditlog.
          this.expectedStudy.getProjectExpectedStudyPublications().add(studyPublicationSave);
        } else {
          final ProjectExpectedStudyPublication studyPublicationSave =
            this.projectExpectedStudyPublicationManager.getProjectExpectedStudyPublicationById(publication.getId());

          studyPublicationSave.setProjectExpectedStudy(projectExpectedStudy);
          studyPublicationSave.setPhase(phase);
          studyPublicationSave.setName(publication.getName());
          studyPublicationSave.setPosition(publication.getPosition());
          studyPublicationSave.setAffiliation(publication.getAffiliation());

          this.projectExpectedStudyPublicationManager.saveProjectExpectedStudyPublication(studyPublicationSave);
          // This is to add studyQuantificationSave to generate
          // correct auditlog.
          this.expectedStudy.getProjectExpectedStudyPublications().add(studyPublicationSave);
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
    if ((projectExpectedStudy.getProjectExpectedStudyQuantifications() != null)
      && !projectExpectedStudy.getProjectExpectedStudyQuantifications().isEmpty()) {
      final List<ProjectExpectedStudyQuantification> quantificationPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyQuantifications().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudyQuantification studyQuantification : quantificationPrev) {
        if ((this.expectedStudy.getQuantifications() == null)
          || !this.expectedStudy.getQuantifications().contains(studyQuantification)) {
          this.projectExpectedStudyQuantificationManager
            .deleteProjectExpectedStudyQuantification(studyQuantification.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getQuantifications() != null) {
      for (final ProjectExpectedStudyQuantification studyQuantification : this.expectedStudy.getQuantifications()) {
        if (studyQuantification.getId() == null) {
          final ProjectExpectedStudyQuantification studyQuantificationSave = new ProjectExpectedStudyQuantification();
          studyQuantificationSave.setProjectExpectedStudy(projectExpectedStudy);
          studyQuantificationSave.setPhase(phase);

          // Default Values for type Quantification.
          if (studyQuantification.getTypeQuantification() != null) {
            studyQuantificationSave.setTypeQuantification(studyQuantification.getTypeQuantification());
          } else {
            studyQuantificationSave.setTypeQuantification("A");
          }

          if ((studyQuantification.getQuantificationType() != null)
            && (studyQuantification.getQuantificationType().getId() != -1)) {
            studyQuantificationSave.setQuantificationType(studyQuantification.getQuantificationType());
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
          final ProjectExpectedStudyQuantification studyQuantificationSave =
            this.projectExpectedStudyQuantificationManager
              .getProjectExpectedStudyQuantificationById(studyQuantification.getId());

          // Default Values for type Quantification.
          if (studyQuantification.getTypeQuantification() != null) {
            studyQuantificationSave.setTypeQuantification(studyQuantification.getTypeQuantification());
          } else {
            studyQuantificationSave.setTypeQuantification("A");
          }

          if ((studyQuantification.getQuantificationType() != null)
            && (studyQuantification.getQuantificationType().getId() != -1)) {
            studyQuantificationSave.setQuantificationType(studyQuantification.getQuantificationType());
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
   * Save Expected Studies References Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  private void saveReferences(ProjectExpectedStudy projectExpectedStudy, Phase phase) {
    // Search and deleted form Information
    if (projectExpectedStudy.getProjectExpectedStudyReferences() != null) {
      final List<ProjectExpectedStudyReference> referencesPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyReferences().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudyReference studyReference : referencesPrev) {
        if ((this.expectedStudy.getReferences() == null)
          || !this.expectedStudy.getReferences().contains(studyReference)) {
          this.projectExpectedStudyReferenceManager.deleteProjectExpectedStudyReference(studyReference.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getReferences() != null) {
      for (final ProjectExpectedStudyReference studyReference : this.expectedStudy.getReferences()) {
        if (studyReference.getId() == null) {
          final ProjectExpectedStudyReference studyReferenceSave = new ProjectExpectedStudyReference();
          studyReferenceSave.setProjectExpectedStudy(projectExpectedStudy);
          studyReferenceSave.setPhase(phase);
          studyReferenceSave.setReference(studyReference.getReference());
          studyReferenceSave.setLink(studyReference.getLink());
          boolean externalAutor = false;
          if (studyReference.getExternalAuthor() != null) {
            externalAutor = true;
          }
          studyReferenceSave.setExternalAuthor(externalAutor);

          this.projectExpectedStudyReferenceManager.saveProjectExpectedStudyReference(studyReferenceSave);
          // This is to add studyReferenceSave to generate correct
          // auditlog.
          this.expectedStudy.getProjectExpectedStudyReferences().add(studyReferenceSave);
        } else {
          try {
            final ProjectExpectedStudyReference studyReferenceSave =
              this.projectExpectedStudyReferenceManager.getProjectExpectedStudyReferenceById(studyReference.getId());
            if ((studyReferenceSave != null) && (projectExpectedStudy != null)) {
              studyReferenceSave.setProjectExpectedStudy(projectExpectedStudy);
              studyReferenceSave.setPhase(phase);
              studyReferenceSave.setReference(studyReference.getReference());
              studyReferenceSave.setLink(studyReference.getLink());
              studyReferenceSave.setExternalAuthor(studyReference.getExternalAuthor());
            }

            this.projectExpectedStudyReferenceManager.saveProjectExpectedStudyReference(studyReferenceSave);
            // This is to add studyReferenceSave to generate correct
            // auditlog.
            this.expectedStudy.getProjectExpectedStudyReferences().add(studyReferenceSave);
          } catch (final LockAcquisitionException lae) {
            // i am tired of this exception
          }
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
    if ((projectExpectedStudy.getProjectExpectedStudyFlagships() != null)
      && (projectExpectedStudy.getProjectExpectedStudyFlagships().size() > 0)) {

      final List<ProjectExpectedStudyFlagship> flagshipPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())
            && (nu.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()))
          .collect(Collectors.toList()));

      for (final ProjectExpectedStudyFlagship studyFlagship : flagshipPrev) {
        if ((this.expectedStudy.getRegions() == null) || !this.expectedStudy.getRegions().contains(studyFlagship)) {
          this.projectExpectedStudyFlagshipManager.deleteProjectExpectedStudyFlagship(studyFlagship.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getRegions() != null) {
      for (final ProjectExpectedStudyFlagship studyFlagship : this.expectedStudy.getRegions()) {
        if (studyFlagship.getId() == null) {
          final ProjectExpectedStudyFlagship studyFlagshipSave = new ProjectExpectedStudyFlagship();
          studyFlagshipSave.setProjectExpectedStudy(projectExpectedStudy);
          studyFlagshipSave.setPhase(phase);

          final CrpProgram crpProgram = this.crpProgramManager.getCrpProgramById(studyFlagship.getCrpProgram().getId());

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
   * Save Expected Studies SdgAllianceLever Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveSdgAllianceLever(ProjectExpectedStudy projectExpectedStudy, Phase phase) {

    // Search and deleted form Information
    if ((projectExpectedStudy.getProjectExpectedStudySdgAllianceLevers() != null)
      && !projectExpectedStudy.getProjectExpectedStudySdgAllianceLevers().isEmpty()) {
      final List<ProjectExpectedStudySdgAllianceLever> sdgAllianceLeverPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySdgAllianceLevers().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudySdgAllianceLever sdgAllianceLever : sdgAllianceLeverPrev) {
        if ((this.expectedStudy.getSdgAllianceLevers() == null)
          || !this.expectedStudy.getSdgAllianceLevers().contains(sdgAllianceLever)) {
          this.projectExpectedStudySdgAllianceLeverManager
            .deleteProjectExpectedStudySdgAllianceLever(sdgAllianceLever.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getSdgAllianceLevers() != null) {
      for (final ProjectExpectedStudySdgAllianceLever sdgAllianceLever : this.expectedStudy.getSdgAllianceLevers()) {
        if (sdgAllianceLever.getId() == null) {
          final ProjectExpectedStudySdgAllianceLever sdgAllianceLeverSave = new ProjectExpectedStudySdgAllianceLever();
          sdgAllianceLeverSave.setProjectExpectedStudy(projectExpectedStudy);
          sdgAllianceLeverSave.setPhase(phase);
          sdgAllianceLeverSave.setAllianceLever(sdgAllianceLever.getAllianceLever());
          sdgAllianceLeverSave.setsDGContribution(sdgAllianceLever.getsDGContribution());
          sdgAllianceLeverSave.setLeverComments(sdgAllianceLever.getLeverComments());
          sdgAllianceLeverSave.setIsPrimary(sdgAllianceLever.getIsPrimary());


          this.projectExpectedStudySdgAllianceLeverManager
            .saveProjectExpectedStudySdgAllianceLever(sdgAllianceLeverSave);
          // This is to add studyQuantificationSave to generate
          // correct auditlog.
          this.expectedStudy.getProjectExpectedStudySdgAllianceLevers().add(sdgAllianceLeverSave);
        } else {
          final ProjectExpectedStudySdgAllianceLever sdgAllianceLeverSave =
            this.projectExpectedStudySdgAllianceLeverManager
              .getProjectExpectedStudySdgAllianceLeverById(sdgAllianceLever.getId());

          sdgAllianceLeverSave.setAllianceLever(sdgAllianceLever.getAllianceLever());
          sdgAllianceLeverSave.setsDGContribution(sdgAllianceLever.getsDGContribution());
          sdgAllianceLeverSave.setLeverComments(sdgAllianceLever.getLeverComments());
          sdgAllianceLeverSave.setIsPrimary(sdgAllianceLever.getIsPrimary());


          this.projectExpectedStudySdgAllianceLeverManager
            .saveProjectExpectedStudySdgAllianceLever(sdgAllianceLeverSave);
          // This is to add studyQuantificationSave to generate
          // correct auditlog.
          this.expectedStudy.getProjectExpectedStudySdgAllianceLevers().add(sdgAllianceLeverSave);
        }
      }
    }

  }


  /**
   * Save primary alliance lever Information
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void saveSingleAllianceLever(ProjectExpectedStudy projectExpectedStudy, Phase phase) {
    try {

      // save the other
      if (this.expectedStudy.getAllianceLever() != null && this.expectedStudy.getAllianceLever().getId() != null
        && this.expectedStudy.getAllianceLever().getLeverComments() != null
        && this.expectedStudy.getAllianceLever().getLeverComments().length() > 0
        && this.expectedStudy.getAllianceLever().getSdgContributions() == null) {
        final ProjectExpectedStudySdgAllianceLever sdgAllianceLeverSaveTmp = new ProjectExpectedStudySdgAllianceLever();
        sdgAllianceLeverSaveTmp.setProjectExpectedStudy(projectExpectedStudy);
        sdgAllianceLeverSaveTmp.setPhase(phase);
        sdgAllianceLeverSaveTmp.setAllianceLever(this.expectedStudy.getAllianceLever());
        sdgAllianceLeverSaveTmp.setIsPrimary(true);
        sdgAllianceLeverSaveTmp.setLeverComments(this.expectedStudy.getAllianceLever().getLeverComments());
        this.projectExpectedStudySdgAllianceLeverManager
          .saveProjectExpectedStudySdgAllianceLever(sdgAllianceLeverSaveTmp);

        // This is to add studyQuantificationSave to generate
        // correct auditlog.
        this.expectedStudy.getProjectExpectedStudySdgAllianceLevers().add(sdgAllianceLeverSaveTmp);


      }


      if (this.expectedStudy.getAllianceLevers() != null) {
        for (AllianceLever allianceLeverTmp : this.expectedStudy.getAllianceLevers()) {
          if (allianceLeverTmp != null && allianceLeverTmp.getId() != null
            && allianceLeverTmp.getLeverComments() != null && allianceLeverTmp.getLeverComments().length() > 0) {
            final ProjectExpectedStudySdgAllianceLever sdgAllianceLeverSave =
              new ProjectExpectedStudySdgAllianceLever();
            sdgAllianceLeverSave.setProjectExpectedStudy(projectExpectedStudy);
            sdgAllianceLeverSave.setPhase(phase);
            sdgAllianceLeverSave.setAllianceLever(allianceLeverTmp);
            sdgAllianceLeverSave.setIsPrimary(false);
            sdgAllianceLeverSave.setLeverComments(allianceLeverTmp.getLeverComments());
            this.projectExpectedStudySdgAllianceLeverManager
              .saveProjectExpectedStudySdgAllianceLever(sdgAllianceLeverSave);
            // This is to add studyQuantificationSave to generate
            // correct auditlog.
            this.expectedStudy.getProjectExpectedStudySdgAllianceLevers().add(sdgAllianceLeverSave);
          }
        }
      }


    } catch (Exception e) {
      logger.info(" error in saveSingleAllianceLever function " + e.getMessage());
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
    if ((projectExpectedStudy.getProjectExpectedStudySrfTargets() != null)
      && (projectExpectedStudy.getProjectExpectedStudySrfTargets().size() > 0)) {

      final List<ProjectExpectedStudySrfTarget> targetPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySrfTargets().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudySrfTarget studytarget : targetPrev) {
        if ((this.expectedStudy.getSrfTargets() == null) || !this.expectedStudy.getSrfTargets().contains(studytarget)) {
          this.projectExpectedStudySrfTargetManager.deleteProjectExpectedStudySrfTarget(studytarget.getId());
        }
      }

      // Delete previous srf targets if the answer of the question is not
      if ((projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()) != null)
        && (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getIsSrfTarget() != null)
        && (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getIsSrfTarget()
          .equals("targetsOptionNo")
          || projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()).getIsSrfTarget()
            .equals("targetsOptionTooEarlyToSay"))) {
        for (final ProjectExpectedStudySrfTarget studytarget : targetPrev) {
          this.projectExpectedStudySrfTargetManager.deleteProjectExpectedStudySrfTarget(studytarget.getId());

        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getSrfTargets() != null) {
      for (final ProjectExpectedStudySrfTarget studytarget : this.expectedStudy.getSrfTargets()) {
        if (studytarget.getId() == null) {
          final ProjectExpectedStudySrfTarget studytargetSave = new ProjectExpectedStudySrfTarget();
          studytargetSave.setProjectExpectedStudy(projectExpectedStudy);
          studytargetSave.setPhase(phase);

          final SrfSloIndicator sloIndicator =
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

    final List<ProjectExpectedStudyRegion> regionPrev = this.projectExpectedStudyRegionManager
      .getProjectExpectedStudyRegionbyPhase(this.expectedStudy.getId(), phase.getId()).stream()
      .filter(le -> le.isActive() && le.getLocElement().getLocElementType().getId().equals(1L))
      .collect(Collectors.toList());

    // Search and deleted form Information
    if ((regionPrev != null) && (regionPrev.size() > 0)) {
      for (final ProjectExpectedStudyRegion studyRegion : regionPrev) {
        if ((this.expectedStudy.getStudyRegions() == null)
          || !this.expectedStudy.getStudyRegions().contains(studyRegion)) {
          this.projectExpectedStudyRegionManager.deleteProjectExpectedStudyRegion(studyRegion.getId());
        }
      }
    }

    if (this.expectedStudy.getStudyRegions() != null) {
      for (final ProjectExpectedStudyRegion studyRegion : this.expectedStudy.getStudyRegions()) {
        if (studyRegion.getId() == null) {
          final ProjectExpectedStudyRegion studyRegionSave = new ProjectExpectedStudyRegion();
          studyRegionSave.setProjectExpectedStudy(projectExpectedStudy);
          studyRegionSave.setPhase(phase);

          final LocElement locElement = this.locElementManager.getLocElementById(studyRegion.getLocElement().getId());

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
    if ((projectExpectedStudy.getProjectExpectedStudySubIdos() != null)
      && (projectExpectedStudy.getProjectExpectedStudySubIdos().size() > 0)) {

      final List<ProjectExpectedStudySubIdo> subIdoPrev =
        new ArrayList<>(projectExpectedStudy.getProjectExpectedStudySubIdos().stream()
          .filter(nu -> nu.isActive() && nu.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()));

      for (final ProjectExpectedStudySubIdo studySubIdo : subIdoPrev) {
        if ((this.expectedStudy.getSubIdos() == null) || !this.expectedStudy.getSubIdos().contains(studySubIdo)) {
          this.projectExpectedStudySubIdoManager.deleteProjectExpectedStudySubIdo(studySubIdo.getId());
        }
      }
    }

    // Save form Information
    if (this.expectedStudy.getSubIdos() != null) {
      for (final ProjectExpectedStudySubIdo studySubIdo : this.expectedStudy.getSubIdos()) {
        if (studySubIdo.getId() == null) {
          final ProjectExpectedStudySubIdo studySubIdoSave = new ProjectExpectedStudySubIdo();
          studySubIdoSave.setProjectExpectedStudy(projectExpectedStudy);
          studySubIdoSave.setPhase(phase);
          studySubIdoSave.setPrimary(studySubIdo.getPrimary());

          if ((studySubIdo.getSrfSubIdo() != null) && (studySubIdo.getSrfSubIdo().getId() != null)) {
            final SrfSubIdo srfSubIdo = this.srfSubIdoManager.getSrfSubIdoById(studySubIdo.getSrfSubIdo().getId());
            studySubIdoSave.setSrfSubIdo(srfSubIdo);
          }

          if ((this.expectedStudy.getSubIdos() != null) && (this.expectedStudy.getSubIdos().size() == 1)) {
            studySubIdoSave.setPrimary(true);
          }

          this.projectExpectedStudySubIdoManager.saveProjectExpectedStudySubIdo(studySubIdoSave);
          // This is to add studySubIdoSave to generate correct
          // auditlog.
          this.expectedStudy.getProjectExpectedStudySubIdos().add(studySubIdoSave);
        } else {
          // if sub ido already exist - save primary
          ProjectExpectedStudySubIdo studySubIdoSave = new ProjectExpectedStudySubIdo();
          studySubIdoSave =
            this.projectExpectedStudySubIdoManager.getProjectExpectedStudySubIdoById(studySubIdo.getId());

          studySubIdoSave.setProjectExpectedStudy(projectExpectedStudy);
          studySubIdoSave.setPhase(phase);
          studySubIdoSave.setPrimary(studySubIdo.getPrimary());

          if ((studySubIdo.getSrfSubIdo() != null) && (studySubIdo.getSrfSubIdo().getId() != null)) {
            final SrfSubIdo srfSubIdo = this.srfSubIdoManager.getSrfSubIdoById(studySubIdo.getSrfSubIdo().getId());
            studySubIdoSave.setSrfSubIdo(srfSubIdo);
          }

          if ((this.expectedStudy.getSubIdos() != null) && (this.expectedStudy.getSubIdos().size() == 1)) {
            studySubIdoSave.setPrimary(true);
          }
          this.projectExpectedStudySubIdoManager.saveProjectExpectedStudySubIdo(studySubIdoSave);

        }
      }
    }

  }


  public void setAllianceLeverList(List<AllianceLever> allianceLeverList) {
    this.allianceLeverList = allianceLeverList;
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

  public void setCrpOutcomes(List<CrpProgramOutcome> crpOutcomes) {
    this.crpOutcomes = crpOutcomes;
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

  public void setFeedbackComments(List<FeedbackQACommentableFields> feedbackComments) {
    this.feedbackComments = feedbackComments;
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

  public void setImpactAreasList(List<ImpactArea> impactAreasList) {
    this.impactAreasList = impactAreasList;
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


  public void setPartnerPersons(List<ProjectPartnerPerson> partnerPersons) {
    this.partnerPersons = partnerPersons;
  }

  public void setPartners(List<ProjectPartner> partners) {
    this.partners = partners;
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


  public void setProjectOutcomes(List<ProjectOutcome> projectOutcomes) {
    this.projectOutcomes = projectOutcomes;
  }

  public void setQuantificationTypes(List<QuantificationType> quantificationTypes) {
    this.quantificationTypes = quantificationTypes;
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

  public void setTag(String tag) {
    this.tag = tag;
  }

  public void setTagList(List<ProjectExpectedStudyTag> tagList) {
    this.tagList = tagList;
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


  /**
   * Validate OICR tag comparing level of maturity and year changes
   * 
   * @param projectExpectedStudy
   * @param phase
   */
  public void validateOICRTag(ProjectExpectedStudy projectExpectedStudyDB, Phase phase) {


    if (this.previousTagID == 0) {
      /* New OICR: When creating a new OICR and maintaining the same "Current reporting year" */
      if ((phase != null) && (this.expectedStudy.getProjectExpectedStudyInfo() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getYear().intValue() == this.previousYear)) {
        this.expectedStudy.getProjectExpectedStudyInfo()
          .setTag(this.projectExpectedStudyTagManager.getProjectExpectedStudyTagById(1));
      }
    }

    if (this.previousTagID == 1) {
      /*
       * Updated OICR (Same level of
       * maturity)": When the reporting year of an OICR is updated without changing the "level
       * of maturity"
       */
      if ((phase != null) && (this.expectedStudy.getProjectExpectedStudyInfo(phase) != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId() != null)
        && (this.previousMaturityID == this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId()
          .intValue())
        && (this.expectedStudy.getProjectExpectedStudyInfo().getYear().intValue() != this.previousYear)) {
        this.expectedStudy.getProjectExpectedStudyInfo()
          .setTag(this.projectExpectedStudyTagManager.getProjectExpectedStudyTagById(2));
      }
    }

    if ((this.previousTagID == 1) || (this.previousTagID == 2)) {
      /*
       * Updated OICR (New level of maturity)": When the reporting year of an OICR is updated and the "level of
       * maturity"
       * have been change
       */
      if ((phase != null) && (this.expectedStudy.getProjectExpectedStudyInfo() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy() != null)
        && (this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId() != null)
        && (this.previousMaturityID != this.expectedStudy.getProjectExpectedStudyInfo().getRepIndStageStudy().getId()
          .intValue())) {
        this.expectedStudy.getProjectExpectedStudyInfo()
          .setTag(this.projectExpectedStudyTagManager.getProjectExpectedStudyTagById(3));
      }
    }
  }


}

