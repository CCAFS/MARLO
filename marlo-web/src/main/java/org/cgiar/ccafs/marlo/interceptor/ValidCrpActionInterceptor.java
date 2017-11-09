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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;

import java.util.Map;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * This interceptor is responsible for validating if the crp entered in the action or url is valid.
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ValidCrpActionInterceptor extends AbstractInterceptor {

  private static final long serialVersionUID = 2239276003694732851L;

  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  @Inject
  public ValidCrpActionInterceptor(GlobalUnitManager crpManager) {
    this.crpManager = crpManager;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    String[] actionMap = ActionContext.getContext().getName().split("/");
    Map<String, Object> session = invocation.getInvocationContext().getSession();
    session.remove(APConstants.TEMP_CYCLE);
    if (actionMap.length > 1) {
      String enteredCrp = actionMap[0];
      GlobalUnit crp = crpManager.findGlobalUnitByAcronym(enteredCrp);
      if (crp != null) {
        return invocation.invoke();
      } else {
        return BaseAction.NOT_FOUND;
      }
    }
    return BaseAction.NOT_FOUND;
  }

}
