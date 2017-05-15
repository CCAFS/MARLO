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


package org.cgiar.ccafs.marlo.action.projects;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ActivityManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.ActivityPartner;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PartnersSaveAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -5137162991426442026L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(PartnersSaveAction.class);

  private ActivityPartner activityPartner;
  // Managers
  private LocElementManager locationManager;
  private InstitutionTypeManager institutionManager;
  private InstitutionManager institutionsManager;
  private ActivityManager activityManager;
  private ProjectManager projectManager;
  private PartnerRequestManager partnerRequestManager;

  // Model
  private List<LocElement> countriesList;
  private List<InstitutionType> institutionTypesList;
  private List<Institution> institutions;

  // private ActivityPartner activityPartner;
  private boolean messageSent;


  private String partnerWebPage;

  private int projectID;

  private int activityID;

  @Inject
  public PartnersSaveAction(APConfig config, LocElementManager locationManager,
    InstitutionTypeManager institutionManager, InstitutionManager institutionsManager, ActivityManager activityManager,
    ProjectManager projectManager, PartnerRequestManager partnerRequestManager) {
    super(config);
    this.locationManager = locationManager;
    this.institutionManager = institutionManager;
    this.activityManager = activityManager;
    this.projectManager = projectManager;
    this.institutionsManager = institutionsManager;
    this.partnerRequestManager = partnerRequestManager;
  }

  public int getActivityID() {
    return activityID;
  }

  public ActivityPartner getActivityPartner() {
    return activityPartner;
  }

  public List<LocElement> getCountriesList() {
    return countriesList;
  }


  public List<Institution> getInstitutions() {
    return institutions;
  }

  public List<InstitutionType> getInstitutionTypesList() {
    return institutionTypesList;
  }

  public String getPartnerWebPage() {
    return partnerWebPage;
  }

  public int getProjectID() {
    return projectID;
  }

  public boolean isMessageSent() {
    return messageSent;
  }

  @Override
  public void prepare() throws Exception {
    super.prepare();
    // Take the activity id only the first time the page loads

    if (this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID) != null) {
      projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
      LOG.info("The user {} load the request partner section related to the project {}.",
        this.getCurrentUser().getEmail(), projectID);
    }

    this.countriesList = locationManager.findAll().stream()
      .filter(c -> c.isActive() && c.getLocElementType().getId().longValue() == 2).collect(Collectors.toList());
    this.institutionTypesList = institutionManager.findAll();
    institutions = institutionsManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());

    institutions.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
  }


  @Override
  public String save() {
    String institutionName, institutionAcronym, institutionTypeName, countryId, countryName, city, headQuaterName,
      website;
    String subject;
    StringBuilder message = new StringBuilder();

    long partnerTypeId;
    long headQuater = -1;
    // Take the values to create the message
    institutionName = activityPartner.getPartner().getName();
    institutionAcronym = activityPartner.getPartner().getAcronym();
    partnerTypeId = activityPartner.getPartner().getInstitutionType().getId();
    // countryId = String.valueOf(activityPartner.getPartner().getLocElement().getId());
    // city = activityPartner.getPartner().getCity();
    website = activityPartner.getPartner().getWebsiteLink();
    headQuaterName = "";
    /*
     * try {
     * headQuater = activityPartner.getPartner().getHeadquarter().getId();
     * headQuaterName = institutionsManager.getInstitutionById(headQuater).getComposedName();
     * } catch (Exception e) {
     * headQuater = -1;
     * }
     * // Get the country name
     * countryName = locationManager.getLocElementById(Long.parseLong(countryId)).getName();
     */
    // Get the partner type name
    institutionTypeName = "";
    for (InstitutionType pt : institutionTypesList) {
      if (pt.getId() == partnerTypeId) {
        institutionTypeName = pt.getName();
      }
    }


    // Add Partner Request information.
    PartnerRequest partnerRequest = new PartnerRequest();
    partnerRequest.setActive(true);
    partnerRequest.setActiveSince(new Date());
    partnerRequest.setCreatedBy(this.getCurrentUser());
    partnerRequest.setModifiedBy(this.getCurrentUser());
    partnerRequest.setModificationJustification("");

    partnerRequest.setPartnerName(institutionName);
    partnerRequest.setAcronym(institutionAcronym);
    // partnerRequest.setCity(city);
    // partnerRequest.setLocElement(locationManager.getLocElementById(Long.parseLong(countryId)));
    partnerRequest.setInstitutionType(institutionManager.getInstitutionTypeById(partnerTypeId));

    if (this.partnerWebPage != null && !this.partnerWebPage.isEmpty()) {
      partnerRequest.setWebPage(partnerWebPage);
    }

    if (headQuater != -1) {
      partnerRequest.setInstitution(institutionsManager.getInstitutionById(headQuater));
    }

    partnerRequestManager.savePartnerRequest(partnerRequest);


    // message subject
    subject = "[MARLO-" + this.getCrpSession().toUpperCase() + "] Partner verification - " + institutionName;
    // Message content
    message.append(this.getCurrentUser().getFirstName() + " " + this.getCurrentUser().getLastName() + " ");
    message.append("(" + this.getCurrentUser().getEmail() + ") ");
    message.append("is requesting to add the following partner information:");
    message.append("</br></br>");
    message.append("Partner Name: ");
    message.append(institutionName);
    message.append("</br>");
    message.append("Acronym: ");
    message.append(institutionAcronym);
    message.append(" </br>");
    message.append("Partner type: ");
    message.append(institutionTypeName);
    message.append(" </br>");

    if (headQuater != -1) {
      message.append("HeadQuater: ");
      message.append(headQuaterName);
      message.append(" </br>");
    }

    // message.append("City: ");
    // message.append(city);
    // message.append(" </br>");

    // message.append("Country: ");
    // message.append(countryName);
    // message.append(" </br>");

    // Is there a web page?
    if (this.partnerWebPage != null && !this.partnerWebPage.isEmpty()) {
      message.append("Web Page: ");
      message.append(partnerWebPage);
      message.append(" </br>");
    }
    message.append(" </br>");

    if (activityID > 0) {
      message.append("Activity: (");
      message.append(activityID);
      message.append(") - ");
      message.append(activityManager.getActivityById(activityID).getTitle());
    } else if (projectID > 0) {
      message.append("Project: (");
      message.append(projectID);
      message.append(") - ");
      message.append(projectManager.getProjectById(projectID).getTitle());
    }

    message.append(".</br>");
    message.append("</br>");
    try {
      SendMailS sendMail = new SendMailS(this.config);
      sendMail.send(config.getEmailNotification(), null, config.getEmailNotification(), subject, message.toString(),
        null, null, null, true);
    } catch (Exception e) {

    }
    messageSent = true;

    LOG.info("The user {} send a message requesting add partners to the project {}", this.getCurrentUser().getEmail(),
      projectID);


    Collection<String> messages = this.getActionMessages();
    this.addActionMessage("message:" + this.getText("saving.saved"));
    messages = this.getActionMessages();
    return SUCCESS;
  }


  public void setActivityID(int activityID) {
    this.activityID = activityID;
  }

  public void setActivityPartner(ActivityPartner activityPartner) {
    this.activityPartner = activityPartner;
  }


  public void setInstitutions(List<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setMessageSent(boolean messageSent) {
    this.messageSent = messageSent;
  }

  public void setPartnerWebPage(String partnerWebPage) {
    this.partnerWebPage = partnerWebPage;
  }

  public void setProjectID(int projectID) {
    this.projectID = projectID;
  }

  @Override
  public void validate() {
    boolean anyError = false;

    // If the page is loading don't validate
    if (this.getRequest().getMethod().equalsIgnoreCase("post")) {

      // Check the partner name
      if (activityPartner.getPartner().getName().isEmpty()) {
        this.addFieldError("activityPartner.partner.name", this.getText("validation.field.required"));
        anyError = true;
      }


      if (activityPartner.getPartner().getInstitutionType().getId() == -1) {
        this.addFieldError("activityPartner.institutionType.id", this.getText("validation.field.required"));
        anyError = true;
      }
      /*
       * if (activityPartner.getPartner().getLocElement().getId() == -1) {
       * this.addFieldError("activityPartner.locElement.id", this.getText("validation.field.required"));
       * anyError = true;
       * }
       */

      if (anyError) {
        this.addActionError(this.getText("saving.fields.required"));
      }
    }
    super.validate();
  }

}
