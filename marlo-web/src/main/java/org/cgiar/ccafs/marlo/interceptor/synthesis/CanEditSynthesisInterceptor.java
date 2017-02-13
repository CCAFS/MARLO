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
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.IpLiaisonUser;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CanEditSynthesisInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = -1238999468476665730L;


  Map<String, Object> parameters;
  Map<String, Object> session;
  Crp crp;

  private IpLiaisonInstitutionManager IpLiaisonInstitutionManager;
  private IpProgramManager ipProgramManager;

  @Inject
  public CanEditSynthesisInterceptor(IpLiaisonInstitutionManager IpLiaisonInstitutionManager,
    IpProgramManager ipProgramManager) {
    this.IpLiaisonInstitutionManager = IpLiaisonInstitutionManager;
    this.ipProgramManager = ipProgramManager;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

  void setPermissionParameters(ActionInvocation invocation) {

    User user = (User) session.get(APConstants.SESSION_USER);
    BaseAction baseAction = (BaseAction) invocation.getAction();
    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    boolean canSwitchProject = false;
    baseAction.setSession(session);

    long liaisonInstitutionID;

    try {
      liaisonInstitutionID = Long
        .parseLong(StringUtils.trim(baseAction.getRequest().getParameter(APConstants.LIAISON_INSTITUTION_REQUEST_ID)));
    } catch (Exception e) {
      if (baseAction.getCurrentUser().getIpLiaisonUsers() != null
        || !baseAction.getCurrentUser().getIpLiaisonUsers().isEmpty()) {

        List<IpLiaisonUser> liaisonUsers = new ArrayList<>(baseAction.getCurrentUser().getIpLiaisonUsers());

        if (!liaisonUsers.isEmpty()) {
          LiaisonUser liaisonUser = new LiaisonUser();
          liaisonUser = new ArrayList<>(baseAction.getCurrentUser().getLiasonsUsers()).get(0);
          liaisonInstitutionID = liaisonUser.getLiaisonInstitution().getId();
        } else {
          liaisonInstitutionID = new Long(7);
        }


      } else {
        liaisonInstitutionID = new Long(7);
      }

    }


    IpLiaisonInstitution currentLiaisonInstitution =
      IpLiaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);

    long programID;

    try {
      programID = Long.valueOf(currentLiaisonInstitution.getIpProgram());
    } catch (Exception ex) {
      programID = 1;
      liaisonInstitutionID = new Long(2);
      currentLiaisonInstitution = IpLiaisonInstitutionManager.getIpLiaisonInstitutionById(liaisonInstitutionID);

    }
    IpProgram program = ipProgramManager.getIpProgramById(programID);


  }

}
