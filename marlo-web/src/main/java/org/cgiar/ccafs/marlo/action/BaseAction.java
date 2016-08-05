/*****************************************************************
 * isCompleteImpact * This file is part of Managing Agricultural Research for Learning &
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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectComponentLessonManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Auditlog;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectComponentLesson;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.BaseSecurityContext;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.security.SessionCounter;
import org.cgiar.ccafs.marlo.security.UserToken;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
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
  public static final String SAVED_STATUS = "savedStatus";
  private static final long serialVersionUID = -740360140511380630L;
  protected boolean add;
  @Inject
  private AuditLogManager auditLogManager;
  private String basePermission;

  protected boolean cancel;

  private boolean canEdit; // If user is able to edit the form.
  protected APConfig config;
  private Long crpID;
  // Managers
  @Inject
  private CrpManager crpManager;
  // Variables
  private String crpSession;
  protected boolean dataSaved;
  protected boolean delete;
  private boolean draft;

  private boolean reportingActive;
  private boolean planningActive;
  private boolean lessonsActive;

  private int reportingYear;


  private int planningYear;

  private boolean fullEditable; // If user is able to edit all the form.
  // User actions
  private boolean isEditable; // If user is able to edit the form.


  // Justification of the changes
  private String justification;


  protected boolean next;
  @Inject
  private ProjectComponentLessonManager projectComponentLessonManager;
  private Map<String, Object> parameters;


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


  public boolean canAcessImpactPathway() {
    String permission = this.generatePermission(Permission.IMPACT_PATHWAY_VISIBLE_PRIVILEGES, this.getCrpSession());
    return securityContext.hasPermission(permission);
  }

  /* Override this method depending of the cancel action. */
  public String cancel() {
    return CANCEL;
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
    if (session != null && !session.isEmpty()) {
      try {
        Crp crp =
          (Crp) session.get(APConstants.SESSION_CRP) != null ? (Crp) session.get(APConstants.SESSION_CRP) : null;
        this.crpID = crp.getId();
      } catch (Exception e) {
        LOG.warn("There was a problem trying to find the user crp in the session.");
      }
    } else {

      this.crpID = null;

    }
    return this.crpID;
  }

  /**
   * Get the Crp List
   * 
   * @return List<Crp> object
   */
  public List<Crp> getCrpList() {
    return crpManager.findAll();
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

  public boolean getImpactSectionStatus(String section, long crpProgramID) {
    SectionStatus sectionStatus = sectionStatusManager.getSectionStatusByCrpProgam(crpProgramID, section);
    if (sectionStatus != null) {
      if (sectionStatus.getMissingFields().length() == 0) {
        return true;
      }
    }
    return false;
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

  public boolean hasPersmissionSubmit() {

    boolean permissions = this.hasPermission("submit");
    return permissions;
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
    if (sections.size() < 2) {
      return false;
    }
    return true;
  }

  public boolean isDataSaved() {
    return dataSaved;
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
    return Integer.parseInt(this.getSession().get(APConstants.CRP_PLANNING_ACTIVE).toString()) == 1;

  }


  public boolean isReportingActive() {

    return Integer.parseInt(this.getSession().get(APConstants.CRP_REPORTING_ACTIVE).toString()) == 1;

  }

  public boolean isSaveable() {
    return saveable;
  }

  public boolean isSubmit() {
    return submit;
  }


  public void loadLessons(Crp crp, Project project) {
    if (this.isReportingActive()) {

      List<ProjectComponentLesson> lessons = project.getProjectComponentLessons().stream()
        .filter(
          c -> c.isActive() && c.getYear() == this.getReportingYear() && c.getCycle().equals(APConstants.REPORTING)
            && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessons.isEmpty()) {
        project.setProjectComponentLesson(lessons.get(0));
      }
      List<ProjectComponentLesson> lessonsPreview = project.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getReportingYear() && c.getCycle().equals(APConstants.PLANNING)
          && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessonsPreview.isEmpty()) {
        project.setProjectComponentLessonPreview(lessonsPreview.get(0));
      }
    } else {

      List<ProjectComponentLesson> lessons = project.getProjectComponentLessons().stream()
        .filter(c -> c.isActive() && c.getYear() == this.getPlanningYear() && c.getCycle().equals(APConstants.PLANNING)
          && c.getComponentName().equals(this.getActionName().replaceAll(crp.getAcronym() + "/", "")))
        .collect(Collectors.toList());
      if (!lessons.isEmpty()) {
        project.setProjectComponentLesson(lessons.get(0));
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

  public String submit() {
    return SUCCESS;
  }

}
