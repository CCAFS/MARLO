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
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.NoPhaseException;

import java.io.Serializable;
import java.util.Calendar;
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
public class EditProjectOutcomeInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 1423197153747668108L;


  private Map<String, Parameter> parameters;
  private Map<String, Object> session;
  private GlobalUnit crp;
  private long projectOutcomeId = 0;
  private Phase phase;
  private PhaseManager phaseManager;
  private final ProjectOutcomeManager projectOutcomeManager;
  private final ProjectManager projectManager;

  @Inject
  public EditProjectOutcomeInterceptor(ProjectOutcomeManager projectOutcomeManager, ProjectManager projectManager,
    PhaseManager phaseManager) {
    this.projectOutcomeManager = projectOutcomeManager;
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
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
      BaseAction action = (BaseAction) invocation.getAction();
      return action.NOT_FOUND;
    }
  }

  void setPermissionParameters(ActionInvocation invocation) throws NoPhaseException {
    BaseAction baseAction = (BaseAction) invocation.getAction();
    User user = (User) session.get(APConstants.SESSION_USER);
    baseAction.setSession(session);
    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    boolean canSwitchProject = false;
    baseAction.setSession(session);


    // String projectParameter = ((String[]) parameters.get(APConstants.PROJECT_OUTCOME_REQUEST_ID))[0];
    String projectParameter = parameters.get(APConstants.PROJECT_OUTCOME_REQUEST_ID).getMultipleValues()[0];

    projectOutcomeId = Long.parseLong(projectParameter);

    ProjectOutcome outcome = projectOutcomeManager.getProjectOutcomeById(projectOutcomeId);
    if (!outcome.getPhase().equals(baseAction.getActualPhase())) {
      List<ProjectOutcome> projectOutcomes = outcome.getProject().getProjectOutcomes().stream()
        .filter(c -> c.isActive()
          && c.getCrpProgramOutcome().getComposeID().equals(outcome.getCrpProgramOutcome().getComposeID())
          && c.getPhase().equals(baseAction.getActualPhase()))
        .collect(Collectors.toList());
      if (!projectOutcomes.isEmpty()) {
        projectOutcomeId = projectOutcomes.get(0).getId();
      }
    }
    ProjectOutcome project = projectOutcomeManager.getProjectOutcomeById(projectOutcomeId);
    phase = baseAction.getActualPhase();
    phase = phaseManager.getPhaseById(phase.getId());
    if (project != null && project.isActive()) {

      String params[] = {crp.getAcronym(), project.getProject().getId() + ""};
      if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin()) {
        if (!baseAction.isSubmit(project.getProject().getId())) {

          canSwitchProject = true;
        }

        canEdit = true;
      } else {
        List<Project> projects = projectManager.getUserProjects(user.getId(), crp.getAcronym());
        if (projects.contains(project.getProject()) && baseAction
          .hasPermission(baseAction.generatePermission(Permission.PROJECT_CONTRIBRUTIONCRP_EDIT_PERMISSION, params))) {


          canEdit = true;

        }

        if (baseAction.isSubmit(project.getProject().getId())) {
          canEdit = false;

        }
        if (baseAction.isCrpClosed()) {
          if (!(baseAction.hasSpecificities(APConstants.CRP_PMU) && baseAction.isPMU())) {
            canEdit = false;
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
        hasPermissionToEdit = ((baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin())) ? true : baseAction
          .hasPermission(baseAction.generatePermission(Permission.PROJECT_CONTRIBRUTIONCRP_EDIT_PERMISSION, params));
      }


      if (baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__SWITCH, params))) {
        canSwitchProject = true;
      }

      if (phase.getProjectPhases().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == project.getProject().getId())
        .collect(Collectors.toList()).isEmpty()) {
        canEdit = false;
      }

      if (phase.getProjectPhases().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == project.getProject().getId())
        .collect(Collectors.toList()).isEmpty()) {
        List<ProjectInfo> infos =
          project.getProject().getProjectInfos().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        infos.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));

        baseAction.setActualPhase(infos.get(infos.size() - 1).getPhase());
      }
      if (!baseAction.getActualPhase().getEditable()) {
        canEdit = false;
      }

      Calendar cal = Calendar.getInstance();
      cal.setTime(project.getProject().getProjecInfoPhase(baseAction.getActualPhase()).getEndDate());
      if (project.getProject().getProjecInfoPhase(baseAction.getActualPhase()).getStatus().longValue() == Long
        .parseLong(ProjectStatusEnum.Ongoing.getStatusId())
        && baseAction.getActualPhase().getYear() > cal.get(Calendar.YEAR)) {
        canEdit = false;
        canSwitchProject = false;
        baseAction.setEditStatus(true);

      }
      if (project.getProject().getProjecInfoPhase(baseAction.getActualPhase()).getStatus().longValue() == Long
        .parseLong(ProjectStatusEnum.Cancelled.getStatusId())

        || project.getProject().getProjecInfoPhase(baseAction.getActualPhase()).getStatus().longValue() == Long
          .parseLong(ProjectStatusEnum.Complete.getStatusId())) {
        canEdit = false;
        baseAction.setEditStatus(true);
      }
      if (project.getProject().getProjecInfoPhase(baseAction.getActualPhase()).getPhase().getDescription()
        .equals(APConstants.REPORTING)
        && project.getProject().getProjecInfoPhase(baseAction.getActualPhase()).getPhase().getYear() == 2016) {
        canEdit = false;
        baseAction.setEditStatus(false);
      }

      if (parameters.get(APConstants.TRANSACTION_ID).isDefined()) {
        // String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];

        editParameter = false;
        // If the user is not asking for edition privileges we don't need to validate them.

      }
      if (!baseAction.getActualPhase().getEditable()) {
        canEdit = false;
        baseAction.setCanEditPhase(false);
      }
      // Set the variable that indicates if the user can edit the section
      if (!editParameter) {
        baseAction.setEditStatus(false);
      }
      baseAction.setEditableParameter(editParameter && canEdit);
      baseAction.setCanEdit(canEdit);
      baseAction.setCanSwitchProject(canSwitchProject);

    } else {
      throw new NullPointerException();
    }
  }

}
