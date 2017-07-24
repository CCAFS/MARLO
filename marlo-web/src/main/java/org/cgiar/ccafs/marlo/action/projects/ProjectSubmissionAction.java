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
import org.cgiar.ccafs.marlo.action.summaries.ReportingSummaryAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Andrés Valencia - CIAT/CCAFS
 * @author Christian Garcia - CIAT/CCAFS
 */
public class ProjectSubmissionAction extends BaseAction {


  private static final long serialVersionUID = 4635459226337562141L;
  private static Logger LOG = LoggerFactory.getLogger(ProjectSubmissionAction.class);

  // Manager
  private SubmissionManager submissionManager;
  private ProjectManager projectManager;
  private CrpManager crpManager;
  private SendMailS sendMail;
  private LiaisonUserManager liasonUserManager;
  private Crp loggedCrp;
  private String cycleName;
  private RoleManager roleManager;


  private boolean complete;


  private long projectID;

  private Project project;


  @Inject
  ReportingSummaryAction reportingSummaryAction;

  @Inject
  public ProjectSubmissionAction(APConfig config, SubmissionManager submissionManager, ProjectManager projectManager,
    CrpManager crpManager, SendMailS sendMail, LiaisonUserManager liasonUserManager, RoleManager roleManager) {
    super(config);
    this.submissionManager = submissionManager;
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.sendMail = sendMail;
    this.liasonUserManager = liasonUserManager;
    this.roleManager = roleManager;
  }

  @Override
  public String execute() throws Exception {
    complete = false;
    if (this.hasPermission("submitProject")) {
      if (this.isCompleteProject(projectID)) {
        List<Submission> submissions = project.getSubmissions()
          .stream().filter(c -> c.getCycle().equals(APConstants.PLANNING)
            && c.getYear().intValue() == this.getCurrentCycleYear() && (c.isUnSubmit() == null || !c.isUnSubmit()))
          .collect(Collectors.toList());

        if (submissions.isEmpty()) {
          this.submitProject();
          complete = true;
        } else {
          this.setSubmission(submissions.get(0));
          complete = true;
        }
      }

      return INPUT;
    } else {

      return NOT_AUTHORIZED;
    }
  }


  public String getCycleName() {
    return cycleName;
  }

  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("Full_Project_Report-");
    fileName.append(loggedCrp.getName() + "-");
    fileName.append("P" + projectID + "-");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
    return fileName.toString();

  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }


  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }


  public boolean isComplete() {
    return complete;
  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

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
    cycleName = APConstants.PLANNING;

  }

  private void sendNotficationEmail() {
    // Send email to the user that is submitting the project.
    // TO
    String toEmail = this.getCurrentUser().getEmail();
    String ccEmail = "";


    StringBuilder ccEmails = new StringBuilder();

    // CC will be also the Management Liaison associated with the flagship(s), if is PMU only the PMU contact
    Long crpPmuRole = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
    Role roleCrpPmu = roleManager.getRoleById(crpPmuRole);
    // If Managment liason is PMU
    if (project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getAcronym()
      .equals(roleCrpPmu.getAcronym())) {
      ccEmails.append(project.getProjecInfoPhase(this.getActualPhase()).getLiaisonUser().getUser().getEmail());
      ccEmails.append(", ");
    } else if (project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getCrpProgram() != null) {
      // If Managment liason is FL
      List<CrpProgram> crpPrograms =
        project
          .getCrp().getCrpPrograms().stream().filter(cp -> cp.getId() == project
            .getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getCrpProgram().getId())
        .collect(Collectors.toList());
      if (crpPrograms != null) {
        if (crpPrograms.size() > 1) {
          LOG.warn("Crp programs should be 1");
        }
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
    } else {
      for (LiaisonUser liaisonUser : project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution()
        .getLiaisonUsers()) {
        ccEmails.append(liaisonUser.getUser().getEmail());
        ccEmails.append(", ");
      }
    }


    // Add project leader
    if (project.getLeaderPerson(this.getActualPhase()) != null && project.getLeaderPerson(this.getActualPhase())
      .getUser().getId().longValue() != this.getCurrentUser().getId().longValue()) {
      ccEmails.append(project.getLeaderPerson(this.getActualPhase()).getUser().getEmail());
      ccEmails.append(", ");
    }
    // Add project coordinator(s)
    for (ProjectPartnerPerson projectPartnerPerson : project.getCoordinatorPersons(this.getActualPhase())) {
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

    // subject
    String subject = null;
    subject = this.getText("submit.email.subject", new String[] {loggedCrp.getName(),
      String.valueOf(project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER))});

    // Building the email message
    StringBuilder message = new StringBuilder();
    String[] values = new String[6];
    values[0] = this.getCurrentUser().getFirstName();
    values[1] = loggedCrp.getName();
    values[2] = project.getProjecInfoPhase(this.getActualPhase()).getTitle();
    values[3] = String.valueOf(project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER));
    values[4] = String.valueOf(this.getCurrentCycleYear());
    values[5] = this.getCurrentCycle().toLowerCase();
    // Message to download the pdf
    /*
     * values[6] = config.getBaseUrl() + "/projects/" + this.getCurrentCrp().getAcronym() + "/reportingSummary.do?"
     * + APConstants.PROJECT_REQUEST_ID + "=" + projectID + "&" + APConstants.YEAR_REQUEST + "="
     * + this.getCurrentCycleYear() + "&" + APConstants.CYCLE + "=" + this.getCurrentCycle();
     */

    message.append(this.getText("submit.email.message", values));
    message.append(this.getText("email.support", new String[] {crpAdmins}));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));

    // Send pdf
    // Get the PDF from the Project report url.
    ByteBuffer buffer = null;
    String fileName = null;
    String contentType = null;
    try {
      // Set the parameters that are assigned in the prepare by reportingSummaryAction
      reportingSummaryAction.setSession(this.getSession());
      reportingSummaryAction.setYear(this.getCurrentCycleYear());
      reportingSummaryAction.setLoggedCrp(loggedCrp);
      reportingSummaryAction.setCycle(this.getCurrentCycle());
      reportingSummaryAction.setProjectID(projectID);
      reportingSummaryAction.setProject(projectManager.getProjectById(projectID));
      reportingSummaryAction.setCrpSession(loggedCrp.getAcronym());
      reportingSummaryAction.execute();
      // Getting the file data.
      //
      buffer = ByteBuffer.wrap(reportingSummaryAction.getBytesPDF());
      fileName = this.getFileName();
      contentType = "application/pdf";
      //
    } catch (Exception e) {
      // // Do nothing.
      LOG.error("There was an error trying to get the URL to download the PDF file: " + e.getMessage());
    }


    if (buffer != null && fileName != null && contentType != null) {
      sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), buffer.array(), contentType, fileName,
        true);
    } else {
      sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }
  }


  public void setComplete(boolean complete) {
    this.complete = complete;
  }

  public void setCycleName(String cycleName) {
    this.cycleName = cycleName;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  private void submitProject() {
    Submission submission = new Submission();

    submission.setCycle(this.getCurrentCycle());
    submission.setUser(this.getCurrentUser());


    submission.setYear((short) this.getCurrentCycleYear());
    submission.setDateTime(new Date());
    submission.setProject(project);

    long result = submissionManager.saveSubmission(submission);
    this.setSubmission(submission);
    if (result > 0) {
      submission.setId(result);
      this.sendNotficationEmail();

    }
  }

}
