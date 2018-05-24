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
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;

import java.io.Serializable;
import java.util.Map;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.dispatcher.Parameter;

public class PublicationInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 1L;

  private Map<String, Parameter> parameters;
  private Map<String, Object> session;
  private GlobalUnit crp;
  private long deliverableID = 0;

  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private DeliverableManager deliverableManager;

  @Inject
  public PublicationInterceptor(GlobalUnitManager crpManager, DeliverableManager deliverableManager) {
    this.crpManager = crpManager;
    this.deliverableManager = deliverableManager;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {

    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();
    crp = (GlobalUnit) session.get(APConstants.SESSION_CRP);
    crp = crpManager.getGlobalUnitById(crp.getId());
    try {
      this.setPermissionParameters(invocation);
      return invocation.invoke();
    } catch (Exception e) {
      BaseAction action = (BaseAction) invocation.getAction();
      return action.NOT_FOUND;
    }
  }

  void setPermissionParameters(ActionInvocation invocation) {
    User user = (User) session.get(APConstants.SESSION_USER);
    BaseAction baseAction = (BaseAction) invocation.getAction();
    baseAction.setSession(session);
    boolean canEdit = false;
    boolean editParameter = false;

    String deliverableParameter = parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID).getMultipleValues()[0];
    deliverableID = Long.parseLong(deliverableParameter);
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
    boolean editableDefined = parameters.get(APConstants.EDITABLE_REQUEST).isDefined();

    if (deliverable != null) {
      String crpAcronymParam[] = {crp.getAcronym()};
      String publicationParams[] = {crp.getAcronym(), deliverable.getId() + ""};
      boolean hasPublicationFullPermission = baseAction
        .hasPermission(baseAction.generatePermission(Permission.PUBLICATION_FULL_PERMISSION, crpAcronymParam));
      boolean hasPublicationPermission =
        baseAction.hasPermission(baseAction.generatePermission(Permission.PUBLICATION_PERMISSION, publicationParams));
      boolean isCreator = user.getId().equals(deliverable.getCreatedBy().getId());
      boolean isInDeliverablePhase = deliverable.getPhase().getId() == baseAction.getActualPhase().getId();
      boolean isTransaction = parameters.get(APConstants.TRANSACTION_ID).isDefined();
      boolean isSaving = parameters.get("save").isDefined();

      if (!isInDeliverablePhase) {
        canEdit = false;
      } else {
        if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin()) {
          canEdit = true;
        } else {
          if (isCreator || hasPublicationFullPermission || hasPublicationPermission) {
            canEdit = true;
          }
          if (baseAction.isCrpClosed()) {
            canEdit = false;
          }
        }
      }

      if (canEdit) {
        if (editableDefined) {
          String stringEditable = parameters.get(APConstants.EDITABLE_REQUEST).getMultipleValues()[0];
          editParameter = stringEditable.equals("true") && canEdit;
        }
        if (isSaving) {
          editParameter = true;
        }
        // Set the variable that indicates if the user can edit the section
        baseAction.setCanEdit(canEdit);
        baseAction.setEditableParameter(editParameter && canEdit && !isTransaction);
      } else {
        baseAction.setCanEdit(false);
        baseAction.setEditableParameter(false);
      }

    } else {
      throw new NullPointerException();
    }
  }

}