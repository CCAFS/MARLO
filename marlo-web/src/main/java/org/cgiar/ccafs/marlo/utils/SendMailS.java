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
package org.cgiar.ccafs.marlo.utils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SendMailS {

  // LOG
  private static final Logger LOG = LoggerFactory.getLogger(SendMailS.class);

  // Managers
  private APConfig config;

  @Inject
  public SendMailS(APConfig config) {
    this.config = config;
  }

  /**
   * This method send an email from the main email system.
   * 
   * @param toEmail is the email or the list of emails separated by a single space. This parameter can be null.
   * @param ccEmail is the email or the list of emails separated by a single space that will be as CC. This parameter
   *        can be null.
   * @param bbcEmail is the email or the list of emails separated by a single space that will be in BBC. This parameter
   *        can be null.
   * @param subject is the email title.
   * @param messageContent the content of the email
   * @param attachement is a byte array with the file to be attached.
   * @param attachmentMimeType is the MIME Type
   * @param is the name of the file
   */
  public void send(String toEmail, String ccEmail, String bbcEmail, String subject, String messageContent,
    byte[] attachment, String attachmentMimeType, String fileName, boolean isHtml) {

    // Get a Properties object
    Properties properties = System.getProperties();
    if (ccEmail != null) {
      ccEmail = ccEmail.replaceAll(", " + toEmail, "");

    }
    // properties.put("mail.smtp.auth", "true");
    // properties.put("mail.smtp.starttls.enable", "true");
    // properties.put("mail.smtp.ssl.trust", config.getEmailHost());
    properties.put("mail.smtp.host", config.getEmailHost());
    properties.put("mail.smtp.port", config.getEmailPort());


    // Un-comment this line to watch javaMail debug
    // properties.put("mail.debug", "true");


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

    try {
      msg.saveChanges();
    } catch (MessagingException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }

    // Set the FROM and TO fields
    try {
      if (!config.isProduction()) {
        // Adding TEST words.
        // Set the Test Header to list the emails that will send in production
        StringBuilder testingHeader = new StringBuilder();
        testingHeader.append("To: " + toEmail + "<br>");
        testingHeader.append("CC: " + ccEmail + "<br>");
        testingHeader.append("BBC: " + bbcEmail + "<br>");
        testingHeader.append("----------------------------------------------------<br><br>");
        subject = "TEST " + subject;
        messageContent = testingHeader.toString() + messageContent;
        // if (toEmail != null) {
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(bbcEmail, false));
        LOG.info("   - TO: " + bbcEmail);
        // }
      } else {
        if (toEmail != null) {
          msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
          LOG.info("   - TO: " + toEmail);
        }
        if (ccEmail != null) {
          msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
          LOG.info("   - CC: " + ccEmail);
        }
      }
      msg.setFrom(new InternetAddress(config.getEmailHost(), "MARLO Platform"));
      if (bbcEmail != null) {
        msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bbcEmail, false));
        LOG.info("   - BBC: " + bbcEmail);
      }


      msg.setSubject(subject);
      msg.setSentDate(new Date());

      MimeMultipart mimeMultipart = new MimeMultipart("alternative");

      // Body content: TEXT
      MimeBodyPart mimeBodyPart = new MimeBodyPart();
      if (isHtml) {
        mimeBodyPart.setContent(messageContent, "text/html; charset=utf-8");
      } else {
        mimeBodyPart.setContent(messageContent, "text; charset=utf-8");
      }

      mimeMultipart.addBodyPart(mimeBodyPart);

      if (attachment != null && attachmentMimeType != null && fileName != null) {
        // Body content: ATTACHMENT
        DataSource dataSource = new ByteArrayDataSource(attachment, attachmentMimeType);
        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.setDataHandler(new DataHandler(dataSource));
        attachmentBodyPart.setFileName(fileName);
        mimeMultipart.addBodyPart(attachmentBodyPart);
      }

      LOG.info("Message ID: \n" + msg.getMessageID());
      msg.setContent(mimeMultipart);
      ThreadSendMail thread = new ThreadSendMail(msg, subject);
      thread.start();


    } catch (MessagingException e) {
      e.printStackTrace();
      LOG.error("There was an error sending a message", e.getMessage());
    } catch (UnsupportedEncodingException e) {
      LOG.error("There was an error setting up the FROM Email when trying to send a message", e.getMessage());
    }
  }

}
