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

package org.cgiar.ccafs.marlo.interceptor.powb;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
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
public class CanEditPowbSynthesisInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 3991605046733049951L;

  Map<String, Parameter> parameters;
  Map<String, Object> session;
  GlobalUnit crp;

  private UserManager userManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private PowbSynthesisManager powbSynthesisManager;

  @Inject
  public CanEditPowbSynthesisInterceptor(UserManager userManager, LiaisonInstitutionManager liaisonInstitutionManager,
    PowbSynthesisManager powbSynthesisManager) {
    this.userManager = userManager;
    this.powbSynthesisManager = powbSynthesisManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;

  }

  public Long firstFlagship() {
    List<LiaisonInstitution> liaisonInstitutions = new ArrayList<>(crp.getLiaisonInstitutions().stream()
      .filter(c -> c.getCrpProgram() != null
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));
    liaisonInstitutions.sort(Comparator.comparing(LiaisonInstitution::getAcronym));
    long liaisonInstitutionId = liaisonInstitutions.get(0).getId();
    return liaisonInstitutionId;
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

  void setPermissionParameters(ActionInvocation invocation) {

    User user = (User) session.get(APConstants.SESSION_USER);
    BaseAction baseAction = (BaseAction) invocation.getAction();
    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    baseAction.setSession(session);
    PowbSynthesis powbSynthesis;

    long liaisonInstitutionID;
    user = userManager.getUser(baseAction.getCurrentUser().getId());

    // Get The liaison Institution
    try {
      liaisonInstitutionID =
        Long.parseLong(parameters.get(APConstants.LIAISON_INSTITUTION_REQUEST_ID).getMultipleValues()[0]);
    } catch (Exception e) {
      if (user.getLiasonsUsers() != null || !user.getLiasonsUsers().isEmpty()) {
        List<LiaisonUser> liaisonUsers = new ArrayList<>(user.getLiasonsUsers().stream()
          .filter(lu -> lu.isActive() && lu.getLiaisonInstitution().getCrp().getId() == crp.getId())
          .collect(Collectors.toList()));
        if (!liaisonUsers.isEmpty()) {
          LiaisonUser liaisonUser = new LiaisonUser();
          liaisonUser = liaisonUsers.get(0);
          liaisonInstitutionID = liaisonUser.getLiaisonInstitution().getId();
        } else {
          liaisonInstitutionID = this.firstFlagship();
        }
      } else {
        liaisonInstitutionID = this.firstFlagship();;
      }
    }

    // Get the PWOB Synthesis section
    long powbSynthesisID;
    try {
      powbSynthesisID = Long.parseLong(parameters.get(APConstants.POWB_SYNTHESIS_ID).getMultipleValues()[0]);
      powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
    } catch (Exception e) {
      Phase phase = baseAction.getActualPhase();
      powbSynthesis = powbSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
      if (powbSynthesis == null) {
        powbSynthesis = baseAction.createPowbSynthesis(phase.getId(), liaisonInstitutionID);
      }
      powbSynthesisID = powbSynthesis.getId();
    }

    // Check if the user have permissions
    String params[] = {crp.getAcronym(), powbSynthesis.getId() + "",};
    if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin()) {
      canEdit = true;
    } else {
      if (baseAction.hasPermission(baseAction.generatePermission(Permission.POWB_SYNTHESIS_PERMISSION, params))) {
        if (baseAction.isPlanningActive()) {
          canEdit = true;
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
    if (editParameter || parameters.get("save") != null) {
      hasPermissionToEdit = ((baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin())) ? true
        : baseAction.hasPermission(baseAction.generatePermission(Permission.POWB_SYNTHESIS_PERMISSION, params));
    }

    baseAction.setEditableParameter(hasPermissionToEdit && canEdit);
    baseAction.setCanEdit(canEdit);

  }

}
