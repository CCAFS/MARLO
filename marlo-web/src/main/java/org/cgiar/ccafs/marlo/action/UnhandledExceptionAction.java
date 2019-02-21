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
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.ClientAbortException;
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
    // Print the exception in the log
    LOG.error("There was an unexpected exception", exception);
    // Send email only if we are in production mode.
    if (config.isProduction() && !(exception instanceof ClientAbortException)) {
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

  public void sendExceptionMessage() {
    String subject;
    StringBuilder message = new StringBuilder();

    StringWriter writer = new StringWriter();
    if (exception == null) {
      exception = new Exception("MARLOCustomPersistFilter ERROR!");
    }

    Exception e = (Exception) request.getSession().getAttribute("exception");
    if (e != null) {
      exception = e;
    }
    request.getSession().setAttribute("exception", null);
    exception.printStackTrace(new PrintWriter(writer));


    GlobalUnit crp = this.getCurrentCrp();

    subject = "Exception occurred in MARLO";

    message.append("The user " + this.getCurrentUser().getFirstName() + " " + this.getCurrentUser().getLastName() + " ("
      + this.getCurrentUser().getEmail() + ") ");
    message.append("has experienced an exception on the platform. </br>");
    message.append("This exception occurs in the server: " + config.getBaseUrl() + "</br></br>");
    String crpAcronymName = crp.getAcronym() != null && !crp.getAcronym().isEmpty() ? crp.getAcronym() : crp.getName();
    if (crpAcronymName != null) {
      message.append("<b>CRP : </b>" + crp.getAcronym() + ".</br>");
    }
    if (this.getActualPhase() != null) {
      message.append("<b>Phase: </b>" + this.getActualPhase().getComposedName() + ".</br>");
    }
    if (this.getActionName() != null) {
      message.append("<b>ActionName: </b>" + this.getActionName() + ".</br>");
    }

    message.append("</br></br><b>Exception message: </b></br></br>");
    message.append(writer.toString() + "</br></br></br>");

    if (this.getRequest() != null) {
      HttpServletRequest httpServletRequest = this.getRequest();
      if (httpServletRequest.getParameterMap() != null && !httpServletRequest.getParameterMap().isEmpty()) {
        message.append("<b>Parameters: </b></br>");
        for (String parameter : httpServletRequest.getParameterMap().keySet()) {
          if (httpServletRequest.getParameterMap().get(parameter) != null
            && httpServletRequest.getParameterMap().get(parameter).length > 0) {
            message.append("</br>" + parameter + ": ");
            Set<String> values = new HashSet<>();
            for (String value : httpServletRequest.getParameterMap().get(parameter)) {
              values.add(value);
            }
            message.append(String.join(", ", values));
          }
        }
      }
    }

    sendMail.send(config.getEmailNotification(), null, config.getEmailNotification(), subject, message.toString(), null,
      null, null, true);
    LOG.info("sendExceptionMessage() > The platform has sent a message reporting a exception.",
      this.getCurrentUser().getEmail());
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }
}
