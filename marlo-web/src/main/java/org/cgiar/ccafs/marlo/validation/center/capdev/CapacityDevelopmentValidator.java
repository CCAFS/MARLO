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


  public void validateCapDevDetail(BaseAction baseAction, CapacityDevelopment capdev, Participant participant,
    File uploadFile, String uploadFileContentType) {
    baseAction.setInvalidFields(new HashMap<>());

    if (capdev.getCapdevType().getId() == -1) {
      this.addMessage(baseAction.getText("capdev.action.type"));
      baseAction.getInvalidFields().put("CIAT_detailCapdev_capdev_capdevType_id", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (capdev.getStartDate() == null) {
      this.addMessage(baseAction.getText("capdev.action.startDate"));
      baseAction.getInvalidFields().put("input-capdev.startDate", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (capdev.getCategory() == 1) {

      this.validateParticipant(participant, baseAction);
    }
    if (capdev.getCategory() == 2) {
      if (capdev.getTitle().equalsIgnoreCase("")) {
        this.addMessage(baseAction.getText("capdev.action.title"));
        baseAction.getInvalidFields().put("input-capdev.title", InvalidFieldsMessages.EMPTYFIELD);
      }
      if (capdev.getCtFirstName().equalsIgnoreCase("") || capdev.getCtLastName().equalsIgnoreCase("")
        || capdev.getCtEmail().equalsIgnoreCase("")) {
        this.addMessage("contact");
        baseAction.getInvalidFields().put("input-contact", InvalidFieldsMessages.EMPTYFIELD);
      }
      if ((uploadFile == null) && (capdev.getNumParticipants() == null)) {
        this.addMessage(baseAction.getText("capdev.action.numParticipants"));
        baseAction.getInvalidFields().put("input-capdev.numParticipants", InvalidFieldsMessages.EMPTYFIELD);
      }
      if (uploadFile != null) {
        if (!uploadFileContentType.equals("application/vnd.ms-excel")
          && !uploadFileContentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
          System.out.println("formato incorrecto");
          this.addMessage("uploadFile");
          baseAction.getInvalidFields().put("input-uploadFile", InvalidFieldsMessages.INVALID_FORMAT);
        }
        if (uploadFile.length() > 31457280) {
          System.out.println("file muy pesado");
          this.addMessage("uploadFile");
          baseAction.getInvalidFields().put("input-uploadFile", InvalidFieldsMessages.FILE_SIZE);
        }
        if (!reader.validarExcelFile(uploadFile)) {
          System.out.println("el archivo no coincide con la plantilla");
          this.addMessage("uploadFile");
          baseAction.getInvalidFields().put("input-uploadFile", "file wrong");
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
    if (participant.getCitizenship().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.participant.citizenship"));
      baseAction.getInvalidFields().put("input-participant.citizenship", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (participant.getEmail().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.participant.email"));
      baseAction.getInvalidFields().put("input-participant.email", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (!participant.getEmail().equalsIgnoreCase("")) {
      final boolean validEmail = this.isValidEmail(participant.getEmail());
      if (!validEmail) {
        System.out.println("Email no valido");
        this.addMessage(baseAction.getText("capdev.action.participant.email"));
        baseAction.getInvalidFields().put("input-participant.email", InvalidFieldsMessages.EMPTYFIELD);
      }
    }
    if (participant.getInstitution().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.participant.institution"));
      baseAction.getInvalidFields().put("input-participant.institution", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (participant.getCountryOfInstitucion().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.participant.countryOfInstitucion"));
      baseAction.getInvalidFields().put("input-participant.countryOfInstitucion", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (participant.getSupervisor().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.participant.supervisor"));
      baseAction.getInvalidFields().put("input-participant.supervisor", InvalidFieldsMessages.EMPTYFIELD);
    }

  }
}
