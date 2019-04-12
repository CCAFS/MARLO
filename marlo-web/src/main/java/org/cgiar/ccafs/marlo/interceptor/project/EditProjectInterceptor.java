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
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SharedProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.NoPhaseException;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  private final LiaisonUserManager liaisonUserManager;

  @Inject
  public EditProjectInterceptor(ProjectManager projectManager, GlobalUnitManager crpManager, PhaseManager phaseManager,
    GlobalUnitProjectManager globalUnitProjectManager, LiaisonUserManager liaisonUserManager) {
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.liaisonUserManager = liaisonUserManager;
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
    boolean isAdmin = false;

    boolean contactPointEditProject = baseAction.hasSpecificities(APConstants.CRP_CONTACT_POINT_EDIT_PROJECT);


    // this.setBasePermission(this.getText(Permission.PROJECT_DESCRIPTION_BASE_PERMISSION, params));


    // String projectParameter = ((String[]) parameters.get(APConstants.PROJECT_REQUEST_ID))[0];
    String projectParameter = parameters.get(APConstants.PROJECT_REQUEST_ID).getMultipleValues()[0];

    projectId = Long.parseLong(projectParameter);

    Project project = projectManager.getProjectById(projectId);

    // Get The Crp/Center/Platform where the project was created
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
          if (!baseAction.isSubmit(projectId)) {
            canSwitchProject = true;
          }
          canEdit = true;
        } else {
          // List<Project> projects = projectManager.getUserProjects(user.getId(), crp.getAcronym());
          if (baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__PERMISSION, params))) {
            canEdit = true;

          }
          if (project.getProjecInfoPhase(baseAction.getActualPhase()) != null) {


            if (!project.getProjecInfoPhase(baseAction.getActualPhase()).isProjectEditLeader()
              && !baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__SWITCH, params))) {
              canEdit = false;
            }
          }


          LiaisonUser lUser = liaisonUserManager.getLiaisonUserByUserId(user.getId(), loggedCrp.getId());
          if (contactPointEditProject && lUser != null) {
            LiaisonInstitution liaisonInstitution = lUser.getLiaisonInstitution();
            ProjectPartner projectPartner = project.getLeader();

            if (projectPartner != null) {
              Institution institutionProject = projectPartner.getInstitution();

              Institution institutionCp = liaisonInstitution.getInstitution();

              if (institutionCp != null) {
                if (institutionCp.getId().equals(institutionProject.getId())) {
                  canSwitchProject = true;
                } else {
                  if (baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__SWITCH, params))) {
                    canSwitchProject = true;
                  }
                }
              } else {
                if (baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__SWITCH, params))) {
                  canSwitchProject = true;
                }
              }
            } else {
              if (baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__SWITCH, params))) {
                canSwitchProject = true;
              }
            }


          } else {
            if (baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__SWITCH, params))) {
              canSwitchProject = true;
            }
          }


          if (baseAction.isSubmit(projectId) && !baseAction.getActualPhase().getUpkeep()) {
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

        if (project.getProjecInfoPhase(baseAction.getActualPhase()).getEndDate() != null) {
          Calendar cal = Calendar.getInstance();
          cal.setTime(project.getProjecInfoPhase(baseAction.getActualPhase()).getEndDate());
          System.out.println(cal.get(Calendar.YEAR));
          System.out.println(baseAction.getActualPhase().getYear());
          if (project.getProjecInfoPhase(baseAction.getActualPhase()).getStatus().longValue() == Long
            .parseLong(ProjectStatusEnum.Complete.getStatusId())) {
            if (baseAction.getActualPhase().getYear() > cal.get(Calendar.YEAR)) {
              if (!isAdmin) {
                canEdit = false;
              }
              canSwitchProject = false;
            }
          }
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
        String actionName = baseAction.getActionName().replaceAll(crp.getAcronym() + "/", "");


        // check permission to edit budget execution in reporting
        if (baseAction.isReportingActive() && actionName.contains(ProjectSectionStatusEnum.BUDGET.getStatus())) {
          canEdit = baseAction.canEditAnyProjectExecution(projectId);
        }

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
        if (editParameter || parameters.get("save").isDefined()) {
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

        // Check if is a Shared project (Crp to Center)
        if (!globalUnitProject.isOrigin()) {
          canEdit = false;
          if (actionName.equals(SharedProjectSectionStatusEnum.CENTER_MAPPING.getStatus())) {
            if (baseAction.hasPermission(baseAction.generatePermission(Permission.SHARED_PROJECT_PERMISSION, params))) {
              canEdit = true;
            }
          }

        }

        if (!editParameter) {
          baseAction.setEditStatus(false);
        }
        // Set the variable that indicates if the user can edit the section
        baseAction.setEditableParameter(editParameter && canEdit);
        baseAction.setCanSwitchProject(canSwitchProject && globalUnitProject.isOrigin());
        baseAction.setCanEdit(canEdit);
        baseAction.setEditStatus(baseAction.isEditStatus() && globalUnitProject.isOrigin());

        // Allow Superadmin edit
        if (baseAction.canAccessSuperAdmin() && editParameter) {
          baseAction.setEditableParameter(true);
          baseAction.setCanEdit(true);
          baseAction.setEditStatus(true);
        }


      } else {
        throw new NullPointerException();
      }
    } else {
      throw new NullPointerException();
    }
  }

}
