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

package org.cgiar.ccafs.marlo.action.superadmin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.InstitutionLocationManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.CountryOfficePOJO;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * PartnerRequestAction:
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author avalencia - CCAFS
 * @date Oct 18, 2017
 * @time 3:45:12 PM
 */
public class PartnerRequestAction extends BaseAction {

  private static final long serialVersionUID = -4592281983603538935L;
  private static Logger LOG = LoggerFactory.getLogger(PartnerRequestAction.class);
  // Managers
  private PartnerRequestManager partnerRequestManager;
  private InstitutionManager institutionManager;
  private InstitutionTypeManager institutionTypeManager;
  private InstitutionLocationManager institutionLocationManager;
  private LocElementManager locElementManager;

  // Variables
  private List<LocElement> countriesList = new ArrayList<>();
  private List<InstitutionType> institutionTypesList = new ArrayList<>();
  private List<CountryOfficePOJO> countryOfficesList = new ArrayList<>();
  private List<PartnerRequest> partners;
  private long requestID;
  private SendMailS sendMail;
  private boolean success;

  // Justification for reject office(s)
  private String justification;


  /**
   * CountryOffices selected
   * 
   * @author avalencia - CCAFS
   * @date Oct 19, 2017
   * @time 8:22:32 AM
   */
  private CountryOfficePOJO countryOfficePOJO;

  @Inject
  public PartnerRequestAction(APConfig config, PartnerRequestManager partnerRequestManager,
    InstitutionManager institutionManager, InstitutionTypeManager institutionTypeManager,
    LocElementManager locElementManager, InstitutionLocationManager institutionLocationManager, SendMailS sendMail) {
    super(config);
    this.partnerRequestManager = partnerRequestManager;
    this.institutionManager = institutionManager;
    this.institutionTypeManager = institutionTypeManager;
    this.locElementManager = locElementManager;
    this.institutionLocationManager = institutionLocationManager;
    this.sendMail = sendMail;
  }

  /**
   * Add a list of office request and send an email to the users who requested the Countries (The full list of
   * locations is send to each user)
   * 
   * @author avalencia - CCAFS
   * @date Oct 18, 2017
   * @time 3:55:44 PM
   * @return
   */
  public String addCountryOffices() {
    try {
      String[] partnerRequestIds = countryOfficePOJO.getIds().split(",");
      if (countryOfficePOJO != null) {
        Institution institution = institutionManager.getInstitutionById(countryOfficePOJO.getInstitution().getId());
        Set<User> users = new HashSet<User>();
        Set<LocElement> locElements = new HashSet<LocElement>();
        for (String partnerRequestId : partnerRequestIds) {
          PartnerRequest partnerRequest = partnerRequestManager.getPartnerRequestById(Long.valueOf(partnerRequestId));
          partnerRequest.setAcepted(new Boolean(true));
          partnerRequest.setActive(false);
          partnerRequest.setModifiedBy(this.getCurrentUser());
          // Store the list of user to send the email
          users.add(partnerRequest.getCreatedBy());
          // verify if the location has been added previously
          if (locElements.contains(partnerRequest.getLocElement())) {
            LOG.warn("LocElement duplicated: " + partnerRequest.getLocElement().getId() + " will be skipped");
          } else {
            locElements.add(partnerRequest.getLocElement());
            InstitutionLocation institutionLocation = new InstitutionLocation();
            if (institutionLocationManager.findByLocation(partnerRequest.getLocElement().getId(),
              partnerRequest.getInstitution().getId()) == null) {
              institutionLocation =
                new InstitutionLocation(partnerRequest.getInstitution(), partnerRequest.getLocElement(), false);
              institutionLocationManager.saveInstitutionLocation(institutionLocation);
            } else {
              String warningMessage = "The InstitutionLocation ID:" + institutionLocationManager
                .findByLocation(partnerRequest.getLocElement().getId(), partnerRequest.getInstitution().getId()).getId()
                + " already exist in the system.";
              LOG.warn(warningMessage);
              partnerRequest.setAcepted(new Boolean(false));
              partnerRequest.setModificationJustification(warningMessage);
            }
          }
          partnerRequestManager.savePartnerRequest(partnerRequest);
        }
        // Send notification email
        this.sendAcceptedOfficeNotficationEmail(users, locElements, institution);
      }
    } catch (Exception e) {
      success = false;
    }
    return SUCCESS;
  }


  public String addPartner() {
    PartnerRequest partnerRequest = partnerRequestManager.getPartnerRequestById(requestID);

    Institution institution = new Institution();
    // Create institution
    institution.setName(partnerRequest.getPartnerName());
    institution.setAcronym(partnerRequest.getAcronym());
    institution.setWebsiteLink(partnerRequest.getWebPage());

    InstitutionType institutionType =
      institutionTypeManager.getInstitutionTypeById(partnerRequest.getInstitutionType().getId());
    institution.setInstitutionType(institutionType);

    LocElement locElement = locElementManager.getLocElementById(partnerRequest.getLocElement().getId());
    institution.setAdded(new Date());

    institutionManager.saveInstitution(institution);

    // Create institution location
    InstitutionLocation institutionLocation = new InstitutionLocation();
    institutionLocation.setInstitution(institution);
    institutionLocation.setLocElement(locElement);
    institutionLocation.setHeadquater(new Boolean(true));
    institutionLocationManager.saveInstitutionLocation(institutionLocation);

    partnerRequest.setAcepted(new Boolean(true));
    partnerRequest.setActive(false);
    partnerRequest.setModifiedBy(this.getCurrentUser());
    partnerRequestManager.savePartnerRequest(partnerRequest);

    // Send notification email
    this.sendAcceptedNotficationEmail(partnerRequest);

    return SUCCESS;
  }

  public List<LocElement> getCountriesList() {
    return countriesList;
  }


  public CountryOfficePOJO getCountryOfficePOJO() {
    return countryOfficePOJO;
  }

  public List<CountryOfficePOJO> getCountryOfficesList() {
    return countryOfficesList;
  }

  public List<InstitutionType> getInstitutionTypesList() {
    return institutionTypesList;
  }


  @Override
  public String getJustification() {
    return justification;
  }


  public List<PartnerRequest> getPartners() {
    return partners;
  }


  public long getRequestID() {
    return requestID;
  }

  public boolean isSuccess() {
    return success;
  }

  /**
   * @author avalencia - CCAFS
   * @date Oct 18, 2017
   * @time 3:46:44 PM Added countryOffices to a POJO instead of a HashMap
   * @throws Exception
   */
  @Override
  public void prepare() throws Exception {
    success = true;
    HashMap<Institution, List<PartnerRequest>> countryOfficesHashMap = new HashMap<Institution, List<PartnerRequest>>();
    // Verify if exists active partnerRequest
    if (partnerRequestManager.findAll().stream().filter(pr -> pr.isActive()) != null) {
      partners = new ArrayList<>(partnerRequestManager.findAll().stream().filter(pr -> pr.isActive() && !pr.isOffice())
        .collect(Collectors.toList()));
      for (PartnerRequest officeRequest : partnerRequestManager.findAll().stream()
        .filter(pr -> pr.isActive() && pr.isOffice() && pr.getInstitution() != null).collect(Collectors.toList())) {
        if (countryOfficesHashMap.containsKey(officeRequest.getInstitution())) {
          countryOfficesHashMap.get(officeRequest.getInstitution()).add(officeRequest);
        } else {
          List<PartnerRequest> requestList = new ArrayList<>();
          requestList.add(officeRequest);
          countryOfficesHashMap.put(officeRequest.getInstitution(), requestList);
        }
      }
    } else {
      partners = new ArrayList<>();
      countryOfficesHashMap = new HashMap<Institution, List<PartnerRequest>>();
    }
    // Fill countryOfficeList with the HashMap
    if (countryOfficesHashMap.size() > 0) {
      for (Institution institution : countryOfficesHashMap.keySet()) {
        CountryOfficePOJO countryOfficePojo =
          new CountryOfficePOJO(institution, countryOfficesHashMap.get(institution));
        countryOfficesList.add(countryOfficePojo);
      }
    } else {
      countryOfficesList = new ArrayList<>();
    }
    this.countriesList = locElementManager.findAll().stream()
      .filter(c -> c.isActive() && c.getLocElementType().getId().longValue() == 2).collect(Collectors.toList());
    this.institutionTypesList = institutionTypeManager.findAll().stream().filter(it -> it.isActive() && !it.getOld())
      .collect(Collectors.toList());
    countriesList.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
  }

  /**
   * Reject a list of office requests and send an email to the users who requested the Countries (The full list of
   * locations is send to each user)
   * 
   * @author avalencia - CCAFS
   * @date Oct 19, 2017
   * @time 11:11:19 AM
   * @return
   */
  public String rejectCountryOffices() {
    try {
      try {
        Map<String, Object> parameters = this.getParameters();
        justification = StringUtils.trim(((String[]) parameters.get(APConstants.JUSTIFICATION_REQUEST))[0]);
      } catch (Exception e) {
        System.out.println(e.getMessage());
        justification = "";
      }

      String[] partnerRequestIds = countryOfficePOJO.getIds().split(",");
      if (countryOfficePOJO != null) {
        Institution institution = institutionManager.getInstitutionById(countryOfficePOJO.getInstitution().getId());
        Set<User> users = new HashSet<User>();
        Set<LocElement> locElements = new HashSet<LocElement>();
        for (String partnerRequestId : partnerRequestIds) {
          PartnerRequest partnerRequest = partnerRequestManager.getPartnerRequestById(Long.valueOf(partnerRequestId));
          // Store the list of user to send the email
          users.add(partnerRequest.getCreatedBy());
          locElements.add(partnerRequest.getLocElement());
          partnerRequest.setAcepted(new Boolean(false));
          partnerRequest.setActive(false);
          partnerRequest.setRejectedBy(this.getCurrentUser());
          partnerRequest.setRejectJustification(justification);
          partnerRequestManager.savePartnerRequest(partnerRequest);
        }
        // Send notification email
        this.sendRejectOfficeNotificationEmail(users, locElements, institution);
      }
    } catch (Exception e) {
      success = false;
    }
    return SUCCESS;
  }

  private void sendAcceptedNotficationEmail(PartnerRequest partnerRequest) {
    String toEmail = "";
    // ToEmail: User who requested the partner
    toEmail = partnerRequest.getCreatedBy().getEmail();

    // CC Email: User who accepted the request
    String ccEmail = this.getCurrentUser().getEmail();

    // BBC: Our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    // subject
    String subject =
      this.getText("marloRequestInstitution.accept.email.subject", new String[] {partnerRequest.getPartnerName()});

    // Building the email message
    StringBuilder message = new StringBuilder();
    message.append(this.getText("email.dear", new String[] {partnerRequest.getCreatedBy().getFirstName()}));
    message
      .append(this.getText("marloRequestInstitution.accept.email", new String[] {partnerRequest.getPartnerInfo()}));

    message.append(this.getText("email.support.noCrpAdmins"));
    message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));
    sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
  }

  /**
   * Sends an email to all users who requested the office(s) for an specific institution
   * 
   * @author avalencia - CCAFS
   * @date Oct 19, 2017
   * @time 10:29:39 AM
   * @param users: ToEmail - User who requested the office
   * @param locElements: Offices (countries) requested
   * @param institution
   */
  private void sendAcceptedOfficeNotficationEmail(Set<User> users, Set<LocElement> locElements,
    Institution institution) {
    // CC Email: User who accepted the request
    String ccEmail = this.getCurrentUser().getEmail();

    // BBC: Our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    // subject
    String subject =
      this.getText("marloRequestInstitution.office.accept.email.subject", new String[] {institution.getComposedName()});
    // Prepare loc elements to send in html format
    String loc_elements = "";
    for (LocElement locElement : locElements) {
      loc_elements += "<li>" + locElement.getName() + "</li>";
    }
    // Send message to the users who requested the office(s)
    for (User user : users) {
      String toEmail = "";
      // ToEmail: User who requested the partner
      toEmail = user.getEmail();
      // Building the email message
      StringBuilder message = new StringBuilder();
      message.append(this.getText("email.dear", new String[] {user.getFirstName()}));

      message.append(this.getText("marloRequestInstitution.office.accept.email", new String[] {loc_elements}));

      message.append(this.getText("email.support.noCrpAdmins"));
      message.append(this.getText("email.getStarted"));
      message.append(this.getText("email.bye"));
      sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }
  }

  private void sendRejectOfficeNotificationEmail(Set<User> users, Set<LocElement> locElements,
    Institution institution) {
    // CC Email: User who accepted the request
    String ccEmail = this.getCurrentUser().getEmail();

    // BBC: Our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    // subject
    String subject =
      this.getText("marloRequestInstitution.office.reject.email.subject", new String[] {institution.getComposedName()});
    // Prepare loc elements to send in html format
    String loc_elements = "";
    for (LocElement locElement : locElements) {
      loc_elements += "<li>" + locElement.getName() + "</li>";
    }
    // Send message to the users who requested the office(s)
    for (User user : users) {
      String toEmail = "";
      // ToEmail: User who requested the partner
      toEmail = user.getEmail();
      // Building the email message
      StringBuilder message = new StringBuilder();
      message.append(this.getText("email.dear", new String[] {user.getFirstName()}));

      message.append(
        this.getText("marloRequestInstitution.office.reject.email", new String[] {loc_elements, justification}));

      message.append(this.getText("email.support.noCrpAdmins"));
      message.append(this.getText("email.getStarted"));
      message.append(this.getText("email.bye"));
      sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }
  }

  public void setCountryOfficePOJO(CountryOfficePOJO countryOfficePOJO) {
    this.countryOfficePOJO = countryOfficePOJO;
  }


  public void setCountryOfficesList(List<CountryOfficePOJO> countryOfficesList) {
    this.countryOfficesList = countryOfficesList;
  }

  @Override
  public void setJustification(String justification) {
    this.justification = justification;
  }


  public void setPartners(List<PartnerRequest> partners) {
    this.partners = partners;
  }


  public void setRequestID(long requestID) {
    this.requestID = requestID;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

}
