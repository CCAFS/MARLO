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
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectSubmissionAction extends BaseAction {


  private static final long serialVersionUID = 4635459226337562141L;


  private SubmissionManager submissionManager;

  private ProjectManager projectManager;
  private CrpManager crpManager;
  private long projectID;
  private Project project;

  @Inject
  public ProjectSubmissionAction(APConfig config, SubmissionManager submissionManager, ProjectManager projectManager,
    CrpManager crpManager) {
    super(config);
    this.submissionManager = submissionManager;
    this.projectManager = projectManager;
    this.crpManager = crpManager;
  }

  @Override
  public String execute() throws Exception {
    if (this.hasPermission("submitProject")) {
      if (this.isCompleteProject(projectID)) {
        List<Submission> submissions = project.getSubmissions().stream()
          .filter(
            c -> c.getCycle().equals(APConstants.PLANNING) && c.getYear().intValue() == this.getCurrentCycleYear())
          .collect(Collectors.toList());

        if (submissions.isEmpty()) {
          this.submitProject();
        }
      }

      return INPUT;
    } else {

      return NOT_AUTHORIZED;
    }
  }

  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }


  @Override
  public boolean isCompleteProject(long projectID) {

    Project project = projectManager.getProjectById(projectID);
    List<SectionStatus> sections = project.getSectionStatuses().stream().collect(Collectors.toList());

    for (SectionStatus sectionStatus : sections) {
      if (sectionStatus.getMissingFields().length() > 0) {
        return false;
      }
    }
    if (sections.size() == 0) {
      return false;
    }
    if (sections.size() < 8) {
      return false;
    }
    return true;
  }

  @Override
  public void prepare() throws Exception {

    try {
      projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));


    } catch (NumberFormatException e) {
      projectID = -1;
      return; // Stop here and go to execute method.
    }


    project = projectManager.getProjectById(projectID);

    if (project != null) {
      String params[] = {crpManager.getCrpById(this.getCrpID()).getAcronym(), project.getId() + ""};
      this.setBasePermission(this.getText(Permission.PROJECT_MANAGE_BASE_PERMISSION, params));
      // Initializing Section Statuses:
      // this.initializeProjectSectionStatuses(project, String.valueOf(this.getCurrentCycleYear()));
    }

  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  private void submitProject() {
    Submission submission = new Submission();

    submission.setCycle(APConstants.PLANNING);
    submission.setUser(this.getCurrentUser());


    submission.setYear((short) this.getCurrentCycleYear());
    submission.setDateTime(new Date());
    submission.setProject(project);

    long result = submissionManager.saveSubmission(submission);

    if (result > 0) {
      submission.setId(result);

    }
  }

}
