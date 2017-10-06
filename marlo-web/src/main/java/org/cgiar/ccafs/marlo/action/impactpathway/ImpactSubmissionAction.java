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
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
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
  private RoleManager roleManager;
  private SubmissionManager submissionManager;

  private List<SectionStatus> sectionStatus;
  private CrpProgram crpProgram;

  @Inject
  public ImpactSubmissionAction(APConfig config, CrpProgramManager crpProgramManager,
    SectionStatusManager sectionStatusManager, SubmissionManager submissionManager, SendMailS sendMail,
    RoleManager roleManager) {
    super(config);
    this.crpProgramManager = crpProgramManager;
    this.sectionStatusManager = sectionStatusManager;
    this.submissionManager = submissionManager;
    this.sendMail = sendMail;
    this.roleManager = roleManager;
  }

  @Override
  public String execute() throws Exception {
    if (this.hasPermissionNoBase(this.generatePermission(Permission.IMPACT_PATHWAY_SUBMISSION_PERMISSION,
      crpProgram.getCrp().getAcronym(), crpProgram.getId().toString()))) {
      if (this.isCompleteImpact(progamID)) {
        List<Submission> submissions = submissionManager.findAll();
        if (submissions != null) {
          submissions = submissions.stream()
            .filter(c -> c.getCrpProgram() != null && c.getCrpProgram().equals(crpProgram)
              && (c.isUnSubmit() == null || !c.isUnSubmit()) && c.getYear() != null
              && c.getYear().intValue() == this.getActualPhase().getYear()
              && c.getCycle().equals(this.getActualPhase().getDescription()))
            .collect(Collectors.toList());
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
    // Send email to the user that is submitting the project.
    // TO
    String toEmail = this.getCurrentUser().getEmail();
    String ccEmail = "";
    // CC will be the other MLs.
    List<CrpProgramLeader> owners =
      crpProgram.getCrpProgramLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    for (CrpProgramLeader crpProgramLeader : owners) {
      User user = crpProgramLeader.getUser();
      if (user.getId() != this.getCurrentUser().getId()) {
        if (ccEmail.isEmpty()) {
          ccEmail = user.getEmail();
        } else {
          ccEmail += ", " + user.getEmail();
        }
      }
    }
    // CC will be also the CRP Admins
    String crpAdmins = "";
    String crpAdminsEmail = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += userRole.getUser().getEmail();
      } else {
        crpAdmins += ", " + userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
        crpAdminsEmail += ", " + userRole.getUser().getEmail();
      }
    }
    if (!crpAdminsEmail.isEmpty()) {
      if (ccEmail.isEmpty()) {
        ccEmail = crpAdminsEmail;
      } else {
        ccEmail += ", " + crpAdminsEmail;
      }
    }
    // CC will be also other Cluster Leaders
    for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
      .filter(cl -> cl.isActive() && cl.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
      for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity.getCrpClusterActivityLeaders()
        .stream().filter(cl -> cl.isActive()).collect(Collectors.toList())) {
        if (ccEmail.isEmpty()) {
          ccEmail += crpClusterActivityLeader.getUser().getEmail();
        } else {
          ccEmail += ", " + crpClusterActivityLeader.getUser().getEmail();
        }
      }
    }
    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    // subject
    String subject = null;
    subject = this.getText("impact.submit.email.subject",
      new String[] {crpProgram.getCrp().getName(), crpProgram.getAcronym()});
    // Building the email message
    String crp = crpProgram.getCrp().getAcronym() != null && !crpProgram.getCrp().getAcronym().isEmpty()
      ? crpProgram.getCrp().getAcronym() : crpProgram.getCrp().getName();
    StringBuilder message = new StringBuilder();
    String[] values = new String[4];
    values[0] = this.getCurrentUser().getFirstName();
    values[1] = crpProgram.getAcronym();
    values[2] = crp;
    values[3] = crpProgram.getName();

    message.append(this.getText("impact.submit.email.message", values));
    message.append(this.getText("email.support", new String[] {crpAdmins}));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));

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
