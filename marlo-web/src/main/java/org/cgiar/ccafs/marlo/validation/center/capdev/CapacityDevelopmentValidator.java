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

package org.cgiar.ccafs.marlo.validation.center.capdev;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.Participant;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.utils.ReadExcelFile;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class CapacityDevelopmentValidator extends BaseValidator {

  private final ReadExcelFile reader = new ReadExcelFile();

  @Inject
  public CapacityDevelopmentValidator() {

  }

  public Boolean bolValue(String value) {
    if ((value == null) || value.isEmpty() || value.toLowerCase().equals("null")) {
      return null;
    }
    return Boolean.valueOf(value);
  }

  public void validate(BaseAction baseAction, CapacityDevelopment capdev, Participant participant, File uploadFile,
    String uploadFileContentType) {
    baseAction.setInvalidFields(new HashMap<>());

    if (!baseAction.getFieldErrors().isEmpty()) {
      baseAction.addActionError(baseAction.getText("saving.fields.required"));
    }

    this.validateCapDevDetail(baseAction, capdev, participant, uploadFile, uploadFileContentType);

    this.saveMissingFields(capdev, "detailCapdev");

  }

  public void validateCapDevDetail(BaseAction baseAction, CapacityDevelopment capdev, Participant participant,
    File uploadFile, String uploadFileContentType) {


    if (capdev.getTitle() != null) {
      if (!this.isValidString(capdev.getTitle()) && (this.wordCount(capdev.getTitle()) <= 50)) {
        this.addMessage(baseAction.getText("capdev.action.title"));
        baseAction.getInvalidFields().put("input-capdev.title", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(baseAction.getText("capdev.action.title"));
      baseAction.getInvalidFields().put("input-capdev.title", InvalidFieldsMessages.EMPTYFIELD);
    }

    // if (capdev.getCapdevType().getId() == -1) {
    // this.addMessage(baseAction.getText("capdev.action.type"));
    // baseAction.getInvalidFields().put("input-capdev.capdevType.id", InvalidFieldsMessages.EMPTYFIELD);
    // }

    if (capdev.getStartDate() == null) {
      this.addMessage(baseAction.getText("capdev.action.startDate"));
      baseAction.getInvalidFields().put("input-capdev.startDate", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (capdev.getEndDate() == null) {
      this.addMessage(baseAction.getText("capdev.action.startDate"));
      baseAction.getInvalidFields().put("input-capdev.endDate", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (capdev.getDuration() == null) {
      this.addMessage(baseAction.getText("capdev.action.duration"));
      baseAction.getInvalidFields().put("input-capdev.duration", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (this.bolValue(capdev.getsGlobal()) != null) {
      if (this.bolValue(capdev.getsGlobal())) {
        if ((capdev.getCapDevCountries() == null)
          || capdev.getCapDevCountries().stream().filter(c -> c.isActive()).collect(Collectors.toList()).isEmpty()) {
          this.addMessage(baseAction.getText("capdev.action.countries"));
          baseAction.getInvalidFields().put("list-capdev.countries", baseAction.getText(InvalidFieldsMessages.EMPTYLIST,
            new String[] {"Capacity Development Intervention Countries"}));
        }
      }
    } else {
      this.addMessage(baseAction.getText("capdev.action.global"));
      baseAction.getInvalidFields().put("input-capdev.sGlobal", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (this.bolValue(capdev.getsRegional()) != null) {
      if (this.bolValue(capdev.getsRegional())) {
        if ((capdev.getCapDevRegions() == null) || capdev.getCapDevRegions().isEmpty()) {
          this.addMessage(baseAction.getText("capdev.action.regions"));
          baseAction.getInvalidFields().put("list-capdev.regions", baseAction.getText(InvalidFieldsMessages.EMPTYLIST,
            new String[] {"Capacity Development Intervention Regions"}));
        }
      }
    } else {
      this.addMessage(baseAction.getText("capdev.action.region"));
      baseAction.getInvalidFields().put("input-capdev.sRegional", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (capdev.getCategory() == 1) {

      this.validateParticipant(participant, baseAction);
    }
    if (capdev.getCategory() == 2) {

      if (capdev.getCtFirstName().equalsIgnoreCase("") || capdev.getCtLastName().equalsIgnoreCase("")
        || capdev.getCtEmail().equalsIgnoreCase("")) {
        this.addMessage(baseAction.getText("capdev.action.contactPerson"));
        baseAction.getInvalidFields().put("input-contact", InvalidFieldsMessages.EMPTYFIELD);
      }
      if ((uploadFile == null) && (capdev.getNumParticipants() == null)) {
        this.addMessage(baseAction.getText("capdev.action.numParticipants"));
        baseAction.getInvalidFields().put("list-capdev.uploadFile",
          baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Number of participants or a file "}));
      }
      if (uploadFile != null) {
        if (!uploadFileContentType.equals("application/vnd.ms-excel")
          && !uploadFileContentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
          System.out.println("formato incorrecto");
          this.addMessage(baseAction.getText("capdev.action.file"));
          baseAction.getInvalidFields().put("list-capdev.uploadFile",
            baseAction.getText(InvalidFieldsMessages.INVALID_FORMAT, new String[] {""}));
        } else {
          if (!reader.validarExcelFile(uploadFile)) {
            System.out.println("el archivo no coincide con la plantilla");
            this.addMessage(baseAction.getText("capdev.action.file"));
            baseAction.getInvalidFields().put("list-capdev.uploadFile",
              baseAction.getText(InvalidFieldsMessages.WRONG_FILE, new String[] {""}));
          }
          if (!reader.validarExcelFileData(uploadFile)) {
            System.out.println("el archivo esta vacio o tiene campos nulos");
            this.addMessage(baseAction.getText("capdev.action.file"));
            baseAction.getInvalidFields().put("list-capdev.uploadFile",
              baseAction.getText(InvalidFieldsMessages.EMPTY_FILE, new String[] {""}));
          }
        }
        if (uploadFile.length() > 31457280) {
          System.out.println("file muy pesado");
          this.addMessage(baseAction.getText("capdev.action.file"));
          baseAction.getInvalidFields().put("list-capdev.uploadFile",
            baseAction.getText(InvalidFieldsMessages.FILE_SIZE, new String[] {""}));

        }

      }
      if (capdev.getNumParticipants() != null) {
        final int totalParticipants = capdev.getNumMen() + capdev.getNumWomen();
        if ((capdev.getNumParticipants() < totalParticipants) || (capdev.getNumParticipants() > totalParticipants)) {
          baseAction.getInvalidFields().put("input-capdev.numParticipants", "La suma no coincide");
          baseAction.getInvalidFields().put("input-capdev.numMen", "La suma no coincide");
          baseAction.getInvalidFields().put("input-capdev.numWomen", "La suma no coincide");
        }
      }
    }

  }


  public boolean validateEmail(String email) {
    final Pattern VALID_EMAIL_ADDRESS_REGEX =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    final Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
    return matcher.find();

  }

  public void validateParticipant(Participant participant, BaseAction baseAction) {
    if (participant.getCode() == 0) {
      this.addMessage(baseAction.getText("capdev.action.participant.code"));
      baseAction.getInvalidFields().put("input-participant.code", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (participant.getName().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.participant.name"));
      baseAction.getInvalidFields().put("input-participant.name", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (participant.getLastName().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.participant.lastName"));
      baseAction.getInvalidFields().put("input-participant.lastName", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (participant.getGender().equalsIgnoreCase("-1")) {
      this.addMessage(baseAction.getText("capdev.action.participant.gender"));
      baseAction.getInvalidFields().put("input-participant.gender", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (participant.getLocElementsByCitizenship().getId() == -1) {
      this.addMessage(baseAction.getText("capdev.action.participant.citizenship"));
      baseAction.getInvalidFields().put("input-participant.locElementsByCitizenship.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (participant.getPersonalEmail().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.participant.personalEmail"));
      baseAction.getInvalidFields().put("input-participant.personalEmail", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (!participant.getPersonalEmail().equalsIgnoreCase("")) {
      final boolean validEmail = this.isValidEmail(participant.getPersonalEmail());
      if (!validEmail) {
        this.addMessage(baseAction.getText("capdev.action.participant.personalEmail"));
        baseAction.getInvalidFields().put("input-participant.personalEmail", InvalidFieldsMessages.EMPTYFIELD);
      }
    }
    if (participant.getInstitutions().getId() == -1) {
      this.addMessage(baseAction.getText("capdev.action.participant.institution"));
      baseAction.getInvalidFields().put("input-participant.institutions.id", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (participant.getLocElementsByCountryOfInstitucion().getId() == -1) {
      this.addMessage(baseAction.getText("capdev.action.participant.countryOfInstitucion"));
      baseAction.getInvalidFields().put("input-participant.locElementsByCountryOfInstitucion.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (participant.getSupervisor().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.participant.supervisor"));
      baseAction.getInvalidFields().put("input-participant.supervisor", InvalidFieldsMessages.EMPTYFIELD);
    }

  }
}
