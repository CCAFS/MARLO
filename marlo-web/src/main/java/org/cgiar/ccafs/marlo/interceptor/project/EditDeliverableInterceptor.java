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
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.NoPhaseException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import org.apache.struts2.dispatcher.Parameter;
import org.jfree.util.Log;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class EditDeliverableInterceptor extends AbstractInterceptor implements Serializable {

  private static final long serialVersionUID = 7287623847333177230L;


  private Map<String, Parameter> parameters;
  private Map<String, Object> session;
  private GlobalUnit crp;
  private long deliverableId = 0;
  private Phase phase;
  private PhaseManager phaseManager;
  private final DeliverableManager deliverableManager;
  private final ProjectManager projectManager;
  private final GlobalUnitManager crpManager;
  private GlobalUnit loggedCrp;
  private GlobalUnitProjectManager globalUnitProjectManager;
  private final LiaisonUserManager liaisonUserManager;
  private DeliverableInfoManager deliverableInfoManager;
  boolean completeInPreviousPhase = false;


  @Inject
  public EditDeliverableInterceptor(DeliverableManager deliverableManager, ProjectManager projectManager,
    PhaseManager phaseManager, GlobalUnitManager crpManager, GlobalUnitProjectManager globalUnitProjectManager,
    LiaisonUserManager liaisonUserManager, DeliverableInfoManager deliverableInfoManager) {
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
    this.projectManager = projectManager;
    this.deliverableManager = deliverableManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.liaisonUserManager = liaisonUserManager;
    this.deliverableInfoManager = deliverableInfoManager;
  }

  public Boolean canEditDeliverable(Deliverable deliverable, Phase phase) {


    if (deliverable.getDeliverableInfo(phase).getStatus() != null) {
      if (deliverable.getDeliverableInfo(phase).getStatus().intValue() == Integer
        .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
        if (deliverable.getDeliverableInfo(phase).getNewExpectedYear() != null
          && deliverable.getDeliverableInfo(phase).getNewExpectedYear() >= phase.getYear()) {
          return true;
        }

      }
      if (deliverable.getDeliverableInfo(phase).getStatus().intValue() == Integer
        .parseInt(ProjectStatusEnum.Complete.getStatusId())) {
        return false;
      }
      if (deliverable.getDeliverableInfo(phase).getStatus().intValue() == Integer
        .parseInt(ProjectStatusEnum.Cancelled.getStatusId())) {
        return false;
      }
    }
    if (deliverable.getDeliverableInfo(phase).getYear() >= phase.getYear()) {
      return true;
    }
    return false;

  }

  public void checkDeliverableStatusInPreviousPhases(Deliverable deliverable) {
    int status1 = this.getStatusFromPreviousPhase(deliverable, phase.getId());
    // int status2 = this.getStatusFromPreviousPhase(deliverable,
    // phaseManager.findPreviousPhase(phase.getId()).getId());

    if (status1 == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())) {
      // Set deliverable Not editable
      completeInPreviousPhase = true;
    } else {
      completeInPreviousPhase = false;
    }
  }

  public DeliverableInfo deliverableInfoByPhase(Deliverable deliverable, Phase phase) {
    DeliverableInfo deliverableInfoPhase = new DeliverableInfo();
    try {
      List<DeliverableInfo> deliverableInfos =
        deliverableInfoManager.getDeliverablesInfoByDeliverableId(deliverable.getId());
      if (deliverableInfos != null) {
        deliverableInfoPhase =
          deliverableInfos.stream().filter(di -> di != null && di.getPhase() != null && di.getPhase().getId() != null
            && di.getPhase().getId().equals(phase.getId())).collect(Collectors.toList()).get(0);
      }
    } catch (Exception e) {
      Log.error(e + " error getting deliverable info by phase");
    }
    return deliverableInfoPhase;
  }


  public int getStatusFromPreviousPhase(Deliverable deliverable, long phaseId) {
    Phase previousPhase = phaseManager.findPreviousPhase(phaseId);
    if (previousPhase != null) {
      DeliverableInfo deliverableInfo = this.deliverableInfoByPhase(deliverable, previousPhase);
      if (deliverableInfo != null && deliverableInfo.getStatus() != null) {
        return deliverableInfo.getStatus();
      }
    }
    return 0;
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
      BaseAction action = (BaseAction) invocation.getAction();
      return action.NOT_FOUND;
    }
  }

  void setPermissionParameters(ActionInvocation invocation) throws NoPhaseException {
    User user = (User) session.get(APConstants.SESSION_USER);
    BaseAction baseAction = (BaseAction) invocation.getAction();
    baseAction.setSession(session);
    boolean canEdit = false;
    boolean hasPermissionToEdit = false;
    boolean editParameter = false;
    boolean canSwitchProject = false;
    baseAction.setSession(session);
    // String projectParameter = ((String[]) parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID))[0];
    String projectParameter = parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID).getMultipleValues()[0];
    phase = baseAction.getActualPhase();
    phase = phaseManager.getPhaseById(phase.getId());
    deliverableId = Long.parseLong(projectParameter);
    boolean contactPointEditProject = baseAction.hasSpecificities(APConstants.CRP_CONTACT_POINT_EDIT_PROJECT);

    loggedCrp = (GlobalUnit) session.get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableId);

    Project project = projectManager.getProjectById(deliverable.getProject().getId());

    this.checkDeliverableStatusInPreviousPhases(deliverable);

    // Get The Crp/Center/Platform where the project was created
    GlobalUnitProject globalUnitProject =
      globalUnitProjectManager.findByProjectAndGlobalUnitId(project.getId(), loggedCrp.getId());

    if (deliverable != null && deliverable.isActive()) {

      if (!globalUnitProject.isOrigin()) {
        Phase ph = baseAction.getActualPhase();
        GlobalUnitProject globalUnitProjectOrigin = globalUnitProjectManager.findByProjectId(project.getId());
        List<Phase> phases = globalUnitProjectOrigin.getGlobalUnit().getPhases().stream()
          .filter(c -> c.isActive() && c.getDescription().equals(baseAction.getActualPhase().getDescription())
            && c.getYear() == baseAction.getActualPhase().getYear())
          .collect(Collectors.toList());
        if (phases.size() > 0) {
          baseAction.setPhaseID(phases.get(0).getId());
          project.getProjecInfoPhase(phases.get(0));
        }

        phase = baseAction.getActualPhase();
        phase = phaseManager.getPhaseById(phase.getId());
      }


      String params[] = {crp.getAcronym(), deliverable.getProject().getId() + ""};

      if (baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin() || baseAction.canAcessCrpAdmin()) {

        if (!baseAction.isSubmit(deliverable.getProject().getId())) {

          canSwitchProject = true;
        }

        canEdit = true;
      } else {
        // List<Project> projects = projectManager.getUserProjects(user.getId(), crp.getAcronym());
        if (baseAction
          .hasPermission(baseAction.generatePermission(Permission.PROJECT_DELIVERABLE_EDIT_PERMISSION, params))) {
          canEdit = true;
        }

        if (baseAction.isSubmit(deliverable.getProject().getId()) && !baseAction.getActualPhase().getUpkeep()) {
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
      if (editParameter || parameters.get("save").isDefined()) {
        hasPermissionToEdit =
          ((baseAction.canAccessSuperAdmin() || baseAction.canEditCrpAdmin() || baseAction.canAcessCrpAdmin())) ? true
            : baseAction.hasPermission(
              baseAction.generatePermission(Permission.PROJECT_DELIVERABLE_LIST_EDIT_PERMISSION, params));
      }

      LiaisonUser lUser = liaisonUserManager.getLiaisonUserByUserId(user.getId(), loggedCrp.getId());
      if (contactPointEditProject && lUser != null) {
        LiaisonInstitution liaisonInstitution = lUser.getLiaisonInstitution();
        ProjectPartner projectPartner = project.getLeader();

        Institution institutionProject = projectPartner.getInstitution();

        Institution institutionCp = liaisonInstitution.getInstitution();

        if (institutionCp != null) {
          if (institutionCp.getId().equals(institutionProject.getId())) {
            canSwitchProject = true;
          } else {
            if (baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__SWITCH, params))) {
              canSwitchProject = true;
            }
          }
        } else {
          if (baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__SWITCH, params))) {
            canSwitchProject = true;
          }
        }
      } else {
        if (baseAction.hasPermission(baseAction.generatePermission(Permission.PROJECT__SWITCH, params))) {
          canSwitchProject = true;
        }
      }

      if (!baseAction.isUpKeepActive() && baseAction
        .hasPermission(baseAction.generatePermission(Permission.PROJECT_DELIVERABLE_EDIT_PERMISSION, params))) {
        if (deliverable.getDeliverableInfo(baseAction.getActualPhase()).getStatus() != null) {
          if (deliverable.getDeliverableInfo(baseAction.getActualPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId())) {
            canEdit = false;
          }
        }


        if ((baseAction.isReportingActive() || baseAction.isUpKeepActive())
          && deliverable.getDeliverableInfo(baseAction.getActualPhase()).getStatus() != null
          && deliverable.getDeliverableInfo(baseAction.getActualPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId())
          && deliverable.getDeliverableInfo(baseAction.getActualPhase()).getYear() == baseAction
            .getCurrentCycleYear()) {
          canEdit = true;
        }

        if ((baseAction.isReportingActive() || baseAction.isUpKeepActive())
          && deliverable.getDeliverableInfo(baseAction.getActualPhase()).getStatus() != null
          && deliverable.getDeliverableInfo(baseAction.getActualPhase()).getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId())
          && deliverable.getDeliverableInfo(baseAction.getActualPhase()).getNewExpectedYear() != null
          && deliverable.getDeliverableInfo(baseAction.getActualPhase()).getNewExpectedYear().intValue() >= baseAction
            .getCurrentCycleYear()) {
          canEdit = true;
        }
      }

      if (phase.getProjectPhases().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == deliverable.getProject().getId())
        .collect(Collectors.toList()).isEmpty()) {
        List<ProjectInfo> infos =
          deliverable.getProject().getProjectInfos().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        infos.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));

        baseAction.setActualPhase(infos.get(infos.size() - 1).getPhase());
      }
      if (!baseAction.getActualPhase().getEditable()) {
        canEdit = false;
      }

      if (baseAction.isPlanningActive() && !baseAction.isUpKeepActive()) {
        if (!this.canEditDeliverable(deliverable, phase)) {
          canEdit = false;
          baseAction.setEditStatus(true);
        }
      }

      if (phase.getProjectPhases().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == deliverable.getProject().getId())
        .collect(Collectors.toList()).isEmpty()) {
        List<ProjectInfo> infos =
          deliverable.getProject().getProjectInfos().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        infos.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));

        baseAction.setActualPhase(infos.get(infos.size() - 1).getPhase());
      }
      if (!baseAction.getActualPhase().getEditable()) {
        canEdit = false;
      }

      if (baseAction.isPlanningActive() && !baseAction.isUpKeepActive()) {
        if (deliverable.getDeliverableInfo(baseAction.getActualPhase()).getStatus().longValue() == Long
          .parseLong(ProjectStatusEnum.Cancelled.getStatusId())

          || deliverable.getDeliverableInfo(baseAction.getActualPhase()).getStatus().longValue() == Long
            .parseLong(ProjectStatusEnum.Complete.getStatusId())) {
          canEdit = false;
          baseAction.setEditStatus(true);
        }
      }

      // TODO What is This?
      if (deliverable.getProject().getProjecInfoPhase(baseAction.getActualPhase()).getPhase().getDescription()
        .equals(APConstants.REPORTING)
        && deliverable.getProject().getProjecInfoPhase(baseAction.getActualPhase()).getPhase().getYear() == 2016) {
        canEdit = false;
        baseAction.setEditStatus(false);
      }

      // Set the variable that indicates if the user can edit the section
      if (parameters.get(APConstants.TRANSACTION_ID).isDefined()) {
        // String stringEditable = ((String[]) parameters.get(APConstants.EDITABLE_REQUEST))[0];

        editParameter = false;
        // If the user is not asking for edition privileges we don't need to validate them.

      }

      // Check if is a Shared project (Crp to Center)
      if (!globalUnitProject.isOrigin()) {
        canEdit = false;
      }

      if (!baseAction.getActualPhase().getEditable()) {
        canEdit = false;
        baseAction.setCanEditPhase(false);
        baseAction.setEditStatus(false);
      }
      // Set canEdit variable if the deliverable is not completed in previous phases
      if (completeInPreviousPhase && (!baseAction.canAccessSuperAdmin() && !baseAction.canAcessCrpAdmin())) {
        canEdit = false;
        editParameter = false;
      }
      // Set the variable that indicates if the user can edit the section
      if (!editParameter) {
        baseAction.setEditStatus(false);
      }
      baseAction.setEditableParameter(editParameter && canEdit);
      baseAction.setCanEdit(canEdit);
      baseAction.setCanSwitchProject(canSwitchProject && globalUnitProject.isOrigin());

      // Allow Superadmin edit
      if (baseAction.canAccessSuperAdmin() && editParameter) {
        baseAction.setEditableParameter(true);
        baseAction.setCanEdit(true);
        baseAction.setEditStatus(true);
      }
    } else {
      throw new NullPointerException();
    }
  }
}
