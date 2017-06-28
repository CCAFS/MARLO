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
import org.cgiar.ccafs.marlo.action.summaries.ImpactSubmissionSummaryAction;
import org.cgiar.ccafs.marlo.config.APConfig;
import org.cgiar.ccafs.marlo.data.model.ImpactPathwayCyclesEnum;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterCycle;
import org.cgiar.ccafs.marlo.data.model.CenterLeader;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterSubmission;
import org.cgiar.ccafs.marlo.data.service.ICenterService;
import org.cgiar.ccafs.marlo.data.service.ICenterProgramService;
import org.cgiar.ccafs.marlo.data.service.ICenterCycleService;
import org.cgiar.ccafs.marlo.data.service.ICenterSubmissionService;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConstants;
import org.cgiar.ccafs.marlo.utils.SendMail;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class IPSubmissionAction extends BaseAction {


  private static final long serialVersionUID = 4882851743044518890L;


  // LOG
  private static Logger LOG = LoggerFactory.getLogger(IPSubmissionAction.class);
  private ICenterSubmissionService submissionService;
  private ICenterProgramService programService;
  private ICenterCycleService cycleService;
  private ICenterService centerService;


  private SendMail sendMail;
  private CenterProgram program;
  private CenterCycle cycle;

  private Center loggedCenter;
  private long programID;
  private boolean isSubmited = false;
  @Inject
  ImpactSubmissionSummaryAction impactSubmissionSummaryAction;

  @Inject
  public IPSubmissionAction(APConfig config, ICenterSubmissionService submissionService, ICenterProgramService programService,
    ICenterCycleService cycleService, ICenterService centerService, SendMail sendMail) {
    super(config);
    this.programService = programService;
    this.submissionService = submissionService;
    this.cycleService = cycleService;
    this.centerService = centerService;
    this.sendMail = sendMail;
  }

  @Override
  public String execute() throws Exception {
    if (this.hasPermission("*")) {
      if (this.isCompleteIP(programID)) {
        if (submissionService.findAll() != null) {
          CenterProgram program = programService.getProgramById(programID);

          List<CenterSubmission> submissions = new ArrayList<>(program.getSubmissions().stream()
            .filter(s -> s.getResearchCycle().equals(cycle) && s.getYear().intValue() == this.getYear())
            .collect(Collectors.toList()));

          if (submissions != null && submissions.size() > 0) {
            this.setSubmission(submissions.get(0));
            isSubmited = true;
          }
        }

        if (!isSubmited) {

          CenterSubmission submission = new CenterSubmission();
          submission.setResearchProgram(program);
          submission.setDateTime(new Date());
          submission.setUser(this.getCurrentUser());
          submission.setYear((short) this.getYear());
          submission.setResearchCycle(cycle);

          long submissionId = submissionService.saveSubmission(submission);

          if (submissionId > 0) {
            this.setSubmission(submissionService.getSubmissionById(submissionId));
            this.sendNotficationEmail();
          }

        }
      }
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }

  }

  public String getFileName() {
    StringBuffer fileName = new StringBuffer();
    fileName.append("ImpactPathway-");
    fileName.append(loggedCenter.getName() + "-");
    fileName.append("IP" + programID + "-");
    fileName.append(new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()));
    fileName.append(".pdf");
    return fileName.toString();

  }

  // public boolean isCompleteIP(long programId) {
  //
  // if (sectionStatusService.findAll() == null) {
  // return false;
  // }
  //
  // CenterProgram researchProgram = programService.getProgramById(programId);
  //
  // List<CenterSectionStatus> sectionStatuses = new ArrayList<>(researchProgram.getSectionStatuses().stream()
  // .filter(ss -> ss.getYear() == (short) this.getYear()).collect(Collectors.toList()));
  //
  // if (sectionStatuses != null && sectionStatuses.size() > 0) {
  // for (CenterSectionStatus sectionStatus : sectionStatuses) {
  // if (sectionStatus.getMissingFields().length() > 0) {
  // return false;
  // }
  // }
  // } else {
  // return false;
  // }
  // return true;
  // }

  public long getProgramID() {
    return programID;
  }

  @Override
  public void prepare() throws Exception {

    loggedCenter = (Center) this.getSession().get(APConstants.SESSION_CENTER);
    loggedCenter = centerService.getCrpById(loggedCenter.getId());

    try {
      programID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CENTER_PROGRAM_ID)));
    } catch (NumberFormatException e) {
      programID = -1;
      return; // Stop here and go to execute method.
    }

    program = programService.getProgramById(programID);

    String params[] = {loggedCenter.getAcronym(), program.getResearchArea().getId() + "", program.getId() + ""};
    this.setBasePermission(this.getText(Permission.RESEARCH_PROGRAM_BASE_PERMISSION, params));

    cycle = cycleService.getResearchCycleById(ImpactPathwayCyclesEnum.IMPACT_PATHWAY.getId());

  }

  private void sendNotficationEmail() {
    // Building the email message
    StringBuilder message = new StringBuilder();
    String[] values = new String[5];
    values[0] = this.getCurrentUser().getComposedCompleteName();
    values[1] = loggedCenter.getName();
    values[2] = program.getName();
    values[3] = String.valueOf(this.getYear());
    values[4] = cycle.getName();

    String subject = null;
    message.append(this.getText("impact.submit.email.message", values));
    message.append(this.getText("email.support"));
    message.append(this.getText("email.bye"));
    subject = this.getText("impact.submit.email.subject", new String[] {program.getName()});


    String toEmail = null;
    String ccEmail = null;

    // Send email to the user that is submitting the project.
    // TO
    toEmail = this.getCurrentUser().getEmail();
    StringBuilder ccEmails = new StringBuilder();

    // CC
    CenterArea area = program.getResearchArea();

    List<CenterLeader> areaLeaders =
      new ArrayList<>(area.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList()));


    if (!areaLeaders.isEmpty()) {
      for (CenterLeader leader : areaLeaders) {
        ccEmails.append(leader.getUser().getEmail());
        ccEmails.append(", ");
      }
    }

    List<CenterLeader> programLeaders =
      new ArrayList<>(program.getResearchLeaders().stream().filter(rl -> rl.isActive()).collect(Collectors.toList()));

    if (!programLeaders.isEmpty()) {
      for (CenterLeader leader : programLeaders) {
        ccEmails.append(leader.getUser().getEmail());
        ccEmails.append(", ");
      }
    }

    // CC will be the other MLs.
    ccEmail = ccEmails.toString().isEmpty() ? null : ccEmails.toString();
    // Detect if a last ; was added to CC and remove it
    if (ccEmail != null && ccEmail.length() > 0 && ccEmail.charAt(ccEmail.length() - 2) == ',') {
      ccEmail = ccEmail.substring(0, ccEmail.length() - 2);
    }


    // BBC will be our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    // Send pdf
    // Get the PDF from the CenterProject report url.
    ByteBuffer buffer = null;
    String fileName = null;
    String contentType = null;
    try {
      CenterProgram program = programService.getProgramById(programID);
      impactSubmissionSummaryAction.setResearchProgram(program);
      impactSubmissionSummaryAction.execute();

      // Getting the file data.
      buffer = ByteBuffer.wrap(impactSubmissionSummaryAction.getBytesPDF());
      fileName = this.getFileName();
      contentType = "application/pdf";
    } catch (Exception e) {
      // Do nothing.
      LOG.error("There was an error trying to get the URL to download the PDF file: " + e.getMessage());
    }

    if (buffer != null && fileName != null && contentType != null) {
      sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), buffer.array(), contentType, fileName,
        true);
    } else {
      sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }

  }

  public void setProgramID(long programID) {
    this.programID = programID;
  }

}
