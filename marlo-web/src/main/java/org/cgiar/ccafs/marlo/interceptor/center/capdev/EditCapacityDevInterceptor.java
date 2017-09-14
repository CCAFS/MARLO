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
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.User;

import java.io.Serializable;
import java.util.Map;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class EditCapacityDevInterceptor extends AbstractInterceptor implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = -6274702832951538448L;

  private Map<String, Object> parameters;
  private final ICapacityDevelopmentService capacityDevelopmentService;
  private Map<String, Object> session;

  private Center researchCenter;
  private long capdevID = -1;

  @Inject
  public EditCapacityDevInterceptor(ICapacityDevelopmentService capacityDevelopmentService) {
    this.capacityDevelopmentService = capacityDevelopmentService;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();
    researchCenter = (Center) session.get(APConstants.SESSION_CENTER);

    try {
      capdevID = Long.parseLong(((String[]) parameters.get(APConstants.CAPDEV_ID))[0]);
    } catch (final Exception e) {
      return BaseAction.NOT_FOUND;
    }

    try {
      this.setPermissionParameters(invocation);
      return invocation.invoke();
    } catch (final Exception e) {
      System.out.println(e);
      return BaseAction.NOT_FOUND;
    }
  }

  public void setPermissionParameters(ActionInvocation invocation) throws Exception {

    System.out.println("setPermissionParameters");

    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    final BaseAction baseAction = (BaseAction) invocation.getAction();
    final CapacityDevelopment capdev = capacityDevelopmentService.getCapacityDevelopmentById(capdevID);


    if (capdev != null) {

      capdevID = capdev.getId();

      if (baseAction.canAccessSuperAdmin()) {
        canEdit = true;
        hasPermissionToEdit = true;
      }

      System.out.println("antes de equal user");
      System.out.println("capdev.getUsersByCreatedBy()" + capdev.getUsersByCreatedBy().getId() + " class "
        + capdev.getUsersByCreatedBy().getId().getClass());
      final User currentUser = (User) session.get(APConstants.SESSION_USER);
      System.out
        .println(" baseAction.getCurrentUser()" + currentUser.getId() + " class " + currentUser.getId().getClass());
      System.out.println(capdev.getUsersByCreatedBy().getId().equals(currentUser.getId()));


      if (capdev.getUsersByCreatedBy().getId().equals(currentUser.getId())) {
        System.out.println("equal user");
        canEdit = true;
        hasPermissionToEdit = true;
      }


      if (parameters.get(APConstants.EDITABLE_REQUEST) != null) {
        final String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];
        editParameter = stringEditable.equals("true");
        // If the user is not asking for edition privileges we don't need to validate them.
        if (!editParameter) {
          baseAction.setEditableParameter(hasPermissionToEdit);
        }
      }


      // Set the variable that indicates if the user can edit the section
      baseAction.setEditableParameter(hasPermissionToEdit && canEdit);
      baseAction.setCanEdit(canEdit);

    } else {
      throw new Exception();
    }

  }


}
