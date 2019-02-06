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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.User;

import java.io.Serializable;
import java.util.Map;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditPrivateStudyInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 4178469256964398247L;

  private Map<String, Parameter> parameters;
  private Map<String, Object> session;
  private GlobalUnit crp;
  private long expectedId = 0;
  private boolean isPrivate;

  private ProjectExpectedStudyManager projectExpectedStudyManager;

  // GlobalUnit Manager
  private GlobalUnitManager crpManager;


  @Inject
  public EditPrivateStudyInterceptor(ProjectExpectedStudyManager projectExpectedStudyManager,
    GlobalUnitManager crpManager) {
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.crpManager = crpManager;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();
    crp = (GlobalUnit) session.get(APConstants.SESSION_CRP);
    crp = crpManager.getGlobalUnitById(crp.getId());
    try {
      this.setPermissionParameters(invocation);
      if (isPrivate) {
        return invocation.invoke();
      } else {
        BaseAction action = (BaseAction) invocation.getAction();
        return action.PRIVATE;
      }
    } catch (NullPointerException e) {
      BaseAction action = (BaseAction) invocation.getAction();
      return action.NOT_FOUND;
    }
  }

  void setPermissionParameters(ActionInvocation invocation) throws Exception {
    BaseAction baseAction = (BaseAction) invocation.getAction();
    User user = (User) session.get(APConstants.SESSION_USER);
    baseAction.setSession(session);
    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    boolean canSwitchProject = false;
    baseAction.setSession(session);
    String projectParameter = parameters.get(APConstants.STUDY_REQUEST_ID).getMultipleValues()[0];

    expectedId = Long.parseLong(projectParameter);

    ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(expectedId);

    if (projectExpectedStudy != null && projectExpectedStudy.isActive()
      && projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()) != null) {
      isPrivate = false;
      if (!projectExpectedStudy.getProjectExpectedStudyInfo(baseAction.getActualPhase()).getIsPublic()) {
        isPrivate = true;
      }

    } else {
      throw new NullPointerException();
    }

  }

}
