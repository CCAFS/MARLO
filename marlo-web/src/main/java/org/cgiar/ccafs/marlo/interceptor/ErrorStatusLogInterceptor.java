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
import org.cgiar.ccafs.marlo.logging.LogContext;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs the requests that end in the 401, 403 or 404 result, with that status in the log context. Struts renders those
 * results with HTTP 200, and they are returned from dozens of interceptors and actions, so the status is taken from the
 * result code instead: a PreResultListener receives it right before the result runs, whether an action or an
 * interceptor that stopped the chain returned it, and also when the exception mapping turned a Shiro
 * AuthorizationException into 403.
 * 401 and 404 are logged at INFO: an expired session and a missing record are routine. 403 is logged at WARN when the
 * session has a user, who was denied something, and at INFO otherwise. A code the action has no result for (the json
 * package maps no 401 or 403) is logged without a status: Struts then fails the request, so that status is never sent.
 * Logging never stops the request.
 * It must be the first interceptor of every stack, so that it sees the result of every interceptor after it.
 */
public class ErrorStatusLogInterceptor extends AbstractInterceptor {

  private static final long serialVersionUID = 2817305583204816215L;

  private static final Logger LOG = LoggerFactory.getLogger(ErrorStatusLogInterceptor.class);

  private static final PreResultListener ERROR_STATUS_LISTENER = ErrorStatusLogInterceptor::logErrorStatus;

  /**
   * Whether the action has a result for the code, its own or a global one. When the configuration cannot be read, the
   * result is assumed to exist, which is the case of every MARLO page action.
   */
  private static boolean hasResult(ActionInvocation invocation, String resultCode) {
    try {
      return invocation.getProxy().getConfig().getResults().containsKey(resultCode);
    } catch (RuntimeException e) {
      LOG.debug("Could not read the results of the action", e);
      return true;
    }
  }

  /**
   * Whether the session has a user. A session that cannot be read counts as one without a user.
   */
  private static boolean hasSessionUser(ActionInvocation invocation) {
    try {
      Map<String, Object> session = invocation.getInvocationContext().getSession();
      return session != null && session.get(APConstants.SESSION_USER) != null;
    } catch (RuntimeException e) {
      LOG.debug("Could not read the session user", e);
      return false;
    }
  }

  private static void logErrorStatus(ActionInvocation invocation, String resultCode) {
    try {
      int statusCode;
      if (BaseAction.NOT_LOGGED.equals(resultCode)) {
        statusCode = HttpServletResponse.SC_UNAUTHORIZED;
      } else if (BaseAction.NOT_AUTHORIZED.equals(resultCode)) {
        statusCode = HttpServletResponse.SC_FORBIDDEN;
      } else if (BaseAction.NOT_FOUND.equals(resultCode)) {
        statusCode = HttpServletResponse.SC_NOT_FOUND;
      } else {
        return;
      }
      if (!hasResult(invocation, resultCode)) {
        LOG.info("The action returned {} but has no result for it, so Struts fails the request", resultCode);
        return;
      }
      boolean deniedUser = statusCode == HttpServletResponse.SC_FORBIDDEN && hasSessionUser(invocation);
      LogContext.putStatusCode(statusCode);
      if (statusCode == HttpServletResponse.SC_UNAUTHORIZED) {
        LOG.info("Answered 401: the request needs a logged user");
      } else if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
        LOG.info("Answered 404: the requested page or record does not exist");
      } else if (deniedUser) {
        LOG.warn("Answered 403: the user is not allowed to access this page");
      } else {
        LOG.info("Answered 403: the request has no logged user");
      }
    } catch (RuntimeException e) {
      LOG.debug("Could not log the error status of the request", e);
    }
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    invocation.addPreResultListener(ERROR_STATUS_LISTENER);
    return invocation.invoke();
  }

}
