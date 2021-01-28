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
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectPolicyListAction extends BaseAction {


  private static final long serialVersionUID = 3586039079035252726L;

  // Manager
  private ProjectPolicyManager projectPolicyManager;
  private ProjectPolicyInfoManager projectPolicyInfoManager;
  private SectionStatusManager sectionStatusManager;
  private ProjectManager projectManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;

  // Variables
  // Model for the back-end
  private Project project;
  // Model for the front-end
  private long projectID;
  private long policyID;
  private List<Integer> allYears;
  private List<ProjectPolicy> projectOldPolicies;
  private String justification;


  @Inject
  public ProjectPolicyListAction(APConfig config, ProjectPolicyManager projectPolicyManager,
    ProjectPolicyInfoManager projectPolicyInfoManager, SectionStatusManager sectionStatusManager,
    ProjectManager projectManager, ProjectInnovationCrpManager projectInnovationCrpManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager) {
    super(config);
    this.projectPolicyManager = projectPolicyManager;
    this.projectPolicyInfoManager = projectPolicyInfoManager;
    this.sectionStatusManager = sectionStatusManager;
    this.projectManager = projectManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
  }

  @Override
  public String add() {
    ProjectPolicy projectPolicy = new ProjectPolicy();

    projectPolicy.setProject(project);

    projectPolicy = projectPolicyManager.saveProjectPolicy(projectPolicy);

    ProjectPolicyInfo projectPolicyInfo =
      new ProjectPolicyInfo(this.getActualPhase(), projectPolicy, new Long(this.getCurrentCycleYear()));

    projectPolicyInfoManager.saveProjectPolicyInfo(projectPolicyInfo);

    policyID = projectPolicy.getId();

    if (policyID > 0) {

      return SUCCESS;
    }

    return INPUT;
  }


  @Override
  public String delete() {
    for (ProjectPolicy projectPolicy : project.getPolicies()) {
      if (projectPolicy.getId().longValue() == policyID) {
        ProjectPolicy projectPolicyBD = projectPolicyManager.getProjectPolicyById(policyID);
        for (SectionStatus sectionStatus : projectPolicyBD.getSectionStatuses()) {
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
        }
        projectPolicy.setModificationJustification(justification);
        projectPolicyManager.deleteProjectPolicy(projectPolicy.getId());
      }
    }
    return SUCCESS;
  }


  @Override
  public List<Integer> getAllYears() {
    return allYears;
  }

  @Override
  public String getJustification() {
    return justification;
  }

  public long getPolicyID() {
    return policyID;
  }


  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }

  public List<ProjectPolicy> getProjectOldPolicies() {
    return projectOldPolicies;
  }

  @Override
  public void prepare() throws Exception {
    projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    project = projectManager.getProjectById(projectID);


    allYears = project.getProjecInfoPhase(this.getActualPhase()).getAllYears();
    projectOldPolicies = new ArrayList<>();
    List<ProjectPolicy> policies =
      project.getProjectPolicies().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    project.setPolicies(new ArrayList<ProjectPolicy>());
    for (ProjectPolicy projectPolicy : policies) {
      if (projectPolicy.getProjectPolicyInfo(this.getActualPhase()) != null) {
        // SubIdos List
        if (projectPolicy.getProjectPolicySubIdos() != null) {
          projectPolicy.setSubIdos(new ArrayList<>(projectPolicy.getProjectPolicySubIdos().stream()
            .filter(o -> o.isActive() && o.getPhase().getId() == this.getActualPhase().getId())
            .collect(Collectors.toList())));
        }

        // SubIdos List
        if (projectPolicy.getProjectPolicySubIdos() != null) {
          projectPolicy.setSubIdos(new ArrayList<>(projectPolicy.getProjectPolicySubIdos().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getActualPhase().getId()))
            .collect(Collectors.toList())));
        }

        // Geographic Scope List
        if (projectPolicy.getProjectPolicyGeographicScopes() != null) {
          projectPolicy.setGeographicScopes(new ArrayList<>(projectPolicy.getProjectPolicyGeographicScopes().stream()
            .filter(o -> o.isActive() && o.getPhase().getId().equals(this.getActualPhase().getId()))
            .collect(Collectors.toList())));
        }

        if (projectPolicy.getGeographicScopes() != null) {
          for (ProjectPolicyGeographicScope projectPolicyGeographicScope : projectPolicy.getGeographicScopes()) {
            projectPolicyGeographicScope.setRepIndGeographicScope(repIndGeographicScopeManager
              .getRepIndGeographicScopeById(projectPolicyGeographicScope.getRepIndGeographicScope().getId()));
          }
        }
        if (projectPolicy.getProjectPolicyInfo(this.getActualPhase()).isPrevious()) {
          projectOldPolicies.add(projectPolicy);
        } else {
          project.getPolicies().add(projectPolicy);
        }
      }
    }
  }

  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
  }

  @Override
  public void setJustification(String justification) {
    this.justification = justification;
  }

  public void setPolicyID(long policyID) {
    this.policyID = policyID;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectOldPolicies(List<ProjectPolicy> projectOldPolicies) {
    this.projectOldPolicies = projectOldPolicies;
  }

}
