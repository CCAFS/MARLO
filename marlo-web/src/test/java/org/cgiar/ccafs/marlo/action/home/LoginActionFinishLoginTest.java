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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.ServletActionContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Pins the observable behavior of {@code LoginAction.login(User, GlobalUnit)}'s post-authentication tail
 * -- the {@code Referer} branch and the Global-Unit-type routing switch -- so that
 * CHG-COGNITO-AUTH-001-T01's extraction of {@code finishLogin} can be proven behavior-preserving.
 * <p>
 * <b>These tests were written against the pre-extraction code</b> and drive the public
 * {@code login(User, GlobalUnit)} entry point, not {@code finishLogin} directly. That is deliberate and is
 * T01's <i>Not evidence when</i> clause: a routing test written by reading the extracted method proves only
 * that the new code agrees with itself. Driving the old entry point means the same assertions run unchanged
 * before and after the extraction, so a green run on both sides is real equivalence evidence.
 * <p>
 * Every collaborator is a hand-rolled double: MARLO has no mocking framework ({@code DEC-005} is
 * {@code PENDING}, and child 2 of the Cognito family owns {@code marlo-parent/pom.xml}, so this suite
 * deliberately adds no test dependency). The {@code HttpServletRequest} is a {@link Proxy} answering only
 * {@code getHeader("Referer")} -- the single request interaction this code path makes.
 */
public class LoginActionFinishLoginTest {

  private static final String BASE_URL = "https://marlo.example.org";

  private TestableLoginAction action;

  /**
   * The parameter is deliberately {@code Object}, not {@code LoginAction}: the old
   * {@code maven-surefire-plugin:2.12.4} JUnit4 test-class scanner resolves every declared method's
   * parameter and return types on this outer test class while probing it for {@code @Test} methods, and
   * doing that for {@code LoginAction} (which pulls in the whole {@code BaseAction}) crashes the scanning
   * fork with a bare {@code NoClassDefFoundError} before any test runs. A reference inside a method body,
   * resolved lazily at first execution, does not trigger it. Same hazard, same workaround, as
   * {@code CrpUsersActionDirectoryTest}.
   */
  private static void setFormUser(Object loginAction, User formUser) throws Exception {
    Field field = LoginAction.class.getDeclaredField("user");
    field.setAccessible(true);
    field.set(loginAction, formUser);
  }

  /** Reads back the {@code url} field that the routing switch and the {@code Referer} branch both write. */
  private static String urlOf(Object loginAction) throws Exception {
    Field field = LoginAction.class.getDeclaredField("url");
    field.setAccessible(true);
    return (String) field.get(loginAction);
  }

  /**
   * Installs an {@code HttpServletRequest} whose {@code getHeader("Referer")} returns {@code value}.
   * A {@code null} value models the real, reachable case this action never guarded: a POST to
   * {@code login.do} with no {@code Referer} header, which browsers omit under
   * {@code Referrer-Policy: no-referrer} and which curl and Postman omit by default.
   */
  private static void setReferer(final String value) {
    HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
      LoginActionFinishLoginTest.class.getClassLoader(), new Class<?>[] {HttpServletRequest.class},
      new InvocationHandler() {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
          if ("getHeader".equals(method.getName()) && args != null && args.length == 1
            && "Referer".equals(args[0])) {
            return value;
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
    // Struts 6.8's ServletActionContext.setRequest delegates to ActionContext.getContext(), which is
    // null outside a live request. Binding an empty context is the minimum needed to install the stub.
    ActionContext.of(new HashMap<String, Object>()).bind();
    ServletActionContext.setRequest(request);
  }

  private static GlobalUnit globalUnitOfType(int typeId) {
    GlobalUnitType type = new GlobalUnitType();
    type.setId(Long.valueOf(typeId));
    GlobalUnit globalUnit = new GlobalUnit();
    globalUnit.setId(Long.valueOf(7L));
    globalUnit.setAcronym("TESTCRP");
    globalUnit.setGlobalUnitType(type);
    return globalUnit;
  }

  private static User loggedUser() {
    User loggedUser = new User();
    loggedUser.setId(Long.valueOf(42L));
    loggedUser.setEmail("jane.smith@cgiar.org");
    return loggedUser;
  }

  @Before
  public void setUp() throws Exception {
    APConfig config = new APConfig();
    this.action = new TestableLoginAction(config, new FakeUserManager(), new NoOpGlobalUnitManager(),
      new AlwaysMemberCrpUserManager(), new NoCustomParametersManager(), new NoOpParameterManager());
    this.action.setSession(new HashMap<String, Object>());
    // The success log line dereferences the `user` FIELD, not the loggedUser argument. T01 leaves that
    // line alone (T08 is what makes the field non-null on the Cognito path), so the field must be set
    // here or every test NPEs for a reason unrelated to what it measures.
    setFormUser(this.action, loggedUser());
  }

  @After
  public void tearDown() {
    ActionContext.clear();
  }

  /** A {@code Referer} carrying {@code .do} is the deep link to return to: {@code LOGIN} plus {@code url}. */
  @Test
  public void refererContainingDoReturnsLoginAndSetsUrl() throws Exception {
    setReferer("https://marlo.example.org/projects/testcrpProjectList.do");

    String result = this.action.login(loggedUser(), globalUnitOfType(1));

    assertEquals(Action.LOGIN, result);
    assertEquals("https://marlo.example.org/projects/testcrpProjectList.do", urlOf(this.action));
  }

  /**
   * A {@code logout} URL must NOT be returned to -- that would bounce the user straight back out. The
   * branch falls through to type routing instead. Type 1 is used so the expected result ({@code success})
   * differs from the {@code LOGIN} the {@code .do} branch returns; otherwise the assertion could not tell
   * the two branches apart, since a logout URL contains {@code .do} as well.
   */
  @Test
  public void refererContainingLogoutFallsThroughToTypeRouting() throws Exception {
    setReferer("https://marlo.example.org/logout.do");

    String result = this.action.login(loggedUser(), globalUnitOfType(1));

    assertEquals(Action.SUCCESS, result);
    assertNull("the logout URL must not be adopted as the return target", urlOf(this.action));
  }

  /**
   * The null guard. <b>Fails when run against the pre-extraction code</b>: the {@code Referer} branch
   * calls {@code urlAction.contains(".do")} on an unguarded {@code getHeader("Referer")} result, so a
   * request with no {@code Referer} throws {@code NullPointerException}. That NPE is the proof the guard
   * T01 adds is real and not decorative.
   */
  @Test
  public void absentRefererDoesNotThrowAndFallsThroughToTypeRouting() throws Exception {
    setReferer(null);

    String result;
    try {
      result = this.action.login(loggedUser(), globalUnitOfType(1));
    } catch (NullPointerException e) {
      fail("a login with no Referer header must not NPE: " + e);
      return;
    }

    assertEquals(Action.SUCCESS, result);
    assertNull(urlOf(this.action));
  }

  /**
   * The seam itself. Every test above drives {@code login(User, GlobalUnit)} so it can run on both sides of
   * the extraction; this one calls {@code finishLogin} directly, with an explicit {@code returnUrl} and no
   * {@code Referer} installed at all, because that is exactly how CHG-COGNITO-AUTH-001-T09's
   * {@code CognitoCallbackAction} will call it -- a federated callback has no {@code Referer} to read.
   * <p>
   * It therefore cannot exist before the extraction, and it is the only test here that is not equivalence
   * evidence. It is reachability evidence: it proves the seam T08 and T09 depend on is actually usable.
   */
  @Test
  public void finishLoginAcceptsAnExplicitReturnUrlWithNoRequestPresent() throws Exception {
    String result = this.action.finishLogin(loggedUser(), globalUnitOfType(1),
      "https://marlo.example.org/projects/testcrpProjectList.do");

    assertEquals(Action.LOGIN, result);
    assertEquals("https://marlo.example.org/projects/testcrpProjectList.do", urlOf(this.action));
  }

  /**
   * Pins the routing switch exactly as it reads today, including the fact that type 2 is the only branch
   * that builds a URL and that {@code default} returns {@code input}. Written from the old source, not
   * from the extracted method.
   */
  @Test
  public void globalUnitTypeRoutingIsUnchangedForEveryType() throws Exception {
    assertEquals(Action.SUCCESS, this.routeWithBenignReferer(1));
    assertNull(urlOf(this.action));

    assertEquals(Action.LOGIN, this.routeWithBenignReferer(2));
    assertEquals(BASE_URL + "/TESTCRP/centerDashboard.do", urlOf(this.action));

    assertEquals(Action.SUCCESS, this.routeWithBenignReferer(3));
    assertEquals(Action.SUCCESS, this.routeWithBenignReferer(4));
    assertEquals(Action.SUCCESS, this.routeWithBenignReferer(5));

    assertEquals(Action.INPUT, this.routeWithBenignReferer(99));
  }

  /**
   * Drives one routing case on a freshly-built action so a {@code url} written by a previous case cannot
   * leak into the next assertion.
   */
  private String routeWithBenignReferer(int typeId) throws Exception {
    this.setUp();
    // A Referer that is neither a `.do` deep link nor a logout URL, so the branch falls through to the
    // switch. Deliberately NOT null: this test measures routing, and a null here would instead trip the
    // unguarded dereference that `absentRefererDoesNotThrowAndFallsThroughToTypeRouting` owns -- which
    // would make this test un-runnable against the pre-extraction code and destroy its equivalence value.
    setReferer("https://marlo.example.org/home");
    return this.action.login(loggedUser(), globalUnitOfType(typeId));
  }

  /** Always reports the user as a member, so the happy path reaches the routing tail under test. */
  private static final class AlwaysMemberCrpUserManager implements CrpUserManager {

    @Override
    public void deleteCrpUser(long crpUserId) {
    }

    @Override
    public boolean existActiveCrpUser(long userId, long crpId) {
      return true;
    }

    @Override
    public boolean existCrpUser(long crpUserID) {
      return true;
    }

    @Override
    public boolean existCrpUser(long userId, long crpId) {
      return true;
    }

    @Override
    public List<CrpUser> findAll() {
      return new ArrayList<CrpUser>();
    }

    @Override
    public CrpUser getCrpUserById(long crpUserID) {
      return null;
    }

    @Override
    public CrpUser getCrpUserByUserIdAndCrpId(long userId, long crpId) {
      return null;
    }

    @Override
    public CrpUser saveCrpUser(CrpUser crpUser) {
      return crpUser;
    }
  }

  /** No custom parameters, so the session-population loop runs zero iterations and pins nothing extra. */
  private static final class NoCustomParametersManager implements CustomParameterManager {

    @Override
    public void deleteCustomParameter(long customParameterId) {
    }

    @Override
    public boolean existCustomParameter(long customParameterID) {
      return false;
    }

    @Override
    public List<CustomParameter> findAll() {
      return new ArrayList<CustomParameter>();
    }

    @Override
    public List<CustomParameter> getAllCustomParametersByGlobalUnitId(long globalUnitId) {
      return new ArrayList<CustomParameter>();
    }

    @Override
    public CustomParameter getCustomParameterById(long customParameterID) {
      return null;
    }

    @Override
    public CustomParameter getCustomParameterByParameterKeyAndGlobalUnitId(String paramaterKey, long globalUnitId) {
      return null;
    }

    @Override
    public CustomParameter saveCustomParameter(CustomParameter customParameter) {
      return customParameter;
    }
  }

  /** Echoes the user back from {@code getUser} and accepts {@code saveLastLogin}; nothing else is reached. */
  private static final class FakeUserManager implements UserManager {

    @Override
    public User getActiveSuperAdminUserByUsernameOccurrence() {
      return null;
    }

    @Override
    public List<String> getCenterPermission(int userId, String crp) {
      return new ArrayList<String>();
    }

    @Override
    public List<String> getPermission(int userId, String crp) {
      return new ArrayList<String>();
    }

    @Override
    public User getUser(Long userId) {
      User user = new User();
      user.setId(userId);
      user.setEmail("jane.smith@cgiar.org");
      return user;
    }

    @Override
    public User getUserByEmail(String email) {
      return null;
    }

    @Override
    public User getUserByUsername(String username) {
      return null;
    }

    @Override
    public User login(String email, String password) {
      return null;
    }

    @Override
    public boolean saveLastLogin(User user) {
      return true;
    }

    @Override
    public User saveUser(User user) {
      return user;
    }

    @Override
    public List<User> searchUser(String searchValue) {
      return new ArrayList<User>();
    }
  }

  /**
   * Unused on this path (T11b's guard is not exercised by these tests -- see
   * {@code LoginActionCgiarGuardTest}); present only to satisfy the constructor CHG-COGNITO-AUTH-001-T11b
   * added to resolve {@code cognito_auth_active} through the shared {@code CognitoAuthSpecificity} resolver.
   */
  private static final class NoOpParameterManager implements ParameterManager {

    @Override
    public void deleteParameter(long parameterId) {
    }

    @Override
    public boolean existParameter(long parameterID) {
      return false;
    }

    @Override
    public List<Parameter> findAll() {
      return new ArrayList<Parameter>();
    }

    @Override
    public Parameter getParameterById(long parameterID) {
      return null;
    }

    @Override
    public Parameter getParameterByKey(String key, long globalUnitId) {
      return null;
    }

    @Override
    public Parameter saveParameter(Parameter parameter) {
      return parameter;
    }
  }

  /** Unused on this path; present only to satisfy the constructor. */
  private static final class NoOpGlobalUnitManager implements GlobalUnitManager {

    @Override
    public List<GlobalUnit> crpUsers(String email) {
      return new ArrayList<GlobalUnit>();
    }

    @Override
    public void deleteGlobalUnit(long globalUnitId) {
    }

    @Override
    public boolean existGlobalUnit(long globalUnitID) {
      return false;
    }

    @Override
    public List<GlobalUnit> findAll() {
      return new ArrayList<GlobalUnit>();
    }

    @Override
    public GlobalUnit findGlobalUnitByAcronym(String acronym) {
      return null;
    }

    @Override
    public GlobalUnit findGlobalUnitBySMOCode(String smoCode) {
      return null;
    }

    @Override
    public GlobalUnit getGlobalUnitById(long globalUnitID) {
      return null;
    }

    @Override
    public GlobalUnit saveGlobalUnit(GlobalUnit globalUnit) {
      return globalUnit;
    }
  }

  /**
   * Bypasses only the infrastructure this task does not touch. {@code isVisibleTopGUList} would otherwise
   * stream {@code getCrpUsers()} off a detached entity; {@code getBaseUrl} would read an unpopulated
   * {@code APConfig}; {@code getText} needs a live Struts container. The {@code Referer} branch and the
   * routing switch -- the only things under test -- run as real production code.
   */
  private static final class TestableLoginAction extends LoginAction {

    private static final long serialVersionUID = 1L;

    TestableLoginAction(APConfig config, UserManager userManager, GlobalUnitManager crpManager,
      CrpUserManager crpUserManager, CustomParameterManager customParameterManager,
      ParameterManager parameterManager) {
      super(config, userManager, crpManager, crpUserManager, customParameterManager, parameterManager);
    }

    @Override
    public String getBaseUrl() {
      return BASE_URL;
    }

    @Override
    public String getText(String aTextName) {
      return aTextName;
    }

    @Override
    public boolean isVisibleTopGUList() {
      return false;
    }
  }
}
