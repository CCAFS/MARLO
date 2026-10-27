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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditPrivateStudyInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 4178469256964398247L;

  private static final Logger LOG = LoggerFactory.getLogger(EditPrivateStudyInterceptor.class);

  private Map<String, Parameter> parameters;
  private Map<String, Object> session;
  private GlobalUnit crp;
  private long expectedId = 0;
  private boolean isPrivate;
  private Phase phase;

  private ProjectExpectedStudyManager projectExpectedStudyManager;

  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private GlobalUnitProjectManager globalUnitProjectManager;
  private PhaseManager phaseManager;


  @Inject
  public EditPrivateStudyInterceptor(ProjectExpectedStudyManager projectExpectedStudyManager,
    GlobalUnitManager crpManager, GlobalUnitProjectManager globalUnitProjectManager, PhaseManager phaseManager) {
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.crpManager = crpManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.phaseManager = phaseManager;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    parameters = invocation.getInvocationContext().getParameters();
    session = invocation.getInvocationContext().getSession();


    try {
      this.setPermissionParameters(invocation);
    } catch (NullPointerException | NumberFormatException | IndexOutOfBoundsException e) {
      // A study id, cycle or year that is missing or is not a number is a bad request, and so is a study whose project
      // has no active origin unit: the study is not found. Only the reading of the request is covered, so a failure of
      // the action itself is not hidden as a 404.
      LOG.warn("The study requested could not be read from the request: {}", e.getClass().getSimpleName());
      BaseAction action = (BaseAction) invocation.getAction();
      return action.NOT_FOUND;
    }

    try {
      if (!isPrivate) {
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
    baseAction.setSession(session);
    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    boolean canSwitchProject = false;
    baseAction.setSession(session);
    String projectParameter = parameters.get(APConstants.STUDY_REQUEST_ID).getMultipleValues()[0];

    String cycle = parameters.get(APConstants.CYCLE).getMultipleValues()[0];
    String year = parameters.get(APConstants.YEAR_REQUEST).getMultipleValues()[0];
    // String crpID = parameters.get(APConstants.CRP_ID).getMultipleValues()[0];

    expectedId = Long.parseLong(projectParameter);

    ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(expectedId);
    GlobalUnit unit = null;
    if (projectExpectedStudy.getProject() != null) {
      Project project = projectExpectedStudy.getProject();

      unit = project.getGlobalUnitProjects().stream().filter(gu -> gu.isActive() && gu.isOrigin())
        .collect(Collectors.toList()).get(0).getGlobalUnit();
    }

    Long crpID = baseAction.getCrpID();
    if (unit != null) {
      crpID = unit.getId();
    }

    // Crp session is empty
    if (crpID == null) {
      String[] actionMap = ActionContext.getContext().getActionName().split("/");
      if (actionMap.length > 1) {
        String enteredCrp = actionMap[0];
        crpID = crpManager.findGlobalUnitByAcronym(enteredCrp).getId();
      }
    }

    phase = phaseManager.findCycle(cycle, Integer.valueOf(year), false, crpID);

    if (projectExpectedStudy != null && projectExpectedStudy.isActive()) {
      isPrivate = false;

      if (projectExpectedStudy.getProjectExpectedStudyInfo(phase).getIsPublic() != null
        && !projectExpectedStudy.getProjectExpectedStudyInfo(phase).getIsPublic()) {
        isPrivate = true;
      }
    }


  }

}
