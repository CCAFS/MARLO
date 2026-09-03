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

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CHG-COGNITO-AUTH-001-T16: clears the stale, request-level cache of the Shiro session wrapper that survives
 * {@code Subject.login(...)} rotating the underlying session (V-2, reproduced against the live Cognito pool
 * -- see {@code execution.md} 28 and {@code tasks.md} T16).
 * <p>
 * <b>The defect this exists to work around, verified in {@code shiro-web-1.13.0.jar}'s bytecode, not
 * inferred.</b> {@link ShiroHttpServletRequest} caches the {@link javax.servlet.http.HttpSession} wrapper it
 * builds in a {@code protected HttpSession session} field. Its {@code getSession(boolean)} reads that field
 * and, whenever it is non-null <b>and</b> the current {@link org.apache.shiro.subject.Subject} reports that
 * some session currently exists, returns the cached field <b>without checking whether that cached wrapper's
 * underlying session is the SAME session the Subject currently holds</b> -- it only rebuilds when the field
 * is {@code null}. {@code Subject.login(...)} authenticates onto a brand-new native session (Shiro's
 * {@code DefaultSubjectDAO} eagerly persists the principal collection into it), but nothing tells the
 * servlet request to drop its own, separately-cached wrapper of the session that existed before login. Any
 * component that reaches the request afterwards -- Struts' CSP interceptor is the first observed consumer,
 * via {@code DefaultCspSettings.addCspHeadersWithSession} -- gets a wrapper bound to a session that
 * {@code session.stop()} already destroyed, and a write through it throws
 * {@link org.apache.shiro.session.UnknownSessionException}.
 * <p>
 * <b>Why this is fixed with reflection, and why every supported alternative was checked and refused</b> --
 * this is a narrowly scoped compatibility workaround, approved by the user 2026-09-03, not a first resort:
 * <ul>
 * <li>{@link org.apache.shiro.subject.Subject} exposes {@code login}, {@code logout}, and {@code getSession}
 * only -- no method that renews or invalidates a caller's own cached view of the session.</li>
 * <li>{@code org.apache.shiro.web.util.WebUtils} exposes nothing that touches
 * {@link ShiroHttpServletRequest}'s cached wrapper.</li>
 * <li>{@code org.apache.shiro.web.mgt.DefaultWebSecurityManager} offers no hook for this either.</li>
 * <li>{@code Subject.logout()} only sets the {@code IDENTITY_REMOVED_KEY} <b>request attribute</b> --
 * {@code ShiroHttpServletRequest.getSession(boolean)}'s bytecode has zero references to that key, so it is
 * not consulted by the method that would need to honour it.</li>
 * <li>{@code HttpServletRequest.changeSessionId()} is a container-session operation; Shiro's native sessions
 * (wired by {@code MarloShiroConfiguration}'s {@code DefaultWebSessionManager}) do not go through it.</li>
 * </ul>
 * The field is {@code protected} with no accessor and no setter, which leaves reflection as the only
 * remaining path -- confined to this one class, exactly the shape {@code tasks.md} T16 requires.
 * <p>
 * <b>Managed, not merely fragile.</b> {@code ShiroRequestSessionCacheResetterTest}'s compatibility test pins
 * the declaring class, field name, and field type this class depends on against {@code shiro-web 1.13.0}. A
 * future Shiro upgrade that renames, retypes, or relocates the field turns that test red -- never a silently
 * disabled workaround. At runtime, a field that has moved, been renamed, or become inaccessible (a sealed
 * jar, a security manager, a JPMS module boundary) is handled by {@link #clearCachedSession(ServletRequest)}
 * failing soft: it logs a warning and returns, leaving today's (already-present) staleness in place rather
 * than risking a worse failure mode.
 */
public final class ShiroRequestSessionCacheResetter {

  private static final Logger LOG = LoggerFactory.getLogger(ShiroRequestSessionCacheResetter.class);

  /**
   * The exact field {@link ShiroHttpServletRequest} (shiro-web 1.13.0) uses to cache its
   * {@code ShiroHttpSession} wrapper. Named as a constant so the reflective lookup below and the
   * compatibility test that pins it read the same literal.
   */
  static final String CACHED_SESSION_FIELD_NAME = "session";

  private ShiroRequestSessionCacheResetter() {
  }

  /**
   * Clears the cached session wrapper on the {@link ShiroHttpServletRequest} found by unwrapping
   * {@code request}, so the next {@code getSession(...)} call on it rebuilds against whatever session the
   * current {@link org.apache.shiro.subject.Subject} actually holds, instead of returning whatever it had
   * cached before.
   * <p>
   * <b>Call this only after {@code Subject.login(...)} has established the new session</b> -- the whole
   * point is to discard a cache that is stale relative to the session that exists <i>right now</i>; calling
   * it earlier clears a cache that a later access could still repopulate with the wrong session. {@code
   * tasks.md} T16 requires this ordering to be provably load-bearing: moving this call earlier must redden
   * the mechanism test built against the real {@link ShiroHttpServletRequest}.
   * <p>
   * <b>Walks the request wrapper chain rather than assuming {@code request} is itself a
   * {@link ShiroHttpServletRequest}</b> -- a filter running after Shiro's in the chain (multipart handling,
   * character encoding, Struts' own wrappers) may have wrapped it further by the time an action sees it. A
   * request that is not, and does not wrap, a {@link ShiroHttpServletRequest} at all is left untouched: fail
   * soft, never throw, per {@code tasks.md} T16's fifth test.
   *
   * @param request the request an action or interceptor currently holds; may or may not be, or wrap, a
   *        {@link ShiroHttpServletRequest}
   */
  public static void clearCachedSession(ServletRequest request) {
    clearCachedSession(request, CACHED_SESSION_FIELD_NAME);
  }

  /**
   * The real implementation, parameterized on the field name so a test can drive the fail-soft catch below
   * with a name that does not exist -- see {@code ShiroRequestSessionCacheResetterTest}'s coverage of
   * condition 4, which the two-argument public entry point cannot reach on its own (review finding F2).
   *
   * @param request the request an action or interceptor currently holds; may or may not be, or wrap, a
   *        {@link ShiroHttpServletRequest}
   * @param fieldName the name of the field to clear on the resolved {@link ShiroHttpServletRequest} --
   *        always {@link #CACHED_SESSION_FIELD_NAME} in production
   */
  static void clearCachedSession(ServletRequest request, String fieldName) {
    ServletRequest current = request;
    while (current instanceof ServletRequestWrapper && !(current instanceof ShiroHttpServletRequest)) {
      current = ((ServletRequestWrapper) current).getRequest();
    }
    if (!(current instanceof ShiroHttpServletRequest)) {
      // Fail soft: nothing to clear (a local, non-Cognito login path may never be Shiro-wrapped in a test,
      // and a future filter reordering must degrade to today's behaviour, not a new failure).
      return;
    }
    try {
      Field cachedSessionField = ShiroHttpServletRequest.class.getDeclaredField(fieldName);
      cachedSessionField.setAccessible(true);
      cachedSessionField.set(current, null);
    } catch (ReflectiveOperationException | RuntimeException e) {
      // Fail soft (tasks.md T16): the field is absent, renamed, or inaccessible -- almost certainly a Shiro
      // version this class no longer matches. Degrading to today's (already-present) staleness is
      // acceptable; throwing from here, on the success path of a login, is not. The broad RuntimeException
      // arm is deliberate (review finding F1): setAccessible(true) throws the unchecked
      // java.lang.reflect.InaccessibleObjectException on a JPMS module boundary -- one of the three
      // triggers this class's own javadoc and tasks.md T16 name -- and a narrower catch would let exactly
      // that named trigger escape this method uncaught, on the success path of a corporate login.
      LOG.warn("Could not clear ShiroHttpServletRequest's cached session -- this compatibility helper targets "
        + "shiro-web 1.13.0 and may need updating for the version on the classpath: {}", e.toString());
    }
  }
}
