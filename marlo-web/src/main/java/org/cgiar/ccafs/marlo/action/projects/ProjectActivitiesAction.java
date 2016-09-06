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

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectActivitiesAction extends BaseAction {


  private static final long serialVersionUID = 2146101620783927003L;


  private Crp loggedCrp;

  private Project project;


  private Map<String, String> status;

  private long projectID;


  private List<ProjectPartnerPerson> partnerPersons;

  private ProjectManager projectManager;

  private CrpManager crpManager;

  private ProjectPartnerPersonManager projectPartnerPersonManager;


  @Inject
  public ProjectActivitiesAction(APConfig config, ProjectManager projectManager, CrpManager crpManager,
    ProjectPartnerPersonManager projectPartnerPersonManager) {
    super(config);
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.projectPartnerPersonManager = projectPartnerPersonManager;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public List<ProjectPartnerPerson> getPartnerPersons() {
    return partnerPersons;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public Map<String, String> getStatus() {
    return status;
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    project = projectManager.getProjectById(projectID);

    if (project != null) {

      project
        .setOpenProjectActivities(
          new ArrayList<Activity>(
            project.getActivities().stream()
              .filter(a -> a.isActive()
                && ((a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
                  || (a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))))
              .collect(Collectors.toList())));

      project
        .setClosedProjectActivities(
          new ArrayList<Activity>(
            project.getActivities().stream()
              .filter(a -> a.isActive()
                && ((a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())
                  || (a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId())))))
              .collect(Collectors.toList())));

      status = new HashMap<>();
      List<ProjectStatusEnum> list = Arrays.asList(ProjectStatusEnum.values());
      for (ProjectStatusEnum projectStatusEnum : list) {
        status.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
      }

      project.setProjectDeliverables(new ArrayList<Deliverable>(
        project.getDeliverables().stream().filter(d -> d.isActive()).collect(Collectors.toList())));

      partnerPersons = projectPartnerPersonManager.findAll().stream()
        .filter(pp -> pp.isActive() && pp.getProjectPartner().getProject().getId() == project.getId())
        .collect(Collectors.toList());

    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_ACTIVITIES_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (project.getOpenProjectActivities() != null) {
        project.getOpenProjectActivities().clear();
      }

      if (project.getClosedProjectActivities() != null) {
        project.getClosedProjectActivities().clear();
      }

      if (partnerPersons != null) {
        partnerPersons.clear();
      }
    }
  }

  @Override
  public String save() {
    if (this.hasPermission("*")) {

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setPartnerPersons(List<ProjectPartnerPerson> partnerPersons) {
    this.partnerPersons = partnerPersons;
  }

  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setStatus(Map<String, String> status) {
    this.status = status;
  }

}
