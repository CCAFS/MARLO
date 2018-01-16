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
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.security.Permission;

import java.io.Serializable;
import java.util.Map;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditDeliverableInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 1L;

  private final ICenterDeliverableManager deliverableService;
  private final ICenterProjectManager projectService;
  private final ICenterProgramManager programService;

  private Map<String, Parameter> parameters;
  private Map<String, Object> session;
  private GlobalUnit researchCenter;
  private long areaID = -1;
  private long programID = -1;
  private long projectID = -1;
  private long deliverableID = -1;

  @Inject
  public EditDeliverableInterceptor(ICenterDeliverableManager deliverableService, ICenterProjectManager projectService,
    ICenterProgramManager programService) {
    this.deliverableService = deliverableService;
    this.programService = programService;
    this.projectService = projectService;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();
    researchCenter = (GlobalUnit) session.get(APConstants.SESSION_CRP);

    try {
      // deliverableID = Long.parseLong(((String[]) parameters.get(APConstants.DELIVERABLE_ID))[0]);
      deliverableID =
        Long.parseLong(StringUtils.trim(parameters.get(APConstants.CENTER_DELIVERABLE_ID).getMultipleValues()[0]));
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
    CenterDeliverable deliverable = deliverableService.getDeliverableById(deliverableID);

    if (deliverable != null) {

      projectID = deliverable.getProject().getId();
      CenterProject project = projectService.getCenterProjectById(projectID);

      if (project != null) {

        programID = project.getResearchProgram().getId();
        CenterProgram program = programService.getProgramById(programID);

        if (program != null) {

          areaID = program.getResearchArea().getId();

          String params[] =
            {researchCenter.getAcronym(), areaID + "", programID + "", projectID + "", deliverableID + ""};
          if (baseAction.canAccessSuperAdmin()) {
            canEdit = true;
          } else {

            if (baseAction.hasPermissionCenter(
              baseAction.generatePermissionCenter(Permission.CENTER_PROJECT_DEIVERABLE_BASE_PERMISSION, params))) {
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
              baseAction.generatePermissionCenter(Permission.CENTER_PROJECT_DEIVERABLE_BASE_PERMISSION, params));
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


    } else {
      throw new Exception();
    }

  }

}
