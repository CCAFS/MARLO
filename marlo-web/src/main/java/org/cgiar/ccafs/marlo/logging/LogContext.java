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

package org.cgiar.ccafs.marlo.logging;

import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.regex.Pattern;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Puts the current user id, Global Unit acronym, request route and HTTP status in the SLF4J MDC, so that logback.xml
 * can print them on every log line of the request. The keys are the field names of the logging standard proposed for
 * the CGIAR tools (ENH-LOGGING-STANDARDIZATION-001): tool_name (the Global Unit), user_id, user_name,
 * controller_affected (the route) and status_code. logback.xml prints only the keys that have a value, as key=value
 * pairs (logfmt), so that the text log can be queried by field without a JSON appender.
 * <p>
 * The route is the request path only: the query string is never logged because it can carry form values, and a
 * ;jsessionid path parameter is cut off because a session id in a log file can be used to hijack the session. A quote,
 * backslash or equals sign in the path is percent-encoded, so that the key=value line stays parseable.
 * <p>
 * The numeric user id is logged on every line. The user's first and last name are personal data, so they are only
 * logged on the lines of a failed request: {@link #putUserName(User)} keeps the name aside, and it reaches the log
 * only once {@link #putStatusCode(int)} records a status of 400 or more. The email is never logged.
 * <p>
 * Writing the context must never break a request, so no method ever throws: null values are ignored, and any runtime
 * failure (a detached Hibernate proxy behind getAcronym(), for example) is caught and logged at DEBUG. Callers sit in
 * the middle of filters, interceptors and finally blocks, where an exception would fail the request, leave the session
 * half updated or mask the original error, so they must not need their own try/catch. An acronym that does not look
 * like one is ignored too, so that a value can never forge a log line.
 * <p>
 * The values are bound to the request thread, and this class is also the request listener that owns their lifecycle
 * (registered by {@code @WebListener}, so neither web.xml nor WebAppInitializer lists it). A listener rather than a
 * filter, because no filter wraps every request: the web.xml struts2 filter runs each Struts action without continuing
 * the chain, so the filters registered in WebAppInitializer never see a .do request. Tomcat fires both events on the
 * request thread, before the first filter and after the last one (error page included), whatever the filter order, so
 * clearing there guarantees that a pooled thread never carries one request's context into the next. Tomcat fails the
 * request when a request listener throws, so neither event may throw.
 * <p>
 * Only the route is written by the listener. The user and the Global Unit need the session, so they are written where
 * it is available: RequireUserInterceptor for Struts and AddSessionToRestRequestFilter for REST.
 */
@WebListener
public final class LogContext implements ServletContextListener, ServletRequestListener {

  public static final String CONTROLLER_AFFECTED = "controller_affected";

  public static final String STATUS_CODE = "status_code";

  public static final String TOOL_NAME = "tool_name";

  public static final String USER_ID = "user_id";

  public static final String USER_NAME = "user_name";

  private static final Pattern GLOBAL_UNIT_ACRONYM = Pattern.compile("[A-Za-z0-9_.-]{1,50}");

  private static final Pattern NON_PRINTABLE = Pattern.compile("[^\\x21-\\x7E]");

  private static final int ROUTE_MAX_LENGTH = 200;

  private static final Pattern LINE_BREAKS = Pattern.compile("[\\r\\n\\u0085\\u2028\\u2029]+");

  private static final int MAX_CAUSE_DEPTH = 10;

  // Every Unicode control character (C0 and C1, line breaks included), the line and paragraph separators, the square
  // brackets, and the double quote and backslash: the name is printed as user_name="...", so a quote would close the
  // value and a backslash would escape the closing quote. Each one is replaced by a space
  private static final Pattern NAME_UNSAFE = Pattern.compile("[\\p{Cc}\\u2028\\u2029\\[\\]\"\\\\]");

  // Invisible format characters (right-to-left override, zero-width space, byte order mark...): removed, not replaced,
  // so that they can neither disguise a line in a terminal nor split a word
  private static final Pattern NAME_INVISIBLE = Pattern.compile("\\p{Cf}");

  private static final Pattern WHITESPACE = Pattern.compile("\\s+");

  private static final int NAME_MAX_LENGTH = 100;

  private static final int ERROR_STATUS = 400;

  /**
   * The name of the request's user, kept out of the MDC until the request fails. Cleared with the rest of the context.
   */
  private static final ThreadLocal<String> PENDING_USER_NAME = new ThreadLocal<>();

  private static final Logger LOG = LoggerFactory.getLogger(LogContext.class);

  public static void clear() {
    try {
      MDC.remove(USER_ID);
      MDC.remove(TOOL_NAME);
      MDC.remove(CONTROLLER_AFFECTED);
      MDC.remove(STATUS_CODE);
      MDC.remove(USER_NAME);
      PENDING_USER_NAME.remove();
    } catch (RuntimeException e) {
      LOG.debug("Could not clear the log context", e);
    }
  }

  public static void putGlobalUnit(GlobalUnit globalUnit) {
    try {
      if (globalUnit != null) {
        putGlobalUnit(globalUnit.getAcronym());
      }
    } catch (RuntimeException e) {
      LOG.debug("Could not read the Global Unit acronym for the log context", e);
    }
  }

  public static void putGlobalUnit(String globalUnitAcronym) {
    try {
      if (globalUnitAcronym != null && GLOBAL_UNIT_ACRONYM.matcher(globalUnitAcronym).matches()) {
        MDC.put(TOOL_NAME, globalUnitAcronym);
      }
    } catch (RuntimeException e) {
      LOG.debug("Could not put the Global Unit in the log context", e);
    }
  }

  /**
   * Puts the request path in the log context. Anything from the first ';' on is dropped (path parameters such as
   * ;jsessionid), as is every character outside printable ASCII, so the value cannot split a log line, and the result
   * is truncated so that a crafted URL cannot flood the log.
   *
   * @param requestUri the value of HttpServletRequest.getRequestURI(), which never includes the query string
   */
  public static void putRoute(String requestUri) {
    try {
      if (requestUri == null) {
        return;
      }
      String route = requestUri;
      int pathParameters = route.indexOf(';');
      if (pathParameters >= 0) {
        route = route.substring(0, pathParameters);
      }
      route = NON_PRINTABLE.matcher(route).replaceAll("");
      // Percent-encoded like in a URL, because the route is printed unquoted in key=value (logfmt) form, where a raw
      // quote, backslash or equals sign makes strict parsers reject the whole line
      route = route.replace("\\", "%5C").replace("\"", "%22").replace("=", "%3D");
      if (route.length() > ROUTE_MAX_LENGTH) {
        route = route.substring(0, ROUTE_MAX_LENGTH);
      }
      if (!route.isEmpty()) {
        MDC.put(CONTROLLER_AFFECTED, route);
      }
    } catch (RuntimeException e) {
      LOG.debug("Could not put the request route in the log context", e);
    }
  }

  /**
   * Puts the HTTP status of the request in the log context. Call it only where the status is known for certain, right
   * before the line that logs the error: Struts renders its 401/403/404 and unhandled-exception pages with HTTP 200, so
   * the response status cannot be read back from the response. A value outside 100-599 is ignored.
   *
   * @param statusCode the HTTP status the request answers, or will answer, with
   */
  public static void putStatusCode(int statusCode) {
    try {
      if (statusCode >= 100 && statusCode <= 599) {
        MDC.put(STATUS_CODE, String.valueOf(statusCode));
        String userName = PENDING_USER_NAME.get();
        if (statusCode >= ERROR_STATUS && userName != null) {
          MDC.put(USER_NAME, userName);
        }
      }
    } catch (RuntimeException e) {
      LOG.debug("Could not put the status code in the log context", e);
    }
  }

  /**
   * Returns a throwable that is safe to log when its messages may echo what a client sent. If no message in the cause
   * chain contains a line break, the original throwable is returned untouched, which is the normal case. Otherwise a
   * copy of the chain is returned whose messages have each line break replaced by a space and are prefixed with the
   * original class name, keeping every stack trace, so that a crafted value cannot forge a separate log line.
   *
   * @param throwable the throwable about to be logged, may be null
   * @return the same throwable, or a line-break-free copy of it; never throws
   */
  public static Throwable withoutLineBreaks(Throwable throwable) {
    try {
      return hasLineBreak(throwable, 0) ? copyWithoutLineBreaks(throwable, 0) : throwable;
    } catch (RuntimeException e) {
      LOG.debug("Could not remove the line breaks of an exception before logging it", e);
      return throwable;
    }
  }

  private static Throwable copyWithoutLineBreaks(Throwable throwable, int depth) {
    if (throwable == null || depth > MAX_CAUSE_DEPTH) {
      return null;
    }
    String message = throwable.getMessage();
    String description = throwable.getClass().getName();
    if (message != null) {
      description += ": " + LINE_BREAKS.matcher(message).replaceAll(" ");
    }
    RuntimeException copy = new RuntimeException(description, copyWithoutLineBreaks(throwable.getCause(), depth + 1));
    copy.setStackTrace(throwable.getStackTrace());
    return copy;
  }

  private static boolean hasLineBreak(Throwable throwable, int depth) {
    if (throwable == null || depth > MAX_CAUSE_DEPTH) {
      return false;
    }
    String message = throwable.getMessage();
    return (message != null && LINE_BREAKS.matcher(message).find()) || hasLineBreak(throwable.getCause(), depth + 1);
  }

  /**
   * Keeps the first and last name of the request's user, to be logged only if the request fails (status 400 or more).
   * If the failure was already recorded, the name is logged right away. Control characters (Unicode C0 and C1, line
   * breaks included), square brackets, double quotes and backslashes become spaces, and invisible format characters
   * are removed, so that a name can neither split a line, break out of its quoted value, imitate another field nor
   * disguise the line in a terminal; whitespace is then collapsed and the result truncated. A user with neither name is
   * ignored.
   *
   * @param user the user of the request, may be null
   */
  public static void putUserName(User user) {
    try {
      if (user == null) {
        return;
      }
      StringBuilder name = new StringBuilder();
      for (String part : new String[] {user.getFirstName(), user.getLastName()}) {
        if (part != null) {
          name.append(' ').append(part);
        }
      }
      String visible = NAME_INVISIBLE.matcher(NAME_UNSAFE.matcher(name).replaceAll(" ")).replaceAll("");
      String userName = WHITESPACE.matcher(visible).replaceAll(" ").trim();
      if (userName.length() > NAME_MAX_LENGTH) {
        userName = userName.substring(0, NAME_MAX_LENGTH).trim();
      }
      if (userName.isEmpty()) {
        return;
      }
      PENDING_USER_NAME.set(userName);
      if (isErrorStatus(MDC.get(STATUS_CODE))) {
        MDC.put(USER_NAME, userName);
      }
    } catch (RuntimeException e) {
      LOG.debug("Could not keep the user name for the log context", e);
    }
  }

  private static boolean isErrorStatus(String statusCode) {
    if (statusCode == null) {
      return false;
    }
    try {
      return Integer.parseInt(statusCode) >= ERROR_STATUS;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static void putUserId(Long userId) {
    try {
      if (userId != null) {
        MDC.put(USER_ID, userId.toString());
      }
    } catch (RuntimeException e) {
      LOG.debug("Could not put the user id in the log context", e);
    }
  }

  /**
   * Instantiated only by the servlet container, as the request listener. Every other member is static.
   */
  public LogContext() {
  }

  @Override
  public void contextDestroyed(ServletContextEvent event) {
    // Nothing to release: the per-request values are cleared by requestDestroyed
  }

  /**
   * Announces at startup that the container registered this listener. The listener is found by annotation scanning
   * only, so a server where that scanning is off (metadata-complete="true" in web.xml, for example) would silently skip
   * it, and the per-request context would then never be cleared. The absence of this line in the startup log is what
   * reveals it.
   */
  @Override
  public void contextInitialized(ServletContextEvent event) {
    LOG.info("Log context listener registered: the per-request log context is cleared at the start and end of every"
      + " request");
  }

  @Override
  public void requestDestroyed(ServletRequestEvent event) {
    clear();
  }

  @Override
  public void requestInitialized(ServletRequestEvent event) {
    // A thread is never expected to arrive with a context, but clearing first costs nothing
    clear();
    try {
      if (event != null && event.getServletRequest() instanceof HttpServletRequest) {
        putRoute(((HttpServletRequest) event.getServletRequest()).getRequestURI());
      }
    } catch (RuntimeException e) {
      LOG.debug("Could not put the request route in the log context", e);
    }
  }
}
