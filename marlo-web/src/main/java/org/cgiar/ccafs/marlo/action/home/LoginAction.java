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

package org.cgiar.ccafs.marlo.action.home;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.ADLoginMessages;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.awt.Color;
import java.util.Date;
import java.util.Random;

import com.google.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.Session;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class LoginAction extends BaseAction {


  private static final long serialVersionUID = 8819133560997109925L;


  // Logging
  private static final Logger LOG = LoggerFactory.getLogger(LoginAction.class);


  // Variables
  private User user;
  private String url;
  private String crp;


  // Managers
  private UserManager userManager;


  private CrpManager crpManager;

  private CrpUserManager crpUserManager;

  @Inject
  public LoginAction(APConfig config, UserManager userManager, CrpManager crpManager, CrpUserManager crpUserManager) {
    super(config);
    this.userManager = userManager;
    this.crpManager = crpManager;
    this.crpUserManager = crpUserManager;
  }

  @Override
  public String execute() throws Exception {
    return SUCCESS;
  }

  public String getCrp() {
    return crp;
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

  @Override
  public String getUrl() {
    return url;
  }

  public User getUser() {
    return user;
  }


  public UserManager getUserManager() {
    return userManager;
  }

  public String login() {
    if (user != null) {

      // Check if is a valid user
      String userEmail = user.getEmail().trim().toLowerCase();
      User loggedUser = userManager.login(userEmail, user.getPassword());
      this.getLoginMessages();
      if (loggedUser != null) {

        // Obtain the crp selected
        Crp loggedCrp = crpManager.findCrpByAcronym(this.crp);

        // Validate if the user belongs to the selected crp
        if (loggedCrp != null) {
          if (crpUserManager.existCrpUser(loggedUser.getId(), loggedCrp.getId())) {
            loggedUser.setLastLogin(new Date());
            userManager.saveLastLogin(loggedUser);
            this.getSession().put(APConstants.SESSION_USER, loggedUser);
            this.getSession().put(APConstants.SESSION_CRP, loggedCrp);
            // put the crp parameters in the session
            for (CustomParameter parameter : loggedCrp.getCustomParameters()) {
              if (parameter.isActive()) {
                this.getSession().put(parameter.getParameter().getKey(), parameter.getValue());
              }
            }
            this.getSession().put("color", this.randomColor());
            // Validate if the user already logged in other session.
            /*
             * if (((User) this.getSession().get(APConstants.SESSION_USER)).getId() == -1) {
             * this.addFieldError("loginMessage", this.getText("login.error.duplicated"));
             * this.getSession().clear();
             * SecurityUtils.getSubject().logout();
             * user.setPassword(null);
             * return BaseAction.INPUT;
             * }
             */
          } else {
            this.addFieldError("loginMessage", this.getText("login.error.invalidUserCrp"));
            this.setCrpSession(loggedCrp.getAcronym());
            this.getSession().clear();
            SecurityUtils.getSubject().logout();
            user.setPassword(null);
            user.setPassword(null);
            return BaseAction.INPUT;
          }
        } else {
          this.addFieldError("loginMessage", this.getText("login.error.selectCrp"));
          user.setPassword(null);
          this.getSession().clear();
          SecurityUtils.getSubject().logout();
          user.setPassword(null);
          return BaseAction.INPUT;
        }

        LOG.info("User " + user.getEmail() + " logged in successfully.");
        /*
         * Save the user url with trying to enter the system to redirect after
         * loged.
         */
        String urlAction = ServletActionContext.getRequest().getHeader("Referer");
        /*
         * take the ".do" pattern in the url to differentiate the main page.
         * also discard the "logout" url beacause this action close the user session.
         */
        if (urlAction.contains(".do") && !urlAction.contains("logout")) {
          this.url = urlAction;
          return LOGIN;
        } else {
          return SUCCESS;
        }
      } else {
        LOG.info("User " + user.getEmail() + " tried to log-in but failed. Message : "
          + this.getSession().get(APConstants.LOGIN_MESSAGE));
        user.setPassword(null);
        if (this.getSession().get(APConstants.LOGIN_MESSAGE) != null) {
          this.addFieldError("loginMessage", this.getText((String) this.getSession().get(APConstants.LOGIN_MESSAGE)));
        } else {
          this.addFieldError("loginMessage", this.getText("login.error.userOrPass"));
        }
        return BaseAction.INPUT; // TODO change to return INPUT when the login front-end is finished.
      }
    } else {
      // Check if the user exists in the session
      return (this.getCurrentUser() == null) ? INPUT : SUCCESS;
    }
  }

  public String logout() {
    User user = (User) this.getSession().get(APConstants.SESSION_USER);
    if (user != null) {
      LOG.info("User {} logout succesfully", user.getEmail());
    }
    this.getSession().clear();
    SecurityUtils.getSubject().logout();

    // Hack for cleaning cached authorization.
    for (Realm realm : ((RealmSecurityManager) SecurityUtils.getSecurityManager()).getRealms()) {
      if (realm instanceof APCustomRealm) {
        APCustomRealm customRealm = (APCustomRealm) realm;
        customRealm.clearCachedAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
      }
    }

    return SUCCESS;
  }

  public String randomColor() {

    Random random = new Random(); // Probably really put this somewhere where it gets executed only once
    int red = random.nextInt(256);
    int green = random.nextInt(256);
    int blue = random.nextInt(256);
    Color color = new Color(red, green, blue);
    String hex = "#" + Integer.toHexString(color.getRGB()).substring(2);
    return hex;
  }

  public void setCrp(String crp) {
    this.crp = crp;
  }


  @Override
  public void setUrl(String url) {
    this.url = url;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setUserManager(UserManager userManager) {
    this.userManager = userManager;
  }

  @Override
  public void validate() {
    // If is the first time the user is loading the page
    if (user != null) {
      if (user.getEmail().isEmpty()) {
        this.addFieldError("user.email", this.getText("validation.field.required"));
        user.setPassword(null);
      }
    }
  }
}
