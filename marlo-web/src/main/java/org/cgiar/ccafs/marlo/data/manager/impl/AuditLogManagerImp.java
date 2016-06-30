/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.data.IAuditLog;
import org.cgiar.ccafs.marlo.data.dao.AuditLogDao;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;

import com.google.inject.Inject;

public class AuditLogManagerImp implements AuditLogManager {


  private AuditLogDao auditLogDao;

  @Inject
  public AuditLogManagerImp(AuditLogDao auditLogDao) {
    this.auditLogDao = auditLogDao;
  }

  @Override
  public IAuditLog getHistory(long transactionID) {

    return auditLogDao.getHistory(transactionID);
  }

}
