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
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class UnSubmitImpactpathwayAction extends BaseAction {


  private static final long serialVersionUID = 6328194359119346721L;

  private final CrpProgramManager crpProgramManager;

  private final SubmissionManager submissionManager;
  private final RoleManager roleManager;

  private final SendMailS sendMail;

  private long programID;
  private String justification;

  private List<Map<String, Object>> message;

  @Inject
  public UnSubmitImpactpathwayAction(APConfig config, SubmissionManager submissionManager, SendMailS sendMail,
    CrpProgramManager crpProgramManager, RoleManager roleManager) {
    super(config);
    this.submissionManager = submissionManager;
    this.sendMail = sendMail;
    this.crpProgramManager = crpProgramManager;
    this.roleManager = roleManager;
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
    // Map<String, Object> parameters = this.getParameters();
    // programID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.CRP_PROGRAM_ID))[0]));
    // justification = StringUtils.trim(((String[]) parameters.get(APConstants.JUSTIFICATION_REQUEST))[0]);

    Map<String, Parameter> parameters = this.getParameters();
    programID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.CRP_PROGRAM_ID).getMultipleValues()[0]));
    justification = StringUtils.trim(parameters.get(APConstants.JUSTIFICATION_REQUEST).getMultipleValues()[0]);
  }

  private void sendNotficationEmail(CrpProgram program) {
    // Send email to the user that is submitting the project.
    // TO
    String toEmail = "";
    // To will be the other MLs.
    List<CrpProgramLeader> owners =
      program.getCrpProgramLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    for (CrpProgramLeader crpProgramLeader : owners) {
      User user = crpProgramLeader.getUser();
      if (user.getId() != this.getCurrentUser().getId()) {
        if (toEmail.isEmpty()) {
          toEmail = user.getEmail();
        } else {
          toEmail += ", " + user.getEmail();
        }
      }
    }
    // CC will be also other Cluster Leaders
    for (CrpClusterOfActivity crpClusterOfActivity : program.getCrpClusterOfActivities().stream()
      .filter(cl -> cl.isActive() && cl.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
      for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity.getCrpClusterActivityLeaders()
        .stream().filter(cl -> cl.isActive()).collect(Collectors.toList())) {
        if (toEmail.isEmpty()) {
          toEmail += crpClusterActivityLeader.getUser().getEmail();
        } else {
          toEmail += ", " + crpClusterActivityLeader.getUser().getEmail();
        }
      }
    }
    // CC will be user who unsubmited
    String ccEmail = this.getCurrentUser().getEmail();


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

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    String crp = program.getCrp().getAcronym() != null && !program.getCrp().getAcronym().isEmpty()
      ? program.getCrp().getAcronym() : program.getCrp().getName();
    // subject
    String subject = null;
    subject = this.getText("impact.unsubmit.email.subject", new String[] {crp, program.getAcronym()});

    // Building the email message
    StringBuilder message = new StringBuilder();
    String[] values = new String[6];
    values[0] = this.getCurrentUser().getFirstName();
    values[1] = program.getAcronym();
    values[2] = crp;
    values[3] = program.getName();
    values[4] = justification;

    message.append(this.getText("impact.unsubmit.email.message", values));
    message.append(this.getText("email.support", new String[] {crpAdmins}));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));

    sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
  }

  public void setMessage(List<Map<String, Object>> message) {
    this.message = message;
  }

}
