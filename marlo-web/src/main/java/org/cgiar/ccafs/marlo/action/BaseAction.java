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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPpaPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramLeaderManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectComponentLessonManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Auditlog;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpPpaPartner;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.FileDB;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.BaseSecurityContext;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.security.SessionCounter;
import org.cgiar.ccafs.marlo.security.UserToken;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.ServletActionContext;
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
  protected boolean add;

  @Inject
  private AuditLogManager auditLogManager;
  private String basePermission;
  protected boolean cancel;
  private boolean canEdit; // If user is able to edit the form.

  private boolean canSwitchProject; // If user is able to Switch Project. (generally is a project leader)
  protected APConfig config;
  @Inject
  private CrpClusterKeyOutputManager crpClusterKeyOutputManager;
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
  // Variables
  private String crpSession;
  private Crp currentCrp;


  protected boolean dataSaved;

  protected boolean delete;
  @Inject
  private DeliverableManager deliverableManager;
  private boolean draft;


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
  private Map<String, Object> parameters;
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
  public BaseAction(APConfig config) {
    this.config = config;
    this.saveable = true;
    this.fullEditable = true;
    this.justification = "";
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


  public boolean canAccessSuperAdmin() {
    return this.securityContext.hasAllPermissions(Permission.FULL_PRIVILEGES);
  }

  public boolean canAcessCrpAdmin() {
    String permission = this.generatePermission(Permission.CRP_ADMIN_VISIBLE_PRIVILEGES, this.getCrpSession());
    return securityContext.hasPermission(permission);
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

  public boolean canAddBilateralProject() {
    String permission = this.generatePermission(Permission.PROJECT_BILATERAL_ADD, this.getCrpSession());
    return securityContext.hasPermission(permission);
  }

  public boolean canAddCoreProject() {
    String permission = this.generatePermission(Permission.PROJECT_CORE_ADD, this.getCrpSession());
    return securityContext.hasPermission(permission);
  }

  public boolean canBeDeleted(long id, String className) {
    Class clazz;
    try {
      clazz = Class.forName(className);
      if (clazz == UserRole.class) {
        UserRole userRole = userRoleManager.getUserRoleById(id);
        long cuId = Long.parseLong((String) this.getSession().get(APConstants.CRP_CU));
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

        List<ProjectFocus> programs =
          crpProgram.getProjectFocuses().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        boolean deleted = true;
        if (programs.size() > 0) {

          for (ProjectFocus projectFocus : programs) {
            if (projectFocus.getProject().getStatus() != null) {
              switch (ProjectStatusEnum.getValue(projectFocus.getProject().getStatus().intValue())) {
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
        for (LiaisonUser liaisonUser : crpProgramLeader.getUser().getLiasonsUsers()) {


          List<Project> projects =
            liaisonUser.getProjects().stream().filter(c -> c.isActive()).collect(Collectors.toList());
          boolean deleted = true;
          if (projects.size() > 0) {

            for (Project project : projects) {
              if (project.getLiaisonInstitution().getCrpProgram().getId()
                .equals(crpProgramLeader.getCrpProgram().getId())) {
                if (project.getStatus() != null) {
                  switch (ProjectStatusEnum.getValue(project.getStatus().intValue())) {
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

      if (clazz == CrpClusterKeyOutput.class) {
        CrpClusterKeyOutput crpClusterKeyOutput = crpClusterKeyOutputManager.getCrpClusterKeyOutputById(id);
        if (crpClusterKeyOutput.getCrpClusterKeyOutputOutcomes().stream().filter(c -> c.isActive())
          .collect(Collectors.toList()).size() > 0) {
          return false;
        }
      }

      if (clazz == FundingSource.class) {
        FundingSource fundingSource = fundingSourceManager.getFundingSourceById(id);
        if (fundingSource.getProjectBudgets().stream().filter(c -> c.isActive()).collect(Collectors.toList())
          .size() > 0) {
          return false;
        }
      }

      if (clazz == CrpPpaPartner.class) {
        CrpPpaPartner crpPpaPartner = crpPpaPartnerManager.getCrpPpaPartnerById(id);
        if (crpPpaPartner.getInstitution().getProjectPartners().stream().filter(c -> c.isActive())
          .collect(Collectors.toList()).size() > 0) {
          return false;
        }
      }
      return true;
    } catch (Exception e) {
      return false;
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
    if (project.getProjectClusterActivities().stream().filter(pc -> pc.isActive())
      .collect(Collectors.toList()) == null) {
      return false;
    }
    if (project.getProjectClusterActivities().stream().filter(pc -> pc.isActive()).collect(Collectors.toList())
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

  public boolean canProjectSubmited(long projectID) {
    String params[] = {crpManager.getCrpById(this.getCrpID()).getAcronym(), projectID + ""};
    return this.hasPermission(this.generatePermission(Permission.PROJECT_SUBMISSION_PERMISSION, params));
  }

  /**
   * This method clears the cache and re-load the user permissions in the next iteration.
   */
  public void clearPermissionsCache() {
    ((APCustomRealm) securityContext.getRealm())
      .clearCachedAuthorizationInfo(securityContext.getSubject().getPrincipals());
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

  public String generatePermission(String permission, String... params) {
    return this.getText(permission, params);

  }


  public String getActionName() {
    return ServletActionContext.getActionMapping().getName();
  }


  public String getBasePermission() {
    return basePermission;
  }


  public String getBaseUrl() {
    return config.getBaseUrl();
  }

  public APConfig getConfig() {
    return config;
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

    }
    return this.crpID;
  }


  /**
   * Get the Crp List
   * 
   * @return List<Crp> object
   */
  public List<Crp> getCrpList() {
    return crpManager.findAll().stream().filter(c -> c.isMarlo()).collect(Collectors.toList());
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

  public int getCurrentCycleYear() {
    try {
      if (this.isReportingActive()) {
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
   * This method gets the specific section status from the sectionStatuses array for a Deliverable.
   * 
   * @param deliverableID is the deliverable ID to be identified.
   * @param section is the name of some section.
   * @return a SectionStatus object with the information requested.
   */
  public SectionStatus getDeliverableStatus(long deliverableID) {
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);

    List<SectionStatus> sectionStatuses = deliverable.getSectionStatuses().stream()
      .filter(c -> c.getYear() == this.getCurrentCycleYear() && c.getCycle().equals(this.getCurrentCycle()))
      .collect(Collectors.toList());

    if (!sectionStatuses.isEmpty()) {
      return sectionStatuses.get(0);
    }
    return null;
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

  public boolean getImpactSectionStatus(String section, long crpProgramID) {
    SectionStatus sectionStatus = sectionStatusManager.getSectionStatusByCrpProgam(crpProgramID, section);
    if (sectionStatus != null) {
      if (sectionStatus.getMissingFields().length() == 0) {
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

  public List<Auditlog> getListLog(IAuditLog object) {
    try {
      return auditLogManager.listLogs(object.getClass(), Long.parseLong(object.getId().toString()),
        this.getActionName());
    } catch (Exception e) {
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

  public Map<String, Object> getParameters() {
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
        List<ProjectOutcome> projectOutcomes =
          project.getProjectOutcomes().stream().filter(c -> c.isActive()).collect(Collectors.toList());


        project.setOutcomes(projectOutcomes);
        if (project.getOutcomes().isEmpty()) {
          return false;
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


      case DELIVERABLES:
        project = projectManager.getProjectById(projectID);

        List<Deliverable> deliverables =
          project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList());
        List<Deliverable> openA = deliverables.stream()
          .filter(a -> a.isActive()
            && ((a.getStatus() == null || a.getStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
              || (a.getStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())
                || a.getStatus().intValue() == 0))))
          .collect(Collectors.toList());
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
        break;

      case ACTIVITIES:
        project = projectManager.getProjectById(projectID);

        project
          .setProjectActivities(
            new ArrayList<Activity>(project.getActivities().stream()
              .filter(a -> a.isActive()
                && ((a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                  || (a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))))
            .collect(Collectors.toList())));

        if (project.getProjectActivities().isEmpty()) {
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


      case BUDGET:
        project = projectManager.getProjectById(projectID);
        if (project.getProjectBudgets().stream().filter(d -> d.isActive()).collect(Collectors.toList()).isEmpty()) {
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


      default:
        sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID, APConstants.PLANNING,
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

  public int getReportingYear() {
    return Integer.parseInt(this.getSession().get(APConstants.CRP_REPORTING_YEAR).toString());
  }

  public HttpServletRequest getRequest() {
    return request;
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


  public boolean hasPermission(String fieldName) {
    if (basePermission == null) {
      return securityContext.hasPermission(fieldName);
    } else {
      return securityContext.hasPermission(this.getBasePermission() + ":" + fieldName);
    }
  }


  public boolean hasPermissionNoBase(String fieldName) {

    return securityContext.hasPermission(fieldName);

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

  public boolean hasProgramnsRegions() {
    try {
      return Boolean.parseBoolean(this.getSession().get(APConstants.CRP_HAS_REGIONS).toString());
    } catch (Exception e) {
      return false;
    }
  }


  /**
   * @param role
   * @return true if is the user role
   */
  public boolean isAdmin() {
    return securityContext.hasRole("Admin");
  }

  public boolean isCanEdit() {
    return canEdit;
  }

  public boolean isCanSwitchProject() {
    return canSwitchProject;
  }

  public boolean isCompleteImpact(long crpProgramID) {

    List<SectionStatus> sectionsBD = sectionStatusManager.findAll();
    if (sectionsBD == null) {
      return false;
    }
    List<SectionStatus> sections = sectionsBD.stream()
      .filter(c -> (c.getCrpProgram() != null && c.getCrpProgram().getId().longValue() == crpProgramID))
      .collect(Collectors.toList());

    for (SectionStatus sectionStatus : sections) {
      if (sectionStatus.getMissingFields().length() > 0) {
        return false;
      }
    }
    if (sections.size() == 0) {
      return false;
    }

    return true;
  }

  public boolean isCompletePreProject(long projectID) {

    Project project = projectManager.getProjectById(projectID);
    List<SectionStatus> sections = project.getSectionStatuses().stream().collect(Collectors.toList());
    int i = 0;
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
        }
      }


    }
    if (sections.size() == 0) {
      return false;
    }
    if (i != 3) {
      return false;
    }
    return true;
  }

  public boolean isCompleteProject(long projectID) {

    Project project = projectManager.getProjectById(projectID);
    List<SectionStatus> sections = project.getSectionStatuses().stream().collect(Collectors.toList());

    for (SectionStatus sectionStatus : sections) {
      if (sectionStatus.getMissingFields().length() > 0) {
        return false;
      }
    }
    if (sections.size() == 0) {
      return false;
    }
    if (sections.size() < 8) {
      return false;
    }
    return true;
  }


  public boolean isDataSaved() {
    return dataSaved;
  }

  public Boolean isDeliverableNew(long deliverableID) {

    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);

    SimpleDateFormat dateFormat = new SimpleDateFormat(APConstants.DATE_FORMAT);

    if (this.isReportingActive()) {

      try {
        Date reportingDate = dateFormat.parse(this.getSession().get(APConstants.CRP_OPEN_REPORTING_DATE).toString());
        if (deliverable.getCreateDate().compareTo(reportingDate) >= 0) {
          return true;
        } else {
          return false;
        }

      } catch (ParseException e) {
        e.printStackTrace();
        return false;
      }

    } else {
      try {

        Date reportingDate = dateFormat.parse(this.getSession().get(APConstants.CRP_OPEN_PLANNING_DATE).toString());
        if (deliverable.getCreateDate().compareTo(reportingDate) >= 0) {
          return true;
        } else {
          return false;
        }

      } catch (ParseException e) {
        e.printStackTrace();
        return false;
      }

    }
  }


  public boolean isDraft() {
    return draft;
  }


  public boolean isEditable() {
    return isEditable;
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

  public boolean isLessonsActive() {
    return Integer.parseInt(this.getSession().get(APConstants.CRP_LESSONS_ACTIVE).toString()) == 1;
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

  public boolean isPlanningActive() {
    return false;

  }

  public Boolean isProjectNew(long projectID) {

    Project project = projectManager.getProjectById(projectID);

    SimpleDateFormat dateFormat = new SimpleDateFormat(APConstants.DATE_FORMAT);

    if (this.isReportingActive()) {

      try {
        Date reportingDate = dateFormat.parse(this.getSession().get(APConstants.CRP_OPEN_REPORTING_DATE).toString());
        if (project.getCreateDate().compareTo(reportingDate) >= 0) {
          return true;
        } else {
          return false;
        }

      } catch (ParseException e) {
        e.printStackTrace();
        return false;
      }

    } else {
      try {
        Date reportingDate = dateFormat.parse(this.getSession().get(APConstants.CRP_OPEN_PLANNING_DATE).toString());
        if (project.getCreateDate().compareTo(reportingDate) >= 0) {
          return true;
        } else {
          return false;
        }

      } catch (ParseException e) {
        e.printStackTrace();
        return false;
      }

    }
  }

  public boolean isProjectSubmitted(long projectID) {

    Project project = projectManager.getProjectById(projectID);
    List<Submission> submissions = project.getSubmissions().stream()
      .filter(c -> c.getCycle().equals(APConstants.PLANNING) && c.getYear().intValue() == this.getCurrentCycleYear())
      .collect(Collectors.toList());
    if (submissions.isEmpty()) {
      return false;
    }
    return true;
  }

  public boolean isReportingActive() {

    return true;

  }

  public boolean isSaveable() {
    return saveable;
  }

  public boolean isSubmit(long projectID) {
    Project project = projectManager.getProjectById(projectID);
    int year = this.getCurrentCycleYear();
    List<Submission> submissions = project.getSubmissions().stream()
      .filter(c -> c.getCycle().equals(APConstants.PLANNING) && c.getYear().intValue() == year)
      .collect(Collectors.toList());

    if (submissions.isEmpty()) {
      return false;
    }
    return true;
  }


  public void loadLessons(Crp crp, Project project) {

    Project projectDB = projectManager.getProjectById(project.getId());
    if (this.isReportingActive()) {

      List<ProjectComponentLesson> lessons = projectDB.getProjectComponentLessons().stream()
        .filter(
          c -> c.isActive() && c.getYear() == this.getReportingYear() && c.getCycle().equals(APConstants.REPORTING)
            && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessons.isEmpty()) {
        project.setProjectComponentLesson(lessons.get(0));
      }
      List<ProjectComponentLesson> lessonsPreview = projectDB.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getReportingYear() && c.getCycle().equals(APConstants.PLANNING)
          && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessonsPreview.isEmpty()) {
        project.setProjectComponentLessonPreview(lessonsPreview.get(0));
      }
    } else {

      List<ProjectComponentLesson> lessons = projectDB.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getPlanningYear() && c.getCycle().equals(APConstants.PLANNING)
          && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessons.isEmpty()) {
        project.setProjectComponentLesson(lessons.get(0));
      }
    }
  }

  public void loadLessons(Crp crp, Project project, String actionName) {

    Project projectDB = projectManager.getProjectById(project.getId());
    if (this.isReportingActive()) {

      List<ProjectComponentLesson> lessons = projectDB.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getReportingYear()
          && c.getCycle().equals(APConstants.REPORTING) && c.getComponentName().equals(actionName))
        .collect(Collectors.toList());
      if (!lessons.isEmpty()) {
        project.setProjectComponentLesson(lessons.get(0));
      }
      List<ProjectComponentLesson> lessonsPreview = projectDB.getProjectComponentLessons()
        .stream().filter(c -> c.isActive() && c.getYear() == this.getReportingYear()
          && c.getCycle().equals(APConstants.PLANNING) && c.getComponentName().equals(actionName))
        .collect(Collectors.toList());
      if (!lessonsPreview.isEmpty()) {
        project.setProjectComponentLessonPreview(lessonsPreview.get(0));
      }
    } else {

      List<ProjectComponentLesson> lessons = projectDB.getProjectComponentLessons()
        .stream().filter(c -> c.isActive() && c.getYear() == this.getPlanningYear()
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
        .filter(
          c -> c.isActive() && c.getYear() == this.getReportingYear() && c.getCycle().equals(APConstants.REPORTING)
            && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessons.isEmpty()) {
        projectOutcome.setProjectComponentLesson(lessons.get(0));
      }
      List<ProjectComponentLesson> lessonsPreview = projectOutcomeDB.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getReportingYear() && c.getCycle().equals(APConstants.PLANNING)
          && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessonsPreview.isEmpty()) {
        projectOutcome.setProjectComponentLessonPreview(lessonsPreview.get(0));
      }
    } else {

      List<ProjectComponentLesson> lessons = projectOutcomeDB.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getPlanningYear() && c.getCycle().equals(APConstants.PLANNING)
          && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessons.isEmpty()) {
        projectOutcome.setProjectComponentLesson(lessons.get(0));
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

    if (project.isProjectEditLeader() && !this.isProjectNew(project.getId())) {

      String actionName = this.getActionName().replaceAll(crp.getAcronym() + "/", "");

      project.getProjectComponentLesson().setActive(true);
      project.getProjectComponentLesson().setActiveSince(new Date());
      project.getProjectComponentLesson().setComponentName(actionName);
      project.getProjectComponentLesson().setCreatedBy(this.getCurrentUser());
      project.getProjectComponentLesson().setModifiedBy(this.getCurrentUser());
      project.getProjectComponentLesson().setModificationJustification("");
      project.getProjectComponentLesson().setProject(project);
      if (this.isReportingActive()) {
        project.getProjectComponentLesson().setCycle(APConstants.REPORTING);
        project.getProjectComponentLesson().setYear(this.getReportingYear());

      } else {
        project.getProjectComponentLesson().setCycle(APConstants.PLANNING);
        project.getProjectComponentLesson().setYear(this.getPlanningYear());
      }
      projectComponentLessonManager.saveProjectComponentLesson(project.getProjectComponentLesson());
    }


  }


  public void saveLessonsOutcome(Crp crp, ProjectOutcome projectOutcome) {

    Project project = projectManager.getProjectById(projectOutcome.getProject().getId());
    if (project.isProjectEditLeader() && !this.isProjectNew(project.getId())) {

      String actionName = this.getActionName().replaceAll(crp.getAcronym() + "/", "");

      projectOutcome.getProjectComponentLesson().setActive(true);
      projectOutcome.getProjectComponentLesson().setActiveSince(new Date());
      projectOutcome.getProjectComponentLesson().setComponentName(actionName);
      projectOutcome.getProjectComponentLesson().setCreatedBy(this.getCurrentUser());
      projectOutcome.getProjectComponentLesson().setModifiedBy(this.getCurrentUser());
      projectOutcome.getProjectComponentLesson().setModificationJustification("");
      projectOutcome.getProjectComponentLesson().setProjectOutcome(projectOutcome);


      if (this.isReportingActive()) {
        projectOutcome.getProjectComponentLesson().setCycle(APConstants.REPORTING);
        projectOutcome.getProjectComponentLesson().setYear(this.getReportingYear());

      } else {
        projectOutcome.getProjectComponentLesson().setCycle(APConstants.PLANNING);
        projectOutcome.getProjectComponentLesson().setYear(this.getPlanningYear());
      }
      projectComponentLessonManager.saveProjectComponentLesson(projectOutcome.getProjectComponentLesson());
    }
  }

  public void setAdd(boolean add) {
    this.add = true;
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


  public void setCanSwitchProject(boolean canSwitchProject) {
    this.canSwitchProject = canSwitchProject;
  }

  public void setCrpID(Long crpID) {
    this.crpID = crpID;
  }


  public void setCrpSession(String crpSession) {
    this.crpSession = crpSession;
  }

  public void setDataSaved(boolean dataSaved) {
    this.dataSaved = dataSaved;
  }


  public void setDelete(boolean delete) {
    this.delete = delete;
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

  public void setUrl(String url) {
    this.url = url;
  }

  public String submit() {
    return SUCCESS;
  }
}