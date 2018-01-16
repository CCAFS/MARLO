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

import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class TestSMTPAction extends BaseAction {

  private static final Logger LOG = LoggerFactory.getLogger(TestSMTPAction.class);
  private static final long serialVersionUID = -8091302087807798710L;

  private boolean fail;

  // Managers
  private final Message sendMail;
  private boolean sent;

  @Inject
  public TestSMTPAction(APConfig config, Message sendMail) {
    super(config);
    this.sendMail = sendMail;
  }

  @Override
  public String execute() throws Exception {

    Properties properties = System.getProperties();
    properties.put("mail.smtp.host", config.getEmailHost());
    properties.put("mail.smtp.port", config.getEmailPort());

    Session session = Session.getInstance(properties, new Authenticator() {

      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(config.getEmailUsername(), config.getEmailPassword());
      }
    });

    // Create a new message
    MimeMessage msg = new MimeMessage(session) {

      @Override
      protected void updateMessageID() throws MessagingException {
        if (this.getHeader("Message-ID") == null) {
          super.updateMessageID();
        }
      }
    };

    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("h.jimenez@cgiar.org", false));
    msg.setSubject("Test email");
    msg.setSentDate(new Date());
    MimeBodyPart mimeBodyPart = new MimeBodyPart();
    mimeBodyPart.setContent("If you receive this email, it means that the server is working correctly.",
      "text; charset=utf-8");


    Thread thread = new Thread() {

      @Override
      public void run() {


        sent = false;
        int i = 0;
        while (!sent) {
          try {
            Transport.send(sendMail);
            LOG.info("Message sent TRIED#: " + i + " \n" + "Test email");
            sent = true;

          } catch (MessagingException e) {
            LOG.info("Message  DON'T sent: \n" + "Test email");

            i++;
            if (i == 10) {
              break;

            }
            try {
              Thread.sleep(1 * // minutes to sleep
              60 * // seconds to a minute
              1000);
            } catch (InterruptedException e1) {

              e1.printStackTrace();
            }
            e.printStackTrace();
          }

        }

      };
    };

    thread.run();

    if (sent) {
      return SUCCESS;
    } else {
      return INPUT;
    }
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    try {
      this.fail = Boolean.valueOf(StringUtils.trim(parameters.get("fail").getMultipleValues()[0]));
    } catch (Exception e) {
      this.fail = false;
    }
  }

}
