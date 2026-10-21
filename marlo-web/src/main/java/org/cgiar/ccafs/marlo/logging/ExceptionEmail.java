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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

/**
 * Builds the HTML of the error report emailed to the support team, shared by the Struts unhandled exceptions
 * (UnhandledExceptionAction) and the REST 5xx (ExceptionTranslator), so both read the same.
 * <p>
 * The report goes out in the frame of the branded MARLO emails (logo, lockup, grey card, band), but it is built here
 * and not from the i18n key email.layout: the report is internal, goes to the same team whatever the Global Unit, and
 * the REST path has no text provider; the footer of email.layout also tells a registered user why they receive it,
 * which is not true of this report. The cause comes first, then the request, the parameters and the full stack
 * trace. Every value is HTML-escaped, since messages and parameters can carry what the client sent, and the values of
 * parameters that look like credentials are masked, because the email is also stored in email_logs.
 */
public final class ExceptionEmail {

  static final String MASK = "[hidden]";

  // A longer parameter or message is cut, so that one long text field does not bury the rest of the report.
  static final int MAX_VALUE_LENGTH = 500;

  static final int MAX_MESSAGE_LENGTH = 1000;

  // Names of parameters whose values are credentials; "code" alone is the sign-in code of the OTP login.
  private static final Pattern SENSITIVE_PARAMETER = Pattern.compile(
    "(?i).*(password|passwd|pwd|secret|token|otp|apikey|api_key|authorization|credential|cookie|session).*|code");

  private static final String FONT = "font-family:Poppins, Arial, Helvetica, sans-serif;";

  private static final String MONO = "font-family:Consolas, Menlo, Monaco, 'Courier New', monospace;";

  private static final String LABEL_CELL = "padding:6px 12px 6px 0;vertical-align:top;white-space:nowrap;" + FONT
    + "font-size:13px;line-height:20px;font-weight:bold;color:#555555;";

  private static final String VALUE_CELL =
    "padding:6px 0;vertical-align:top;word-break:break-word;" + FONT + "font-size:14px;line-height:20px;color:#1F1F1F;";

  private static final String SECTION_TITLE = "margin:32px 0 8px 0;" + FONT
    + "font-size:16px;line-height:24px;font-weight:bold;color:#0B7AA3;";

  // One block per section, not a styled cell per line: a form posts hundreds of parameters, and a table row costs
  // about 500 bytes of inline style against the 64 KB of email_logs.message.
  private static final String PRE_BLOCK = "margin:0;padding:12px;background-color:#FFFFFF;border:1px solid #D9D9D9;"
    + MONO + "font-size:12px;line-height:17px;color:#1F1F1F;white-space:pre-wrap;word-break:break-all;";

  private static String hostName;

  private final String title;

  private String intro;

  private final List<String[]> details = new ArrayList<>();

  private final List<String[]> parameters = new ArrayList<>();

  private Throwable exception;

  /**
   * @param title the heading of the report, such as "Unexpected error in AICCRA".
   */
  public ExceptionEmail(String title) {
    this.title = title;
  }

  /**
   * The text the report names the server it runs on with, cached: it identifies the container that failed when
   * several serve the same URL.
   */
  static synchronized String hostName() {
    if (hostName == null) {
      try {
        hostName = InetAddress.getLocalHost().getHostName();
      } catch (Exception e) {
        hostName = "";
      }
    }
    return hostName;
  }

  static boolean isSensitive(String parameterName) {
    return parameterName != null && SENSITIVE_PARAMETER.matcher(parameterName).matches();
  }

  static String shorten(String value, int maxLength) {
    if (value == null || value.length() <= maxLength) {
      return value;
    }
    return value.substring(0, maxLength) + " [... " + (value.length() - maxLength) + " more characters]";
  }

  /**
   * Returns the deepest cause of an error, which is usually the one that explains it (the SQL constraint under the
   * Hibernate exception); a cycle of causes stops at the last new one.
   */
  static Throwable rootCause(Throwable throwable) {
    Set<Throwable> seen = Collections.newSetFromMap(new IdentityHashMap<>());
    Throwable root = throwable;
    while (root != null && root.getCause() != null && seen.add(root)) {
      root = root.getCause();
    }
    return root;
  }

  private static String describe(Throwable throwable) {
    String message = throwable.getMessage();
    return throwable.getClass().getName()
      + (message != null && !message.isEmpty() ? ": " + shorten(message, MAX_MESSAGE_LENGTH) : "");
  }

  private static String escape(String value) {
    return StringEscapeUtils.escapeHtml4(value);
  }

  /**
   * Adds the lines that tie the report to the log lines of the request: request id, status code, route, plus the
   * repeats of the error that were not emailed in the previous hour (see ErrorNotificationThrottle).
   *
   * @param suppressedRepeats the value ErrorNotificationThrottle.register returned for this error.
   * @return this report.
   */
  public ExceptionEmail correlation(int suppressedRepeats) {
    this.detail("Request id", LogContext.get(LogContext.REQUEST_ID));
    this.detail("Status code", LogContext.get(LogContext.STATUS_CODE));
    this.detail("Route", LogContext.get(LogContext.CONTROLLER_AFFECTED));
    if (suppressedRepeats > 0) {
      this.detail("Repeats not emailed in the previous hour", String.valueOf(suppressedRepeats));
    }
    return this;
  }

  /**
   * Adds a line to the request details; a null or empty value is left out.
   */
  public ExceptionEmail detail(String label, String value) {
    if (label != null && value != null && !value.trim().isEmpty()) {
      details.add(new String[] {label, value});
    }
    return this;
  }

  /**
   * Adds the server lines every report carries: the URL of the instance, the host that answered and the time, in UTC.
   */
  public ExceptionEmail environment(String baseUrl) {
    this.detail("Server", baseUrl);
    this.detail("Host", hostName());
    this.detail("Time (UTC)",
      ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    return this;
  }

  public ExceptionEmail exception(Throwable exception) {
    this.exception = exception;
    return this;
  }

  /**
   * Sets the sentence under the heading that says who hit the error and where.
   */
  public ExceptionEmail intro(String intro) {
    this.intro = intro;
    return this;
  }

  /**
   * Adds the request parameters, sorted by name, each value once, with credentials masked and long values cut.
   *
   * @param parameterMap the parameters of the request; may be null.
   * @return this report.
   */
  public ExceptionEmail parameters(Map<String, String[]> parameterMap) {
    if (parameterMap == null || parameterMap.isEmpty()) {
      return this;
    }
    SortedSet<String> names = new TreeSet<>(parameterMap.keySet());
    for (String name : names) {
      String[] values = parameterMap.get(name);
      if (name == null || values == null || values.length == 0) {
        continue;
      }
      String value;
      if (isSensitive(name)) {
        value = MASK;
      } else {
        Set<String> distinct = new LinkedHashSet<>();
        for (String item : values) {
          distinct.add(item != null ? item : "");
        }
        value = shorten(String.join(", ", distinct), MAX_VALUE_LENGTH);
      }
      parameters.add(new String[] {name, value});
    }
    return this;
  }

  private void appendRows(StringBuilder html, List<String[]> rows) {
    html.append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" "
      + "style=\"border-collapse:collapse;\">");
    for (String[] row : rows) {
      html.append("<tr><td style=\"").append(LABEL_CELL).append("\">").append(escape(row[0]))
        .append("</td><td style=\"").append(VALUE_CELL).append("\">")
        .append(escape(row[1])).append("</td></tr>");
    }
    html.append("</table>");
  }

  private void appendBody(StringBuilder html) {
    html.append("<p style=\"margin:0 0 16px 0;").append(FONT)
      .append("font-size:20px;line-height:28px;font-weight:bold;color:#1F1F1F;\">").append(escape(title))
      .append("</p>");
    if (intro != null) {
      html.append("<p style=\"margin:0 0 24px 0;\">").append(escape(intro)).append("</p>");
    }

    if (exception != null) {
      Throwable root = rootCause(exception);
      html.append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" "
        + "style=\"border-collapse:collapse;\"><tr><td bgcolor=\"#FFFFFF\" style=\"background-color:#FFFFFF;"
        + "border-left:4px solid #C62828;padding:14px 16px;\">");
      html.append("<p style=\"margin:0 0 4px 0;").append(FONT)
        .append("font-size:13px;line-height:20px;font-weight:bold;color:#C62828;\">Error</p>");
      html.append("<p style=\"margin:0;").append(MONO).append("font-size:13px;line-height:20px;color:#1F1F1F;"
        + "word-break:break-word;\">").append(escape(describe(exception))).append("</p>");
      if (root != exception) {
        html.append("<p style=\"margin:12px 0 4px 0;").append(FONT)
          .append("font-size:13px;line-height:20px;font-weight:bold;color:#C62828;\">Root cause</p>");
        html.append("<p style=\"margin:0;").append(MONO).append("font-size:13px;line-height:20px;color:#1F1F1F;"
          + "word-break:break-word;\">").append(escape(describe(root))).append("</p>");
      }
      html.append("</td></tr></table>");
    }

    if (!details.isEmpty()) {
      html.append("<p style=\"").append(SECTION_TITLE).append("\">Request</p>");
      this.appendRows(html, details);
    }

    if (!parameters.isEmpty()) {
      html.append("<p style=\"").append(SECTION_TITLE).append("\">Parameters</p>");
      html.append("<pre style=\"").append(PRE_BLOCK).append("\">");
      for (String[] parameter : parameters) {
        html.append(escape(parameter[0])).append(": ").append(escape(parameter[1])).append("\n");
      }
      html.append("</pre>");
    }

    if (exception != null) {
      StringWriter stackTrace = new StringWriter();
      exception.printStackTrace(new PrintWriter(stackTrace));
      html.append("<p style=\"").append(SECTION_TITLE).append("\">Stack trace</p>");
      html.append("<pre style=\"").append(PRE_BLOCK).append("\">").append(escape(stackTrace.toString()))
        .append("</pre>");
    }
  }

  /**
   * Returns the report inside the MARLO frame.
   *
   * @param assetsUrl the URL the logo and the band are served from, without the trailing slash; with none, the frame
   *        is drawn without images.
   * @return the HTML of the email body.
   */
  public String toHtml(String assetsUrl) {
    boolean images = assetsUrl != null && !assetsUrl.trim().isEmpty();
    StringBuilder html = new StringBuilder();
    html.append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" "
      + "bgcolor=\"#FFFFFF\" style=\"background-color:#FFFFFF;border-collapse:collapse;\"><tr>"
      + "<td align=\"center\" bgcolor=\"#FFFFFF\" style=\"background-color:#FFFFFF;padding:32px 12px 32px 12px;\">");
    html.append("<!--[if mso]><table role=\"presentation\" width=\"600\" cellpadding=\"0\" cellspacing=\"0\" "
      + "border=\"0\"><tr><td><![endif]-->");
    html.append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" "
      + "style=\"max-width:600px;border-collapse:collapse;").append(FONT).append("\">");

    // Header: logo and lockup, as in email.layout
    html.append("<tr><td align=\"left\" style=\"padding:0 0 8px 0;\"><table role=\"presentation\" cellpadding=\"0\" "
      + "cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;\"><tr>");
    if (images) {
      html.append("<td width=\"66\" valign=\"middle\"><img src=\"").append(escape(assetsUrl.trim()))
        .append("/global/images/email/marlo-logo.png\" width=\"50\" height=\"50\" alt=\"MARLO\" "
          + "style=\"display:block;width:50px;height:50px;border:0;outline:none;text-decoration:none;\"></td>");
    }
    html.append("<td valign=\"middle\"><div style=\"").append(FONT)
      .append("font-size:30px;line-height:34px;font-weight:bold;color:#0B7AA3;\">MARLO</div><div style=\"")
      .append(FONT).append("font-size:17px;line-height:23px;font-weight:bold;color:#1F1F1F;\">Error report for the "
        + "support team</div></td></tr></table></td></tr>");
    html.append("<tr><td height=\"32\" style=\"height:32px;font-size:0;line-height:0;\">&nbsp;</td></tr>");

    // Card
    html.append("<tr><td align=\"left\" bgcolor=\"#F8F8F8\" style=\"background-color:#F8F8F8;padding:40px 36px 40px "
      + "36px;box-shadow:0 2px 8px rgba(0,0,0,0.12);").append(FONT)
      .append("font-size:16px;line-height:24px;color:#1F1F1F;\">");
    this.appendBody(html);
    html.append("</td></tr>");

    // Band and footer
    html.append("<tr><td height=\"40\" style=\"height:40px;font-size:0;line-height:0;\">&nbsp;</td></tr>");
    if (images) {
      html.append("<tr><td style=\"padding:0;font-size:0;line-height:0;\"><img src=\"").append(escape(assetsUrl.trim()))
        .append("/global/images/email/email-band.png\" width=\"600\" alt=\"\" style=\"display:block;width:100%;"
          + "max-width:600px;height:auto;border:0;outline:none;text-decoration:none;\"></td></tr>");
    }
    html.append("<tr><td height=\"1\" bgcolor=\"#D9D9D9\" style=\"background-color:#D9D9D9;height:1px;font-size:0;"
      + "line-height:0;\">&nbsp;</td></tr>");
    html.append("<tr><td align=\"center\" style=\"padding:28px 8px 8px 8px;").append(FONT)
      .append("font-size:14px;line-height:20px;color:#555555;\">This is an automated error report from MARLO, sent "
        + "only to the support team.<br>The same error is emailed at most once an hour; the log has every "
        + "occurrence.</td></tr>");
    html.append("</table><!--[if mso]></td></tr></table><![endif]--></td></tr></table>");
    return html.toString();
  }
}
