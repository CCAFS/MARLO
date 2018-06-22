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
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.StudyType;
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

  private List<ProjectExpectedStudy> nonProjectStudies;


  private List<ProjectExpectedStudy> myNonProjectStudies;


  private List<ProjectExpectedStudy> projectStudies;


  // Parameters or Variables
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

    projectExpectedStudy.setYear(this.getActualPhase().getYear());

    if (project != null) {
      projectExpectedStudy.setProject(project);
    }

    projectExpectedStudy.setModificationJustification("");
    projectExpectedStudy.setYear(this.getActualPhase().getYear());

    projectExpectedStudy = projectExpectedStudyManager.saveProjectExpectedStudy(projectExpectedStudy);

    ProjectExpectedStudyInfo projectExpectedStudyInfo = new ProjectExpectedStudyInfo(this.getActualPhase(),
      projectExpectedStudy, "", "", "", "", "", "", "", "", "", "", "", "", "");

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
    for (SectionStatus sectionStatus : projectExpectedStudyBD.getSectionStatuses()) {
      sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
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
      List<ProjectExpectedStudy> studies =
        project.getProjectExpectedStudies().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      projectStudies = new ArrayList<ProjectExpectedStudy>();
      for (ProjectExpectedStudy projectExpectedStudy : studies) {
        if (projectExpectedStudy.getProjectExpectedStudyInfo(this.getActualPhase()) != null) {
          projectStudies.add(projectExpectedStudy);
        }
      }


      List<ExpectedStudyProject> expectedStudyProject = new ArrayList<>(project
        .getExpectedStudyProjects().stream().filter(px -> px.isActive()
          && px.getPhase().getId() == this.getActualPhase().getId() && px.getProjectExpectedStudy().isActive())
        .collect(Collectors.toList()));
      for (ExpectedStudyProject expectedStudy : expectedStudyProject) {
        if (!projectStudies.contains(expectedStudy.getProjectExpectedStudy())) {
          if (expectedStudy.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getActualPhase()) != null) {
            projectStudies.add(expectedStudy.getProjectExpectedStudy());
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
        if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin()) {

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

  public void setProjectStudies(List<ProjectExpectedStudy> projectStudies) {
    this.projectStudies = projectStudies;
  }


}
