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

package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.dao.UserDAO;
import org.cgiar.ccafs.marlo.data.model.User;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.util.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * The log lines of a failed login name the account by id, never by what was typed: it can be a password entered in
 * the wrong field (ENH-LOGGING-STANDARDIZATION-001). Runs the real UserManagerImp.login() against a real Shiro realm
 * holding in-memory accounts; only the UserDAO is a stub.
 */
public class UserManagerImpLoginLogTest {

  private static final String KNOWN_EMAIL = "known.user@example.org";

  private static final String KNOWN_USERNAME = "knownuser";

  private static final String LOCKED_EMAIL = "locked.user@example.org";

  /** Accounts the stub DAO knows, by email. */
  private final Map<String, User> usersByEmail = new HashMap<>();

  private final Map<String, String> emailsByUsername = new HashMap<>();

  private boolean daoFails;

  private ListAppender<ILoggingEvent> appender;

  private Logger logger;

  private Level originalLevel;

  private UserManagerImp manager;

  private static User user(long id, String email) {
    User user = new User();
    user.setId(Long.valueOf(id));
    user.setEmail(email);
    return user;
  }

  private void assertLogged(String text) {
    for (ILoggingEvent event : this.appender.list) {
      if (event.getFormattedMessage().contains(text)) {
        return;
      }
    }
    throw new AssertionError("expected a log line containing [" + text + "]");
  }

  private void assertNotLogged(String text) {
    for (ILoggingEvent event : this.appender.list) {
      assertFalse("[" + text + "] must not be logged: [" + event.getFormattedMessage() + "]",
        event.getFormattedMessage().contains(text));
    }
  }

  @Before
  public void setUp() {
    this.usersByEmail.put(KNOWN_EMAIL, user(7L, KNOWN_EMAIL));
    this.usersByEmail.put(LOCKED_EMAIL, user(8L, LOCKED_EMAIL));
    this.emailsByUsername.put(KNOWN_USERNAME, KNOWN_EMAIL);

    SimpleAccountRealm realm = new SimpleAccountRealm() {

      {
        this.addAccount(KNOWN_EMAIL, "right-password");
        this.addAccount(KNOWN_USERNAME, "right-password");
        SimpleAccount locked = new SimpleAccount(LOCKED_EMAIL, "right-password", this.getName());
        locked.setLocked(true);
        this.add(locked);
      }
    };
    SecurityUtils.setSecurityManager(new DefaultSecurityManager(realm));

    UserDAO userDAO = (UserDAO) Proxy.newProxyInstance(UserDAO.class.getClassLoader(), new Class<?>[] {UserDAO.class},
      new InvocationHandler() {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
          if (UserManagerImpLoginLogTest.this.daoFails) {
            throw new IllegalStateException("database unavailable");
          }
          if ("getUser".equals(method.getName()) && args != null && args[0] instanceof String) {
            return UserManagerImpLoginLogTest.this.usersByEmail.get(args[0]);
          }
          if ("getEmailByUsername".equals(method.getName())) {
            return UserManagerImpLoginLogTest.this.emailsByUsername.get(args[0]);
          }
          Class<?> type = method.getReturnType();
          return type == boolean.class ? Boolean.FALSE : type.isPrimitive() && type != void.class ? 0 : null;
        }
      });
    this.manager = new UserManagerImp(userDAO);

    this.logger = (Logger) LoggerFactory.getLogger(UserManagerImp.class);
    this.originalLevel = this.logger.getLevel();
    this.logger.setLevel(Level.TRACE);
    this.appender = new ListAppender<>();
    this.appender.start();
    this.logger.addAppender(this.appender);
  }

  @After
  public void tearDown() {
    this.logger.detachAppender(this.appender);
    this.logger.setLevel(this.originalLevel);
    SecurityUtils.setSecurityManager(null);
    ThreadContext.remove();
  }

  @Test
  public void aWrongPasswordNamesTheAccountById() {
    assertNull(this.manager.login(KNOWN_EMAIL, "wrong-password"));
    this.assertLogged("Login failed for user 7: the credentials were rejected.");
    this.assertNotLogged(KNOWN_EMAIL);
    this.assertNotLogged("wrong-password");
  }

  @Test
  public void aWrongPasswordByUsernameNamesTheAccountById() {
    assertNull(this.manager.login(KNOWN_USERNAME, "wrong-password"));
    this.assertLogged("Login failed for user 7: the credentials were rejected.");
    this.assertNotLogged(KNOWN_USERNAME);
  }

  @Test
  public void aLockedAccountIsNamedById() {
    assertNull(this.manager.login(LOCKED_EMAIL, "right-password"));
    this.assertLogged("Login failed for user 8: the account is locked.");
    this.assertNotLogged(LOCKED_EMAIL);
  }

  @Test
  public void anUnknownAccountLogsNothingThatWasTyped() {
    String typed = "Sup3rS3cretPw-typed-as-username";
    assertNull(this.manager.login(typed, "x"));
    this.assertLogged("Login failed: no account matches the given email or username.");
    this.assertNotLogged(typed);
  }

  /** The id lookup is for the log only: if it fails, the login still fails normally and nothing is thrown. */
  @Test
  public void aFailingIdLookupNeverChangesTheOutcome() {
    this.daoFails = true;
    assertNull(this.manager.login(KNOWN_EMAIL, "wrong-password"));
    this.assertLogged("Login failed for user null: the credentials were rejected.");
    assertTrue(this.appender.list.size() > 0);
  }
}
