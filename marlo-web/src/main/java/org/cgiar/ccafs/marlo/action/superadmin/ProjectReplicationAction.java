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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ActivityManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectClusterActivityManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLocationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPhaseManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerContribution;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class ProjectReplicationAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -5068203564268629259L;
  private static Logger LOG = LoggerFactory.getLogger(ProjectReplicationAction.class);

  // Managers
  private GlobalUnitManager globalUnitManager;
  private ProjectManager projectManager;
  private ProjectBudgetManager projectBudgetManager;
  private PhaseManager phaseManager;
  private ProjectInfoManager projectInfoManager;
  private ProjectFocusManager projectFocusManager;
  private ProjectPhaseManager projectPhaseManager;
  private ProjectClusterActivityManager projectClusterActivityManager;
  private ProjectPartnerManager projectPartnerManager;
  private ProjectLocationManager projectLocationManager;
  private ActivityManager activityManager;
  private ProjectOutcomeManager projectOutcomeManager;

  // Variables
  private String entityByPhaseList;
  private List<GlobalUnit> crps;
  private long selectedPhaseID;
  private Phase phase;
  private Project project;


  public ProjectReplicationAction(APConfig config, PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    ProjectManager projectManager, ProjectBudgetManager projectBudgetManager, ProjectInfoManager projectInfoManager,
    ProjectPhaseManager projectPhaseManager, ProjectFocusManager projectFocusManager, ActivityManager activityManager,
    ProjectClusterActivityManager projectClusterActivityManager, ProjectPartnerManager projectPartnerManager,
    ProjectLocationManager projectLocationManager, ProjectOutcomeManager projectOutcomeManager) {
    super(config);
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.projectManager = projectManager;
    this.projectBudgetManager = projectBudgetManager;
    this.projectInfoManager = projectInfoManager;
    this.projectPhaseManager = projectPhaseManager;
    this.projectFocusManager = projectFocusManager;
    this.activityManager = activityManager;
    this.projectClusterActivityManager = projectClusterActivityManager;
    this.projectPartnerManager = projectPartnerManager;
    this.projectLocationManager = projectLocationManager;
    this.projectOutcomeManager = projectOutcomeManager;
  }

  /**
   * The name of the autosave file is constructed and the path is searched
   * 
   * @return Auto save file path
   */
  private Path getAutoSaveFilePath() {
    // get the class simple name
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + phase.getName() + "_" + phase.getYear()
      + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<GlobalUnit> getCrps() {
    return crps;
  }


  public String getEntityByPhaseList() {
    return entityByPhaseList;
  }


  public long getSelectedPhaseID() {
    return selectedPhaseID;
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();
    crps = globalUnitManager.findAll().stream().filter(c -> c.isMarlo() && c.isActive()).collect(Collectors.toList());
  }

  @Override
  public String save() {
    if (this.canAccessSuperAdmin()) {
      if (entityByPhaseList != null && !entityByPhaseList.isEmpty()) {
        LOG.debug("Start replication for phase: " + selectedPhaseID);
        phase = phaseManager.getPhaseById(selectedPhaseID);

        List<String> relationsName = new ArrayList<>();
        relationsName.add(APConstants.PROJECT_INFO_RELATION);
        // relationsName.add(APConstants.PROJECT_PHASE_RELATION);
        relationsName.add(APConstants.PROJECT_FOCUSES_RELATION);
        relationsName.add(APConstants.PROJECT_CLUSTER_ACTIVITIES_RELATION);
        relationsName.add(APConstants.PROJECT_PARTNERS_RELATION);
        relationsName.add(APConstants.PROJECT_LOCATIONS_RELATION);
        relationsName.add(APConstants.PROJECT_ACTIVITIES_RELATION);
        relationsName.add(APConstants.PROJECT_BUDGETS_RELATION);
        // relationsName.add(APConstants.PROJECT_OUTCOMES_RELATION);

        for (String id : entityByPhaseList.trim().split(",")) {
          LOG.debug("Replicating project: " + id);
          project = projectManager.getProjectById(new Long(id.trim()));
          ProjectInfo projectInfo = project.getProjecInfoPhase(phase);

          if (projectInfo != null && phase.getNext() != null) {
            // ProjectInfo Replication
            ProjectInfo projectInfoPhaseAdd = new ProjectInfo();
            projectInfoPhaseAdd.setProject(projectInfo.getProject());
            projectInfoPhaseAdd.setPhase(phase);
            projectInfoPhaseAdd.setProjectEditLeader(projectInfo.getProjectEditLeader());
            projectInfoPhaseAdd.updateProjectInfo(projectInfo);
            projectInfoPhaseAdd = projectInfoManager.saveProjectInfo(projectInfoPhaseAdd);

            // ProjectPhase Replication
            ProjectPhase projectPhase = new ProjectPhase();
            projectPhase.setPhase(phase);
            projectPhase.setProject(projectInfo.getProject());
            projectPhase = projectPhaseManager.saveProjectPhase(projectPhase);

            // ProjectFocus Replication
            List<ProjectFocus> projectFocus = projectInfo.getProject().getProjectFocuses().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
            for (ProjectFocus projectFocusDB : projectFocus) {
              projectFocusManager.saveProjectFocus(projectFocusDB);
            }

            // ProjectClusterActivity Replication
            List<ProjectClusterActivity> projectClusters =
              projectInfo.getProject().getProjectClusterActivities().stream()
                .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
            for (ProjectClusterActivity projectClusterActivity : projectClusters) {
              projectClusterActivityManager.saveProjectClusterActivity(projectClusterActivity);
            }

            // ProjectPartner Replication
            List<ProjectPartner> projectPartners = projectInfo.getProject().getProjectPartners().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
            for (ProjectPartner projectPartner : projectPartners) {
              projectPartner.setPartnerPersons(projectPartner.getProjectPartnerPersons().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList()));
              List<ProjectPartnerContribution> contributors = new ArrayList<>();


              List<ProjectPartnerContribution> partnerContributions = projectPartner.getProjectPartnerContributions()
                .stream().filter(c -> c.isActive()).collect(Collectors.toList());
              for (ProjectPartnerContribution projectPartnerContribution : partnerContributions) {
                contributors.add(projectPartnerContribution);
              }
              projectPartner.setSelectedLocations(new ArrayList<>());
              for (ProjectPartnerLocation projectPartnerLocation : projectPartner.getProjectPartnerLocations().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList())) {
                projectPartner.getSelectedLocations().add(projectPartnerLocation.getInstitutionLocation());
              }
              projectPartner.setPartnerContributors(contributors);
              projectPartner.setPartnerPersons(projectPartner.getProjectPartnerPersons().stream()
                .filter(c -> c != null && c.isActive()).collect(Collectors.toList()));
              projectPartnerManager.copyPartner(projectPartner, phase);
            }

            // ProjectLocation Replication
            List<ProjectLocation> projectLocations = projectInfo
              .getProject().getProjectLocations().stream().filter(c -> c != null && c.isActive() && c.getPhase() != null
                && projectInfo.getPhase() != null && c.getPhase().equals(projectInfo.getPhase()))
              .collect(Collectors.toList());
            for (ProjectLocation projectLocation : projectLocations) {
              projectLocationManager.copyProjectLocation(projectLocation, phase);
            }

            // Activity Replication
            List<Activity> activities = projectInfo.getProject().getActivities().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
            for (Activity activity : activities) {
              activity.setDeliverables(
                activity.getDeliverableActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
              activityManager.copyActivity(activity, phase);
            }

            // ProjectBudget Replication
            List<ProjectBudget> budgets = projectInfo.getProject().getProjectBudgets().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
            for (ProjectBudget projectBudget : budgets) {
              projectBudgetManager.copyProjectBudget(projectBudget, phase);
            }

            // ProjectOutcome Replication
            List<ProjectOutcome> outcomes = projectInfo.getProject().getProjectOutcomes().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(projectInfo.getPhase())).collect(Collectors.toList());
            for (ProjectOutcome projectOutcome : outcomes) {
              projectOutcome.setMilestones(
                projectOutcome.getProjectMilestones().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

              projectOutcome.setCommunications(projectOutcome.getProjectCommunications().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList()));
              projectOutcome.setNextUsers(
                projectOutcome.getProjectNextusers().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

              projectOutcomeManager.copyProjectOutcome(projectOutcome, phase);
            }

            projectManager.saveProject(project, this.getActionName(), relationsName, this.getActualPhase());

            // Delete autosave
            Path path = this.getAutoSaveFilePath();

            if (path.toFile().exists()) {
              path.toFile().delete();
            }
          }
        }
      }
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void setCrps(List<GlobalUnit> crps) {
    this.crps = crps;
  }

  public void setEntityByPhaseList(String entityByPhaseList) {
    this.entityByPhaseList = entityByPhaseList;
  }


  public void setSelectedPhaseID(long selectedPhaseID) {
    this.selectedPhaseID = selectedPhaseID;
  }


}
