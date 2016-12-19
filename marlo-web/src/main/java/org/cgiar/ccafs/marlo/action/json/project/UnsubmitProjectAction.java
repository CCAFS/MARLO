/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,s
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class UnsubmitProjectAction extends BaseAction {


  private static final long serialVersionUID = 6328194359119346721L;


  private ProjectManager projectManager;

  private SubmissionManager submissionManager;

  private long projectID;
  private String justification;

  private List<Map<String, Object>> message;

  @Inject
  public UnsubmitProjectAction(APConfig config, ProjectManager projectManager, SubmissionManager submissionManager) {
    super(config);
    this.projectManager = projectManager;
    this.submissionManager = submissionManager;
  }

  @Override
  public String execute() throws Exception {
    message = new ArrayList<>();
    Map<String, Object> mStatus = new HashMap<>();

    Project project = projectManager.getProjectById(projectID);

    String cycle = this.getCurrentCycle();

    int year = this.getCurrentCycleYear();

    List<Submission> submissions = project.getSubmissions().stream().filter(
      c -> c.getCycle().equals(cycle) && c.getYear().intValue() == year && (c.isUnSubmit() == null || !c.isUnSubmit()))
      .collect(Collectors.toList());

    if (!submissions.isEmpty()) {
      Submission submission = submissions.get(0);

      submission.setUnSubmitUser(this.getCurrentUser());
      submission.setUnSubmitJustification(justification);
      submission.setUnSubmit(true);

      submissionManager.saveSubmission(submission);

      mStatus.put("status", "ok");
      mStatus.put("message", "project unsubmitted");

    } else {

      mStatus.put("status", "error");
      mStatus.put("message", "project has not submits");

    }

    return SUCCESS;
  }


  public List<Map<String, Object>> getMessage() {
    return message;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    projectID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_REQUEST_ID))[0]));
    justification = StringUtils.trim(((String[]) parameters.get(APConstants.JUSTIFICATION_REQUEST))[0]);
  }

  public void setMessage(List<Map<String, Object>> message) {
    this.message = message;
  }


}
