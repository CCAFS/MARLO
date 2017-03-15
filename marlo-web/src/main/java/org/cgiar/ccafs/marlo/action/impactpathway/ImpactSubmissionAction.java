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


package org.cgiar.ccafs.marlo.action.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImpactSubmissionAction extends BaseAction {

  private static Logger LOG = LoggerFactory.getLogger(ImpactSubmissionAction.class);

  /**
   * 
   */
  private static final long serialVersionUID = -1469350327289414225L;

  private long progamID;
  // Model for the front-end
  private Submission submission;

  private boolean alreadySubmitted;
  private SendMailS sendMail;

  private CrpProgramManager crpProgramManager;

  private SectionStatusManager sectionStatusManager;

  private SubmissionManager submissionManager;

  private List<SectionStatus> sectionStatus;
  private CrpProgram crpProgram;

  @Inject
  public ImpactSubmissionAction(APConfig config, CrpProgramManager crpProgramManager,
    SectionStatusManager sectionStatusManager, SubmissionManager submissionManager, SendMailS sendMail) {
    super(config);
    this.crpProgramManager = crpProgramManager;
    this.sectionStatusManager = sectionStatusManager;
    this.submissionManager = submissionManager;
    this.sendMail = sendMail;
  }

  @Override
  public String execute() throws Exception {
    if (this.hasPermissionNoBase(
      this.generatePermission(Permission.IMPACT_PATHWAY_SUBMISSION_PERMISSION, crpProgram.getCrp().getAcronym()))) {
      if (this.isCompleteImpact(progamID)) {
        List<Submission> submissions = submissionManager.findAll();
        if (submissions != null) {
          submissions =
            submissions.stream().filter(c -> c.getCrpProgram() != null && c.getCrpProgram().equals(crpProgram)
              && (c.isUnSubmit() == null || !c.isUnSubmit())).collect(Collectors.toList());
          for (Submission theSubmission : submissions) {
            submission = theSubmission;
            alreadySubmitted = true;
          }
        }

        if (!alreadySubmitted) {
          // Let's submit the project. <:)
          submission = new Submission();
          submission.setCrpProgram(crpProgram);
          submission.setDateTime(new Date());
          submission.setUser(this.getCurrentUser());
          submission.setYear((short) this.getCurrentCycleYear());
          submission.setCycle(this.getCurrentCycle());
          submissionManager.saveSubmission(submission);
          this.sendNotficationEmail();
        }
      }
      return SUCCESS;
    }

    return NOT_AUTHORIZED;

  }

  public long getProgamID() {
    return progamID;
  }

  @Override
  public Submission getSubmission() {
    return submission;
  }


  @Override
  public void prepare() throws Exception {
    super.prepare();

    try {
      progamID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CRP_PROGRAM_ID)));
    } catch (NumberFormatException e) {
      LOG.error("-- prepare() > There was an error parsing the project identifier '{}'.", progamID, e);
      progamID = -1;
      return; // Stop here and go to execute method.
    }


    // Getting the program information.
    crpProgram = crpProgramManager.getCrpProgramById(progamID);
    String params[] = {crpProgram.getCrp().getAcronym(), crpProgram.getId().toString()};
    this.setBasePermission(this.getText(Permission.IMPACT_PATHWAY_BASE_PERMISSION, params));
    // Initializing Section Statuses:
    sectionStatus = sectionStatusManager.findAll().stream()
      .filter(c -> c.getCrpProgram() != null && c.getCrpProgram().equals(crpProgram)).collect(Collectors.toList());
    if (!crpProgram.getSubmissions().isEmpty()) {
      submission = crpProgram.getSubmissions().stream().collect(Collectors.toList()).get(0);
    }
  }

  private void sendNotficationEmail() {
    // Building the email message
    StringBuilder message = new StringBuilder();
    String[] values = new String[3];
    values[0] = this.getCurrentUser().getComposedCompleteName();
    values[1] = "<b>" + crpProgram.getCrp().getAcronym().toUpperCase() + " impact pathway</b> for <b>"
      + crpProgram.getAcronym().toUpperCase() + " - " + crpProgram.getName() + "</b>";
    values[2] = crpProgram.getAcronym().toUpperCase();

    String subject = null;

    message.append(this.getText("impact.submit.email.message", values));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));
    subject = this.getText("impact.submit.email.subject",
      new String[] {crpProgram.getCrp().getAcronym().toUpperCase() + " " + crpProgram.getAcronym().toUpperCase()});


    /**
     * 
     */
    String toEmail = null;
    String ccEmail = null;

    // Send email to the user that is submitting the project.
    // TO
    toEmail = this.getCurrentUser().getEmail();


    List<CrpProgramLeader> owners =
      crpProgram.getCrpProgramLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList());
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


    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    /*
     * // Get the PDF from the Project report url.
     * ByteBuffer buffer = null;
     * String fileName = null;
     * String contentType = null;
     * if (buffer != null && fileName != null && contentType != null) {
     * sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), buffer.array(), contentType, fileName,
     * true);
     * } else {
     * sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
     * }
     */
    sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
  }


  public void setProgamID(long progamID) {
    this.progamID = progamID;
  }

  @Override
  public void setSubmission(Submission submission) {
    this.submission = submission;
  }


}
