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

package org.cgiar.ccafs.marlo.interceptor.center;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterUserManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterCustomParameter;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.UserToken;

import java.util.Map;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * This interceptor is responsible for validating if the current crp url equals to the open session
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ValidSessionInterceptor extends AbstractInterceptor {

  private static final long serialVersionUID = -3706764472200123669L;

  private ICenterManager centerService;
  private ICenterUserManager userService;
  private Center looggedCenter;


  @Inject
  public ValidSessionInterceptor(ICenterManager centerService, ICenterUserManager userService) {
    this.centerService = centerService;
    this.userService = userService;
  }

  private void changeSessionSection(Map<String, Object> session) {

    UserToken userToken = new UserToken();
    userToken.setUser((User) session.get(APConstants.SESSION_USER));
    userToken.setSection(ActionContext.getContext().getName());
    if (session.containsKey(APConstants.USER_TOKEN)) {
      session.remove(APConstants.USER_TOKEN);
      session.put(APConstants.USER_TOKEN, userToken);
    } else {
      session.put(APConstants.USER_TOKEN, userToken);
    }
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    BaseAction action = (BaseAction) invocation.getAction();
    action.setSwitchSession(false);
    Map<String, Object> session = invocation.getInvocationContext().getSession();

    looggedCenter = (Center) session.get(APConstants.SESSION_CENTER);

    String[] actionMap = ActionContext.getContext().getName().split("/");
    if (actionMap.length > 1) {
      String enteredCrp = actionMap[0];
      Center center = centerService.findCrpByAcronym(enteredCrp);
      if (center != null) {
        // Change crp to center session; check if the user don't have center session
        if (looggedCenter != null) {
          looggedCenter = centerService.getCrpById(looggedCenter.getId());
        } else {
          looggedCenter = center;
          session.put(APConstants.SESSION_CENTER, looggedCenter);

          if (session.containsKey(APConstants.SESSION_CRP)) {

            Crp crp = (Crp) session.get(APConstants.SESSION_CRP);

            // remove the crp parameters
            for (CustomParameter parameter : crp.getCustomParameters()) {
              if (parameter.isActive()) {
                session.remove(parameter.getParameter().getKey());
              }
            }
            // Remove the crp session
            session.remove(APConstants.SESSION_CRP);
          }
          // add the center parameters
          for (CenterCustomParameter parameter : looggedCenter.getCenterCustomParameters()) {
            if (parameter.isActive()) {
              session.put(parameter.getCenterParameter().getKey(), parameter.getValue());
            }
          }

          action.setSwitchSession(true);
        }
        if (center.equals(looggedCenter)) {
          this.changeSessionSection(session);
          return invocation.invoke();
        } else {
          User user = (User) session.get(APConstants.SESSION_USER);
          if (userService.existCrpUser(user.getId(), center.getId())) {
            // for (CrpParameter parameter : loggedCrp.getCrpParameters()) {
            // if (parameter.isActive()) {
            // session.remove(parameter.getKey());
            // }
            // }
            session.replace(APConstants.SESSION_CENTER, center);
            // put the crp parameters in the session
            // for (CrpParameter parameter : crp.getCrpParameters()) {
            // if (parameter.isActive()) {
            // session.put(parameter.getKey(), parameter.getValue());
            // }
            // }
            this.changeSessionSection(session);
            return invocation.invoke();
          }
        }
      } else {
        return BaseAction.NOT_FOUND;
      }
    }
    return BaseAction.NOT_FOUND;
  }

}
