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

import org.cgiar.ccafs.marlo.config.APConstants;
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
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.CognitoAssertion;
import org.cgiar.ccafs.marlo.security.CognitoAuthenticationToken;
import org.cgiar.ccafs.marlo.security.authentication.Authenticator;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * CHG-COGNITO-AUTH-001-FN-007 (<i>Logout is not undone by SSO</i>), added by {@code /akili-test}'s coverage
 * extension to close {@code test-report.md} 8 finding 3: <b>neither clause of FN-007 had any automated
 * coverage at all</b>, on either the local or the Cognito path.
 * <p>
 * <b>No production code changed for logout.</b> OQ-8 closed FN-007 by deciding MARLO must end only its own
 * session and never perform RP-initiated logout, so {@code LoginAction.logout()} was left exactly as it was.
 * That is precisely why it needed a test: an untouched requirement is still an in-scope requirement, and the
 * clause it carries ({@code AND IT MUST NOT} be possible for a subsequent page load to restore the session)
 * is a negative constraint, which {@code /akili-validate} grades as a FAIL rather than a WARN when it is
 * ungated.
 * <p>
 * <b>The harness is the real one</b>, reusing the pattern {@code ShiroRequestSessionCacheResetterTest}
 * established: a real {@link DefaultSecurityManager} over a real {@link APCustomRealm}, a real
 * {@code Subject.login(...)} with a real {@link CognitoAuthenticationToken}, and a real Shiro session. The
 * only doubles are the managers below the realm, MARLO having no mocking framework (DEC-005 is PENDING).
 * <p>
 * <b>{@code ThreadContext} discipline (T20's audit note, and the reason it is repeated here).</b> Shiro binds
 * a {@link Subject} to {@link ThreadContext} on the first {@code SecurityUtils.getSubject()} call, tied to
 * whichever security manager is active at that instant, and a later {@code setSecurityManager(...)} does
 * <b>not</b> rebind an already-created Subject. A logout suite is where that bites hardest: without
 * {@link #tearDown()}'s {@code ThreadContext.remove()} a logged-out Subject would leak into the next test
 * method and make its {@code isAuthenticated()} assertions read the previous test's state. The security
 * manager is therefore installed in {@link #setUp()} before anything touches {@code getSubject()}, and the
 * thread is stripped bare afterwards.
 */
public class LoginActionLogoutTest {

  private static final long USER_ID = 4242L;
  private static final String EMAIL = "priya.cgiar@cgiar.org";
  private static final long GLOBAL_UNIT_ID = 55L;

  private CountingUserManager userManager;
  private APCustomRealm realm;
  private TestableLoginAction action;

  private static CognitoAssertion assertion() {
    return new CognitoAssertion(EMAIL, EMAIL, "priyac", Instant.now());
  }

  private static GlobalUnit globalUnit() {
    GlobalUnitType type = new GlobalUnitType();
    type.setId(Long.valueOf(1L));
    GlobalUnit unit = new GlobalUnit();
    unit.setId(Long.valueOf(GLOBAL_UNIT_ID));
    unit.setAcronym("TESTCRP");
    unit.setGlobalUnitType(type);
    return unit;
  }

  private static User cgiarUser() {
    User user = new User();
    user.setId(Long.valueOf(USER_ID));
    user.setEmail(EMAIL);
    user.setCgiarUser(true);
    user.setActive(true);
    // doGetAuthorizationInfo iterates this unconditionally; an unset Set NPEs before anything is measured.
    user.setUserRoles(new HashSet<UserRole>());
    return user;
  }

  @Before
  public void setUp() {
    this.userManager = new CountingUserManager();
    this.realm =
      new APCustomRealm(new ExplodingAuthenticator(), new ExplodingAuthenticator(), this.userManager, new APConfig());
    SecurityUtils.setSecurityManager(new DefaultSecurityManager(this.realm));

    this.action = new TestableLoginAction(new APConfig(), this.userManager, new NoOpGlobalUnitManager(),
      new NoOpCrpUserManager(), new NoOpCustomParameterManager(), new NoOpParameterManager());
    this.action.setSession(new HashMap<String, Object>());
  }

  @After
  public void tearDown() {
    SecurityUtils.setSecurityManager(null);
    ThreadContext.remove();
    ActionContext.clear();
  }

  /** Signs in through the real realm and returns the Shiro session the login established. */
  private Session signIn() {
    Subject subject = SecurityUtils.getSubject();
    subject.login(new CognitoAuthenticationToken(assertion(), Long.valueOf(USER_ID)));
    assertTrue("the fixture is broken if the login itself did not authenticate", subject.isAuthenticated());

    Session shiroSession = subject.getSession();
    shiroSession.setAttribute(APConstants.SESSION_USER, cgiarUser());
    shiroSession.setAttribute(APConstants.SESSION_CRP, globalUnit());
    this.action.getSession().put(APConstants.SESSION_USER, cgiarUser());
    this.action.getSession().put(APConstants.SESSION_CRP, globalUnit());
    return shiroSession;
  }

  /**
   * FN-007 S13, first clause: <i>the Shiro session <b>MUST</b> be cleared and the cached authorization info
   * invalidated</i>.
   * <p>
   * <b>The cache half is asserted by mechanism, not by presence.</b> An assertion that
   * {@code clearCachedAuthorizationInfo} was reached would certify nothing -- MARLO's own call to it in
   * {@code logout()} passes {@code SecurityUtils.getSubject().getPrincipals()} <i>after</i>
   * {@code Subject.logout()} has already nulled them, so it is a no-op that returns on
   * {@code AuthorizingRealm}'s null guard. What actually discharges the requirement is Shiro itself:
   * {@code DefaultSecurityManager.logout} hands the still-live principals to
   * {@code ModularRealmAuthenticator.onLogout}, which calls {@code onLogout} on every {@code LogoutAware}
   * realm, which clears that realm's authorization cache. This test proves the <b>effect</b> of that chain by
   * counting how many times the authorization lookup reaches {@link CountingUserManager}: twice before logout
   * is once (the cache served the second read), and a third read after logout must reach the manager again.
   * <p>
   * <b>Guarding the guard.</b> The mid-test assertion that the second pre-logout read did <i>not</i> reach the
   * manager is load-bearing: if authorization caching were ever off, every read would reach the manager and
   * the final assertion would pass while proving nothing about invalidation.
   */
  @Test
  public void logoutClearsTheShiroSessionAndInvalidatesTheCachedAuthorizationInfo() {
    Session shiroSession = this.signIn();
    PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
    assertNotNull(principals);

    this.realm.getAuthorizationInfo(principals);
    assertEquals("the first authorization read must reach the manager", 1, this.userManager.permissionLookupCount);
    this.realm.getAuthorizationInfo(principals);
    assertEquals("guards the guard: with no live authorization cache, the post-logout assertion below would "
      + "pass without invalidating anything", 1, this.userManager.permissionLookupCount);

    String result = this.action.logout();

    assertEquals(Action.SUCCESS, result);
    // (a) the Shiro session is GONE, not merely emptied. Asserted before anything below re-creates one.
    assertNull("FN-007: logout must leave no Shiro session bound to the subject",
      SecurityUtils.getSubject().getSession(false));
    try {
      shiroSession.getAttribute(APConstants.SESSION_USER);
      fail("FN-007: the session logout stopped must be unusable afterwards, not merely emptied");
    } catch (InvalidSessionException expected) {
      assertNotNull(expected);
    }
    // (b) MARLO's own session map -- what the FTL layer and every action read -- was cleared too.
    assertFalse("FN-007: the application session must not still carry SESSION_USER",
      this.action.getSession().containsKey(APConstants.SESSION_USER));
    assertTrue("logout() clears the whole session map, not just the user", this.action.getSession().isEmpty());
    // (c) the cached AuthorizationInfo was invalidated: the next read must reach the manager again.
    this.realm.getAuthorizationInfo(principals);
    assertEquals("FN-007: the cached AuthorizationInfo must be invalidated by logout -- a stale entry would "
      + "let a re-established subject inherit the roles and permissions of the session that just ended", 2,
      this.userManager.permissionLookupCount);
  }

  /**
   * FN-007 S13, the {@code AND IT MUST NOT} clause: <i>it must not be possible for a subsequent page load to
   * restore the session without an explicit new sign-in action</i>.
   * <p>
   * <b>What "a subsequent page load" is modelled as.</b> Nothing of the logging-out request's thread survives
   * into the next one except the session id the browser's cookie carries, so this test strips the thread
   * ({@code ThreadContext.remove()}) and rebuilds a {@link Subject} from exactly that id -- which is what
   * Shiro's own web filter does on every request. Asserting only {@code isAuthenticated()} on the
   * <i>same</i> Subject instance would be much weaker: that instance was mutated in place by {@code logout()}
   * and could read false while the session behind it was still perfectly resumable by anyone holding the
   * cookie. Both are asserted here, in that order.
   */
  @Test
  public void afterLogoutALaterRequestFindsNoAuthenticatedSubject() {
    Session shiroSession = this.signIn();
    Serializable sessionId = shiroSession.getId();
    assertNotNull("the fixture is broken if the login established no session id", sessionId);

    // Positive control, and the reason the assertions after logout mean anything. Rebuilding a Subject from
    // the session id is exactly what the assertions below do; run BEFORE logout it must find an
    // authenticated subject. Without this, a Subject.Builder that could never authenticate anything -- a
    // wrong key, a session id Shiro cannot resolve, a harness quirk -- would make "not authenticated after
    // logout" pass for a reason that has nothing to do with logout.
    Subject beforeLogout =
      new Subject.Builder(SecurityUtils.getSecurityManager()).sessionId(sessionId).buildSubject();
    assertTrue("control: a request presenting this session id must find an authenticated subject WHILE the "
      + "session is live -- otherwise the post-logout assertions below prove nothing", beforeLogout
        .isAuthenticated());
    assertEquals("control: ...and it must recover the principal too", Long.valueOf(USER_ID),
      beforeLogout.getPrincipal());

    this.action.logout();

    // The logging-out request itself.
    assertFalse("FN-007: the subject must not remain authenticated after logout",
      SecurityUtils.getSubject().isAuthenticated());
    assertNull("FN-007: no principal may survive logout", SecurityUtils.getSubject().getPrincipal());
    assertNull("FN-007: the session must not still carry SESSION_USER -- there must be no session at all",
      SecurityUtils.getSubject().getSession(false));

    // A LATER request: a bare thread, and only the cookie's session id to go on.
    ThreadContext.remove();
    Session resumed = null;
    try {
      resumed = SecurityUtils.getSecurityManager().getSession(new DefaultSessionKey(sessionId));
    } catch (InvalidSessionException expected) {
      resumed = null;
    }
    assertNull("FN-007: the stopped session must not be resumable by its id", resumed);

    Subject laterRequest =
      new Subject.Builder(SecurityUtils.getSecurityManager()).sessionId(sessionId).buildSubject();
    assertFalse("FN-007 AND IT MUST NOT: a subsequent page load presenting the same session id must not "
      + "find an authenticated subject", laterRequest.isAuthenticated());
    assertNull("FN-007 AND IT MUST NOT: nor may it recover the principal", laterRequest.getPrincipal());
    assertNull("FN-007 AND IT MUST NOT: nor may it restore the session", laterRequest.getSession(false));
    Session freshSession = laterRequest.getSession();
    assertNotNull("a later request may of course start a NEW anonymous session", freshSession);
    assertNull("...but that session must be empty -- nothing of the ended one may reappear in it",
      freshSession.getAttribute(APConstants.SESSION_USER));
  }

  /** Throws on every call: this suite authenticates through Cognito, so no LDAP or DB bind may occur. */
  private static final class ExplodingAuthenticator implements Authenticator {

    @Override
    public Map<String, Object> authenticate(String email, String password) {
      throw new AssertionError("no directory bind may happen on the Cognito path");
    }
  }

  /**
   * Resolves the one user this suite signs in as and <b>counts</b> the permission lookups
   * {@code APCustomRealm.doGetAuthorizationInfo} makes. The count is the instrument: it is how this suite
   * tells "the authorization cache served that read" from "the realm recomputed it", which is the only
   * observable difference between a cache that was invalidated at logout and one that was not.
   */
  private static final class CountingUserManager implements UserManager {

    private int permissionLookupCount;

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
      this.permissionLookupCount++;
      return new ArrayList<String>();
    }

    @Override
    public User getUser(Long userId) {
      return cgiarUser();
    }

    @Override
    public User getUserByEmail(String email) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User getUserByUsername(String username) {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User login(String email, String password) {
      throw new AssertionError("no local login may happen on the Cognito path");
    }

    @Override
    public boolean saveLastLogin(User user) {
      throw new UnsupportedOperationException("not needed by this suite");
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

  /** Unused by {@code logout()}; present only to satisfy the constructor. */
  private static final class NoOpGlobalUnitManager implements GlobalUnitManager {

    @Override
    public List<GlobalUnit> crpUsers(String email) {
      throw new UnsupportedOperationException("not needed by this suite");
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

  /** Unused by {@code logout()}; present only to satisfy the constructor. */
  private static final class NoOpCrpUserManager implements CrpUserManager {

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

  /** Unused by {@code logout()}; present only to satisfy the constructor. */
  private static final class NoOpCustomParameterManager implements CustomParameterManager {

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
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public CustomParameter saveCustomParameter(CustomParameter customParameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Unused by {@code logout()}; present only to satisfy the constructor. */
  private static final class NoOpParameterManager implements ParameterManager {

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
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public Parameter saveParameter(Parameter parameter) {
      throw new UnsupportedOperationException("not needed by this suite");
    }
  }

  /** Overrides only what needs a live Struts/servlet container, matching every sibling test action. */
  private static final class TestableLoginAction extends LoginAction {

    private static final long serialVersionUID = 1L;

    TestableLoginAction(APConfig config, UserManager userManager, GlobalUnitManager crpManager,
      CrpUserManager crpUserManager, CustomParameterManager customParameterManager,
      ParameterManager parameterManager) {
      super(config, userManager, crpManager, crpUserManager, customParameterManager, parameterManager);
    }

    @Override
    public String getBaseUrl() {
      return "https://marlo.example.org";
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
