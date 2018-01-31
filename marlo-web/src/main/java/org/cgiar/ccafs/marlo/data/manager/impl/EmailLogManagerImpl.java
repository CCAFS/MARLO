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

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

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

  @Override
  public EmailLog saveEmailLog(EmailLog emailLog) {

    return emailLogDAO.save(emailLog);
  }


}
