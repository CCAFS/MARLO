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


package org.cgiar.ccafs.marlo.interceptor.funding;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class FundingSourceInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 1L;

  private Map<String, Object> parameters;
  private Map<String, Object> session;
  private Crp crp;
  private long fundingSourceID = 0;

  private CrpManager crpManager;
  private FundingSourceManager fundingSourceManager;

  @Inject
  public FundingSourceInterceptor(CrpManager crpManager, FundingSourceManager fundingSourceManager) {
    this.crpManager = crpManager;
    this.fundingSourceManager = fundingSourceManager;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {

    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();
    crp = (Crp) session.get(APConstants.SESSION_CRP);
    crp = crpManager.getCrpById(crp.getId());
    try {
      this.setPermissionParameters(invocation);
      return invocation.invoke();
    } catch (Exception e) {
      BaseAction action = (BaseAction) invocation.getAction();
      return action.NOT_FOUND;
    }
  }

  void setPermissionParameters(ActionInvocation invocation) {
    BaseAction baseAction = (BaseAction) invocation.getAction();
    User user = (User) session.get(APConstants.SESSION_USER);
    baseAction.clearPermissionsCache();

    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;

    String projectParameter = ((String[]) parameters.get(APConstants.FUNDING_SOURCE_REQUEST_ID))[0];
    if (projectParameter == null) {
      throw new NullPointerException();
    }
    fundingSourceID = Long.parseLong(projectParameter);

    FundingSource fundingSource = fundingSourceManager.getFundingSourceById(fundingSourceID);

    if (fundingSource != null) {
      String params[] = {crp.getAcronym(), fundingSource.getId() + ""};
      if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin()) {
        canEdit = true;
      } else {
        List<FundingSource> fundingSources = fundingSourceManager.getFundingSource(user.getId(), crp.getAcronym());
        if (fundingSources.contains(fundingSource) && (baseAction
          .hasPermission(baseAction.generatePermission(Permission.PROJECT_FUNDING_SOURCE_BASE_PERMISSION, params))
          || baseAction
            .hasPermission(baseAction.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, params)))) {
          canEdit = true;

        }

        if (baseAction.isCrpClosed()) {
          if (!(baseAction.hasSpecificities(APConstants.CRP_PMU) && baseAction.isPMU())) {
            canEdit = false;
          }
        }
      }


      if (parameters.get(APConstants.EDITABLE_REQUEST) != null) {
        String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];
        editParameter = stringEditable.equals("true");
        if (!editParameter) {
          baseAction.setEditableParameter(hasPermissionToEdit);
        }
      }

      // Check the permission if user want to edit or save the form
      if (editParameter || parameters.get("save") != null) {
        hasPermissionToEdit = ((baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin())) ? true
          : (baseAction
            .hasPermission(baseAction.generatePermission(Permission.PROJECT_FUNDING_SOURCE_BASE_PERMISSION, params))
            || baseAction
              .hasPermission(baseAction.generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, params)));
      }


      // Set the variable that indicates if the user can edit the section
      baseAction.setEditableParameter(hasPermissionToEdit && canEdit);
      baseAction.setCanEdit(canEdit);

    } else {
      throw new NullPointerException();
    }
  }

}