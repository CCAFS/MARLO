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
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class UnsubmitProjectAction extends BaseAction {


  private static final long serialVersionUID = 6328194359119346721L;


  private ProjectManager projectManager;

  private SubmissionManager submissionManager;

  private RoleManager roleManager;

  private SendMailS sendMail;

  private long projectID;
  private String justification;

  private List<Map<String, Object>> message;

  @Inject
  public UnsubmitProjectAction(APConfig config, ProjectManager projectManager, SubmissionManager submissionManager,
    SendMailS sendMail, RoleManager roleManager) {
    super(config);
    this.projectManager = projectManager;
    this.submissionManager = submissionManager;
    this.roleManager = roleManager;
    this.sendMail = sendMail;
  }

  @Override
  public String execute() throws Exception {
    message = new ArrayList<>();
    Map<String, Object> mStatus = new HashMap<>();

    Project project = projectManager.getProjectById(projectID);

    // this.sendNotficationEmail(project);

    String cycle = this.getCurrentCycle();
    int year = this.getCurrentCycleYear();

    List<Submission> submissions = project.getSubmissions().stream().filter(
      c -> c.getCycle().equals(cycle) && c.getYear().intValue() == year && (c.isUnSubmit() == null || !c.isUnSubmit()))
      .collect(Collectors.toList());

    if (!submissions.isEmpty()) {
      Submission submission = submissions.get(0);

      submission.setUnSubmitUser(this.getCurrentUser());
      submission.setUnSubmitJustification(justification.trim());
      submission.setUnSubmit(true);

      submissionManager.saveSubmission(submission);

      mStatus.put("status", "ok");
      mStatus.put("message", "project unsubmitted");

      message.add(mStatus);

      this.sendNotficationEmail(project);

    } else {

      mStatus.put("status", "error");
      mStatus.put("message", "project has not submits");

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
    // projectID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_REQUEST_ID))[0]));
    // justification = StringUtils.trim(((String[]) parameters.get(APConstants.JUSTIFICATION_REQUEST))[0]);

    Map<String, Parameter> parameters = this.getParameters();
    projectID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_REQUEST_ID).getMultipleValues()[0]));
    justification = StringUtils.trim(parameters.get(APConstants.JUSTIFICATION_REQUEST).getMultipleValues()[0]);
  }

  private void sendNotficationEmail(Project project) {
    String toEmail = "";
    // Add project leader
    if (project.getLeaderPerson() != null
      && project.getLeaderPerson().getUser().getId() != this.getCurrentUser().getId()) {
      toEmail = project.getLeaderPerson().getUser().getEmail();
    }

    // CC Emails
    String ccEmail = "";
    StringBuilder ccEmails = new StringBuilder();

    ccEmails.append(this.getCurrentUser().getEmail());
    ccEmails.append(", ");

    // CC will be also the Management Liaison associated with the flagship(s), if is PMU only the PMU contact
    Long crpPmuRole = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
    Role roleCrpPmu = roleManager.getRoleById(crpPmuRole);
    // If Managment liason is PMU
    if (project.getLiaisonInstitution().getAcronym().equals(roleCrpPmu.getAcronym())) {
      ccEmails.append(project.getLiaisonUser().getUser().getEmail());
      ccEmails.append(", ");
    } else if (project.getLiaisonInstitution().getCrpProgram() != null
      && project.getLiaisonInstitution().getCrpProgram().getProgramType() == 1) {
      // If Managment liason is FL
      List<CrpProgram> crpPrograms = project.getCrp().getCrpPrograms().stream()
        .filter(cp -> cp.getId() == project.getLiaisonInstitution().getCrpProgram().getId())
        .collect(Collectors.toList());
      if (crpPrograms != null) {
        CrpProgram crpProgram = crpPrograms.get(0);
        for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
          .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
          ccEmails.append(crpProgramLeader.getUser().getEmail());
          ccEmails.append(", ");
        }
        // CC will be also other Cluster Leaders
        for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
          .filter(cl -> cl.isActive()).collect(Collectors.toList())) {
          for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity.getCrpClusterActivityLeaders()
            .stream().filter(cl -> cl.isActive()).collect(Collectors.toList())) {
            ccEmails.append(crpClusterActivityLeader.getUser().getEmail());
            ccEmails.append(", ");
          }
        }
      }
    }

    // Add project coordinator(s)
    for (ProjectPartnerPerson projectPartnerPerson : project.getCoordinatorPersons()) {
      if (projectPartnerPerson.getUser().getId() != this.getCurrentUser().getId()) {
        ccEmails.append(projectPartnerPerson.getUser().getEmail());
        ccEmails.append(", ");
      }
    }

    // CC will be also the CRP Admins
    String crpAdmins = "";
    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
    Role roleAdmin = roleManager.getRoleById(adminRol);
    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
    for (UserRole userRole : userRoles) {
      ccEmails.append(userRole.getUser().getEmail());
      ccEmails.append(", ");
      if (crpAdmins.isEmpty()) {
        crpAdmins += userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
      } else {
        crpAdmins += ", " + userRole.getUser().getFirstName() + " (" + userRole.getUser().getEmail() + ")";
      }
    }

    ccEmail = ccEmails.toString().isEmpty() ? null : ccEmails.toString();
    // Detect if a last ; was added to CC and remove it
    if (ccEmail != null && ccEmail.length() > 0 && ccEmail.charAt(ccEmail.length() - 2) == ',') {
      ccEmail = ccEmail.substring(0, ccEmail.length() - 2);
    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    String crp = project.getCrp().getAcronym() != null && !project.getCrp().getAcronym().isEmpty()
      ? project.getCrp().getAcronym() : project.getCrp().getName();
    // subject
    String subject = this.getText("unsubmit.email.subject",
      new String[] {crp, String.valueOf(project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER))});

    // Building the email message
    StringBuilder message = new StringBuilder();
    String[] values = new String[8];
    String plName = "";
    if (project.getLeaderPerson() != null
      && project.getLeaderPerson().getUser().getId() != this.getCurrentUser().getId()) {
      plName = project.getLeaderPerson().getUser().getFirstName();
    }
    values[0] = plName;
    values[1] = this.getCurrentUser().getFirstName();
    values[2] = crp;
    values[3] = project.getTitle();
    values[4] = String.valueOf(project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER));
    values[5] = String.valueOf(this.getCurrentCycleYear());
    values[6] = this.getCurrentCycle().toLowerCase();
    values[7] = justification;

    message.append(this.getText("unsubmit.email.message", values));
    message.append(this.getText("email.support", new String[] {crpAdmins}));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));

    sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
  }

  public void setMessage(List<Map<String, Object>> message) {
    this.message = message;
  }


}
