/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/


package org.cgiar.ccafs.marlo.action.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.SubmissionManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.data.model.Submission;
import org.cgiar.ccafs.marlo.utils.APConfig;

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


  private CrpProgramManager crpProgramManager;

  private SectionStatusManager sectionStatusManager;

  private SubmissionManager submissionManager;

  private List<SectionStatus> sectionStatus;
  private CrpProgram crpProgram;

  @Inject
  public ImpactSubmissionAction(APConfig config, CrpProgramManager crpProgramManager,
    SectionStatusManager sectionStatusManager, SubmissionManager submissionManager) {
    super(config);
    this.crpProgramManager = crpProgramManager;
    this.sectionStatusManager = sectionStatusManager;
    this.submissionManager = submissionManager;

  }

  @Override
  public String execute() throws Exception {
    // Check if user has permissions to submit the project.
    if (this.isCompleteImpact(progamID)) {
      List<Submission> submissions = submissionManager.findAll();
      if (submissions != null) {
        submissions =
          submissions.stream().filter(c -> c.getCrpProgram().equals(crpProgram)).collect(Collectors.toList());
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

        submissionManager.saveSubmission(submission);
      }
    }
    return INPUT;

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

    // Initializing Section Statuses:
    sectionStatus = sectionStatusManager.findAll().stream().filter(c -> c.getCrpProgram().equals(crpProgram))
      .collect(Collectors.toList());
  }


}
