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
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.NoPhaseException;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditProjectInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 1423197153747668108L;

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

  @Inject
  public EditProjectInterceptor(ProjectManager projectManager, GlobalUnitManager crpManager, PhaseManager phaseManager,
    GlobalUnitProjectManager globalUnitProjectManager) {
    this.projectManager = projectManager;
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

    User user = (User) session.get(APConstants.SESSION_USER);
    baseAction.setSession(session);
    phase = baseAction.getActualPhase();
    phase = phaseManager.getPhaseById(phase.getId());

    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    boolean canSwitchProject = false;

    // this.setBasePermission(this.getText(Permission.PROJECT_DESCRIPTION_BASE_PERMISSION, params));


    // String projectParameter = ((String[]) parameters.get(APConstants.PROJECT_REQUEST_ID))[0];
    String projectParameter = parameters.get(APConstants.PROJECT_REQUEST_ID).getMultipleValues()[0];

    projectId = Long.parseLong(projectParameter);

    Project project = projectManager.getProjectById(projectId);

    // Get The Crp/Center/Platform where the project was created
    GlobalUnitProject globalUnitProject =
      globalUnitProjectManager.findByProjectAndGlobalUnitId(project.getId(), loggedCrp.getId());
    if (globalUnitProject != null) {

      if (project != null && project.isActive() && globalUnitProject.isOrigin()) {

        String params[] =
          {crp.getAcronym(), project.getId() + "", baseAction.getActionName().replaceAll(crp.getAcronym() + "/", "")};

        if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin()) {
          if (!baseAction.isSubmit(projectId)) {

            canSwitchProject = true;
          }
          canEdit = true;


        } else {
          List<Project> projects = projectManager.getUserProjects(user.getId(), crp.getAcronym());
          if (projects.contains(project)
            && baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__PERMISSION, params))) {
            canEdit = true;

          }
          if (project.getProjecInfoPhase(baseAction.getActualPhase()) != null) {
            if (baseAction.isPlanningActive()) {
              if (project.getProjecInfoPhase(baseAction.getActualPhase()).getStatus() != null
                && project.getProjecInfoPhase(baseAction.getActualPhase()).getStatus().intValue() != Integer
                  .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
                canEdit = false;
              }

            }


            if (!project.getProjecInfoPhase(baseAction.getActualPhase()).isProjectEditLeader()
              && !baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__SWITCH, params))) {
              canEdit = false;
            }
          }


          if (baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__SWITCH, params))) {
            canSwitchProject = true;
          }

          if (baseAction.isSubmit(projectId)) {
            canEdit = false;

          }


          if (baseAction.isCrpClosed()) {
            if (!(baseAction.hasSpecificities(APConstants.CRP_PMU) && baseAction.isPMU())) {
              canEdit = false;
            }
          }


        }
        if (project.getProjecInfoPhase(baseAction.getActualPhase()).getEndDate() != null) {
          Calendar cal = Calendar.getInstance();
          cal.setTime(project.getProjecInfoPhase(baseAction.getActualPhase()).getEndDate());
          if (project.getProjecInfoPhase(baseAction.getActualPhase()).getStatus().longValue() == Long
            .parseLong(ProjectStatusEnum.Ongoing.getStatusId())
            && baseAction.getActualPhase().getYear() > cal.get(Calendar.YEAR)) {
            canEdit = false;
            canSwitchProject = false;
            baseAction.setEditStatus(true);

          }

        }

        String actionName = baseAction.getActionName().replaceAll(crp.getAcronym() + "/", "");
        if (baseAction.isReportingActive()
          && actionName.equalsIgnoreCase(ProjectSectionStatusEnum.BUDGET.getStatus())) {
          canEdit = false;
        }
        if (project.getProjecInfoPhase(baseAction.getActualPhase()).getStatus().longValue() == Long
          .parseLong(ProjectStatusEnum.Cancelled.getStatusId())

          || project.getProjecInfoPhase(baseAction.getActualPhase()).getStatus().longValue() == Long
            .parseLong(ProjectStatusEnum.Complete.getStatusId())) {
          canEdit = false;
          baseAction.setEditStatus(true);
        }
        if (project.getProjecInfoPhase(baseAction.getActualPhase()).getPhase().getDescription()
          .equals(APConstants.REPORTING)
          && project.getProjecInfoPhase(baseAction.getActualPhase()).getPhase().getYear() == 2016) {
          canEdit = false;
          baseAction.setEditStatus(false);
        }
        /*
         * List<ProjectPhase> projectPhases = phase.getProjectPhases().stream()
         * .filter(c -> c.isActive() && c.getProject().getId().longValue() == projectId).collect(Collectors.toList());
         * if (projectPhases.isEmpty()) {
         * List<ProjectInfo> infos =
         * project.getProjectInfos().stream().filter(c -> c.isActive()).collect(Collectors.toList());
         * infos.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
         * // baseAction.setActualPhase(infos.get(0).getPhase());
         * baseAction.setAvailabePhase(false);
         * }
         * if (!baseAction.getActualPhase().getEditable()) {
         * canEdit = false;
         * }
         */

        // String paramsPermissions[] = {loggedCrp.getAcronym(), project.getId() + ""};
        // baseAction
        // .setBasePermission(baseAction.getText(Permission.PROJECT_DESCRIPTION_BASE_PERMISSION, paramsPermissions));


        // TODO Validate is the project is new
        if (parameters.get(APConstants.EDITABLE_REQUEST).isDefined()) {
          // String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];
          String stringEditable = parameters.get(APConstants.EDITABLE_REQUEST).getMultipleValues()[0];
          editParameter = stringEditable.equals("true");
          if (!editParameter) {
            baseAction.setEditableParameter(hasPermissionToEdit);
          }
        }

        // Check the permission if user want to edit or save the form
        if (editParameter || parameters.get("save") != null) {
          hasPermissionToEdit = ((baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin())) ? true
            : baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__PERMISSION, params));
        }
        if (parameters.get(APConstants.TRANSACTION_ID).isDefined()) {
          // String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];
          editParameter = false;
        }
        // If the user is not asking for edition privileges we don't need to validate them.
        if (!baseAction.getActualPhase().getEditable()) {
          canEdit = false;
          baseAction.setCanEditPhase(false);
        }

        if (baseAction.getActionName().replaceAll(crp.getAcronym() + "/", "").equals(ProjectSectionStatusEnum.BUDGET)) {
          if (baseAction.isReportingActive()) {
            canEdit = false;
          }
        }
        // Set the variable that indicates if the user can edit the section
        baseAction.setEditableParameter(editParameter && canEdit);
        baseAction.setCanEdit(canEdit);
        baseAction.setCanSwitchProject(canSwitchProject);

      } else {
        throw new NullPointerException();
      }
    } else {
      throw new NullPointerException();
    }
  }

}
