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


package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.PartnerRequestEmails;
import org.cgiar.ccafs.marlo.utils.PartnerRequestEmails.Email;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RequestCountryOfficeAction:
 * 
 * @author avalencia - CCAFS
 * @date Oct 30, 2017
 * @time 11:43:44 AM: Add CRP
 */
public class RequestCountryOfficeAction extends BaseAction {

  private static final Logger LOG = LoggerFactory.getLogger(RequestCountryOfficeAction.class);


  /**
   * 
   */
  private static final long serialVersionUID = 4363664661595086316L;

  private Map<String, Object> sucess;

  // Variables
  private Long projectID;
  private Long institutionID;
  private String[] countries;
  private GlobalUnit loggedCrp;

  private final SendMailS sendMail;

  // Managers
  private InstitutionManager institutionManager;
  private ProjectManager projectManager;
  private LocElementManager locElementManager;
  private PartnerRequestManager partnerRequestManager;
  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  private boolean messageSent;

  @Inject
  public RequestCountryOfficeAction(APConfig config, InstitutionManager institutionManager,
    LocElementManager locElementManager, ProjectManager projectManager, PartnerRequestManager partnerRequestManager,
    GlobalUnitManager crpManager, SendMailS sendMail) {
    super(config);
    this.institutionManager = institutionManager;
    this.locElementManager = locElementManager;
    this.projectManager = projectManager;
    this.partnerRequestManager = partnerRequestManager;
    this.crpManager = crpManager;
    this.sendMail = sendMail;
  }


  @Override
  public String execute() throws Exception {
    try {


      sucess = new HashMap<String, Object>();

      String countriesName = null;
      String countriesISO2Code = null;

      for (String string : countries) {
        if (countriesName == null) {
          countriesName = locElementManager.getLocElementByISOCode((string)).getName();
          countriesISO2Code = string;
        } else {
          countriesName = countriesName + ", " + locElementManager.getLocElementByISOCode((string)).getName();
          countriesISO2Code = countriesISO2Code + ", " + string;
        }
        // Add Partner Request information.
        PartnerRequest partnerRequest = new PartnerRequest();
        partnerRequest.setInstitution(institutionManager.getInstitutionById(institutionID));
        partnerRequest.setRequestSource("Project: (" + projectID + ") - "
          + projectManager.getProjectById(projectID).getProjecInfoPhase(this.getActualPhase()).getTitle());
        partnerRequest.setLocElement(locElementManager.getLocElementByISOCode((string)));
        partnerRequest.setOffice(true);
        partnerRequest.setCrp(loggedCrp);
        partnerRequest.setPhase(this.getActualPhase());
        partnerRequestManager.savePartnerRequest(partnerRequest);
      }
      String institutionName = institutionManager.getInstitutionById(institutionID).getName();

      // The requests are saved; the alert is best effort and never fails them
      this.sendNewOfficeAlert(institutionName, countriesName, countriesISO2Code);
      messageSent = true;
    } catch (Exception e) {
      LOG.error("Could not save the office location requests of the project {}", projectID, e);
      messageSent = false;
    }

    if (messageSent) {
      sucess.put("result", "1");
    } else {
      sucess.put("result", "0");
    }

    LOG.info("The user {} requested office locations for the project {}",
      this.getCurrentUser() == null ? null : this.getCurrentUser().getId(), projectID);
    return SUCCESS;

  }

  public Map<String, Object> getSucess() {
    return sucess;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    projectID =
      Long.parseLong((StringUtils.trim(parameters.get(APConstants.PROJECT_REQUEST_ID).getMultipleValues()[0])));
    institutionID =

      Long.parseLong((StringUtils.trim(parameters.get(APConstants.INSTITUTION_REQUEST_ID).getMultipleValues()[0])));
    countries = (parameters.get(APConstants.COUNTRIES_REQUEST_ID).getMultipleValues());

    // Get loggerCrp
    try {
      loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
      loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    } catch (Exception e) {
      LOG.error("Failed to get {} from the session", APConstants.SESSION_CRP, e);
    }
  }


  /**
   * Tells the support mailbox that country offices were requested. The Global Unit is the one of the session, as the
   * database holds it, and its switch decides whether anything is sent. A failure is logged and never reaches the
   * requester: the requests are already saved.
   */
  private void sendNewOfficeAlert(String institutionName, String countriesName, String countriesIso) {
    try {
      if (!PartnerRequestEmails.isEnabled(loggedCrp)) {
        return;
      }
      User requester = this.getCurrentUser();
      String requesterName =
        requester == null ? null : StringUtils.trim(requester.getFirstName() + " " + requester.getLastName());
      String requesterEmail = requester == null ? null : requester.getEmail();
      Email email = PartnerRequestEmails.newOffice(loggedCrp, this.getBaseUrlCdn(), requesterName, requesterEmail,
        institutionName, countriesName, countriesIso, "P" + projectID);
      sendMail.send(config.getEmailNotification(), null, config.getEmailNotification(), email.getSubject(),
        email.getBody(), null, null, null, true);
    } catch (Exception e) {
      LOG.error("Could not send the new office alert of the project {}", projectID, e);
    }
  }

  public void setSucess(Map<String, Object> sucess) {
    this.sucess = sucess;
  }


}
