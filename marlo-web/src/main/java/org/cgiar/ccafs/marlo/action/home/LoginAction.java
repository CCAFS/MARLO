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
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.ADLoginMessages;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.CognitoAuthSpecificity;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

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

  // Test Cambios Jenkins

  private static final long serialVersionUID = 8819133560997109925L;


  // Logging
  private static final Logger LOG = LoggerFactory.getLogger(LoginAction.class);

  // Variables
  private User user;


  private String url;

  private String crp;


  private Long globalUnit;

  // Managers
  private final UserManager userManager;

  // GlobalUnit Manager
  private final GlobalUnitManager crpManager;


  private final CrpUserManager crpUserManager;

  private final CustomParameterManager customParameterManager;

  private final ParameterManager parameterManager;

  private List<GlobalUnit> crpList;
  private List<GlobalUnit> centerList;
  private List<GlobalUnit> platformsList;

  // @Inject
  public LoginAction(APConfig config, UserManager userManager, GlobalUnitManager crpManager,
    CrpUserManager crpUserManager, CustomParameterManager customParameterManager, ParameterManager parameterManager) {
    super(config);
    this.userManager = userManager;
    this.crpManager = crpManager;
    this.crpUserManager = crpUserManager;
    this.customParameterManager = customParameterManager;
    this.parameterManager = parameterManager;
  }

  @Override
  public String execute() throws Exception {
    crpList = getCrpCategoryList("1");
    platformsList = getCrpCategoryList("3");
    centerList = getCrpCategoryList("4");
    return SUCCESS;
  }

  public List<GlobalUnit> getCrpList() { return crpList; }
  public List<GlobalUnit> getCenterList() { return centerList; }
  public List<GlobalUnit> getPlatformsList() { return platformsList; }

  public String getCrp() {
    return crp;
  }


  public Long getGlobalUnit() {
    return globalUnit;
  }

  /**
   * CHG-COGNITO-AUTH-001-T11b: {@code true} when {@code candidateEmail} resolves to an
   * {@code is_cgiar_user = 1} account that belongs to at least one Global Unit with
   * {@code cognito_auth_active} enabled.
   * <p>
   * This is {@code ValidateUserAction#isCgiarCredentialRelayBlocked()} (T11) adapted to this action's
   * shape -- see that method's javadoc for the full MIG-001 reasoning, which applies unchanged here. The
   * short version: refusing on "any membership is migrated" would lock a CGIAR user with a non-migrated
   * membership out of that unit's local login entirely, which MIG-001's <i>Both paths coexist</i> scenario
   * forbids. So when {@code selectedGlobalUnit} is a unit the account actually belongs to, that unit alone
   * decides.
   * <p>
   * <b>{@code selectedGlobalUnit} is a REQUEST, not an authority.</b> It is resolved from the caller-supplied
   * {@code crp} acronym ({@link GlobalUnitManager#findGlobalUnitByAcronym(String)}), which only proves the
   * acronym exists -- not that this account belongs to it. T11's first correction FAILed its audit for
   * exactly this shape: trusting a caller-named unit without checking it against real membership let
   * {@code &globalUnitId=99999} switch the guard off entirely (execution.md 15.1). Here, a
   * {@code selectedGlobalUnit} that matches no membership is therefore treated as no selection at all, and
   * the method falls through to the fail-closed sweep across every membership.
   *
   * @param candidateEmail the trimmed, lowercased email the caller is attempting to authenticate as
   * @param selectedGlobalUnit the Global Unit resolved from the {@code crp} acronym, or {@code null} when
   *        none was supplied or none resolved
   * @return {@code true} only when the account is CGIAR-authenticated AND the deciding Global Unit (the
   *         selected one when it is a real membership, otherwise any membership) has the flag active;
   *         {@code false} for every local ({@code is_cgiar_user = 0}) account and for an account this
   *         action cannot resolve at all (SEC-005's {@code BUT MUST NOT} clause)
   */
  private boolean isCgiarCredentialRelayBlocked(String candidateEmail, GlobalUnit selectedGlobalUnit) {
    if (candidateEmail == null || candidateEmail.trim().isEmpty()) {
      return false;
    }
    User candidate = this.userManager.getUserByEmail(candidateEmail);
    if (candidate == null) {
      candidate = this.userManager.getUserByUsername(candidateEmail);
    }
    if (candidate == null || !candidate.isCgiarUser()) {
      return false;
    }
    List<GlobalUnit> memberships = this.crpManager.crpUsers(candidateEmail);

    // MIG-001: when the selected Global Unit is one the account actually belongs to, that unit alone
    // decides -- a user in a migrated X and a non-migrated Y must still reach Y's local login. The
    // membership check is the whole security of this branch; see the javadoc above for why
    // `selectedGlobalUnit` cannot be trusted on its own.
    if (selectedGlobalUnit != null) {
      for (GlobalUnit membership : memberships) {
        if (selectedGlobalUnit.getId().equals(membership.getId())) {
          return CognitoAuthSpecificity.isActiveFor(membership, this.customParameterManager, this.parameterManager);
        }
      }
      // Matched no membership: treat it as no selection at all and fall through to the fail-closed sweep.
    }

    for (GlobalUnit membership : memberships) {
      if (CognitoAuthSpecificity.isActiveFor(membership, this.customParameterManager, this.parameterManager)) {
        return true;
      }
    }
    return false;
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

    // Load lists for login page display
    if (platformsList == null) {
      crpList = getCrpCategoryList("1");
      platformsList = getCrpCategoryList("3");
      centerList = getCrpCategoryList("4");
    }

    if (user != null) {

      // Obtain the global unit selected
      // GlobalUnit loggedCrp = crpManager.getGlobalUnitById(globalUnit);
      GlobalUnit loggedCrp = crpManager.findGlobalUnitByAcronym(crp);
      // Check if is a valid user
      String userEmail = user.getEmail().trim().toLowerCase();

      // CHG-COGNITO-AUTH-001-T11b (SEC-005): the guard runs, and can refuse, BEFORE userManager.login() is
      // reached -- once that call executes the submitted password has already left MARLO for the realm's
      // LDAP branch. Mirrors ValidateUserAction#isCgiarCredentialRelayBlocked() (T11), the reference
      // implementation corrected after two audits (execution.md 15-16): a caller-supplied Global Unit may
      // only narrow the decision to a unit the account actually holds, it may never let the decision escape
      // one, and the flag is never read except through the shared CognitoAuthSpecificity resolver (PS-16).
      if (this.isCgiarCredentialRelayBlocked(userEmail, loggedCrp)) {
        LOG.info("User " + user.getEmail() + " tried to log-in but failed. Message : "
          + ADLoginMessages.ERROR_LOGON_FAILURE.getValue());
        user.setPassword(null);
        // Same generic shape a wrong password produces (design.md 5.3, matching T11's ValidateUserAction
        // guard): no new oracle telling a caller which accounts are CGIAR-migrated (SEC-005's BUT MUST NOT
        // clause). ADLoginMessages's literal-string values are used directly, not as i18n keys (DD-7) --
        // this is the exact value APCustomRealm/LDAPAuthenticator leave behind for a genuine wrong password.
        this.addFieldError("loginMessage", this.getText(ADLoginMessages.ERROR_LOGON_FAILURE.getValue()));
        return BaseAction.INPUT;
      }

      User loggedUser = userManager.login(userEmail, user.getPassword());
      this.getLoginMessages();
      if (loggedUser != null) {

        return this.login(loggedUser, loggedCrp);
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
      if (this.getCurrentUser() != null) {
        switch (this.getCurrentCrp().getGlobalUnitType().getId().intValue()) {
          case 1:
            return SUCCESS;

          case 2:
            this.url = this.getBaseUrl() + "/" + this.getCurrentCrp().getAcronym() + "/centerDashboard.do";
            return LOGIN;

          case 3:
            return SUCCESS;
          case 4:
            return SUCCESS;
          case 5:
            return SUCCESS;
          default:
            return INPUT;
        }
      } else {
        return INPUT;
      }

      // return (this.getCurrentUser() == null) ? INPUT : SUCCESS;
    }

  }

  public String login(User loggedUser, GlobalUnit loggedCrp) {
    return this.finishLogin(loggedUser, loggedCrp, ServletActionContext.getRequest().getHeader("Referer"));
  }

  /**
   * Establishes the session for an already-authenticated user and decides where to send them.
   * <p>
   * Extracted from {@link #login(User, GlobalUnit)} so that an authentication flow which does not carry a
   * {@code Referer} header can supply its own return URL (CHG-COGNITO-AUTH-001, DD-6). The only behavioral
   * change made during the extraction is the null guard on {@code returnUrl}.
   *
   * @param loggedUser the authenticated user
   * @param loggedCrp the Global Unit selected at login
   * @param returnUrl the URL to return to after login, or {@code null} when the caller has none
   * @return the Struts result name
   */
  protected String finishLogin(User loggedUser, GlobalUnit loggedCrp, String returnUrl) {

    // Validate if the user belongs to the selected crp
    if (loggedCrp != null) {
      if (crpUserManager.existCrpUser(loggedUser.getId(), loggedCrp.getId())) {

        this.getSession().put(APConstants.SESSION_USER, loggedUser);
        this.getSession().put(APConstants.SESSION_CRP, loggedCrp);
        // put the crp parameters in the session

        List<CustomParameter> customParameters =
          customParameterManager.getAllCustomParametersByGlobalUnitId(loggedCrp.getId());

        for (CustomParameter parameter : customParameters) {
          if (parameter.isActive()) {
            this.getSession().put(parameter.getParameter().getKey(), parameter.getValue());
          }
        }

        this.getSession().put(APConstants.CRP_VISIBLE_TOP_GULIST, new Boolean(this.isVisibleTopGUList()));
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


    loggedUser = userManager.getUser(loggedUser.getId());
    userManager.saveLastLogin(loggedUser);
    /*
     * Save the user url with trying to enter the system to redirect after
     * loged.
     */
    String urlAction = returnUrl;
    /*
     * take the ".do" pattern in the url to differentiate the main page.
     * also discard the "logout" url beacause this action close the user session.
     * The null check is the one behavioral change in this extraction: a request with no Referer header
     * (Referrer-Policy: no-referrer, curl, Postman, and every caller that has no header to send) used to
     * throw NullPointerException here.
     */
    if (urlAction != null && urlAction.contains(".do") && !urlAction.contains("logout")) {
      this.url = urlAction;
      return LOGIN;
    } else {
      switch (loggedCrp.getGlobalUnitType().getId().intValue()) {
        case 1:
          return SUCCESS;

        case 2:
          this.url = this.getBaseUrl() + "/" + loggedCrp.getAcronym() + "/centerDashboard.do";
          return LOGIN;

        case 3:
          return SUCCESS;

        case 4:
          return SUCCESS;

        case 5:
          return SUCCESS;

        default:
          return INPUT;

      }


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


  public void setGlobalUnit(Long globalUnit) {
    this.globalUnit = globalUnit;
  }

  @Override
  public void setUrl(String url) {
    this.url = url;
  }

  public void setUser(User user) {
    this.user = user;
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
