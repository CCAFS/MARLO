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

import com.google.inject.Inject;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UnhandledExceptionAction extends BaseAction {

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(UnhandledExceptionAction.class);
  private static final long serialVersionUID = 1095057952669633270L;

  // Model
  private Exception exception;

  @Inject
  public UnhandledExceptionAction(APConfig config) {
    super(config);
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

  public void sendExceptionMessage() {
    String subject;
    StringBuilder message = new StringBuilder();

    StringWriter writer = new StringWriter();
    exception.printStackTrace(new PrintWriter(writer));


    GlobalUnit crp = this.getCurrentCrp();

    subject = "Exception occurred in MARLO";

    message.append("The user " + this.getCurrentUser().getFirstName() + " " + this.getCurrentUser().getLastName() + " <"
      + this.getCurrentUser().getEmail() + "> ");
    message.append("has experienced an exception on the platform. </br>");
    message.append("This execption occurs in the server: " + config.getBaseUrl() + ".</br>");
    if (crp != null) {
      message.append("In the CRP : " + crp.getAcronym() != null && !crp.getAcronym().isEmpty() ? crp.getAcronym()
        : crp.getName() + ".</br>");
    }
    message.append("The exception message was: </br></br>");
    message.append(writer.toString());

    SendMailS sendMail = new SendMailS(this.config);
    sendMail.send(config.getEmailNotification(), null, config.getEmailNotification(), subject, message.toString(), null,
      null, null, true);
    LOG.info("sendExceptionMessage() > The platform has sent a message reporting a exception.",
      this.getCurrentUser().getEmail());
  }

  public void setException(Exception exception) {
    this.exception = exception;
  }
}
