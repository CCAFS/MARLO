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

package org.cgiar.ccafs.marlo.validation.superadmin;

import org.cgiar.ccafs.marlo.action.superadmin.GlobalUnitCreateAction;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.HashMap;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

@Named
public class GlobalUnitCreateValidator extends BaseValidator {

  private static final String REQUIRED_FIELD_KEY = "validation.field.required";

  public void validate(GlobalUnitCreateAction action) {
    action.setInvalidFields(new HashMap<>());

    if (action.isManagementMode()) {
      this.validateManagement(action);
    } else {
      this.validateCreateForm(action);
      this.validateLogo(action);
    }

    if (!action.getInvalidFields().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    }
  }

  private void validateManagement(GlobalUnitCreateAction action) {
    List<GlobalUnit> globalUnits = action.getGlobalUnits();
    if (globalUnits == null || globalUnits.isEmpty()) {
      return;
    }

    for (int index = 0; index < globalUnits.size(); index++) {
      this.validateManagementItem(action, globalUnits.get(index), index);
    }
  }

  private void validateManagementItem(GlobalUnitCreateAction action, GlobalUnit globalUnit, int index) {
    String fieldPrefix = "globalUnits[" + index + "]";
    String invalidFieldPrefix = "input-globalUnits[" + index + "].";

    if (globalUnit == null || !this.isValidString(globalUnit.getName())) {
      action.getInvalidFields().put(invalidFieldPrefix + "name", InvalidFieldsMessages.EMPTYFIELD);
      action.addFieldError(fieldPrefix + ".name", action.getText(REQUIRED_FIELD_KEY));
    }

    if (globalUnit == null || !this.isValidString(globalUnit.getAcronym())) {
      action.getInvalidFields().put(invalidFieldPrefix + "acronym", InvalidFieldsMessages.EMPTYFIELD);
      action.addFieldError(fieldPrefix + ".acronym", action.getText(REQUIRED_FIELD_KEY));
    }

    if (globalUnit == null || globalUnit.getInstitution() == null || globalUnit.getInstitution().getId() == null
      || globalUnit.getInstitution().getId().longValue() <= 0L) {
      action.getInvalidFields().put(invalidFieldPrefix + "institution.id", InvalidFieldsMessages.EMPTYFIELD);
      action.addFieldError(fieldPrefix + ".institution.id", action.getText(REQUIRED_FIELD_KEY));
    }

    boolean hasCrpAdmin = globalUnit != null && globalUnit.getCrpAdminTeam() != null
      && globalUnit.getCrpAdminTeam().stream().anyMatch(userRole -> userRole != null && userRole.getUser() != null
        && userRole.getUser().getId() != null && userRole.getUser().getId().longValue() > 0L);
    if (!hasCrpAdmin) {
      action.getInvalidFields().put(invalidFieldPrefix + "crpAdminTeam", InvalidFieldsMessages.EMPTYUSERLIST);
      action.addFieldError(fieldPrefix + ".crpAdminTeam", action.getText(REQUIRED_FIELD_KEY));
    }
  }

  private void validateLogo(GlobalUnitCreateAction action) {
    if (action.getLogoFile() == null) {
      return;
    }

    String contentType = StringUtils.trimToEmpty(action.getLogoFileContentType()).toLowerCase();
    if (!contentType.startsWith("image/")) {
      action.getInvalidFields().put("input-logoFile", InvalidFieldsMessages.INVALID_FORMAT);
      action.addFieldError("logoFile", action.getText("globalUnitManagement.validation.logoImage"));
    }
  }

  private void validateCreateForm(GlobalUnitCreateAction action) {
    if (!this.isValidString(action.getName())) {
      action.getInvalidFields().put("input-name", InvalidFieldsMessages.EMPTYFIELD);
      action.addFieldError("name", action.getText(REQUIRED_FIELD_KEY));
    }

    if (!this.isValidString(action.getAcronym())) {
      action.getInvalidFields().put("input-acronym", InvalidFieldsMessages.EMPTYFIELD);
      action.addFieldError("acronym", action.getText(REQUIRED_FIELD_KEY));
    }

    if (action.getInstitutionId() == null || action.getInstitutionId().longValue() <= 0L) {
      action.getInvalidFields().put("input-institutionId", InvalidFieldsMessages.EMPTYFIELD);
      action.addFieldError("institutionId", action.getText(REQUIRED_FIELD_KEY));
    }

    if (!this.isValidString(action.getPhasesDefinition())) {
      action.getInvalidFields().put("input-phasesDefinition", InvalidFieldsMessages.EMPTYFIELD);
      action.addFieldError("phasesDefinition", action.getText("globalUnitManagement.validation.phasesRequired"));
    }

    if (action.getCurrentPhaseIndex() == null || action.getCurrentPhaseIndex().intValue() < 0) {
      action.getInvalidFields().put("input-currentPhaseIndex", InvalidFieldsMessages.EMPTYFIELD);
      action.addFieldError("currentPhaseIndex", action.getText(REQUIRED_FIELD_KEY));
    }
  }
}
