/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
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
import org.cgiar.ccafs.marlo.utils.SendMail;

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
  private SendMail sendMail;

  private CrpProgramManager crpProgramManager;

  private SectionStatusManager sectionStatusManager;

  private SubmissionManager submissionManager;

  private List<SectionStatus> sectionStatus;
  private CrpProgram crpProgram;

  @Inject
  public ImpactSubmissionAction(APConfig config, CrpProgramManager crpProgramManager,
    SectionStatusManager sectionStatusManager, SubmissionManager submissionManager, SendMail sendMail) {
    super(config);
    this.crpProgramManager = crpProgramManager;
    this.sectionStatusManager = sectionStatusManager;
    this.submissionManager = submissionManager;
    this.sendMail = sendMail;
  }

  @Override
  public String execute() throws Exception {
    if (this.hasPermission("submit")) {
      if (this.isCompleteImpact(progamID)) {
        List<Submission> submissions = submissionManager.findAll();
        if (submissions != null) {
          submissions =
            submissions.stream().filter(c -> c.getCrpProgram().equals(crpProgram)).collect(Collectors.toList());
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


  public boolean hasPersmissionSubmit() {
    return this.hasPermission("submit");
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
    sectionStatus = sectionStatusManager.findAll().stream().filter(c -> c.getCrpProgram().equals(crpProgram))
      .collect(Collectors.toList());
    if (!crpProgram.getSubmissions().isEmpty()) {
      submission = crpProgram.getSubmissions().stream().collect(Collectors.toList()).get(0);
    }
  }

  private void sendNotficationEmail() {
    // Building the email message
    StringBuilder message = new StringBuilder();
    String[] values = new String[3];
    values[0] = this.getCurrentUser().getComposedCompleteName();
    values[1] = crpProgram.getAcronym().toUpperCase();

    String subject = null;


    message.append(this.getText("impact.submit.email.message", values));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));
    subject = this.getText("impact.submit.email.subject",
      new String[] {crpProgram.getCrp().getAcronym().toUpperCase(), crpProgram.getAcronym().toUpperCase()});


    /**
     * 
     */
    String toEmail = null;
    String ccEmail = null;
    if (config.isProduction()) {
      // Send email to the user that is submitting the project.
      // TO
      toEmail = this.getCurrentUser().getEmail();

      // Getting all the MLs associated to the Project Liaison institution
      List<CrpProgramLeader> owners =
        crpProgram.getCrpProgramLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      StringBuilder ccEmails = new StringBuilder();
      for (CrpProgramLeader crpProgramLeader : owners) {
        User user = crpProgramLeader.getUser();
        if (user.getId() != this.getCurrentUser().getId()) {
          ccEmails.append(user.getEmail());
          ccEmails.append(" ");
        }
      }
      // CC will be the other MLs.
      ccEmail = ccEmails.toString().isEmpty() ? null : ccEmails.toString();

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
