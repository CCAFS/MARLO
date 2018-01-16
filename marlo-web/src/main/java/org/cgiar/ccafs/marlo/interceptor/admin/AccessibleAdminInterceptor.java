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

package org.cgiar.ccafs.marlo.interceptor.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;

import java.io.Serializable;
import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class AccessibleAdminInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 2827118230094764204L;


  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    BaseAction baseAction = (BaseAction) invocation.getAction();
    Map<String, Object> session = invocation.getInvocationContext().getSession();
    baseAction = (BaseAction) invocation.getAction();
    baseAction.setSession(session);
    if (baseAction.canAccessSuperAdmin() || baseAction.canAcessCrpAdmin()) {
      return invocation.invoke();
    } else {
      return BaseAction.NOT_AUTHORIZED;
    }
  }

}
