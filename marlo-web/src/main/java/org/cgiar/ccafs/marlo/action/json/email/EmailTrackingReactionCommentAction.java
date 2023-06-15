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

package org.cgiar.ccafs.marlo.action.json.email;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailTrackingReactionCommentAction extends BaseAction {

  private static Logger LOG = LoggerFactory.getLogger(EmailTrackingReactionCommentAction.class);

  private static final long serialVersionUID = 6328194359119346721L;
  private final ProjectManager projectManager;
  private final RoleManager roleManager;
  private final GlobalUnitManager globalUnitManager;
  private final SendMailS sendMail;
  private ProjectOutcomeManager projectOutcomeManager;
  private CrpProgramManager crpProgramManager;

  private long projectID;
  private String assesorName;
  private String assesorEmail;
  private String assesorInput;
  private String sectionName;
  private String feedbackCommentReaction;
  private String feedbackReplayUsername;
  private long sectionID;
  private String feedbackResponse;
  private String sectionLink;

  private List<Map<String, Object>> message;

  @Inject
  public EmailTrackingReactionCommentAction(APConfig config, ProjectManager projectManager, SendMailS sendMail,
    RoleManager roleManager, GlobalUnitManager globalUnitManager, ProjectOutcomeManager projectOutcomeManager,
    CrpProgramManager crpProgramManager) {
    super(config);
    this.projectManager = projectManager;
    this.roleManager = roleManager;
    this.sendMail = sendMail;
    this.globalUnitManager = globalUnitManager;
    this.projectOutcomeManager = projectOutcomeManager;
    this.crpProgramManager = crpProgramManager;
  }

  @Override
  public String execute() throws Exception {
    message = new ArrayList<>();
    Map<String, Object> mStatus = new HashMap<>();
    mStatus.put("status", "ok");
    message.add(mStatus);

    Project project = projectManager.getProjectById(projectID);

    this.sendNotficationEmail(project);
    return SUCCESS;
  }


  public List<Map<String, Object>> getMessage() {
    return message;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    projectID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PROJECT_REQUEST_ID).getMultipleValues()[0]));
    assesorName = StringUtils.trim(parameters.get(APConstants.FEEDBACK_ASSESOR_NAME).getMultipleValues()[0]);
    assesorInput = StringUtils.trim(parameters.get(APConstants.FEEDBACK_ASSESOR_INPUT).getMultipleValues()[0]);
    assesorEmail = StringUtils.trim(parameters.get(APConstants.FEEDBACK_ASSESOR_EMAIL).getMultipleValues()[0]);
    sectionName = StringUtils.trim(parameters.get(APConstants.SECTION_NAME).getMultipleValues()[0]);
    feedbackCommentReaction =
      StringUtils.trim(parameters.get(APConstants.FEEDBACK_COMMENT_REACTION).getMultipleValues()[0]);
    sectionID = Long.parseLong(StringUtils.trim(parameters.get(APConstants.SECTION_ID).getMultipleValues()[0]));
    feedbackReplayUsername =
      StringUtils.trim(parameters.get(APConstants.FEEDBACK_REPLAY_USERNAME).getMultipleValues()[0]);
    try {
      feedbackResponse = StringUtils.trim(parameters.get(APConstants.FEEDBACK_RESPONSE).getMultipleValues()[0]);
    } catch (Exception e) {
      LOG.error("error getting feedback response " + e);
    }
  }

  private void sendNotficationEmail(Project project) {
    Phase currentPhase = this.getActualPhase();

    // Get The Crp/Center/Platform where the project was created
    GlobalUnitProject globalUnitProject = project.getGlobalUnitProjects().stream()
      .filter(gu -> gu.isActive() && gu.isOrigin()).collect(Collectors.toList()).get(0);

    ProjectInfo projectInfo = project.getProjecInfoPhase(this.getActualPhase());

    String toEmail = "";
    // Add project leader
    String leader = null;
    if (project.getLeaderPerson(this.getActualPhase()) != null
      && project.getLeaderPerson(this.getActualPhase()).getUser().getId() != this.getCurrentUser().getId()) {
      leader = project.getLeaderPerson(this.getActualPhase()).getUser().getEmail();
    }
    if (assesorEmail != null && this.isValidEmail(assesorEmail)) {
      toEmail = assesorEmail;
    } else {
      toEmail = null;
    }

    // CC Emails
    String ccEmail = "";
    StringBuilder ccEmails = new StringBuilder();

    ProjectOutcome projectOutcome = null;
    try {
      projectOutcome = projectOutcomeManager.getProjectOutcomeById(sectionID);
    } catch (Exception e) {
      LOG.error("error getting project outcome " + e);
    }

    if (projectOutcome != null && projectOutcome.getCrpProgramOutcome() != null
      && projectOutcome.getCrpProgramOutcome().getCrpProgram() != null) {
      long crpProgramID = projectOutcome.getCrpProgramOutcome().getCrpProgram().getId();
      CrpProgram crpProgram = this.crpProgramManager.getCrpProgramById(crpProgramID);
      // CC will be also the others FL already assigned to the Flagship
      for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
        .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
        if (ccEmail.isEmpty()) {
          ccEmail += crpProgramLeader.getUser().getEmail();
        } else {
          ccEmail += ", " + crpProgramLeader.getUser().getEmail();
        }
      }
    }

    ccEmail = ccEmails.toString().isEmpty() ? null : ccEmails.toString();
    // Detect if a last ; was added to CC and remove it
    if (ccEmail != null && ccEmail.length() > 0 && ccEmail.charAt(ccEmail.length() - 2) == ',') {
      ccEmail = ccEmail.substring(0, ccEmail.length() - 2);
    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    String acronym = null;
    if (project.getAcronym() != null) {
      acronym = "[" + project.getAcronym() + "]";
    }
    if (sectionName != null) {
      sectionName = "[" + sectionName + "]";
    }

    if (sectionName != null && !sectionName.isEmpty()) {

      switch (sectionName) {
        case "projectContributionCrp":
          sectionLink = this.getBaseUrl() + "/clusters/" + this.getCurrentCrp().getAcronym() + "/contributionCrp.do?"
            + "projectOutcomeID=" + sectionID + "&phaseID=" + currentPhase.getId() + "&edit=true";
          break;
        case "deliverable":
          sectionLink = this.getBaseUrl() + "/clusters/" + this.getCurrentCrp().getAcronym() + "/deliverable.do?"
            + "deliverableID=" + sectionID + "&phaseID=" + currentPhase.getId() + "&edit=true";
          break;
        case "study":
          sectionLink = this.getBaseUrl() + "/clusters/" + this.getCurrentCrp().getAcronym() + "/study.do?"
            + "expectedID=" + sectionID + "&phaseID=" + currentPhase.getId() + "&edit=true";
          break;
        case "innovation":
          sectionLink = this.getBaseUrl() + "/clusters/" + this.getCurrentCrp().getAcronym() + "/innovation.do?"
            + "innovationID=" + sectionID + "&phaseID=" + currentPhase.getId() + "&edit=true";
          break;
      }

    }

    String subject = this.getText("email.tracking.comment.reaction.subject", new String[] {acronym, sectionName});
    // Building the email message
    StringBuilder message = new StringBuilder();
    String[] values = new String[9];

    values[0] = assesorName;
    values[1] = sectionName;
    values[2] = projectID + "";
    values[3] = acronym;
    values[4] = assesorInput;
    values[5] = feedbackReplayUsername;
    values[6] = feedbackCommentReaction;
    values[7] = feedbackResponse;
    values[8] = sectionLink;

    message.append(this.getText("email.tracking.comment.reaction.body", values));
    message.append(this.getText("email.support.noCrpAdmins"));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));

    GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(globalUnitProject.getGlobalUnit().getId());
    if (this.validateEmailNotification(globalUnit)) {
      sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }
  }

  public void setMessage(List<Map<String, Object>> message) {
    this.message = message;
  }

  private boolean validateEmailNotification(GlobalUnit globalUnit) {
    Boolean crpNotification = globalUnit.getCustomParameters().stream()
      .filter(c -> c.getParameter().getKey().equalsIgnoreCase(APConstants.CRP_EMAIL_NOTIFICATIONS))
      .allMatch(t -> (t.getValue() == null) ? true : t.getValue().equalsIgnoreCase("true"));
    return crpNotification;
  }


}
