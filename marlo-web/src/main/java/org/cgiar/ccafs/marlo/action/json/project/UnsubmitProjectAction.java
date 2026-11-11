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
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterActivityLeader;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramLeader;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.data.model.UserRole;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.EmailLayout;
import org.cgiar.ccafs.marlo.utils.SendMailS;
import org.cgiar.ccafs.marlo.utils.UnitTextProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.opensymphony.xwork2.TextProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class UnsubmitProjectAction extends BaseAction {


  private static final long serialVersionUID = 6328194359119346721L;
  private static final Logger LOG = LoggerFactory.getLogger(UnsubmitProjectAction.class);


  private final ProjectManager projectManager;

  private final SubmissionManager submissionManager;

  private final RoleManager roleManager;
  private final GlobalUnitManager globalUnitManager;

  private final SendMailS sendMail;
  private final CustomParameterManager customParameterManager;

  private long projectID;
  private String justification;

  private List<Map<String, Object>> message;

  @Inject
  public UnsubmitProjectAction(APConfig config, ProjectManager projectManager, SubmissionManager submissionManager,
    SendMailS sendMail, RoleManager roleManager, GlobalUnitManager globalUnitManager,
    CustomParameterManager customParameterManager) {
    super(config);
    this.projectManager = projectManager;
    this.submissionManager = submissionManager;
    this.roleManager = roleManager;
    this.sendMail = sendMail;
    this.globalUnitManager = globalUnitManager;
    this.customParameterManager = customParameterManager;
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

    // Get The Crp/Center/Platform where the project was created
    GlobalUnitProject globalUnitProject = project.getGlobalUnitProjects().stream()
      .filter(gu -> gu.isActive() && gu.isOrigin()).collect(Collectors.toList()).get(0);

    ProjectInfo projectInfo = project.getProjecInfoPhase(this.getActualPhase());

    String toEmail = "";
    // Add project leader
    if (project.getLeaderPerson(this.getActualPhase()) != null
      && project.getLeaderPerson(this.getActualPhase()).getUser().getId() != this.getCurrentUser().getId()) {
      toEmail = project.getLeaderPerson(this.getActualPhase()).getUser().getEmail();
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

    
//    // Old method CC Emails
//    String ccEmail = "";
//    StringBuilder ccEmails = new StringBuilder();
//
//    ccEmails.append(this.getCurrentUser().getEmail());
//    ccEmails.append(", ");
//
//    // CC will be also the Management Liaison associated with the flagship(s), if is PMU only the PMU contact
//    Long crpPmuRole = Long.parseLong((String) this.getSession().get(APConstants.CRP_PMU_ROLE));
//    Role roleCrpPmu = roleManager.getRoleById(crpPmuRole);
//    // If Managment liason is PMU
//    if (projectInfo.getLiaisonInstitution() != null && projectInfo.getLiaisonInstitution().getAcronym() != null
//      && roleCrpPmu.getAcronym() != null
//      && projectInfo.getLiaisonInstitution().getAcronym().equals(roleCrpPmu.getAcronym())) {
//      // Deleted managment liason contact person
//    } else if (projectInfo.getLiaisonInstitution() != null
//      && projectInfo.getLiaisonInstitution().getCrpProgram() != null
//      && projectInfo.getLiaisonInstitution().getCrpProgram().getProgramType() == 1) {
//      // If Managment liason is FL
//      List<CrpProgram> crpPrograms = globalUnitProject
//        .getGlobalUnit().getCrpPrograms().stream().filter(cp -> cp.getId() == project
//          .getProjecInfoPhase(this.getActualPhase()).getLiaisonInstitution().getCrpProgram().getId())
//        .collect(Collectors.toList());
//      if (crpPrograms != null) {
//
//        CrpProgram crpProgram = crpPrograms.get(0);
//        for (CrpProgramLeader crpProgramLeader : crpProgram.getCrpProgramLeaders().stream()
//          .filter(cpl -> cpl.getUser().isActive() && cpl.isActive()).collect(Collectors.toList())) {
//          ccEmails.append(crpProgramLeader.getUser().getEmail());
//          ccEmails.append(", ");
//        }
//
//        if (this.hasSpecificities(APConstants.CRP_EMAIL_CC_FL_FM_CL)) {
//          // CC will be also other Cluster Leaders
//          for (CrpClusterOfActivity crpClusterOfActivity : crpProgram.getCrpClusterOfActivities().stream()
//            .filter(cl -> cl.isActive() && cl.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
//            for (CrpClusterActivityLeader crpClusterActivityLeader : crpClusterOfActivity.getCrpClusterActivityLeaders()
//              .stream().filter(cl -> cl.isActive()).collect(Collectors.toList())) {
//              ccEmails.append(crpClusterActivityLeader.getUser().getEmail());
//              ccEmails.append(", ");
//            }
//          }
//        }
//      }
//    }
//
//    // Add project coordinator(s)
//    for (ProjectPartnerPerson projectPartnerPerson : project.getCoordinatorPersons(this.getActualPhase())) {
//      if (projectPartnerPerson.getUser().getId() != this.getCurrentUser().getId()) {
//        ccEmails.append(projectPartnerPerson.getUser().getEmail());
//        ccEmails.append(", ");
//      }
//    }
//
//    // CC will be also the CRP Admins
//    String crpAdmins = "";
//    long adminRol = Long.parseLong((String) this.getSession().get(APConstants.CRP_ADMIN_ROLE));
//    Role roleAdmin = roleManager.getRoleById(adminRol);
//    List<UserRole> userRoles = roleAdmin.getUserRoles().stream()
//      .filter(ur -> ur.getUser() != null && ur.getUser().isActive()).collect(Collectors.toList());
//    for (UserRole userRole : userRoles) {
//      ccEmails.append(userRole.getUser().getEmail());
//      ccEmails.append(", ");
//      if (crpAdmins.isEmpty()) {
//        crpAdmins += userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
//      } else {
//        crpAdmins += ", " + userRole.getUser().getComposedCompleteName() + " (" + userRole.getUser().getEmail() + ")";
//      }
//    }
//
//    ccEmail = ccEmails.toString().isEmpty() ? null : ccEmails.toString();
//    // Detect if a last ; was added to CC and remove it
//    if (ccEmail != null && ccEmail.length() > 0 && ccEmail.charAt(ccEmail.length() - 2) == ',') {
//      ccEmail = ccEmail.substring(0, ccEmail.length() - 2);
//    }

    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    String crp = globalUnitProject.getGlobalUnit().getAcronym() != null
      && !globalUnitProject.getGlobalUnit().getAcronym().isEmpty() ? globalUnitProject.getGlobalUnit().getAcronym()
        : globalUnitProject.getGlobalUnit().getName();
    // subject
    String projectAcronym = null;
    if (project != null && project.getAcronym() != null) {
      projectAcronym = project.getAcronym();
    } else {
      projectAcronym = "C" + project.getId();
    }
    TextProvider texts = this.unitTexts(globalUnitProject.getGlobalUnit());

    // Building the email message
    String[] values = new String[8];
    String plName = "";
    if (project.getLeaderPerson(this.getActualPhase()) != null
      && project.getLeaderPerson(this.getActualPhase()).getUser().getId() != this.getCurrentUser().getId()) {
      plName = project.getLeaderPerson(this.getActualPhase()).getUser().getFirstName();
    }
    values[0] = plName;
    values[1] = this.getCurrentUser().getFirstName();
    values[2] = crp;
    values[3] = projectInfo.getTitle();

    values[4] = String.valueOf(project.getStandardIdentifier(Project.EMAIL_SUBJECT_IDENTIFIER));
    values[5] = String.valueOf(this.getCurrentCycleYear());
    values[6] = this.getCurrentCycle().toLowerCase();
    values[7] = justification;

    UnsubmitEmail email = assembleEmail(texts, this.getBaseUrlCdn(), crp, projectAcronym, values);

    GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(globalUnitProject.getGlobalUnit().getId());
    if (this.validateEmailNotification(globalUnit)) {
      sendMail.send(toEmail, ccEmail, bbcEmails, email.subject, email.body, null, null, null, true);
    }
  }

  /** The assembled unsubmit email: its subject and its body. */
  static final class UnsubmitEmail {

    final String subject;
    final String body;

    UnsubmitEmail(String subject, String body) {
      this.subject = subject;
      this.body = body;
    }
  }

  /**
   * Assembles the subject and the body of the unsubmit email with the texts of one Global Unit: the unit's copy, the
   * role footer once, and the whole body inside the shell.
   */
  // @akili-spec changes/email-layout-default
  static UnsubmitEmail assembleEmail(TextProvider texts, String assetsUrl, String crp, String projectAcronym,
    String[] values) {
    String subject = texts.getText("unsubmit.email.subject", new String[] {crp, projectAcronym});
    StringBuilder message = new StringBuilder();
    message.append(texts.getText("unsubmit.email.message", values));
    message.append(texts.getText("email.role.support.noCrpAdmins"));
    message.append(texts.getText("email.role.getStarted"));
    message.append(texts.getText("email.role.bye"));
    return new UnsubmitEmail(subject, EmailLayout.wrap(texts, assetsUrl, message.toString()));
  }

  /**
   * The texts of the Global Unit the project belongs to, read from its own custom file and not from the shared
   * provider, which holds whichever unit the server served last. A failure is logged and answered with this action's
   * provider.
   */
  private TextProvider unitTexts(GlobalUnit unit) {
    try {
      CustomParameter parameter = customParameterManager
        .getCustomParameterByParameterKeyAndGlobalUnitId(APConstants.CRP_CUSTOM_FILE, unit.getId().longValue());
      return UnitTextProvider.forCustomFile(parameter == null ? null : parameter.getValue());
    } catch (RuntimeException e) {
      LOG.warn("The texts of the unit owning the project could not be read, the unsubmit email uses the default texts",
        e);
      return this;
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
