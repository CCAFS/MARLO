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
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
      BaseAction action = (BaseAction) invocation.getAction();
      // set the session to the BaseAction.
      action.setSession(session);
      if (action.getActualPhase() != null) {
        return invocation.invoke();
      } else {
        /**
         * This is problematic as any exceptions caught in the action.getActualPhase method, return
         * a default phase.
         */
        return this.sendUserToLoginScreen(session);
      }
    } else {
      return this.sendUserToLoginScreen(session);
    }

  }

  private String sendUserToLoginScreen(Map<String, Object> session) {
    // Clear the session - to avoid a half complete session.
    session.clear();
    return BaseAction.NOT_LOGGED;
  }

}
