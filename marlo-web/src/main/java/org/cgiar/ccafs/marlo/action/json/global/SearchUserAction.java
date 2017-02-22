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
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

import org.cgiar.ciat.auth.LDAPService;
import org.cgiar.ciat.auth.LDAPUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class SearchUserAction extends BaseAction {


  private static final long serialVersionUID = 9059423997951037898L;


  private UserManager userManager;

  private String userEmail;


  private User user;


  private Map<String, Object> userFound;


  private Map<String, Object> crpUserFound;

  @Inject
  public SearchUserAction(APConfig config, UserManager userManager) {
    super(config);
    this.userManager = userManager;
  }


  @Override
  public String execute() throws Exception {
    userFound = new HashMap<String, Object>();
    crpUserFound = new HashMap<String, Object>();
    boolean emailExists = false;
    // We need to validate that the email does not exist yet into our database.
    emailExists = userManager.getUserByEmail(userEmail) == null ? false : true;

    if (emailExists) {
      user = userManager.getUserByEmail(userEmail);

      userFound.put("newUser", false);
      userFound.put("id", user.getId());
      userFound.put("name", user.getFirstName());
      userFound.put("lastName", user.getLastName());
      userFound.put("username", user.getUsername());
      userFound.put("email", user.getEmail());
      userFound.put("cgiar", user.isCgiarUser());
      userFound.put("active", user.isActive());

      List<CrpUser> crpUsers =
        new ArrayList<CrpUser>(user.getCrpUsers().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

      if (!crpUsers.isEmpty()) {
        for (CrpUser crpUser : crpUsers) {
          crpUserFound.put("crpUserId", crpUser.getId());
          crpUserFound.put("crpId", crpUser.getCrp().getId());
          crpUserFound.put("crpName", crpUser.getCrp().getName());
          crpUserFound.put("crpAcronym", crpUser.getCrp().getAcronym());
        }

      }

      return SUCCESS;
    } else {
      if (userEmail.toLowerCase().endsWith(APConstants.OUTLOOK_EMAIL)) {
        LDAPUser userLDAP = new LDAPService().searchUserByEmail(userEmail.toLowerCase());
        if (userLDAP != null) {

          userFound.put("newUser", true);
          userFound.put("id", -1);
          userFound.put("name", userLDAP.getFirstName());
          userFound.put("lastName", userLDAP.getLastName());
          userFound.put("username", userLDAP.getLogin().toLowerCase());
          userFound.put("email", userLDAP.getEmail().toLowerCase());
          userFound.put("cgiar", true);

        } else {
          userFound.put("newUser", false);
          userFound.put("cgiar", false);
        }
      } else {
        userFound.put("newUser", true);
        userFound.put("id", -1);
        userFound.put("email", userEmail.toLowerCase());
        userFound.put("cgiar", false);
      }
    }


    return SUCCESS;
  }

  public Map<String, Object> getCrpUserFound() {
    return crpUserFound;
  }

  public String getUserEmail() {
    return userEmail;
  }


  public Map<String, Object> getUserFound() {
    return userFound;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    userEmail = StringUtils.trim(((String[]) parameters.get(APConstants.USER_EMAIL))[0]);
  }

  public void setCrpUserFound(Map<String, Object> crpUserFound) {
    this.crpUserFound = crpUserFound;
  }


  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public void setUserFound(Map<String, Object> userFound) {
    this.userFound = userFound;
  }


}
