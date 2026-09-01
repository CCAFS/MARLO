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

package org.cgiar.ccafs.marlo.security;

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.authentication.Authenticator;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Covers CHG-COGNITO-AUTH-001-T06: the {@code instanceof CognitoAuthenticationToken} guard inserted
 * <b>above</b> the unconditional {@code UsernamePasswordToken} cast in
 * {@code APCustomRealm.doGetAuthenticationInfo} (design.md 2.1, DD-1).
 * <p>
 * <b>Ordering discipline (T06's own <i>Not evidence when</i> clause).</b>
 * {@link #localUserProducesTheSameAuthenticationInfoAsBeforeTheChange()} and
 * {@link #cgiarUserStillRoutesToTheLdapBranch()} were written and run <b>against the unmodified realm</b>
 * before the guard was added -- both drive a plain {@link UsernamePasswordToken} and exercise only code
 * below the cast, so their expected values are exactly what the pre-change method produces: the outcome
 * was observed by running this suite first, not derived by reading the post-change code. See
 * {@code execution.md} for the recorded pre-change run. Because the guard only intercepts
 * {@link CognitoAuthenticationToken}, both tests keep passing, unchanged, after the edit -- that is what
 * "byte-identical before and after" means here.
 * <p>
 * {@link #cognitoTokenReturnsInfoBuiltFromTheAssertionWithNoIo()} is necessarily a <b>new</b> test: nothing
 * before this change can dispatch a {@link CognitoAuthenticationToken} at all -- it hits the unconditional
 * cast and throws {@link ClassCastException}, which is exactly T06's <i>Fails when</i> ordering proof
 * (recorded manually in {@code execution.md}, not run as a standing {@code @Test} that would fail on
 * purpose).
 * <p>
 * Every collaborator is a hand-rolled double: MARLO has no mocking framework ({@code DEC-005} is
 * {@code PENDING}), matching {@code CognitoAssertionTest} and {@code CrpUsersActionDirectoryTest}. The
 * exploding doubles below are the "no I/O" evidence for the Cognito path: if the guard ever fell through
 * to the code below the cast, one of them would throw and the test would fail loudly instead of silently
 * passing.
 */
public class APCustomRealmDispatchTest {

  /** Throws on every call. Used where a collaborator must never be reached. */
  private static final class ExplodingAuthenticator implements Authenticator {

    private final String label;

    ExplodingAuthenticator(String label) {
      this.label = label;
    }

    @Override
    public Map<String, Object> authenticate(String email, String password) {
      throw new AssertionError(this.label + " must not be called on this path");
    }
  }

  /** Records whether it was invoked and returns a fixed successful result. */
  private static final class RecordingAuthenticator implements Authenticator {

    private boolean called;
    private String seenEmailOrUsername;

    @Override
    public Map<String, Object> authenticate(String email, String password) {
      this.called = true;
      this.seenEmailOrUsername = email;
      Map<String, Object> result = new HashMap<>();
      result.put("loginStatus", true);
      result.put("loginMessage", null);
      return result;
    }
  }

  /** Throws on every call. Used to prove the Cognito dispatch path performs no user lookup at all. */
  private static final class ExplodingUserManager implements UserManager {

    @Override
    public List<String> getCenterPermission(int userId, String crp) {
      throw new AssertionError("UserManager must not be called on the Cognito path");
    }

    @Override
    public List<String> getPermission(int userId, String crp) {
      throw new AssertionError("UserManager must not be called on the Cognito path");
    }

    @Override
    public User getUser(Long userId) {
      throw new AssertionError("UserManager must not be called on the Cognito path");
    }

    @Override
    public User getUserByEmail(String email) {
      throw new AssertionError("UserManager must not be called on the Cognito path");
    }

    @Override
    public User getUserByUsername(String username) {
      throw new AssertionError("UserManager must not be called on the Cognito path");
    }

    @Override
    public User getActiveSuperAdminUserByUsernameOccurrence() {
      throw new AssertionError("UserManager must not be called on the Cognito path");
    }

    @Override
    public User login(String email, String password) {
      throw new AssertionError("UserManager must not be called on the Cognito path");
    }

    @Override
    public boolean saveLastLogin(User user) {
      throw new AssertionError("UserManager must not be called on the Cognito path");
    }

    @Override
    public User saveUser(User user) {
      throw new AssertionError("UserManager must not be called on the Cognito path");
    }

    @Override
    public List<User> searchUser(String searchValue) {
      throw new AssertionError("UserManager must not be called on the Cognito path");
    }
  }

  /** Resolves exactly the two users this suite configures; anything else is a test-authoring error. */
  private static final class StubUserManager implements UserManager {

    private final Map<String, User> byEmail = new HashMap<>();

    void register(User user) {
      this.byEmail.put(user.getEmail(), user);
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
      User user = this.byEmail.get(email);
      if (user == null) {
        throw new IllegalStateException("test authoring error: unregistered email " + email);
      }
      return user;
    }

    @Override
    public User getUserByUsername(String username) {
      throw new UnsupportedOperationException("this suite only exercises the email lookup branch");
    }

    @Override
    public User getActiveSuperAdminUserByUsernameOccurrence() {
      throw new UnsupportedOperationException("not needed by this suite");
    }

    @Override
    public User login(String email, String password) {
      throw new UnsupportedOperationException("not needed by this suite");
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

  /**
   * Overrides only {@code getCgiarNickname}, which -- unrelated to this task -- instantiates a real
   * {@code org.cgiar.ciat.auth.LDAPService} and performs a live directory lookup. That call is orthogonal
   * to what T06 changes (it lives entirely below the cast, unmodified) and cannot run in a unit test.
   * Package-private and non-final on the production class, so overriding it here is a seam, not a change
   * to production behavior -- the real class, {@code LDAPAuthenticator}, and {@code Authenticator} are
   * untouched.
   */
  private static final class RealmWithStubbedNicknameLookup extends APCustomRealm {

    private final boolean nicknameFound;

    RealmWithStubbedNicknameLookup(Authenticator dbAuthenticator, Authenticator ldapAuthenticator,
      UserManager userManager, APConfig apConfig, boolean nicknameFound) {
      super(dbAuthenticator, ldapAuthenticator, userManager, apConfig);
      this.nicknameFound = nicknameFound;
    }

    @Override
    boolean getCgiarNickname(User user) {
      return this.nicknameFound;
    }
  }

  private static User localUser() {
    User user = new User();
    user.setId(101L);
    user.setEmail("jane.local@cgiar.org");
    user.setPassword("db-hashed-secret");
    user.setCgiarUser(false);
    user.setActive(true);
    return user;
  }

  private static User cgiarUser() {
    User user = new User();
    user.setId(202L);
    user.setEmail("carlos.cgiar@cgiar.org");
    user.setUsername("ccgiar");
    // Set deliberately. Without it, `assertEquals(user.getPassword(), info.getCredentials())` below is
    // null == null -- an assertion that holds whatever the realm does with credentials on this path.
    user.setPassword("cgiar-stored-hash");
    user.setCgiarUser(true);
    user.setActive(true);
    return user;
  }

  @Before
  public void bindSecurityManager() {
    // The local and CGIAR paths (below the cast, untouched by T06) read
    // SecurityUtils.getSubject().getSession(); a plain non-web manager is enough to give them a real,
    // in-memory Shiro session with no servlet container involved.
    SecurityUtils.setSecurityManager(new DefaultSecurityManager());
  }

  @After
  public void unbindSecurityManager() {
    SecurityUtils.setSecurityManager(null);
    // Not merely tidiness. The local and CGIAR tests call SecurityUtils.getSubject(), which BINDS the
    // constructed Subject into ThreadContext -- and JUnit reuses one thread across this class and across
    // classes. Without this line the leaked Subject outlives the suite, carrying a security manager whose
    // static reference has just been nulled, and any later marlo-web test that touches getSubject() gets it.
    ThreadContext.remove();
  }

  /**
   * The enumeration in {@code supports()} is a stated design requirement, not an implementation detail:
   * {@code design.md} §2.1 records that widening it to {@code AuthenticationToken.class} would convert a
   * clean framework-level rejection of an unknown token type into a {@code ClassCastException} inside
   * {@code doGetAuthenticationInfo}'s unconditional cast.
   * <p>
   * Nothing structurally prevented that widening: every other test in this class would stay green if the
   * body became {@code return true}. This is the one-line defence.
   */
  @Test
  public void anUnknownTokenTypeIsStillRefusedByTheRealm() {
    APCustomRealm realm = new APCustomRealm(new ExplodingAuthenticator("dbAuthenticator"),
      new ExplodingAuthenticator("ldapAuthenticator"), new ExplodingUserManager(), new APConfig());

    AuthenticationToken unknown = new AuthenticationToken() {

      private static final long serialVersionUID = 1L;

      @Override
      public Object getCredentials() {
        return "irrelevant";
      }

      @Override
      public Object getPrincipal() {
        return "irrelevant";
      }
    };

    assertFalse("a third token type must be refused by supports(), not reach the unconditional cast",
      realm.supports(unknown));
    assertFalse("null must be refused too", realm.supports(null));
  }

  /**
   * <b>The end-to-end reachability test, and the reason it exists.</b>
   * <p>
   * Every other test on this path calls {@code realm.doGetAuthenticationInfo(token)} directly, which is
   * same-package access that bypasses the framework entirely. That proves the method is <i>correct</i> and
   * is structurally incapable of proving it is <i>reached</i> — and it was not: {@code AuthenticatingRealm}
   * defaults {@code authenticationTokenClass} to {@code UsernamePasswordToken}, so
   * {@code ModularRealmAuthenticator} threw {@code UnsupportedTokenException} before delegating, while this
   * suite stayed green. An independent audit found it; the suite could not.
   * <p>
   * This test drives the real {@code Subject.login(...)} through a {@code DefaultSecurityManager}, which is
   * the path {@code CognitoCallbackAction} takes at design.md §13.3 step ⑥.
   */
  @Test
  public void aCognitoTokenAuthenticatesThroughTheRealSubjectLoginPath() {
    APCustomRealm realm = new APCustomRealm(new ExplodingAuthenticator("dbAuthenticator"),
      new ExplodingAuthenticator("ldapAuthenticator"), new ExplodingUserManager(), new APConfig());
    DefaultSecurityManager securityManager = new DefaultSecurityManager(realm);

    CognitoAssertion assertion = new CognitoAssertion("sub-e2e-1", "e2e.user@cgiar.org", "e2euser",
      Instant.parse("2026-08-31T11:00:00Z"));
    Long resolvedUserId = Long.valueOf(31337L);

    // Built from the security manager directly rather than through SecurityUtils.getSubject(): JUnit reuses
    // one thread, and a Subject bound to ThreadContext by an earlier test would otherwise be reused here,
    // carrying that test's realm-less security manager. This test would then fail with "No realms have been
    // configured" and say nothing about the dispatch it exists to prove.
    Subject subject = new Subject.Builder(securityManager).buildSubject();
    subject.login(new CognitoAuthenticationToken(assertion, resolvedUserId));

    assertTrue("the subject must actually be authenticated", subject.isAuthenticated());
    Object principal = subject.getPrincipal();
    assertTrue("~20 unguarded (Long) getPrincipal() sites consume this", principal instanceof Long);
    assertEquals(resolvedUserId, principal);
  }

  /**
   * T06 test 3: a {@link CognitoAuthenticationToken} returns {@link AuthenticationInfo} built directly
   * from the carried {@link CognitoAssertion}, and touches neither {@link UserManager} nor either
   * {@link Authenticator} -- both are exploding doubles here, so any fall-through to the code below the
   * cast fails loudly. No {@code SecurityUtils} binding is exercised either: the guard returns before the
   * local path's {@code SecurityUtils.getSubject()} call is ever reached.
   */
  @Test
  public void cognitoTokenReturnsInfoBuiltFromTheAssertionWithNoIo() {
    APCustomRealm realm = new APCustomRealm(new ExplodingAuthenticator("dbAuthenticator"),
      new ExplodingAuthenticator("ldapAuthenticator"), new ExplodingUserManager(), new APConfig());

    CognitoAssertion assertion = new CognitoAssertion("sub-cognito-999", "priya.cgiar@cgiar.org", "pcgiar",
      Instant.parse("2026-08-31T09:00:00Z"));
    Long resolvedUserId = Long.valueOf(7777L);
    CognitoAuthenticationToken token = new CognitoAuthenticationToken(assertion, resolvedUserId);

    // REACHABILITY, not behavior. AuthenticatingRealm defaults authenticationTokenClass to
    // UsernamePasswordToken, and ModularRealmAuthenticator calls supports() and throws
    // UnsupportedTokenException BEFORE delegating to doGetAuthenticationInfo. Calling the method directly
    // -- as the rest of this test does -- cannot detect that. Without this line the whole Cognito dispatch
    // was dead code in production while this suite stayed green.
    assertTrue("Shiro must accept this token type, or the guard below is never reached",
      realm.supports(token));
    AuthenticationInfo info = realm.doGetAuthenticationInfo(token);

    // The principal must match the SHAPE the local path produces, not just be non-null. Every principal
    // consumer in MARLO casts to Long without a guard -- AddUserIdFilter on the very next request after
    // login, doGetAuthorizationInfo on every permission check, AbstractMarloDAO silently. Asserting the
    // type here is what stops the Cognito path from dying on its own dashboard redirect (design.md 2.1).
    Object principal = info.getPrincipals().getPrimaryPrincipal();
    assertTrue("the principal must be a Long, exactly as the local path produces", principal instanceof Long);
    assertEquals(resolvedUserId, principal);
    assertEquals("credentials carry the validated assertion (DD-5)", assertion, info.getCredentials());
  }

  /**
   * T06 test 2: a {@link UsernamePasswordToken} for a CGIAR user ({@code is_cgiar_user = 1}) still routes
   * to the LDAP branch. The realm has no Global Unit and no session at authentication time (design.md
   * 9.1), so there is no specificity flag it could even read here -- this test demonstrates that
   * structurally, by construction, rather than by toggling a flag that does not reach this class.
   * <p>
   * Written and run against the unmodified realm before the guard was added (see class javadoc); the guard
   * only intercepts {@link CognitoAuthenticationToken}, so this assertion is unaffected by the edit.
   */
  @Test
  public void cgiarUserStillRoutesToTheLdapBranch() {
    User user = cgiarUser();
    StubUserManager userManager = new StubUserManager();
    userManager.register(user);

    RecordingAuthenticator ldapAuthenticator = new RecordingAuthenticator();
    ExplodingAuthenticator dbAuthenticator = new ExplodingAuthenticator("dbAuthenticator");

    APCustomRealm realm =
      new RealmWithStubbedNicknameLookup(dbAuthenticator, ldapAuthenticator, userManager, new APConfig(), true);

    UsernamePasswordToken token = new UsernamePasswordToken(user.getEmail(), "whatever-password");

    AuthenticationInfo info = realm.doGetAuthenticationInfo(token);

    assertTrue("the LDAP authenticator must be the one invoked for a CGIAR user", ldapAuthenticator.called);
    assertEquals(user.getUsername(), ldapAuthenticator.seenEmailOrUsername);
    assertEquals(user.getId(), info.getPrincipals().getPrimaryPrincipal());
    assertEquals(user.getPassword(), info.getCredentials());
  }

  /**
   * T06 test 1: a {@link UsernamePasswordToken} for a local user ({@code is_cgiar_user = 0}) produces
   * {@link AuthenticationInfo} identical to what the pre-change realm produces. Written and run against the
   * unmodified realm first (see class javadoc and {@code execution.md} for the captured pre-change run);
   * the guard only intercepts {@link CognitoAuthenticationToken} instances, so a plain
   * {@link UsernamePasswordToken} never reaches it and this assertion is unaffected by the edit.
   */
  @Test
  public void localUserProducesTheSameAuthenticationInfoAsBeforeTheChange() {
    User user = localUser();
    StubUserManager userManager = new StubUserManager();
    userManager.register(user);

    RecordingAuthenticator dbAuthenticator = new RecordingAuthenticator();
    ExplodingAuthenticator ldapAuthenticator = new ExplodingAuthenticator("ldapAuthenticator");

    APCustomRealm realm = new APCustomRealm(dbAuthenticator, ldapAuthenticator, userManager, new APConfig());

    UsernamePasswordToken token = new UsernamePasswordToken(user.getEmail(), "whatever-password");

    AuthenticationInfo info = realm.doGetAuthenticationInfo(token);

    // The LDAP authenticator double explodes on any call; reaching this line without a thrown
    // AssertionError is itself the proof a local user never falls into the LDAP branch.
    assertTrue("the DB authenticator must be the one invoked for a local user", dbAuthenticator.called);
    assertEquals(user.getEmail(), dbAuthenticator.seenEmailOrUsername);
    // Captured pre-change: principal == user.getId(), credentials == user.getPassword(), matching
    // APCustomRealm.java's (unmodified, below-the-cast) `new SimpleAuthenticationInfo(user.getId(),
    // user.getPassword(), this.getName())`.
    assertEquals(user.getId(), info.getPrincipals().getPrimaryPrincipal());
    assertEquals(user.getPassword(), info.getCredentials());
    assertTrue(info.getPrincipals().getRealmNames().contains("APCustomRealm"));
  }
}
