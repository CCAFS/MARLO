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


package org.cgiar.ccafs.marlo.interceptor.publication;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;

import java.io.Serializable;
import java.util.Map;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class PublicationInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 1L;

  private Map<String, Object> parameters;
  private Map<String, Object> session;
  private Crp crp;
  private long deliverableID = 0;

  private CrpManager crpManager;
  private DeliverableManager deliverableManager;

  @Inject
  public PublicationInterceptor(CrpManager crpManager, DeliverableManager deliverableManager) {
    this.crpManager = crpManager;
    this.deliverableManager = deliverableManager;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {

    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();
    crp = (Crp) session.get(APConstants.SESSION_CRP);
    crp = crpManager.getCrpById(crp.getId());
    try {
      this.setPermissionParameters(invocation);
      return invocation.invoke();
    } catch (Exception e) {
      BaseAction action = (BaseAction) invocation.getAction();
      return action.NOT_FOUND;
    }
  }

  void setPermissionParameters(ActionInvocation invocation) {
    BaseAction baseAction = (BaseAction) invocation.getAction();
    User user = (User) session.get(APConstants.SESSION_USER);
    baseAction.clearPermissionsCache();

    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;

    String projectParameter = ((String[]) parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID))[0];

    deliverableID = Long.parseLong(projectParameter);

    Deliverable project = deliverableManager.getDeliverableById(deliverableID);

    if (project != null) {
      String params[] = {crp.getAcronym()};
      String paramDeliverableID[] = {crp.getAcronym(), project.getId() + ""};
      if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin()) {
        canEdit = true;
      } else {

        if (baseAction.hasPermission(baseAction.generatePermission(Permission.PUBLICATION_FULL_PERMISSION, params))
          || baseAction
            .hasPermission(baseAction.generatePermission(Permission.PUBLICATION_INSTITUTION, paramDeliverableID))) {
          canEdit = true;


        }

        if (baseAction.isCrpClosed()) {
          canEdit = false;
        }
      }


      if (parameters.get(APConstants.EDITABLE_REQUEST) != null) {
        String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];
        editParameter = stringEditable.equals("true");
        if (!editParameter) {
          baseAction.setEditableParameter(hasPermissionToEdit);
        }
      }
      if (editParameter || parameters.get("save") != null) {
        hasPermissionToEdit = ((baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin())) ? true
          : baseAction.hasPermission(baseAction.generatePermission(Permission.PUBLICATION_FULL_PERMISSION, params))
            || baseAction
              .hasPermission(baseAction.generatePermission(Permission.PUBLICATION_INSTITUTION, paramDeliverableID))

        ;
      }

      if (parameters.get(APConstants.EDITABLE_REQUEST) != null) {
        String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];
        editParameter = stringEditable.equals("true");
        if (!editParameter) {
          baseAction.setEditableParameter(hasPermissionToEdit);
        }
      }

      // Set the variable that indicates if the user can edit the section
      baseAction.setEditableParameter(hasPermissionToEdit && canEdit);
      baseAction.setCanEdit(canEdit);

    } else {
      throw new NullPointerException();
    }
  }

}