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
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.HashMap;

import javax.inject.Named;

@Named
public class CrossCuttingValidator extends BaseValidator {

  public CrossCuttingValidator() {
    super();
  }

  public void validate(BaseAction action, PowbSynthesis powbSynthesis, String summarize, String assets,
    boolean saving) {

    action.setInvalidFields(new HashMap<>());

    if (!(this.isValidString(summarize) && this.wordCount(summarize) <= 100)) {
      action.addMessage(action.getText("liaisonInstitution.powb.summarizeCorssCutting"));
      action.getInvalidFields().put("input-powbSynthesis.crossCutting.summarize", InvalidFieldsMessages.EMPTYFIELD);
    }


    if (!(this.isValidString(assets) && this.wordCount(assets) <= 100)) {
      action.addMessage(action.getText("liaisonInstitution.powb.openDataIntellectualAssests"));
      action.getInvalidFields().put("input-powbSynthesis.crossCutting.assets", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (action.getValidationMessage().length() > 0) {
      action.addActionMessage(
        " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
    }

    this.saveMissingFields(powbSynthesis, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
      PowbSynthesisSectionStatusEnum.CROSS_CUTTING_DIMENSIONS.getStatus(), action);

  }

}
