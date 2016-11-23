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
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMail;
import org.cgiar.ccafs.marlo.utils.URLFileDownloader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author Andrés Valencia - CIAT/CCAFS
 */
public class ProjectSubmissionAction extends BaseAction {


  private static final long serialVersionUID = 4635459226337562141L;
  private static Logger LOG = LoggerFactory.getLogger(ProjectSubmissionAction.class);

  // Manager
  private SubmissionManager submissionManager;
  private ProjectManager projectManager;
  private CrpManager crpManager;
  private SendMail sendMail;
  private LiaisonUserManager liasonUserManager;
  private Crp loggedCrp;
  private String cycleName;


  private boolean complete;


  private long projectID;

  private Project project;


  @Inject
  public ProjectSubmissionAction(APConfig config, SubmissionManager submissionManager, ProjectManager projectManager,
    CrpManager crpManager, SendMail sendMail, LiaisonUserManager liasonUserManager) {
    super(config);
    this.submissionManager = submissionManager;
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.sendMail = sendMail;
    this.liasonUserManager = liasonUserManager;
  }

  @Override
  public String execute() throws Exception {
    complete = false;
    if (this.hasPermission("submitProject")) {
      if (this.isCompleteProject(projectID)) {
        List<Submission> submissions = project.getSubmissions().stream()
          .filter(
            c -> c.getCycle().equals(APConstants.PLANNING) && c.getYear().intValue() == this.getCurrentCycleYear())
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
  public boolean isCompleteProject(long projectID) {

    Project project = projectManager.getProjectById(projectID);
    List<SectionStatus> sections = project.getSectionStatuses().stream().collect(Collectors.toList());

    for (SectionStatus sectionStatus : sections) {
      if (sectionStatus.getMissingFields().length() > 0) {
        return false;
      }
    }
    if (sections.size() == 0) {
      return false;
    }

    HashSet<String> sectionsString = new HashSet<>();
    for (SectionStatus sectionStatus : sections) {
      sectionsString.add(sectionStatus.getSectionName());
    }
    if (!sectionsString.contains(ProjectSectionStatusEnum.BUDGETBYCOA)) {
      if (sectionsString.size() < 7) {
        return false;
      }
    } else {
      if (sectionsString.size() < 8) {
        return false;
      }
    }

    return true;
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
    // Building the email message
    StringBuilder message = new StringBuilder();
    String[] values = new String[4];
    values[0] = this.getCurrentUser().getComposedCompleteName();
    values[1] = loggedCrp.getName();
    values[2] = project.getTitle();

    String subject = null;
    values[3] = String.valueOf(this.getCurrentCycleYear());
    message.append(this.getText("submit.email.message", values));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));
    subject = this.getText("submit.email.subject", new String[] {loggedCrp.getName(),
      String.valueOf(project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER))});


    String toEmail = null;
    String ccEmail = null;

    if (config.isProduction()) {
      // Send email to the user that is submitting the project.
      // TO
      toEmail = this.getCurrentUser().getEmail();

      // Get MLs associated to the Project Liaison institution
      List<LiaisonUser> liasonUsers =
        liasonUserManager.getLiasonUsersByInstitutionId(project.getLiaisonInstitution().getId());

      StringBuilder ccEmails = new StringBuilder();

      for (LiaisonUser liasonUser : liasonUsers) {
        // Verify currentUser for avoid duplicate addition
        if (liasonUser.getUser().getId() != this.getCurrentUser().getId()) {
          ccEmails.append(liasonUser.getUser().getEmail());
          ccEmails.append(" ");
        }
      }

      // Add project leader
      if (project.getLeaderPerson() != null
        && project.getLeaderPerson().getUser().getId() != this.getCurrentUser().getId()) {
        ccEmails.append(project.getLeaderPerson().getUser().getEmail());
        ccEmails.append(" ");
      }
      // Add project coordinator(s)
      for (ProjectPartnerPerson projectPartnerPerson : project.getCoordinatorPersons()) {
        if (projectPartnerPerson.getUser().getId() != this.getCurrentUser().getId()) {
          ccEmails.append(projectPartnerPerson.getUser().getEmail());
          ccEmails.append(" ");
        }
      }

      // CC will be the other MLs.
      ccEmail = ccEmails.toString().isEmpty() ? null : ccEmails.toString();
    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    // Send pdf
    // Get the PDF from the Project report url.
    ByteBuffer buffer = null;
    String fileName = null;
    String contentType = null;

    try {
      // Making the URL to get the report.

      // URL pdfURL = new URL("https://localhost:8080/marlo-web/reportingSummary.do?projectID=21");
      URL pdfURL = new URL(config.getBaseUrl() + "/projects/reportingSummary.do?" + APConstants.PROJECT_REQUEST_ID + "="
        + projectID + "&" + APConstants.YEAR_REQUEST + "=" + this.getCurrentCycleYear() + "&" + APConstants.CYCLE + "="
        + this.getCurrentCycle());

      // Getting the file data.
      Map<String, Object> fileProperties = URLFileDownloader.getAsByteArray(pdfURL);
      buffer = fileProperties.get("byte_array") != null ? (ByteBuffer) fileProperties.get("byte_array") : null;
      fileName = fileProperties.get("filename") != null ? (String) fileProperties.get("filename") : null;
      contentType = fileProperties.get("mime_type") != null ? (String) fileProperties.get("mime_type") : null;
    } catch (MalformedURLException e) {
      // Do nothing.
      LOG.error("There was an error trying to get the URL to download the PDF file: " + e.getMessage());
    } catch (IOException e) {
      // Do nothing
      LOG.error(
        "There was a problem trying to download the PDF file for the projectID=" + projectID + " : " + e.getMessage());
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

    submission.setCycle(APConstants.PLANNING);
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
