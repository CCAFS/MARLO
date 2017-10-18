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
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;


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

  public List<CountryOfficePOJO> getCountryOfficesList() {
    return countryOfficesList;
  }

  public List<InstitutionType> getInstitutionTypesList() {
    return institutionTypesList;
  }


  public List<PartnerRequest> getPartners() {
    return partners;
  }

  public long getRequestID() {
    return requestID;
  }


  /**
   * @author avalencia - CCAFS
   * @date Oct 18, 2017
   * @time 3:46:44 PM Added countryOffices to a POJO instead of a HashMap
   * @throws Exception
   */
  @Override
  public void prepare() throws Exception {
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


  public void setCountryOfficesList(List<CountryOfficePOJO> countryOfficesList) {
    this.countryOfficesList = countryOfficesList;
  }


  public void setPartners(List<PartnerRequest> partners) {
    this.partners = partners;
  }

  public void setRequestID(long requestID) {
    this.requestID = requestID;
  }

}
