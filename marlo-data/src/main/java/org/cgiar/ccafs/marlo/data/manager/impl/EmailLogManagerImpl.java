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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.EmailLogDAO;
import org.cgiar.ccafs.marlo.data.manager.EmailLogManager;
import org.cgiar.ccafs.marlo.data.model.EmailLog;
import org.cgiar.ccafs.marlo.data.model.EmailLogSearch;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Christian Garcia
 */
@Named
public class EmailLogManagerImpl implements EmailLogManager {


  private EmailLogDAO emailLogDAO;
  // Managers


  @Inject
  public EmailLogManagerImpl(EmailLogDAO emailLogDAO) {
    this.emailLogDAO = emailLogDAO;


  }

  @Override
  public long count(EmailLogSearch search) {
    return emailLogDAO.count(search);
  }

  @Override
  public List<Long> findGlobalUnitIds() {
    return emailLogDAO.findGlobalUnitIds();
  }

  @Override
  public List<String> findSourceActions() {
    return emailLogDAO.findSourceActions();
  }

  @Override
  public List<EmailLog> findSummaries(EmailLogSearch search, int first, int max) {
    return emailLogDAO.findSummaries(search, first, max);
  }

  @Override
  public List<EmailLog> search(EmailLogSearch search) {
    return emailLogDAO.search(search);
  }

  @Override
  @Transactional
  public void deleteEmailLog(long emailLogId) {

    emailLogDAO.deleteEmailLog(emailLogId);
  }

  @Override
  public boolean existEmailLog(long emailLogID) {

    return emailLogDAO.existEmailLog(emailLogID);
  }

  @Override
  public List<EmailLog> findAll() {

    return emailLogDAO.findAll();

  }

  @Override
  public EmailLog getEmailLogById(long emailLogID) {

    return emailLogDAO.find(emailLogID);
  }

  /**
   * Transactional so an update of an existing row is flushed: the failed-email retry marks the rows it resends,
   * and without a transaction the request session stays in FlushMode.MANUAL, so the row kept succes_email = 0 and
   * the next retry sent the same email again.
   */
  @Override
  @Transactional
  public EmailLog saveEmailLog(EmailLog emailLog) {

    return emailLogDAO.save(emailLog);
  }


}
