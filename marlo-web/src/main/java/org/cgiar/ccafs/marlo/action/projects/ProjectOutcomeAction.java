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
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 * @author Christian Garcia- CIAT/CCAFS
 */
public class ProjectOutcomeAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 4520862722467820286L;
  private ProjectManager projectManager;
  private CrpManager crpManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;


  private ProjectOutcomeManager projectOutcomeManager;
  // Front-end
  private long projectID;
  private long projectOutcomeID;
  private Crp loggedCrp;
  private Project project;

  private ProjectOutcome projectOutcome;


  @Inject
  public ProjectOutcomeAction(APConfig config, ProjectManager projectManager, CrpManager crpManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, ProjectOutcomeManager projectOutcomeManager) {
    super(config);
    this.projectManager = projectManager;

    this.crpManager = crpManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.projectOutcomeManager = projectOutcomeManager;
  }


  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }


  public ProjectOutcome getProjectOutcome() {
    return projectOutcome;
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
    try {
      projectOutcomeID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_OUTCOME_REQUEST_ID)));
    } catch (Exception e) {

    }
    project = projectManager.getProjectById(projectID);
    projectOutcome = projectOutcomeManager.getProjectOutcomeById(projectOutcomeID);
    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_CONTRIBRUTIONCRP_BASE_PERMISSION, params));

  }


  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectOutcome(ProjectOutcome projectOutcome) {
    this.projectOutcome = projectOutcome;
  }

  public void setProjectOutcomeID(long projectOutcomeID) {
    this.projectOutcomeID = projectOutcomeID;
  }

}
