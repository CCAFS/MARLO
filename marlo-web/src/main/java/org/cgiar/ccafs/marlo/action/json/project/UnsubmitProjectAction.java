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
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

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

  private SendMailS sendMail;

  private long projectID;
  private String justification;

  private List<Map<String, Object>> message;

  @Inject
  public UnsubmitProjectAction(APConfig config, ProjectManager projectManager, SubmissionManager submissionManager,
    SendMailS sendMail) {
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

      this.sendNotficationEmail(project);

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

  private void sendNotficationEmail(Project project) {
    // Building the email message
    StringBuilder message = new StringBuilder();
    String[] values = new String[5];
    values[0] = this.getCurrentUser().getComposedCompleteName();
    values[1] = justification;
    values[2] = project.getTitle();
    values[3] = String.valueOf(this.getCurrentCycleYear());
    values[4] = this.getCurrentCycle().toLowerCase();

    String subject = null;
    message.append(this.getText("unsubmit.email.message", values));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));
    subject = this.getText("submit.email.subject", new String[] {project.getCrp().getName(),
      String.valueOf(project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER))});


    String toEmail = null;
    String ccEmail = null;

    // Send email to the user that is submitting the project.
    // TO
    toEmail = this.getCurrentUser().getEmail();

    StringBuilder ccEmails = new StringBuilder();

    List<ProjectPartner> partners =
      new ArrayList<>(project.getProjectPartners().stream().filter(pp -> pp.isActive()).collect(Collectors.toList()));

    for (ProjectPartner projectPartner : partners) {
      List<ProjectPartnerPerson> partnerPersons = new ArrayList<>(
        projectPartner.getProjectPartnerPersons().stream().filter(pp -> pp.isActive()).collect(Collectors.toList()));
      for (ProjectPartnerPerson projectPartnerPerson : partnerPersons) {
        if (projectPartnerPerson.getContactType().equals(APConstants.PROJECT_PARTNER_PL)
          || projectPartnerPerson.getContactType().equals(APConstants.PROJECT_PARTNER_PC)) {
          ccEmails.append(projectPartnerPerson.getUser().getEmail());
          ccEmails.append(", ");
        }
      }
    }

    ccEmail = ccEmails.toString().isEmpty() ? null : ccEmails.toString();
    // Detect if a last ; was added to CC and remove it
    if (ccEmail != null && ccEmail.length() > 0 && ccEmail.charAt(ccEmail.length() - 2) == ',') {
      ccEmail = ccEmail.substring(0, ccEmail.length() - 2);
    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
  }

  public void setMessage(List<Map<String, Object>> message) {
    this.message = message;
  }


}
