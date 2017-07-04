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

package org.cgiar.ccafs.marlo.interceptor.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.service.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.service.ICenterOutcomeManager;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConstants;

import java.io.Serializable;
import java.util.Map;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditOutcomeInterceptor extends AbstractInterceptor implements Serializable {


  private static final long serialVersionUID = 6885486232213967456L;


  private ICenterOutcomeManager outcomeService;

  private ICenterProgramManager programService;
  private Map<String, Object> parameters;

  private Map<String, Object> session;
  private Center researchCenter;
  private long outcomeID = -1;
  private long areaID = -1;
  private long programID = -1;

  @Inject
  public EditOutcomeInterceptor(ICenterOutcomeManager outcomeService, ICenterProgramManager programService) {
    this.outcomeService = outcomeService;
    this.programService = programService;
  }

  public long getProgramID() {
    return programID;
  }


  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();
    researchCenter = (Center) session.get(APConstants.SESSION_CENTER);

    try {
      outcomeID = Long.parseLong(((String[]) parameters.get(APConstants.OUTCOME_ID))[0]);
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
    CenterOutcome outcome = outcomeService.getResearchOutcomeById(outcomeID);

    if (outcome != null) {


      programID = outcome.getResearchTopic().getResearchProgram().getId();
      CenterProgram program = programService.getProgramById(programID);

      if (program != null) {

        areaID = program.getResearchArea().getId();

        String params[] = {researchCenter.getAcronym(), areaID + "", programID + ""};
        if (baseAction.canAccessSuperAdmin()) {
          canEdit = true;
        } else {

          if (baseAction
            .hasPermission(baseAction.generatePermission(Permission.RESEARCH_PROGRAM_FULL_PRIVILEGES, params))) {
            canEdit = true;
          }
        }

        if (parameters.get(APConstants.EDITABLE_REQUEST) != null) {
          String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];
          editParameter = stringEditable.equals("true");
          // If the user is not asking for edition privileges we don't need to validate them.
          if (!editParameter) {
            baseAction.setEditableParameter(hasPermissionToEdit);
          }
        }

        // Check the permission if user want to edit or save the form
        if (editParameter || parameters.get("save") != null) {
          hasPermissionToEdit = (baseAction.isAdmin()) ? true : baseAction
            .hasPermission(baseAction.generatePermission(Permission.RESEARCH_PROGRAM_FULL_PRIVILEGES, params));
        }

        if (baseAction.isSubmitIP(programID)) {
          canEdit = false;
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

  public void setProgramID(long programID) {
    this.programID = programID;
  }

}
