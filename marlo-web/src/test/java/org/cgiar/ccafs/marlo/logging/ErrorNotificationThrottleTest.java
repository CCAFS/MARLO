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

import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import org.junit.Test;
import org.slf4j.MDC;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ErrorNotificationThrottleTest {

  private static final long WINDOW = 1000L;

  private final AtomicLong now = new AtomicLong(0L);

  private final ErrorNotificationThrottle throttle = new ErrorNotificationThrottle(WINDOW, 3, now::get);

  private static Throwable thrownAt(String className, String method, int line) {
    RuntimeException exception = new IllegalStateException("boom");
    exception.setStackTrace(new StackTraceElement[] {new StackTraceElement(className, method, "X.java", line)});
    return exception;
  }

  @After
  public void clearContext() {
    LogContext.clear();
  }

  @Test
  public void correlationHtmlEscapesTheRouteAndCountsRepeats() {
    MDC.put(LogContext.REQUEST_ID, "00ab");
    MDC.put(LogContext.STATUS_CODE, "500");
    MDC.put(LogContext.CONTROLLER_AFFECTED, "/a<b>");
    String html = ErrorNotificationThrottle.correlationHtml(4);
    assertTrue(html.contains("<b>Request id: </b>00ab</br>"));
    assertTrue(html.contains("<b>Status code: </b>500</br>"));
    assertTrue(html.contains("/a&lt;b&gt;"));
    assertTrue(html.contains("previous hour: </b>4."));
  }

  @Test
  public void correlationHtmlOmitsMissingValuesAndZeroRepeats() {
    assertEquals("", ErrorNotificationThrottle.correlationHtml(0));
  }

  @Test
  public void differentErrorsAreEmailedSeparately() {
    String key = ErrorNotificationThrottle.key("AICCRA", "/a.do", thrownAt("A", "m", 1), "500");
    assertEquals(0, throttle.register(key));
    assertEquals(0, throttle.register(ErrorNotificationThrottle.key("AICCRA", "/b.do", thrownAt("A", "m", 1), "500")));
    assertEquals(0, throttle.register(ErrorNotificationThrottle.key("AICCRA", "/a.do", thrownAt("A", "m", 2), "500")));
    assertEquals(0, throttle.register(ErrorNotificationThrottle.key("AICCRA", "/a.do", thrownAt("A", "m", 1), "503")));
  }

  @Test
  public void fullTrackerStillEmailsAnUntrackedError() {
    throttle.register("a");
    throttle.register("b");
    throttle.register("c");
    assertEquals(0, throttle.register("d"));
    assertEquals(0, throttle.register("d"));
  }

  @Test
  public void fullTrackerReusesExpiredWindows() {
    throttle.register("a");
    throttle.register("b");
    throttle.register("c");
    now.set(WINDOW);
    assertEquals(0, throttle.register("d"));
    assertEquals(ErrorNotificationThrottle.SUPPRESSED, throttle.register("d"));
  }

  @Test
  public void keyMatchesOnTheSameErrorAndToleratesMissingParts() {
    assertEquals(ErrorNotificationThrottle.key("G", "/r", thrownAt("A", "m", 1), "500"),
      ErrorNotificationThrottle.key("G", "/r", thrownAt("A", "m", 1), "500"));
    assertEquals("||||", ErrorNotificationThrottle.key(null, null, null, null));
    assertNotEquals(ErrorNotificationThrottle.key("G", "/r", new IllegalStateException(), "500"),
      ErrorNotificationThrottle.key("G", "/r", new IllegalArgumentException(), "500"));
  }

  @Test
  public void newRequestIdIsSixteenHexCharacters() {
    String requestId = LogContext.newRequestId();
    assertTrue(requestId.matches("[0-9a-f]{16}"));
    assertFalse(requestId.equals(LogContext.newRequestId()));
  }

  @Test
  public void repeatAfterTheWindowIsEmailedWithTheCount() {
    throttle.register("k");
    throttle.register("k");
    throttle.register("k");
    now.set(WINDOW);
    assertEquals(2, throttle.register("k"));
    assertEquals(ErrorNotificationThrottle.SUPPRESSED, throttle.register("k"));
  }

  @Test
  public void twoIdenticalErrorsSendOneEmail() {
    assertEquals(0, throttle.register("k"));
    now.set(WINDOW - 1);
    assertEquals(ErrorNotificationThrottle.SUPPRESSED, throttle.register("k"));
  }
}
