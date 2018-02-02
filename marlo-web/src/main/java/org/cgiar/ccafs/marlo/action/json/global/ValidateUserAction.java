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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class ValidateUserAction extends BaseAction {

  private static final long serialVersionUID = 8993663508312484245L;
  // Managers
  private UserManager userManager;
  // Parameters
  private String userEmail;
  private String userPassword;

  private Map<String, Object> userFound;


  @Inject
  public ValidateUserAction(APConfig config, UserManager userManager) {
    super(config);
    this.userManager = userManager;
  }

  @Override
  public String execute() throws Exception {
    userFound = new HashMap<String, Object>();

    User user = userManager.login(userEmail, userPassword);
    if (user != null) {
      userFound.put("loginSuccess", true);
    } else {
      userFound.put("loginSuccess", false);
    }

    return SUCCESS;
  }


  public String getUserEmail() {
    return userEmail;
  }


  public Map<String, Object> getUserFound() {
    return userFound;
  }


  public String getUserPassword() {
    return userPassword;
  }


  @Override
  public void prepare() throws Exception {
    //Map<String, Parameter> parameters = this.getParameters();
    //userEmail = StringUtils.trim(parameters.get(APConstants.USER_EMAIL).getMultipleValues()[0]);
    //userPassword = StringUtils.trim(parameters.get(APConstants.USER_PASSWORD).getMultipleValues()[0]);
  }


  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public void setUserFound(Map<String, Object> userFound) {
    this.userFound = userFound;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }


}
