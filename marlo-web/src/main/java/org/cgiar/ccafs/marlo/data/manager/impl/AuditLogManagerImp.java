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

import org.cgiar.ccafs.marlo.data.IAuditLog;
import org.cgiar.ccafs.marlo.data.dao.AuditLogDao;
import org.cgiar.ccafs.marlo.data.dao.UserDAO;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.model.Auditlog;

import java.util.List;

import com.google.inject.Inject;

public class AuditLogManagerImp implements AuditLogManager {

  private final AuditLogDao auditLogDao;

  private final UserDAO userDao;

  @Inject
  public AuditLogManagerImp(AuditLogDao auditLogDao, UserDAO userDao) {
    this.auditLogDao = auditLogDao;
    this.userDao = userDao;
  }

  @Override
  public Auditlog getAuditlog(String transactionID) {
    return auditLogDao.getAuditlog(transactionID);
  }

  @Override
  public Auditlog getAuditlog(String transactionID, IAuditLog auditLog) {
    return auditLogDao.getAuditlog(transactionID, auditLog);
  }

  @Override
  public List<Auditlog> getCompleteHistory(String transactionID) {
    return auditLogDao.getCompleteHistory(transactionID);
  }

  @Override
  public IAuditLog getHistory(String transactionID) {

    return auditLogDao.getHistory(transactionID);
  }

  @Override
  public List<Auditlog> getHistoryBefore(String transactionID) {
    return auditLogDao.getHistoryBefore(transactionID);
  }

  @Override
  public List<Auditlog> getHistoryBeforeList(String transactionID, String className, String entityID) {

    return auditLogDao.getHistoryBeforeList(transactionID, className, entityID);
  }

  @Override
  public List<Auditlog> listLogs(Class<?> classAudit, long id, String actionName) {

    List<Auditlog> auditLogs = auditLogDao.findAllWithClassNameAndIdAndActionName(classAudit, id, actionName);

    for (Auditlog auditlog : auditLogs) {
      auditlog.setUser(userDao.getUser(auditlog.getUserId()));
    }

    if (auditLogs.size() > 11) {
      return auditLogs.subList(0, 11);
    }
    return auditLogs;
  }
}
