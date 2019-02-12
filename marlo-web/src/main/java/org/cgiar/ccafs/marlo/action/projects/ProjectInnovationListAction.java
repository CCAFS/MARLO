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
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectInnovationListAction extends BaseAction {


  private static final long serialVersionUID = 3586039079035252726L;


  // Manager
  private ProjectInnovationManager projectInnovationManager;
  private ProjectInnovationInfoManager projectInnovationInfoManager;
  private SectionStatusManager sectionStatusManager;
  private ProjectManager projectManager;
  private ProjectInnovationCrpManager projectInnovationCrpManager;

  // Variables
  // Model for the back-end
  private Project project;
  // Model for the front-end
  private long projectID;
  private long innovationID;
  private List<Integer> allYears;

  @Inject
  public ProjectInnovationListAction(APConfig config, ProjectInnovationManager projectInnovationManager,
    ProjectInnovationInfoManager projectInnovationInfoManager, SectionStatusManager sectionStatusManager,
    ProjectManager projectManager, ProjectInnovationCrpManager projectInnovationCrpManager) {
    super(config);
    this.projectInnovationManager = projectInnovationManager;
    this.projectInnovationInfoManager = projectInnovationInfoManager;
    this.sectionStatusManager = sectionStatusManager;
    this.projectManager = projectManager;
    this.projectInnovationCrpManager = projectInnovationCrpManager;
  }

  @Override
  public String add() {
    ProjectInnovation projectInnovation = new ProjectInnovation();

    projectInnovation.setProject(project);

    projectInnovation = projectInnovationManager.saveProjectInnovation(projectInnovation);

    ProjectInnovationInfo projectInnovationInfo = new ProjectInnovationInfo(projectInnovation, this.getActualPhase(),
      "", "", "", "", "", "", new Long(this.getActualPhase().getYear()));

    projectInnovationInfoManager.saveProjectInnovationInfo(projectInnovationInfo);

    innovationID = projectInnovation.getId();

    if (innovationID > 0) {

      ProjectInnovationCrp projectInnovationCrp = new ProjectInnovationCrp();

      projectInnovationCrp.setGlobalUnit(this.getCurrentCrp());
      projectInnovationCrp.setPhase(this.getActualPhase());
      projectInnovationCrp.setProjectInnovation(projectInnovation);

      projectInnovationCrpManager.saveProjectInnovationCrp(projectInnovationCrp);

      return SUCCESS;
    }

    return INPUT;
  }

  @Override
  public String delete() {
    for (ProjectInnovation projectInnovation : project.getInnovations()) {
      if (projectInnovation.getId().longValue() == innovationID) {
        ProjectInnovation projectInnovationBD = projectInnovationManager.getProjectInnovationById(innovationID);
        /*
         * for (SectionStatus sectionStatus : projectInnovationBD.getSectionStatuses()) {
         * sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
         * }
         */
        // projectInnovationManager.saveProjectInnovation(projectInnovation);
        projectInnovationManager.deleteProjectInnovation(projectInnovation.getId());
      }
    }
    return SUCCESS;
  }

  @Override
  public List<Integer> getAllYears() {
    return allYears;
  }

  public long getInnovationID() {
    return innovationID;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  @Override
  public void prepare() throws Exception {

    projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    project = projectManager.getProjectById(projectID);

    allYears = project.getProjecInfoPhase(this.getActualPhase()).getAllYears();

    List<ProjectInnovation> innovations =
      project.getProjectInnovations().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    project.setInnovations(new ArrayList<ProjectInnovation>());
    for (ProjectInnovation projectInnovation : innovations) {
      if (projectInnovation.getProjectInnovationInfo(this.getActualPhase()) != null) {
        project.getInnovations().add(projectInnovation);
      }
    }
  }

  public void setAllYears(List<Integer> allYears) {
    this.allYears = allYears;
  }

  public void setInnovationID(long innovationID) {
    this.innovationID = innovationID;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

}
