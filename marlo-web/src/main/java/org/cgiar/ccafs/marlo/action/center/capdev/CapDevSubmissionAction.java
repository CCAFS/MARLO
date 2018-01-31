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

package org.cgiar.ccafs.marlo.action.center.capdev;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.manager.ICenterSubmissionManager;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CenterSubmission;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CapDevSubmissionAction extends BaseAction {


  private static final long serialVersionUID = 381947719159951991L;


  /// LOG
  private static Logger LOG = LoggerFactory.getLogger(CapDevSubmissionAction.class);

  private ICenterSubmissionManager submissionService;


  private ICapacityDevelopmentService capdevService;


  private GlobalUnitManager centerService;
  private GlobalUnit loggedCenter;

  private CapacityDevelopment capacityDevelopment;

  private long capDevID;
  private boolean isSubmited = false;

  @Inject
  public CapDevSubmissionAction(APConfig config, ICenterSubmissionManager submissionService,
    GlobalUnitManager centerService, ICapacityDevelopmentService capdevService) {
    super(config);
    this.submissionService = submissionService;
    this.centerService = centerService;
    this.capdevService = capdevService;
  }

  @Override
  public String execute() throws Exception {
    if (this.hasPermission("*")) {
      if (this.isCompleteCapDev(capDevID)) {
        if (submissionService.findAll() != null) {
          CapacityDevelopment capDev = capdevService.getCapacityDevelopmentById(capDevID);

          List<CenterSubmission> submissions = new ArrayList<>(capDev.getSubmissions().stream()
            .filter(s -> s.getYear().intValue() == this.getCenterYear()).collect(Collectors.toList()));

          if (submissions != null && submissions.size() > 0) {
            this.setCenterSubmission(submissions.get(0));
            isSubmited = true;
          }
        }

        if (!isSubmited) {

          CenterSubmission submission = new CenterSubmission();
          submission.setCapacityDevelopment(capacityDevelopment);
          submission.setDateTime(new Date());
          submission.setUser(this.getCurrentUser());
          submission.setYear((short) this.getCenterYear());
          // submission.setResearchCycle(cycle);

          submission = submissionService.saveSubmission(submission);

          if (submission.getId() > 0) {
            this.setCenterSubmission(submission);
          }

        }
      }
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }

  public long getCapDevID() {
    return capDevID;
  }

  public GlobalUnit getLoggedCenter() {
    return loggedCenter;
  }


  @Override
  public void prepare() throws Exception {
    loggedCenter = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCenter = centerService.getGlobalUnitById(loggedCenter.getId());

    try {
      capDevID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CAPDEV_ID)));
    } catch (NumberFormatException e) {
      capDevID = -1;
      return; // Stop here and go to execute method.
    }

    capacityDevelopment = capdevService.getCapacityDevelopmentById(capDevID);

    // TODO create base permission
  }


  public void setCapDevID(long capDevID) {
    this.capDevID = capDevID;
  }

  public void setLoggedCenter(GlobalUnit loggedCenter) {
    this.loggedCenter = loggedCenter;
  }


}
