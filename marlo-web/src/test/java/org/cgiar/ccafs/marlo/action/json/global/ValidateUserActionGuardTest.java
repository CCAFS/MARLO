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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.OtpChallengeManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.RealCredentialPath;
import org.cgiar.ccafs.marlo.security.otp.OtpEligibility;
import org.cgiar.ccafs.marlo.security.otp.OtpKeys;
import org.cgiar.ccafs.marlo.security.otp.UnconfiguredOtpEligibility;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.opensymphony.xwork2.ActionContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.apache.struts2.ServletActionContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Covers CHG-COGNITO-AUTH-001-T11: the guard {@code ValidateUserAction} must apply before it ever calls
 * {@code userManager.login()} -- design.md 5.3, SEC-005's new scenario.
 * <p>
 * <b>Why test 1 asserts on the collaborator, not the HTTP response.</b> T11's own <i>Not evidence when</i>
 * clause is explicit: a response can be shaped correctly while the LDAP bind already happened, because by
 * then the submitted password has already left MARLO. {@link #aMigratedCgiarAccountIsRefusedAndLoginIsNever
 * Called()} therefore uses an {@link UserManager} double whose {@code login(String, String)}
 * <b>throws</b> {@code AssertionError} unconditionally -- the same "exploding double" idiom
 * {@code APCustomRealmDispatchTest} uses to prove a collaborator was never reached. If the guard ever fell
 * through to {@code userManager.login(...)}, this test fails loudly instead of passing on a merely
 * correctly-shaped JSON body.
 * <p>
 * Every collaborator is a hand-rolled double: MARLO has no mocking framework ({@code DEC-005} is
 * {@code PENDING}), matching {@code CrpByUserEmailActionTest} and {@code CognitoLoginActionTest} -- both of
 * which this suite mirrors closely, since {@code ValidateUserAction} now shares {@code CognitoAuthSpecificity}
 * (PS-16) with {@code CrpByUserEmailAction} and {@code CognitoLoginAction}.
 * <p>
 * <b>What this suite does not cover</b> (recorded here rather than only in the implementer's report): the
 * Struts interceptor stack that routes a real request to {@link ValidateUserAction#execute()}, and the
 * {@code validateUser.do} JSON result's actual serialization. Tests drive {@code execute()} directly against
 * a hand-bound {@link ActionContext} and a proxied POST {@link HttpServletRequest} -- the layer available
 * without a servlet container.
 * <p>
 * <b>CHG-PWD-SWITCH-001-T05: the password gate (SEC-001, SEC-002, OPS-001, DD-5).</b> The tests whose name starts with
 * {@code theGate} or {@code thePasswordSwitch} drive the real {@link OtpEligibility} over doubles: only the managers
 * are fakes, so the real gate, the real rule and the real narrowing run. The doubles answer from data keyed by the
 * arguments they receive and record those arguments, and the "no credential call" proof is a recorded list of
 * {@code login} calls with their arguments, asserted empty on a refusal and equal to the one submitted pair otherwise.
 * Every other test in this class runs on {@link UnconfiguredOtpEligibility}: the code key is missing, the gate is
 * inert and any read it made would fail the test.
 */
public class ValidateUserActionGuardTest {

  private static final String CGIAR_EMAIL = "priya.cgiar@cgiar.org";
  private static final String LOCAL_EMAIL = "jane.local@cgiar.org";
  private static final String SUBMITTED_PASSWORD = "whatever-password";

  /** base64 of the 32 bytes 0x00..0x1f: a valid one-time-code key, so the gate is live. */
  private static final String VALID_SECRET = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";

  /** The key list of the one settings query, as the spec names the three keys. */
  private static final String ALL_KEYS =
    "[system_otp_login_active, system_otp_excluded_email_domains, system_password_login_active]";

  private static final String REFUSED_LINE = "auth.password.refused ";

  private ch.qos.logback.classic.Logger capturedLogger;
  private Level previousLevel;
  private ListAppender<ILoggingEvent> appender;

  /**
   * Global Unit ids and Global Unit TYPE ids are deliberately disjoint number ranges, matching
   * {@code CrpByUserEmailActionTest}'s convention: if they overlapped, passing the wrong one to the catalog
   * lookup would resolve anyway and a mistake would never surface.
   */
  private static final long EXPECTED_TYPE_ID = 91L;

  private static GlobalUnit globalUnit(long id, String acronym) {
    GlobalUnitType type = new GlobalUnitType();
    type.setId(Long.valueOf(EXPECTED_TYPE_ID));
    GlobalUnit unit = new GlobalUnit();
    unit.setId(Long.valueOf(id));
    unit.setAcronym(acronym);
    unit.setGlobalUnitType(type);
    return unit;
  }

  private static User cgiarUser() {
    User user = new User();
    user.setId(Long.valueOf(9001L));
    user.setEmail(CGIAR_EMAIL);
    user.setCgiarUser(true);
    user.setActive(true);
    return user;
  }

  private static User localUser() {
    User user = new User();
    user.setId(Long.valueOf(9002L));
    user.setEmail(LOCAL_EMAIL);
    user.setCgiarUser(false);
    user.setActive(true);
    return user;
  }

  private static Parameter catalogRow(String defaultValue) {
    Parameter parameter = new Parameter();
    parameter.setKey(APConstants.COGNITO_AUTH_ACTIVE);
    parameter.setDefaultValue(defaultValue);
    return parameter;
  }

  /**
   * The real {@code UserMySQLDAO.getUser(String)} does {@code email.toLowerCase()} straight into a
   * concatenated SQL string, so a null identifier throws there. Doubles must reproduce that, or the
   * production null-check becomes untestable -- a test would pass with the check deleted.
   */
  private static void requireResolvableIdentifier(String identifier) {
    if (identifier == null) {
      throw new NullPointerException("UserMySQLDAO.getUser(String) lowercases its argument");
    }
  }

  private static CustomParameter activeOverride(String value) {
    CustomParameter override = new CustomParameter();
    override.setValue(value);
    override.setActive(true);
    return override;
  }

  @Before
  public void bindSecurityManagerAndRequest() {
    // The non-guard path (userManager.login() -> getLoginMessages()) reads
    // SecurityUtils.getSubject().getSession(); a plain non-web manager gives it a real, in-memory Shiro
    // session with no servlet container involved -- same setup as CognitoLoginActionTest.
    SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    bindPostRequest();
  }

  @Before
  public void captureLogs() {
    this.capturedLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.cgiar.ccafs.marlo");
    this.previousLevel = this.capturedLogger.getLevel();
    this.capturedLogger.setLevel(Level.TRACE);
    this.appender = new ListAppender<ILoggingEvent>();
    this.appender.start();
    this.capturedLogger.addAppender(this.appender);
  }

  @After
  public void releaseLogs() {
    this.capturedLogger.detachAppender(this.appender);
    this.capturedLogger.setLevel(this.previousLevel);
  }

  @After
  public void unbindSecurityManagerAndRequest() {
    SecurityUtils.setSecurityManager(null);
    // Not merely tidiness -- see APCustomRealmDispatchTest's identical note: JUnit reuses one thread, and a
    // Subject bound to ThreadContext by this class would otherwise leak into a later test.
    ThreadContext.remove();
    ActionContext.clear();
  }

  /**
   * Installs a POST {@link HttpServletRequest} -- the existing method guard this task must preserve -- via
   * a {@link Proxy}, the same technique {@code LoginActionFinishLoginTest} uses for the same reason (no
   * servlet container available to a plain unit test).
   */
  private static void bindPostRequest() {
    HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
      ValidateUserActionGuardTest.class.getClassLoader(), new Class<?>[] {HttpServletRequest.class},
      new InvocationHandler() {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
          if ("getMethod".equals(method.getName())) {
            return "POST";
          }
          Class<?> returnType = method.getReturnType();
          if (returnType == boolean.class) {
            return Boolean.FALSE;
          }
          if (returnType == int.class) {
            return Integer.valueOf(0);
          }
          if (returnType == long.class) {
            return Long.valueOf(0L);
          }
          return null;
        }
      });
    // Struts 6.8's ServletActionContext.setRequest delegates to ActionContext.getContext(), which is null
    // outside a live request -- same workaround as LoginActionFinishLoginTest.
    ActionContext.of(new HashMap<String, Object>()).bind();
    ServletActionContext.setRequest(request);
  }

  private static ValidateUserAction newAction(UserManager userManager, GlobalUnitManager crpManager,
    CustomParameterManager customParameterManager, ParameterManager parameterManager) {
    return newAction(userManager, crpManager, customParameterManager, parameterManager,
      UnconfiguredOtpEligibility.create());
  }

  private static ValidateUserAction newAction(UserManager userManager, GlobalUnitManager crpManager,
    CustomParameterManager customParameterManager, ParameterManager parameterManager, OtpEligibility eligibility) {
    ValidateUserAction action = new ValidateUserAction(new APConfig(), userManager, crpManager,
      customParameterManager, parameterManager, eligibility);
    action.setSession(new HashMap<String, Object>());
    return action;
  }

  private static void execute(ValidateUserAction action) {
    try {
      action.execute();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * T11 test 1 -- the task's sharpest clause. {@code is_cgiar_user = 1} and the account's Global Unit has
   * {@code cognito_auth_active} on: refused, and {@link ExplodingLoginUserManager#login(String, String)}
   * would throw if it were ever reached. Reaching the assertions below without a thrown
   * {@code AssertionError} <b>is</b> the proof the LDAP bind never happened.
   */
  @Test
  public void aMigratedCgiarAccountIsRefusedAndLoginIsNeverCalled() {
    ExplodingLoginUserManager userManager = new ExplodingLoginUserManager();
    userManager.register(cgiarUser());
    StubGlobalUnitManager crpManager = new StubGlobalUnitManager();
    crpManager.units = Arrays.asList(globalUnit(1L, "AAA"));
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogDefault = "false";

    ValidateUserAction action = newAction(userManager, crpManager, customParameterManager, parameterManager);
    action.setUserEmail(CGIAR_EMAIL);
    action.setUserPassword(SUBMITTED_PASSWORD);
    action.setAgree(Boolean.TRUE);

    execute(action);

    assertEquals(Boolean.FALSE, action.getUserFound().get("loginSuccess"));
    assertFalse("a refusal must not disclose a display name", action.getUserFound().containsKey("userName"));
  }

  /**
   * <b>The bypass an independent audit found in the Leader's own correction.</b>
   * <p>
   * The optional {@code globalUnitId} was introduced to satisfy MIG-001, and its first implementation
   * narrowed on the raw parameter: the loop {@code continue}d past every non-matching membership and fell out
   * returning {@code false}. So a {@code globalUnitId} matching <b>no</b> membership — a nonexistent id, or
   * another unit's — switched the guard off entirely, and
   * {@code POST /validateUser.do?…&globalUnitId=99999} relayed a fully migrated account's password to Active
   * Directory. One extra parameter, no authentication required.
   * <p>
   * The suite could not see it: both ids the MIG-001 test uses are real memberships. **A caller-supplied id
   * may narrow a decision to a unit the account holds; it may never escape one.**
   */
  @Test
  public void aGlobalUnitIdTheAccountDoesNotBelongToCannotRoundTheGuard() {
    ExplodingLoginUserManager userManager = new ExplodingLoginUserManager();
    userManager.register(cgiarUser());
    StubGlobalUnitManager crpManager = new StubGlobalUnitManager();
    crpManager.units = Arrays.asList(globalUnit(1L, "MIGRATED"));
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogDefault = "false";

    ValidateUserAction action = newAction(userManager, crpManager, customParameterManager, parameterManager);
    action.setUserEmail(CGIAR_EMAIL);
    action.setUserPassword(SUBMITTED_PASSWORD);
    action.setAgree(Boolean.TRUE);
    action.setGlobalUnitId(Long.valueOf(99999L));

    execute(action);

    // ExplodingLoginUserManager throws if login() runs, so reaching this assertion at all is the proof the
    // password never left MARLO.
    assertEquals(Boolean.FALSE, action.getUserFound().get("loginSuccess"));
  }

  /**
   * A malformed unauthenticated POST must still return JSON, not an HTTP 500. The guard resolves the user
   * before deciding, and {@code UserMySQLDAO.getUser(String)} lowercases its argument into a concatenated
   * SQL string — so an absent {@code userEmail} threw out of {@code execute()} until this was guarded.
   */
  @Test
  public void aMissingUserEmailStillReturnsJsonRatherThanThrowing() {
    RecordingUserManager userManager = new RecordingUserManager();
    StubGlobalUnitManager crpManager = new StubGlobalUnitManager();
    crpManager.units = Arrays.asList();

    ValidateUserAction action = newAction(userManager, crpManager, new StubCustomParameterManager(),
      new StubParameterManager());
    action.setUserPassword(SUBMITTED_PASSWORD);
    action.setAgree(Boolean.TRUE);

    execute(action);

    assertEquals(Boolean.FALSE, action.getUserFound().get("loginSuccess"));
  }

  /**
   * <b>MIG-001's <i>Both paths coexist</i> scenario, which the first implementation of this guard violated.</b>
   * <p>
   * A CGIAR user belongs to a migrated unit (1, flag on) and a non-migrated one (2, flag off). MIG-001 is
   * explicit that *"the path used MUST be determined by the Global Unit selected in wizard step 2"*. The
   * original guard refused whenever <b>any</b> membership was migrated, which locked this user out of unit 2's
   * local login entirely — a regression during exactly the staged rollout MIG-001 describes.
   * <p>
   * Selecting the non-migrated unit must therefore still reach LDAP; selecting the migrated one must not.
   */
  @Test
  public void aMixedMembershipUserIsJudgedByTheUnitTheySelected() {
    // Selecting the NON-migrated unit: the local path must still work.
    RecordingUserManager permissive = new RecordingUserManager();
    permissive.register(cgiarUser());
    StubGlobalUnitManager crpManager = new StubGlobalUnitManager();
    crpManager.units = Arrays.asList(globalUnit(1L, "MIGRATED"), globalUnit(2L, "LEGACY"));
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogDefault = "false";

    ValidateUserAction action = newAction(permissive, crpManager, customParameterManager, parameterManager);
    action.setUserEmail(CGIAR_EMAIL);
    action.setUserPassword(SUBMITTED_PASSWORD);
    action.setAgree(Boolean.TRUE);
    action.setGlobalUnitId(Long.valueOf(2L));

    execute(action);

    assertTrue("selecting the non-migrated unit must still authenticate via LDAP", permissive.loginCalled);

    // Selecting the MIGRATED unit: the password must never leave MARLO.
    ExplodingLoginUserManager exploding = new ExplodingLoginUserManager();
    exploding.register(cgiarUser());
    ValidateUserAction refused = newAction(exploding, crpManager, customParameterManager, parameterManager);
    refused.setUserEmail(CGIAR_EMAIL);
    refused.setUserPassword(SUBMITTED_PASSWORD);
    refused.setAgree(Boolean.TRUE);
    refused.setGlobalUnitId(Long.valueOf(1L));

    execute(refused);

    assertEquals(Boolean.FALSE, refused.getUserFound().get("loginSuccess"));
  }

  /**
   * T11 test 2 -- the task's named <i>Fails when</i> target. {@code is_cgiar_user = 1} but the account's
   * only Global Unit has the flag <b>off</b>: behavior is unchanged, and {@code userManager.login()} MUST
   * still run. A guard that checks only {@code is_cgiar_user} and ignores the flag would refuse this and
   * this test would fail -- see the implementer's report for the mutation run against this exact test.
   */
  @Test
  public void aCgiarAccountWithTheFlagOffStillAuthenticatesViaLdap() {
    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(cgiarUser());
    StubGlobalUnitManager crpManager = new StubGlobalUnitManager();
    crpManager.units = Arrays.asList(globalUnit(1L, "AAA"));
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    // No active override for unit 1L: falls through to the catalog default.
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogDefault = "false";

    ValidateUserAction action = newAction(userManager, crpManager, customParameterManager, parameterManager);
    action.setUserEmail(CGIAR_EMAIL);
    action.setUserPassword(SUBMITTED_PASSWORD);
    action.setAgree(Boolean.TRUE);

    execute(action);

    assertTrue("userManager.login() must still run when no Global Unit has the flag on", userManager.loginCalled);
    assertEquals(CGIAR_EMAIL, userManager.loginEmailSeen);
    assertEquals(SUBMITTED_PASSWORD, userManager.loginPasswordSeen);
  }

  /**
   * T11 test 3 -- SEC-005's {@code BUT MUST NOT} clause. A local ({@code is_cgiar_user = 0}) account is
   * unchanged in every case, even when one of its Global Units has the flag on: this guard exists to stop a
   * CGIAR credential relay, not to gate local accounts. The guard exits on the {@code is_cgiar_user} check
   * before it ever asks which Global Units the account belongs to.
   */
  @Test
  public void aLocalAccountIsUnchangedEvenWhenItsGlobalUnitHasTheFlagOn() {
    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(localUser());
    StubGlobalUnitManager crpManager = new StubGlobalUnitManager();
    crpManager.units = Arrays.asList(globalUnit(1L, "AAA"));
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogDefault = "false";

    ValidateUserAction action = newAction(userManager, crpManager, customParameterManager, parameterManager);
    action.setUserEmail(LOCAL_EMAIL);
    action.setUserPassword(SUBMITTED_PASSWORD);
    action.setAgree(Boolean.TRUE);

    execute(action);

    assertTrue("a local account must still authenticate through the unmodified DB path",
      userManager.loginCalled);
    assertFalse("is_cgiar_user = 0 must never trigger a Global Unit membership lookup",
      crpManager.crpUsersCalled);
  }

  /**
   * T11 test 4 -- no new oracle. The guard's refusal must be byte-identical to what a genuine wrong password
   * produces, so this endpoint cannot be used to fingerprint which accounts have migrated to Cognito.
   * <p>
   * The "genuine wrong password" side is simulated by a {@link RecordingUserManager} configured to mirror
   * exactly what {@code APCustomRealm} plus {@code LDAPAuthenticator} do on a bad CGIAR password: set the
   * Shiro session's {@code LOGIN_MESSAGE} to the raw {@code ERROR_LOGON_FAILURE} key and let the call return
   * {@code null} -- precisely what {@code UserManagerImp.login()}'s caught {@code IncorrectCredentialsException}
   * branch leaves behind. That is the same machinery {@link ValidateUserAction#getLoginMessages()} already
   * reads on every non-guarded refusal, so this comparison exercises the guard against the endpoint's own
   * real failure path, not against a value copied from {@code ADLoginMessages} by hand.
   */
  @Test
  public void theRefusalIsByteIdenticalToAWrongPasswordRefusal() {
    ExplodingLoginUserManager blockedUserManager = new ExplodingLoginUserManager();
    blockedUserManager.register(cgiarUser());
    StubGlobalUnitManager blockedCrpManager = new StubGlobalUnitManager();
    blockedCrpManager.units = Arrays.asList(globalUnit(1L, "AAA"));
    StubCustomParameterManager blockedCustomParameterManager = new StubCustomParameterManager();
    blockedCustomParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager blockedParameterManager = new StubParameterManager();
    blockedParameterManager.catalogDefault = "false";

    ValidateUserAction blockedAction =
      newAction(blockedUserManager, blockedCrpManager, blockedCustomParameterManager, blockedParameterManager);
    blockedAction.setUserEmail(CGIAR_EMAIL);
    blockedAction.setUserPassword(SUBMITTED_PASSWORD);
    blockedAction.setAgree(Boolean.TRUE);
    execute(blockedAction);

    RecordingUserManager wrongPasswordUserManager = new RecordingUserManager();
    wrongPasswordUserManager.register(cgiarUser());
    wrongPasswordUserManager.simulateWrongPassword = true;
    StubGlobalUnitManager wrongPasswordCrpManager = new StubGlobalUnitManager();
    wrongPasswordCrpManager.units = Arrays.asList(globalUnit(1L, "AAA"));
    StubCustomParameterManager wrongPasswordCustomParameterManager = new StubCustomParameterManager();
    // No active override: the flag is off for this attempt, so the request reaches the real login() call.
    StubParameterManager wrongPasswordParameterManager = new StubParameterManager();
    wrongPasswordParameterManager.catalogDefault = "false";

    ValidateUserAction wrongPasswordAction = newAction(wrongPasswordUserManager, wrongPasswordCrpManager,
      wrongPasswordCustomParameterManager, wrongPasswordParameterManager);
    wrongPasswordAction.setUserEmail(CGIAR_EMAIL);
    wrongPasswordAction.setUserPassword("not-the-real-password");
    wrongPasswordAction.setAgree(Boolean.TRUE);
    execute(wrongPasswordAction);

    assertEquals("the guard's refusal must match a genuine wrong-password refusal exactly",
      wrongPasswordAction.getUserFound(), blockedAction.getUserFound());
    assertEquals("the guard's message must match a genuine wrong-password message exactly",
      wrongPasswordAction.getMessageEror(), blockedAction.getMessageEror());
  }

  // ---- CHG-PWD-SWITCH-001-T05: the password gate (SEC-001, SEC-002, OPS-001, DD-5) ----------------------------

  /**
   * AC-10, the sharpest clause: a member of a password-off unit is refused, and the proof is the recorded list of
   * {@code login} calls (with their arguments), which is empty. The gate sits before {@code userManager.login}, so no
   * hash comparison and no LDAP bind happened. One INFO line names the gate and the unit, never the email or the
   * password.
   */
  @Test
  public void thePasswordSwitchRefusesAMemberOfAPasswordOffUnitAndMakesNoCredentialCall() {
    GateWorld world = new GateWorld();
    world.store.passwordByUnitId.put(Long.valueOf(1L), "false");
    world.addMember(localUser(), globalUnit(1L, "OFFUNIT"));

    ValidateUserAction action = world.action(LOCAL_EMAIL, Long.valueOf(1L));
    execute(action);

    assertEquals(Boolean.FALSE, action.getUserFound().get("loginSuccess"));
    assertFalse("a refusal must not disclose a display name", action.getUserFound().containsKey("userName"));
    assertEquals("no credential call: login() must never be reached", Collections.<String>emptyList(),
      world.loginCalls);
    assertEquals("one query, for the unit of the attempt", Arrays.asList(ALL_KEYS + "|[1]"), world.store.reads);
    List<ILoggingEvent> lines = this.linesStartingWith(REFUSED_LINE);
    assertEquals("exactly one refusal line", 1, lines.size());
    assertEquals(Level.INFO, lines.get(0).getLevel());
    assertEquals("auth.password.refused gate=password_switch gu=OFFUNIT", lines.get(0).getFormattedMessage());
    this.assertNothingLoggedMentions(LOCAL_EMAIL, SUBMITTED_PASSWORD);
    assertEquals("a clean refusal logs no ERROR", 0, this.errorLines().size());
  }

  /**
   * <b>The foreign-unit bypass (Judgment Day C-2).</b> The account is a member of unit 1, where the password is off.
   * The caller names unit 2, where it is on and the account is no member, or an id that does not exist. A gate that
   * trusted the named unit would ask only about it, find the password on and compare the credentials. The named unit
   * may narrow the decision to a unit the account holds; it may never escape one. The settings read lists only the
   * account's own units: the named id never reaches the query.
   */
  @Test
  public void aGlobalUnitIdTheAccountDoesNotBelongToCannotRoundThePasswordGate() {
    for (Long named : new Long[] {Long.valueOf(2L), Long.valueOf(99999L)}) {
      this.appender.list.clear();
      GateWorld world = new GateWorld();
      world.store.passwordByUnitId.put(Long.valueOf(1L), "false");
      world.store.passwordByUnitId.put(Long.valueOf(2L), "true");
      world.addMember(localUser(), globalUnit(1L, "OFFUNIT"));

      ValidateUserAction action = world.action(LOCAL_EMAIL, named);
      execute(action);

      assertEquals("named unit " + named, Boolean.FALSE, action.getUserFound().get("loginSuccess"));
      assertEquals("named unit " + named + ": no credential call", Collections.<String>emptyList(),
        world.loginCalls);
      assertEquals("named unit " + named + ": only the account's own units are read",
        Arrays.asList(ALL_KEYS + "|[1]"), world.store.reads);
      assertEquals("named unit " + named, 1, this.linesStartingWith(REFUSED_LINE).size());
    }
  }

  /**
   * The unit of the attempt counts when the account is a member of it: a user of a password-off unit 1 and a
   * password-on unit 2 still reaches unit 2's credential check, and is refused on unit 1.
   */
  @Test
  public void theUnitOfTheAttemptDecidesWhenTheAccountIsAMemberOfIt() {
    GateWorld onUnit = new GateWorld();
    onUnit.store.passwordByUnitId.put(Long.valueOf(1L), "false");
    onUnit.store.passwordByUnitId.put(Long.valueOf(2L), "true");
    onUnit.addMember(localUser(), globalUnit(1L, "OFFUNIT"), globalUnit(2L, "ONUNIT"));

    execute(onUnit.action(LOCAL_EMAIL, Long.valueOf(2L)));

    assertEquals("unit 2 allows the password: the credential check runs, with the submitted pair",
      Arrays.asList(LOCAL_EMAIL + "|" + SUBMITTED_PASSWORD), onUnit.loginCalls);
    assertEquals("only the unit of the attempt is read", Arrays.asList(ALL_KEYS + "|[2]"), onUnit.store.reads);
    assertEquals(0, this.linesStartingWith(REFUSED_LINE).size());

    this.appender.list.clear();
    GateWorld offUnit = new GateWorld();
    offUnit.store.passwordByUnitId.put(Long.valueOf(1L), "false");
    offUnit.store.passwordByUnitId.put(Long.valueOf(2L), "true");
    offUnit.addMember(localUser(), globalUnit(1L, "OFFUNIT"), globalUnit(2L, "ONUNIT"));

    ValidateUserAction refused = offUnit.action(LOCAL_EMAIL, Long.valueOf(1L));
    execute(refused);

    assertEquals(Boolean.FALSE, refused.getUserFound().get("loginSuccess"));
    assertEquals(Collections.<String>emptyList(), offUnit.loginCalls);
    assertEquals("auth.password.refused gate=password_switch gu=OFFUNIT",
      this.linesStartingWith(REFUSED_LINE).get(0).getFormattedMessage());
  }

  /** No unit named: the gate sweeps every membership and refuses when any of them has the password off. */
  @Test
  public void withNoUnitNamedTheGateSweepsEveryMembership() {
    GateWorld mixed = new GateWorld();
    mixed.store.passwordByUnitId.put(Long.valueOf(1L), "true");
    mixed.store.passwordByUnitId.put(Long.valueOf(2L), "false");
    mixed.addMember(localUser(), globalUnit(1L, "ONUNIT"), globalUnit(2L, "OFFUNIT"));

    ValidateUserAction refused = mixed.action(LOCAL_EMAIL, null);
    execute(refused);

    assertEquals(Boolean.FALSE, refused.getUserFound().get("loginSuccess"));
    assertEquals(Collections.<String>emptyList(), mixed.loginCalls);
    assertEquals("every membership in one query", Arrays.asList(ALL_KEYS + "|[1, 2]"), mixed.store.reads);
    assertEquals("auth.password.refused gate=password_switch gu=OFFUNIT",
      this.linesStartingWith(REFUSED_LINE).get(0).getFormattedMessage());

    this.appender.list.clear();
    GateWorld allOn = new GateWorld();
    allOn.store.passwordByUnitId.put(Long.valueOf(1L), "true");
    allOn.store.passwordByUnitId.put(Long.valueOf(2L), "true");
    allOn.addMember(localUser(), globalUnit(1L, "ONUNIT"), globalUnit(2L, "OTHERON"));

    execute(allOn.action(LOCAL_EMAIL, null));

    assertEquals("every membership allows the password: the credential check runs",
      Arrays.asList(LOCAL_EMAIL + "|" + SUBMITTED_PASSWORD), allOn.loginCalls);
    assertEquals(0, this.linesStartingWith(REFUSED_LINE).size());
  }

  /**
   * An account no record knows has no membership, so the gate has nothing to refuse and the credential check fails as
   * it does today. A membership exists for another identifier, so a gate that asked for memberships without resolving
   * the account first would refuse here.
   */
  @Test
  public void anUnknownAccountIsNotRefusedAndTheCredentialCheckRunsAsToday() {
    GateWorld world = new GateWorld();
    world.store.passwordByUnitId.put(Long.valueOf(1L), "false");
    world.addMember(localUser(), globalUnit(1L, "OFFUNIT"));

    ValidateUserAction action = world.action("nobody@cgiar.org", Long.valueOf(1L));
    execute(action);

    assertEquals("the credential check ran, with the submitted pair",
      Arrays.asList("nobody@cgiar.org|" + SUBMITTED_PASSWORD), world.loginCalls);
    assertEquals("an unknown account makes no settings read", 0, world.store.reads.size());
    assertEquals(0, this.linesStartingWith(REFUSED_LINE).size());
    assertEquals(0, this.errorLines().size());
  }

  /** Without a valid code key the password is the last resort: the gate never refuses and reads nothing. */
  @Test
  public void withoutTheCodeKeyTheGateNeverRefusesAndReadsNothing() {
    GateWorld world = new GateWorld();
    world.secret = "";
    world.store.passwordByUnitId.put(Long.valueOf(1L), "false");
    world.addMember(localUser(), globalUnit(1L, "OFFUNIT"));

    execute(world.action(LOCAL_EMAIL, Long.valueOf(1L)));

    assertEquals(Arrays.asList(LOCAL_EMAIL + "|" + SUBMITTED_PASSWORD), world.loginCalls);
    assertEquals("no settings read", 0, world.store.reads.size());
    assertEquals("no membership read", Collections.<String>emptyList(), world.crpManager.crpUsersArgs);
    assertEquals(0, this.linesStartingWith(REFUSED_LINE).size());
    assertEquals("the ERROR of this case is decided in crpByEmail.do, not here", 0, this.errorLines().size());
  }

  /**
   * Fail-open (requirements Decision Log, exception to SEC-001): a database fault must not become a lock-out, because
   * the login itself already fails on such a fault. The credential check runs as today and one ERROR line says why.
   */
  @Test
  public void aFailingSettingsQueryFailsOpenWithOneErrorLine() {
    GateWorld world = new GateWorld();
    world.store.failure = new IllegalStateException("settings read failed");
    world.store.passwordByUnitId.put(Long.valueOf(1L), "false");
    world.addMember(localUser(), globalUnit(1L, "OFFUNIT"));

    execute(world.action(LOCAL_EMAIL, Long.valueOf(1L)));

    assertEquals("fail-open: the credential check runs as today",
      Arrays.asList(LOCAL_EMAIL + "|" + SUBMITTED_PASSWORD), world.loginCalls);
    assertEquals(1, world.store.reads.size());
    assertEquals(0, this.linesStartingWith(REFUSED_LINE).size());
    List<ILoggingEvent> errors = this.errorLines();
    assertEquals(1, errors.size());
    assertEquals("auth.password.gate outcome=settings_unavailable fail_open=true", errors.get(0).getFormattedMessage());
    // The endpoint's own failed-login line may name the account (it always has); the gate's line never does.
    assertFalse(errors.get(0).getFormattedMessage().contains(LOCAL_EMAIL));
    assertFalse(errors.get(0).getFormattedMessage().contains(SUBMITTED_PASSWORD));
  }

  /**
   * SEC-002, local account: the refusal is what a real wrong password gives. For a local account the realm calls
   * {@code DBAuthenticator}, which leaves <b>no</b> message, so the response carries no {@code messageEror} key at all
   * and {@code login.js} treats it as an ordinary failure. The comparison runs on the real credential path
   * ({@link RealCredentialPath}): the real {@code UserManagerImp.login} over the real {@code APCustomRealm}. The same
   * account is attempted on a unit with the password switch off (the refusal) and on one with it on (a real wrong
   * password); the expected values are also literals, so the comparison cannot pass by both sides agreeing wrongly.
   */
  @Test
  public void aRefusedLocalAccountAnswersAsARealWrongPasswordDoes() {
    ValidateUserAction refused = this.attemptOnRealPath(localUser(), "false");
    ValidateUserAction wrong = this.attemptOnRealPath(localUser(), "true");

    assertEquals(wrong.getUserFound(), refused.getUserFound());
    assertEquals(wrong.getMessageEror(), refused.getMessageEror());
    assertEquals(Collections.singletonMap("loginSuccess", Boolean.FALSE), refused.getUserFound());
    assertNull("a local account's wrong password carries no message (DBAuthenticator leaves none)",
      refused.getMessageEror());
  }

  /**
   * SEC-002, CGIAR account (no Cognito in the unit). The directory bind is a stand-in that answers
   * {@code ERROR_LOGON_FAILURE} (see {@link RealCredentialPath}), so this pins the choice the gate makes, not what
   * the directory says.
   */
  @Test
  public void aRefusedCgiarAccountAnswersAsARealWrongPasswordDoes() {
    ValidateUserAction refused = this.attemptOnRealPath(cgiarUser(), "false");
    ValidateUserAction wrong = this.attemptOnRealPath(cgiarUser(), "true");

    assertEquals(wrong.getUserFound(), refused.getUserFound());
    assertEquals(wrong.getMessageEror(), refused.getMessageEror());
    assertEquals("Invalid CGIAR email or password, please try again", refused.getMessageEror());
  }

  /**
   * SEC-002, inactive account: today the realm answers the disabled-account message before any password is compared,
   * whatever the password. A refusal for an inactive member of a password-off unit must say the same, for a local and
   * for a CGIAR account alike.
   */
  @Test
  public void aRefusedInactiveAccountAnswersAsARealWrongPasswordDoes() {
    for (User account : new User[] {localUser(), cgiarUser()}) {
      account.setActive(false);
      ValidateUserAction refused = this.attemptOnRealPath(account, "false");
      ValidateUserAction wrong = this.attemptOnRealPath(account, "true");

      assertEquals(wrong.getUserFound(), refused.getUserFound());
      assertEquals(wrong.getMessageEror(), refused.getMessageEror());
      assertEquals("Your account is disabled, please contact the MARLO staff", refused.getMessageEror());
    }
  }

  /**
   * SEC-002, cookies: a real failed login goes through {@code Subject.login}, and Shiro answers every failure with the
   * remember-me manager's failed-login handling, which on the web security manager production builds sends
   * {@code Set-Cookie: rememberMe=deleteMe}. A refusal never calls {@code login}, so without its own call it would be
   * the one failure that does not send it. The attempt runs on a real {@code DefaultWebSecurityManager} with a web
   * subject over a mock request and response, once refused and once as a real wrong password, and the cookies are
   * compared.
   */
  @Test
  public void aRefusalSendsTheSameCookiesAsARealWrongPasswordOnTheWebSecurityManager() {
    for (User account : new User[] {localUser(), cgiarUser()}) {
      List<String> refused = this.cookiesOfAttempt(account, "false");
      List<String> wrong = this.cookiesOfAttempt(account, "true");

      assertEquals(wrong, refused);
      assertEquals("exactly one remember-me deletion, as Shiro sends it", 1, this.rememberMeDeletions(refused));
    }
  }

  /**
   * The accepted residual of requirements 10 (Decision Log, 2026-10-16, "Execute-time decisions on T05"): a CGIAR
   * account that a directory search or bind would reject before its password is checked gets the CGIAR wrong-password
   * message on refusal, where a real failure shows the answer observed here. The test pins both answers, so a change
   * to either is seen. It does not say the residual is good; it says it is known and accepted.
   */
  @Test
  public void theAcceptedResidualACgiarAccountTheDirectoryWouldRejectGetsTheCgiarTextOnRefusal() {
    ValidateUserAction refused = this.attempt(cgiarUser(), "false", false, null, null);
    ValidateUserAction wrong = this.attempt(cgiarUser(), "true", false, null, null);

    assertNull("a real failure for such an account shows no message", wrong.getMessageEror());
    assertEquals("the refusal shows the CGIAR text", "Invalid CGIAR email or password, please try again",
      refused.getMessageEror());
  }

  /** The {@code Set-Cookie} headers one attempt sent, with the random session id masked. */
  private List<String> cookiesOfAttempt(User account, String passwordSwitch) {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/validateUser.do");
    MockHttpServletResponse response = new MockHttpServletResponse();
    this.attempt(account, passwordSwitch, true, request, response);
    List<String> cookies = new ArrayList<String>();
    for (String header : response.getHeaders("Set-Cookie")) {
      cookies.add(header.replaceAll("JSESSIONID=[^;]*", "JSESSIONID=<id>"));
    }
    return cookies;
  }

  private int rememberMeDeletions(List<String> cookies) {
    int deletions = 0;
    for (String cookie : cookies) {
      if (cookie.startsWith("rememberMe=deleteMe;") && cookie.contains("Max-Age=0")) {
        deletions++;
      }
    }
    return deletions;
  }

  /**
   * One attempt on the real credential path, for an account that is a member of unit 1 and nothing else, on a unit
   * whose password switch is {@code passwordSwitch}. With the switch off the credential check must not run; with it
   * on it runs exactly once, with the submitted pair.
   */
  private ValidateUserAction attemptOnRealPath(User account, String passwordSwitch) {
    return this.attempt(account, passwordSwitch, true, null, null);
  }

  /**
   * The attempt of {@link #attemptOnRealPath}, with the answer of the directory search as a parameter and, when a
   * request is given, on the web security manager production builds, so the cookies the attempt sends are written to
   * {@code response}.
   */
  private ValidateUserAction attempt(User account, String passwordSwitch, boolean nicknameFound,
    MockHttpServletRequest request, MockHttpServletResponse response) {
    RealCredentialPath path = new RealCredentialPath(nicknameFound);
    if (request == null) {
      path.install();
    } else {
      path.installWeb(request, response);
    }
    GateWorld world = new GateWorld(path);
    world.store.passwordByUnitId.put(Long.valueOf(1L), passwordSwitch);
    world.parameterManager.catalogDefault = "false";
    world.addMember(account, globalUnit(1L, "UNIT"));

    ValidateUserAction action = world.action(account.getEmail(), Long.valueOf(1L));
    execute(action);

    if ("false".equals(passwordSwitch)) {
      assertEquals("the refusal made no credential call", Collections.<String>emptyList(), path.loginCalls());
    } else {
      assertEquals("a real wrong password: one credential check, with the submitted pair",
        Arrays.asList(account.getEmail() + "|" + SUBMITTED_PASSWORD), path.loginCalls());
    }
    return action;
  }

  /**
   * SEC-003 and SEC-005 hold unchanged with the gate live: a Cognito account in a unit where the password switch is
   * <b>on</b> is still refused, by the SEC-005 guard, and the gate does not take its place.
   */
  @Test
  public void aMigratedCgiarAccountIsStillRefusedBySec005WithTheGateLive() {
    GateWorld world = new GateWorld();
    world.store.passwordByUnitId.put(Long.valueOf(1L), "true");
    world.customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    world.parameterManager.catalogDefault = "false";
    world.addMember(cgiarUser(), globalUnit(1L, "MIGRATED"));

    ValidateUserAction action = world.action(CGIAR_EMAIL, Long.valueOf(1L));
    execute(action);

    assertEquals(Boolean.FALSE, action.getUserFound().get("loginSuccess"));
    assertEquals("the password never left MARLO", Collections.<String>emptyList(), world.loginCalls);
    boolean refusedBySec005 = false;
    for (ILoggingEvent event : this.appender.list) {
      refusedBySec005 |= event.getFormattedMessage().contains("SEC-005 CGIAR relay guard");
    }
    assertTrue("the SEC-005 guard is the one that refused", refusedBySec005);
    assertEquals("the unit allows the password, so the gate has nothing to say", 0,
      this.linesStartingWith(REFUSED_LINE).size());
  }

  /**
   * The credential check resolves the account by trimming and lower-casing what was typed, but the membership read
   * compares what it is given as it is. The gate asks for memberships by the account's <b>stored</b> email, so padding
   * or case in the identifier cannot make the gate see no membership and let the password through.
   */
  @Test
  public void anIdentifierTheRealmNormalisesStillMeetsTheGateThroughTheStoredEmail() {
    GateWorld world = new GateWorld();
    world.recording.normaliseLookups = true;
    world.store.passwordByUnitId.put(Long.valueOf(1L), "false");
    world.addMember(localUser(), globalUnit(1L, "OFFUNIT"));

    ValidateUserAction action = world.action("  Jane.Local@CGIAR.org ", Long.valueOf(1L));
    execute(action);

    assertEquals(Boolean.FALSE, action.getUserFound().get("loginSuccess"));
    assertEquals(Collections.<String>emptyList(), world.loginCalls);
    assertEquals("memberships are read by the stored email, not by what was typed",
      Arrays.asList(LOCAL_EMAIL), world.crpManager.crpUsersArgs);
    assertEquals(1, this.linesStartingWith(REFUSED_LINE).size());
  }

  /** An account typed as its username is gated like one typed as its email. */
  @Test
  public void anAccountTypedAsItsUsernameIsGatedToo() {
    GateWorld world = new GateWorld();
    world.store.passwordByUnitId.put(Long.valueOf(1L), "false");
    world.addMember(localUser(), globalUnit(1L, "OFFUNIT"));
    world.recording.registerUsername("jane.local", localUser());

    ValidateUserAction action = world.action("jane.local", Long.valueOf(1L));
    execute(action);

    assertEquals(Boolean.FALSE, action.getUserFound().get("loginSuccess"));
    assertEquals(Collections.<String>emptyList(), world.loginCalls);
    assertEquals(Arrays.asList(LOCAL_EMAIL), world.crpManager.crpUsersArgs);
  }

  // ---- log capture and the gate fixture ----------------------------------------------------------------------

  private List<ILoggingEvent> linesStartingWith(String prefix) {
    List<ILoggingEvent> lines = new ArrayList<ILoggingEvent>();
    for (ILoggingEvent event : this.appender.list) {
      if (event.getFormattedMessage().startsWith(prefix)) {
        lines.add(event);
      }
    }
    return lines;
  }

  private List<ILoggingEvent> errorLines() {
    List<ILoggingEvent> lines = new ArrayList<ILoggingEvent>();
    for (ILoggingEvent event : this.appender.list) {
      if (event.getLevel().isGreaterOrEqual(Level.ERROR)) {
        lines.add(event);
      }
    }
    return lines;
  }

  private void assertNothingLoggedMentions(String... secrets) {
    for (ILoggingEvent event : this.appender.list) {
      for (String secret : secrets) {
        assertFalse("a log line must not carry " + secret + ": " + event.getFormattedMessage(),
          event.getFormattedMessage().contains(secret));
      }
    }
  }

  private interface Answer {

    Object answer(String method, Object[] args);
  }

  @SuppressWarnings("unchecked")
  private static <T> T fake(Class<T> type, Answer answer) {
    InvocationHandler handler =
      (proxy, method, args) -> answer.answer(method.getName(), args == null ? new Object[0] : args);
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
  }

  /** The uncached settings store the password gate reads: what each unit's switch says, and what it was asked. */
  private static final class SettingsStore {

    /** The raw effective value of the password switch by Global Unit id; a unit with no entry reads as empty. */
    private final Map<Long, String> passwordByUnitId = new HashMap<Long, String>();
    private final List<String> reads = new ArrayList<String>();
    private RuntimeException failure;

    OtpChallengeManager manager() {
      return fake(OtpChallengeManager.class, (method, args) -> {
        if (!"specificityValuesFor".equals(method)) {
          throw new AssertionError("the gate must read only the settings, but " + method + " was called");
        }
        Collection<?> ids = (Collection<?>) args[1];
        this.reads.add(args[0] + "|" + ids);
        if (this.failure != null) {
          throw this.failure;
        }
        Map<Long, Map<String, String>> answer = new HashMap<Long, Map<String, String>>();
        for (Object id : ids) {
          Map<String, String> values = new HashMap<String, String>();
          values.put("system_otp_login_active", "");
          values.put("system_otp_excluded_email_domains", "");
          String stored = this.passwordByUnitId.get(id);
          values.put("system_password_login_active", stored == null ? "" : stored);
          answer.put((Long) id, values);
        }
        return answer;
      });
    }
  }

  /** One gate scenario: the real {@link OtpEligibility} over the doubles below, and an action built on it. */
  private static final class GateWorld {

    private final SettingsStore store = new SettingsStore();
    /** The recording double, or {@code null} when this world runs on the real credential path. */
    private final RecordingUserManager recording;
    /** The real credential path, or {@code null} when this world runs on the recording double. */
    private final RealCredentialPath real;
    private final UserManager userManager;
    /** Every {@code login} call as {@code email|password}, from whichever manager this world uses. */
    private final List<String> loginCalls;
    private final StubGlobalUnitManager crpManager = new StubGlobalUnitManager();
    private final StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    private final StubParameterManager parameterManager = new StubParameterManager();
    private String secret = VALID_SECRET;

    GateWorld() {
      this.recording = new RecordingUserManager();
      this.real = null;
      this.userManager = this.recording;
      this.loginCalls = this.recording.loginCalls;
    }

    GateWorld(RealCredentialPath real) {
      this.recording = null;
      this.real = real;
      this.userManager = real.userManager();
      this.loginCalls = real.loginCalls();
    }

    /** Registers the account and its memberships, which the membership read answers by the stored email only. */
    void addMember(User user, GlobalUnit... units) {
      if (this.recording != null) {
        this.recording.register(user);
      } else {
        this.real.add(user);
      }
      this.crpManager.unitsByStoredEmail.put(user.getEmail(), Arrays.asList(units));
    }

    ValidateUserAction action(String identifier, Long globalUnitId) {
      final String configured = this.secret;
      APConfig config = new APConfig() {

        @Override
        public String getOtpHmacSecret() {
          return configured;
        }
      };
      CrpUserManager crpUserManager = fake(CrpUserManager.class, (method, args) -> {
        throw new AssertionError("the password gate must not read crp_users rows, but " + method + " was called");
      });
      OtpEligibility eligibility = new OtpEligibility(new OtpKeys(config), this.userManager, this.crpManager,
        crpUserManager, this.store.manager(), this.customParameterManager, this.parameterManager);
      ValidateUserAction action = newAction(this.userManager, this.crpManager, this.customParameterManager,
        this.parameterManager, eligibility);
      action.setUserEmail(identifier);
      action.setUserPassword(SUBMITTED_PASSWORD);
      action.setAgree(Boolean.TRUE);
      if (globalUnitId != null) {
        action.setGlobalUnitId(globalUnitId);
      }
      return action;
    }
  }

  /** Resolves exactly the users registered; throws on {@code login} to prove it is never reached. */
  private static final class ExplodingLoginUserManager implements UserManager {

    private final Map<String, User> byEmail = new HashMap<String, User>();

    void register(User user) {
      this.byEmail.put(user.getEmail(), user);
    }

    @Override
    public User getActiveSuperAdminUserByUsernameOccurrence() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<String> getCenterPermission(int userId, String crp) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<String> getPermission(int userId, String crp) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User getUser(Long userId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User getUserByEmail(String email) {
      // Mirrors the real DAO: UserMySQLDAO.getUser(String) does email.toLowerCase() into a concatenated SQL
      // string, so a null argument throws there. A double that quietly returns null for a null key makes the
      // production null-check untestable -- the test would pass with the check deleted.
      requireResolvableIdentifier(email);
      return this.byEmail.get(email);
    }

    @Override
    public User getUserByUsername(String username) {
      requireResolvableIdentifier(username);
      return this.byEmail.get(username);
    }

    @Override
    public User login(String email, String password) {
      throw new AssertionError("userManager.login() must not be called (SEC-005): the password would "
        + "already have left MARLO for the realm's LDAP branch");
    }

    @Override
    public boolean saveLastLogin(User user) {
      throw new AssertionError("saveLastLogin must not be called on a refused attempt");
    }

    @Override
    public User saveUser(User user) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Records every {@code login} call and can simulate a genuine wrong-password Shiro session outcome. */
  private static final class RecordingUserManager implements UserManager {

    private final Map<String, User> byEmail = new HashMap<String, User>();
    private final Map<String, User> byUsername = new HashMap<String, User>();
    private boolean simulateWrongPassword;
    /** The real DAO trims and lower-cases what it is asked for, which is what the credential check resolves by. */
    private boolean normaliseLookups;
    private boolean loginCalled;
    private String loginEmailSeen;
    private String loginPasswordSeen;
    /** Every {@code login} call as {@code email|password}: the record the "no credential call" proofs read. */
    private final List<String> loginCalls = new ArrayList<String>();

    void register(User user) {
      this.byEmail.put(user.getEmail(), user);
    }

    void registerUsername(String username, User user) {
      this.byUsername.put(username, user);
    }

    @Override
    public User getActiveSuperAdminUserByUsernameOccurrence() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<String> getCenterPermission(int userId, String crp) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<String> getPermission(int userId, String crp) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User getUser(Long userId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User getUserByEmail(String email) {
      // Mirrors the real DAO: UserMySQLDAO.getUser(String) does email.toLowerCase() into a concatenated SQL
      // string, so a null argument throws there. A double that quietly returns null for a null key makes the
      // production null-check untestable -- the test would pass with the check deleted.
      requireResolvableIdentifier(email);
      return this.byEmail.get(this.normaliseLookups ? email.trim().toLowerCase() : email);
    }

    @Override
    public User getUserByUsername(String username) {
      requireResolvableIdentifier(username);
      User byName = this.byUsername.get(username);
      return byName != null ? byName : this.byEmail.get(username);
    }

    @Override
    public User login(String email, String password) {
      this.loginCalls.add(email + "|" + password);
      this.loginCalled = true;
      this.loginEmailSeen = email;
      this.loginPasswordSeen = password;
      if (this.simulateWrongPassword) {
        // Mirrors APCustomRealm's session.setAttribute(LOGIN_MESSAGE, ...) plus LDAPAuthenticator's
        // ERROR_LOGON_FAILURE on a bad password, followed by UserManagerImp.login()'s caught
        // IncorrectCredentialsException returning null -- the exact machinery
        // ValidateUserAction#getLoginMessages() reads on every non-guarded refusal.
        SecurityUtils.getSubject().getSession().setAttribute(APConstants.LOGIN_MESSAGE,
          APConstants.ERROR_LOGON_FAILURE);
      }
      return null;
    }

    @Override
    public boolean saveLastLogin(User user) {
      throw new UnsupportedOperationException("not needed by this suite: these tests never authenticate");
    }

    @Override
    public User saveUser(User user) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Records whether {@code crpUsers(String)} was invoked, and returns the configured unit list. */
  private static final class StubGlobalUnitManager implements GlobalUnitManager {

    private List<GlobalUnit> units = new ArrayList<GlobalUnit>();
    /**
     * When non-empty the answer is keyed by the exact email asked for, as the real HQL equality is: an email that
     * is not a stored one finds nothing, whatever {@link #units} holds.
     */
    private final Map<String, List<GlobalUnit>> unitsByStoredEmail = new HashMap<String, List<GlobalUnit>>();
    private final List<String> crpUsersArgs = new ArrayList<String>();
    private boolean crpUsersCalled;

    @Override
    public List<GlobalUnit> crpUsers(String email) {
      this.crpUsersCalled = true;
      this.crpUsersArgs.add(email);
      if (!this.unitsByStoredEmail.isEmpty()) {
        List<GlobalUnit> stored = this.unitsByStoredEmail.get(email);
        return stored == null ? new ArrayList<GlobalUnit>() : new ArrayList<GlobalUnit>(stored);
      }
      return new ArrayList<GlobalUnit>(this.units);
    }

    @Override
    public void deleteGlobalUnit(long globalUnitId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existGlobalUnit(long globalUnitID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<GlobalUnit> findAll() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public GlobalUnit findGlobalUnitByAcronym(String acronym) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public GlobalUnit findGlobalUnitBySMOCode(String smoCode) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public GlobalUnit getGlobalUnitById(long globalUnitID) {
      // CHG-COGNITO-AUTH-001-T14 (audit finding 3): ValidateUserAction's SEC-005 guard log line now
      // resolves the Global Unit's acronym for observability (design.md 11 row 4). Resolved from the same
      // `units` fixture every test in this suite already populates, rather than thrown, since the guard
      // reaches this call on every blocked attempt -- not only in a test written to exercise it.
      for (GlobalUnit unit : this.units) {
        if (unit.getId().longValue() == globalUnitID) {
          return unit;
        }
      }
      return null;
    }

    @Override
    public GlobalUnit saveGlobalUnit(GlobalUnit globalUnit) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Resolves only the {@code cognito_auth_active} key, keyed by Global Unit id; anything else is a test bug. */
  private static final class StubCustomParameterManager implements CustomParameterManager {

    private final Map<Long, CustomParameter> overridesByUnitId = new HashMap<Long, CustomParameter>();

    @Override
    public void deleteCustomParameter(long customParameterId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existCustomParameter(long customParameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<CustomParameter> findAll() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<CustomParameter> getAllCustomParametersByGlobalUnitId(long globalUnitId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CustomParameter getCustomParameterById(long customParameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CustomParameter getCustomParameterByParameterKeyAndGlobalUnitId(String paramaterKey, long globalUnitId) {
      if (!APConstants.COGNITO_AUTH_ACTIVE.equals(paramaterKey)) {
        throw new IllegalStateException("test authoring error: unexpected key " + paramaterKey);
      }
      return this.overridesByUnitId.get(Long.valueOf(globalUnitId));
    }

    @Override
    public CustomParameter saveCustomParameter(CustomParameter customParameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Resolves only the {@code cognito_auth_active} key with one configured catalog default. */
  private static final class StubParameterManager implements ParameterManager {

    private String catalogDefault;

    @Override
    public void deleteParameter(long parameterId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existParameter(long parameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<Parameter> findAll() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public Parameter getParameterById(long parameterID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public Parameter getParameterByKey(String key, long globalUnitId) {
      if (!APConstants.COGNITO_AUTH_ACTIVE.equals(key)) {
        throw new IllegalStateException("test authoring error: unexpected key " + key);
      }
      if (globalUnitId != EXPECTED_TYPE_ID) {
        return null;
      }
      return catalogRow(this.catalogDefault);
    }

    @Override
    public Parameter saveParameter(Parameter parameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }
}
