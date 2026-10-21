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
package org.cgiar.ccafs.marlo.action;

import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.logging.ErrorNotificationThrottle;
import org.cgiar.ccafs.marlo.logging.ExceptionEmail;
import org.cgiar.ccafs.marlo.logging.LogContext;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UnhandledExceptionAction extends BaseAction {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(UnhandledExceptionAction.class);
  private static final long serialVersionUID = 1095057952669633270L;

  // Model
  private Exception exception;

  private final SendMailS sendMail;

  @Inject
  public UnhandledExceptionAction(APConfig config, SendMailS sendMail) {
    super(config);
    this.sendMail = sendMail;
  }

  @Override
  public String execute() throws Exception {
    // Print the exception in the log. The page is rendered with HTTP 200, but the request failed as a 500; a client
    // that closed the connection received nothing, so it gets no status, not even the 401/403/404 of the page that was
    // being written when the connection closed.
    boolean clientAborted = exception instanceof ClientAbortException;
    if (clientAborted) {
      // Not a server failure: the client closed the connection, typically by cancelling a download
      LogContext.removeStatusCode();
      LOG.warn("The client closed the connection before the response was written: {}", exception.getMessage());
    } else {
      LogContext.putStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      LOG.error("There was an unexpected exception", exception);
    }

    // The email is sent whenever the request has a Global Unit, and otherwise only in production, as
    // ExceptionTranslator does for REST errors. A closed connection is never emailed
    if (!clientAborted && (this.getCurrentCrp() != null || config.isProduction())) {
      this.sendExceptionMessage();
    }
    return super.execute();
  }

  public String getExceptionMessage() {
    StringWriter writer = new StringWriter();
    exception.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }

  public HttpServletRequest getServletRequest() {
    return this.request;
  }

  /**
   * Emails the exception to the support team, unless the same error was already emailed within the hour (see
   * ErrorNotificationThrottle). The email carries the request id and status of the failed request, so that it can be
   * matched with its lines in the log.
   */
  public void sendExceptionMessage() {
    String subject;

    if (exception == null) {
      exception = new Exception("MARLOCustomPersistFilter ERROR!");
    }

    if (request != null && request.getSession(false) != null) {
      Exception e = (Exception) request.getSession(false).getAttribute("exception");
      if (e != null) {
        exception = e;
      }
      request.getSession(false).setAttribute("exception", null);
    }

    int suppressedRepeats = ErrorNotificationThrottle.shared()
      .register(ErrorNotificationThrottle.key(LogContext.get(LogContext.TOOL_NAME),
        LogContext.get(LogContext.CONTROLLER_AFFECTED), exception, LogContext.get(LogContext.STATUS_CODE)));
    if (suppressedRepeats == ErrorNotificationThrottle.SUPPRESSED) {
      LOG.info("sendExceptionMessage() > The same exception was already reported within the hour; no email sent.");
      return;
    }

    GlobalUnit crp = this.getCurrentCrp();

    String globalUnitName = "MARLO";
    if (crp != null) {
      globalUnitName = crp.getAcronym() != null && !crp.getAcronym().isEmpty() ? crp.getAcronym()
        : crp.getName() != null ? crp.getName() : "MARLO";
    }
    subject = "Exception occurred in " + globalUnitName;

    ExceptionEmail message = new ExceptionEmail("Unexpected error in " + globalUnitName);
    User user = this.getCurrentUser();
    String userName = null;
    if (user != null) {
      userName = (StringUtils.trimToEmpty(user.getFirstName()) + " " + StringUtils.trimToEmpty(user.getLastName()))
        .trim();
      message.intro(userName + " (" + user.getEmail() + ") hit an unhandled exception on " + config.getBaseUrl() + ".");
    } else {
      message.intro("A user who was not logged in hit an unhandled exception on " + config.getBaseUrl() + ".");
    }
    message.exception(exception);
    message.detail("Global Unit", globalUnitName);
    Phase phase = this.getActualPhase();
    if (phase != null) {
      message.detail("Phase", phase.getComposedName());
      // getActualPhase() can answer an empty Phase, whose id is null: the line is then left out.
      message.detail("Phase id", phase.getId() != null ? String.valueOf(phase.getId()) : null);
    }
    message.detail("Action", this.getActionName());
    message.correlation(suppressedRepeats);
    if (user != null) {
      message.detail("User", userName);
      message.detail("User email", user.getEmail());
      message.detail("User id", user.getId() != null ? String.valueOf(user.getId()) : null);
    }
    HttpServletRequest httpServletRequest = this.getRequest();
    if (httpServletRequest != null) {
      message.detail("HTTP method", httpServletRequest.getMethod());
      message.detail("Referer", httpServletRequest.getHeader("Referer"));
      message.detail("User agent", httpServletRequest.getHeader("User-Agent"));
    }
    message.environment(config.getBaseUrl());

    String actionNameSubject = "";
    if (this.getActionName() != null) {
      String actionName = this.getActionName();
      if (crp != null) {
        int namespaceSeparator = actionName.indexOf('/');
        actionNameSubject = " - "
          + (namespaceSeparator >= 0 ? actionName.substring(namespaceSeparator + 1) : actionName);
      } else {
        actionNameSubject = " - " + actionName;
      }
    }

    if (this.getActualPhase() != null && this.getActualPhase().getComposedName() != null) {
      actionNameSubject += " - " + this.getActualPhase().getComposedName();
    }

    if (user != null && user.getFirstName() != null && user.getLastName() != null) {
      actionNameSubject += " - " + user.getFirstName() + " " + user.getLastName();
    }
    subject += actionNameSubject;

    if (httpServletRequest != null) {
      message.parameters(httpServletRequest.getParameterMap());
    }

    sendMail.send(config.getEmailNotification(), null, config.getEmailNotification(), subject,
      message.toHtml(this.getBaseUrlCdn()), null, null, null, true);
    LOG.info("sendExceptionMessage() > The platform has sent a message reporting a exception.");
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }
}
