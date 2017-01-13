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

package org.cgiar.ccafs.marlo.action.json.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.data.model.User;
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
public class UnSubmitImpactpathwayAction extends BaseAction {


  private static final long serialVersionUID = 6328194359119346721L;

  private CrpProgramManager crpProgramManager;

  private SubmissionManager submissionManager;

  private SendMailS sendMail;

  private long programID;
  private String justification;

  private List<Map<String, Object>> message;

  @Inject
  public UnSubmitImpactpathwayAction(APConfig config, SubmissionManager submissionManager, SendMailS sendMail,
    CrpProgramManager crpProgramManager) {
    super(config);
    this.submissionManager = submissionManager;
    this.sendMail = sendMail;
    this.crpProgramManager = crpProgramManager;
  }

  @Override
  public String execute() throws Exception {
    message = new ArrayList<>();
    Map<String, Object> mStatus = new HashMap<>();

    CrpProgram program = crpProgramManager.getCrpProgramById(programID);

    String cycle = this.getCurrentCycle();
    int year = this.getCurrentCycleYear();

    List<Submission> submissions = program.getSubmissions().stream().filter(
      c -> c.getCycle().equals(cycle) && c.getYear().intValue() == year && (c.isUnSubmit() == null || !c.isUnSubmit()))
      .collect(Collectors.toList());

    if (!submissions.isEmpty()) {
      Submission submission = submissions.get(0);

      submission.setUnSubmitUser(this.getCurrentUser());
      submission.setUnSubmitJustification(justification.trim());
      submission.setUnSubmit(true);

      submissionManager.saveSubmission(submission);

      mStatus.put("status", "ok");
      mStatus.put("message", "ip unsubmitted");

      message.add(mStatus);

      this.sendNotficationEmail(program);

    } else {

      mStatus.put("status", "error");
      mStatus.put("message", "ip has not submits");

      message.add(mStatus);

    }

    return SUCCESS;
  }


  public List<Map<String, Object>> getMessage() {
    return message;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    programID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.CRP_PROGRAM_ID))[0]));
    justification = StringUtils.trim(((String[]) parameters.get(APConstants.JUSTIFICATION_REQUEST))[0]);
  }

  private void sendNotficationEmail(CrpProgram program) {
    // Building the email message
    StringBuilder message = new StringBuilder();
    String[] values = new String[6];
    values[0] = this.getCurrentUser().getComposedCompleteName();
    values[1] = "<b>" + program.getCrp().getAcronym().toUpperCase() + " impact pathway</b> for <b>"
      + program.getAcronym().toUpperCase() + " - " + program.getName() + "</b>";
    values[2] = program.getAcronym().toUpperCase();
    values[3] = String.valueOf(this.getCurrentCycleYear());
    values[4] = this.getCurrentCycle().toLowerCase();
    values[5] = justification;

    String subject = null;
    message.append(this.getText("impact.unsubmit.email.message", values));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));
    subject = this.getText("impact.unsubmit.email.subject",
      new String[] {program.getCrp().getAcronym().toUpperCase() + " " + program.getAcronym().toUpperCase()});


    String toEmail = null;
    String ccEmail = null;

    // Send email to the user that is submitting the project.
    // TO
    toEmail = this.getCurrentUser().getEmail();


    List<CrpProgramLeader> owners =
      program.getCrpProgramLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    StringBuilder ccEmails = new StringBuilder();
    for (CrpProgramLeader crpProgramLeader : owners) {
      User user = crpProgramLeader.getUser();
      if (user.getId() != this.getCurrentUser().getId()) {
        ccEmails.append(user.getEmail());
        ccEmails.append(", ");
      }
    }
    // CC will be the other MLs.
    ccEmail = ccEmails.toString().isEmpty() ? null : ccEmails.toString();
    // Detect if a last ; was added to CC and remove it
    if (ccEmail != null && ccEmail.length() > 0 && ccEmail.charAt(ccEmail.length() - 2) == ',') {
      ccEmail = ccEmail.substring(0, ccEmail.length() - 2);
    }

    ccEmail = ccEmails.toString().isEmpty() ? null : ccEmails.toString();
    // Detect if a last ; was added to CC and remove it
    if (ccEmail != null && ccEmail.length() > 0 && ccEmail.charAt(ccEmail.length() - 2) == ',') {
      ccEmail = ccEmail.substring(0, ccEmail.length() - 2);
    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    sendMail.send(ccEmail, toEmail, bbcEmails, subject, message.toString(), null, null, null, true);
  }

  public void setMessage(List<Map<String, Object>> message) {
    this.message = message;
  }

}
