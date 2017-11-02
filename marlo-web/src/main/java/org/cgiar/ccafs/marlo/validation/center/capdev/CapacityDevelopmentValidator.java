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

    if ((capdev.getDuration() != null) && (capdev.getDurationUnit() == null)) {
      this.addMessage(baseAction.getText("capdev.action.durationUnit"));
      baseAction.getInvalidFields().put("input-capdev.durationUnit", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (this.bolValue(capdev.getsGlobal()) == null) {
      this.addMessage(baseAction.getText("capdev.action.global"));
      baseAction.getInvalidFields().put("input-capdev.sGlobal", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (this.bolValue(capdev.getsRegional()) == null) {
      this.addMessage(baseAction.getText("capdev.action.region"));
      baseAction.getInvalidFields().put("input-capdev.sRegional", InvalidFieldsMessages.EMPTYFIELD);
    }


    if ((this.bolValue(capdev.getsRegional()) == null) && (this.bolValue(capdev.getsGlobal()) == null)) {
      if (capdev.getCapDevCountries() == null) {
        this.addMessage(baseAction.getText("capdev.action.countries"));
        baseAction.getInvalidFields().put("list-capdev.countries", baseAction.getText(InvalidFieldsMessages.EMPTYLIST,
          new String[] {"Capacity Development Intervention Countries"}));
      }
    } else {
      if ((this.bolValue(capdev.getsRegional()) != null)) {
        if (this.bolValue(capdev.getsRegional()) == false) {
          if (capdev.getCapDevCountries() == null) {
            this.addMessage(baseAction.getText("capdev.action.countries"));
            baseAction.getInvalidFields().put("list-capdev.countries", baseAction
              .getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Capacity Development Intervention Countries"}));
          }
        }

      }
      if (this.bolValue(capdev.getsGlobal()) != null) {
        if (this.bolValue(capdev.getsGlobal()) == true) {
          if (capdev.getCapDevCountries() != null) {
            if (capdev.getCapDevCountries().isEmpty()) {
              this.addMessage(baseAction.getText("capdev.action.countries"));
              baseAction.getInvalidFields().put("list-capdev.countries", baseAction.getText(
                InvalidFieldsMessages.EMPTYLIST, new String[] {"Capacity Development Intervention Countries"}));
            }
          }

        }
      }
    }

    if (capdev.getCapDevRegions() == null) {
      this.addMessage(baseAction.getText("capdev.action.regions"));
      baseAction.getInvalidFields().put("list-capdev.regions", baseAction.getText(InvalidFieldsMessages.EMPTYLIST,
        new String[] {"Capacity Development Intervention Regions"}));
    }

    if (capdev.getCategory() == 1) {

      this.validateParticipant(participant, baseAction);
    }
    if (capdev.getCategory() == 2) {

      if ((capdev.getCtFirstName() == null) || (capdev.getCtLastName() == null) || (capdev.getCtEmail() == null)) {
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
          && !uploadFileContentType.equals("application/vnd.ms-excel.sheet.macroEnabled.12")
          && !uploadFileContentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
          this.addMessage(baseAction.getText("capdev.action.file"));
          baseAction.getInvalidFields().put("list-capdev.uploadFile",
            baseAction.getText(InvalidFieldsMessages.INVALID_FORMAT, new String[] {""}));
        } else {
          if (!reader.validarExcelFile(uploadFile)) {
            this.addMessage(baseAction.getText("capdev.action.file"));
            baseAction.getInvalidFields().put("list-capdev.uploadFile",
              baseAction.getText(InvalidFieldsMessages.WRONG_FILE, new String[] {""}));
          }
          if (!reader.validarExcelFileData(uploadFile)) {
            this.addMessage(baseAction.getText("capdev.action.file"));
            baseAction.getInvalidFields().put("list-capdev.uploadFile",
              baseAction.getText(InvalidFieldsMessages.EMPTY_FILE, new String[] {""}));
          }
        }
        if (uploadFile.length() > 31457280) {
          this.addMessage(baseAction.getText("capdev.action.file"));
          baseAction.getInvalidFields().put("list-capdev.uploadFile",
            baseAction.getText(InvalidFieldsMessages.FILE_SIZE, new String[] {""}));
        }

      }
      if (capdev.getNumParticipants() != null) {
        int totalParticipants = capdev.getNumMen() + capdev.getNumWomen() + capdev.getNumOther();
        if ((capdev.getNumParticipants() < totalParticipants) || (capdev.getNumParticipants() > totalParticipants)) {
          baseAction.getInvalidFields().put("input-capdev.numParticipants", "The sum no match");
          baseAction.getInvalidFields().put("input-capdev.numMen", "The sum no match");
          baseAction.getInvalidFields().put("input-capdev.numWomen", "The sum no match");
          baseAction.getInvalidFields().put("input-capdev.numOther", "The sum no match");
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
    if (participant.getCode() == null) {
      this.addMessage(baseAction.getText("capdev.action.participant.code"));
      baseAction.getInvalidFields().put("input-capdev.participant.code", InvalidFieldsMessages.EMPTYFIELD);
    }
    if ((participant.getName() == null) || participant.getName().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.participant.firstName"));
      baseAction.getInvalidFields().put("input-capdev.participant.name", InvalidFieldsMessages.EMPTYFIELD);
    }
    if ((participant.getLastName() == null) || participant.getLastName().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.participant.lastName"));
      baseAction.getInvalidFields().put("input-capdev.participant.lastName", InvalidFieldsMessages.EMPTYFIELD);
    }
    if ((participant.getGender() == null) || participant.getGender().equalsIgnoreCase("-1")) {
      this.addMessage(baseAction.getText("capdev.action.participant.gender"));
      baseAction.getInvalidFields().put("input-capdev.participant.gender", InvalidFieldsMessages.EMPTYFIELD);
    }
    if ((participant.getLocElementsByCitizenship() == null)
      || (participant.getLocElementsByCitizenship().getId() == -1)) {
      this.addMessage(baseAction.getText("capdev.action.participant.citizenship"));
      baseAction.getInvalidFields().put("input-capdev.participant.locElementsByCitizenship.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if ((participant.getPersonalEmail() == null) || participant.getPersonalEmail().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.participant.personalEmail"));
      baseAction.getInvalidFields().put("input-capdev.participant.personalEmail", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (!participant.getPersonalEmail().equalsIgnoreCase("")) {
      final boolean validEmail = this.isValidEmail(participant.getPersonalEmail());
      if (!validEmail) {
        this.addMessage(baseAction.getText("capdev.action.participant.personalEmail"));
        baseAction.getInvalidFields().put("input-capdev.participant.personalEmail", InvalidFieldsMessages.WRONG_EMAIL);
      }
    }

    if (!participant.getEmail().equalsIgnoreCase("")) {
      final boolean validEmail = this.isValidEmail(participant.getEmail());
      if (!validEmail) {
        baseAction.getInvalidFields().put("input-capdev.participant.email", InvalidFieldsMessages.WRONG_EMAIL);
      }
    }

    if ((participant.getSupervisor() == null) || participant.getSupervisor().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.participant.Supervisor"));
      baseAction.getInvalidFields().put("input-capdev.participant.supervisor", InvalidFieldsMessages.EMPTYFIELD);
    }

  }
}
