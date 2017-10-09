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
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestCountryOfficeAction extends BaseAction {

  private static final Logger LOG = LoggerFactory.getLogger(RequestCountryOfficeAction.class);


  /**
   * 
   */
  private static final long serialVersionUID = 4363664661595086316L;

  private Map<String, Object> sucess;

  private Long projectID;
  private Long institutionID;
  private String[] countries;

  private InstitutionManager institutionManager;
  private ProjectManager projectManager;
  private LocElementManager locElementManager;
  private PartnerRequestManager partnerRequestManager;

  private boolean messageSent;

  @Inject
  public RequestCountryOfficeAction(APConfig config, InstitutionManager institutionManager,
    LocElementManager locElementManager, ProjectManager projectManager, PartnerRequestManager partnerRequestManager) {
    super(config);
    this.institutionManager = institutionManager;
    this.locElementManager = locElementManager;
    this.projectManager = projectManager;
    this.partnerRequestManager = partnerRequestManager;
  }


  @Override
  public String execute() throws Exception {
    try {


      sucess = new HashMap<String, Object>();
      String subject;
      StringBuilder message = new StringBuilder();

      String countriesName = null;

      for (String string : countries) {
        if (countriesName == null) {
          countriesName = locElementManager.getLocElementByISOCode((string)).getName();
        } else {
          countriesName = countriesName + ", " + locElementManager.getLocElementByISOCode((string)).getName();
        }
        // Add Partner Request information.
        PartnerRequest partnerRequest = new PartnerRequest();
        partnerRequest.setInstitution(institutionManager.getInstitutionById(institutionID));
        partnerRequest
          .setRequestSource("Project: (" + projectID + ") - " + projectManager.getProjectById(projectID).getTitle());
        partnerRequest.setActive(true);
        partnerRequest.setActiveSince(new Date());
        partnerRequest.setCreatedBy(this.getCurrentUser());
        partnerRequest.setModifiedBy(this.getCurrentUser());
        partnerRequest.setModificationJustification("");
        partnerRequest.setLocElement(locElementManager.getLocElementByISOCode((string)));
        partnerRequest.setOffice(true);
        partnerRequestManager.savePartnerRequest(partnerRequest);
      }
      String institutionName = institutionManager.getInstitutionById(institutionID).getName();

      subject = "[MARLO-" + this.getCrpSession().toUpperCase() + "] Add Office - " + institutionName;
      // Message content
      message.append(this.getCurrentUser().getFirstName() + " " + this.getCurrentUser().getLastName() + " ");
      message.append("(" + this.getCurrentUser().getEmail() + ") ");
      message.append("is requesting to add the following offices for the institution: ");

      message.append("</br></br>");
      message.append("Project : P");
      message.append(projectID);
      message.append("</br></br>");
      message.append("Partner Name: ");
      message.append(institutionName);

      message.append("</br></br>");
      message.append("Countries : ");
      message.append(countriesName);
      message.append(".</br>");
      message.append("</br>");


      try {
        SendMailS sendMail = new SendMailS(this.config);
        sendMail.send(config.getEmailNotification(), null, config.getEmailNotification(), subject, message.toString(),
          null, null, null, true);
      } catch (Exception e) {
        messageSent = false;
      }
      messageSent = true;
    } catch (Exception e) {
      messageSent = false;
    }

    if (messageSent) {
      sucess.put("result", "1");
    } else {
      sucess.put("result", "0");
    }

    LOG.info("The user {} send a message requesting add partners to the project {}", this.getCurrentUser().getEmail(),
      projectID);
    return SUCCESS;

  }


  public Map<String, Object> getSucess() {
    return sucess;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Object> parameters = this.getParameters();
    projectID = Long.parseLong((StringUtils.trim(((String[]) parameters.get(APConstants.PROJECT_REQUEST_ID))[0])));
    institutionID =
      Long.parseLong((StringUtils.trim(((String[]) parameters.get(APConstants.INSTITUTION_REQUEST_ID))[0])));
    countries = ((String[]) parameters.get(APConstants.COUNTRIES_REQUEST_ID));

  }


  public void setSucess(Map<String, Object> sucess) {
    this.sucess = sucess;
  }


}
