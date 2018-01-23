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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.EmailLogDAO;
import org.cgiar.ccafs.marlo.data.model.EmailLog;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class EmailLogMySQLDAO extends AbstractMarloDAO<EmailLog, Long> implements EmailLogDAO {


  @Inject
  public EmailLogMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteEmailLog(long emailLogId) {
    EmailLog emailLog = this.find(emailLogId);
    emailLog.setActive(false);
    this.save(emailLog);
  }

  @Override
  public boolean existEmailLog(long emailLogID) {
    EmailLog emailLog = this.find(emailLogID);
    if (emailLog == null) {
      return false;
    }
    return true;

  }

  @Override
  public EmailLog find(long id) {
    return super.find(EmailLog.class, id);

  }

  @Override
  public List<EmailLog> findAll() {
    String query = "from " + EmailLog.class.getName() + " where is_active=1";
    List<EmailLog> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public EmailLog save(EmailLog emailLog) {
    if (emailLog.getId() == null) {
      super.saveEntity(emailLog);
    } else {
      emailLog = super.update(emailLog);
    }


    return emailLog;
  }


}