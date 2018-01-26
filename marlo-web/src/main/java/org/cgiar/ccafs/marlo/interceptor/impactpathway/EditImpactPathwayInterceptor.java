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
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
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

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditImpactPathwayInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 8294421978295446976L;
  private final GlobalUnitManager crpManager;
  private final UserManager userManager;
  private final CrpProgramManager crpProgramManager;

  private Phase phase;
  private PhaseManager phaseManager;
  private Map<String, Parameter> parameters;
  private Map<String, Object> session;
  private GlobalUnit crp;
  private long crpProgramID = 0;

  @Inject
  public EditImpactPathwayInterceptor(GlobalUnitManager crpManager, UserManager userManager,
    CrpProgramManager crpProgramManager, PhaseManager phaseManager) {
    this.crpManager = crpManager;
    this.userManager = userManager;
    this.crpProgramManager = crpProgramManager;
    this.phaseManager = phaseManager;
  }

  long getCrpProgramId() {
    try {
      // return Long.parseLong(((String[]) parameters.get(APConstants.CRP_PROGRAM_ID))[0]);
      return Long.parseLong(parameters.get(APConstants.CRP_PROGRAM_ID).getMultipleValues()[0]);
    } catch (Exception e) {
      GlobalUnit loggedCrp = (GlobalUnit) session.get(APConstants.SESSION_CRP);

      loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

      User user = (User) session.get(APConstants.SESSION_USER);
      user = userManager.getUser(user.getId());
      if (user.getCrpProgramLeaders() != null) {
        List<CrpProgramLeader> userLeads = user.getCrpProgramLeaders().stream()
          .filter(c -> c.isActive() && c.getCrpProgram().isActive()
            && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList());
        if (!userLeads.isEmpty()) {
          return userLeads.get(0).getCrpProgram().getId();
        } else {
          List<CrpProgram> allPrograms = loggedCrp.getCrpPrograms().stream()
            .filter(c -> c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue() && c.isActive())
            .collect(Collectors.toList());
          if (!allPrograms.isEmpty()) {
            return allPrograms.get(0).getId();
          }
        }
      }

    }
    return 0;
  }


  @Override
  public String intercept(ActionInvocation invocation) throws Exception {

    BaseAction baseAction = (BaseAction) invocation.getAction();

    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();
    baseAction.setSession(session);
    crp = (GlobalUnit) session.get(APConstants.SESSION_CRP);
    crpProgramID = this.getCrpProgramId();

    if (!baseAction
      .hasPermission(baseAction.generatePermission(Permission.IMPACT_PATHWAY_VISIBLE_PRIVILEGES, crp.getAcronym()))) {
      return BaseAction.NOT_AUTHORIZED;
    }


    try {
      this.setPermissionParameters(invocation);
      return invocation.invoke();
    } catch (Exception e) {
      e.printStackTrace();
      return BaseAction.NOT_FOUND;
    }
  }

  public void setPermissionParameters(ActionInvocation invocation) {
    BaseAction baseAction = (BaseAction) invocation.getAction();
    baseAction.setSession(session);
    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    phase = baseAction.getActualPhase();
    phase = phaseManager.getPhaseById(phase.getId());

    CrpProgram crpProgram = crpProgramManager.getCrpProgramById(crpProgramID);

    if (crpProgram != null) {

      if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {

        // If user is admin, it should have privileges to edit all projects.
        if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin()) {
          canEdit = true;
        } else {
          if (baseAction.hasPermissionNoBase(baseAction.generatePermission(Permission.IMPACT_PATHWAY_EDIT_PRIVILEGES,
            crp.getAcronym(), crpProgramID + ""))) {
            canEdit = true;
          }

          if (baseAction.isCrpClosed()) {
            canEdit = false;
          }
        }

        if (!phase.getEditable()) {
          canEdit = false;
          baseAction.setCanEditPhase(false);
        }
        if (phase.getDescription().equals(APConstants.REPORTING)) {
          canEdit = false;
          baseAction.setCanEditPhase(false);
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
        if (parameters.get(APConstants.TRANSACTION_ID).isDefined()) {
          // String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];

          editParameter = false;
          // If the user is not asking for edition privileges we don't need to validate them.

        }
        // Check the permission if user want to edit or save the form
        if (editParameter || parameters.get("save") != null) {
          hasPermissionToEdit = (baseAction.isAdmin()) ? true : baseAction.hasPermission(baseAction
            .generatePermission(Permission.IMPACT_PATHWAY_EDIT_PRIVILEGES, crp.getAcronym(), crpProgramID + ""));
        }

        // Set the variable that indicates if the user can edit the section
        baseAction.setEditableParameter(editParameter && canEdit);
        baseAction.setCanEdit(canEdit);

      } else {
        throw new NullPointerException();
      }
    }
  }

}
