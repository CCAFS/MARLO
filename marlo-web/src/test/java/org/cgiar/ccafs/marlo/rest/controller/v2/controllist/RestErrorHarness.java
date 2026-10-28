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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist;

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.logging.LogContext;
import org.cgiar.ccafs.marlo.rest.errors.ExceptionTranslator;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.junit.rules.ExternalResource;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * ENH-LOGGING-STANDARDIZATION-001-T17: what a REST v2 controller does with a failure, seen from outside. Provides the
 * request-scoped fixtures a controller reads (the Shiro subject, the user manager, the environment) and
 * {@link #propagate(Call)}, which runs a controller call that must fail, hands the exception it threw to the same
 * {@code @ExceptionHandler} method Spring would pick in {@link ExceptionTranslator}, and checks that the failure is
 * logged exactly once, there, with the status the client receives.
 * <p>
 * Spring MVC itself is not started: the project has no spring-test, and the handler lookup uses Spring's own
 * {@link ExceptionHandlerMethodResolver}, so the status is the one the application answers.
 */
final class RestErrorHarness extends ExternalResource {

  /**
   * A call to a controller method.
   */
  interface Call {

    Object invoke() throws Exception;
  }

  /**
   * What the client receives for a failure the controller propagated.
   */
  static final class Translated {

    /** The exception the controller threw. */
    final Throwable exception;
    /** The HTTP status declared by the handler that answers it. */
    final int status;
    /** The response body the handler returned. */
    final Object body;
    /** The level of the single log event the handler wrote. */
    final Level loggedLevel;

    Translated(Throwable exception, int status, Object body, Level loggedLevel) {
      this.exception = exception;
      this.status = status;
      this.body = body;
      this.loggedLevel = loggedLevel;
    }
  }

  private static final String REST_LOGGERS = "org.cgiar.ccafs.marlo.rest";

  private static final ExceptionHandlerMethodResolver HANDLERS =
    new ExceptionHandlerMethodResolver(ExceptionTranslator.class);

  /** The principal the bound Shiro subject reports; only a user manager asked for exactly this id knows the user. */
  private static final Long PRINCIPAL = 4711L;

  /**
   * Sets a field the Spring container would inject ({@code @Autowired Environment env}).
   */
  static void inject(Object target, String fieldName, Object value) {
    try {
      Field field = target.getClass().getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(target, value);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError("Cannot inject " + fieldName + " into " + target.getClass().getName(), e);
    }
  }

  /**
   * An environment that answers the given properties, to be injected as the controller's {@code env}.
   */
  static Environment environment(String key, String value) {
    StandardEnvironment environment = new StandardEnvironment();
    environment.getPropertySources().addFirst(new MapPropertySource("rest-error-test",
      Collections.<String, Object>singletonMap(key, value)));
    return environment;
  }

  /**
   * The exception the items throw for an unknown entity, phase or identifier.
   */
  static MARLOFieldValidationException fieldValidation(String dto, String field, String message) {
    return new MARLOFieldValidationException("Field Validation errors", "",
      Collections.singletonList(new FieldErrorDTO(dto, field, message)));
  }

  private ListAppender<ILoggingEvent> appender;
  private Logger root;
  private Level rootLevel;

  private final User user = new User();

  private final UserManager userManager = (UserManager) Proxy.newProxyInstance(UserManager.class.getClassLoader(),
    new Class<?>[] {UserManager.class}, (proxy, method, args) -> {
      if ("getUser".equals(method.getName()) && args != null && args.length == 1) {
        return PRINCIPAL.equals(args[0]) ? this.user : null;
      }
      throw new UnsupportedOperationException(method.getName());
    });

  @Override
  protected void after() {
    this.root.detachAppender(this.appender);
    this.root.setLevel(this.rootLevel);
    ThreadContext.remove();
    LogContext.clear();
  }

  /**
   * The INFO and above events logged under the REST packages since the test started.
   */
  List<ILoggingEvent> loggedEvents() {
    return this.restEvents();
  }

  /**
   * Asserts that nothing under the REST packages has been logged since the last call.
   */
  void assertNothingLogged() {
    List<String> events = new ArrayList<String>();
    for (ILoggingEvent event : this.restEvents()) {
      events.add(event.getLevel() + " " + event.getLoggerName() + " " + event.getFormattedMessage());
    }
    assertEquals("the controller must not log: " + events, 0, events.size());
  }

  @Override
  protected void before() {
    LogContext.clear();
    ThreadContext.bind(this.subject());
    this.root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    this.rootLevel = this.root.getLevel();
    this.root.setLevel(Level.ALL);
    this.appender = new ListAppender<ILoggingEvent>();
    this.appender.start();
    this.root.addAppender(this.appender);
  }

  /**
   * Runs a controller call that must fail. The failure has to leave the controller, so the call must not return
   * (an empty answer is what a swallowed failure looks like) and the controller must not have logged anything. The
   * exception is then handled the way the application handles it.
   *
   * @return what the client receives for it
   */
  Translated propagate(Call call) {
    Object returned = null;
    Throwable thrown = null;
    try {
      returned = call.invoke();
    } catch (Throwable e) {
      thrown = e;
    }
    if (thrown == null) {
      fail("the failure was swallowed by the controller, which answered " + returned);
    }
    this.assertNothingLogged();
    return this.translate(thrown);
  }

  /**
   * The INFO and above events of the REST packages. The items log the field errors they reject at DEBUG, which is
   * diagnostic detail and not an event about the failure.
   */
  private List<ILoggingEvent> restEvents() {
    List<ILoggingEvent> events = new ArrayList<ILoggingEvent>();
    for (ILoggingEvent event : this.appender.list) {
      if (event.getLoggerName().startsWith(REST_LOGGERS) && event.getLevel().isGreaterOrEqual(Level.INFO)) {
        events.add(event);
      }
    }
    return events;
  }

  private Subject subject() {
    return (Subject) Proxy.newProxyInstance(Subject.class.getClassLoader(), new Class<?>[] {Subject.class},
      (proxy, method, args) -> {
        if ("getPrincipal".equals(method.getName())) {
          return PRINCIPAL;
        }
        throw new UnsupportedOperationException(method.getName());
      });
  }

  private Translated translate(Throwable thrown) {
    Method handler = HANDLERS.resolveMethodByThrowable(thrown);
    assertNotNull("ExceptionTranslator has no handler for " + thrown.getClass().getName(), handler);
    ResponseStatus declared = AnnotatedElementUtils.findMergedAnnotation(handler, ResponseStatus.class);
    assertNotNull(handler.getName() + " declares no @ResponseStatus", declared);
    int status = declared.code().value();

    Object body;
    try {
      handler.setAccessible(true);
      body = handler.invoke(new ExceptionTranslator(null, null), thrown);
    } catch (InvocationTargetException | IllegalAccessException e) {
      throw new AssertionError("ExceptionTranslator." + handler.getName() + " failed", e);
    }

    List<ILoggingEvent> events = this.restEvents();
    assertEquals("exactly one log event for the failure: " + events, 1, events.size());
    ILoggingEvent event = events.get(0);
    assertEquals(ExceptionTranslator.class.getName(), event.getLoggerName());
    assertEquals("the logged status_code is the status the client receives", String.valueOf(status),
      event.getMDCPropertyMap().get(LogContext.STATUS_CODE));
    return new Translated(thrown, status, body, event.getLevel());
  }

  /**
   * The user the controller must resolve from the subject's principal and hand to the item.
   */
  User user() {
    return this.user;
  }

  /**
   * A user manager that answers the principal's user.
   */
  UserManager userManager() {
    return this.userManager;
  }

  /**
   * A user manager whose lookup fails, as it does when the database is down.
   */
  UserManager userManagerFailingWith(RuntimeException failure) {
    return (UserManager) Proxy.newProxyInstance(UserManager.class.getClassLoader(), new Class<?>[] {UserManager.class},
      (proxy, method, args) -> {
        throw failure;
      });
  }
}
