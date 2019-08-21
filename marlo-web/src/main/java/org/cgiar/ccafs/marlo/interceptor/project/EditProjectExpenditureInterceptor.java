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
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.dispatcher.Parameter;

public class EditProjectExpenditureInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = -6946851480092464011L;
  /**
   * 
   */
  private Map<String, Parameter> parameters;
  private Map<String, Object> session;
  private GlobalUnit crp;
  private long projectId = 0;
  private GlobalUnit loggedCrp;

  private Phase phase;
  private final GlobalUnitManager crpManager;
  private final ProjectManager projectManager;
  private final PhaseManager phaseManager;
  private final GlobalUnitProjectManager globalUnitProjectManager;
  private final LiaisonUserManager liaisonUserManager;

  @Inject
  public EditProjectExpenditureInterceptor(GlobalUnitManager crpManager, ProjectManager projectManager,
    PhaseManager phaseManager, GlobalUnitProjectManager globalUnitProjectManager,
    LiaisonUserManager liaisonUserManager) {
    super();
    this.crpManager = crpManager;
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.liaisonUserManager = liaisonUserManager;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
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

    User user = (User) session.get(APConstants.SESSION_USER);
    baseAction.setSession(session);
    phase = baseAction.getActualPhase();
    phase = phaseManager.getPhaseById(phase.getId());

    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    boolean canSwitchProject = false;
    boolean isAdmin = false;

    String projectParameter = parameters.get(APConstants.PROJECT_REQUEST_ID).getMultipleValues()[0];

    projectId = Long.parseLong(projectParameter);

    Project project = projectManager.getProjectById(projectId);
    GlobalUnitProject globalUnitProject =
      globalUnitProjectManager.findByProjectAndGlobalUnitId(project.getId(), loggedCrp.getId());
    if (globalUnitProject != null) {
      if (project != null && project.isActive()) {
        if (!globalUnitProject.isOrigin()) {
          GlobalUnitProject globalUnitProjectOrigin = globalUnitProjectManager.findByProjectId(project.getId());
          List<Phase> phases = globalUnitProjectOrigin.getGlobalUnit().getPhases().stream()
            .filter(c -> c.isActive() && c.getDescription().equals(baseAction.getActualPhase().getDescription())
              && c.getYear() == baseAction.getActualPhase().getYear() && c.getUpkeep())
            .collect(Collectors.toList());
          if (phases.size() > 0) {
            baseAction.setPhaseID(phases.get(0).getId());
            project.getProjecInfoPhase(phases.get(0));
          }
        }

        String params[] =
          {crp.getAcronym(), project.getId() + "", baseAction.getActionName().replaceAll(crp.getAcronym() + "/", "")};
        if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin()) {
          isAdmin = true;
          canEdit = true;
          editParameter = true;
        } else {
          if (!baseAction.hasSpecificities(APConstants.CRP_ENABLE_BUDGET_EXECUTION)) {
            canEdit = false;
          }
          if (!baseAction.isRole("FM")) {
            canEdit = false;
          }
          if (baseAction.hasPermission(
            baseAction.generatePermission(Permission.PROJECT_BUDGET_EXECUTION_CAN_EDIT_PERMISSION, params))) {
            canEdit = true;
            editParameter = true;

          }
          if (project.getProjecInfoPhase(baseAction.getActualPhase()).getPhase().getDescription()
            .equals(APConstants.REPORTING)) {
            canEdit = false;
          }


        }
        // Set the variable that indicates if the user can edit the section
        baseAction.setEditableParameter(editParameter && canEdit);
        baseAction.setCanSwitchProject(false);
        baseAction.setCanEdit(canEdit);
        baseAction.setEditStatus(baseAction.isEditStatus() && globalUnitProject.isOrigin());
      } else {

        throw new NullPointerException();
      }
    }
  }


}
