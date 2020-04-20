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

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.Institutions;
import org.cgiar.ccafs.marlo.rest.dto.CountryOfficeRequestDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewCountryOfficeRequestDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.InstitutionMapper;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.SendMailS;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class CountryOfficeRequestItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(Institutions.class);
  @Autowired
  private Environment env;
  private final SendMailS sendMail;
  private InstitutionManager institutionManager;
  private LocElementManager locElementManager;
  private InstitutionMapper institutionMapper;
  private PartnerRequestManager partnerRequestManager;
  private GlobalUnitManager globalUnitManager;
  protected APConfig config;
  private boolean messageSent;
  // List<FieldErrorDTO> fieldErrors;


  @Inject
  public CountryOfficeRequestItem(InstitutionManager institutionManager, InstitutionMapper institutionMapper,
    LocElementManager locElementManager, PartnerRequestManager partnerRequestManager,
    GlobalUnitManager globalUnitManager, SendMailS sendMail, APConfig config) {
    this.institutionManager = institutionManager;
    this.institutionMapper = institutionMapper;
    this.locElementManager = locElementManager;
    this.partnerRequestManager = partnerRequestManager;
    this.globalUnitManager = globalUnitManager;
    this.sendMail = sendMail;
    this.config = config;
    // this.fieldErrors = new ArrayList<FieldErrorDTO>();

  }

  /**
   * Create a new country office request *
   * 
   * @param newCountryOfficeRequestDTO with the request info
   * @param CGIAR entity acronym who is requesting
   * @param Logged user on system
   * @return CountryOfficeRequestDTO founded
   */
  public ResponseEntity<CountryOfficeRequestDTO>
    createCountryOfficeRequest(NewCountryOfficeRequestDTO newCountryOfficeRequestDTO, String entityAcronym, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    Pattern pattern = Pattern.compile(regex);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors
        .add(new FieldErrorDTO("createOfficeLocationRequest", "GlobalUnitEntity", "Invalid CGIAR entity acronym"));
    }
    LocElement locElement = this.locElementManager.getLocElementByISOCode(newCountryOfficeRequestDTO.getCountryIso());
    if (locElement == null) {
      fieldErrors.add(new FieldErrorDTO("createOfficeLocationRequest", "Country", "Invalid country iso Alpha code"));
    }

    Institution institution =
      this.institutionManager.getInstitutionById(newCountryOfficeRequestDTO.getInstitutionCode());
    if (institution == null) {
      fieldErrors.add(new FieldErrorDTO("createOfficeLocationRequest", "Institution", "Invalid institution code"));
    }

    // externalUserMail is mandatory
    if (newCountryOfficeRequestDTO.getExternalUserMail() != null) {
      Matcher matcher = pattern.matcher(newCountryOfficeRequestDTO.getExternalUserMail());
      if (!matcher.matches()) {
        fieldErrors
          .add(new FieldErrorDTO("createOfficeLocationRequest", "getExternalUserMail", "Bad external email format"));
      }
    } else {
      fieldErrors
        .add(new FieldErrorDTO("createOfficeLocationRequest", "getExternalUserMail", "External email is empty"));
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    PartnerRequest partnerRequest = this.institutionMapper.NewCountryOfficeRequestDTOToPartnerRequest(
      newCountryOfficeRequestDTO, globalUnitEntity, locElement, institution, user);

    partnerRequest = this.partnerRequestManager.savePartnerRequest(partnerRequest);

    // SEND THE MAIL
    this.sendOfficeRequestEmail(partnerRequest, user, entityAcronym);

    return new ResponseEntity<CountryOfficeRequestDTO>(
      this.institutionMapper.PartnerRequestToCountryOfficeRequestDTO(partnerRequest), HttpStatus.CREATED);
  }


  /**
   * Get a country office request by an id *
   * 
   * @return CountryOfficeRequestDTO founded
   */
  public ResponseEntity<CountryOfficeRequestDTO> getCountryOfficeRequest(Long id, String entityAcronym) {
    PartnerRequest partnerRequest = this.partnerRequestManager.getPartnerRequestById(id);
    if (partnerRequest != null && !partnerRequest.isOffice()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return Optional.ofNullable(partnerRequest).map(this.institutionMapper::PartnerRequestToCountryOfficeRequestDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

  }


  String getText(String property, String[] params) {
    String value = this.env.getProperty(property);
    MessageFormat message = new MessageFormat(value);
    return message.format(params);

  }

  private void sendOfficeRequestEmail(PartnerRequest partnerRequest, User user, String entityAcronym) {
    String institutionName, countryName, countryISOAlpha;
    institutionName = partnerRequest.getInstitution().getName();
    countryName = partnerRequest.getLocElement().getName();
    countryISOAlpha = partnerRequest.getLocElement().getIsoAlpha2();

    String subject;
    StringBuilder message = new StringBuilder();

    // message subject
    String ccEmail = user.getEmail();

    subject = this.getText("marloRequestInstitution.office.clarisa.email.subject",
      new String[] {entityAcronym.toUpperCase(), countryISOAlpha, institutionName});

    // Message content
    message.append(partnerRequest.getExternalUserName() == null ? (user.getFirstName() + " " + user.getLastName() + " ")
      : partnerRequest.getExternalUserName());
    message.append(" (");
    message.append(
      partnerRequest.getExternalUserMail() == null ? user.getEmail() : partnerRequest.getExternalUserMail() + ") ");
    message.append(" is requesting to add the following office location(s): ");
    message.append("</br></br>");
    message.append("Partner Name: ");
    message.append(institutionName);
    message.append("</br>");
    message.append("Countries: ");
    message.append(countryName);
    message.append(" </br></br>");
    message.append("This request was sent through CLARISA logged as " + user.getFirstName() + " " + user.getLastName()
      + " (" + user.getEmail() + ")  </br>");
    message.append("</br>");
    try {
      this.sendMail.send(this.config.getEmailNotification(), ccEmail, this.config.getEmailNotification(), subject,
        message.toString(), null, null, null, true);
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

}
