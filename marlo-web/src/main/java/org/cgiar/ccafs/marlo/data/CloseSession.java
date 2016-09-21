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


package org.cgiar.ccafs.marlo.data;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseSession extends Thread {

  private static final Logger LOG = LoggerFactory.getLogger(CloseSession.class);
  private Session session;


  public Session getSession() {
    return session;
  }


  @Override
  public void run() {
    try {

      Thread.sleep(10000);
      session.disconnect();
      if (!session.isOpen()) {
        session.close();
      }

      session.connection().close();

    } catch (Exception e) {
    }

  }

  public void setSession(Session session) {
    this.session = session;
  }
}
