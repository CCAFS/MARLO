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
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.security.Permission;

import java.io.Serializable;
import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditCrpAdminInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = -6233803983753722378L;

  private Map<String, Object> parameters;
  private Map<String, Object> session;
  private GlobalUnit crp;

  public EditCrpAdminInterceptor() {
    super();
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {


    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();
    crp = (GlobalUnit) session.get(APConstants.SESSION_CRP);
    /*
     * if (!baseAction.canEditCrpAdmin()) {
     * return BaseAction.NOT_AUTHORIZED;
     * }
     */
    try {
      this.setPermissionParameters(invocation);
      return invocation.invoke();
    } catch (Exception e) {
      return BaseAction.NOT_FOUND;
    }
  }

  public void setPermissionParameters(ActionInvocation invocation) {
    BaseAction baseAction = (BaseAction) invocation.getAction();

    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;


    // If user is admin, it should have privileges to edit all projects.
    if (baseAction.canAccessSuperAdmin() && baseAction.canEditCrpAdmin()) {
      canEdit = true;
    } else {
      if (baseAction
        .hasPermission(baseAction.generatePermission(Permission.CRP_ADMIN_EDIT_PRIVILEGES, crp.getAcronym()))) {
        canEdit = true;
      }
    }

    if (parameters.get(APConstants.EDITABLE_REQUEST) != null) {
      String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];
      editParameter = stringEditable.equals("true");
      // If the user is not asking for edition privileges we don't need to validate them.
      if (!editParameter) {
        baseAction.setEditableParameter(hasPermissionToEdit);
      }
    }

    // Check the permission if user want to edit or save the form
    if (editParameter || parameters.get("save") != null) {
      hasPermissionToEdit = (baseAction.isAdmin()) ? true : baseAction
        .hasPermission(baseAction.generatePermission(Permission.CRP_ADMIN_EDIT_PRIVILEGES, crp.getAcronym()));
    }

    // Set the variable that indicates if the user can edit the section
    baseAction.setEditableParameter(editParameter && canEdit);
    baseAction.setCanEdit(canEdit);

  }


}
