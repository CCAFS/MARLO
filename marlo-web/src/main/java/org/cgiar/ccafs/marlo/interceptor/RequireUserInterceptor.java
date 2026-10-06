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

package org.cgiar.ccafs.marlo.interceptor;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.logging.LogContext;

import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cgiar.ccafs.marlo.utils.AuditLogContext;
import org.cgiar.ccafs.marlo.utils.AuditLogContextProvider;

/**
 * This interceptor is responsible for validating if the user is actually logged or not, in order to be able to access
 * the contents of the specified page.
 * If there is no an user in the current session it will return a 401 error (Authentication Required).
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Héctor Fabio Tobón R.
 */
public class RequireUserInterceptor extends AbstractInterceptor {

  private static final long serialVersionUID = 6570189216694718785L;

  private static final Logger LOG = LoggerFactory.getLogger(RequireUserInterceptor.class);

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    LOG.debug("=> RequireUserInterceptor");
    Map<String, Object> session = invocation.getInvocationContext().getSession();
    User user = (User) session.get(APConstants.SESSION_USER);
    if (user != null && user.getId() != null) {

        AuditLogContext auditContext = new AuditLogContext(); 
        auditContext.setCurrentUserId(user.getId());
        AuditLogContextProvider.push(auditContext); 

        try {
            // Inside the try, so that the pop in the finally block can never be skipped
            this.putLogContext(user, session);
            BaseAction action = (BaseAction) invocation.getAction();

            action.setSession(session);
              
            if (action.getActualPhase() != null) {
                String result = invocation.invoke();
                LOG.debug("=> RequireUserInterceptor");
                  return result;
            } else {
                return this.sendUserToLoginScreen(session);
              }
          } finally {
              AuditLogContextProvider.pop();
              LOG.debug("<= RequireUserInterceptor");
          }
      } else {
        return this.sendUserToLoginScreen(session);
      }
  }

  /**
   * Puts the user id, the user's name (logged only if the request fails) and the session Global Unit in the log
   * context. LogContext, as request listener, clears it when the request ends. Any failure is ignored, because the log
   * context must never stop the request.
   */
  private void putLogContext(User user, Map<String, Object> session) {
    try {
      LogContext.putUserId(user.getId());
      LogContext.putUserName(user);
      Object globalUnit = session.get(APConstants.SESSION_CRP);
      if (globalUnit instanceof GlobalUnit) {
        LogContext.putGlobalUnit((GlobalUnit) globalUnit);
      }
    } catch (RuntimeException e) {
      LOG.debug("Could not put the user in the log context", e);
    }
  }

  private String sendUserToLoginScreen(Map<String, Object> session) {
    // Clear the session - to avoid a half complete session.
    session.clear();
    return BaseAction.NOT_LOGGED;
  }

}
