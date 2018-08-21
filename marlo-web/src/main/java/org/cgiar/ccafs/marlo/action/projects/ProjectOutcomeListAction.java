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
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
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

  // Front-end
  private long projectID;
  private long projectOutcomeID;
  private GlobalUnit loggedCrp;
  private Project project;
  private long outcomeId;
  private List<CrpProgramOutcome> outcomes;
  private long sharedPhaseID;

  @Inject
  public ProjectOutcomeListAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, SectionStatusManager sectionStatusManager,
    ProjectOutcomeManager projectOutcomeManager, GlobalUnitProjectManager globalUnitProjectManager) {
    super(config);
    this.projectManager = projectManager;
    this.sectionStatusManager = sectionStatusManager;
    this.crpManager = crpManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.projectOutcomeManager = projectOutcomeManager;
    this.globalUnitProjectManager = globalUnitProjectManager;

  }


  public String addProjectOutcome() {
    if (this.hasPermission("add")) {
      ProjectOutcome projectOutcome = new ProjectOutcome();
      projectOutcome.setPhase(this.getActualPhase());
      projectOutcome.setProject(project);
      projectOutcome.setCrpProgramOutcome(crpProgramOutcomeManager.getCrpProgramOutcomeById(outcomeId));
      projectOutcomeManager.saveProjectOutcome(projectOutcome);
      projectOutcomeID = projectOutcome.getId().longValue();

      return SUCCESS;
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


  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }


  public long getProjectOutcomeID() {
    return projectOutcomeID;
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

    Phase phase = this.getActualPhase();
    sharedPhaseID = phase.getId();


    project = projectManager.getProjectById(projectID);
    project.setProjectInfo(project.getProjecInfoPhase(phase));
    List<ProjectOutcome> projectOutcomes = project.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());


    GlobalUnitProject gp = globalUnitProjectManager.findByProjectId(project.getId());

    project.setOutcomes(projectOutcomes);

    System.out.println(project.getOutcomes().size());

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


    System.out.println(project.getOutcomes().size());
    String params[] = {gp.getGlobalUnit().getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_CONTRIBRUTIONCRP_BASE_PERMISSION, params));

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

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setProjectOutcomeID(long projectOutcomeID) {
    this.projectOutcomeID = projectOutcomeID;
  }
}
