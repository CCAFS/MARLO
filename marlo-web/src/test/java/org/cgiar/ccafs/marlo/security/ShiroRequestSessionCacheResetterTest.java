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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.AuthenticatingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * CHG-COGNITO-AUTH-001-T16: the five tests {@code tasks.md} requires for
 * {@link ShiroRequestSessionCacheResetter}, run against the real {@code shiro-web} classes -- never a
 * hand-written double standing in for {@link ShiroHttpServletRequest} itself, which is precisely the
 * substitution {@code tasks.md}'s <i>Not evidence when</i> clause names as the reason V-2 reached
 * production undetected (see {@code CognitoCallbackActionTest}'s {@code InvalidatedSessionMap}, which
 * exercises {@code SessionMap}, not {@code ShiroHttpServletRequest}).
 * <p>
 * <b>Why a plain, non-web {@link DefaultSecurityManager} is enough here.</b> {@code ShiroHttpServletRequest}'s
 * own {@code getSubject()} method (verified in bytecode -- see {@link ShiroRequestSessionCacheResetter}'s
 * class javadoc) is a bare call to {@code SecurityUtils.getSubject()}: it never asks the security manager
 * whether it is web-aware. Any {@code SecurityManager} bound to {@code ThreadContext} exercises the exact
 * caching bytecode this suite targets, matching the same setup {@code CognitoCallbackActionTest} and
 * {@code APCustomRealmDispatchTest} already use for the same reason.
 */
public class ShiroRequestSessionCacheResetterTest {

  @Before
  public void setUp() {
    org.apache.shiro.SecurityUtils.setSecurityManager(new DefaultSecurityManager(new AlwaysAuthenticatesRealm()));
  }

  @After
  public void tearDown() {
    org.apache.shiro.SecurityUtils.setSecurityManager(null);
    org.apache.shiro.util.ThreadContext.remove();
  }

  private static ShiroHttpServletRequest newShiroRequest() {
    return new ShiroHttpServletRequest(newFakeHttpServletRequest(), null, false);
  }

  /**
   * A minimal, real {@link HttpServletRequest} -- built as a JDK dynamic proxy over an attribute map,
   * matching the technique {@code LoginActionFinishLoginTest#setReferer} already uses in this repository for
   * exactly the same reason: MARLO has no mocking framework (DEC-005 is PENDING), and hand-implementing every
   * one of the ~47 {@code ServletRequest}/{@code HttpServletRequest} methods individually would bury the one
   * behaviour this suite actually needs -- a working attribute store -- in boilerplate.
   * <p>
   * <b>Why the attribute store must be real, not stubbed.</b> {@code ShiroHttpServletRequest.getSession(true)}
   * calls {@code this.setAttribute(REFERENCED_SESSION_IS_NEW, TRUE)} on a freshly-created session (bytecode
   * confirmed), which {@code HttpServletRequestWrapper} delegates straight to the wrapped request. A proxy
   * that threw on {@code setAttribute} would make the very act of "forcing it to cache" throw.
   */
  private static HttpServletRequest newFakeHttpServletRequest() {
    final Map<String, Object> attributes = new HashMap<>();
    InvocationHandler handler = new InvocationHandler() {

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) {
        String name = method.getName();
        if ("setAttribute".equals(name)) {
          attributes.put((String) args[0], args[1]);
          return null;
        }
        if ("getAttribute".equals(name)) {
          return attributes.get(args[0]);
        }
        if ("removeAttribute".equals(name)) {
          attributes.remove(args[0]);
          return null;
        }
        if ("equals".equals(name)) {
          return Boolean.valueOf(proxy == args[0]);
        }
        if ("hashCode".equals(name)) {
          return Integer.valueOf(System.identityHashCode(proxy));
        }
        if ("toString".equals(name)) {
          return "FakeHttpServletRequest";
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
        // Every other ServletRequest/HttpServletRequest method (getSession(boolean) included -- this
        // request's own getSession is never reached with httpSessions=false, see the bytecode analysis in
        // ShiroRequestSessionCacheResetter's javadoc) is not needed by this suite.
        return null;
      }
    };
    return (HttpServletRequest) Proxy.newProxyInstance(ShiroRequestSessionCacheResetterTest.class.getClassLoader(),
      new Class<?>[] {HttpServletRequest.class}, handler);
  }

  /**
   * Test 1 (compatibility). Pins the declaring class, field name, and field type
   * {@link ShiroRequestSessionCacheResetter} depends on in shiro-web 1.13.0. A future Shiro upgrade that
   * renames, retypes, or relocates this field must turn this test red -- never leave the workaround silently
   * disabled. This is the condition {@code tasks.md} T16 requires for the workaround to be "managed", not
   * merely fragile.
   */
  @Test
  public void theCachedSessionFieldStillHasTheExactShapeThisHelperDependsOn() throws Exception {
    Field cachedSessionField = ShiroHttpServletRequest.class
      .getDeclaredField(ShiroRequestSessionCacheResetter.CACHED_SESSION_FIELD_NAME);

    assertEquals("declaring class must still be ShiroHttpServletRequest itself, not a superclass",
      ShiroHttpServletRequest.class, cachedSessionField.getDeclaringClass());
    assertEquals("session", cachedSessionField.getName());
    assertEquals(HttpSession.class, cachedSessionField.getType());
    assertTrue("expected the field to remain protected (accessible only via setAccessible), per shiro-web 1.13.0",
      Modifier.isProtected(cachedSessionField.getModifiers()));
    assertFalse("a public field would need no reflective workaround at all",
      Modifier.isPublic(cachedSessionField.getModifiers()));
  }

  /**
   * Test 2 (real-mechanism). A real {@link ShiroHttpServletRequest}, forced to cache its wrapper of the
   * pre-auth session, that session stopped, {@code Subject.login(...)} establishing a new one, the helper
   * invoked, and {@code getSession(false)} resolving -- and letting a write through -- the NEW session
   * without throwing.
   * <p>
   * <b>Why the assertion writes through the resolved session rather than just checking it is non-null.</b> A
   * stale wrapper is non-null too -- that is the entire defect. Only a successful
   * {@code setAttribute}/{@code getAttribute} round trip, and a session id that differs from the stopped
   * one, distinguish "the correct new session" from "the same dead wrapper returned again".
   */
  @Test
  public void clearingAfterLoginResolvesTheNewSessionAndAcceptsAWrite() throws Exception {
    ShiroHttpServletRequest request = newShiroRequest();

    HttpSession forcedCache = request.getSession(true);
    assertNotNull("forcing getSession(true) must cache a wrapper", forcedCache);
    String preAuthId = org.apache.shiro.SecurityUtils.getSubject().getSession().getId().toString();

    org.apache.shiro.SecurityUtils.getSubject().getSession().stop();
    org.apache.shiro.SecurityUtils.getSubject().login(new UsernamePasswordToken("user-t16-2", "irrelevant"));

    ShiroRequestSessionCacheResetter.clearCachedSession(request);

    HttpSession resolved = request.getSession(false);
    assertNotNull("a session must still be resolvable after the cache is cleared", resolved);
    try {
      resolved.setAttribute("t16Probe", "value");
    } catch (IllegalStateException e) {
      fail("the rebuilt session must accept a write, not bind the stopped one: " + e);
    }
    assertEquals("value", resolved.getAttribute("t16Probe"));

    String newId = org.apache.shiro.SecurityUtils.getSubject().getSession().getId().toString();
    assertNotEquals("the resolved session must be the NEW one, not the stopped pre-auth session", preAuthId, newId);
  }

  /**
   * Test 3 (the test that bites). Identical setup to test 2, with the helper never invoked: the request must
   * still return a non-null session from {@code getSession(false)} -- it is the stale, cached wrapper -- and
   * a write through it must throw, reproducing V-2's exact failure shape:
   * {@code ShiroHttpSession.setAttribute} catches Shiro's {@code InvalidSessionException} (the superclass of
   * the {@code UnknownSessionException} the live pool actually raised -- {@code execution.md} 28) and
   * rethrows it as {@code IllegalStateException}, matching this same class's precedent for the identical
   * wrapper class recorded in {@code CognitoCallbackAction}'s own javadoc (T09's audit, Issue 1).
   * <p>
   * Without this test, test 2 proves nothing: a helper that silently did nothing at all would also leave
   * {@code getSession(false)} non-null (still the stale wrapper) and could only be told apart from a working
   * one by the write actually being accepted or rejected.
   */
  @Test
  public void withoutTheHelperTheStaleSessionRejectsAWrite() throws Exception {
    ShiroHttpServletRequest request = newShiroRequest();

    request.getSession(true);
    org.apache.shiro.SecurityUtils.getSubject().getSession().stop();
    org.apache.shiro.SecurityUtils.getSubject().login(new UsernamePasswordToken("user-t16-3", "irrelevant"));

    // The helper is deliberately never called on this path.
    HttpSession stale = request.getSession(false);
    assertNotNull("the defect returns the stale wrapper, not null -- getSession(false) must not itself throw",
      stale);

    try {
      stale.setAttribute("t16Probe", "value");
      fail("a write through the stale, stopped session must throw -- this is V-2 itself");
    } catch (IllegalStateException e) {
      assertTrue("the cause must be Shiro's own invalid/unknown-session signal, matching the live pool's "
        + "UnknownSessionException (execution.md 28), not an unrelated failure",
        e.getCause() instanceof org.apache.shiro.session.InvalidSessionException);
    }
  }

  /**
   * Test 5 (fail-soft). A request that is not, and does not wrap, a {@link ShiroHttpServletRequest} at all
   * must be left alone -- no exception, no side effect visible to the caller.
   */
  @Test
  public void aNonShiroRequestIsLeftUntouched() {
    ServletRequest plain = newFakeHttpServletRequest();
    try {
      ShiroRequestSessionCacheResetter.clearCachedSession(plain);
    } catch (RuntimeException e) {
      fail("a non-Shiro request must never throw: " + e);
    }
  }

  /**
   * Reads the private cache field directly via reflection -- an assertion tool, not the production path
   * (which never reads the field back). Used by both new tests below to prove the field's actual state
   * rather than inferring it from {@code getSession(...)}'s behaviour.
   */
  private static Object readCachedSessionField(ShiroHttpServletRequest request) throws Exception {
    String fieldName = ShiroRequestSessionCacheResetter.CACHED_SESSION_FIELD_NAME;
    Field field = ShiroHttpServletRequest.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return field.get(request);
  }

  /**
   * Review finding F2. The two-argument public {@code clearCachedSession(ServletRequest)} can never reach
   * the fail-soft catch on a real {@link ShiroHttpServletRequest} -- the field name it passes always exists.
   * Test 5 above ({@code aNonShiroRequestIsLeftUntouched}) does not cover the catch either: it returns via
   * the earlier {@code instanceof} guard, so deleting the catch block entirely would leave that test green.
   * This test drives the package-private, field-name-parameterized overload with a name that does not exist
   * on {@link ShiroHttpServletRequest}, so the reflective lookup itself fails and the catch block -- which
   * is what actually implements condition 4 of {@code tasks.md} T16 -- is the code path under test.
   * <p>
   * <b>Asserts the mechanism, not merely the absence of a throw</b> (the auditor's finding, verbatim): a
   * Logback {@link ListAppender} is attached to this class's own logger so the warning the catch block logs
   * is captured and inspected, proving the fail-soft branch was actually taken rather than some other code
   * path that happens not to throw either.
   */
  @Test
  public void aMissingFieldIsCaughtAndLogsAWarningInsteadOfThrowing() throws Exception {
    ShiroHttpServletRequest request = newShiroRequest();
    request.getSession(true);

    ch.qos.logback.classic.Logger logbackLogger =
      (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ShiroRequestSessionCacheResetter.class);
    ListAppender<ILoggingEvent> appender = new ListAppender<>();
    appender.start();
    logbackLogger.addAppender(appender);
    try {
      ShiroRequestSessionCacheResetter.clearCachedSession(request, "fieldThatDoesNotExistInThisShiroVersion");
    } catch (RuntimeException e) {
      fail("a missing field must be caught and logged, never thrown from the success path of a login: " + e);
    } finally {
      logbackLogger.detachAppender(appender);
    }

    boolean warningLogged = false;
    for (ILoggingEvent event : appender.list) {
      if (event.getFormattedMessage().contains("Could not clear ShiroHttpServletRequest's cached session")) {
        warningLogged = true;
        break;
      }
    }
    assertTrue("the fail-soft catch must log a warning naming the compatibility helper, not fail silently",
      warningLogged);
  }

  /**
   * Review finding F4. Termination, the null-chain case, and later-filter wrapping were verified correct by
   * reading the walk in {@link ShiroRequestSessionCacheResetter#clearCachedSession(ServletRequest)}, but
   * nothing exercised the wrapped case -- every other test in this suite passes a bare
   * {@link ShiroHttpServletRequest}. This test wraps a real one in a plain {@link ServletRequestWrapper}
   * (standing in for a later filter -- multipart handling, character encoding, Struts' own wrapper -- that
   * legitimately wraps the Shiro-installed request further down the chain) and calls the public entry point
   * on the WRAPPER, never on the inner request directly.
   * <p>
   * Asserts the field was actually nulled on the inner {@link ShiroHttpServletRequest} (via
   * {@link #readCachedSessionField(ShiroHttpServletRequest)}), which proves the walk unwraps correctly AND
   * gives {@code tasks.md} T16's fail-soft condition 4 a direct mechanism assertion, per the review finding.
   */
  @Test
  public void aWrappedShiroRequestIsFoundAndItsCachedSessionIsActuallyNulled() throws Exception {
    ShiroHttpServletRequest innerRequest = newShiroRequest();
    innerRequest.getSession(true);
    assertNotNull("must be cached before the wrapper walk is exercised", readCachedSessionField(innerRequest));

    ServletRequestWrapper outerWrapper = new ServletRequestWrapper(innerRequest) {
    };

    ShiroRequestSessionCacheResetter.clearCachedSession(outerWrapper);

    assertNull("the walk must find the real ShiroHttpServletRequest through the wrapper and null its cache",
      readCachedSessionField(innerRequest));
  }

  /**
   * A minimal {@link AuthenticatingRealm} that accepts any {@link UsernamePasswordToken} -- this suite tests
   * the Shiro-web request/session mechanism itself, not MARLO's identity mapping (already covered by
   * {@code APCustomRealmDispatchTest} and {@code CognitoCallbackActionTest}), so a MARLO-specific realm would
   * only add unrelated dependencies.
   */
  private static final class AlwaysAuthenticatesRealm extends AuthenticatingRealm {

    AlwaysAuthenticatesRealm() {
      super(new AllowAllCredentialsMatcher());
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
      PrincipalCollection principals =
        new org.apache.shiro.subject.SimplePrincipalCollection(((UsernamePasswordToken) token).getUsername(),
          this.getName());
      return new org.apache.shiro.authc.SimpleAuthenticationInfo(principals, "irrelevant");
    }
  }
}
