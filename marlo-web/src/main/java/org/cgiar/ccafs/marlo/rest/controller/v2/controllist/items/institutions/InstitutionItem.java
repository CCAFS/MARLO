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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.institutions;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionLocationManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.Institutions;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionRequestDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionSimpleDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewInstitutionDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.InstitutionMapper;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import com.ibm.icu.text.MessageFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Configuration
@PropertySource("classpath:global.properties")
@Named
public class InstitutionItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(Institutions.class);
  @Autowired
  private Environment env;
  private final SendMailS sendMail;
  private InstitutionManager institutionManager;
  private InstitutionLocationManager institutionLocationManager;
  private LocElementManager locElementManager;
  private InstitutionMapper institutionMapper;
  private PartnerRequestManager partnerRequestManager;
  private GlobalUnitManager globalUnitManager;
  private InstitutionTypeManager institutionTypeManager;
  private CustomParameterManager customParameterManager;
  protected APConfig config;

  private boolean messageSent;
  // private List<FieldErrorDTO> fieldErrors;

  @Inject
  public InstitutionItem(InstitutionTypeManager institutionTypeManager, InstitutionManager institutionManager,
    InstitutionMapper institutionMapper, LocElementManager locElementManager,
    InstitutionLocationManager institutionLocationManager, PartnerRequestManager partnerRequestManager,
    GlobalUnitManager globalUnitManager, CustomParameterManager customParameterManager, SendMailS sendMail,
    APConfig config) {
    this.institutionTypeManager = institutionTypeManager;
    this.institutionLocationManager = institutionLocationManager;
    this.institutionManager = institutionManager;
    this.institutionMapper = institutionMapper;
    this.locElementManager = locElementManager;
    this.partnerRequestManager = partnerRequestManager;
    this.globalUnitManager = globalUnitManager;
    this.customParameterManager = customParameterManager;
    this.sendMail = sendMail;
    this.config = config;
    // this.fieldErrors = new ArrayList<FieldErrorDTO>();
  }

  public ResponseEntity<InstitutionRequestDTO> acceptPartnerRequest(Long id, boolean accepted, String justification,
    String entityAcronym, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    PartnerRequest partnerRequest = this.partnerRequestManager.getPartnerRequestById(id);
    Set<CrpUser> lstUser = user.getCrpUsers();
    // If not exists
    if (partnerRequest != null && partnerRequest.getPartnerRequest() == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    // if the request was from an CRP which the user don't have authorization
    if (!lstUser.stream().anyMatch(crp -> crp.getCrp().getAcronym().equalsIgnoreCase(entityAcronym))) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    // if the request was from an CRP diferent to the acronym
    if (!entityAcronym.equalsIgnoreCase(partnerRequest.getCrp().getAcronym())) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    if (partnerRequest.getAcepted() == null) {
      if (accepted) {
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
        this.sendAcceptedNotficationEmail(partnerRequest, user);
      } else {
        partnerRequest.setAcepted(new Boolean(false));
        partnerRequest.setRejectJustification(justification);
        partnerRequest.setRejectedBy(user);
        partnerRequest.setRejectedDate(new Date());
        partnerRequest.setActive(false);
        partnerRequest = this.partnerRequestManager.savePartnerRequest(partnerRequest);
        // inactive the parent partnerRequest
        PartnerRequest partnerRequestParent =
          this.partnerRequestManager.getPartnerRequestById(partnerRequest.getPartnerRequest().getId());
        partnerRequestParent.setActive(false);
        this.partnerRequestManager.savePartnerRequest(partnerRequestParent);
        this.sendRejectedNotficationEmail(partnerRequest, user, justification);
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("PartnerRequest", "Accepted", "This Request has already been processed"));
    }
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(partnerRequest).map(this.institutionMapper::partnerRequestToInstitutionRequestDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<InstitutionRequestDTO> createPartnerRequest(NewInstitutionDTO newInstitutionDTO,
    String entityAcronym, User user) throws Exception {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    Pattern pattern = Pattern.compile(regex);


    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream().anyMatch(crp -> crp.getCrp().getAcronym().equalsIgnoreCase(entityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("InstitutionRequestDTO", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("InstitutionRequestDTO", "GlobalUnitEntity", "Invalid CGIAR entity acronym"));
    }

    LocElement locElement = this.locElementManager.getLocElementByISOCode(newInstitutionDTO.getHqCountryIso());
    if (locElement == null) {
      fieldErrors.add(new FieldErrorDTO("InstitutionRequestDTO", "Country", "Invalid country iso Alpha code"));
    }

    InstitutionType institutionType =
      this.institutionTypeManager.getInstitutionTypeById(Long.parseLong(newInstitutionDTO.getInstitutionTypeCode()));
    if (institutionType == null) {
      fieldErrors.add(new FieldErrorDTO("InstitutionRequestDTO", "institutionType", "Invalid Institution type code"));
    }

    // externalUserMail is mandatory
    if (newInstitutionDTO.getExternalUserMail() != null) {
      Matcher matcher = pattern.matcher(newInstitutionDTO.getExternalUserMail());
      if (!matcher.matches()) {
        fieldErrors.add(new FieldErrorDTO("InstitutionRequestDTO", "getExternalUserMail", "Bad external email format"));
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("InstitutionRequestDTO", "getExternalUserMail", "External email is empty"));
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    PartnerRequest partnerRequestParent = this.institutionMapper.newInstitutionDTOToPartnerRequest(newInstitutionDTO,
      globalUnitEntity, locElement, institutionType, user);

    partnerRequestParent = this.partnerRequestManager.savePartnerRequest(partnerRequestParent);

    /**
     * Need to create a parent child relationship for the partnerRequest to
     * display. That design might need to be re-visited.
     */
    PartnerRequest partnerRequestChild = this.institutionMapper.newInstitutionDTOToPartnerRequest(newInstitutionDTO,
      globalUnitEntity, locElement, institutionType, user);

    partnerRequestChild.setPartnerRequest(partnerRequestParent);

    partnerRequestChild = this.partnerRequestManager.savePartnerRequest(partnerRequestChild);

    // SEND THE MAIL
    this.sendPartnerRequestEmail(partnerRequestChild, user, entityAcronym);

    return new ResponseEntity<InstitutionRequestDTO>(
      this.institutionMapper.partnerRequestToInstitutionRequestDTO(partnerRequestChild), HttpStatus.CREATED);

  }

  /**
   * Find a institution requesting a MARLO id
   * 
   * @param id
   * @return a InstitutionDTO with the Institution Type data.
   */
  public ResponseEntity<InstitutionDTO> findInstitutionById(Long id) {
    Institution institution = this.institutionManager.getInstitutionById(id);

    return Optional.ofNullable(institution).map(this.institutionMapper::institutionToInstitutionDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the institution *
   * 
   * @return a List of institutions.
   */
  public ResponseEntity<List<InstitutionDTO>> getAllInstitutions() {
    List<Institution> institutions = this.institutionManager.findAll();
    List<InstitutionDTO> institutionDTOs = institutions.stream()
      .map(institution -> this.institutionMapper.institutionToInstitutionDTO(institution)).collect(Collectors.toList());
    return Optional.ofNullable(institutionDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<InstitutionSimpleDTO>> getAllInstitutionsSimple() {
    List<Institution> institutions = this.institutionManager.getAllInstitutionsSimple();
    List<InstitutionSimpleDTO> institutionDTOs =
      institutions.stream().map(institution -> this.institutionMapper.institutionToInstitutionSimpleDTO(institution))
        .collect(Collectors.toList());
    return Optional.ofNullable(institutionDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public List<InstitutionRequestDTO> getParterRequestByGlobalUnit(String entityAcronym, User user) {
    List<InstitutionRequestDTO> partnerRequestList = new ArrayList<InstitutionRequestDTO>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    Set<CrpUser> lstUser = user.getCrpUsers();
    // if the request was from an CRP which the user don't have authorization
    if (!lstUser.stream().anyMatch(crp -> crp.getCrp().getAcronym().equalsIgnoreCase(entityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("PartnerRequestList", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }
    List<PartnerRequest> requestList = new ArrayList<PartnerRequest>();
    if (this.partnerRequestManager.findAll() != null) {
      requestList = new ArrayList<>(this.partnerRequestManager.findAll().stream()
        .filter(pr -> pr.isActive() && !pr.isOffice()
          && pr.getCrp().getAcronym().toUpperCase().equals(entityAcronym.toUpperCase())
          && pr.getPartnerRequest() != null)
        .collect(Collectors.toList()));
    }
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    partnerRequestList = requestList.stream().map(this.institutionMapper::partnerRequestToInstitutionRequestDTO)
      .collect(Collectors.toList());
    return partnerRequestList;
  }

  /**
   * Get a partner request by an id *
   * 
   * @return PartnerRequestDTO founded
   */
  public ResponseEntity<InstitutionRequestDTO> getPartnerRequest(Long id, String entityAcronym, User user) {
    PartnerRequest partnerRequest = this.partnerRequestManager.getPartnerRequestById(id);
    Set<CrpUser> lstUser = user.getCrpUsers();
    // If not exists
    if (partnerRequest != null && partnerRequest.getPartnerRequest() == null) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    // if the request was from an CRP which the user don't have authorization
    if (!lstUser.stream().anyMatch(crp -> crp.getCrp().getAcronym().equalsIgnoreCase(entityAcronym))) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    // if the request was from an CRP diferent to the acronym
    if (!entityAcronym.equalsIgnoreCase(partnerRequest.getCrp().getAcronym())) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return Optional.ofNullable(partnerRequest).map(this.institutionMapper::partnerRequestToInstitutionRequestDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  String getText(String property, String[] params) {
    String value = this.env.getProperty(property);
    MessageFormat message = new MessageFormat(value);
    return message.format(params);

  }

  private void sendAcceptedNotficationEmail(PartnerRequest partnerRequest, User user) {
    String toEmail = "";

    // CC Email: User who accepted the request
    String ccEmail = user.getEmail();

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
    String subject = this.getText("marloRequestInstitution.clarisa.accept.email.subject",
      new String[] {partnerRequest.getPartnerName()});

    // Building the email message
    StringBuilder message = new StringBuilder();
    String userName = partnerRequest.getExternalUserName() == null ? partnerRequest.getCreatedBy().getFirstName()
      : partnerRequest.getExternalUserName();
    message.append(this.getText("email.dear", new String[] {userName}));
    message
      .append(this.getText("marloRequestInstitution.accept.email", new String[] {partnerRequest.getPartnerInfo()}));

    message.append("This request was sent through CLARISA logged as " + user.getFirstName() + " " + user.getLastName()
      + " (" + user.getEmail() + ")  </br>");

    GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(partnerRequest.getCrp().getId());
    CustomParameter parameter = customParameterManager
      .getCustomParameterByParameterKeyAndGlobalUnitId(APConstants.CRP_EMAIL_NOTIFICATIONS, globalUnit.getId());
    Boolean crpNotification = parameter == null ? true : parameter.getValue().equalsIgnoreCase("true");
    if (crpNotification) {
      this.sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }
  }

  private void sendPartnerRequestEmail(PartnerRequest partnerRequest, User user, String entityAcronym) {
    String institutionName, institutionAcronym, institutionTypeName, countryName, partnerWebPage;
    institutionName = partnerRequest.getPartnerName();
    institutionAcronym = partnerRequest.getAcronym();
    institutionTypeName = partnerRequest.getInstitutionType().getName();
    countryName = partnerRequest.getLocElement().getName();
    partnerWebPage = partnerRequest.getWebPage();
    String subject;
    StringBuilder message = new StringBuilder();

    // message subject

    subject = this.getText("marloRequestInstitution.clarisa.email.subject",
      new String[] {entityAcronym.toUpperCase(), institutionName});
    String ccEmail = user.getEmail();
    if (partnerRequest.getExternalUserMail() != null) {
      ccEmail = partnerRequest.getExternalUserMail();
    }
    // Message content
    message.append(partnerRequest.getExternalUserName() == null ? (user.getFirstName() + " " + user.getLastName() + " ")
      : partnerRequest.getExternalUserName());
    message.append(" (");
    message.append(
      partnerRequest.getExternalUserMail() == null ? user.getEmail() : partnerRequest.getExternalUserMail() + ") ");
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

    message.append("Headquarter country location: ");
    message.append(countryName);
    message.append(" </br>");


    // Is there a web page?
    if (partnerWebPage != null && !partnerWebPage.isEmpty()) {
      message.append("Web Page: ");
      message.append(partnerWebPage);
      message.append(" </br>");
    }
    message.append(" </br></br>");
    message.append("This request was sent through CLARISA API  </br>");
    message.append("</br>");
    try {
      GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(partnerRequest.getCrp().getId());
      CustomParameter parameter = customParameterManager
        .getCustomParameterByParameterKeyAndGlobalUnitId(APConstants.CRP_EMAIL_NOTIFICATIONS, globalUnit.getId());
      Boolean crpNotification = parameter == null ? true : parameter.getValue().equalsIgnoreCase("true");
      if (crpNotification) {
        this.sendMail.send(this.config.getEmailNotification(), ccEmail, this.config.getEmailNotification(), subject,
          message.toString(), null, null, null, true);
      }
    } catch (Exception e) {
      LOG.error("unable to send mail", e);
      this.messageSent = false;
      /**
       * Original code swallows the exception and didn't even log it. Now
       * we at least log it, but we need to revisit to see if we should
       * continue processing or re-throw the exception.
       */
    }
    this.messageSent = true;
  }

  private void sendRejectedNotficationEmail(PartnerRequest partnerRequest, User user, String justification) {
    String toEmail = "";
    // CC Email: User who rejected the request
    String ccEmail = user.getEmail();

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
    String subject = this.getText("marloRequestInstitution.clarisa.reject.email.subject",
      new String[] {partnerRequest.getPartnerName()});
    // Building the email message
    StringBuilder message = new StringBuilder();
    String userName = partnerRequest.getExternalUserName() == null ? partnerRequest.getCreatedBy().getFirstName()
      : partnerRequest.getExternalUserName();
    message.append(this.getText("email.dear", new String[] {userName}));
    message.append(this.getText("marloRequestInstitution.reject.email",
      new String[] {partnerRequest.getPartnerInfo(), justification}));
    message.append("This request was sent through CLARISA API  </br>");

    GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(partnerRequest.getCrp().getId());
    CustomParameter parameter = customParameterManager
      .getCustomParameterByParameterKeyAndGlobalUnitId(APConstants.CRP_EMAIL_NOTIFICATIONS, globalUnit.getId());
    Boolean crpNotification = parameter == null ? true : parameter.getValue().equalsIgnoreCase("true");
    if (crpNotification) {
      this.sendMail.send(toEmail, ccEmail, bbcEmails, subject, message.toString(), null, null, null, true);
    }
  }

}
