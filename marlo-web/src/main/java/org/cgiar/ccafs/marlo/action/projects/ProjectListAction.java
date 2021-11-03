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
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
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
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.PhaseComparator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.shiro.authz.AuthorizationInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class ProjectListAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  private static final Logger LOG = LoggerFactory.getLogger(ProjectListAction.class);

  private GlobalUnit loggedCrp;

  private SectionStatusManager sectionStatusManager;

  private long projectID;

  // Managers
  private ProjectManager projectManager;
  private ProjectBudgetManager projectBudgetManager;
  private ProjectInfoManager projectInfoManager;
  private GlobalUnitManager crpManager;
  private GlobalUnitProjectManager globalUnitProjectManager;
  private PhaseManager phaseManager;
  private ProjectPhaseManager projectPhaseManager;
  private LiaisonUserManager liaisonUserManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ProjectPartnerManager projectPartnerManager;
  private ProjectFocusManager projectFocusManager;

  // Front-end
  private List<Project> myProjects;
  private List<Project> allProjects;
  private List<Project> centerProjects;
  private List<Project> closedProjects;
  private String filterBy;

  @Inject
  public ProjectListAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    LiaisonUserManager liaisonUserManager, LiaisonInstitutionManager liaisonInstitutionManager,
    ProjectPhaseManager projectPhaseManager, PhaseManager phaseManager, ProjectInfoManager projectInfoManager,
    ProjectBudgetManager projectBudgetManager, GlobalUnitProjectManager globalUnitProjectManager,
    SectionStatusManager sectionStatusManager, ProjectPartnerManager projectPartnerManager,
    ProjectFocusManager projectFocusManager) {
    super(config);
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
    this.projectPhaseManager = projectPhaseManager;
    this.liaisonUserManager = liaisonUserManager;
    this.projectInfoManager = projectInfoManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.sectionStatusManager = sectionStatusManager;
    this.projectBudgetManager = projectBudgetManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.projectPartnerManager = projectPartnerManager;
    this.projectFocusManager = projectFocusManager;
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

          AuthorizationInfo info = ((APCustomRealm) this.securityContext.getRealm())
            .getAuthorizationInfo(this.securityContext.getSubject().getPrincipals());

          String params[] = {loggedCrp.getAcronym(), projectID + "", ""};
          info.getStringPermissions().add(this.generatePermission(Permission.PROJECT_ALL_EDITION, params));

          return SUCCESS;
        }
        return INPUT;
      } else {
        return NOT_AUTHORIZED;
      }
    }
  }

  public void addProjectOnPhase(Phase phase, Project project, LiaisonInstitution liaisonInstitution,
    LiaisonUser liaisonUser, String type, boolean admin) {
    if (phase != null) {
      ProjectPhase projectPhase = new ProjectPhase();
      projectPhase.setPhase(phase);
      projectPhase.setProject(project);
      projectPhaseManager.saveProjectPhase(projectPhase);
    }
    ProjectInfo projectInfo = new ProjectInfo();
    projectInfo.setModificationJustification("New expected Project created");
    projectInfo.setType(type);
    projectInfo.setLiaisonInstitution(liaisonInstitution);
    projectInfo.setScale(0);
    projectInfo.setCofinancing(false);
    // CIAT (Center) Project Creation
    if (this.isCenterGlobalUnit()) {
      projectInfo.setProjectEditLeader(true);

      // Create Center Partner
      ProjectPartner projectPartner = new ProjectPartner();
      projectPartner.setProject(project);
      projectPartner.setInstitution(loggedCrp.getInstitution());
      projectPartner.setPhase(this.getActualPhase());
      projectPartnerManager.saveProjectPartner(projectPartner);

    } else {
      projectInfo.setProjectEditLeader(false);
    }
    projectInfo.setPresetDate(new Date());
    projectInfo.setStatus(Long.parseLong(ProjectStatusEnum.Ongoing.getStatusId()));
    projectInfo.setAdministrative(new Boolean(admin));
    projectInfo.setPhase(phase);
    projectInfo.setProject(project);
    projectInfoManager.saveProjectInfo(projectInfo);
    SectionStatus status = null;
    if (status == null) {

      status = new SectionStatus();
      status.setCycle(this.getCurrentCycle());
      status.setYear(this.getCurrentCycleYear());
      status.setUpkeep(this.isUpKeepActive());
      status.setProject(project);
      status.setSectionName(ProjectSectionStatusEnum.ACTIVITIES.getStatus());

    }
    status.setMissingFields("");
    sectionStatusManager.saveSectionStatus(status);
  }

  /**
   * This method validates if a project can be deleted or not. Keep in mind that a
   * project can be deleted if it was created in the current planning cycle.
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

    // If nothing returned yet, we need to loop the second list which is the list of
    // projects that the user is not able
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
      project.setCreateDate(new Date());
      project = projectManager.saveProject(project);
      projectID = project.getId();

      // Setting the Global Unit Project
      GlobalUnitProject globalUnitProject = new GlobalUnitProject();
      globalUnitProject.setProject(project);
      globalUnitProject.setGlobalUnit(loggedCrp);
      globalUnitProject.setOrigin(true);
      globalUnitProjectManager.saveGlobalUnitProject(globalUnitProject);

      Phase phase = this.phaseManager.findCycle(this.getCurrentCycle(), this.getCurrentCycleYear(),
        this.getActualPhase().getUpkeep(), this.getCrpID());

      this.addProjectOnPhase(phase, project, liaisonInstitution, liaisonUser, type, admin);

      boolean hasNext = true;

      while (hasNext) {
        if (phase.getNext() != null) {
          this.addProjectOnPhase(phase.getNext(), project, liaisonInstitution, liaisonUser, type, admin);
          phase = phase.getNext();
        }
        hasNext = false;
      }
      if (projectID > 0) {
        return true;
      }
      return false;

    } else {
      Project project = new Project();
      project.setCreateDate(new Date());
      project = projectManager.saveProject(project);
      projectID = project.getId();

      // Setting the Global Unit Project
      GlobalUnitProject globalUnitProject = new GlobalUnitProject();
      globalUnitProject.setProject(project);
      globalUnitProject.setGlobalUnit(loggedCrp);
      globalUnitProject.setOrigin(true);
      globalUnitProjectManager.saveGlobalUnitProject(globalUnitProject);

      Phase phase = this.phaseManager.findCycle(this.getCurrentCycle(), this.getCurrentCycleYear(),
        this.getActualPhase().getUpkeep(), this.getCrpID());
      ProjectInfo projectInfo = new ProjectInfo();
      projectInfo.setModificationJustification("New expected Project created");
      projectInfo.setType(type);
      projectInfo.setLiaisonInstitution(liaisonInstitution);
      projectInfo.setScale(0);
      projectInfo.setCofinancing(false);

      if (this.isCenterGlobalUnit()) {
        projectInfo.setProjectEditLeader(true);

        // Create Center Partner
        ProjectPartner projectPartner = new ProjectPartner();
        projectPartner.setProject(project);
        projectPartner.setInstitution(loggedCrp.getInstitution());
        projectPartner.setPhase(this.getActualPhase());
        projectPartnerManager.saveProjectPartner(projectPartner);

      } else {
        projectInfo.setProjectEditLeader(false);
      }

      projectInfo.setPresetDate(new Date());
      projectInfo.setStatus(Long.parseLong(ProjectStatusEnum.Ongoing.getStatusId()));
      projectInfo.setAdministrative(new Boolean(admin));

      if (phase != null) {
        ProjectPhase projectPhase = new ProjectPhase();
        projectPhase.setPhase(phase);
        projectPhase.setProject(project);
        projectPhaseManager.saveProjectPhase(projectPhase);
      }
      projectInfo.setPhase(phase);
      projectInfo.setProject(project);
      projectInfoManager.saveProjectInfo(projectInfo);
      SectionStatus status = null;
      if (status == null) {

        status = new SectionStatus();
        status.setCycle(this.getCurrentCycle());
        status.setYear(this.getCurrentCycleYear());
        status.setUpkeep(this.isUpKeepActive());
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
        try {
          project.setModifiedBy(this.getCurrentUser());
          project.setModificationJustification("Deleted by : " + this.getCurrentUser().getComposedCompleteName());
          projectManager.deleteProject(project);
          for (ProjectPhase projectPhase : project.getProjectPhases()) {
            projectPhaseManager.deleteProjectPhase(projectPhase.getId());
          }
          this.addActionMessage(
            "message:" + this.getText("deleting.successProject", new String[] {this.getText("project").toLowerCase()}));
        } catch (Exception e) {
          LOG.error("Unable to delete project", e);
          this.addActionError(this.getText("deleting.problem", new String[] {this.getText("project").toLowerCase()}));
          /**
           * Assume we don't need to re-throw the exception as this transaction is limited
           * to deleting only.
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

  public List<Project> getCenterProjects() {
    return centerProjects;
  }

  public List<Project> getClosedProjects() {
    return closedProjects;
  }

  public String getFilterBy() {
    return filterBy;
  }

  public List<Project> getMyProjects() {
    return myProjects;
  }

  public long getProjectID() {
    return projectID;
  }

  public void leadCenterProjects() {

    centerProjects = new ArrayList<>();

    List<GlobalUnitProject> globalUnitProjects = new ArrayList<>(loggedCrp.getGlobalUnitProjects().stream()
      .filter(gp -> gp.isActive() && !gp.isOrigin()).collect(Collectors.toList()));

    for (GlobalUnitProject globalUnitProject : globalUnitProjects) {

      Project project = projectManager.getProjectById(globalUnitProject.getProject().getId());

      GlobalUnitProject globalUnitProjectOrigin = globalUnitProjectManager.findByProjectId(project.getId());

      if (globalUnitProjectOrigin != null) {
        Phase phase = phaseManager.findCycle(this.getCurrentCycle(), this.getCurrentCycleYear(),
          this.getActualPhase().getUpkeep(), globalUnitProjectOrigin.getGlobalUnit().getId());

        project.getProjecInfoPhase(phase);
        if (project.getProjectInfo() != null) {
          if (project.getProjectInfo().getProjectEditLeader()) {
            ProjectInfo info = project.getProjectInfo();
            if ((info.getStatus() == Long.parseLong(ProjectStatusEnum.Ongoing.getStatusId())
              || info.getStatus() == Long.parseLong(ProjectStatusEnum.Extended.getStatusId()))) {
              if (this.isSubmit(project.getId())) {
                project.setCurrentPhase(phase);
                centerProjects.add(project);
              }
            }
          }
        }
      }
    }
  }

  /**
   * load the flagships and regions for each project on list
   * 
   * @param list the list of project
   */
  public void loadFlagshipgsAndRegions(List<Project> list) {
    for (Project project : list) {

      try {

        List<CrpProgram> programs = projectManager.getPrograms(project.getId(),
          ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue(), this.getActualPhase().getId());
        List<CrpProgram> regions = projectManager.getPrograms(project.getId(),
          ProgramType.REGIONAL_PROGRAM_TYPE.getValue(), this.getActualPhase().getId());

        project.setFlagships(programs);
        project.setRegions(regions);

      } catch (Exception e) {
        project.setFlagships(new ArrayList<>());
        project.setRegions(new ArrayList<>());
      }
      if (this.getActualPhase() != null && this.getActualPhase().getId() != null && this.getActualPhase().getYear() != 0
        && project.getId() != null) {
        project.setCoreBudget(projectBudgetManager.getTotalBudget(project.getId(), this.getActualPhase().getId(), 1,
          this.getActualPhase().getYear()));
        project.setBilateralBudget(projectBudgetManager.getTotalBudget(project.getId(), this.getActualPhase().getId(),
          3, this.getActualPhase().getYear()));
        project.setW3Budget(projectBudgetManager.getTotalBudget(project.getId(), this.getActualPhase().getId(), 2,
          this.getActualPhase().getYear()));
      }
    }
  }

  public void loadFlagshipgsAndRegionsCurrentPhase(List<Project> list) {
    for (Project project : list) {

      List<CrpProgram> programs = projectManager.getPrograms(project.getId(),
        ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue(), project.getProjectInfo().getPhase().getId());
      List<CrpProgram> regions = projectManager.getPrograms(project.getId(),
        ProgramType.REGIONAL_PROGRAM_TYPE.getValue(), project.getProjectInfo().getPhase().getId());

      project.setFlagships(programs);
      project.setRegions(regions);

    }
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    Phase phase = this.getActualPhase();
    if (phase != null && phase.getId() != null && phaseManager.getPhaseById(phase.getId()) != null) {
      phase = phaseManager.getPhaseById(phase.getId());
    }

    // this.updateProjectFocuses();

    if (projectManager.findAll() != null && phase != null && phase.getProjectPhases() != null) {
      if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin()) {
        myProjects = new ArrayList<>();
        for (ProjectPhase projectPhase : phase.getProjectPhases()) {
          if (projectPhase.getProject().getProjecInfoPhase(this.getActualPhase()) != null) {
            myProjects.add(projectPhase.getProject());
          }
        }
        allProjects = new ArrayList<>();
      } else {
        allProjects = new ArrayList<>();
        for (ProjectPhase projectPhase : phase.getProjectPhases()) {
          if (projectPhase.getProject().getProjecInfoPhase(this.getActualPhase()) != null) {
            allProjects.add(projectManager.getProjectById(projectPhase.getProject().getId()));
          }
        }

        myProjects = projectManager.getUserProjects(this.getCurrentUser().getId(), loggedCrp.getAcronym()).stream()
          .filter(p -> p.isActive()).collect(Collectors.toList());

        List<Project> mProjects = new ArrayList<>();
        mProjects.addAll(myProjects);
        for (Project project : mProjects) {
          if (!allProjects.contains(project)) {
            myProjects.remove(project);
          }
          if (project.getProjecInfoPhase(this.getActualPhase()) == null) {
            myProjects.remove(project);
          }
        }
        allProjects.removeAll(myProjects);
      }

      for (Project project : allProjects) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));

      }

      for (Project project : myProjects) {
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));

      }

      this.loadFlagshipgsAndRegions(myProjects);
      this.loadFlagshipgsAndRegions(allProjects);
    }
    closedProjects = new ArrayList<>();
    List<Project> completedProjects = null;
    if (this.getCrpID() != null && this.getActualPhase() != null && this.getActualPhase().getId() != null) {
      if (projectManager.getCompletedProjects(this.getCrpID(), this.getActualPhase().getId()) != null) {
        completedProjects = projectManager.getCompletedProjects(this.getCrpID(), this.getActualPhase().getId());
      }
    }

    // Skip closed projects for Reporting
    if (this.isPlanningActive()) {
      if (completedProjects != null) {
        for (Project project : completedProjects) {
          closedProjects.add(project);
        }
      }

      if (closedProjects != null) {
        myProjects.removeAll(closedProjects);
        if (allProjects != null) {
          allProjects.removeAll(closedProjects);
        }
        Set<Project> uniqueProjects = new HashSet<>();

        uniqueProjects.addAll(closedProjects);
        closedProjects.clear();
        closedProjects.addAll(uniqueProjects);
        this.loadFlagshipgsAndRegionsCurrentPhase(closedProjects);
      }
    }

    if (this.isCenterGlobalUnit()) {
      this.leadCenterProjects();
      this.loadFlagshipgsAndRegionsCurrentPhase(centerProjects);

      LiaisonUser liaisonUser =
        liaisonUserManager.getLiaisonUserByUserId(this.getCurrentUser().getId(), loggedCrp.getId());

      if (liaisonUser != null) {
        for (Project project : centerProjects) {
          ProjectInfo info = project.getProjecInfoPhase(this.getActualPhase());
          if (info.getLiaisonInstitutionCenter() != null) {
            if (liaisonUser.getLiaisonInstitution().getId().equals(info.getLiaisonInstitutionCenter().getId())) {
              myProjects.add(project);
            } else {
              allProjects.add(project);
            }
          } else {
            allProjects.add(project);
          }

        }
      } else {
        allProjects.addAll(centerProjects);
      }

      // myProjects.addAll(centerProjects);
    }

    // AR 2018 Filter by End Date
    if (this.isReportingActive()) {

      SimpleDateFormat dateFormat = new SimpleDateFormat("y");

      myProjects =
        myProjects.stream()
          .filter(
            mp -> mp.isActive() && mp.getProjecInfoPhase(this.getActualPhase()) != null
              && (mp.getProjecInfoPhase(this.getActualPhase()).getEndDate() == null || Integer.parseInt(dateFormat
                .format(mp.getProjecInfoPhase(this.getActualPhase()).getEndDate())) >= this.getCurrentCycleYear()))
          .collect(Collectors.toList());

      allProjects =
        allProjects.stream()
          .filter(
            mp -> mp.isActive() && mp.getProjecInfoPhase(this.getActualPhase()) != null
              && (mp.getProjecInfoPhase(this.getActualPhase()).getEndDate() == null || Integer.parseInt(dateFormat
                .format(mp.getProjecInfoPhase(this.getActualPhase()).getEndDate())) >= this.getCurrentCycleYear()))
          .collect(Collectors.toList());

      closedProjects =
        closedProjects.stream()
          .filter(
            mp -> mp.isActive() && mp.getProjecInfoPhase(this.getActualPhase()) != null
              && (mp.getProjecInfoPhase(this.getActualPhase()).getEndDate() == null || Integer.parseInt(dateFormat
                .format(mp.getProjecInfoPhase(this.getActualPhase()).getEndDate())) >= this.getCurrentCycleYear()))
          .collect(Collectors.toList());
    }

    // closedProjects.sort((p1, p2) -> p1.getStatus().compareTo(p2.getStatus()));
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

  public void setCenterProjects(List<Project> centerProjects) {
    this.centerProjects = centerProjects;
  }

  public void setClosedProjects(List<Project> closedProjects) {
    this.closedProjects = closedProjects;
  }

  public void setFilterBy(String filterBy) {
    this.filterBy = filterBy;
  }

  public void setMyProjects(List<Project> myProjects) {
    this.myProjects = myProjects;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  private void updateProjectFocuses() {
    Comparator<Phase> phaseComparator = PhaseComparator.getInstance();

    Map<Project, SortedSet<Phase>> ar2021AndBeyond = this.projectFocusManager.findAll().stream()
      .filter(pf -> pf != null && pf.getId() != null && pf.getPhase() != null && pf.getPhase().getId() != null
        && pf.getPhase().getCrp() != null && pf.getPhase().getCrp().getId() != null && pf.getProject() != null
        && pf.getProject().getId() != null)
      .collect(Collectors.groupingBy(ProjectFocus::getProject, Collectors.mapping(ProjectFocus::getPhase,
        Collectors.toCollection(() -> new TreeSet<Phase>(phaseComparator)))));

    Map<GlobalUnit, Set<Project>> projectsPerCrp = this.projectFocusManager.findAll().stream()
      .filter(pf -> pf != null && pf.getId() != null && pf.getPhase() != null && pf.getPhase().getId() != null
        && pf.getPhase().getCrp() != null && pf.getPhase().getCrp().getId() != null && pf.getProject() != null
        && pf.getProject().getId() != null)
      .collect(Collectors.groupingBy(pf -> ar2021AndBeyond.get(pf.getProject()).first().getCrp(),
        Collectors.mapping(ProjectFocus::getProject,
          Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingLong(Project::getId))))));

    List<String> inserts = new ArrayList<>();
    for (Entry<GlobalUnit, Set<Project>> entry : projectsPerCrp.entrySet()) {
      GlobalUnit crp = entry.getKey();
      Long crpId = crp.getId();
      for (Project project : entry.getValue()) {
        Long projectInnovationId = project.getId();
        Set<Phase> allPhasesWithRows = ar2021AndBeyond.get(project);
        Map<Phase, Set<CrpProgram>> projectLinkedCrpsPerPhase = project.getProjectFocuses().stream()
          .filter(pic -> pic != null && pic.getId() != null && pic.getCrpProgram() != null
            && pic.getCrpProgram().getId() != null && pic.getPhase() != null && pic.getPhase().getId() != null
            && pic.getPhase().getCrp() != null && pic.getPhase().getCrp().getId() != null)
          .collect(Collectors.groupingBy(pic -> pic.getPhase(), () -> new TreeMap<>(phaseComparator),
            Collectors.mapping(ProjectFocus::getCrpProgram, Collectors.toSet())));
        for (Phase phase : allPhasesWithRows) {
          Long phaseId = phase.getId();
          if (!projectLinkedCrpsPerPhase.getOrDefault(phase, Collections.emptySet()).contains(crp)) {
            StringBuilder insert =
              new StringBuilder("INSERT INTO project_focuses(project_id, program_id, id_phase) VALUES (");
            insert =
              insert.append(projectInnovationId).append(",").append(crpId).append(",").append(phaseId).append(");");
            inserts.add(insert.toString());
          }
        }
      }
    }

    LOG.info("test");

    /*
     * Path fileSuccess = Paths.get("D:\\misc\\insert-icrps.txt");
     * try {
     * Files.write(fileSuccess, inserts, StandardCharsets.UTF_8);
     * } catch (IOException e) {
     * LOG.error("rip");
     * e.printStackTrace();
     * }
     */
  }

  @Override
  public void validate() {
    if (save) {

    }
  }

}