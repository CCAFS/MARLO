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
package org.cgiar.ccafs.marlo.action;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.IAuditLog;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterKeyOutputManager;
import org.cgiar.ccafs.marlo.data.manager.CrpClusterOfActivityManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterCycleManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterImpactManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterOutputManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterSectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterTopicManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpLiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectComponentLessonManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Auditlog;
import org.cgiar.ccafs.marlo.data.model.CaseStudy;
import org.cgiar.ccafs.marlo.data.model.CaseStudyProject;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterCycle;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableOutput;
import org.cgiar.ccafs.marlo.data.model.CenterImpact;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.CenterProjectFundingSource;
import org.cgiar.ccafs.marlo.data.model.CenterProjectOutput;
import org.cgiar.ccafs.marlo.data.model.CenterSectionStatus;
import org.cgiar.ccafs.marlo.data.model.CenterSubmission;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.data.model.CenterUser;
import org.cgiar.ccafs.marlo.data.model.Crp;
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
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.FileDB;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.FundingSourceInstitution;
import org.cgiar.ccafs.marlo.data.model.ImpactPathwayCyclesEnum;
import org.cgiar.ccafs.marlo.data.model.ImpactPathwaySectionsEnum;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.LicensesTypeEnum;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementType;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlight;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionsEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
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
import org.cgiar.ccafs.marlo.validation.fundingSource.FundingSourceValidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This action aims to define general functionalities that are going to be used by all other Actions.
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


  public static final String REDIRECT = "redirect";

  public static final String SAVED_STATUS = "savedStatus";

  private static final long serialVersionUID = -740360140511380630L;

  private List<HistoryDifference> differences;

  protected boolean add;
  private Long phaseID;


  /**
   * Use field injection in BaseAction only. Subclasses should use constructor injection.
   */
  @Inject
  private AuditLogManager auditLogManager;


  @Inject
  private InstitutionManager institutionManager;

  private String basePermission;


  protected boolean cancel;
  private boolean editStatus = false; // If user is able to edit the form.
  private boolean canEdit; // If user is able to edit the form.
  private boolean availabePhase = true; // If user is able to edit the form.


  private boolean canEditPhase = true; // If user is able to edit the form.


  private boolean canSwitchProject; // If user is able to Switch Project. (generally is a project leader)

  private boolean switchSession;
  protected APConfig config;


  @Inject
  private PhaseManager phaseManager;


  @Inject
  private CrpClusterKeyOutputManager crpClusterKeyOutputManager;

  @Inject
  private CrpClusterOfActivityManager crpClusterOfActivityManager;

  @Inject
  private CrpMilestoneManager crpMilestoneManager;
  private Long crpID;

  // Managers
  @Inject
  private CrpManager crpManager;

  @Inject
  private CrpPpaPartnerManager crpPpaPartnerManager;

  @Inject
  private CrpProgramLeaderManager crpProgramLeaderManager;


  @Inject
  private CrpProgramManager crpProgramManager;

  @Inject
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  // Variables
  private String crpSession;

  private Crp currentCrp;
  protected boolean dataSaved;

  protected boolean delete;

  @Inject
  private DeliverableManager deliverableManager;

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
  private RoleManager roleManager;
  @Inject
  private FileDBManager fileDBManager;
  private boolean fullEditable; // If user is able to edit all the form.

  @Inject
  private FundingSourceManager fundingSourceManager;
  @Inject
  private FundingSourceInstitutionManager fundingSourceInstitutionManager;

  @Inject
  private FundingSourceValidator fundingSourceValidator;
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

  private boolean reportingActive;


  private int reportingYear;

  private HttpServletRequest request;
  /*********************************************************
   * CENTER VARIABLES
   * *******************************************************
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
  private ICenterManager centerService;
  @Inject
  private ICenterProgramManager programService;

  @Inject
  private ICenterProjectManager projectService;

  @Inject
  private ICenterDeliverableManager deliverableService;
  @Inject
  private ICenterSectionStatusManager sectionStatusService;
  private String centerSession;

  private Long centerID;

  private Center currentCenter;
  private CenterSubmission centerSubmission;


  /*********************************************************/

  // button actions
  protected boolean save;

  private boolean saveable; // If user is able to see the save, cancel, delete buttons
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
  private IpProgramManager ipProgramManager;
  @Inject
  private IpLiaisonInstitutionManager ipLiaisonInstitutionManager;
  private StringBuilder validationMessage = new StringBuilder();

  private StringBuilder missingFields = new StringBuilder();

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

  /**
   * This function add a flag (--warn--) to the message in order to give
   * a different style to the success message using javascript once the html is ready.
   * 
   * @param message
   */
  public void addActionWarning(String message) {
    this.addActionMessage("--warn--" + message);
  }

  public void addMessage(String message) {
    validationMessage.append("<p> - ");
    validationMessage.append(message);
    validationMessage.append("</p>");

    this.addMissingField(message);
  }

  /**
   * This method add a missing field separated by a semicolon (;).
   * 
   * @param field is the name of the field.
   */
  public void addMissingField(String field) {
    if (missingFields.length() != 0) {
      missingFields.append(";");
    }
    missingFields.append(field);
  }

  public boolean canAccessSuperAdmin() {
    return this.securityContext.hasAllPermissions(Permission.FULL_PRIVILEGES);
  }


  /**
   * ***********************CENTER METHOD*********************
   * return true if the user can view the impactPathway
   * *********************************************************
   * 
   * @return true if the user is super admin.
   */
  public boolean canAcessCenterImpactPathway() {

    String permission =
      this.generatePermissionCenter(Permission.RESEARCH_AREA_FULL_PRIVILEGES, this.getCenterSession());
    LOG.debug(permission);
    LOG.debug(String.valueOf(securityContext.hasPermission(permission)));
    return securityContext.hasPermission(permission);
  }

  public boolean canAcessCrp() {
    return this.canAcessPublications() || this.canAcessSynthesisMog();
  }

  public boolean canAcessCrpAdmin() {
    try {
      String permission = this.generatePermission(Permission.CRP_ADMIN_VISIBLE_PRIVILEGES, this.getCrpSession());
      return securityContext.hasPermission(permission);
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
    return securityContext.hasPermission(permission);
  }

  public boolean canAcessPublications() {
    String params[] = {this.getCrpSession()};
    return (this.hasPermission(this.generatePermission(Permission.PUBLICATION_ADD, params)));
  }

  public boolean canAcessSumaries() {
    if (this.canAcessCrpAdmin() || this.canAccessSuperAdmin()) {
      return true;
    } else {
      User u = this.getCurrentUser();
      if (u != null) {
        u = userManager.getUser(u.getId());
        List<Role> roles = new ArrayList<>();
        for (UserRole userRole : u.getUserRoles()) {
          roles.add(userRole.getRole());
        }
        long pmuRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
        Role rolePreview = roleManager.getRoleById(pmuRol);
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
    return securityContext.hasPermission(permission);
  }

  public boolean canAddBilateralProject() {
    String permission = this.generatePermission(Permission.PROJECT_BILATERAL_ADD, this.getCrpSession());
    return securityContext.hasPermission(permission);
  }

  public boolean canAddCoreProject() {
    String permission = this.generatePermission(Permission.PROJECT_CORE_ADD, this.getCrpSession());
    return securityContext.hasPermission(permission);
  }

  public boolean canBeDeleted(long id, String className) {
    Class<?> clazz;
    try {
      clazz = Class.forName(className);
      if (clazz == UserRole.class) {
        UserRole userRole = userRoleManager.getUserRoleById(id);
        long cuId = Long.parseLong((String) this.getSession().get(APConstants.CRP_CU));
        /** Optimize this to a SQL query that takes the userId and the LiasionInstitutionId as parameters **/
        List<LiaisonUser> liaisonUsers = liaisonUserManager.findAll().stream()
          .filter(c -> c.getUser().getId().longValue() == userRole.getUser().getId().longValue()
            && c.getLiaisonInstitution().getId().longValue() == cuId)
          .collect(Collectors.toList());

        for (LiaisonUser liaisonUser : liaisonUsers) {
          if (!liaisonUser.getProjects().isEmpty()) {
            return false;
          }
        }
      }

      if (clazz == CrpProgram.class) {
        CrpProgram crpProgram = crpProgramManager.getCrpProgramById(id);

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
        CrpProgramLeader crpProgramLeader = crpProgramLeaderManager.getCrpProgramLeaderById(id);
        for (LiaisonUser liaisonUser : crpProgramLeader.getUser().getLiasonsUsers().stream()
          .filter(c -> c.getLiaisonInstitution().getCrpProgram() != null && c.getLiaisonInstitution().getCrpProgram()
            .getId().longValue() == crpProgramLeader.getCrpProgram().getId().longValue())
          .collect(Collectors.toList())) {


          List<ProjectInfo> projects =
            liaisonUser.getProjects().stream().filter(c -> c.isActive()).collect(Collectors.toList());
          boolean deleted = true;
          if (projects.size() > 0) {

            for (ProjectInfo projectInfo : projects) {
              Project project = projectInfo.getProject();
              if (project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getCrpProgram().getId()
                .equals(crpProgramLeader.getCrpProgram().getId())) {
                if (project.getProjecInfoPhase(this.getActualPhase()).getStatus() != null) {
                  switch (ProjectStatusEnum
                    .getValue(project.getProjecInfoPhase(this.getActualPhase()).getStatus().intValue())) {
                    case Ongoing:
                    case Extended:
                      deleted = false;
                      break;


                  }
                }
              }

            }
            return deleted;
          }


        }


      }


      if (clazz == FundingSource.class) {
        FundingSource fundingSource = fundingSourceManager.getFundingSourceById(id);
        if (fundingSource.getProjectBudgets().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList()).size() > 0) {
          return false;
        }
      }
      if (clazz == FundingSourceInstitution.class) {
        FundingSourceInstitution fundingSourceInstitution =
          fundingSourceInstitutionManager.getFundingSourceInstitutionById(id);
        if (fundingSourceInstitution.getFundingSource().getProjectBudgets().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())
            && c.getInstitution().getId().equals(fundingSourceInstitution.getInstitution().getId()))
          .collect(Collectors.toList()).size() > 0) {
          return false;
        }
      }

      if (clazz == CrpPpaPartner.class) {
        CrpPpaPartner crpPpaPartner = crpPpaPartnerManager.getCrpPpaPartnerById(id);

        List<Project> partners = this.getProjectRelationsImpact(id, CrpPpaPartner.class.getName());
        if (partners.size() > 0) {
          return false;
        }
      }

      if (clazz == SrfTargetUnit.class) {
        SrfTargetUnit targetUnit = targetUnitManager.getSrfTargetUnitById(id);

        if (targetUnit == null) {
          return true;
        }

        if (targetUnit.getCrpProgramOutcomes().stream().filter(o -> o.isActive()).collect(Collectors.toList())
          .size() > 0) {
          return false;
        }

        if (targetUnit.getCrpMilestones().stream().filter(u -> u.isActive()).collect(Collectors.toList()).size() > 0) {
          return false;
        }
      }

      if (clazz == LocElementType.class) {
        LocElementType locElementType = locElementTypeManager.getLocElementTypeById(id);
        if (locElementType.getCrpLocElementTypes().stream().filter(o -> o.isActive()).collect(Collectors.toList())
          .size() > 0) {
          return false;
        }


      }

      if (clazz == ProjectPartnerPerson.class) {
        ProjectPartnerPerson partnerPerson = partnerPersonManager.getProjectPartnerPersonById(id);
        /*
         * if (partnerPerson != null) {
         * if (partnerPerson.getDeliverablePartnerships().stream()
         * .filter(o -> o.isActive() && o.getPhase() != null && o.getPhase().equals(this.getActualPhase()))
         * .collect(Collectors.toList()).size() > 0) {
         * return false;
         * }
         * }
         */
        List<Deliverable> deliverablesLeads = new ArrayList<>();
        if (partnerPerson != null) {
          List<DeliverablePartnership> deliverablePartnerships = partnerPerson.getDeliverablePartnerships().stream()
            .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
            .collect(Collectors.toList());
          for (DeliverablePartnership deliverablePartnership : deliverablePartnerships) {
            Deliverable deliverable = deliverablePartnership.getDeliverable();


            deliverable.setDeliverableInfo(deliverable.getDeliverableInfo(this.getActualPhase()));
            if (deliverable.getDeliverableInfo().getStatus() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())
              || deliverable.getDeliverableInfo().getStatus() == Integer
                .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
              if (!deliverablesLeads.contains(deliverable)) {
                if (deliverable.getDeliverableInfo().getYear() >= this.getActualPhase().getYear()) {

                  deliverablesLeads.add(deliverable);
                } else {
                  if (deliverable.getDeliverableInfo().getStatus().intValue() == Integer
                    .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                    if (deliverable.getDeliverableInfo().getNewExpectedYear() != null
                      && deliverable.getDeliverableInfo().getNewExpectedYear() >= this.getActualPhase().getYear()) {

                      deliverablesLeads.add(deliverable);
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


        ProjectBudget projectBudget = projectBudgetManager.getProjectBudgetById(id);
        FundingSource fundingSource =
          fundingSourceManager.getFundingSourceById(projectBudget.getFundingSource().getId());
        List<DeliverableFundingSource> deliverableFundingSources =
          fundingSource.getDeliverableFundingSources().stream()
            .filter(
              c -> c.isActive() && c.getDeliverable().isActive() && c.getPhase() != null
                && c.getPhase().getYear() == projectBudget.getYear() && c.getDeliverable().getProject() != null && c
                  .getDeliverable().getProject().getId().longValue() == projectBudget.getProject().getId().longValue())
          .collect(Collectors.toList());

        List<Deliverable> onDeliverables = new ArrayList<>();
        for (DeliverableFundingSource deliverableFundingSource : deliverableFundingSources) {
          if (deliverableFundingSource.getDeliverable().getDeliverableInfo(this.getActualPhase())
            .getYear() < projectBudget.getYear()
            && deliverableFundingSource.getDeliverable().getDeliverableInfo(this.getActualPhase()).getStatus()
              .intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            onDeliverables.add(deliverableFundingSource.getDeliverable());
          }
          if (deliverableFundingSource.getDeliverable().getDeliverableInfo(this.getActualPhase())
            .getYear() >= projectBudget.getYear()
            && deliverableFundingSource.getDeliverable().getDeliverableInfo(this.getActualPhase()).getStatus()
              .intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
            onDeliverables.add(deliverableFundingSource.getDeliverable());
          }
        }
        System.out.println(fundingSource.getId() + " " + Arrays.toString(onDeliverables.toArray()));
        if (!onDeliverables.isEmpty()) {
          return false;
        }
      }
      if (clazz == CustomLevelSelect.class) {
        LocElementType locElementType = locElementTypeManager.getLocElementTypeById(id);

        for (LocElement locElements : locElementType.getLocElements().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          if (!locElements.getProjectLocations().stream()
            .filter(c -> c.isActive() && c.getProject().getCrp().getId().longValue() == this.getCrpID().longValue())
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
        CrpClusterKeyOutput crpClusterKeyOutput = crpClusterKeyOutputManager.getCrpClusterKeyOutputById(id);
        if (crpClusterKeyOutput.getCrpClusterKeyOutputOutcomes().stream().filter(c -> c.isActive())
          .collect(Collectors.toList()).size() > 0) {
          return false;
        }
      }
      if (clazz == ProjectOutcome.class) {

        ProjectOutcome projectOutcome = projectOutcomeManager.getProjectOutcomeById(id);
        List<Deliverable> projects =
          this.getDeliverableRelationsProject(id, className, projectOutcome.getProject().getId());
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

  /**
   * Verify if the project have Cluster of Activity to activate Budget by CoA
   * 
   * @return true if the project have CoA or false otherwise.
   */
  public Boolean canEditBudgetByCoAs(long projectID) {
    Project project = projectManager.getProjectById(projectID);
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
  }

  public boolean canEditCenterType() {
    return this.hasPermissionNoBase(
      this.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, this.getCrpSession()));
  }


  public boolean canEditCrpAdmin() {
    String permission = this.generatePermission(Permission.CRP_ADMIN_EDIT_PRIVILEGES, this.getCrpSession());
    return securityContext.hasPermission(permission);
  }

  public boolean canProjectSubmited(long projectID) {
    String params[] = {crpManager.getCrpById(this.getCrpID()).getAcronym(), projectID + ""};
    return this.hasPermission(this.generatePermission(Permission.PROJECT_SUBMISSION_PERMISSION, params));
  }


  /**
   ************************ CENTER METHOD *********************
   * return true if the user can view the impactPathway
   * *********************************************************
   * Verify if the class model name have not relations for enable the delete button.
   * 
   * @param id - the id of the model
   * @param className - the model class name
   * @return true for enabling the delete button or false to disable the delete button.
   */
  public boolean centerCanBeDeleted(long id, String className) {
    Class clazz;
    try {

      clazz = Class.forName(className);

      // Verify CenterTopic Model
      if (clazz == CenterTopic.class) {
        CenterTopic topic = topicService.getResearchTopicById(id);

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
        CenterImpact impact = impactService.getResearchImpactById(id);

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
        CenterOutcome outcome = outcomeService.getResearchOutcomeById(id);

        List<CenterOutput> outputs = new ArrayList<>(
          outcome.getResearchOutputs().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));

        if (outputs != null) {
          if (!outputs.isEmpty()) {
            return false;
          }
        }
      }

      // Verify CenterProject Model
      if (clazz == CenterProject.class) {
        CenterProject project = projectService.getCenterProjectById(id);

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

        CenterOutput output = outputService.getResearchOutputById(id);

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

        CenterDeliverable deliverable = deliverableService.getDeliverableById(id);

        List<CenterDeliverableOutput> deliverableOutputs = new ArrayList<>(
          deliverable.getDeliverableOutputs().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));

        if (deliverableOutputs != null) {
          if (!deliverableOutputs.isEmpty()) {
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
   * ***********************CENTER METHOD********************
   * Check if the capDev section is Active
   * ************************************************************
   * 
   * @return true if the section is Active.
   */
  public boolean centerCapDevActive() {
    try {
      boolean sectionActive = Boolean.parseBoolean(this.getSession().get(APConstants.CENTER_CAPDEV_ACTIVE).toString());
      return sectionActive;
    } catch (Exception e) {
      return false;
    }

  }

  /**
   * ***********************CENTER METHOD********************
   * Check if the Impact Pathway section is Active
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
   * ***********************CENTER METHOD********************
   * Check if the Monitoring section is Active
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
   * ***********************CENTER METHOD********************
   * Check if the Summaries section is Active
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


    if (differences != null) {
      if (differences.contains(new HistoryDifference(field))) {
        int index = differences.indexOf(new HistoryDifference(field));
        HistoryDifference historyDifference = differences.get(index);
        historyDifference.setIndex(index);
        return historyDifference;
      }
    }
    return null;

  }

  /**
   * This method clears the cache and re-load the user permissions in the next iteration.
   */
  public void clearPermissionsCache() {
    ((APCustomRealm) securityContext.getRealm())
      .clearCachedAuthorizationInfo(securityContext.getSubject().getPrincipals());
  }

  public String crpActivitesModule() {
    return APConstants.CRP_ACTIVITES_MODULE;
  }


  /* Override this method depending of the delete action. */
  public String delete() {
    return SUCCESS;
  }


  @Override
  public String execute() throws Exception {
    if (save) {
      return this.save();
    } else if (delete) {
      return this.delete();
    } else if (cancel) {
      return this.cancel();
    } else if (next) {
      return this.next();
    } else if (add) {
      return this.add();
    } else if (submit) {
      return this.submit();
    }
    return INPUT;
  }

  /**
   * ***********************CENTER METHOD********************
   * This method calculates all the years between the start date and the end date.
   * ************************************************************
   * 
   * @return a List of numbers representing all the years, or an empty list if nothing found.
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
   * public String generatePermission(String permission, Map<String, Object> session, long crpID, String... params) {
   * Phase phase = this.getActualPhase(session, crpID);
   * String paramsRefactor[] = Arrays.copyOf(params, params.length);
   * paramsRefactor[0] = paramsRefactor[0] + ":" + phase.getDescription() + ":" + phase.getYear();
   * return this.getText(permission, paramsRefactor);
   * }
   */
  public String generatePermission(String permission, String... params) {
    Phase phase = this.getActualPhase();
    String paramsRefactor[] = Arrays.copyOf(params, params.length);
    paramsRefactor[0] = paramsRefactor[0] + ":" + phase.getDescription() + ":" + phase.getYear();
    return this.getText(permission, paramsRefactor);

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


  /**
   * get the actual
   * 
   * @return the actual phase of the crp
   */
  public Phase getActualPhase() {


    try {
      Map<Long, Phase> allPhases = null;
      if (this.getSession() != null) {
        if (!this.getSession().containsKey(APConstants.ALL_PHASES)) {
          List<Phase> phases = phaseManager.findAll().stream()
            .filter(c -> c.getCrp().getId().longValue() == this.getCrpID().longValue()).collect(Collectors.toList());
          phases.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
          Map<Long, Phase> allPhasesMap = new HashMap<>();
          for (Phase phase : phases) {
            allPhasesMap.put(phase.getId(), phase);
          }
          this.getSession().put(APConstants.ALL_PHASES, allPhasesMap);
        }

      }
      allPhases = (Map<Long, Phase>) this.getSession().get(APConstants.ALL_PHASES);

      Map<String, Parameter> parameters = this.getParameters();
      if (this.getPhaseID() != null) {
        long phaseID = this.getPhaseID();
        Phase phase = allPhases.get(new Long(phaseID));
        return phase;
      }
      if (parameters != null && parameters.containsKey(APConstants.PHASE_ID)) {
        long phaseID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
        Phase phase = allPhases.get(new Long(phaseID));
        return phase;
      }
      if (this.getSession().containsKey(APConstants.CURRENT_PHASE)) {
        Phase phase = (Phase) this.getSession().get(APConstants.CURRENT_PHASE);
        return phase;
      } else {
        Phase phase =
          phaseManager.findCycle(this.getCurrentCycleParam(), this.getCurrentCycleYearParam(), this.getCrpID());
        this.getSession().put(APConstants.CURRENT_PHASE, phase);
        return phase;
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
          List<Phase> phases = phaseManager.findAll().stream()
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
      Phase phase =
        phaseManager.findCycle(this.getCurrentCycleParam(), this.getCurrentCycleYearParam(), this.getCrpID());
      return phase;
    } catch (Exception e) {
      return new Phase(null, "", -1);
    }


  }


  /*
   * public Phase getActualPhase(Map<String, Object> session, long crpID) {
   * try {
   * if (session.containsKey(APConstants.CURRENT_PHASE)) {
   * return (Phase) session.get(APConstants.CURRENT_PHASE);
   * } else {
   * String cyle = "";
   * int year = 0;
   * if (Boolean.parseBoolean(session.get(APConstants.CRP_REPORTING_ACTIVE).toString())) {
   * cyle = APConstants.REPORTING;
   * year = Integer.parseInt(session.get(APConstants.CRP_REPORTING_YEAR).toString());
   * } else {
   * cyle = APConstants.PLANNING;
   * year = Integer.parseInt(session.get(APConstants.CRP_PLANNING_YEAR).toString());
   * }
   * Phase phase = phaseManager.findCycle(cyle, year, crpID);
   * session.put(APConstants.CURRENT_PHASE, phase);
   * return phase;
   * }
   * } catch (Exception e) {
   * return new Phase(null, "", -1);
   * }
   * }
   */


  /**
   * ************************ CENTER METHOD ******************************
   * This method calculates all the years between the start date and the end date.
   * ********************************************************************
   * 
   * @return a List of numbers representing all the years, or an empty list if nothing found.
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

  public Boolean getAutoSaveFilePath(String simpleName, String actionName, long id) {
    String composedClassName = simpleName;
    String actionFile = this.getCrpSession() + "_" + actionName;
    String autoSaveFile = id + "_" + composedClassName + "_" + actionFile + ".json";
    boolean exist = Paths.get(config.getAutoSaveFolder() + autoSaveFile).toFile().exists();
    return exist;
  }


  public String getBasePermission() {
    return basePermission;
  }

  public String getBaseUrl() {
    return config.getBaseUrl();
  }


  public String getBaseUrlMedia() {
    if (this.getCurrentCrp() != null) {
      return config.getBaseUrl() + "/crp";
    }

    if (this.getCurrentCenter() != null) {
      return config.getBaseUrl() + "/center";
    }

    return config.getBaseUrl() + "/crp";
  }

  public List<CrpCategoryEnum> getCategories() {

    return Arrays.asList(CrpCategoryEnum.values());
  }

  /**
   * ***********************CENTER METHOD***************************************************************
   * This method gets the specific section status from the sectionStatuses array for a CenterDeliverable.
   * ***************************************************************************************************
   * 
   * @param deliverableID is the deliverable ID to be identified.
   * @param section is the name of some section.
   * @return a CenterSectionStatus object with the information requested.
   */
  public CenterSectionStatus getCenterDeliverableStatus(long deliverableID) {

    CenterDeliverable deliverable = deliverableService.getDeliverableById(deliverableID);
    List<CenterSectionStatus> sectionStatuses;

    if (deliverable.getSectionStatuses() != null) {
      sectionStatuses = new ArrayList<>(deliverable.getSectionStatuses().stream()
        .filter(c -> c.getYear() == this.getCenterYear()).collect(Collectors.toList()));
    } else {
      return null;
    }

    if (!sectionStatuses.isEmpty()) {
      return sectionStatuses.get(0);
    }
    return null;
  }

  /**
   * ************************ CENTER METHOD *********************
   * Get the center that is currently save in the session, if the user access to
   * the platform whit a diferent url, get the current action to catch the center
   * 
   * @return the center session
   */
  public Long getCenterID() {
    if (session != null && !session.isEmpty()) {
      try {
        Center center = (Center) session.get(APConstants.SESSION_CENTER) != null
          ? (Center) session.get(APConstants.SESSION_CENTER) : null;
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
   * ***********************CENTER METHOD********************
   * This method gets the specific section status from the sectionStatuses array for a Outcome.
   * ********************************************************
   * 
   * @param deliverableID is the deliverable ID to be identified.
   * @param section is the name of some section.
   * @return a CenterSectionStatus object with the information requested.
   */
  public CenterSectionStatus getCenterOutcomeStatus(long outcomeID) {

    CenterOutcome outcome = outcomeService.getResearchOutcomeById(outcomeID);
    List<CenterSectionStatus> sectionStatuses;
    if (outcome.getSectionStatuses() != null) {
      sectionStatuses = new ArrayList<>(outcome.getSectionStatuses().stream()
        .filter(c -> c.getYear() == this.getCenterYear()).collect(Collectors.toList()));
    } else {
      return null;
    }

    if (!sectionStatuses.isEmpty()) {
      return sectionStatuses.get(0);
    }
    return null;
  }

  /**
   * ***********************CENTER METHOD********************
   * This method gets the specific section status from the sectionStatuses array for a Output.
   * ************************************************************
   * 
   * @param deliverableID is the deliverable ID to be identified.
   * @param section is the name of some section.
   * @return a CenterSectionStatus object with the information requested.
   */
  public CenterSectionStatus getCenterOutputStatus(long outputID) {

    CenterOutput output = outputService.getResearchOutputById(outputID);
    List<CenterSectionStatus> sectionStatuses;

    if (output.getSectionStatuses() != null) {
      sectionStatuses = new ArrayList<>(output.getSectionStatuses().stream()
        .filter(c -> c.getYear() == this.getCenterYear()).collect(Collectors.toList()));
    } else {
      return null;
    }

    if (!sectionStatuses.isEmpty()) {
      return sectionStatuses.get(0);
    }
    return null;
  }

  /**
   * ************************ CENTER METHOD *********************
   * Validate the sections of the Impact Pathway *
   * ***************************************************************
   * 
   * @return true if the IP is complete
   */
  public boolean getCenterSectionStatusIP(String section, long programID) {
    CenterProgram program = programService.getProgramById(programID);

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
   * ************************ CENTER METHOD *********************
   * validate the sections of the project
   * ***************************************************************
   * 
   * @return true if the Project is complete
   */
  public boolean getCenterSectionStatusProject(String section, long projectID) {

    CenterProject project = projectService.getCenterProjectById(projectID);

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
   * ************************ CENTER METHOD *********************
   * Get the center that is currently save in the session, if the user access to
   * the platform whit a diferent url, get the current action to catch the center
   * ***************************************************************
   * 
   * @return the center that the user has log in
   */
  public String getCenterSession() {
    if (session != null && !session.isEmpty()) {
      try {
        Center center = (Center) session.get(APConstants.SESSION_CENTER) != null
          ? (Center) session.get(APConstants.SESSION_CENTER) : null;
        // Assumed there is only one center in the system, the default one.
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

  public List<Center> getCentersList() {
    List<Center> centers = new ArrayList<>();
    if (!this.canAccessSuperAdmin()) {
      User user = this.getCurrentUser();
      user = userManager.getUser(user.getId());
      List<CenterUser> users =
        new ArrayList<>(user.getCenterUsers().stream().filter(u -> u.isActive()).collect(Collectors.toList()));

      for (CenterUser crpUser : users) {
        if (crpUser.getResearchCenter().isActive()) {
          centers.add(crpUser.getResearchCenter());
        }
      }
    } else {
      centers = centerService.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    }
    return centers;
  }

  public CenterSubmission getCenterSubmission() {
    return centerSubmission;
  }


  /**
   * ************************ CENTER METHOD *********************
   * return the actual center year
   * ***************************************************************
   * 
   * @return the actual year
   */
  public int getCenterYear() {
    return Calendar.getInstance().get(Calendar.YEAR);
  }


  public long getCGIARInstitution() {
    return APConstants.INSTITUTION_CGIAR;
  }

  public List<CrpClusterOfActivity> getClusterOutcome(long projectID, long crpProgramID) {
    Project project = projectManager.getProjectById(projectID);

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
    return config;
  }

  public List<CrpProgramOutcome> getContributionsOutcome(long projectID, long crpProgramID) {
    Project project = projectManager.getProjectById(projectID);

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

    Project project = projectManager.getProjectById(projectID);
    if (project != null) {
      if (project.getProjectFocuses() != null) {
        List<ProjectFocus> projectFocuses = new ArrayList<>(project.getProjectFocuses().stream()
          .filter(pf -> pf.isActive() && pf.getPhase().equals(this.getActualPhase())
            && pf.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList()));
        if (projectFocuses != null) {
          if (projectFocuses.size() >= 2) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public List<Crp> getCrpCategoryList(String category) {
    return crpManager.findAll().stream()
      .filter(c -> c.isMarlo() && c.getCategory().intValue() == Integer.parseInt(category))
      .collect(Collectors.toList());
  }

  /**
   * Get the crp that is currently save in the session, if the user access to the platform whit a diferent url, get the
   * current action to catch the crp
   * 
   * @return the crp that the user has log in
   */
  public Long getCrpID() {
    try {
      if (session != null && !session.isEmpty()) {
        try {
          Crp crp =
            (Crp) session.get(APConstants.SESSION_CRP) != null ? (Crp) session.get(APConstants.SESSION_CRP) : null;
          this.crpID = crp.getId();
        } catch (Exception e) {
          LOG.warn("There was a problem trying to find the user crp in the session.");
        }
      } else {

        return this.crpID;

      }
    } catch (Exception e) {
      LOG.error(
        "I'm not exactly sure what exception this is supposed to catch!  If this statement ever gets printed, I will be surprised!",
        e);
      /**
       * Original code swallows the exception and didn't even log it. Now we at least log it,
       * but we need to revisit to see if we should continue processing or re-throw the exception.
       */
    }
    return this.crpID;
  }

  /**
   * Get the Crp List
   * 
   * @return List<Crp> object
   */
  public List<Crp> getCrpList() {
    List<Crp> crps = new ArrayList<>();
    if (!this.canAccessSuperAdmin()) {
      User user = this.getCurrentUser();
      user = userManager.getUser(user.getId());
      List<CrpUser> users =
        new ArrayList<>(user.getCrpUsers().stream().filter(u -> u.isActive()).collect(Collectors.toList()));

      for (CrpUser crpUser : users) {
        if (crpUser.getCrp().isActive()) {
          crps.add(crpUser.getCrp());
        }
      }
    } else {
      crps = this.getCrpCategoryList("1");
    }

    return crps;
  }

  /**
   * Get the crp that is currently save in the session, if the user access to the platform whit a diferent url, get the
   * current action to catch the crp
   * 
   * @return the crp that the user has log in
   */
  public String getCrpSession() {
    if (session != null && !session.isEmpty()) {
      try {
        Crp crp =
          (Crp) session.get(APConstants.SESSION_CRP) != null ? (Crp) session.get(APConstants.SESSION_CRP) : null;
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
   * ************************ CENTER METHOD *********************
   * Get the center that is currently save in the session
   * ***************************************************************
   * 
   * @return the center that the user has log in
   */
  public Center getCurrentCenter() {
    if (session != null && !session.isEmpty()) {
      try {
        Center center = (Center) session.get(APConstants.SESSION_CENTER) != null
          ? (Center) session.get(APConstants.SESSION_CENTER) : null;
        this.currentCenter = center;
      } catch (Exception e) {
        LOG.warn("There was a problem trying to find the user center in the session.");
      }
    } else {

      this.currentCenter = null;

    }
    return this.currentCenter;
  }

  public Crp getCurrentCrp() {
    if (session != null && !session.isEmpty()) {
      try {
        Crp crp =
          (Crp) session.get(APConstants.SESSION_CRP) != null ? (Crp) session.get(APConstants.SESSION_CRP) : null;
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

  public String getCurrentCycleParam() {
    try {
      if (this.isReportingActiveParam()) {
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

  public int getCurrentCycleYearParam() {
    try {
      if (this.isReportingActiveParam()) {
        return Integer.parseInt(this.getSession().get(APConstants.CRP_REPORTING_YEAR).toString());
      } else {
        return Integer.parseInt(this.getSession().get(APConstants.CRP_PLANNING_YEAR).toString());
      }
    } catch (Exception e) {
      return 0;
    }
  }

  /**
   * Get the user that is currently saved in the session.
   * 
   * @return a user object or null if no user was found.
   */
  public User getCurrentUser() {
    User u = null;
    if (session != null && !session.isEmpty()) {
      try {
        u = session.get(APConstants.SESSION_USER) != null ? (User) session.get(APConstants.SESSION_USER) : null;
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

  public List<Deliverable> getDeliverableRelationsImpact(Long id, String className) {
    Class<?> clazz;
    List<Deliverable> deliverables = null;
    try {
      clazz = Class.forName(className);
      if (clazz == CrpClusterKeyOutput.class) {
        CrpClusterKeyOutput crpClusterKeyOutput = crpClusterKeyOutputManager.getCrpClusterKeyOutputById(id);
        List<DeliverableInfo> deList =
          crpClusterKeyOutput.getDeliverables().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        Set<Deliverable> deSet = new HashSet<>();
        for (DeliverableInfo deliverableInfo : deList) {
          Deliverable deliverable = deliverableInfo.getDeliverable();
          deliverable.getDeliverableInfo(this.getActualPhase());
          if (deliverable.getDeliverableInfo() != null) {
            if (deliverable.getDeliverableInfo().getYear() < this.getActualPhase().getYear()
              && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
              deSet.add(deliverable);
            }
            if (deliverable.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getActualPhase().getYear()
              && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
              deSet.add(deliverable);
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
        ProjectBudget projectBudget = projectBudgetManager.getProjectBudgetById(id);
        List<DeliverableFundingSource> deList = projectBudget.getFundingSource().getDeliverableFundingSources().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())
            && c.getDeliverable().getProject().getId().longValue() == projectID.longValue())
          .collect(Collectors.toList());
        Set<Deliverable> deSet = new HashSet<>();
        for (DeliverableFundingSource deliverableInfo : deList) {
          Deliverable deliverable = deliverableInfo.getDeliverable();
          long projectDB = deliverable.getProject().getId().longValue();
          if (deliverable.getProject() != null && projectDB == projectID) {
            if (deliverable.getDeliverableInfo() != null) {
              if (deliverable.getDeliverableInfo().getYear() < this.getActualPhase().getYear()
                && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                deSet.add(deliverable);
              }
              if (deliverable.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getActualPhase().getYear()
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

      if (clazz == Project.class) {
        ProjectBudget projectBudget = projectBudgetManager.getProjectBudgetById(id);
        List<DeliverableFundingSource> deList = projectBudget.getFundingSource().getDeliverableFundingSources().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())
            && c.getDeliverable().getProject().getId().longValue() == projectID.longValue())
          .collect(Collectors.toList());
        Set<Deliverable> deSet = new HashSet<>();
        for (DeliverableFundingSource deliverableInfo : deList) {
          Deliverable deliverable = deliverableInfo.getDeliverable();
          long projectDB = deliverable.getProject().getId().longValue();
          if (deliverable.getProject() != null && projectDB == projectID) {
            if (deliverable.getDeliverableInfo() != null) {
              if (deliverable.getDeliverableInfo().getYear() < this.getActualPhase().getYear()
                && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
                deSet.add(deliverable);
              }
              if (deliverable.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getActualPhase().getYear()
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

      if (clazz == ProjectOutcome.class) {
        deliverables = new ArrayList<>();
        ProjectOutcome projectOutcome = projectOutcomeManager.getProjectOutcomeById(id);
        List<CrpClusterKeyOutputOutcome> keyOutputOutcomes = projectOutcome.getCrpProgramOutcome()
          .getCrpClusterKeyOutputOutcomes().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        for (CrpClusterKeyOutputOutcome crpClusterKeyOutputOutcome : keyOutputOutcomes) {

          deliverables.addAll(this.getDeliverableRelationsImpact(
            crpClusterKeyOutputOutcome.getCrpClusterKeyOutput().getId(), CrpClusterKeyOutput.class.getName()));
        }
        List<Deliverable> deList = new ArrayList<>();

        for (Deliverable deliverable : deliverables) {
          if (deliverable.getProject() != null
            && deliverable.getProject().getId().longValue() == projectID.longValue()) {
            deList.add(deliverable);
          }
        }
        deliverables.clear();
        deliverables.addAll(deList);


      }

    } catch (Exception e) {
      e.printStackTrace();

    }
    return deliverables;

  }


  /**
   * This method gets the specific section status from the sectionStatuses array for a Deliverable.
   * 
   * @param deliverableID is the deliverable ID to be identified.
   * @param section is the name of some section.
   * @return a SectionStatus object with the information requested.
   */
  public SectionStatus getDeliverableStatus(long deliverableID) {
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
    if (deliverableID == 4063) {
      System.out.println("holi");
    }
    if (deliverable.getDeliverableInfo(this.getActualPhase()) != null) {
      DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(this.getActualPhase());
      if (deliverableInfo.getStatus() != null
        && deliverableInfo.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
        if (deliverableInfo.getYear() < this.getActualPhase().getYear()) {
          return new SectionStatus();
        }
      }
    }
    List<SectionStatus> sectionStatuses = deliverable.getSectionStatuses().stream()
      .filter(c -> c.getYear() == this.getCurrentCycleYear() && c.getCycle().equals(this.getCurrentCycle()))
      .collect(Collectors.toList());

    if (!sectionStatuses.isEmpty()) {
      return sectionStatuses.get(0);
    }
    return null;
  }

  public List<HistoryDifference> getDifferences() {
    return differences;
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
          fileDBManager.saveFileDB(dbFilePrev);
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
      fileDBManager.saveFileDB(dbFile);
      return dbFile;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }

  private boolean getFundingSourceStatus(FundingSource fundingSource) {
    fundingSource.setFundingSourceInfo(fundingSource.getFundingSourceInfo(this.getActualPhase()));
    if (fundingSource.getFundingSourceInfo(this.getActualPhase()) != null) {
      List<SectionStatus> sectionStatuses = fundingSource.getSectionStatuses().stream()
        .filter(c -> c.getCycle().equals(this.getActualPhase().getDescription())
          && c.getYear() == this.getActualPhase().getYear())

        .collect(Collectors.toList());

      if (!sectionStatuses.isEmpty()) {
        SectionStatus sectionStatus = sectionStatuses.get(0);
        return sectionStatus.getMissingFields().length() == 0
          && !this.getAutoSaveFilePath(fundingSource.getClass().getSimpleName(),
            ProjectSectionStatusEnum.FUNDINGSOURCE.getStatus(), fundingSource.getId());

      } else {

        fundingSourceValidator.validate(this, fundingSource, false);
        return this.getFundingSourceStatus(fundingSource);
      }
    } else {
      return false;
    }

  }

  public boolean getFundingSourceStatus(long fundingSourceID) {
    FundingSource fundingSource = fundingSourceManager.getFundingSourceById(fundingSourceID);
    return this.getFundingSourceStatus(fundingSource);
  }

  /**
   * Get the folder path according if the user navigate in center,crp or platform sections.
   * 
   * @return the String folder path.
   */
  public String getHeaderPath() {
    if (this.getCurrentCrp() != null) {
      return "crp/";
    }

    if (this.getCurrentCenter() != null) {
      return "center/";
    }
    return null;
  }

  public long getIFPRIId() {
    return APConstants.IFPRI_ID;
  }

  public boolean getImpactSectionStatus(String section, long crpProgramID) {
    SectionStatus sectionStatus = sectionStatusManager.getSectionStatusByCrpProgam(crpProgramID, section,
      this.getActualPhase().getDescription(), this.getActualPhase().getYear());
    if (sectionStatus != null) {
      if (sectionStatus.getMissingFields().length() == 0
        && !this.getAutoSaveFilePath(CrpProgram.class.getSimpleName(), section, crpProgramID)) {
        return true;
      }
    }
    return false;
  }

  public HashMap<String, String> getInvalidFields() {
    return invalidFields;
  }

  public String getJustification() {
    return justification;
  }


  public String getLiasons() {
    String liasonsUsers = "";
    User u = userManager.getUser(this.getCurrentUser().getId());
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

  public List<Auditlog> getListLog(IAuditLog object) {
    try {
      return auditLogManager.listLogs(object.getClass(), Long.parseLong(object.getId().toString()),
        this.getActionName(), this.getActualPhase().getId());
    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<Auditlog>();
    }
  }

  /**
   * Define default locale while we decide to support other languages in the future.
   */
  @Override
  public Locale getLocale() {
    return Locale.ENGLISH;
  }

  public StringBuilder getMissingFields() {
    return missingFields;
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

  public List<Deliverable> getOpenDeliverables(List<Deliverable> deliverables) {

    List<Deliverable> openDeliverables = new ArrayList<>();

    for (Deliverable a : deliverables) {

      if (a.isActive() && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == null
        || a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
          .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
        || (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
          .parseInt(ProjectStatusEnum.Extended.getStatusId())
          || a.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == 0)))) {

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

  public Map<String, Parameter> getParameters() {
    parameters = ActionContext.getContext().getParameters();
    return parameters;
  }


  public String getParameterValue(String param) {
    Object paramObj = this.getParameters().get(param);
    if (paramObj == null) {
      return null;
    }
    return ((String[]) paramObj)[0];
  }


  public Long getPhaseID() {
    return phaseID;
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
      List<Phase> phases = phaseManager.findAll().stream().filter(
        c -> c.getCrp().getId().longValue() == this.getCrpID().longValue() && c.getVisible() != null && c.getVisible())
        .collect(Collectors.toList());
      phases.sort((p1, p2) -> p1.getStartDate().compareTo(p2.getStartDate()));
      this.getSession().put(APConstants.PHASES, phases);
      return phases;
    }
  }

  public List<Phase> getPhasesImpact() {
    if (this.getSession().containsKey(APConstants.PHASES_IMPACT)) {
      return (List<Phase>) this.getSession().get(APConstants.PHASES_IMPACT);
    } else {
      List<Phase> phases = phaseManager
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
      phases = phaseManager
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
   * validate if the list of phases are on session if not, will be find on bd on json format
   * 
   * @return the jsons of the phases for the crp
   */
  public String getPhasesJson() {
    List<Phase> phases;
    if (this.getSession().containsKey(APConstants.PHASES)) {
      phases = (List<Phase>) this.getSession().get(APConstants.PHASES);
    } else {
      phases = phaseManager.findAll().stream().filter(
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

  public SectionStatus getProjectOutcomeStatus(long projectOutcomeID) {
    ProjectOutcome projectOutcome = projectOutcomeManager.getProjectOutcomeById(projectOutcomeID);

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
        CrpProgramOutcome crpProgramOutcome = crpProgramOutcomeManager.getCrpProgramOutcomeById(id);
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
      if (clazz == CrpClusterOfActivity.class) {
        CrpClusterOfActivity crpClusterOfActivity = crpClusterOfActivityManager.getCrpClusterOfActivityById(id);
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
        CrpMilestone crpMilestone = crpMilestoneManager.getCrpMilestoneById(id);
        List<ProjectMilestone> projectMilestones = crpMilestone.getProjectMilestones().stream().filter(c -> c.isActive()
          && c.getProjectOutcome().getPhase() != null && c.getProjectOutcome().getPhase().equals(this.getActualPhase()))
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
        CrpPpaPartner crpPpaPartner = crpPpaPartnerManager.getCrpPpaPartnerById(id);
        List<ProjectPartner> partners = crpPpaPartner.getInstitution().getProjectPartners().stream()
          .filter(
            c -> c.isActive() && c.getPhase() != null && c.getPhase().getId().equals(this.getActualPhase().getId())
              && c.getProject().getCrp().getId().longValue() == this.getCrpID().longValue())
          .collect(Collectors.toList());
        Set<Project> projectsSet = new HashSet<>();
        for (ProjectPartner projectPartner : partners) {
          projectsSet.add(projectPartner.getProject());
        }
        projects = new ArrayList<>();
        projects.addAll(projectsSet);

      }
    } catch (Exception e) {

    }

    List<Project> avaliableProjects = new ArrayList<>();
    if (projects != null) {
      for (Project project : projects) {
        if (project.getProjecInfoPhase(this.getActualPhase()).getPhase().equals(this.getActualPhase())) {
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

  public boolean getProjectSectionStatus(String section, long projectID) {
    boolean returnValue = false;
    SectionStatus sectionStatus;
    Project project;

    if (ProjectSectionStatusEnum.value(section.toUpperCase()) == null) {
      return false;
    }
    switch (ProjectSectionStatusEnum.value(section.toUpperCase())) {
      case OUTCOMES:
        project = projectManager.getProjectById(projectID);
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
          sectionStatus = sectionStatusManager.getSectionStatusByProjectOutcome(projectOutcome.getId(),
            this.getCurrentCycle(), this.getCurrentCycleYear(), section);
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
        project = projectManager.getProjectById(projectID);
        List<CaseStudyProject> caseStudies =
          project.getCaseStudyProjects().stream().filter(d -> d.isActive()).collect(Collectors.toList());
        List<CaseStudy> caStudies = new ArrayList<>();


        for (CaseStudyProject caseStudyProject : caseStudies) {
          if (caseStudyProject.isCreated() && caseStudyProject.getCaseStudy().getYear() == this.getCurrentCycleYear()) {
            caStudies.add(caseStudyProject.getCaseStudy());
            sectionStatus = sectionStatusManager.getSectionStatusByCaseStudy(caseStudyProject.getCaseStudy().getId(),
              this.getCurrentCycle(), this.getCurrentCycleYear(), section);
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

      case HIGHLIGHT:
        project = projectManager.getProjectById(projectID);
        List<ProjectHighlight> highlights = project.getProjectHighligths().stream()
          .filter(d -> d.isActive() && d.getYear().intValue() == this.getCurrentCycleYear())
          .collect(Collectors.toList());
        if (highlights.isEmpty()) {
          return true;
        }

        for (ProjectHighlight highlight : highlights) {

          sectionStatus = sectionStatusManager.getSectionStatusByProjectHighlight(highlight.getId(),
            this.getCurrentCycle(), this.getCurrentCycleYear(), section);
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
        project = projectManager.getProjectById(projectID);
        if (project.getProjecInfoPhase(this.getActualPhase()).getAdministrative() != null
          && project.getProjecInfoPhase(this.getActualPhase()).getAdministrative().booleanValue()) {
          return true;
        } else {
          List<Deliverable> deliverables =
            project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList());
          List<Deliverable> openA = new ArrayList<>();


          if (this.isPlanningActive()) {
            openA =
              deliverables.stream().filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null

                && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == null
                  || (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                    .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                  && a.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getActualPhase().getYear())
                  || (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                    .parseInt(ProjectStatusEnum.Extended.getStatusId())
                    || a.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == 0))))
                .collect(Collectors.toList());
          } else {
            openA = deliverables.stream()
              .filter(a -> a.isActive() && a.getDeliverableInfo(this.getActualPhase()) != null
                && a.getDeliverableInfo(this.getActualPhase()) != null
                && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == null
                  || a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                    .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                || (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
                  .parseInt(ProjectStatusEnum.Extended.getStatusId())
                  || a.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == 0))))
              .collect(Collectors.toList());

            openA.addAll(deliverables.stream()
              .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
                && d.getDeliverableInfo(this.getActualPhase()) != null
                && d.getDeliverableInfo(this.getActualPhase()).getYear() == this.getCurrentCycleYear()
                && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                && d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Complete.getStatusId()))
              .collect(Collectors.toList()));

            openA.addAll(deliverables.stream()
              .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null
                && d.getDeliverableInfo(this.getActualPhase()) != null
                && d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null
                && d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear().intValue() == this
                  .getCurrentCycleYear()
                && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
                && d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                  .parseInt(ProjectStatusEnum.Complete.getStatusId()))
              .collect(Collectors.toList()));

          }
          if (openA.isEmpty()) {
            return false;
          }
          for (Deliverable deliverable : openA) {
            sectionStatus = sectionStatusManager.getSectionStatusByDeliverable(deliverable.getId(),
              this.getCurrentCycle(), this.getCurrentCycleYear(), section);
            if (sectionStatus == null) {
              return false;
            }

            if (sectionStatus.getMissingFields().length() != 0) {
              return false;
            }

          }

          returnValue = true;
        }


        break;

      case ACTIVITIES:
        project = projectManager.getProjectById(projectID);

        project.setProjectActivities(new ArrayList<Activity>(project.getActivities().stream()
          .filter(a -> a.isActive() && a.getPhase().equals(this.getActualPhase()) && a.getActivityStatus() != null
            && ((a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
              || (a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))))
          .collect(Collectors.toList())));

        if (project.getProjectActivities().isEmpty()) {
          return true;
        }

        sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, this.getCurrentCycle(),
          this.getCurrentCycleYear(), section);
        if (sectionStatus != null) {
          if (sectionStatus.getMissingFields().length() == 0) {
            return true;
          }
        }
        break;


      case BUDGET:

        if (this.isReportingActive()) {
          return true;
        }
        project = projectManager.getProjectById(projectID);
        if (project.getProjectBudgets().stream()
          .filter(d -> d.isActive() && d.getPhase() != null && d.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList()).isEmpty()) {
          return false;
        }

        sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, this.getCurrentCycle(),
          this.getCurrentCycleYear(), section);
        if (sectionStatus != null) {
          if (sectionStatus.getMissingFields().length() == 0) {
            return true;
          }
        }
        break;

      case DESCRIPTION:
      case LOCATIONS:


        sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, this.getCurrentCycle(),
          this.getCurrentCycleYear(), section);
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

      case LEVERAGES:


        sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, this.getCurrentCycle(),
          this.getCurrentCycleYear(), section);
        if (sectionStatus != null) {
          if (sectionStatus.getMissingFields().length() == 0) {
            return true;
          }
        } else {
          return true;
        }
        returnValue = false;


        break;

      default:
        sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, this.getCurrentCycle(),
          this.getCurrentCycleYear(), section);
        if (sectionStatus != null) {
          if (sectionStatus.getMissingFields().length() == 0) {
            return true;
          }
          break;

        }
    }
    return returnValue;

  }


  public List<Submission> getProjectSubmissions(long projectID) {
    Project project = projectManager.getProjectById(projectID);
    List<Submission> submissions = project.getSubmissions()
      .stream().filter(c -> c.getCycle().equals(this.getCurrentCycle())
        && c.getYear().intValue() == this.getCurrentCycleYear() && (c.isUnSubmit() == null || !c.isUnSubmit()))
      .collect(Collectors.toList());
    if (submissions.isEmpty()) {
      return new ArrayList<>();
    }
    return submissions;
  }

  /**
   ************************ CENTER METHOD *********************
   * return true if the user can view the impactPathway
   * *********************************************************
   * Return the sync code if the center project has synchronized by another project
   * 
   * @param centerProjectID
   * @return the Sync Code
   */
  public String getProjectSyncCode(long centerProjectID) {


    CenterProject project = projectService.getCenterProjectById(centerProjectID);

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

  public int getReportingYear() {
    return Integer.parseInt(this.getSession().get(APConstants.CRP_REPORTING_YEAR).toString());
  }


  public HttpServletRequest getRequest() {
    return request;
  }

  public String getRoles() {
    String roles = "";
    User u = userManager.getUser(this.getCurrentUser().getId());
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

  public BaseSecurityContext getSecurityContext() {
    return securityContext;
  }

  public Map<String, Object> getSession() {
    return session;
  }

  public Submission getSubmission() {
    return submission;
  }


  public String getTimeZone() {
    TimeZone timeZone = TimeZone.getDefault();
    String display = timeZone.getDisplayName();
    return display;
  }

  public String getUrl() {
    return url;
  }


  public List<UserToken> getUsersOnline() {
    return SessionCounter.users;
  }

  public StringBuilder getValidationMessage() {
    return validationMessage;
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


  public int goldDataValue(long deliverableID) {
    Deliverable deliverableBD = deliverableManager.getDeliverableById(deliverableID);
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

  public boolean hasPermission(String fieldName) {

    if (basePermission == null) {
      return securityContext.hasPermission(fieldName);
    } else {
      if (this.getCrpSession() != null) {
        Phase phase = this.getActualPhase();
        String basePhase = this.getBasePermission().replaceAll(this.getCrpSession(),
          this.getCrpSession() + ":" + phase.getDescription() + ":" + phase.getYear());
        return securityContext.hasPermission(basePhase + ":" + fieldName) || securityContext.hasPermission(basePhase);
      } else {
        return securityContext.hasPermission(this.getBasePermission() + ":" + fieldName);
      }


    }
  }

  /**
   * ************************ CENTER METHOD *********************
   * validation base method to check the permission in MARLO centers.
   * ***************************************************************
   * 
   * @return true if the user have the permission
   */
  public boolean hasPermissionCenter(String fieldName) {
    if (basePermission == null) {
      return securityContext.hasPermission(fieldName);
    } else {
      return securityContext.hasPermission(this.getBasePermission() + ":" + fieldName);
    }
  }

  public boolean hasPermissionCrpIndicators(long liaisonID) {
    String params[] = {this.getCrpSession(), liaisonID + "",};
    boolean permission =
      this.hasPermissionNoBase(this.generatePermission(Permission.CRP_INDICATORS_PERMISSION, params));
    return permission;
  }

  public boolean hasPermissionNoBase(String fieldName) {

    return securityContext.hasPermission(fieldName);

  }

  public boolean hasPermissionSynthesis(long program) {
    String params[] = {this.getCrpSession(), program + "",};
    boolean permission =
      this.hasPermissionNoBase(this.generatePermission(Permission.SYNTHESIS_BY_MOG_PERMISSION, params));
    return permission;
  }


  public boolean hasPersmissionSubmit(long projectId) {
    String permission = this.generatePermission(Permission.PROJECT_SUBMISSION_PERMISSION,
      this.getCurrentCrp().getAcronym(), String.valueOf(projectId));
    boolean permissions = this.securityContext.hasPermission(permission);
    return permissions;
  }

  public boolean hasPersmissionSubmitImpact() {

    return this.hasPermission("submit");
  }

  /**
   * ************************ CENTER METHOD *********************
   * validate if the user can submit the impact pathway
   * ***************************************************************
   * 
   * @return true if the user have the permission
   */
  public boolean hasPersmissionSubmitIP(long programID) {
    CenterProgram program = programService.getProgramById(programID);
    String permission = this.generatePermissionCenter(Permission.RESEARCH_PROGRAM_SUBMISSION_PERMISSION,
      this.getCurrentCenter().getAcronym(), String.valueOf(program.getResearchArea().getId()),
      String.valueOf(programID));
    boolean permissions = this.securityContext.hasPermission(permission);
    return permissions;
  }

  /**
   * ************************ CENTER METHOD *********************
   * validate if the user can submit the project
   * ***************************************************************
   * 
   * @return true if the user have the permission
   */
  public boolean hasPersmissionSubmitProject(long projectID) {
    CenterProject project = projectService.getCenterProjectById(projectID);
    CenterProgram program = project.getResearchProgram();;
    String permission =
      this.generatePermissionCenter(Permission.PROJECT_SUBMISSION_PERMISSION, this.getCurrentCenter().getAcronym(),
        String.valueOf(program.getResearchArea().getId()), String.valueOf(program.getId()), String.valueOf(projectID));
    boolean permissions = this.securityContext.hasPermission(permission);
    return permissions;
  }

  public boolean hasPersmissionUnSubmit(long projectId) {
    String permission = this.generatePermission(Permission.PROJECT_UNSUBMISSION_PERMISSION,
      this.getCurrentCrp().getAcronym(), String.valueOf(projectId));
    boolean permissions = this.securityContext.hasPermission(permission);
    return permissions;
  }

  public boolean hasPersmissionUnSubmitImpact(long programID) {
    String permission = this.generatePermission(Permission.IMPACT_PATHWAY_UNSUBMISSION_PERMISSION,
      this.getCurrentCrp().getAcronym(), String.valueOf(programID));
    boolean permissions = this.securityContext.hasPermission(permission);
    return permissions;
  }

  public boolean hasProgramnsRegions() {
    try {
      return Boolean.parseBoolean(this.getSession().get(APConstants.CRP_HAS_REGIONS).toString());
    } catch (Exception e) {
      return false;
    }
  }

  public boolean hasSpecificities(String specificity) {
    try {
      boolean param = Boolean.parseBoolean(this.getSession().get(specificity).toString());
      return param;
    } catch (Exception e) {
      return false;
    }

  }


  public Boolean isA(long deliverableID) {
    try {
      Deliverable deliverableBD = deliverableManager.getDeliverableById(deliverableID);
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
    return securityContext.hasRole("Admin");
  }

  public boolean isAvailabePhase() {
    return availabePhase;
  }


  public boolean isCanEdit() {
    return canEdit;
  }

  public boolean isCanEditPhase() {
    return canEditPhase;
  }

  public boolean isCanSwitchProject() {
    return canSwitchProject;
  }

  /**
   * ************************ CENTER METHOD *********************
   * verify if the project is complete
   * ***************************************************************
   * 
   * @return true if the project is complete
   */
  public boolean isCompleteCenterProject(long projectID) {

    if (sectionStatusService.findAll() == null) {
      return false;
    }

    CenterProject project = projectService.getCenterProjectById(projectID);

    List<String> statuses = secctionStatusService.distinctSectionStatusProject(projectID);

    if (statuses.size() != 3) {
      return false;
    }

    List<CenterSectionStatus> sectionStatuses = new ArrayList<>(project.getSectionStatuses().stream()
      .filter(ss -> ss.getYear() == (short) this.getCenterYear()).collect(Collectors.toList()));

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
      ipLiaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonIntitution);

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

    List<SectionStatus> sectionsBD = sectionStatusManager.findAll();
    if (sectionsBD == null) {
      return false;
    }

    CrpProgram cpCrpProgram = crpProgramManager.getCrpProgramById(crpProgramID);
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
   * ************************ CENTER METHOD *********************
   * verify if the impact pathway is complete
   * ***************************************************************
   * 
   * @return true if the IP is complete
   */
  public boolean isCompleteIP(long programID) {

    if (sectionStatusService.findAll() == null) {
      return false;
    }

    CenterProgram researchProgram = programService.getProgramById(programID);

    List<String> statuses = secctionStatusService.distinctSectionStatus(programID);

    if (statuses.size() != 4) {
      return false;
    }


    if (!this.validateCenterOutcome(researchProgram)) {
      return false;
    }

    if (!this.validateCenterOutput(researchProgram)) {
      return false;
    }

    List<CenterSectionStatus> sectionStatuses = new ArrayList<>(researchProgram.getSectionStatuses().stream()
      .filter(ss -> ss.getYear() == (short) this.getCenterYear()).collect(Collectors.toList()));

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


  public boolean isCompletePreProject(long projectID) {

    Project project = projectManager.getProjectById(projectID);
    List<SectionStatus> sections = project.getSectionStatuses().stream().collect(Collectors.toList());
    int i = 0;
    int budgetFlagshipSection = 0;
    for (SectionStatus sectionStatus : sections) {
      if (sectionStatus.getCycle().equals(this.getCurrentCycle())
        && sectionStatus.getYear().intValue() == this.getCurrentCycleYear()) {
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


  public boolean isCompleteProject(long projectID) {

    try {
      Project project = projectManager.getProjectById(projectID);
      List<SectionStatus> sections = project.getSectionStatuses().stream().collect(Collectors.toList());
      int totalSections = 0;
      int deliverableSection = 0;
      int budgetCoASection = 0;
      int budgetFlagshipSection = 0;
      int outcomeSection = 0;
      int caseStudySection = 0;
      int highlightSection = 0;

      List<Deliverable> deliverables =
        project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList());
      List<Deliverable> openA = deliverables.stream()
        .filter(a -> a.isActive() && ((a.getDeliverableInfo(this.getActualPhase()).getStatus() == null
          || (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
          && a.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getCurrentCycleYear())
        || (a.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
          .parseInt(ProjectStatusEnum.Extended.getStatusId())
          || a.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == 0))))
        .collect(Collectors.toList());

      if (this.isReportingActive()) {
        openA.addAll(deliverables.stream()
          .filter(
            d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()).getYear() == this.getCurrentCycleYear()
              && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
              && d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Complete.getStatusId()))
          .collect(Collectors.toList()));
        openA
          .addAll(deliverables.stream()
            .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null
              && d.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear().intValue() == this
                .getCurrentCycleYear()
              && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
              && d.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
                .parseInt(ProjectStatusEnum.Complete.getStatusId()))
            .collect(Collectors.toList()));
      }

      for (Deliverable deliverable : openA) {
        deliverable = deliverableManager.getDeliverableById(deliverable.getId());
      }

      if (project.getProjecInfoPhase(this.getActualPhase()).getAdministrative() != null
        && project.getProjecInfoPhase(this.getActualPhase()).getAdministrative().booleanValue()) {
        deliverableSection = 1;
        totalSections++;
      }
      for (SectionStatus sectionStatus : sections) {
        if (sectionStatus.getCycle().equals(this.getCurrentCycle())
          && sectionStatus.getYear().intValue() == this.getCurrentCycleYear()) {

          if (sectionStatus.getSectionName().equals(ProjectSectionStatusEnum.DELIVERABLES.getStatus())) {
            Deliverable a = deliverableManager.getDeliverableById(sectionStatus.getDeliverable().getId());

            if (project.getProjecInfoPhase(this.getActualPhase()).getAdministrative() != null
              && !project.getProjecInfoPhase(this.getActualPhase()).getAdministrative().booleanValue()) {
              if (openA.contains(a)) {
                if (sectionStatus.getMissingFields().length() > 0) {
                  return false;
                }
              }
            }

          } else {
            if (sectionStatus.getMissingFields().length() > 0) {
              return false;
            }
          }

        }

      }
      if (sections.size() == 0) {
        return false;
      }

      if (this.isPlanningActive()) {
        for (SectionStatus sectionStatus : sections) {
          if (sectionStatus.getCycle().equals(this.getCurrentCycle())
            && sectionStatus.getYear().intValue() == this.getCurrentCycleYear()) {
            switch (ProjectSectionStatusEnum.value(sectionStatus.getSectionName().toUpperCase())) {

              case ACTIVITIES:
                if (this.hasSpecificities(APConstants.CRP_ACTIVITES_MODULE)) {
                  totalSections++;
                }

                break;
              case DESCRIPTION:
              case PARTNERS:
              case LOCATIONS:
              case BUDGET:
              case EXPECTEDSTUDIES:

                totalSections++;
                break;
              case DELIVERABLES:
                if (deliverableSection == 0) {
                  deliverableSection = 1;
                  totalSections++;
                }
                break;

              case OUTCOMES:
                if (outcomeSection == 0) {
                  outcomeSection = 1;
                  totalSections++;
                }
                break;
              case BUDGETBYCOA:
                if (budgetCoASection == 0) {
                  budgetCoASection = 1;
                  totalSections++;
                }
                break;
              case BUDGETBYFLAGSHIP:
                if (budgetFlagshipSection == 0) {
                  budgetFlagshipSection = 1;
                  totalSections++;
                }
                break;
            }

          }
        }

        if (budgetCoASection == 1 && budgetFlagshipSection == 0) {
          if (this.hasSpecificities(APConstants.CRP_ACTIVITES_MODULE)) {
            return totalSections == 9;
          } else {
            return totalSections == 8;
          }
        } else if (budgetCoASection == 0 && budgetFlagshipSection == 1) {
          return totalSections == 8;
        } else if (budgetCoASection == 1 && budgetFlagshipSection == 1) {
          if (this.hasSpecificities(APConstants.CRP_ACTIVITES_MODULE)) {
            return totalSections == 10;
          } else {
            return totalSections == 9;
          }
        } else {

          if (!(project.getProjecInfoPhase(this.getActualPhase()).getAdministrative() != null
            && project.getProjecInfoPhase(this.getActualPhase()).getAdministrative().booleanValue() == true)) {
            if (project.getProjectClusterActivities().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())
              .size() <= 1) {
              if (this.hasSpecificities(APConstants.CRP_ACTIVITES_MODULE)) {
                return totalSections == 7;
              } else {
                return totalSections == 6;
              }

            } else {

              if (this.hasSpecificities(APConstants.CRP_ACTIVITES_MODULE)) {
                return totalSections == 8;
              } else {
                return totalSections == 7;
              }


            }


          } else {
            if (this.hasSpecificities(APConstants.CRP_ACTIVITES_MODULE)) {
              return totalSections == 7;
            } else {
              return totalSections == 6;
            }

          }

        }
      } else {

        for (SectionStatus sectionStatus : sections) {
          if (sectionStatus.getCycle().equals(this.getCurrentCycle())
            && sectionStatus.getYear().intValue() == this.getCurrentCycleYear()) {
            switch (ProjectSectionStatusEnum.value(sectionStatus.getSectionName().toUpperCase())) {

              case DESCRIPTION:
              case PARTNERS:
              case LOCATIONS:
              case OUTCOMES_PANDR:
              case CCAFSOUTCOMES:
              case OUTPUTS:
              case BUDGET:
              case LEVERAGES:
              case OTHERCONTRIBUTIONS:
              case ACTIVITIES:
                totalSections++;
                break;
              case DELIVERABLES:
                if (deliverableSection == 0) {
                  deliverableSection = 1;
                  totalSections++;
                }
                break;

              case HIGHLIGHT:
                if (highlightSection == 0) {
                  highlightSection = 1;
                  totalSections++;
                }
                break;
              case CASESTUDIES:
                if (caseStudySection == 0) {
                  caseStudySection = 1;
                  totalSections++;
                }

                break;
            }

          }
        }

        project = projectManager.getProjectById(projectID);
        if (project.getProjecInfoPhase(this.getActualPhase()).getProjectEditLeader() == null
          || project.getProjecInfoPhase(this.getActualPhase()).getProjectEditLeader().booleanValue() == false) {
          return false;
        }

        List<ProjectHighlight> highlights = project.getProjectHighligths().stream()
          .filter(d -> d.isActive() && d.getYear().intValue() == this.getCurrentCycleYear())
          .collect(Collectors.toList());
        if (highlights.isEmpty()) {
          totalSections++;
        }

        List<CaseStudyProject> caseStudyProjects =
          project.getCaseStudyProjects().stream().filter(d -> d.isActive()).collect(Collectors.toList());
        List<CaseStudy> caseStudies = new ArrayList<>();
        for (CaseStudyProject caseStudyProject : caseStudyProjects) {
          if (caseStudyProject.isCreated() && caseStudyProject.getCaseStudy().getYear() == this.getCurrentCycleYear()) {
            caseStudies.add(caseStudyProject.getCaseStudy());
          }


        }
        if (caseStudies.isEmpty() && !project.getProjecInfoPhase(this.getActualPhase()).getAdministrative()) {
          totalSections++;
        }

        if ((project.getProjecInfoPhase(this.getActualPhase()).getAdministrative() != null
          && project.getProjecInfoPhase(this.getActualPhase()).getAdministrative().booleanValue() == true)) {
          if (this.hasSpecificities(APConstants.CRP_ACTIVITES_MODULE)) {
            return totalSections == 9;
          } else {
            return totalSections == 8;
          }

        } else {

          if (this.hasSpecificities(APConstants.CRP_ACTIVITES_MODULE)) {
            return totalSections == 12;
          } else {
            return totalSections == 11;
          }

        }

      }
    } catch (Exception e) {
      return false;
    }


  }

  public boolean isCompleteSynthesys(long program, int type) {

    List<SectionStatus> sectionStatus = null;
    IpProgram ipProgram = ipProgramManager.getIpProgramById(program);

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


  public boolean isCrpClosed() {
    try {
      // return Integer.parseInt(this.getSession().get(APConstants.CRP_CLOSED).toString()) == 1;
      return Boolean.parseBoolean(crpManager.getCrpById(this.getCrpID()).getCustomParameters().stream()
        .filter(c -> c.getParameter().getKey().equals(APConstants.CRP_CLOSED)).collect(Collectors.toList()).get(0)
        .getValue());
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isCrpRefresh() {
    try {
      // return Integer.parseInt(this.getSession().get(APConstants.CRP_CLOSED).toString()) == 1;
      return Boolean.parseBoolean(crpManager.getCrpById(this.getCrpID()).getCustomParameters().stream()
        .filter(c -> c.getParameter().getKey().equals(APConstants.CRP_REFRESH)).collect(Collectors.toList()).get(0)
        .getValue());
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isDataSaved() {
    return dataSaved;
  }

  public Boolean isDeliverableNew(long deliverableID) {
    /*
     * Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
     * SimpleDateFormat dateFormat = new SimpleDateFormat(APConstants.DATE_FORMAT);
     * if (this.isReportingActive()) {
     * try {
     * Date reportingDate =
     * dateFormat.parse(this.getSession().get(APConstants.CRP_OPEN_REAL_DATE_REPORTING).toString());
     * if (deliverable.getCreateDate().compareTo(reportingDate) >= 0) {
     * return true;
     * } else {
     * return false;
     * }
     * } catch (ParseException e) {
     * e.printStackTrace();
     * return false;
     * }
     * } else {
     * try {
     * Date reportingDate = dateFormat.parse(this.getSession().get(APConstants.CRP_OPEN_PLANNING_DATE).toString());
     * if (deliverable.getCreateDate().compareTo(reportingDate) >= 0) {
     * return true;
     * } else {
     * return false;
     * }
     * } catch (ParseException e) {
     * e.printStackTrace();
     * return false;
     * }
     * }
     */
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
    if (deliverable.getPhase() == null) {
      return false;
    }
    if (deliverable.getPhase().equals(this.getActualPhase())) {
      return true;
    }
    return false;
  }

  public boolean isDraft() {
    return draft;
  }


  public boolean isEditable() {
    return isEditable;
  }

  public boolean isEditStatus() {
    return editStatus;
  }

  public Boolean isF(long deliverableID) {


    try {
      Deliverable deliverableBD = deliverableManager.getDeliverableById(deliverableID);
      this.loadDissemination(deliverableBD);
      if (deliverableBD.getDissemination().getAlreadyDisseminated() != null
        && deliverableBD.getDissemination().getAlreadyDisseminated().booleanValue()) {
        return true;
      }
      if (deliverableBD.getDissemination().getAlreadyDisseminated() == null) {
        return null;
      }

      return false;
    } catch (Exception e) {
      return null;
    }

  }

  public boolean isFullEditable() {
    return fullEditable;
  }

  protected boolean isHttpPost() {
    if (this.getRequest().getMethod().equalsIgnoreCase("post")) {
      return true;
    }
    return false;
  }

  public Boolean isI(long deliverableID) {
    try {
      Deliverable deliverableBD = deliverableManager.getDeliverableById(deliverableID);
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
        if ((deliverableBD.getDissemination().getSynced() != null)
          && (deliverableBD.getDissemination().getSynced().booleanValue())) {
          return true;
        }

        switch (channel) {
          case "cgspace":
            if (!this.validURL(link)) {
              return null;
            }
            if ((link.contains("cgspace")) || (link.contains("hdl")) || (link.contains("handle"))) {
              return true;
            }
            break;
          case "dataverse":
            if (!link.contains("dataverse.harvard.edu")) {
              if (!this.validURL(link)) {
                return null;
              }
              return null;
            }
            break;
          case "other":
            return null;

          default:
            return null;

        }


        return true;
      }
      if (deliverableBD.getDissemination().getAlreadyDisseminated() == null) {
        return null;
      }
    } catch (Exception e) {
      return null;
    }
    return null;


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

  public boolean isPhaseOne() {
    try {
      if (this.isReportingActive() && this.getCrpSession().equals("ccafs") && (this.getCurrentCycleYear() == 2016)) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isPlanningActive() {
    return this.getActualPhase().getDescription().equals(APConstants.PLANNING);
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

  public boolean isPPA(Institution institution) {
    if (institution == null) {
      return false;
    }

    if (institution.getId() != null) {
      institution = institutionManager.getInstitutionById(institution.getId());
      if (institution != null) {
        if (institution.getCrpPpaPartners().stream().filter(c -> c.getCrp().getId().longValue() == this.getCrpID()
          && c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()).size() > 0) {
          return true;
        }
      }

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

    Project project = projectManager.getProjectById(projectID);


    if (this.isReportingActive()) {

      try {
        Date reportingDate = this.getActualPhase().getStartDate();
        if (project.getCreateDate().compareTo(reportingDate) >= 0) {
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
        if (project.getCreateDate().compareTo(reportingDate) >= 0) {
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
    Project project = projectManager.getProjectById(projectID);
    List<Submission> submissions = project.getSubmissions()
      .stream().filter(c -> c.getCycle().equals(this.getCurrentCycle())
        && c.getYear().intValue() == this.getCurrentCycleYear() && (c.isUnSubmit() == null || !c.isUnSubmit()))
      .collect(Collectors.toList());
    if (submissions.isEmpty()) {
      return false;
    }
    return true;
  }

  public Boolean isR(long deliverableID) {
    try {
      Deliverable deliverableBD = deliverableManager.getDeliverableById(deliverableID);
      if (deliverableBD.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense() == null) {
        return null;
      }
      if (deliverableBD.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense()) {
        if (deliverableBD.getDeliverableInfo(this.getActualPhase()).getLicense() == null) {
          return false;
        } else {
          if (!(deliverableBD.getDeliverableInfo(this.getActualPhase()).getLicense()
            .equals(LicensesTypeEnum.OTHER.getValue())
            || deliverableBD.getDeliverableInfo(this.getActualPhase()).getLicense()
              .equals(LicensesTypeEnum.CC_BY_ND.getValue())
            || deliverableBD.getDeliverableInfo(this.getActualPhase()).getLicense()
              .equals(LicensesTypeEnum.CC_BY_NC_ND.getValue()))) {
            return true;
          } else {
            if (deliverableBD.getDeliverableInfo(this.getActualPhase()).getAllowModifications() == null
              || !deliverableBD.getDeliverableInfo(this.getActualPhase()).getAllowModifications().booleanValue()) {
              return false;
            }
            if (deliverableBD.getDeliverableInfo(this.getActualPhase()).getOtherLicense() == null
              || deliverableBD.getDeliverableInfo(this.getActualPhase()).getOtherLicense().isEmpty()) {
              return false;
            }
            return true;
          }

        }
      }
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  public boolean isReportingActive() {

    return this.getActualPhase().getDescription().equals(APConstants.REPORTING);

  }

  public boolean isReportingActiveMenu() {


    return Boolean.parseBoolean(this.getSession().get(APConstants.CRP_REPORTING_ACTIVE).toString());

  }

  public boolean isReportingActiveParam() {

    if (this.getSession().containsKey(APConstants.TEMP_CYCLE)) {
      return true;
    }
    return Boolean.parseBoolean(this.getSession().get(APConstants.CRP_REPORTING_ACTIVE).toString());

  }

  public boolean isSaveable() {
    return saveable;
  }

  public boolean isSubmit(long projectID) {
    Project project = projectManager.getProjectById(projectID);
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
   * ************************ CENTER METHOD *********************
   * Check if the project is submitted
   * ***************************************************************
   * 
   * @return true if the project is submitted
   */
  public boolean isSubmitCenterProject(long projectID) {

    CenterProject project = projectService.getCenterProjectById(projectID);
    if (project != null) {

      CenterCycle cycle = cycleService.getResearchCycleById(ImpactPathwayCyclesEnum.MONITORING.getId());

      List<CenterSubmission> submissions = new ArrayList<>(project.getSubmissions().stream()
        .filter(s -> s.getResearchCycle().equals(cycle) && s.getYear().intValue() == this.getCenterYear())
        .collect(Collectors.toList()));

      if (submissions != null && submissions.size() > 0) {
        this.setCenterSubmission(submissions.get(0));
        return true;
      }
    }

    return false;
  }

  /**
   * ************************ CENTER METHOD *********************
   * Check if the impact pathway is submitted
   * ***************************************************************
   * 
   * @return true if the impact pathway is submitted
   */
  public boolean isSubmitIP(long programID) {

    CenterProgram program = programService.getProgramById(programID);
    if (program != null) {

      CenterCycle cycle = cycleService.getResearchCycleById(ImpactPathwayCyclesEnum.IMPACT_PATHWAY.getId());

      List<CenterSubmission> submissions = new ArrayList<>(program.getSubmissions().stream()
        .filter(s -> s.getResearchCycle().equals(cycle) && s.getYear().intValue() == this.getCenterYear())
        .collect(Collectors.toList()));

      if (submissions != null && submissions.size() > 0) {
        this.setCenterSubmission(submissions.get(0));
        return true;
      }
    }
    return false;
  }

  public boolean isSwitchSession() {
    return switchSession;
  }

  public void loadDissemination(Deliverable deliverableBD) {

    if (deliverableBD.getDeliverableDisseminations() != null) {
      deliverableBD.setDisseminations(new ArrayList<>(deliverableBD.getDeliverableDisseminations()));
      if (deliverableBD.getDeliverableDisseminations().size() > 0) {
        deliverableBD.setDissemination(deliverableBD.getDisseminations().get(0));
      } else {
        deliverableBD.setDissemination(new DeliverableDissemination());
      }

    }
  }


  public void loadLessons(Crp crp, Project project) {

    Project projectDB = projectManager.getProjectById(project.getId());
    List<ProjectComponentLesson> lessons = projectDB.getProjectComponentLessons().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())
        && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
      .collect(Collectors.toList());
    if (!lessons.isEmpty()) {
      project.setProjectComponentLesson(lessons.get(0));
    }
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

  public void loadLessons(Crp crp, Project project, String actionName) {

    Project projectDB = projectManager.getProjectById(project.getId());
    if (this.isReportingActive()) {

      List<ProjectComponentLesson> lessons = projectDB.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getActualPhase().getYear()
          && c.getCycle().equals(APConstants.REPORTING) && c.getComponentName().equals(actionName))
        .collect(Collectors.toList());
      if (!lessons.isEmpty()) {
        project.setProjectComponentLesson(lessons.get(0));
      }
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
      if (!lessons.isEmpty()) {
        project.setProjectComponentLesson(lessons.get(0));
      }
    }
  }

  public void loadLessonsOutcome(Crp crp, ProjectOutcome projectOutcome) {

    ProjectOutcome projectOutcomeDB = projectOutcomeManager.getProjectOutcomeById(projectOutcome.getId());
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

  public void loadLessonsSynthesis(Crp crp, IpProgram program) {

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

    if (deliverableBD.getDeliverableQualityChecks() != null) {

      if (deliverableBD.getDeliverableQualityChecks().size() > 0) {
        deliverableBD.setQualityCheck(deliverableBD.getDeliverableQualityChecks().iterator().next());
      } else {
        deliverableBD.setQualityCheck(new DeliverableQualityCheck());
      }

    }
  }

  public String next() {
    return NEXT;
  }

  @Override
  public void prepare() throws Exception {
    // So far, do nothing here!
  }

  /* Override this method depending of the save action. */
  public String save() {
    return SUCCESS;
  }

  public void saveLessons(Crp crp, Project project) {

    if (project.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader()
      && !this.isProjectNew(project.getId())) {

      String actionName = this.getActionName().replaceAll(crp.getAcronym() + "/", "");

      project.getProjectComponentLesson().setActive(true);
      project.getProjectComponentLesson().setActiveSince(new Date());
      project.getProjectComponentLesson().setComponentName(actionName);
      project.getProjectComponentLesson().setCreatedBy(this.getCurrentUser());
      project.getProjectComponentLesson().setModifiedBy(this.getCurrentUser());
      project.getProjectComponentLesson().setModificationJustification("");
      project.getProjectComponentLesson().setProject(project);

      project.getProjectComponentLesson().setCycle(this.getActualPhase().getDescription());
      project.getProjectComponentLesson().setYear(this.getActualPhase().getYear());
      project.getProjectComponentLesson().setPhase(this.getActualPhase());
      projectComponentLessonManager.saveProjectComponentLesson(project.getProjectComponentLesson());
    }


  }

  public void saveLessonsOutcome(Crp crp, ProjectOutcome projectOutcome) {

    Project project = projectManager.getProjectById(projectOutcome.getProject().getId());
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

        projectComponenetLesson.setActive(true);
        projectComponenetLesson.setActiveSince(new Date());
        projectComponenetLesson.setComponentName(actionName);
        projectComponenetLesson.setCreatedBy(this.getCurrentUser());
        projectComponenetLesson.setModifiedBy(this.getCurrentUser());
        projectComponenetLesson.setModificationJustification("");
        projectComponenetLesson.setProjectOutcome(projectOutcome);
        projectComponenetLesson.setPhase(this.getActualPhase());
        projectComponenetLesson.setCycle(this.getActualPhase().getDescription());
        projectComponenetLesson.setYear(this.getActualPhase().getYear());

        projectComponenetLesson = projectComponentLessonManager.saveProjectComponentLesson(projectComponenetLesson);
      } else {

        ProjectComponentLesson projectComponenetLesson = projectOutcome.getProjectComponentLesson();

        ProjectComponentLesson projectComponentDB =
          projectComponentLessonManager.getProjectComponentLessonById(projectComponenetLesson.getId());;

        projectComponentDB.setActive(true);
        projectComponentDB.setComponentName(actionName);
        projectComponentDB.setModifiedBy(this.getCurrentUser());
        projectComponentDB.setModificationJustification("");

        projectOutcome.getProjectComponentLesson().setPhase(this.getActualPhase());
        projectOutcome.getProjectComponentLesson().setCycle(this.getActualPhase().getDescription());
        projectOutcome.getProjectComponentLesson().setYear(this.getActualPhase().getYear());

        projectComponentDB.setLessons(projectComponenetLesson.getLessons());
        // project
        // projectComponenetLesson.setComponentName(projectOutcome.getP);

        projectComponentDB = projectComponentLessonManager.saveProjectComponentLesson(projectComponentDB);


      }
    }

  }

  public void saveLessonsSynthesis(Crp crp, IpProgram ipProgram) {

    String actionName = this.getActionName().replaceAll(crp.getAcronym() + "/", "");


    ipProgram.getProjectComponentLesson().setActive(true);
    ipProgram.getProjectComponentLesson().setActiveSince(new Date());
    ipProgram.getProjectComponentLesson().setComponentName(actionName);
    ipProgram.getProjectComponentLesson().setCreatedBy(this.getCurrentUser());
    ipProgram.getProjectComponentLesson().setModifiedBy(this.getCurrentUser());
    ipProgram.getProjectComponentLesson().setModificationJustification("");
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
    projectComponentLessonManager.saveProjectComponentLesson(ipProgram.getProjectComponentLesson());

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

  public void setCrpID(Long crpID) {
    this.crpID = crpID;
  }


  public void setCrpSession(String crpSession) {
    this.crpSession = crpSession;
  }

  public void setCurrentCenter(Center currentCenter) {
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

  public void setNext(boolean next) {
    this.next = true;
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

  public void setUrl(String url) {
    this.url = url;
  }

  public String specificityValue(String specificity) {
    try {
      String value = (this.getSession().get(specificity).toString());
      return value;
    } catch (Exception e) {
      return null;
    }

  }

  public String submit() {
    return SUCCESS;
  }

  /**
   * ************************ CENTER METHOD *********************
   * Validate the missing fields in the deliverables section
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
   * ************************ CENTER METHOD *********************
   * Validate the missing fields in the program impacts section
   * ***************************************************************
   * 
   * @return false if has missing fields.
   */
  public boolean validateCenterImpact(CenterProgram program, String sectionName) {

    CenterSectionStatus sectionStatus =
      secctionStatusService.getSectionStatusByProgram(program.getId(), sectionName, this.getCenterYear());

    if (sectionStatus == null) {
      return false;
    }
    if (sectionStatus.getMissingFields().length() != 0) {
      return false;
    }

    return true;
  }

  /**
   * ************************ CENTER METHOD *********************
   * Validate the missing fields in the outcome section
   * ***************************************************************
   * 
   * @return false if has missing fields.
   */
  public boolean validateCenterOutcome(CenterProgram program) {
    if (program != null) {
      List<CenterTopic> topics =
        new ArrayList<>(program.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList()));
      if (topics != null && !topics.isEmpty()) {
        for (CenterTopic researchTopic : topics) {
          List<CenterOutcome> outcomes = new ArrayList<>(
            researchTopic.getResearchOutcomes().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));
          if (outcomes != null && !outcomes.isEmpty()) {
            for (CenterOutcome researchOutcome : outcomes) {
              CenterSectionStatus sectionStatus = this.getCenterOutcomeStatus(researchOutcome.getId());
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
      } else {
        return false;
      }
    } else {
      return false;
    }

    return true;
  }

  /**
   * ************************ CENTER METHOD *********************
   * Validate the missing fields in the output section
   * ***************************************************************
   * 
   * @return false if has missing fields.
   */
  public boolean validateCenterOutput(CenterProgram program) {

    if (program != null) {
      List<CenterTopic> topics =
        new ArrayList<>(program.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList()));
      if (topics != null && !topics.isEmpty()) {
        for (CenterTopic researchTopic : topics) {
          List<CenterOutcome> outcomes = new ArrayList<>(
            researchTopic.getResearchOutcomes().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));
          if (outcomes != null && !outcomes.isEmpty()) {
            for (CenterOutcome researchOutcome : outcomes) {
              researchOutcome.setMilestones(new ArrayList<>(researchOutcome.getResearchMilestones().stream()
                .filter(rm -> rm.isActive()).collect(Collectors.toList())));

              List<CenterOutput> outputs = new ArrayList<>(
                researchOutcome.getResearchOutputs().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));
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
            }
          } else {
            return false;
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

  /**
   * ************************ CENTER METHOD *********************
   * Validate the missing fields in the project section
   * ***************************************************************
   * 
   * @return false if has missing fields.
   */
  public boolean validateCenterProject(CenterProject project, String sectionName) {
    CenterSectionStatus sectionStatus = secctionStatusService.getSectionStatusByProject(
      project.getResearchProgram().getId(), project.getId(), sectionName, this.getCenterYear());

    if (sectionStatus == null) {
      return false;
    }
    if (sectionStatus.getMissingFields().length() != 0) {
      return false;
    }

    return true;
  }

  /**
   * ************************ CENTER METHOD *********************
   * Validate the missing fields in the research topic section
   * ***************************************************************
   * 
   * @return false if has missing fields.
   */
  public boolean validateCenterTopic(CenterProgram program, String sectionName) {

    CenterSectionStatus sectionStatus =
      secctionStatusService.getSectionStatusByProgram(program.getId(), sectionName, this.getCenterYear());

    if (sectionStatus == null) {
      return false;
    }
    if (sectionStatus.getMissingFields().length() != 0) {
      return false;
    }

    return true;
  }

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