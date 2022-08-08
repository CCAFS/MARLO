/**
 * ***************************************************************
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
 * ***************************************************************
 */
package org.cgiar.ccafs.marlo.action;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.IAuditLog;
import org.cgiar.ccafs.marlo.data.manager.ActivityManager;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CenterOutputsOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ClusterTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterKeyOutputManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterKeyOutputOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterOfActivityManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrpOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeRuleManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.manager.ICenterCycleManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterImpactManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutputManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterSectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterTopicManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpLiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.MarloMessageManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectComponentLessonManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectDeliverableSharedManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.ActivityTitle;
import org.cgiar.ccafs.marlo.data.model.Auditlog;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.CapDevSectionEnum;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;
import org.cgiar.ccafs.marlo.data.model.CenterCycle;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableOutput;
import org.cgiar.ccafs.marlo.data.model.CenterImpact;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterOutputsOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectFundingSource;
import org.cgiar.ccafs.marlo.data.model.CenterProjectOutput;
import org.cgiar.ccafs.marlo.data.model.CenterSectionStatus;
import org.cgiar.ccafs.marlo.data.model.CenterSubmission;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.data.model.ClusterType;
import org.cgiar.ccafs.marlo.data.model.CrpCategoryEnum;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.CustomLevelSelect;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrpOutcome;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.DeliverableTypeRule;
import org.cgiar.ccafs.marlo.data.model.FileDB;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.ImpactPathwayCyclesEnum;
import org.cgiar.ccafs.marlo.data.model.ImpactPathwaySectionsEnum;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.MarloAuditableEntity;
import org.cgiar.ccafs.marlo.data.model.MarloMessage;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis2019SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectDeliverableShared;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6ContributionDeliverable;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionsEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis2018SectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.BaseSecurityContext;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.security.SessionCounter;
import org.cgiar.ccafs.marlo.security.UserToken;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.HistoryDifference;

import org.cgiar.ciat.auth.LDAPService;
import org.cgiar.ciat.auth.LDAPUser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This action aims to define general functionalities that are going to be used
 * by all other Actions.
 *
 * @author Hernán David Carvajal
 * @author Héctor Fabio Tobón R.
 * @author Christian Garcia
 */
public class BaseAction extends ActionSupport implements Preparable, SessionAware, ServletRequestAware {

  public static final String CANCEL = "cancel";

  // Loggin
  private static final Logger LOG = LoggerFactory.getLogger(BaseAction.class);

  public static final String NEXT = "next";

  public static final String NOT_AUTHORIZED = "403";

  public static final String NOT_FOUND = "404";

  public static final String NOT_LOGGED = "401";

  public static final String PRIVATE = "private";

  public static final String REDIRECT = "redirect";

  public static final String SAVED_STATUS = "savedStatus";

  private static final long serialVersionUID = -740360140511380630L;

  private List<HistoryDifference> differences;

  // Years available per CRPs (used in Summaries)
  private ArrayList<String> years;

  protected boolean add;
  private Long phaseID;

  /**
   * Use field injection in BaseAction only. Subclasses should use constructor
   * injection.
   */
  @Inject
  private AuditLogManager auditLogManager;

  @Inject
  private InstitutionManager institutionManager;

  @Inject
  private GlobalUnitTypeManager globalUnitTypeManager;
  private String basePermission;
  protected boolean cancel;
  private boolean editStatus = false; // If user is able to edit the form.
  private boolean otherUrl = false;

  private boolean canEdit; // If user is able to edit the form.

  private boolean availabePhase = true; // If user is able to edit the form.

  private boolean canEditPhase = true; // If user is able to edit the form.

  private boolean canSwitchProject; // If user is able to Switch Project.
  // (generally is a project leader)

  private boolean switchSession;
  protected APConfig config;
  @Inject
  private PhaseManager phaseManager;
  @Inject
  private ClusterTypeManager clusterTypeManager;
  private CenterOutputsOutcomeManager centerOutputsOutcomeManager;

  @Inject
  private ProjectLp6ContributionManager projectLp6ContributionManager;

  @Inject
  private CrpClusterKeyOutputManager crpClusterKeyOutputManager;

  @Inject
  private CrpClusterOfActivityManager crpClusterOfActivityManager;

  @Inject
  ActivityManager activityManager;

  @Inject
  private CrpMilestoneManager crpMilestoneManager;

  private Long crpID;
  // Managers
  @Inject
  private GlobalUnitManager crpManager;
  @Inject
  private CrpPpaPartnerManager crpPpaPartnerManager;

  @Inject
  private CrpProgramLeaderManager crpProgramLeaderManager;

  @Inject
  private CrpProgramManager crpProgramManager;

  @Inject
  private CrpProgramOutcomeManager crpProgramOutcomeManager;

  @Inject
  private CrpClusterKeyOutputOutcomeManager crpClusterKeyOutputOutcomeManager;
  @Inject
  private CustomParameterManager customParameterManager;

  @Inject
  private DeliverableTypeRuleManager deliverableTypeRuleManager;

  @Inject
  private ProjectDeliverableSharedManager projectDeliverableSharedManager;

  @Inject
  private DeliverableCrpOutcomeManager deliverableCrpOutcomeManager;

  @Inject
  private DeliverableInfoManager deliverableInfoManager;

  @Inject
  private ProjectPolicyManager projectPolicyManager;

  @Inject
  private ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;

  @Inject
  private ProjectPolicyInfoManager projectPolicyInfoManager;

  @Inject
  private ProjectInnovationInfoManager projectInnovationInfoManager;

  // Variables
  private String crpSession;

  protected boolean dataSaved;

  private GlobalUnit currentCrp;

  protected boolean delete;

  @Inject
  private DeliverableManager deliverableManager;
  @Inject
  private ProjectPolicyManager policyManager;

  private boolean draft;

  @Inject
  private SrfTargetUnitManager targetUnitManager;
  @Inject
  private LocElementTypeManager locElementTypeManager;

  @Inject
  private ProjectBudgetManager projectBudgetManager;

  @Inject
  private ProjectPartnerPersonManager partnerPersonManager;

  @Inject
  private UserManager userManager;
  @Inject
  private FileDBManager fileDBManager;
  private boolean fullEditable; // If user is able to edit all the form.

  @Inject
  private FundingSourceManager fundingSourceManager;

  private HashMap<String, String> invalidFields;

  // User actions
  private boolean isEditable; // If user is able to edit the form.

  // Justification of the changes
  private String justification;

  private boolean lessonsActive;

  @Inject
  private LiaisonUserManager liaisonUserManager;

  protected boolean next;
  private Map<String, Parameter> parameters;
  private boolean planningActive;
  private int planningYear;
  @Inject
  private ProjectComponentLessonManager projectComponentLessonManager;
  @Inject
  private ProjectManager projectManager;

  @Inject
  private ProjectOutcomeManager projectOutcomeManager;

  @Inject
  private PowbSynthesisManager powbSynthesisManager;

  @Inject
  private LiaisonInstitutionManager liaisonInstitutionManager;
  @Inject
  private DeliverableTypeManager deliverableTypeManager;
  private boolean reportingActive;

  private int reportingYear;

  protected HttpServletRequest request;

  @Inject
  private BudgetTypeManager budgetTypeManager;

  /**
   * *******************************************************
   * CENTER VARIABLES *******************************************************
   */
  @Inject
  private ICenterTopicManager topicService;

  @Inject
  private ICenterImpactManager impactService;

  @Inject
  private ICenterOutcomeManager outcomeService;
  @Inject
  private ICenterOutputManager outputService;

  @Inject
  private ICenterSectionStatusManager secctionStatusService;

  @Inject
  private ICenterCycleManager cycleService;

  @Inject
  private ICenterProjectManager projectService;

  @Inject
  private ICenterDeliverableManager deliverableService;

  @Inject
  private ICenterSectionStatusManager sectionStatusService;
  @Inject
  private ICapacityDevelopmentService capacityDevelopmentService;

  @Inject
  private ProjectExpectedStudyManager projectExpectedStudyManager;

  @Inject
  private ProjectInnovationManager projectInnovationManager;
  private String centerSession;

  private Long centerID;

  private GlobalUnit currentCenter;

  private CenterSubmission centerSubmission;

  /**
   * ******************************************************
   */
  // button actions
  protected boolean save;

  private boolean saveable; // If user is able to see the save, cancel, delete
  // buttons
  @Inject
  private SectionStatusManager sectionStatusManager;
  // Config Variables
  @Inject
  protected BaseSecurityContext securityContext;

  private Map<String, Object> session;

  private Submission submission;
  protected boolean submit;

  private String url;

  @Inject
  private UserRoleManager userRoleManager;
  @Inject
  private RoleManager roleManager;
  @Inject
  private IpProgramManager ipProgramManager;
  @Inject
  private IpLiaisonInstitutionManager ipLiaisonInstitutionManager;
  private List<Map<String, Object>> usersToActive;
  @Inject
  private GlobalUnitProjectManager globalUnitProjectManager;
  @Inject
  private ReportSynthesisManager reportSynthesisManager;
  @Inject
  private MarloMessageManager marloMessageManager;
  @Inject
  private ProjectPartnerPersonManager projectPartnerPersonManager;
  @Inject
  private ProjectPartnerManager projectPartnerManager;

  private StringBuilder validationMessage = new StringBuilder();

  private StringBuilder missingFields = new StringBuilder();
  private StringBuilder synthesisFlagships = new StringBuilder();

  public BaseAction() {
    this.saveable = true;
    this.fullEditable = true;
    this.justification = "";
  }

  public BaseAction(APConfig config) {
    this();
    this.config = config;
  }

  /* Override this method depending of the save action. */
  public String add() {
    return SUCCESS;
  }

  @Override
  public void addActionError(String anErrorMessage) {
    // TODO Auto-generated method stub
    super.addActionError(anErrorMessage);
  }

  /**
   * This function add a flag (--warn--) to the message in order to give a
   * different style to the success message using javascript once the html is
   * ready.
   *
   * @param message
   */
  public void addActionWarning(String message) {
    this.addActionMessage("--warn--" + message);
  }

  public void addMessage(String message) {
    if (!StringUtils.stripToEmpty(message).isEmpty()) {
      this.validationMessage.append("<p> - ");
      this.validationMessage.append(message);
      this.validationMessage.append("</p>");

      this.addMissingField(message);
    }
  }

  /**
   * This method add a missing field separated by a semicolon (;).
   *
   * @param field is the name of the field.
   */
  public void addMissingField(String field) {
    if (!StringUtils.stripToEmpty(field).isEmpty()) {
      if (this.missingFields.length() != 0) {
        this.missingFields.append(";");
      }
      this.missingFields.append(field);
    }
  }

  /**
   * This method add a synthesis flagship separated by a semicolon (;).
   *
   * @param field is the name of the field.
   */
  public void addSynthesisFlagship(String flagship) {
    if (this.synthesisFlagships.length() != 0) {
      this.synthesisFlagships.append(";");
    }
    this.synthesisFlagships.append(flagship);
  }

  public void addUsers() {
    if (this.usersToActive != null) {
      for (Map<String, Object> userMap : this.usersToActive) {
        User user = (User) userMap.get("user");
        /**
         * Leaving this here for now as there is strangeness as to how
         * users are set active and inactive that needs to be sorted
         * out.
         */
        user.setActive(true);
        if (!user.isCgiarUser()) {
          user.setPassword(userMap.get("password").toString());
        }

        this.userManager.saveUser(user);
      }
    }

  }

  public boolean canAccessSuperAdmin() {
    return this.securityContext.hasAllPermissions(Permission.FULL_PRIVILEGES);
  }

  /**
   * ***********************CENTER METHOD********************* return true if
   * the user can view the impactPathway
   * *********************************************************
   *
   * @return true if the user is super admin.
   */
  public boolean canAcessCenterImpactPathway() {

    String permission =
      this.generatePermissionCenter(Permission.RESEARCH_AREA_FULL_PRIVILEGES, this.getCenterSession());
    LOG.debug(permission);
    LOG.debug(String.valueOf(this.securityContext.hasPermission(permission)));
    return this.securityContext.hasPermission(permission);
  }

  public boolean canAcessCrp() {
    return this.canAcessPublications() || this.canAcessSynthesisMog();
  }

  public boolean canAcessCrpAdmin() {
    try {
      String permission = this.generatePermission(Permission.CRP_ADMIN_VISIBLE_PRIVILEGES, this.getCrpSession());
      return this.securityContext.hasPermission(permission);
    } catch (Exception e) {
      return false;
    }
  }

  public boolean canAcessFunding() {

    boolean permission =
      this.hasPermissionNoBase(this.generatePermission(Permission.PROJECT_FUNDING_, this.getCrpSession()));

    return permission;

  }

  public boolean canAcessImpactPathway() {
    String permission = this.generatePermission(Permission.IMPACT_PATHWAY_VISIBLE_PRIVILEGES, this.getCrpSession());
    return this.securityContext.hasPermission(permission);
  }

  public boolean canAcessPOWB() {
    String permission = this.generatePermission(Permission.POWB_SYNTHESIS_CAN_VIEW, this.getCrpSession());
    return this.securityContext.hasPermission(permission);
  }

  public boolean canAcessPublications() {
    String params[] = {this.getCrpSession()};
    return this.hasPermission(this.generatePermission(Permission.PUBLICATION_ADD, params));
  }

  public boolean canAcessSumaries() {
    if (this.canAcessCrpAdmin() || this.canAccessSuperAdmin()) {
      return true;
    } else {
      User u = this.getCurrentUser();
      if (u != null) {
        u = this.userManager.getUser(u.getId());
        List<Role> roles = new ArrayList<>();
        for (UserRole userRole : u.getUserRoles()) {
          roles.add(userRole.getRole());
        }
        long pmuRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
        Role rolePreview = this.roleManager.getRoleById(pmuRol);
        if (roles.contains(rolePreview)) {
          return true;
        } else {
          return false;
        }
      }
      return false;

    }
  }

  public boolean canAcessSynthesisMog() {
    String permission = this.generatePermission(Permission.SYNTHESIS_BY_MOG_PERMISSION, this.getCrpSession());
    return this.securityContext.hasPermission(permission);
  }

  public boolean canAddBilateralProject() {
    String permission = this.generatePermission(Permission.PROJECT_BILATERAL_ADD, this.getCrpSession());
    return this.securityContext.hasPermission(permission);
  }

  public boolean canAddCoreProject() {
    String permission = this.generatePermission(Permission.PROJECT_CORE_ADD, this.getCrpSession());
    return this.securityContext.hasPermission(permission);
  }

  public boolean canBeDeleted(long id, String className) {
    Class<?> clazz;
    try {
      clazz = Class.forName(className);
      /*
       * if (clazz == UserRole.class) {
       * UserRole userRole = this.userRoleManager.getUserRoleById(id);
       * long cuId = Long.parseLong((String) this.getSession().get(APConstants.CRP_CU));
       * /**
       * Optimize this to a SQL query that takes the userId and the
       * LiasionInstitutionId as parameters
       **/
      /*
       * List<LiaisonUser> liaisonUsers = this.liaisonUserManager.findAll().stream()
       * .filter(c -> c.getUser().getId().longValue() == userRole.getUser().getId().longValue()
       * && c.getLiaisonInstitution().getId().longValue() == cuId)
       * .collect(Collectors.toList());
       * for (LiaisonUser liaisonUser : liaisonUsers) {
       * if (!liaisonUser.getProjects().stream()
       * .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase()) && c.getStatus() != null
       * && (c.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
       * || c.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))
       * .collect(Collectors.toList()).isEmpty()) {
       * return false;
       * }
       * }
       * }
       **/

      if (clazz == CrpProgram.class) {
        CrpProgram crpProgram = this.crpProgramManager.getCrpProgramById(id);

        List<ProjectFocus> programs = crpProgram.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList());
        boolean deleted = true;
        if (programs.size() > 0) {

          for (ProjectFocus projectFocus : programs) {
            if (projectFocus.getProject().getProjecInfoPhase(this.getActualPhase()).getStatus() != null) {
              switch (ProjectStatusEnum
                .getValue(projectFocus.getProject().getProjecInfoPhase(this.getActualPhase()).getStatus().intValue())) {
                case Ongoing:
                case Extended:
                  deleted = false;
                  break;

              }
            }
          }
          return deleted;
        }
      }
      if (clazz == CrpProgramLeader.class) {
        /*
         * CrpProgramLeader crpProgramLeader = this.crpProgramLeaderManager.getCrpProgramLeaderById(id);
         * for (LiaisonUser liaisonUser : crpProgramLeader.getUser().getLiasonsUsers().stream()
         * .filter(c -> c.getLiaisonInstitution().getCrpProgram() != null && c.getLiaisonInstitution().getCrpProgram()
         * .getId().longValue() == crpProgramLeader.getCrpProgram().getId().longValue())
         * .collect(Collectors.toList())) {
         * List<ProjectInfo> projects = liaisonUser.getProjects().stream()
         * .filter(c -> c.isActive() && c.getPhase().getId().equals(this.getActualPhase().getId()))
         * .collect(Collectors.toList());
         * boolean deleted = true;
         * if (projects.size() > 0) {
         * for (ProjectInfo projectInfo : projects) {
         * Project project = projectInfo.getProject();
         * if (project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getCrpProgram().getId()
         * .equals(crpProgramLeader.getCrpProgram().getId())) {
         * if (project.getProjecInfoPhase(this.getActualPhase()).getStatus() != null) {
         * switch (ProjectStatusEnum
         * .getValue(project.getProjecInfoPhase(this.getActualPhase()).getStatus().intValue())) {
         * case Ongoing:
         * case Extended:
         * deleted = false;
         * break;
         * }
         * }
         * }
         * }
         * return deleted;
         * }
         * }
         */

      }

      if (clazz == FundingSource.class) {
        FundingSource fundingSource = this.fundingSourceManager.getFundingSourceById(id);
        if (this.isFundingSourceNew(fundingSource.getId())) {
          if (fundingSource.getProjectBudgets().stream()
            .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
            .collect(Collectors.toList()).size() > 0) {
            return false;
          }
          return true;
        } else {
          return false;
        }

      }

      if (clazz == CrpPpaPartner.class) {
        CrpPpaPartner crpPpaPartner = this.crpPpaPartnerManager.getCrpPpaPartnerById(id);
        List<Project> partners = this.getProjectRelationsImpact(id, CrpPpaPartner.class.getName());
        if (partners.size() > 0) {
          return false;
        }
      }

      if (clazz == SrfTargetUnit.class) {
        SrfTargetUnit targetUnit = this.targetUnitManager.getSrfTargetUnitById(id);

        if (targetUnit == null) {
          return true;
        }

        if (targetUnit.getCrpProgramOutcomes().stream()
          .filter(o -> o.isActive() && o.getCrpProgram().getCrp().getId().equals(this.getCrpID()))
          .collect(Collectors.toList()).size() > 0) {
          return false;
        }

        if (targetUnit.getCrpMilestones().stream()
          .filter(
            u -> u.isActive() && u.getCrpProgramOutcome().getCrpProgram().getCrp().getId().equals(this.getCrpID()))
          .collect(Collectors.toList()).size() > 0) {
          return false;
        }
      }

      if (clazz == LocElementType.class) {
        LocElementType locElementType = this.locElementTypeManager.getLocElementTypeById(id);
        if (locElementType.getCrpLocElementTypes().stream().filter(o -> o.isActive()).collect(Collectors.toList())
          .size() > 0) {
          return false;
        }

      }

      if (clazz == ProjectPartnerPerson.class) {

        ProjectPartnerPerson partnerPerson = this.partnerPersonManager.getProjectPartnerPersonById(id);

        List<Deliverable> deliverablesLeads = new ArrayList<>();

        if (partnerPerson != null) {

          long projectId = partnerPerson.getProjectPartner().getProject().getId();
          long userId = partnerPerson.getUser().getId();

          List<Deliverable> deliverables =
            deliverableManager.getDeliverablesLeadByUser(userId, this.getActualPhase().getId());
          if (deliverables != null) {
            for (Deliverable deliverable : deliverables) {
              if (deliverable.getProject() != null && deliverable.getProject().getId().equals(projectId)) {
                deliverable.setDeliverableInfo(deliverable.getDeliverableInfo(this.getActualPhase()));
                if (deliverable.getDeliverableInfo() != null && deliverable.getDeliverableInfo().getStatus() != null
                  && (deliverable.getDeliverableInfo().getStatus() == Integer
                    .parseInt(ProjectStatusEnum.Extended.getStatusId())
                    || deliverable.getDeliverableInfo().getStatus() == Integer
                      .parseInt(ProjectStatusEnum.Ongoing.getStatusId()))) {
                  if (!deliverablesLeads.contains(deliverable)) {
                    if (deliverable.getDeliverableInfo().getYear() >= this.getActualPhase().getYear()) {

                      if (deliverable.isActive()) {
                        deliverablesLeads.add(deliverable);
                      }

                    } else {
                      if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
                        .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                        if (deliverable.getDeliverableInfo().getNewExpectedYear() != null
                          && deliverable.getDeliverableInfo().getNewExpectedYear() >= this.getActualPhase().getYear()) {
                          if (deliverable.isActive()) {
                            deliverablesLeads.add(deliverable);
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        if (!deliverablesLeads.isEmpty()) {
          return false;
        }

      }

      if (clazz == ProjectBudget.class) {

        ProjectBudget projectBudget = this.projectBudgetManager.getProjectBudgetById(id);
        FundingSource fundingSource =
          this.fundingSourceManager.getFundingSourceById(projectBudget.getFundingSource().getId());
        List<DeliverableFundingSource> deliverableFundingSources = fundingSource.getDeliverableFundingSources().stream()
          .filter(c -> c.isActive() && c.getDeliverable().isActive() && c.getPhase() != null
            && c.getPhase().getYear() == projectBudget.getYear() && c.getDeliverable().getProject() != null
            && c.getDeliverable().getProject().getId().longValue() == projectBudget.getProject().getId().longValue())
          .collect(Collectors.toList());

        List<Deliverable> onDeliverables =
          this.getDeliverableRelationsProject(id, ProjectBudget.class.getName(), projectBudget.getProject().getId());
        if (!onDeliverables.isEmpty()) {
          return false;
        }
      }
      if (clazz == CustomLevelSelect.class) {
        LocElementType locElementType = this.locElementTypeManager.getLocElementTypeById(id);

        for (LocElement locElements : locElementType.getLocElements().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          if (!locElements.getProjectLocations().stream()
            .filter(c -> c.isActive() && c.getProject().getGlobalUnitProjects().stream()
              .filter(gup -> gup.isActive() && gup.getGlobalUnit().getId().equals(this.getCrpID().longValue()))
              .collect(Collectors.toList()).size() > 0)
            .collect(Collectors.toList()).isEmpty()) {
            return false;
          }

        }

      }
      if (clazz == CrpProgramOutcome.class || clazz == CrpClusterOfActivity.class || clazz == CrpMilestone.class) {
        List<Project> projects = this.getProjectRelationsImpact(id, className);
        if (!projects.isEmpty()) {
          return false;
        }

      }
      if (clazz == CrpClusterKeyOutput.class) {

        List<Deliverable> projects = this.getDeliverableRelationsImpact(id, className);
        if (!projects.isEmpty()) {
          return false;
        }
        CrpClusterKeyOutput crpClusterKeyOutput = this.crpClusterKeyOutputManager.getCrpClusterKeyOutputById(id);
        if (crpClusterKeyOutput.getCrpClusterKeyOutputOutcomes().stream().filter(c -> c.isActive())
          .collect(Collectors.toList()).size() > 0) {
          return false;
        }
      }
      if (clazz == ProjectOutcome.class) {
        if (this.isAiccra()) {
          boolean canDelete = true;
          ProjectOutcome projectOutcome = this.projectOutcomeManager.getProjectOutcomeById(id);

          for (Deliverable deliverable : projectOutcome.getProject().getCurrentDeliverables(this.getActualPhase())) {
            if (deliverable.getDeliverableProjectOutcomes() != null) {
              deliverable.setProjectOutcomes(new ArrayList<>(deliverable.getDeliverableProjectOutcomes().stream()
                .filter(o -> o.getPhase().getId().equals(this.getActualPhase().getId())).collect(Collectors.toList())));
            }
            if (deliverable != null && deliverable.getProjectOutcomes() != null
              && !deliverable.getProjectOutcomes().isEmpty()) {
              if (deliverable != null && deliverable.getProjectOutcomes() != null
                && !deliverable.getProjectOutcomes().isEmpty()) {
                for (DeliverableProjectOutcome deliverableProjectOutcome : deliverable.getProjectOutcomes()) {
                  if (deliverableProjectOutcome != null && deliverableProjectOutcome.getProjectOutcome() != null
                    && deliverableProjectOutcome.getProjectOutcome().getId() != null
                    && deliverableProjectOutcome.getProjectOutcome().getId().compareTo(projectOutcome.getId()) == 0) {

                    canDelete = false;
                    break;
                  }
                }
              }
            }
          }


          try {
            for (ProjectExpectedStudy expectedStudy : projectOutcome.getProject().getProjectExpectedStudies().stream()
              .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(this.getActualPhase()) != null
                && ps.getProjectExpectedStudyInfo(this.getActualPhase()).isActive())
              .collect(Collectors.toList())) {
              if (expectedStudy.getProjectExpectedStudyProjectOutcomes() != null) {
                expectedStudy.setProjectOutcomes(new ArrayList<>(expectedStudy.getProjectExpectedStudyProjectOutcomes()
                  .stream().filter(o -> o.getPhase().getId().equals(this.getActualPhase().getId()))
                  .collect(Collectors.toList())));
              }
              if (expectedStudy != null && expectedStudy.getProjectOutcomes() != null
                && !expectedStudy.getProjectOutcomes().isEmpty()) {
                for (ProjectExpectedStudyProjectOutcome expectedStudyProjectOutcome : expectedStudy
                  .getProjectOutcomes()) {
                  if (expectedStudyProjectOutcome != null && expectedStudyProjectOutcome.getProjectOutcome() != null
                    && expectedStudyProjectOutcome.getProjectOutcome().getId() != null
                    && expectedStudyProjectOutcome.getProjectOutcome().getId().compareTo(projectOutcome.getId()) == 0) {
                    canDelete = false;
                    break;
                  }
                }
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }

          try {
            for (ProjectInnovation innovation : projectOutcome.getProject().getProjectInnovations().stream()
              .filter(ps -> ps.isActive() && ps.getProjectInnovationInfo(this.getActualPhase()) != null
                && ps.getProjectInnovationInfo(this.getActualPhase()).isActive())
              .collect(Collectors.toList())) {
              if (innovation.getProjectInnovationProjectOutcomes() != null) {
                innovation.setProjectOutcomes(new ArrayList<>(innovation.getProjectInnovationProjectOutcomes().stream()
                  .filter(o -> o.getPhase().getId().equals(this.getActualPhase().getId()))
                  .collect(Collectors.toList())));
              }
              if (innovation != null && innovation.getProjectOutcomes() != null
                && !innovation.getProjectOutcomes().isEmpty()) {
                for (ProjectInnovationProjectOutcome innovationStudyProjectOutcome : innovation.getProjectOutcomes()) {
                  if (innovationStudyProjectOutcome != null && innovationStudyProjectOutcome.getProjectOutcome() != null
                    && innovationStudyProjectOutcome.getProjectOutcome().getId() != null
                    && innovationStudyProjectOutcome.getProjectOutcome().getId()
                      .compareTo(projectOutcome.getId()) == 0) {
                    canDelete = false;
                    break;
                  }
                }
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }

          return canDelete;
        } else {

          ProjectOutcome projectOutcome = this.projectOutcomeManager.getProjectOutcomeById(id);
          List<CrpProgramOutcome> crpProgramOutcomes = new ArrayList<>();
          List<ProjectOutcome> projectOutcomes = projectOutcome.getProject().getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase()) && c.getPhase() != null)
            .collect(Collectors.toList());
          for (ProjectOutcome mProjectOutcome : projectOutcomes) {
            crpProgramOutcomes.add(
              this.crpProgramOutcomeManager.getCrpProgramOutcomeById(mProjectOutcome.getCrpProgramOutcome().getId()));
          }
          boolean canDelete = false;
          List<Deliverable> projects =
            this.getDeliverableRelationsProject(id, className, projectOutcome.getProject().getId());
          if (!projects.isEmpty()) {
            for (Deliverable deliverable : projects) {
              CrpClusterKeyOutput clusterKeyOutput =
                deliverable.getDeliverableInfo(this.getActualPhase()).getCrpClusterKeyOutput();
              List<CrpClusterKeyOutputOutcome> clusterKeyOutputOutcomes = clusterKeyOutput
                .getCrpClusterKeyOutputOutcomes().stream().filter(c -> c.isActive()).collect(Collectors.toList());
              for (CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome : clusterKeyOutputOutcomes) {
                if (!crpClusterKeyOutputOutcome.getCrpProgramOutcome().equals(projectOutcome.getCrpProgramOutcome())) {
                  if (crpProgramOutcomes.contains(crpClusterKeyOutputOutcome.getCrpProgramOutcome())) {
                    canDelete = true;
                  }

                }
              }

            }

            return canDelete;
          } else {
            canDelete = true;
          }
        }
      }

      if (clazz == ActivityTitle.class) {
        List<Project> projects = new ArrayList<>();
        projects =
          projectManager.getActiveProjectsByPhase(this.getActualPhase(), this.getActualPhase().getYear(), null);

        if (projects != null && !projects.isEmpty()) {
          for (Project project : projects) {
            project.setProjectActivities(
              project.getActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
            if (project.getProjectActivities() != null) {
              for (Activity activity : project.getProjectActivities()) {
                if (activity != null && activity.getActivityTitle() != null
                  && activity.getActivityTitle().getId() != null) {
                  if (activity.getActivityTitle().getId().equals(id)) {
                    return false;
                  }
                }
              }
            }
          }
        }
        return true;
      }

      if (clazz == CrpClusterKeyOutputOutcome.class) {

        CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome =
          this.crpClusterKeyOutputOutcomeManager.getCrpClusterKeyOutputOutcomeById(id);

        List<Project> projects = this.getProjectRelationsImpact(id, className);
        if (!projects.isEmpty()) {

          return false;
        }
      }
      return true;
    } catch (Exception e) {

      return true;
    }

  }

  /* Override this method depending of the cancel action. */
  public String cancel() {
    return CANCEL;
  }

  public boolean canDeleteActivity(long activityTitleID) {
    boolean canDelete = true;
    List<Activity> activities = new ArrayList<>();
    activities = activityManager.findAll().stream()
      .filter(a -> a.getPhase().getId().equals(this.getActualPhase().getId()) && a.getActivityTitle() != null
        && a.getActivityTitle().getId() != null && a.getActivityTitle().getId().equals(activityTitleID))
      .collect(Collectors.toList());
    if (activities != null && !activities.isEmpty()) {
      canDelete = false;
    }
    return canDelete;
  }

  /**
   * Make the validation for CRP Admin, PMU or Finance Manager role to
   * determinate if a Funding source can be duplicated.
   *
   * @return boolean with true o false permission to duplicate FS.
   */
  public boolean canDuplicateFunding() {
    boolean canDuplicate = false;
    String roles = this.getRoles();
    if (roles != null && !roles.isEmpty() && (roles.contains("CRP-Admin") || roles.contains("PMU")
      || roles.contains("FM") || roles.contains("SuperAdmin"))) {
      canDuplicate = true;
    } else {
      canDuplicate = false;
    }
    return canDuplicate;
  }

  /**
   * Verify the permissions to edit any project budget execution in the
   * project
   */
  public boolean canEditAnyProjectExecution(long projectID) {
    String actionName = this.getActionName();

    if (!actionName.contains(ProjectSectionStatusEnum.BUDGET.getStatus())) {
      return false;
    }

    if (!this.hasSpecificities(this.getCrpEnableBudgetExecution())) {
      return false;
    }

    if (this.isPlanningActive()) {
      return false;
    }

    Project project = projectManager.getProjectById(projectID);
    project.setPartners(project.getProjectPartners().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
      .collect(Collectors.toList()));
    List<ProjectPartner> projectPPAPartners = new ArrayList<ProjectPartner>();
    if (project.getPartners() != null && !project.getPartners().isEmpty()) {
      for (ProjectPartner pp : project.getPartners()) {

        if (pp.getInstitution().getId() != null) {
          Institution institution = this.institutionManager.getInstitutionById(pp.getInstitution().getId());
          if (institution != null) {
            if (institution
              .getCrpPpaPartners().stream().filter(c -> c.getCrp().getId().longValue() == this.getCrpID()
                && c.isActive() && c.getPhase().equals(this.getActualPhase()))
              .collect(Collectors.toList()).size() > 0) {
              projectPPAPartners.add(pp);
            }
          }
        }
      }
    }

    List<BudgetType> budgetTypes = budgetTypeManager.findAll().stream().collect(Collectors.toList());
    if (budgetTypes != null && !budgetTypes.isEmpty()) {
      for (BudgetType budgetType : budgetTypes) {
        for (ProjectPartner projectPartner : projectPPAPartners) {
          if (this.canEditProjectExecution(budgetType.getId(), projectID, projectPartner.getInstitution().getId())) {
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * Verify if the project have Cluster of Activity to activate Budget by CoA
   *
   * @return true if the project have CoA or false otherwise.
   */
  public Boolean canEditBudgetByCoAs(long projectID) {
    Project project = this.projectManager.getProjectById(projectID);
    if (this.hasSpecificities(this.getCrpEnableBudgetByCoas())) {
      if (project.getProjectClusterActivities().stream()
        .filter(pc -> pc.isActive() && pc.getPhase().equals(this.getActualPhase()))
        .collect(Collectors.toList()) == null) {
        return false;
      }
      if (project.getProjectClusterActivities().stream()
        .filter(pc -> pc.isActive() && pc.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())
        .size() > 1) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }

  }

  public boolean canEditCenterType() {
    return this.hasPermissionNoBase(
      this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, this.getCrpSession()));
  }

  public boolean canEditCrpAdmin() {
    String permission = this.generatePermission(Permission.CRP_ADMIN_EDIT_PRIVILEGES, this.getCrpSession());
    return this.securityContext.hasPermission(permission);
  }

  /**
   * Verify permission to edit project budget execution for an specific
   * project, budget type and/or institution
   */
  public boolean canEditProjectExecution(long budgetTypeID, long projectID, long institutionID) {
    String actionName = this.getActionName();

    if (!actionName.contains(ProjectSectionStatusEnum.BUDGET.getStatus())) {
      return false;
    }

    if (!this.hasSpecificities(this.getCrpEnableBudgetExecution())) {
      return false;
    }

    if (this.isPlanningActive()) {
      return false;
    }

    // Validation to PMU (Due to full permissions grant in projects): This shouldn't
    // be done but, in this case we need
    // to avoid editing this field by the PMU
    if (this.isRole("PMU") && !this.isRole("FM")) {
      return false;
    }
    if (this.getActualPhase().getVisible() && this.getActualPhase().getDescription().equals(APConstants.REPORTING)
      && this.isRole("FM")) {
      return true;
    } else {
      String params[] = {this.crpManager.getGlobalUnitById(this.getCrpID()).getAcronym(), projectID + "",
        budgetTypeID + "", institutionID + ""};
      Boolean canEditBudget =
        this.hasPermission(this.generatePermission(Permission.PROJECT_BUDGET_EXECUTION_BASE_PERMISSION, params));
      Boolean canEditBudgetLiaison =
        this.hasPermission(this.generatePermission(Permission.PROJECT_BUDGET_EXECUTION_LIAISON_PERMISSION, params));

      if (canEditBudget || canEditBudgetLiaison) {
        return true;
      }
    }

    return false;
  }

  public boolean canLeaveComments() {
    boolean response = false;

    // TODO: Update the permissions for manage feedback comments
    if (this.canAccessSuperAdmin()) {
      response = true;
    }

    if (this.getRolesList() != null && !this.getRolesList().isEmpty()) {
      for (Role role : this.getRolesList()) {
        if (role != null && role.getAcronym() != null) {
          // FPL & FPM roles can comment

          if (role.getAcronym().equals("FPL") || role.getAcronym().equals("FPM") || role.getAcronym().equals("RPL")
            || role.getAcronym().equals("RPM")) {
            response = true;
          }
        }
      }
    }
    return response;
  }

  public boolean canManageFeedback(Long projectID) {
    boolean response = false;

    // TODO: Update the permissions for manage feedback comments
    if (this.canAccessSuperAdmin()) {
      response = true;
    }

    if (this.getRolesList() != null && !this.getRolesList().isEmpty()) {
      for (Role role : this.getRolesList()) {
        if (role != null && role.getAcronym() != null) {
          // FPL & FPM roles can comment

          if (role.getAcronym().equals("PL") || role.getAcronym().equals("PC")) {
            response = true;
          }
        }
      }
    }

    if (projectID != null && response) {
      try {
        List<ProjectPartner> projectPartners = projectPartnerManager
          .getProjectPartnersForProjectWithActiveProjectPhasePartnerPersons(projectID, this.getActualPhase().getId());
        List<ProjectPartnerPerson> projectParnerPersons = new ArrayList<>();
        if (projectPartners != null) {
          for (ProjectPartner projectPartner : projectPartners) {
            if (projectPartner != null && projectPartner.getId() != null) {
              projectParnerPersons = projectPartnerPersonManager.findAllActiveForProjectPartner(projectPartner.getId());

              if (projectParnerPersons != null) {
                projectParnerPersons = projectParnerPersons.stream()
                  .filter(pp -> pp != null && pp.getUser() != null && pp.getUser().getId() != null
                    && this.getCurrentUser() != null && pp.getUser().getId().equals(this.getCurrentUser().getId()))
                  .collect(Collectors.toList());

                if (projectParnerPersons != null) {
                  for (ProjectPartnerPerson projectParnerPerson : projectParnerPersons) {
                    if (projectParnerPerson != null && projectParnerPerson.getContactType() != null
                      && (projectParnerPerson.getContactType().equals("PL")
                        && projectParnerPerson.getContactType().equals("PC"))) {

                    }
                  }
                }
              }
            }
          }
        }
      } catch (Exception e) {
        LOG.error("Error getting project partners ", e);
      }
    }
    return response;
  }

  public boolean canModifiedProjectStatus() {
    String actionName = this.getActionName();
    if (actionName.contains(ProjectSectionStatusEnum.DESCRIPTION.getStatus()) && this.hasPermission("statusDescription")
      && this.getActualPhase().getEditable()) {
      return true;
    }
    return false;
  }

  public boolean canProjectSubmited(long projectID) {
    String params[] = {this.crpManager.getGlobalUnitById(this.getCrpID()).getAcronym(), projectID + ""};
    return this.hasPermission(this.generatePermission(Permission.PROJECT_SUBMISSION_PERMISSION, params));
  }

  /**
   ************************ CENTER METHOD ********************* return true
   * if the user can view the impactPathway
   * ********************************************************* Verify if the
   * class model name have not relations for enable the delete button.
   *
   * @param id - the id of the model
   * @param className - the model class name
   * @return true for enabling the delete button or false to disable the
   *         delete button.
   */
  public boolean centerCanBeDeleted(long id, String className) {
    Class clazz;
    try {

      clazz = Class.forName(className);
      // Verify CenterTopic Model
      if (clazz == CenterTopic.class) {
        CenterTopic topic = this.topicService.getResearchTopicById(id);

        List<CenterOutcome> outcomes = new ArrayList<>(
          topic.getResearchOutcomes().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));

        if (outcomes != null) {
          if (!outcomes.isEmpty()) {
            return false;
          }
        }
      }

      // Verify CenterImpact Model
      if (clazz == CenterImpact.class) {
        CenterImpact impact = this.impactService.getResearchImpactById(id);

        List<CenterOutcome> outcomes = new ArrayList<>(
          impact.getResearchOutcomes().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));

        if (outcomes != null) {
          if (!outcomes.isEmpty()) {
            return false;
          }
        }
      }

      // Verify CenterOutcome Model
      if (clazz == CenterOutcome.class) {
        CenterOutcome outcome = this.outcomeService.getResearchOutcomeById(id);

        List<CenterOutput> outputs = new ArrayList<>();
        List<CenterOutputsOutcome> centerOutputsOutcomes = new ArrayList<>(
          outcome.getCenterOutputsOutcomes().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));
        for (CenterOutputsOutcome centerOutputsOutcome : centerOutputsOutcomes) {
          outputs.add(centerOutputsOutcome.getCenterOutput());
        }

        if (outputs != null) {
          if (!outputs.isEmpty()) {
            return false;
          }
        }
      }

      // Verify CenterProject Model
      if (clazz == CenterProject.class) {
        CenterProject project = this.projectService.getCenterProjectById(id);

        List<CenterDeliverable> deliverables =
          new ArrayList<>(project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList()));

        if (deliverables != null) {
          if (!deliverables.isEmpty()) {
            return false;
          }
        }
      }

      // Verify CenterOutput Model
      if (clazz == CenterOutput.class) {

        CenterOutput output = this.outputService.getResearchOutputById(id);

        List<CenterProjectOutput> projectOutputs =
          new ArrayList<>(output.getProjectOutputs().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));

        if (projectOutputs != null) {
          if (!projectOutputs.isEmpty()) {
            return false;
          }
        }
      }

      // Verify CenterDeliverable Model
      if (clazz == CenterDeliverable.class) {

        CenterDeliverable deliverable = this.deliverableService.getDeliverableById(id);

        List<CenterDeliverableOutput> deliverableOutputs = new ArrayList<>(
          deliverable.getDeliverableOutputs().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));

        if (deliverableOutputs != null) {
          if (!deliverableOutputs.isEmpty()) {
            return false;
          }
        }
      }

      // Verify CapacityDevelopment Model
      if (clazz == CapacityDevelopment.class) {

        final CapacityDevelopment capdev = this.capacityDevelopmentService.getCapacityDevelopmentById(id);

        if (capdev != null) {
          if (!capdev.getCreatedBy().getId().equals(this.getCurrentUser().getId()) && !this.canAccessSuperAdmin()) {
            return false;
          }
        }
      }

      return true;
    } catch (Exception e) {
      return false;
    }

  }

  /**
   * ***********************CENTER METHOD******************** Check if the
   * capDev section is Active
   * ************************************************************
   *
   * @return true if the section is Active.
   */
  public boolean centerCapDevActive() {
    try {
      final boolean sectionActive =
        Boolean.parseBoolean(this.getSession().get(APConstants.CRP_CAP_DEV_ACTIVE).toString());
      return sectionActive;
    } catch (final Exception e) {
      return false;
    }

  }

  /**
   * ***********************CENTER METHOD******************** Check if the
   * Impact Pathway section is Active
   * ************************************************************
   *
   * @return true if the section is Active.
   */
  public boolean centerImpactPathwayActive() {
    try {
      boolean sectionActive =
        Boolean.parseBoolean(this.getSession().get(APConstants.CENTER_IMPACT_PATHWAY_ACTIVE).toString());
      return sectionActive;
    } catch (Exception e) {
      return false;
    }

  }

  /**
   * ***********************CENTER METHOD******************** Check if the
   * Monitoring section is Active
   * ************************************************************
   *
   * @return true if the section is Active.
   */
  public boolean centerMonitoringActive() {
    try {
      boolean sectionActive =
        Boolean.parseBoolean(this.getSession().get(APConstants.CENTER_MONITORING_ACTIVE).toString());
      return sectionActive;
    } catch (Exception e) {
      return false;
    }

  }

  /**
   * ***********************CENTER METHOD******************** Check if the
   * Monitoring Outcomes section is Active
   * ************************************************************
   *
   * @return true if the section is Active.
   */
  public boolean centerMonitoringOutcomeActive() {
    try {
      boolean sectionActive =
        Boolean.parseBoolean(this.getSession().get(APConstants.CENTER_MONITORING_OUTCOME_ACTIVE).toString());
      return sectionActive;
    } catch (Exception e) {
      return false;
    }

  }

  /**
   * ***********************CENTER METHOD******************** Check if the
   * Summaries section is Active
   * ************************************************************
   *
   * @return true if the section is Active.
   */
  public boolean centerSummariesActive() {
    try {
      boolean sectionActive =
        Boolean.parseBoolean(this.getSession().get(APConstants.CENTER_SUMMARIES_ACTIVE).toString());
      return sectionActive;
    } catch (Exception e) {
      return false;
    }

  }

  public HistoryDifference changedField(String field) {

    if (this.differences != null) {
      if (this.differences.contains(new HistoryDifference(field))) {
        int index = this.differences.indexOf(new HistoryDifference(field));
        HistoryDifference historyDifference = this.differences.get(index);
        historyDifference.setIndex(index);
        return historyDifference;
      }
    }
    return null;

  }

  /**
   * This method clears the cache and re-load the user permissions in the next
   * iteration.
   */
  public void clearPermissionsCache() {
    ((APCustomRealm) this.securityContext.getRealm())
      .clearCachedAuthorizationInfo(this.securityContext.getSubject().getPrincipals());
  }

  /**
   * Create a liaison institution POWB Synthesis in this phase
   *
   * @param phase
   * @return PowbSynthesis object
   */
  public PowbSynthesis createPowbSynthesis(long phaseID, long liaisonInstitutionID) {
    LiaisonInstitution liaisonInstitution =
      this.liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);
    Phase phase = this.phaseManager.getPhaseById(phaseID);

    PowbSynthesis synthesis = new PowbSynthesis();

    synthesis.setPhase(phase);
    synthesis.setLiaisonInstitution(liaisonInstitution);

    synthesis = this.powbSynthesisManager.savePowbSynthesis(synthesis);

    this.clearPermissionsCache();

    return synthesis;
  }

  /**
   * Create a liaison institution Annual Report Synthesis in this phase
   *
   * @param phase
   * @return ReportSynthesis object
   */
  public ReportSynthesis createReportSynthesis(long phaseID, long liaisonInstitutionID) {

    LiaisonInstitution liaisonInstitution =
      this.liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);
    Phase phase = this.phaseManager.getPhaseById(phaseID);

    ReportSynthesis synthesis = new ReportSynthesis();
    synthesis.setPhase(phase);
    synthesis.setLiaisonInstitution(liaisonInstitution);

    synthesis = this.reportSynthesisManager.saveReportSynthesis(synthesis);

    this.clearPermissionsCache();

    return synthesis;

  }

  public String crpActivitesModule() {
    return APConstants.CRP_ACTIVITES_MODULE;
  }

  public String crpDeliverableIntellectualAsset() {
    return APConstants.CRP_DELIVERABLES_INTELLECTUAL_ASSET;
  }

  public String crpLocationCsvActivities() {
    return APConstants.CRP_LOCATION_CSV_ACTIVITIES;
  }

  /*
   * View Project Highligths section
   */
  public String crpViewHighligths() {
    return APConstants.CRP_VIEW_HIGHLIGTHS;
  }

  /* Override this method depending of the delete action. */
  public String delete() {
    return SUCCESS;
  }

  @Override
  public String execute() throws Exception {
    if (this.save) {

      return this.save();
    } else if (this.delete) {
      return this.delete();
    } else if (this.cancel) {
      return this.cancel();
    } else if (this.next) {
      return this.next();
    } else if (this.add) {
      return this.add();
    } else if (this.submit) {
      return this.submit();
    }
    return INPUT;
  }

  public String feedbackModule() {
    return APConstants.FEEDBACK_ACTIVE;
  }

  /**
   * ***********************CENTER METHOD******************** This method
   * calculates all the years between the start date and the end date.
   * ************************************************************
   *
   * @return a List of numbers representing all the years, or an empty list if
   *         nothing found.
   */
  public List<Integer> geCentertOutcomeYears(int outcomeYear) {
    List<Integer> allYears = new ArrayList<>();

    Calendar calendarStart = Calendar.getInstance();
    calendarStart.set(Calendar.YEAR, 2017);
    Calendar calendarEnd = Calendar.getInstance();
    calendarEnd.set(Calendar.YEAR, outcomeYear);

    while (calendarStart.get(Calendar.YEAR) <= calendarEnd.get(Calendar.YEAR)) {
      // Adding the year to the list.
      allYears.add(calendarStart.get(Calendar.YEAR));
      // Adding a year (365 days) to the start date.
      calendarStart.add(Calendar.YEAR, 1);
    }

    return allYears;
  }

  /*
   * public String generatePermission(String permission, Map<String, Object>
   * session, long crpID, String... params) { Phase phase =
   * this.getActualPhase(session, crpID); String paramsRefactor[] =
   * Arrays.copyOf(params, params.length); paramsRefactor[0] = paramsRefactor[0] +
   * ":" + phase.getDescription() + ":" + phase.getYear(); return
   * this.getText(permission, paramsRefactor); }
   */
  public String generatePermission(String permission, String... params) {
    Phase phase = this.getActualPhase();
    // TODO global unit.
    if (phase != null && phase.getId() != null) {

      String paramsRefactor[] = Arrays.copyOf(params, params.length);
      paramsRefactor[0] = paramsRefactor[0] + ":" + phase.getDescription() + ":" + phase.getYear();
      return this.getText(permission, paramsRefactor);
    } else {

      String paramsRefactor[] = Arrays.copyOf(params, params.length);

      return this.getText(permission, paramsRefactor);
    }

  }

  /**
   * ************************ CENTER METHOD ******************************
   * Generate permissions String to Centers
   * ********************************************************************
   *
   * @return a permission String
   */
  public String generatePermissionCenter(String permission, String... params) {
    return this.getText(permission, params);

  }

  public String getActionName() {
    return ServletActionContext.getActionMapping().getName();
  }

  public String getActionNameSimple() {
    return ServletActionContext.getActionMapping().getName().split("/")[1];
  }

  /**
   * get the actual
   *
   * @return the actual phase of the crp
   */
  public Phase getActualPhase() {
    try {
      Map<Long, Phase> allPhases = null;
      if (this.getSession() != null && !this.getSession().isEmpty()) {
        if (!this.getSession().containsKey(APConstants.ALL_PHASES)) {
          List<Phase> phases = this.phaseManager.findAll().stream()
            .filter(c -> c.getCrp().getId().longValue() == this.getCrpID().longValue()).collect(Collectors.toList());
          phases.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
          Map<Long, Phase> allPhasesMap = new HashMap<>();
          for (Phase phase : phases) {
            allPhasesMap.put(phase.getId(), phase);
          }
          this.getSession().put(APConstants.ALL_PHASES, allPhasesMap);
        }

      }
      /**
       * This throws a null pointer exception if invoked from a struts2
       * interceptor as the session has not been set on the BaseAction.
       * I've made the RequireUserInterceptor set the session on the
       * baseAction now but this seems a little hacky.
       */
      allPhases = (Map<Long, Phase>) this.getSession().get(APConstants.ALL_PHASES);

      Long phaseID = this.getPhaseID();
      if (phaseID != null && phaseID != 0L) {
        Phase phase = null;
        if (allPhases != null) {
          phase = allPhases.get(new Long(phaseID));
        }
        if (phase == null) {
          phase = this.phaseManager.getPhaseById(phaseID);
        }
        return phase;
      }

      Map<String, Parameter> parameters = this.getParameters();
      if (parameters != null && parameters.containsKey(APConstants.PHASE_ID)) {
        Phase phase;
        try {
          phaseID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
          phase = allPhases.get(new Long(phaseID));
          return phase;
        } catch (Exception e) {
          phase = this.phaseManager.getPhaseById(this.getCurrentPhaseParam());
        }

        if (phase != null) {
          this.getSession().put(APConstants.CURRENT_PHASE, phase);
          return phase;
        } else {
          return new Phase(null, "", -1);
        }

      }

      if (this.getSession().containsKey(APConstants.CURRENT_PHASE)) {
        Phase phase = (Phase) this.getSession().get(APConstants.CURRENT_PHASE);
        if (phase.getId() != null) {
          phase = this.phaseManager.getPhaseById(phase.getId());
        } else {
          phase = this.phaseManager.getPhaseById(this.getCurrentPhaseParam());
        }
        return phase;

      } else {

        Phase phase = this.phaseManager.getPhaseById(this.getCurrentPhaseParam());

        if (phase != null) {
          this.getSession().put(APConstants.CURRENT_PHASE, phase);
          return phase;
        } else {
          return new Phase(null, "", -1);
        }

      }

    } catch (Exception e) {
      return new Phase(null, "", -1);
    }

  }

  public Phase getActualPhase(Map<String, Object> session, long crpID) {

    try {
      Map<Long, Phase> allPhases = null;
      if (session != null) {
        if (session.containsKey(APConstants.ALL_PHASES)) {
          List<Phase> phases = this.phaseManager.findAll().stream()
            .filter(c -> c.getCrp().getId().longValue() == this.getCrpID().longValue()).collect(Collectors.toList());
          phases.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
          Map<Long, Phase> allPhasesMap = new HashMap<>();
          for (Phase phase : phases) {
            allPhasesMap.put(phase.getId(), phase);
          }
          session.put(APConstants.ALL_PHASES, allPhasesMap);
        }
        allPhases = (Map<Long, Phase>) session.get(APConstants.ALL_PHASES);
      }

      Map<String, Parameter> parameters = this.getParameters();
      if (this.getPhaseID() != null) {
        long phaseID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
        Phase phase = allPhases.get(new Long(phaseID));
        return phase;
      }
      if (parameters != null && parameters.containsKey(APConstants.PHASE_ID)) {
        long phaseID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
        Phase phase = allPhases.get(new Long(phaseID));
        return phase;
      }
      Phase phase = this.phaseManager.getPhaseById(this.getCurrentPhaseParam());
      return phase;
    } catch (Exception e) {
      return new Phase(null, "", -1);
    }

  }

  /**
   * Get the phases created for the crp
   *
   * @return the list of all the phases for the crp
   */
  public List<Phase> getAllCreatedPhases() {

    List<Phase> phases = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getId().longValue() == this.getCrpID().longValue()).collect(Collectors.toList());
    phases.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
    this.getSession().put(APConstants.PHASES, phases);
    return phases;

  }

  /**
   * get all phases per CRP
   *
   * @return the list of the phases for the crp
   */
  public Map<Long, Phase> getAllPhases() {
    if (this.getSession() != null) {
      if (!this.getSession().containsKey(APConstants.ALL_PHASES)) {
        List<Phase> phases = this.phaseManager.findAll().stream()
          .filter(c -> c.getCrp().getId().longValue() == this.getCrpID().longValue()).collect(Collectors.toList());
        phases.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
        Map<Long, Phase> allPhasesMap = new HashMap<>();
        for (Phase phase : phases) {
          allPhasesMap.put(phase.getId(), phase);
        }
        this.getSession().put(APConstants.ALL_PHASES, allPhasesMap);
      }

    }
    return (Map<Long, Phase>) this.getSession().get(APConstants.ALL_PHASES);
  }

  /**
   * Years for all phases
   *
   * @return String of years for a CRP/Platform/Center for all created phases
   */
  public ArrayList<String> getAllPhaseYears() {
    this.years = new ArrayList<>();
    Set<Integer> yearsSet = new HashSet<>();
    List<Phase> phases = this.getAllCreatedPhases();
    if (phases != null && !phases.isEmpty()) {
      for (Phase phase : phases) {
        yearsSet.add(phase.getYear());
      }
      if (yearsSet != null && !yearsSet.isEmpty()) {
        for (Integer yearInt : yearsSet) {
          this.years.add(yearInt.toString());
        }
        java.util.Collections.sort(this.years);
      }
    }
    return this.years;
  }

  /**
   * Obtains the list of years greater than the current one.
   *
   * @return String of years for a CRP/Platform/Center for all created phases
   */
  public ArrayList<String> getAllPhaseYearsGreater() {
    List<Phase> phases = this.getAllCreatedPhases();

    this.years = phases != null ? phases.stream().map(p -> p.getYear()).filter(y -> y > this.getCurrentCycleYear())
      .distinct().sorted().map(String::valueOf).collect(Collectors.toCollection(ArrayList::new)) : new ArrayList<>();

    return this.years;
  }

  public List<Submission> getAllProjectSubmissionsByProjectID(long projectID) {
    Project project = this.projectManager.getProjectById(projectID);
    List<Submission> submissions = project.getSubmissions().stream()
      .filter(c -> c.getCycle().equals(this.getCurrentCycle()) && c.getYear().intValue() == this.getCurrentCycleYear())
      .collect(Collectors.toList());
    if (submissions.isEmpty()) {
      return new ArrayList<>();
    }
    return submissions;
  }

  /**
   * ************************ CENTER METHOD ******************************
   * This method calculates all the years between the start date and the end
   * date.
   * ********************************************************************
   *
   * @return a List of numbers representing all the years, or an empty list if
   *         nothing found.
   */
  public List<Integer> getAllYears() {
    List<Integer> allYears = new ArrayList<>();

    Calendar calendarStart = Calendar.getInstance();
    calendarStart.set(Calendar.YEAR, 2014);
    Calendar calendarEnd = Calendar.getInstance();
    calendarEnd.set(Calendar.YEAR, 2050);

    while (calendarStart.get(Calendar.YEAR) <= calendarEnd.get(Calendar.YEAR)) {
      // Adding the year to the list.
      allYears.add(calendarStart.get(Calendar.YEAR));
      // Adding a year (365 days) to the start date.
      calendarStart.add(Calendar.YEAR, 1);
    }

    return allYears;
  }

  public List<Submission> getAR2018Submissions(long synthesisID) {
    ReportSynthesis reportSynthesis = this.reportSynthesisManager.getReportSynthesisById(synthesisID);
    List<Submission> submissions = reportSynthesis
      .getSubmissions().stream().filter(c -> c.getCycle().equals(this.getCurrentCycle())
        && c.getYear().intValue() == this.getCurrentCycleYear() && (c.isUnSubmit() == null || !c.isUnSubmit()))
      .collect(Collectors.toList());
    if (submissions.isEmpty()) {
      return new ArrayList<>();
    }
    return submissions;
  }

  public Boolean getAutoSaveFilePath(String simpleName, String actionName, Long id) {
    String composedClassName = simpleName;
    String actionFile = this.getCrpSession() + "_" + actionName;
    if (id != null) {
      String autoSaveFile = id + "_" + composedClassName + "_" + this.getActualPhase().getName() + "_"
        + this.getActualPhase().getYear() + "_" + actionFile + ".json";
      Path path = Paths.get(this.config.getAutoSaveFolder() + autoSaveFile);
      if (path.toFile().exists()) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public String getBasePermission() {
    return this.basePermission;
  }

  public String getBaseUrl() {
    /*
     * String baseUrl = config.getBaseUrl(); String url = request.getRequestURL() +
     * ""; System.out.println(url);
     */
    return this.config.getBaseUrl();
  }

  /**
   * Get the static content and libraries in general
   *
   * @return
   */
  public String getBaseUrlCdn() {
    String mediaURL = this.config.getBaseUrlCdn();
    if (mediaURL == null) {
      mediaURL = this.config.getBaseUrl();
    }

    return mediaURL;

  }

  /**
   * Get the front end libraries according to Center o CRP access
   *
   * @return
   */
  public String getBaseUrlMedia() {
    String mediaURL = this.config.getBaseUrlCdn();
    if (mediaURL == null) {
      mediaURL = this.config.getBaseUrl();
    }

    if (this.getCurrentCrp() != null) {
      switch (this.getCurrentCrp().getGlobalUnitType().getId().intValue()) {
        case 1:
          return mediaURL + "/crp";
        case 2:
          return mediaURL + "/center";
        case 3:
          return mediaURL + "/crp";
        case 4:
          return mediaURL + "/crp";
        default:
          return mediaURL + "/crp";
      }
    } else {
      return mediaURL + "/crp";
    }
  }

  public List<CenterSubmission> getCapdevSubmissions(long capDevID) {
    CapacityDevelopment capacityDevelopment = this.capacityDevelopmentService.getCapacityDevelopmentById(capDevID);

    List<CenterSubmission> submissions = capacityDevelopment.getSubmissions().stream()
      .filter(c -> c.getYear().intValue() == this.getCurrentCycleYear()).collect(Collectors.toList());
    if (submissions.isEmpty()) {
      return new ArrayList<>();
    }
    return submissions;
  }

  public List<CrpCategoryEnum> getCategories() {

    return Arrays.asList(CrpCategoryEnum.values());
  }

  /**
   * TODO: Delete this method as is not used anymore ************************
   * CENTER METHOD ****************************** Get The phase that extract
   * the crp information.
   * ********************************************************************
   *
   * @return a Crp Phase Object
   */
  public Phase getCenterCrpPhase(GlobalUnit crp) {

    try {
      String cycle = this.getSession().get(APConstants.CENTER_CRP_PHASE_CYCLE).toString();
      int year = Integer.parseInt(this.getSession().get(APConstants.CENTER_CRP_PHASE_YEAR).toString());

      if (this.getSession().containsKey(APConstants.CENTER_CRP_PHASE)) {
        Phase phase = (Phase) this.getSession().get(APConstants.CENTER_CRP_PHASE);
        return phase;
      } else {
        Phase phase = this.phaseManager.findCycle(cycle, year, this.getActualPhase().getUpkeep(), crp.getId());
        this.getSession().put(APConstants.CENTER_CRP_PHASE, phase);
        return phase;
      }

    } catch (Exception e) {
      return null;
    }

  }

  /**
   * ***********************CENTER
   * METHOD***************************************************************
   * This method gets the specific section status from the sectionStatuses
   * array for a CenterDeliverable.
   * ***************************************************************************************************
   *
   * @param deliverableID is the deliverable ID to be identified.
   * @param section is the name of some section.
   * @return a CenterSectionStatus object with the information requested.
   */
  public CenterSectionStatus getCenterDeliverableStatus(long deliverableID) {

    CenterDeliverable deliverable = this.deliverableService.getDeliverableById(deliverableID);
    List<CenterSectionStatus> sectionStatuses;

    if (deliverable.getSectionStatuses() != null) {
      sectionStatuses = new ArrayList<>(deliverable.getSectionStatuses().stream()
        .filter(c -> c.getYear() == this.getActualPhase().getYear()).collect(Collectors.toList()));
    } else {
      return null;
    }

    if (!sectionStatuses.isEmpty()) {
      return sectionStatuses.get(0);
    }
    return null;
  }

  /**
   * ************************ CENTER METHOD ********************* Get the
   * center that is currently save in the session, if the user access to the
   * platform whit a diferent url, get the current action to catch the center
   *
   * @return the center session
   */
  public Long getCenterID() {
    if (this.session != null && !this.session.isEmpty()) {
      try {
        GlobalUnit center = (GlobalUnit) this.session.get(APConstants.SESSION_CRP) != null
          ? (GlobalUnit) this.session.get(APConstants.SESSION_CRP) : null;
        this.centerID = center.getId();
      } catch (Exception e) {
        LOG.warn("There was a problem trying to find the user center in the session.");
      }
    } else {

      this.centerID = null;

    }
    return this.centerID;
  }

  /**
   * ***********************CENTER METHOD******************** This method
   * gets the specific section status from the sectionStatuses array for a
   * Outcome. ********************************************************
   *
   * @param deliverableID is the deliverable ID to be identified.
   * @param section is the name of some section.
   * @return a CenterSectionStatus object with the information requested.
   */
  public CenterSectionStatus getCenterOutcomeStatus(long outcomeID, String nameAction) {

    String name;

    if (nameAction.equals("centerOutcomesList")) {
      name = "outcomesList";
    } else if (nameAction.equals("monitoringOutcomesList")) {
      name = "monitoringOutcome";
    } else {
      name = nameAction;
    }

    CenterOutcome outcome = this.outcomeService.getResearchOutcomeById(outcomeID);
    List<CenterSectionStatus> sectionStatuses;
    if (outcome.getSectionStatuses() != null) {
      sectionStatuses = new ArrayList<>(outcome.getSectionStatuses().stream()
        .filter(c -> c.getYear().equals(this.getActualPhase().getYear()) && c.getSectionName().equals(name))
        .collect(Collectors.toList()));
    } else {
      return null;
    }

    if (!sectionStatuses.isEmpty()) {
      return sectionStatuses.get(0);
    }
    return null;
  }

  /**
   * ***********************CENTER METHOD******************** This method
   * gets the specific section status from the sectionStatuses array for a
   * Output. ************************************************************
   *
   * @param deliverableID is the deliverable ID to be identified.
   * @param section is the name of some section.
   * @return a CenterSectionStatus object with the information requested.
   */
  public CenterSectionStatus getCenterOutputStatus(long outputID) {

    CenterOutput output = this.outputService.getResearchOutputById(outputID);
    List<CenterSectionStatus> sectionStatuses;

    if (output.getSectionStatuses() != null) {
      sectionStatuses = new ArrayList<>(output.getSectionStatuses().stream()
        .filter(c -> c.getYear() == this.getActualPhase().getYear()).collect(Collectors.toList()));
    } else {
      return null;
    }

    if (!sectionStatuses.isEmpty()) {
      return sectionStatuses.get(0);
    }
    return null;
  }

  /**
   * ************************ CENTER METHOD ********************* Validate
   * the sections of CapDev *
   * ***************************************************************
   *
   * @return true if the CapDev is complete
   */
  public boolean getCenterSectionStatusCapDev(String section, long capDevID) {
    CapacityDevelopment capacityDevelopment = this.capacityDevelopmentService.getCapacityDevelopmentById(capDevID);

    if (CapDevSectionEnum.getValue(section) == null) {
      return false;
    }

    switch (CapDevSectionEnum.getValue(section)) {
      case INTERVENTION:
        return this.validateCapDevSection(capacityDevelopment, section);
      case DESCRIPTION:
        return this.validateCapDevSection(capacityDevelopment, section);
      case SUPPORTINGDOCS:
        return this.validateCapDevSupDocs(capacityDevelopment);

    }

    return true;
  }

  /**
   * ************************ CENTER METHOD ********************* Validate
   * the sections of the Impact Pathway *
   * ***************************************************************
   *
   * @return true if the IP is complete
   */
  public boolean getCenterSectionStatusIP(String section, long programID) {
    CrpProgram program = this.crpProgramManager.getCrpProgramById(programID);

    if (ImpactPathwaySectionsEnum.getValue(section) == null) {
      return false;
    }

    switch (ImpactPathwaySectionsEnum.getValue(section)) {
      case PROGRAM_IMPACT:
        return this.validateCenterImpact(program, section);
      case TOPIC:
        return this.validateCenterTopic(program, section);
      case OUTCOMES:
        return this.validateCenterOutcome(program);
      case OUTPUTS:
        return this.validateCenterOutput(program);
    }

    return true;
  }

  /**
   * ************************ CENTER METHOD ********************* validate
   * the sections of the project
   * ***************************************************************
   *
   * @return true if the Project is complete
   */
  public boolean getCenterSectionStatusProject(String section, long projectID) {

    CenterProject project = this.projectService.getCenterProjectById(projectID);

    if (ProjectSectionsEnum.getValue(section) == null) {
      return false;
    }

    switch (ProjectSectionsEnum.getValue(section)) {
      case DESCRIPTION:
        return this.validateCenterProject(project, section);
      case PARTNERS:
        return this.validateCenterProject(project, section);
      case DELIVERABLES:
        return this.validateCenterDeliverable(project);
    }

    return true;
  }

  /**
   * ************************ CENTER METHOD ********************* Get the
   * center that is currently save in the session, if the user access to the
   * platform whit a diferent url, get the current action to catch the center
   * ***************************************************************
   *
   * @return the center that the user has log in
   */
  public String getCenterSession() {
    if (this.session != null && !this.session.isEmpty()) {
      try {
        GlobalUnit center = (GlobalUnit) this.session.get(APConstants.SESSION_CRP) != null
          ? (GlobalUnit) this.session.get(APConstants.SESSION_CRP) : null;
        // Assumed there is only one center in the system, the default
        // one.
        this.centerSession = center.getAcronym();
      } catch (Exception e) {
        LOG.warn("There was a problem trying to find the user's center in the session.");
      }
    } else {
      String actionName = this.getActionName();
      if (actionName.split("/").length > 1) {
        this.centerSession = actionName.split("/")[0];
      }
    }
    return this.centerSession;
  }

  public List<GlobalUnit> getCentersList() {
    List<GlobalUnit> centers = new ArrayList<>();
    if (!this.canAccessSuperAdmin()) {
      User user = this.getCurrentUser();
      user = this.userManager.getUser(user.getId());
      List<CrpUser> users =
        new ArrayList<>(user.getCrpUsers().stream().filter(u -> u.isActive()).collect(Collectors.toList()));

      for (CrpUser crpUser : users) {
        long guType = crpUser.getCrp().getGlobalUnitType().getId();
        if (crpUser.getCrp().isActive() && guType == 2) {
          centers.add(crpUser.getCrp());
        }
      }
      return centers;
    } else {
      return this.crpManager.findAll().stream().filter(c -> c.isActive() && c.getGlobalUnitType().getId() == 2)
        .collect(Collectors.toList());
    }
  }

  public CenterSubmission getCenterSubmission() {
    return this.centerSubmission;
  }

  /**
   * ************************ CENTER METHOD ********************* return the
   * actual center year
   * ***************************************************************
   *
   * @return the actual year
   */
  public int getCenterYear() {
    try {
      return Integer.parseInt(this.getSession().get(APConstants.CENTER_YEAR).toString());
    } catch (Exception e) {
      return 0;
    }
    // return Calendar.getInstance().get(Calendar.YEAR);
  }

  public long getCGIARInstitution() {
    return APConstants.INSTITUTION_CGIAR;
  }

  public List<CrpClusterOfActivity> getClusterOutcome(long projectID, long crpProgramID) {
    Project project = this.projectManager.getProjectById(projectID);

    List<ProjectClusterActivity> clusters = project.getProjectClusterActivities().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())
        && c.getCrpClusterOfActivity().getCrpProgram().getId().longValue() == crpProgramID)
      .collect(Collectors.toList());

    List<CrpClusterOfActivity> clusterOfActivities = new ArrayList<>();
    for (ProjectClusterActivity projectClusterActivity : clusters) {
      clusterOfActivities.add(projectClusterActivity.getCrpClusterOfActivity());

    }
    return clusterOfActivities;
  }

  public APConfig getConfig() {
    return this.config;
  }

  public List<CrpProgramOutcome> getContributionsOutcome(long projectID, long crpProgramID) {
    Project project = this.projectManager.getProjectById(projectID);

    List<ProjectOutcome> outcomes =
      project.getProjectOutcomes().stream().filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())
        && c.getCrpProgramOutcome().getCrpProgram().getId().longValue() == crpProgramID).collect(Collectors.toList());

    List<CrpProgramOutcome> crpProgramOutcomes = new ArrayList<>();
    for (ProjectOutcome projectOutcome : outcomes) {
      crpProgramOutcomes.add(projectOutcome.getCrpProgramOutcome());

    }
    return crpProgramOutcomes;
  }

  /**
   * Check if the project contributes two or more flagships
   *
   * @param projectID - the project ID
   * @return true if the project contribute two or more flagships
   */
  public boolean getCountProjectFlagships(long projectID) {
    if (!this.isCenterGlobalUnit()) {
      Project project = this.projectManager.getProjectById(projectID);
      if (project != null) {
        if (project.getProjectFocuses() != null) {
          List<ProjectFocus> projectFocuses = new ArrayList<>(project.getProjectFocuses().stream()
            .filter(pf -> pf.isActive() && pf.getPhase().equals(this.getActualPhase())
              && pf.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
              && pf.getCrpProgram().getResearchArea() == null)
            .collect(Collectors.toList()));
          if (projectFocuses != null) {
            if (projectFocuses.size() >= 2) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /*
   * List a Global Unit depends of the category (TODO change the method Name after
   * test the functionality)
   */
  public List<GlobalUnit> getCrpCategoryList(String category) {
    List<GlobalUnit> globalUnits = this.crpManager.findAll().stream()
      .filter(c -> c.isMarlo() && c.getGlobalUnitType().getId().intValue() == Integer.parseInt(category))
      .collect(Collectors.toList());
    globalUnits.sort((gu1, gu2) -> gu1.getAcronym().compareTo(gu2.getAcronym()));
    return globalUnits;
  }

  public String getCrpEnableBudgetByCoas() {
    return APConstants.CRP_ENABLE_BUDGETBYCOAS;
  }

  public String getCrpEnableBudgetExecution() {
    return APConstants.CRP_ENABLE_BUDGET_EXECUTION;
  }

  /**
   * Get the crp that is currently save in the session, if the user access to
   * the platform whit a diferent url, get the current action to catch the crp
   *
   * @return the crp that the user has log in
   */
  public Long getCrpID() {
    try {
      if (this.session != null && !this.session.isEmpty()) {
        try {
          GlobalUnit crp = (GlobalUnit) this.session.get(APConstants.SESSION_CRP) != null
            ? (GlobalUnit) this.session.get(APConstants.SESSION_CRP) : null;
          this.crpID = crp.getId();

          if (crp == null || crpID == null || crpID == 0) {
            crp = crpManager.getGlobalUnitById(45);
          }
        } catch (Exception e) {
          LOG.warn("There was a problem trying to find the user crp in the session.");
        }
      } else {
        if (this.crpID == null || this.crpID == 0) {
          GlobalUnit crp = crpManager.getGlobalUnitById(45);
          this.crpID = crp.getId();
        }
        return this.crpID;

      }
    } catch (Exception e) {
      LOG.error(
        "I'm not exactly sure what exception this is supposed to catch!  If this statement ever gets printed, I will be surprised!",
        e);
      /**
       * Original code swallows the exception and didn't even log it. Now
       * we at least log it, but we need to revisit to see if we should
       * continue processing or re-throw the exception.
       */
    }
    return this.crpID;
  }

  /**
   * Get the Crp List
   *
   * @return List<Crp> object
   */
  public List<GlobalUnit> getCrpList() {

    List<GlobalUnit> centers = new ArrayList<>();
    if (!this.canAccessSuperAdmin()) {
      User user = this.getCurrentUser();
      user = this.userManager.getUser(user.getId());
      List<CrpUser> users =
        new ArrayList<>(user.getCrpUsers().stream().filter(u -> u.isActive()).collect(Collectors.toList()));

      for (CrpUser crpUser : users) {
        long guType = crpUser.getCrp().getGlobalUnitType().getId();
        if (crpUser.getCrp().isActive() && guType == 1) {
          centers.add(crpUser.getCrp());
        }
      }
      return centers;
    } else {
      return this.crpManager.findAll().stream().filter(c -> c.isActive() && c.getGlobalUnitType().getId() == 1)
        .collect(Collectors.toList());
    }
  }

  public Phase getCrpPhase(Long crpID, int year, String phaseDescription) {
    Phase phase = null;
    if (crpID != null && crpID != 0 && year != 0 && phaseDescription != null && !phaseDescription.isEmpty()) {

      phase = phaseManager.findAll().stream()
        .filter(p -> p != null && p.getYear() == year && p.getCrp() != null && p.getCrp().getId() != null
          && p.getCrp().getId() == crpID && p.getDescription() != null && p.getDescription().equals(phaseDescription))
        .collect(Collectors.toList()).get(0);
    }
    return phase;
  }

  /**
   * Get the crp that is currently save in the session, if the user access to
   * the platform whit a diferent url, get the current action to catch the crp
   *
   * @return the crp that the user has log in
   */
  public String getCrpSession() {
    if (this.session != null && !this.session.isEmpty()) {
      try {
        GlobalUnit crp = (GlobalUnit) this.session.get(APConstants.SESSION_CRP) != null
          ? (GlobalUnit) this.session.get(APConstants.SESSION_CRP) : null;
        this.crpSession = crp.getAcronym();
      } catch (Exception e) {
        LOG.warn("There was a problem trying to find the user crp in the session.");
      }
    } else {
      String actionName = this.getActionName();
      if (actionName.split("/").length > 1) {
        this.crpSession = actionName.split("/")[0];
      }
    }
    return this.crpSession;
  }

  /**
   * ************************ CENTER METHOD ******************************
   * Gets the current center phase
   * ********************************************************************
   *
   * @return a Current Phase Object
   */
  public Phase getCurrentCenterPhase() {

    try {
      Map<Long, Phase> allPhases = this.getAllPhases();
      // TODO
      // Map<String, Parameter> parameters = this.getParameters();
      // if (this.getPhaseID() != null) {
      // long phaseID =
      // Long.parseLong(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
      // Phase phase = allPhases.get(new Long(phaseID));
      // return phase;
      // }
      // if (parameters != null &&
      // parameters.containsKey(APConstants.PHASE_ID)) {
      // long phaseID =
      // Long.parseLong(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
      // Phase phase = allPhases.get(new Long(phaseID));
      // return phase;
      // }
      if (this.getSession().containsKey(APConstants.CENTER_CURRENT_PHASE)) {
        Phase phase = (Phase) this.getSession().get(APConstants.CENTER_CURRENT_PHASE);
        return phase;
      } else {
        Phase phase = this.phaseManager.findCycle(APConstants.REPORTING, this.getActualPhase().getYear(),
          this.getActualPhase().getUpkeep(), this.getCrpID());
        this.getSession().put(APConstants.CENTER_CURRENT_PHASE, phase);
        return phase;
      }

    } catch (Exception e) {
      e.printStackTrace();
      return new Phase(null, "", -1);
    }

  }

  public GlobalUnit getCurrentCrp() {
    if (this.session != null && !this.session.isEmpty()) {
      try {
        GlobalUnit crp = (GlobalUnit) this.session.get(APConstants.SESSION_CRP) != null
          ? (GlobalUnit) this.session.get(APConstants.SESSION_CRP) : null;
        this.currentCrp = crp;
      } catch (Exception e) {
        LOG.warn("There was a problem trying to find the user crp in the session.");
      }
    } else {

      this.currentCrp = null;

    }
    return this.currentCrp;
  }

  public String getCurrentCycle() {
    try {
      if (this.isReportingActive()) {
        return APConstants.REPORTING;
      } else {
        return APConstants.PLANNING;
      }
    } catch (Exception e) {
      return null;
    }
  }

  public int getCurrentCycleYear() {
    return this.getActualPhase().getYear();
  }

  public GlobalUnit getCurrentGlobalUnit() {
    if (this.session != null && !this.session.isEmpty()) {
      try {
        GlobalUnit crp = (GlobalUnit) this.session.get(APConstants.SESSION_CRP) != null
          ? (GlobalUnit) this.session.get(APConstants.SESSION_CRP) : null;
        this.currentCrp = crp;
      } catch (Exception e) {
        LOG.warn("There was a problem trying to find the Global Unit in the session.");
      }
    } else {

      this.currentCrp = null;

    }
    return this.currentCrp;
  }

  private long getCurrentPhaseParam() {
    try {
      return new Long(Integer.parseInt(this.getSession().get(APConstants.CURRENT_PHASE_PARAM).toString()));
    } catch (Exception e) {
      return new Long(0);
    }
  }

  /**
   * Get the user that is currently saved in the session.
   *
   * @return a user object or null if no user was found.
   */
  public User getCurrentUser() {
    User u = null;
    if (this.session != null && !this.session.isEmpty()) {
      try {
        u =
          this.session.get(APConstants.SESSION_USER) != null ? (User) this.session.get(APConstants.SESSION_USER) : null;
      } catch (Exception e) {
        LOG.warn("There was a problem trying to find the user in the session.");
      }
    }
    return u;
  }

  /**
   * This method return the Date Format from APConstants class
   *
   * @return A dateformat (yyyy-MM-dd)
   */
  public String getDateFormat() {
    return APConstants.DATE_FORMAT;
  }

  public String getDeliverableComplianceCheck() {
    return APConstants.DELIVERABLE_RULE_COMPILANCE_CHECK;
  }

  public String getDeliverableComputerLicense() {
    return APConstants.DELIVERABLE_RULE_COMPUTER_LICENSE;
  }

  public String getDeliverableDataLicense() {
    return APConstants.DELIVERABLE_RULE_DATA_LICENSE;
  }

  public String getDeliverableJournalArticles() {
    return APConstants.DELIVERABLE_RULE_JORNAL_ARTICLES;
  }

  public String getDeliverablePublicationMetadata() {
    return APConstants.DELIVERABLE_RULE_PUBLICATION_METADATA;
  }

  public List<Deliverable> getDeliverableRelationsImpact(Long id, String className) {
    Class<?> clazz;
    List<Deliverable> deliverables = null;
    try {
      clazz = Class.forName(className);
      if (clazz == CrpClusterKeyOutput.class) {
        CrpClusterKeyOutput crpClusterKeyOutput = this.crpClusterKeyOutputManager.getCrpClusterKeyOutputById(id);
        List<DeliverableInfo> deList = crpClusterKeyOutput.getDeliverables().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList());
        Set<Deliverable> deSet = new HashSet<>();
        for (DeliverableInfo deliverableInfo : deList) {
          Deliverable deliverable = deliverableInfo.getDeliverable();
          deliverable.getDeliverableInfo(this.getActualPhase());
          if (deliverable.getDeliverableInfo() != null) {

            if (this.isReportingActive() || this.isUpKeepActive()) {
              if (deliverable.getDeliverableInfo().isRequiredToComplete()
                || deliverable.getDeliverableInfo().isStatusCompleteInNextPhases()) {
                deSet.add(deliverable);
              }
            } else {
              if (deliverable.isActive() && deliverable.getDeliverableInfo().getNewExpectedYear() != null
                && deliverable.getDeliverableInfo().getNewExpectedYear() >= this.getActualPhase().getYear()
                && deliverable.getDeliverableInfo().getStatus() != null
                && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                deSet.add(deliverable);
              }
              if (deliverable.isActive()
                && deliverable.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getActualPhase().getYear()
                && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
                deSet.add(deliverable);
              }
            }
          }
        }
        deliverables = new ArrayList<>();
        deliverables.addAll(deSet);
      }

    } catch (Exception e) {
      e.printStackTrace();

    }
    return deliverables;

  }

  public List<Deliverable> getDeliverableRelationsProject(Long id, String className, Long projectID) {
    Class<?> clazz;
    List<Deliverable> deliverables = null;
    try {
      clazz = Class.forName(className);

      if (clazz == ProjectBudget.class) {
        ProjectBudget projectBudget = this.projectBudgetManager.getProjectBudgetById(id);
        List<DeliverableFundingSource> deList = projectBudget.getFundingSource().getDeliverableFundingSources().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())
            && c.getDeliverable().getProject() != null
            && c.getDeliverable().getProject().getId().longValue() == projectID.longValue())
          .collect(Collectors.toList());
        Set<Deliverable> deSet = new HashSet<>();
        for (DeliverableFundingSource deliverableInfo : deList) {
          Deliverable deliverable = deliverableInfo.getDeliverable();
          long projectDB = deliverable.getProject().getId().longValue();
          if (deliverable.getProject() != null && projectDB == projectID) {
            if (deliverable.getDeliverableInfo() != null) {
              if (this.isReportingActive() || this.isUpKeepActive()) {
                if (deliverable.getDeliverableInfo().isRequiredToComplete()
                  || deliverable.getDeliverableInfo().isStatusCompleteInNextPhases()) {
                  deSet.add(deliverable);
                }
              } else {
                if (deliverable.isActive() && deliverable.getDeliverableInfo().getNewExpectedYear() != null
                  && deliverable.getDeliverableInfo().getNewExpectedYear() >= this.getActualPhase().getYear()
                  && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                  && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                    .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                  deSet.add(deliverable);
                }
                if (deliverable.isActive()
                  && deliverable.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getActualPhase().getYear()
                  && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                  && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                    .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
                  deSet.add(deliverable);
                }
              }
            }
          }

        }
        deliverables = new ArrayList<>();
        deliverables.addAll(deSet);
      }

      if (clazz == Project.class) {
        ProjectBudget projectBudget = this.projectBudgetManager.getProjectBudgetById(id);
        List<DeliverableFundingSource> deList = projectBudget.getFundingSource().getDeliverableFundingSources().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())
            && c.getDeliverable().getProject().getId().longValue() == projectID.longValue())
          .collect(Collectors.toList());
        Set<Deliverable> deSet = new HashSet<>();
        for (DeliverableFundingSource deliverableInfo : deList) {
          Deliverable deliverable = deliverableInfo.getDeliverable();
          long projectDB = deliverable.getProject().getId().longValue();
          if (deliverable.getProject() != null && projectDB == projectID) {
            if (deliverable.getDeliverableInfo() != null) {
              if (this.isReportingActive() || this.isUpKeepActive()) {
                if (deliverable.getDeliverableInfo().isRequiredToComplete()) {
                  deSet.add(deliverable);
                }
              } else {
                if (deliverable.isActive() && deliverable.getDeliverableInfo().getNewExpectedYear() != null
                  && deliverable.getDeliverableInfo().getNewExpectedYear() >= this.getActualPhase().getYear()
                  && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                  && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                    .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                  deSet.add(deliverable);
                }
                if (deliverable.isActive()
                  && deliverable.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getActualPhase().getYear()
                  && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                  && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                    .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
                  deSet.add(deliverable);
                }
              }
            }
          }

        }
        deliverables = new ArrayList<>();
        deliverables.addAll(deSet);
      }

      if (clazz == ProjectOutcome.class) {
        if (this.isAiccra()) {
          deliverables = new ArrayList<>();
          ProjectOutcome projectOutcome = this.projectOutcomeManager.getProjectOutcomeById(id);

          /*
           * Commented for implement the new relationship deliverable_project_outcomes
           * for (Deliverable deliverable : projectOutcome.getProject().getCurrentDeliverables(this.getActualPhase())) {
           * if (deliverable.getDeliverableInfo() != null
           * && deliverable.getDeliverableInfo().getCrpProgramOutcome() != null && deliverable.getDeliverableInfo()
           * .getCrpProgramOutcome().getId().compareTo(projectOutcome.getCrpProgramOutcome().getId()) == 0) {
           * deliverables.add(deliverable);
           * }
           * }
           */

          // New method for new relationship deliverable_project_outcomes table
          /*
           * List<Deliverable> deliverablesTemp = null;
           * deliverablesTemp = projectOutcome.getProject().getCurrentDeliverables(this.getActualPhase());
           * if (this.isReportingActive()) {
           * deliverablesTemp = deliverablesTemp.stream().filter(d -> d.getDeliverableInfo(this.getActualPhase()) !=
           * null
           * && d.getDeliverableInfo(this.getActualPhase()).getStatus() == 3).collect(Collectors.toList());
           * }
           * for (Deliverable deliverable : deliverablesTemp) {
           * if (deliverable.getDeliverableProjectOutcomes() != null) {
           * deliverable.setProjectOutcomes(new ArrayList<>(deliverable.getDeliverableProjectOutcomes().stream()
           * .filter(o -> o.getPhase().getId().equals(this.getActualPhase().getId())).collect(Collectors.toList())));
           * }
           * if (deliverable != null && deliverable.getProjectOutcomes() != null
           * && !deliverable.getProjectOutcomes().isEmpty()) {
           * for (DeliverableProjectOutcome deliverableProjectOutcome : deliverable.getProjectOutcomes()) {
           * if (deliverableProjectOutcome != null && deliverableProjectOutcome.getProjectOutcome() != null
           * && deliverableProjectOutcome.getProjectOutcome().getId() != null
           * && deliverableProjectOutcome.getProjectOutcome().getId().compareTo(projectOutcome.getId()) == 0) {
           * deliverables.add(deliverable);
           * }
           * }
           * }
           * }
           */
          // New method for new relationship deliverable_project_outcomes table
          List<Deliverable> deliverablesTemp = null;

          deliverablesTemp = projectOutcome.getProject().getCurrentDeliverables(this.getActualPhase());

          if (this.reportingActive) {
            deliverablesTemp = deliverablesTemp.stream().filter(d -> d.getDeliverableInfo(this.getActualPhase()) != null
              && d.getDeliverableInfo(this.getActualPhase()).getStatus() == 3).collect(Collectors.toList());
          }

          // Shared with others
          List<ProjectDeliverableShared> deliverablesSharedOther = new ArrayList<>();

          deliverablesSharedOther = projectDeliverableSharedManager.getByPhase(this.getActualPhase().getId());
          if (deliverablesSharedOther != null && !deliverablesSharedOther.isEmpty()) {
            deliverablesSharedOther = deliverablesSharedOther.stream()
              .filter(ds -> ds.getDeliverable() != null && ds.getDeliverable().getProject().getId().equals(projectID))
              .collect(Collectors.toList());
          }

          for (Deliverable deliverable : deliverablesTemp) {
            if (deliverable.getDeliverableCrpOutcomes() != null) {
              deliverable.setCrpOutcomes(new ArrayList<>(deliverable.getDeliverableCrpOutcomes().stream()
                .filter(o -> o.getPhase().getId().equals(this.getActualPhase().getId())).collect(Collectors.toList())));
            }
            if (deliverable != null && deliverable.getCrpOutcomes() != null
              && !deliverable.getCrpOutcomes().isEmpty()) {
              for (DeliverableCrpOutcome deliverableCrpOutcome : deliverable.getCrpOutcomes()) {
                if (deliverableCrpOutcome != null && deliverableCrpOutcome.getCrpProgramOutcome() != null
                  && deliverableCrpOutcome.getCrpProgramOutcome().getId() != null && deliverableCrpOutcome
                    .getCrpProgramOutcome().getId().compareTo(projectOutcome.getCrpProgramOutcome().getId()) == 0) {
                  // Owner
                  if (deliverable.getProject() != null && !deliverable.getProject().getId().equals(projectID)) {
                    deliverable
                      .setOwner(deliverable.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
                    deliverable
                      .setSharedWithMe(deliverable.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
                  } else {
                    deliverable.setOwner("This Cluster");
                    deliverable.setSharedWithMe("Not Applicable");
                  }

                  // check if shared with others
                  for (ProjectDeliverableShared deliverableSharedT : deliverablesSharedOther) {
                    if (deliverableSharedT.getDeliverable().getId().equals(deliverable.getId()) && deliverableSharedT
                      .getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym() != null) {

                      if (deliverable.getSharedWithProjects() == null || (deliverable.getSharedWithProjects() != null
                        && deliverable.getSharedWithProjects().isEmpty())) {
                        deliverable.setSharedWithProjects(
                          "" + deliverableSharedT.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
                      } else {
                        if (deliverable.getSharedWithProjects() != null
                          && (!deliverable.getSharedWithProjects().contains(
                            deliverableSharedT.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym()))) {
                          deliverable.setSharedWithProjects(deliverable.getSharedWithProjects() + "; "
                            + deliverableSharedT.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
                        }
                      }
                    }
                  }
                  deliverables.add(deliverable);
                }
              }
            }
          }

          try {

            // Shared with others
            List<ProjectDeliverableShared> deliverablesSharedFromOther = new ArrayList<>();

            deliverablesSharedFromOther = projectDeliverableSharedManager.getByPhase(this.getActualPhase().getId());
            if (deliverablesSharedOther != null && !deliverablesSharedOther.isEmpty()) {
              deliverablesSharedOther = deliverablesSharedOther.stream()
                .filter(ds -> ds.getDeliverable() != null && ds.getProject().getId().equals(projectID))
                .collect(Collectors.toList());
            }
            // Load Shared deliverables
            List<ProjectDeliverableShared> deliverableShared = this.projectDeliverableSharedManager
              .getByProjectAndPhase(projectID, this.getActualPhase().getId()) != null
                ? this.projectDeliverableSharedManager.getByProjectAndPhase(projectID, this.getActualPhase().getId())
                  .stream()
                  .filter(px -> px.isActive() && px.getDeliverable().isActive()
                    && px.getDeliverable().getDeliverableInfo(this.getActualPhase()) != null)
                  .collect(Collectors.toList())
                : Collections.emptyList();

            if (deliverableShared != null && !deliverableShared.isEmpty()) {
              for (ProjectDeliverableShared deliverableS : deliverableShared) {

                if (deliverableS.getDeliverable() != null && deliverableCrpOutcomeManager.findAll() != null) {
                  List<DeliverableCrpOutcome> deliverableOutcomes = deliverableCrpOutcomeManager.findAll().stream()
                    .filter(d -> d != null && d.getDeliverable() != null
                      && d.getDeliverable().getId().equals(deliverableS.getDeliverable().getId()))
                    .collect(Collectors.toList());
                  if (deliverableOutcomes != null && !deliverableOutcomes.isEmpty()) {
                    for (DeliverableCrpOutcome deliverableOutcome : deliverableOutcomes) {

                      if (deliverableOutcome.getCrpProgramOutcome().getId()
                        .equals(projectOutcome.getCrpProgramOutcome().getId())) {

                        if (deliverableS.getDeliverable().getProject() != null
                          && deliverableS.getDeliverable().getProject().getId() != null
                          && !deliverableS.getDeliverable().getProject().getId().equals(projectID)) {
                          deliverableS.getDeliverable()
                            .setTagTitle(deliverableS.getDeliverable().getDeliverableInfo().getTitle());
                          deliverableS.getDeliverable().setOwner(deliverableS.getDeliverable().getProject()
                            .getProjecInfoPhase(this.getActualPhase()).getAcronym());
                          deliverableS.getDeliverable().setSharedWithMe(deliverableS.getDeliverable().getProject()
                            .getProjecInfoPhase(this.getActualPhase()).getAcronym());


                          // Shared clusters from others deliverables
                          for (ProjectDeliverableShared sharedOthers : deliverablesSharedFromOther) {
                            if (sharedOthers.getDeliverable().getId().equals(deliverableS.getDeliverable().getId())) {

                              if (deliverableS.getDeliverable().getSharedWithProjects() == null
                                || (deliverableS.getDeliverable().getSharedWithProjects() != null
                                  && deliverableS.getDeliverable().getSharedWithProjects().isEmpty())) {
                                deliverableS.getDeliverable().setSharedWithProjects(""
                                  + sharedOthers.getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
                              } else {
                                if (sharedOthers != null && sharedOthers.getProject() != null
                                  && sharedOthers.getProject().getProjecInfoPhase(this.getActualPhase()) != null
                                  && sharedOthers.getProject().getProjecInfoPhase(this.getActualPhase())
                                    .getAcronym() != null
                                  && deliverableS.getDeliverable().getSharedWithProjects() != null
                                  && (!deliverableS.getDeliverable().getSharedWithProjects().contains(sharedOthers
                                    .getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym()))) {
                                  deliverableS.getDeliverable().setSharedWithProjects(
                                    deliverableS.getDeliverable().getSharedWithProjects() + "; " + sharedOthers
                                      .getProject().getProjecInfoPhase(this.getActualPhase()).getAcronym());
                                }
                              }

                            }
                          }

                        }
                        deliverables.add(deliverableS.getDeliverable());
                      }
                    }
                  }
                }
              }
            }
          } catch (Exception e) {
            LOG.error("unable to get shared deliverables", e);
          }
        } else {
          deliverables = new ArrayList<>();
          ProjectOutcome projectOutcome = this.projectOutcomeManager.getProjectOutcomeById(id);
          List<CrpClusterKeyOutputOutcome> keyOutputOutcomes = projectOutcome.getCrpProgramOutcome()
            .getCrpClusterKeyOutputOutcomes().stream().filter(c -> c.isActive()).collect(Collectors.toList());
          for (CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome : keyOutputOutcomes) {

            deliverables.addAll(this.getDeliverableRelationsImpact(
              crpClusterKeyOutputOutcome.getCrpClusterKeyOutput().getId(), CrpClusterKeyOutput.class.getName()));
          }

          HashSet<Deliverable> deList = new HashSet<>();

          for (Deliverable deliverable : deliverables) {
            deliverable.setDeliverableInfo(deliverable.getDeliverableInfo(this.getActualPhase()));
            if (deliverable.isActive() && deliverable.getProject() != null
              && deliverable.getProject().getId().longValue() == projectID.longValue()) {
              if (deliverable.getDeliverableInfo() != null) {
                if (this.isReportingActive() || this.isUpKeepActive()) {
                  if (deliverable.getDeliverableInfo().isRequiredToComplete()) {
                    deList.add(deliverable);
                  }
                } else {
                  if (deliverable.isActive() && deliverable.getDeliverableInfo().getNewExpectedYear() != null
                    && deliverable.getDeliverableInfo().getNewExpectedYear() >= this.getActualPhase().getYear()
                    && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                    && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                      .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                    deList.add(deliverable);
                  }
                  if (deliverable.isActive()
                    && deliverable.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getActualPhase()
                      .getYear()
                    && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                    && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                      .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
                    deList.add(deliverable);
                  }
                }
              }

            }
          }
          deliverables.clear();
          deliverables.addAll(deList);

        }
      }

    } catch (Exception e) {
      e.printStackTrace();

    }
    return deliverables;

  }

  public List<String> getDeliverableTypesByRule(String rule) {
    List<String> rules = new ArrayList<>();
    List<DeliverableTypeRule> deliverableTypeRules =
      this.deliverableTypeRuleManager.findDeliverableTypeRuleByRule(rule);

    for (DeliverableTypeRule deliverableTypeRule : deliverableTypeRules) {
      rules.add(deliverableTypeRule.getDeliverableType().getId().toString());
    }
    return rules;
  }

  public List<HistoryDifference> getDifferences() {
    return this.differences;
  }

  public List<ProjectExpectedStudy> getexpectedProjectOutcomes(Long id) {
    List<ProjectExpectedStudy> expectedStudies = new ArrayList<>();
    ProjectOutcome projectOutcome = this.projectOutcomeManager.getProjectOutcomeById(id);

    try {
      for (ProjectExpectedStudy expectedStudy : projectOutcome.getProject().getProjectExpectedStudies().stream()
        .filter(ps -> ps.isActive() && ps.getProjectExpectedStudyInfo(this.getActualPhase()) != null
          && ps.getProjectExpectedStudyInfo(this.getActualPhase()).isActive())
        .collect(Collectors.toList())) {
        if (expectedStudy.getProjectExpectedStudyProjectOutcomes() != null) {
          expectedStudy
            .setProjectOutcomes(new ArrayList<>(expectedStudy.getProjectExpectedStudyProjectOutcomes().stream()
              .filter(o -> o.getPhase().getId().equals(this.getActualPhase().getId())).collect(Collectors.toList())));
        }
        if (expectedStudy != null && expectedStudy.getProjectOutcomes() != null
          && !expectedStudy.getProjectOutcomes().isEmpty()) {
          for (ProjectExpectedStudyProjectOutcome expectedStudyProjectOutcome : expectedStudy.getProjectOutcomes()) {
            if (expectedStudyProjectOutcome != null && expectedStudyProjectOutcome.getProjectOutcome() != null
              && expectedStudyProjectOutcome.getProjectOutcome().getId() != null
              && expectedStudyProjectOutcome.getProjectOutcome().getId().compareTo(projectOutcome.getId()) == 0) {
              expectedStudies.add(expectedStudy);
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return expectedStudies;
  }

  public List<Integer> getExpectedStudiesYears(Long expectedStudy) {
    List<ProjectExpectedStudyInfo> projectExpectedStudyInfoList =
      this.projectExpectedStudyInfoManager.findAll().stream()
        .filter(c -> c != null && c.getProjectExpectedStudy() != null
          && c.getProjectExpectedStudy().getId().longValue() == expectedStudy.longValue()
          && c.getPhase().getName().equals(APConstants.PROJECT_INDICATOR_PHASE_PREVIOUS_NAME)
          && c.getPhase().getYear() == (this.getActualPhase().getYear()))
        .collect(Collectors.toList());
    List<Integer> allYears = new ArrayList<>();
    if (projectExpectedStudyInfoList.size() > 0) {
      if (projectExpectedStudyInfoList.get(0).getYear() != this.getActualPhase().getYear()) {
        allYears.add(projectExpectedStudyInfoList.get(0).getYear());
      } else {
        List<ProjectExpectedStudyInfo> projectExpectedStudyInfoList2 =
          this.projectExpectedStudyInfoManager.findAll().stream()
            .filter(c -> c != null && c.getProjectExpectedStudy() != null
              && c.getProjectExpectedStudy().getId().longValue() == expectedStudy.longValue()
              && c.getPhase().getName().equals(APConstants.PROJECT_INDICATOR_PHASE_PREVIOUS_NAME)
              && c.getPhase().getYear() < (this.getActualPhase().getYear()))
            .collect(Collectors.toList());
        if (projectExpectedStudyInfoList2.size() > 0) {
          allYears.add(projectExpectedStudyInfoList2.get(0).getYear());
        }
      }

    }
    // Avoid to duplicate the actual year phase in allYears List
    if (allYears != null && !allYears.contains(this.getActualPhase().getYear())) {
      allYears.add(this.getActualPhase().getYear());
    }

    return allYears;
  }

  public FileDB getFileDB(FileDB preview, File file, String fileFileName, String path) {

    try {

      FileInputStream fis = new FileInputStream(file);
      String md5 = DigestUtils.md5Hex(fis);
      FileDB dbFile = null;

      if (preview != null) {

        if (preview.getFileName().equals(fileFileName) && !md5.equals(preview.getTokenId())) {
          dbFile = new FileDB(fileFileName, md5);
          FileDB dbFilePrev = preview;
          Path prevFile = Paths.get(path + dbFilePrev.getFileName());
          String newName = FilenameUtils.removeExtension(fileFileName) + "_" + UUID.randomUUID().toString() + "."
            + FilenameUtils.getExtension(fileFileName);
          newName = newName.replaceAll(":", "-");
          Files.move(prevFile, prevFile.resolveSibling(newName));
          dbFilePrev.setFileName(newName);
          this.fileDBManager.saveFileDB(dbFilePrev);
        } else {
          if (preview.getFileName().equals(fileFileName) && md5.equals(preview.getTokenId())) {
            dbFile = preview;
          } else {
            dbFile = new FileDB(fileFileName, md5);
          }
        }

      } else {
        dbFile = new FileDB(fileFileName, md5);
      }
      this.fileDBManager.saveFileDB(dbFile);
      return dbFile;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }

  /**
   * Get the Global Unit Type
   *
   * @return the global unit type ID.
   */
  public int getGlobalUnitType() {

    GlobalUnit globalUnit = this.getCurrentCrp();
    int re = globalUnit.getGlobalUnitType().getId().intValue();
    return re;

  }

  public Boolean getHasLp6ContributionDeliverable(long deliverableID, long phaseID) {
    Deliverable deliverable = this.deliverableManager.getDeliverableById(deliverableID);
    // Get selected deliverables
    List<ProjectLp6ContributionDeliverable> projectLp6ContributionDeliverables = deliverable.getDeliverableLp6s()
      .stream().filter(dl -> dl.isActive() && dl.getPhase().getId().equals(phaseID)).collect(Collectors.toList());
    if (projectLp6ContributionDeliverables != null && !projectLp6ContributionDeliverables.isEmpty()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Get the folder path according if the user navigate in center,crp or
   * platform sections.
   *
   * @return the String folder path.
   */
  public String getHeaderPath() {

    switch (this.getCurrentCrp().getGlobalUnitType().getId().intValue()) {
      case 1:
        return "crp/";
      case 2:
        return "center/";
      default:
        return null;
    }

  }

  public long getIFPRIId() {
    return APConstants.IFPRI_ID;
  }

  public boolean getImpactSectionStatus(String section, long crpProgramID) {
    SectionStatus sectionStatus = this.sectionStatusManager.getSectionStatusByCrpProgam(crpProgramID, section,
      this.getActualPhase().getDescription(), this.getActualPhase().getYear(), this.getActualPhase().getUpkeep());
    if (sectionStatus != null) {
      if (sectionStatus.getMissingFields().length() == 0
        && !this.getAutoSaveFilePath(CrpProgram.class.getSimpleName(), section, crpProgramID)) {
        return true;
      }
    }
    return false;
  }

  public List<ProjectInnovation> getInnovationProjectOutcomes(Long id) {
    List<ProjectInnovation> innovations = new ArrayList<>();
    ProjectOutcome projectOutcome = this.projectOutcomeManager.getProjectOutcomeById(id);

    try {
      for (ProjectInnovation innovation : projectOutcome.getProject().getProjectInnovations().stream()
        .filter(ps -> ps.isActive() && ps.getProjectInnovationInfo(this.getActualPhase()) != null
          && ps.getProjectInnovationInfo(this.getActualPhase()).isActive())
        .collect(Collectors.toList())) {
        if (innovation.getProjectInnovationProjectOutcomes() != null) {
          innovation.setProjectOutcomes(new ArrayList<>(innovation.getProjectInnovationProjectOutcomes().stream()
            .filter(o -> o.getPhase().getId().equals(this.getActualPhase().getId())).collect(Collectors.toList())));
        }
        if (innovation != null && innovation.getProjectOutcomes() != null
          && !innovation.getProjectOutcomes().isEmpty()) {
          for (ProjectInnovationProjectOutcome innovationStudyProjectOutcome : innovation.getProjectOutcomes()) {
            if (innovationStudyProjectOutcome != null && innovationStudyProjectOutcome.getProjectOutcome() != null
              && innovationStudyProjectOutcome.getProjectOutcome().getId() != null
              && innovationStudyProjectOutcome.getProjectOutcome().getId().compareTo(projectOutcome.getId()) == 0) {
              innovations.add(innovation);
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return innovations;
  }

  public SectionStatus getInnovationStatus(long innovationID) {

    ProjectInnovation innovation = this.projectInnovationManager.getProjectInnovationById(innovationID);

    List<SectionStatus> sectionStatuses;

    if (innovation.getSectionStatuses() != null) {
      sectionStatuses = new ArrayList<>(innovation.getSectionStatuses().stream()
        .filter(c -> c.getYear().equals(this.getActualPhase().getYear()) && c.getCycle().equals(this.getCurrentCycle()))
        .collect(Collectors.toList()));
    } else {
      return null;
    }

    if (!sectionStatuses.isEmpty()) {
      return sectionStatuses.get(0);
    }
    return null;

  }

  public List<Integer> getInnovationsYears(Long innovation) {
    List<ProjectInnovationInfo> projectInnovationInfoList = this.projectInnovationInfoManager.findAll().stream()
      .filter(c -> c != null && c.getProjectInnovation() != null
        && c.getProjectInnovation().getId().longValue() == innovation.longValue()
        && c.getPhase().getName().equals(APConstants.PROJECT_INDICATOR_PHASE_PREVIOUS_NAME)
        && c.getPhase().getYear() == (this.getActualPhase().getYear()))
      .collect(Collectors.toList());
    List<Integer> allYears = new ArrayList<>();
    if (projectInnovationInfoList.size() > 0) {
      if (projectInnovationInfoList.get(0).getYear() != this.getActualPhase().getYear()) {
        allYears.add(projectInnovationInfoList.get(0).getYear().intValue());
      } else {
        List<ProjectInnovationInfo> projectInnovationInfoList2 = this.projectInnovationInfoManager.findAll().stream()
          .filter(c -> c != null && c.getProjectInnovation() != null
            && c.getProjectInnovation().getId().longValue() == innovation.longValue()
            && c.getPhase().getName().equals(APConstants.PROJECT_INDICATOR_PHASE_PREVIOUS_NAME)
            && c.getPhase().getYear() < (this.getActualPhase().getYear()))
          .collect(Collectors.toList());
        if (projectInnovationInfoList2.size() > 0) {
          allYears.add(projectInnovationInfoList2.get(0).getYear().intValue());
        }
      }

    }
    allYears.add(this.getActualPhase().getYear());
    return allYears;
  }

  public HashMap<String, String> getInvalidFields() {
    return this.invalidFields;
  }

  public String getJustification() {
    return this.justification;
  }

  public String getLiasons() {
    String liasonsUsers = "";
    User u = this.userManager.getUser(this.getCurrentUser().getId());
    for (LiaisonUser liaisonUser : u.getLiasonsUsers().stream()
      .filter(c -> c.isActive() && c.getCrp().getId().intValue() == this.getCrpID().intValue())
      .collect(Collectors.toList())) {
      if (liasonsUsers.isEmpty()) {
        liasonsUsers = liaisonUser.getLiaisonInstitution().getAcronym();
      } else {
        liasonsUsers = liasonsUsers + "," + liaisonUser.getLiaisonInstitution().getAcronym();
      }
    }
    return liasonsUsers;
  }

  public List<GlobalUnitType> getListGlobalUnitTypes() {

    List<GlobalUnitType> globalUnitTypes = this.globalUnitTypeManager.findAll();

    for (GlobalUnitType globalUnitType : globalUnitTypes) {
      globalUnitType.setGlobalUnitsList(globalUnitType.getGlobalUnits().stream()
        .filter(c -> c.isActive() && c.getAcronym().equals("AICCRA")).collect(Collectors.toList()));
    }

    return globalUnitTypes;

  }

  public List<GlobalUnitType> getListGlobalUnitTypesUser() {
    if (this.getSession().containsKey(APConstants.AVAILABLES_GLOBAL_TYPES)) {

      return (List<GlobalUnitType>) this.getSession().get(APConstants.AVAILABLES_GLOBAL_TYPES);
    } else {

      List<GlobalUnitType> globalUnitTypes = new ArrayList();

      User user = this.getCurrentUser();
      user = this.userManager.getUser(user.getId());
      List<CrpUser> users =
        new ArrayList<>(user.getCrpUsers().stream().filter(u -> u.isActive()).collect(Collectors.toList()));

      for (CrpUser crpUser : users) {
        if (!globalUnitTypes.contains(crpUser.getCrp().getGlobalUnitType())) {
          crpUser.getCrp().getGlobalUnitType().setGlobalUnitsList(new ArrayList<>());
          crpUser.getCrp().getGlobalUnitType().getGlobalUnitsList().add(crpUser.getCrp());
          if (crpUser.getCrp().getGlobalUnitType().isActive()) {
            globalUnitTypes.add(crpUser.getCrp().getGlobalUnitType());
          }

        } else {
          int index = globalUnitTypes.indexOf(crpUser.getCrp().getGlobalUnitType());
          GlobalUnitType globalUnitType = globalUnitTypes.get(index);
          if (!globalUnitType.getGlobalUnitsList().contains(crpUser.getCrp())) {
            globalUnitType.getGlobalUnitsList().add(crpUser.getCrp());
          }

        }
      }
      this.getSession().put(APConstants.AVAILABLES_GLOBAL_TYPES, globalUnitTypes);
      return globalUnitTypes;
    }
  }

  public List<Auditlog> getListLog(IAuditLog object) {
    try {
      return this.auditLogManager.listLogs(object.getClass(), Long.parseLong(object.getId().toString()),
        this.getActionName(), this.getActualPhase().getId());
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<Auditlog>();
    }
  }

  /**
   * Define default locale while we decide to support other languages in the
   * future.
   */
  @Override
  public Locale getLocale() {
    return Locale.ENGLISH;
  }

  public ClusterType getManagementClusterType() {
    ClusterType clusterType = new ClusterType();

    List<ClusterType> clusterTypes = new ArrayList<>();
    clusterTypes = clusterTypeManager.findAll();
    if (clusterTypes != null && !clusterTypes.isEmpty()) {
      if (clusterTypes.stream().filter(c -> c.getName().contains("Management")).collect(Collectors.toList()) != null
        && !clusterTypes.stream().filter(c -> c.getName().contains("Management")).collect(Collectors.toList())
          .isEmpty()) {
        clusterType =
          clusterTypes.stream().filter(c -> c.getName().contains("Management")).collect(Collectors.toList()).get(0);
      }
    }
    return clusterType;
  }

  /**
   * get all active messages of MARLO messages table
   *
   * @return List of MarloMessages
   */
  public List<MarloMessage> getMarloMessages() {
    return this.marloMessageManager.findAll();
  }

  public StringBuilder getMissingFields() {
    return this.missingFields;
  }

  public String getNamespace() {
    return ServletActionContext.getActionMapping().getNamespace();
  }

  /**
   * get the number of users log in in the application
   *
   * @return the number of users online
   */
  public int getOnline() {
    if (SessionCounter.users != null) {
      return SessionCounter.users.size();
    }
    return 0;
  }

  public String getOnlyReadHtmlText(String htmlText) {
    Document doc = Jsoup.parse(htmlText);
    Elements elements = doc.select("P, [href]");
    int i = 0;
    String text = "";
    for (Element element : elements) {
      switch (element.tagName()) {
        case "p":
          String simpleText = element.text();
          text = text + "\n " + simpleText;
          break;
      }
      i++;
    }
    return text;
  }

  public List<Deliverable> getOpenDeliverables(List<Deliverable> deliverables) {

    List<Deliverable> openDeliverables = new ArrayList<>();

    for (Deliverable a : deliverables) {

      if (a.isActive() && (a.getDeliverableInfo(this.getActualPhase()).getStatus() == null
        || a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
          .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
        || a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
          .parseInt(ProjectStatusEnum.Extended.getStatusId())
        || a.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == 0)) {

        if (a.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null) {
          if (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            if (a.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() >= this.getCurrentCycleYear()) {
              openDeliverables.add(a);
            }
          } else {
            if (a.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getCurrentCycleYear()) {
              openDeliverables.add(a);
            }
          }
        } else {
          if (a.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getCurrentCycleYear()) {
            openDeliverables.add(a);
          }
        }
      }
    }

    return openDeliverables;

  }

  /**
   * Validate if a given user exists in the Outlook Active Directory .
   *
   * @param email is the CGIAR email.
   * @return a populated user with all the information that is coming from the
   *         OAD, or null if the email does not exist.
   */
  public LDAPUser getOutlookUser(String email) {
    LDAPService service = new LDAPService();
    if (this.config.isProduction()) {
      service.setInternalConnection(false);
    } else {
      service.setInternalConnection(true);
    }
    LDAPUser user = null;
    try {
      user = service.searchUserByEmail(email);
    } catch (Exception e) {
      user = null;
    }
    return user;
  }

  public Map<String, Parameter> getParameters() {
    this.parameters = ActionContext.getContext().getParameters();
    return this.parameters;
  }

  public String getParameterValue(String param) {
    Object paramObj = this.getParameters().get(param);
    if (paramObj == null) {
      return null;
    }
    return ((String[]) paramObj)[0];
  }

  public Long getPhaseID() {
    return this.phaseID;
  }

  /**
   * validate if the list of phases are on session if not, will be find on bd
   *
   * @return the list of the phases for the crp
   */
  public List<Phase> getPhases() {
    if (this.getSession().containsKey(APConstants.PHASES)) {
      return (List<Phase>) this.getSession().get(APConstants.PHASES);
    } else {
      List<Phase> phases = this.phaseManager.findAll().stream().filter(
        c -> c.getCrp().getId().longValue() == this.getCrpID().longValue() && c.getVisible() != null && c.getVisible())
        .collect(Collectors.toList());
      phases.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
      this.getSession().put(APConstants.PHASES, phases);
      return phases;
    }
  }

  /*
   * @return the list of phases for upkeep
   */
  public List<Phase> getPhasesByCycles(List<String> reportCycles) {
    List<Phase> phasesFilter = new ArrayList<>();
    List<Phase> phases = this.phaseManager.findAll().stream()
      .filter(
        c -> c.getCrp().getId().longValue() == this.getCrpID().longValue() && c.getVisible() != null && c.getVisible())
      .collect(Collectors.toList());
    for (int i = 0; i < phases.size(); i++) {
      for (int j = 0; j < reportCycles.size(); j++) {
        if (phases.get(i).getDescription().equalsIgnoreCase(reportCycles.get(j))) {
          phasesFilter.add(phases.get(i));
        }
      }
    }

    return phasesFilter;
  }

  public List<Phase> getPhasesImpact() {
    if (this.getSession().containsKey(APConstants.PHASES_IMPACT)) {
      return (List<Phase>) this.getSession().get(APConstants.PHASES_IMPACT);
    } else {
      List<Phase> phases = this.phaseManager
        .findAll().stream().filter(c -> c.getCrp().getId().longValue() == this.getCrpID().longValue()
          && c.getVisible() != null && c.getVisible() && c.getDescription().equals(APConstants.PLANNING))
        .collect(Collectors.toList());
      phases.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
      this.getSession().put(APConstants.PHASES_IMPACT, phases);
      return phases;
    }
  }

  public String getPhasesImpactJson() {
    List<Phase> phases;
    if (this.getSession().containsKey(APConstants.PHASES_IMPACT)) {
      phases = (List<Phase>) this.getSession().get(APConstants.PHASES_IMPACT);
    } else {
      phases = this.phaseManager
        .findAll().stream().filter(c -> c.getCrp().getId().longValue() == this.getCrpID().longValue()
          && c.getVisible() != null && c.getVisible() && c.getDescription().equals(APConstants.PLANNING))
        .collect(Collectors.toList());
      phases.sort((p1, p2) -> new Integer(p1.getYear()).compareTo(new Integer(p2.getYear())));
      this.getSession().put(APConstants.PHASES_IMPACT, phases);
    }

    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    return gson.toJson(phases);
  }

  /**
   * validate if the list of phases are on session if not, will be find on bd
   * on json format
   *
   * @return the jsons of the phases for the crp
   */
  public String getPhasesJson() {
    List<Phase> phases;
    if (this.getSession().containsKey(APConstants.PHASES)) {
      phases = (List<Phase>) this.getSession().get(APConstants.PHASES);
    } else {
      phases = this.phaseManager.findAll().stream().filter(
        c -> c.getCrp().getId().longValue() == this.getCrpID().longValue() && c.getVisible() != null && c.getVisible())
        .collect(Collectors.toList());
      phases.sort((p1, p2) -> new Integer(p1.getYear()).compareTo(new Integer(p2.getYear())));
      this.getSession().put(APConstants.PHASES, phases);
    }
    GsonBuilder builder = new GsonBuilder();
    Gson gson = builder.create();
    return gson.toJson(phases);
  }

  public int getPlanningYear() {
    return Integer.parseInt(this.getSession().get(APConstants.CRP_PLANNING_YEAR).toString());

  }

  public List<GlobalUnit> getPlatformsList() {
    List<GlobalUnit> centers = new ArrayList<>();
    if (!this.canAccessSuperAdmin()) {
      User user = this.getCurrentUser();
      user = this.userManager.getUser(user.getId());
      List<CrpUser> users =
        new ArrayList<>(user.getCrpUsers().stream().filter(u -> u.isActive()).collect(Collectors.toList()));

      for (CrpUser crpUser : users) {
        long guType = crpUser.getCrp().getGlobalUnitType().getId();
        if (crpUser.getCrp().isActive() && guType == 3) {
          centers.add(crpUser.getCrp());
        }
      }
      return centers;
    } else {
      return this.crpManager.findAll().stream().filter(c -> c.isActive() && c.getGlobalUnitType().getId() == 3)
        .collect(Collectors.toList());
    }
  }

  public List<Integer> getPoliciesYears(Long policy) {
    List<ProjectPolicyInfo> projectPolicyInfoList =
      this.projectPolicyInfoManager.findAll().stream()
        .filter(c -> c != null && c.getProjectPolicy() != null
          && c.getProjectPolicy().getId().longValue() == policy.longValue()
          && c.getPhase().getName().equals(APConstants.PROJECT_INDICATOR_PHASE_PREVIOUS_NAME)
          && c.getPhase().getYear() == (this.getActualPhase().getYear()))
        .collect(Collectors.toList());
    List<Integer> allYears = new ArrayList<>();
    if (projectPolicyInfoList.size() > 0) {
      if (projectPolicyInfoList.get(0).getYear() != this.getActualPhase().getYear()) {
        allYears.add(projectPolicyInfoList.get(0).getYear().intValue());
      } else {
        List<ProjectPolicyInfo> projectPolicyInfoList2 = this.projectPolicyInfoManager.findAll().stream()
          .filter(c -> c != null && c.getProjectPolicy() != null
            && c.getProjectPolicy().getId().longValue() == policy.longValue()
            && c.getPhase().getName().equals(APConstants.PROJECT_INDICATOR_PHASE_PREVIOUS_NAME)
            && c.getPhase().getYear() < (this.getActualPhase().getYear()))
          .collect(Collectors.toList());
        if (projectPolicyInfoList2.size() > 0) {
          allYears.add(projectPolicyInfoList2.get(0).getYear().intValue());
        }
      }

    }
    allYears.add(this.getActualPhase().getYear());
    return allYears;
  }

  /**
   * Check the powb Synthesis Section Status
   *
   * @param section
   * @return
   */
  public boolean getPowbSynthesisSectionStatus(String sectionName, long powbSynthesisID) {

    boolean returnValue = false;
    SectionStatus sectionStatus;

    PowbSynthesis powbSynthesis = this.powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);

    if (PowbSynthesisSectionStatusEnum.value(sectionName.toUpperCase()) == null) {
      return false;
    }

    switch (PowbSynthesisSectionStatusEnum.value(sectionName.toUpperCase())) {
      case FLAGSHIP_PLANS:
        if (this.isPowbFlagship(powbSynthesis.getLiaisonInstitution())) {
          sectionStatus =
            this.sectionStatusManager.getSectionStatusByPowbSynthesis(powbSynthesis.getId(), this.getCurrentCycle(),
              powbSynthesis.getPhase().getYear(), powbSynthesis.getPhase().getUpkeep(), sectionName);

          if (sectionStatus == null) {
            return false;
          }
          if (sectionStatus.getMissingFields().length() > 0) {
            return false;
          }
        }
        returnValue = true;
        break;
      case EVIDENCES:
        if (this.isPowbPMU(powbSynthesis.getLiaisonInstitution())) {
          sectionStatus =
            this.sectionStatusManager.getSectionStatusByPowbSynthesis(powbSynthesis.getId(), this.getCurrentCycle(),
              powbSynthesis.getPhase().getYear(), powbSynthesis.getPhase().getUpkeep(), sectionName);
          if (sectionStatus == null) {
            return false;
          }
          if (sectionStatus.getMissingFields().length() > 0) {
            return false;
          }
        }
        returnValue = true;
        break;
      case CROSS_CUTTING_DIMENSIONS:
      case STAFFING:
      case FINANCIAL_PLAN:
      case MANAGEMENT_GOVERNANCE:
      case MANAGEMENT_RISK:
        if (this.isPowbPMU(powbSynthesis.getLiaisonInstitution())) {
          sectionStatus =
            this.sectionStatusManager.getSectionStatusByPowbSynthesis(powbSynthesis.getId(), this.getCurrentCycle(),
              powbSynthesis.getPhase().getYear(), powbSynthesis.getPhase().getUpkeep(), sectionName);
          if (sectionStatus == null) {
            return false;
          }
          if (sectionStatus.getMissingFields().length() > 0) {
            return false;
          }
        }
        returnValue = true;
        break;

      default:
        sectionStatus =
          this.sectionStatusManager.getSectionStatusByPowbSynthesis(powbSynthesis.getId(), this.getCurrentCycle(),
            powbSynthesis.getPhase().getYear(), powbSynthesis.getPhase().getUpkeep(), sectionName);
        if (sectionStatus == null) {
          return false;
        }
        if (sectionStatus.getMissingFields().length() > 0) {
          return false;
        }
        returnValue = true;
        break;
    }

    return returnValue;
  }

  public boolean getPowbSynthesisSectionStatus2019(String sectionName, long powbSynthesisID) {

    boolean returnValue = false;
    SectionStatus sectionStatus;

    PowbSynthesis powbSynthesis = this.powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);

    if (PowbSynthesis2019SectionStatusEnum.value(sectionName.toUpperCase()) == null) {
      return false;
    }

    switch (PowbSynthesis2019SectionStatusEnum.value(sectionName.toUpperCase())) {
      default:
        sectionStatus =
          this.sectionStatusManager.getSectionStatusByPowbSynthesis(powbSynthesis.getId(), this.getCurrentCycle(),
            powbSynthesis.getPhase().getYear(), powbSynthesis.getPhase().getUpkeep(), sectionName);
        if (sectionStatus == null) {
          return false;
        }
        if (sectionStatus.getMissingFields().length() > 0) {
          return false;
        }
        returnValue = true;
        break;
    }

    return returnValue;
  }

  public List<Submission> getPowbSynthesisSubmissions(long powbSynthesisID) {
    PowbSynthesis powbSynthesis = this.powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
    List<Submission> submissions = powbSynthesis
      .getSubmissions().stream().filter(c -> c.getCycle().equals(this.getCurrentCycle())
        && c.getYear().intValue() == this.getCurrentCycleYear() && (c.isUnSubmit() == null || !c.isUnSubmit()))
      .collect(Collectors.toList());
    if (submissions.isEmpty()) {
      return new ArrayList<>();
    }
    return submissions;
  }

  public List<CrpPpaPartner> getPpaPartners() {
    List<CrpPpaPartner> crpPpaPartners =
      this.phaseManager.getPhaseById(this.getActualPhase().getId()).getCrpPpaPartner().stream()
        .filter(c -> c.isActive() && c.getCrp().equals(this.getCurrentCrp())).collect(Collectors.toList());
    if (crpPpaPartners != null && !crpPpaPartners.isEmpty()) {
      crpPpaPartners.sort((i1, i2) -> i1.getInstitution().getName().compareTo(i2.getInstitution().getName()));
      return crpPpaPartners;
    } else {
      return new ArrayList<>();
    }
  }

  public Boolean getProjectLp6ContributionValue(long projectID, long phaseID) {
    try {
      ProjectLp6Contribution projectLp6Contribution = new ProjectLp6Contribution();
      Boolean value = null;
      if (projectID != 0 && phaseID != 0) {
        projectLp6Contribution = this.projectLp6ContributionManager.findAll().stream()
          .filter(c -> c.isActive() && c.getProject().getId() == projectID && c.getPhase().getId() == phaseID)
          .collect(Collectors.toList()).get(0);

        if (projectLp6Contribution != null) {
          value = projectLp6Contribution.isContribution();
        }
      }
      return value;
    } catch (Exception e) {
      return false;
    }
  }

  public ProjectOutcome getProjectOutcomeRelationImpact(long phaseId, long projectId, long outcomeId) {
    ProjectOutcome projectOutcome = new ProjectOutcome();
    Phase phase = phaseManager.getPhaseById(phaseId);
    List<ProjectOutcome> projectOutcomes = new ArrayList<>();
    if (phase != null) {
      projectOutcomes = projectOutcomeManager.getProjectOutcomeByPhase(phase);
      try {
        if (projectOutcomes != null && !projectOutcomes.isEmpty()) {
          projectOutcomes = projectOutcomes.stream()
            .filter(po -> po.getProject() != null && po.getProject().getId().equals(projectId)
              && po.getCrpProgramOutcome() != null && po.getCrpProgramOutcome().getId().equals(outcomeId))
            .collect(Collectors.toList());

          if (projectOutcomes != null && !projectOutcomes.isEmpty() && projectOutcomes.get(0) != null) {
            projectOutcome = projectOutcomes.get(0);
          }
        }
      } catch (Exception e) {
        LOG.error("Project outcome relation impact ", e);
      }
    }
    return projectOutcome;
  }

  public SectionStatus getProjectOutcomeStatus(long projectOutcomeID) {
    ProjectOutcome projectOutcome = this.projectOutcomeManager.getProjectOutcomeById(projectOutcomeID);

    List<SectionStatus> sectionStatuses = projectOutcome.getSectionStatuses().stream()
      .filter(c -> c.getYear() == this.getCurrentCycleYear() && c.getCycle().equals(this.getCurrentCycle()))
      .collect(Collectors.toList());

    if (!sectionStatuses.isEmpty()) {
      return sectionStatuses.get(0);
    }
    return null;
  }

  public List<Project> getProjectRelationsImpact(Long id, String className) {
    Class<?> clazz;
    List<Project> projects = null;
    try {
      clazz = Class.forName(className);
      if (clazz == CrpProgramOutcome.class) {
        CrpProgramOutcome crpProgramOutcome = this.crpProgramOutcomeManager.getCrpProgramOutcomeById(id);
        List<ProjectOutcome> outcomes = crpProgramOutcome.getProjectOutcomes().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList());
        Set<Project> projectsSet = new HashSet<>();
        for (ProjectOutcome projectOutcome : outcomes) {
          projectOutcome.getProject().getProjecInfoPhase(this.getActualPhase());
          if (projectOutcome.getProject().getProjectInfo() != null) {
            projectsSet.add(projectOutcome.getProject());
          }

        }
        projects = new ArrayList<>();
        projects.addAll(projectsSet);
      }

      if (clazz == CrpClusterKeyOutputOutcome.class) {
        CrpClusterKeyOutputOutcome clusterKeyOutputOutcome =
          this.crpClusterKeyOutputOutcomeManager.getCrpClusterKeyOutputOutcomeById(id);
        CrpProgramOutcome crpProgramOutcome = this.crpProgramOutcomeManager
          .getCrpProgramOutcomeById(clusterKeyOutputOutcome.getCrpProgramOutcome().getId());
        List<ProjectOutcome> outcomes = crpProgramOutcome.getProjectOutcomes().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList());
        Set<Project> projectsSet = new HashSet<>();
        for (ProjectOutcome projectOutcome : outcomes) {
          projectOutcome.getProject().getProjecInfoPhase(this.getActualPhase());
          if (projectOutcome.getProject().getProjectInfo() != null) {
            projectsSet.add(projectOutcome.getProject());
          }

        }
        List<Deliverable> deliverables =
          this.getDeliverableRelationsImpact(clusterKeyOutputOutcome.getCrpClusterKeyOutput().getId(),
            clusterKeyOutputOutcome.getCrpClusterKeyOutput().getClass().getName());
        List<Project> deProjects = new ArrayList<>();
        for (Deliverable deliverable : deliverables) {
          deProjects.add(deliverable.getProject());
        }

        List<Project> projectsList = new ArrayList<>();
        projectsList.addAll(projectsSet);
        projects = this.intersection(projectsList, deProjects);

      }
      if (clazz == CrpClusterOfActivity.class) {
        CrpClusterOfActivity crpClusterOfActivity = this.crpClusterOfActivityManager.getCrpClusterOfActivityById(id);
        List<ProjectClusterActivity> activities = crpClusterOfActivity.getProjectClusterActivities().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList());
        Set<Project> projectsSet = new HashSet<>();
        for (ProjectClusterActivity crProjectClusterActivity : activities) {
          crProjectClusterActivity.getProject().getProjecInfoPhase(this.getActualPhase());
          if (crProjectClusterActivity.getProject().getProjectInfo() != null) {
            projectsSet.add(crProjectClusterActivity.getProject());
          }
          projectsSet.add(crProjectClusterActivity.getProject());
        }
        projects = new ArrayList<>();
        projects.addAll(projectsSet);
      }
      if (clazz == CrpMilestone.class) {
        CrpMilestone crpMilestone = this.crpMilestoneManager.getCrpMilestoneById(id);
        List<ProjectMilestone> projectMilestones = crpMilestone
          .getProjectMilestones().stream().filter(c -> c.isActive() && c.getProjectOutcome().getPhase() != null
            && c.getProjectOutcome().isActive() && c.getProjectOutcome().getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList());
        Set<Project> projectsSet = new HashSet<>();
        for (ProjectMilestone projectMilestone : projectMilestones) {
          projectMilestone.getProjectOutcome().getProject().getProjecInfoPhase(this.getActualPhase());
          if (projectMilestone.getProjectOutcome().getProject().getProjectInfo() != null) {
            projectsSet.add(projectMilestone.getProjectOutcome().getProject());
          }

        }
        projects = new ArrayList<>();
        projects.addAll(projectsSet);
      }
      if (clazz == CrpPpaPartner.class) {
        CrpPpaPartner crpPpaPartner = this.crpPpaPartnerManager.getCrpPpaPartnerById(id);

        List<ProjectPartner> partners =
          crpPpaPartner.getInstitution().getProjectPartners().stream()
            .filter(c -> c.isActive() && c.getPhase() != null
              && c.getPhase().getId().equals(this.getActualPhase().getId())
              && c.getProject().getGlobalUnitProjects().stream()
                .filter(gup -> gup.isActive() && gup.isOrigin() && gup.getGlobalUnit().getId().equals(this.getCrpID()))
                .collect(Collectors.toList()).size() > 0)
            .collect(Collectors.toList());
        Set<Project> projectsSet = new HashSet<>();
        for (ProjectPartner projectPartner : partners) {
          projectsSet.add(projectPartner.getProject());
        }
        projects = new ArrayList<>();
        projects.addAll(projectsSet);

      }
    } catch (Exception e) {
      LOG.error("Project outcome status ", e);
    }

    List<Project> avaliableProjects = new ArrayList<>();
    if (projects != null) {
      for (Project project : projects) {
        if (project.getProjecInfoPhase(this.getActualPhase()) != null
          && project.getProjecInfoPhase(this.getActualPhase()).getPhase().equals(this.getActualPhase())) {
          if (project.getProjecInfoPhase(this.getActualPhase()).getStatus().longValue() == Long
            .parseLong(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjecInfoPhase(this.getActualPhase()).getStatus().longValue() == Long
              .parseLong(ProjectStatusEnum.Extended.getStatusId())) {
            if (project.getProjecInfoPhase(this.getActualPhase()).getEndDate() != null) {
              Calendar cal = Calendar.getInstance();
              cal.setTime(project.getProjecInfoPhase(this.getActualPhase()).getEndDate());
              if (cal.get(Calendar.YEAR) >= this.getActualPhase().getYear()) {
                avaliableProjects.add(project);
              }
            }

          }

        }

      }
      projects.clear();
      projects.addAll(avaliableProjects);
    }

    return projects;

  }

  /**
   * Green checks in Projects Menu
   */
  public boolean getProjectSectionStatus(String section, long projectID) {
    boolean returnValue = false;
    SectionStatus sectionStatus;
    Project project;

    if (section != null && !section.isEmpty()) {
      if (ProjectSectionStatusEnum.value(section.toUpperCase()) == null) {
        return false;
      }
      switch (ProjectSectionStatusEnum.value(section.toUpperCase())) {
        case OUTCOMES:
          project = this.projectManager.getProjectById(projectID);

          // Validate LP6 Contribution question
          if (this.hasSpecificities(APConstants.CRP_LP6_ACTIVE) && this.isReportingActive()) {

            List<ProjectLp6Contribution> projectLp6Contributions = project.getProjectLp6Contributions().stream()
              .filter(pl -> pl.isActive() && pl.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());
            if (projectLp6Contributions != null && !projectLp6Contributions.isEmpty()) {
              ProjectLp6Contribution projectLp6Contribution = projectLp6Contributions.get(0);
              if (projectLp6Contribution.getContribution() == null) {
                return false;
              }
            } else {
              return false;
            }

          }

          List<ProjectOutcome> projectOutcomes = project.getProjectOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
            .collect(Collectors.toList());

          project.setOutcomes(projectOutcomes);

          if (!(project.getProjecInfoPhase(this.getActualPhase()).getAdministrative() != null
            && project.getProjecInfoPhase(this.getActualPhase()).getAdministrative().booleanValue() == true)) {
            if (project.getOutcomes().isEmpty()) {
              return false;
            }
          } else {
            return true;
          }

          for (ProjectOutcome projectOutcome : project.getOutcomes()) {
            sectionStatus = this.sectionStatusManager.getSectionStatusByProjectOutcome(projectOutcome.getId(),
              this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(), section);
            if (sectionStatus == null) {
              return false;
            }
            if (sectionStatus.getMissingFields().length() != 0) {
              return false;
            }
          }

          returnValue = true;
          break;

        case CASESTUDIES:
          project = this.projectManager.getProjectById(projectID);
          List<CaseStudyProject> caseStudies =
            project.getCaseStudyProjects().stream().filter(d -> d.isActive()).collect(Collectors.toList());
          List<CaseStudy> caStudies = new ArrayList<>();

          for (CaseStudyProject caseStudyProject : caseStudies) {
            if (caseStudyProject.isActive()
              && caseStudyProject.getCaseStudy().getYear() == this.getCurrentCycleYear()) {
              caStudies.add(caseStudyProject.getCaseStudy());
              sectionStatus =
                this.sectionStatusManager.getSectionStatusByCaseStudy(caseStudyProject.getCaseStudy().getId(),
                  this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(), section);
              if (sectionStatus == null) {
                return false;

              }
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;

              }
            }
          }
          if (caStudies.isEmpty()) {
            return true;
          }
          returnValue = true;
          break;

        case HIGHLIGHTS:
          project = this.projectManager.getProjectById(projectID);
          List<ProjectHighlight> highlights = project.getProjectHighligths().stream()
            .filter(d -> d.getProjectHighlightInfo(this.getActualPhase()) != null && d.isActive()
              && d.getProjectHighlightInfo(this.getActualPhase()).getYear().intValue() == this.getCurrentCycleYear())
            .collect(Collectors.toList());
          if (highlights.isEmpty()) {
            return true;
          }

          for (ProjectHighlight highlight : highlights) {

            sectionStatus = this.sectionStatusManager.getSectionStatusByProjectHighlight(highlight.getId(),
              this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(), section);
            if (sectionStatus == null) {
              return false;

            }
            if (sectionStatus.getMissingFields().length() > 0) {
              return false;

            }

          }

          returnValue = true;
          break;
        case DELIVERABLES:
          project = this.projectManager.getProjectById(projectID);

          Phase phase = this.getActualPhase();
          List<Deliverable> deliverables = new ArrayList<>();

          if (project.getDeliverables() != null) {

            List<DeliverableInfo> infos =
              this.deliverableInfoManager.getDeliverablesInfoByProjectAndPhase(phase, project);

            if (infos != null && !infos.isEmpty()) {
              for (DeliverableInfo deliverableInfo : infos) {
                Deliverable deliverable = deliverableInfo.getDeliverable();
                deliverable.setDeliverableInfo(deliverableInfo);
                deliverables.add(deliverable);
              }
            }
          }

          for (Deliverable deliverable : deliverables) {

            if (!this.isDeliverableComplete(deliverable.getId(), this.getActualPhase().getId())) {
              return false;
            }

            // sectionStatus =
            // this.sectionStatusManager.getSectionStatusByDeliverable(deliverable.getId(),
            // this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(),
            // section);
            // if (sectionStatus == null) {
            //
            // return false;
            // } else {
            // if (deliverable.getDeliverableInfo(phase).getStatus() != null &&
            // deliverable.getDeliverableInfo(phase)
            // .getStatus().intValue() ==
            // Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
            // if (deliverable.getDeliverableInfo(phase).getYear() >
            // this.getActualPhase().getYear()) {
            // sectionStatus.setMissingFields("");
            // }
            // }
            //
            // if (this.isPlanningActive() && !this.isUpKeepActive()) {
            // if (deliverable.getDeliverableInfo(phase).getStatus() != null &&
            // deliverable.getDeliverableInfo(phase)
            // .getStatus().intValue() ==
            // Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())) {
            // sectionStatus.setMissingFields("");
            // }
            // }
            // }
            //
            // if (sectionStatus.getMissingFields().length() != 0) {
            // return false;
            // }
          }

          returnValue = true;

          break;

        case ACTIVITIES:
          project = this.projectManager.getProjectById(projectID);

          project.setProjectActivities(new ArrayList<Activity>(project.getActivities().stream()
            .filter(a -> a.isActive() && a.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));

          if (project.getProjectActivities().isEmpty()) {
            return true;
          }

          sectionStatus = this.sectionStatusManager.getSectionStatusByProject(projectID, this.getCurrentCycle(),
            this.getCurrentCycleYear(), this.isUpKeepActive(), section);

          if (this.isAiccra()) {
            // Check if exist deliverables without activities
            List<Deliverable> deliverablesMissingActivity = new ArrayList<>();

            project.getCurrentDeliverables(this.getActualPhase()).stream()
              .filter((deliverable) -> (deliverable.getDeliverableActivities().isEmpty()
                || deliverable.getDeliverableActivities().stream().filter(da -> da.isActive())
                  .collect(Collectors.toList()).isEmpty()
                || deliverable.getDeliverableActivities().stream()
                  .filter(da -> da.getPhase().getId().equals(this.getActualPhase().getId())
                    && da.getActivity().isActive() && da.isActive())
                  .collect(Collectors.toList()).isEmpty()))
              .forEachOrdered((_item) -> {
                deliverablesMissingActivity.add(_item);
              });


            if (deliverablesMissingActivity != null && !deliverablesMissingActivity.isEmpty()) {
              // this.addMessage(this.getText("missingDeliverableActivity", "deliverable.missing.activity"));
              // this.getInvalidFields().put("list-deliverable.missing.activity.alert",
              // InvalidFieldsMessages.EMPTYFIELD);
              /*
               * SectionStatus status = null;
               * status = new SectionStatus();
               * status.setCycle(this.getCurrentCycle());
               * status.setYear(this.getCurrentCycleYear());
               * status.setUpkeep(this.isUpKeepActive());
               * status.setProject(project);
               * status.setSectionName(ProjectSectionStatusEnum.ACTIVITIES.getStatus());
               * status.setMissingFields("missingDeliverableActivity");
               * sectionStatusManager.saveSectionStatus(status);
               */
              return false;
            }
          }


          if (sectionStatus != null) {
            if (sectionStatus.getMissingFields().length() == 0) {
              return true;
            }
          }
          break;

        case BUDGET:
          if (this.isReportingActive()) {
            return true;
          } else {
            if (!this.hasSpecificities(this.getCrpEnableBudgetExecution())) {
              return true;
            }
            project = this.projectManager.getProjectById(projectID);
            if (project.getProjectBudgets().stream()
              .filter(d -> d.isActive() && d.getPhase() != null && d.getPhase().equals(this.getActualPhase()))
              .collect(Collectors.toList()).isEmpty()) {
              return false;
            }
            if (this.isReportingActive() && this.hasSpecificities(this.getCrpEnableBudgetExecution())) {
              if (project.getProjectBudgetExecutions().stream()
                .filter(d -> d.isActive() && d.getPhase() != null && d.getPhase().equals(this.getActualPhase()))
                .collect(Collectors.toList()).isEmpty()) {
                return false;
              }
            }

            sectionStatus = this.sectionStatusManager.getSectionStatusByProject(projectID, this.getCurrentCycle(),
              this.getCurrentCycleYear(), this.isUpKeepActive(), section);
            if (sectionStatus != null) {
              if (sectionStatus.getMissingFields().length() == 0) {
                return true;
              }
            }
          }
          break;

        case DESCRIPTION:
        case LOCATIONS:

          sectionStatus = this.sectionStatusManager.getSectionStatusByProject(projectID, this.getCurrentCycle(),
            this.getCurrentCycleYear(), this.isUpKeepActive(), section);
          if (sectionStatus != null) {
            if (sectionStatus.getMissingFields().length() == 0) {
              return true;
            }
          } else {
            if (this.isReportingActive()) {
              return true;
            }
          }

          break;

        case EXPECTEDSTUDIES:

          project = this.projectManager.getProjectById(projectID);
          List<ProjectExpectedStudy> allProjectStudies = new ArrayList<ProjectExpectedStudy>();

          // Load Studies
          List<ProjectExpectedStudy> studies = project.getProjectExpectedStudies().stream()
            .filter(c -> c.isActive() && c.getProjectExpectedStudyInfo(this.getActualPhase()) != null)
            .collect(Collectors.toList());
          if (studies != null && studies.size() > 0) {
            allProjectStudies.addAll(studies);
          }

          List<ProjectExpectedStudy> projectStudies = new ArrayList<ProjectExpectedStudy>();

          if (allProjectStudies != null && allProjectStudies.size() > 0) {
            // Editable project studies: Current cycle year-1 will be
            // editable except Complete and Cancelled.
            // Every study of the current cycle year will be editable
            projectStudies = allProjectStudies.stream()
              .filter(ps -> ps.getProjectExpectedStudyInfo().getYear() != null
                && ps.getProjectExpectedStudyInfo().getStatus() != null
                && ps.getProjectExpectedStudyInfo().getYear() >= this.getCurrentCycleYear())
              .collect(Collectors.toList());
          }

          if (projectStudies.isEmpty()) {
            return true;
          }

          for (ProjectExpectedStudy projectExpectedStudy : projectStudies) {
            sectionStatus =
              this.sectionStatusManager.getSectionStatusByProjectExpectedStudy(projectExpectedStudy.getId(),
                this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(), section);
            if (sectionStatus != null) {
              if (sectionStatus.getMissingFields() != null && sectionStatus.getMissingFields().length() != 0) {
                return false;
              }
            } else {
              return false;
            }
          }
          returnValue = true;
          break;

        case INNOVATIONS:

          project = this.projectManager.getProjectById(projectID);
          List<ProjectInnovation> innovations = project.getProjectInnovations().stream()
            .filter(c -> c.getProjectInnovationInfo(this.getActualPhase()) != null && c.isActive()
              && c.getProjectInnovationInfo(this.getActualPhase()).getYear().intValue() == this.getCurrentCycleYear())
            .collect(Collectors.toList());

          if (innovations.isEmpty()) {
            return true;
          }

          for (ProjectInnovation projectInnovation : innovations) {
            sectionStatus = this.sectionStatusManager.getSectionStatusByProjectInnovation(projectInnovation.getId(),
              this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(), section);
            if (sectionStatus != null) {
              if (sectionStatus.getMissingFields().length() != 0) {
                return false;
              }
            } else {
              return false;
            }
          }
          returnValue = true;

          break;

        case POLICIES:

          project = this.projectManager.getProjectById(projectID);
          List<ProjectPolicy> policies = project.getProjectPolicies().stream()
            .filter(c -> c.getProjectPolicyInfo(this.getActualPhase()) != null && c.isActive()
              && c.getProjectPolicyInfo(this.getActualPhase()).getYear().intValue() == this.getCurrentCycleYear())
            .collect(Collectors.toList());

          for (ProjectPolicy projectPolicy : policies) {
            sectionStatus = this.sectionStatusManager.getSectionStatusByProjectPolicy(projectPolicy.getId(),
              this.getCurrentCycle(), this.getCurrentCycleYear(), this.isUpKeepActive(), section);
            if (sectionStatus != null) {
              if (sectionStatus.getMissingFields().length() != 0) {
                return false;
              }
            } else {
              return false;
            }
          }
          returnValue = true;

          break;

        case LEVERAGES:

          sectionStatus = this.sectionStatusManager.getSectionStatusByProject(projectID, this.getCurrentCycle(),
            this.getCurrentCycleYear(), this.isUpKeepActive(), section);
          if (sectionStatus != null) {
            if (sectionStatus.getMissingFields().length() == 0) {
              return true;
            }
          } else {
            return true;
          }
          returnValue = false;

          break;

        case BUDGETBYFLAGSHIP:
          if (this.isAiccra()) {
            return true;
          } else {
            sectionStatus = this.sectionStatusManager.getSectionStatusByProject(projectID, this.getCurrentCycle(),
              this.getCurrentCycleYear(), this.isUpKeepActive(), section);
            if (sectionStatus != null) {
              if (sectionStatus.getMissingFields().length() == 0) {
                return true;
              }
            }
          }
          break;
        default:
          sectionStatus = this.sectionStatusManager.getSectionStatusByProject(projectID, this.getCurrentCycle(),
            this.getCurrentCycleYear(), this.isUpKeepActive(), section);
          if (sectionStatus != null) {
            if (sectionStatus.getMissingFields().length() == 0) {
              return true;
            }
            break;

          }
      }
    }
    return returnValue;

  }

  public List<Submission> getProjectSubmissions(long projectID) {
    Project project = this.projectManager.getProjectById(projectID);
    List<Submission> submissions = project
      .getSubmissions().stream().filter(c -> c.getCycle().equals(this.getCurrentCycle())
        && c.getYear().intValue() == this.getCurrentCycleYear() && (c.isUnSubmit() == null || !c.isUnSubmit()))
      .collect(Collectors.toList());
    if (submissions.isEmpty()) {
      return new ArrayList<>();
    }
    return submissions;
  }

  /**
   ************************ CENTER METHOD ********************* return true
   * if the user can view the impactPathway
   * ********************************************************* Return the sync
   * code if the center project has synchronized by another project
   *
   * @param centerProjectID
   * @return the Sync Code
   */
  public String getProjectSyncCode(long centerProjectID) {

    CenterProject project = this.projectService.getCenterProjectById(centerProjectID);

    if (project != null) {
      if (project.isAutoFill()) {
        try {
          CenterProjectFundingSource fundingSource = project.getProjectFundingSources().stream()
            .filter(fs -> fs.isActive() && fs.isAutoFill()).collect(Collectors.toList()).get(0);
          if (fundingSource != null) {
            return fundingSource.getCode();
          }
        } catch (Exception e) {
          return "---";
        }

      }
    }

    return "---";
  }

  public long getReportingIndGeographicScopeGlobal() {
    return APConstants.PROJECT_PARTNER_PARTNERSHIP_GLOBAL;
  }

  public long getReportingIndGeographicScopeMultiNational() {
    return APConstants.PROJECT_PARTNER_PARTNERSHIP_MULTI_NATIONAL;
  }

  public long getReportingIndGeographicScopeNational() {
    return APConstants.PROJECT_PARTNER_PARTNERSHIP_NATIONAL;
  }

  public long getReportingIndGeographicScopeRegional() {
    return APConstants.PROJECT_PARTNER_PARTNERSHIP_REGIONAL;
  }

  public long getReportingIndGeographicScopeSubNational() {
    return APConstants.PROJECT_PARTNER_PARTNERSHIP_SUB_NATIONAL;
  }

  public long getReportingIndTypeActivityAcademicDegree() {
    return APConstants.REPORTING_INDICATOR_TYPE_ACTIVITY_ACADEMIC_DEGREE;
  }

  public int getReportingYear() {
    return Integer.parseInt(this.getSession().get(APConstants.CRP_REPORTING_YEAR).toString());
  }

  /**
   * Check the annual report 2018 Section Status
   *
   * @param section
   * @return
   */
  public boolean getReportSynthesis2018SectionStatus(String sectionName, long synthesisID) {

    boolean returnValue = false;
    SectionStatus sectionStatus;

    ReportSynthesis reportSynthesis = this.reportSynthesisManager.getReportSynthesisById(synthesisID);

    if (ReportSynthesis2018SectionStatusEnum.value(sectionName.toUpperCase()) == null) {
      return false;
    }

    switch (ReportSynthesis2018SectionStatusEnum.value(sectionName.toUpperCase())) {
      // case FLAGSHIP_PROGRESS:
      // if (this.isPowbFlagship(reportSynthesis.getLiaisonInstitution())) {
      //
      // sectionStatus =
      // this.sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
      // this.getCurrentCycle(),
      // reportSynthesis.getPhase().getYear(), reportSynthesis.getPhase().getUpkeep(),
      // sectionName);
      //
      // if (sectionStatus == null) {
      // return false;
      // }
      // if (sectionStatus.getMissingFields().length() > 0) {
      // return false;
      // }
      // returnValue = true;
      //
      // }
      // break;

      // case FLAGSHIP_PROGRESS:
      // case CRP_PROGRESS:
      // case EFFICIENCY:
      // case GOVERNANCE:
      // case RISKS:
      // case CC_DIMENSIONS:
      //
      // if (this.isPowbPMU(reportSynthesis.getLiaisonInstitution())) {
      // sectionStatus =
      // this.sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(),
      // this.getCurrentCycle(),
      // reportSynthesis.getPhase().getYear(), reportSynthesis.getPhase().getUpkeep(),
      // sectionName);
      //
      // if (sectionStatus == null) {
      // return false;
      // }
      // if (sectionStatus.getMissingFields().length() > 0) {
      // return false;
      // }
      // returnValue = true;
      // }
      //
      // break;
      default:
        sectionStatus =
          this.sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(), this.getCurrentCycle(),
            reportSynthesis.getPhase().getYear(), reportSynthesis.getPhase().getUpkeep(), sectionName);
        if (sectionStatus == null) {
          return false;
        }
        if (sectionStatus.getMissingFields().length() > 0) {
          return false;
        }
        returnValue = true;
        break;
    }

    return returnValue;
  }

  /**
   * Check the annual report Section Status
   *
   * @param section
   * @return
   */
  public boolean getReportSynthesisSectionStatus(String sectionName, long synthesisID) {

    boolean returnValue = false;
    SectionStatus sectionStatus;

    ReportSynthesis reportSynthesis = this.reportSynthesisManager.getReportSynthesisById(synthesisID);

    if (ReportSynthesisSectionStatusEnum.value(sectionName.toUpperCase()) == null) {
      return false;
    }

    switch (ReportSynthesisSectionStatusEnum.value(sectionName.toUpperCase())) {
      case FLAGSHIP_PROGRESS:
        if (this.isPowbFlagship(reportSynthesis.getLiaisonInstitution())) {

          sectionStatus =
            this.sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(), this.getCurrentCycle(),
              reportSynthesis.getPhase().getYear(), reportSynthesis.getPhase().getUpkeep(), sectionName);

          if (sectionStatus == null) {
            return false;
          }
          if (sectionStatus.getMissingFields().length() > 0) {
            return false;
          }
          returnValue = true;

        }
        break;

      case VARIANCE:
      case FUNDING_USE:
      case EFFICIENCY:
      case GOVERNANCE:
      case RISKS:
      case FINANCIAL_SUMMARY:
      case INFLUENCE:
      case CONTROL:
        if (this.isPowbPMU(reportSynthesis.getLiaisonInstitution())) {
          sectionStatus =
            this.sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(), this.getCurrentCycle(),
              reportSynthesis.getPhase().getYear(), reportSynthesis.getPhase().getUpkeep(), sectionName);

          if (sectionStatus == null) {
            return false;
          }
          if (sectionStatus.getMissingFields().length() > 0) {
            return false;
          }
          returnValue = true;
        }

        break;

      default:
        sectionStatus =
          this.sectionStatusManager.getSectionStatusByReportSynthesis(reportSynthesis.getId(), this.getCurrentCycle(),
            reportSynthesis.getPhase().getYear(), reportSynthesis.getPhase().getUpkeep(), sectionName);
        if (sectionStatus == null) {
          return false;
        }
        if (sectionStatus.getMissingFields().length() > 0) {
          return false;
        }
        returnValue = true;
        break;
    }

    return returnValue;
  }

  public HttpServletRequest getRequest() {
    return this.request;
  }

  public String getRoles() {
    String roles = "";
    User u = this.userManager.getUser(this.getCurrentUser().getId());
    for (UserRole userRole : u.getUserRoles().stream()
      .filter(c -> c.getRole().getCrp().getId().intValue() == this.getCrpID().intValue())
      .collect(Collectors.toList())) {
      if (roles.isEmpty()) {
        roles = userRole.getRole().getAcronym();
      } else {
        roles = roles + "," + userRole.getRole().getAcronym();
      }
    }
    return roles;
  }

  public List<Role> getRolesList() {
    List<Role> roles = new ArrayList<>();
    User u = this.userManager.getUser(this.getCurrentUser().getId());
    for (UserRole userRole : u.getUserRoles().stream()
      .filter(c -> c.getRole().getCrp().getId().intValue() == this.getCrpID().intValue())
      .collect(Collectors.toList())) {
      roles.add(userRole.getRole());
    }
    return roles;
  }

  public BaseSecurityContext getSecurityContext() {
    return this.securityContext;
  }

  public Map<String, Object> getSession() {
    return this.session;
  }

  public Submission getSubmission() {
    return this.submission;
  }

  public StringBuilder getSynthesisFlagships() {
    return synthesisFlagships;
  }

  public String getTimeZone() {
    TimeZone timeZone = TimeZone.getDefault();
    String display = timeZone.getDisplayName();
    return display;
  }

  public String getUrl() {
    return this.url;
  }

  public List<UserToken> getUsersOnline() {
    return SessionCounter.users;
  }

  public List<Map<String, Object>> getUsersToActive() {
    return this.usersToActive;
  }

  public StringBuilder getValidationMessage() {
    return this.validationMessage;
  }

  /**
   * Return the artifact version of the Marlo project pom.xml
   *
   * @return the actual Marlo version
   */
  public String getVersion() {
    String version = this.getClass().getPackage().getImplementationVersion();
    if (version == null) {
      Properties prop = new Properties();
      try {
        prop.load(ServletActionContext.getServletContext().getResourceAsStream("/META-INF/MANIFEST.MF"));
        version = prop.getProperty("Implementation-Version");
      } catch (IOException e) {
        LOG.warn("MAINFEST file Does not exist");
      }
    }
    return version;
  }

  /**
   * Years available per CRPs (used in Summaries)
   *
   * @return String of years for a CRP/Platform/Center
   */
  public ArrayList<String> getYears() {
    this.years = new ArrayList<>();
    Set<Integer> yearsSet = new HashSet<>();
    List<Phase> phases = this.getPhases();
    if (phases != null && !phases.isEmpty()) {
      for (Phase phase : phases) {
        yearsSet.add(phase.getYear());
      }
      if (yearsSet != null && !yearsSet.isEmpty()) {
        for (Integer yearInt : yearsSet) {
          this.years.add(yearInt.toString());
        }
        java.util.Collections.sort(this.years);
      }
    }
    return this.years;
  }

  public int goldDataValue(long deliverableID) {
    Deliverable deliverableBD = this.deliverableManager.getDeliverableById(deliverableID);
    int total = 0;

    this.loadQualityCheck(deliverableBD);
    if (deliverableBD.getQualityCheck() != null) {
      if (deliverableBD.getQualityCheck().getQualityAssurance() != null) {
        switch (deliverableBD.getQualityCheck().getQualityAssurance().getId().intValue()) {
          case APConstants.DELIVERABLE_QUALITY_ANSWER_YES_BUT_NO:
            total = total + 25;
            break;

          case APConstants.DELIVERABLE_QUALITY_ANSWER_YES:
            total = total + 50;
            break;
          case APConstants.DELIVERABLE_QUALITY_ANSWER_NO:
            total = total + 5;
            break;
        }

      }
      if (deliverableBD.getQualityCheck().getDataDictionary() != null) {
        switch (deliverableBD.getQualityCheck().getDataDictionary().getId().intValue()) {
          case APConstants.DELIVERABLE_QUALITY_ANSWER_YES_BUT_NO:
            total = total + 25;
            break;

          case APConstants.DELIVERABLE_QUALITY_ANSWER_YES:
            total = total + 50;
            break;
          case APConstants.DELIVERABLE_QUALITY_ANSWER_NO:
            total = total + 5;
            break;
        }

      }
      if (deliverableBD.getQualityCheck().getDataTools() != null) {
        switch (deliverableBD.getQualityCheck().getDataTools().getId().intValue()) {
          case APConstants.DELIVERABLE_QUALITY_ANSWER_YES_BUT_NO:
            total = total + 25;
            break;

          case APConstants.DELIVERABLE_QUALITY_ANSWER_YES:
            total = total + 50;
            break;
          case APConstants.DELIVERABLE_QUALITY_ANSWER_NO:
            total = total + 5;
            break;
        }

      }
    }

    return total;
  }

  /**
   * @param type
   * @param year
   * @param projectID
   * @return true if the project has budget >0 for the current year, type and
   *         project
   */
  public boolean hasBudgets(Long type, int year, long projectID) {
    Project projectBD = this.projectManager.getProjectById(projectID);
    List<ProjectBudget> budgets = projectBD.getProjectBudgets().stream()
      .filter(c -> c.isActive() && c.getYear() == year && c.getPhase().equals(this.getActualPhase())
        && c.getBudgetType().getId().longValue() == type.longValue() && c.getAmount() != null && c.getAmount() >= 0)
      .collect(Collectors.toList());

    boolean haveBudget = false;
    if (budgets != null) {

      for (ProjectBudget projectBudget : budgets) {
        if (projectBudget.getAmount() > 0) {
          haveBudget = true;
        }
      }
    }

    return haveBudget;
  }

  public Boolean hasClusterType(long projectID) {
    Project project = this.projectManager.getProjectById(projectID);
    if (project.getProjecInfoPhase(this.getActualPhase()) != null
      && project.getProjecInfoPhase(this.getActualPhase()).getClusterType() != null
      && project.getProjecInfoPhase(this.getActualPhase()).getClusterType().getId() != null) {
      return true;
    } else {
      return false;
    }
  }

  public Boolean hasDeliverableRule(DeliverableInfo deliverableInfo, String rule) {
    if (deliverableInfo != null && deliverableInfo.getDeliverableType() != null
      && deliverableInfo.getDeliverableType().getId() != null && deliverableInfo.getDeliverableType().getId() != -1) {
      DeliverableType deliverableType =
        this.deliverableTypeManager.getDeliverableTypeById(deliverableInfo.getDeliverableType().getId());
      List<DeliverableTypeRule> deliverableTypeRules = deliverableType.getDeliverableTypeRules().stream()
        .filter(dtr -> dtr.getDeliverableRule().getName().equals(rule)).collect(Collectors.toList());
      if (deliverableType.getDeliverableCategory() != null) {
        deliverableTypeRules.addAll(deliverableType.getDeliverableCategory().getDeliverableTypeRules().stream()
          .filter(dtr -> dtr.getDeliverableRule().getName().equals(rule)).collect(Collectors.toList()));
      }
      if (deliverableTypeRules != null && !deliverableTypeRules.isEmpty()) {
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  /**
   * Validate Missing fields in Funding Sources
   *
   * @return
   */
  public boolean hasFundingSourcesMissingFields(long id) {

    SectionStatus sectionStatus = null;

    FundingSource fundingSource = this.fundingSourceManager.getFundingSourceById(id);

    sectionStatus =
      this.sectionStatusManager.getSectionStatusByFundingSource(fundingSource.getId(), this.getCurrentCycle(),
        this.getCurrentCycleYear(), this.isUpKeepActive(), ProjectSectionStatusEnum.FUNDINGSOURCE.getStatus());

    if (sectionStatus != null) {
      if (sectionStatus.getMissingFields() != null) {
        if (sectionStatus.getMissingFields().trim().equals("")) {
          return false;
        }
      }
    } else {
      return false;
    }

    return true;
  }

  /**
   * Validate Missing fields in Innovations
   *
   * @return
   */
  public boolean hasInnovationMissingFields(long id) {
    SectionStatus sectionStatus = null;

    ProjectInnovation innovation = this.projectInnovationManager.getProjectInnovationById(id);

    sectionStatus =
      this.sectionStatusManager.getSectionStatusByProjectInnovation(innovation.getId(), this.getCurrentCycle(),
        this.getCurrentCycleYear(), this.isUpKeepActive(), ProjectSectionStatusEnum.INNOVATIONS.getStatus());

    if (sectionStatus != null) {
      if (sectionStatus.getMissingFields() != null) {
        if (sectionStatus.getMissingFields().trim().equals("")) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean hasPermission(String fieldName) {
    if (this.basePermission == null) {
      return this.securityContext.hasPermission(fieldName);
    } else {
      if (this.getCrpSession() != null) {
        Phase phase = this.getActualPhase();
        String basePhase = this.getBasePermission().replaceAll(this.getCrpSession(),
          this.getCrpSession() + ":" + phase.getDescription() + ":" + phase.getYear());
        return this.securityContext.hasPermission(basePhase + ":" + fieldName)
          || this.securityContext.hasPermission(basePhase)
          || this.securityContext.hasPermission(this.getBasePermission() + ":" + fieldName);
      } else {
        return this.securityContext.hasPermission(this.getBasePermission() + ":" + fieldName);
      }

    }
  }

  /**
   * ************************ CENTER METHOD ********************* validation
   * base method to check the permission in MARLO centers.
   * ***************************************************************
   *
   * @return true if the user have the permission
   */
  public boolean hasPermissionCenter(String fieldName) {
    if (this.basePermission == null) {
      return this.securityContext.hasPermission(fieldName);
    } else {
      return this.securityContext.hasPermission(this.getBasePermission() + ":" + fieldName);
    }
  }


  public boolean hasPermissionCrpIndicators(long liaisonID) {
    String params[] = {this.getCrpSession(), liaisonID + "",};
    boolean permission =
      this.hasPermissionNoBase(this.generatePermission(Permission.CRP_INDICATORS_PERMISSION, params));
    return permission;
  }

  public boolean hasPermissionDeliverable(String fieldName, long projectIDl, String currentSection) {
    String projectID = String.valueOf(projectIDl);
    Phase phase = this.getActualPhase();
    fieldName = "crp:" + this.getCrpSession() + ":" + phase.getDescription() + ":" + phase.getYear() + ":project:"
      + projectID + ":" + currentSection + ":" + fieldName;

    // fieldName = "crp:AICCRA:Planning:2022:project:102084:deliverableList:addDeliverable";
    if (this.basePermission == null) {
      return this.securityContext.hasPermission(fieldName);
    } else {
      if (this.getCrpSession() != null) {
        phase = this.getActualPhase();
        String basePhase = this.getBasePermission().replaceAll(this.getCrpSession(),
          this.getCrpSession() + ":" + phase.getDescription() + ":" + phase.getYear());
        return this.securityContext.hasPermission(basePhase + ":" + fieldName)
          || this.securityContext.hasPermission(basePhase)
          || this.securityContext.hasPermission(this.getBasePermission() + ":" + fieldName);
      } else {
        return this.securityContext.hasPermission(this.getBasePermission() + ":" + fieldName);
      }

    }
  }


  public boolean hasPermissionNoBase(String fieldName) {

    return this.securityContext.hasPermission(fieldName);

  }

  public boolean hasPermissionSynthesis(long program) {
    String params[] = {this.getCrpSession(), program + "",};
    boolean permission =
      this.hasPermissionNoBase(this.generatePermission(Permission.SYNTHESIS_BY_MOG_PERMISSION, params));
    return permission;
  }

  public boolean hasPersmissionSubmit(long projectId) {
    if (!this.getActualPhase().getUpkeep()) {
      String permission = this.generatePermission(Permission.PROJECT_SUBMISSION_PERMISSION,
        this.getCurrentCrp().getAcronym(), String.valueOf(projectId));
      boolean permissions = this.securityContext.hasPermission(permission);
      return permissions;
    } else {
      return false;
    }
  }

  public boolean hasPersmissionSubmitAR2018(long synthesisId) {
    if (!this.getActualPhase().getUpkeep()) {
      String permission = this.generatePermission(Permission.REPORT_SYNTHESIS_SUBMIT_PERMISSION,
        this.getCurrentCrp().getAcronym(), String.valueOf(synthesisId));
      boolean permissions = this.securityContext.hasPermission(permission);
      return permissions;
    } else {
      return false;
    }
  }

  /**
   * TODO ************************ CENTER METHOD *********************
   * validate if the user can submit the capdev
   * ***************************************************************
   *
   * @return true if the user have the permission
   */
  public boolean hasPersmissionSubmitCapDev(long capDevID) {
    CapacityDevelopment capacityDevelopment = this.capacityDevelopmentService.getCapacityDevelopmentById(capDevID);
    return true;
  }

  public boolean hasPersmissionSubmitImpact() {

    return this.hasPermission("submit");
  }

  /**
   * ************************ CENTER METHOD ********************* validate if
   * the user can submit the impact pathway
   * ***************************************************************
   *
   * @return true if the user have the permission
   */
  public boolean hasPersmissionSubmitIP(long programID) {
    CrpProgram program = this.crpProgramManager.getCrpProgramById(programID);
    String permission = this.generatePermission(Permission.RESEARCH_PROGRAM_SUBMISSION_PERMISSION,
      this.getCurrentCrp().getAcronym(), String.valueOf(program.getId()), String.valueOf(programID));
    boolean permissions = this.securityContext.hasPermission(permission);
    return permissions;
  }

  public boolean hasPersmissionSubmitPowb(long powbSynthesisID) {
    String permission = this.generatePermission(Permission.POWB_SYNTHESIS_SUBMISSION_PERMISSION,
      this.getCurrentCrp().getAcronym(), String.valueOf(powbSynthesisID));
    boolean permissions = this.securityContext.hasPermission(permission);
    return permissions;
  }

  /**
   * ************************ CENTER METHOD ********************* validate if
   * the user can submit the project
   * ***************************************************************
   *
   * @return true if the user have the permission
   */
  public boolean hasPersmissionSubmitProject(long projectID) {
    CenterProject project = this.projectService.getCenterProjectById(projectID);
    CrpProgram program = null;
    String permission =
      this.generatePermissionCenter(Permission.PROJECT_SUBMISSION_PERMISSION, this.getCurrentCrp().getAcronym(),
        String.valueOf(program.getResearchArea().getId()), String.valueOf(program.getId()), String.valueOf(projectID));
    boolean permissions = this.securityContext.hasPermission(permission);
    return permissions;
  }

  public boolean hasPersmissionUnSubmit(long projectId) {
    if (!this.getActualPhase().getUpkeep()) {
      String permission = this.generatePermission(Permission.PROJECT_UNSUBMISSION_PERMISSION,
        this.getCurrentCrp().getAcronym(), String.valueOf(projectId));
      boolean permissions = this.securityContext.hasPermission(permission);
      return permissions;
    } else {
      return false;
    }

  }

  public boolean hasPersmissionUnSubmitImpact(long programID) {
    String permission = this.generatePermission(Permission.IMPACT_PATHWAY_UNSUBMISSION_PERMISSION,
      this.getCurrentCrp().getAcronym(), String.valueOf(programID));
    boolean permissions = this.securityContext.hasPermission(permission);
    return permissions;
  }

  /**
   * Validate Missing fields in Policies
   *
   * @return
   */
  public boolean hasPoliciesMissingFields(long id) {
    SectionStatus sectionStatus = null;

    ProjectPolicy projectPolicy = this.projectPolicyManager.getProjectPolicyById(id);

    sectionStatus =
      this.sectionStatusManager.getSectionStatusByProjectPolicy(projectPolicy.getId(), this.getCurrentCycle(),
        this.getCurrentCycleYear(), this.isUpKeepActive(), ProjectSectionStatusEnum.POLICIES.getStatus());

    if (sectionStatus != null) {
      if (sectionStatus.getMissingFields() != null) {
        if (sectionStatus.getMissingFields().trim().equals("")) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean hasProgramnsRegions() {
    try {
      return Boolean.parseBoolean(this.getSession().get(APConstants.CRP_HAS_REGIONS).toString());
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasProjectOutcomeRelationImpact(long phaseId, long projectId, long outcomeId) {
    boolean existRelation = false;
    Phase phase = phaseManager.getPhaseById(phaseId);
    List<ProjectOutcome> projectOutcomes = new ArrayList<>();
    if (phase != null) {
      projectOutcomes = projectOutcomeManager.getProjectOutcomeByPhase(phase);
      try {
        if (projectOutcomes != null && !projectOutcomes.isEmpty()) {
          projectOutcomes = projectOutcomes.stream()
            .filter(po -> po.getProject() != null && po.getProject().getId().equals(projectId)
              && po.getCrpProgramOutcome() != null && po.getCrpProgramOutcome().getId().equals(outcomeId))
            .collect(Collectors.toList());

          if (projectOutcomes != null && !projectOutcomes.isEmpty() && projectOutcomes.get(0) != null) {
            existRelation = true;
          }
        }
      } catch (Exception e) {
        existRelation = false;
      }
    }
    return existRelation;
  }

  public boolean hasSpecificities(String specificity) {
    try {
      boolean param = Boolean.parseBoolean(this.getSession().get(specificity).toString());
      return param;
    } catch (Exception e) {
      return false;
    }

  }

  /**
   * Validate Missing fields in Project Expected Studies
   *
   * @return
   */
  public boolean hasStudiesMissingFields(String className, long id) {
    SectionStatus sectionStatus = null;
    ProjectExpectedStudy expectedStudy = this.projectExpectedStudyManager.getProjectExpectedStudyById(id);

    sectionStatus =
      this.sectionStatusManager.getSectionStatusByProjectExpectedStudy(expectedStudy.getId(), this.getCurrentCycle(),
        this.getCurrentCycleYear(), this.isUpKeepActive(), ProjectSectionStatusEnum.EXPECTEDSTUDIES.getStatus());

    if (sectionStatus != null) {
      if (sectionStatus.getMissingFields() != null) {
        if (sectionStatus.getMissingFields().trim().equals("")) {
          return true;
        }
      }
    }

    return false;
  }

  public <T> List<T> intersection(List<T> list1, List<T> list2) {
    List<T> list = new ArrayList<T>();

    for (T t : list1) {
      if (list2.contains(t)) {
        list.add(t);
      }
    }

    return list;
  }

  public Boolean isA(long deliverableID) {
    try {
      Deliverable deliverableBD = this.deliverableManager.getDeliverableById(deliverableID);
      this.loadDissemination(deliverableBD);

      if (deliverableBD.getDissemination().getIsOpenAccess() != null
        && deliverableBD.getDissemination().getIsOpenAccess().booleanValue()) {
        return true;
      }

      if (deliverableBD.getDissemination().getIsOpenAccess() == null) {
        return null;
      }
      return false;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * @param role
   * @return true if is the user role
   */
  public boolean isAdmin() {
    return this.securityContext.hasRole("Admin");
  }

  public boolean isAiccra() {
    if (this.getCurrentCrp() != null && this.getCurrentCrp().getId() != null && this.getCurrentCrp().getId() == 45) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Function to validate the annual report version the front will show
   *
   * @return
   */
  public boolean isAnnualReport2018() {
    try {
      if (this.getCurrentCycleYear() >= 2018) {
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public boolean isAr2018Submitted(long synthesisID) {
    if (!this.getActualPhase().getUpkeep()) {

      ReportSynthesis reportSynthesis = this.reportSynthesisManager.getReportSynthesisById(synthesisID);

      List<Submission> submissions = reportSynthesis
        .getSubmissions().stream().filter(c -> c.getCycle().equals(this.getCurrentCycle())
          && c.getYear().intValue() == this.getCurrentCycleYear() && (c.isUnSubmit() == null || !c.isUnSubmit()))
        .collect(Collectors.toList());
      if (submissions.isEmpty()) {
        return false;
      }
      return true;
    } else {
      return false;
    }
  }

  public boolean isAvailabePhase() {
    return this.availabePhase;
  }

  public boolean isCanEdit() {
    return this.canEdit;
  }

  public boolean isCanEditPhase() {
    return this.canEditPhase;
  }

  public boolean isCanSwitchProject() {
    return this.canSwitchProject;
  }

  public boolean isCapDevManager() {
    String params[] = {this.getCrpSession()};
    return this.hasPermission(this.generatePermission(Permission.CAP_DEV_FULL_PERMISSION, params));
  }

  public boolean isCenterGlobalUnit() {
    if (this.getCurrentCrp() != null) {
      if (this.getCurrentCrp().getGlobalUnitType().getId().intValue() == 4) {
        return true;
      }
    }
    return false;
  }

  /**
   * ************************ CENTER METHOD ********************* verify if
   * the cap-dev is complete
   * ***************************************************************
   *
   * @return true if the cap dev is complete
   */
  public boolean isCompleteCapDev(long capDevID) {

    if (this.sectionStatusService.findAll() == null) {
      return false;
    }

    final CapacityDevelopment capacityDevelopment =
      this.capacityDevelopmentService.getCapacityDevelopmentById(capDevID);

    final List<String> statuses = this.secctionStatusService.distinctSectionStatusCapDev(capDevID);

    if (statuses.size() != 3) {
      return false;
    }

    if (!this.validateCapDevSupDocs(capacityDevelopment)) {
      return false;
    }

    final List<CenterSectionStatus> sectionStatuses = new ArrayList<>(capacityDevelopment.getSectionStatuses().stream()
      .filter(ss -> ss.getYear() == (short) this.getActualPhase().getYear()).collect(Collectors.toList()));

    if (sectionStatuses != null && sectionStatuses.size() > 0) {
      for (final CenterSectionStatus sectionStatus : sectionStatuses) {
        if (sectionStatus.getMissingFields().length() > 0) {
          return false;
        }
      }
    } else {
      return false;
    }
    return true;
  }

  /**
   * ************************ CENTER METHOD ********************* verify if
   * the project is complete
   * ***************************************************************
   *
   * @return true if the project is complete
   */
  public boolean isCompleteCenterProject(long projectID) {

    if (this.sectionStatusService.findAll() == null) {
      return false;
    }

    CenterProject project = this.projectService.getCenterProjectById(projectID);

    List<String> statuses = this.secctionStatusService.distinctSectionStatusProject(projectID);

    if (statuses.size() != 3) {
      return false;
    }

    List<CenterSectionStatus> sectionStatuses = new ArrayList<>(project.getSectionStatuses().stream()
      .filter(ss -> ss.getYear() == (short) this.getActualPhase().getYear()).collect(Collectors.toList()));

    if (sectionStatuses != null && sectionStatuses.size() > 0) {
      for (CenterSectionStatus sectionStatus : sectionStatuses) {
        if (sectionStatus.getMissingFields().length() > 0) {
          return false;
        }
      }
    } else {
      return false;
    }

    return true;
  }

  public boolean isCompleteCrpIndicator(long liaisonIntitution) {
    List<SectionStatus> sectionStatus = null;
    IpLiaisonInstitution ipLiaisonInstitution =
      this.ipLiaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonIntitution);

    sectionStatus = ipLiaisonInstitution.getSectionStatus().stream()
      .filter(c -> c.getSectionName().equals(ProjectSectionStatusEnum.CRP_INDICATORS.getStatus())
        && c.getYear().intValue() == this.getCurrentCycleYear() && c.getCycle().equals(this.getCurrentCycle()))
      .collect(Collectors.toList());

    for (SectionStatus sectionStatus2 : sectionStatus) {
      if (sectionStatus2.getMissingFields().length() > 0) {
        return false;
      }
    }

    if (sectionStatus.isEmpty()) {
      return false;
    }
    return true;
  }

  public boolean isCompleteImpact(long crpProgramID) {

    List<SectionStatus> sectionsBD = this.sectionStatusManager.findAll();
    if (sectionsBD == null) {
      return false;
    }

    CrpProgram cpCrpProgram = this.crpProgramManager.getCrpProgramById(crpProgramID);
    List<SectionStatus> sections =
      cpCrpProgram
        .getSectionStatuses().stream().filter(c -> c.getYear() == this.getActualPhase().getYear()
          && c.getCycle() != null && c.getCycle().equals(this.getActualPhase().getDescription()))
        .collect(Collectors.toList());

    for (SectionStatus sectionStatus : sections) {
      if (sectionStatus.getMissingFields().length() > 0) {
        return false;
      }
    }
    if (sections.size() == 0) {
      return false;
    }
    if (sections.size() < 2) {
      return false;
    }
    return true;
  }

  /**
   * ************************ CENTER METHOD ********************* verify if
   * the impact pathway is complete
   * ***************************************************************
   *
   * @return true if the IP is complete
   */
  public boolean isCompleteIP(long programID) {

    if (this.sectionStatusService.findAll() == null) {
      return false;
    }

    CrpProgram researchProgram = this.crpProgramManager.getCrpProgramById(programID);

    List<String> statuses = this.secctionStatusService.distinctSectionStatus(programID);

    if (statuses.size() != 4) {
      return false;
    }

    if (!this.validateCenterOutcome(researchProgram)) {
      return false;
    }

    if (!this.validateCenterOutput(researchProgram)) {
      return false;
    }

    List<CenterSectionStatus> sectionStatuses = new ArrayList<>(researchProgram.getCenterSectionStatuses().stream()
      .filter(ss -> ss.getYear() == (short) this.getActualPhase().getYear()).collect(Collectors.toList()));

    if (sectionStatuses != null && sectionStatuses.size() > 0) {
      for (CenterSectionStatus sectionStatus : sectionStatuses) {
        if (sectionStatus.getMissingFields().length() > 0) {
          return false;
        }
      }
    } else {
      return false;
    }
    return true;
  }

  /**
   * Check if all the powb Synthesis Sections by Liaison Institution is
   * completed
   *
   * @param sectionName
   * @param liaisonInstitutionID
   * @return
   */
  public boolean isCompleteLiaisonSection(long liaisonInstitutionID) {
    Phase phase = this.getActualPhase();

    PowbSynthesis powbSynthesis = this.powbSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);

    if (powbSynthesis != null) {
      return this.isCompletePowbSynthesis(powbSynthesis.getId());
    } else {
      return false;
    }

  }

  /**
   * Check if all the powb Synthesis Sections by Liaison Institution is
   * completed
   *
   * @param sectionName
   * @param liaisonInstitutionID
   * @return
   */
  public boolean isCompleteLiaisonSection2019(long liaisonInstitutionID) {
    Phase phase = this.getActualPhase();

    PowbSynthesis powbSynthesis = this.powbSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);

    if (powbSynthesis != null) {
      return this.isCompletePowbSynthesis2019(powbSynthesis.getId());
    } else {
      return false;
    }

  }

  /**
   * Check if all the annual Synthesis Sections by Liaison Institution is
   * completed
   *
   * @param liaisonInstitutionID
   * @return
   */
  public boolean isCompleteLiaisonSectionReport(long liaisonInstitutionID) {
    Phase phase = this.getActualPhase();

    ReportSynthesis reportSynthesis = this.reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);

    if (reportSynthesis != null) {
      return this.isCompleteReportSynthesis(reportSynthesis.getId());
    } else {
      return false;
    }

  }

  /**
   * Check if all the annual Synthesis Sections by Liaison Institution is
   * completed
   *
   * @param liaisonInstitutionID
   * @return
   */
  public boolean isCompleteLiaisonSectionReport2018(long liaisonInstitutionID) {
    if (liaisonInstitutionID != 0 && this.getActualPhase() != null && this.getActualPhase().getId() != null) {
      Phase phase = this.getActualPhase();

      ReportSynthesis reportSynthesis = this.reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);

      if (reportSynthesis != null) {
        return this.isCompleteReportSynthesis2018(reportSynthesis.getId());
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  /**
   * Check if the powb synthesis is complete by the flagships or the PMU.
   *
   * @param phaseID
   * @return
   */
  public boolean isCompletePowbSynthesis(long powbSynthesisID) {

    int secctions = 0;
    if (this.sectionStatusManager.findAll() == null) {
      return false;
    }

    PowbSynthesis powbSynthesis = this.powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);

    if (powbSynthesis.getSectionStatuses() != null) {
      List<SectionStatus> sections = new ArrayList<>(powbSynthesis.getSectionStatuses());
      for (SectionStatus sectionStatus : sections) {
        if (sectionStatus.getCycle().equals(this.getCurrentCycle())
          && sectionStatus.getYear().intValue() == this.getCurrentCycleYear()) {
          switch (PowbSynthesisSectionStatusEnum.value(sectionStatus.getSectionName().toUpperCase())) {
            case TOC_ADJUSTMENTS:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case CRP_PROGRESS:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case EVIDENCES:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case FLAGSHIP_PLANS:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case CROSS_CUTTING_DIMENSIONS:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case STAFFING:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case FINANCIAL_PLAN:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case COLABORATION_INTEGRATION:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case MEL:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case MANAGEMENT_RISK:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case MANAGEMENT_GOVERNANCE:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
          }
        }
      }
    } else {
      return false;
    }

    if (this.isPowbFlagship(powbSynthesis.getLiaisonInstitution())) {
      if (secctions != 5) {
        return false;
      }
    }

    if (this.isPowbPMU(powbSynthesis.getLiaisonInstitution())) {
      if (secctions != 9) {
        return false;
      }
    }
    return true;

  }

  /**
   * Check if the powb synthesis is complete by the flagships or the PMU.
   *
   * @param phaseID
   * @return
   */
  public boolean isCompletePowbSynthesis2019(long powbSynthesisID) {

    int secctions = 0;
    PowbSynthesis powbSynthesis = this.powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);

    if (powbSynthesis.getSectionStatuses() != null) {
      List<SectionStatus> sections = new ArrayList<>(powbSynthesis.getSectionStatuses());
      for (SectionStatus sectionStatus : sections) {
        if (sectionStatus.getCycle().equals(this.getCurrentCycle())
          && sectionStatus.getYear().intValue() == this.getCurrentCycleYear()) {
          switch (PowbSynthesis2019SectionStatusEnum.value(sectionStatus.getSectionName().toUpperCase())) {
            case TOC:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case PROGRAM_CHANGES:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case PROGRESS_OUTCOMES:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case PLANNED_STUDIES:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case PLANNED_COLLABORATIONS:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case PLANNED_BUDGET:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
          }

        }
      }
    } else {
      return false;
    }

    if (this.hasSpecificities(this.powbProgramChangeModule())) {
      if (secctions != 6) {
        return false;
      }
    } else {
      if (secctions != 5) {
        return false;
      }
    }

    return true;

  }

  public boolean isCompletePreProject(long projectID) {

    Project project = this.projectManager.getProjectById(projectID);
    List<SectionStatus> sections = project.getSectionStatuses().stream().collect(Collectors.toList());
    int i = 0;
    int budgetFlagshipSection = 0;
    for (SectionStatus sectionStatus : sections) {
      if (sectionStatus.getCycle().equals(this.getCurrentCycle())
        && sectionStatus.getYear().intValue() == this.getCurrentCycleYear()) {

        if (ProjectSectionStatusEnum.value(sectionStatus.getSectionName().toUpperCase()) != null) {
          switch (ProjectSectionStatusEnum.value(sectionStatus.getSectionName().toUpperCase())) {
            case DESCRIPTION:
              i++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case PARTNERS:
              i++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case BUDGET:
              i++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case BUDGETBYFLAGSHIP:
              if (budgetFlagshipSection == 0) {
                budgetFlagshipSection = 1;
              }
              i++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
          }
        }

      }

    }
    if (sections.size() == 0) {
      return false;
    }
    if (budgetFlagshipSection == 1) {
      if (i != 4) {
        return false;
      }
    } else {
      if (i != 3) {
        return false;
      }
    }

    return true;
  }

  /**
   * Check if the annual Report is complete by the flagships or the PMU.
   *
   * @param phaseID
   * @return
   */
  public boolean isCompleteReportSynthesis(long synthesisID) {

    if (this.getActualPhase().getYear() == 2018) {
      return false;
    }

    int secctions = 0;
    if (this.sectionStatusManager.findAll() == null) {
      return false;
    }

    ReportSynthesis reportSynthesis = this.reportSynthesisManager.getReportSynthesisById(synthesisID);

    if (reportSynthesis.getSectionStatuses() != null) {
      List<SectionStatus> sections = new ArrayList<>(reportSynthesis.getSectionStatuses());
      for (SectionStatus sectionStatus : sections) {
        if (sectionStatus.getCycle().equals(this.getCurrentCycle())
          && sectionStatus.getYear().intValue() == this.getCurrentCycleYear()) {
          // AR2019(?) section_names
          ReportSynthesisSectionStatusEnum sectionStatusValue =
            ReportSynthesisSectionStatusEnum.value(sectionStatus.getSectionName().toUpperCase());
          if (sectionStatusValue != null) {
            switch (sectionStatusValue) {
              case CRP_PROGRESS:
              case FLAGSHIP_PROGRESS:
              case CROSS_CUTTING:
              case VARIANCE:
              case FUNDING_USE:
              case EXTERNAL_PARTNERSHIP:
              case CROSS_CGIAR:
              case MELIA:
              case EFFICIENCY:
              case GOVERNANCE:
              case RISKS:
              case FINANCIAL_SUMMARY:
              case INFLUENCE:
              case CONTROL:
                secctions++;
                if (sectionStatus.getMissingFields().length() > 0) {
                  return false;
                }
                break;
            }
          } else {
            // may be section_names from AR2018
            ReportSynthesis2018SectionStatusEnum sectionStatusValue2018 =
              ReportSynthesis2018SectionStatusEnum.value(sectionStatus.getSectionName().toUpperCase());
            if (sectionStatusValue2018 != null) {
              switch (sectionStatusValue2018) {
                case CRP_PROGRESS:
                case FLAGSHIP_PROGRESS:
                case POLICIES:
                case OICR:
                case INNOVATIONS:
                case OUTOMESMILESTONES:
                case PUBLICATIONS:
                case CC_DIMENSIONS:
                case GOVERNANCE:
                case EXTERNAL_PARTNERSHIPS:
                case INTELLECTUAL_ASSETS:
                case MELIA:
                case EFFICIENCY:
                case RISKS:
                case FUNDING_USE:
                case FINANCIAL:
                case NARRATIVE:
                  secctions++;
                  if (sectionStatus.getMissingFields().length() > 0) {
                    return false;
                  }
                  break;
              }
            } else {
              // no idea what section_name it could be
              return false;
            }
          }
        }
      }
    } else {
      return false;
    }

    if (this.isPowbFlagship(reportSynthesis.getLiaisonInstitution())) {
      if (secctions != 6) {
        return false;
      }
    }

    if (this.isPowbPMU(reportSynthesis.getLiaisonInstitution())) {
      if (secctions != 13) {
        return false;
      }
    }
    return true;

  }

  /**
   * Check if the annual Report 2018 is complete by the flagships or the PMU.
   *
   * @param phaseID
   * @return
   */
  public boolean isCompleteReportSynthesis2018(long synthesisID) {

    if (this.getActualPhase().getYear() != 2018) {
      return false;
    }

    int secctions = 0;
    if (this.sectionStatusManager.findAll() == null) {
      return false;
    }

    ReportSynthesis reportSynthesis = this.reportSynthesisManager.getReportSynthesisById(synthesisID);

    if (reportSynthesis.getSectionStatuses() != null) {
      List<SectionStatus> sections = new ArrayList<>(reportSynthesis.getSectionStatuses());
      for (SectionStatus sectionStatus : sections) {
        if (sectionStatus.getCycle().equals(this.getCurrentCycle())
          && sectionStatus.getYear().intValue() == this.getCurrentCycleYear()) {
          switch (ReportSynthesis2018SectionStatusEnum.value(sectionStatus.getSectionName().toUpperCase())) {
            case CRP_PROGRESS:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case FLAGSHIP_PROGRESS:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case POLICIES:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case OICR:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case INNOVATIONS:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case OUTOMESMILESTONES:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case PUBLICATIONS:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case CC_DIMENSIONS:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case GOVERNANCE:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case EXTERNAL_PARTNERSHIPS:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case INTELLECTUAL_ASSETS:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case MELIA:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case EFFICIENCY:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case RISKS:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case FUNDING_USE:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case FINANCIAL:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
            case NARRATIVE:
              secctions++;
              if (sectionStatus.getMissingFields().length() > 0) {
                return false;
              }
              break;
          }
        }
      }
    } else {
      return false;
    }

    if (this.isPowbFlagship(reportSynthesis.getLiaisonInstitution())) {
      if (secctions != 6) {
        return false;
      }
    }

    if (this.isPowbPMU(reportSynthesis.getLiaisonInstitution())) {
      if (secctions != 13) {
        return false;
      }
    }
    return true;

  }

  public boolean isCompleteSynthesys(long program, int type) {

    List<SectionStatus> sectionStatus = null;
    IpProgram ipProgram = this.ipProgramManager.getIpProgramById(program);

    switch (type) {
      case 1:

        sectionStatus = ipProgram.getSectionStatuses().stream()
          .filter(c -> c.getSectionName().equals(ProjectSectionStatusEnum.SYNTHESISOUTCOME.getStatus())
            && c.getYear().intValue() == this.getCurrentCycleYear() && c.getCycle().equals(this.getCurrentCycle()))
          .collect(Collectors.toList());

        break;
      case 2:

        sectionStatus = ipProgram.getSectionStatuses().stream()
          .filter(c -> c.getSectionName().equals(ProjectSectionStatusEnum.SYNTHESISMOG.getStatus())
            && c.getYear().intValue() == this.getCurrentCycleYear() && c.getCycle().equals(this.getCurrentCycle()))
          .collect(Collectors.toList());

        break;

    }

    for (SectionStatus sectionStatus2 : sectionStatus) {
      if (sectionStatus2.getMissingFields().length() > 0) {
        return false;
      }
    }

    if (sectionStatus.isEmpty()) {
      return false;
    }
    return true;
  }

  public boolean isCrpCCAFS() {
    Boolean isCrpCCAFS = false;
    if (this.crpID != null && this.getCrpID().intValue() == 1) {
      isCrpCCAFS = true;
    } else {
      isCrpCCAFS = false;
    }
    return isCrpCCAFS;
  }

  public boolean isCrpClosed() {
    try {
      // return
      // Integer.parseInt(this.getSession().get(APConstants.CRP_CLOSED).toString())
      // == 1;
      CustomParameter crpClosed = this.customParameterManager
        .getCustomParameterByParameterKeyAndGlobalUnitId(APConstants.CRP_CLOSED, this.getCrpID());

      return Boolean.parseBoolean(crpClosed.getValue());
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isCrpRefresh() {
    try {
      // return
      // Integer.parseInt(this.getSession().get(APConstants.CRP_CLOSED).toString())
      // == 1;
      CustomParameter crpRefresh = this.customParameterManager
        .getCustomParameterByParameterKeyAndGlobalUnitId(APConstants.CRP_REFRESH, this.getCrpID());
      // return
      // Integer.parseInt(this.getSession().get(APConstants.CRP_CLOSED).toString())
      // == 1;
      return Boolean.parseBoolean(crpRefresh.getValue());

    } catch (Exception e) {
      return false;
    }
  }

  public boolean isDataSaved() {
    return this.dataSaved;
  }

  /**
   * This method get the status of an specific deliverable depending of the
   * sectionStatuses and the year Previous deliverable will be marked as
   * completed
   *
   * @param deliverableID is the deliverable ID to be identified.
   * @return Boolean object with the status of the deliverable
   */
  public Boolean isDeliverableComplete(Long deliverableID, Long phaseID) {
    if (deliverableID != null && phaseID != null) {
      Deliverable deliverable = this.deliverableManager.getDeliverableById(deliverableID);
      Phase phase = this.phaseManager.getPhaseById(phaseID);

      if (deliverable.getDeliverableInfo(phase) != null) {
        DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(phase);

        if (deliverableInfo.isRequiredToComplete() || deliverableInfo.isStatusCompleteInNextPhases()) {
          SectionStatus sectionStatus = this.sectionStatusManager.getSectionStatusByDeliverable(deliverable.getId(),
            phase.getDescription(), phase.getYear(), phase.getUpkeep(), "deliverableList");
          if (sectionStatus == null) {
            return false;
          }

          if (sectionStatus.getMissingFields() == null || sectionStatus.getMissingFields().length() != 0) {
            return false;
          }
        } else {
          return true;
        }
      }

      return true;
    } else {
      return false;
    }

  }

  public Boolean isDeliverableNew(Long deliverableID) {
    if (deliverableID != null) {
      Deliverable deliverable = this.deliverableManager.getDeliverableById(deliverableID);
      if (deliverable.getPhase() == null) {
        return false;
      }
      if (deliverable.getPhase().equals(this.getActualPhase())) {
        return true;
      }
      return false;
    } else {
      return false;
    }
  }

  public boolean isDraft() {
    return this.draft;
  }

  public boolean isEditable() {
    return this.isEditable;
  }

  public boolean isEditStatus() {
    return this.editStatus;
  }

  public boolean isEntityCenter() {
    if (this.getCurrentCrp() != null) {
      if (this.getCurrentCrp().getGlobalUnitType().getId().intValue() == 4) {
        return true;
      }
    }
    return false;
  }

  public boolean isEntityCRP() {
    if (this.getCurrentCrp() != null) {
      if (this.getCurrentCrp().getGlobalUnitType().getId().intValue() == 1) {
        return true;
      }
    }
    return false;
  }

  public boolean isEntityPlatform() {
    if (this.getCurrentCrp() != null) {
      if (this.getCurrentCrp().getGlobalUnitType().getId().intValue() == 3) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get if the Evidence is new
   *
   * @param id
   * @return
   */
  public Boolean isEvidenceNew(long evidenceId) {

    Phase currentPhase = this.getActualPhase();
    Phase previousPhase = this.phaseManager.findPreviousPhase(currentPhase.getId());

    ProjectExpectedStudy studyDB = this.projectExpectedStudyManager.getProjectExpectedStudyById(evidenceId);

    try {
      List<ProjectExpectedStudyInfo> infos = new ArrayList<>(studyDB.getProjectExpectedStudyInfos().stream()
        .filter(i -> i.getPhase().getId().equals(previousPhase.getId())).collect(Collectors.toList()));
      if (infos != null && !infos.isEmpty()) {
        return false;
      } else {
        return true;
      }
    } catch (Exception e) {
      return false;
    }

  }

  public boolean isExpectedDeliverablesReportAllYearsVisible() {
    // Specificity for show expected deliverable summary - all years selection - in summaries section
    Boolean isVisible = false;
    try {
      if (this.hasSpecificities(APConstants.IS_EXPECTED_DELIVERABLE_REPORT_All_YEARS_VISIBLE)) {
        isVisible = true;
      } else {
        isVisible = false;
      }

    } catch (Exception e) {
      LOG.error("Deliverable AllYears ", e);
    }

    return isVisible;
  }

  /**
   * Findable
   *
   * @param deliverableID
   * @return
   */
  public Boolean isF(long deliverableID) {
    try {
      Deliverable deliverableBD = this.deliverableManager.getDeliverableById(deliverableID);
      this.loadDissemination(deliverableBD);
      if (deliverableBD.getDissemination().getAlreadyDisseminated() != null) {
        if (deliverableBD.getDissemination().getAlreadyDisseminated().booleanValue()) {
          if (deliverableBD.getDissemination().getDisseminationChannel() != null) {
            if (deliverableBD.getDissemination().getDisseminationChannel().equals("other")) {
              if (deliverableBD.getDissemination().getDisseminationUrl() != null
                && !deliverableBD.getDissemination().getDisseminationUrl().trim().isEmpty()) {
                return true;
              }
            } else {
              if (deliverableBD.getDissemination().getSynced() != null
                && deliverableBD.getDissemination().getSynced()) {
                return true;
              }
            }
          }
        } else {
          return false;
        }
      } else {
        return null;
      }
      return null;
    } catch (Exception e) {
      return null;
    }

  }

  public boolean isFullEditable() {
    return this.fullEditable;
  }

  public Boolean isFundingSourceNew(long fundingSourceID) {

    FundingSource fundingSource = this.fundingSourceManager.getFundingSourceById(fundingSourceID);

    if (this.isReportingActive()) {

      try {
        Date reportingDate = this.getActualPhase().getStartDate();
        if (fundingSource.getCreateDate().compareTo(reportingDate) >= 0) {
          return true;
        } else {
          return false;
        }

      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

    } else {
      try {
        Date reportingDate = this.getActualPhase().getStartDate();
        if (fundingSource.getCreateDate().compareTo(reportingDate) >= 0) {
          return true;
        } else {
          return false;
        }

      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

    }
  }

  protected boolean isHttpPost() {
    if (this.getRequest().getMethod().equalsIgnoreCase("post")) {
      return true;
    }
    return false;
  }

  public Boolean isI(long deliverableID) {
    try {
      Deliverable deliverableBD = this.deliverableManager.getDeliverableById(deliverableID);
      this.loadDissemination(deliverableBD);
      if (deliverableBD.getDissemination().getAlreadyDisseminated() != null
        && deliverableBD.getDissemination().getAlreadyDisseminated().booleanValue()) {

        String channel = deliverableBD.getDissemination().getDisseminationChannel();
        String link = deliverableBD.getDissemination().getDisseminationUrl().replaceAll(" ", "%20");;
        if (channel == null || channel.equals("-1")) {
          return null;
        }
        if (link == null || link.equals("-1") || link.isEmpty()) {
          return null;
        }

        // If the deliverable is synced
        if (deliverableBD.getDissemination().getSynced() != null
          && deliverableBD.getDissemination().getSynced().booleanValue()) {
          return true;
        }

        return null;
      }
      if (deliverableBD.getDissemination().getAlreadyDisseminated() == null) {
        return null;
      }
    } catch (Exception e) {
      return null;
    }
    return null;

  }

  /**
   * Get if the Innovation is new
   *
   * @param id
   * @return
   */
  public Boolean isInnovationNew(long innovationId) {
    if (innovationId != 0 && this.getActualPhase() != null) {
      Phase currentPhase = this.getActualPhase();
      Phase previousPhase = this.phaseManager.findPreviousPhase(currentPhase.getId());

      ProjectInnovation innovationNew = this.projectInnovationManager.getProjectInnovationById(innovationId);

      try {
        List<ProjectInnovationInfo> innos = new ArrayList<>(innovationNew.getProjectInnovationInfos().stream()
          .filter(i -> i.getPhase().getId().equals(previousPhase.getId())).collect(Collectors.toList()));
        if (innos != null && !innos.isEmpty()) {
          return false;
        } else {
          return true;
        }
      } catch (Exception e) {
        return false;
      }
    } else {
      return false;
    }
  }

  public boolean isLessonsActive() {
    return Boolean.parseBoolean(this.getSession().get(APConstants.CRP_LESSONS_ACTIVE).toString());
  }

  /**
   * Validate if the user is already logged in or not.
   *
   * @return true if the user is logged in, false otherwise.
   */
  public boolean isLogged() {
    if (this.getCurrentUser() == null) {
      return false;
    }
    return true;
  }

  public boolean isManagementCluster(long id) {
    boolean isManagement = false;
    Project project = projectManager.getProjectById(id);
    if (project != null) {
      project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
      if (project.getProjectInfo() != null && project.getProjectInfo().getClusterType() != null
        && project.getProjectInfo().getClusterType().getId() != null) {
        if (project.getProjectInfo().getClusterType().getId().equals(this.getManagementClusterType().getId())) {
          isManagement = true;
        }
      }
    }
    return isManagement;
  }

  /**
   * Check if the project was created in a new Center
   *
   * @param projectID
   * @return
   */
  public boolean isNewCenterType(long projectID) {

    GlobalUnitProject globalUnitProject = this.globalUnitProjectManager.findByProjectId(projectID);

    if (globalUnitProject.getGlobalUnit().getGlobalUnitType().getId().intValue() == 5) {
      return true;
    }

    return false;

  }

  public boolean isOtherUrl() {
    return this.otherUrl;
  }

  public boolean isPhaseOne() {
    try {
      if (this.isReportingActive() && this.getCrpSession().equals("ccafs") && this.getCurrentCycleYear() <= 2016) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isPlanningActive() {
    if (this.getActualPhase() != null && this.getActualPhase().getDescription() != null) {
      return this.getActualPhase().getDescription().equals(APConstants.PLANNING);
    } else {
      return false;
    }
  }

  public boolean isPlanningActiveParam() {
    return Boolean.parseBoolean(this.getSession().get(APConstants.CRP_PLANNING_ACTIVE).toString());
  }

  public boolean isPMU() {
    String roles = this.getRoles();
    if (roles.contains("PMU")) {
      return true;
    }
    return false;
  }

  /**
   * Get if the Policy is new
   *
   * @param id
   * @return
   */
  public Boolean isPolicyNew(long policyId) {

    Phase currentPhase = this.getActualPhase();
    Phase previousPhase = this.phaseManager.findPreviousPhase(currentPhase.getId());

    ProjectPolicy policyD = this.projectPolicyManager.getProjectPolicyById(policyId);

    try {
      List<ProjectPolicyInfo> poli = new ArrayList<>(policyD.getProjectPolicyInfos().stream()
        .filter(i -> i.getPhase().getId().equals(previousPhase.getId())).collect(Collectors.toList()));
      if (poli != null && !poli.isEmpty()) {
        return false;
      } else {
        return true;
      }
    } catch (Exception e) {
      return false;
    }

  }

  public boolean isPOWB() {
    if (this.getActualPhase() != null && this.getActualPhase().getName() != null
      && (this.getActualPhase().getName().equals("POWB")
        || this.getActualPhase().getName().equals(APConstants.POWB_ACRONYM))) {
      return true;
    } else {
      return false;
    }
  }

  public boolean isPowb2019() {
    try {
      if (this.getCurrentCycleYear() >= 2019) {
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public boolean isPowbFlagship(LiaisonInstitution liaisonInstitution) {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() != null) {
        CrpProgram crpProgram =
          this.crpProgramManager.getCrpProgramById(liaisonInstitution.getCrpProgram().getId().longValue());
        if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
          isFP = true;
        }
      }
    }
    return isFP;
  }

  public boolean isPowbPMU(LiaisonInstitution liaisonInstitution) {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;

  }

  public boolean isPowbSynthesisSubmitted(long powbSynthesisID) {
    PowbSynthesis powbSynthesis = this.powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
    List<Submission> submissions = powbSynthesis
      .getSubmissions().stream().filter(c -> c.getCycle().equals(this.getCurrentCycle())
        && c.getYear().intValue() == this.getCurrentCycleYear() && (c.isUnSubmit() == null || !c.isUnSubmit()))
      .collect(Collectors.toList());
    if (submissions.isEmpty()) {
      return false;
    }
    return true;
  }

  public boolean isPPA(Institution institution) {
    if (institution == null) {
      return false;
    }

    if (institution.getId() != null) {
      institution = this.institutionManager.getInstitutionById(institution.getId());
      if (institution != null) {
        if (institution.getCrpPpaPartners().stream().filter(c -> c.getCrp().getId().longValue() == this.getCrpID()
          && c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()).size() > 0) {
          return true;
        }
      }

    }

    return false;
  }

  /**
   * Check if the project was created in a Center
   *
   * @param projectID
   * @return
   */
  public boolean isProjectCenter(long projectID) {

    GlobalUnitProject globalUnitProject = this.globalUnitProjectManager.findByProjectId(projectID);

    if (globalUnitProject.getGlobalUnit().getGlobalUnitType().getId().intValue() == 4) {
      return true;
    }

    return false;

  }

  /**
   * Check if the project was created in a Crp or Platform
   *
   * @param projectID
   * @return
   */
  public boolean isProjectCrpOrPlatform(long projectID) {

    GlobalUnitProject globalUnitProject = this.globalUnitProjectManager.findByProjectId(projectID);

    if (globalUnitProject.getGlobalUnit().getGlobalUnitType().getId().intValue() == 1
      || globalUnitProject.getGlobalUnit().getGlobalUnitType().getId().intValue() == 3) {
      return true;
    }

    return false;

  }

  public boolean isProjectDescription() {
    String name = this.getActionName();
    if (name.contains(ProjectSectionStatusEnum.DESCRIPTION.getStatus())) {
      return true;
    }
    return false;
  }

  public Boolean isProjectNew(long projectID) {

    Project project = this.projectManager.getProjectById(projectID);

    if (this.isReportingActive()) {

      try {
        Date reportingDate = this.getActualPhase().getStartDate();
        if (project.getCreateDate() != null && reportingDate != null
          && project.getCreateDate().compareTo(reportingDate) >= 0) {
          return true;
        } else {
          return false;
        }

      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

    } else {
      try {
        Date reportingDate = this.getActualPhase().getStartDate();
        if (project.getCreateDate() != null && reportingDate != null
          && project.getCreateDate().compareTo(reportingDate) >= 0) {
          return true;
        } else {
          return false;
        }

      } catch (Exception e) {
        e.printStackTrace();
        return false;
      }

    }
  }

  public boolean isProjectSubmitted(long projectID) {
    try {
      if (this.getActualPhase() != null && this.getActualPhase().getUpkeep() != null
        && !this.getActualPhase().getUpkeep()) {
        Project project = this.projectManager.getProjectById(projectID);
        List<Submission> submissions = project.getSubmissions().stream()
          .filter(c -> c.getCycle() != null && this.getCurrentCycle() != null
            && c.getCycle().equals(this.getCurrentCycle()) && c.getYear() != null
            && c.getYear().intValue() == this.getCurrentCycleYear() && (c.isUnSubmit() == null || !c.isUnSubmit()))
          .collect(Collectors.toList());
        if (submissions == null || submissions.isEmpty()) {
          return false;
        }
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Reusable
   *
   * @param deliverableID
   * @return
   */
  public Boolean isR(long deliverableID) {
    try {
      Deliverable deliverableBD = this.deliverableManager.getDeliverableById(deliverableID);
      if (deliverableBD.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense() == null) {
        return null;
      }
      if (deliverableBD.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense()) {
        return true;
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isReportingActive() {
    boolean reporting = false;
    if (this.getActualPhase() != null && this.getActualPhase().getDescription() != null
      && APConstants.REPORTING != null) {
      reporting = this.getActualPhase().getDescription().equals(APConstants.REPORTING);
    } else {
      reporting = false;
    }
    return reporting;
  }

  public boolean isReportingActiveParam() {

    if (this.getSession().containsKey(APConstants.TEMP_CYCLE)) {
      return true;
    }
    return Boolean.parseBoolean(this.getSession().get(APConstants.CRP_REPORTING_ACTIVE).toString());

  }

  public boolean isRole(String roleAcronym) {
    String roles = this.getRoles();
    if (roles.contains(roleAcronym)) {
      return true;
    }
    return false;
  }

  public boolean isSaveable() {
    return this.saveable;
  }

  public boolean isSubmit(long projectID) {
    Project project = this.projectManager.getProjectById(projectID);
    int year = this.getCurrentCycleYear();
    List<Submission> submissions =
      project
        .getSubmissions().stream().filter(c -> c.getCycle().equals(this.getCurrentCycle())
          && c.getYear().intValue() == year && (c.isUnSubmit() == null || !c.isUnSubmit()))
        .collect(Collectors.toList());
    if (submissions.isEmpty()) {
      return false;
    }
    return true;
  }

  /**
   * ************************ CENTER METHOD ********************* Check if
   * the impact pathway is submitted
   * ***************************************************************
   *
   * @return true if the impact pathway is submitted
   */
  public boolean isSubmitCapDev(long capDevID) {

    CapacityDevelopment capacityDevelopment = this.capacityDevelopmentService.getCapacityDevelopmentById(capDevID);
    if (capacityDevelopment != null) {

      List<CenterSubmission> submissions = new ArrayList<>(capacityDevelopment.getSubmissions().stream()
        .filter(s -> (s.getYear().intValue() == this.getActualPhase().getYear())).collect(Collectors.toList()));

      if (submissions != null && submissions.size() > 0) {
        this.setCenterSubmission(submissions.get(0));
        return true;
      }
    }
    return false;
  }

  /**
   * ************************ CENTER METHOD ********************* Check if
   * the project is submitted
   * ***************************************************************
   *
   * @return true if the project is submitted
   */
  public boolean isSubmitCenterProject(long projectID) {

    CenterProject project = this.projectService.getCenterProjectById(projectID);
    if (project != null) {

      CenterCycle cycle = this.cycleService.getResearchCycleById(ImpactPathwayCyclesEnum.MONITORING.getId());

      List<CenterSubmission> submissions = new ArrayList<>(project.getSubmissions().stream()
        .filter(s -> s.getResearchCycle().equals(cycle) && s.getYear().intValue() == this.getActualPhase().getYear())
        .collect(Collectors.toList()));

      if (submissions != null && submissions.size() > 0) {
        this.setCenterSubmission(submissions.get(0));
        return true;
      }
    }

    return false;
  }

  /**
   * ************************ CENTER METHOD ********************* Check if
   * the impact pathway is submitted
   * ***************************************************************
   *
   * @return true if the impact pathway is submitted
   */
  public boolean isSubmitIP(long programID) {

    CrpProgram program = this.crpProgramManager.getCrpProgramById(programID);
    if (program != null) {

      CenterCycle cycle = this.cycleService.getResearchCycleById(ImpactPathwayCyclesEnum.IMPACT_PATHWAY.getId());

      List<CenterSubmission> submissions = new ArrayList<>(program.getCenterSubmissions().stream()
        .filter(s -> s.getResearchCycle().equals(cycle) && s.getYear().intValue() == this.getActualPhase().getYear())
        .collect(Collectors.toList()));

      if (submissions != null && submissions.size() > 0) {
        this.setCenterSubmission(submissions.get(0));
        return true;
      }
    }
    return false;
  }

  public boolean isSwitchSession() {
    return this.switchSession;
  }

  public boolean isUpKeepActive() {

    return this.getActualPhase().getUpkeep();

  }

  public boolean isValidEmail(String emailStr) {
    boolean isValid = false;
    Matcher matcher =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(emailStr);
    if (matcher.find()) {
      isValid = true;
    }
    return isValid;
  }

  /**
   * Show the superAdmin Menu.
   *
   * @return
   */
  public boolean isVisibleTop() {
    try {
      if (this.canAccessSuperAdmin()) {
        return true;
      }

      if (this.canAcessCrpAdmin()) {
        return true;
      }

      if (this.isVisibleTopGUList()) {
        return true;
      }

      return false;
    } catch (Exception e) {
      return false;
    }

  }

  /**
   * Boolean to validate if show the crp top list.
   *
   * @return
   */
  public boolean isVisibleTopGUList() {
    if (this.getSession().containsKey(APConstants.CRP_VISIBLE_TOP_GULIST)) {
      Boolean viBoolean = (Boolean) this.getSession().get(APConstants.CRP_VISIBLE_TOP_GULIST);
      return viBoolean.booleanValue();
    } else {
      User user = this.getCurrentUser();
      user = this.userManager.getUser(user.getId());
      List<CrpUser> crpUsers =
        new ArrayList<>(user.getCrpUsers().stream().filter(u -> u.isActive()).collect(Collectors.toList()));
      if (crpUsers.size() > 1) {
        return true;
      }
      return false;
    }

  }

  public boolean isYearToShowSectionCovid19() {
    try {
      if (Boolean.parseBoolean(this.getSession().get(APConstants.CRP_SHOW_SECTION_IMPACT_COVID19).toString())) {
        String rangesYears = (String) this.getSession().get(APConstants.CRP_SHOW_SECTION_IMPACT_COVID19_RANGES_YEARS);
        String[] years = rangesYears.split("-");
        if (years.length == 2) {
          if (Integer.parseInt(years[0]) <= this.getActualPhase().getYear()
            && Integer.parseInt(years[1]) >= this.getActualPhase().getYear()) {
            return true;
          }
        } else {
          if (years.length == 1) {
            if (Integer.parseInt(years[0]) <= this.getActualPhase().getYear()) {
              return true;
            }
          }
        }
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public void loadDissemination(Deliverable deliverableBD) {

    if (deliverableBD.getDeliverableDisseminations() != null) {
      deliverableBD.setDisseminations(new ArrayList<>(deliverableBD.getDeliverableDisseminations().stream()
        .filter(dd -> dd.isActive() && dd.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
      if (deliverableBD.getDeliverableDisseminations().size() > 0) {
        deliverableBD.setDissemination(deliverableBD.getDisseminations().get(0));
      } else {
        deliverableBD.setDissemination(new DeliverableDissemination());
      }

    }
  }

  public List<Project> loadFlagShipBudgetInfoProgram(long crpProgramID) {
    List<Project> projectsToRet = new ArrayList<>();
    CrpProgram crpProgram = this.crpProgramManager.getCrpProgramById(crpProgramID);
    List<ProjectFocus> projects =
      crpProgram.getProjectFocuses().stream().filter(c -> c.getProject().isActive() && c.isActive()
        && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());
    Set<Project> myProjects = new HashSet();
    for (ProjectFocus projectFocus : projects) {
      Project project = projectFocus.getProject();
      if (project.isActive()) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        if (project.getProjectInfo() != null && project.getProjectInfo().getStatus() != null) {
          if (project.getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || project.getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            myProjects.add(project);
          }
        }

      }
    }
    for (Project project : myProjects) {

      project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
      double w1 = project.getCoreBudget(this.getActualPhase().getYear(), this.getActualPhase());
      double w3 = project.getW3Budget(this.getActualPhase().getYear(), this.getActualPhase());
      double bilateral = project.getBilateralBudget(this.getActualPhase().getYear(), this.getActualPhase());
      double centerFunds = project.getCenterBudget(this.getActualPhase().getYear(), this.getActualPhase());

      List<ProjectBudgetsFlagship> budgetsFlagships = project.getProjectBudgetsFlagships().stream()
        .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == crpProgram.getId().longValue()
          && c.getPhase().equals(this.getActualPhase()) && c.getYear() == this.getActualPhase().getYear())
        .collect(Collectors.toList());
      double percentageW1 = 0;
      double percentageW3 = 0;
      double percentageB = 0;
      double percentageCenterFunds = 0;

      if (!this.getCountProjectFlagships(project.getId())) {
        if (w1 > 0) {
          percentageW1 = 100;
        }
        if (w3 > 0) {
          percentageW3 = 100;
        }
        if (bilateral > 0) {
          percentageB = 100;
        }
        if (centerFunds > 0) {
          percentageCenterFunds = 100;
        }
      }
      for (ProjectBudgetsFlagship projectBudgetsFlagship : budgetsFlagships) {
        switch (projectBudgetsFlagship.getBudgetType().getId().intValue()) {
          case 1:
            percentageW1 = percentageW1 + projectBudgetsFlagship.getAmount();
            break;
          case 2:
            percentageW3 = percentageW3 + projectBudgetsFlagship.getAmount();
            break;
          case 3:
            percentageB = percentageB + projectBudgetsFlagship.getAmount();
            break;
          case 4:
            percentageCenterFunds = percentageCenterFunds + projectBudgetsFlagship.getAmount();
            break;
          default:
            break;
        }
      }
      project.setW3Budget(w3);
      project.setCoreBudget(w1);
      project.setBilateralBudget(bilateral);
      project.setCentenFundsBudget(centerFunds);

      project.setPercentageW3(percentageW3);
      project.setPercentageW1(percentageW1);
      project.setPercentageBilateral(percentageB);
      project.setPercentageFundsBudget(percentageCenterFunds);

      w1 = w1 * percentageW1 / 100;
      w3 = w3 * percentageW3 / 100;
      bilateral = bilateral * percentageB / 100;
      centerFunds = centerFunds * percentageCenterFunds / 100;

      project.setTotalW3(w3);
      project.setTotalW1(w1);
      project.setTotalBilateral(bilateral);
      project.setTotalCenterFunds(centerFunds);

      projectsToRet.add(project);
    }
    return projectsToRet;
  }

  public void loadLessons(GlobalUnit crp, Project project) {

    Project projectDB = this.projectManager.getProjectById(project.getId());
    List<ProjectComponentLesson> lessons = projectDB.getProjectComponentLessons().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())
        && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
      .collect(Collectors.toList());

    if (this.isReportingActive()) {
      List<ProjectComponentLesson> lessonsPreview = projectDB.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getActualPhase().getYear()
          && c.getCycle().equals(APConstants.PLANNING)
          && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessonsPreview.isEmpty()) {
        project.setProjectComponentLessonPreview(lessonsPreview.get(0));
      }
    }
  }

  public void loadLessons(GlobalUnit crp, Project project, String actionName) {

    Project projectDB = this.projectManager.getProjectById(project.getId());
    if (this.isReportingActive()) {

      List<ProjectComponentLesson> lessons = projectDB.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getActualPhase().getYear()
          && c.getCycle().equals(APConstants.REPORTING) && c.getComponentName().equals(actionName))
        .collect(Collectors.toList());

      List<ProjectComponentLesson> lessonsPreview = projectDB.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getActualPhase().getYear()
          && c.getCycle().equals(APConstants.PLANNING) && c.getComponentName().equals(actionName))
        .collect(Collectors.toList());
      if (!lessonsPreview.isEmpty()) {
        project.setProjectComponentLessonPreview(lessonsPreview.get(0));
      }
    } else {

      List<ProjectComponentLesson> lessons = projectDB.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getActualPhase().getYear()
          && c.getCycle().equals(APConstants.PLANNING) && c.getComponentName().equals(actionName))
        .collect(Collectors.toList());
    }
  }

  public void loadLessonsOutcome(GlobalUnit crp, ProjectOutcome projectOutcome) {

    ProjectOutcome projectOutcomeDB = this.projectOutcomeManager.getProjectOutcomeById(projectOutcome.getId());
    if (this.isReportingActive()) {

      List<ProjectComponentLesson> lessons = projectOutcomeDB.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getActualPhase().getYear()
          && c.getCycle().equals(APConstants.REPORTING)
          && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessons.isEmpty()) {
        projectOutcome.setProjectComponentLesson(lessons.get(0));
      }
      List<ProjectComponentLesson> lessonsPreview = projectOutcomeDB.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getActualPhase().getYear()
          && c.getCycle().equals(APConstants.PLANNING)
          && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessonsPreview.isEmpty()) {
        projectOutcome.setProjectComponentLessonPreview(lessonsPreview.get(0));
      }
    } else {

      List<ProjectComponentLesson> lessons = projectOutcomeDB.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getActualPhase().getYear()
          && c.getCycle().equals(APConstants.PLANNING)
          && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessons.isEmpty()) {
        projectOutcome.setProjectComponentLesson(lessons.get(0));
      }
    }
  }

  public void loadLessonsSynthesis(GlobalUnit crp, IpProgram program) {

    if (this.isReportingActive()) {

      List<ProjectComponentLesson> lessons = program.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getActualPhase().getYear()
          && c.getCycle().equals(APConstants.REPORTING)
          && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessons.isEmpty()) {
        program.setProjectComponentLesson(lessons.get(0));
      }
      List<ProjectComponentLesson> lessonsPreview = program.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getActualPhase().getYear()
          && c.getCycle().equals(APConstants.PLANNING)
          && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessonsPreview.isEmpty()) {
        program.setProjectComponentLessonPreview(lessonsPreview.get(0));
      }
    } else {

      List<ProjectComponentLesson> lessons = program.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getActualPhase().getYear()
          && c.getCycle().equals(APConstants.PLANNING)
          && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessons.isEmpty()) {
        program.setProjectComponentLesson(lessons.get(0));
      }
    }
  }

  public void loadQualityCheck(Deliverable deliverableBD) {
    List<DeliverableQualityCheck> deliverableQualityChecksDB = deliverableBD.getDeliverableQualityChecks().stream()
      .filter(q -> q.isActive() && q.getPhase() != null && q.getPhase().equals(this.getActualPhase()))
      .collect(Collectors.toList());
    if (deliverableQualityChecksDB != null) {
      if (deliverableQualityChecksDB.size() > 0) {
        deliverableBD.setQualityCheck(deliverableBD.getDeliverableQualityChecks().stream()
          .filter(q -> q.isActive() && q.getPhase() != null && q.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toSet()).iterator().next());
      } else {
        deliverableBD.setQualityCheck(new DeliverableQualityCheck());
      }

    }
  }

  public String next() {
    return NEXT;
  }

  public boolean outcomeCanBeDeleted(long id) {
    boolean canBeDeleted = false;

    // Verify CenterOutcome Model
    try {
      CenterOutputsOutcome centerOutputsOutcome = this.centerOutputsOutcomeManager.getCenterOutputsOutcomeById(id);

      if (centerOutputsOutcome != null) {
        if (!centerOutputsOutcome.isActive()) {
          canBeDeleted = true;
        }

      } else {
        canBeDeleted = false;
      }
      return canBeDeleted;
    } catch (Exception e) {
      System.out.println(e + "error");
      return false;
    }

    /*
     * if (clazz == CenterOutcome.class) { CenterOutcome outcome =
     * outcomeService.getResearchOutcomeById(id); List<CenterOutput> outputs = new
     * ArrayList<>(); List<CenterOutputsOutcome> centerOutputsOutcomes = new
     * ArrayList<>( outcome.getCenterOutputsOutcomes().stream().filter(ro ->
     * ro.isActive()).collect(Collectors.toList())); for (CenterOutputsOutcome
     * centerOutputsOutcome : centerOutputsOutcomes) {
     * outputs.add(centerOutputsOutcome.getCenterOutput()); } if (outputs != null) {
     * if (!outputs.isEmpty()) { return false; } } }
     */
  }

  /*
   * POWB 2019 program Change section
   */
  public String powbProgramChangeModule() {
    return APConstants.POWB_PROGRAM_CHANGE_MODULE;
  }

  @Override
  public void prepare() throws Exception {

  }

  /**
   * Remove HTML tags
   *
   * @param html
   * @return
   */
  public String removeHtmlTags(String html) {
    if (html != null) {
      Whitelist whitelist = Whitelist.none();
      whitelist.addTags("a");
      whitelist.addAttributes("a", "href");

      String noTags = Jsoup.clean(html, whitelist);
      return noTags;
    } else {
      return "";
    }

  }

  /* Override this method depending of the save action. */
  public String save() {
    return SUCCESS;
  }

  public void saveLessons(GlobalUnit crp, Project project) {

  }

  public void saveLessonsOutcome(GlobalUnit crp, ProjectOutcome projectOutcomeDB, ProjectOutcome projectOutcome) {
    Project project = this.projectManager.getProjectById(projectOutcome.getProject().getId());
    if (project.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader()
      && !this.isProjectNew(project.getId())) {

      String actionName = this.getActionName().replaceAll(crp.getAcronym() + "/", "");

      if (projectOutcome.getProjectComponentLesson() == null) {
        LOG.debug("No lesson attached with projectOutcome");
        return;

      } else if (projectOutcome.getProjectComponentLesson().getId() == -1L) {
        // Save a new entity

        ProjectComponentLesson projectComponenetLesson = projectOutcome.getProjectComponentLesson();
        projectComponenetLesson.setId(null);

        projectComponenetLesson.setComponentName(actionName);
        projectComponenetLesson.setProjectOutcome(projectOutcomeDB);

        projectComponenetLesson.setPhase(this.getActualPhase());
        projectComponenetLesson.setCycle(this.getActualPhase().getDescription());
        projectComponenetLesson.setYear(this.getActualPhase().getYear());

        projectComponenetLesson =
          this.projectComponentLessonManager.saveProjectComponentLesson(projectComponenetLesson);
      } else {

        ProjectComponentLesson projectComponenetLesson = projectOutcome.getProjectComponentLesson();
        ProjectComponentLesson projectComponentDB =
          this.projectComponentLessonManager.getProjectComponentLessonById(projectComponenetLesson.getId());

        projectComponentDB.setComponentName(actionName);
        projectComponentDB.setLessons(projectOutcome.getProjectComponentLesson().getLessons());
        projectComponentDB.setPhase(this.getActualPhase());
        projectComponentDB.setCycle(this.getActualPhase().getDescription());
        projectComponentDB.setYear(this.getActualPhase().getYear());
        projectComponentDB.setProjectOutcome(projectOutcomeDB);

        projectComponentDB = this.projectComponentLessonManager.saveProjectComponentLesson(projectComponentDB);

      }
    }

  }

  public void saveLessonsSynthesis(GlobalUnit crp, IpProgram ipProgram) {

    String actionName = this.getActionName().replaceAll(crp.getAcronym() + "/", "");

    ipProgram.getProjectComponentLesson().setComponentName(actionName);
    ipProgram.getProjectComponentLesson().setIpProgram(ipProgram);
    if (ipProgram.getProjectComponentLesson().getId().longValue() == -1) {
      ipProgram.getProjectComponentLesson().setId(null);
    }

    if (this.isReportingActive()) {
      ipProgram.getProjectComponentLesson().setCycle(APConstants.REPORTING);
      ipProgram.getProjectComponentLesson().setYear(this.getReportingYear());

    } else {
      ipProgram.getProjectComponentLesson().setCycle(APConstants.PLANNING);
      ipProgram.getProjectComponentLesson().setYear(this.getPlanningYear());
    }
    this.projectComponentLessonManager.saveProjectComponentLesson(ipProgram.getProjectComponentLesson());

  }

  public boolean sendEmailJustToSupport() {
    try {
      CustomParameter sendEmailSupport = this.customParameterManager
        .getCustomParameterByParameterKeyAndGlobalUnitId(APConstants.CRP_EMAIL_SUPPORT_TEAM, this.getCrpID());
      return Boolean.parseBoolean(sendEmailSupport.getValue());

    } catch (Exception e) {
      return false;
    }
  }

  public void setActualPhase(Phase phase) {
    this.getSession().put(APConstants.CURRENT_PHASE, phase);
  }

  public void setAdd(boolean add) {
    this.add = true;
  }

  public void setAvailabePhase(boolean avilabePhase) {
    this.availabePhase = avilabePhase;
  }

  public void setBasePermission(String basePermission) {
    this.basePermission = basePermission;
  }

  public void setCancel(boolean cancel) {
    this.cancel = true;
  }

  public void setCanEdit(boolean canEdit) {
    this.canEdit = canEdit;
  }

  public void setCanEditPhase(boolean canEditPhase) {
    this.canEditPhase = canEditPhase;
  }

  public void setCanSwitchProject(boolean canSwitchProject) {
    this.canSwitchProject = canSwitchProject;
  }

  public void setCenterID(Long centerID) {
    this.centerID = centerID;
  }

  public void setCenterSession(String centerSession) {
    this.centerSession = centerSession;
  }

  public void setCenterSubmission(CenterSubmission centerSubmission) {
    this.centerSubmission = centerSubmission;
  }

  public void setConfig(APConfig config) {
    this.config = config;
  }

  public void setCrpID(Long crpID) {
    this.crpID = crpID;
  }

  public void setCrpSession(String crpSession) {
    this.crpSession = crpSession;
  }

  public void setCurrentCenter(GlobalUnit currentCenter) {
    this.currentCenter = currentCenter;
  }

  public void setDataSaved(boolean dataSaved) {
    this.dataSaved = dataSaved;
  }

  public void setDelete(boolean delete) {
    this.delete = delete;
  }

  public void setDifferences(List<HistoryDifference> differences) {
    this.differences = differences;
  }

  public void setDraft(boolean draft) {
    this.draft = draft;
  }

  public void setEditable(boolean isEditable) {
    this.isEditable = isEditable;
  }

  public void setEditableParameter(boolean isEditable) {
    this.isEditable = isEditable;
  }

  public void setEditStatus(boolean editStatus) {
    this.editStatus = editStatus;
  }

  public void setFullEditable(boolean fullEditable) {
    this.fullEditable = fullEditable;
  }

  public void setInvalidFields(HashMap<String, String> invalidFields) {
    this.invalidFields = invalidFields;
  }

  public void setJustification(String justification) {
    this.justification = justification;
  }

  public void setLessonsActive(boolean lessonsActive) {
    this.lessonsActive = lessonsActive;
  }

  public void setMissingFields(StringBuilder missingFields) {
    this.missingFields = missingFields;
  }

  /**
   * Common logic for setting the Modification Justification
   *
   * @param entity
   */
  protected void setModificationJustification(MarloAuditableEntity entity) {
    /**
     * Hibernate will not save unchanged entities, so having the
     * localDateTime ensures a unique entry is created each save.
     */
    if (StringUtils.isEmpty(this.justification)) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd,yyyy HH:mm");
      entity.setModificationJustification("No justification provided at : " + LocalDateTime.now().format(formatter));
    } else if (this.justification.equals(entity.getModificationJustification())) {
      /**
       * We have the same justification text as before - which can lead to
       * the entity not being saved if no other fields have been saved.
       * Add a * character to ensure that they are different.
       */
      entity.setModificationJustification(this.justification + "*");
    } else {
      entity.setModificationJustification(this.justification);
    }
  }

  public void setNext(boolean next) {
    this.next = true;
  }

  public void setOtherUrl(boolean otherUrl) {
    this.otherUrl = otherUrl;
  }

  public void setPhaseID(Long phaseID) {
    this.phaseID = phaseID;
  }

  public void setPlanningActive(boolean planningActive) {
    this.planningActive = planningActive;
  }

  public void setPlanningYear(int planningYear) {
    this.planningYear = planningYear;
  }

  public void setReportingActive(boolean reportingActive) {
    this.reportingActive = reportingActive;
  }

  public void setReportingYear(int reportingYear) {
    this.reportingYear = reportingYear;
  }

  public void setSave(boolean save) {
    this.save = true;
  }

  public void setSaveable(boolean saveable) {
    this.saveable = saveable;
  }

  public void setSecurityContext(BaseSecurityContext securityContext) {
    this.securityContext = securityContext;
  }

  @Override
  public void setServletRequest(HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public void setSession(Map<String, Object> session) {
    this.session = session;
  }

  public void setSubmission(Submission submission) {
    this.submission = submission;
  }

  public void setSubmit(boolean submit) {
    this.submit = true;
  }

  public void setSwitchSession(boolean switchSession) {
    this.switchSession = switchSession;
  }

  public void setSynthesisFlagships(StringBuilder synthesisFlagships) {
    this.synthesisFlagships = synthesisFlagships;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setUsersToActive(List<Map<String, Object>> usersToActive) {
    this.usersToActive = usersToActive;
  }

  public void setValidationMessage(StringBuilder validationMessage) {
    this.validationMessage = validationMessage;
  }

  public void setYears(ArrayList<String> years) {
    this.years = years;
  }

  public String specificityValue(String specificity) {
    try {
      String value = this.getSession().get(specificity).toString();
      return value;
    } catch (Exception e) {
      return null;
    }

  }

  public String submit() {
    return SUCCESS;
  }

  /**
   * ************************ CENTER METHOD ********************* Validate
   * the missing fields in the program impacts section
   * ***************************************************************
   *
   * @return false if has missing fields.
   */
  public boolean validateCapDevSection(CapacityDevelopment capacityDevelopment, String sectionName) {

    final CenterSectionStatus sectionStatus = this.secctionStatusService
      .getSectionStatusByCapdev(capacityDevelopment.getId(), sectionName, this.getActualPhase().getYear());

    if (sectionStatus == null) {
      return false;
    }
    if (sectionStatus.getMissingFields().length() > 0) {
      return false;
    }

    return true;
  }

  /**
   * ************************ CENTER METHOD ********************* Validate
   * the missing fields in the Cap-Dev Supporting Documents section
   * ***************************************************************
   *
   * @return false if has missing fields.
   */
  public boolean validateCapDevSupDocs(CapacityDevelopment capacityDevelopment) {
    if (capacityDevelopment != null) {
      List<CenterDeliverable> centerDeliverables = new ArrayList<>(
        capacityDevelopment.getDeliverables().stream().filter(cd -> cd.isActive()).collect(Collectors.toList()));
      if (centerDeliverables != null && !centerDeliverables.isEmpty()) {
        for (CenterDeliverable centerDeliverable : centerDeliverables) {
          final CenterSectionStatus sectionStatus = this.getCenterDeliverableStatus(centerDeliverable.getId());
          if (sectionStatus == null) {
            return false;
          } else {
            if (sectionStatus.getMissingFields().length() > 0) {
              return false;
            }
          }
        }
      } else {
        return false;
      }
      return true;
    }
    return true;
  }

  /**
   * ************************ CENTER METHOD ********************* Validate
   * the missing fields in the deliverables section
   * ***************************************************************
   *
   * @return false if has missing fields.
   */
  public boolean validateCenterDeliverable(CenterProject project) {
    if (project != null) {
      List<CenterDeliverable> deliverables =
        new ArrayList<>(project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList()));

      if (deliverables != null && !deliverables.isEmpty()) {
        for (CenterDeliverable deliverable : deliverables) {
          CenterSectionStatus sectionStatus = this.getCenterDeliverableStatus(deliverable.getId());
          if (sectionStatus == null) {
            return false;
          } else {
            if (sectionStatus.getMissingFields().length() != 0) {
              return false;
            }
          }
        }
      } else {
        return false;
      }
    }

    return true;
  }

  /**
   * ************************ CENTER METHOD ********************* Validate
   * the missing fields in the program impacts section
   * ***************************************************************
   *
   * @return false if has missing fields.
   */
  public boolean validateCenterImpact(CrpProgram program, String sectionName) {

    CenterSectionStatus sectionStatus = this.secctionStatusService.getSectionStatusByProgram(program.getId(),
      sectionName, this.getActualPhase().getYear());

    if (sectionStatus == null) {
      return false;
    }
    if (sectionStatus.getMissingFields().length() != 0) {
      return false;
    }

    return true;
  }

  /**
   * ************************ CENTER METHOD ********************* Validate
   * the missing fields in the outcome section
   * ***************************************************************
   *
   * @return false if has missing fields.
   */
  public boolean validateCenterOutcome(CrpProgram program) {
    boolean hasOutcomes = false;
    if (program != null) {
      List<CenterTopic> topics =
        new ArrayList<>(program.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList()));
      if (topics != null && !topics.isEmpty()) {
        for (CenterTopic researchTopic : topics) {
          List<CenterOutcome> outcomes = new ArrayList<>(
            researchTopic.getResearchOutcomes().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));
          if (outcomes != null && !outcomes.isEmpty()) {
            hasOutcomes = true;
            for (CenterOutcome researchOutcome : outcomes) {
              CenterSectionStatus sectionStatus = this.getCenterOutcomeStatus(researchOutcome.getId(), "outcomesList");
              if (sectionStatus == null) {
                return false;
              } else {
                if (sectionStatus.getMissingFields().length() != 0) {
                  return false;
                }
              }
            }
          }
        }
      } else {
        return false;
      }
    } else {
      return false;
    }
    if (hasOutcomes) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * ************************ CENTER METHOD ********************* Validate
   * the missing fields in the output section
   * ***************************************************************
   *
   * @return false if has missing fields.
   */
  public boolean validateCenterOutput(CrpProgram program) {
    if (program != null) {
      List<CenterOutput> outputs =
        new ArrayList<>(program.getCenterOutputs().stream().filter(op -> op.isActive()).collect(Collectors.toList()));
      if (outputs != null && !outputs.isEmpty()) {
        for (CenterOutput researchOutput : outputs) {
          CenterSectionStatus sectionStatus = this.getCenterOutputStatus(researchOutput.getId());
          if (sectionStatus == null) {
            return false;
          } else {
            if (sectionStatus.getMissingFields().length() != 0) {
              return false;
            }
          }
        }
      } else {
        return false;
      }
    } else {
      return false;
    }
    return true;

  }

  //
  /**
   * ************************ CENTER METHOD ********************* Validate
   * the missing fields in the project section
   * ***************************************************************
   *
   * @return false if has missing fields.
   */
  public boolean validateCenterProject(CenterProject project, String sectionName) {
    CenterSectionStatus sectionStatus = this.secctionStatusService.getSectionStatusByProject(
      project.getResearchProgram().getId(), project.getId(), sectionName, this.getActualPhase().getYear());

    if (sectionStatus == null) {
      return false;
    }
    if (sectionStatus.getMissingFields().length() != 0) {
      return false;
    }

    return true;
  }

  /**
   * ************************ CENTER METHOD ********************* Validate
   * the missing fields in the research topic section
   * ***************************************************************
   *
   * @return false if has missing fields.
   */
  public boolean validateCenterTopic(CrpProgram program, String sectionName) {

    CenterSectionStatus sectionStatus = this.secctionStatusService.getSectionStatusByProgram(program.getId(),
      sectionName, this.getActualPhase().getYear());

    if (sectionStatus == null) {
      return false;
    }
    if (sectionStatus.getMissingFields().length() != 0) {
      return false;
    }

    return true;
  }

  public boolean validatePolicy(long policyID) {
    SectionStatus sectionStatus =
      this.sectionStatusManager.getSectionStatusByProjectPolicy(policyID, this.getCurrentCycle(),
        this.getCurrentCycleYear(), this.isUpKeepActive(), ProjectSectionStatusEnum.POLICIES.getStatus());
    if (sectionStatus != null) {
      if (sectionStatus.getMissingFields().length() != 0) {
        return false;
      }
    } else {
      return false;
    }
    return true;
  }

  //
  public boolean validURL(String URL) {
    try {
      java.net.URL url = new java.net.URL(URL);
      url.toURI();
      return true;
    } catch (MalformedURLException e) {

      return false;
    } catch (URISyntaxException e) {

      return false;
    }

  }

}
