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
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.ADLoginMessages;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.CognitoAuthSpecificity;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.LogSanitizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class ValidateUserAction extends BaseAction {

  private static final long serialVersionUID = 8993663508312484245L;

  // CHG-COGNITO-AUTH-001-T14 (OPS-001): this action had no logger at all before -- every rejection here
  // (the invalid-method guard, the SEC-005 relay guard, and a failed LDAP/DB login) was silent.
  private static final Logger LOG = LoggerFactory.getLogger(ValidateUserAction.class);

  // Managers
  private UserManager userManager;
  /**
   * The Global Unit selected in wizard step 2, when the caller sends it. Optional today: {@code login.js}
   * does not yet post it (PS-20). Absent means the guard checks every membership, which is the fail-closed
   * direction — see {@link #isCgiarCredentialRelayBlocked()}.
   */
  private Long globalUnitId;
  private final GlobalUnitManager crpManager;
  private final CustomParameterManager customParameterManager;
  private final ParameterManager parameterManager;
  // Parameters
  private String userEmail;
  private String userPassword;
  private Boolean agree;


  private String messageEror;


  private Map<String, Object> userFound;


  // @Inject
  public ValidateUserAction(APConfig config, UserManager userManager, GlobalUnitManager crpManager,
    CustomParameterManager customParameterManager, ParameterManager parameterManager) {
    super(config);
    this.userManager = userManager;
    this.crpManager = crpManager;
    this.customParameterManager = customParameterManager;
    this.parameterManager = parameterManager;
  }


  @Override
  public String execute() throws Exception {
    userFound = new HashMap<String, Object>();

    // Reject non-POST requests to avoid exposing credentials in URL query strings.
    if (ServletActionContext.getRequest() == null
      || !"POST".equalsIgnoreCase(ServletActionContext.getRequest().getMethod())) {
      userFound.put("loginSuccess", false);
      messageEror = "Invalid request method";
      LOG.info("ValidateUserAction rejected: request method was not POST");
      return SUCCESS;
    }

    // CHG-COGNITO-AUTH-001-T11 (SEC-005): a CGIAR-migrated account must never have its password relayed
    // to Active Directory through this endpoint. The check runs, and can refuse, BEFORE userManager.login()
    // -- once that call is made the submitted password has already left MARLO for the realm's LDAP branch.
    if (this.isCgiarCredentialRelayBlocked()) {
      // CHG-COGNITO-AUTH-001-T14 (OPS-001, design.md 11 row 4: gate + user id + Global Unit -- audit
      // finding): the LOG line names the gate AND the Global Unit, resolved from this.globalUnitId, which
      // was already in hand and unused. The field error two lines below stays the byte-identical
      // wrong-password message -- no new oracle (SEC-005's BUT MUST NOT clause).
      GlobalUnit requestedGlobalUnit =
        this.globalUnitId == null ? null : this.crpManager.getGlobalUnitById(this.globalUnitId.longValue());
      LOG.info("User " + LogSanitizer.sanitizeForLog(this.userEmail)
        + " denied by the SEC-005 CGIAR relay guard (local login blocked for a CGIAR-migrated account; "
        + "Global Unit " + (requestedGlobalUnit == null ? "<none selected>" : requestedGlobalUnit.getAcronym())
        + ").");
      userFound.put("loginSuccess", false);
      messageEror = ADLoginMessages.ERROR_LOGON_FAILURE.getValue();
      return SUCCESS;
    }

    User user = userManager.login(userEmail, userPassword);
    this.getLoginMessages();
    if (this.getSession().containsKey(APConstants.LOGIN_MESSAGE)) {
      messageEror = this.getSession().get(APConstants.LOGIN_MESSAGE).toString();
    }

    if (user != null) {
      userFound.put("loginSuccess", true);
      if (user.getFirstName() != null && user.getLastName() != null) {
        userFound.put("userName", user.getFirstName() + " " + user.getLastName());
      }
    } else {
      userFound.put("loginSuccess", false);
      LOG.info("User " + LogSanitizer.sanitizeForLog(this.userEmail) + " tried to log-in but failed. Message : "
        + messageEror);
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

  /**
   * CHG-COGNITO-AUTH-001-T11: {@code true} when {@code userEmail} resolves to an {@code is_cgiar_user = 1}
   * account that belongs to at least one Global Unit with {@code cognito_auth_active} enabled.
   * <p>
   * <b>Which Global Unit, and why it is not simply "any of them".</b> MIG-001's <i>Both paths coexist</i>
   * scenario is explicit: for a CGIAR user belonging to a migrated unit <i>X</i> and a non-migrated unit
   * <i>Y</i>, <b>the path used MUST be determined by the unit selected in wizard step 2</b>. Refusing on
   * "any membership is migrated" would lock that user out of <i>Y</i>'s local login entirely — a regression
   * during exactly the staged rollout MIG-001 describes.
   * <p>
   * So when {@code globalUnitId} is supplied, <b>only that unit is checked</b>.
   * <p>
   * <b>When it is absent, the guard falls back to "any membership", which is deliberate.</b> Today
   * {@code checkPassword()} in {@code login.js} posts only the email, password and terms flag — the wizard
   * knows the selected unit by step 3 and simply does not send it. **T12 must add it** (recorded as PS-20).
   * Until then, a mixed-membership CGIAR user is refused on both units, which is a known and recorded gap —
   * but the fallback must stay fail-closed regardless of T12, because an attacker reaching this endpoint
   * directly, bypassing the wizard, would otherwise get a path around the guard by simply omitting the
   * parameter. **Never make the absent case permissive.**
   * <p>
   * Delegates every {@code cognito_auth_active} read to {@link CognitoAuthSpecificity} (PS-16) -- the same
   * shared resolver {@code CognitoLoginAction} and {@code CrpByUserEmailAction} call -- so this, the third
   * of design.md 9.2's three resolution points, cannot drift into a fourth independent reading.
   *
   * @return {@code true} only when the account is CGIAR-authenticated AND at least one of its Global Units
   *         has the flag active; {@code false} for every local ({@code is_cgiar_user = 0}) account and for
   *         an account this endpoint cannot resolve at all (SEC-005's {@code BUT MUST NOT} clause)
   */
  private boolean isCgiarCredentialRelayBlocked() {
    // Restores the pre-diff behaviour for a missing identifier. UserMySQLDAO.getUser(String) lowercases its
    // argument into a concatenated SQL string, so a null here threw out of execute() -- turning a malformed
    // unauthenticated POST into an HTTP 500 where it previously returned a clean {"loginSuccess":false}.
    if (this.userEmail == null || this.userEmail.trim().isEmpty()) {
      return false;
    }
    User user = this.userManager.getUserByEmail(this.userEmail);
    if (user == null) {
      user = this.userManager.getUserByUsername(this.userEmail);
    }
    if (user == null || !user.isCgiarUser()) {
      return false;
    }
    List<GlobalUnit> memberships = this.crpManager.crpUsers(this.userEmail);

    // MIG-001: when the caller names a unit it ACTUALLY BELONGS TO, that unit alone decides -- a user in a
    // migrated X and a non-migrated Y must still reach Y's local login.
    //
    // The membership check is the whole security of this branch. An earlier revision narrowed on the raw
    // parameter and fell out of the loop returning false when it matched nothing, so appending
    // `&globalUnitId=99999` switched the guard off completely and relayed the password to AD. A
    // caller-supplied id is a REQUEST, not an authority: it may only ever narrow a decision to a unit this
    // account holds, never escape one.
    if (this.globalUnitId != null) {
      for (GlobalUnit globalUnit : memberships) {
        if (this.globalUnitId.equals(globalUnit.getId())) {
          return CognitoAuthSpecificity.isActiveFor(globalUnit, this.customParameterManager, this.parameterManager);
        }
      }
      // Matched no membership: treat it as no selection at all and fall through to the fail-closed sweep.
    }

    for (GlobalUnit globalUnit : memberships) {
      if (CognitoAuthSpecificity.isActiveFor(globalUnit, this.customParameterManager, this.parameterManager)) {
        return true;
      }
    }
    return false;
  }

  public Long getGlobalUnitId() {
    return this.globalUnitId;
  }

  public void setGlobalUnitId(Long globalUnitId) {
    this.globalUnitId = globalUnitId;
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
