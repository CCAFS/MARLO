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
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditExpectedStudyInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 4178469256964398247L;

  private Map<String, Parameter> parameters;
  private Map<String, Object> session;
  private GlobalUnit crp;
  private long expectedId = 0;

  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectManager projectManager;
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private final LiaisonUserManager liaisonUserManager;

  @Inject
  public EditExpectedStudyInterceptor(ProjectExpectedStudyManager projectExpectedStudyManager,
    ProjectManager projectManager, GlobalUnitManager crpManager, LiaisonUserManager liaisonUserManager) {
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.liaisonUserManager = liaisonUserManager;
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

  void setPermissionParameters(ActionInvocation invocation) throws Exception {
    BaseAction baseAction = (BaseAction) invocation.getAction();
    User user = (User) session.get(APConstants.SESSION_USER);
    baseAction.setSession(session);
    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    boolean canSwitchProject = false;
    baseAction.setSession(session);
    String projectParameter = parameters.get(APConstants.EXPECTED_REQUEST_ID).getMultipleValues()[0];
    boolean contactPointEditProject = baseAction.hasSpecificities(APConstants.CRP_CONTACT_POINT_EDIT_PROJECT);

    expectedId = Long.parseLong(projectParameter);

    ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(expectedId);

    if (projectExpectedStudy != null && projectExpectedStudy.isActive()
      && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null) {

      String params[] = new String[2];
      params[0] = crp.getAcronym();

      if (projectExpectedStudy.getProject() != null) {
        params[1] = projectExpectedStudy.getProject().getId() + "";
      } else {
        params[1] = projectExpectedStudy.getId() + "";
      }

      if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin()) {

        canEdit = true;
        canSwitchProject = true;

      } else {

        if (projectExpectedStudy.getProject() != null) {

          List<Project> projects = projectManager.getUserProjects(user.getId(), crp.getAcronym());
          if (projects.contains(projectExpectedStudy.getProject()) && baseAction.hasPermission(
            baseAction.generatePermission(Permission.PROJECT_EXPECTED_STUDIES_EDIT_PERMISSION, params))) {
            canEdit = true;
          }
          if (baseAction.isSubmit(projectExpectedStudy.getProject().getId()) && !baseAction.getActualPhase().getUpkeep()
            && !baseAction.isPMU()) {
            canEdit = false;
          }
        } else {
          if (user.getId().equals(projectExpectedStudy.getCreatedBy().getId())) {
            canEdit = true;
          } else if (baseAction
            .hasPermission(baseAction.generatePermission(Permission.STUDIES_FULL_EDIT_PERMISSION, crp.getAcronym()))) {
            canEdit = true;
          } else {
            if (baseAction.hasPermission(baseAction.generatePermission(Permission.STUDIES_EDIT_PERMISSION, params))) {
              canEdit = true;
            }
          }
        }

        if (baseAction.isCrpClosed()) {
          if (!(baseAction.hasSpecificities(APConstants.CRP_PMU) && baseAction.isPMU())) {
            canEdit = false;
          }
        }
      }

      if (parameters.get(APConstants.EDITABLE_REQUEST).isDefined()) {
        // String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];
        String stringEditable = parameters.get(APConstants.EDITABLE_REQUEST).getMultipleValues()[0];
        editParameter = stringEditable.equals("true");
        if (!editParameter) {
          baseAction.setEditableParameter(hasPermissionToEdit);
        }
      }

      // Check the permission if user want to edit or save the form
      if (projectExpectedStudy.getProject() != null) {
        if (editParameter || parameters.get("save").isDefined()) {
          hasPermissionToEdit = ((baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin())) ? true : baseAction
            .hasPermission(baseAction.generatePermission(Permission.PROJECT_EXPECTED_STUDIES_EDIT_PERMISSION, params));
        }
      } else {
        if (editParameter || parameters.get("save").isDefined()) {
          hasPermissionToEdit = ((baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin())) ? true
            : baseAction.hasPermission(baseAction.generatePermission(Permission.STUDIES_EDIT_PERMISSION, params));
          // Change to Studies Without projects
          if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin()) {
            hasPermissionToEdit = true;
          } else {
            if (projectExpectedStudy.getProject() != null) {
              if (baseAction.hasPermission(
                baseAction.generatePermission(Permission.PROJECT_EXPECTED_STUDIES_EDIT_PERMISSION, params))) {
                hasPermissionToEdit = true;
              }

            } else {
              if (user.getId().equals(projectExpectedStudy.getCreatedBy().getId())) {
                hasPermissionToEdit = true;
              } else {
                if (baseAction
                  .hasPermission(baseAction.generatePermission(Permission.STUDIES_EDIT_PERMISSION, params))) {
                  hasPermissionToEdit = true;
                }
              }
            }
          }
        }
      }
      if (parameters.get(APConstants.TRANSACTION_ID).isDefined()) {
        hasPermissionToEdit = false;
      }


      if (baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__SWITCH, params))) {
        canSwitchProject = true;
      }


      // Set the variable that indicates if the user can edit the section
      baseAction.setEditableParameter(hasPermissionToEdit && canEdit && baseAction.getActualPhase().getEditable());
      baseAction.setCanEdit(canEdit);
      baseAction.setCanSwitchProject(canSwitchProject);

      if (!baseAction.getActualPhase().getEditable()) {
        baseAction.setEditableParameter(false);
      }


    } else {
      throw new NullPointerException();
    }

  }

}
