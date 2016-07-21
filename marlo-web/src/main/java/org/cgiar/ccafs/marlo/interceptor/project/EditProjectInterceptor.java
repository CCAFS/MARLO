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
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.User;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditProjectInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 1423197153747668108L;

  private BaseAction baseAction;
  private Map<String, Object> parameters;
  private Map<String, Object> session;
  private Crp crp;
  private long projectId = 0;

  private ProjectManager projectManager;

  @Inject
  public EditProjectInterceptor(ProjectManager projectManager) {
    this.projectManager = projectManager;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {

    baseAction = (BaseAction) invocation.getAction();
    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();
    crp = (Crp) session.get(APConstants.SESSION_CRP);

    return null;
  }

  void setPermissionParameters(ActionInvocation invocation) {

    User user = (User) session.get(APConstants.SESSION_USER);

    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;

    String projectParameter = ((String[]) parameters.get(APConstants.PROJECT_REQUEST_ID))[0];

    long projectID = Long.parseLong(projectParameter);

    Project project = projectManager.getProjectById(projectID);

    // TODO Validate if the Project is in Planning or Reporting and get the Year
    int currentCycleYear = 2015;

    if (baseAction.canAccessSuperAdmin() || baseAction.canAcessCrpAdmin()) {
      canEdit = true;
    } else {
      List<Project> projects = projectManager.getUserProjects(user.getId(), crp.getAcronym());
      if (projects.contains(project)) {

        if (project.getSubmissions().stream().filter(s -> s.getYear().equals(currentCycleYear))
          .collect(Collectors.toList()).size() > 0) {
          canEdit = true;
        }
      }
    }

    // TODO Validate is the project is new
    if (parameters.get(APConstants.EDITABLE_REQUEST) != null) {
      String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];
      editParameter = stringEditable.equals("true");
      if (!editParameter) {
        baseAction.setEditableParameter(hasPermissionToEdit);
      }
    }


    // Check the permission if user want to edit or save the form
    if (editParameter || parameters.get("save") != null) {
      // hasPermissionToEdit = (baseAction.isAdmin()) ? true : baseAction.hasPermission(baseAction
      // .generatePermission(Permission.IMPACT_PATHWAY_EDIT_PRIVILEGES, crp.getAcronym(), crpProgramID + ""));
    }

    // Set the variable that indicates if the user can edit the section
    baseAction.setEditableParameter(hasPermissionToEdit && canEdit);
    baseAction.setCanEdit(canEdit);


  }

}
