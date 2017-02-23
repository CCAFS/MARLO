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
import org.cgiar.ccafs.marlo.utils.SendMailS;

import org.cgiar.ciat.auth.LDAPService;
import org.cgiar.ciat.auth.LDAPUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Hernán David Carvajal
 * @author Héctor F. Tobón R. - CIAT/CCAFS
 */
public class SearchInstitutionsAction extends BaseAction {


  private static final long serialVersionUID = 281018603716118132L;


  private static Logger LOG = LoggerFactory.getLogger(SearchInstitutionsAction.class);

  private static String PARAM_FIRST_NAME = "firstName";
  private static String PARAM_LAST_NAME = "lastName";

  private static String PARAM_EMAIL = "email";
  private static String PARAM_IS_ACTIVE = "isActive";
  private UserManager userManager;
  private SendMailS sendMail;
  private String actionName;

  private String queryParameter;
  private List<Map<String, Object>> users;

  private User newUser;
  private String message;

  @Inject
  public SearchInstitutionsAction(APConfig config, UserManager userManager, SendMailS sendMail) {
    super(config);
    this.userManager = userManager;
    this.sendMail = sendMail;
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
      userMap.put("name", user.getComposedCompleteName());
      userMap.put("email", user.getEmail());
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
