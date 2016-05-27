/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.action;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.BaseSecurityContext;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.security.SessionCounter;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

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


  private static final long serialVersionUID = -740360140511380630L;
  public static final String CANCEL = "cancel";
  public static final String NEXT = "next";
  public static final String NOT_LOGGED = "401";
  public static final String NOT_AUTHORIZED = "403";
  public static final String NOT_FOUND = "404";

  public static final String SAVED_STATUS = "savedStatus";
  // Loggin
  private static final Logger LOG = LoggerFactory.getLogger(BaseAction.class);

  // button actions
  protected boolean save;
  protected boolean next;
  protected boolean delete;
  protected boolean cancel;
  protected boolean submit;
  protected boolean dataSaved;
  protected boolean add;
  private Map<String, Object> session;
  private HttpServletRequest request;
  private String basePermission;

  // Variables
  private String crpSession;


  // Managers
  @Inject
  private CrpManager crpManager;

  // User actions
  private boolean isEditable; // If user is able to edit the form.


  private boolean canEdit; // If user is able to edit the form.

  private boolean saveable; // If user is able to see the save, cancel, delete buttons

  private boolean fullEditable; // If user is able to edit all the form.
  // Config Variables
  @Inject
  protected BaseSecurityContext securityContext;
  protected APConfig config;
  private Map<String, Object> parameters;

  @Inject
  public BaseAction(APConfig config) {
    this.config = config;
    this.saveable = true;
    this.fullEditable = true;
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

  public boolean canAcessCrpAdmin() {
    return this.hasPermission(this.generatePermission(Permission.CRP_ADMIN_VISIBLE_PRIVILEGES, this.getCrpSession()));
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

  public HttpServletRequest getRequest() {
    return request;
  }


  public BaseSecurityContext getSecurityContext() {
    return securityContext;
  }

  public Map<String, Object> getSession() {
    return session;
  }

  public List<User> getUsersOnline() {
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


  public boolean isDataSaved() {
    return dataSaved;
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


  public boolean isSaveable() {
    return saveable;
  }

  public boolean isSubmit() {
    return submit;
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

  public void setEditableParameter(boolean isEditable) {
    this.isEditable = isEditable;
  }

  public void setFullEditable(boolean fullEditable) {
    this.fullEditable = fullEditable;
  }


  public void setNext(boolean next) {
    this.next = true;
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

  public void setSubmit(boolean submit) {
    this.submit = true;
  }

  public String submit() {
    return SUCCESS;
  }

}
