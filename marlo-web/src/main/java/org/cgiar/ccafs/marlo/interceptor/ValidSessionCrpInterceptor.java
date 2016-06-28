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

package org.cgiar.ccafs.marlo.interceptor;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.Map;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * This interceptor is responsible for validating if the current crp url equals to the open session
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ValidSessionCrpInterceptor extends AbstractInterceptor {

  private static final long serialVersionUID = -3706764472200123669L;

  private CrpManager crpManager;
  private CrpUserManager crpUserManager;
  private Crp loggedCrp;

  @Inject
  public ValidSessionCrpInterceptor(CrpManager crpManager, CrpUserManager crpUserManager) {
    this.crpManager = crpManager;
    this.crpUserManager = crpUserManager;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {

    Map<String, Object> session = invocation.getInvocationContext().getSession();
    loggedCrp = (Crp) session.get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    String[] actionMap = ActionContext.getContext().getName().split("/");
    if (actionMap.length > 1) {
      String enteredCrp = actionMap[0];
      Crp crp = crpManager.findCrpByAcronym(enteredCrp);
      if (crp != null) {
        if (crp.equals(loggedCrp)) {
          return invocation.invoke();
        } else {
          User user = (User) session.get(APConstants.SESSION_USER);
          if (crpUserManager.existCrpUser(user.getId(), crp.getId())) {
            session.remove(APConstants.SESSION_CRP);
            session.put(APConstants.SESSION_CRP, crp);
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
