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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionLocationManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.CountryOfficePOJO;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
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

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PartnerRequestAction:
 * 
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author avalencia - CCAFS
 * @date Oct 18, 2017
 * @time 3:45:12 PM
 * @date Nov 10, 2017
 * @time 10:09:18 AM:Inactive parent partner request
 */
public class PartnerRequestAction extends BaseAction {

  private static final long serialVersionUID = -4592281983603538935L;

  private static Logger LOG = LoggerFactory.getLogger(PartnerRequestAction.class);
  private final PartnerRequestManager partnerRequestManager;
  private final InstitutionManager institutionManager;
  private final InstitutionTypeManager institutionTypeManager;
  private final InstitutionLocationManager institutionLocationManager;
  private final LocElementManager locElementManager;
  private final GlobalUnitManager globalUnitManager;

  // Variables
  private List<LocElement> countriesList = new ArrayList<>();
  private List<InstitutionType> institutionTypesList = new ArrayList<>();
  private List<CountryOfficePOJO> countryOfficesList = new ArrayList<>();
  private List<PartnerRequest> partners;
  private long requestID;
  private final SendMailS sendMail;
  private boolean success;
  private boolean sendNotification;

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
    LocElementManager locElementManager, InstitutionLocationManager institutionLocationManager,
    GlobalUnitManager globalUnitManager, SendMailS sendMail) {
    super(config);
    this.partnerRequestManager = partnerRequestManager;
    this.institutionManager = institutionManager;
    this.institutionTypeManager = institutionTypeManager;
    this.locElementManager = locElementManager;
    this.institutionLocationManager = institutionLocationManager;
    this.globalUnitManager = globalUnitManager;
    this.sendMail = sendMail;
  }

  /**
   * Add a list of office request and send an email to the users who requested
   * the Countries (The full list of locations is send to each user)
   * 
   * @author avalencia - CCAFS
   * @date Oct 18, 2017
   * @time 3:55:44 PM
   * @return
   */
  public String addCountryOffices() {
    try {
      String[] partnerRequestIds = this.countryOfficePOJO.getIds().split(",");
      if (this.countryOfficePOJO != null) {
        Institution institution =
          this.institutionManager.getInstitutionById(this.countryOfficePOJO.getInstitution().getId());
        Set<User> users = new HashSet<User>();
        Set<LocElement> locElements = new HashSet<LocElement>();
        for (String partnerRequestId : partnerRequestIds) {
          PartnerRequest partnerRequest =
            this.partnerRequestManager.getPartnerRequestById(Long.valueOf(partnerRequestId));
          partnerRequest.setAcepted(new Boolean(true));
          partnerRequest.setAceptedDate(new Date());
          partnerRequest.setActive(false);
          // Store the list of user to send the email
          users.add(partnerRequest.getCreatedBy());
          // If the request comes from CLARISA, include external mail
          if (partnerRequest.getExternalUserMail() != null
            && !"".equalsIgnoreCase(partnerRequest.getExternalUserMail())) {
            User externalUser = new User();
            externalUser.setEmail(partnerRequest.getExternalUserMail());
            externalUser.setFirstName(partnerRequest.getExternalUserName());
            users.add(externalUser);
          }
          // verify if the location has been added previously
          if (locElements.contains(partnerRequest.getLocElement())) {
            LOG.warn("LocElement duplicated: " + partnerRequest.getLocElement().getId() + " will be skipped");
          } else {
            locElements.add(partnerRequest.getLocElement());
            InstitutionLocation institutionLocation = new InstitutionLocation();
            if (this.institutionLocationManager.findByLocation(partnerRequest.getLocElement().getId(),
              partnerRequest.getInstitution().getId()) == null) {
              institutionLocation =
                new InstitutionLocation(partnerRequest.getInstitution(), partnerRequest.getLocElement(), false);
              this.institutionLocationManager.saveInstitutionLocation(institutionLocation);
            } else {
              String warningMessage = "The InstitutionLocation ID:" + this.institutionLocationManager
                .findByLocation(partnerRequest.getLocElement().getId(), partnerRequest.getInstitution().getId()).getId()
                + " already exist in the system.";
              LOG.warn(warningMessage);
              partnerRequest.setAcepted(new Boolean(false));
              partnerRequest.setModificationJustification(warningMessage);
              partnerRequest.setActive(false);
            }
          }
          this.partnerRequestManager.savePartnerRequest(partnerRequest);
        }
        // Send notification email
        this.sendAcceptedOfficeNotficationEmail(users, locElements, institution);
      }
    } catch (Exception e) {
      this.success = false;
    }
    return SUCCESS;
  }

  public String addPartner() {
    PartnerRequest partnerRequest = this.partnerRequestManager.getPartnerRequestById(this.requestID);

    Institution institution = new Institution();
    // Create institution
    institution.setName(partnerRequest.getPartnerName());
    institution.setAcronym(partnerRequest.getAcronym());
    institution.setWebsiteLink(partnerRequest.getWebPage());

    InstitutionType institutionType =
      this.institutionTypeManager.getInstitutionTypeById(partnerRequest.getInstitutionType().getId());
    institution.setInstitutionType(institutionType);

    LocElement locElement = this.locElementManager.getLocElementById(partnerRequest.getLocElement().getId());
    institution.setAdded(new Date());

    this.institutionManager.saveInstitution(institution);

    // Create institution location
    InstitutionLocation institutionLocation = new InstitutionLocation();
    institutionLocation.setInstitution(institution);
    institutionLocation.setLocElement(locElement);
    institutionLocation.setHeadquater(new Boolean(true));
    this.institutionLocationManager.saveInstitutionLocation(institutionLocation);

    partnerRequest.setAcepted(new Boolean(true));
    partnerRequest.setAceptedDate(new Date());
    partnerRequest.setActive(false);
    partnerRequest.setInstitution(institution);
    this.partnerRequestManager.savePartnerRequest(partnerRequest);
    // inactive the parent partnerRequest
    PartnerRequest partnerRequestParent =
      this.partnerRequestManager.getPartnerRequestById(partnerRequest.getPartnerRequest().getId());
    partnerRequestParent.setActive(false);
    this.partnerRequestManager.savePartnerRequest(partnerRequestParent);

    // Send notification email
    this.sendAcceptedNotficationEmail(partnerRequest);

    return SUCCESS;
  }

  public List<LocElement> getCountriesList() {
    return this.countriesList;
  }

  public CountryOfficePOJO getCountryOfficePOJO() {
    return this.countryOfficePOJO;
  }

  public List<CountryOfficePOJO> getCountryOfficesList() {
    return this.countryOfficesList;
  }

  public List<InstitutionType> getInstitutionTypesList() {
    return this.institutionTypesList;
  }

  @Override
  public String getJustification() {
    return this.justification;
  }

  public List<PartnerRequest> getPartners() {
    return this.partners;
  }

  public long getRequestID() {
    return this.requestID;
  }

  public boolean isSuccess() {
    return this.success;
  }

  /**
   * @author avalencia - CCAFS
   * @date Oct 18, 2017
   * @time 3:46:44 PM Added countryOffices to a POJO instead of a HashMap
   * @throws Exception
   */
  @Override
  public void prepare() throws Exception {
    this.success = true;
    HashMap<Institution, List<PartnerRequest>> countryOfficesHashMap = new HashMap<Institution, List<PartnerRequest>>();
    // Verify if exists active partnerRequest
    if (this.partnerRequestManager.findAll() != null) {
      this.partners = new ArrayList<>(this.partnerRequestManager.findAll().stream()
        .filter(pr -> pr.isActive() && !pr.isOffice() && pr.getPartnerRequest() != null).collect(Collectors.toList()));
      for (PartnerRequest officeRequest : this.partnerRequestManager.findAll().stream()
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
      this.partners = new ArrayList<>();
      countryOfficesHashMap = new HashMap<Institution, List<PartnerRequest>>();
    }
    // Fill countryOfficeList with the HashMap
    if (countryOfficesHashMap.size() > 0) {
      for (Institution institution : countryOfficesHashMap.keySet()) {
        CountryOfficePOJO countryOfficePojo =
          new CountryOfficePOJO(institution, countryOfficesHashMap.get(institution));
        this.countryOfficesList.add(countryOfficePojo);
      }
    } else {
      this.countryOfficesList = new ArrayList<>();
    }

    this.countriesList = this.locElementManager.findAll().stream()
      .filter(c -> c.isActive() && c.getLocElementType().getId().longValue() == 2).collect(Collectors.toList());
    /*
     * this.institutionTypesList = this.institutionTypeManager.findAll().stream()
     * .filter(it -> it.isActive() && !it.getOld()).collect(Collectors.toList());
     */
    this.institutionTypesList = this.institutionTypeManager.findAllIATITypes();
    this.countriesList.sort((p1, p2) -> p1.getName().compareTo(p2.getName()));
  }

  /**
   * Reject a list of office requests and send an email to the users who
   * requested the Countries (The full list of locations is send to each user)
   * 
   * @author avalencia - CCAFS
   * @date Oct 19, 2017
   * @time 11:11:19 AM
   * @return
   */
  public String rejectCountryOffices() {
    try {
      try {
        Map<String, Parameter> parameters = this.getParameters();
        this.justification = StringUtils.trim(parameters.get(APConstants.JUSTIFICATION_REQUEST).getMultipleValues()[0]);
        this.sendNotification = Boolean.valueOf(
          StringUtils.trim(parameters.get(APConstants.PARTNER_REQUEST_SEND_NOTIFICATION).getMultipleValues()[0]));
      } catch (Exception e) {
        this.justification = "";
      }

      String[] partnerRequestIds = this.countryOfficePOJO.getIds().split(",");
      if (this.countryOfficePOJO != null) {
        Institution institution =
          this.institutionManager.getInstitutionById(this.countryOfficePOJO.getInstitution().getId());
        Set<User> users = new HashSet<User>();
        Set<LocElement> locElements = new HashSet<LocElement>();
        for (String partnerRequestId : partnerRequestIds) {
          PartnerRequest partnerRequest =
            this.partnerRequestManager.getPartnerRequestById(Long.valueOf(partnerRequestId));
          // Store the list of user to send the email
          users.add(partnerRequest.getCreatedBy());
          // If the request comes from CLARISA, include external mail
          if (partnerRequest.getExternalUserMail() != null
            && !"".equalsIgnoreCase(partnerRequest.getExternalUserMail())) {
            User externalUser = new User();
            externalUser.setEmail(partnerRequest.getExternalUserMail());
            externalUser.setFirstName(partnerRequest.getExternalUserName());
            users.add(externalUser);
          }
          locElements.add(partnerRequest.getLocElement());
          partnerRequest.setAcepted(new Boolean(false));
          partnerRequest.setActive(false);
          partnerRequest.setRejectedBy(this.getCurrentUser());
          partnerRequest.setRejectJustification(this.justification);
          partnerRequest.setRejectedDate(new Date());
          this.partnerRequestManager.savePartnerRequest(partnerRequest);
        }
        // Send notification email
        if (this.sendNotification) {
          this.sendRejectOfficeNotificationEmail(users, locElements, institution);
        }
      }
    } catch (Exception e) {
      this.success = false;
    }
    return SUCCESS;
  }

  private void sendAcceptedNotficationEmail(PartnerRequest partnerRequest) {
    String toEmail = "";

    // CC Email: User who accepted the request
    String ccEmail = this.getCurrentUser().getEmail();

    // ToEmail: User who requested the partner or the mail received by CLARISA
    if (partnerRequest.getExternalUserMail() != null) {
      toEmail = partnerRequest.getExternalUserMail();
      ccEmail = ccEmail + ", " + partnerRequest.getCreatedBy().getEmail();
    } else {

      toEmail = partnerRequest.getCreatedBy().getEmail();
    }

    // BBC: Our gmail notification email.
    String bbcEmails = this.config.getEmailNotification();

    // subject
    String subject =
      this.getText("marloRequestInstitution.accept.email.subject", new String[] {partnerRequest.getPartnerName()});

    // Building the email message
    StringBuilder message = new StringBuilder();
    String userName = partnerRequest.getExternalUserName() == null ? partnerRequest.getCreatedBy().getFirstName()
      : partnerRequest.getExternalUserName();
    message.append(this.getText("email.dear", new String[] {userName}));
    message
      .append(this.getText("marloRequestInstitution.accept.email", new String[] {partnerRequest.getPartnerInfo()}));

    message.append(this.getText("email.support.noCrpAdmins"));
    // message.append(this.getText("email.getStarted"));
    message.append(this.getText("email.bye"));
    GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(partnerRequest.getCrp().getId());
    if (this.validateEmailNotification(globalUnit)) {
      this.sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }

  }

  /**
   * Sends an email to all users who requested the office(s) for an specific
   * institution
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
      this.sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
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
        this.getText("marloRequestInstitution.office.reject.email", new String[] {loc_elements, this.justification}));

      message.append(this.getText("email.support.noCrpAdmins"));
      // message.append(this.getText("email.getStarted"));
      message.append(this.getText("email.bye"));
      this.sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
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

  private boolean validateEmailNotification(GlobalUnit globalUnit) {
    Boolean crpNotification = globalUnit.getCustomParameters().stream()
      .filter(c -> c.getParameter().getKey().equalsIgnoreCase(APConstants.CRP_EMAIL_NOTIFICATIONS))
      .allMatch(t -> (t.getValue() == null) ? true : t.getValue().equalsIgnoreCase("true"));
    return crpNotification;
  }

}
