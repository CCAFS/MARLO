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
import org.cgiar.ccafs.marlo.data.manager.ExpectedStudyProjectManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.StudyTypeManager;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
import org.cgiar.ccafs.marlo.data.model.FeedbackStatusEnum;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.StudyType;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
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
  private ExpectedStudyProjectManager expectedStudyProjectManager;
  private FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager;
  private FeedbackQACommentManager commentManager;

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
  private String justification;


  @Inject
  public ProjectExpectedStudiesListAction(APConfig config, SectionStatusManager sectionStatusManager,
    ProjectManager projectManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager, StudyTypeManager studyTypeManager,
    ExpectedStudyProjectManager expectedStudyProjectManager,
    FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager, FeedbackQACommentManager commentManager) {
    super(config);
    this.sectionStatusManager = sectionStatusManager;
    this.projectManager = projectManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectExpectedStudyInfoManager = projectExpectedStudyInfoManager;
    this.studyTypeManager = studyTypeManager;
    this.expectedStudyProjectManager = expectedStudyProjectManager;
    this.feedbackQACommentableFieldsManager = feedbackQACommentableFieldsManager;
    this.commentManager = commentManager;
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
    projectExpectedStudyInfo.setIsContribution(true);

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

  /*
   * @Override
   * public String delete() {
   * ProjectExpectedStudy projectExpectedStudyBD = projectExpectedStudyManager.getProjectExpectedStudyById(expectedID);
   * if (projectExpectedStudyBD.getSectionStatuses() != null) {
   * for (SectionStatus sectionStatus : projectExpectedStudyBD.getSectionStatuses()) {
   * sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
   * }
   * }
   * projectExpectedStudyBD.setModificationJustification(justification);
   * projectExpectedStudyManager.deleteProjectExpectedStudy(projectExpectedStudyBD.getId());
   * return SUCCESS;
   * }
   */

  @Override
  public String delete() {
    // if (project.getExpectedStudies() != null) {
    if (project != null) {
      if (projectStudies != null) {
        // for (ProjectExpectedStudy projectStudy : project.getExpectedStudies()) {
        for (ProjectExpectedStudy projectStudy : projectStudies) {
          if (projectStudy.getId().longValue() == expectedID) {
            ProjectExpectedStudy projectExpectedBD =
              projectExpectedStudyManager.getProjectExpectedStudyById(expectedID);
            for (SectionStatus sectionStatus : projectExpectedBD.getSectionStatuses()) {
              sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
            }

            projectStudy.setModificationJustification(justification);
            projectExpectedStudyManager.deleteProjectExpectedStudy(projectStudy.getId());
          }
        }
      }
    } else {
      for (ProjectExpectedStudy projectStudy : myNonProjectStudies) {
        if (projectStudy.getId().longValue() == expectedID) {
          ProjectExpectedStudy projectExpectedBD = projectExpectedStudyManager.getProjectExpectedStudyById(expectedID);
          for (SectionStatus sectionStatus : projectExpectedBD.getSectionStatuses()) {
            sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
          }

          projectStudy.setModificationJustification(justification);
          projectExpectedStudyManager.deleteProjectExpectedStudy(projectStudy.getId());
        }
      }
    }

    return SUCCESS;
  }

  @Override
  public List<Integer> getAllYears() {
    return allYears;
  }


  public void getCommentStatuses() {

    try {

      List<FeedbackQACommentableFields> commentableFields = new ArrayList<>();

      // get the commentable fields by sectionName
      if (feedbackQACommentableFieldsManager.findAll() != null) {
        commentableFields = feedbackQACommentableFieldsManager.findAll().stream()
          .filter(f -> f != null && f.getSectionName().equals("study")).collect(Collectors.toList());
      }
      if (projectStudies != null && !projectStudies.isEmpty() && commentableFields != null
        && !commentableFields.isEmpty()) {


        // Set the comment status in each project outcome
        for (ProjectExpectedStudy study : projectStudies) {
          int answeredComments = 0, totalComments = 0;
          try {


            for (FeedbackQACommentableFields commentableField : commentableFields) {
              if (commentableField != null && commentableField.getId() != null) {

                if (study != null && study.getId() != null && commentableField != null
                  && commentableField.getId() != null) {
                  List<FeedbackQAComment> comments = commentManager.findAll().stream()
                    .filter(f -> f != null && f.getParentId() == study.getId()

                      && (f.getFeedbackStatus() != null && f.getFeedbackStatus().getId() != null && (!f
                        .getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Dismissed.getStatusId()))
                      // &&
                      // !f.getFeedbackStatus().getId().equals(Long.parseLong(FeedbackStatusEnum.Draft.getStatusId()))
                      ))

                      && f.getField() != null && f.getField().getId().equals(commentableField.getId()))
                    .collect(Collectors.toList());
                  if (comments != null && !comments.isEmpty()) {
                    totalComments += comments.size();
                    comments = comments.stream()
                      .filter(f -> f != null && ((f.getFeedbackStatus() != null && f.getFeedbackStatus().getId()
                        .equals(Long.parseLong(FeedbackStatusEnum.Agreed.getStatusId())))
                        || (f.getFeedbackStatus() != null && f.getReply() != null)))
                      .collect(Collectors.toList());
                    if (comments != null) {
                      answeredComments += comments.size();
                    }
                  }
                }
              }
            }

            study.setCommentStatus(answeredComments + "/" + totalComments);

            if (study.getCommentStatus() == null
              || (study.getCommentStatus() != null && study.getCommentStatus().isEmpty())) {
              study.setCommentStatus(0 + "/" + 0);
            }
          } catch (Exception e) {
            study.setCommentStatus(0 + "/" + 0);

          }
        }

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void getCommentStatusesNew() {
    try {

      List<String> commentList = null;
      commentList = feedbackQACommentableFieldsManager.getCommentStatusByPhaseToStudy(this.getActualPhase().getId());

      List<String> commentAnsweredList = null;
      commentAnsweredList =
        feedbackQACommentableFieldsManager.getAnsweredCommentByPhaseToStudy(this.getActualPhase().getId());

      if (projectStudies != null && !projectStudies.isEmpty()) {
        for (ProjectExpectedStudy study : projectStudies) {
          int answeredComments = 0;
          int totalComments = 0;
          try {

            for (String string : commentList) {
              String test = string.replace("|", ";");

              if (test.split(";")[0].equals(study.getId() + "")) {
                totalComments = Integer.parseInt(test.split(";")[1]);
              }
            }

            for (String string : commentAnsweredList) {
              String test = string.replace("|", ";");

              if (test.split(";")[0].equals(study.getId() + "")) {
                answeredComments = Integer.parseInt(test.split(";")[1]);
              }
            }

            study.setCommentStatus(answeredComments + "/" + totalComments);
            if (study.getCommentStatus() == null
              || (study.getCommentStatus() != null && study.getCommentStatus().isEmpty())) {
              study.setCommentStatus(0 + "/" + 0);
            }

          } catch (Exception e) {
            study.setCommentStatus(0 + "/" + 0);
          }
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public long getExpectedID() {
    return expectedID;
  }


  @Override
  public String getJustification() {
    return justification;
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
    this.setPhaseID(this.getActualPhase().getId());

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
      if (studies != null && !studies.isEmpty()) {
        allProjectStudies.addAll(studies);
      }

      // Load Shared studies
      List<ExpectedStudyProject> expectedStudyProject =
        this.expectedStudyProjectManager.getByProjectAndPhase(project.getId(), this.getPhaseID()) != null
          ? this.expectedStudyProjectManager.getByProjectAndPhase(project.getId(), this.getPhaseID()).stream()
            .filter(px -> px.isActive() && px.getProjectExpectedStudy().isActive()
              && px.getProjectExpectedStudy().getProjectExpectedStudyInfo(this.getActualPhase()) != null)
            .collect(Collectors.toList())
          : Collections.emptyList();
      if (expectedStudyProject != null && !expectedStudyProject.isEmpty()) {
        for (ExpectedStudyProject expectedStudy : expectedStudyProject) {
          if (!allProjectStudies.contains(expectedStudy.getProjectExpectedStudy())) {
            allProjectStudies.add(expectedStudy.getProjectExpectedStudy());
          }
        }
      }

      if (allProjectStudies != null && !allProjectStudies.isEmpty()) {
        // Editable project studies: Current cycle year-1 will be editable except Complete and Cancelled.
        // Every study of the current cycle year will be editable
        projectStudies = new ArrayList<ProjectExpectedStudy>();
        projectStudies = allProjectStudies.stream()
          .filter(ps -> ps.getProjectExpectedStudyInfo().getYear() != null
            && ps.getProjectExpectedStudyInfo().getStatus() != null
            && ps.getProjectExpectedStudyInfo().getYear() >= this.getCurrentCycleYear())
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

    ProjectInfo projectInfo = new ProjectInfo();
    if (projectStudies != null) {
      for (ProjectExpectedStudy study : projectStudies) {
        if (study.getProject() != null && study.getProject().getProjecInfoPhase(this.getActualPhase()) != null) {
          projectInfo = study.getProject().getProjecInfoPhase(this.getActualPhase());
        }
        if (projectInfo != null) {
          study.getProject().setProjectInfo(projectInfo);
        }
      }
    }
    if (projectOldStudies != null) {
      for (ProjectExpectedStudy studyOld : projectOldStudies) {
        if (studyOld.getProject() != null && studyOld.getProject().getProjecInfoPhase(this.getActualPhase()) != null) {
          projectInfo = studyOld.getProject().getProjecInfoPhase(this.getActualPhase());
        }
        if (projectInfo != null) {
          studyOld.getProject().setProjectInfo(projectInfo);
        }
      }
    }
    if (this.hasSpecificities(this.feedbackModule())) {
      // this.getCommentStatuses();
      this.getCommentStatusesNew();
    }

  }


  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
  }

  public void setExpectedID(long expectedID) {
    this.expectedID = expectedID;
  }

  @Override
  public void setJustification(String justification) {
    this.justification = justification;
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
