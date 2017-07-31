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
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
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
  private CrpManager crpManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private ProjectOutcomeManager projectOutcomeManager;
  private SectionStatusManager sectionStatusManager;

  // Front-end
  private long projectID;
  private long projectOutcomeID;
  private Crp loggedCrp;
  private Project project;
  private long outcomeId;
  private List<CrpProgramOutcome> outcomes;

  @Inject
  public ProjectOutcomeListAction(APConfig config, ProjectManager projectManager, CrpManager crpManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, SectionStatusManager sectionStatusManager,
    ProjectOutcomeManager projectOutcomeManager) {
    super(config);
    this.projectManager = projectManager;
    this.sectionStatusManager = sectionStatusManager;
    this.crpManager = crpManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.projectOutcomeManager = projectOutcomeManager;

  }


  public String addProjectOutcome() {
    if (this.hasPermission("add")) {
      ProjectOutcome projectOutcome = new ProjectOutcome();
      projectOutcome.setActive(true);
      projectOutcome.setCreatedBy(this.getCurrentUser());
      projectOutcome.setModificationJustification("");
      projectOutcome.setActiveSince(new Date());
      projectOutcome.setPhase(this.getActualPhase());
      projectOutcome.setModifiedBy(this.getCurrentUser());
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

      projectOutcomeManager.deleteProjectOutcome(outcomeId);
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
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    } catch (Exception e) {

    }
    project = projectManager.getProjectById(projectID);
    project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
    List<ProjectOutcome> projectOutcomes = project.getProjectOutcomes().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());


    project.setOutcomes(projectOutcomes);
    outcomes = new ArrayList<CrpProgramOutcome>();
    for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList())) {

      outcomes.addAll(projectFocuses.getCrpProgram().getCrpProgramOutcomes().stream()
        .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
    }

    List<CrpProgram> programs = new ArrayList<>();
    for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
        && c.getPhase().equals(this.getActualPhase()))
      .collect(Collectors.toList())) {
      programs.add(projectFocuses.getCrpProgram());
    }
    project.setFlagships(programs);

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_CONTRIBRUTIONCRP_BASE_PERMISSION, params));

  }


  @Override
  public String save() {
    // TODO Auto-generated method stub
    return super.save();
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
