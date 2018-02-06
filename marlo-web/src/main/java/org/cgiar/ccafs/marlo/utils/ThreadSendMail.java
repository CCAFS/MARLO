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

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ThreadSendMail extends Thread {

  private static final Logger LOG = LoggerFactory.getLogger(ThreadSendMail.class);
  private Message sendeMail;
  private String subject;

  private EmailLogManager emailLogManager;
  private EmailLog emailLog;
  private SessionFactory sessionFactory;

  public ThreadSendMail(Message sendeMail, String subject, EmailLogManager emailLogManager, EmailLog emailLog,
    SessionFactory sessionFactory) {
    this.sendeMail = sendeMail;
    this.subject = subject;
    this.emailLogManager = emailLogManager;
    this.emailLog = emailLog;
    this.sessionFactory = sessionFactory;

  }

  @Override
  public void run() {
    boolean sent = false;

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
          e.printStackTrace();
          emailLog.setTried(i);
          emailLog.setSucces(false);
          emailLog.setError(e.getMessage());
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

          e1.printStackTrace();
        }
        e.printStackTrace();
      }

    }

  }
}
