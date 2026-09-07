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

/**
 * Class that allows us to pass values from the DAOs to the AuditLogEventListener.
 * This class and framework exists because Hibernate event listeners and Hibernate interceptors are outside the scope of
 * dependency injection frameworks.
 * 
 * @author GrantL
 */
public final class AuditLogContextProvider {

  private static final ThreadLocal<AuditLogContext> auditLogContextMap = new ThreadLocal<AuditLogContext>();

  /**
   * Returns the AuditLogContext bound to the current thread.
   *
   * @return the context pushed to this thread.
   * @throws IllegalStateException if no context has been pushed. This is a programming error in the calling code, not a
   *         recoverable condition: callers that can push their own context MUST check hasAuditLogContext() first
   *         instead of catching this exception.
   */
  public static AuditLogContext getAuditLogContext() {
    AuditLogContext context = auditLogContextMap.get();
    if (context == null) {
      throw new IllegalStateException("No AuditLogContext has been pushed to the thread");
    }
    return context;
  }

  /**
   * Tells whether the current thread already has an AuditLogContext, without throwing.
   *
   * @return true if a context has been pushed to this thread.
   */
  public static boolean hasAuditLogContext() {
    return auditLogContextMap.get() != null;
  }

  public static void pop() {
    auditLogContextMap.remove();
  }

  public static void push(AuditLogContext context) {
    auditLogContextMap.set(context);
  }


}
