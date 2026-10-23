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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.UserDAO;
import org.cgiar.ccafs.marlo.data.dao.mysql.UserMySQLDAO;
import org.cgiar.ccafs.marlo.data.manager.AuthenticationManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.impl.UserManagerImp;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.authentication.Authenticator;
import org.cgiar.ccafs.marlo.security.authentication.DBAuthenticator;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.subject.WebSubject;

/**
 * CHG-PWD-SWITCH-001-T05 (SEC-002): the credential check of {@code validateUser.do} and {@code login.do} as production
 * runs it, so a test can compare what a <b>real</b> wrong password leaves behind with what the password gate leaves.
 * <p>
 * What is real: {@link UserManagerImp#login} (its Shiro login and its caught exceptions), {@link APCustomRealm} (its
 * branch per account kind and the {@code LOGIN_MESSAGE} it leaves in the Shiro session, including the inactive and the
 * "no nickname" branches) and {@link DBAuthenticator} (which, for a local account, leaves <b>no</b> message at all).
 * What is replaced is only what needs a database or a network: the user DAO and the credential store, which answers
 * every password as wrong, and the directory, whose two seams are below.
 * <ul>
 * <li>{@code LDAPAuthenticator} binds to Active Directory, and the message of a failed bind comes from the directory
 * library, which no test can call. The stand-in answers {@link APConstants#ERROR_LOGON_FAILURE}: <b>an assumption</b>,
 * the code {@code LDAPAuthenticator} itself uses when the directory returns no connection, not a recording of what
 * the directory says. The tests that depend on it assert that choice, not the directory's behaviour.</li>
 * <li>{@link APCustomRealm#getCgiarNickname(User)} searches the directory. Its answer is the {@code nicknameFound}
 * argument; with {@code false} the realm leaves its "no nickname" message, whatever the password.</li>
 * </ul>
 * {@link #install()} binds a non-web security manager. {@link #installWeb(ServletRequest, ServletResponse)} binds the
 * web one production builds (a {@code DefaultWebSecurityManager} with its default remember-me cookie manager and
 * native web sessions) and a web subject over the given request and response, so a test can read the cookies a
 * failed login sends.
 * The shape is the one {@code APCustomRealmDispatchTest} and {@code LoginActionLogoutTest} established; it lives in
 * this package because {@code getCgiarNickname} is package-private.
 * <p>
 * {@link #install()} replaces the Shiro security manager and unbinds the current subject: call it at the start of every
 * scenario, so the Shiro session of one scenario is never read by the next. The caller owns the clean-up after the
 * test ({@code SecurityUtils.setSecurityManager(null)}, {@code ThreadContext.remove()}).
 */
public final class RealCredentialPath {

  private final Map<String, User> usersByEmail = new HashMap<String, User>();
  private final Map<String, String> emailsByUsername = new HashMap<String, String>();
  private final List<String> loginCalls = new ArrayList<String>();
  private final RecordingUserManager userManager;
  private final APCustomRealm realm;

  /**
   * @param nicknameFound the answer of the directory search a CGIAR account makes before its bind
   */
  public RealCredentialPath(final boolean nicknameFound) {
    UserDAO userDao = (UserDAO) Proxy.newProxyInstance(UserDAO.class.getClassLoader(), new Class<?>[] {UserDAO.class},
      (proxy, method, args) -> {
        if ("getUser".equals(method.getName()) && args != null && args[0] instanceof String) {
          // The real DAO trims and lower-cases what it is asked for.
          String key = UserMySQLDAO.normalizeEmail((String) args[0]);
          return key == null ? null : this.usersByEmail.get(key);
        }
        if ("getEmailByUsername".equals(method.getName())) {
          return this.emailsByUsername.get(args[0]);
        }
        Class<?> type = method.getReturnType();
        return type == boolean.class ? Boolean.FALSE : type.isPrimitive() && type != void.class ? 0 : null;
      });
    this.userManager = new RecordingUserManager(userDao, this.loginCalls);

    // The credential store answers every password as wrong; DBAuthenticator itself is the production class.
    AuthenticationManager credentials = (AuthenticationManager) Proxy.newProxyInstance(
      AuthenticationManager.class.getClassLoader(), new Class<?>[] {AuthenticationManager.class},
      (proxy, method, args) -> Boolean.FALSE);
    Authenticator directoryBind = (email, password) -> {
      Map<String, Object> answer = new HashMap<String, Object>();
      answer.put(APConstants.LOGIN_STATUS, false);
      answer.put(APConstants.LOGIN_MESSAGE, APConstants.ERROR_LOGON_FAILURE);
      return answer;
    };
    this.realm = new APCustomRealm(new DBAuthenticator(credentials), directoryBind, this.userManager, new APConfig()) {

      @Override
      boolean getCgiarNickname(User user) {
        return nicknameFound;
      }
    };
  }

  /**
   * Registers an account, found by its stored email and, when it has one, by its username.
   *
   * @param user the account; its email is required
   */
  public void add(User user) {
    this.usersByEmail.put(UserMySQLDAO.normalizeEmail(user.getEmail()), user);
    if (user.getUsername() != null) {
      this.emailsByUsername.put(user.getUsername(), user.getEmail());
    }
  }

  /**
   * Makes this path the one {@code SecurityUtils} uses, with a fresh subject and so a fresh Shiro session.
   */
  public void install() {
    ThreadContext.remove();
    SecurityUtils.setSecurityManager(new DefaultSecurityManager(this.realm));
  }

  /**
   * Makes this path the one {@code SecurityUtils} uses, as the web application builds it, with a web subject over the
   * given request and response. It is {@code MarloShiroConfiguration}'s construction: a
   * {@code DefaultWebSecurityManager} whose remember-me manager is the default {@code CookieRememberMeManager}, with
   * {@code DefaultWebSessionManager} and its {@code JSESSIONID} cookie. The one difference is that the session
   * validation scheduler stays off, so the test starts no thread.
   *
   * @param request the request the web subject belongs to
   * @param response the response the cookies are written to
   */
  public void installWeb(ServletRequest request, ServletResponse response) {
    ThreadContext.remove();
    SimpleCookie sessionIdCookie = new SimpleCookie("JSESSIONID");
    sessionIdCookie.setHttpOnly(true);
    sessionIdCookie.setPath("/");
    DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
    sessionManager.setGlobalSessionTimeout(30 * 60 * 1000L);
    sessionManager.setSessionIdCookieEnabled(true);
    sessionManager.setSessionIdCookie(sessionIdCookie);
    sessionManager.setSessionValidationSchedulerEnabled(false);
    DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
    securityManager.setRealm(this.realm);
    securityManager.setSessionManager(sessionManager);
    SecurityUtils.setSecurityManager(securityManager);
    WebSubject subject = new WebSubject.Builder(securityManager, request, response).buildWebSubject();
    ThreadContext.bind(securityManager);
    ThreadContext.bind(subject);
  }

  /**
   * @return every {@code login} call the actions made, as {@code email|password}: the record behind every "no
   *         credential call" assertion
   */
  public List<String> loginCalls() {
    return this.loginCalls;
  }

  /**
   * @return the real {@link UserManagerImp}, recording the calls to {@code login}
   */
  public UserManager userManager() {
    return this.userManager;
  }

  /** The production manager, with one addition: it records what it was asked to check before it checks it. */
  private static final class RecordingUserManager extends UserManagerImp {

    private final List<String> calls;

    RecordingUserManager(UserDAO userDao, List<String> calls) {
      super(userDao);
      this.calls = calls;
    }

    @Override
    public User login(String email, String password) {
      this.calls.add(email + "|" + password);
      return super.login(email, password);
    }
  }
}
