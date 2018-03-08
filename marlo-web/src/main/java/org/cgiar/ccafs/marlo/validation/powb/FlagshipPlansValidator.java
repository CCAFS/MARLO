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
public class FlagshipPlansValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;

  public FlagshipPlansValidator(GlobalUnitManager crpManager) {
    super();
    this.crpManager = crpManager;
  }

  private Path getAutoSaveFilePath(PowbSynthesis powbSynthesis, long crpID) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = powbSynthesis.getClass().getSimpleName();
    String actionFile = PowbSynthesisSectionStatusEnum.FLAGSHIP_PLANS.getStatus().replace("/", "_");
    String autoSaveFile =
      powbSynthesis.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, PowbSynthesis powbSynthesis, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(powbSynthesis, action.getCrpID());
      if (path.toFile().exists()) {
        action.addMissingField("draft");
      }
    }

    this.validateFlagshipPlans(action, powbSynthesis);

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }
    this.saveMissingFields(powbSynthesis, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      PowbSynthesisSectionStatusEnum.FLAGSHIP_PLANS.getStatus(), action);
  }

  private void validateFlagshipPlans(BaseAction action, PowbSynthesis powbSynthesis) {
    if (!(this.isValidString(powbSynthesis.getPowbFlagshipPlans().getPlanSummary()))) {
      action.addMessage(action.getText("liaisonInstitution.powb.planSummary.readText",
        new String[] {Integer.toString(action.getCurrentCycleYear())}));
      action.getInvalidFields().put("input-powbSynthesis.powbFlagshipPlans.planSummary",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }

}
