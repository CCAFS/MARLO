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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Decides whether a server error is emailed to the support team, so that one broken page hit a hundred times sends one
 * email, not a hundred (ENH-LOGGING-STANDARDIZATION-001 FN-008). Every error is still logged; only the email is held
 * back.
 * <p>
 * Two errors are the same when they share the Global Unit, the route, the exception class, the line that threw it and
 * the HTTP status (see {@link #key}). The first one is emailed and opens a one-hour window; the repeats inside the
 * window are only counted, and the first repeat after the window is emailed again with that count, so the support team
 * knows how often the error happened. The count of the last window is never emailed if the error stops: the log has
 * every occurrence.
 * <p>
 * The state is in memory, shared by the Struts and the REST paths of this server, and lost on restart, which at worst
 * sends one email too many. The number of tracked errors is capped; when the cap is reached and no window has expired,
 * the error is emailed without being tracked, because a missed alert costs more than an extra email.
 */
public final class ErrorNotificationThrottle {

  /**
   * The value {@link #register(String)} returns for an error that must not be emailed.
   */
  public static final int SUPPRESSED = -1;

  public static final long DEFAULT_WINDOW_MILLIS = TimeUnit.HOURS.toMillis(1);

  public static final int DEFAULT_MAX_TRACKED = 1000;

  private static final String KEY_SEPARATOR = "|";

  private static final ErrorNotificationThrottle SHARED =
    new ErrorNotificationThrottle(DEFAULT_WINDOW_MILLIS, DEFAULT_MAX_TRACKED, System::currentTimeMillis);

  /**
   * The throttle shared by every notification path of this server.
   */
  public static ErrorNotificationThrottle shared() {
    return SHARED;
  }

  /**
   * Builds the key that identifies an error: Global Unit, route, exception class, the frame that threw it and the HTTP
   * status. A missing part is written empty, so that two errors missing the same part still match.
   *
   * @param globalUnit the tool_name of the log context, may be null
   * @param route the controller_affected of the log context, may be null
   * @param throwable the error, may be null
   * @param statusCode the status_code of the log context, may be null
   * @return the key; never null
   */
  public static String key(String globalUnit, String route, Throwable throwable, String statusCode) {
    String exceptionClass = "";
    String origin = "";
    if (throwable != null) {
      exceptionClass = throwable.getClass().getName();
      StackTraceElement[] stackTrace = throwable.getStackTrace();
      if (stackTrace != null && stackTrace.length > 0 && stackTrace[0] != null) {
        origin = stackTrace[0].toString();
      }
    }
    return String.join(KEY_SEPARATOR, nullToEmpty(globalUnit), nullToEmpty(route), exceptionClass, origin,
      nullToEmpty(statusCode));
  }

  /**
   * Returns the HTML lines that tie a support email to the log lines of the request that failed: the request id, the
   * HTTP status and the route, read from the current log context, plus the number of repeats that were not emailed.
   * Values are HTML-escaped, since the route comes from the client.
   *
   * @param suppressedRepeats the value {@link #register(String)} returned for this error
   * @return the lines, each ending in a line break; never null
   */
  public static String correlationHtml(int suppressedRepeats) {
    StringBuilder html = new StringBuilder();
    appendLine(html, "Request id", LogContext.get(LogContext.REQUEST_ID));
    appendLine(html, "Status code", LogContext.get(LogContext.STATUS_CODE));
    appendLine(html, "Route", LogContext.get(LogContext.CONTROLLER_AFFECTED));
    if (suppressedRepeats > 0) {
      html.append("<b>Repeats not emailed in the previous hour: </b>").append(suppressedRepeats).append(".</br>");
    }
    return html.toString();
  }

  private static void appendLine(StringBuilder html, String label, String value) {
    if (value != null && !value.isEmpty()) {
      html.append("<b>").append(label).append(": </b>").append(StringEscapeUtils.escapeHtml4(value)).append("</br>");
    }
  }

  private static String nullToEmpty(String value) {
    return value != null ? value : "";
  }

  private final long windowMillis;

  private final int maxTracked;

  private final LongSupplier clock;

  private final Map<String, Window> windows = new HashMap<>();

  ErrorNotificationThrottle(long windowMillis, int maxTracked, LongSupplier clock) {
    this.windowMillis = windowMillis;
    this.maxTracked = maxTracked;
    this.clock = clock;
  }

  /**
   * Records an occurrence of an error and tells whether it must be emailed.
   *
   * @param key the error, as built by {@link #key}
   * @return {@link #SUPPRESSED} when the error was already emailed within the window; otherwise the number of repeats
   *         that were not emailed in the previous window, 0 for a first occurrence
   */
  public synchronized int register(String key) {
    long now = clock.getAsLong();
    Window window = windows.get(key);
    if (window != null && now - window.start < windowMillis) {
      window.suppressed++;
      return SUPPRESSED;
    }
    int previouslySuppressed = window != null ? window.suppressed : 0;
    if (window == null && windows.size() >= maxTracked) {
      this.removeExpired(now);
      if (windows.size() >= maxTracked) {
        return previouslySuppressed;
      }
    }
    windows.put(key, new Window(now));
    return previouslySuppressed;
  }

  private void removeExpired(long now) {
    Iterator<Window> iterator = windows.values().iterator();
    while (iterator.hasNext()) {
      if (now - iterator.next().start >= windowMillis) {
        iterator.remove();
      }
    }
  }

  private static final class Window {

    private final long start;

    private int suppressed;

    private Window(long start) {
      this.start = start;
    }
  }
}
