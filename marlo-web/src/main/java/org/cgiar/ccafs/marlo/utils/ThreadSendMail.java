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

import org.cgiar.ccafs.marlo.data.manager.EmailLogManager;
import org.cgiar.ccafs.marlo.data.model.EmailLog;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadSendMail extends Thread {

  private static final Logger LOG = LoggerFactory.getLogger(ThreadSendMail.class);
  // Attempts per server before giving up on it.
  private static final int MAX_ATTEMPTS = 10;
  private Message sendeMail;
  private Message backupsendeMail;
  private String subject;

  private EmailLogManager emailLogManager;
  private EmailLog emailLog;
  private APConfig config;
  // Failed attempts across both servers, recorded as the tried count of the log row.
  private int attempts;

  public ThreadSendMail(Message sendeMail, String subject, EmailLogManager emailLogManager, EmailLog emailLog,
    APConfig config) {
    this.sendeMail = sendeMail;
    this.subject = subject;
    this.emailLogManager = emailLogManager;
    this.emailLog = emailLog;
    this.config = config;
  }

  /**
   * Saves the log of a message. EmailLogManager.saveEmailLog is @Transactional, so Spring opens, commits and closes
   * a Hibernate session of its own even though this plain Thread has none bound. Opening and beginning one here as
   * well made Spring fail with "Transaction already active". Nothing is rethrown: by this point the send is over,
   * and letting a failure of the log escape would kill the thread.
   *
   * @param log the entry to save.
   */
  // Package-private so ThreadSendMailTest can capture the row instead of writing it.
  void persistEmailLog(EmailLog log) {
    try {
      emailLogManager.saveEmailLog(log);
    } catch (Exception e) {
      LOG.error("Could not save the log of the message '{}'", subject, e);
    }
  }

  /**
   * Attempts a message up to MAX_ATTEMPTS times, one minute apart.
   *
   * @param message the message to hand to the mail server.
   * @param label what the message is, for the log.
   * @return null when it was sent, or the error of the last attempt.
   */
  private String sendWithRetries(Message message, String label) {
    int failures = 0;
    while (true) {
      try {
        Transport.send(message);
        LOG.info("The {} '{}' was sent after {} failed attempts", label, subject, failures);
        return null;
      } catch (MessagingException e) {
        failures++;
        attempts++;
        if (failures == MAX_ATTEMPTS) {
          LOG.error("The {} '{}' could not be sent after {} attempts", label, subject, failures, e);
          return e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
        }
        LOG.warn("Attempt {} to send the {} '{}' failed", failures, label, subject, e);
        this.pause(label);
      }
    }
  }

  /**
   * Waits one minute between send attempts. Package-private so ThreadSendMailTest can skip the wait.
   */
  void pause(String label) {
    try {
      Thread.sleep(1 * // minutes to sleep
        60 * // seconds to a minute
        1000);
    } catch (InterruptedException e1) {
      LOG.warn("The wait between send attempts of the {} '{}' was interrupted", label, subject, e1);
    }
  }

  /**
   * Writes the one log row of this email, once its outcome is known. A delivered email drops its attachment,
   * which is only kept so the failed-email retry can resend it.
   */
  private void recordOutcome(boolean sent, String error) {
    emailLog.setTried(attempts);
    emailLog.setSucces(sent);
    emailLog.setError(error);
    if (sent) {
      emailLog.setFileContent(null);
    }
    this.persistEmailLog(emailLog);
  }

  /**
   * Sends the message through the main server and, when that fails, through the backup server, and logs the email
   * in a single row. The main failure used to be logged as a row of its own before the backup was tried, so an email
   * the backup delivered stayed marked as failed and the retry of System Admin -> Emails sent it a second time. The
   * backup outcome went to a second row that lost the date and the global unit of the original.
   */
  @Override
  public void run() {
    AuditLogContextProvider.push(new AuditLogContext());
    String mainError = this.sendWithRetries(sendeMail, "message");
    if (mainError == null) {
      this.recordOutcome(true, null);
      return;
    }

    LOG.info("Sending the backup copy of the message '{}'", subject);
    Properties backupproperties = System.getProperties();
    backupproperties.put("mail.debug", "true");
    backupproperties.put("mail.smtp.host", config.getEmailHostbackup());
    backupproperties.put("mail.smtp.port", config.getEmailPortbackup());
    backupproperties.put("mail.smtp.auth", config.getEmail_authBackup());
    backupproperties.put("mail.smtp.starttls.enable", config.getEmail_starttlsbackup());

    Session backupsession = Session.getInstance(backupproperties, new Authenticator() {

      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(config.getEmail_user_backup(), config.getEmail_password_backup());
      }
    });
    MimeMessage msgbackup = new MimeMessage(backupsession) {

      @Override
      protected void updateMessageID() throws MessagingException {
        if (this.getHeader("Message-ID") == null) {
          super.updateMessageID();
        }
      }
    };
    try {

      msgbackup.saveChanges();
    } catch (MessagingException e1) {
      LOG.error("Could not save the changes of the backup message '{}'", subject, e1);
    }
    try {
      if (!config.isProduction()) {
        msgbackup.setRecipients(Message.RecipientType.TO, sendeMail.getRecipients(Message.RecipientType.TO));
      } else {
        msgbackup.setRecipients(Message.RecipientType.TO, sendeMail.getRecipients(Message.RecipientType.TO));
        msgbackup.setRecipients(Message.RecipientType.CC, sendeMail.getRecipients(Message.RecipientType.CC));
      }
      try {
        msgbackup.setFrom(new InternetAddress(config.getEmail_notificaction_backup()));
      } catch (AddressException e) {
        msgbackup.setFrom((InternetAddress) null);
        LOG.error("Could not set the FROM address of the backup message '{}'", subject, e);
      }
      msgbackup.setRecipients(Message.RecipientType.BCC, sendeMail.getRecipients(Message.RecipientType.BCC));
      msgbackup.setSubject(sendeMail.getSubject());
      msgbackup.setSentDate(sendeMail.getSentDate());
      msgbackup.setContent((MimeMultipart) sendeMail.getContent());
    } catch (MessagingException e) {
      LOG.error("Could not build the backup message '{}'", subject, e);

    } catch (Exception e) {
      LOG.error("There was an unexpected error building the backup message '{}'", subject, e);
    }

    String backupError = this.sendWithRetries(msgbackup, "backup message");
    if (backupError == null) {
      // The row now describes the copy that was actually delivered.
      try {
        emailLog.setMessageID(msgbackup.getMessageID());
      } catch (MessagingException e1) {
        LOG.error("Could not read the id of the backup message '{}'", subject, e1);
      }
      this.recordOutcome(true, "Delivered by the backup server after the main server failed: " + mainError);
    } else {
      this.recordOutcome(false, "Main server: " + mainError + " Backup server: " + backupError);
    }
  }
}
