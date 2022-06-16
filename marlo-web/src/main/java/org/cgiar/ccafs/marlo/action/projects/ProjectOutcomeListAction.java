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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentManager;
import org.cgiar.ccafs.marlo.data.manager.FeedbackQACommentableFieldsManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.FeedbackQAComment;
import org.cgiar.ccafs.marlo.data.model.FeedbackQACommentableFields;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 * @author Christian Garcia- CIAT/CCAFS
 */
public class ProjectOutcomeListAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 4520862722467820286L;

  private ProjectManager projectManager;
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private ProjectOutcomeManager projectOutcomeManager;
  private SectionStatusManager sectionStatusManager;
  private GlobalUnitProjectManager globalUnitProjectManager;
  private ProjectLp6ContributionManager projectLp6ContributionManager;
  private ProjectMilestoneManager projectMilestoneManager;
  private FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager;
  private FeedbackQACommentManager commentManager;

  // Front-end
  private long projectID;
  private long projectOutcomeID;
  private GlobalUnit loggedCrp;
  private Project project;
  private long outcomeId;
  private List<CrpProgramOutcome> outcomes;
  private List<CrpMilestone> milestones;
  private ProjectLp6Contribution projectLp6Contribution;
  private Map<String, Object> status;
  private boolean contributionValue;
  private long phaseID;
  private Phase phase;


  @Inject
  public ProjectOutcomeListAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, SectionStatusManager sectionStatusManager,
    ProjectOutcomeManager projectOutcomeManager, GlobalUnitProjectManager globalUnitProjectManager,
    ProjectLp6ContributionManager projectLp6ContributionManager, ProjectMilestoneManager projectMilestoneManager,
    FeedbackQACommentableFieldsManager feedbackQACommentableFieldsManager, FeedbackQACommentManager commentManager) {
    super(config);
    this.projectManager = projectManager;
    this.sectionStatusManager = sectionStatusManager;
    this.crpManager = crpManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.projectOutcomeManager = projectOutcomeManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.projectLp6ContributionManager = projectLp6ContributionManager;
    this.projectMilestoneManager = projectMilestoneManager;
    this.feedbackQACommentableFieldsManager = feedbackQACommentableFieldsManager;
    this.commentManager = commentManager;
  }

  public void addAllCrpMilestones(ProjectOutcome projectOutcome) {
    if (projectOutcome != null && projectOutcome.getCrpProgramOutcome() != null
      && projectOutcome.getCrpProgramOutcome().getCrpMilestones() != null
      && !projectOutcome.getCrpProgramOutcome().getCrpMilestones().isEmpty()) {
      // Fill Milestones list
      milestones = projectOutcome.getCrpProgramOutcome().getCrpMilestones().stream().filter(c -> c.isActive())
        .collect(Collectors.toList());
    }

    if (projectOutcome != null && milestones != null) {
      milestones.sort(Comparator.comparing(CrpMilestone::getYear));
      List<ProjectMilestone> projectMilestones = new ArrayList<>();
      for (CrpMilestone crpMilestone : milestones) {
        ProjectMilestone projectMilestone = new ProjectMilestone();
        projectMilestone.setCrpMilestone(crpMilestone);
        projectMilestone.setProjectOutcome(projectOutcome);

        if (crpMilestone.getExtendedYear() != null) {
          projectMilestone.setYear(crpMilestone.getExtendedYear());
        } else if (crpMilestone.getYear() != null) {
          projectMilestone.setYear(crpMilestone.getYear());
        }


        if (projectOutcome.getMilestones() != null && !projectOutcome.getMilestones().isEmpty()) {

          boolean exist = false;
          for (ProjectMilestone prevProjectMilestone : projectOutcome.getMilestones()) {
            if (prevProjectMilestone.getCrpMilestone() != null && prevProjectMilestone.getCrpMilestone() != null
              && crpMilestone != null && crpMilestone.getId() != null
              && prevProjectMilestone.getCrpMilestone().getId().equals(crpMilestone.getId())
              && prevProjectMilestone.getProjectOutcome() != null
              && prevProjectMilestone.getProjectOutcome().getId() != null
              && prevProjectMilestone.getProjectOutcome().getId().equals(projectOutcome.getId())) {
              exist = true;
            }
          }

          if (exist == false) {
            // If not exist previously this project Milestone then it is added to the list
            projectMilestone = projectMilestoneManager.saveProjectMilestone(projectMilestone);
            projectMilestones.add(projectMilestone);
          }

        } else {
          projectMilestone = projectMilestoneManager.saveProjectMilestone(projectMilestone);
          projectMilestones.add(projectMilestone);
        }
      }

      if (projectMilestones != null && !projectMilestones.isEmpty()) {
        projectOutcome.setMilestones(projectMilestones);
      }
    }
  }

  public String addProjectOutcome() {
    if (this.hasPermission("add")) {
      ProjectOutcome projectOutcome = new ProjectOutcome();
      projectOutcome.setPhase(this.getActualPhase());
      projectOutcome.setProject(project);
      projectOutcome.setCrpProgramOutcome(crpProgramOutcomeManager.getCrpProgramOutcomeById(outcomeId));
      projectOutcome = projectOutcomeManager.saveProjectOutcome(projectOutcome);
      projectOutcomeID = projectOutcome.getId().longValue();
      projectOutcome.setOrder(this.defineProjectOutcomeOrder(projectOutcome));
      if (this.isAiccra()) {
        this.addAllCrpMilestones(projectOutcome);
      }

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public double defineProjectOutcomeOrder(ProjectOutcome projectOutcome) {
    double orderIndex = 1;
    if (projectOutcome != null && projectOutcome.getCrpProgramOutcome() != null
      && projectOutcome.getCrpProgramOutcome().getDescription() != null) {
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("Indicator 1")) {
        orderIndex = 1;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("Indicator 2")) {
        orderIndex = 2;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("Indicator 3")) {
        orderIndex = 3;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("1.1")) {
        orderIndex = 11;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("1.2")) {
        orderIndex = 12;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("1.3")) {
        orderIndex = 13;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("1.4")) {
        orderIndex = 14;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("1.5")) {
        orderIndex = 15;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("2.1")) {
        orderIndex = 21;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("2.1")) {
        orderIndex = 21;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("2.2")) {
        orderIndex = 22;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("2.3")) {
        orderIndex = 23;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("2.4")) {
        orderIndex = 24;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("2.5")) {
        orderIndex = 25;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("3.1")) {
        orderIndex = 31;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("3.1")) {
        orderIndex = 31;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("3.2")) {
        orderIndex = 32;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("3.3")) {
        orderIndex = 33;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("3.4")) {
        orderIndex = 34;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("3.5")) {
        orderIndex = 35;
      }
    }
    return orderIndex;
  }

  public String deleteProjectOutcome() {
    if (this.hasPermission("delete")) {
      ProjectOutcome outcome = projectOutcomeManager.getProjectOutcomeById(outcomeId);
      for (SectionStatus sectionStatus : outcome.getSectionStatuses()) {
        sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
      }
      projectOutcomeManager.deleteProjectOutcome(outcome.getId());
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public void getCommentStatuses() {

    try {


      List<FeedbackQACommentableFields> commentableFields = new ArrayList<>();

      // get the commentable fields by sectionName
      if (feedbackQACommentableFieldsManager.findAll() != null) {
        commentableFields = feedbackQACommentableFieldsManager.findAll().stream()
          .filter(f -> f != null && f.getSectionName().equals("projectContributionCrp")).collect(Collectors.toList());
      }
      if (project.getOutcomes() != null && !project.getOutcomes().isEmpty() && commentableFields != null
        && !commentableFields.isEmpty()) {


        // Set the comment status in each project outcome

        for (ProjectOutcome projectOutcome : project.getOutcomes()) {
          int answeredComments = 0, totalComments = 0;
          try {


            for (FeedbackQACommentableFields commentableField : commentableFields) {
              if (commentableField != null && commentableField.getId() != null) {

                if (projectOutcome != null && projectOutcome.getId() != null && commentableField != null
                  && commentableField.getId() != null) {
                  List<FeedbackQAComment> comments = commentManager
                    .findAll().stream().filter(f -> f != null && f.getParentId() == projectOutcome.getId()
                      && f.getField() != null && f.getField().getId().equals(commentableField.getId()))
                    .collect(Collectors.toList());
                  if (comments != null && !comments.isEmpty()) {
                    totalComments += comments.size();
                    comments = comments.stream()
                      .filter(f -> f != null && ((f.getStatus() != null && f.getStatus().equals("approved"))
                        || (f.getStatus() != null && f.getReply() != null)))
                      .collect(Collectors.toList());
                    if (comments != null) {
                      answeredComments += comments.size();
                    }
                  }
                }
              }
            }
            projectOutcome.setCommentStatus(answeredComments + "/" + totalComments);
          } catch (Exception e) {
            projectOutcome.setCommentStatus(0 + "/" + 0);

          }
        }

      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Long getOutcomeId() {
    return outcomeId;
  }

  public List<CrpProgramOutcome> getOutcomes() {
    return outcomes;
  }

  @Override
  public Long getPhaseID() {
    return phaseID;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public ProjectLp6Contribution getProjectLp6Contribution() {
    return projectLp6Contribution;
  }

  public long getProjectOutcomeID() {
    return projectOutcomeID;
  }

  public Map<String, Object> getStatus() {
    return status;
  }

  public boolean isContributionValue() {
    return contributionValue;
  }

  @Override
  public void prepare() throws Exception {

    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    } catch (Exception e) {

    }

    contributionValue =
      Boolean.parseBoolean(StringUtils.trim(this.getRequest().getParameter(APConstants.CRP_LP6_CONTRIBUTION_VALUE)));

    phase = this.getActualPhase();

    project = projectManager.getProjectById(projectID);
    project.setProjectInfo(project.getProjecInfoPhase(phase));
    List<ProjectOutcome> projectOutcomes = project.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());

    GlobalUnitProject gp = globalUnitProjectManager.findByProjectId(project.getId());

    project.setOutcomes(projectOutcomes);

    outcomes = new ArrayList<CrpProgramOutcome>();
    for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(phase)
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
        && c.getCrpProgram().getCrp().getId().equals(gp.getGlobalUnit().getId()))
      .collect(Collectors.toList())) {

      List<CrpProgramOutcome> outcomesT = new ArrayList<>(projectFocuses.getCrpProgram().getCrpProgramOutcomes()
        .stream().filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList()));

      outcomes.addAll(outcomesT);
    }

    List<CrpProgram> programs = new ArrayList<>();
    for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
        && c.getCrpProgram().getCrp().getId().equals(gp.getGlobalUnit().getId()) && c.getPhase().equals(phase))
      .collect(Collectors.toList())) {
      programs.add(projectFocuses.getCrpProgram());
    }
    project.setFlagships(programs);

    if (this.hasSpecificities(this.feedbackModule())) {
      this.getCommentStatuses();
    }

    String params[] = {gp.getGlobalUnit().getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_CONTRIBRUTIONCRP_BASE_PERMISSION, params));

    if (this.getActualPhase() != null && projectID != 0) {

      if (projectLp6ContributionManager.findAll() != null) {
        List<ProjectLp6Contribution> projectLp6Contributions = projectLp6ContributionManager.findAll().stream()
          .filter(
            c -> c.isActive() && c.getProject().getId().equals(projectID) && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList());
        if (projectLp6Contributions != null && !projectLp6Contributions.isEmpty()) {
          this.setProjectLp6Contribution(projectLp6Contributions.get(0));
        } else {
          if (contributionValue) {
            ProjectLp6Contribution newContribution = new ProjectLp6Contribution();
            newContribution.setProject(project);
            newContribution.setPhase(this.getActualPhase());
            newContribution.setContribution(contributionValue);
            projectLp6ContributionManager.saveProjectLp6Contribution(newContribution);
          }
        }
      }
    }
  }


  public void setContributionValue(boolean contributionValue) {
    this.contributionValue = contributionValue;
  }

  public void setOutcomeId(long outcomeId) {
    this.outcomeId = outcomeId;
  }


  public void setOutcomeId(Long outcomeId) {
    this.outcomeId = outcomeId;
  }


  public void setOutcomes(List<CrpProgramOutcome> outcomes) {
    this.outcomes = outcomes;
  }


  @Override
  public void setPhaseID(Long phaseID) {
    this.phaseID = phaseID;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectLp6Contribution(ProjectLp6Contribution projectLp6Contribution) {
    this.projectLp6Contribution = projectLp6Contribution;
  }


  public void setProjectOutcomeID(long projectOutcomeID) {
    this.projectOutcomeID = projectOutcomeID;
  }

  public void setStatus(Map<String, Object> status) {
    this.status = status;
  }
}
