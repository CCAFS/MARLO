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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectCommunicationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectCommunication;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 * @author Christian Garcia- CIAT/CCAFS
 */
public class ProjectOutcomeAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 4520862722467820286L;
  private ProjectManager projectManager;
  private ProjectMilestoneManager projectMilestoneManager;
  private ProjectCommunicationManager projectCommunicationManager;

  private CrpManager crpManager;
  private SrfTargetUnitManager srfTargetUnitManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;


  private ProjectOutcomeManager projectOutcomeManager;
  // Front-end
  private long projectID;
  private long projectOutcomeID;
  private Crp loggedCrp;
  private Project project;
  private List<CrpMilestone> milestones;
  private List<SrfTargetUnit> targetUnits;
  private CrpProgramOutcome crpProgramOutcome;

  private ProjectOutcome projectOutcome;


  @Inject
  public ProjectOutcomeAction(APConfig config, ProjectManager projectManager, CrpManager crpManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, ProjectOutcomeManager projectOutcomeManager,
    SrfTargetUnitManager srfTargetUnitManager, ProjectMilestoneManager projectMilestoneManager,
    ProjectCommunicationManager projectCommunicationManager) {
    super(config);
    this.projectManager = projectManager;
    this.srfTargetUnitManager = srfTargetUnitManager;
    this.crpManager = crpManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.projectOutcomeManager = projectOutcomeManager;
    this.projectMilestoneManager = projectMilestoneManager;
    this.projectCommunicationManager = projectCommunicationManager;

  }


  public int getIndex(ProjectCommunication milestone) {
    return projectOutcome.getMilestones().indexOf(milestone);
  }


  public int getIndex(ProjectMilestone communication) {
    return projectOutcome.getCommunications().indexOf(communication);
  }

  public List<CrpMilestone> getMilestones() {
    return milestones;
  }

  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }


  public ProjectOutcome getProjectOutcome() {
    return projectOutcome;
  }


  public long getProjectOutcomeID() {
    return projectOutcomeID;
  }


  public List<SrfTargetUnit> getTargetUnits() {
    return targetUnits;
  }


  public ProjectCommunication loadProjectCommunication(int year) {

    List<ProjectCommunication> projectCommunications = projectOutcome.getCommunications().stream()
      .filter(c -> c.isActive() && c.getYear() == year).collect(Collectors.toList());


    if (projectCommunications.size() > 0) {
      return projectCommunications.get(0);
    }

    return new ProjectCommunication();


  }

  public List<ProjectMilestone> loadProjectMilestones(int year) {

    List<ProjectMilestone> projectMilestones = projectOutcome.getMilestones().stream()
      .filter(c -> c.isActive() && c.getYear() == year).collect(Collectors.toList());

    return projectMilestones;


  }

  @Override
  public void prepare() throws Exception {

    // Get current CRP
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    try {
      projectOutcomeID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_OUTCOME_REQUEST_ID)));
    } catch (Exception e) {

    }
    projectOutcome = projectOutcomeManager.getProjectOutcomeById(projectOutcomeID);
    project = projectManager.getProjectById(projectOutcome.getProject().getId());
    projectID = project.getId();

    if (projectOutcome != null) {
      crpProgramOutcome = projectOutcome.getCrpProgramOutcome();
      milestones = projectOutcome.getCrpProgramOutcome().getCrpMilestones().stream().filter(c -> c.isActive())
        .collect(Collectors.toList());
    }

    projectOutcome.setMilestones(
      projectOutcome.getProjectMilestones().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

    projectOutcome.setCommunications(
      projectOutcome.getProjectCommunications().stream().filter(c -> c.isActive()).collect(Collectors.toList()));


    /*
     * Loading basic List
     */
    targetUnits = srfTargetUnitManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_CONTRIBRUTIONCRP_BASE_PERMISSION, params));

  }


  @Override
  public String save() {


    this.saveProjectOutcome();
    this.saveMilestones();
    this.saveCommunications();
    if (this.hasPermission("canEdit")) {
      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage(this.getText("saving.saved"));
      }
      return SUCCESS;
    } else

    {

      return NOT_AUTHORIZED;
    }
  }

  public void saveCommunications() {

    ProjectOutcome projectOutcomeDB = projectOutcomeManager.getProjectOutcomeById(projectOutcomeID);
    for (ProjectCommunication projectCommunication : projectOutcomeDB.getProjectCommunications().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {

      if (projectOutcome.getCommunications() == null) {
        projectOutcome.setCommunications(new ArrayList<>());
      }
      if (!projectOutcome.getCommunications().contains(projectCommunication)) {
        projectCommunicationManager.deleteProjectCommunication(projectCommunication.getId());

      }
    }

    if (projectOutcome.getCommunications() != null) {
      for (ProjectCommunication projectCommunication : projectOutcome.getCommunications()) {
        if (projectCommunication.getId() == null) {
          projectCommunication.setCreatedBy(this.getCurrentUser());

          projectCommunication.setActiveSince(new Date());
          projectCommunication.setActive(true);
          projectCommunication.setProjectOutcome(projectOutcome);
          projectCommunication.setModifiedBy(this.getCurrentUser());
          projectCommunication.setModificationJustification("");

        } else {
          ProjectCommunication projectCommunicationDB =
            projectCommunicationManager.getProjectCommunicationById(projectCommunication.getId());
          projectCommunication.setCreatedBy(projectCommunicationDB.getCreatedBy());

          projectCommunication.setActiveSince(projectCommunicationDB.getActiveSince());
          projectCommunication.setActive(true);
          projectCommunication.setProjectOutcome(projectOutcome);
          projectCommunication.setModifiedBy(this.getCurrentUser());
          projectCommunication.setModificationJustification("");
        }
        projectCommunicationManager.saveProjectCommunication(projectCommunication);

      }
    }
  }

  public void saveMilestones() {

    ProjectOutcome projectOutcomeDB = projectOutcomeManager.getProjectOutcomeById(projectOutcomeID);
    for (ProjectMilestone projectMilestone : projectOutcomeDB.getProjectMilestones().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {

      if (projectOutcome.getMilestones() == null) {
        projectOutcome.setMilestones(new ArrayList<>());
      }
      if (!projectOutcome.getMilestones().contains(projectMilestone)) {
        projectMilestoneManager.deleteProjectMilestone(projectMilestone.getId());

      }
    }

    if (projectOutcome.getMilestones() != null) {
      for (ProjectMilestone projectMilestone : projectOutcome.getMilestones()) {
        if (projectMilestone.getId() == null) {
          projectMilestone.setCreatedBy(this.getCurrentUser());

          projectMilestone.setActiveSince(new Date());
          projectMilestone.setActive(true);
          projectMilestone.setProjectOutcome(projectOutcome);
          projectMilestone.setModifiedBy(this.getCurrentUser());
          projectMilestone.setModificationJustification("");

        } else {
          ProjectMilestone projectMilestoneDB =
            projectMilestoneManager.getProjectMilestoneById(projectMilestone.getId());
          projectMilestone.setCreatedBy(projectMilestoneDB.getCreatedBy());

          projectMilestone.setActiveSince(projectMilestoneDB.getActiveSince());
          projectMilestone.setActive(true);
          projectMilestone.setProjectOutcome(projectOutcome);
          projectMilestone.setModifiedBy(this.getCurrentUser());
          projectMilestone.setModificationJustification("");
        }
        projectMilestoneManager.saveProjectMilestone(projectMilestone);

      }
    }
  }

  public void saveProjectOutcome() {

    int startYear = 0;
    int endYear = 0;
    Calendar startDate = Calendar.getInstance();
    startDate.setTime(project.getStartDate());
    startYear = startDate.get(Calendar.YEAR);

    Calendar endDate = Calendar.getInstance();
    endDate.setTime(project.getEndDate());
    endYear = endDate.get(Calendar.YEAR);

    if (this.getCurrentCycleYear() == startYear || this.getCurrentCycleYear() == endYear) {
      projectOutcome.setActive(true);
      projectOutcome.setModifiedBy(this.getCurrentUser());
      projectOutcome.setCreatedBy(this.getCurrentUser());
      projectOutcome.setActiveSince(new Date());
      projectOutcome.setCrpProgramOutcome(crpProgramOutcome);
      projectOutcome.setProject(project);
      projectOutcome.setId(projectOutcomeID);
      projectOutcomeManager.saveProjectOutcome(projectOutcome);

    }

  }


  public void setMilestones(List<CrpMilestone> milestones) {
    this.milestones = milestones;
  }


  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectOutcome(ProjectOutcome projectOutcome) {
    this.projectOutcome = projectOutcome;
  }

  public void setProjectOutcomeID(long projectOutcomeID) {
    this.projectOutcomeID = projectOutcomeID;
  }

  public void setTargetUnits(List<SrfTargetUnit> targetUnits) {
    this.targetUnits = targetUnits;
  }

}
