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


package org.cgiar.ccafs.marlo.data.service;

import org.cgiar.ccafs.marlo.data.IAuditLog;
import org.cgiar.ccafs.marlo.data.model.Auditlog;
import org.cgiar.ccafs.marlo.data.service.impl.AuditLogManager;

import java.util.List;

import com.google.inject.ImplementedBy;

@ImplementedBy(AuditLogManager.class)
public interface IAuditLogManager {


  /**
   * Get the element history from AuditLog
   * 
   * @param transactionID the num of transaction ID save on table AuditLog
   * @return
   */
  public IAuditLog getHistory(String transactionID);

  /**
   * List of AuditLogs from the class of parameter that has the entity id
   * 
   * @param classAudit: the class we want to get logs
   * @param id the entity id
   */

  public List<Auditlog> listLogs(Class classAudit, long id, String actionName);
}
