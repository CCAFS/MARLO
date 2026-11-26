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

package org.cgiar.ccafs.marlo.action.json.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome;
import org.cgiar.ccafs.marlo.data.model.PartnerRequestOutcome.Status;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.PartnerRequestEmails;
import org.cgiar.ccafs.marlo.utils.PartnerRequestEmails.Email;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RejectPartnerRequestAction:
 * 
 * @author avalencia - CCAFS
 * @date Oct 31, 2017
 * @time 10:52:21 AM: Add sendEmail boolean parameter
 * @date Nov 10, 2017
 * @time 10:09:18 AM:Inactive parent partner request
 */
public class RejectPartnerRequestAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 821788435993637711L;

  private static final Logger LOG = LoggerFactory.getLogger(RejectPartnerRequestAction.class);

  /** Status string of a rejection that was refused because the request was not an HTTP POST. */
  static final String OUTCOME_NOT_ALLOWED = "notAllowed";
  /** Status string of a rejection that ended in an exception. */
  static final String OUTCOME_FAILED = "failed";

  // Managers
  private final PartnerRequestManager partnerRequestManager;
  private final GlobalUnitManager globalUnitManager;

  // Variables
  private String requestID;
  private String justification;
  private boolean success;
  private final SendMailS sendMail;
  private boolean sendNotification;
  // Result of the rejection as one status string (design section 4, JSON contract)
  private String outcome;

  @Inject
  public RejectPartnerRequestAction(APConfig config, PartnerRequestManager partnerRequestManager,
    GlobalUnitManager globalUnitManager, SendMailS sendMail) {
    super(config);
    this.partnerRequestManager = partnerRequestManager;
    this.sendMail = sendMail;
    this.globalUnitManager = globalUnitManager;
  }

  /**
   * Rejects a pending partner request. Only a POST is accepted; the manager records the decision and closes both
   * rows in one transaction, and the requester is mailed afterwards, only when the superadmin asked for it and the
   * rejection was applied.
   */
  @Override
  public String execute() throws Exception {
    if (!this.isHttpPost()) {
      LOG.warn("rejectPartnerRequest refused for request {}: not an HTTP POST", this.loggableRequestId());
      this.success = false;
      this.outcome = OUTCOME_NOT_ALLOWED;
      return SUCCESS;
    }
    if (OUTCOME_FAILED.equals(this.outcome)) {
      // prepare() could not read the posted fields and already logged it: nothing is written.
      this.success = false;
      return SUCCESS;
    }
    PartnerRequestOutcome result;
    try {
      result = this.partnerRequestManager.rejectPartnerRequest(Long.parseLong(this.requestID), this.justification,
        this.getCurrentUser());
      if (result == null || result.getStatus() == null) {
        throw new IllegalStateException("The manager returned no outcome");
      }
    } catch (Exception e) {
      LOG.error("rejectPartnerRequest: could not reject partner request {}", this.loggableRequestId(), e);
      this.success = false;
      this.outcome = OUTCOME_FAILED;
      return SUCCESS;
    }
    this.outcome = statusName(result.getStatus());
    this.success = result.getStatus() == Status.DONE;
    if (!this.success) {
      LOG.warn("rejectPartnerRequest: request {} was not rejected: {}", this.loggableRequestId(), result.getStatus());
    } else if (this.sendNotification) {
      this.sendRejectedNotificationEmail(result);
    }
    return SUCCESS;
  }

  @Override
  public String getJustification() {
    return this.justification;
  }

  public String getOutcome() {
    return this.outcome;
  }

  public String getRequestID() {
    return this.requestID;
  }

  public boolean isSuccess() {
    return this.success;
  }

  @Override
  public void prepare() throws Exception {
    this.success = true;
    this.outcome = null;
    try {
      Map<String, Parameter> parameters = this.getParameters();
      this.justification = StringUtils.trim(parameters.get(APConstants.JUSTIFICATION_REQUEST).getMultipleValues()[0]);
      this.requestID = StringUtils.trim(parameters.get(APConstants.PARTNER_REQUEST_ID).getMultipleValues()[0]);
      this.sendNotification = Boolean.valueOf(
        StringUtils.trim(parameters.get(APConstants.PARTNER_REQUEST_SEND_NOTIFICATION).getMultipleValues()[0]));
    } catch (Exception e) {
      LOG.error("rejectPartnerRequest: could not read the posted fields of request {}", this.loggableRequestId(), e);
      this.success = false;
      this.outcome = OUTCOME_FAILED;
    }
  }

  /**
   * The posted request id for a log line: line breaks removed because the value is request input.
   */
  private String loggableRequestId() {
    return this.requestID == null ? null : this.requestID.replaceAll("[\\r\\n]", " ");
  }

  /**
   * Tells the requester that the partner was rejected, with the justification. Recipients as they have always been:
   * the requester (or the mail received from CLARISA) with the rejecting user in copy, plus the creator when the
   * request is external, and our notification mailbox in blind copy. A failure is logged and never reaches the
   * superadmin: the decision is already committed.
   */
  private void sendRejectedNotificationEmail(PartnerRequestOutcome result) {
    try {
      PartnerRequest request = result.getRequest();
      GlobalUnit unit = null;
      if (request != null && request.getCrp() != null) {
        // The unit as the database holds it: the builder reads its lazy parameters, which a detached one cannot.
        unit = this.globalUnitManager.getGlobalUnitById(request.getCrp().getId());
      }
      if (!PartnerRequestEmails.isEnabled(unit)) {
        return;
      }
      User creator = request.getCreatedBy();
      String creatorEmail = creator == null ? null : creator.getEmail();
      User rejecter = this.getCurrentUser();
      String ccEmail = rejecter == null ? null : rejecter.getEmail();
      String toEmail;
      String dearName;
      if (StringUtils.isNotBlank(request.getExternalUserMail())) {
        toEmail = request.getExternalUserMail();
        if (StringUtils.isNotBlank(creatorEmail)) {
          ccEmail = StringUtils.isBlank(ccEmail) ? creatorEmail : ccEmail + ", " + creatorEmail;
        }
        dearName = StringUtils.isNotBlank(request.getExternalUserName()) ? request.getExternalUserName()
          : (creator == null ? null : creator.getFirstName());
      } else {
        toEmail = creatorEmail;
        dearName = creator == null ? null : creator.getFirstName();
      }
      if (StringUtils.isBlank(toEmail)) {
        LOG.warn("rejectPartnerRequest: request {} was rejected but has no requester to notify",
          result.getRequestId());
        return;
      }
      Email email = PartnerRequestEmails.partnerRejected(unit, this.getBaseUrlCdn(), dearName,
        request.getPartnerName(), this.justification);
      this.sendMail.send(toEmail, ccEmail, this.config.getEmailNotification(), email.getSubject(), email.getBody(),
        null, null, null, true);
    } catch (Exception e) {
      LOG.error("rejectPartnerRequest: could not send the rejected email of partner request {}",
        result.getRequestId(), e);
    }
  }

  @Override
  public void setJustification(String justification) {
    this.justification = justification;
  }

  public void setRequestID(String requestID) {
    this.requestID = requestID;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * The status as the one string the JSON contract uses.
   */
  static String statusName(Status status) {
    switch (status) {
      case DONE:
        return "done";
      case ALREADY_HANDLED:
        return "alreadyHandled";
      case NOT_FOUND:
        return "notFound";
      case WRONG_KIND:
        return "wrongKind";
      default:
        return OUTCOME_FAILED;
    }
  }
}
