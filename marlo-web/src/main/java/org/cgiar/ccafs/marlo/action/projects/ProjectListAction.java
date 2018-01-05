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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPhaseManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class ProjectListAction extends BaseAction {


  private static final long serialVersionUID = -793652591843623397L;


  private final Logger logger = LoggerFactory.getLogger(ProjectListAction.class);

  private GlobalUnit loggedCrp;


  private SectionStatusManager sectionStatusManager;


  private long projectID;
  // Managers
  private ProjectManager projectManager;
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  private GlobalUnitProjectManager globalUnitProjectManager;
  private PhaseManager phaseManager;
  private ProjectPhaseManager projectPhaseManager;

  private LiaisonUserManager liaisonUserManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;

  // Front-end
  private List<Project> myProjects;
  private List<Project> allProjects;
  private List<Project> closedProjects;
  private String filterBy;

  @Inject
  public ProjectListAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    LiaisonUserManager liaisonUserManager, LiaisonInstitutionManager liaisonInstitutionManager,
    ProjectPhaseManager projectPhaseManager, PhaseManager phaseManager,
    GlobalUnitProjectManager globalUnitProjectManager) {
    super(config);
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
    this.projectPhaseManager = projectPhaseManager;
    this.liaisonUserManager = liaisonUserManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
  }

  public String addAdminProject() {

    if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin()) {

      if (this.createProject(APConstants.PROJECT_CORE, null, null, true)) {
        this.clearPermissionsCache();
        return SUCCESS;
      }
      return INPUT;
    } else {

      LiaisonUser liaisonUser =
        liaisonUserManager.getLiaisonUserByUserId(this.getCurrentUser().getId(), loggedCrp.getId());

      if (liaisonUser != null && this.canAddCoreProject()) {
        long liId = liaisonUser.getLiaisonInstitution().getId();
        LiaisonInstitution liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liId);
        if (this.createProject(APConstants.PROJECT_CORE, liaisonUser, liaisonInstitution, true)) {

          this.clearPermissionsCache();
          return SUCCESS;
        }
        return INPUT;
      } else {
        return NOT_AUTHORIZED;
      }
    }
  }


  public String addBilateralProject() {
    if (this.canAccessSuperAdmin()) {

      if (this.createProject(APConstants.PROJECT_BILATERAL, null, null, false)) {
        this.clearPermissionsCache();
        return SUCCESS;
      }
      return INPUT;
    } else {

      LiaisonUser liaisonUser =
        liaisonUserManager.getLiaisonUserByUserId(this.getCurrentUser().getId(), loggedCrp.getId());

      if (liaisonUser != null && this.canAddBilateralProject()) {
        LiaisonInstitution liaisonInstitution =
          liaisonInstitutionManager.getLiaisonInstitutionById(liaisonUser.getLiaisonInstitution().getId());
        if (this.createProject(APConstants.PROJECT_BILATERAL, liaisonUser, liaisonInstitution, false)) {
          this.clearPermissionsCache();
          return SUCCESS;
        }
        return INPUT;
      } else {
        return NOT_AUTHORIZED;
      }
    }
  }


  public String addCoreProject() {

    if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin()) {

      if (this.createProject(APConstants.PROJECT_CORE, null, null, false)) {
        this.clearPermissionsCache();
        return SUCCESS;
      }
      return INPUT;
    } else {

      LiaisonUser liaisonUser =
        liaisonUserManager.getLiaisonUserByUserId(this.getCurrentUser().getId(), loggedCrp.getId());

      if (liaisonUser != null && this.canAddCoreProject()) {
        long liId = liaisonUser.getLiaisonInstitution().getId();
        LiaisonInstitution liaisonInstitution = liaisonInstitutionManager.getLiaisonInstitutionById(liId);
        if (this.createProject(APConstants.PROJECT_CORE, liaisonUser, liaisonInstitution, false)) {

          this.clearPermissionsCache();
          return SUCCESS;
        }
        return INPUT;
      } else {
        return NOT_AUTHORIZED;
      }
    }
  }


  /**
   * This method validates if a project can be deleted or not.
   * Keep in mind that a project can be deleted if it was created in the current planning cycle.
   * 
   * @param projectID is the project identifier.
   * @return true if the project can be deleted, false otherwise.
   */
  public boolean canDelete(long projectID) {
    // First, loop all projects that the user is able to edit.
    for (Project project : myProjects) {
      if (project.getId() == projectID) {
        if (this.isProjectNew(projectID)) {
          return true;
        }
      }
    }

    // If nothing returned yet, we need to loop the second list which is the list of projects that the user is not able
    // to edit.
    for (Project project : this.getAllProjects()) {
      if (project.getId() == projectID) {
        if (this.isProjectNew(projectID)) {
          return true;
        }
      }
    }
    // If nothing found, return false.
    return false;
  }


  public boolean createProject(String type, LiaisonUser liaisonUser, LiaisonInstitution liaisonInstitution,
    boolean admin) {

    if (liaisonUser != null) {

      Project project = new Project();
      project.setCreatedBy(this.getCurrentUser());
      project.setModifiedBy(this.getCurrentUser());
      project.setModificationJustification("New expected Project created");
      project.setActive(true);
      project.setActiveSince(new Date());
      project.setType(type);
      project.setLiaisonUser(liaisonUser);
      project.setLiaisonInstitution(liaisonInstitution);
      project.setScale(0);
      project.setCofinancing(false);

      project.setCreateDate(new Date());
      project.setProjectEditLeader(false);
      project.setPresetDate(new Date());
      project.setStatus(Long.parseLong(ProjectStatusEnum.Ongoing.getStatusId()));
      project.setAdministrative(new Boolean(admin));
      project = projectManager.saveProject(project);
      projectID = project.getId();

      // Setting the Global Unit Project
      GlobalUnitProject globalUnitProject = new GlobalUnitProject();
      globalUnitProject.setProject(project);
      globalUnitProject.setGlobalUnit(loggedCrp);
      globalUnitProject.setActive(true);
      globalUnitProject.setActiveSince(new Date());
      globalUnitProject.setModifiedBy(this.getCurrentUser());
      globalUnitProject.setCreatedBy(this.getCurrentUser());
      globalUnitProject.setOrigin(true);
      globalUnitProjectManager.saveGlobalUnitProject(globalUnitProject);

      Phase phase = this.phaseManager.findCycle(this.getCurrentCycle(), this.getCurrentCycleYear(), this.getCrpID());
      if (phase != null) {
        ProjectPhase projectPhase = new ProjectPhase();
        projectPhase.setPhase(phase);
        projectPhase.setProject(project);
        projectPhaseManager.saveProjectPhase(projectPhase);
      }
      SectionStatus status = null;
      if (status == null) {

        status = new SectionStatus();
        status.setCycle(this.getCurrentCycle());
        status.setYear(this.getCurrentCycleYear());
        status.setProject(project);
        status.setSectionName(ProjectSectionStatusEnum.ACTIVITIES.getStatus());


      }

      status.setMissingFields("");
      sectionStatusManager.saveSectionStatus(status);

      if (projectID > 0) {
        return true;
      }
      return false;

    } else {
      Project project = new Project();
      project.setCreatedBy(this.getCurrentUser());
      project.setModifiedBy(this.getCurrentUser());
      project.setModificationJustification("New expected Project created");
      project.setActive(true);
      project.setActiveSince(new Date());
      project.setType(type);
      project.setScale(0);
      project.setCofinancing(false);
      project.setCreateDate(new Date());
      project.setProjectEditLeader(false);
      project.setPresetDate(new Date());
      project.setStatus(Long.parseLong(ProjectStatusEnum.Ongoing.getStatusId()));
      project.setAdministrative(new Boolean(admin));

      project = projectManager.saveProject(project);
      projectID = project.getId();

      // Setting the Global Unit Project
      GlobalUnitProject globalUnitProject = new GlobalUnitProject();
      globalUnitProject.setProject(project);
      globalUnitProject.setGlobalUnit(loggedCrp);
      globalUnitProject.setActive(true);
      globalUnitProject.setActiveSince(new Date());
      globalUnitProject.setModifiedBy(this.getCurrentUser());
      globalUnitProject.setCreatedBy(this.getCurrentUser());
      globalUnitProject.setOrigin(true);
      globalUnitProjectManager.saveGlobalUnitProject(globalUnitProject);

      Phase phase = this.phaseManager.findCycle(this.getCurrentCycle(), this.getCurrentCycleYear(), this.getCrpID());
      if (phase != null) {
        ProjectPhase projectPhase = new ProjectPhase();
        projectPhase.setPhase(phase);
        projectPhase.setProject(project);
        projectPhaseManager.saveProjectPhase(projectPhase);
      }
      SectionStatus status = null;
      if (status == null) {

        status = new SectionStatus();
        status.setCycle(this.getCurrentCycle());
        status.setYear(this.getCurrentCycleYear());
        status.setProject(project);
        status.setSectionName(ProjectSectionStatusEnum.ACTIVITIES.getStatus());


      }
      status.setMissingFields("");
      sectionStatusManager.saveSectionStatus(status);
      if (projectID > 0) {
        return true;
      }
      return false;
    }

  }

  @Override
  public String delete() {
    // Deleting project.
    if (this.canDelete(projectID)) {
      String permissionStr =
        this.generatePermission(Permission.PROJECT_DELETE_BASE_PERMISSION, loggedCrp.getAcronym(), projectID + "");
      boolean permission = this.hasPermissionNoBase(permissionStr);
      if (permission) {
        Project project = projectManager.getProjectById(projectID);
        project.setActive(false);
        project
          .setModificationJustification(this.getJustification() == null ? "Project deleted" : this.getJustification());
        project.setModifiedBy(this.getCurrentUser());

        try {
          projectManager.deleteProject(project);
          for (ProjectPhase projectPhase : project.getProjectPhases()) {
            projectPhaseManager.deleteProjectPhase(projectPhase.getId());
          }
          this.addActionMessage(
            "message:" + this.getText("deleting.successProject", new String[] {this.getText("project").toLowerCase()}));
        } catch (Exception e) {
          logger.error("Unable to delete project", e);
          this.addActionError(this.getText("deleting.problem", new String[] {this.getText("project").toLowerCase()}));
          /**
           * Assume we don't need to re-throw the exception as this transaction is limited to deleting only.
           */
        }
      } else {
        this.addActionError(this.getText("projects.cannotDelete"));
      }
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }


  }

  public boolean deletePermission(long projectID) {
    String permissionStr =
      this.generatePermission(Permission.PROJECT_DELETE_BASE_PERMISSION, loggedCrp.getAcronym(), projectID + "");
    boolean permission = this.hasPermissionNoBase(permissionStr);
    return permission;
  }


  public List<Project> getAllProjects() {
    return allProjects;
  }

  public List<Project> getClosedProjects() {
    return closedProjects;
  }

  public String getFilterBy() {
    return filterBy;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }


  public List<Project> getMyProjects() {
    return myProjects;
  }

  public long getProjectID() {
    return projectID;
  }


  /**
   * load the flagships and regions for each project on list
   * 
   * @param list the list of project
   */
  public void loadFlagshipgsAndRegions(List<Project> list) {
    for (Project project : list) {
      List<CrpProgram> programs = new ArrayList<>();
      List<CrpProgram> regions = new ArrayList<>();
      for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
        .filter(c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList())) {
        programs.add(projectFocuses.getCrpProgram());
      }
      for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
        .filter(c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList())) {
        regions.add(projectFocuses.getCrpProgram());
      }
      project.setFlagships(programs);
      project.setRegions(regions);
    }
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    Phase phase =
      phaseManager.findCycle(this.getCurrentCycle(), this.getCurrentCycleYear(), loggedCrp.getId().longValue());
    if (projectManager.findAll() != null) {
      if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin()) {
        myProjects = new ArrayList<>();
        for (ProjectPhase projectPhase : phase.getProjectPhases()) {
          myProjects.add(projectPhase.getProject());
        }
        allProjects = new ArrayList<>();
      } else {
        allProjects = new ArrayList<>();
        for (ProjectPhase projectPhase : phase.getProjectPhases()) {
          allProjects.add(projectManager.getProjectById(projectPhase.getProject().getId()));
        }
        if (this.isPlanningActive()) {
          myProjects = projectManager.getUserProjects(this.getCurrentUser().getId(), loggedCrp.getAcronym()).stream()
            .filter(p -> p.isActive()).collect(Collectors.toList());
        } else {
          myProjects = projectManager.getUserProjectsReporting(this.getCurrentUser().getId(), loggedCrp.getAcronym())
            .stream().filter(p -> p.isActive()).collect(Collectors.toList());
        }
        List<Project> mProjects = new ArrayList<>();
        mProjects.addAll(myProjects);
        for (Project project : mProjects) {
          if (!allProjects.contains(project)) {
            myProjects.remove(project);
          }
        }
        allProjects.removeAll(myProjects);
      }
      this.loadFlagshipgsAndRegions(myProjects);
      if (allProjects != null) {
        this.loadFlagshipgsAndRegions(allProjects);
      }
    }
    // Get all Global Unit Projects
    List<GlobalUnitProject> globalUnitProjects = new ArrayList<>(loggedCrp.getGlobalUnitProjects());
    List<Project> guProjects = new ArrayList<>();
    for (GlobalUnitProject globalUnitProject : globalUnitProjects) {
      guProjects.add(globalUnitProject.getProject());
    }

    closedProjects = guProjects.stream()
      .filter(
        c -> c.isActive() && (c.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId())
          || c.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())))
      .collect(Collectors.toList());
    myProjects.removeAll(closedProjects);
    if (allProjects != null) {
      allProjects.removeAll(closedProjects);
    }
    this.loadFlagshipgsAndRegions(closedProjects);
    closedProjects.sort((p1, p2) -> p1.getStatus().compareTo(p2.getStatus()));
    String params[] = {loggedCrp.getAcronym() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_LIST_BASE_PERMISSION, params));
  }


  @Override
  public String save() {
    return SUCCESS;
  }


  public void setAllProjects(List<Project> allProjects) {
    this.allProjects = allProjects;
  }

  public void setClosedProjects(List<Project> closedProjects) {
    this.closedProjects = closedProjects;
  }


  public void setFilterBy(String filterBy) {
    this.filterBy = filterBy;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMyProjects(List<Project> myProjects) {
    this.myProjects = myProjects;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  @Override
  public void validate() {
    if (save) {

    }
  }

}