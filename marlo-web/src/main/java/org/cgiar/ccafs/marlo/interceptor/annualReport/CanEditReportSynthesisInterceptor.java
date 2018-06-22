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

package org.cgiar.ccafs.marlo.interceptor.annualReport;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
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
public class CanEditReportSynthesisInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = -7018609701246586417L;

  Map<String, Parameter> parameters;
  Map<String, Object> session;
  GlobalUnit crp;

  private UserManager userManager;
  private ReportSynthesisManager reportSynthesisManager;
  private GlobalUnitManager crpManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;

  @Inject
  public CanEditReportSynthesisInterceptor(UserManager userManager, ReportSynthesisManager reportSynthesisManager,
    GlobalUnitManager crpManager, LiaisonInstitutionManager liaisonInstitutionManager) {
    this.userManager = userManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.crpManager = crpManager;
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
    crp = crpManager.getGlobalUnitById(crp.getId());
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
    ReportSynthesis reportSynthesis;

    long liaisonInstitutionID;
    user = userManager.getUser(baseAction.getCurrentUser().getId());

    // Get The liaison Institution
    try {
      liaisonInstitutionID =
        Long.parseLong(parameters.get(APConstants.LIAISON_INSTITUTION_REQUEST_ID).getMultipleValues()[0]);
    } catch (Exception e) {
      if (user.getLiasonsUsers() != null || !user.getLiasonsUsers().isEmpty()) {
        List<LiaisonUser> liaisonUsers = new ArrayList<>(user.getLiasonsUsers().stream()
          .filter(lu -> lu.isActive() && lu.getLiaisonInstitution().isActive()
            && lu.getLiaisonInstitution().getCrp().getId() == crp.getId()
            && lu.getLiaisonInstitution().getInstitution() == null)
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

    // Get the Report Synthesis section
    long synthesisID;
    try {
      synthesisID = Long.parseLong(parameters.get(APConstants.REPORT_SYNTHESIS_ID).getMultipleValues()[0]);
      reportSynthesis = reportSynthesisManager.getReportSynthesisById(synthesisID);
    } catch (Exception e) {
      LiaisonInstitution liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liaisonInstitutionID);
      // If the LiaisonInstitution is not a PMU or Flagship.
      if (liaisonInstitution.getInstitution() != null) {
        throw new NullPointerException();
      }
      Phase phase = baseAction.getActualPhase();
      reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitutionID);
      if (reportSynthesis == null) {
        reportSynthesis = baseAction.createReportSynthesis(phase.getId(), liaisonInstitutionID);
      }
      synthesisID = reportSynthesis.getId();
    }

    // Check if the user have permissions
    String params[] = {crp.getAcronym(), reportSynthesis.getId() + "",};
    if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin()) {
      canEdit = true;
    } else {
      if (baseAction.hasPermission(baseAction.generatePermission(Permission.REPORT_SYNTHESIS_PERMISSION, params))) {
        if (baseAction.isReportingActive()) {
          canEdit = true;
        }
      }
    }

    // Check the permission if user want to edit or save the form
    if (editParameter || parameters.get("save").isDefined()) {
      hasPermissionToEdit = ((baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin())) ? true
        : baseAction.hasPermission(baseAction.generatePermission(Permission.REPORT_SYNTHESIS_PERMISSION, params));
    }

    if (parameters.get(APConstants.EDITABLE_REQUEST).isDefined()) {
      // String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];
      String stringEditable = parameters.get(APConstants.EDITABLE_REQUEST).getMultipleValues()[0];
      editParameter = stringEditable.equals("true");
      if (!editParameter) {
        baseAction.setEditableParameter(hasPermissionToEdit);
      }
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

    if (!editParameter) {
      baseAction.setEditStatus(false);
    }
    baseAction.setEditableParameter(editParameter && canEdit);
    baseAction.setCanEdit(canEdit);

  }

}
