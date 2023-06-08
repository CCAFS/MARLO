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

package org.cgiar.ccafs.marlo.action.json.global;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.EmailLogManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.model.EmailLog;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.jfree.util.Log;

public class SendNotificationEmailAction extends BaseAction {

  private static final long serialVersionUID = -6338578372277010087L;

  private EmailLogManager emailLogManager;
  private GlobalUnitManager globalUnitManager;
  private ProjectManager projectManager;
  private final SendMailS sendMail;
  private ArrayList<Map<String, String>> results;
  private ArrayList<EmailLog> emails;

  private int type;
  private String projectID;
  private String commentID;
  private String sectionName;
  private String commentText;
  private String sectionURL;
  private String parentID;
  private String username;
  private String userEmail;
  private String sentTo;
  private GlobalUnit globalUnit;
  private RoleManager roleManager;
  String contentType = "application/pdf";

  @Inject
  public SendNotificationEmailAction(APConfig config, EmailLogManager emailLogManager, SendMailS sendMail,
    GlobalUnitManager globalUnitManager, ProjectManager projectManager, RoleManager roleManager) {
    super(config);
    this.emailLogManager = emailLogManager;
    this.sendMail = sendMail;
    this.globalUnitManager = globalUnitManager;
    this.projectManager = projectManager;
    this.roleManager = roleManager;
  }


  @Override
  public String execute() throws Exception {
    results = new ArrayList<>();
    Project project = null;
    globalUnit = globalUnitManager.getGlobalUnitById(45);
    if (projectID != null) {
      project = projectManager.getProjectById(Long.parseLong(projectID));
    }

    GlobalUnitProject globalUnitProject = project.getGlobalUnitProjects().stream()
      .filter(gu -> gu.isActive() && gu.isOrigin()).collect(Collectors.toList()).get(0);

    // Send email to the user that is submitting the project.
    // TO
    String toEmail = this.getCurrentUser().getEmail();
    String ccEmail = "";


    StringBuilder ccEmails = new StringBuilder();

    // CC will be also the Management Liaison associated with the flagship(s), if is PMU only the PMU contact
    Long crpPmuRole = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));

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


    ccEmail = ccEmails.toString().isEmpty() ? null : ccEmails.toString();
    // Detect if a last ; was added to CC and remove it
    if (ccEmail != null && ccEmail.length() > 0 && ccEmail.charAt(ccEmail.length() - 2) == ',') {
      ccEmail = ccEmail.substring(0, ccEmail.length() - 2);
    }
    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    String crp = globalUnit.getAcronym() != null && !globalUnit.getAcronym().isEmpty() ? globalUnit.getAcronym()
      : globalUnit.getName();
    // subject
    String subject = null;
    subject = this.getText("submit.email.subject",
      new String[] {crp, String.valueOf(project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER))});


    // Building the email message
    StringBuilder message = new StringBuilder();
    String[] values = new String[7];
    values[0] = this.getCurrentUser().getComposedCompleteName();
    values[1] = crp;
    values[2] = project.getProjecInfoPhase(this.getActualPhase()).getTitle();
    values[3] = String.valueOf(project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER));
    values[4] = String.valueOf(this.getActualPhase().getYear());
    values[5] = this.getActualPhase().getDescription().toLowerCase();


    if (this.isPlanningActive()) {
      message.append(this.getText("submit.email.message", values));
    } else {
      message.append(this.getText("submit.email.message.noPDF", values));
    }

    // message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));

    // Send pdf
    // Get the PDF from the Project report url.
    ByteBuffer buffer = null;
    String fileName = null;
    // Allow for Reporting when Reporting-PDF is completed
    try {
      // Set the parameters that are assigned in the prepare by reportingSummaryAction

      Set<Submission> submissions = new HashSet<>();
      submissions.add(this.getSubmission());
      project.setSubmissions(submissions);

      // Getting the file data.
      //
      fileName = null;
      contentType = "application/pdf";
      //
    } catch (Exception e) {
      e.printStackTrace();
      // // Do nothing.
    }

    if (this.validateEmailNotification()) {
      if (buffer != null && fileName != null && contentType != null) {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), buffer.array(), contentType, fileName,
          true);
      } else {
        sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
      }
    }

    HashMap<String, String> map = new HashMap<>();
    map.put("id", "");
    map.put("subject", "");
    map.put("to", "");

    map.put("result", "");
    results.add(map);

    return SUCCESS;
  }


  public ArrayList<EmailLog> getEmails() {
    return emails;
  }


  public ArrayList<Map<String, String>> getResults() {
    return results;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    type = Integer.parseInt(StringUtils.trim(parameters.get("type").getMultipleValues()[0]));

    try {
      projectID = StringUtils.trim(parameters.get(APConstants.PROJECT_ID).getMultipleValues()[0]);
      commentID = StringUtils.trim(parameters.get(APConstants.COMMENT_REQUEST_ID).getMultipleValues()[0]);
      sectionName = StringUtils.trim(parameters.get(APConstants.SECTION_NAME).getMultipleValues()[0]);
      commentText = StringUtils.trim(parameters.get(APConstants.COMMENT_REQUEST).getMultipleValues()[0]);
      sectionURL = StringUtils.trim(parameters.get(APConstants.COMMENT_REQUEST).getMultipleValues()[0]);
      parentID = StringUtils.trim(parameters.get(APConstants.COMMENT_REQUEST).getMultipleValues()[0]);
      username = StringUtils.trim(parameters.get(APConstants.USER_DISABLED).getMultipleValues()[0]);
      userEmail = StringUtils.trim(parameters.get(APConstants.USER_EMAIL).getMultipleValues()[0]);
      sentTo = StringUtils.trim(parameters.get(APConstants.OUTLOOK_EMAIL).getMultipleValues()[0]);
    } catch (Exception e) {
      projectID = null;
      Log.info(e);
    }
  }


  public void setEmails(ArrayList<EmailLog> emails) {
    this.emails = emails;
  }

  public void setResults(ArrayList<Map<String, String>> results) {
    this.results = results;
  }


  private boolean validateEmailNotification() {

    return globalUnit.getCustomParameters().stream()
      .filter(c -> c.getParameter().getKey().equalsIgnoreCase(APConstants.CRP_EMAIL_NOTIFICATIONS))
      .allMatch(t -> (t.getValue() == null) ? true : t.getValue().equalsIgnoreCase("true"));
  }


}
