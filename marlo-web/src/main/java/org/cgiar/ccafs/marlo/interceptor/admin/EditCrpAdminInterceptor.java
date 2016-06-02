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

package org.cgiar.ccafs.marlo.interceptor.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.security.BaseSecurityContext;
import org.cgiar.ccafs.marlo.security.Permission;

import java.io.Serializable;
import java.util.Map;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.ServletActionContext;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditCrpAdminInterceptor extends AbstractInterceptor implements Serializable {


  private static final long serialVersionUID = -6233803983753722378L;

  public static void setPermissionParameters(ActionInvocation invocation) {

    BaseAction baseAction = (BaseAction) invocation.getAction();

    Map<String, Object> parameters = invocation.getInvocationContext().getParameters();
    String[] actionName = ServletActionContext.getActionMapping().getName().split("/");

    Map<String, Object> session = invocation.getInvocationContext().getSession();
    Crp crp = (Crp) session.get(APConstants.SESSION_CRP);

    boolean canEdit = false;


    // If user is admin, it should have privileges to edit all projects.
    if (baseAction.isAdmin()) {
      canEdit = true;
    } else {
      if (baseAction
        .hasPermission(baseAction.generatePermission(Permission.CRP_ADMIN_EDIT_PRIVILEGES, crp.getAcronym()))) {
        canEdit = true;
      }
    }

    // Set the variable that indicates if the user can edit the section
    baseAction.setEditableParameter(canEdit);
    baseAction.setCanEdit(canEdit);

  }

  @Inject
  public EditCrpAdminInterceptor(BaseSecurityContext securityContext) {
    super();
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    try {
      setPermissionParameters(invocation);
      return invocation.invoke();
    } catch (Exception e) {
      BaseAction action = (BaseAction) invocation.getAction();
      return action.NOT_FOUND;
    }
  }


}
