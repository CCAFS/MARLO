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

package org.cgiar.ccafs.marlo.action.center.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMail;

import org.cgiar.ciat.auth.LDAPService;
import org.cgiar.ciat.auth.LDAPUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
  private UserManager userManager;
  private SendMail sendMail;
  private String actionName;

  private String queryParameter;
  private List<Map<String, Object>> users;

  private User newUser;
  private String message;

  @Inject
  public ManageUsersAction(APConfig config, UserManager userManager, SendMail sendMail) {
    super(config);
    this.userManager = userManager;
    this.sendMail = sendMail;
  }

  /**
   * Add a new user into the database;
   * 
   * @return true if the user was successfully added, false otherwise.
   */
  private boolean addUser() {
    newUser.setModificationJustification("User created in MARLO " + actionName.replace("/", "-"));
    newUser.setActiveSince(new Date());
    newUser.setModifiedBy(this.getCurrentUser());
    newUser.setActive(true);
    newUser.setId(null);

    newUser = userManager.saveUser(newUser, this.getCurrentUser());
    // If successfully added.
    if (newUser.getId() > 0) {

      this.users = new ArrayList<>();
      Map<String, Object> userMap = new HashMap<>();
      userMap.put("id", newUser.getId());
      userMap.put("composedName", newUser.getComposedName());
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
    if (newUser.getEmail() != null) {
      boolean emailExists = false;
      // We need to validate that the email does not exist yet into our database.
      emailExists = userManager.getUserByEmail(newUser.getEmail()) == null ? false : true;

      // If email already exists.
      if (emailExists) {
        // If email already exists into our database.
        message = this.getText("manageUsers.email.existing");
        newUser = null;
        return SUCCESS; // Stop here!
      }

      // Validate if is a CGIAR email.
      if (newUser.getEmail().toLowerCase().endsWith(APConstants.OUTLOOK_EMAIL)) {
        newUser.setCgiarUser(true); // marking it as CGIAR user.

        // Validate and populate the information that is coming from the CGIAR Outlook Active Directory.
        newUser = this.validateOutlookUser(newUser.getEmail());
        // If user was not found in the Active Directory.
        if (newUser == null) {
          message = this.getText("manageUsers.email.doesNotExist");
          return SUCCESS; // Stop here!
        } else {
          // If user was found, let's add it into our database.
          this.addUser();
        }
      } else {
        // If the email does not belong to the CGIAR.
        if (newUser.getFirstName() != null && newUser.getLastName() != null) {
          newUser.setCgiarUser(false);
          // Generating a random password.
          // String newPassword = RandomStringUtils.random(6, "0123456789abcdefghijkmnpqrstuvwxyz");
          String newPassword = RandomStringUtils.randomNumeric(6);
          newUser.setPassword(newPassword);
          if (!this.addUser()) {
            // If user could not be added.
            newUser = null;
            message = this.getText("manageUsers.email.notAdded");
          }
          return SUCCESS;
        } else {
          message = this.getText("manageUsers.email.validation");
          return SUCCESS;
        }
      }
    }
    return SUCCESS;
  }


  @Override
  public String execute() throws Exception {
    // Nothing to do here yet.
    return SUCCESS;
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

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    // if searching a user, we need to get the queried String.
    if (ActionContext.getContext().getName().equals("searchUsers")) {

      queryParameter = StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]);
    } else if (ActionContext.getContext().getName().equals("createUser")) {
      // if Adding a new user, we need to get the info to be added.
      newUser = new User();
      newUser.setId((long) -1);
      if (!StringUtils.trim(((String[]) parameters.get(PARAM_EMAIL))[0]).toLowerCase()
        .endsWith(APConstants.OUTLOOK_EMAIL)) {
        newUser.setFirstName(StringUtils.trim(((String[]) parameters.get(PARAM_FIRST_NAME))[0]));
        newUser.setLastName(StringUtils.trim(((String[]) parameters.get(PARAM_LAST_NAME))[0]));
      }
      newUser.setEmail(StringUtils.trim(((String[]) parameters.get(PARAM_EMAIL))[0]));
      newUser.setActive(StringUtils.trim(((String[]) parameters.get(PARAM_IS_ACTIVE))[0]).equals("1") ? true : false);

      actionName = StringUtils.trim(((String[]) parameters.get("actionName"))[0]);
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
      this.users.add(userMap);
    }

    LOG.info("The search of users by '{}' was made successfully.", queryParameter);
    return SUCCESS;
  }


  /**
   * Validate if a given user exists in the Outlook Active Directory .
   * 
   * @param email is the CGIAR email.
   * @return a populated user with all the information that is coming from the OAD, or null if the email does not exist.
   */
  private User validateOutlookUser(String email) {
    LDAPService service = new LDAPService();
    if (config.isProduction()) {
      service.setInternalConnection(false);
    } else {
      service.setInternalConnection(true);
    }
    LDAPUser user = service.searchUserByEmail(email);
    if (user != null) {
      newUser.setFirstName(user.getFirstName());
      newUser.setLastName(user.getLastName());
      newUser.setUsername(user.getLogin().toLowerCase());
      return newUser;
    }
    return null;
  }

}
