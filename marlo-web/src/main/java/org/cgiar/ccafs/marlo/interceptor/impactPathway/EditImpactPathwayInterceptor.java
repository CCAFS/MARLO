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

package org.cgiar.ccafs.marlo.interceptor.impactPathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.security.Permission;

import java.io.Serializable;
import java.util.Map;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditImpactPathwayInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 1L;

  public static void setPermissionParameters(ActionInvocation invocation) {

    BaseAction baseAction = (BaseAction) invocation.getAction();

    Map<String, Object> parameters = invocation.getInvocationContext().getParameters();
    Map<String, Object> session = invocation.getInvocationContext().getSession();
    Crp crp = (Crp) session.get(APConstants.SESSION_CRP);

    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;


    // If user is admin, it should have privileges to edit all projects.
    if (baseAction.isAdmin()) {
      canEdit = true;
    } else {
      if (baseAction
        .hasPermission(baseAction.generatePermission(Permission.IMPACT_PATHWAY_EDIT_PRIVILEGES, crp.getAcronym()))) {
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
        .hasPermission(baseAction.generatePermission(Permission.IMPACT_PATHWAY_EDIT_PRIVILEGES, crp.getAcronym()));
    }

    // Set the variable that indicates if the user can edit the section
    baseAction.setEditableParameter(editParameter);
    baseAction.setCanEdit(canEdit);

  }


  public EditImpactPathwayInterceptor() {
    super();
  }


  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    try {
      setPermissionParameters(invocation);
      return invocation.invoke();
    } catch (Exception e) {
      return BaseAction.NOT_FOUND;
    }
  }

}
