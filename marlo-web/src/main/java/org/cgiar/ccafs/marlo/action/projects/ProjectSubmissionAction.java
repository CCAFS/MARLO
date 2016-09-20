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
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.utils.APConfig;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectSubmissionAction extends BaseAction {


  private static final long serialVersionUID = 4635459226337562141L;


  private SubmissionManager submissionManager;

  private ProjectManager projectManager;

  private long projectID;
  private Project project;

  @Inject
  public ProjectSubmissionAction(APConfig config, SubmissionManager submissionManager, ProjectManager projectManager) {
    super(config);
    this.submissionManager = submissionManager;
    this.projectManager = projectManager;
  }

  @Override
  public String execute() throws Exception {
    if (this.hasPermission("submitProject")) {


      return SUCCESS;
    } else {

      return NOT_AUTHORIZED;
    }
  }

  public Project getProject() {
    return project;
  }

  @Override
  public void prepare() throws Exception {

    try {
      projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    } catch (NumberFormatException e) {
      projectID = -1;
      return; // Stop here and go to execute method.
    }

    int year = 0;
    if (this.isReportingActive()) {
      year = this.getReportingYear();
    } else {
      year = this.getPlanningYear();
    }

    project = projectManager.getProjectById(projectID);

    if (project != null) {
      // Initializing Section Statuses:
      // this.initializeProjectSectionStatuses(project, String.valueOf(this.getCurrentCycleYear()));
    }

  }

  public void setProject(Project project) {
    this.project = project;
  }

}
