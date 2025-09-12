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

package org.cgiar.ccafs.marlo.interceptor.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.NoPhaseException;

import java.io.Serializable;
import java.util.Map;
import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditAiInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 1423197153747668108L;

  private Map<String, Parameter> parameters;
  private Map<String, Object> session;
  private GlobalUnit crp;
  private GlobalUnit loggedCrp;
  private Phase phase;

  private final GlobalUnitManager crpManager;
  private final PhaseManager phaseManager;
  private final GlobalUnitProjectManager globalUnitProjectManager;

  @Inject
  public EditAiInterceptor(ProjectManager projectManager, GlobalUnitManager crpManager, PhaseManager phaseManager,
    GlobalUnitProjectManager globalUnitProjectManager) {
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws NoPhaseException {

    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();
    crp = (GlobalUnit) session.get(APConstants.SESSION_CRP);
    try {
      this.setPermissionParameters(invocation);
      return invocation.invoke();
    } catch (Exception e) {
      e.printStackTrace();
      return BaseAction.NOT_FOUND;
    }
  }

  void setPermissionParameters(ActionInvocation invocation) throws Exception {
    BaseAction baseAction = (BaseAction) invocation.getAction();
    baseAction.setBasePermission(null);
    baseAction.setSession(session);
    loggedCrp = (GlobalUnit) session.get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    baseAction.setSession(session);
    phase = baseAction.getActualPhase();
    phase = phaseManager.getPhaseById(phase.getId());

    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    boolean isAdmin = false;

    String params[] = {crp.getAcronym(), baseAction.getActionName().replaceAll(crp.getAcronym() + "/", "")};

    if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin()) {
      isAdmin = true;
      canEdit = true;
    } else {
      if (baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__PERMISSION, params))) {
        canEdit = true;
      }

      if (baseAction.isCrpClosed()) {
        if (!(baseAction.hasSpecificities(APConstants.CRP_PMU) && baseAction.isPMU())) {
          canEdit = false;
        }
      }
    }

    // TODO Validate is the project is new
    if (parameters.get(APConstants.EDITABLE_REQUEST).isDefined()) {
      String stringEditable = parameters.get(APConstants.EDITABLE_REQUEST).getMultipleValues()[0];
      editParameter = stringEditable.equals("true");
      if (!editParameter) {
        baseAction.setEditableParameter(hasPermissionToEdit);
      }
    }

    // Check the permission if user want to edit or save the form
    if (editParameter || parameters.get("save").isDefined()) {
      hasPermissionToEdit = ((baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin())) ? true
        : baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__PERMISSION, params));
    }

    // If the user is not asking for edition privileges we don't need to validate them.
    if (!baseAction.getActualPhase().getEditable()) {
      canEdit = false;
      baseAction.setCanEditPhase(false);
    }

    if (!editParameter) {
      baseAction.setEditStatus(false);
    }
    // Set the variable that indicates if the user can edit the section
    baseAction.setEditableParameter(editParameter && canEdit && baseAction.getActualPhase().getEditable());
    baseAction.setCanEdit(canEdit);
    baseAction.setEditStatus(baseAction.isEditStatus());

    // Allow edit permissions
    if ((baseAction.canAccessSuperAdmin() || isAdmin || baseAction.isRole("PC") || baseAction.isRole("PL"))
      && editParameter) {
      baseAction.setEditableParameter(true);
      baseAction.setCanEdit(true);
      baseAction.setEditStatus(true);
    }
  }
}