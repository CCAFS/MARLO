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

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadSendMail extends Thread {

  private static final Logger LOG = LoggerFactory.getLogger(ThreadSendMail.class);
  private Message sendeMail;
  private Message backupsendeMail;
  private String subject;

  private EmailLogManager emailLogManager;
  private EmailLog emailLog;
  private SessionFactory sessionFactory;
  private APConfig config;

  public ThreadSendMail(Message sendeMail, String subject, EmailLogManager emailLogManager, EmailLog emailLog,
    SessionFactory sessionFactory, APConfig config) {
    this.sendeMail = sendeMail;
    this.subject = subject;
    this.emailLogManager = emailLogManager;
    this.emailLog = emailLog;
    this.sessionFactory = sessionFactory;
    this.config = config;
  }

  @Override
  public void run() {
    boolean sent = false;
    boolean reply = false;
    EmailLog emailLogBkup = new EmailLog();
    emailLogBkup.setBbc(emailLog.getBbc());
    emailLogBkup.setCc(emailLog.getCc());
    emailLogBkup.setTo(emailLog.getTo());

    emailLogBkup.setMessage(emailLog.getMessage());
    emailLogBkup.setSubject(emailLog.getSubject());
    if (emailLog.getFileName() != null) {
      emailLogBkup.setFileContent(emailLog.getFileContent());
      emailLogBkup.setFileName(emailLog.getFileName());
    }
    AuditLogContextProvider.push(new AuditLogContext());
    int i = 0;
    while (!sent) {
      try {

        Transport.send(sendeMail);
        LOG.info("Message sent TRIED#: " + i + " \n" + subject);
        sent = true;
        emailLog.setTried(i++);
        emailLog.setSucces(true);
        emailLog.setFileContent(null);
        sessionFactory.getCurrentSession().beginTransaction();
        emailLogManager.saveEmailLog(emailLog);
        sessionFactory.getCurrentSession().getTransaction().commit();

      } catch (MessagingException e) {
        LOG.info("Message  DID NOT sent: \n" + subject);

        i++;
        if (i == 10) {
          reply = true;
          LOG.error("The message '{}' could not be sent after {} attempts", subject, i, e);
          emailLog.setTried(i);
          emailLog.setSucces(false);
          emailLog.setError(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
          sessionFactory.getCurrentSession().beginTransaction();
          emailLogManager.saveEmailLog(emailLog);
          sessionFactory.getCurrentSession().getTransaction().commit();
          break;

        }
        try {
          Thread.sleep(1 * // minutes to sleep
            60 * // seconds to a minute
            1000);
        } catch (InterruptedException e1) {
          LOG.warn("The wait between send attempts of the message '{}' was interrupted", subject, e1);
        }
        LOG.warn("Attempt {} to send the message '{}' failed", i, subject, e);
      }

    }
    if (reply) {
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
      i = 0;
      try {
        emailLogBkup.setMessageID(msgbackup.getMessageID());
      } catch (MessagingException e1) {
        LOG.error("Could not read the id of the backup message '{}'", subject, e1);
      }
      while (!sent) {
        try {

          Transport.send(msgbackup);
          LOG.info("Backup Message sent TRIED#: " + i + " \n" + subject);
          sent = true;
          emailLogBkup.setTried(i++);
          emailLogBkup.setSucces(true);
          emailLogBkup.setFileContent(null);
          sessionFactory.getCurrentSession().beginTransaction();
          emailLogManager.saveEmailLog(emailLogBkup);
          sessionFactory.getCurrentSession().getTransaction().commit();

        } catch (MessagingException e) {
          LOG.info("Backup Message  DID NOT sent: \n" + subject);

          i++;
          if (i == 10) {
            LOG.error("The backup message '{}' could not be sent after {} attempts", subject, i, e);
            emailLogBkup.setTried(i);
            emailLogBkup.setSucces(false);
            emailLogBkup.setError(e.getCause() == null ? e.getMessage() : e.getCause().getMessage());
            sessionFactory.getCurrentSession().beginTransaction();
            emailLogManager.saveEmailLog(emailLogBkup);
            sessionFactory.getCurrentSession().getTransaction().commit();
            break;

          }
          try {
            Thread.sleep(1 * // minutes to sleep
              60 * // seconds to a minute
              1000);
          } catch (InterruptedException e1) {
            LOG.warn("The wait between send attempts of the backup message '{}' was interrupted", subject, e1);
          }
          LOG.warn("Attempt {} to send the backup message '{}' failed", i, subject, e);
        }
      }
    }

  }
}
