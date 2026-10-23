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


package org.cgiar.ccafs.marlo.security.otp;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.CognitoAuthSpecificity;
import org.cgiar.ccafs.marlo.security.otp.SignInMethodRule.Methods;
import org.cgiar.ccafs.marlo.utils.LogSanitizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The single eligibility predicate of the Center email one-time-code sign-in (CHG-OTP-LOGIN-001-T06, FN-001, FN-003,
 * FN-004, FN-005, FN-015, DD-4; amended by T21, DD-13, and by CHG-PWD-SWITCH-001-T03, FN-004 to FN-006, SEC-003,
 * SEC-004, OPS-002, DD-3, DD-4, DD-6; and T05, SEC-001, SEC-002, OPS-001, DD-5, for {@link #passwordGate}).
 * <p>
 * <b>Full predicate</b> ({@link #isEligible(String, long)}), used when a code is requested (design 5.1 step 6) and
 * again after it is consumed (5.2 step 8, FN-015). It is true only when <b>all</b> of these hold:
 * <ol>
 * <li>the feature is configured ({@link OtpKeys#isConfigured()});</li>
 * <li>the code is available to this email in this Global Unit by {@link SignInMethodRule}, from the three settings
 * {@code system_otp_login_active}, {@code system_otp_excluded_email_domains} and
 * {@code system_password_login_active}, read <b>uncached</b> in one query through
 * {@link OtpChallengeManager#specificityValuesFor} (DD-12). While the password switch of the unit is on this is T21's
 * rule: the code switch is on and the domain is not excluded. While it is off, an account without Cognito in the unit
 * gets the code whatever the code switch and the exclusion say (the default method, rule 3d);</li>
 * <li>the Global Unit exists;</li>
 * <li>the user exists, by the case-insensitive and trimmed lookup of {@link UserManager#getUserByEmail} (P-12);</li>
 * <li>the user is active;</li>
 * <li>the user is an <b>active</b> {@code crp_users} member of the Global Unit, through
 * {@link CrpUserManager#existActiveCrpUser(long, long)} (FN-005: a soft-deleted row, {@code is_active=0}, is not a
 * member). Password login ({@code LoginAction.finishLogin}) still uses the activity-blind {@code existCrpUser}; this
 * is a deliberate departure from that parity (requirements Decision Log, 2026-10-13);</li>
 * <li>the account is not a Cognito account in the unit, unless the unit offers it the code by its own settings (a
 * Cognito account keeps exactly today's methods, FN-005). This refusal is the last step, after the membership
 * check.</li>
 * </ol>
 * <b>Cognito is read only where the password is off (DD-4).</b> The Cognito flag of the unit is resolved through
 * {@link CognitoAuthSpecificity#isActiveFor}, the one resolver step 3 uses, and {@code users.is_cgiar_user} is read,
 * only while the password switch of the unit is off. On a password-on unit neither is touched, so the path is exactly
 * T21's. While the password is off the flag is resolved for <b>every</b> request that reaches the unit, before the
 * account lookup, and the refusal of a Cognito account comes after {@code existActiveCrpUser}: a known account, an
 * unknown one and a Cognito one pay the same Cognito reads and the Cognito account the same queries as a member
 * (SEC-004). The checks run cheapest and least revealing first and stop at the first refusal, but the answer is a bare
 * boolean: the caller can never tell which fact refused.
 * <p>
 * <b>Disclosure-safe variants.</b> {@link #isOffered(String, long)} (request step 4) and
 * {@link #otpEnabledFor(String, Collection)} ({@code crpByEmail.do}) never read the user's active state or
 * membership, so the response discloses nothing beyond today's {@code isCgiarUser} flag and unit list (JI-11).
 * {@code isOffered} is account-blind: it assumes an account without Cognito, so the override is admitted and the
 * Cognito refusal is left to {@code isEligible}. {@link #methodsFor(String, Collection, Map)} answers, per unit, the
 * methods the pre-auth lookup reports. {@link #passwordGate(String, Long)} is the gate of the two password endpoints:
 * it reads the password switch of the account's units and nothing about Cognito.
 * <p>
 * <b>Call it only from an action's {@code execute()}.</b> The user, membership and specificity reads go through
 * transactional managers, and a transactional call made from {@code validate()} commits whatever {@code prepare()}
 * left half-edited. A store failure surfaces as the manager's runtime exception; the caller turns it into its own
 * {@code unavailable} or {@code internal_error} answer. The class holds no session and no per-request state; its one
 * piece of memory is the set of units whose last-resort ERROR was written (OPS-002).
 * <p>
 * The email is normalised here, and only after {@link OtpDomainPolicy#domainOf(String)} has accepted it, because
 * {@link OtpDomainPolicy#normaliseEmail(String)} answers the empty string for a rejected (non-ASCII) address, which
 * must never be looked up.
 * <p>
 * It is a container singleton: {@code @Named}, found by the component scan and created once from its managers, so
 * every action shares one instance. Do not construct it in an action: inject it.
 */
@Named
public final class OtpEligibility {

  /**
   * Why an account may be sent a code (CHG-PWD-SWITCH-001 design 11). The decision is the rule's, asked twice: as the
   * unit's password switch says, and as if it were on.
   */
  public enum Basis {

    /** The account is not eligible. */
    NONE,

    /** Eligible by the unit's own settings: the code switch is on and the domain is not excluded. */
    UNIT_SETTINGS,

    /** Eligible only because the unit's password switch is off: the code would not be offered with it on. */
    PASSWORD_SWITCH_OFF
  }

  /**
   * What the password gate decided for one attempt (CHG-PWD-SWITCH-001-T05, SEC-002). A refusal carries what the
   * credential check would have left behind for this kind of account, so the endpoint can answer exactly as it answers
   * a real wrong password and the answer reveals nothing about the account.
   */
  public static final class PasswordGateDecision {

    private static final PasswordGateDecision ALLOWED = new PasswordGateDecision(false, null);

    private final boolean refused;
    private final String realmMessage;

    private PasswordGateDecision(boolean refused, String realmMessage) {
      this.refused = refused;
      this.realmMessage = realmMessage;
    }

    /**
     * @return {@code true} when the password must not reach the credential check
     */
    public boolean isRefused() {
      return this.refused;
    }

    /**
     * Leaves in the Shiro session what {@code APCustomRealm} leaves there when the credential check fails for this
     * kind of account (SEC-002): {@link APConstants#USER_DISABLED} for an inactive account, which the realm answers
     * before it compares any password; nothing for an active local account, because {@code DBAuthenticator} leaves no
     * message (the realm sets the attribute to {@code null}, which removes it, and so does this); and
     * {@link APConstants#ERROR_LOGON_FAILURE} for an active CGIAR account. That last one is a <b>choice</b>, not a
     * reproduction: a real CGIAR failure leaves whatever the directory answers (the message comes from the directory
     * library, which this code does not call), and {@code ERROR_LOGON_FAILURE} is the code {@code LDAPAuthenticator}
     * itself uses when the directory returns no connection. A CGIAR account that a directory search or bind would
     * reject before it checks the password (no directory entry, a locked or expired account, an outage) therefore gets
     * a different answer from a real failure. That residual is accepted (requirements Decision Log, 2026-10-16,
     * "Execute-time decisions on T05"): reproducing it takes a directory call before the refusal, which SEC-001
     * forbids. The endpoint then reads the session as it does after a failed {@code userManager.login}.
     *
     * @param shiroSession the session {@code getLoginMessages()} reads
     */
    public void leaveRealmMessage(Session shiroSession) {
      shiroSession.setAttribute(APConstants.LOGIN_MESSAGE, this.realmMessage);
    }

    /**
     * Runs Shiro's failed-login handling for remember-me, which {@code Subject.login} runs on every failure and a
     * refusal never reaches (SEC-002). {@code DefaultSecurityManager.login} calls the remember-me manager's
     * {@code onFailedLogin}; the cookie manager that {@code DefaultWebSecurityManager} installs answers it with
     * {@code Set-Cookie: rememberMe=deleteMe}, for a wrong password and for an unknown account alike. Without this call
     * a refusal would be the one failure that does not send it.
     * <p>
     * No credential is checked: {@code onFailedLogin} only forgets the remembered identity. The token is built as
     * {@code UserManagerImp.login} builds it, and the exception is the one the realm throws for every failed
     * password. Nothing here can change the answer: a security manager that is not a {@code DefaultSecurityManager},
     * one without a remember-me manager and a subject that is not a web subject (the cookie manager then has no
     * response to write to) are all no-ops, and a failure of the remember-me manager is logged, as Shiro logs it, and
     * contained.
     *
     * @param subject the subject the endpoint is serving, as {@code SecurityUtils.getSubject()} returns it
     * @param securityManager the configured security manager, as {@code SecurityUtils.getSecurityManager()} returns it
     * @param username the identifier as typed, which {@code userManager.login} would have put in the token
     * @param password the password as typed, never read here
     */
    public void leaveRememberMeFailure(Subject subject, org.apache.shiro.mgt.SecurityManager securityManager,
      String username, String password) {
      if (!(securityManager instanceof DefaultSecurityManager)) {
        return;
      }
      RememberMeManager rememberMe = ((DefaultSecurityManager) securityManager).getRememberMeManager();
      if (rememberMe == null) {
        return;
      }
      UsernamePasswordToken token = new UsernamePasswordToken(username, password);
      try {
        rememberMe.onFailedLogin(subject, token, new IncorrectCredentialsException());
      } catch (RuntimeException e) {
        // Shiro's own DefaultSecurityManager logs and contains the same failure; the token is not logged (it names
        // the account).
        LOG.warn("auth.password.gate outcome=remember_me_unavailable: remember-me handling threw on a refusal", e);
      } finally {
        token.clear();
      }
    }
  }

  /** The password switch as the rule reads it when it is on: what the code switch and the exclusion offer alone. */
  private static final String PASSWORD_SWITCH_ON = "true";

  private static final Logger LOG = LoggerFactory.getLogger(OtpEligibility.class);

  /** The three specificities of FN-001 and FN-003 and of the password switch, read together in one query (DD-3). */
  private static final List<String> SPECIFICITY_KEYS = Collections.unmodifiableList(
    Arrays.asList(APConstants.OTP_LOGIN_ACTIVE, APConstants.OTP_EXCLUDED_EMAIL_DOMAINS,
      APConstants.PASSWORD_LOGIN_ACTIVE));

  private final OtpKeys keys;
  private final UserManager userManager;
  private final GlobalUnitManager globalUnitManager;
  private final CrpUserManager crpUserManager;
  private final OtpChallengeManager otpChallengeManager;
  private final CustomParameterManager customParameterManager;
  private final ParameterManager parameterManager;

  /**
   * OPS-002: the ids of the Global Units whose last-resort ERROR was already written since the application started.
   * The ids come from the units the pre-auth lookup lists, never from the request, so the set is bounded by the number
   * of Global Units and an unauthenticated caller cannot make the line repeat.
   */
  private final Set<Long> lastResortLogged = ConcurrentHashMap.newKeySet();

  @Inject
  public OtpEligibility(OtpKeys keys, UserManager userManager, GlobalUnitManager globalUnitManager,
    CrpUserManager crpUserManager, OtpChallengeManager otpChallengeManager,
    CustomParameterManager customParameterManager, ParameterManager parameterManager) {
    this.keys = keys;
    this.userManager = userManager;
    this.globalUnitManager = globalUnitManager;
    this.crpUserManager = crpUserManager;
    this.otpChallengeManager = otpChallengeManager;
    this.customParameterManager = customParameterManager;
    this.parameterManager = parameterManager;
  }

  /**
   * The units that decide the gate: the unit of the attempt when the account is a member of it, otherwise every
   * membership (the sweep). The attempt's unit is a request, not an authority: only a membership can narrow.
   */
  private static List<GlobalUnit> decidingUnits(List<GlobalUnit> memberships, Long selectedUnitId) {
    if (selectedUnitId != null) {
      for (GlobalUnit membership : memberships) {
        if (selectedUnitId.equals(membership.getId())) {
          return Collections.singletonList(membership);
        }
      }
    }
    return memberships;
  }

  private static String valueOf(Map<String, String> values, String key) {
    return values == null ? null : values.get(key);
  }

  /**
   * Design 5.1 step 4 on its own, account-blind: the code is available to an account without Cognito in this unit,
   * that is, the code switch is on and the domain is not excluded, or the password switch of the unit is off (the
   * default method). The secret is checked here only because {@link SignInMethodRule} needs it (the request action
   * answers an unconfigured secret before it gets here), and nothing about the account is read: neither the user, nor
   * its membership, nor the Cognito flag.
   *
   * @param email the submitted email, raw; possibly {@code null}
   * @param globalUnitId the selected Global Unit. One that does not exist has no switch, so its password reads as on
   *        and its code as off
   * @return {@code true} only when the code is offered to this email on this Global Unit
   */
  public boolean isOffered(String email, long globalUnitId) {
    if (OtpDomainPolicy.domainOf(email) == null) {
      return false;
    }
    Long unitId = Long.valueOf(globalUnitId);
    return this.isCodeAvailable(email, this.specificities(Collections.singletonList(unitId)).get(unitId), false);
  }

  /**
   * The full predicate (design 5.1 step 6 and 5.2 step 8).
   *
   * @param email the submitted email, raw; possibly {@code null}
   * @param globalUnitId the selected Global Unit
   * @return {@code true} only when every condition in the class description holds
   */
  public boolean isEligible(String email, long globalUnitId) {
    return this.eligibilityBasis(email, globalUnitId) != Basis.NONE;
  }

  /**
   * The full predicate of {@link #isEligible(String, long)}, with the reason it holds. It makes exactly the reads
   * {@code isEligible} makes, in the same order: the reason is computed from the settings already read, so a caller
   * that logs it adds no query and no timing difference between accounts.
   *
   * @param email the submitted email, raw; possibly {@code null}
   * @param globalUnitId the selected Global Unit
   * @return {@link Basis#NONE} when the account is not eligible. Otherwise {@link Basis#PASSWORD_SWITCH_OFF} when the
   *         code is available only because the unit's password switch is off, and {@link Basis#UNIT_SETTINGS} when
   *         the unit's code switch and exclusion offer it on their own
   */
  public Basis eligibilityBasis(String email, long globalUnitId) {
    if (!this.keys.isConfigured() || OtpDomainPolicy.domainOf(email) == null) {
      return Basis.NONE;
    }
    Long unitId = Long.valueOf(globalUnitId);
    Map<String, String> values = this.specificities(Collections.singletonList(unitId)).get(unitId);
    if (!this.isCodeAvailable(email, values, false)) {
      return Basis.NONE;
    }
    GlobalUnit globalUnit = this.globalUnitManager.getGlobalUnitById(globalUnitId);
    if (globalUnit == null) {
      return Basis.NONE;
    }
    // DD-4: on a password-off unit the Cognito flag is resolved for every request, known account or not, before the
    // account is looked up. On a password-on unit it is never read.
    boolean passwordOff = !SignInMethodRule.isPasswordSwitchOn(valueOf(values, APConstants.PASSWORD_LOGIN_ACTIVE));
    boolean cognitoActive = passwordOff
      && CognitoAuthSpecificity.isActiveFor(globalUnit, this.customParameterManager, this.parameterManager);
    User user = this.userManager.getUserByEmail(OtpDomainPolicy.normaliseEmail(email));
    if (user == null || !user.isActive() || user.getId() == null) {
      return Basis.NONE;
    }
    if (!this.crpUserManager.existActiveCrpUser(user.getId().longValue(), globalUnitId)) {
      return Basis.NONE;
    }
    // After the membership check, so every account that reaches it pays the same queries (SEC-004). A Cognito
    // account keeps today's methods: it gets the code only when the unit offers it by its own settings (FN-005).
    boolean cognitoAccount = cognitoActive && user.isCgiarUser();
    if (!this.isCodeAvailable(email, values, cognitoAccount)) {
      return Basis.NONE;
    }
    // The same rule as if the password switch were on: what the code switch and the exclusion offer on their own.
    return this.isCodeAvailable(email, values, cognitoAccount, PASSWORD_SWITCH_ON) ? Basis.UNIT_SETTINGS
      : Basis.PASSWORD_SWITCH_OFF;
  }

  /**
   * The methods of every listed unit for the pre-auth lookup ({@code crpByEmail.do}, DD-6): per unit,
   * {@link Methods#isCode()} is {@code otpEnabled} and {@link Methods#isPassword()} is {@code passwordEnabled}, both
   * from {@link SignInMethodRule}. It reads no account state: the caller says which units the account is a Cognito
   * account in.
   * <p>
   * The one uncached query <b>always runs</b> when at least one unit is listed, also when the code key is missing
   * and when the identifier has no domain, because {@code passwordEnabled} needs the password switch. Where the key
   * is missing and the password switch of a unit is off, the password is the last resort, and the ERROR of OPS-002 is
   * written here, once per unit per application start.
   * <p>
   * A store failure surfaces as the manager's runtime exception; the caller maps it to its own fail-open answer.
   *
   * @param identifier what the user typed in step 1, raw; an email or a username, possibly {@code null}
   * @param units the Global Units to answer for; {@code null} entries and entries without an id are skipped
   * @param cognitoAccountByUnit by unit id, whether the account is a Cognito account in that unit; {@code null} or a
   *        missing entry reads as {@code false}
   * @return one entry per listed unit id, in the order given. Empty, with no query, when no unit is listed
   */
  public Map<Long, Methods> methodsFor(String identifier, Collection<GlobalUnit> units,
    Map<Long, Boolean> cognitoAccountByUnit) {
    Map<Long, GlobalUnit> listed = new LinkedHashMap<Long, GlobalUnit>();
    if (units != null) {
      for (GlobalUnit unit : units) {
        if (unit != null && unit.getId() != null && !listed.containsKey(unit.getId())) {
          listed.put(unit.getId(), unit);
        }
      }
    }
    Map<Long, Methods> result = new LinkedHashMap<Long, Methods>();
    if (listed.isEmpty()) {
      return result;
    }
    boolean keyConfigured = this.keys.isConfigured();
    Map<Long, Map<String, String>> specificities = this.specificities(new ArrayList<Long>(listed.keySet()));
    for (Map.Entry<Long, GlobalUnit> entry : listed.entrySet()) {
      Map<String, String> values = specificities.get(entry.getKey());
      boolean cognitoAccount =
        cognitoAccountByUnit != null && Boolean.TRUE.equals(cognitoAccountByUnit.get(entry.getKey()));
      String passwordValue = valueOf(values, APConstants.PASSWORD_LOGIN_ACTIVE);
      Methods methods = SignInMethodRule.methodsFor(keyConfigured, cognitoAccount, passwordValue,
        valueOf(values, APConstants.OTP_LOGIN_ACTIVE), valueOf(values, APConstants.OTP_EXCLUDED_EMAIL_DOMAINS),
        identifier);
      result.put(entry.getKey(), methods);
      if (methods.isPassword() && !keyConfigured && !SignInMethodRule.isPasswordSwitchOn(passwordValue)) {
        this.logLastResortOnce(entry.getValue());
      }
    }
    return result;
  }

  /**
   * The password gate of the two login-page endpoints, {@code validateUser.do} and {@code login.do}
   * (CHG-PWD-SWITCH-001-T05, DD-5; SEC-001, SEC-002, OPS-001). The endpoint asks it <b>before</b> the credential check:
   * once {@code userManager.login} runs, the password has been compared or has already left MARLO for the realm's LDAP
   * branch. When the decision is a refusal the endpoint skips the credential check and answers as it answers a failed
   * one, after {@link PasswordGateDecision#leaveRealmMessage(Session)} has left what the realm would have left.
   * <p>
   * The password is refused when the code key is configured and the password switch is off for the Global Unit of the
   * attempt. The decision is the rule's ({@link SignInMethodRule}, an account without Cognito); the Cognito accounts
   * are refused earlier by the SEC-005 guard of each endpoint, which this does not replace.
   * <ul>
   * <li><b>The account</b> is resolved as the credential check resolves it: by email when the identifier holds an
   * {@code @}, otherwise by username. Its memberships are then read by its <b>stored</b> email, never by what was
   * typed: the realm trims and lower-cases what it is given, the membership read compares it as it is, and a
   * membership read made with padding or case would find none and let the password through.</li>
   * <li><b>The unit of the attempt</b> counts only when the account is a member of it. When it is missing, unknown or
   * not a membership, every membership is swept and the first one with the password off refuses. A caller-supplied unit
   * may narrow the decision to a unit the account holds; it may never escape one (the defect the SEC-005 guard already
   * fixed, Judgment Day C-2). The settings read lists only the account's own units.</li>
   * <li><b>An unknown account</b> has no membership: the gate does not refuse and the credential check fails as it
   * does today.</li>
   * <li><b>Without a valid code key</b> the password is the last resort: the gate never refuses, reads nothing and
   * writes nothing. The ERROR that tells the administrator is decided in {@code crpByEmail.do}
   * ({@link #methodsFor(String, Collection, Map)}, OPS-002).</li>
   * <li><b>A failing read</b> (account, memberships or settings) fails <b>open</b>: the gate allows the password and
   * writes one ERROR line. This is the deliberate exception to SEC-001 of the requirements Decision Log, unlike the
   * fail-closed sweep of the SEC-005 guard: a database fault already fails the login itself, and the switch must not
   * turn it into a lock-out.</li>
   * </ul>
   * <b>What a refusal leaves behind (SEC-002).</b> The endpoints follow the road of a failed login, so the decision
   * leaves what a failed {@code Subject.login} leaves: the realm's message for the kind of account
   * ({@link PasswordGateDecision#leaveRealmMessage(Session)}, which says what is reproduced and what is chosen) and
   * Shiro's remember-me failure handling ({@link PasswordGateDecision#leaveRememberMeFailure}). A CGIAR account that
   * the directory would reject before its password is checked is the accepted residual of the requirements Decision
   * Log, 2026-10-16, "Execute-time decisions on T05".
   * <p>
   * A refusal writes one INFO line, {@code auth.password.refused gate=password_switch gu=<acronym>}, with the acronym
   * as stored and sanitised, as the last-resort line writes it. It carries neither the identifier nor the password.
   * <p>
   * Call it only from the action method that handles the request, never from {@code validate()}: the three reads go
   * through managers that may be transactional.
   *
   * @param identifier what the user typed as the account, an email or a username, raw; possibly {@code null}
   * @param selectedUnitId the id of the Global Unit the attempt names; {@code null} when it names none or none resolved
   * @return the decision. Never {@code null}
   */
  public PasswordGateDecision passwordGate(String identifier, Long selectedUnitId) {
    if (!this.keys.isConfigured() || identifier == null || identifier.trim().isEmpty()) {
      return PasswordGateDecision.ALLOWED;
    }
    try {
      User account = identifier.contains("@") ? this.userManager.getUserByEmail(identifier)
        : this.userManager.getUserByUsername(identifier);
      if (account == null || account.getEmail() == null) {
        return PasswordGateDecision.ALLOWED;
      }
      List<GlobalUnit> deciding = decidingUnits(this.globalUnitManager.crpUsers(account.getEmail()), selectedUnitId);
      if (deciding.isEmpty()) {
        return PasswordGateDecision.ALLOWED;
      }
      List<Long> unitIds = new ArrayList<Long>();
      for (GlobalUnit unit : deciding) {
        unitIds.add(unit.getId());
      }
      Map<Long, Map<String, String>> specificities = this.specificities(unitIds);
      for (GlobalUnit unit : deciding) {
        if (!this.isPasswordAllowed(specificities.get(unit.getId()))) {
          PasswordGateDecision refusal = new PasswordGateDecision(true, realmMessageFor(account));
          String acronym = LogSanitizer.sanitizeForLog(unit.getAcronym());
          LOG.info("auth.password.refused gate=password_switch gu={}", acronym);
          return refusal;
        }
      }
      return PasswordGateDecision.ALLOWED;
    } catch (RuntimeException e) {
      LOG.error("auth.password.gate outcome=settings_unavailable fail_open=true", e);
      return PasswordGateDecision.ALLOWED;
    }
  }

  /**
   * The variant for {@code crpByEmail.do}: configured, the unit's switch on and the email's domain not excluded. It
   * reads no account state. It does not know the password switch; {@link #methodsFor(String, Collection, Map)}
   * replaces it for the pre-auth lookup (CHG-PWD-SWITCH-001-T04).
   *
   * @param email the submitted email, raw; possibly {@code null}
   * @param units the Global Units to answer for; {@code null} entries are skipped
   * @return one entry per listed unit id, in the order given. The switch and the exclusion of all the units come
   *         from one uncached query, made only when the feature is configured and the email has a domain
   */
  public Map<Long, Boolean> otpEnabledFor(String email, Collection<GlobalUnit> units) {
    Map<Long, Boolean> result = new LinkedHashMap<Long, Boolean>();
    List<Long> unitIds = new ArrayList<Long>();
    if (units != null) {
      for (GlobalUnit unit : units) {
        if (unit != null && unit.getId() != null) {
          unitIds.add(unit.getId());
          result.put(unit.getId(), Boolean.FALSE);
        }
      }
    }
    if (unitIds.isEmpty() || !this.keys.isConfigured() || OtpDomainPolicy.domainOf(email) == null) {
      return result;
    }
    Map<Long, Map<String, String>> specificities = this.specificities(unitIds);
    for (Long unitId : unitIds) {
      if (this.isOfferedByTheUnit(email, specificities.get(unitId))) {
        result.put(unitId, Boolean.TRUE);
      }
    }
    return result;
  }

  /** The rule of the class description for one account: whether the code is among its methods. */
  private boolean isCodeAvailable(String email, Map<String, String> values, boolean cognitoAccountInG) {
    return this.isCodeAvailable(email, values, cognitoAccountInG, valueOf(values, APConstants.PASSWORD_LOGIN_ACTIVE));
  }

  private boolean isCodeAvailable(String email, Map<String, String> values, boolean cognitoAccountInG,
    String passwordSwitchValue) {
    return SignInMethodRule.methodsFor(this.keys.isConfigured(), cognitoAccountInG, passwordSwitchValue,
      valueOf(values, APConstants.OTP_LOGIN_ACTIVE), valueOf(values, APConstants.OTP_EXCLUDED_EMAIL_DOMAINS),
      email).isCode();
  }

  /**
   * The message a refusal leaves for this account (SEC-002), following the branches of
   * {@code APCustomRealm.doGetAuthenticationInfo} in its order:
   * <ul>
   * <li>an inactive account: {@link APConstants#USER_DISABLED}, which the realm sets before any password is
   * compared;</li>
   * <li>an active CGIAR account: {@link APConstants#ERROR_LOGON_FAILURE}. The realm would leave what the directory
   * answers, which this code cannot know without a directory call; this is the accepted residual described at
   * {@link PasswordGateDecision#leaveRealmMessage(Session)};</li>
   * <li>an active local account: {@code null}, because {@code DBAuthenticator} leaves no message.</li>
   * </ul>
   */
  private static String realmMessageFor(User account) {
    if (!account.isActive()) {
      return APConstants.USER_DISABLED;
    }
    return account.isCgiarUser() ? APConstants.ERROR_LOGON_FAILURE : null;
  }

  /**
   * The rule's answer on the password for an account without Cognito, from the unit's settings (the key is
   * configured, or the gate would not be here). The code switch and the exclusion do not change it.
   */
  private boolean isPasswordAllowed(Map<String, String> values) {
    String passwordSwitchValue = valueOf(values, APConstants.PASSWORD_LOGIN_ACTIVE);
    return SignInMethodRule.methodsFor(this.keys.isConfigured(), false, passwordSwitchValue, null, null, null)
      .isPassword();
  }

  /** T21's own rule, without the override: the code switch is on and the domain is not excluded. */
  private boolean isOfferedByTheUnit(String email, Map<String, String> values) {
    return values != null && OtpDomainPolicy.isOffered(values.get(APConstants.OTP_LOGIN_ACTIVE),
      values.get(APConstants.OTP_EXCLUDED_EMAIL_DOMAINS), email);
  }

  /**
   * OPS-002: the password is offered in this unit only because the code key is missing. The line names the unit and
   * nothing else, and is written once per unit per start.
   */
  private void logLastResortOnce(GlobalUnit unit) {
    if (this.lastResortLogged.add(unit.getId())) {
      LOG.error("auth.password.last_resort gu={} reason=code_key_unconfigured",
        LogSanitizer.sanitizeForLog(unit.getAcronym()));
    }
  }

  private Map<Long, Map<String, String>> specificities(Collection<Long> unitIds) {
    Map<Long, Map<String, String>> values = this.otpChallengeManager.specificityValuesFor(SPECIFICITY_KEYS, unitIds);
    return values == null ? Collections.<Long, Map<String, String>>emptyMap() : values;
  }
}
