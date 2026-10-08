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

package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.model.User;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * verifiyCredentials() must bind the email and the password hash as query parameters. Concatenated into the HQL they
 * were an injection, and Hibernate's SQL logging (show_sql) printed both in clear on every login attempt. Runs the real
 * DAO method against a SessionFactory, Session and Query built with java.lang.reflect.Proxy (no mocking framework).
 */
public class UserMySQLDAOVerifyCredentialsTest {

  private static final String EMAIL = "jane.doe@example.org";

  private static final String PASSWORD_HASH = "5f4dcc3b5aa765d61d8327deb882cf99";

  /** Every HQL string the DAO asked the session to parse. */
  private final List<String> queries = new ArrayList<>();

  /** The parameters the DAO bound, by name. */
  private final Map<String, Object> parameters = new HashMap<>();

  /** What Query.list() returns. */
  private List<Object> rows = new ArrayList<>();

  private UserMySQLDAO dao;

  private ListAppender<ILoggingEvent> appender;

  private Logger logger;

  private Level originalLevel;

  private static Object defaultValue(Class<?> type) {
    if (type == boolean.class) {
      return Boolean.FALSE;
    }
    if (type == int.class) {
      return 0;
    }
    if (type == long.class) {
      return 0L;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  private <T> T proxy(Class<T> type, InvocationHandler handler) {
    return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type}, handler);
  }

  @Before
  public void setUp() {
    Query<?> query = this.proxy(Query.class, new InvocationHandler() {

      @Override
      public Object invoke(Object self, Method method, Object[] args) {
        if ("setParameter".equals(method.getName()) && args.length == 2 && args[0] instanceof String) {
          UserMySQLDAOVerifyCredentialsTest.this.parameters.put((String) args[0], args[1]);
          return self;
        }
        if ("list".equals(method.getName())) {
          return UserMySQLDAOVerifyCredentialsTest.this.rows;
        }
        return method.getReturnType().isInstance(self) ? self : defaultValue(method.getReturnType());
      }
    });
    Session session = this.proxy(Session.class, new InvocationHandler() {

      @Override
      public Object invoke(Object self, Method method, Object[] args) {
        if ("createQuery".equals(method.getName()) && args.length >= 1 && args[0] instanceof String) {
          UserMySQLDAOVerifyCredentialsTest.this.queries.add((String) args[0]);
          return query;
        }
        return defaultValue(method.getReturnType());
      }
    });
    SessionFactory sessionFactory = this.proxy(SessionFactory.class, new InvocationHandler() {

      @Override
      public Object invoke(Object self, Method method, Object[] args) {
        return "getCurrentSession".equals(method.getName()) ? session : defaultValue(method.getReturnType());
      }
    });
    this.dao = new UserMySQLDAO(sessionFactory);

    this.logger = (Logger) LoggerFactory.getLogger(UserMySQLDAO.class);
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
  }

  @Test
  public void theEmailAndPasswordAreBoundNeverWrittenIntoTheQuery() {
    this.rows.add(new User());

    assertTrue(this.dao.verifiyCredentials(EMAIL, PASSWORD_HASH));

    assertEquals(1, this.queries.size());
    assertFalse("the email must not be in the HQL text", this.queries.get(0).contains(EMAIL));
    assertFalse("the password hash must not be in the HQL text", this.queries.get(0).contains(PASSWORD_HASH));
    assertEquals(EMAIL, this.parameters.get("email"));
    assertEquals(PASSWORD_HASH, this.parameters.get("password"));
  }

  @Test
  public void noMatchingActiveUserIsFalseAndLogsNoEmail() {
    assertFalse(this.dao.verifiyCredentials(EMAIL, PASSWORD_HASH));

    for (ILoggingEvent event : this.appender.list) {
      assertFalse("the email must not be logged: " + event.getFormattedMessage(),
        event.getFormattedMessage().contains(EMAIL));
    }
  }

  /** An injection attempt is a value bound to a parameter: the HQL text is the same as for any other email. */
  @Test
  public void anInjectionAttemptCannotChangeTheQuery() {
    this.dao.verifiyCredentials(EMAIL, PASSWORD_HASH);
    String normalQuery = this.queries.get(0);

    String injection = "x' or '1'='1";
    this.dao.verifiyCredentials(injection, PASSWORD_HASH);

    assertEquals(normalQuery, this.queries.get(1));
    assertEquals(injection, this.parameters.get("email"));
  }
}
