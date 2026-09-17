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

import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Covers CHG-COGNITO-AUTH-001-T11b: the guard {@code LoginAction#login()} must apply before it ever calls
 * {@code userManager.login()} -- design.md 5.3, SEC-005's remaining endpoint.
 * <p>
 * This suite mirrors {@code ValidateUserActionGuardTest} (T11) deliberately and closely: same fixture
 * shapes, same "exploding double" idiom, same mixed-membership scenario, because {@code LoginAction} now
 * shares {@code CognitoAuthSpecificity} (PS-16) with {@code ValidateUserAction}, {@code CrpByUserEmailAction}
 * and {@code CognitoLoginAction}, and the two guards are the same security property applied to two doors
 * (execution.md 15.4, PS-21).
 * <p>
 * <b>Why test 1 asserts on the collaborator, not the returned result string.</b> T11b's own <i>Not evidence
 * when</i> clause is explicit: a response can be shaped correctly while the LDAP bind already happened,
 * because by then the submitted password has already left MARLO. {@link
 * #aMigratedCgiarAccountIsRefusedAndLoginIsNeverCalled()} therefore uses a {@link UserManager} double whose
 * {@code login(String, String)} <b>throws</b> {@code AssertionError} unconditionally -- the same idiom
 * {@code ValidateUserActionGuardTest} and {@code APCustomRealmDispatchTest} use to prove a collaborator was
 * never reached.
 * <p>
 * Every collaborator is a hand-rolled double: MARLO has no mocking framework ({@code DEC-005} is
 * {@code PENDING}). {@code platformsList} is seeded via reflection before every {@code login()} call so the
 * unrelated {@code getCrpCategoryList} landing-page population in {@code login()}'s first lines -- which
 * reads a different, BaseAction-private {@code crpManager} field this suite does not wire -- never runs;
 * that population is orthogonal to the guard under test.
 */
public class LoginActionCgiarGuardTest {

  private static final String CGIAR_EMAIL = "priya.cgiar@cgiar.org";
  private static final String LOCAL_EMAIL = "jane.local@cgiar.org";
  private static final String SUBMITTED_PASSWORD = "whatever-password";

  /**
   * Global Unit ids and Global Unit TYPE ids are deliberately disjoint number ranges, matching
   * {@code ValidateUserActionGuardTest}'s convention: if they overlapped, passing the wrong one to the
   * catalog lookup would resolve anyway and a mistake would never surface.
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

  private static User formUser(String email) {
    User user = new User();
    user.setEmail(email);
    user.setPassword(SUBMITTED_PASSWORD);
    return user;
  }

  private static User cgiarAccount() {
    User user = new User();
    user.setId(Long.valueOf(9001L));
    user.setEmail(CGIAR_EMAIL);
    user.setCgiarUser(true);
    user.setActive(true);
    return user;
  }

  private static User localAccount() {
    User user = new User();
    user.setId(Long.valueOf(9002L));
    user.setEmail(LOCAL_EMAIL);
    user.setCgiarUser(false);
    user.setActive(true);
    return user;
  }

  private static Parameter catalogRow(String defaultValue) {
    Parameter parameter = new Parameter();
    parameter.setDefaultValue(defaultValue);
    return parameter;
  }

  private static CustomParameter activeOverride(String value) {
    CustomParameter override = new CustomParameter();
    override.setValue(value);
    override.setActive(true);
    return override;
  }

  /**
   * Empties {@code platformsList} via reflection so {@code login()}'s landing-page population never runs --
   * see the class javadoc for why that population is out of scope for this guard suite.
   */
  private static void suppressLandingPageListLoad(LoginAction action) throws Exception {
    Field field = LoginAction.class.getDeclaredField("platformsList");
    field.setAccessible(true);
    field.set(action, new ArrayList<GlobalUnit>());
  }

  @Before
  public void bindSecurityManager() {
    // The non-guard path (userManager.login() -> getLoginMessages()) reads
    // SecurityUtils.getSubject().getSession(); a plain non-web manager gives it a real, in-memory Shiro
    // session with no servlet container involved -- same setup as ValidateUserActionGuardTest.
    SecurityUtils.setSecurityManager(new DefaultSecurityManager());
    ActionContext.of(new HashMap<String, Object>()).bind();
  }

  @After
  public void unbindSecurityManager() {
    SecurityUtils.setSecurityManager(null);
    // Not merely tidiness -- see APCustomRealmDispatchTest's identical note: JUnit reuses one thread, and a
    // Subject bound to ThreadContext by this class would otherwise leak into a later test.
    ThreadContext.remove();
    ActionContext.clear();
  }

  private static TestableLoginAction newAction(UserManager userManager, GlobalUnitManager crpManager,
    CustomParameterManager customParameterManager, ParameterManager parameterManager) {
    TestableLoginAction action = new TestableLoginAction(new APConfig(), userManager, crpManager,
      new UnreachedCrpUserManager(), customParameterManager, parameterManager);
    action.setSession(new HashMap<String, Object>());
    return action;
  }

  private static String login(TestableLoginAction action) {
    try {
      suppressLandingPageListLoad(action);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
    return action.login();
  }

  /**
   * T11b test 1 -- the task's sharpest clause. {@code is_cgiar_user = 1} and the account's Global Unit has
   * {@code cognito_auth_active} on: refused, and {@link ExplodingLoginUserManager#login(String, String)}
   * would throw if it were ever reached. Reaching the assertions below without a thrown
   * {@code AssertionError} <b>is</b> the proof the LDAP bind never happened.
   */
  @Test
  public void aMigratedCgiarAccountIsRefusedAndLoginIsNeverCalled() {
    ExplodingLoginUserManager userManager = new ExplodingLoginUserManager();
    userManager.register(cgiarAccount());
    StubGlobalUnitManager crpManager = new StubGlobalUnitManager();
    crpManager.units = Arrays.asList(globalUnit(1L, "AAA"));
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogDefault = "false";

    TestableLoginAction action = newAction(userManager, crpManager, customParameterManager, parameterManager);
    action.setUser(formUser(CGIAR_EMAIL));
    // No crp selected: falls through to the fail-closed sweep across every membership, exactly as an
    // unresolvable/absent acronym must.

    String result = login(action);

    // ExplodingLoginUserManager throws if login() runs, so reaching this assertion at all is the proof the
    // password never left MARLO.
    assertEquals(Action.INPUT, result);
  }

  /**
   * MIG-001's <i>Both paths coexist</i> scenario, applied via a caller-named unit the account does NOT
   * belong to -- the exact shape of T11's first-correction bypass (execution.md 15.1):
   * {@code &globalUnitId=99999} matched no membership and fell out of the loop returning "allow". Here the
   * {@code crp} acronym resolves to a real Global Unit, but not one this account is a member of, so it must
   * be treated as no selection and fall through to the fail-closed sweep -- not treated as an escape hatch.
   */
  @Test
  public void aGlobalUnitTheAccountDoesNotBelongToCannotRoundTheGuard() {
    ExplodingLoginUserManager userManager = new ExplodingLoginUserManager();
    userManager.register(cgiarAccount());
    StubGlobalUnitManager crpManager = new StubGlobalUnitManager();
    crpManager.units = Arrays.asList(globalUnit(1L, "MIGRATED"));
    // The selected acronym resolves to a real, but unrelated, Global Unit -- not a membership.
    crpManager.byAcronym.put("OTHERUNIT", globalUnit(99999L, "OTHERUNIT"));
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogDefault = "false";

    TestableLoginAction action = newAction(userManager, crpManager, customParameterManager, parameterManager);
    action.setUser(formUser(CGIAR_EMAIL));
    action.setCrp("OTHERUNIT");

    String result = login(action);

    assertEquals(Action.INPUT, result);
  }

  /**
   * T11b test 2 -- the task's named <i>Fails when</i> target. {@code is_cgiar_user = 1} but the account's
   * only Global Unit has the flag <b>off</b>: behavior is unchanged, and {@code userManager.login()} MUST
   * still run.
   */
  @Test
  public void aCgiarAccountWithTheFlagOffStillAuthenticatesViaLdap() {
    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(cgiarAccount());
    StubGlobalUnitManager crpManager = new StubGlobalUnitManager();
    crpManager.units = Arrays.asList(globalUnit(1L, "AAA"));
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    // No active override for unit 1L: falls through to the catalog default.
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogDefault = "false";

    TestableLoginAction action = newAction(userManager, crpManager, customParameterManager, parameterManager);
    action.setUser(formUser(CGIAR_EMAIL));

    login(action);

    assertTrue("userManager.login() must still run when no Global Unit has the flag on", userManager.loginCalled);
    assertEquals(CGIAR_EMAIL, userManager.loginEmailSeen);
    assertEquals(SUBMITTED_PASSWORD, userManager.loginPasswordSeen);
  }

  /**
   * T11b test 3 -- SEC-005's {@code BUT MUST NOT} clause. A local ({@code is_cgiar_user = 0}) account is
   * unchanged in every case, even when one of its Global Units has the flag on.
   */
  @Test
  public void aLocalAccountIsUnchangedEvenWhenItsGlobalUnitHasTheFlagOn() {
    RecordingUserManager userManager = new RecordingUserManager();
    userManager.register(localAccount());
    StubGlobalUnitManager crpManager = new StubGlobalUnitManager();
    crpManager.units = Arrays.asList(globalUnit(1L, "AAA"));
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogDefault = "false";

    TestableLoginAction action = newAction(userManager, crpManager, customParameterManager, parameterManager);
    action.setUser(formUser(LOCAL_EMAIL));

    login(action);

    assertTrue("a local account must still authenticate through the unmodified DB/LDAP path",
      userManager.loginCalled);
    assertFalse("is_cgiar_user = 0 must never trigger a Global Unit membership lookup",
      crpManager.crpUsersCalled);
  }

  /**
   * T11b test 4 -- no new oracle. The guard's refusal must be indistinguishable from what a genuine wrong
   * password produces on this same action, so this endpoint cannot be used to fingerprint which accounts
   * have migrated to Cognito.
   * <p>
   * The "genuine wrong password" side is simulated by a {@link RecordingUserManager} configured to mirror
   * exactly what {@code APCustomRealm} plus {@code LDAPAuthenticator} do on a bad CGIAR password: set the
   * Shiro session's {@code LOGIN_MESSAGE} to the raw {@code ERROR_LOGON_FAILURE} key and return {@code null}
   * -- precisely what {@code UserManagerImp.login()}'s caught {@code IncorrectCredentialsException} branch
   * leaves behind, and the same machinery {@code LoginAction#getLoginMessages()} reads on every non-guarded
   * refusal.
   */
  @Test
  public void theRefusalIsIndistinguishableFromAWrongPasswordRefusal() {
    ExplodingLoginUserManager blockedUserManager = new ExplodingLoginUserManager();
    blockedUserManager.register(cgiarAccount());
    StubGlobalUnitManager blockedCrpManager = new StubGlobalUnitManager();
    blockedCrpManager.units = Arrays.asList(globalUnit(1L, "AAA"));
    StubCustomParameterManager blockedCustomParameterManager = new StubCustomParameterManager();
    blockedCustomParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager blockedParameterManager = new StubParameterManager();
    blockedParameterManager.catalogDefault = "false";

    TestableLoginAction blockedAction =
      newAction(blockedUserManager, blockedCrpManager, blockedCustomParameterManager, blockedParameterManager);
    blockedAction.setUser(formUser(CGIAR_EMAIL));
    String blockedResult = login(blockedAction);

    RecordingUserManager wrongPasswordUserManager = new RecordingUserManager();
    wrongPasswordUserManager.register(cgiarAccount());
    wrongPasswordUserManager.simulateWrongPassword = true;
    StubGlobalUnitManager wrongPasswordCrpManager = new StubGlobalUnitManager();
    wrongPasswordCrpManager.units = Arrays.asList(globalUnit(1L, "AAA"));
    StubCustomParameterManager wrongPasswordCustomParameterManager = new StubCustomParameterManager();
    // No active override: the flag is off for this attempt, so the request reaches the real login() call.
    StubParameterManager wrongPasswordParameterManager = new StubParameterManager();
    wrongPasswordParameterManager.catalogDefault = "false";

    TestableLoginAction wrongPasswordAction = newAction(wrongPasswordUserManager, wrongPasswordCrpManager,
      wrongPasswordCustomParameterManager, wrongPasswordParameterManager);
    wrongPasswordAction.setUser(formUser(CGIAR_EMAIL));
    String wrongPasswordResult = login(wrongPasswordAction);

    assertEquals("the guard's result code must match a genuine wrong-password refusal exactly",
      wrongPasswordResult, blockedResult);
    assertEquals("the guard's field error must match a genuine wrong-password field error exactly",
      wrongPasswordAction.getFieldErrors().get("loginMessage"), blockedAction.getFieldErrors().get("loginMessage"));
  }

  /**
   * T11b test 5 -- MIG-001's <i>Both paths coexist</i> scenario, which T11's guard on the sibling endpoint
   * got wrong on its first attempt (execution.md 15). A CGIAR user belongs to a migrated unit (1, flag on)
   * and a non-migrated one (2, flag off). Selecting the non-migrated unit must still reach LDAP; selecting
   * the migrated one must not.
   */
  @Test
  public void aMixedMembershipUserIsJudgedByTheUnitTheySelected() {
    // Selecting the NON-migrated unit: the local path must still work.
    RecordingUserManager permissive = new RecordingUserManager();
    permissive.register(cgiarAccount());
    StubGlobalUnitManager permissiveCrpManager = new StubGlobalUnitManager();
    permissiveCrpManager.units = Arrays.asList(globalUnit(1L, "MIGRATED"), globalUnit(2L, "LEGACY"));
    permissiveCrpManager.byAcronym.put("LEGACY", globalUnit(2L, "LEGACY"));
    StubCustomParameterManager customParameterManager = new StubCustomParameterManager();
    customParameterManager.overridesByUnitId.put(Long.valueOf(1L), activeOverride("true"));
    StubParameterManager parameterManager = new StubParameterManager();
    parameterManager.catalogDefault = "false";

    TestableLoginAction permissiveAction =
      newAction(permissive, permissiveCrpManager, customParameterManager, parameterManager);
    permissiveAction.setUser(formUser(CGIAR_EMAIL));
    permissiveAction.setCrp("LEGACY");

    login(permissiveAction);

    assertTrue("selecting the non-migrated unit must still authenticate via LDAP", permissive.loginCalled);

    // Selecting the MIGRATED unit: the password must never leave MARLO.
    ExplodingLoginUserManager exploding = new ExplodingLoginUserManager();
    exploding.register(cgiarAccount());
    StubGlobalUnitManager refusedCrpManager = new StubGlobalUnitManager();
    refusedCrpManager.units = Arrays.asList(globalUnit(1L, "MIGRATED"), globalUnit(2L, "LEGACY"));
    refusedCrpManager.byAcronym.put("MIGRATED", globalUnit(1L, "MIGRATED"));

    TestableLoginAction refusedAction =
      newAction(exploding, refusedCrpManager, customParameterManager, parameterManager);
    refusedAction.setUser(formUser(CGIAR_EMAIL));
    refusedAction.setCrp("MIGRATED");

    String refusedResult = login(refusedAction);

    assertEquals(Action.INPUT, refusedResult);
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
      return this.byEmail.get(email);
    }

    @Override
    public User getUserByUsername(String username) {
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
      return this.byEmail.get(email);
    }

    @Override
    public User getUserByUsername(String username) {
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
        // LoginAction#getLoginMessages() reads on every non-guarded refusal.
        SecurityUtils.getSubject().getSession().setAttribute(org.cgiar.ccafs.marlo.config.APConstants.LOGIN_MESSAGE,
          org.cgiar.ccafs.marlo.config.APConstants.ERROR_LOGON_FAILURE);
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

  /**
   * Records whether {@code crpUsers(String)} was invoked, resolves acronyms via {@code byAcronym}, and
   * returns the configured membership list.
   */
  private static final class StubGlobalUnitManager implements GlobalUnitManager {

    private List<GlobalUnit> units = new ArrayList<GlobalUnit>();
    private final Map<String, GlobalUnit> byAcronym = new HashMap<String, GlobalUnit>();
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
      return this.byAcronym.get(acronym);
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

  /** Never reached by this suite: {@code finishLogin} (T01's shared tail) is out of the guard's path. */
  private static final class UnreachedCrpUserManager implements CrpUserManager {

    @Override
    public void deleteCrpUser(long crpUserId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existActiveCrpUser(long userId, long crpId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existCrpUser(long crpUserID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public boolean existCrpUser(long userId, long crpId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public List<CrpUser> findAll() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CrpUser getCrpUserById(long crpUserID) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CrpUser getCrpUserByUserIdAndCrpId(long userId, long crpId) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CrpUser saveCrpUser(CrpUser crpUser) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /**
   * Bypasses only the infrastructure this task does not touch, matching {@code LoginActionFinishLoginTest}'s
   * {@code TestableLoginAction}. {@code getText} needs a live Struts container; the guard's field error and
   * routing are what is under test.
   */
  private static final class TestableLoginAction extends LoginAction {

    private static final long serialVersionUID = 1L;

    TestableLoginAction(APConfig config, UserManager userManager, GlobalUnitManager crpManager,
      CrpUserManager crpUserManager, CustomParameterManager customParameterManager,
      ParameterManager parameterManager) {
      super(config, userManager, crpManager, crpUserManager, customParameterManager, parameterManager);
    }

    @Override
    public String getText(String aTextName) {
      return aTextName;
    }
  }
}
