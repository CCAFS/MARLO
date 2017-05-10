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

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadSendMail extends Thread {

  private static final Logger LOG = LoggerFactory.getLogger(ThreadSendMail.class);
  private Message sendeMail;
  private String subject;

  public ThreadSendMail(Message sendeMail, String subject) {
    this.sendeMail = sendeMail;
    this.subject = subject;

  }

  @Override
  public void run() {
    boolean sent = false;
    int i = 0;
    while (!sent) {
      try {
        Transport.send(sendeMail);
        LOG.info("Message sent: \n" + subject);
        sent = true;

      } catch (MessagingException e) {
        LOG.info("Message  DON'T sent: \n" + subject);

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

  }
}
