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

package org.cgiar.ccafs.marlo.interceptor.center.monitoring.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.security.Permission;

import java.io.Serializable;
import java.util.Map;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditProjectInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 1292628953840385651L;


  private Map<String, Parameter> parameters;
  private final ICenterProjectManager projectService;
  private final ICenterProgramManager programService;
  private Map<String, Object> session;

  private Center researchCenter;
  private long areaID = -1;
  private long programID = -1;
  private long projectID = -1;

  @Inject
  public EditProjectInterceptor(ICenterProjectManager projectService, ICenterProgramManager programService) {
    this.projectService = projectService;
    this.programService = programService;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();
    researchCenter = (Center) session.get(APConstants.SESSION_CENTER);

    try {
      // projectID = Long.parseLong(((String[]) parameters.get(APConstants.PROJECT_ID))[0]);
      projectID = Long.parseLong(parameters.get(APConstants.PROJECT_ID).getMultipleValues()[0]);
    } catch (Exception e) {
      return BaseAction.NOT_FOUND;
    }

    try {
      this.setPermissionParameters(invocation);
      return invocation.invoke();
    } catch (Exception e) {
      return BaseAction.NOT_FOUND;
    }
  }

  public void setPermissionParameters(ActionInvocation invocation) throws Exception {

    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    BaseAction baseAction = (BaseAction) invocation.getAction();
    CenterProject project = projectService.getCenterProjectById(projectID);

    if (project != null) {


      programID = project.getResearchProgram().getId();
      CenterProgram program = programService.getProgramById(programID);

      if (program != null) {

        areaID = program.getResearchArea().getId();

        String params[] = {researchCenter.getAcronym(), areaID + "", programID + "", projectID + ""};
        if (baseAction.canAccessSuperAdmin()) {
          canEdit = true;
        } else {

          if (baseAction
            .hasPermissionCenter(baseAction.generatePermissionCenter(Permission.PROJECT_BASE_PERMISSION, params))) {
            canEdit = true;
          }
        }

        if (parameters.get(APConstants.EDITABLE_REQUEST).isDefined()) {
          // String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];
          String stringEditable = parameters.get(APConstants.EDITABLE_REQUEST).getMultipleValues()[0];
          editParameter = stringEditable.equals("true");
          // If the user is not asking for edition privileges we don't need to validate them.
          if (!editParameter) {
            baseAction.setEditableParameter(hasPermissionToEdit);
          }
        }

        // Check the permission if user want to edit or save the form
        if (editParameter || parameters.get("save") != null) {
          hasPermissionToEdit = (baseAction.isAdmin()) ? true : baseAction.hasPermissionCenter(
            baseAction.generatePermissionCenter(Permission.RESEARCH_PROGRAM_FULL_PRIVILEGES, params));
        }

        // Set the variable that indicates if the user can edit the section
        baseAction.setEditableParameter(hasPermissionToEdit && canEdit);
        baseAction.setCanEdit(canEdit);
      } else {
        throw new Exception();
      }
    } else {
      throw new Exception();
    }

  }

}
