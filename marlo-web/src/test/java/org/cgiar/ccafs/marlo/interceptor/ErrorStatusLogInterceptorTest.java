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

package org.cgiar.ccafs.marlo.interceptor;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.logging.LogContext;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Covers ErrorStatusLogInterceptor. The interceptor is driven directly with an in-memory invocation: the registered
 * PreResultListener is called with each result code, as DefaultActionInvocation does right before the result runs.
 */
public class ErrorStatusLogInterceptorTest {

  private final List<PreResultListener> listeners = new ArrayList<>();
  private final Map<String, Object> session = new HashMap<>();

  private ListAppender<ILoggingEvent> appender;
  private Logger logger;
  private Level previousLevel;
  private ActionInvocation invocation;

  private static User user() {
    User user = new User();
    user.setId(1082L);
    user.setFirstName("Ana");
    user.setLastName("Perez");
    user.setEmail("a.perez@example.org");
    return user;
  }

  private ActionInvocation invocation(String resultCode, Map<String, Object> invocationSession) {
    ActionContext context = ActionContext.of(new HashMap<>()).withSession(invocationSession);
    return (ActionInvocation) Proxy.newProxyInstance(this.getClass().getClassLoader(),
      new Class<?>[] {ActionInvocation.class}, (proxy, method, args) -> {
        if ("addPreResultListener".equals(method.getName())) {
          this.listeners.add((PreResultListener) args[0]);
          return null;
        }
        if ("getInvocationContext".equals(method.getName())) {
          return context;
        }
        if ("getProxy".equals(method.getName())) {
          // No proxy configuration: the interceptor must then assume the result exists, as for every MARLO page
          return null;
        }
        if ("invoke".equals(method.getName())) {
          return resultCode;
        }
        throw new AssertionError("ErrorStatusLogInterceptor must not call " + method.getName());
      });
  }

  private ILoggingEvent onlyEvent() {
    assertEquals("exactly one line must be logged", 1, this.appender.list.size());
    return this.appender.list.get(0);
  }

  private String run(String resultCode) throws Exception {
    this.invocation = this.invocation(resultCode, this.session);
    String returned = new ErrorStatusLogInterceptor().intercept(this.invocation);
    assertEquals("one listener must be registered", 1, this.listeners.size());
    this.listeners.get(0).beforeResult(this.invocation, resultCode);
    return returned;
  }

  @Before
  public void setUp() {
    LogContext.clear();
    this.logger = (Logger) LoggerFactory.getLogger(ErrorStatusLogInterceptor.class);
    this.previousLevel = this.logger.getLevel();
    this.logger.setLevel(Level.INFO);
    this.appender = new ListAppender<>();
    this.appender.start();
    this.logger.addAppender(this.appender);
  }

  @After
  public void tearDown() {
    this.logger.detachAppender(this.appender);
    this.logger.setLevel(this.previousLevel);
    LogContext.clear();
  }

  @Test
  public void aMissingRecordIsLoggedAtInfoWith404() throws Exception {
    assertEquals(BaseAction.NOT_FOUND, this.run(BaseAction.NOT_FOUND));
    assertEquals(Level.INFO, this.onlyEvent().getLevel());
    assertEquals("404", this.onlyEvent().getMDCPropertyMap().get(LogContext.STATUS_CODE));
  }

  @Test
  public void anExpiredSessionIsLoggedAtInfoWith401() throws Exception {
    this.run(BaseAction.NOT_LOGGED);
    assertEquals(Level.INFO, this.onlyEvent().getLevel());
    assertEquals("401", this.onlyEvent().getMDCPropertyMap().get(LogContext.STATUS_CODE));
  }

  @Test
  public void aNameAndEmailAreOnlyLoggedOnceTheRequestFailed() throws Exception {
    LogContext.putUserName(user());
    assertNull("the name must stay out of the context before the failure", MDC.get(LogContext.USER_NAME));
    assertNull("the email must stay out of the context before the failure", MDC.get(LogContext.USER_EMAIL));
    this.session.put(APConstants.SESSION_USER, user());
    this.run(BaseAction.NOT_AUTHORIZED);
    assertEquals("Ana Perez", this.onlyEvent().getMDCPropertyMap().get(LogContext.USER_NAME));
    assertEquals("a.perez@example.org", this.onlyEvent().getMDCPropertyMap().get(LogContext.USER_EMAIL));
  }

  @Test
  public void anEmailThatCouldForgeALineIsNotLogged() throws Exception {
    User user = user();
    user.setEmail("a.perez@example.org status_code=200");
    LogContext.putUserName(user);
    this.session.put(APConstants.SESSION_USER, user);
    this.run(BaseAction.NOT_AUTHORIZED);
    assertNull(this.onlyEvent().getMDCPropertyMap().get(LogContext.USER_EMAIL));
    assertEquals("Ana Perez", this.onlyEvent().getMDCPropertyMap().get(LogContext.USER_NAME));
  }

  @Test
  public void aDeniedUserIsLoggedAtWarnWith403() throws Exception {
    this.session.put(APConstants.SESSION_USER, user());
    this.run(BaseAction.NOT_AUTHORIZED);
    assertEquals(Level.WARN, this.onlyEvent().getLevel());
    assertEquals("403", this.onlyEvent().getMDCPropertyMap().get(LogContext.STATUS_CODE));
  }

  @Test
  public void aForbiddenRequestWithoutUserIsLoggedAtInfo() throws Exception {
    this.run(BaseAction.NOT_AUTHORIZED);
    assertEquals(Level.INFO, this.onlyEvent().getLevel());
    assertEquals("403", this.onlyEvent().getMDCPropertyMap().get(LogContext.STATUS_CODE));
  }

  @Test
  public void aMissingSessionDoesNotStopTheRequest() throws Exception {
    this.invocation = this.invocation(BaseAction.NOT_AUTHORIZED, null);
    new ErrorStatusLogInterceptor().intercept(this.invocation);
    this.listeners.get(0).beforeResult(this.invocation, BaseAction.NOT_AUTHORIZED);
    assertEquals(Level.INFO, this.onlyEvent().getLevel());
  }

  @Test
  public void aSessionThatCannotBeReadStillLogsThe403() throws Exception {
    Map<String, Object> brokenSession = new HashMap<String, Object>() {

      private static final long serialVersionUID = 1L;

      @Override
      public Object get(Object key) {
        throw new IllegalStateException("getAttribute: Session already invalidated");
      }
    };
    this.invocation = this.invocation(BaseAction.NOT_AUTHORIZED, brokenSession);
    new ErrorStatusLogInterceptor().intercept(this.invocation);
    this.listeners.get(0).beforeResult(this.invocation, BaseAction.NOT_AUTHORIZED);
    assertEquals(Level.INFO, this.onlyEvent().getLevel());
    assertEquals("403", this.onlyEvent().getMDCPropertyMap().get(LogContext.STATUS_CODE));
  }

  @Test
  public void removingTheStatusAlsoRemovesTheNameAndEmailItPublished() throws Exception {
    LogContext.putUserName(user());
    this.session.put(APConstants.SESSION_USER, user());
    this.run(BaseAction.NOT_FOUND);
    assertEquals("Ana Perez", MDC.get(LogContext.USER_NAME));
    assertEquals("a.perez@example.org", MDC.get(LogContext.USER_EMAIL));
    LogContext.removeStatusCode();
    assertNull(MDC.get(LogContext.STATUS_CODE));
    assertNull(MDC.get(LogContext.USER_NAME));
    assertNull(MDC.get(LogContext.USER_EMAIL));
  }

  @Test
  public void otherResultsAreNotLoggedAndLeaveNoStatus() throws Exception {
    for (String resultCode : new String[] {Action.SUCCESS, Action.INPUT, BaseAction.REDIRECT, "unhandledException",
      null}) {
      this.listeners.clear();
      assertEquals(resultCode, this.run(resultCode));
    }
    assertTrue("no line must be logged", this.appender.list.isEmpty());
    assertNull(MDC.get(LogContext.STATUS_CODE));
  }

  @Test
  public void theInvocationResultIsReturnedUnchanged() throws Exception {
    assertEquals(BaseAction.NOT_AUTHORIZED, this.run(BaseAction.NOT_AUTHORIZED));
  }

}
