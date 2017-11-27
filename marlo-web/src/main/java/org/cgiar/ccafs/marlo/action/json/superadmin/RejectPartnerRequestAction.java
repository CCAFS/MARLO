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
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.Date;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

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


  // Managers
  private PartnerRequestManager partnerRequestManager;

  // Variables
  private String requestID;
  private String justification;
  private boolean success;
  private SendMailS sendMail;
  private boolean sendNotification;

  @Inject
  public RejectPartnerRequestAction(APConfig config, PartnerRequestManager partnerRequestManager, SendMailS sendMail) {
    super(config);
    this.partnerRequestManager = partnerRequestManager;
    this.sendMail = sendMail;
  }

  @Override
  public String execute() throws Exception {
    try {
      PartnerRequest partnerRequest = partnerRequestManager.getPartnerRequestById(Long.parseLong(requestID));
      partnerRequest.setAcepted(new Boolean(false));
      partnerRequest.setRejectJustification(justification);
      partnerRequest.setRejectedBy(this.getCurrentUser());
      partnerRequest.setRejectedDate(new Date());
      partnerRequestManager.savePartnerRequest(partnerRequest);
      partnerRequestManager.deletePartnerRequest(partnerRequest.getId());
      // inactive the parent partnerRequest
      PartnerRequest partnerRequestParent =
        partnerRequestManager.getPartnerRequestById(partnerRequest.getPartnerRequest().getId());
      partnerRequestParent.setActive(false);
      partnerRequestManager.savePartnerRequest(partnerRequestParent);
      // Send notification email
      if (sendNotification) {
        this.sendRejectedNotficationEmail(partnerRequest);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      success = false;
    }

    return SUCCESS;

  }

  @Override
  public String getJustification() {
    return justification;
  }

  public String getRequestID() {
    return requestID;
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public void prepare() throws Exception {
    success = true;
    try {
      Map<String, Object> parameters = this.getParameters();
      justification = StringUtils.trim(((String[]) parameters.get(APConstants.JUSTIFICATION_REQUEST))[0]);
      requestID = StringUtils.trim(((String[]) parameters.get(APConstants.PARTNER_REQUEST_ID))[0]);
      sendNotification = Boolean
        .valueOf(StringUtils.trim(((String[]) parameters.get(APConstants.PARTNER_REQUEST_SEND_NOTIFICATION))[0]));
    } catch (Exception e) {
      System.out.println(e.getMessage());
      success = false;
    }
  }


  private void sendRejectedNotficationEmail(PartnerRequest partnerRequest) {
    String toEmail = "";
    // ToEmail: User who requested the partner
    toEmail = partnerRequest.getCreatedBy().getEmail();
    // CC Email: User who rejected the request
    String ccEmail = this.getCurrentUser().getEmail();
    // BBC: Our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();
    // subject
    String subject =
      this.getText("marloRequestInstitution.reject.email.subject", new String[] {partnerRequest.getPartnerName()});
    // Building the email message
    StringBuilder message = new StringBuilder();
    message.append(this.getText("email.dear", new String[] {partnerRequest.getCreatedBy().getFirstName()}));
    message.append(this.getText("marloRequestInstitution.reject.email",
      new String[] {partnerRequest.getPartnerInfo(), justification}));
    message.append(this.getText("email.support.noCrpAdmins"));
    message.append(this.getText("email.bye"));
    sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
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

}
