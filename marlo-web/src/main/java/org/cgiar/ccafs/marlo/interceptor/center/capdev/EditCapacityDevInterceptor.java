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

package org.cgiar.ccafs.marlo.interceptor.center.capdev;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.User;

import java.io.Serializable;
import java.util.Map;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

public class EditCapacityDevInterceptor extends AbstractInterceptor implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -6274702832951538448L;

  private Map<String, Parameter> parameters;
  private final ICapacityDevelopmentService capacityDevelopmentService;
  private Map<String, Object> session;


  private long capdevID = -1;

  @Inject
  public EditCapacityDevInterceptor(ICapacityDevelopmentService capacityDevelopmentService) {
    this.capacityDevelopmentService = capacityDevelopmentService;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {


    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();


    try {
      capdevID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.CAPDEV_ID).getMultipleValues()[0]));
    } catch (final Exception e) {
      return BaseAction.NOT_FOUND;
    }

    try {
      this.setPermissionParameters(invocation);
      return invocation.invoke();
    } catch (final Exception e) {
      return BaseAction.NOT_FOUND;
    }
  }

  public void setPermissionParameters(ActionInvocation invocation) throws Exception {


    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    BaseAction baseAction = (BaseAction) invocation.getAction();
    CapacityDevelopment capdev = capacityDevelopmentService.getCapacityDevelopmentById(capdevID);


    if (capdev != null) {

      capdevID = capdev.getId();

      if (baseAction.canAccessSuperAdmin()) {
        canEdit = true;
      }

      User currentUser = (User) session.get(APConstants.SESSION_USER);

      if (capdev.getCreatedBy().getId().equals(currentUser.getId())) {
        canEdit = true;
      }


      if (parameters.get(APConstants.EDITABLE_REQUEST) != null) {
        String stringEditable = parameters.get(APConstants.EDITABLE_REQUEST).getMultipleValues()[0];
        editParameter = stringEditable.equals("true");
        // If the user is not asking for edition privileges we don't need to validate them.
        if (!editParameter) {
          baseAction.setEditableParameter(hasPermissionToEdit);
        }
      }

      // Check the permission if user want to edit or save the form
      if (editParameter || (parameters.get("save") != null)) {
        hasPermissionToEdit =
          (baseAction.canAccessSuperAdmin()) ? true : capdev.getCreatedBy().getId().equals(currentUser.getId());

      }

      // Set the variable that indicates if the user can edit the section
      baseAction.setEditableParameter(hasPermissionToEdit && canEdit);
      baseAction.setCanEdit(canEdit);

    } else {
      throw new Exception();
    }

  }


}
