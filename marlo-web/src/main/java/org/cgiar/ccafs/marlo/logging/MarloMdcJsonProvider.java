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

import java.io.IOException;
import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.AbstractJsonProvider;

/**
 * Writes the MDC-sourced fields of the JSON log event that the stock providers of logstash-logback-encoder cannot
 * write (ENH-LOGGING-STANDARDIZATION-001, ADR-9). It is declared in the {@code <providers>} block of logback.xml, which
 * is also where the field names are set, so that a rename is one edit (NF-004).
 * <ul>
 * <li>{@code status_code} and {@code user_id} are written as JSON numbers (NF-006). The MDC is a
 * {@code Map<String,String>} and the encoder this project is pinned to (6.6, because 7.x needs logback 1.3) has no
 * typed MDC writer, so a stock provider would write {@code "404"}, and {@code "404" >= "500"} compares text.</li>
 * <li>{@code user_name} and {@code user_email} are written only when the event carries a {@code status_code} of 400 or
 * more (SEC-001). They enter the MDC when the request starts and the status is known only when it ends, so the
 * restriction can only be applied when the line is written. {@link LogContext} already holds them back until the status
 * is recorded; this is the second barrier, so that a value placed in the MDC by any other route is still not
 * published on a successful request.</li>
 * <li>{@code service_affected} is never written (ADR-9): it has no meaning for a monolith, and an always-empty field
 * costs bytes on every line and teaches consumers to ignore the name.</li>
 * </ul>
 * A missing, blank or unparseable value is treated as absent and its field is left out, never written as a string.
 * Nothing here throws on a bad MDC value (NF-005): a failure in the logging path must not fail the request.
 * <p>
 * The other context fields ({@code request_id}, {@code tool_name}, {@code controller_affected}) are plain strings and
 * are written by the stock MDC provider next to this one.
 */
public class MarloMdcJsonProvider extends AbstractJsonProvider<ILoggingEvent> {

  private static final int ERROR_STATUS = 400;

  private String statusCodeFieldName = LogContext.STATUS_CODE;

  private String userEmailFieldName = LogContext.USER_EMAIL;

  private String userIdFieldName = LogContext.USER_ID;

  private String userNameFieldName = LogContext.USER_NAME;

  /**
   * Parses an MDC value that must be a whole number.
   *
   * @param value the MDC value, may be null
   * @return the number, or null when the value is missing, blank or not a number
   */
  private static Long parseNumber(String value) {
    if (value == null) {
      return null;
    }
    try {
      return Long.valueOf(value.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private static boolean hasText(String value) {
    return value != null && !value.trim().isEmpty();
  }

  @Override
  public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
    Map<String, String> mdc = event.getMDCPropertyMap();
    if (mdc == null || mdc.isEmpty()) {
      return;
    }
    Long statusCode = parseNumber(mdc.get(LogContext.STATUS_CODE));
    Long userId = parseNumber(mdc.get(LogContext.USER_ID));
    if (statusCode != null) {
      generator.writeNumberField(statusCodeFieldName, statusCode.longValue());
    }
    if (userId != null) {
      generator.writeNumberField(userIdFieldName, userId.longValue());
    }
    if (statusCode != null && statusCode.longValue() >= ERROR_STATUS) {
      String userName = mdc.get(LogContext.USER_NAME);
      if (hasText(userName)) {
        generator.writeStringField(userNameFieldName, userName);
      }
      String userEmail = mdc.get(LogContext.USER_EMAIL);
      if (hasText(userEmail)) {
        generator.writeStringField(userEmailFieldName, userEmail);
      }
    }
  }

  public void setStatusCodeFieldName(String statusCodeFieldName) {
    this.statusCodeFieldName = statusCodeFieldName;
  }

  public void setUserEmailFieldName(String userEmailFieldName) {
    this.userEmailFieldName = userEmailFieldName;
  }

  public void setUserIdFieldName(String userIdFieldName) {
    this.userIdFieldName = userIdFieldName;
  }

  public void setUserNameFieldName(String userNameFieldName) {
    this.userNameFieldName = userNameFieldName;
  }

}
