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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that allows us to pass values from the DAOs to the AuditLogEventListener.
 * This class and framework exists because Hibernate event listeners and Hibernate interceptors are outside the scope of
 * dependency injection frameworks.
 * 
 * @author GrantL
 */
public final class AuditLogContextProvider {

  public static Logger LOG = LoggerFactory.getLogger(AuditLogContextProvider.class);
  //
  private static final ThreadLocal<AuditLogContext> auditLogContextMap = new ThreadLocal<AuditLogContext>();

  public static AuditLogContext getAuditLogContext() {
    if (auditLogContextMap.get() == null) {
      String errorMessage = "No AuditLogContext has been pushed to the thread";
      LOG.error(errorMessage);
      throw new RuntimeException(errorMessage);
    } ;
    return auditLogContextMap.get();
  }

  public static void pop() {
    auditLogContextMap.remove();
  }

  public static void push(AuditLogContext context) {
    auditLogContextMap.set(context);
  }


}
