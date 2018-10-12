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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

import org.cgiar.ciat.auth.LDAPService;
import org.cgiar.ciat.auth.LDAPUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Hernán David Carvajal
 * @author Héctor F. Tobón R. - CIAT/CCAFS
 */
public class ManageUsersAction extends BaseAction {


  private static final long serialVersionUID = 281018603716118132L;


  private static Logger LOG = LoggerFactory.getLogger(ManageUsersAction.class);


  private static String PARAM_FIRST_NAME = "firstName";


  private static String PARAM_LAST_NAME = "lastName";


  private static String PARAM_EMAIL = "email";


  private static String PARAM_IS_ACTIVE = "isActive";


  private static String PARAM_CGIAR = "isCGIAR";


  private UserManager userManager;

  private String actionName;

  private String queryParameter;
  private List<Map<String, Object>> users;

  private Map<String, Object> emailStatus;
  private User newUser;
  private String message;


  private boolean showInputs;

  @Inject
  public ManageUsersAction(APConfig config, UserManager userManager) {
    super(config);
    this.userManager = userManager;
  }

  /**
   * Add a new user into the database;
   * 
   * @return true if the user was successfully added, false otherwise.
   */
  private boolean addUser() {
    newUser.setModificationJustification("User created in MARLO " + actionName.replace("/", "-"));

    newUser.setAutoSave(true);
    newUser.setId(null);
    newUser = userManager.saveUser(newUser);

    // If successfully added.
    if (newUser.getId() > 0) {
      newUser.setActive(false);
      newUser = userManager.saveUser(newUser);
      this.users = new ArrayList<>();
      Map<String, Object> userMap = new HashMap<>();
      userMap.put("id", newUser.getId());
      userMap.put("composedName", newUser.getComposedName());
      userMap.put("name", newUser.getComposedCompleteName());
      userMap.put("email", newUser.getEmail());
      userMap.put("fName", newUser.getFirstName());
      userMap.put("lName", newUser.getLastName());
      this.users.add(userMap);

      return true;
    } else {
      // If some error occurred.
      return false;
    }
  }


  /**
   * Create a new user in the system.
   * 
   * @return SUCCESS if user could be successfully created, INPUT if some information is needed and ERROR if some error
   *         appeared.
   * @throws Exception
   */
  public String create() throws Exception {
    emailStatus = new HashMap<>();
    showInputs = true;
    if (newUser.getEmail() != null) {


      if (this.isValidEmail(newUser.getEmail())) {
        boolean emailExists = false;
        // We need to validate that the email does not exist yet into our database.
        emailExists = userManager.getUserByEmail(newUser.getEmail()) == null ? false : true;

        // If email already exists.
        if (emailExists) {
          // If email already exists into our database.
          message = this.getText("manageUsers.email.existing");
          newUser = null;
          showInputs = false;
          return SUCCESS; // Stop here!
        }


        // Validate if is a CGIAR email.
        if (this.validateOutlookUser(newUser.getEmail())) {
          newUser.setCgiarUser(true); // marking it as CGIAR user.
          this.addUser();
        } else {

          if (newUser.getFirstName() != null && newUser.getLastName() != null
            && newUser.getFirstName().trim().length() > 0 && newUser.getLastName().trim().length() > 0) {
            newUser.setCgiarUser(false);
            if (!this.addUser()) {
              // If user could not be added.
              newUser = null;
              message = this.getText("manageUsers.email.notAdded");
            }
            return SUCCESS;
          } else {
            message = this.getText("manageUsers.email.validation");
            emailStatus.put("status", true);
            return SUCCESS;
          }
        }
      } else {
        message = this.getText("manageUsers.email.notValid");
      }


    }
    return SUCCESS;
  }

  @Override
  public String execute() throws Exception {
    // Nothing to do here yet.
    return SUCCESS;
  }

  public Map<String, Object> getEmailStatus() {
    return emailStatus;
  }

  /**
   * Get a message of the result of the query.
   * 
   * @return a confirmation message of the result
   */
  public String getMessage() {
    return this.message;
  }


  public List<Map<String, Object>> getUsers() {
    return users;
  }


  public boolean isShowInputs() {
    return showInputs;
  }


  private boolean isValidEmail(String emailStr) {
    boolean isValid = false;
    Matcher matcher =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(emailStr);
    if (matcher.find()) {
      isValid = true;
    }
    return isValid;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    // if searching a user, we need to get the queried String.
    if (ActionContext.getContext().getName().equals("searchUsers")) {
      queryParameter = StringUtils.trim(parameters.get(APConstants.QUERY_PARAMETER).getMultipleValues()[0]);
    } else if (ActionContext.getContext().getName().equals("createUser")) {
      // if Adding a new user, we need to get the info to be added.
      newUser = new User();
      newUser.setId((long) -1);
      try {
        newUser.setFirstName(StringUtils.trim(parameters.get(PARAM_FIRST_NAME).getMultipleValues()[0]));
      } catch (Exception e) {
        newUser.setFirstName(null);
      }
      try {
        newUser.setLastName(StringUtils.trim(parameters.get(PARAM_LAST_NAME).getMultipleValues()[0]));
      } catch (Exception e) {
        newUser.setLastName(null);
      }
      newUser.setEmail(StringUtils.trim(parameters.get(PARAM_EMAIL).getMultipleValues()[0]));
      newUser
        .setActive(StringUtils.trim(parameters.get(PARAM_IS_ACTIVE).getMultipleValues()[0]).equals("1") ? true : false);

      // isCGIAR = StringUtils.trim(parameters.get(PARAM_CGIAR).getMultipleValues()[0]).equals("1") ? true : false;

      actionName = StringUtils.trim(parameters.get("actionName").getMultipleValues()[0]);
    }
  }


  /**
   * Search a user in the database
   * 
   * @return SUCCESS if the search was successfully made.
   * @throws Exception if some error appear.
   */
  public String search() throws Exception {
    List<User> users = userManager.searchUser(queryParameter);
    this.users = new ArrayList<>();
    for (User user : users) {
      Map<String, Object> userMap = new HashMap<>();
      userMap.put("id", user.getId());
      userMap.put("composedName", user.getComposedName());
      userMap.put("username", user.getUsername());
      userMap.put("roles", user.getUserRoles().toString());
      userMap.put("name", user.getComposedCompleteName());
      userMap.put("email", user.getEmail());
      userMap.put("fName", user.getFirstName());
      userMap.put("lName", user.getLastName());
      userMap.put("isActive", user.isActive());
      userMap.put("autoSaveActive", user.isAutoSave());
      userMap.put("lastLogin", user.getLastLogin());
      this.users.add(userMap);
    }

    LOG.info("The search of users by '{}' was made successfully.", queryParameter);
    return SUCCESS;
  }

  public void setEmailStatus(Map<String, Object> emailStatus) {
    this.emailStatus = emailStatus;
  }


  public void setShowInputs(boolean showInputs) {
    this.showInputs = showInputs;
  }


  /**
   * Validate if a given user exists in the Outlook Active Directory .
   * 
   * @param email is the CGIAR email.
   * @return a populated user with all the information that is coming from the OAD, or null if the email does not exist.
   */
  private boolean validateOutlookUser(String email) {
    LDAPService service = new LDAPService();
    if (config.isProduction()) {
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
    if (user != null) {
      newUser.setFirstName(user.getFirstName());
      newUser.setLastName(user.getLastName());
      newUser.setUsername(user.getLogin().toLowerCase());
      return true;
    }
    return false;
  }

}
