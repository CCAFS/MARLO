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
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.StudyTypeManager;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.StudiesStatusPlanningEnum;
import org.cgiar.ccafs.marlo.data.model.StudyType;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectExpectedStudiesListAction extends BaseAction {


  private static final long serialVersionUID = 5533305942651533875L;


  // Managers
  private SectionStatusManager sectionStatusManager;
  private ProjectManager projectManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;
  private StudyTypeManager studyTypeManager;


  // Parameters or Variables
  private List<ProjectExpectedStudy> nonProjectStudies;
  private List<ProjectExpectedStudy> myNonProjectStudies;
  private List<ProjectExpectedStudy> projectStudies;
  private List<ProjectExpectedStudy> projectOldStudies;
  private Project project;
  private List<Integer> allYears;
  private long projectID;
  private long expectedID;
  private GlobalUnit loggedCrp;

  @Inject
  public ProjectExpectedStudiesListAction(APConfig config, SectionStatusManager sectionStatusManager,
    ProjectManager projectManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager, StudyTypeManager studyTypeManager) {
    super(config);
    this.sectionStatusManager = sectionStatusManager;
    this.projectManager = projectManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectExpectedStudyInfoManager = projectExpectedStudyInfoManager;
    this.studyTypeManager = studyTypeManager;

  }

  @Override
  public String add() {
    ProjectExpectedStudy projectExpectedStudy = new ProjectExpectedStudy();

    if (project != null) {
      projectExpectedStudy.setProject(project);
    }

    projectExpectedStudy.setModificationJustification("");
    projectExpectedStudy.setYear(this.getActualPhase().getYear());

    projectExpectedStudy = projectExpectedStudyManager.saveProjectExpectedStudy(projectExpectedStudy);

    /*
     * ProjectExpectedStudyInfo projectExpectedStudyInfo = new ProjectExpectedStudyInfo(this.getActualPhase(),
     * projectExpectedStudy, "", "", "", "", "", "", "", "", "", "", "", "", "", this.getActualPhase().getYear());
     */

    ProjectExpectedStudyInfo projectExpectedStudyInfo = new ProjectExpectedStudyInfo();
    projectExpectedStudyInfo.setPhase(this.getActualPhase());
    projectExpectedStudyInfo.setProjectExpectedStudy(projectExpectedStudy);
    projectExpectedStudyInfo.setYear(this.getActualPhase().getYear());

    // when a project expected study is created, it is assigned by default status 2 = On going
    GeneralStatus status = new GeneralStatus();
    status.setId(new Long(2));
    projectExpectedStudyInfo.setStatus(status);

    long studyTypeID = -1;
    try {
      studyTypeID =
        Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.STUDY_TYPE_REQUEST_ID)));
      StudyType studyType = studyTypeManager.getStudyTypeById(studyTypeID);
      projectExpectedStudyInfo.setStudyType(studyType);
    } catch (Exception e) {
      studyTypeID = -1;
    }

    projectExpectedStudyInfoManager.saveProjectExpectedStudyInfo(projectExpectedStudyInfo);

    expectedID = projectExpectedStudy.getId();

    if (expectedID > 0) {
      return SUCCESS;
    }

    return INPUT;
  }

  @Override
  public String delete() {
    ProjectExpectedStudy projectExpectedStudyBD = projectExpectedStudyManager.getProjectExpectedStudyById(expectedID);
    if (projectExpectedStudyBD.getSectionStatuses() != null) {
      for (SectionStatus sectionStatus : projectExpectedStudyBD.getSectionStatuses()) {
        sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
      }
    }
    projectExpectedStudyManager.deleteProjectExpectedStudy(projectExpectedStudyBD.getId());
    return SUCCESS;
  }


  @Override
  public List<Integer> getAllYears() {
    return allYears;
  }


  public long getExpectedID() {
    return expectedID;
  }

  public List<ProjectExpectedStudy> getMyNonProjectStudies() {
    return myNonProjectStudies;
  }


  public List<ProjectExpectedStudy> getNonProjectStudies() {
    return nonProjectStudies;
  }

  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }

  public List<ProjectExpectedStudy> getProjectOldStudies() {
    return projectOldStudies;
  }

  public List<ProjectExpectedStudy> getProjectStudies() {
    return projectStudies;
  }


  @Override
  public void prepare() throws Exception {

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);


    try {
      projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
      project = projectManager.getProjectById(projectID);
      allYears = project.getProjecInfoPhase(this.getActualPhase()).getAllYears();
    } catch (Exception e) {
      project = null;
    }


    if (project != null) {

      List<ProjectExpectedStudy> allProjectStudies = new ArrayList<ProjectExpectedStudy>();

      // Load Studies
      List<ProjectExpectedStudy> studies = project.getProjectExpectedStudies().stream()
        .filter(c -> c.isActive() && c.getProjectExpectedStudyInfo(this.getActualPhase()) != null)
        .collect(Collectors.toList());
      if (studies != null && studies.size() > 0) {
        allProjectStudies.addAll(studies);
      }

      // Load Shared studies
      List<ExpectedStudyProject> expectedStudyProject = new ArrayList<>(project.getExpectedStudyProjects().stream()
        .filter(px -> px.isActive() && px.getPhase().getId() == this.getActualPhase().getId()
          && px.getProjectExpectedStudy().isActive()
          && px.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getActualPhase()) != null)
        .collect(Collectors.toList()));
      if (expectedStudyProject != null && expectedStudyProject.size() > 0) {
        for (ExpectedStudyProject expectedStudy : expectedStudyProject) {
          if (!allProjectStudies.contains(expectedStudy.getProjectExpectedStudy())) {
            allProjectStudies.add(expectedStudy.getProjectExpectedStudy());
          }
        }
      }

      if (allProjectStudies != null && allProjectStudies.size() > 0) {
        // Editable project studies: Current cycle year-1 will be editable except Complete and Cancelled.
        // Every study of the current cycle year will be editable
        projectStudies = new ArrayList<ProjectExpectedStudy>();
        projectStudies = allProjectStudies.stream().filter(ps -> ps.getProjectExpectedStudyInfo().getYear() != null
          && ps.getProjectExpectedStudyInfo().getStatus() != null
          && ps.getProjectExpectedStudyInfo().getYear() >= this.getActualPhase().getYear() - 1
          && ps.getProjectExpectedStudyInfo().getYear() >= 2018
          && ((ps.getProjectExpectedStudyInfo().getStatus().getId()
            .equals(Long.parseLong(StudiesStatusPlanningEnum.Ongoing.getStatusId()))
            || ps.getProjectExpectedStudyInfo().getStatus().getId()
              .equals(Long.parseLong(StudiesStatusPlanningEnum.Extended.getStatusId()))
            || ps.getProjectExpectedStudyInfo().getStatus().getId().equals(StudiesStatusPlanningEnum.New.getStatusId()))
            || ((ps.getProjectExpectedStudyInfo().getStatus().getId()
              .equals(Long.parseLong(StudiesStatusPlanningEnum.Complete.getStatusId()))
              || ps.getProjectExpectedStudyInfo().getStatus().getId()
                .equals(Long.parseLong(StudiesStatusPlanningEnum.Cancelled.getStatusId())))
              && ps.getProjectExpectedStudyInfo().getYear() >= this.getActualPhase().getYear())))
          .collect(Collectors.toList());

        // Non Editable project studies
        projectOldStudies = new ArrayList<ProjectExpectedStudy>();
        if (projectStudies == null || projectStudies.isEmpty()) {
          projectOldStudies.addAll(allProjectStudies);
        } else {
          for (ProjectExpectedStudy projectExpectedStudy : allProjectStudies) {
            List<ProjectExpectedStudy> studiesFiltered = projectStudies.stream()
              .filter(ps -> ps.getId().equals(projectExpectedStudy.getId())).collect(Collectors.toList());
            if (studiesFiltered == null || studiesFiltered.isEmpty()) {
              projectOldStudies.add(projectExpectedStudy);
            }
          }
        }
      }
    } else {
      nonProjectStudies = new ArrayList<>();
      List<ProjectExpectedStudy> expectedStudies = new ArrayList<>(projectExpectedStudyManager.findAll().stream()
        .filter(s -> s.isActive() && s.getProject() == null).collect(Collectors.toList()));
      for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
        if (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()) != null) {
          nonProjectStudies.add(projectExpectedStudy);
        }
      }
      myNonProjectStudies = new ArrayList<>();

      if (!nonProjectStudies.isEmpty()) {
        if (this.canAccessSuperAdmin() || this
          .hasPermission(this.generatePermission(Permission.STUDIES_FULL_EDIT_PERMISSION, loggedCrp.getAcronym()))) {

          for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
            if (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()) != null) {
              nonProjectStudies.remove(projectExpectedStudy);
              myNonProjectStudies.add(projectExpectedStudy);
            }
          }

        } else {

          for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
            if (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()) != null) {
              if (projectExpectedStudy.getCreatedBy().getId().equals(this.getCurrentUser().getId())) {
                nonProjectStudies.remove(projectExpectedStudy);
                myNonProjectStudies.add(projectExpectedStudy);
              }
            }
          }

          expectedStudies = new ArrayList<>(
            projectExpectedStudyManager.getUserStudies(this.getCurrentUser().getId(), loggedCrp.getAcronym()).stream()
              .filter(e -> e.isActive()).collect(Collectors.toList()));
          for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
            if (nonProjectStudies.contains(projectExpectedStudy)) {
              nonProjectStudies.remove(projectExpectedStudy);
              myNonProjectStudies.add(projectExpectedStudy);
            }
          }
        }
      }
    }
  }


  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
  }

  public void setExpectedID(long expectedID) {
    this.expectedID = expectedID;
  }

  public void setMyNonProjectStudies(List<ProjectExpectedStudy> myNonProjectStudies) {
    this.myNonProjectStudies = myNonProjectStudies;
  }

  public void setNonProjectStudies(List<ProjectExpectedStudy> nonProjectStudies) {
    this.nonProjectStudies = nonProjectStudies;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectOldStudies(List<ProjectExpectedStudy> projectOldStudies) {
    this.projectOldStudies = projectOldStudies;
  }

  public void setProjectStudies(List<ProjectExpectedStudy> projectStudies) {
    this.projectStudies = projectStudies;
  }


}
