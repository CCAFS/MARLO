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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

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
  private UserManager userManager;

  private ProjectManager projectManager;
  private GlobalUnitManager crpManager;
  private SendMailS sendMail;
  private GlobalUnit loggedCrp;
  private String cycleName;
  private final RoleManager roleManager;
  private PhaseManager phaseManager;


  private long projectID;

  private Project project;

  private ReportingSummaryAction reportingSummaryAction;

  @Inject
  public ProjectSubmissionAction(APConfig config, SubmissionManager submissionManager, ProjectManager projectManager,
    GlobalUnitManager crpManager, SendMailS sendMail, LiaisonUserManager liasonUserManager, RoleManager roleManager,
    PhaseManager phaseManager, UserManager userManager, ReportingSummaryAction reportingSummaryAction) {
    super(config);
    this.submissionManager = submissionManager;
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.sendMail = sendMail;
    this.roleManager = roleManager;
    this.userManager = userManager;
    this.phaseManager = phaseManager;
    this.reportingSummaryAction = reportingSummaryAction;
  }

  @Override
  public String execute() throws Exception {
    if (this.hasPermission("submitProject")) {
      List<Submission> submissions = project.getSubmissions().stream()
        .filter(c -> c.getCycle().equals(this.getActualPhase().getDescription())
          && c.getYear().intValue() == this.getActualPhase().getYear() && (c.isUnSubmit() == null || !c.isUnSubmit()))
        .collect(Collectors.toList());

      if (submissions.isEmpty()) {
        this.submitProject();
      } else {
        long submissionId = submissions.get(0).getId();
        Submission submission = submissionManager.getSubmissionById(submissionId);
        submission.setUser(userManager.getUser(submission.getUser().getId()));
        this.setSubmission(submission);
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
    fileName.append("Full_Cluster_Report-");
    fileName.append(loggedCrp.getAcronym() + "-");
    fileName.append("C" + projectID + "-");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
    return fileName.toString();

  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    try {
      projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));


    } catch (NumberFormatException e) {
      projectID = -1;
      return; // Stop here and go to execute method.
    }


    project = projectManager.getProjectById(projectID);

    if (project != null) {
      String params[] = {crpManager.getGlobalUnitById(this.getCrpID()).getAcronym(), project.getId() + ""};
      this.setBasePermission(this.getText(Permission.PROJECT_MANAGE_BASE_PERMISSION, params));
      project.getProjecInfoPhase(this.getActualPhase());
      // Initializing Section Statuses:
      // this.initializeProjectSectionStatuses(project, String.valueOf(this.getActualPhase().getYear()));
    }
    cycleName = APConstants.PLANNING;

  }

  private void sendNotficationEmail() {
    // Get The Crp/Center/Platform where the project was created
    GlobalUnitProject globalUnitProject = project.getGlobalUnitProjects().stream()
      .filter(gu -> gu.isActive() && gu.isOrigin()).collect(Collectors.toList()).get(0);

    // Send email to the user that is submitting the project.
    // TO
    String toEmail = "";
    if (this.config.getEmail_pmu() != null) {
      toEmail = this.config.getEmail_pmu();
    } else {
      toEmail = this.getCurrentUser().getEmail();
    }

    // new method
    String ccEmail = "";
    ccEmail += this.getCurrentUser().getEmail();
    ProjectPartnerPerson projectLeader = project.getLeaderPersonDB(this.getActualPhase());
    if (projectLeader != null && projectLeader.getUser() != null && projectLeader.getUser().getEmail() != null) {

      if (ccEmail.isEmpty()) {
        ccEmail += projectLeader.getUser().getEmail();
      } else {
        ccEmail += ", " + projectLeader.getUser().getEmail();
      }
    }

    /*
     * StringBuilder ccEmails = new StringBuilder();
     * // CC will be also the Management Liaison associated with the flagship(s), if is PMU only the PMU contact
     * Long crpPmuRole = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
     * Role roleCrpPmu = roleManager.getRoleById(crpPmuRole);
     * // If Managment liason is PMU
     * if (project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getCrpProgram() != null) {
     * // If Managment liason is FL
     * List<CrpProgram> crpPrograms = globalUnitProject
     * .getGlobalUnit().getCrpPrograms().stream().filter(cp -> cp.getId() == project
     * .getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getCrpProgram().getId())
     * .collect(Collectors.toList());
     * if (crpPrograms != null) {
     * if (crpPrograms.size() > 1) {
     * LOG.warn("Crp programs should be 1");
     * }
     * CrpProgram crpProgram = crpPrograms.get(0);
     * for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
     * .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
     * ccEmails.append(crpProgramLeader.getUser().getEmail());
     * ccEmails.append(", ");
     * }
     * // CC will be also other Cluster Leaders
     * if (this.hasSpecificities(APConstants.CRP_EMAIL_CC_FL_FM_CL)) {
     * for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
     * .filter(cl -> cl.isActive() && cl.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
     * for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity.getCrpClusterActivityLeaders()
     * .stream().filter(cl -> cl.isActive()).collect(Collectors.toList())) {
     * ccEmails.append(crpClusterActivityLeader.getUser().getEmail());
     * ccEmails.append(", ");
     * }
     * }
     * }
     * }
     * } else {
     * for (LiaisonUser liaisonUser : project.getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution()
     * .getLiaisonUsers()) {
     * ccEmails.append(liaisonUser.getUser().getEmail());
     * ccEmails.append(", ");
     * }
     * }
     * // Add project leader
     * if (project.getLeaderPerson(this.getActualPhase()) != null && project.getLeaderPerson(this.getActualPhase())
     * .getUser().getId().longValue() != this.getCurrentUser().getId().longValue()) {
     * ccEmails.append(project.getLeaderPerson(this.getActualPhase()).getUser().getEmail());
     * ccEmails.append(", ");
     * }
     * // Add project coordinator(s)
     * for (ProjectPartnerPerson projectPartnerPerson : project.getCoordinatorPersons(this.getActualPhase())) {
     * if (projectPartnerPerson.getUser().getId() != this.getCurrentUser().getId()) {
     * ccEmails.append(projectPartnerPerson.getUser().getEmail());
     * ccEmails.append(", ");
     * }
     * }
     * // CC will be also the CRP Admins
     * String crpAdmins = "";
     * long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
     * Role roleAdmin = roleManager.getRoleById(adminRol);
     * List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
     * .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
     * for (UserRole userRole : userRoles) {
     * ccEmails.append(userRole.getUser().getEmail());
     * ccEmails.append(", ");
     * if (crpAdmins.isEmpty()) {
     * crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
     * } else {
     * crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
     * }
     * }
     * ccEmail = ccEmails.toString().isEmpty() ? null : ccEmails.toString();
     * // Detect if a last ; was added to CC and remove it
     * if (ccEmail != null && ccEmail.length() > 0 && ccEmail.charAt(ccEmail.length() - 2) == ',') {
     * ccEmail = ccEmail.substring(0, ccEmail.length() - 2);
     * }
     */


    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    String crp = loggedCrp.getAcronym() != null && !loggedCrp.getAcronym().isEmpty() ? loggedCrp.getAcronym()
      : loggedCrp.getName();
    // subject
    String projectAcronym = null;
    if (project != null && project.getAcronym() != null) {
      projectAcronym = project.getAcronym();
    } else {
      projectAcronym = "C" + project.getId();
    }
    String subject = null;
    subject = this.getText("submit.email.subject", new String[] {crp, projectAcronym});


    // Building the email message
    StringBuilder message = new StringBuilder();
    String[] values = new String[7];
    values[0] = this.getCurrentUser().getComposedCompleteName();
    values[1] = crp;
    values[2] = project.getProjecInfoPhase(this.getActualPhase()).getTitle();
    values[3] = String.valueOf(project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER));
    values[4] = String.valueOf(this.getActualPhase().getYear());
    values[5] = this.getActualPhase().getDescription().toLowerCase();
    // Message to download the pdf
    if (!this.isPlanningActive()) {
      values[6] = config.getBaseUrl() + "/projects/" + this.getCurrentCrp().getAcronym() + "/reportingSummary.do?"
        + APConstants.PROJECT_REQUEST_ID + "=" + projectID + "&" + APConstants.PHASE_ID + "="
        + this.getActualPhase().getId();
    }


    if (this.isPlanningActive()) {
      message.append(this.getText("submit.email.message", values));
    } else {
      message.append(this.getText("submit.email.message.noPDF", values));
    }

    message.append(this.getText("email.support.noCrpAdmins"));
    // message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));

    // Send pdf
    // Get the PDF from the Project report url.
    ByteBuffer buffer = null;
    String fileName = null;
    String contentType = null;
    // Allow for Reporting when Reporting-PDF is completed
    if (this.isPlanningActive()) {
      try {
        // Set the parameters that are assigned in the prepare by reportingSummaryAction
        reportingSummaryAction.setSession(this.getSession());
        reportingSummaryAction.setSelectedYear(this.getActualPhase().getYear());
        reportingSummaryAction.setLoggedCrp(loggedCrp);
        reportingSummaryAction.setSelectedCycle(this.getActualPhase().getDescription());
        reportingSummaryAction.setProjectID(projectID);
        Project project = projectManager.getProjectById(projectID);
        Set<Submission> submissions = new HashSet<>();
        submissions.add(this.getSubmission());
        project.setSubmissions(submissions);
        reportingSummaryAction.setProject(project);
        reportingSummaryAction.setCrpSession(loggedCrp.getAcronym());
        reportingSummaryAction.setSelectedPhase(phaseManager.findCycle(reportingSummaryAction.getSelectedCycle(),
          reportingSummaryAction.getSelectedYear(), this.getActualPhase().getUpkeep(), loggedCrp.getId().longValue()));
        reportingSummaryAction.setProjectInfo(project.getProjecInfoPhase(reportingSummaryAction.getSelectedPhase()));
        reportingSummaryAction.loadProvider(this.getSession());
        reportingSummaryAction.execute();
        // Getting the file data.
        //
        buffer = ByteBuffer.wrap(reportingSummaryAction.getBytesPDF());
        fileName = this.getFileName();
        contentType = "application/pdf";
        //
      } catch (Exception e) {
        e.printStackTrace();
        // // Do nothing.
        LOG.error("There was an error trying to get the URL to download the PDF file: " + e.getMessage());
      }
    }
    if (this.validateEmailNotification()) {
      if (buffer != null && fileName != null && contentType != null) {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), buffer.array(), contentType, fileName,
          true);
      } else {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
      }
    }
  }

  public void setCycleName(String cycleName) {
    this.cycleName = cycleName;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  private void submitProject() {
    Submission submission = new Submission();

    submission.setCycle(this.getActualPhase().getDescription());
    submission.setUser(this.getCurrentUser());


    submission.setYear((short) this.getActualPhase().getYear());
    submission.setDateTime(new Date());
    submission.setProject(project);

    submission = submissionManager.saveSubmission(submission);
    this.setSubmission(submission);
    if (submission != null) {
      if (this.validateEmailNotification()) {
        this.sendNotficationEmail();
      }
    }
  }


}
