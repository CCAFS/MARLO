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

package org.cgiar.ccafs.marlo.interceptor.synthesis;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.IpLiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonUser;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Christian Garciaa - CIAT/CCAFS
 */
public class CanEditCrpIndicatorsInterceptor extends AbstractInterceptor implements Serializable {


  /**
   * 
   */
  private static final long serialVersionUID = -2220001958027276963L;
  Map<String, Parameter> parameters;
  Map<String, Object> session;
  GlobalUnit crp;

  private final IpLiaisonInstitutionManager IpLiaisonInstitutionManager;
  private final UserManager userManager;

  @Inject
  public CanEditCrpIndicatorsInterceptor(IpLiaisonInstitutionManager IpLiaisonInstitutionManager,
    UserManager userManager) {
    this.IpLiaisonInstitutionManager = IpLiaisonInstitutionManager;
    this.userManager = userManager;
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
    user = userManager.getUser(baseAction.getCurrentUser().getId());
    long liaisonInstitutionID;

    try {
      // liaisonInstitutionID = Long.parseLong(((String[])
      // parameters.get(APConstants.LIAISON_INSTITUTION_REQUEST_ID))[0]);
      liaisonInstitutionID =
        Long.parseLong(parameters.get(APConstants.LIAISON_INSTITUTION_REQUEST_ID).getMultipleValues()[0]);
    } catch (Exception e) {
      if (user.getIpLiaisonUsers() != null || !user.getIpLiaisonUsers().isEmpty()) {

        List<IpLiaisonUser> liaisonUsers = new ArrayList<>(user.getIpLiaisonUsers());

        if (!liaisonUsers.isEmpty()) {
          IpLiaisonUser liaisonUser = new IpLiaisonUser();
          liaisonUser = liaisonUsers.get(0);
          liaisonInstitutionID = liaisonUser.getIpLiaisonInstitution().getId();
        } else {
          liaisonInstitutionID = new Long(7);
        }


      } else {
        liaisonInstitutionID = new Long(7);
      }

    }


    IpLiaisonInstitution currentLiaisonInstitution =
      IpLiaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);


    String params[] = {crp.getAcronym(), currentLiaisonInstitution.getId() + "",};

    if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin()) {
      canEdit = true;
    } else {
      if (baseAction.hasPermission(baseAction.generatePermission(Permission.CRP_INDICATORS_PERMISSION, params))) {
        if (baseAction.isReportingActive()) {
          canEdit = true;
        }
      }


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
    if (editParameter || parameters.get("save") != null) {
      hasPermissionToEdit = ((baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin())) ? true
        : baseAction.hasPermission(baseAction.generatePermission(Permission.CRP_INDICATORS_PERMISSION, params));
    }

    baseAction.setEditableParameter(hasPermissionToEdit && canEdit);
    baseAction.setCanEdit(canEdit);
  }

}
