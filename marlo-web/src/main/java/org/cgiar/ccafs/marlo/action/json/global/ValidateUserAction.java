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
import org.cgiar.ccafs.marlo.data.model.ADLoginMessages;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;

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
  private Boolean agree;


  private String messageEror;


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
    this.getLoginMessages();
    if (this.getSession().containsKey(APConstants.LOGIN_MESSAGE)) {
      messageEror = this.getSession().get(APConstants.LOGIN_MESSAGE).toString();
    }

    if (user != null) {
      userFound.put("loginSuccess", true);
    } else {
      userFound.put("loginSuccess", false);
    }

    if (user != null) {
      user.setAgreeTerms(agree);
      userManager.saveLastLogin(user);

    }

    return SUCCESS;
  }


  public Boolean getAgree() {
    return agree;
  }

  private void getLoginMessages() {
    Session session = SecurityUtils.getSubject().getSession();
    if (session.getAttribute(APConstants.LOGIN_MESSAGE) != null) {
      switch ((String) session.getAttribute(APConstants.LOGIN_MESSAGE)) {
        case APConstants.LOGON_SUCCES:
          this.getSession().put(APConstants.LOGIN_MESSAGE, ADLoginMessages.LOGON_SUCCESS.getValue());
          break;
        case APConstants.ERROR_NO_SUCH_USER:
          this.getSession().put(APConstants.LOGIN_MESSAGE, ADLoginMessages.ERROR_NO_SUCH_USER.getValue());
          break;
        case APConstants.ERROR_LOGON_FAILURE:
          this.getSession().put(APConstants.LOGIN_MESSAGE, ADLoginMessages.ERROR_LOGON_FAILURE.getValue());
          break;
        case APConstants.ERROR_INVALID_LOGON_HOURS:
          this.getSession().put(APConstants.LOGIN_MESSAGE, ADLoginMessages.ERROR_INVALID_LOGON_HOURS.getValue());
          break;
        case APConstants.ERROR_PASSWORD_EXPIRED:
          this.getSession().put(APConstants.LOGIN_MESSAGE, ADLoginMessages.ERROR_PASSWORD_EXPIRED.getValue());
          break;
        case APConstants.ERROR_ACCOUNT_DISABLED:
          this.getSession().put(APConstants.LOGIN_MESSAGE, ADLoginMessages.ERROR_ACCOUNT_DISABLED.getValue());
          break;
        case APConstants.ERROR_ACCOUNT_EXPIRED:
          this.getSession().put(APConstants.LOGIN_MESSAGE, ADLoginMessages.ERROR_ACCOUNT_EXPIRED.getValue());
          break;
        case APConstants.ERROR_ACCOUNT_LOCKED_OUT:
          this.getSession().put(APConstants.LOGIN_MESSAGE, ADLoginMessages.ERROR_ACCOUNT_LOCKED_OUT.getValue());
          break;
        case APConstants.ERROR_LDAP_CONNECTION:
          this.getSession().put(APConstants.LOGIN_MESSAGE, ADLoginMessages.ERROR_LDAP_CONNECTION.getValue());
          break;
        case APConstants.USER_DISABLED:
          this.getSession().put(APConstants.LOGIN_MESSAGE, ADLoginMessages.USER_DISABLED.getValue());
          break;
        default:
          break;
      }
    }
  }


  public String getMessageEror() {
    return messageEror;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public Map<String, Object> getUserFound() {
    return userFound;
  }


  @Override
  public void prepare() throws Exception {
    // Map<String, Parameter> parameters = this.getParameters();
    // userEmail = StringUtils.trim(parameters.get(APConstants.USER_EMAIL).getMultipleValues()[0]);
    // userPassword = StringUtils.trim(parameters.get(APConstants.USER_PASSWORD).getMultipleValues()[0]);
  }


  public void setAgree(Boolean agree) {
    this.agree = agree;
  }


  public void setMessageEror(String messageEror) {
    this.messageEror = messageEror;
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
