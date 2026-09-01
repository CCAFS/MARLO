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
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.xwork2.ActionContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.apache.struts2.ServletActionContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
 */
public class ValidateUserActionGuardTest {

  private static final String CGIAR_EMAIL = "priya.cgiar@cgiar.org";
  private static final String LOCAL_EMAIL = "jane.local@cgiar.org";
  private static final String SUBMITTED_PASSWORD = "whatever-password";

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
    ValidateUserAction action =
      new ValidateUserAction(new APConfig(), userManager, crpManager, customParameterManager, parameterManager);
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
    private boolean simulateWrongPassword;
    private boolean loginCalled;
    private String loginEmailSeen;
    private String loginPasswordSeen;

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
    private boolean crpUsersCalled;

    @Override
    public List<GlobalUnit> crpUsers(String email) {
      this.crpUsersCalled = true;
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
      throw new UnsupportedOperationException("not needed by this suite");
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
