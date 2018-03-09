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

package org.cgiar.ccafs.marlo.validation.powb;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisCrpStaffingCategory;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class CrpStaffingValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;

  public CrpStaffingValidator(GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(PowbSynthesis powbSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = powbSynthesis.getClass().getSimpleName();
    String actionFile = PowbSynthesisSectionStatusEnum.STAFFING.getStatus().replace("/", "_");
    String autoSaveFile =
      powbSynthesis.getId() + "_" + composedClassName + "_" + baseAction.getActualPhase().getDescription() + "_"
        + baseAction.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, PowbSynthesis powbSynthesis, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(powbSynthesis, action.getCrpID(), action);
      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }
    if (powbSynthesis != null) {
      if (!(this.isValidString(powbSynthesis.getCrpStaffing().getStaffingIssues()))) {
        action.addMessage(action.getText("powbSynthesis.crpStaffing.staffingIssues.readText"));
        action.getInvalidFields().put("input-powbSynthesis.crpStaffing.staffingIssues",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      int i = 0;
      for (PowbSynthesisCrpStaffingCategory powbStaffingCategory : powbSynthesis
        .getPowbSynthesisCrpStaffingCategoryList()) {
        this.validateCrpStaffingCategory(powbStaffingCategory, action, i);
        i++;
      }
      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(powbSynthesis, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
        PowbSynthesisSectionStatusEnum.STAFFING.getStatus(), action);
    }

  }

  private void validateCrpStaffingCategory(PowbSynthesisCrpStaffingCategory powbStaffingCategory, BaseAction action,
    int i) {
    if (powbStaffingCategory.getFemale() != null && powbStaffingCategory.getFemale() < 0) {
      action.addMissingField(action.getText("crpStaffing.tableD.female"));
      action.getInvalidFields().put("input-powbSynthesis.powbSynthesisCrpStaffingCategoryList[" + i + "].female",
        InvalidFieldsMessages.INVALID_NUMBER);
    }
    if (powbStaffingCategory.getMale() != null && powbStaffingCategory.getMale() < 0) {
      action.addMissingField(action.getText("crpStaffing.tableD.male"));
      action.getInvalidFields().put("input-powbSynthesis.powbSynthesisCrpStaffingCategoryList[" + i + "].male",
        InvalidFieldsMessages.INVALID_NUMBER);
    }
  }

}
