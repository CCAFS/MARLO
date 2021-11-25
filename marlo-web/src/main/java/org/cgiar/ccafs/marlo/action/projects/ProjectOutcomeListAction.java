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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLp6ContributionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.collections4.CollectionUtils;
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

  // Front-end
  private long projectID;
  private long projectOutcomeID;
  private GlobalUnit loggedCrp;
  private Project project;
  private long outcomeId;
  private List<CrpProgramOutcome> outcomes;
  private ProjectLp6Contribution projectLp6Contribution;
  private Map<String, Object> status;
  private boolean contributionValue;
  private long phaseID;
  private Phase phase;


  @Inject
  public ProjectOutcomeListAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, SectionStatusManager sectionStatusManager,
    ProjectOutcomeManager projectOutcomeManager, GlobalUnitProjectManager globalUnitProjectManager,
    ProjectLp6ContributionManager projectLp6ContributionManager) {
    super(config);
    this.projectManager = projectManager;
    this.sectionStatusManager = sectionStatusManager;
    this.crpManager = crpManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.projectOutcomeManager = projectOutcomeManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.projectLp6ContributionManager = projectLp6ContributionManager;
  }

  public String addProjectOutcome() {
    if (this.hasPermission("add")) {
      ProjectOutcome projectOutcome = new ProjectOutcome();
      projectOutcome.setPhase(this.getActualPhase());
      projectOutcome.setProject(project);
      projectOutcome.setCrpProgramOutcome(crpProgramOutcomeManager.getCrpProgramOutcomeById(outcomeId));
      projectOutcomeManager.saveProjectOutcome(projectOutcome);
      projectOutcomeID = projectOutcome.getId().longValue();

      if (projectOutcomeID > 0) {
        SectionStatus sectionStatus =
          this.sectionStatusManager.getSectionStatusByIndicator(this.getCurrentCycle(), this.getCurrentCycleYear(),
            this.isUpKeepActive(), ProjectSectionStatusEnum.OUTCOMES.getStatus(), this.project.getId());
        if (sectionStatus != null) {
          sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
        }

        return SUCCESS;
      }

      return INPUT;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public String deleteProjectOutcome() {
    if (this.hasPermission("delete")) {
      ProjectOutcome outcome = projectOutcomeManager.getProjectOutcomeById(outcomeId);
      for (SectionStatus sectionStatus : outcome.getSectionStatuses()) {
        sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
      }
      projectOutcomeManager.deleteProjectOutcome(outcome.getId());

      SectionStatus sectionStatus =
        this.sectionStatusManager.getSectionStatusByIndicator(this.getCurrentCycle(), this.getCurrentCycleYear(),
          this.isUpKeepActive(), ProjectSectionStatusEnum.OUTCOMES.getStatus(), this.project.getId());

      List<ProjectOutcome> currentOutcomes =
        CollectionUtils
          .emptyIfNull(this.project.getOutcomes()).stream().filter(o -> o != null && o.getId() != null && o.isActive()
            && o.getCrpProgramOutcome() != null && o.getCrpProgramOutcome().getId() != null)
          .collect(Collectors.toList());

      if (this.isEmpty(currentOutcomes)) {
        if (sectionStatus == null) {
          sectionStatus = new SectionStatus();
          sectionStatus.setCycle(this.getCurrentCycle());
          sectionStatus.setYear(this.getCurrentCycleYear());
          sectionStatus.setUpkeep(this.isUpKeepActive());
          sectionStatus.setSectionName(ProjectSectionStatusEnum.OUTCOMES.getStatus());
          sectionStatus.setProject(this.project);
        }

        sectionStatus.setMissingFields(APConstants.STATUS_EMPTY_OUTCOME_LIST);
        sectionStatus = this.sectionStatusManager.saveSectionStatus(sectionStatus);
      }

      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
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
